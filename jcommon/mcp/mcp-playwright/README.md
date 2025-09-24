"playwright-mcp": {
"command": "java",
"args": [
"-jar",
"/Users/zhangzhiyong/IdeaProjects/open/mone/jcommon/mcp/mcp-playwright/target/app.jar"
]
}



+ 同时这个服务还是chrome agent 的服务器(起了一个websocket,chrome插件会连接过来)
+ 支持打开tab
  + 打开tab页面
  + 绘制页面(支持数字属性)
  + 截图返回
+ 判断下一步操作