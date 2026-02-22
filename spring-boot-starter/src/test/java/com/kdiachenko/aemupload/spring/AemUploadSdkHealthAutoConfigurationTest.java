package com.kdiachenko.aemupload.spring;

import com.kdiachenko.aemupload.api.AssetFolderApi;
import com.kdiachenko.aemupload.exception.SdkError;
import com.kdiachenko.aemupload.model.AssetApiResponse;
import com.kdiachenko.aemupload.model.AssetElement;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AemUploadSdkHealthAutoConfigurationTest {

    @Test
    void healthIndicator_shouldReportUpWhenApiSuccess() {
        AssetFolderApi api = mock(AssetFolderApi.class);
        when(api.getFolder("/content/dam"))
                .thenReturn(AssetApiResponse.success(new AssetElement()));

        AemUploadSdkHealthAutoConfiguration.AemUploadSdkHealthIndicator indicator =
                new AemUploadSdkHealthAutoConfiguration.AemUploadSdkHealthIndicator(api, "https://example.com");

        Health health = indicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails()).containsEntry("server", "https://example.com");
        assertThat(health.getDetails()).containsEntry("status", "Connected");
    }

    @Test
    void healthIndicator_shouldReportDownWithStatusCode() {
        AssetFolderApi api = mock(AssetFolderApi.class);
        when(api.getFolder("/content/dam"))
                .thenReturn(AssetApiResponse.fail(SdkError.apiError("bad", 500)));

        AemUploadSdkHealthAutoConfiguration.AemUploadSdkHealthIndicator indicator =
                new AemUploadSdkHealthAutoConfiguration.AemUploadSdkHealthIndicator(api, "https://example.com");

        Health health = indicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails()).containsEntry("server", "https://example.com");
        assertThat(health.getDetails()).containsEntry("error", "bad");
        assertThat(health.getDetails()).containsEntry("statusCode", 500);
    }

    @Test
    void healthIndicator_shouldReportDownWithoutStatusCodeWhenMissing() {
        AssetFolderApi api = mock(AssetFolderApi.class);
        when(api.getFolder("/content/dam"))
                .thenReturn(AssetApiResponse.fail(SdkError.apiError("bad", 0)));

        AemUploadSdkHealthAutoConfiguration.AemUploadSdkHealthIndicator indicator =
                new AemUploadSdkHealthAutoConfiguration.AemUploadSdkHealthIndicator(api, "https://example.com");

        Health health = indicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails()).containsEntry("error", "bad");
        assertThat(health.getDetails()).doesNotContainKey("statusCode");
    }

    @Test
    void healthIndicator_shouldReportDownOnException() {
        AssetFolderApi api = mock(AssetFolderApi.class);
        when(api.getFolder("/content/dam")).thenThrow(new RuntimeException("boom"));

        AemUploadSdkHealthAutoConfiguration.AemUploadSdkHealthIndicator indicator =
                new AemUploadSdkHealthAutoConfiguration.AemUploadSdkHealthIndicator(api, "https://example.com");

        Health health = indicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails()).containsEntry("server", "https://example.com");
    }

    @Test
    void healthIndicatorBean_shouldBeCreated() {
        AssetFolderApi api = mock(AssetFolderApi.class);
        AemUploadSdkProperties properties = new AemUploadSdkProperties();
        properties.setServerUrl("https://example.com");

        AemUploadSdkHealthAutoConfiguration config = new AemUploadSdkHealthAutoConfiguration();

        HealthIndicator indicator = config.aemUploadSdkHealthIndicator(api, properties);

        assertThat(indicator).isNotNull();
    }
}
