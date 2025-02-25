```shell
# 安装依赖
npm install -g @mermaid-js/mermaid-cli

# 测试mermaid-cli是否安装成功
mmdc --help
```

```json
# 配置MCP
{
  "mcpServers": {
    "mermaid": {
      "command": "/usr/bin/java",
      "args": [
        "-jar",
        "/Users/kevin/Coding/mone/jcommon/mcp/mcp-mermaid/target/app.jar"
      ],
      "env": {
        "MMDC": "/usr/local/lib/node_modules/bin/mmdc"
      }
    }
  }
}
```