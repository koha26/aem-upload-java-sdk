package com.kdiachenko.aemupload.http.response;

import com.kdiachenko.aemupload.http.client.HttpClientSerializer;
import com.kdiachenko.aemupload.http.entity.ApiHttpResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.AbstractHttpClientResponseHandler;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class ApiHttpClientResponseHandler<T> extends AbstractHttpClientResponseHandler<ApiHttpResponse<T>> {

    private final Class<T> responseType;
    private HttpClientSerializer httpClientSerializer;

    @Override
    public ApiHttpResponse<T> handleEntity(final HttpEntity entity) throws IOException {
        try {
            String responseBody = EntityUtils.toString(entity);
            if (Void.class.equals(responseType)) {
                return ApiHttpResponse.<T>builder().build();
            }
            T body = httpClientSerializer.deserialize(responseBody, responseType);
            return ApiHttpResponse.<T>builder()
                    .body(body)
                    .build();
        } catch (final Exception ex) {
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
                        .errorMessage(httpClientSerializer.serialize(errorObject))
                        .build();
            }
            if (entity == null) {
                return ApiHttpResponse.<T>builder()
                        .status(response.getCode())
                        .build();
            }
            return handleEntityWithCode(entity, response);
        } catch (final Exception ex) {
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
