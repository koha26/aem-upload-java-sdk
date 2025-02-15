package com.kdia.aemupload.expection;

import lombok.Getter;

@Getter
public class ApiHttpClientException extends RuntimeException {
    private final int statusCode;
    private final String statusText;

    public ApiHttpClientException(int statusCode, String message) {
        super("HTTP status: " + statusCode + ". " + message);
        this.statusText = message;
        this.statusCode = statusCode;
    }
}
