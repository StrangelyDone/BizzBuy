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
import java.util.List;
import java.util.ArrayList;


@Component
@RequiredArgsConstructor
public class JsonFileManager {

    private final ObjectMapper objectMapper;
    @Value("${app.data-directory:src/main/resources/data}")
    private String dataDirectory;   // this stores the location of the folder where all the json files are there as a string

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
            Files.write(path, "[]".getBytes()); // prevents object mapper from crashing if json file is empty
        }
    }

    public <T> List<T> readList(String fileName, Class<T> class_name) {      //Without generics, you would have to write a separate method for every single class you want to save

        try {
            Path path = resolvePath(fileName);
            ensureFileExists(path);
            JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, class_name); // to get the object type
            return objectMapper.readValue(path.toFile(), type); // this is what main thing is
        } catch (IOException e) {
            System.out.println("Error reading " + fileName + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }


    public <T> void writeList(String fileName, List<T> data) {

        try {
            Path path = resolvePath(fileName);
            ensureFileExists(path);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), data);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to save data to file: " + fileName, e);
        }
    }

}

