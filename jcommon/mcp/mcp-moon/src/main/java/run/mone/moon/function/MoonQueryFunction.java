package run.mone.moon.function;

import cn.hutool.core.bean.BeanUtil;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.rpc.service.GenericService;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.moon.constants.Constants;
import run.mone.moon.function.bo.MoonMoneTpcContext;
import run.mone.moon.function.bo.ReadTaskReq;
import run.mone.moon.function.bo.Result;
import run.mone.moon.function.bo.TaskList;
import run.mone.moon.utils.GsonUtil;
import run.mone.moon.utils.MoonUitl;

import java.util.List;
import java.util.Map;

/**
 * Moon系统创建任务执行器
 */
@Data
@Slf4j
public class MoonQueryFunction implements McpFunction {

    private String name = "query_task_executor";

    private String desc = "Help user to query task";
    private String taskQuerySchema = """
            {
                "type": "object",
                "properties": {
                    "tenant": {
                        "type": "integer",
                        "enum": [1, 2, 3, 4, 5],
                        "description": "租户: 中国区: 1, 国际： 2, 新加坡： 3， 欧洲： 4",
                        "default": "1"
                    },
                    "taskName": {
                        "type": "string",
                        "required": false,
                        "description": "任务名称",
                        "default": ""
                    },
                    "userName": {
                        "type": "string",
                        "required": false,
                        "description": "不用传",
                        "default": "wangzhidong1"
                    },
                    "taskType": {
                        "type": "string",
                        "required": false,
                        "description": "任务类型",
                        "default": ""
                    },
                    "status": {
                        "type": "integer",
                        "required": false,
                        "description": "状态code",
                        "default": 0
                    },
                    "creator": {
                        "type": "string",
                        "required": false,
                        "description": "创建者",
                        "default": ""
                    },
                    "servicePath": {
                        "type": "string",
                        "required": false,
                        "description": "服务路径。 该路径为http url或者dubbo provider 路径",
                        "default": ""
                    },
                    "page": {
                        "type": "integer",
                        "required": true,
                        "description": "当前的页码",
                        "default": 1
                    },
                    "pageSize": {
                        "type": "integer",
                        "required": true,
                        "description": "每页条数",
                        "default": 10
                    },
                    "mischeduleID": {
                        "type": "integer",
                        "required": false,
                        "description": "调度ID",
                        "default": 0
                    },
                    "projectID": {
                        "type": "integer",
                        "required": false,
                        "description": "项目id",
                        "default": 0
                    }
                },
                "required": ["tenant"]
            }
            """;

    ReferenceConfig<GenericService> queryReference;

    public MoonQueryFunction(ApplicationConfig applicationConfig, RegistryConfig registryConfig, String group) {
        queryReference = new ReferenceConfig<>();
        // 消费者端不需要接口定义
        queryReference.setApplication(applicationConfig);
        queryReference.setRegistry(registryConfig);
        queryReference.setGroup(group);
        log.info("MoonQueryFunction group: {}", group);
        queryReference.setInterface("run.mone.moon.api.service.MoonTaskDubboService");
        MoonUitl.commonParam(queryReference);
    }

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> args) {
        log.info("query moon apply function args: {}", GsonUtil.toJson(args));
        try {
            // 1. 参数验证和转换
            if (args == null || args.isEmpty()) {
                throw new IllegalArgumentException("Task parameters are required");
            }

            // 必填参数校验
            if (args.get(Constants.PARAM_TENANT) == null) {
                throw new IllegalArgumentException("tenant is required");
            }

            MoonMoneTpcContext context = new MoonMoneTpcContext();
            Number tenant = MoonUitl.getNumber(args.get(Constants.PARAM_TENANT));
            Integer tenantInt = tenant == null ? null : tenant.intValue();
            context.setTenant(MoonUitl.getString(tenantInt));
            
            // 构建查询参数对象
            ReadTaskReq queryParams = new ReadTaskReq();
            
            // 使用 Hutool 的 BeanUtil 将 Map 转换为 Bean
            BeanUtil.fillBeanWithMap(args, queryParams, true);
            
            // 处理必填的Number类型参数 (Hutool 无法处理的默认值)
            Number page = MoonUitl.getNumber(args.get(Constants.PARAM_PAGE));
            queryParams.setPage(page == null ? Constants.DEFAULT_PAGE : page.intValue());
            Number pageSize = MoonUitl.getNumber(args.get(Constants.PARAM_PAGE_SIZE));
            queryParams.setPageSize(pageSize == null ? Constants.DEFAULT_PAGE_SIZE : pageSize.intValue());

            // 3. 调用服务创建任务
            log.info("查询moon任务列表参数 context: {}, queryParams: {}", GsonUtil.toJsonTree(context), GsonUtil.toJson(queryParams));
            GenericService genericService = queryReference.get();
            Object describeUserJsonRes = genericService.$invoke("list", new String[]{"run.mone.moon.api.bo.user.MoonMoneTpcContext", "run.mone.moon.api.bo.task.ReadTaskReq"},
                    new Object[]{BeanUtil.beanToMap(context), BeanUtil.beanToMap(queryParams)});
            log.info("查询moon任务列表返回结果 result: {}", describeUserJsonRes);

            Result<TaskList> result = GsonUtil.fromJson(GsonUtil.toJson(describeUserJsonRes), new TypeToken<Result<TaskList>>() {
            }.getType());
            // 4. 处理返回结果
            if (result.getCode() == 0) {
                return Flux.just(new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent("Task query result: " + GsonUtil.toJson(result.getData()))),
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