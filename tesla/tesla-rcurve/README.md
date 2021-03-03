# 概述 （Chinese)
* 主要作者:丁佩 张志勇 任清福
* rcurve 负责mesh层
* 协议:和服务层交互的协议是uds(unix domain socket)
* ingress支持
    - 支持dubbo协议
    - 支持http协议
    - 支持grpc协议
* egress
    - dubbo
    - nacos(config)
    - redis
    - mysql
* 性能棒棒的
* egress 出去的请求
* ingress 进来的请求
