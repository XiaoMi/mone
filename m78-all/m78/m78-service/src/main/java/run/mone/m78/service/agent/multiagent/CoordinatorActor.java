package run.mone.m78.service.agent.multiagent;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.pattern.Patterns;
import akka.util.Timeout;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import run.mone.m78.service.agent.bo.AgentPrompt;
import run.mone.m78.service.agent.bo.DiscussionHistory;
import run.mone.m78.service.agent.bo.StartDiscussionMessage;
import run.mone.m78.service.agent.multiagent.message.*;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2024/9/6 17:56
 * <p>
 * 共识系统
 */
@Slf4j
public class CoordinatorActor extends AbstractActor {

    public static final String DEFAULT_COORDINATOR_ID = "130471";

    private State currentState = State.INITIALIZING;

    private int roundCount = 0;

    private int totalRound = 6;

    private String coordinatorBotId = DEFAULT_COORDINATOR_ID;

    private ActorRef agent1;
    private ActorRef agent2;
    private Map<ActorRef, String> lastResponses = new HashMap<>();
    private Map<String, String> initialViews = new HashMap<>();
    private String currentConsensus = "";

    @Setter
    private String summaryHint = "";

    private ActorRef consensusJudge;

    private ActorRef summaryActor;

    private final DiscussionHistory discussionHistory = new DiscussionHistory();

    private ActorRef requester;

    public CoordinatorActor(int totalRound, String botId) {
        this.totalRound = totalRound;
        this.coordinatorBotId = StringUtils.isNoneBlank(botId) ? botId : DEFAULT_COORDINATOR_ID;
    }

    @Override
    public void preStart() {
        consensusJudge = getContext().actorOf(ConsensusJudgeActor.props(), "consensusJudge");
        summaryActor = getContext().actorOf(AgentActor.props("botActor"), "summaryActor");
    }


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SysMessage.class, this::onSystemMessage)
                .match(AgentResponse.class, this::onAgentResponse)
                .match(InitialView.class, this::onInitialView)
                .build();
    }


    private void onInitialView(InitialView view) {
        initialViews.put(view.agentName, view.view);
        if (initialViews.size() == 2) {
            currentConsensus = mergeInitialViews();
            getSelf().tell(SysMessage.builder().systemMessage(SystemMessage.CONTINUE_DISCUSSION).build(), getSelf());
        }
    }

    private void updateConsensus() {
        String response1 = lastResponses.get(agent1);
        String response2 = lastResponses.get(agent2);
        currentConsensus = mergeResponses(response1, response2);
    }

    private String mergeInitialViews() {
        return "Please continue the discussion.";
    }

    private String mergeResponses(String response1, String response2) {
        // 合并两个响应
        return "Updated consensus based on: " + response1 + " and " + response2;
    }

    private boolean consensusReached() {
        if (true) {
            return false;
        }
        String response1 = lastResponses.get(agent1);
        String response2 = lastResponses.get(agent2);

        ConsensusJudgeActor.JudgeConsensusRequest request =
                new ConsensusJudgeActor.JudgeConsensusRequest(response1, response2, currentConsensus);

        Timeout timeout = Timeout.apply(Duration.create(5, TimeUnit.SECONDS));

        Future<Object> future = Patterns.ask(consensusJudge, request, timeout);

        try {
            Object result = Await.result(future, timeout.duration());
            if (result instanceof ConsensusJudgeActor.JudgeConsensusResponse) {
                ConsensusJudgeActor.JudgeConsensusResponse response =
                        (ConsensusJudgeActor.JudgeConsensusResponse) result;
                System.out.println("Consensus judge explanation: " + response.explanation);
                return response.consensusReached;
            } else {
                log.error("Unexpected response type from ConsensusJudgeActor");
                return false;
            }
        } catch (Exception e) {
            log.error("Error while judging consensus: " + e.getMessage());
            return false;
        }

    }

    private void sendDiscussionPrompt(String prompt) {
        if (currentState == State.SUMMARIZING) {
            System.out.println("Discussion has ended. Cannot send more prompts.");
            return;
        }

        List<String> recentHistory = discussionHistory.getLastNEntries(50);
        AgentPrompt agentPrompt = new AgentPrompt(prompt, recentHistory, this.roundCount, this.totalRound);

        ActorRef targetAgent;

        if (currentState == State.AGENT1_TURN) {
            targetAgent = agent2;
            currentState = State.AGENT2_TURN; // 准备下一轮
        } else {
            targetAgent = agent1;
            currentState = State.AGENT1_TURN; // 准备下一轮
        }
        log.info(prompt);
        targetAgent.tell(agentPrompt, getSelf());
    }

    public List<String> generateFinalSummary() {
        log.info("Final Consensus reached after " + roundCount + " rounds:");
        return this.discussionHistory.getAllEntries().stream().map(it -> {
            log.info(it.toString());
            return it.toString();
        }).toList();

    }


    private String currentPrompt = "";

    @SneakyThrows
    private void onSystemMessage(SysMessage message) {
        switch (message.getSystemMessage()) {
            case START_DISCUSSION:
                requester = getSender();
                if (message instanceof StartDiscussionMessage startMsg) {
                    agent1 = getContext().actorOf(AgentActor.props(
                            startMsg.getAgent1Name(),
                            startMsg.getAgent1Id(),
                            "",
                            startMsg.getAgent1Role(),
                            startMsg.getMainPoint(),
                            startMsg.getTeam(),
                            startMsg.getTopic(),
                            startMsg.getAgent1Model()
                    ), "agent1");

                    agent2 = getContext().actorOf(AgentActor.props(
                            startMsg.getAgent2Name(),
                            startMsg.getAgent2Id(),
                            "",
                            startMsg.getAgent2Role(),
                            startMsg.getMainPoint(),
                            startMsg.getTeam(),
                            startMsg.getTopic(),
                            startMsg.getAgent2Model()
                    ), "agent2");
                    currentState = State.PRESENTING_VIEWS;
                    agent1.tell(SystemMessage.PRESENT_INITIAL_VIEWS, getSelf());
                    agent2.tell(SystemMessage.PRESENT_INITIAL_VIEWS, getSelf());
                    setSummaryHint(startMsg.getSummaryHint());
                }
                break;
            case CONTINUE_DISCUSSION:
                if (currentState == State.PRESENTING_VIEWS) {
                    currentState = State.AGENT1_TURN;
                    AgentPrompt ap = AgentPrompt.builder().round(this.roundCount).totalRound(this.totalRound).currentPrompt("").history(this.discussionHistory.getLastNEntries(50)).build();
                    agent1.tell(ap, getSelf());
                }
                break;
            case CHECK_CONSENSUS:
                if (consensusReached() || roundCount >= totalRound) {
                    currentState = State.SUMMARIZING;
                    getSelf().tell(SysMessage.builder().systemMessage(SystemMessage.FINAL_SUMMARY).build(), getSelf());
                } else {
                    sendDiscussionPrompt(currentConsensus);
                }
                break;
            case FINAL_SUMMARY:
                List<String> summary = generateFinalSummary();
                Future future = Patterns.ask(summaryActor, BotParam.builder().botId(coordinatorBotId)
                        .input(summaryHint + ":\n" + summary.stream().collect(Collectors.joining("\n")))
                        .build(), Timeout.apply(Duration.create(1, TimeUnit.MINUTES)));
                Object result = Await.result(future, Duration.create(1, TimeUnit.MINUTES));
                requester.tell(FinalSummary.builder().summary(summary).summaryStr(result.toString()).build(), getSelf());
                break;
        }
    }

    private void onAgentResponse(AgentResponse response) {
        ActorRef currentAgent = getSender();

        discussionHistory.addEntry(response.getName(), response.getContent());
        lastResponses.put(currentAgent, response.getContent());
        roundCount++;

        if (currentState == State.AGENT1_TURN) {
            currentState = State.AGENT2_TURN;
            currentPrompt = response.getContent();
            sendPromptToAgent(agent2);
        } else if (currentState == State.AGENT2_TURN) {
            currentPrompt = response.getContent();
            getSelf().tell(SysMessage.builder().systemMessage(SystemMessage.CHECK_CONSENSUS).build(), getSelf());
        }
    }

    private void sendPromptToAgent(ActorRef agent) {
        List<String> recentHistory = discussionHistory.getLastNEntries(50);
        AgentPrompt prompt = new AgentPrompt(currentPrompt, recentHistory, this.roundCount, this.totalRound);
        agent.tell(prompt, getSelf());
    }

    @Override
    public void postStop() throws Exception {
        log.info("stop coordinator actor");
        super.postStop();
    }
}
