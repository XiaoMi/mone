package com.google.a2a.common.types;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

import java.util.Map;

/**
 * 表示消息中的不同类型的部分
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TextPart.class, name = "text"),
        @JsonSubTypes.Type(value = FilePart.class, name = "file"),
        @JsonSubTypes.Type(value = DataPart.class, name = "data")
})
public interface Part {
    String getType();
    Map<String, Object> getMetadata();
}

/**
 * 表示消息中的文本部分
 */
@Data
class TextPart implements Part {
    private final String type = "text";
    private String text;
    private Map<String, Object> metadata;
}

/**
 * 表示文件内容
 */
@Data
class FileContent {
    private String name;
    private String mimeType;
    private String bytes;
    private String uri;

    public void validate() {
        if ((bytes == null || bytes.isEmpty()) && (uri == null || uri.isEmpty())) {
            throw new IllegalArgumentException("Either 'bytes' or 'uri' must be present in the file data");
        }
        if (bytes != null && !bytes.isEmpty() && uri != null && !uri.isEmpty()) {
            throw new IllegalArgumentException("Only one of 'bytes' or 'uri' can be present in the file data");
        }
    }
}

/**
 * 表示消息中的文件部分
 */
@Data
class FilePart implements Part {
    private final String type = "file";
    private FileContent file;
    private Map<String, Object> metadata;
}

/**
 * 表示消息中的数据部分
 */
@Data
class DataPart implements Part {
    private final String type = "data";
    private Map<String, Object> data;
    private Map<String, Object> metadata;
} 