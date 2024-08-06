## startup config
+ build.gradle.kts
  + 添加Maven repository
+ ResourceUtils 
  + ULTRAMAN_URL_PREFIX
    + 修改域名为真实域名
  + Const.BOT_URL
    + 修改域名为真实域名
  + Const.CONF_DASH_URL
    + 修改IP为真实IP
  + Const.CONF_AI_PROXY_URL
    + 修改IP为真实IP
  + Const.CONF_M78_URL
    + 修改域名为真实域名
+ UltramanWindowFactory
  + 修改nginxChatUrl为真实url
+ WsClient
  + 修改ws url为真实url
+ GuideService
  + HttpClient.post修改为真实url
+ BotService
  + 修改BOT_URL为真实的BOT调用的url
+ OpenImageConsumer
  + imgUrl修改
+ plugin.xml
  + vendor里的url改为真实url

## change log
+ 2023年12月25日
    + 1.代码注释 comment_side_car
    + 2.代码建议 code_suggest_sidecar
    + 给方法添加注释 comment_2 
    + 2.生成代码 biz_sidecar
        + 分析代码范围 analysis_scope
    + 3.屏蔽一些功能+部署到外网
    + 4.单元测试
    + 5.查找bug
    + 6.单元测试 test_code
+ 2023年12月26日
    + 搭一个外网版本
+ 2023年12月27日
    + 简化ide中生成代码的流程
+ 2024年01月01日
    + 尽量不再使用静态方法,而是使用ioc和aop
+ 2024年01月29日
  + 优化了单元测试代码
  + 优化了引入po

+ 最后的版本这是效能组所有中间件+效能工具+平台的决策大脑(给用户测的)
+ 方法重命名  rename_method
+ 函数参数重命名  rename_method_param_name
+ translation 选中翻译
+ suggest_sidecar 方法review(建议)
+ 2024年02月19日
  + 方法补全:biz_completion
  + 生成代码:biz_sidecar
  + 都统一叫生成代码
  + 添加的代码注释支持多语言切换
  + prompt支持了函数能力
  + 代码补全也能自动分析代码作用范围(开启代码分析)
+ 2024年02月21日
  + 优化调试模式 (用来测试的prompt:24414:test_prompt)
+ 2024年03月02日(以后很长时间内都不在做新功能了,只优化现有的4个功能 生成代码 补全代码 智能命名 单元测试)
  + 解决了生成单元测试的一个bug(service里的代码在server创建测试方法的时候,不能列出老的方法)
  + 如果生成局部代码的时候,所处的位置是一个注释行,则在下一行开始生成代码
  + 给参数重命名
+ 2024年04月22日
  + 支持生成完整的测试类 test_class_code
+ 2024年06月02日
  + 所有prompt替换为bot(以后bot是第一元素)
  + 代码通过Inlay生成(100294)
  + 方法名重命名(rename_method->)
  + 添加注释(comment_2->)
  + 单元测试(test_code->)
  + 生成代码(biz_sidecar->)
  + 问题修复(bug_fix->)