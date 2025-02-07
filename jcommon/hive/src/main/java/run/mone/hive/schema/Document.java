package run.mone.hive.schema;

import lombok.Data;

@Data
public class Document {
    private String rootPath;
    private String filename;
    private String content;
    private String rootRelativePath;

    public Document() {}

    public Document(String rootPath, String filename, String content) {
        this.rootPath = rootPath;
        this.filename = filename;
        this.content = content;
        this.rootRelativePath = rootPath + "/" + filename;
    }
} 