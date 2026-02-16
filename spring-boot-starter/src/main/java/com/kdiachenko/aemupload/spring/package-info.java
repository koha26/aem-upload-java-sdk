/**
 * Spring Boot auto-configuration for the AEM Upload SDK.
 *
 * <p>This package provides:</p>
 * <ul>
 *   <li>{@link com.kdiachenko.aemupload.spring.AemUploadSdkAutoConfiguration} - Main auto-configuration</li>
 *   <li>{@link com.kdiachenko.aemupload.spring.AemUploadSdkProperties} - Configuration properties</li>
 *   <li>{@link com.kdiachenko.aemupload.spring.AemUploadSdkHealthAutoConfiguration} - Actuator health indicator</li>
 * </ul>
 *
 * <p>Add the following dependency to your Spring Boot project:</p>
 * <pre>{@code
 * <dependency>
 *     <groupId>com.kdiachenko</groupId>
 *     <artifactId>aem-upload-sdk-spring-boot-starter</artifactId>
 *     <version>1.0.0-SNAPSHOT</version>
 * </dependency>
 * }</pre>
 *
 * <p>Then configure in application.yml:</p>
 * <pre>{@code
 * aem:
 *   upload:
 *     server-url: https://author.adobeaemcloud.com
 *     auth-type: basic
 *     username: admin
 *     password: admin
 * }</pre>
 *
 * @see com.kdiachenko.aemupload.spring.AemUploadSdkAutoConfiguration
 */
package com.kdiachenko.aemupload.spring;
