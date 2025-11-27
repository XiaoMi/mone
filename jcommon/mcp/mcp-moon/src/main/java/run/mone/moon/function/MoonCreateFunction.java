package run.mone.moon.function;

import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.rpc.service.GenericService;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.moon.constants.Constants;
import run.mone.moon.function.bo.*;
import run.mone.moon.utils.GsonUtil;
import run.mone.moon.utils.MoonUitl;

import java.util.List;
import java.util.Map;

/**
 * Moon系统创建任务执行器
 */
@Data
@Slf4j
public class MoonCreateFunction implements McpFunction {


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
                    "account": {
                        "type": "String",
                        "description": "the log in user account or the task creator",
                        "default": "mcp_user"
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
                        "default": "cancel_new",
                        "description": "并行策略：并行，新任务取消，停止老任务，队列"
                    },
                    "alertTimeout": {
                        "type": "boolean",
                        "default": true,
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
                        "default": true,
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
                        "default": true,
                        "description": "失败报警"
                    },
                    "alertFailLevel": {
                        "type": "string",
                        "default": "P1",
                        "description": "失败报警级别"
                    },
                    "alertStop": {
                        "type": "boolean",
                        "default": true,
                        "description": "停止报警"
                    },
                    "alertStopLevel": {
                        "type": "string",
                        "default": "P0",
                        "description": "停止报警级别"
                    },
                    "alertSkip": {
                        "type": "boolean",
                        "default": false,
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
                        "default": true,
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
                        "description": "http相关配置",
                        "url": {
                            "type": "string",
                            "description": "http url 路径",
                        },
                        "method": {
                            "type": "string",
                            "enum": ["get", "post", "delete", "update"],
                            "description": "http请求方式",
                        },
                        "headers": {
                            "type": "Map<String,String>",
                            "description": "http请求头",
                        }
                    },
                    "dubboParam": {
                        "type": "object",
                        "description": "dubbo相关配置",
                        "version": {
                            "type": "string",
                            "description": "dubbo分组的版本",
                        },
                        "serviceName": {
                            "type": "string",
                            "description": "dubbo provider 的类路径",
                        },
                        "methodName": {
                            "type": "string",
                            "description": "dubbo provider的方法名称",
                        },
                        "group": {
                            "type": "string",
                            "description": "dubbo provider 的分组名称",
                        },
                        "retries": {
                            "type": "integer",
                            "description": "dubbo provider 单次执行重试次数",
                            "default": 1
                        }
                    }
                },
                "required": ["tenant", "name", "projectID", "type", "execParam"]
            }
            """;

    ReferenceConfig<GenericService> createReferenceConfig;

    public MoonCreateFunction(ApplicationConfig applicationConfig, RegistryConfig registryConfig, String group) {
        createReferenceConfig = new ReferenceConfig<>();
        // 消费者端不需要接口定义
        createReferenceConfig.setInterface("run.mone.moon.api.service.MoonTaskDubboService");
        createReferenceConfig.setApplication(applicationConfig);
        createReferenceConfig.setRegistry(registryConfig);
        createReferenceConfig.setGroup(group);
        log.info("MoonQueryFunction group: {}", group);
        MoonUitl.commonParam(createReferenceConfig);
    }

    @SneakyThrows
    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> args) {
        log.info("create moon apply function args: {}", GsonUtil.toJson(args));
        try {
            // 1. 参数验证和转换
            if (args == null || args.isEmpty()) {
                throw new IllegalArgumentException("Task parameters are required");
            }

            // 必填参数校验
            if (args.get(Constants.PARAM_TENANT) == null) {
                throw new IllegalArgumentException("tenant is required");
            }
            if (args.get(Constants.PARAM_NAME) == null || ((String) args.get(Constants.PARAM_NAME)).trim().isEmpty()) {
                throw new IllegalArgumentException("task name is required and cannot be empty");
            }
            if (args.get(Constants.PARAM_PROJECT_ID) == null) {
                throw new IllegalArgumentException("task projectID is required");
            }
            if (args.get(Constants.PARAM_TYPE) == null || ((String) args.get(Constants.PARAM_TYPE)).trim().isEmpty()) {
                throw new IllegalArgumentException("type is required and cannot be empty");
            }
            String type = (String) args.get(Constants.PARAM_TYPE);
            if (!Constants.TASK_TYPE_HTTP.equals(type) && !Constants.TASK_TYPE_DUBBO.equals(type) && !Constants.TASK_TYPE_FAAS.equals(type)) {
                throw new IllegalArgumentException("type must be one of: httpTask, dubboTask, faasTask");
            }

            // 2. 构建任务参数
            TaskReq taskParam = new TaskReq();
            MoonMoneTpcContext context = new MoonMoneTpcContext();
            Integer tenant = ((Number) args.get(Constants.PARAM_TENANT)).intValue();
            context.setTenant(String.valueOf(tenant));
            context.setAccount(args.containsKey(Constants.PARAM_ACCOUNT) ?
                    (String) args.get(Constants.PARAM_ACCOUNT) : Constants.DEFAULT_ACCOUNT);

            // 设置必填参数
            taskParam.setName((String) args.get(Constants.PARAM_NAME));
            taskParam.setProjectID(((Number) args.get(Constants.PARAM_PROJECT_ID)).longValue());
            taskParam.setType(type);

            // 设置默认值和可选参数
            taskParam.setDescription((String) args.getOrDefault(Constants.PARAM_DESCRIPTION, "Task created by MCP"));
            taskParam.setExecMode((String) args.getOrDefault(Constants.PARAM_EXEC_MODE, Constants.DEFAULT_EXEC_MODE));
            taskParam.setPriority((String) args.getOrDefault(Constants.PARAM_PRIORITY, Constants.DEFAULT_PRIORITY));
            taskParam.setExecParam((String) args.getOrDefault(Constants.PARAM_EXEC_PARAM, Constants.DEFAULT_EXEC_PARAM));
            taskParam.setRetryWait(((Number) args.getOrDefault(Constants.PARAM_RETRY_WAIT, Constants.DEFAULT_RETRY_WAIT)).intValue());
            taskParam.setConcurrency(((Number) args.getOrDefault(Constants.PARAM_CONCURRENCY, Constants.DEFAULT_CONCURRENCY)).intValue());
            taskParam.setMachine((String) args.getOrDefault(Constants.PARAM_MACHINE, ""));
            taskParam.setScheduleMode((String) args.getOrDefault(Constants.PARAM_SCHEDULE_MODE, Constants.DEFAULT_SCHEDULE_MODE));
            taskParam.setScheduleParam((String) args.getOrDefault(Constants.PARAM_SCHEDULE_PARAM, Constants.DEFAULT_SCHEDULE_PARAM));
            taskParam.setStartTime(args.containsKey(Constants.PARAM_START_TIME) ? 
                    ((Number) args.get(Constants.PARAM_START_TIME)).longValue() : System.currentTimeMillis());
            taskParam.setConcurrencyStrategy((String) args.getOrDefault(Constants.PARAM_CONCURRENCY_STRATEGY, Constants.DEFAULT_CONCURRENCY_STRATEGY));

            // 报警相关默认参数，根据schema更新默认值
            taskParam.setAlertTimeout((Boolean) args.getOrDefault(Constants.PARAM_ALERT_TIMEOUT, Constants.DEFAULT_ALERT_TIMEOUT));
            taskParam.setAlertTimeoutLevel((String) args.getOrDefault(Constants.PARAM_ALERT_TIMEOUT_LEVEL, Constants.DEFAULT_ALERT_TIMEOUT_LEVEL));
            taskParam.setTimeout(((Number) args.getOrDefault(Constants.PARAM_TIMEOUT, Constants.DEFAULT_TIMEOUT)).longValue());
            taskParam.setTimeoutHalt((Boolean) args.getOrDefault(Constants.PARAM_TIMEOUT_HALT, Constants.DEFAULT_TIMEOUT_HALT));
            taskParam.setAlertSuccess((Boolean) args.getOrDefault(Constants.PARAM_ALERT_SUCCESS, Constants.DEFAULT_ALERT_SUCCESS));
            taskParam.setAlertSuccessLevel((String) args.getOrDefault(Constants.PARAM_ALERT_SUCCESS_LEVEL, Constants.DEFAULT_ALERT_SUCCESS_LEVEL));
            taskParam.setAlertFail((Boolean) args.getOrDefault(Constants.PARAM_ALERT_FAIL, Constants.DEFAULT_ALERT_FAIL));
            taskParam.setAlertFailLevel((String) args.getOrDefault(Constants.PARAM_ALERT_FAIL_LEVEL, Constants.DEFAULT_ALERT_FAIL_LEVEL));
            taskParam.setAlertStop((Boolean) args.getOrDefault(Constants.PARAM_ALERT_STOP, Constants.DEFAULT_ALERT_STOP));
            taskParam.setAlertStopLevel((String) args.getOrDefault(Constants.PARAM_ALERT_STOP_LEVEL, Constants.DEFAULT_ALERT_STOP_LEVEL));
            taskParam.setAlertSkip((Boolean) args.getOrDefault(Constants.PARAM_ALERT_SKIP, Constants.DEFAULT_ALERT_SKIP));
            taskParam.setAlertSkipLevel((String) args.getOrDefault(Constants.PARAM_ALERT_SKIP_LEVEL, Constants.DEFAULT_ALERT_SKIP_LEVEL));
            taskParam.setMaxRetry(((Number) args.getOrDefault(Constants.PARAM_MAX_RETRY, Constants.DEFAULT_MAX_RETRY)).intValue());
            taskParam.setAlertNoMachine((Boolean) args.getOrDefault(Constants.PARAM_ALERT_NO_MACHINE, Constants.DEFAULT_ALERT_NO_MACHINE));
            taskParam.setAlertNoMachineLevel((String) args.getOrDefault(Constants.PARAM_ALERT_NO_MACHINE_LEVEL, Constants.DEFAULT_ALERT_NO_MACHINE_LEVEL));
            taskParam.setAlertConfig((String) args.getOrDefault(Constants.PARAM_ALERT_CONFIG, Constants.DEFAULT_ALERT_CONFIG));
            taskParam.setHistoryKeep(((Number) args.getOrDefault(Constants.PARAM_HISTORY_KEEP, Constants.DEFAULT_HISTORY_KEEP)).intValue());

            // 特殊参数处理
            if (args.containsKey(Constants.PARAM_FAAS_PARAM)) {
                taskParam.setFaasParam(convertToFaasParam(args.get(Constants.PARAM_FAAS_PARAM)));
            }
            if (args.containsKey(Constants.PARAM_HTTP_PARAM)) {
                taskParam.setHttpParam(convertToHttpParam(args.get(Constants.PARAM_HTTP_PARAM)));
            }
            if (args.containsKey(Constants.PARAM_DUBBO_PARAM)) {
                taskParam.setDubboParam(convertToDubboParam(args.get(Constants.PARAM_DUBBO_PARAM)));
            }

            // 3. 调用服务创建任务
            log.info("创建moon任务参数 context: {}, taskParam: {}", GsonUtil.toJsonTree(context), GsonUtil.toJson(taskParam));
            GenericService genericService = createReferenceConfig.get();
            Object describeUserJsonRes = genericService.$invoke("create", new String[]{"run.mone.moon.api.bo.user.MoonMoneTpcContext", "run.mone.moon.api.bo.task.TaskReq"},
                    new Object[]{BeanUtil.beanToMap(context), BeanUtil.beanToMap(taskParam)});
            log.info("创建任务返回结果 result: {}", describeUserJsonRes);

            Result<Long> result = GsonUtil.fromJson(GsonUtil.toJson(describeUserJsonRes), new TypeToken<Result<Long>>() {
            }.getType());
            // 4. 处理返回结果
            if (result.getCode() == 0) {
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("Task created successfully. Task ID: " + result.getData())),
                        false
                ));
            } else {
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("Failed to create task code : " + result.getCode() + ", message: " + result.getMessage())),
                        false
                ));
            }

        } catch (Exception ex) {
            log.error("Failed to create task", ex);
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("Task created exception: " + ex.getMessage())),
                    false
            ));
        }
    }

    /**
     * 辅助方法：转换特殊参数
     *
     * @param param
     * @return
     */
    private FaasParam convertToFaasParam(Object param) {
        if (param instanceof Map) {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.convertValue(param, FaasParam.class);
        }
        return null;
    }

    private HttpParam convertToHttpParam(Object param) {
        if (param instanceof Map) {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.convertValue(param, HttpParam.class);
        }
        return null;
    }

    private DubboParam convertToDubboParam(Object param) {
        if (param instanceof Map) {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.convertValue(param, DubboParam.class);
        }
        return null;
    }

    @Override
    public String getToolScheme() {
        return taskToolSchema;
    }

    @Override
    public String getName() {
        return name;
    }


    @Override
    public String getDesc() {
        return desc;
    }
}