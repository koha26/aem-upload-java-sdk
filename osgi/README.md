# AEM Upload SDK - OSGi Bundle

OSGi wrapper for the AEM Upload Java SDK, enabling use in AEM/OSGi environments.

## Modules

- **bundle**: OSGi bundle containing SDK adapters and services
- **package**: AEM content package for deploying the bundle

## Installation

### Maven Dependency

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

## Configuration

Configure the SDK via OSGi configuration in the Felix Console or via `.cfg.json` files.

### Configuration Options

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

### Example Configuration Files

#### Basic Auth (Local Development)

`com.kdiachenko.aemupload.provider.impl.AemUploadSdkServiceImpl.cfg.json`:
```json
{
    "serverUrl": "http://localhost:4502",
    "authType": "basic",
    "username": "admin",
    "password": "admin"
}
```

#### Access Token (Development)

```json
{
    "serverUrl": "https://author-pXXXXX-eYYYYY.adobeaemcloud.com",
    "authType": "accessToken",
    "accessToken": "eyJ0eXAiOiJKV1Q..."
}
```

#### Service Credentials (Production)

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

## Usage

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

### Using Sling Models

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

## Backward Compatibility

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

## License

See [LICENSE](../LICENSE) for details.

### UI tests

They will test the UI layer of your AEM application using either Cypress or Selenium technology.

Check README file in `ui.tests.cypress` or `ui.tests.wdio` module for more details.

## ClientLibs

The frontend module is made available using an [AEM ClientLib](https://helpx.adobe.com/experience-manager/6-5/sites/developing/using/clientlibs.html). When executing the NPM build script, the app is built and the [`aem-clientlib-generator`](https://github.com/wcm-io-frontend/aem-clientlib-generator) package takes the resulting build output and transforms it into such a ClientLib.

A ClientLib will consist of the following files and directories:

- `css/`: CSS files which can be requested in the HTML
- `css.txt` (tells AEM the order and names of files in `css/` so they can be merged)
- `js/`: JavaScript files which can be requested in the HTML
- `js.txt` (tells AEM the order and names of files in `js/` so they can be merged
- `resources/`: Source maps, non-entrypoint code chunks (resulting from code splitting), static assets (e.g. icons), etc.

## Maven settings

The project comes with the auto-public repository configured. To setup the repository in your Maven settings, refer to:

    http://helpx.adobe.com/experience-manager/kb/SetUpTheAdobeMavenRepository.html
