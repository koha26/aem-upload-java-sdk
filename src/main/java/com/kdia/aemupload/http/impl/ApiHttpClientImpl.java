package com.kdia.aemupload.http.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdia.aemupload.config.ServerConfiguration;
import com.kdia.aemupload.expection.ApiHttpClientException;
import com.kdia.aemupload.http.ApiHttpClient;
import com.kdia.aemupload.http.ApiHttpClientResponseHandlerFactory;
import com.kdia.aemupload.http.ApiHttpEntity;
import com.kdia.aemupload.http.ApiHttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.entity.EntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntityContainer;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class ApiHttpClientImpl implements ApiHttpClient {

    private final CloseableHttpClient httpClient;
    private final ServerConfiguration serverConfiguration;
    private final ObjectMapper objectMapper;
    private final ApiHttpClientResponseHandlerFactory responseHandlerFactory;

    public ApiHttpClientImpl(CloseableHttpClient httpClient, ServerConfiguration serverConfiguration) {
        this(httpClient, serverConfiguration, new ObjectMapper(), new ApiHttpClientResponseHandlerFactoryImpl());
    }

    public ApiHttpClientImpl(CloseableHttpClient httpClient, ServerConfiguration serverConfiguration,
                             ObjectMapper objectMapper, ApiHttpClientResponseHandlerFactory responseHandlerFactory) {
        this.httpClient = httpClient;
        this.serverConfiguration = serverConfiguration;
        this.objectMapper = objectMapper;
        this.responseHandlerFactory = responseHandlerFactory;
    }

    @Override
    public <T> ApiHttpResponse<T> get(final String url, final Class<T> responseType) throws ApiHttpClientException {
        var requestUrl = getRequestUrl(url);
        HttpGet request = new HttpGet(requestUrl);
        return executeRequest(request, responseType);
    }

    @Override
    public <E, R> ApiHttpResponse<R> post(final String url, ApiHttpEntity<E> entity,
                                          final Class<R> responseType) throws ApiHttpClientException {
        try {
            var requestUrl = getRequestUrl(url);
            HttpPost httpPost = new HttpPost(requestUrl);
            setRequestEntity(httpPost, entity);
            entity.getHeaders().forEach(httpPost::setHeader);
            return executeRequest(httpPost, responseType);
        } catch (Exception e) {
            return ApiHttpResponse.<R>builder()
                    .status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .errorMessage("Error while executing request: " + url)
                    .build();
        }
    }

    @Override
    public <E, R> ApiHttpResponse<R> put(final String url, ApiHttpEntity<E> entity,
                                         final Class<R> responseType) throws ApiHttpClientException {
        try {
            var requestUrl = getRequestUrl(url);
            HttpPut httpPut = new HttpPut(requestUrl);
            setRequestEntity(httpPut, entity);
            entity.getHeaders().forEach(httpPut::setHeader);
            return executeRequest(httpPut, responseType);
        } catch (Exception e) {
            return ApiHttpResponse.<R>builder()
                    .status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .errorMessage("Error while executing request: " + url)
                    .build();
        }
    }

    private <T> ApiHttpResponse<T> executeRequest(final HttpUriRequestBase request,
                                                  final Class<T> responseType) throws ApiHttpClientException {
        try {
            return httpClient.execute(request, responseHandlerFactory.createHandler(responseType));
        } catch (IOException e) {
            log.debug("Error while executing request", e);
            return ApiHttpResponse.<T>builder()
                    .status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .errorMessage("Error while executing request: " + request.getMethod() + " " + request.getRequestUri())
                    .build();
        }
    }

    private <T> void setRequestEntity(final HttpEntityContainer request,
                                      final ApiHttpEntity<T> entity) throws ApiHttpClientException {
        T body = entity.getBody();
        if (body instanceof InputStream) {
            request.setEntity(EntityBuilder.create()
                    .setStream((InputStream) body)
                    .setContentType(ContentType.create(entity.getHeaders().get(HttpHeaders.CONTENT_TYPE)))
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
            log.debug("Failed to serialize request body", e);
            throw new RuntimeException("Failed to serialize request body", e);
        }
    }

    private String getRequestUrl(final String url) {
        var hostUrl = getHostUrl();
        return StringUtils.startsWith(url, "/") ? hostUrl + url : url;
    }

    private String getHostUrl() {
        return serverConfiguration.getSchema() + "://" + serverConfiguration.getHost()
                + (StringUtils.isEmpty(serverConfiguration.getPort()) ? "" : ":" + serverConfiguration.getPort());
    }
}
