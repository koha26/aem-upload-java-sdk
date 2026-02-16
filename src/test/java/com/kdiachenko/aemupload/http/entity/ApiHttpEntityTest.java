package com.kdiachenko.aemupload.http.entity;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ApiHttpEntityTest {

    @Test
    void apiHttpEntity_shouldHaveDefaultHeaders() {
        ApiHttpEntity<String> entity = new ApiHttpEntity<>();

        assertThat(entity.getHeaders()).isEmpty();

        ApiHttpEntity<String> built = ApiHttpEntity.<String>builder().body("data").build();
        assertThat(built.getHeaders()).isNotNull();
        assertThat(built.getHeaders()).isEmpty();
    }

    @Test
    void apiHttpContext_shouldDefaultToEmptyAttributes() {
        ApiHttpContext context = ApiHttpContext.builder().build();

        assertThat(context.getAttributes()).isNotNull();
        assertThat(context.getAttributes()).isEmpty();
    }

    @Test
    void httpContexts_shouldProvidePredefinedInstances() {
        assertThat(HttpContexts.AUTHORIZED.getAttributes())
                .containsEntry(HttpContexts.AUTHORIZATION_REQUIRED_ATTR, "true");
        assertThat(HttpContexts.ANONYMOUS.getAttributes()).isEmpty();
    }

    @Test
    void apiHttpResponse_shouldReportSuccessFor2xx() {
        assertThat(ApiHttpResponse.builder().status(199).build().isSuccess()).isFalse();
        assertThat(ApiHttpResponse.builder().status(200).build().isSuccess()).isTrue();
        assertThat(ApiHttpResponse.builder().status(299).build().isSuccess()).isTrue();
        assertThat(ApiHttpResponse.builder().status(300).build().isSuccess()).isFalse();
    }

    @Test
    void apiHttpEntity_shouldPreserveHeaders() {
        ApiHttpEntity<String> entity = new ApiHttpEntity<>("body", Map.of("h", "v"));

        assertThat(entity.getHeaders()).containsEntry("h", "v");
    }
}
