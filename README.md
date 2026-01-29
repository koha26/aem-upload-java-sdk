# AEM Upload Java SDK

A Java SDK for uploading assets to Adobe Experience Manager (AEM) using the Direct Binary Upload protocol.

## Features

- **Direct Binary Upload**: Efficiently upload large files to AEM Assets using cloud-native multipart upload
- **Simple API**: Fluent builder pattern for easy SDK initialization
- **Multiple Auth Strategies**: Support for access tokens, basic auth, and service credentials (JWT)
- **OSGi Ready**: Can be used as an OSGi bundle in AEM
- **Testable**: All dependencies are injectable for easy mocking

## Quick Start

### Installation

Add the dependency to your Maven project:

```xml
<dependency>
    <groupId>com.kdiachenko</groupId>
    <artifactId>aem-upload-sdk</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### Basic Usage

```java
// Create SDK with access token (for development)
try (AemUploadSdk sdk = AemUploadSdk.builder()
        .serverUrl("https://author.adobeaemcloud.com")
        .withAccessToken("your-dev-token")
        .build()) {
    
    // Initiate upload
    var initiateResponse = sdk.directBinaryUploadApi()
        .initiateUpload(InitiateBinaryUploadOptions.builder()
            .damAssetFolder("/content/dam/my-folder")
            .fileName("image.jpg")
            .fileSize(Files.size(Path.of("image.jpg")))
            .build());
    
    // Handle response functionally
    initiateResponse
        .ifSuccess(data -> {
            System.out.println("Upload initiated: " + data.getUploadToken());
            // Continue with binary upload...
        })
        .ifFailure(error -> {
            System.err.println("Failed: " + error.getMessage());
        });
    
    // Or throw on error
    InitiateUploadResponse data = initiateResponse.getOrThrow();
}
```

## Authentication Options

### Access Token (Development)

For local development with a developer token:

```java
AemUploadSdk sdk = AemUploadSdk.builder()
    .serverUrl("https://author.adobeaemcloud.com")
    .withAccessToken("your-dev-token")
    .build();
```

### Basic Auth (On-Premise AEM)

For on-premise AEM instances:

```java
AemUploadSdk sdk = AemUploadSdk.builder()
    .serverUrl("http://localhost:4502")
    .withBasicAuth("admin", "admin")
    .build();
```

### Service Credentials (Production)

For server-to-server authentication with JWT:

```java
ServiceCredentialsAuthConfig authConfig = ServiceCredentialsAuthConfig.builder()
    .clientId("your-client-id")
    .clientSecret("your-client-secret")
    .technicalAccountId("your-tech-account-id")
    .orgId("your-org-id@AdobeOrg")
    .privateKeyPath("/path/to/private.key")
    .metaScopes(List.of("ent_aem_cloud_api"))
    .build();

AemUploadSdk sdk = AemUploadSdk.builder()
    .serverUrl("https://author.adobeaemcloud.com")
    .withServiceCredentials(authConfig)
    .build();
```

## Available APIs

### DirectBinaryUploadApi

For uploading asset binaries using the direct binary upload protocol:

```java
DirectBinaryUploadApi api = sdk.directBinaryUploadApi();

// 1. Initiate upload
var initResponse = api.initiateUpload(InitiateBinaryUploadOptions.builder()
    .damAssetFolder("/content/dam/folder")
    .fileName("asset.jpg")
    .fileSize(1024L)
    .build());

// 2. Upload binary parts
var uploadResponse = api.uploadBinary(UploadBinaryOptions.builder()
    .binary(Path.of("asset.jpg"))
    .uploadURIs(initResponse.getOrThrow().getUploadURIs())
    .maxPartSize(initResponse.getOrThrow().getMaxPartSize())
    .contentType("image/jpeg")
    .build());

// 3. Complete upload
var completeResponse = api.completeUpload(CompleteBinaryUploadOptions.builder()
    .completeUri(initResponse.getOrThrow().getCompleteURI())
    .fileName("asset.jpg")
    .mimeType("image/jpeg")
    .uploadToken(initResponse.getOrThrow().getUploadToken())
    .build());
```

### AssetFolderApi

For managing DAM folders:

```java
AssetFolderApi api = sdk.assetFolderApi();

// Get folder info
api.getFolder("/content/dam/my-folder");

// Create folder
api.createFolder("/content/dam/new-folder");

// Create folder with properties
api.createFolder("/content/dam/new-folder", Map.of(
    "title", "My Folder",
    "description", "A new folder"
));
```

### AssetMetadataApi

For managing asset metadata:

```java
AssetMetadataApi api = sdk.assetMetadataApi();

// Get metadata
api.getAssetMetadata("/content/dam/folder/asset.jpg");

// Update metadata
api.updateAssetMetadata("/content/dam/folder/asset.jpg", Map.of(
    "dc:title", "New Title",
    "dc:description", "New Description"
));

// Delete asset
api.deleteAsset("/content/dam/folder/asset.jpg");
```

## Response Handling

All API methods return `AssetApiResponse<T>` which provides functional error handling:

```java
AssetApiResponse<InitiateUploadResponse> response = api.initiateUpload(options);

// Check success/failure
if (response.isSuccess()) {
    InitiateUploadResponse data = response.getBody();
}

// Functional style
response.ifSuccess(data -> System.out.println("Success: " + data))
        .ifFailure(error -> System.err.println("Error: " + error.getMessage()));

// Get with default
InitiateUploadResponse data = response.getOrElse(defaultValue);

// Get or throw
InitiateUploadResponse data = response.getOrThrow(); // Throws SdkException on failure

// Map/transform
AssetApiResponse<String> mapped = response.map(data -> data.getUploadToken());
```

## Exception Hierarchy

The SDK provides typed exceptions for different error scenarios:

- `SdkException` - Base exception
- `ApiException` - API/HTTP errors (includes status code)
- `AuthenticationException` - Authentication failures  
- `TransportException` - Network/transport errors
- `SerializationException` - JSON serialization errors

## Custom HTTP Client

You can provide a custom HTTP client:

```java
CloseableHttpClient customClient = HttpClients.custom()
    .setConnectionManager(...)
    .setDefaultRequestConfig(...)
    .build();

AemUploadSdk sdk = AemUploadSdk.builder()
    .serverUrl("https://author.adobeaemcloud.com")
    .withAccessToken("token")
    .httpClient(customClient) // SDK won't close this client
    .build();
```

## OSGi Bundle

For use in AEM/OSGi environments, see the `osgi/` module which provides:
- OSGi bundle packaging
- Service component annotations
- AEM package for deployment

## License

See [LICENSE](LICENSE) for details.
