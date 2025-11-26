package run.mone.mcp.dayu.servicequery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Dayu 服务查询 MCP 模块启动类
 * 提供基于 Dayu 微服务治理中心的服务查询功能
 */
@SpringBootApplication
@ComponentScan(basePackages = {"run.mone.mcp.dayu.servicequery", "run.mone.hive.mcp.service"})
public class DayuServiceQueryBootstrap {

    public static void main(String[] args) {
      
        SpringApplication.run(DayuServiceQueryBootstrap.class, args);
    }
}
