+ 一个简单的agent框架
+ agent代码借鉴自MetaGpt (https://github.com/geekan/MetaGPT)
+ mcp代码借鉴自Spring Ai 和 Cline(https://github.com/cline/cline)
+ 提示词很多直接照搬的cline (https://github.com/cline/cline)
+ 有 team role action
+ 主要的思考路径:think->act->think->act
+ 一只蜜蜂很傻,但一群蜜蜂很聪明
+ idea debug test 需要给ide 添加参数:-Deditable.java.test.console=true
+ 启动hive的app方式:

```
  "my-server": {  
  "command": "java",  
  "args": [  
  "-jar",  
  "/Users/zhangzhiyong/IdeaProjects/open/mone/jcommon/hive/target/app.jar"  
  ]  
  }  
```
+ 方便杀死进程:  jps -l|grep app.jar|awk -F '' '{print $1}'|xargs kill -9
+ grpc 支持通过headers 传递信息(clientId token)
+ 支持权限验证
+ 支持server push 信息回来
+ 任何mcp都有一个chat工具(方便和用户沟通)

+ 2025年06月09日
    + 如果用户断开连线,则Agent被摘除
    + 用户很久没有消息也会摘除掉

+ 2025年09月08日
    + 支持打断  (也得通过意图识别来判断)
    + 支持agent主动沟通  (意图识别需要和用户沟通了)
    + 模型切换  (意图识别需要切换模型了)
    + 添加记忆代码
    + 添加todolist代码
    + 上下文压缩
