package run.mone.mcp.milinenew.function;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.llm.LLM;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.List;
import java.util.Map;

/**
 * JVM参数生成工具
 * 根据JDK版本和内存大小，调用大模型生成生产级JVM参数
 *
 * @author goodjava@qq.com
 * @date 2025/1/17
 */
@Slf4j
@Component
public class JvmGenerationFunction implements McpFunction {

    @Autowired
    private LLM llm;

    private static final String SYSTEM_PROMPT = """
            部署平台 JVM 参数推荐 AI - System Prompt（精简版）
            
            你是 JVM 参数配置专家。根据用户提供的 JDK 版本和内存配置，直接输出单行生产级 JVM 参数，无需任何解释说明。
            
            ## 输入格式
            
            用户会提供：JDK 版本 + 内存大小
            示例：JDK 17, 4G 或 JDK 8 8G 内存
            
            ## 输出要求
            
            仅输出单行 JVM 参数，参数之间用空格分隔，不要有任何额外文字、说明或格式。
            
            ## 参数生成规则
            
            ### 内存配置
            
            - 堆内存：容器内存的 70%，-Xms 和 -Xmx 设置相同值
            - 元空间：256m-512m（小应用）/ 512m-1024m（大应用）
            - 直接内存：-XX:MaxDirectMemorySize=512m（检测到框架需要时添加）
            - 栈大小：-Xss1m
            
            ### GC 策略
            
            **JDK 8:**
            
            - 内存 < 4G: -XX:+UseParallelGC
            - 内存 >= 4G: -XX:+UseG1GC -XX:MaxGCPauseMillis=200
            
            **JDK 11-21:**
            
            - 内存 < 8G: -XX:+UseG1GC -XX:MaxGCPauseMillis=200
            - 内存 >= 8G: -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:G1HeapRegionSize=16m
            
            ### GC 日志
            
            **JDK 8:**
            -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:/opt/logs/gc.log -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=100M
            
            **JDK 9+:**
            -Xlog:gc*:file=/opt/logs/gc.log:time,uptime,level,tags:filecount=10,filesize=100M
            
            ### 模块系统（JDK 9+ 必需）
            
            --add-opens java.base/jdk.internal.misc=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.nio=ALL-UNNAMED --add-opens java.base/sun.nio.ch=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.util.concurrent=ALL-UNNAMED -Dio.netty.tryReflectionSetAccessible=true
            
            ### 标准参数
            
            -XX:+UseCompressedOops -XX:+UseCompressedClassPointers -Djava.security.egd=file:/dev/./urandom -Dfile.encoding=UTF-8 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/opt/logs/heapdump.hprof -XX:ErrorFile=/opt/logs/hs_err_pid.log
            
            ### JDK 8 额外参数
            
            -XX:+UseStringDeduplication（仅 G1GC）
            
            ## 输出示例
            
            用户输入：JDK 17, 4G
            你的输出：
            -Xms2800m -Xmx2800m -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=512m -XX:MaxDirectMemorySize=512m -Xss1m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -Xlog:gc*:file=/opt/logs/gc.log:time,uptime,level,tags:filecount=10,filesize=100M --add-opens java.base/jdk.internal.misc=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.nio=ALL-UNNAMED --add-opens java.base/sun.nio.ch=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.util.concurrent=ALL-UNNAMED -Dio.netty.tryReflectionSetAccessible=true -XX:+UseCompressedOops -XX:+UseCompressedClassPointers -Djava.security.egd=file:/dev/./urandom -Dfile.encoding=UTF-8 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/opt/logs/heapdump.hprof -XX:ErrorFile=/opt/logs/hs_err_pid.log
            
            用户输入：JDK 8, 8G
            你的输出：
            -Xms5600m -Xmx5600m -Xmn2240m -XX:MetaspaceSize=512m -XX:MaxMetaspaceSize=1024m -XX:MaxDirectMemorySize=512m -Xss1m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UseStringDeduplication -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:/opt/logs/gc.log -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=100M -XX:+UseCompressedOops -XX:+UseCompressedClassPointers -Djava.security.egd=file:/dev/./urandom -Dfile.encoding=UTF-8 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/opt/logs/heapdump.hprof -XX:ErrorFile=/opt/logs/hs_err_pid.log
            
            严格遵守：只输出参数，不输出任何其他内容。
            """;

    public static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "jdkVersion": {
                        "type": "string",
                        "description": "JDK版本，例如：8、11、17、21（必填）"
                    },
                    "memorySize": {
                        "type": "string",
                        "description": "内存大小，例如：2048 1024（必填）"
                    }
                },
                "required": ["jdkVersion", "memorySize"]
            }
            """;

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        log.info("JvmGeneration arguments: {}", arguments);

        try {
            // 验证必填参数
            Object jdkVersionObj = arguments.get("jdkVersion");
            Object memorySizeObj = arguments.get("memorySize");

            if (jdkVersionObj == null || StringUtils.isBlank(jdkVersionObj.toString())) {
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("错误：缺少必填参数'jdkVersion'")),
                        true
                ));
            }

            if (memorySizeObj == null || StringUtils.isBlank(memorySizeObj.toString())) {
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("错误：缺少必填参数'memorySize'")),
                        true
                ));
            }

            String jdkVersion = jdkVersionObj.toString().trim();
            String memorySize = memorySizeObj.toString().trim();

            // 构建用户输入
            String userInput = String.format("JDK %s, %s", jdkVersion, memorySize);
            log.info("JvmGeneration userInput: {}", userInput);

            // 调用大模型生成JVM参数
            String jvmParams = llm.chat(SYSTEM_PROMPT + "\n\n用户输入：" + userInput);
            log.info("JvmGeneration result: {}", jvmParams);

            if (StringUtils.isBlank(jvmParams)) {
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("错误：大模型返回结果为空")),
                        true
                ));
            }

            // 返回生成的JVM参数
            String resultText = jvmParams.trim();

            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(resultText)),
                    false
            ));
        } catch (Exception e) {
            log.error("执行jvm_generation操作时发生异常", e);
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("错误：执行操作失败: " + e.getMessage())),
                    true
            ));
        }
    }

    @Override
    public String getName() {
        return "jvm_generation";
    }

    @Override
    public String getDesc() {
        return """
                JVM参数生成工具，根据JDK版本和内存大小生成生产级JVM参数。
                
                **使用场景：**
                - 需要为Java应用配置JVM参数
                - 根据不同JDK版本选择合适的GC策略
                - 根据内存大小优化堆配置
                - 生成包含GC日志、OOM dump等生产级配置
                """;
    }

    @Override
    public String getToolScheme() {
        return TOOL_SCHEMA;
    }
}
