+ agent代码借鉴自MetaGpt (https://github.com/geekan/MetaGPT)
+ mcp代码借鉴自Spring Ai 和 Cline(https://github.com/cline/cline)
+ 有 team role action
+ 主要的思考路径:think->act->think->act
+ 蜜蜂很傻,但一群蜜蜂很聪明
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