package com.kdiachenko.aemupload.exception;

/**
 * Exception thrown when HTTP transport fails.
 * This includes connection errors, timeouts, and other network-related issues.
 */
public class TransportException extends SdkException {

    public TransportException(String message) {
        super(message);
    }

    public TransportException(String message, Throwable cause) {
        super(message, cause);
    }
}
