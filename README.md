# AEM Upload Java SDK

[![Build Status](https://github.com/koha26/aem-upload-java-sdk/actions/workflows/build.yml/badge.svg)](https://github.com/koha26/aem-upload-java-sdk/actions/workflows/build.yml)
[![Coverage](https://codecov.io/gh/koha26/aem-upload-java-sdk/branch/main/graph/badge.svg)](https://codecov.io/gh/koha26/aem-upload-java-sdk)
[![Maven Central](https://img.shields.io/maven-central/v/com.kdiachenko/aem-upload-sdk.svg)](https://search.maven.org/artifact/com.kdiachenko/aem-upload-sdk)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Java Version](https://img.shields.io/badge/Java-11%2B-orange.svg)](https://www.oracle.com/java/technologies/downloads/)

A Java SDK for uploading assets to Adobe Experience Manager (AEM) using the Direct Binary Upload protocol.

## 💡 Idea

This SDK simplifies integration with AEM Assets by providing a clean, type-safe Java API for:
- **Direct Binary Upload**: Efficiently upload large files using cloud-native multipart upload
- **Asset Management**: Create folders, manage metadata, and delete assets
- **Multiple Environments**: Works with AEM as a Cloud Service and on-premise AEM instances

## Quick Start

```java
// Create SDK with access token (for development)
try (AemUploadSdk sdk = AemUploadSdk.builder()
        .serverUrl("https://author.adobeaemcloud.com")
        .withAccessToken("your-dev-token")
        .build()) {
    
    // Create a folder
    sdk.assetFolderApi().createFolder("/content/dam/my-folder").getOrThrow();
    
    // Upload a file
    var response = sdk.directBinaryUploadApi()
        .initiateUpload(InitiateBinaryUploadOptions.builder()
            .damAssetFolder("/content/dam/my-folder")
            .fileName("image.jpg")
            .fileSize(Files.size(Path.of("image.jpg")))
            .build());
    
    response.getOrThrow(); // throws on error
}
```

## Supported Java version

- **Java 11** or higher is required

## Installation

### Maven

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.kdiachenko</groupId>
    <artifactId>aem-upload-sdk</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## Features

- **Direct Binary Upload**: Efficiently upload large files to AEM Assets using cloud-native multipart upload
- **Simple API**: Fluent builder pattern for easy SDK initialization
- **Multiple Auth Strategies**: Support for access tokens, basic auth, and service credentials (JWT)
- **OSGi Ready**: Can be used as an OSGi bundle in AEM
- **Spring Boot Starter**: Auto-configuration for Spring Boot applications
- **Testable**: All dependencies are injectable for easy mocking

## Example usage

### Complete file upload

```java
try (AemUploadSdk sdk = AemUploadSdk.builder()
        .serverUrl("https://author.adobeaemcloud.com")
        .withAccessToken("your-dev-token")
        .build()) {

    DirectBinaryUploadApi api = sdk.directBinaryUploadApi();

    // 1. Initiate upload
    var initResponse = api.initiateUpload(InitiateBinaryUploadOptions.builder()
        .damAssetFolder("/content/dam/folder")
        .fileName("asset.jpg")
        .fileSize(1024L)
        .build()).getOrThrow();

    // 2. Upload binary parts
    var uploadResponse = api.uploadBinary(UploadBinaryOptions.builder()
        .binary(Path.of("asset.jpg"))
        .uploadURIs(initResponse.getUploadURIs())
        .maxPartSize(initResponse.getMaxPartSize())
        .contentType("image/jpeg")
        .build()).getOrThrow();

    // 3. Complete upload
    var completeResponse = api.completeUpload(CompleteBinaryUploadOptions.builder()
        .completeUri(initResponse.getCompleteURI())
        .fileName("asset.jpg")
        .mimeType("image/jpeg")
        .uploadToken(initResponse.getUploadToken())
        .build()).getOrThrow();
}
```

### Functional response handling

```java
AssetApiResponse<InitiateUploadResponse> response = api.initiateUpload(options);

// Functional style
response.ifSuccess(data -> System.out.println("Success: " + data))
        .ifFailure(error -> System.err.println("Error: " + error.getMessage()));

// Get with default
InitiateUploadResponse data = response.getOrElse(defaultValue);

// Map/transform
AssetApiResponse<String> mapped = response.map(data -> data.getUploadToken());
```

## Authentication options

### Access token (development)

For local development with a developer token:

```java
AemUploadSdk sdk = AemUploadSdk.builder()
    .serverUrl("https://author.adobeaemcloud.com")
    .withAccessToken("your-dev-token")
    .build();
```

### Basic auth (On-premise AEM)

For on-premise AEM instances:

```java
AemUploadSdk sdk = AemUploadSdk.builder()
    .serverUrl("http://localhost:4502")
    .withBasicAuth("admin", "admin")
    .build();
```

### Service credentials (production)

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

For uploading asset binaries using the direct binary upload protocol.

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

## Related Modules

| Module | Description |
|--------|-------------|
| [OSGi Bundle](osgi/README.md) | For use in AEM/OSGi environments |
| [Spring Boot Starter](spring-boot-starter/README.md) | Auto-configuration for Spring Boot |


## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines on how to contribute to this project.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
