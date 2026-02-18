package com.kdiachenko.aemupload.auth.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kdiachenko.aemupload.auth.ApiAccessTokenProvider;
import com.kdiachenko.aemupload.auth.Clock;
import com.kdiachenko.aemupload.auth.TokenCache;
import com.kdiachenko.aemupload.config.ApiAccessTokenConfiguration;
import com.kdiachenko.aemupload.exception.AuthenticationException;
import com.kdiachenko.aemupload.http.entity.ApiHttpResponse;
import com.kdiachenko.aemupload.http.response.ApiHttpClientResponseHandlerFactory;
import com.kdiachenko.aemupload.internal.auth.InMemoryTokenCache;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.hc.core5.http.ContentType.APPLICATION_FORM_URLENCODED;
import static org.apache.hc.core5.http.HttpHeaders.CONTENT_TYPE;

@Slf4j
public class ServiceCredentialsApiAccessTokenProvider implements ApiAccessTokenProvider, Closeable {

    private final ApiAccessTokenConfiguration apiAccessTokenConfiguration;
    private final CloseableHttpClient httpClient;
    private final HttpClientResponseHandler<ApiHttpResponse<AccessTokenWrapper>> responseHandlerFactory;
    private final Clock clock;
    private final TokenCache tokenCache;
    private final boolean ownedHttpClient;

    /**
     * Creates a provider with default HTTP client and system clock.
     *
     * @param apiAccessTokenConfiguration the token configuration
     */
    public ServiceCredentialsApiAccessTokenProvider(ApiAccessTokenConfiguration apiAccessTokenConfiguration) {
        this(
                apiAccessTokenConfiguration,
                HttpClients.createDefault(),
                ApiHttpClientResponseHandlerFactory.create().createHandler(AccessTokenWrapper.class),
                Clock.systemClock(),
                new InMemoryTokenCache(),
                true
        );
    }

    /**
     * Creates a provider with all dependencies injected (for testing).
     *
     * @param apiAccessTokenConfiguration the token configuration
     * @param httpClient                  the HTTP client
     * @param responseHandlerFactory      the response handler
     * @param clock                       the clock for time operations
     */
    public ServiceCredentialsApiAccessTokenProvider(
            ApiAccessTokenConfiguration apiAccessTokenConfiguration,
            CloseableHttpClient httpClient,
            HttpClientResponseHandler<ApiHttpResponse<AccessTokenWrapper>> responseHandlerFactory,
            Clock clock) {
        this(apiAccessTokenConfiguration, httpClient, responseHandlerFactory, clock, new InMemoryTokenCache(clock), false);
    }

    /**
     * Creates a provider with all dependencies injected (for testing).
     *
     * @param apiAccessTokenConfiguration the token configuration
     * @param httpClient                  the HTTP client
     * @param responseHandlerFactory      the response handler
     * @param clock                       the clock for time operations
     * @param tokenCache                  token cache implementation
     */
    public ServiceCredentialsApiAccessTokenProvider(
            ApiAccessTokenConfiguration apiAccessTokenConfiguration,
            CloseableHttpClient httpClient,
            HttpClientResponseHandler<ApiHttpResponse<AccessTokenWrapper>> responseHandlerFactory,
            Clock clock,
            TokenCache tokenCache) {
        this(apiAccessTokenConfiguration, httpClient, responseHandlerFactory, clock, tokenCache, false);
    }

    /**
     * Creates a provider with all dependencies injected.
     *
     * @param apiAccessTokenConfiguration the token configuration
     * @param httpClient                  the HTTP client
     * @param responseHandlerFactory      the response handler
     * @param clock                       the clock for time operations
     * @param tokenCache                  token cache implementation
     * @param ownedHttpClient             whether this provider owns (and should close) the HTTP client
     */
    public ServiceCredentialsApiAccessTokenProvider(
            ApiAccessTokenConfiguration apiAccessTokenConfiguration,
            CloseableHttpClient httpClient,
            HttpClientResponseHandler<ApiHttpResponse<AccessTokenWrapper>> responseHandlerFactory,
            Clock clock,
            TokenCache tokenCache,
            boolean ownedHttpClient) {
        this.apiAccessTokenConfiguration = apiAccessTokenConfiguration;
        this.httpClient = httpClient;
        this.responseHandlerFactory = responseHandlerFactory;
        this.clock = clock;
        this.tokenCache = tokenCache;
        this.ownedHttpClient = ownedHttpClient;
    }

    @Override
    public void close() throws IOException {
        if (ownedHttpClient && httpClient != null) {
            httpClient.close();
        }
    }

    @Override
    public String getAccessToken() {
        Optional<String> cachedToken = tokenCache.get();
        if (cachedToken.isPresent()) {
            return cachedToken.get();
        }
        String jwtToken = getJWTToken();
        AccessTokenWrapper accessToken = exchangeJwtForAccessToken(jwtToken);
        log.info("Access token has been received. Expires in: {}", accessToken.expiresIn);
        tokenCache.put(accessToken.getAccessToken(), Duration.ofSeconds(accessToken.getExpiresIn()));
        return accessToken.getAccessToken();
    }

    private AccessTokenWrapper exchangeJwtForAccessToken(final String jwtToken) {
        try {
            HttpPut httpPut = new HttpPut(apiAccessTokenConfiguration.getImsEndpoint());
            httpPut.addHeader(CONTENT_TYPE, APPLICATION_FORM_URLENCODED.toString());
            List<NameValuePair> params = getFormParams(jwtToken).entrySet().stream()
                    .map(entry -> new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())))
                    .collect(Collectors.toList());
            httpPut.setEntity(new UrlEncodedFormEntity(params));

            ApiHttpResponse<AccessTokenWrapper> response = httpClient.execute(httpPut, responseHandlerFactory);

            if (response.getStatus() >= HttpStatus.SC_REDIRECTION || response.getBody() == null) {
                throw new AuthenticationException("Failed to exchange JWT for access token. Status: "
                        + response.getStatus() + ", Error: " + response.getErrorMessage());
            }
            return response.getBody();
        } catch (IOException e) {
            log.error("Error while getting access token", e);
            throw new AuthenticationException("Failed to exchange JWT for access token", e);
        }
    }

    private String getJWTToken() {
        RSAPrivateKey privateKey = getRsaPrivateKey();

        return Jwts.builder()
                .claims(createClaims())
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    private Map<String, Object> createClaims() {
        String imsHost = getImsHost();
        Instant expirationTime = clock.now().plusSeconds(apiAccessTokenConfiguration.getTokenLifeTimeInSec());

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", apiAccessTokenConfiguration.getId());
        claims.put("iss", apiAccessTokenConfiguration.getOrg());
        claims.put("aud", "https://" + imsHost + "/c/" + apiAccessTokenConfiguration.getClientId());
        claims.put("exp", Date.from(expirationTime));

        apiAccessTokenConfiguration.getMetaScopes().stream()
                .map(metaScope -> "https://" + imsHost + "/s/" + metaScope)
                .forEach(value -> claims.put(value, true));
        return claims;
    }

    /**
     * Extracts IMS host from the configured endpoint URL.
     * Falls back to the raw value if it isn't a valid URI.
     */
    private String getImsHost() {
        String imsEndpoint = apiAccessTokenConfiguration.getImsEndpoint();
        if (imsEndpoint == null) {
            return null;
        }
        try {
            URI uri = URI.create(imsEndpoint);
            return uri.getHost() != null ? uri.getHost() : imsEndpoint;
        } catch (IllegalArgumentException e) {
            return imsEndpoint;
        }
    }

    private RSAPrivateKey getRsaPrivateKey() {
        try {
            String privateKeyContent = StringUtils.isNoneEmpty(apiAccessTokenConfiguration.getPrivateKeyContent())
                    ? apiAccessTokenConfiguration.getPrivateKeyContent()
                    : getPrivateKeyContentFromFile();
            if (privateKeyContent == null) {
                throw new AuthenticationException(
                        "Private key not configured. Set either privateKeyContent or privateKeyFilePath");
            }
            String privateKeyContentNormalized = privateKeyContent
                    .replaceFirst("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] decode = Base64.getDecoder().decode(privateKeyContentNormalized);
            PKCS8EncodedKeySpec keySpecPv = new PKCS8EncodedKeySpec(decode);
            KeyFactory keyFactory = getKeyFactory();
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpecPv);
        } catch (AuthenticationException e) {
            throw e;
        } catch (NoSuchAlgorithmException e) {
            throw new AuthenticationException("RSA algorithm not available", e);
        } catch (IOException e) {
            throw new AuthenticationException("Cannot read private key from file: "
                    + apiAccessTokenConfiguration.getPrivateKeyFilePath(), e);
        } catch (InvalidKeySpecException e) {
            throw new AuthenticationException("Invalid private key format: "
                    + apiAccessTokenConfiguration.getPrivateKeyFilePath(), e);
        } catch (Exception e) {
            throw new AuthenticationException("Error while reading private key", e);
        }
    }

    private String getPrivateKeyContentFromFile() throws IOException {
        if (StringUtils.isEmpty(apiAccessTokenConfiguration.getPrivateKeyFilePath())) {
            return null;
        }
        Path privateKeyPath = Paths.get(apiAccessTokenConfiguration.getPrivateKeyFilePath());
        return String.join("", Files.readAllLines(privateKeyPath));
    }

    private Map<String, String> getFormParams(final String jwtToken) {
        var formParams = new HashMap<String, String>();
        formParams.put("client_id", apiAccessTokenConfiguration.getClientId());
        formParams.put("client_secret", apiAccessTokenConfiguration.getClientSecret());
        formParams.put("jwt_token", jwtToken);
        return formParams;
    }

    /**
     * Factory method for RSA {@link KeyFactory}. Extracted for testability.
     */
    protected KeyFactory getKeyFactory() throws NoSuchAlgorithmException {
        return KeyFactory.getInstance("RSA");
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AccessTokenWrapper {
        @JsonProperty("access_token")
        private String accessToken;
        @JsonProperty("token_type")
        private String tokenType;
        /**
         * Token lifetime in seconds as returned by IMS.
         */
        @JsonProperty("expires_in")
        private long expiresIn;
    }
}
