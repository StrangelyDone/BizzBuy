package com.example.BizzBuy.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
@RequiredArgsConstructor
public class JsonFileManager {

    private final ObjectMapper objectMapper;
    @Value("${app.data-directory:src/main/resources/data}")
    private String dataDirectory;
    private final Map<String, ReentrantReadWriteLock> locks = new ConcurrentHashMap<>();

    public <T> List<T> readList(String fileName, Class<T> clazz) {
        ReentrantReadWriteLock lock = locks.computeIfAbsent(fileName, k -> new ReentrantReadWriteLock());
        lock.readLock().lock();
        try {
            Path path = resolvePath(fileName);
            ensureFileExists(path);
            JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
            return objectMapper.readValue(path.toFile(), type);
        } catch (IOException e) {
            return Collections.emptyList();
        } finally {
            lock.readLock().unlock();
        }
    }

    public <T> void writeList(String fileName, List<T> data) {
        ReentrantReadWriteLock lock = locks.computeIfAbsent(fileName, k -> new ReentrantReadWriteLock());
        lock.writeLock().lock();
        try {
            Path path = resolvePath(fileName);
            ensureFileExists(path);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), data);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to write data file: " + fileName, e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private Path resolvePath(String fileName) throws IOException {
        Path dir = Paths.get(dataDirectory);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
        return dir.resolve(fileName);
    }

    private void ensureFileExists(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createFile(path);
            Files.write(path, "[]".getBytes());
        }
    }
}

