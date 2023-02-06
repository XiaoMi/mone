# 提供了hera相关的注解、工具类。

## 注解

`@Trace`注解，方法级别修饰，可以使方法接入链路追踪。

`@TraceTimeEvent`注解，方法级别修饰，可以将该注解修饰的方法耗时，以event的形式加入当前span中。

## 工具类

`HeraContextUtil`类，提供了Java代码中读写HeraContext的方法。

`TraceIdUtil`类，提高了Java代码中读取当前traceID与spanID的方法