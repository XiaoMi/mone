package run.mone.hive.spring.starter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import run.mone.hive.mcp.service.RoleService;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

/**
 * Agent 信息处理器
 * 提供 Agent 的基本信息、状态、配置等
 * 
 * @author goodjava@qq.com
 */
@Slf4j
@RestController
@RequestMapping("/mcp/agent")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class AgentInfoHandler {

    private final RoleService roleService;

    @Value("${mcp.grpc.port:9999}")
    private int grpcPort;

    @Value("${mcp.llm:CLAUDE_COMPANY}")
    private String llmType;

    @Value("${mcp.transport.type:grpc}")
    private String transportType;

    @Value("${mcp.sse.enabled:false}")
    private boolean sseEnabled;

    @Value("${mcp.websocket.enabled:false}")
    private boolean websocketEnabled;

    @Value("${spring.application.name:mcp-agent}")
    private String applicationName;

    @Value("${server.port:8080}")
    private int serverPort;

    /**
     * 获取 Agent 基本信息
     * 
     * @return Agent 信息
     */
    @GetMapping("/info")
    public Map<String, Object> getAgentInfo() {
        log.debug("Getting agent info");
        
        Map<String, Object> info = new HashMap<>();
        
        // 基本信息
        info.put("name", applicationName);
        info.put("version", "1.6.0-jdk21-SNAPSHOT");
        info.put("status", "running");
        info.put("timestamp", System.currentTimeMillis());
        info.put("datetime", LocalDateTime.now().toString());
        
        // 配置信息
        Map<String, Object> config = new HashMap<>();
        config.put("llmType", llmType);
        config.put("transportType", transportType);
        config.put("grpcPort", grpcPort);
        config.put("serverPort", serverPort);
        config.put("sseEnabled", sseEnabled);
        config.put("websocketEnabled", websocketEnabled);
        info.put("config", config);
        
        // 运行时信息
        info.put("runtime", getRuntimeInfo());
        
        // 内存信息
        info.put("memory", getMemoryInfo());
        
        return info;
    }

    /**
     * 获取 Agent 健康状态
     * 
     * @return 健康状态
     */
    @GetMapping("/health")
    public Map<String, Object> getHealth() {
        log.debug("Health check");
        
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        
        // 检查各个组件状态
        Map<String, String> components = new HashMap<>();
        components.put("roleService", roleService != null ? "UP" : "DOWN");
        components.put("llm", llmType != null ? "UP" : "DOWN");
        health.put("components", components);
        
        return health;
    }

    /**
     * 获取 Agent 配置信息
     * 
     * @return 配置信息
     */
    @GetMapping("/config")
    public Map<String, Object> getConfig() {
        log.debug("Getting agent config");
        
        Map<String, Object> config = new HashMap<>();
        config.put("llmType", llmType);
        config.put("transportType", transportType);
        config.put("grpcPort", grpcPort);
        config.put("serverPort", serverPort);
        config.put("sseEnabled", sseEnabled);
        config.put("websocketEnabled", websocketEnabled);
        
        return config;
    }

    /**
     * 获取 Agent 统计信息
     * 
     * @return 统计信息
     */
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        log.debug("Getting agent stats");
        
        Map<String, Object> stats = new HashMap<>();
        
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        
        // 运行时间
        long uptime = runtimeMXBean.getUptime();
        stats.put("uptimeMillis", uptime);
        stats.put("uptimeSeconds", uptime / 1000);
        stats.put("uptimeMinutes", uptime / 60000);
        stats.put("uptimeHours", uptime / 3600000);
        
        // 启动时间
        long startTime = runtimeMXBean.getStartTime();
        stats.put("startTime", startTime);
        stats.put("startDateTime", LocalDateTime.ofInstant(
                Instant.ofEpochMilli(startTime), 
                ZoneId.systemDefault()
        ).toString());
        
        // 线程信息
        stats.put("threadCount", Thread.activeCount());
        
        // 内存信息
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        long usedMemory = memoryMXBean.getHeapMemoryUsage().getUsed();
        long maxMemory = memoryMXBean.getHeapMemoryUsage().getMax();
        stats.put("memoryUsedMB", usedMemory / 1024 / 1024);
        stats.put("memoryMaxMB", maxMemory / 1024 / 1024);
        stats.put("memoryUsagePercent", (double) usedMemory / maxMemory * 100);
        
        return stats;
    }

    /**
     * 获取 Agent 能力信息
     * 
     * @return 能力信息
     */
    @GetMapping("/capabilities")
    public Map<String, Object> getCapabilities() {
        log.debug("Getting agent capabilities");
        
        Map<String, Object> capabilities = new HashMap<>();
        
        // 支持的功能
        capabilities.put("llm", true);
        capabilities.put("tools", true);
        capabilities.put("logging", true);
        capabilities.put("grpc", "grpc".equalsIgnoreCase(transportType));
        capabilities.put("sse", sseEnabled);
        capabilities.put("websocket", websocketEnabled);
        
        // LLM 相关
        capabilities.put("llmProvider", llmType);
        capabilities.put("streaming", true);
        
        // 传输协议
        capabilities.put("transport", transportType);
        
        return capabilities;
    }

    /**
     * 获取运行时信息
     */
    private Map<String, Object> getRuntimeInfo() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        
        Map<String, Object> runtime = new HashMap<>();
        runtime.put("jvmName", runtimeMXBean.getVmName());
        runtime.put("jvmVersion", runtimeMXBean.getVmVersion());
        runtime.put("jvmVendor", runtimeMXBean.getVmVendor());
        runtime.put("javaVersion", System.getProperty("java.version"));
        runtime.put("javaVendor", System.getProperty("java.vendor"));
        runtime.put("osName", System.getProperty("os.name"));
        runtime.put("osVersion", System.getProperty("os.version"));
        runtime.put("osArch", System.getProperty("os.arch"));
        runtime.put("processors", Runtime.getRuntime().availableProcessors());
        runtime.put("uptime", runtimeMXBean.getUptime());
        runtime.put("startTime", runtimeMXBean.getStartTime());
        
        return runtime;
    }

    /**
     * 获取内存信息
     */
    private Map<String, Object> getMemoryInfo() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        Runtime runtime = Runtime.getRuntime();
        
        Map<String, Object> memory = new HashMap<>();
        
        // 堆内存
        long heapUsed = memoryMXBean.getHeapMemoryUsage().getUsed();
        long heapMax = memoryMXBean.getHeapMemoryUsage().getMax();
        long heapCommitted = memoryMXBean.getHeapMemoryUsage().getCommitted();
        
        Map<String, Object> heap = new HashMap<>();
        heap.put("used", heapUsed);
        heap.put("max", heapMax);
        heap.put("committed", heapCommitted);
        heap.put("usedMB", heapUsed / 1024 / 1024);
        heap.put("maxMB", heapMax / 1024 / 1024);
        heap.put("committedMB", heapCommitted / 1024 / 1024);
        heap.put("usagePercent", String.format("%.2f", (double) heapUsed / heapMax * 100));
        memory.put("heap", heap);
        
        // 非堆内存
        long nonHeapUsed = memoryMXBean.getNonHeapMemoryUsage().getUsed();
        long nonHeapMax = memoryMXBean.getNonHeapMemoryUsage().getMax();
        long nonHeapCommitted = memoryMXBean.getNonHeapMemoryUsage().getCommitted();
        
        Map<String, Object> nonHeap = new HashMap<>();
        nonHeap.put("used", nonHeapUsed);
        nonHeap.put("max", nonHeapMax);
        nonHeap.put("committed", nonHeapCommitted);
        nonHeap.put("usedMB", nonHeapUsed / 1024 / 1024);
        nonHeap.put("maxMB", nonHeapMax > 0 ? nonHeapMax / 1024 / 1024 : -1);
        nonHeap.put("committedMB", nonHeapCommitted / 1024 / 1024);
        memory.put("nonHeap", nonHeap);
        
        // 总内存
        Map<String, Object> total = new HashMap<>();
        total.put("totalMemory", runtime.totalMemory());
        total.put("freeMemory", runtime.freeMemory());
        total.put("maxMemory", runtime.maxMemory());
        total.put("usedMemory", runtime.totalMemory() - runtime.freeMemory());
        total.put("totalMemoryMB", runtime.totalMemory() / 1024 / 1024);
        total.put("freeMemoryMB", runtime.freeMemory() / 1024 / 1024);
        total.put("maxMemoryMB", runtime.maxMemory() / 1024 / 1024);
        total.put("usedMemoryMB", (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024);
        memory.put("total", total);
        
        return memory;
    }

    /**
     * 获取系统信息
     * 
     * @return 系统信息
     */
    @GetMapping("/system")
    public Map<String, Object> getSystemInfo() {
        log.debug("Getting system info");
        
        Map<String, Object> system = new HashMap<>();
        
        // Java 信息
        system.put("javaVersion", System.getProperty("java.version"));
        system.put("javaVendor", System.getProperty("java.vendor"));
        system.put("javaHome", System.getProperty("java.home"));
        
        // OS 信息
        system.put("osName", System.getProperty("os.name"));
        system.put("osVersion", System.getProperty("os.version"));
        system.put("osArch", System.getProperty("os.arch"));
        
        // 系统资源
        Runtime runtime = Runtime.getRuntime();
        system.put("processors", runtime.availableProcessors());
        system.put("maxMemoryMB", runtime.maxMemory() / 1024 / 1024);
        
        // 用户信息
        system.put("userName", System.getProperty("user.name"));
        system.put("userHome", System.getProperty("user.home"));
        system.put("userDir", System.getProperty("user.dir"));
        
        // 时区信息
        system.put("timezone", ZoneId.systemDefault().toString());
        system.put("currentTime", LocalDateTime.now().toString());
        
        return system;
    }
}

