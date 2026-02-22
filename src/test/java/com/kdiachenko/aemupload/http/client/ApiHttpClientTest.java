package com.kdiachenko.aemupload.http.client;

import com.kdiachenko.aemupload.http.entity.ApiHttpContext;
import com.kdiachenko.aemupload.http.entity.ApiHttpEntity;
import com.kdiachenko.aemupload.http.entity.ApiHttpResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApiHttpClientTest {

    @Test
    void defaultMethods_shouldDelegateToContextVariants() {
        RecordingClient client = new RecordingClient();
        ApiHttpEntity<String> entity = new ApiHttpEntity<>("body", java.util.Map.of());

        client.get("/url", String.class);
        client.post("/url", entity, String.class);
        client.put("/url", entity, String.class);

        assertThat(client.getContext).isNull();
        assertThat(client.postContext).isNull();
        assertThat(client.putContext).isNull();
        assertThat(client.getCalled).isTrue();
        assertThat(client.postCalled).isTrue();
        assertThat(client.putCalled).isTrue();
    }

    private static class RecordingClient implements ApiHttpClient {
        private boolean getCalled;
        private boolean postCalled;
        private boolean putCalled;
        private ApiHttpContext getContext;
        private ApiHttpContext postContext;
        private ApiHttpContext putContext;

        @Override
        public <T> ApiHttpResponse<T> get(String url, ApiHttpContext apiHttpContext, Class<T> responseType) {
            getCalled = true;
            getContext = apiHttpContext;
            return ApiHttpResponse.<T>builder().status(200).build();
        }

        @Override
        public <E, R> ApiHttpResponse<R> post(String url, ApiHttpEntity<E> entity, ApiHttpContext apiHttpContext, Class<R> responseType) {
            postCalled = true;
            postContext = apiHttpContext;
            return ApiHttpResponse.<R>builder().status(200).build();
        }

        @Override
        public <E, R> ApiHttpResponse<R> put(String url, ApiHttpEntity<E> entity, ApiHttpContext apiHttpContext, Class<R> responseType) {
            putCalled = true;
            putContext = apiHttpContext;
            return ApiHttpResponse.<R>builder().status(200).build();
        }
    }
}
