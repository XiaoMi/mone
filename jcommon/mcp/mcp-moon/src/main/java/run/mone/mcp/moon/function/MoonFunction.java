package run.mone.mcp.moon.function;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.moon.api.service.MoonTaskDubboService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Moon系统创建任务执行器
 */
@Data
@Slf4j
@Component
public class MoonFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {


    private String name = "create_task_executor";

    private String desc = "Help user to create task";

    private String taskToolSchema = """
            {
                "type": "object",
                "properties": {
                    "tenant": {
                        "type": "integer",
                        "enum": [1, 2, 3, 4, 5],
                        "description": "租户: 中国区: 1, 国际： 2, 新加坡： 3， 欧洲： 4"
                    },
                    "name": {
                        "type": "string",
                        "description": "任务名"
                    },
                    "description": {
                        "type": "string",
                        "description": "任务描述"
                    },
                    "projectID": {
                        "type": "integer",
                        "description": "关联项目id"
                    },
                    "type": {
                        "type": "string",
                        "enum": ["httpTask", "dubboTask", "faasTask"],
                        "description": "任务类型"
                    },
                    "execMode": {
                        "type": "string",
                        "enum": ["single", "broadcast", "slice"],
                        "default": "broadcast",
                        "description": "执行模式"
                    },
                    "priority": {
                        "type": "string",
                        "description": "优先级"
                    },
                    "execParam": {
                        "type": "string",
                        "description": "执行参数"
                    },
                    "retryWait": {
                        "type": "integer",
                        "default": 1,
                        "description": "失败重试时间间隔，单位s"
                    },
                    "concurrency": {
                        "type": "integer",
                        "default": 1,
                        "description": "任务执行并发量"
                    },
                    "machine": {
                        "type": "string",
                        "description": "指定机器运行"
                    },
                    "scheduleMode": {
                        "type": "string",
                        "enum": ["cron", "fix rate", "second delay", "single shot", "api call"],
                        "default": "cron",
                        "description": "调度模式"
                    },
                    "scheduleParam": {
                        "type": "string",
                        "description": "scheduleMode对应的参数json，contab表达式，执行时间，间隔等参数"
                    },
                    "startTime": {
                        "type": "integer",
                        "description": "调度开始时间"
                    },
                    "concurrencyStrategy": {
                        "type": "string",
                        "enum": ["parallel", "cancel_new", "stop_old", "queue"],
                        "description": "并行策略：并行，新任务取消，停止老任务，队列"
                    },
                    "alertTimeout": {
                        "type": "boolean",
                        "default": false,
                        "description": "是否开启超时报警"
                    },
                    "alertTimeoutLevel": {
                        "type": "string",
                        "default": "P1",
                        "description": "超时报警级别"
                    },
                    "timeout": {
                        "type": "integer",
                        "default": 7200,
                        "description": "超时报警的超时时间(s)"
                    },
                    "timeoutHalt": {
                        "type": "boolean",
                        "default": false,
                        "description": "超时终止"
                    },
                    "alertSuccess": {
                        "type": "boolean",
                        "default": false,
                        "description": "成功通知"
                    },
                    "alertSuccessLevel": {
                        "type": "string",
                        "default": "P2",
                        "description": "成功通知级别"
                    },
                    "alertFail": {
                        "type": "boolean",
                        "default": false,
                        "description": "失败报警"
                    },
                    "alertFailLevel": {
                        "type": "string",
                        "default": "P1",
                        "description": "失败报警级别"
                    },
                    "alertStop": {
                        "type": "boolean",
                        "default": false,
                        "description": "停止报警"
                    },
                    "alertStopLevel": {
                        "type": "string",
                        "default": "P0",
                        "description": "停止报警级别"
                    },
                    "alertSkip": {
                        "type": "boolean",
                        "default": true,
                        "description": "任务跳过报警"
                    },
                    "alertSkipLevel": {
                        "type": "string",
                        "default": "P0",
                        "description": "任务跳过报警级别"
                    },
                    "maxRetry": {
                        "type": "integer",
                        "default": 0,
                        "description": "最大失败重试次数"
                    },
                    "alertNoMachine": {
                        "type": "boolean",
                        "default": false,
                        "description": "没机器时报警"
                    },
                    "alertNoMachineLevel": {
                        "type": "string",
                        "default": "P0",
                        "description": "没机器时报警级别"
                    },
                    "alertConfig": {
                        "type": "string",
                        "description": "通知设置"
                    },
                    "historyKeep": {
                        "type": "integer",
                        "default": 7,
                        "description": "执行历史保存天数"
                    },
                    "faasParam": {
                        "type": "object",
                        "description": "faas相关配置"
                    },
                    "httpParam": {
                        "type": "object",
                        "description": "http相关配置"
                    },
                    "dubboParam": {
                        "type": "object",
                        "description": "dubbo相关配置"
                    }
                },
                "required": ["tenant", "name", "description", "projectID", "type", "historyKeep"]
            }
            """;

    private MoonTaskDubboService moonTaskDubboService;

    public MoonFunction(MoonTaskDubboService moonTaskDubboService) {
        this.moonTaskDubboService = moonTaskDubboService;
    }

    @SneakyThrows
    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> args) {
        String tableName = (String) args.get("tableName");
        String startTime = (String) args.get("startTime");
        Integer count = (Integer) args.get("count");
        String endTime = (String) args.get("endTime");
        if (tableName == null || tableName.trim().isEmpty()) {
            log.error("没有指明表明");
            throw new IllegalArgumentException("tableName is required");
        }
        log.info("tableName: {}, startTime: {}, endTime: {}", tableName, startTime, endTime);

        // 获取连接

        try {

            return new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("moon created: ")), false);
        } catch (Throwable ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

}