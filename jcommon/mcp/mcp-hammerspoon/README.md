


# 用法说明：
- 本地安装hammerspoon
  - https://github.com/Hammerspoon/hammerspoon 下载安装包https://github.com/Hammerspoon/hammerspoon/releases/latest
  - 将本工程 resources/init.lua脚本 及resources/modules目录 放到 ~/.hammerspoon 目录下
  - 启动hammerspoon
- IDE 配置该mcp，mcp就可以与hammerspoon联动，进行本机app的操作
  "hammerspoon_mcp": {
  "command": "java",
  "args": [
  "-jar",
  "/opt/workspace_all/workspace_mi/mone-all/mone/jcommon/mcp/mcp-hammerspoon/target/app.jar"
  ]
  }


## TrigerTradeProFunction使用说明
### 前置操作
- 左侧窗口最小化
- 期权链选择 看跌（会全局保存）
- 行权列表选择20（会全局保存20条展示）

### 调用步骤
- 搜索并打开股票 searchStock("小米集团")
- 点击期权链 clickOptionsChain()
- 卖1手put sellPutOption()



