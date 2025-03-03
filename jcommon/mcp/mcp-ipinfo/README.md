```json
# 配置MCP
{
  "mcpServers": {
    "ipinfo": {
      "command": "/usr/bin/java",
      "args": [
        "-jar",
        "/Users/kevin/Coding/mone/jcommon/mcp/mcp-ipinfo/target/app.jar"
      ],
      "env": {
        "IPINFO_API_TOKEN": "<YOUR TOKEN>"
      }
    }
  }
}
```