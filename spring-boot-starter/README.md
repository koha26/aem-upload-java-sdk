# AEM Upload SDK Spring Boot Starter

Spring Boot auto-configuration for the AEM Upload SDK.

## Installation

Add the dependency to your Spring Boot project:

```xml
<dependency>
    <groupId>com.kdiachenko</groupId>
    <artifactId>aem-upload-sdk-spring-boot-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## Configuration

Configure the SDK in your `application.yml` or `application.properties`:

### Basic Authentication (Local Development)

```yaml
aem:
  upload:
    server-url: http://localhost:4502
    auth-type: basic
    username: admin
    password: admin
```

### Access Token (Development)

```yaml
aem:
  upload:
    server-url: https://author-pXXXXX-eYYYYY.adobeaemcloud.com
    auth-type: access_token
    access-token: eyJ0eXAiOiJKV1Q...
```

### Service Credentials (Production)

```yaml
aem:
  upload:
    server-url: https://author-pXXXXX-eYYYYY.adobeaemcloud.com
    auth-type: service_credentials
    service-credentials:
      client-id: ${AEM_CLIENT_ID}
      client-secret: ${AEM_CLIENT_SECRET}
      technical-account-id: ${AEM_TECH_ACCOUNT_ID}
      org-id: ${AEM_ORG_ID}@AdobeOrg
      private-key-path: /secrets/private.key
      meta-scopes:
        - ent_aem_cloud_api
```

## Configuration Properties

| Property | Description | Default |
|----------|-------------|---------|
| `aem.upload.enabled` | Enable/disable the SDK | `true` |
| `aem.upload.server-url` | AEM server URL | - |
| `aem.upload.auth-type` | Auth type: `basic`, `access_token`, `service_credentials` | `basic` |
| `aem.upload.access-token` | Static access token | - |
| `aem.upload.username` | Basic auth username | `admin` |
| `aem.upload.password` | Basic auth password | `admin` |
| `aem.upload.service-credentials.*` | Service credentials config | - |

## Usage

### Inject APIs

The starter automatically creates beans for all SDK APIs:

```java
import com.kdiachenko.aemupload.api.DirectBinaryUploadApi;
import com.kdiachenko.aemupload.api.AssetFolderApi;
import com.kdiachenko.aemupload.api.AssetMetadataApi;
import com.kdiachenko.aemupload.options.InitiateBinaryUploadOptions;
import org.springframework.stereotype.Service;

@Service
public class AssetUploadService {

    private final DirectBinaryUploadApi uploadApi;
    private final AssetFolderApi folderApi;

    public AssetUploadService(DirectBinaryUploadApi uploadApi, 
                              AssetFolderApi folderApi) {
        this.uploadApi = uploadApi;
        this.folderApi = folderApi;
    }

    public void uploadAsset(String folder, String fileName, long fileSize) {
        var response = uploadApi.initiateUpload(
            InitiateBinaryUploadOptions.builder()
                .damAssetFolder(folder)
                .fileName(fileName)
                .fileSize(fileSize)
                .build()
        );

        response.ifSuccess(data -> {
            System.out.println("Upload initiated: " + data.getUploadToken());
        }).ifFailure(error -> {
            System.err.println("Failed: " + error.getMessage());
        });
    }
}
```

### Inject SDK Directly

You can also inject the SDK instance directly:

```java
import com.kdiachenko.aemupload.AemUploadSdk;
import org.springframework.stereotype.Component;

@Component
public class MyComponent {

    private final AemUploadSdk sdk;

    public MyComponent(AemUploadSdk sdk) {
        this.sdk = sdk;
    }
}
```

## Health Indicator

When Spring Boot Actuator is on the classpath, a health indicator is automatically registered.

Access at: `GET /actuator/health`

```json
{
  "status": "UP",
  "components": {
    "aemUploadSdk": {
      "status": "UP",
      "details": {
        "server": "https://author.adobeaemcloud.com",
        "status": "Connected"
      }
    }
  }
}
```

Disable the health indicator:

```yaml
management:
  health:
    aem-upload:
      enabled: false
```

## Disabling Auto-Configuration

To disable the auto-configuration entirely:

```yaml
aem:
  upload:
    enabled: false
```

Or exclude the auto-configuration class:

```java
@SpringBootApplication(exclude = AemUploadSdkAutoConfiguration.class)
public class MyApplication {
    // ...
}
```

## Building

```bash
cd spring-boot-starter
mvn clean install
```

## License

See [LICENSE](../LICENSE) for details.
