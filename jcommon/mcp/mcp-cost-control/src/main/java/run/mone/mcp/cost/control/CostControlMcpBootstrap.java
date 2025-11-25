package run.mone.mcp.cost.control;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author zhangxiaowei6
 * @Date 2025/11/18 17:18
 */

@SpringBootApplication
@ComponentScan("run.mone.mcp.cost.control")
public class CostControlMcpBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(CostControlMcpBootstrap.class, args);
    }
}
