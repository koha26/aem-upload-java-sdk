package com.kdia.aemupload.auth.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kdia.aemupload.auth.ApiAccessTokenProvider;
import com.kdia.aemupload.config.ApiAccessTokenConfiguration;
import com.kdia.aemupload.http.ApiHttpClient;
import com.kdia.aemupload.http.entity.ApiHttpEntity;
import com.kdia.aemupload.http.entity.ApiHttpResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.hc.core5.http.HttpStatus;

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
import java.util.Map;

@Slf4j
public class ServiceCredentialsApiAccessTokenProvider implements ApiAccessTokenProvider {

    private final ApiAccessTokenConfiguration apiAccessTokenConfiguration;
    private final ApiHttpClient apiHttpClient;
    private String cachedAccessToken;
    private Date expiration;

    public ServiceCredentialsApiAccessTokenProvider(ApiAccessTokenConfiguration apiAccessTokenConfiguration,
                                                    ApiHttpClient apiHttpClient) {
        this.apiAccessTokenConfiguration = apiAccessTokenConfiguration;
        this.apiHttpClient = apiHttpClient;
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

    String getJWTToken() {
        RSAPrivateKey privateKey = getRsaPrivateKey();
        if (privateKey == null) {
            return null;
        }

        return Jwts.builder()
                .setClaims(createClaims())
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();
    }

    Claims createClaims() {
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

    RSAPrivateKey getRsaPrivateKey() {
        try {
            String privateKeyContent = StringUtils.isNoneEmpty(apiAccessTokenConfiguration.getPrivateKeyContent())
                    ? apiAccessTokenConfiguration.getPrivateKeyContent()
                    : getPrivateKeyContentFromFile();
            String privateKeyContentNormalized = privateKeyContent
                    .replaceFirst("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "");
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
        }
        return null;
    }

    AccessTokenWrapper getAccessToken(final String jwtToken) {
        var requestUrl = String.format("https://%s/ims/exchange/jwt", apiAccessTokenConfiguration.getImsEndpoint());
        var headers = Map.of("Content-Type", "application/x-www-form-urlencoded");
        var httpEntity = ApiHttpEntity.builder()
                .body(getFormParams(jwtToken))
                .headers(headers)
                .build();
        ApiHttpResponse<AccessTokenWrapper> response = apiHttpClient.post(requestUrl, httpEntity, AccessTokenWrapper.class);

        return response.getStatus() < HttpStatus.SC_REDIRECTION && response.getBody() != null
                ? response.getBody()
                : null;
    }

    private String getPrivateKeyContentFromFile() throws IOException {
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
