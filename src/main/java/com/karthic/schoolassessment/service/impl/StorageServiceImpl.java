package com.karthic.schoolassessment.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.karthic.schoolassessment.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class StorageServiceImpl implements StorageService {

    private static final Logger log = LoggerFactory.getLogger(StorageServiceImpl.class);

    private final ObjectMapper objectMapper;

    public StorageServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public <T> T readJson(Path path, Class<T> type) throws IOException {
        log.debug("Reading JSON from: {}", path);
        return objectMapper.readValue(path.toFile(), type);
    }

    @Override
    public <T> List<T> readJsonList(Path path, Class<T> elementType) throws IOException {
        log.debug("Reading JSON list from: {}", path);
        var listType = objectMapper.getTypeFactory().constructCollectionType(List.class, elementType);
        return objectMapper.readValue(path.toFile(), listType);
    }

    @Override
    public void writeJson(Path path, Object value) throws IOException {
        createDirectories(path.getParent());
        log.debug("Writing JSON to: {}", path);
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), value);
    }

    @Override
    public void copyFile(Path source, Path destination) throws IOException {
        createDirectories(destination.getParent());
        log.debug("Copying {} to {}", source, destination);
        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public void deleteDirectory(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            return;
        }
        log.info("Deleting directory: {}", directory);
        try (Stream<Path> paths = Files.walk(directory)) {
            paths.sorted(Comparator.reverseOrder())
                 .forEach(path -> {
                     try {
                         Files.delete(path);
                     } catch (IOException e) {
                         throw new UncheckedIOException(e);
                     }
                 });
        }
    }

    @Override
    public void createDirectories(Path path) throws IOException {
        if (path != null && !Files.exists(path)) {
            Files.createDirectories(path);
            log.debug("Created directory: {}", path);
        }
    }

    @Override
    public boolean exists(Path path) {
        return path != null && Files.exists(path);
    }
}
