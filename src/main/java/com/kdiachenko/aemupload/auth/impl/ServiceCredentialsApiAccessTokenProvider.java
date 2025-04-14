package com.kdiachenko.aemupload.auth.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kdiachenko.aemupload.auth.ApiAccessTokenProvider;
import com.kdiachenko.aemupload.config.ApiAccessTokenConfiguration;
import com.kdiachenko.aemupload.http.entity.ApiHttpResponse;
import com.kdiachenko.aemupload.http.response.ApiHttpClientResponseHandlerFactory;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
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
@RequiredArgsConstructor
public class ServiceCredentialsApiAccessTokenProvider implements ApiAccessTokenProvider {

    private final ApiAccessTokenConfiguration apiAccessTokenConfiguration;
    private final CloseableHttpClient httpClient;
    private final HttpClientResponseHandler<ApiHttpResponse<AccessTokenWrapper>> responseHandlerFactory;
    @Setter(value = AccessLevel.PACKAGE)
    @Getter(value = AccessLevel.PACKAGE)
    private String cachedAccessToken;
    @Setter(value = AccessLevel.PACKAGE)
    @Getter(value = AccessLevel.PACKAGE)
    private Date expiration;

    public ServiceCredentialsApiAccessTokenProvider(ApiAccessTokenConfiguration apiAccessTokenConfiguration) {
        this(
                apiAccessTokenConfiguration,
                HttpClients.createDefault(),
                ApiHttpClientResponseHandlerFactory.getInstance().createHandler(AccessTokenWrapper.class)
        );
    }

    @Override
    public String getAccessToken() {
        if (cachedAccessToken != null && isTokenNotExpired()) {
            return cachedAccessToken;
        }
        String jwtToken = getJWTToken();
        if (jwtToken == null) {
            return null;
        }
        AccessTokenWrapper accessToken = getAccessToken(jwtToken);
        if (accessToken == null) {
            return null;
        }
        log.info("Access token has been received. Expires in: {}", accessToken.expiresIn);
        cachedAccessToken = accessToken.getAccessToken();
        expiration = DateUtils.addMilliseconds(getDate(), Math.toIntExact(accessToken.getExpiresIn()));
        return cachedAccessToken;
    }

    Date getDate() {
        return new Date();
    }

    private String getJWTToken() {
        RSAPrivateKey privateKey = getRsaPrivateKey();
        if (privateKey == null) {
            return null;
        }

        return Jwts.builder()
                .setClaims(createClaims())
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();
    }

    private Claims createClaims() {
        String imsHost = apiAccessTokenConfiguration.getImsEndpoint();
        Claims jwtClaims = Jwts.claims()
                .setSubject(apiAccessTokenConfiguration.getId())
                .setIssuer(apiAccessTokenConfiguration.getOrg())
                .setAudience("https://" + imsHost + "/c/" + apiAccessTokenConfiguration.getClientId())
                .setExpiration(DateUtils.addSeconds(getDate(), apiAccessTokenConfiguration.getTokenLifeTimeInSec()));
        apiAccessTokenConfiguration.getMetaScopes().stream()
                .map(metaScope -> "https://" + imsHost + "/s/" + metaScope)
                .forEach(value -> jwtClaims.put(value, true));
        return jwtClaims;
    }

    private RSAPrivateKey getRsaPrivateKey() {
        try {
            String privateKeyContent = StringUtils.isNoneEmpty(apiAccessTokenConfiguration.getPrivateKeyContent())
                    ? apiAccessTokenConfiguration.getPrivateKeyContent()
                    : getPrivateKeyContentFromFile();
            if (privateKeyContent == null) {
                return null;
            }
            String privateKeyContentNormalized = privateKeyContent
                    .replaceFirst("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .trim();
            byte[] decode = Base64.getDecoder().decode(privateKeyContentNormalized);
            PKCS8EncodedKeySpec keySpecPv = new PKCS8EncodedKeySpec(decode, "RSA");
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpecPv);
        } catch (NoSuchAlgorithmException e) {
            log.error("No RSA algorithm", e);
        } catch (IOException e) {
            log.error("Can't read private key path {}", apiAccessTokenConfiguration.getPrivateKeyFilePath(), e);
        } catch (InvalidKeySpecException e) {
            log.error("Invalid key spec {}", apiAccessTokenConfiguration.getPrivateKeyFilePath(), e);
        } catch (Exception e) {
            log.error("Error while reading private key", e);
        }
        return null;
    }

    private AccessTokenWrapper getAccessToken(final String jwtToken) {
        try {
            HttpPut httpPut = new HttpPut(apiAccessTokenConfiguration.getImsEndpoint());
            httpPut.addHeader(CONTENT_TYPE, APPLICATION_FORM_URLENCODED.toString());
            List<NameValuePair> params = getFormParams(jwtToken).entrySet().stream()
                    .map(entry -> new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())))
                    .collect(Collectors.toList());
            httpPut.setEntity(new UrlEncodedFormEntity(params));

            ApiHttpResponse<AccessTokenWrapper> response = httpClient.execute(httpPut, responseHandlerFactory);

            return response.getStatus() < HttpStatus.SC_REDIRECTION && response.getBody() != null
                    ? response.getBody()
                    : null;
        } catch (IOException e) {
            log.error("Error while getting access token", e);
            return null;
        }
    }

    private String getPrivateKeyContentFromFile() throws IOException {
        if (StringUtils.isEmpty(apiAccessTokenConfiguration.getPrivateKeyFilePath())) {
            return null;
        }
        Path privateKeyPath = Paths.get(apiAccessTokenConfiguration.getPrivateKeyFilePath());
        return String.join("", Files.readAllLines(privateKeyPath));
    }

    private boolean isTokenNotExpired() {
        return getDate().before(expiration);
    }

    private Map<String, String> getFormParams(final String jwtToken) {
        var formParams = new HashMap<String, String>();
        formParams.put("client_id", apiAccessTokenConfiguration.getClientId());
        formParams.put("client_secret", apiAccessTokenConfiguration.getClientSecret());
        formParams.put("jwt_token", jwtToken);
        return formParams;
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
        @JsonProperty("expires_in")
        private long expiresIn;
    }
}
