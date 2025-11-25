"minzai-mcp": {
      "type": "grpc",
      "sseRemote": true,
      "env": {
        "port": "9786",
        "clientId": "min",
        "token":"token"
      }
}


# 一个陪你聊天的机器人
# minzai的化身,支持绑定databasemcp (可以看成一个新的agent)

# 启动时需要添加ENV
## 用于asr和tts
STEPFUN_API_KEY=xxxxx

# 添加mcp
- claude mcp add --transport http chat http://127.0.0.1:8081/mcp
- codex mcp add chat --url http://127.0.0.1:8081/mcp
- codex mcp list