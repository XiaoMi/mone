package run.mone.mcp.hera.analysis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Hera分析应用启动类
 *
 * @author dingtao
 */
@SpringBootApplication
@ComponentScan("run.mone.mcp.hera.analysis")
public class HeraAnalysisApplication {
    
    /**
     * 应用入口方法
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(HeraAnalysisApplication.class, args);
    }
} 