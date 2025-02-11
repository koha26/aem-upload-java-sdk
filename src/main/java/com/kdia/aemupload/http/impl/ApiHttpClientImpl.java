package com.kdia.aemupload.http.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdia.aemupload.config.ServerConfiguration;
import com.kdia.aemupload.expection.ApiHttpClientException;
import com.kdia.aemupload.http.ApiHttpClient;
import com.kdia.aemupload.http.ApiHttpResponse;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.entity.EntityBuilder;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntityContainer;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ApiHttpClientImpl implements ApiHttpClient {

    private final CloseableHttpClient httpClient;
    private final ServerConfiguration serverConfiguration;
    private final ObjectMapper objectMapper;

    public ApiHttpClientImpl(CloseableHttpClient httpClient, ServerConfiguration serverConfiguration,
                             ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.serverConfiguration = serverConfiguration;
        this.objectMapper = objectMapper;
    }

    @Override
    public <T> ApiHttpResponse<T> get(final String url, final Class<T> responseType) throws ApiHttpClientException {
        var requestUrl = getRequestUrl(url);
        HttpGet request = new HttpGet(requestUrl);
        return executeRequest(request, responseType);
    }

    @Override
    public <T> ApiHttpResponse<T> post(final String url, Object request, Map<String, String> headers,
                                       final Class<T> responseType) throws ApiHttpClientException {
        var requestUrl = getRequestUrl(url);
        HttpPost httpPost = new HttpPost(requestUrl);
        setRequestEntity(httpPost, request, headers);
        headers.forEach(httpPost::setHeader);
        return executeRequest(httpPost, responseType);
    }

    @Override
    public <T> ApiHttpResponse<T> put(final String url, Object request, Map<String, String> headers,
                                      final Class<T> responseType) throws ApiHttpClientException {
        var requestUrl = getRequestUrl(url);
        HttpPut httpPut = new HttpPut(requestUrl);
        setRequestEntity(httpPut, request, headers);
        headers.forEach(httpPut::setHeader);
        return executeRequest(httpPut, responseType);
    }

    private <T> ApiHttpResponse<T> executeRequest(HttpUriRequestBase request, Class<T> responseType) throws ApiHttpClientException {
        try {
            Result result = httpClient.execute(request, (response) -> {
                BasicHttpClientResponseHandler basicHttpClientResponseHandler = new BasicHttpClientResponseHandler();
                String responseEntity = basicHttpClientResponseHandler.handleResponse(response);
                return new Result(response.getCode(), responseEntity);
            });
            int statusCode = result.getStatus();
            String responseBody = result.getContent();
            T body = objectMapper.readValue(responseBody, responseType);
            return toApiHttpResponse(body, statusCode);
        } catch (IOException e) {
            throw new ApiHttpClientException(500, "HTTP request failed: " + request.getMethod() + " " + request.getRequestUri());
        }
    }

    private <T> ApiHttpResponse<T> toApiHttpResponse(final T body, final int statusCode) {
        return ApiHttpResponse.<T>builder()
                .status(statusCode)
                .body(body)
                .build();
    }

    private void setRequestEntity(HttpEntityContainer request, Object body,
                                  Map<String, String> headers) throws ApiHttpClientException {
        if (body instanceof InputStream) {
            request.setEntity(EntityBuilder.create()
                    .setStream((InputStream) body)
                    .setContentType(ContentType.create(headers.get(HttpHeaders.CONTENT_TYPE)))
                    .build());
            return;
        }
        if (body instanceof Map) {
            Map<String, String> bodyMap = (Map<String, String>) body;
            List<NameValuePair> formParams = new ArrayList<>();
            bodyMap.forEach((key, value) -> {
                formParams.add(new BasicNameValuePair(key, value));
            });
            request.setEntity(EntityBuilder.create()
                    .setParameters(formParams)
                    .build());
            return;
        }
        try {
            String json = objectMapper.writeValueAsString(body);
            request.setEntity(EntityBuilder.create()
                    .setText(json)
                    .setContentType(ContentType.APPLICATION_JSON)
                    .build());
        } catch (Exception e) {
            throw new ApiHttpClientException(500, "Failed to serialize request body");
        }
    }

    String getRequestUrl(final String url) {
        var hostUrl = getHostUrl();
        return StringUtils.startsWith(url, "/") ? hostUrl + url : url;
    }

    String getHostUrl() {
        return serverConfiguration.getSchema() + "://" + serverConfiguration.getHost()
                + (StringUtils.isEmpty(serverConfiguration.getPort()) ? "" : ":" + serverConfiguration.getPort());
    }

    @Getter
    static class Result {

        final int status;
        final String content;

        Result(final int status, final String content) {
            this.status = status;
            this.content = content;
        }

    }
}
