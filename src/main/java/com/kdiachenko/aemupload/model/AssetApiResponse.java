package com.kdiachenko.aemupload.model;

import com.kdiachenko.aemupload.exception.SdkError;
import com.kdiachenko.aemupload.http.entity.ApiHttpResponse;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents the result of an API operation.
 * This is a type-safe wrapper that contains either a successful result or an error.
 *
 * @param <T> the type of the successful result
 */
@Value
public class AssetApiResponse<T> {

    boolean success;
    T body;
    SdkError error;

    private AssetApiResponse(boolean success, T body, SdkError error) {
        this.success = success;
        this.body = body;
        this.error = error;
    }

    /**
     * Creates a successful response with the given body.
     *
     * @param body the response body
     * @param <T>  the type of the body
     * @return a successful AssetApiResponse
     */
    public static <T> AssetApiResponse<T> success(T body) {
        return new AssetApiResponse<>(true, body, null);
    }

    /**
     * Creates a failed response with the given error.
     *
     * @param error the SDK error
     * @param <T>   the type of the expected body
     * @return a failed AssetApiResponse
     */
    public static <T> AssetApiResponse<T> fail(SdkError error) {
        Objects.requireNonNull(error, "error must not be null");
        return new AssetApiResponse<>(false, null, error);
    }

    /**
     * Maps an ApiHttpResponse to an AssetApiResponse.
     *
     * @param response the HTTP response
     * @param <T>      the type of the body
     * @return an AssetApiResponse
     */
    public static <T> AssetApiResponse<T> map(ApiHttpResponse<T> response) {
        if (response == null) {
            return AssetApiResponse.fail(SdkError.apiError("No response received from server", 0));
        }
        if (response.isSuccess()) {
            return AssetApiResponse.success(response.getBody());
        }
        String message = StringUtils.isNotBlank(response.getErrorMessage())
                ? response.getErrorMessage()
                : "Request failed with status " + response.getStatus();
        SdkError error = SdkError.apiError(message, response.getStatus(), response.getErrorMessage());
        return AssetApiResponse.fail(error);
    }


    /**
     * Returns whether this response represents a failed operation.
     *
     * @return true if failed, false otherwise
     */
    public boolean isFailure() {
        return !success;
    }

    /**
     * Returns the typed error if the operation failed.
     *
     * @return an Optional containing the error, or empty if successful
     */
    public Optional<SdkError> getError() {
        return Optional.ofNullable(error);
    }

    /**
     * Returns the body if successful, or throws an exception if failed.
     *
     * @return the response body
     * @throws com.kdiachenko.aemupload.exception.SdkException if the operation failed
     */
    public T getOrThrow() {
        if (success) {
            return body;
        }
        throw error.toException();
    }

    /**
     * Returns the body if successful, or the provided default value if failed.
     *
     * @param defaultValue the default value to return on failure
     * @return the body or the default value
     */
    public T getOrElse(T defaultValue) {
        return success ? body : defaultValue;
    }

    /**
     * Transforms the body using the given function if successful.
     *
     * @param mapper the transformation function
     * @param <R>    the type of the transformed result
     * @return a new AssetApiResponse with the transformed body, or the same error
     */
    public <R> AssetApiResponse<R> map(Function<T, R> mapper) {
        if (success) {
            return AssetApiResponse.success(mapper.apply(body));
        }
        return new AssetApiResponse<>(false, null, error);
    }

    /**
     * Transforms the response using the given function if successful.
     *
     * @param mapper the transformation function returning a new AssetApiResponse
     * @param <R>    the type of the transformed result
     * @return the result of the mapper, or the same error
     */
    public <R> AssetApiResponse<R> flatMap(Function<T, AssetApiResponse<R>> mapper) {
        if (success) {
            return mapper.apply(body);
        }
        return new AssetApiResponse<>(false, null, error);
    }

    /**
     * Executes the given action if the operation was successful.
     *
     * @param action the action to execute with the body
     * @return this response for chaining
     */
    public AssetApiResponse<T> ifSuccess(Consumer<T> action) {
        if (success) {
            action.accept(body);
        }
        return this;
    }

    /**
     * Executes the given action if the operation failed.
     *
     * @param action the action to execute with the error
     * @return this response for chaining
     */
    public AssetApiResponse<T> ifFailure(Consumer<SdkError> action) {
        if (!success && error != null) {
            action.accept(error);
        }
        return this;
    }
}
