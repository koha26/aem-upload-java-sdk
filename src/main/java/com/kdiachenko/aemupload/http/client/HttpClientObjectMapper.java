package com.kdiachenko.aemupload.http.client;

import com.kdiachenko.aemupload.exception.SerializationException;

/**
 * Interface for serializing and deserializing objects.
 * Implementations should handle JSON, XML, or other formats.
 */
public interface HttpClientObjectMapper {
    /**
     * Serializes an object to a string representation.
     *
     * @param object the object to serialize
     * @param <T> the type of the object
     * @return the serialized string
     * @throws SerializationException if serialization fails
     */
    <T> String serialize(T object) throws SerializationException;

    /**
     * Deserializes a string to an object of the specified type.
     *
     * @param json the string to deserialize
     * @param type the target class
     * @param <T> the type of the object
     * @return the deserialized object
     * @throws SerializationException if deserialization fails
     */
    <T> T deserialize(String json, Class<T> type) throws SerializationException;
}

