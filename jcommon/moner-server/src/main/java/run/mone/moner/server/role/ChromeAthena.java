package run.mone.moner.server.role;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import run.mone.hive.Environment;
import run.mone.hive.actions.Action;
import run.mone.hive.common.AiTemplate;
import run.mone.hive.llm.LLMProvider;
import run.mone.hive.roles.Role;
import run.mone.hive.schema.ActionContext;
import run.mone.hive.schema.ActionReq;
import run.mone.hive.schema.Message;
import run.mone.moner.server.bo.ChatWebSocketResp;
import run.mone.moner.server.common.Const;
import run.mone.moner.server.common.GsonUtils;
import run.mone.moner.server.common.JsonUtils;
import run.mone.moner.server.common.MultiXmlParser;
import run.mone.moner.server.common.Result;
import run.mone.moner.server.constant.ResultType;
import run.mone.moner.server.context.ApplicationContextProvider;
import run.mone.moner.server.role.actions.*;
import run.mone.moner.server.service.LLMService;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2025/2/7 14:58
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class ChromeAthena extends Role {

    private Consumer<String> consumer;

    private WebSocketSession session;

    private List<Role> roleList = Lists.newArrayList(new Shopper(), new Searcher(), new Mailer(), new Summarizer(), new BilibiliPublisher());

    private List<Action> actionList = Lists.newArrayList(new OpenTabAction(""), new OperationAction(), new ScrollAction(),
            new FullPageAction(), new GetContentAction(), new ChatAction(), new ProcessAction(), new ClickAfterRefresh(), new CodeAction());

    private static final Type LIST_STRING = new TypeToken<List<String>>() {
    }.getType();

    private String userPrompt = """
            ===========
            历史聊天记录:
            ${history}
            ===========
            当前打开的tab:
            ${tabs}
            ===========
            <% 
            if (code != "") {
            %>
            当前页面的code:(辅助图片来参考选用那些序号,如果页面code是空,则说明没有打开相应页面)
            ${code}
            ===========
            <%
            }
            %>
            ===========
            请帮我判断使用那个TOOL\n
            """;


    public ChromeAthena(WebSocketSession session) {
        super("Shopper", "购物者");
        setEnvironment(new Environment());

        super.prompt = """
                你是我的私人助手。你的任务是根据用户的需求选择合适的TOOL，并执行相应的操作。
                
                TOOL USE
                
                You have access to a set of tools that are executed upon the user's approval. You can use one tool per message, and will receive the result of that tool use in the user's response. You use tools step-by-step to accomplish a given task, with each tool use informed by the result of the previous tool use.
                
                # Tool Use Formatting
                
                Tool use is formatted using XML-style tags. The tool name is enclosed in opening and closing tags, and each parameter is similarly enclosed within its own set of tags. Here's the structure:
                
                <tool_name>
                <parameter1_name>value1</parameter1_name>
                <parameter2_name>value2</parameter2_name>
                ...
                </tool_name>
                
                #支持的TOOL:
                
                %s
                
                #注意事项
                每次操作只能返回一个工具，只需要返回工具内容即可，不用描述你用到了哪个工具.
                返回的的TOOL不要用markdown格式包裹.
                
                
                #角色的定义
                角色是一些工具的集合和使用顺序,如果你发现某个角色很适合完成某个工作,你则直接按他编排的Tool来执行.
                %s
                
                =========
                
                """;

        this.prompt = this.prompt.formatted(
                this.actionList.stream().map(Action::getDescription).collect(Collectors.joining("\n\n")),
                this.roleList.stream().map(it -> "角色名称:" + it.getName() + "\n工具使用流程:\n" + it.getGoal()).collect(Collectors.joining("\n")));
        this.session = session;
    }


    @SneakyThrows
    @Override
    public CompletableFuture<Message> run() {
        ActionContext context = new ActionContext();
        boolean ask_followup_question = false;
        int i = 0;
        while (i++ < 20) {
            ActionReq req = new ActionReq();
            req.setRole(Role.builder().name("user").build());
            Message msg = this.rc.getNews().poll(2, TimeUnit.MINUTES);
            if (msg != null) {
                List<String> images = null;
                String code = "";
                String tabs = "";
                String text = "";

                if (msg.getType().equals("json")) {
                    JsonObject obj = JsonParser.parseString(msg.getContent()).getAsJsonObject();
                    text = JsonUtils.getValueOrDefault(obj, "text", "");
                    JsonArray imgs = obj.getAsJsonArray("img");
                    if (imgs != null) {
                        images = getImageStrings(imgs);
                        msg.setImages(images);
                    }
                    code = JsonUtils.getValueOrDefault(obj, "code", "");
                    tabs = JsonUtils.getValueOrDefault(obj, "tabs", "");

                    if (StringUtils.isNotEmpty(code)) {
                        text = text + "\ncode:\n" + code;
                    }

                    if (!CollectionUtils.isEmpty(images)) {
                        text = text + "\nimages:\n [图片占位符]";
                    }

                    msg.setContent(text);
                    msg.setRole("assistant");
                }

                this.getRc().getMemory().add(msg);

                //历史聊天记录
                String history = this.getRc().getMemory().getStorage().stream().map(it -> it.getRole() + ":" + it.getContent()).collect(Collectors.joining("\n"));

                LLMService llmService = ApplicationContextProvider.getBean(LLMService.class);

                String userPrompt = AiTemplate.renderTemplate(this.userPrompt, ImmutableMap.of("history", history, "code", code, "tabs", tabs));

                String res = llmService.callStream(this, this.llm, userPrompt, images, this.prompt);
                log.info("res:{}", res);
                List<Result> list = new MultiXmlParser().parse(res);
                Result result = list.get(0);

                this.getRc().getMemory().add(Message.builder().role("assistant").content(res).build());

                //流程结束了
                if (result.getTag().equals("attempt_completion") || result.getTag().equals("ask_followup_question")) {
                    if (result.getTag().equals("ask_followup_question")) {
                        ask_followup_question = true;
                    }
                    consumer.accept(Const.actionTemplate.formatted("end", result.getTag()));
                    break;
                }

                String tooleName = result.getKeyValuePairs().getOrDefault("tool_name", "");
                if (StringUtils.isNotEmpty(tooleName)) {
                    Optional<Action> optional = this.getActions().stream().filter(it -> it.getName().equals(tooleName)).findFirst();
                    if (optional.isPresent()) {
                        log.info("toolName:{}", tooleName);
                        req.setMessage(Message.builder().data(result).build());
                        String content = optional.get().run(req, context).join().getContent();
                        consumer.accept(content);
                    }
                }
            }
        }
        try {
            return CompletableFuture.completedFuture(Message.builder().build());
        } finally {
            //如果只是向你询问问题,历史记录不要清除
            if (!ask_followup_question) {
                this.getRc().getNews().clear();
                this.getRc().getMemory().clear();
            }
        }
    }

    @Nullable
    private List<String> getImageStrings(JsonArray imgs) {
        List<String> images;
        images = GsonUtils.gson.fromJson(imgs, LIST_STRING);
        if (llm.getLlmProvider() == LLMProvider.GOOGLE_2) {
            //google gemini 不需要前边的内容
            images = images.stream().map(img -> {
                if (img.startsWith("data:image")) {
                    return img.split("base64,")[1];
                }
                return img;
            }).collect(Collectors.toList());
        }
        return images;
    }


    @Override
    public void sendMessage(Message message) {
        sendMessage(message.getContent(), message.getType());
    }

    private void sendMessage(String content, String messageType) {
        try {
            ChatWebSocketResp resp = ChatWebSocketResp.builder().roleName(name).roleType("ASSISTANT").content(content).messageType(messageType).type(ResultType.CHAT).build();
            session.sendMessage(new TextMessage(GsonUtils.gson.toJson(resp)));
        } catch (Exception e) {
            log.error("send message error, ", e);
        }
    }
}
