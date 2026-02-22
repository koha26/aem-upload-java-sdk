package com.kdiachenko.aemupload.exception;

import lombok.Value;

import java.util.Objects;
import java.util.Optional;

/**
 * Immutable value type representing an SDK error.
 * Used in {@link com.kdiachenko.aemupload.model.AssetApiResponse} for typed error handling.
 */
@Value
public class SdkError {

    ErrorCode errorCode;
    String message;
    int httpStatus;
    String rawResponse;
    Throwable cause;

    private SdkError(Builder builder) {
        this.errorCode = builder.errorCode;
        this.message = builder.message;
        this.httpStatus = builder.httpStatus;
        this.rawResponse = builder.rawResponse;
        this.cause = builder.cause;
    }

    /**
     * Creates a new builder for SdkError.
     *
     * @param errorCode the error code
     * @param message   the error message
     * @return a new builder
     */
    public static Builder builder(ErrorCode errorCode, String message) {
        return new Builder(errorCode, message);
    }

    /**
     * Creates an SdkError for a transport/network failure.
     *
     * @param message the error message
     * @param cause   the underlying cause
     * @return a new SdkError
     */
    public static SdkError transportError(String message, Throwable cause) {
        return builder(ErrorCode.TRANSPORT_ERROR, message)
                .cause(cause)
                .build();
    }

    /**
     * Creates an SdkError for an API failure with HTTP status.
     *
     * @param message    the error message
     * @param httpStatus the HTTP status code
     * @return a new SdkError
     */
    public static SdkError apiError(String message, int httpStatus) {
        return builder(ErrorCode.API_ERROR, message)
                .httpStatus(httpStatus)
                .build();
    }

    /**
     * Creates an SdkError for an API failure with full details.
     *
     * @param message     the error message
     * @param httpStatus  the HTTP status code
     * @param rawResponse the raw response body
     * @return a new SdkError
     */
    public static SdkError apiError(String message, int httpStatus, String rawResponse) {
        return builder(ErrorCode.API_ERROR, message)
                .httpStatus(httpStatus)
                .rawResponse(rawResponse)
                .build();
    }

    /**
     * Creates an SdkError for an authentication failure.
     *
     * @param message the error message
     * @return a new SdkError
     */
    public static SdkError authenticationError(String message) {
        return builder(ErrorCode.AUTHENTICATION_ERROR, message).build();
    }

    /**
     * Creates an SdkError for a serialization failure.
     *
     * @param message the error message
     * @param cause   the underlying cause
     * @return a new SdkError
     */
    public static SdkError serializationError(String message, Throwable cause) {
        return builder(ErrorCode.SERIALIZATION_ERROR, message)
                .cause(cause)
                .build();
    }

    public Optional<String> getRawResponse() {
        return Optional.ofNullable(rawResponse);
    }

    public Optional<Throwable> getCause() {
        return Optional.ofNullable(cause);
    }

    /**
     * Converts this error to an appropriate SdkException.
     *
     * @return an SdkException representing this error
     */
    public SdkException toException() {
        switch (errorCode) {
            case API_ERROR:
                return new ApiException(message, httpStatus, null, rawResponse, cause);
            case AUTHENTICATION_ERROR:
                return new AuthenticationException(message, cause);
            case TRANSPORT_ERROR:
                return new TransportException(message, cause);
            case SERIALIZATION_ERROR:
                return new SerializationException(message, cause);
            default:
                return new SdkException(message, cause);
        }
    }

    /**
     * Error code enumeration for categorizing SDK errors.
     */
    public enum ErrorCode {
        /**
         * API returned an error response
         */
        API_ERROR,
        /**
         * Authentication or authorization failed
         */
        AUTHENTICATION_ERROR,
        /**
         * Network/transport layer error
         */
        TRANSPORT_ERROR,
        /**
         * JSON serialization/deserialization error
         */
        SERIALIZATION_ERROR,
        /**
         * Validation error in request parameters
         */
        VALIDATION_ERROR,
        /**
         * Unknown or uncategorized error
         */
        UNKNOWN_ERROR
    }

    public static final class Builder {
        private final ErrorCode errorCode;
        private final String message;
        private int httpStatus;
        private String rawResponse;
        private Throwable cause;

        private Builder(ErrorCode errorCode, String message) {
            this.errorCode = Objects.requireNonNull(errorCode, "errorCode must not be null");
            this.message = Objects.requireNonNull(message, "message must not be null");
        }

        public Builder httpStatus(int httpStatus) {
            this.httpStatus = httpStatus;
            return this;
        }

        public Builder rawResponse(String rawResponse) {
            this.rawResponse = rawResponse;
            return this;
        }

        public Builder cause(Throwable cause) {
            this.cause = cause;
            return this;
        }

        public SdkError build() {
            return new SdkError(this);
        }
    }
}
