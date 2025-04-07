package com.kdiachenko.aemupload.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FileSplitUtilTest {

    @Test
    @DisplayName("should split file into multiple parts with given chunk size")
    void shouldSplitFileCorrectly(@TempDir Path tempDir) throws IOException {
        // Arrange: create a test file with predictable 1050 bytes
        byte[] content = new byte[1050];
        for (int i = 0; i < content.length; i++) {
            content[i] = (byte) (i % 256);
        }

        Path inputFile = Files.createFile(tempDir.resolve("test-file.bin"));
        Files.write(inputFile, content);

        int chunkSize = 256;

        // Act
        List<Path> parts = FileSplitUtil.splitFile(inputFile, chunkSize);

        // Assert
        assertThat(parts).hasSize(5); // 256 * 4 + 26 = 1050

        int totalBytes = 0;
        for (int i = 0; i < parts.size(); i++) {
            byte[] partBytes = Files.readAllBytes(parts.get(i));
            totalBytes += partBytes.length;

            // Validate each chunk is ≤ chunkSize
            assertThat(partBytes.length).isLessThanOrEqualTo(chunkSize);

            // Validate byte content matches original
            for (int j = 0; j < partBytes.length; j++) {
                int expected = (i * (int) chunkSize + j) % 256;
                assertThat(partBytes[j]).isEqualTo((byte) expected);
            }
        }

        // Validate total bytes written = original file size
        assertThat(totalBytes).isEqualTo(1050);
    }
}
