package com.kdiachenko.aemupload.http.client.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdiachenko.aemupload.http.client.HttpClientObjectMapper;
import com.kdiachenko.aemupload.exception.SerializationException;
import com.kdiachenko.aemupload.internal.http.JacksonHttpClientObjectMapper;
import com.kdiachenko.aemupload.http.response.ApiHttpClientResponseHandlerFactory;
import com.kdiachenko.aemupload.http.client.ApiHttpClient;
import com.kdiachenko.aemupload.http.entity.ApiHttpContext;
import com.kdiachenko.aemupload.http.entity.ApiHttpEntity;
import com.kdiachenko.aemupload.http.entity.ApiHttpResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.entity.EntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.protocol.HttpClientContext;
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
@RequiredArgsConstructor
public class ApiHttpClientImpl implements ApiHttpClient {

    private final CloseableHttpClient httpClient;
    private final HttpClientObjectMapper httpClientObjectMapper;
    private final ApiHttpClientResponseHandlerFactory responseHandlerFactory;

    public ApiHttpClientImpl(CloseableHttpClient httpClient) {
        this(httpClient, new JacksonHttpClientObjectMapper(),
                ApiHttpClientResponseHandlerFactory.create(new JacksonHttpClientObjectMapper()));
    }

    @Override
    public <T> ApiHttpResponse<T> get(final String url, final ApiHttpContext apiHttpContext, final Class<T> responseType) {
        var request = new HttpGet(url);
        return executeRequest(request, apiHttpContext, responseType);
    }

    @Override
    public <E, R> ApiHttpResponse<R> post(final String url, final ApiHttpEntity<E> entity,
                                          final ApiHttpContext apiHttpContext, final Class<R> responseType) {
        return safeExecute(() -> {
            var httpPost = new HttpPost(url);
            setRequestEntity(httpPost, entity);
            setHeaders(httpPost, entity);
            return executeRequest(httpPost, apiHttpContext, responseType);
        });
    }

    @Override
    public <E, R> ApiHttpResponse<R> put(final String url, final ApiHttpEntity<E> entity,
                                         final ApiHttpContext apiHttpContext, final Class<R> responseType) {
        return safeExecute(() -> {
            var httpPut = new HttpPut(url);
            setRequestEntity(httpPut, entity);
            setHeaders(httpPut, entity);
            return executeRequest(httpPut, apiHttpContext, responseType);
        });
    }

    private <T> ApiHttpResponse<T> executeRequest(final HttpUriRequestBase request,
                                                  final ApiHttpContext apiHttpContext,
                                                  final Class<T> responseType) {
        try {
            HttpClientContext clientContext = createHttpClientContext(apiHttpContext);
            return httpClient.execute(request, clientContext, responseHandlerFactory.createHandler(responseType));
        } catch (IOException e) {
            log.debug("Error while executing request", e);
            String cause = e.getMessage();
            return ApiHttpResponse.<T>builder()
                    .status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .errorMessage("Error while executing request: " + request.getMethod() + " "
                            + request.getRequestUri() + (cause != null ? ". Cause: " + cause : ""))
                    .build();
        }
    }

    private HttpClientContext createHttpClientContext(final ApiHttpContext apiHttpContext) {
        if (apiHttpContext == null || apiHttpContext.getAttributes().isEmpty()) {
            return null;
        }
        HttpClientContext clientContext = HttpClientContext.create();
        apiHttpContext.getAttributes().forEach(clientContext::setAttribute);
        return clientContext;
    }

    private <T> void setRequestEntity(final HttpEntityContainer request,
                                      final ApiHttpEntity<T> entity) {
        T body = entity.getBody();
        if (body instanceof InputStream) {
            setInputStreamToBody(request, entity, (InputStream) body);
            return;
        }
        if (body instanceof Map) {
            setFormDataToBody(request, (Map<?, ?>) body);
            return;
        }
        setJSONToBody(request, body);
    }

    private <T> void setJSONToBody(final HttpEntityContainer request, final T body) {
        try {
            String json = httpClientObjectMapper.serialize(body);
            request.setEntity(EntityBuilder.create()
                    .setText(json)
                    .setContentType(ContentType.APPLICATION_JSON)
                    .build());
        } catch (SerializationException e) {
            log.debug("Failed to serialize request body", e);
            if (e.getCause() instanceof IOException) {
                throw new UncheckedIOException((IOException) e.getCause());
            }
            throw new UncheckedIOException(new IOException("Failed to serialize request body", e));
        }
    }

    private void setFormDataToBody(final HttpEntityContainer request, final Map<?, ?> body) {
        List<NameValuePair> formParams = new ArrayList<>();
        body.forEach((key, value) ->
                formParams.add(new BasicNameValuePair(String.valueOf(key), String.valueOf(value))));
        request.setEntity(EntityBuilder.create()
                .setParameters(formParams)
                .build());
    }

    private <T> void setInputStreamToBody(final HttpEntityContainer request,
                                          final ApiHttpEntity<T> entity, InputStream body) {
        try {
            request.setEntity(EntityBuilder.create()
                    .setBinary(toByteArray(body))
                    .setContentType(ContentType.create(entity.getHeaders().get(HttpHeaders.CONTENT_TYPE)))
                    .build());
        } catch (IOException e) {
            log.error("Failed to serialize request body of Input Stream type", e);
            throw new UncheckedIOException(e);
        }
    }

    private <R> ApiHttpResponse<R> safeExecute(final Supplier<ApiHttpResponse<R>> request) {
        try {
            return request.get();
        } catch (Exception e) {
            log.debug("Unexpected error while executing request", e);
            return ApiHttpResponse.<R>builder()
                    .status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .errorMessage("Unexpected error while executing request: " + e.getMessage())
                    .build();
        }
    }

    private <E> void setHeaders(final HttpUriRequestBase httpRequest, final ApiHttpEntity<E> entity) {
        entity.getHeaders().forEach(httpRequest::setHeader);
    }

    private byte[] toByteArray(final InputStream inputStream) throws IOException {
        return inputStream.readAllBytes();
    }

}
