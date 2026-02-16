package com.kdiachenko.aemupload.exception;

/**
 * Exception thrown when JSON serialization or deserialization fails.
 */
public class SerializationException extends SdkException {

    public SerializationException(String message) {
        super(message);
    }

    public SerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
