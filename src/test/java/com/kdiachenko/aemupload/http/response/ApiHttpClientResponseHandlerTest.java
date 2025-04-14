package com.kdiachenko.aemupload.http.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdiachenko.aemupload.http.entity.ApiHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ApiHttpClientResponseHandlerTest {

    private ObjectMapper objectMapper;
    private ApiHttpClientResponseHandler<TestResponse> handler;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        handler = new ApiHttpClientResponseHandler<>(TestResponse.class, objectMapper);
    }

    @Test
    void handleEntity_shouldDeserializeJsonResponse() throws IOException {
        TestResponse expected = new TestResponse("hello");
        String json = objectMapper.writeValueAsString(expected);
        HttpEntity entity = new StringEntity(json);

        ApiHttpResponse<TestResponse> response = handler.handleEntity(entity);

        assertThat(response).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("hello");
    }

    @Test
    void handleEntity_shouldReturnEmptyForVoidType() throws IOException {
        ApiHttpClientResponseHandler<Void> voidHandler = new ApiHttpClientResponseHandler<>(Void.class, objectMapper);
        HttpEntity entity = new StringEntity("{}");

        ApiHttpResponse<Void> response = voidHandler.handleEntity(entity);

        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNull();
    }

    @Test
    void handleEntity_shouldThrowIOExceptionOnJsonParseException() {
        StringEntity entity = new StringEntity("wrong json");

        assertThrows(IOException.class, () -> handler.handleEntity(entity));
    }

    @Test
    void handleResponse_shouldThrowIOExceptionOnJsonParseException() throws IOException {
        BasicClassicHttpResponse response = new BasicClassicHttpResponse(200);
        response.setEntity(new StringEntity("wrong json"));

        assertThrows(IOException.class, () -> handler.handleResponse(response));
    }

    @Test
    void handleResponse_shouldReturnErrorResponseForHttpErrorCode() throws IOException {
        BasicClassicHttpResponse response = new BasicClassicHttpResponse(500, "Server Error");
        response.setEntity(new StringEntity("wrong json"));

        ApiHttpResponse<TestResponse> result = handler.handleResponse(response);

        assertThat(result.getStatus()).isEqualTo(500);
        assertThatJson(result.getErrorMessage())
                .isObject()
                .containsEntry("apiResponse", "wrong json")
                .containsEntry("reasonPhrase", "Server Error");
    }

    @Test
    void handleResponse_shouldReturnNullIfEntityIsNull() throws IOException {
        BasicClassicHttpResponse response = new BasicClassicHttpResponse(200);

        ApiHttpResponse<TestResponse> result = handler.handleResponse(response);

        assertThat(result).isNull();
    }

    @Test
    void handleResponse_shouldParseEntityAndReturnStatus() throws IOException {
        TestResponse expected = new TestResponse("hello");
        String json = objectMapper.writeValueAsString(expected);

        BasicClassicHttpResponse response = new BasicClassicHttpResponse(200);
        response.setEntity(new StringEntity(json));

        ApiHttpResponse<TestResponse> result = handler.handleResponse(response);

        assertThat(result).isNotNull();
        assertThat(result.getBody().getMessage()).isEqualTo("hello");
        assertThat(result.getStatus()).isEqualTo(200);
    }

    static class TestResponse {
        private String message;

        public TestResponse() {
        }

        public TestResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
