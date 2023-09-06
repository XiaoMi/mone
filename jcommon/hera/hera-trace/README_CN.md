# Provides Hera related annotations and utility classes.

## Annotation

`@Trace`注解，方法级别修饰，可以使方法接入链路追踪。

`@Trace` method - level modifications, enable method access link tracing.

`@TraceTimeEvent`注解，方法级别修饰，可以将该注解修饰的方法耗时，以event的形式加入当前span中。

`@TraceTimeEvent` method level modification, which can be added to the current span as an event.

## Tool

`HeraContextUtil`类，提供了Java代码中读写HeraContext的方法。

`HeraContextUtil` which provides methods for reading and writing HeraContext in Java code.

`TraceIdUtil`类，提高了Java代码中读取当前traceID与spanID的方法

`TraceIdUtil` to improve the method of reading the current traceID and spanID in Java code