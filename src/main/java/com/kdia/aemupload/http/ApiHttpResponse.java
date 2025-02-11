package com.kdia.aemupload.http;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ApiHttpResponse<T> {
    private int status;
    private T body;
    private String errorMessage;
}
