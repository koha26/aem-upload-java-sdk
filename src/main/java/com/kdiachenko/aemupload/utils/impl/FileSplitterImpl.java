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
    @Override
    public List<Path> splitFile(final Path path, final long maxChunkSize) throws IOException {
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
        }
        return list;
    }
}
