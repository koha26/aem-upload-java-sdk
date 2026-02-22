package com.kdiachenko.aemupload.provider;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;

@ObjectClassDefinition(
        name = "AEM Upload SDK Configuration",
        description = "Configuration for the AEM Upload SDK OSGi service"
)
public @interface AemUploadSdkServiceConfig {

    @AttributeDefinition(
            name = "Server URL",
            description = "The AEM server URL (e.g., https://author.adobeaemcloud.com or http://localhost:4502)"
    )
    String serverUrl() default "http://localhost:4502";

    @AttributeDefinition(
            name = "Authentication Type",
            description = "The authentication method to use",
            options = {
                    @Option(label = "Access Token", value = "accessToken"),
                    @Option(label = "Basic Auth (username/password)", value = "basic"),
                    @Option(label = "Service Credentials (JWT)", value = "serviceCredentials")
            }
    )
    String authType() default "basic";

    // ===== Access Token Auth =====

    @AttributeDefinition(
            name = "Access Token",
            description = "Static access token (for development). Used when authType = 'accessToken'"
    )
    String accessToken() default "";

    // ===== Basic Auth =====

    @AttributeDefinition(
            name = "Username",
            description = "Username for basic authentication. Used when authType = 'basic'"
    )
    String username() default "admin";

    @AttributeDefinition(
            name = "Password",
            description = "Password for basic authentication. Used when authType = 'basic'"
    )
    String password() default "admin";

    // ===== Service Credentials (JWT) =====

    @AttributeDefinition(
            name = "Client ID",
            description = "Adobe I/O client ID. Used when authType = 'serviceCredentials'"
    )
    String clientId() default "";

    @AttributeDefinition(
            name = "Client Secret",
            description = "Adobe I/O client secret. Used when authType = 'serviceCredentials'"
    )
    String clientSecret() default "";

    @AttributeDefinition(
            name = "Technical Account ID",
            description = "Adobe I/O technical account ID. Used when authType = 'serviceCredentials'"
    )
    String technicalAccountId() default "";

    @AttributeDefinition(
            name = "Organization ID",
            description = "Adobe organization ID (e.g., XXXXX@AdobeOrg). Used when authType = 'serviceCredentials'"
    )
    String orgId() default "";

    @AttributeDefinition(
            name = "Private Key Content",
            description = "PEM-encoded private key content. Used when authType = 'serviceCredentials'. " +
                    "Either this or privateKeyPath is required."
    )
    String privateKeyContent() default "";

    @AttributeDefinition(
            name = "Meta Scopes",
            description = "Adobe I/O meta scopes. Used when authType = 'serviceCredentials'"
    )
    String[] metaScopes() default {"ent_aem_cloud_api"};

    @AttributeDefinition(
            name = "IMS Endpoint",
            description = "Adobe IMS endpoint. Used when authType = 'serviceCredentials'"
    )
    String imsEndpoint() default "https://ims-na1.adobelogin.com/ims/exchange/jwt";
}
