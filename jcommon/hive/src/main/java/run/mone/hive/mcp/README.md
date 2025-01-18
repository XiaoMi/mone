# 创建mcp server
- 参考hive/mcp/demo中的SimpleMcpServer
  - 基于sse通信 ：在application.properties中配置sse.enabled=true
  - 基于stdio通信 ：在application.properties中配置stdio.enabled=true
- mvn -U clean package -Dmaven.test.skip=true 打包，target中的app.jar即为可运行的目标文件

- 在mcp client中配置(eg： cline中)，即可发现刚刚写好的mcp server
```json
{
  "mcpServers": {
    "my-server": {
      "command": "java",
      "args": [
        "-jar",
        "/home/mason/Documents/Workspaces/github/mone/jcommon/hive/target/app.jar"
      ]
    }
  }
}
```
