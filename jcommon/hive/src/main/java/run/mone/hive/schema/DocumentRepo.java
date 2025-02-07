package run.mone.hive.schema;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

/**
 * Repository for managing documentation files
 */
@Slf4j
public class DocumentRepo {
    @Getter
    private final Path basePath;

    public DocumentRepo(Path basePath) {
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
                log.error("Failed to read document: {}", filename, e);
                return null;
            }
        });
    }

    public CompletableFuture<Void> save(String filename, String content) {
        return CompletableFuture.runAsync(() -> {
            try {
                Path filePath = basePath.resolve(filename);
                Files.createDirectories(filePath.getParent());
                Files.writeString(filePath, content);
                log.debug("Saved document: {}", filename);
            } catch (IOException e) {
                log.error("Failed to save document: {}", filename, e);
                throw new RuntimeException("Failed to save document", e);
            }
        });
    }

    public CompletableFuture<Boolean> exists(String filename) {
        return CompletableFuture.supplyAsync(() ->
                Files.exists(basePath.resolve(filename))
        );
    }

    public CompletableFuture<Void> delete(String filename) {
        return CompletableFuture.runAsync(() -> {
            try {
                Path filePath = basePath.resolve(filename);
                Files.deleteIfExists(filePath);
                log.debug("Deleted document: {}", filename);
            } catch (IOException e) {
                log.error("Failed to delete document: {}", filename, e);
                throw new RuntimeException("Failed to delete document", e);
            }
        });
    }
} 