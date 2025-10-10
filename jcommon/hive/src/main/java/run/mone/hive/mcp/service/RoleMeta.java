package run.mone.hive.mcp.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import run.mone.hive.actions.Action;
import run.mone.hive.llm.LLM;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.ITool;
import run.mone.hive.schema.ActionContext;
import run.mone.hive.schema.Message;
import run.mone.hive.schema.RoleContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * @author goodjava@qq.com
 * @date 2025/4/28 11:08
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoleMeta {

    protected String profile;

    protected String goal;

    protected String constraints;

    @Builder.Default
    protected String workflow = "";

    @Builder.Default
    protected String outputFormat = "";

    //只可以内部调用
    @Builder.Default
    protected List<ITool> tools = new ArrayList<>();

    //外边用户也可以调用
    @Builder.Default
    protected List<McpFunction> mcpTools = new ArrayList<>();

    @Builder.Default
    private Function<Message, Integer> checkFinishFunc = (msg) -> 1;

    @Builder.Default
    private List<Action> actions = new ArrayList<>();

    private LLM llm;

    private RoleContext.ReactMode reactMode;

    //ReactorRole Role
    @Builder.Default
    private String roleType = "ReactorRole";

    private Function<String, Integer> thinkFunc;

    private Function<String, Integer> observeFunc;

    private Function<ActionContext, CompletableFuture<Message>> actFunc;

    //自动开启网络搜索
    @Builder.Default
    private WebQuery webQuery = new WebQuery();

    //自动开启rag
    @Builder.Default
    private Rag rag = new Rag();


    //自动打断检测
    @Builder.Default
    private InterruptQuery interruptQuery = new InterruptQuery();

    //默认10s
    @Builder.Default
    private long timeout = 10000;

    @Builder.Default
    private List<Function<ReactorRole, String>> taskList = new ArrayList<>();

}
