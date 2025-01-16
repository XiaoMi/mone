package run.mone.m78.service.agent.state;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import run.mone.m78.service.common.DateUtils;
import run.mone.m78.service.context.ApplicationContextProvider;
import run.mone.m78.service.dao.entity.LongTermChatSummaryPo;
import run.mone.m78.service.dao.entity.ShortTermChatSummaryPo;
import run.mone.m78.service.dto.ReqChatSummaryListDto;
import run.mone.m78.service.service.bot.ChatSummaryService;
import run.mone.m78.service.service.feature.router.FeatureRouterService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2023/12/2 20:58
 */
@Slf4j
public class AthenaState {

    @Setter
    protected AthenaFsm fsm;


    public void enter(StateContext context) {

    }

    public void execute(StateReq req, StateContext context) {

    }


    public void exit(StateContext context) {

    }

    /**
     * 该方法用于调用机器人并获取响应结果
     * <p>
     * 首先,从应用程序上下文中获取 FeatureRouterService 实例
     * 然后,创建一个新的 JsonObject 对象,并添加一些默认属性,如用户名、机器人 ID 和输入消息 "hi"
     * 接着,调用 FeatureRouterService 的 queryProbot 方法,传入创建的 JsonObject,并获取响应结果
     * 最后,从响应结果中提取 JsonObject 数据并返回
     */
    protected static JsonObject callBot(StateContext context, String botId, String input) {
        log.info("call bot bot id:{}", botId);
        if (StringUtils.isEmpty(botId)) {
            return new JsonObject();
        }
        FeatureRouterService featureRouterService = ApplicationContextProvider.getBean(FeatureRouterService.class);
        JsonObject obj = new JsonObject();
        obj.addProperty("userName", context.getUser());
        obj.addProperty("botId", botId);
        obj.addProperty("input", input);
        Result<JsonObject> res = featureRouterService.executeProbot(obj);
        JsonObject jsonObj = res.getData();
        if (jsonObj.has("result")) {
            return jsonObj.get("result").getAsJsonObject();
        }
        return jsonObj;
    }

    //提取记忆
    protected void extractAndSaveChatSummaries(StateContext context) {
        if (context.getBotVo().getMeta()==null || !context.getBotVo().getMeta().containsKey("memory")) {
            return;
        }
        //做一些收尾工作(执行收尾bot,完结这次聊天,给对方打分,收集对方信息)
        JsonObject res = callBot(context, context.getBotIdByName("endingBotId"), botCallInput(context));
        log.info("EndingChatState:{}", res);
        //需要把长期记忆的,和按天记忆的放到数据库中

        ChatSummaryService chatSummaryService = ApplicationContextProvider.getBean(ChatSummaryService.class);

        LongTermChatSummaryPo longTermChatSummary = extractLongTermChatSummary(context, res.get("data").getAsJsonObject().getAsJsonArray("long-term"));
        chatSummaryService.addOrUpdateLongTermChatContent(longTermChatSummary);

        List<ShortTermChatSummaryPo> shortTermChatSummaries = new ArrayList<>();
        res.get("data").getAsJsonObject().getAsJsonArray("short-term").getAsJsonArray().forEach(it -> {
            extractShortTermChatSummary(context, it, shortTermChatSummaries);
        });
        chatSummaryService.addShortTermChatSummaries(shortTermChatSummaries);
    }

    private String botCallInput(StateContext context) {
        ChatSummaryService chatSummaryService = ApplicationContextProvider.getBean(ChatSummaryService.class);
        List<LongTermChatSummaryPo> list = chatSummaryService.qryLongTermChatSummaries(context.getUser(), context.getBotId().intValue()).getData();
        String longMemory = list.stream().map(it -> it.getContent()).collect(Collectors.joining("\n"));

        List<ShortTermChatSummaryPo> shortTerm = chatSummaryService.qryShortTermChatSummaries(ReqChatSummaryListDto.builder().username(context.getUser()).botId(context.getBotId().intValue()).build()).getData();
        String shortMemory = shortTerm.stream().map(it -> it.getSummary()).collect(Collectors.joining("\n"));

        StringBuilder sb = new StringBuilder("这是本次聊天记录：" + context.getMessages());
        if (org.junit.platform.commons.util.StringUtils.isNotBlank(longMemory)) {
            sb.append("这是已有的长期聊天summary信息：").append(longMemory);
        }
        if (org.junit.platform.commons.util.StringUtils.isNotBlank(shortMemory)) {
            sb.append("这是已有的短期聊天summary信息：").append(shortMemory);
        }
        return sb.toString();
    }

    private void extractShortTermChatSummary(StateContext context, JsonElement it, List<ShortTermChatSummaryPo> shortTermChatSummaries) {
        //短期记忆
        JsonObject data = it.getAsJsonObject();
        Long expireTime = data.has("daysToExpire") ? DateUtils.getFutureTimestampInMillis(data.get("daysToExpire").getAsInt()) : null;
        shortTermChatSummaries.add(ShortTermChatSummaryPo.builder()
                .botId(context.getBotId())
                .username(context.getUser())
                .positive(data.get("positive").getAsBoolean())
                .priority(data.get("priority").getAsInt())
                .summary(data.get("summary").getAsString())
                .expireTime(expireTime)
                .build());
    }

    private LongTermChatSummaryPo extractLongTermChatSummary(StateContext context, JsonElement it) {
        //长期记忆
        return LongTermChatSummaryPo.builder()
                .botId(context.getBotId())
                .username(context.getUser())
                .content(it.toString())
                .build();
    }

    protected void clearMessagesExceptLast(StateContext context) {
        try {
            int num = context.getBotVo().getBotSetting().getDialogueTurns();
            log.info("clearMessagesExceptLast num:{}", num);
            context.clearMessagesExceptLast(num);
        } catch (Throwable ex) {
            log.error("clearMessagesExceptLast error:", ex);
        }
    }


}
