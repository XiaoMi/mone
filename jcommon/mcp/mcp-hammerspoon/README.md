


# 用法说明：
- 本地安装hammerspoon
  - https://github.com/Hammerspoon/hammerspoon 下载安装包https://github.com/Hammerspoon/hammerspoon/releases/latest
  - 将本工程 resources/init.lua 脚本 放到 ~/.hammerspoon 目录下
  - 启动hammerspoon
- IDE 配置该mcp，mcp就可以与hammerspoon联动，进行本机app的操作
  "hammerspoon_mcp": {
  "command": "java",
  "args": [
  "-jar",
  "/opt/workspace_all/workspace_mi/mone-all/mone/jcommon/mcp/mcp-hammerspoon/target/app.jar"
  ]
  }