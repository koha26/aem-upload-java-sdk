package com.kdiachenko.aemupload.utils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Interface for splitting files into chunks.
 */
public interface FileSplitter {
    /**
     * Splits a file into multiple parts, each not exceeding the specified maximum chunk size.
     *
     * @param path the file to split
     * @param maxChunkSize the maximum size of each chunk in bytes
     * @return a list of temporary file paths containing the chunks
     * @throws IOException if an I/O error occurs
     */
    List<Path> splitFile(Path path, long maxChunkSize) throws IOException;
}

