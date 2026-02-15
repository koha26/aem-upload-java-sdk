package com.kdiachenko.aemupload.auth.impl;

import com.kdiachenko.aemupload.auth.Clock;
import com.kdiachenko.aemupload.auth.TokenCache;
import com.kdiachenko.aemupload.common.ApiAccessTokenConfigurationStub;
import com.kdiachenko.aemupload.config.ApiAccessTokenConfiguration;
import com.kdiachenko.aemupload.http.entity.ApiHttpResponse;
import com.kdiachenko.aemupload.internal.auth.InMemoryTokenCache;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceCredentialsApiAccessTokenProviderTest {

    public static final String RESOURCES_BASE_PATH = "src/test/resources/com/kdiachenko/aemupload/auth";

    private ApiAccessTokenConfigurationStub config;
    @Mock
    private CloseableHttpClient httpClient;
    @Mock
    private HttpClientResponseHandler<ApiHttpResponse<ServiceCredentialsApiAccessTokenProvider.AccessTokenWrapper>> responseHandler;
    @Captor
    private ArgumentCaptor<HttpPut> putRequestCaptor;

    private MutableClock clock;
    private TokenCache tokenCache;
    private ServiceCredentialsApiAccessTokenProvider provider;
    private ServiceCredentialsApiAccessTokenProvider.AccessTokenWrapper tokenWrapper;

    @BeforeEach
    void setUp() {
        config = ApiAccessTokenConfigurationStub.builder()
                .imsEndpoint("https://ims.example.com/ims/exchange/jwt")
                .clientId("clientId")
                .clientSecret("clientSecret")
                .id("id")
                .org("org")
                .tokenLifeTimeInSec(60)
                .metaScopes(List.of("scope1", "scope2"))
                .build();
        tokenWrapper = new ServiceCredentialsApiAccessTokenProvider.AccessTokenWrapper("abc123", "bearer", 3600);
        clock = new MutableClock(Instant.parse("2024-01-01T00:00:00Z"));
        tokenCache = new InMemoryTokenCache(clock);
        provider = new ServiceCredentialsApiAccessTokenProviderTestWrapper(
                config,
                httpClient,
                responseHandler,
                clock,
                tokenCache
        );
    }

    @Test
    void getAccessToken_shouldReturnCachedTokenWhenValid() throws IOException {
        tokenCache.put("cached_token", Duration.ofSeconds(10));

        assertThat(provider.getAccessToken()).isEqualTo("cached_token");
        verify(httpClient, never()).execute(any(HttpPut.class), any(HttpClientResponseHandler.class));
    }

    @Test
    void getAccessToken_shouldReturnNullWhenPrivateKeyMissing() {
        assertNull(provider.getAccessToken());
    }

    @Test
    void getAccessToken_shouldReturnNullWhenPrivateKeyFileIsMissing() {
        config.setPrivateKeyFilePath(RESOURCES_BASE_PATH + "/does-not-exist.txt");

        assertNull(provider.getAccessToken());
    }

    @Test
    void getAccessToken_shouldReturnNullWhenPrivateKeyFileIsInvalid() {
        config.setPrivateKeyFilePath(RESOURCES_BASE_PATH + "/test-rsa_wrong-key.txt");

        assertNull(provider.getAccessToken());
    }

    @Test
    void getAccessToken_shouldReturnTokenAndCacheIt() throws IOException {
        config.setPrivateKeyFilePath(RESOURCES_BASE_PATH + "/test-rsa_valid-key.txt");
        mockHttpClientResponse(ApiHttpResponse.builder().body(tokenWrapper).status(200).build());

        String token = provider.getAccessToken();

        assertThat(token).isEqualTo("abc123");
        assertThat(tokenCache.get()).contains("abc123");
        clock.advance(Duration.ofSeconds(3601));
        assertThat(tokenCache.get()).isEmpty();
    }

    @Test
    void getAccessToken_shouldUsePrivateKeyContentWhenProvided() throws IOException {
        Path privateKeyPath = Paths.get(RESOURCES_BASE_PATH + "/test-rsa_valid-key.txt");
        String privateFileContent = String.join("", Files.readAllLines(privateKeyPath));
        config.setPrivateKeyContent(privateFileContent);
        mockHttpClientResponse(ApiHttpResponse.builder().body(tokenWrapper).status(200).build());

        assertThat(provider.getAccessToken()).isEqualTo("abc123");
    }

    @Test
    void getAccessToken_shouldSendCorrectAccessTokenRequest() throws IOException, URISyntaxException {
        config.setPrivateKeyFilePath(RESOURCES_BASE_PATH + "/test-rsa_valid-key.txt");
        mockHttpClientResponse(ApiHttpResponse.builder().body(tokenWrapper).status(200).build());

        provider.getAccessToken();

        verify(httpClient).execute(putRequestCaptor.capture(), any(HttpClientResponseHandler.class));
        HttpPut putRequest = putRequestCaptor.getValue();
        assertThat(putRequest.getScheme()).isEqualTo("https");
        assertThat(putRequest.getPath()).isEqualTo("/ims/exchange/jwt");
        assertThat(putRequest.getRequestUri()).isEqualTo("/ims/exchange/jwt");
        assertThat(putRequest.getFirstHeader("Content-Type"))
                .isNotNull().extracting(Header::getValue)
                .isEqualTo("application/x-www-form-urlencoded; charset=ISO-8859-1");
        assertAccessTokenInquireRequestBody(putRequest);
    }

    @Test
    void getAccessToken_shouldReturnNullWhenResponseStatusNotOk() throws IOException {
        config.setPrivateKeyFilePath(RESOURCES_BASE_PATH + "/test-rsa_valid-key.txt");
        mockHttpClientResponse(ApiHttpResponse.builder().status(302).build());

        assertNull(provider.getAccessToken());
    }

    @Test
    void getAccessToken_shouldReturnNullWhenResponseBodyMissing() throws IOException {
        config.setPrivateKeyFilePath(RESOURCES_BASE_PATH + "/test-rsa_valid-key.txt");
        mockHttpClientResponse(ApiHttpResponse.builder().status(200).build());

        assertNull(provider.getAccessToken());
    }

    @Test
    void getAccessToken_shouldReturnNullWhenHttpClientThrowsIOException() throws IOException {
        config.setPrivateKeyFilePath(RESOURCES_BASE_PATH + "/test-rsa_valid-key.txt");
        doThrow(IOException.class).when(httpClient).execute(any(HttpPut.class), any(HttpClientResponseHandler.class));

        assertNull(provider.getAccessToken());
    }

    @Test
    void getAccessToken_shouldReturnNullWhenConfigThrows() {
        config.setPrivateKeyFilePath(RESOURCES_BASE_PATH + "/test-rsa_valid-key.txt");
        ApiAccessTokenConfiguration badConfig = new ApiAccessTokenConfigurationStub(config) {
            @Override
            public String getPrivateKeyFilePath() {
                throw new IllegalArgumentException();
            }
        };
        provider = new ServiceCredentialsApiAccessTokenProviderTestWrapper(badConfig);

        assertNull(provider.getAccessToken());
    }

    @Test
    void getAccessToken_shouldReturnNullWhenNoSuchAlgorithm() throws IOException {
        config.setPrivateKeyFilePath(RESOURCES_BASE_PATH + "/test-rsa_valid-key.txt");
        mockHttpClientResponse(ApiHttpResponse.builder().body(tokenWrapper).status(200).build());
        provider = new NoSuchAlgorithmProvider(config, httpClient, responseHandler, clock, tokenCache);

        assertNull(provider.getAccessToken());
    }

    @Test
    void getAccessToken_shouldHandleInvalidImsEndpoint() throws IOException {
        config.setImsEndpoint("not-a-uri");
        config.setPrivateKeyFilePath(RESOURCES_BASE_PATH + "/test-rsa_valid-key.txt");
        mockHttpClientResponse(ApiHttpResponse.builder().body(tokenWrapper).status(200).build());

        assertThat(provider.getAccessToken()).isEqualTo("abc123");
    }

    private void assertAccessTokenInquireRequestBody(HttpPut putRequest) throws IOException {
        String body = new String(putRequest.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> paramMap = getFormParametersMap(body);
        assertThat(paramMap).containsEntry("client_id", "clientId");
        assertThat(paramMap).containsEntry("client_secret", "clientSecret");
        assertThat(paramMap).containsKey("jwt_token");
        assertThat(paramMap.get("jwt_token")).isNotBlank();
    }

    private Map<String, String> getFormParametersMap(String body) {
        return Arrays.stream(body.split("&"))
                .map(s -> s.split("=", 2))
                .collect(Collectors.toMap(
                        kv -> URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                        kv -> kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : ""
                ));
    }

    private void mockHttpClientResponse(ApiHttpResponse<Object> response) throws IOException {
        lenient().when(httpClient.execute(any(HttpPut.class), any(HttpClientResponseHandler.class)))
                .thenReturn(response);
    }

    static class ServiceCredentialsApiAccessTokenProviderTestWrapper extends ServiceCredentialsApiAccessTokenProvider {

        public ServiceCredentialsApiAccessTokenProviderTestWrapper(ApiAccessTokenConfiguration apiAccessTokenConfiguration) {
            super(apiAccessTokenConfiguration);
        }

        public ServiceCredentialsApiAccessTokenProviderTestWrapper(
                ApiAccessTokenConfiguration apiAccessTokenConfiguration,
                CloseableHttpClient httpClient,
                HttpClientResponseHandler<ApiHttpResponse<AccessTokenWrapper>> responseHandlerFactory,
                Clock clock,
                TokenCache tokenCache) {
            super(apiAccessTokenConfiguration, httpClient, responseHandlerFactory, clock, tokenCache);
        }
    }

    static class NoSuchAlgorithmProvider extends ServiceCredentialsApiAccessTokenProviderTestWrapper {
        public NoSuchAlgorithmProvider(
                ApiAccessTokenConfiguration apiAccessTokenConfiguration,
                CloseableHttpClient httpClient,
                HttpClientResponseHandler<ApiHttpResponse<AccessTokenWrapper>> responseHandlerFactory,
                Clock clock,
                TokenCache tokenCache) {
            super(apiAccessTokenConfiguration, httpClient, responseHandlerFactory, clock, tokenCache);
        }

        @Override
        protected java.security.KeyFactory getKeyFactory() throws NoSuchAlgorithmException {
            throw new NoSuchAlgorithmException();
        }
    }

    static class MutableClock implements Clock {
        private Instant now;

        MutableClock(Instant now) {
            this.now = now;
        }

        void advance(Duration duration) {
            now = now.plus(duration);
        }

        void setNow(Instant now) {
            this.now = now;
        }

        @Override
        public Instant now() {
            return now;
        }
    }
}
