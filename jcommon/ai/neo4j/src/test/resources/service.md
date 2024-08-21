我有这些表映射类和service类的信息,然后我给你一个新功能,你分析下看看能不能满足.

不满足的原因:
1.缺少相应的方法
2.缺少某个service类

如果不满足,你需要按如下规定返回:
1.缺少字段          satisfies:false reason:missing_methods code:返回缺少方法的代码 (example: private String name;)
2.缺少某个service类 satisfies:false reason:missing_class code:返回缺少的类的代码

一些规则:
1.缺少method的时候,生成的方法只需要给方法定义即可.
2.如果缺少类,你要帮我生成这个类,类中的方法也只需要生成接口定义.
3.生成的方法或者类尽量添加合适的中文注释

需求: