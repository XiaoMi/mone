#对prometheus相关操作的封装

将counter、gauge、histogram等prometheus中的数据类型封装起来，便于用户使用。

提供metricsTime注解，可以自动记录函数的耗时、访问量等信息

引用包后，会开启一个简单的httpServer，监听端口默认为4444，本地可以使用localhost:4444查看暴露的指标数据

例如：

    Metrics.getInstance().newCounter("testCounter2", "age", "city").with("99", "china").add(1,"18","beijing"); //counter类型封装
    Metrics.getInstance().newGauge("testGauge", "name").with("zxw").set(128,"zxw"); //gauge类型封装
    Metrics.getInstance().newHistogram("testNotMatchLabelNameAndLabelValueExceptionHistogram", null, "b", "c").with("1").observe(1); //histogram类型封装

