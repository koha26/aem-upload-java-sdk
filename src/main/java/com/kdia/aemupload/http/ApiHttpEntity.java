package com.kdia.aemupload.http;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiHttpEntity<T> {
    private T body;
    @Builder.Default
    private Map<String, String> headers = new LinkedHashMap<>();
}
