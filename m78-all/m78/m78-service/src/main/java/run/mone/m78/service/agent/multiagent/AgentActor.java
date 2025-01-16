package run.mone.m78.service.agent.multiagent;

import akka.actor.AbstractActor;
import akka.actor.Props;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import run.mone.m78.client.bot.BotHttpClient;
import run.mone.m78.client.model.M78BotReq;
import run.mone.m78.service.agent.bo.AgentPrompt;
import run.mone.m78.service.agent.multiagent.message.AgentResponse;
import run.mone.m78.service.agent.multiagent.message.BotParam;
import run.mone.m78.service.agent.multiagent.message.InitialView;
import run.mone.m78.service.agent.multiagent.message.SystemMessage;

import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2024/9/6 18:00
 */
@Slf4j
public class AgentActor extends AbstractActor {

    public static final String DEFAULT_AGENT_ID = "130470";

    private final String agentName;

    private String agentId;

    private String model = "";

    private String initialInput;

    private String characterSetting;

    private String mainPoints;

    private String team;

    //要讨论的主题
    private String topic;

    public AgentActor(String agentName, String initialInput, String characterSetting, String mainPoints, String team, String topic) {
        this(agentName, "", initialInput, characterSetting, mainPoints, team, topic);
    }

    public AgentActor(String agentName, String model, String initialInput, String characterSetting, String mainPoints, String team, String topic) {
        this(agentName, DEFAULT_AGENT_ID, model, initialInput, characterSetting, mainPoints, team, topic);
    }


    public AgentActor(String agentName, String agentId, String model, String initialInput, String characterSetting, String mainPoints, String team, String topic) {
        this.agentName = agentName;
        this.agentId = StringUtils.isNotBlank(agentId) ? agentId : DEFAULT_AGENT_ID;
        this.model = model;
        this.initialInput = initialInput;
        this.characterSetting = characterSetting;
        this.mainPoints = mainPoints;
        this.team = team;
        this.topic = topic;
    }

    public AgentActor(String agentName) {
        this.agentName = agentName;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SystemMessage.class, this::onSystemMessage)
                .match(AgentPrompt.class, this::onPrompt)
                .match(BotParam.class, this::executeBot)
                .build();
    }


    private void onSystemMessage(SystemMessage message) {
        if (message == SystemMessage.PRESENT_INITIAL_VIEWS) {
            String initialView = generateInitialView();
            getSender().tell(new InitialView(agentName, initialView), getSelf());
        }
    }

    private void onPrompt(AgentPrompt prompt) {
        String response = generateResponse(prompt);
        getSender().tell(new AgentResponse(response, this.agentName), getSelf());
    }

    //执行某个bot
    private void executeBot(BotParam botParam) {
        String res = callBot(botParam.getInput(), botParam.getBotId());
        getSender().tell(AgentResponse.builder().content(res).name(this.agentName).cmd("executeBot").build(), getSelf());
    }

    private String generateInitialView() {
        // 基于初始输入生成初始观点
        return "Initial view of " + agentName + " based on: " + initialInput;
    }

    private String generateResponse(AgentPrompt prompt) {
        String history = prompt.getHistory().stream().collect(Collectors.joining("\n"));
        int r = prompt.getRound() + 1;
        int curRound = r / 2 + (r % 2);
        return callBot(this.agentName, prompt.getCurrentPrompt(), history, topic, prompt.getTotalRound() / 2, curRound);
    }


    private String callBot(String name, String input, String history, String topic, int round, int curRound) {
        return callBot(name, input, history, topic, round, curRound, agentId);
    }

    private String callBot(String input, String botId) {
        return callBot("", input, "", topic, 0, 0, botId);
    }

    private String callBot(String name, String input, String history, String topic, int round, int curRound, String botId) {
        return callBot("", "", input, "", topic, 0, 0, botId);
    }

    private String callBot(String name, String model, String input, String history, String topic, int round, int curRound, String botId) {
        BotHttpClient.Builder builder = BotHttpClient.builder()
//                .url("http://127.0.0.1:8077/open-apis/v1/ai-plugin-new/feature/router/probot/query")
                .token("your_token");
        if (StringUtils.isNotBlank(model)) {
            builder.model(model);
        }
        BotHttpClient client = builder.build();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("team", team);
        jsonObject.addProperty("_history", history);
        jsonObject.addProperty("topic", topic);
        jsonObject.addProperty("desc", "");
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("character_setting", characterSetting);
        jsonObject.addProperty("main_points", mainPoints);
        jsonObject.addProperty("input", input);
        jsonObject.addProperty("round", round);
        jsonObject.addProperty("cur_round", curRound);

        String res = client.callBot(M78BotReq.builder()
                .token("your_token")
                .botId(botId)
                .userName("your_name")
                .input("")
                .build(), jsonObject, BotHttpClient.DEFAULT_FUNCTION);
        return res;
    }


    public static Props props(String agentName, String agentId, String initialInput, String characterSetting, String mainPoint, String team, String topic, String model) {
        return Props.create(AgentActor.class, () -> new AgentActor(agentName, agentId, model, initialInput, characterSetting, mainPoint, team, topic));
    }

    public static Props props(String agentName, String initialInput, String characterSetting, String mainPoint, String team, String topic, String model) {
        return Props.create(AgentActor.class, () -> new AgentActor(agentName, model, initialInput, characterSetting, mainPoint, team, topic));
    }

    public static Props props(String agentName) {
        //这个actor 主要用来调用bot
        return Props.create(AgentActor.class, () -> new AgentActor(agentName));
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        log.info("stop actor:{}", this.agentName);
    }
}
