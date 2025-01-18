package run.mone.m78.service.agent.state.bot;

import com.google.common.base.Joiner;
import com.google.gson.JsonArray;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.util.Lists;
import run.mone.m78.api.constant.BotMetaConstant;
import run.mone.m78.common.Constant;
import run.mone.m78.server.AskConsumer;
import run.mone.m78.service.agent.rebot.TemplateUtils;
import run.mone.m78.service.agent.rebot.function.PromptFunction;
import run.mone.m78.service.agent.state.AthenaState;
import run.mone.m78.service.agent.state.StateContext;
import run.mone.m78.service.agent.state.StateReq;
import run.mone.m78.service.bo.chatgpt.*;
import run.mone.m78.service.common.Config;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.common.SafeRun;
import run.mone.m78.service.context.ApplicationContextProvider;
import run.mone.m78.service.dao.entity.MultimodalEnum;
import run.mone.m78.service.database.UUIDUtil;
import run.mone.m78.service.service.base.ChatgptService;
import run.mone.m78.service.service.chat.ChatService;
import run.mone.m78.service.vo.BotVo;
import run.mone.z.proxy.api.dto.ModelInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2024/5/26 11:09
 * 提问阶段(向ai提问)
 */
@Slf4j
public class QuestioningPhaseState extends AthenaState {

    @Override
    public void execute(StateReq req, StateContext context) {
        log.info("QuestioningPhaseState key:{}", context.key());
        try {
            ChatgptService cs = ApplicationContextProvider.getBean(ChatgptService.class);
            BotVo botVo = context.getBotVo();
            String model = botVo.getBotSetting().getAiModel();
            log.info("讯问ai开始 model:{}", model);
            List<Message> messageList = new ArrayList<>();
            //预设的一些message
            messageList.addAll(context.getMessageDefinedList());
            Message lastMsg = context.getMessageList().getLast();

            //处理pin信息
            processPinMessages(lastMsg, messageList);

            //清理超过轮次的信息
            clearMessagesExceptLast(context);
            //之后的聊天记录(包含最后一个问题)
            messageList.addAll(context.getMessageList());
            //最后提问的msg列表
            List<Msg> msgList = new ArrayList<>();
            msgList.addAll(messageList.stream().map(it -> Msg.builder().role(it.getRole()).content(it.getContent()).build()).toList());
            ChatCompletion chatCompletion = ChatCompletion.builder().messages(messageList).build();

            String msgId = generateMessageId(lastMsg);

            AskConsumer consumer = new AskConsumer(context.getUser(), context.getChatSetup().getTopicId(), msgId, context.getChatSetup().getSessionId(), context.getChatSetup().getFsmKey());
            Integer multimodal = lastMsg.getMultimodal() == null ? 1 : lastMsg.getMultimodal();
            //文本的问题
            if (multimodal.equals(MultimodalEnum.text.getCode())) {
                processMessageAndAsk(context.getBotVo(), lastMsg, cs, msgId, model, msgList, chatCompletion, consumer);
            } else if (multimodal.equals(MultimodalEnum.image.getCode())) {
                executeAskWithMultimodalHandling(context, messageList, model, cs, consumer);
            }
        } finally {
            //切回到等待问题状态
            fsm.changeState(new WaitingForQuestionState());
        }

    }

    //处理pin信息
    private static void processPinMessages(Message lastMsg, List<Message> messageList) {
        SafeRun.run(() -> {
            if (null != lastMsg.getInput() && lastMsg.getInput().has("pinMsg")) {
                JsonArray pinMsgArray = lastMsg.getInput().get("pinMsg").getAsJsonArray();
                pinMsgArray.forEach(it -> {
                    Message msg = GsonUtils.gson.fromJson(it, Message.class);
                    messageList.add(msg);
                });
            }
        });
    }

    //获取消息id
    private static String generateMessageId(Message lastMsg) {
        String msgId = UUIDUtil.randomNanoId();
        try {
            if (lastMsg.getInput() != null && lastMsg.getInput().has("msgId")) {
                msgId = Joiner.on("$").join(msgId, lastMsg.getInput().get("msgId").getAsString());
            }
            //主要是idea 等client传递过来的id
            if (null != lastMsg.getInput() && lastMsg.getInput().has("reqId")) {
                msgId = lastMsg.getInput().get("reqId").getAsString();
            }
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
        }
        return msgId;
    }

    private static void processMessageAndAsk(BotVo botVo, Message lastMsg, ChatgptService cs, String msgId, String model, List<Msg> msgList, ChatCompletion chatCompletion, AskConsumer consumer) {
        if (hasParams(lastMsg)) {
            String content = generateResponseContent(botVo, lastMsg);
            lastMsg.setContent(content);
            log.info("content:{}", content);
            msgList.getLast().setContent(content);
        }
        buildSimilarKnowledgeMessage(botVo, model, msgList);
        //获取token
        String token = getToken(lastMsg);

        Ask.AskBuilder builder = Ask.builder();

        extractModelInfoFromMessage(lastMsg, builder);

        cs.ask2(builder.id(msgId).model(model).from("chat").msgList(msgList).zzToken(token).type(1).stream(true).chatCompletion(chatCompletion).userName(consumer.getUserName()).relationId(botVo.getBotId()).build(), consumer);
    }

    private static void extractModelInfoFromMessage(Message lastMsg, Ask.AskBuilder builder) {
        SafeRun.run(() -> {
            if (null != lastMsg.getInput() && lastMsg.getInput().has("modelInfo")) {
                ModelInfo modelInfo = GsonUtils.gson.fromJson(lastMsg.getInput().get("modelInfo"), ModelInfo.class);
                builder.modelInfo(modelInfo);
            }
        });
    }

    private static boolean hasParams(Message lastMsg) {
        return null != lastMsg.getParams() && lastMsg.getParams().size() > 0;
    }

    private static String generateResponseContent(BotVo botVo, Message lastMsg) {
        String content = lastMsg.getContent();
        //如果是私有prompt,则直接私有prompt是最后一条询问的信息
        if (botVo.getMetaValue(BotMetaConstant.PRIVATE_PROMPT, "false").equals("true")) {
            Map<String, String> meta = botVo.getMeta();
            String aiModel = botVo.getBotSetting().getAiModel();
            String modelSpecKey = BotMetaConstant.MODEL_SPEC_PROMPT_PREFIX + aiModel;
            // meta中有特定模型的指定prompt则使用指定的，否则使用bot配置的自定义prompt
            if (meta.containsKey(modelSpecKey) && StringUtils.isNotBlank(meta.get(modelSpecKey).trim())) {
                content = meta.get(modelSpecKey);
            } else {
                content = botVo.getBotSetting().getCustomizePrompt();
            }
        }
        content = TemplateUtils.renderTemplate3(content, lastMsg.getParams(), Lists.newArrayList(Pair.of(PromptFunction.name, new PromptFunction())));
        return content;
    }

    private static void buildSimilarKnowledgeMessage(BotVo botVo, String model, List<Msg> msgList) {
        if (CollectionUtils.isNotEmpty(botVo.getKnowledgeBoList())) {
            String role = ChatService.determineRoleBasedOnModel(model);
            ChatService chatService = ApplicationContextProvider.getBean(ChatService.class);
            chatService.buildSimilarKnowledgeMessageFromBotVo(botVo, msgList, role);
        }
    }

    private static String getToken(Message lastMsg) {
        String token = Config.zToken;
        if (lastMsg.getInput() != null && lastMsg.getInput().has(Constant.TOKEN)) {
            token = lastMsg.getInput().get(Constant.TOKEN).getAsString();
        }
        return token;
    }

    private static void executeAskWithMultimodalHandling(StateContext context, List<Message> messageList, String model, ChatgptService cs, AskConsumer consumer) {
        String mediaType = messageList.get(messageList.size() - 1).getMediaType();
        Ask ask = Ask.builder().id(UUIDUtil.randomNanoId()).model(model).zzToken(Config.zToken).type(1).stream(true).relationId(context.getBotId()).build();
        List<ContentVision> vision = new ArrayList<>();
        vision.add(ContentVision.builder().type(MultimodalEnum.image.getDesc()).source(
                ContentVisionSource.builder().type("base64").media_type(mediaType).data(messageList.get(messageList.size() - 1).getContent()).build()).build());
        //多模态的text优先取输入的附言，如果没有附言就取bot的人设
        String text = context.getMessageList().get(context.getMessageList().size() - 1).getPostscript();
        if (StringUtils.isEmpty(text)) {
            text = messageList.get(0).getContent();
            if (messageList.get(messageList.size() - 1).getPromptParams() != null && !messageList.get(messageList.size() - 1).getPromptParams().isEmpty()) {
                text = TemplateUtils.renderTemplate2(text, messageList.get(messageList.size() - 1).getPromptParams());
            }
        }

        vision.add(ContentVision.builder().type(MultimodalEnum.text.getDesc()).text(StringUtils.isNotEmpty(text) ? text : "请帮忙解释这张图片代表的内容").build());
        List<Msg> user = new ArrayList<>();
        user.add(Msg.builder().role("user").content(GsonUtils.gson.toJson(vision)).jsonContent(true).build());
        // 设置system prompt
        messageList.forEach(msg -> {
            if("system".equals(msg.getRole().toLowerCase())) {
                user.add(
                        Msg.builder().role(msg.getRole()).content(GsonUtils.gson.toJson(ContentVision.builder().type(MultimodalEnum.text.getDesc()).text(msg.getContent()).build())).jsonContent(true).build()
                );
            }
        });
        ask.setFrom("chat");
        ask.setMsgList(user);
        //防止多模态token超过
        if (context.getMessageList() != null && context.getMessageList().size() > 0) {
            for (Message message : context.getMessageList()) {
                if (MultimodalEnum.image.getCode() == message.getMultimodal()) {
                    message.setContent("1");
                }
            }
        }
        cs.ask2(ask, consumer);
    }
}
