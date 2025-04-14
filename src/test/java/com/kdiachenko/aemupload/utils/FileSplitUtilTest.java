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
        byte[] content = new byte[1050];
        for (int i = 0; i < content.length; i++) {
            content[i] = (byte) (i % 256);
        }

        Path inputFile = Files.createFile(tempDir.resolve("test-file.bin"));
        Files.write(inputFile, content);

        int chunkSize = 256;

        List<Path> parts = FileSplitUtil.splitFile(inputFile, chunkSize);

        assertThat(parts).hasSize(5);
        assertPartSizes(parts, chunkSize);
    }

    private void assertPartSizes(List<Path> parts, int chunkSize) throws IOException {
        int totalBytes = 0;
        for (int i = 0; i < parts.size(); i++) {
            byte[] partBytes = Files.readAllBytes(parts.get(i));
            totalBytes += partBytes.length;

            assertThat(partBytes.length).isLessThanOrEqualTo(chunkSize);

            for (int j = 0; j < partBytes.length; j++) {
                int expected = (i * chunkSize + j) % 256;
                assertThat(partBytes[j]).isEqualTo((byte) expected);
            }
        }

        assertThat(totalBytes).isEqualTo(1050);
    }
}
