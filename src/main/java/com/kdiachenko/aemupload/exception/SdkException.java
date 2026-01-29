package com.kdiachenko.aemupload.exception;

/**
 * Base exception for all SDK errors.
 * This is the root of the exception hierarchy for the AEM Upload SDK.
 */
public class SdkException extends RuntimeException {

    public SdkException(String message) {
        super(message);
    }

    public SdkException(String message, Throwable cause) {
        super(message, cause);
    }

    public SdkException(Throwable cause) {
        super(cause);
    }
}
