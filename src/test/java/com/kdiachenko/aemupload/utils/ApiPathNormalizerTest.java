package com.kdiachenko.aemupload.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ApiPathNormalizerTest {

    @ParameterizedTest(name = "[{index}] input: \"{0}\" → expected: \"{1}\"")
    @MethodSource("pathNormalizationProvider")
    @DisplayName("should normalize paths correctly")
    void shouldNormalizePaths(String input, String expected) {
        String result = ApiPathNormalizer.normalize(input);
        assertThat(result).isEqualTo(expected);
    }

    private static Stream<Arguments> pathNormalizationProvider() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of("", "/api/assets/"),
                Arguments.of("/content/dam/myfolder/image.png", "/api/assets/myfolder/image.png"),
                Arguments.of("/myfolder/image.png", "/api/assets/myfolder/image.png"),
                Arguments.of("/content/dam", "/api/assets/content/dam"),
                Arguments.of("/content/dam/", "/api/assets/"),
                Arguments.of("/content/dam-extra/path", "/api/assets/content/dam-extra/path")
        );
    }
}
