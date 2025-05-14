package run.mone.supergateway;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
@SpringBootApplication
public class SuperGateway {

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("v", "version", false, "显示版本信息");
        options.addOption("s", "stdio", true, "要执行的命令");
        options.addOption("e", "sse", true, "SSE服务器URL");
        options.addOption("p", "port", true, "服务器端口");
        options.addOption("b", "baseUrl", true, "基础URL");
        options.addOption("sp", "ssePath", true, "SSE路径");
        options.addOption("mp", "messagePath", true, "消息路径");
        options.addOption("l", "logLevel", true, "日志级别");
        options.addOption("c", "cors", false, "启用CORS");
        options.addOption("h", "healthEndpoint", true, "健康检查端点");

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);

            // 检查版本选项
            if (cmd.hasOption("version")) {
                System.out.println("supergateway version 1.0.0");
                return;
            }

            // 检查互斥选项
            if (cmd.hasOption("sse") && cmd.hasOption("stdio")) {
                log.error("--sse和--stdio选项不能同时使用");
                System.exit(1);
            }

            // 设置系统属性
            if (cmd.hasOption("stdio")) {
                System.setProperty("supergateway.stdio.command", cmd.getOptionValue("stdio"));
            }
            if (cmd.hasOption("sse")) {
                System.setProperty("supergateway.sse.url", cmd.getOptionValue("sse"));
            }
            if (cmd.hasOption("port")) {
                System.setProperty("server.port", cmd.getOptionValue("port"));
            }
            if (cmd.hasOption("baseUrl")) {
                System.setProperty("supergateway.baseUrl", cmd.getOptionValue("baseUrl"));
            }
            if (cmd.hasOption("ssePath")) {
                System.setProperty("supergateway.sse.path", cmd.getOptionValue("ssePath"));
            }
            if (cmd.hasOption("messagePath")) {
                System.setProperty("supergateway.message.path", cmd.getOptionValue("messagePath"));
            }
            if (cmd.hasOption("logLevel")) {
                System.setProperty("logging.level.root", cmd.getOptionValue("logLevel"));
            }
            if (cmd.hasOption("cors")) {
                System.setProperty("supergateway.cors.enabled", "true");
            }
            if (cmd.hasOption("healthEndpoint")) {
                System.setProperty("supergateway.health.endpoint", cmd.getOptionValue("healthEndpoint"));
            }

            // 启动Spring Boot应用
            ConfigurableApplicationContext context = SpringApplication.run(SuperGateway.class, args);
            
            // 添加关闭钩子
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                context.close();
            }));

        } catch (ParseException e) {
            log.error("解析命令行参数时出错", e);
            System.exit(1);
        }
    }
}