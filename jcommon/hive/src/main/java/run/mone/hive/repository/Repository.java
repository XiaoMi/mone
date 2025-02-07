package run.mone.hive.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import lombok.extern.slf4j.Slf4j;

/**
 * Repository for managing project documents and source code
 */
@Slf4j
public class Repository {
    private final Path baseDir;
    private final Path designDir;
    private final Path taskDir;
    private final Path sourceDir;

    public Repository(String basePath) {
        this.baseDir = Paths.get(basePath);
        this.designDir = baseDir.resolve("design");
        this.taskDir = baseDir.resolve("tasks");
        this.sourceDir = baseDir.resolve("src");
        initDirectories();
    }

    private void initDirectories() {
        try {
            Files.createDirectories(designDir);
            Files.createDirectories(taskDir);
            Files.createDirectories(sourceDir);
        } catch (IOException e) {
            log.error("Failed to create repository directories", e);
            throw new RuntimeException("Repository initialization failed", e);
        }
    }

    /**
     * Get system design document content
     */
    public String getSystemDesign(String filename) {
        try {
            Path filePath = designDir.resolve(filename);
            return Files.readString(filePath);
        } catch (IOException e) {
            log.error("Failed to read system design: {}", filename, e);
            throw new RuntimeException("Could not read system design", e);
        }
    }

    /**
     * Get task document content
     */
    public String getTask(String filename) {
        try {
            Path filePath = taskDir.resolve(filename);
            return Files.readString(filePath);
        } catch (IOException e) {
            log.error("Failed to read task: {}", filename, e);
            throw new RuntimeException("Could not read task", e);
        }
    }

    /**
     * Get source code content
     */
    public String getSourceCode(String filename) {
        try {
            Path filePath = sourceDir.resolve(filename);
            return Files.readString(filePath);
        } catch (IOException e) {
            log.error("Failed to read source code: {}", filename, e);
            throw new RuntimeException("Could not read source code", e);
        }
    }

    /**
     * Save system design document
     */
    public void saveSystemDesign(String filename, String content) {
        try {
            Path filePath = designDir.resolve(filename);
            Files.writeString(filePath, content);
            log.debug("Saved system design: {}", filename);
        } catch (IOException e) {
            log.error("Failed to save system design: {}", filename, e);
            throw new RuntimeException("Could not save system design", e);
        }
    }

    /**
     * Save task document
     */
    public void saveTask(String filename, String content) {
        try {
            Path filePath = taskDir.resolve(filename);
            Files.writeString(filePath, content);
            log.debug("Saved task: {}", filename);
        } catch (IOException e) {
            log.error("Failed to save task: {}", filename, e);
            throw new RuntimeException("Could not save task", e);
        }
    }

    /**
     * Save source code
     */
    public void saveSourceCode(String filename, String content) {
        try {
            Path filePath = sourceDir.resolve(filename);
            Files.writeString(filePath, content);
            log.debug("Saved source code: {}", filename);
        } catch (IOException e) {
            log.error("Failed to save source code: {}", filename, e);
            throw new RuntimeException("Could not save source code", e);
        }
    }
} 