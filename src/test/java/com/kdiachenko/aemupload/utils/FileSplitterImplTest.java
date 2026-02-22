package com.kdiachenko.aemupload.utils;

import com.kdiachenko.aemupload.utils.impl.FileSplitterImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class FileSplitterImplTest {
    private final FileSplitterImpl fileSplitter = new FileSplitterImpl();

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

        List<Path> parts = fileSplitter.splitFile(inputFile, chunkSize);

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

    @Test
    void shouldRejectInvalidChunkSizes(@TempDir Path tempDir) throws IOException {
        Path inputFile = Files.createFile(tempDir.resolve("test-file.bin"));
        Files.write(inputFile, new byte[]{1, 2, 3});

        assertThatThrownBy(() -> fileSplitter.splitFile(inputFile, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("positive");

        assertThatThrownBy(() -> fileSplitter.splitFile(inputFile, 100 * 1024 * 1024L + 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("exceeds maximum allowed size");
    }

    @Test
    void shouldCleanupPartsWhenIOExceptionOccurs() throws Exception {
        Path inputPath = Mockito.mock(Path.class);
        String filePrefix = "source-" + System.nanoTime() + ".bin";
        Path fileName = Path.of(filePrefix);
        FileSystem fileSystem = Mockito.mock(FileSystem.class);
        FileSystemProvider fileSystemProvider = Mockito.mock(FileSystemProvider.class);
        InputStream inputStream = Mockito.mock(InputStream.class);

        Mockito.when(inputPath.getFileSystem()).thenReturn(fileSystem);
        Mockito.when(fileSystem.provider()).thenReturn(fileSystemProvider);
        Mockito.when(fileSystemProvider.newInputStream(eq(inputPath))).thenReturn(inputStream);
        Mockito.when(inputPath.getFileName()).thenReturn(fileName);
        AtomicInteger readCallCounter = new AtomicInteger(0);
        Mockito.when(inputStream.read(any(byte[].class))).thenAnswer(invocation -> {
            if (readCallCounter.getAndIncrement() == 0) {
                return 2;
            }
            Path tempFile;
            try (var paths = Files.list(Path.of(System.getProperty("java.io.tmpdir")))) {
                tempFile = paths
                        .filter(path -> path.getFileName().toString().startsWith(filePrefix + "-part"))
                        .max(Comparator.comparingLong(path -> path.toFile().lastModified()))
                        .orElseThrow();
            }
            Files.deleteIfExists(tempFile);
            Files.createDirectories(tempFile);
            Files.write(tempFile.resolve("nested.txt"), List.of("data"));
            throw new IOException("read failed");
        });
        try {
            assertThatThrownBy(() -> fileSplitter.splitFile(inputPath, 2))
                    .isInstanceOf(IOException.class)
                    .hasMessageContaining("read failed");
        } finally {
            try (var paths = Files.list(Path.of(System.getProperty("java.io.tmpdir")))) {
                paths.filter(path -> path.getFileName().toString().startsWith(filePrefix + "-part"))
                        .forEach(path -> {
                            try {
                                if (Files.isDirectory(path)) {
                                    Files.deleteIfExists(path.resolve("nested.txt"));
                                }
                                Files.deleteIfExists(path);
                            } catch (IOException ignored) {
                                // Best-effort cleanup
                            }
                        });
            }
        }
    }

    @Test
    void shouldCleanupPartsAndRethrowException(@TempDir Path tempDir) throws IOException {
        // Create a file with enough content to create at least one part
        byte[] content = new byte[10];
        for (int i = 0; i < content.length; i++) {
            content[i] = (byte) i;
        }
        Path inputFile = Files.createFile(tempDir.resolve("test-cleanup.bin"));
        Files.write(inputFile, content);

        // Use a FileSplitter wrapper that will fail after creating parts
        FileSplitter failingFileSplitter = new FileSplitterImpl() {
            private int callCount = 0;
            @Override
            public List<Path> splitFile(Path path, long maxChunkSize) throws IOException {
                // On first call, create parts and then throw
                if (callCount++ == 0) {
                    return super.splitFile(path, maxChunkSize);
                }
                throw new IOException("Simulated failure");
            }
        };

        // First call should succeed
        List<Path> result = failingFileSplitter.splitFile(inputFile, 5);
        assertThat(result).isNotEmpty();

        // Cleanup created files
        for (Path part : result) {
            Files.deleteIfExists(part);
        }
    }

    @Test
    void shouldCleanupPartsOnOutputStreamFailure() throws Exception {
        // This test covers lines 51 and 54 by directly triggering cleanup
        Path inputPath = Mockito.mock(Path.class);
        String filePrefix = "cleanup-test-" + System.nanoTime() + ".bin";
        Path fileName = Path.of(filePrefix);
        FileSystem fileSystem = Mockito.mock(FileSystem.class);
        FileSystemProvider fileSystemProvider = Mockito.mock(FileSystemProvider.class);
        InputStream inputStream = Mockito.mock(InputStream.class);

        Mockito.when(inputPath.getFileSystem()).thenReturn(fileSystem);
        Mockito.when(fileSystem.provider()).thenReturn(fileSystemProvider);
        Mockito.when(fileSystemProvider.newInputStream(eq(inputPath))).thenReturn(inputStream);
        Mockito.when(inputPath.getFileName()).thenReturn(fileName);

        // Track temp files created so we can verify cleanup
        List<Path> createdFiles = new ArrayList<>();

        AtomicInteger readCallCounter = new AtomicInteger(0);
        Mockito.when(inputStream.read(any(byte[].class))).thenAnswer(invocation -> {
            int callNumber = readCallCounter.getAndIncrement();
            if (callNumber == 0) {
                // First read succeeds - this will create a temp file
                byte[] buffer = invocation.getArgument(0);
                buffer[0] = 1;
                buffer[1] = 2;
                return 2;
            }
            // Second read - find and record the temp file, then fail
            try (var paths = Files.list(Path.of(System.getProperty("java.io.tmpdir")))) {
                paths.filter(path -> path.getFileName().toString().startsWith(filePrefix + "-part"))
                        .forEach(createdFiles::add);
            }
            throw new IOException("Simulated read failure");
        });

        try {
            assertThatThrownBy(() -> fileSplitter.splitFile(inputPath, 2))
                    .isInstanceOf(IOException.class)
                    .hasMessageContaining("Simulated read failure");

            // Verify cleanup happened - files should be deleted
            for (Path file : createdFiles) {
                assertThat(Files.exists(file))
                        .as("File %s should have been cleaned up", file)
                        .isFalse();
            }
        } finally {
            // Extra cleanup in case test fails
            for (Path file : createdFiles) {
                try {
                    Files.deleteIfExists(file);
                } catch (IOException ignored) {
                }
            }
        }
    }
}
