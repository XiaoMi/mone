package run.mone.hive.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.Map;

@Slf4j
public class JsonUtils {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void writeJsonFile(Path path, Object data) {
        try {
            mapper.writeValue(path.toFile(), data);
        } catch (Exception e) {
            log.error("Error writing JSON file: {}", e.getMessage());
            throw new RuntimeException("Failed to write JSON file", e);
        }
    }

    public static Map<String, Object> readJsonFile(Path path) {
        try {
            return mapper.readValue(path.toFile(), Map.class);
        } catch (Exception e) {
            log.error("Error reading JSON file: {}", e.getMessage());
            throw new RuntimeException("Failed to read JSON file", e);
        }
    }
} 