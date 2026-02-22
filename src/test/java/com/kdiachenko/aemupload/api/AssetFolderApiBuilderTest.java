package com.kdiachenko.aemupload.api;

import com.kdiachenko.aemupload.config.ApiServerConfiguration;
import com.kdiachenko.aemupload.utils.PathNormalizer;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

class AssetFolderApiBuilderTest {

    private ApiServerConfiguration apiServerConfiguration;
    private CloseableHttpClient httpClient;

    @BeforeEach
    void setUp() {
        apiServerConfiguration = new StubApiServerConfiguration("https", "example.com", "443");
        httpClient = Mockito.mock(CloseableHttpClient.class);
    }

    @Test
    void build_shouldCreateApiWithDefaultPathNormalizer() {
        AssetFolderApi api = AssetFolderApiBuilder.builder(apiServerConfiguration)
                .withHttpClient(httpClient)
                .build();

        assertThat(api).isNotNull();
    }

    @Test
    void build_shouldCreateApiWithCustomPathNormalizer() {
        PathNormalizer customNormalizer = path -> "/custom" + path;

        AssetFolderApi api = AssetFolderApiBuilder.builder(apiServerConfiguration)
                .withHttpClient(httpClient)
                .withPathNormalizer(customNormalizer)
                .build();

        assertThat(api).isNotNull();
    }

    @Test
    void builder_shouldReturnSameInstanceForChaining() {
        AssetFolderApiBuilder builder = AssetFolderApiBuilder.builder(apiServerConfiguration);

        AssetFolderApiBuilder result = builder.withPathNormalizer(path -> path);

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
