package com.kdiachenko.aemupload.exception;

/**
 * Exception thrown when an API call fails.
 * Contains HTTP status code and error details from the server response.
 */
public class ApiException extends SdkException {

    private final int httpStatus;
    private final String errorCode;
    private final String rawResponse;

    public ApiException(String message, int httpStatus) {
        this(message, httpStatus, null, null, null);
    }

    public ApiException(String message, int httpStatus, String errorCode) {
        this(message, httpStatus, errorCode, null, null);
    }

    public ApiException(String message, int httpStatus, String errorCode, String rawResponse) {
        this(message, httpStatus, errorCode, rawResponse, null);
    }

    public ApiException(String message, int httpStatus, String errorCode, String rawResponse, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.rawResponse = rawResponse;
    }

    /**
     * Returns the HTTP status code from the failed response.
     *
     * @return HTTP status code (e.g., 404, 500)
     */
    public int getHttpStatus() {
        return httpStatus;
    }

    /**
     * Returns the error code from the server, if available.
     *
     * @return error code string or null
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Returns the raw response body from the server, if available.
     *
     * @return raw response string or null
     */
    public String getRawResponse() {
        return rawResponse;
    }

    /**
     * Checks if this is a client error (4xx status code).
     *
     * @return true if status is between 400-499
     */
    public boolean isClientError() {
        return httpStatus >= 400 && httpStatus < 500;
    }

    /**
     * Checks if this is a server error (5xx status code).
     *
     * @return true if status is between 500-599
     */
    public boolean isServerError() {
        return httpStatus >= 500 && httpStatus < 600;
    }
}
