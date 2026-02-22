package com.kdiachenko.aemupload.api;

import com.kdiachenko.aemupload.config.ApiServerConfiguration;
import com.kdiachenko.aemupload.utils.FileSplitter;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DirectBinaryUploadApiBuilderTest {

    private ApiServerConfiguration apiServerConfiguration;
    private CloseableHttpClient httpClient;

    @BeforeEach
    void setUp() {
        apiServerConfiguration = new StubApiServerConfiguration("https", "example.com", "443");
        httpClient = Mockito.mock(CloseableHttpClient.class);
    }

    @Test
    void build_shouldCreateApiWithDefaultFileSplitter() {
        DirectBinaryUploadApi api = DirectBinaryUploadApiBuilder.builder(apiServerConfiguration)
                .withHttpClient(httpClient)
                .build();

        assertThat(api).isNotNull();
    }

    @Test
    void build_shouldCreateApiWithCustomFileSplitter() {
        FileSplitter customSplitter = (path, maxChunkSize) -> List.of(path);

        DirectBinaryUploadApi api = DirectBinaryUploadApiBuilder.builder(apiServerConfiguration)
                .withHttpClient(httpClient)
                .withFileSplitter(customSplitter)
                .build();

        assertThat(api).isNotNull();
    }

    @Test
    void builder_shouldReturnSameInstanceForChaining() {
        DirectBinaryUploadApiBuilder builder = DirectBinaryUploadApiBuilder.builder(apiServerConfiguration);

        DirectBinaryUploadApiBuilder result = builder.withFileSplitter((path, size) -> List.of(path));

        assertThat(result).isSameAs(builder);
    }

    private static class StubApiServerConfiguration implements ApiServerConfiguration {
        private final String schema;
        private final String host;
        private final String port;

        StubApiServerConfiguration(String schema, String host, String port) {
            this.schema = schema;
            this.host = host;
            this.port = port;
        }

        @Override
        public String getSchema() {
            return schema;
        }

        @Override
        public String getHost() {
            return host;
        }

        @Override
        public String getPort() {
            return port;
        }
    }
}
