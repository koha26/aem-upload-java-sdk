package com.kdia.aemupload.http.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdia.aemupload.config.ServerConfiguration;
import com.kdia.aemupload.http.ApiHttpClient;
import com.kdia.aemupload.http.ApiHttpClientResponseHandlerFactory;
import com.kdia.aemupload.http.entity.ApiHttpEntity;
import com.kdia.aemupload.http.entity.ApiHttpResponse;
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
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
public class ApiHttpClientImpl implements ApiHttpClient {

    private final CloseableHttpClient httpClient;
    private final ServerConfiguration serverConfiguration;
    private final ObjectMapper objectMapper;
    private final ApiHttpClientResponseHandlerFactory responseHandlerFactory;

    public ApiHttpClientImpl(CloseableHttpClient httpClient, ServerConfiguration serverConfiguration) {
        this(httpClient, serverConfiguration, new ObjectMapper(), DefaultApiHttpClientResponseHandlerFactory.INSTANCE);
    }

    public ApiHttpClientImpl(CloseableHttpClient httpClient, ServerConfiguration serverConfiguration,
                             ObjectMapper objectMapper, ApiHttpClientResponseHandlerFactory responseHandlerFactory) {
        this.httpClient = httpClient;
        this.serverConfiguration = serverConfiguration;
        this.objectMapper = objectMapper;
        this.responseHandlerFactory = responseHandlerFactory;
    }

    @Override
    public <T> ApiHttpResponse<T> get(final String url, final Class<T> responseType) {
        var requestUrl = getRequestUrl(url);
        var request = new HttpGet(requestUrl);
        return executeRequest(request, responseType);
    }

    @Override
    public <E, R> ApiHttpResponse<R> post(final String url, final ApiHttpEntity<E> entity,
                                          final Class<R> responseType) {
        return safeExecute(() -> {
            var requestUrl = getRequestUrl(url);
            var httpPost = new HttpPost(requestUrl);
            setRequestEntity(httpPost, entity);
            setHeaders(httpPost, entity);
            return executeRequest(httpPost, responseType);
        });
    }

    @Override
    public <E, R> ApiHttpResponse<R> put(final String url, final ApiHttpEntity<E> entity,
                                         final Class<R> responseType) {
        return safeExecute(() -> {
            var requestUrl = getRequestUrl(url);
            var httpPut = new HttpPut(requestUrl);
            setRequestEntity(httpPut, entity);
            setHeaders(httpPut, entity);
            return executeRequest(httpPut, responseType);
        });
    }

    private <T> ApiHttpResponse<T> executeRequest(final HttpUriRequestBase request,
                                                  final Class<T> responseType) {
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
                                      final ApiHttpEntity<T> entity) {
        T body = entity.getBody();
        if (body instanceof InputStream) {
            setInputStreamToBody(request, entity, (InputStream) body);
            return;
        }
        if (body instanceof Map) {
            setFormDataToBody(request, (Map<String, Object>) body);
            return;
        }
        setJSONToBody(request, body);
    }

    private <T> void setJSONToBody(final HttpEntityContainer request, final T body) {
        try {
            String json = objectMapper.writeValueAsString(body);
            request.setEntity(EntityBuilder.create()
                    .setText(json)
                    .setContentType(ContentType.APPLICATION_JSON)
                    .build());
        } catch (IOException e) {
            log.debug("Failed to serialize request body", e);
            throw new UncheckedIOException("Failed to serialize request body", e);
        }
    }

    private <T> void setFormDataToBody(final HttpEntityContainer request, final Map<String, Object> body) {
        List<NameValuePair> formParams = new ArrayList<>();
        body.forEach((key, value) -> formParams.add(new BasicNameValuePair(key, String.valueOf(value))));
        request.setEntity(EntityBuilder.create()
                .setParameters(formParams)
                .build());
    }

    private <T> void setInputStreamToBody(final HttpEntityContainer request,
                                          final ApiHttpEntity<T> entity, InputStream body) {
        request.setEntity(EntityBuilder.create()
                .setStream(body)
                .setContentType(ContentType.create(entity.getHeaders().get(HttpHeaders.CONTENT_TYPE)))
                .build());
    }

    private <R> ApiHttpResponse<R> safeExecute(final Supplier<ApiHttpResponse<R>> request) {
        try {
            return request.get();
        } catch (Exception e) {
            return ApiHttpResponse.<R>builder()
                    .status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .errorMessage("Error while executing request")
                    .build();
        }
    }

    private <E> void setHeaders(final HttpUriRequestBase httpRequest, final ApiHttpEntity<E> entity) {
        entity.getHeaders().forEach(httpRequest::setHeader);
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
