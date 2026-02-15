package com.kdiachenko.aemupload.http.client.impl;

import com.kdiachenko.aemupload.exception.SerializationException;
import com.kdiachenko.aemupload.http.client.HttpClientObjectMapper;
import com.kdiachenko.aemupload.http.entity.ApiHttpContext;
import com.kdiachenko.aemupload.http.entity.ApiHttpEntity;
import com.kdiachenko.aemupload.http.entity.ApiHttpResponse;
import com.kdiachenko.aemupload.http.response.ApiHttpClientResponseHandlerFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiHttpClientImplTest {

    @Mock
    private CloseableHttpClient httpClient;
    @Captor
    private ArgumentCaptor<HttpUriRequestBase> httpRequestArgumentCaptor;
    @Captor
    private ArgumentCaptor<HttpClientContext> httpClientContextArgumentCaptor;
    private ApiHttpClientImpl apiHttpClient;

    @BeforeEach
    void setUp() {
        apiHttpClient = new ApiHttpClientImpl(httpClient);
    }

    @Test
    void get_shouldReturnResponse() throws IOException {
        var url = "https://api.host/v1/api/call";
        var response = ApiHttpResponse.<String>builder().status(200).body("{\"data\": {}} ").build();
        when(httpClient.execute(any(HttpGet.class), any(), any())).thenReturn(response);

        var result = apiHttpClient.get(url, null, String.class);

        assertThat(result.getBody()).isEqualTo("{\"data\": {}} ");
        assertThat(result.getStatus()).isEqualTo(200);
        assertThat(result.getErrorMessage()).isNull();
    }

    @Test
    void get_shouldHandleIOException() throws IOException {
        var url = "https://api.host/v1/api/call";
        doThrow(new IOException("boom")).when(httpClient).execute(any(HttpGet.class), any(), any());

        var result = apiHttpClient.get(url, null, String.class);

        assertThat(result.getStatus()).isEqualTo(500);
        assertThat(result.getErrorMessage()).contains("Error while executing request: GET /v1/api/call");
        assertThat(result.getErrorMessage()).contains("Cause: boom");
        assertThat(result.getBody()).isNull();
    }

    @Test
    void get_shouldPopulateHttpClientContext() throws IOException {
        var url = "https://api.host/v1/api/call";
        var response = ApiHttpResponse.<String>builder().status(200).body("ok").build();
        when(httpClient.execute(any(HttpGet.class), any(), any())).thenReturn(response);
        var apiHttpContext = ApiHttpContext.builder()
                .attributes(Map.of("debug", "true"))
                .build();

        apiHttpClient.get(url, apiHttpContext, String.class);

        verify(httpClient).execute(any(), httpClientContextArgumentCaptor.capture(), any());
        HttpClientContext context = httpClientContextArgumentCaptor.getValue();
        assertThat(context).isNotNull();
        assertThat(context.getAttribute("debug")).isEqualTo("true");
    }

    @Test
    void get_shouldIgnoreEmptyContext() throws IOException {
        var url = "https://api.host/v1/api/call";
        var response = ApiHttpResponse.<String>builder().status(200).body("ok").build();
        when(httpClient.execute(any(HttpGet.class), any(), any())).thenReturn(response);
        var apiHttpContext = ApiHttpContext.builder().build();

        apiHttpClient.get(url, apiHttpContext, String.class);

        verify(httpClient).execute(any(), httpClientContextArgumentCaptor.capture(), any());
        HttpClientContext context = httpClientContextArgumentCaptor.getValue();
        assertThat(context).isNull();
    }

    @Test
    void post_shouldSendMapAsFormData() throws IOException {
        var url = "https://api.host/v1/api/call";
        var entity = new ApiHttpEntity<>(
                Map.of("key", "value"),
                Map.of(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
        );
        var response = ApiHttpResponse.<String>builder().status(201).body("{\"data\": {}} ").build();
        when(httpClient.execute(any(HttpPost.class), any(), any())).thenReturn(response);

        apiHttpClient.post(url, entity, null, String.class);

        verify(httpClient).execute(httpRequestArgumentCaptor.capture(), any(), any());
        HttpUriRequestBase request = httpRequestArgumentCaptor.getValue();
        HttpEntity requestEntity = request.getEntity();
        assertThat(request.getFirstHeader(HttpHeaders.CONTENT_TYPE).getValue()).isEqualTo("application/json");
        assertThat(requestEntity.getContentType()).isEqualTo("application/x-www-form-urlencoded; charset=ISO-8859-1");
        assertThat(requestEntity.getContent()).asString(StandardCharsets.UTF_8).isEqualTo("key=value");
    }

    @Test
    void post_shouldSendInputStreamWithContentType() throws IOException {
        var url = "https://api.host/v1/api/call";
        InputStream stream = new ByteArrayInputStream("data".getBytes(StandardCharsets.UTF_8));
        var entity = new ApiHttpEntity<>(
                stream,
                Map.of(HttpHeaders.CONTENT_TYPE, ContentType.IMAGE_WEBP.getMimeType())
        );
        var response = ApiHttpResponse.<String>builder().status(200).body("{\"data\": {}} ").build();
        when(httpClient.execute(any(HttpPost.class), any(), any())).thenReturn(response);

        apiHttpClient.post(url, entity, null, String.class);

        verify(httpClient).execute(httpRequestArgumentCaptor.capture(), any(), any());
        HttpUriRequestBase request = httpRequestArgumentCaptor.getValue();
        HttpEntity requestEntity = request.getEntity();
        assertThat(request.getFirstHeader(HttpHeaders.CONTENT_TYPE).getValue()).isEqualTo("image/webp");
        assertThat(requestEntity.getContentType()).isEqualTo("image/webp");
        assertThat(requestEntity.getContent()).asString(StandardCharsets.UTF_8).isEqualTo("data");
    }

    @Test
    void post_shouldSerializeObjectToJson() throws IOException {
        var url = "https://api.host/v1/api/call";
        var entity = new ApiHttpEntity<>(
                new TestObject("hello world"),
                Map.of(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
        );
        var response = ApiHttpResponse.<String>builder().status(200).body("ok").build();
        when(httpClient.execute(any(HttpPost.class), any(), any())).thenReturn(response);

        apiHttpClient.post(url, entity, null, String.class);

        verify(httpClient).execute(httpRequestArgumentCaptor.capture(), any(), any());
        HttpUriRequestBase request = httpRequestArgumentCaptor.getValue();
        HttpEntity requestEntity = request.getEntity();
        assertThat(requestEntity.getContentType()).isEqualTo("application/json; charset=UTF-8");
        assertThat(requestEntity.getContent()).asString(StandardCharsets.UTF_8).contains("\"data\":\"hello world\"");
    }

    @Test
    void post_shouldDefaultContentTypeForInputStream() throws IOException {
        var url = "https://api.host/v1/api/call";
        InputStream stream = new ByteArrayInputStream("data".getBytes(StandardCharsets.UTF_8));
        var entity = new ApiHttpEntity<>(
                stream,
                Map.of()
        );
        var response = ApiHttpResponse.<String>builder().status(200).body("ok").build();
        when(httpClient.execute(any(HttpPost.class), any(), any())).thenReturn(response);

        apiHttpClient.post(url, entity, null, String.class);

        verify(httpClient).execute(httpRequestArgumentCaptor.capture(), any(), any());
        HttpUriRequestBase request = httpRequestArgumentCaptor.getValue();
        HttpEntity requestEntity = request.getEntity();
        assertThat(request.getFirstHeader(HttpHeaders.CONTENT_TYPE)).isNull();
        assertThat(requestEntity.getContentType()).isEqualTo("application/octet-stream");
    }

    @Test
    void post_shouldHandleSerializationErrors() {
        ApiHttpClientImpl client = new ApiHttpClientImpl(
                httpClient,
                new FailingObjectMapper(),
                ApiHttpClientResponseHandlerFactory.create(new FailingObjectMapper())
        );
        var url = "https://api.host/v1/api/call";
        var entity = new ApiHttpEntity<>(
                new TestObject("hello world"),
                Map.of(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
        );

        var result = client.post(url, entity, null, String.class);

        assertThat(result.getStatus()).isEqualTo(500);
        assertThat(result.getBody()).isNull();
        assertThat(result.getErrorMessage()).startsWith("Unexpected error while executing request:");
    }

    @Test
    void post_shouldHandleSerializationErrorsWithoutIoCause() {
        HttpClientObjectMapper mapper = new HttpClientObjectMapper() {
            @Override
            public <T> String serialize(T object) throws SerializationException {
                throw new SerializationException("serialize failed");
            }

            @Override
            public <T> T deserialize(String json, Class<T> type) throws SerializationException {
                throw new SerializationException("deserialize failed");
            }
        };
        ApiHttpClientImpl client = new ApiHttpClientImpl(
                httpClient,
                mapper,
                ApiHttpClientResponseHandlerFactory.create(mapper)
        );
        var url = "https://api.host/v1/api/call";
        var entity = new ApiHttpEntity<>(
                new TestObject("hello world"),
                Map.of(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
        );

        var result = client.post(url, entity, null, String.class);

        assertThat(result.getStatus()).isEqualTo(500);
        assertThat(result.getBody()).isNull();
        assertThat(result.getErrorMessage()).startsWith("Unexpected error while executing request:");
    }

    @Test
    void post_shouldHandleIOExceptionFromHttpClient() throws IOException {
        var url = "https://api.host/v1/api/call";
        var entity = new ApiHttpEntity<>(
                new TestObject("hello world"),
                Map.of(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
        );
        doThrow(new IOException("boom")).when(httpClient).execute(any(HttpPost.class), any(), any());

        var result = apiHttpClient.post(url, entity, null, String.class);

        assertThat(result.getStatus()).isEqualTo(500);
        assertThat(result.getBody()).isNull();
        assertThat(result.getErrorMessage()).contains("Error while executing request: POST /v1/api/call");
    }

    @Test
    void put_shouldHandleSerializationErrors() {
        ApiHttpClientImpl client = new ApiHttpClientImpl(
                httpClient,
                new FailingObjectMapper(),
                ApiHttpClientResponseHandlerFactory.create(new FailingObjectMapper())
        );
        var url = "https://api.host/v1/api/call";
        var entity = new ApiHttpEntity<>(
                new TestObject("hello world"),
                Map.of(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
        );

        var result = client.put(url, entity, null, String.class);

        assertThat(result.getStatus()).isEqualTo(500);
        assertThat(result.getBody()).isNull();
        assertThat(result.getErrorMessage()).startsWith("Unexpected error while executing request:");
    }

    @Test
    void put_shouldHandleIOExceptionFromHttpClient() throws IOException {
        var url = "https://api.host/v1/api/call";
        var entity = new ApiHttpEntity<>(
                new TestObject("hello world"),
                Map.of(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
        );
        doThrow(new IOException("boom")).when(httpClient).execute(any(HttpPut.class), any(), any());

        var result = apiHttpClient.put(url, entity, null, String.class);

        assertThat(result.getStatus()).isEqualTo(500);
        assertThat(result.getBody()).isNull();
        assertThat(result.getErrorMessage()).contains("Error while executing request: PUT /v1/api/call");
    }

    @Data
    @AllArgsConstructor
    static class TestObject {
        private String data;
    }

    static class FailingObjectMapper implements HttpClientObjectMapper {
        @Override
        public <T> String serialize(T object) throws SerializationException {
            throw new SerializationException("serialize failed", new IOException("io"));
        }

        @Override
        public <T> T deserialize(String json, Class<T> type) throws SerializationException {
            throw new SerializationException("deserialize failed");
        }
    }
}
