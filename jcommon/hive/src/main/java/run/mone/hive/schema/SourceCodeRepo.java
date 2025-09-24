package run.mone.hive.schema;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Repository for managing source code files
 */
@Slf4j
public class SourceCodeRepo {
    @Getter
    private final Path basePath;

    public SourceCodeRepo(Path basePath) {
        this.basePath = basePath;
    }

    public CompletableFuture<Document> get(String filename) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Path filePath = basePath.resolve(filename);
                if (!Files.exists(filePath)) {
                    return null;
                }
                String content = Files.readString(filePath);
                return new Document(filename, filename, content);
            } catch (IOException e) {
                log.error("Failed to read source file: {}", filename, e);
                return null;
            }
        });
    }

    public CompletableFuture<Void> save(String filename, List<String> dependencies, String content) {
        return CompletableFuture.runAsync(() -> {
            try {
                Path filePath = basePath.resolve(filename);
                Files.createDirectories(filePath.getParent());
                Files.writeString(filePath, content);

                // Save dependencies if provided
                if (dependencies != null && !dependencies.isEmpty()) {
                    Path depsPath = filePath.resolveSibling(filePath.getFileName() + ".deps");
                    Files.write(depsPath, dependencies);
                }

                log.debug("Saved source file: {}", filename);
            } catch (IOException e) {
                log.error("Failed to save source file: {}", filename, e);
                throw new RuntimeException("Failed to save source file", e);
            }
        });
    }

    public CompletableFuture<List<String>> getDependencies(String filename) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Path depsPath = basePath.resolve(filename + ".deps");
                if (!Files.exists(depsPath)) {
                    return new ArrayList<>();
                }
                return Files.readAllLines(depsPath);
            } catch (IOException e) {
                log.error("Failed to read dependencies: {}", filename, e);
                return new ArrayList<>();
            }
        });
    }

    public CompletableFuture<Boolean> exists(String filename) {
        return CompletableFuture.supplyAsync(() ->
                Files.exists(basePath.resolve(filename))
        );
    }
} 