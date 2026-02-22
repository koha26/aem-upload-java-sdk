# AEM Upload SDK - OSGi Bundle

[![Build Status](https://github.com/koha26/aem-upload-java-sdk/actions/workflows/build.yml/badge.svg)](https://github.com/koha26/aem-upload-java-sdk/actions/workflows/build.yml)
[![Coverage](https://codecov.io/gh/koha26/aem-upload-java-sdk/branch/main/graph/badge.svg)](https://codecov.io/gh/koha26/aem-upload-java-sdk)
[![Maven Central](https://img.shields.io/maven-central/v/com.kdiachenko/aem-upload-java-sdk-osgi.bundle.svg)](https://search.maven.org/artifact/com.kdiachenko/aem-upload-java-sdk-osgi.bundle)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](../LICENSE)
[![Java Version](https://img.shields.io/badge/Java-11%2B-orange.svg)](https://www.oracle.com/java/technologies/downloads/)

OSGi wrapper for the AEM Upload Java SDK, enabling use in AEM/OSGi environments.

## 💡 Idea

This module provides an OSGi-ready wrapper for the AEM Upload SDK, allowing seamless integration with:
- **AEM as a Cloud Service**: Deploy as a content package
- **On-premise AEM 6.5+**: Use as an embedded bundle
- **OSGi Configuration**: Configure via Felix Console or `.cfg.json` files
- **Service Injection**: Use `@Reference` annotation for dependency injection

## Quick start

```java
import com.kdiachenko.aemupload.provider.AemUploadSdkService;

@Component(service = MyAssetUploader.class)
public class MyAssetUploader {

    @Reference
    private AemUploadSdkService sdkService;

    public void uploadAsset(String folderPath, String fileName, long fileSize) {
        sdkService.directBinaryUploadApi()
            .initiateUpload(InitiateBinaryUploadOptions.builder()
                .damAssetFolder(folderPath)
                .fileName(fileName)
                .fileSize(fileSize)
                .build())
            .ifSuccess(data -> System.out.println("Upload initiated: " + data.getUploadToken()))
            .ifFailure(error -> System.err.println("Failed: " + error.getMessage()));
    }
}
```

## Supported Java version

- **Java 11** or higher is required
- Compatible with AEM 6.5+ and AEM as a Cloud Service

## Installation

### Maven dependency

Add to your AEM project's `pom.xml`:

```xml
<dependency>
    <groupId>com.kdiachenko</groupId>
    <artifactId>aem-upload-java-sdk-osgi.bundle</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### Deploy to AEM

Deploy the package to a local AEM instance:

```bash
cd osgi
mvn clean install -PautoInstallSinglePackage
```

With custom AEM instance:

```bash
mvn clean install -PautoInstallSinglePackage -Daem.host=localhost -Daem.port=4502
```

## Example Usage

### Inject the Service

```java
import com.kdiachenko.aemupload.provider.AemUploadSdkService;
import com.kdiachenko.aemupload.api.DirectBinaryUploadApi;
import com.kdiachenko.aemupload.options.InitiateBinaryUploadOptions;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = MyAssetUploader.class)
public class MyAssetUploader {

    @Reference
    private AemUploadSdkService sdkService;

    public void uploadAsset(String folderPath, String fileName, long fileSize) {
        // Check if SDK is ready
        if (!sdkService.isReady()) {
            throw new IllegalStateException("SDK not configured");
        }

        // Get the API
        DirectBinaryUploadApi uploadApi = sdkService.directBinaryUploadApi();

        // Initiate upload
        var response = uploadApi.initiateUpload(
            InitiateBinaryUploadOptions.builder()
                .damAssetFolder(folderPath)
                .fileName(fileName)
                .fileSize(fileSize)
                .build()
        );

        // Handle response
        response.ifSuccess(data -> {
            System.out.println("Upload initiated: " + data.getUploadToken());
        }).ifFailure(error -> {
            System.err.println("Failed: " + error.getMessage());
        });
    }
}
```

### Using Sling models

```java
import com.kdiachenko.aemupload.provider.AemUploadSdkService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

@Model(adaptables = Resource.class)
public class AssetUploadModel {

    @OSGiService
    private AemUploadSdkService sdkService;

    public boolean isUploadAvailable() {
        return sdkService != null && sdkService.isReady();
    }
}
```

## Configuration

Configure the SDK via OSGi configuration in the Felix Console or via `.cfg.json` files.

### Configuration options

| Property | Description | Default |
|----------|-------------|---------|
| `serverUrl` | AEM server URL | `http://localhost:4502` |
| `authType` | Authentication type: `accessToken`, `basic`, or `serviceCredentials` | `basic` |
| `accessToken` | Static access token (for development) | - |
| `username` | Username for basic auth | `admin` |
| `password` | Password for basic auth | `admin` |
| `clientId` | Adobe I/O client ID (for service credentials) | - |
| `clientSecret` | Adobe I/O client secret | - |
| `technicalAccountId` | Technical account ID | - |
| `orgId` | Adobe organization ID | - |
| `privateKeyContent` | PEM private key content | - |
| `privateKeyPath` | Path to private key file | - |
| `metaScopes` | Meta scopes array | `["ent_aem_cloud_api"]` |
| `imsEndpoint` | IMS endpoint URL | `https://ims-na1.adobelogin.com/ims/exchange/jwt` |

### Example configuration files

#### Basic auth (local development)

`com.kdiachenko.aemupload.provider.impl.AemUploadSdkServiceImpl.cfg.json`:
```json
{
    "serverUrl": "http://localhost:4502",
    "authType": "basic",
    "username": "admin",
    "password": "admin"
}
```

#### Access token (development)

```json
{
    "serverUrl": "https://author-pXXXXX-eYYYYY.adobeaemcloud.com",
    "authType": "accessToken",
    "accessToken": "eyJ0eXAiOiJKV1Q..."
}
```

#### Service credentials (production)

```json
{
    "serverUrl": "https://author-pXXXXX-eYYYYY.adobeaemcloud.com",
    "authType": "serviceCredentials",
    "clientId": "your-client-id",
    "clientSecret": "your-client-secret",
    "technicalAccountId": "your-tech-account@techacct.adobe.com",
    "orgId": "XXXXXXXXXXXXXXXX@AdobeOrg",
    "privateKeyPath": "/etc/keys/private.key",
    "metaScopes": ["ent_aem_cloud_api"]
}
```

## Modules

- **bundle**: OSGi bundle containing SDK adapters and services
- **package**: AEM content package for deploying the bundle

## Backward compatibility

The bundle also exports the legacy `SdkApiProvider` interface for backward compatibility:

```java
@Reference
private SdkApiProvider sdkApiProvider; // Deprecated - use AemUploadSdkService instead

DirectBinaryUploadApi api = sdkApiProvider.getDirectBinaryUploadApi();
```

> **Note**: `SdkApiProvider` is deprecated. Migrate to `AemUploadSdkService` for better features
> and lifecycle management.

## Building

```bash
# Build bundle only
cd osgi/bundle
mvn clean install

# Build and deploy to AEM
cd osgi
mvn clean install -PautoInstallSinglePackage

# Build with specific AEM instance
mvn clean install -PautoInstallSinglePackage -Daem.host=localhost -Daem.port=4502
```

## Contributing

See [CONTRIBUTING.md](../CONTRIBUTING.md) for guidelines on how to contribute to this project.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](../LICENSE) file for details.
