package run.mone.moon.function;

import com.google.common.reflect.TypeToken;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.rpc.service.GenericService;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.moon.api.bo.task.TaskReq;
import run.mone.moon.utils.GsonUtil;
import run.mone.moon.utils.MoonUitl;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Moon系统创建任务执行器
 */
@Data
@Slf4j
public class MoonGetFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {


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
                    }
                },
                "required": ["id"]
            }
            """;

    ReferenceConfig<GenericService> queryReference = null;

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

    @SneakyThrows
    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> args) {
        log.info("get moon apply function args: {}", GsonUtil.toJson(args));
        try {
            // 1. 参数验证和转换
            if (args == null || args.isEmpty()) {
                throw new IllegalArgumentException("Task parameters are required");
            }

            // 必填参数校验
            if (args.get("id") == null) {
                throw new IllegalArgumentException("id is required");
            }

            Number idNum = MoonUitl.getNumber(args.get("id"));
            Long id = idNum == null ? null : idNum.longValue();
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
                return new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("Task query result: " + GsonUtil.toJson(result.getData()))),
                        false
                );
            } else {
                throw new RuntimeException("Failed to create task: " + result.getMessage());
            }

        } catch (Exception ex) {
            log.error("Failed to query task", ex);
            throw new RuntimeException("Failed to query task: " + ex.getMessage());
        }
    }

}