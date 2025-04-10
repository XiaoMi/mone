package run.mone.mcp.knowledge.base.entity;

import lombok.Data;

@Data
public class BaseVO<T> {
    private int code;
    private String message;
    private T data;
}
