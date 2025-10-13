package run.mone.mcp.codegen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Codegen Agent Bootstrap
 * 代码生成Agent启动类
 * 
 * @author goodjava@qq.com
 * @date 2025/10/7
 */
@SpringBootApplication
@ComponentScan("run.mone.mcp.codegen")
public class CodegenAgentBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(CodegenAgentBootstrap.class, args);
    }
}

