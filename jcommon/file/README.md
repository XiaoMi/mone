# 文件操作库


`LogFile`是以`MoneRandomAccessFile`为基础，将文件内容读取到内存，并通过`ReadListener`将文件内容进行后续的处理。

`MLog`是处理异常栈信息。收集异常栈信息至本地队列中，等待被取走。

这个库是MiLog（自研日志系统）的基础。