package com.kdiachenko.aemupload.auth.impl;

import com.kdiachenko.aemupload.common.ApiAccessTokenConfigurationStub;
import com.kdiachenko.aemupload.config.ApiAccessTokenConfiguration;
import com.kdiachenko.aemupload.http.entity.ApiHttpResponse;
import org.apache.commons.lang3.time.DateUtils;
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
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceCredentialsApiAccessTokenProviderTest {

    public static final String RESOURCES_BASE_PATH = "src/test/resources/com/kdiachenko/aemupload/auth";

    private ApiAccessTokenConfigurationStub config;
    @Mock(strictness = Mock.Strictness.LENIENT)
    private CloseableHttpClient httpClient;
    @Mock(strictness = Mock.Strictness.LENIENT)
    private HttpClientResponseHandler<ApiHttpResponse<ServiceCredentialsApiAccessTokenProvider.AccessTokenWrapper>> responseHandler;
    @Captor
    private ArgumentCaptor<HttpPut> putRequestCaptor;

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
        provider = new ServiceCredentialsApiAccessTokenProviderTestWrapper(config, httpClient, responseHandler);
    }

    @Test
    void testGetAccessToken_whenTokenIsCachedAndValid_shouldReturnCached() {
        provider.setCachedAccessToken("cached_token");
        provider.setExpiration(new Date(System.currentTimeMillis() + 10000));

        assertEquals("cached_token", provider.getAccessToken());
    }

    @Test
    void testGetAccessToken_whenRsaPrivateKeyIsNull_shouldReturnNull() {
        assertNull(provider.getAccessToken());
    }

    @Test
    void testGetAccessToken_whenAccessTokenCausesInvalidKeySpecException_shouldReturnNull() {
        config.setPrivateKeyFilePath(RESOURCES_BASE_PATH + "/test-rsa_empty-key.txt");

        assertNull(provider.getAccessToken());
    }

    @Test
    void testGetAccessToken_whenRsaPrivateKeyFileIsNotExisting_shouldReturnNull() {
        config.setPrivateKeyFilePath(RESOURCES_BASE_PATH + "/test-rsa_not-existing-key.txt");

        assertNull(provider.getAccessToken());
    }

    @Test
    void testGetAccessToken_whenRsaPrivateKeyFileContentIsWrong_shouldReturnNull() {
        config.setPrivateKeyFilePath(RESOURCES_BASE_PATH + "/test-rsa_wrong-key.txt");

        assertNull(provider.getAccessToken());
    }

    @Test
    void testGetAccessToken_shouldReturnNewAccess() throws IOException {
        config.setPrivateKeyFilePath(RESOURCES_BASE_PATH + "/test-rsa_valid-key.txt");
        ApiHttpResponse<Object> response = ApiHttpResponse.builder().body(tokenWrapper).status(200).build();
        mockHttpClientResponse(response);

        assertThat(provider.getAccessToken()).isEqualTo("abc123");
        assertThat(provider.getCachedAccessToken()).isEqualTo("abc123");
        Date expectedExpiration = DateUtils.addMilliseconds(new Date(), Math.toIntExact(3600));
        assertThat(provider.getExpiration()).isCloseTo(expectedExpiration, 1000);
    }

    @Test
    void testGetAccessToken_whenTokenIsCachedAndExpired_shouldReturnNewToken() throws IOException {
        config.setPrivateKeyFilePath(RESOURCES_BASE_PATH + "/test-rsa_valid-key.txt");
        provider.setCachedAccessToken("cached_token");
        provider.setExpiration(new Date(System.currentTimeMillis() - 10000));

        ApiHttpResponse<Object> response = ApiHttpResponse.builder().body(tokenWrapper).status(200).build();
        mockHttpClientResponse(response);

        assertThat(provider.getAccessToken()).isEqualTo("abc123");
        assertThat(provider.getCachedAccessToken()).isEqualTo("abc123");
        Date expectedExpiration = DateUtils.addMilliseconds(new Date(), Math.toIntExact(3600));
        assertThat(provider.getExpiration()).isCloseTo(expectedExpiration, 1000);
    }

    @Test
    void testGetAccessToken_shouldReturnNewAccess_whenPrivateKeyContentIsDefined() throws IOException {
        Path privateKeyPath = Paths.get(RESOURCES_BASE_PATH + "/test-rsa_valid-key.txt");
        String privateFileContent = String.join("", Files.readAllLines(privateKeyPath));
        config.setPrivateKeyContent(privateFileContent);
        ApiHttpResponse<Object> response = ApiHttpResponse.builder().body(tokenWrapper).status(200).build();
        mockHttpClientResponse(response);

        assertThat(provider.getAccessToken()).isEqualTo("abc123");
        assertThat(provider.getCachedAccessToken()).isEqualTo("abc123");
        Date expectedExpiration = DateUtils.addMilliseconds(new Date(), Math.toIntExact(3600));
        assertThat(provider.getExpiration()).isCloseTo(expectedExpiration, 1000);
    }

    @Test
    void testGetAccessToken_shouldSendCorrectAccessTokenRequest() throws IOException, URISyntaxException {
        config.setPrivateKeyFilePath(RESOURCES_BASE_PATH + "/test-rsa_valid-key.txt");
        ApiHttpResponse<Object> response = ApiHttpResponse.builder().body(tokenWrapper).status(200).build();
        mockHttpClientResponse(response);

        provider.getAccessToken();

        verify(httpClient).execute(putRequestCaptor.capture(), any(HttpClientResponseHandler.class));
        HttpPut putRequest = putRequestCaptor.getValue();
        assertThat(putRequest.getScheme()).isEqualTo("https");
        assertThat(putRequest.getPath()).isEqualTo("/ims/exchange/jwt");
        assertThat(putRequest.getRequestUri()).isEqualTo("/ims/exchange/jwt");
        assertThat(putRequest.getFirstHeader("Content-Type"))
                .isNotNull().extracting(Header::getValue).isEqualTo("application/x-www-form-urlencoded; charset=ISO-8859-1");
        assertAccessTokenInquireRequestBody(putRequest);
    }

    @Test
    void testGetAccessToken_whenAccessTokenResponseStatusIsNotOk_shouldReturnNull() throws IOException {
        ApiHttpResponse<Object> response = ApiHttpResponse.builder()
                .status(302)
                .build();
        mockHttpClientResponse(response);
        config.setPrivateKeyFilePath(RESOURCES_BASE_PATH + "/test-rsa_valid-key.txt");

        assertNull(provider.getAccessToken());
    }

    @Test
    void testGetAccessToken_whenHttpClientIsReal_shouldReturnNull() {
        config.setPrivateKeyFilePath(RESOURCES_BASE_PATH + "/test-rsa_valid-key.txt");

        provider = new ServiceCredentialsApiAccessTokenProviderTestWrapper(config);

        assertNull(provider.getAccessToken());
    }

    @Test
    void testGetAccessToken_whenAccessTokenResponseHasEmptyResponse_shouldReturnNull() throws IOException {
        config.setPrivateKeyFilePath(RESOURCES_BASE_PATH + "/test-rsa_valid-key.txt");
        ApiHttpResponse<Object> response = ApiHttpResponse.builder().status(200).build();
        mockHttpClientResponse(response);

        assertNull(provider.getAccessToken());
    }

    @Test
    void testGetAccessToken_whenAccessTokenCausesIOException_shouldReturnNull() throws IOException {
        config.setPrivateKeyFilePath(RESOURCES_BASE_PATH + "/test-rsa_valid-key.txt");
        doThrow(IOException.class).when(httpClient).execute(any(HttpPut.class), any(HttpClientResponseHandler.class));

        assertNull(provider.getAccessToken());
    }

    @Test
    void testGetAccessToken_whenAccessTokenCausesException_shouldReturnNull() throws IOException {
        config.setPrivateKeyFilePath(RESOURCES_BASE_PATH + "/test-rsa_valid-key.txt");
        config = new ApiAccessTokenConfigurationStub(config) {
            @Override
            public String getPrivateKeyFilePath() {
                throw new IllegalArgumentException();
            }
        };
        provider = new ServiceCredentialsApiAccessTokenProviderTestWrapper(config);

        assertNull(provider.getAccessToken());
    }

    @Test
    void testGetAccessToken_whenAccessTokenCausesNoSuchAlgorithmException_shouldReturnNull() throws NoSuchAlgorithmException, IOException {
        config.setPrivateKeyFilePath(RESOURCES_BASE_PATH + "/test-rsa_valid-key.txt");
        ApiHttpResponse<Object> response = ApiHttpResponse.builder().body(tokenWrapper).status(200).build();
        mockHttpClientResponse(response);
        try (MockedStatic<KeyFactory> keyFactoryMockedStatic = mockStatic(KeyFactory.class)) {
            when(KeyFactory.getInstance("RSA")).thenThrow(new NoSuchAlgorithmException());

            assertNull(provider.getAccessToken());
        }
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
        when(httpClient.execute(any(HttpPut.class), any(HttpClientResponseHandler.class)))
                .thenReturn(response);
    }

    static class ServiceCredentialsApiAccessTokenProviderTestWrapper extends ServiceCredentialsApiAccessTokenProvider {

        public ServiceCredentialsApiAccessTokenProviderTestWrapper(ApiAccessTokenConfiguration apiAccessTokenConfiguration) {
            super(apiAccessTokenConfiguration);
        }

        public ServiceCredentialsApiAccessTokenProviderTestWrapper(
                ApiAccessTokenConfiguration apiAccessTokenConfiguration,
                CloseableHttpClient httpClient,
                HttpClientResponseHandler<ApiHttpResponse<AccessTokenWrapper>> responseHandlerFactory) {
            super(apiAccessTokenConfiguration, httpClient, responseHandlerFactory);
        }
    }
}
