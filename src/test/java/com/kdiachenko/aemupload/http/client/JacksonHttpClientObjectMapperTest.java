package com.kdiachenko.aemupload.http.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdiachenko.aemupload.exception.SerializationException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JacksonHttpClientObjectMapperTest {

    @Test
    void serialize_shouldReturnJson() throws SerializationException {
        JacksonHttpClientObjectMapper mapper = new JacksonHttpClientObjectMapper();

        String json = mapper.serialize(new TestObject("hello"));

        assertThat(json).contains("\"data\":\"hello\"");
    }

    @Test
    void deserialize_shouldReturnObject() throws SerializationException {
        JacksonHttpClientObjectMapper mapper = new JacksonHttpClientObjectMapper();

        TestObject obj = mapper.deserialize("{\"data\":\"hello\"}", TestObject.class);

        assertThat(obj.getData()).isEqualTo("hello");
    }

    @Test
    void serialize_shouldWrapErrors() {
        ObjectMapper failing = new ObjectMapper() {
            @Override
            public String writeValueAsString(Object value) {
                throw new RuntimeException("boom");
            }
        };

        JacksonHttpClientObjectMapper mapper = new JacksonHttpClientObjectMapper(failing);

        assertThatThrownBy(() -> mapper.serialize(new TestObject("x")))
                .isInstanceOf(SerializationException.class)
                .hasMessageContaining("Failed to serialize");
    }

    @Test
    void deserialize_shouldWrapErrors() {
        ObjectMapper failing = new ObjectMapper() {
            @Override
            public <T> T readValue(String content, Class<T> valueType) {
                throw new RuntimeException("boom");
            }
        };

        JacksonHttpClientObjectMapper mapper = new JacksonHttpClientObjectMapper(failing);

        assertThatThrownBy(() -> mapper.deserialize("{}", TestObject.class))
                .isInstanceOf(SerializationException.class)
                .hasMessageContaining("Failed to deserialize");
    }

    static class TestObject {
        private String data;

        public TestObject() {
        }

        public TestObject(String data) {
            this.data = data;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }
}
