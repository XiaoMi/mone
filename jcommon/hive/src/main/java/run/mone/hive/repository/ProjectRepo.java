package run.mone.hive.repository;

import run.mone.hive.schema.Document;
import lombok.Data;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;

@Data
public class ProjectRepo {
    private final Map<String, Document> docs;
    private final Map<String, Document> srcs;
    private String workdir;
    
    public ProjectRepo(String workdir) {
        this.workdir = workdir;
        this.docs = new ConcurrentHashMap<>();
        this.srcs = new ConcurrentHashMap<>();
    }

    public CompletableFuture<Document> save(String path, Document doc) {
        return CompletableFuture.supplyAsync(() -> {
            docs.put(path, doc);
            return doc;
        });
    }

    public CompletableFuture<Document> get(String path) {
        return CompletableFuture.supplyAsync(() -> docs.get(path));
    }

    public Map<String, Document> getAllFiles() {
        Map<String, Document> allFiles = new ConcurrentHashMap<>();
        allFiles.putAll(docs);
        allFiles.putAll(srcs);
        return allFiles;
    }
} 