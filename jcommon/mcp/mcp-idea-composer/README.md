+ 可以操控本地idea的mcp
+ 支持功能
  + composer功能

# stdio mcp配置
"database-idea-composer": {
"command": "java",
"args": [
"-jar",
"-Didea.port=30000",
"-DGOOGLE_AI_GATEWAY=",
"/Users/ericgreen/mycode/mone/jcommon/mcp/macp-idea-composer/target/app.jar"
],
"type": "sse",
"sseRemote": false,
"url": "http://localhost:8080",
"env": {
"IDEA_PORT": "30000"
}
}

{
"mcpServers": {
"database-idea-composer": {
"type": "grpc",
"sseRemote": true,
"env": {
"host": "10.38.216.204",
"port": "9786"
}
}
}
}

# remote mcp启动需要环境变量
GOOGLE_AI_GATEWAY=
GOOGLE_API_KEY=
IDEA_PORT=30000
STEPFUN_API_KEY=
OPENROUTER_API_KEY=