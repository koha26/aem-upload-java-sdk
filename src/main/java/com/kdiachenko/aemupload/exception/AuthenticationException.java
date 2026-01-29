package com.kdiachenko.aemupload.exception;

/**
 * Exception thrown when authentication fails.
 * This includes token generation failures, expired tokens, and invalid credentials.
 */
public class AuthenticationException extends SdkException {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
