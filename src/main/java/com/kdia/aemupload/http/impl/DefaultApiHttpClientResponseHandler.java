package com.kdia.aemupload.http.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdia.aemupload.http.ApiHttpResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.AbstractHttpClientResponseHandler;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class DefaultApiHttpClientResponseHandler<T> extends AbstractHttpClientResponseHandler<ApiHttpResponse<T>> {

    private final Class<T> responseType;
    private final ObjectMapper objectMapper;

    @Override
    public ApiHttpResponse<T> handleEntity(final HttpEntity entity) throws IOException {
        try {
            String responseBody = EntityUtils.toString(entity);
            T body = objectMapper.readValue(responseBody, responseType);
            return ApiHttpResponse.<T>builder()
                    .body(body)
                    .build();
        } catch (final ParseException ex) {
            log.info("Error parsing response", ex);
            throw new IOException(ex);
        }
    }

    @Override
    public ApiHttpResponse<T> handleResponse(final ClassicHttpResponse response) throws IOException {
        try {
            final HttpEntity entity = response.getEntity();
            if (response.getCode() >= HttpStatus.SC_REDIRECTION) {
                String responseBody = EntityUtils.toString(entity);
                Map<Object, Object> errorObject = Map.of(
                        "apiResponse", responseBody,
                        "reasonPhrase", response.getReasonPhrase()
                );
                return ApiHttpResponse.<T>builder()
                        .status(response.getCode())
                        .errorMessage(objectMapper.writeValueAsString(errorObject))
                        .build();
            }
            return entity == null ? null : handleEntityWithCode(entity, response);
        } catch (final ParseException ex) {
            log.info("Error parsing response", ex);
            throw new IOException(ex);
        }
    }

    private ApiHttpResponse<T> handleEntityWithCode(final HttpEntity entity,
                                                    final ClassicHttpResponse response) throws IOException {
        ApiHttpResponse<T> apiHttpResponse = handleEntity(entity);
        apiHttpResponse.setStatus(response.getCode());
        return apiHttpResponse;
    }
}
