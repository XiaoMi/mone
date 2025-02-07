package run.mone.m78.service.agent.state.bot;

import lombok.extern.slf4j.Slf4j;
import run.mone.m78.service.agent.state.AthenaState;
import run.mone.m78.service.agent.state.StateContext;
import run.mone.m78.service.agent.state.StateReq;
import run.mone.m78.service.bo.chatgpt.Message;
import run.mone.m78.service.context.ApplicationContextProvider;
import run.mone.m78.service.service.chat.ChatService;
import run.mone.m78.service.vo.BotVo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2024/5/26 11:17
 */
@Slf4j
public class BotInitializationState extends AthenaState {


    @Override
    public void enter(StateContext context) {
        log.info("BotInitializationState enter");
        if (context.getMessageList().size() > 0) {
            extractAndSaveChatSummaries(context);
        }
        //强行重置context中的状态(因为有可能是从别的状态切过来的)
        context.reset();

        ChatService chatService = ApplicationContextProvider.getBean(ChatService.class);
        BotVo botVo = context.getBotVo();
        String userName = context.getUser();

        String model = botVo.getBotSetting().getAiModel();

        //有的模型,不希望第一个问题是机器人,比如claude
        log.info("use model:{}", model);

        String role = ChatService.determineRoleBasedOnModel(model);

        List<Message> messageList = new ArrayList<>();

        //基础人设
        chatService.addBotSettingMessage(botVo, messageList, role);

        // 知识库知识 0718 不从bot初始化时获取
        chatService.buildKnowledgeMessageFromBotVo(botVo, messageList, role);

        //支持的插件(plugin)
        chatService.addPluginInfoMessage(botVo, messageList, role);

        //开场白
        chatService.addOpeningRemarkToMessages(botVo, messageList, model);

        //之前的记忆(数据库中的)
        chatService.addMemoryToMessageList(userName, botVo, messageList, role);

        //准备好所有与设定的
        context.getMessageDefinedList().addAll(messageList);

    }


    @Override
    public void execute(StateReq req, StateContext context) {
        //等待人类问问题
        fsm.changeState(new WaitingForQuestionState());
    }
}
