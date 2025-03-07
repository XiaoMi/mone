+ 可以操控本地idea的mcp
+ 支持功能
  + 获取当前Editor的内容
  + 支持生成注释
  + 添加注释
  + 生成代码
  + 方法重命名
  + Push代码

"idea-mcp": {
"command": "java",
"type": "sse",
"url": "http://localhost:8080",
"args": [
"-jar",
"-Didea.port=30000",
"-DGOOGLE_AI_GATEWAY=",
"/Users/zhangzhiyong/IdeaProjects/open/mone/jcommon/mcp/mcp-idea/target/app.jar"
],
"env": {
"IDEA_PORT": "30000"
}
}