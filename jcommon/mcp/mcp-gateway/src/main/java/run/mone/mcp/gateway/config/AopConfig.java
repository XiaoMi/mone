package run.mone.mcp.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import run.mone.hive.aspect.ReportCallCountAspect;
import run.mone.hive.http.CallCountReportClient;

/**
 * AOP配置类
 * 启用AspectJ自动代理并注册调用次数上报切面
 * 
 * @author goodjava@qq.com
 * @date 2025/12/02
 */
@Configuration
@EnableAspectJAutoProxy
public class AopConfig {
    
    @Value("${mcp.report.call.url:http://localhost:8080/api/v1/report/callcount}")
    private String reportUrl;

    @Value("${mcp.agent.mode:MCP}")
    private String agentMode;


    /**
     * 注册调用次数上报切面
     * 
     * @return ReportCallCountAspect实例
     */
    @Bean
    public ReportCallCountAspect reportCallCountAspect() {
        // 设置上报URL
        CallCountReportClient.setReportUrl(reportUrl);
        
        ReportCallCountAspect aspect = new ReportCallCountAspect();
        // 设置应用名称
        aspect.setAppName("mcp-gateway");
        // 类型, 1-agent, 2-mcp, 3-其他
        if (agentMode.equals("MCP")) {
            aspect.setType(2);
        } else {
            aspect.setType(1);
        }
        aspect.setInvokeWay(2);
        return aspect;
    }
}

