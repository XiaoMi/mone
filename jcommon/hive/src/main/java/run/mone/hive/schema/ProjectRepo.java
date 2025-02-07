package run.mone.hive.schema;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Repository for managing project files and documents
 */
@Slf4j
public class ProjectRepo {
    private final Path workdir;
    @Getter
    private final SourceCodeRepo srcs;
    @Getter
    private final DocumentRepo docs;

    public ProjectRepo(String workdir) {
        this.workdir = Paths.get(workdir);
        this.srcs = new SourceCodeRepo(this.workdir.resolve("src"));
        this.docs = new DocumentRepo(this.workdir.resolve("docs"));
        initDirectories();
    }

    private void initDirectories() {
        try {
            Files.createDirectories(workdir);
            Files.createDirectories(srcs.getBasePath());
            Files.createDirectories(docs.getBasePath());
        } catch (IOException e) {
            log.error("Failed to create project directories", e);
            throw new RuntimeException("Project initialization failed", e);
        }
    }

    public CompletableFuture<Document> get(String filename) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Path filePath = workdir.resolve(filename);
                if (!Files.exists(filePath)) {
                    return null;
                }
                String content = Files.readString(filePath);
                return new Document("", filename, content);
            } catch (IOException e) {
                log.error("Failed to read file: {}", filename, e);
                return null;
            }
        });
    }

    public CompletableFuture<Void> save(String filename, String content) {
        return CompletableFuture.runAsync(() -> {
            try {
                Path filePath = workdir.resolve(filename);
                Files.createDirectories(filePath.getParent());
                Files.writeString(filePath, content);
                log.debug("Saved file: {}", filename);
            } catch (IOException e) {
                log.error("Failed to save file: {}", filename, e);
                throw new RuntimeException("Failed to save file", e);
            }
        });
    }

    public CompletableFuture<Void> save(String filename, List<String> dependencies, String content) {
        return CompletableFuture.runAsync(() -> {
            try {
                Path filePath = workdir.resolve(filename);
                Files.createDirectories(filePath.getParent());
                Files.writeString(filePath, content);

                // Save dependencies information
                if (dependencies != null && !dependencies.isEmpty()) {
                    Path depsPath = filePath.resolveSibling(filePath.getFileName() + ".deps");
                    Files.write(depsPath, dependencies);
                }

                log.debug("Saved file with dependencies: {}", filename);
            } catch (IOException e) {
                log.error("Failed to save file with dependencies: {}", filename, e);
                throw new RuntimeException("Failed to save file", e);
            }
        });
    }

    public CompletableFuture<List<String>> getDependencies(String filename) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Path depsPath = workdir.resolve(filename + ".deps");
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
                Files.exists(workdir.resolve(filename))
        );
    }

    public CompletableFuture<Void> delete(String filename) {
        return CompletableFuture.runAsync(() -> {
            try {
                Path filePath = workdir.resolve(filename);
                Files.deleteIfExists(filePath);
                // Also delete dependencies file if exists
                Path depsPath = filePath.resolveSibling(filePath.getFileName() + ".deps");
                Files.deleteIfExists(depsPath);
                log.debug("Deleted file: {}", filename);
            } catch (IOException e) {
                log.error("Failed to delete file: {}", filename, e);
                throw new RuntimeException("Failed to delete file", e);
            }
        });
    }

    public Path getWorkdir() {
        return workdir;
    }
}
