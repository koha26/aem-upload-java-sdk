package com.kdiachenko.aemupload.http.client.impl;

import com.kdiachenko.aemupload.http.entity.ApiHttpContext;
import com.kdiachenko.aemupload.http.entity.ApiHttpEntity;
import com.kdiachenko.aemupload.http.entity.ApiHttpResponse;
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

import java.io.BufferedInputStream;
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
    void testGet() throws IOException {
        var url = "https://api.host/v1/api/call";
        var response = ApiHttpResponse.<String>builder().status(200).body("{\"data\": {}}").build();
        when(httpClient.execute(any(HttpGet.class), any(), any())).thenReturn(response);

        var result = apiHttpClient.get(url, null, String.class);

        assertThat(result.getBody()).isEqualTo("{\"data\": {}}");
        assertThat(result.getStatus()).isEqualTo(200);
        assertThat(result.getErrorMessage()).isNull();
    }

    @Test
    void testGetWithException() throws IOException {
        var url = "https://api.host/v1/api/call";
        doThrow(IOException.class).when(httpClient).execute(any(HttpGet.class), any(), any());

        var result = apiHttpClient.get(url, null, String.class);

        assertThat(result.getStatus()).isEqualTo(500);
        assertThat(result.getErrorMessage()).isEqualTo("Error while executing request: GET /v1/api/call");
        assertThat(result.getBody()).isNull();
    }

    @Test
    void testGetWithContext() throws IOException {
        var url = "https://api.host/v1/api/call";
        var response = ApiHttpResponse.<String>builder().status(200).body("{\"data\": {}}").build();
        when(httpClient.execute(any(HttpGet.class), any(), any())).thenReturn(response);
        var apiHttpContext = ApiHttpContext.builder()
                .attributes(Map.of("debug", "true"))
                .build();
        var result = apiHttpClient.get(url, apiHttpContext, String.class);

        assertThat(result.getBody()).isEqualTo("{\"data\": {}}");
        assertThat(result.getStatus()).isEqualTo(200);
        assertThat(result.getErrorMessage()).isNull();
        verify(httpClient).execute(any(), httpClientContextArgumentCaptor.capture(), any());
        HttpClientContext context = httpClientContextArgumentCaptor.getValue();
        assertThat(context).isNotNull();
        assertThat(context.getAttribute("debug")).isEqualTo("true");
    }

    @Test
    void testGetWithEmptyContext() throws IOException {
        var url = "https://api.host/v1/api/call";
        var response = ApiHttpResponse.<String>builder().status(200).body("{\"data\": {}}").build();
        when(httpClient.execute(any(HttpGet.class), any(), any())).thenReturn(response);
        var apiHttpContext = ApiHttpContext.builder().build();
        var result = apiHttpClient.get(url, apiHttpContext, String.class);

        assertThat(result.getBody()).isEqualTo("{\"data\": {}}");
        assertThat(result.getStatus()).isEqualTo(200);
        assertThat(result.getErrorMessage()).isNull();
        verify(httpClient).execute(any(), httpClientContextArgumentCaptor.capture(), any());
        HttpClientContext context = httpClientContextArgumentCaptor.getValue();
        assertThat(context).isNull();
    }

    @Test
    void testPostWithMap() throws IOException {
        var url = "https://api.host/v1/api/call";
        var entity = new ApiHttpEntity<>(
                Map.of("key", "value"),
                Map.of(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
        );
        var response = ApiHttpResponse.<String>builder().status(201).body("{\"data\": {}}").build();
        when(httpClient.execute(any(HttpPost.class), any(), any())).thenReturn(response);

        var result = apiHttpClient.post(url, entity, null, String.class);

        assertThat(result.getStatus()).isEqualTo(201);
        assertThat(result.getBody()).isEqualTo("{\"data\": {}}");
        assertThat(result.getErrorMessage()).isNull();
    }

    @Test
    void testPostWithMap_CorrectRequestWithFormUrlEncodedParameters() throws IOException {
        var url = "https://api.host/v1/api/call";
        var entity = new ApiHttpEntity<>(
                Map.of("key", "value"),
                Map.of(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
        );
        var response = ApiHttpResponse.<String>builder().status(201).body("{\"data\": {}}").build();
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
    void testPostWithInputStream() throws IOException {
        var url = "https://api.host/v1/api/call";
        InputStream stream = new ByteArrayInputStream("data".getBytes());
        var entity = new ApiHttpEntity<>(
                stream,
                Map.of(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED.getMimeType())
        );
        var response = ApiHttpResponse.<String>builder().status(200).body("{\"data\": {}}").build();
        when(httpClient.execute(any(HttpPost.class), any(), any())).thenReturn(response);

        var result = apiHttpClient.post(url, entity, null, String.class);

        assertThat(result.getStatus()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo("{\"data\": {}}");
        assertThat(result.getErrorMessage()).isNull();
    }

    @Test
    void testPostWithInputStream_CorrectRequest() throws IOException {
        var url = "https://api.host/v1/api/call";
        InputStream stream = new ByteArrayInputStream("data".getBytes(StandardCharsets.UTF_8));
        var entity = new ApiHttpEntity<>(
                stream,
                Map.of(HttpHeaders.CONTENT_TYPE, ContentType.IMAGE_WEBP.getMimeType())
        );
        var response = ApiHttpResponse.<String>builder().status(200).body("{\"data\": {}}").build();
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
    void testPostWithInputStream_IOException() throws IOException {
        var url = "https://api.host/v1/api/call";
        InputStream stream = new BufferedInputStream(new ByteArrayInputStream("data".getBytes())) {
            @Override
            public byte[] readAllBytes() throws IOException {
                throw new IOException();
            }
        };
        var entity = new ApiHttpEntity<>(
                stream,
                Map.of(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED.getMimeType())
        );
        var result = apiHttpClient.post(url, entity, null, String.class);

        assertThat(result.getStatus()).isEqualTo(500);
        assertThat(result.getBody()).isNull();
        assertThat(result.getErrorMessage()).isEqualTo("Error while executing request");
    }

    @Test
    void testPostWithObject() throws IOException {
        var url = "https://api.host/v1/api/call";
        var entity = new ApiHttpEntity<>(
                new TestObject("hello world"),
                Map.of(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
        );
        var response = ApiHttpResponse.<String>builder().status(200).body("{\"data\": {}}").build();
        when(httpClient.execute(any(HttpPost.class), any(), any())).thenReturn(response);

        var result = apiHttpClient.post(url, entity, null, String.class);

        assertThat(result.getStatus()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo("{\"data\": {}}");
        assertThat(result.getErrorMessage()).isNull();
    }

    @Test
    void testPostWithObject_CorrectRequest() throws IOException {
        var url = "https://api.host/v1/api/call";
        var entity = new ApiHttpEntity<>(
                new TestObject("hello world"),
                Map.of(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
        );
        var response = ApiHttpResponse.<String>builder().status(200).body("{\"data\": {}}").build();
        when(httpClient.execute(any(HttpPost.class), any(), any())).thenReturn(response);

        apiHttpClient.post(url, entity, null, String.class);

        verify(httpClient).execute(httpRequestArgumentCaptor.capture(), any(), any());
        HttpUriRequestBase request = httpRequestArgumentCaptor.getValue();
        HttpEntity requestEntity = request.getEntity();
        assertThat(request.getFirstHeader(HttpHeaders.CONTENT_TYPE).getValue()).isEqualTo("application/json");
        assertThat(requestEntity.getContentType()).isEqualTo("application/json; charset=UTF-8");
        assertThat(requestEntity.getContent()).asString(StandardCharsets.UTF_8).isEqualTo("{\"data\":\"hello world\"}");
    }

    @Test
    void testPostWithObject_IOException() throws IOException {
        var url = "https://api.host/v1/api/call";
        var entity = new ApiHttpEntity<>(
                new TestObject("hello world") {
                    @Override
                    public String getData() {
                        throw new IllegalArgumentException();
                    }
                },
                Map.of(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
        );

        var result = apiHttpClient.post(url, entity, null, String.class);

        assertThat(result.getStatus()).isEqualTo(500);
        assertThat(result.getBody()).isNull();
        assertThat(result.getErrorMessage()).isEqualTo("Error while executing request");
    }

    @Test
    void testPostWithNullBody() throws IOException {
        var url = "https://api.host/v1/api/call";
        var entity = new ApiHttpEntity<>(
                null,
                Map.of(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
        );
        var response = ApiHttpResponse.<String>builder().status(200).body("{\"data\": {}}").build();
        when(httpClient.execute(any(HttpPost.class), any(), any())).thenReturn(response);

        var result = apiHttpClient.post(url, entity, null, String.class);

        assertThat(result.getStatus()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo("{\"data\": {}}");
        assertThat(result.getErrorMessage()).isNull();
    }

    @Test
    void testPostWithException() throws IOException {
        var url = "https://api.host/v1/api/call";
        var entity = new ApiHttpEntity<>(
                new TestObject("hello world"),
                Map.of(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
        );
        doThrow(IOException.class).when(httpClient).execute(any(HttpPost.class), any(), any());

        var result = apiHttpClient.post(url, entity, null, String.class);

        assertThat(result.getStatus()).isEqualTo(500);
        assertThat(result.getBody()).isNull();
        assertThat(result.getErrorMessage()).isEqualTo("Error while executing request: POST /v1/api/call");
    }

    @Test
    void testPutWithMap() throws IOException {
        var url = "https://api.host/v1/api/call";
        var entity = new ApiHttpEntity<>(
                Map.of("key", "value"),
                Map.of(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
        );
        var response = ApiHttpResponse.<String>builder().status(201).body("{\"data\": {}}").build();
        when(httpClient.execute(any(HttpPut.class), any(), any())).thenReturn(response);

        var result = apiHttpClient.put(url, entity, null, String.class);

        assertThat(result.getStatus()).isEqualTo(201);
        assertThat(result.getBody()).isEqualTo("{\"data\": {}}");
        assertThat(result.getErrorMessage()).isNull();
    }

    @Test
    void testPutWithInputStream() throws IOException {
        var url = "https://api.host/v1/api/call";
        InputStream stream = new ByteArrayInputStream("data".getBytes());
        var entity = new ApiHttpEntity<>(
                stream,
                Map.of(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED.getMimeType())
        );
        var response = ApiHttpResponse.<String>builder().status(200).body("{\"data\": {}}").build();
        when(httpClient.execute(any(HttpPut.class), any(), any())).thenReturn(response);

        var result = apiHttpClient.put(url, entity, null, String.class);

        assertThat(result.getStatus()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo("{\"data\": {}}");
        assertThat(result.getErrorMessage()).isNull();
    }

    @Test
    void testPutWithInputStream_IOException() throws IOException {
        var url = "https://api.host/v1/api/call";
        InputStream stream = new BufferedInputStream(new ByteArrayInputStream("data".getBytes())) {
            @Override
            public byte[] readAllBytes() throws IOException {
                throw new IOException();
            }
        };
        var entity = new ApiHttpEntity<>(
                stream,
                Map.of(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED.getMimeType())
        );
        var result = apiHttpClient.put(url, entity, null, String.class);

        assertThat(result.getStatus()).isEqualTo(500);
        assertThat(result.getBody()).isNull();
        assertThat(result.getErrorMessage()).isEqualTo("Error while executing request");
    }

    @Test
    void testPutWithObject() throws IOException {
        var url = "https://api.host/v1/api/call";
        var entity = new ApiHttpEntity<>(
                new TestObject("hello world"),
                Map.of(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
        );
        var response = ApiHttpResponse.<String>builder().status(200).body("{\"data\": {}}").build();
        when(httpClient.execute(any(HttpPut.class), any(), any())).thenReturn(response);

        var result = apiHttpClient.put(url, entity, null, String.class);

        assertThat(result.getStatus()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo("{\"data\": {}}");
        assertThat(result.getErrorMessage()).isNull();
    }

    @Test
    void testPutWithObject_IOException() throws IOException {
        var url = "https://api.host/v1/api/call";
        var entity = new ApiHttpEntity<>(
                new TestObject("hello world") {
                    @Override
                    public String getData() {
                        throw new IllegalArgumentException();
                    }
                },
                Map.of(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
        );

        var result = apiHttpClient.put(url, entity, null, String.class);

        assertThat(result.getStatus()).isEqualTo(500);
        assertThat(result.getBody()).isNull();
        assertThat(result.getErrorMessage()).isEqualTo("Error while executing request");
    }

    @Test
    void testPutWithNullBody() throws IOException {
        var url = "https://api.host/v1/api/call";
        var entity = new ApiHttpEntity<>(
                null,
                Map.of(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
        );
        var response = ApiHttpResponse.<String>builder().status(200).body("{\"data\": {}}").build();
        when(httpClient.execute(any(HttpPut.class), any(), any())).thenReturn(response);

        var result = apiHttpClient.put(url, entity, null, String.class);

        assertThat(result.getStatus()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo("{\"data\": {}}");
        assertThat(result.getErrorMessage()).isNull();
    }

    @Test
    void testPutWithException() throws IOException {
        var url = "https://api.host/v1/api/call";
        var entity = new ApiHttpEntity<>(
                new TestObject("hello world"),
                Map.of(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
        );
        doThrow(IOException.class).when(httpClient).execute(any(HttpPut.class), any(), any());

        var result = apiHttpClient.put(url, entity, null, String.class);

        assertThat(result.getStatus()).isEqualTo(500);
        assertThat(result.getBody()).isNull();
        assertThat(result.getErrorMessage()).isEqualTo("Error while executing request: PUT /v1/api/call");
    }

    @Data
    @AllArgsConstructor
    static class TestObject {
        private String data;
    }
}
