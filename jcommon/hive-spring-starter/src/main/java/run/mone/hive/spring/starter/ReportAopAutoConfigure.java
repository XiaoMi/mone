package run.mone.hive.spring.starter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import run.mone.hive.aspect.ReportCallCountAspect;
import run.mone.hive.http.CallCountReportClient;

/**
 * 调用次数上报 AOP 自动配置类
 * 
 * <p>通过配置项 hive.report.enabled 控制是否启用调用次数上报功能（默认：false）</p>
 * <p>这是一个通用能力，所有 MCP 服务可以自行选择是否启用</p>
 * 
 * <p>配置示例：</p>
 * <pre>
 * # 启用调用次数上报
 * hive.report.enabled=true
 * 
 * # 上报服务地址
 * hive.report.url=http://localhost:8080/api/v1/report/callcount
 * 
 * # 应用名称（必填）
 * hive.report.app-name=mcp-gateway
 * 
 * # Agent 模式：MCP 或 AGENT（默认：MCP）
 * hive.report.agent-mode=MCP
 * 
 * # 调用方式：1-同步, 2-异步（默认：2）
 * hive.report.invoke-way=2
 * </pre>
 * 
 * @author goodjava@qq.com
 * @date 2025/12/02
 */
@Slf4j
@Configuration
@EnableAspectJAutoProxy
@ConditionalOnProperty(name = "hive.report.enabled", havingValue = "true", matchIfMissing = false)
public class ReportAopAutoConfigure {
    
    @Value("${hive.report.url:http://localhost:8080/api/v1/report/callcount}")
    private String reportUrl;

    @Value("${hive.report.app-name:unknown}")
    private String appName;

    @Value("${hive.report.agent-mode:MCP}")
    private String agentMode;

    @Value("${hive.report.invoke-way:2}")
    private Integer invokeWay;

    /**
     * 注册调用次数上报切面
     * 
     * @return ReportCallCountAspect 实例
     */
    @Bean
    public ReportCallCountAspect reportCallCountAspect() {
        log.info("Initializing ReportCallCountAspect with reportUrl: {}, appName: {}, agentMode: {}", 
                reportUrl, appName, agentMode);
        
        // 设置上报 URL
        CallCountReportClient.setReportUrl(reportUrl);
        
        ReportCallCountAspect aspect = new ReportCallCountAspect();
        
        // 设置应用名称
        aspect.setAppName(appName);
        
        // 设置类型：1-agent, 2-mcp, 3-其他
        if ("MCP".equalsIgnoreCase(agentMode)) {
            aspect.setType(2);
        } else if ("AGENT".equalsIgnoreCase(agentMode)) {
            aspect.setType(1);
        } else {
            aspect.setType(3);
        }
        
        // 设置调用方式
        aspect.setInvokeWay(invokeWay);
        
        log.info("ReportCallCountAspect initialized successfully");
        return aspect;
    }
}

