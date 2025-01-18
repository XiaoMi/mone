-这个项目是智能化办公的开端
  + 更好的支持编码(业务代码自动生成)
  + ai赋能excel(不再需要你掌握任何函数)
  + ai赋能翻译(跨国企业必备)
  + ai赋能问答(有不懂的问ai)
+ SQL分析prompt
    + csvToCreateTableSql 根据csv生成create table sql
  > 返回结果示例：
  > {
  "sql": "CREATE TABLE project_pipelines ( id INT AUTO_INCREMENT PRIMARY KEY COMMENT '唯一标识符', project_id INT NOT NULL COMMENT '项目ID', project_name VARCHAR(255) NOT NULL COMMENT '项目名', pipeline_id INT NOT NULL COMMENT '流水线ID', pipeline_name VARCHAR(255) NOT NULL COMMENT '流水线名称', pipeline_status ENUM('active', 'inactive', 'error', 'running', 'paused') COMMENT '流水线状态' ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目流水线信息表'"
  }
    + generateSelectSql 基于自然语言生成select sql
  > 返回结果示例：
  > {"sql":"select * from project_pipeline_requests where pipeline_name = '预发环境'"}

+ 项目使用了mybatis-flex，拉取代码后若在idea中发现有miss的类，直接一发编译(mvn -U clean compile)即可生成所需类
+ agent 包路径下,是在服务器上跑的机器人,他会和用户客户单跑的rebot连接

+ jvm参数：--add-opens=java.base/java.time=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/java.math=ALL-UNNAMED --add-opens=java.base/sun.reflect=ALL-UNNAMED --add-exports=java.base/sun.reflect.annotation=ALL-UNNAMED --add-exports=java.base/sun.reflect.generics.reflectiveObjects=ALL-UNNAMED --enable-preview
+ z里边支持的函数
  + ${prompt_value('fqcn','')}  //取值,但允许空值
  + ${if_load_prompt('useTable','$promptId')} //如果某个条件是对的,则直接加载某个prompt信息

- bot目前可以设置的meta
  - stream: true/false， bot是否为流式(流式会在m78直接流式调用AI)
  - visualize：bar_chart， bot在返回数据结果时是否进行可视化(目前支持柱状图)
  - appId：填移动端appId，与app关联
  - customCommands: [命令1,命令2...]，用来与stream:true 协同，在stream为true时，如果有输入命中customFlowCommands，则通过tianye调用
  - singleFlowJudge：true/false，bot关联单个工作流时，是否进行关联性检查，默认为false
  - export: true/false, 是否导出bot为http api
  - eager：在流式bot中是否自动开启主动反问

+ vscode 插件的配置要加的label
  + export true
  + private_prompt true
  + stream true
+ 目前支持的bot
+ 100345