package com.karthic.schoolassessment.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Handles all file system operations: JSON read/write, file copy, directory management.
 */
public interface StorageService {

    <T> T readJson(Path path, Class<T> type) throws IOException;

    <T> List<T> readJsonList(Path path, Class<T> elementType) throws IOException;

    void writeJson(Path path, Object value) throws IOException;

    void copyFile(Path source, Path destination) throws IOException;

    void deleteDirectory(Path directory) throws IOException;

    void createDirectories(Path path) throws IOException;

    boolean exists(Path path);
}
