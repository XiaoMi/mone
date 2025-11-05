package run.mone.hive.mcp.hub;

public enum McpType {

    STDIO,
    GRPC,
    SSE,
    STREAMABLE;

    public static McpType fromString(String type) {
        return McpType.valueOf(type.toUpperCase()) == null ? McpType.STDIO : McpType.valueOf(type.toUpperCase());
    }
}