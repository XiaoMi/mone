package run.mone.moon.function;

import com.google.gson.reflect.TypeToken;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.rpc.service.GenericService;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.moon.constants.Constants;
import run.mone.moon.function.bo.Result;
import run.mone.moon.function.bo.TaskReq;
import run.mone.moon.utils.GsonUtil;
import run.mone.moon.utils.MoonUitl;

import java.util.List;
import java.util.Map;

/**
 * Moon系统创建任务执行器
 */
@Data
@Slf4j
public class MoonGetFunction implements McpFunction {

    private String name = "get_task_by_id_executor";

    private String desc = "Help user to query task by id";
    private String taskQuerySchema = """
            {
                "type": "object",
                "properties": {
                    "id": {
                        "type": "long",
                        "description": "根据任务id查询任务， query moon task by id",
                        "default": "1"
                    },
                    "isCopy": {
                        "type": "boolean",
                        "enum": [true, false],
                        "default": false,
                        "description": "is user copy moon task or is user mimic moon task, 判断用户是否需要复制或者模仿创建任务"
                    }
                },
                "required": ["id"]
            }
            """;

    ReferenceConfig<GenericService> queryReference;

    public MoonGetFunction(ApplicationConfig applicationConfig, RegistryConfig registryConfig, String group) {
        queryReference = new ReferenceConfig<>();
        // 消费者端不需要接口定义
        queryReference.setApplication(applicationConfig);
        queryReference.setRegistry(registryConfig);
        log.info("MoonGetFunction group: {}", group);
        queryReference.setGroup(group);
        queryReference.setInterface("run.mone.moon.api.service.MoonTaskDubboService");
        MoonUitl.commonParam(queryReference);
    }

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> args) {
        log.info("get moon apply function args: {}", GsonUtil.toJson(args));
        try {
            // 1. 参数验证和转换
            if (args == null || args.isEmpty()) {
                throw new IllegalArgumentException("Task parameters are required");
            }

            // 必填参数校验
            if (args.get(Constants.PARAM_ID) == null) {
                throw new IllegalArgumentException("id is required");
            }

            Number idNum = MoonUitl.getNumber(args.get(Constants.PARAM_ID));
            Long id = idNum == null ? null : idNum.longValue();

            boolean isCopy = args.get(Constants.PARAM_IS_COPY) != null && (Boolean) args.get(Constants.PARAM_IS_COPY);
            // 设置必填参数
            // 构建查询参数对象
            // 3. 调用服务创建任务
            log.info("根据id查询moon任务id: {}", id);
            GenericService genericService = queryReference.get();
            Object describeUserJsonRes = genericService.$invoke("get", new String[]{"long"},
                    new Object[]{id});
            log.info("查询moon任务列表返回结果 result: {}", describeUserJsonRes);

            Result<TaskReq> result = GsonUtil.fromJson(GsonUtil.toJson(describeUserJsonRes), new TypeToken<Result<TaskReq>>() {
            }.getType());
            // 4. 处理返回结果
            if (result.getCode() == 0) {
                TaskReq data = result.getData();
                if (isCopy) {
                    data.setName(getCopyString(data.getName()));
                    data.setDescription(getCopyString(data.getDescription()));
                }
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("Task query result: " + GsonUtil.toJson(data))),
                        false
                ));
            } else {
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("查询任务失败, code: " + result.getCode() + ", message: " + result.getMessage())), false));
            }

        } catch (Exception ex) {
            log.error("Failed to query task:", ex);
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("查询任务失败：" + ex.getMessage())), true));
        }
    }

    private static @NotNull String getCopyString(String source) {
        return source + " copy";
    }

    @Override
    public String getToolScheme() {
        return taskQuerySchema;
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