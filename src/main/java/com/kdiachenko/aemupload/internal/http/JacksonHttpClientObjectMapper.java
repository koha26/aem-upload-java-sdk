package com.kdiachenko.aemupload.internal.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdiachenko.aemupload.http.client.HttpClientObjectMapper;
import com.kdiachenko.aemupload.exception.SerializationException;
import lombok.RequiredArgsConstructor;

/**
 * Jackson-based implementation of the Serializer interface.
 */
@RequiredArgsConstructor
public class JacksonHttpClientObjectMapper implements HttpClientObjectMapper {
    private final ObjectMapper objectMapper;

    public JacksonHttpClientObjectMapper() {
        this(new ObjectMapper());
    }

    @Override
    public <T> String serialize(final T object) throws SerializationException {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new SerializationException("Failed to serialize object", e);
        }
    }

    @Override
    public <T> T deserialize(final String json, final Class<T> type) throws SerializationException {
        try {
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            throw new SerializationException("Failed to deserialize JSON to " + type.getName(), e);
        }
    }
}

