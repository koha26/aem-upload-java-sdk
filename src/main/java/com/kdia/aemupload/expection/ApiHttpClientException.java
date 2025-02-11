package com.kdia.aemupload.expection;

import lombok.Getter;

import java.nio.charset.StandardCharsets;

@Getter
public class ApiHttpClientException extends RuntimeException {
    private int statusCode;
    private String statusText;
    private byte[] responseBody;

    public ApiHttpClientException(int statusCode) {
        this.statusCode = statusCode;
    }

    public ApiHttpClientException(int statusCode, String message) {
        super(message);
        this.statusText = message;
        this.statusCode = statusCode;
    }

    public ApiHttpClientException(int statusCode, String statusText, byte[] responseBody) {
        this(statusCode, statusText);
        this.statusCode = statusCode;
        this.statusText = statusText;
        this.responseBody = responseBody;
    }

    public String getResponseBodyAsString() {
        if (responseBody == null) {
            return "";
        }
        return new String(responseBody, StandardCharsets.UTF_8);
    }

    public String getErrorMessage() {
        return "HTTP status: " + statusCode + ". " + statusText;
    }
}
