package com.kdiachenko.aemupload.utils.impl;

import com.kdiachenko.aemupload.utils.FileSplitter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of FileSplitter.
 */
public class FileSplitterImpl implements FileSplitter {

    /**
     * Maximum allowed chunk size (100 MB) to prevent excessive memory allocation.
     */
    private static final long MAX_ALLOWED_CHUNK_SIZE = 100 * 1024 * 1024L;

    @Override
    public List<Path> splitFile(final Path path, final long maxChunkSize) throws IOException {
        if (maxChunkSize <= 0) {
            throw new IllegalArgumentException("maxChunkSize must be positive, got: " + maxChunkSize);
        }
        if (maxChunkSize > MAX_ALLOWED_CHUNK_SIZE) {
            throw new IllegalArgumentException("maxChunkSize exceeds maximum allowed size of "
                    + MAX_ALLOWED_CHUNK_SIZE + " bytes, got: " + maxChunkSize);
        }

        var partCounter = 1;
        var buffer = new byte[(int) maxChunkSize];

        List<Path> list = new ArrayList<>();
        try (var inputStream = Files.newInputStream(path)) {
            var fileName = path.getFileName().toString();

            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                Path newFile = Files.createTempFile(fileName + "-part" + partCounter++, "");
                try (var outputStream = Files.newOutputStream(newFile, StandardOpenOption.CREATE)) {
                    outputStream.write(buffer, 0, bytesRead);
                    list.add(newFile);
                }
            }
        } catch (IOException e) {
            // Clean up any partial files on failure
            for (Path part : list) {
                try {
                    Files.deleteIfExists(part);
                } catch (IOException ignored) {
                    // Best effort cleanup
                }
            }
            throw e;
        }
        return list;
    }
}
