package run.mone.m78.service.agent.multiagent;

import akka.actor.AbstractActor;
import akka.actor.Props;
import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 * @date 2024/9/6 18:17
 */
@Slf4j
public class ConsensusJudgeActor extends AbstractActor {

    // 判断共识的请求消息
    public static class JudgeConsensusRequest {
        public final String agent1Response;
        public final String agent2Response;
        public final String currentConsensus;

        public JudgeConsensusRequest(String agent1Response, String agent2Response, String currentConsensus) {
            this.agent1Response = agent1Response;
            this.agent2Response = agent2Response;
            this.currentConsensus = currentConsensus;
        }
    }

    // 判断共识的响应消息
    public static class JudgeConsensusResponse {
        public final boolean consensusReached;
        public final String explanation;

        public JudgeConsensusResponse(boolean consensusReached, String explanation) {
            this.consensusReached = consensusReached;
            this.explanation = explanation;
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(JudgeConsensusRequest.class, this::onJudgeConsensusRequest)
                .build();
    }

    private void onJudgeConsensusRequest(JudgeConsensusRequest request) {
        // 这里应该调用LLM来判断是否达成共识
        boolean consensusReached = judgeConsensus(request);
        String explanation = generateExplanation(request, consensusReached);
        getSender().tell(new JudgeConsensusResponse(consensusReached, explanation), getSelf());
    }

    private boolean judgeConsensus(JudgeConsensusRequest request) {
        // 调用LLM来判断是否达成共识
        // 这里是一个简单的示例实现，实际应该使用LLM
        return Math.random() < 0.3; // 30%的概率认为达成共识
    }

    private String generateExplanation(JudgeConsensusRequest request, boolean consensusReached) {
        // 调用LLM生成解释
        // 这里是一个简单的示例实现，实际应该使用LLM
        if (consensusReached) {
            return "The responses from both agents show significant alignment on key points.";
        } else {
            return "There are still notable differences in the agents' perspectives that need to be reconciled.";
        }
    }

    public static Props props() {
        return Props.create(ConsensusJudgeActor.class);
    }

    @Override
    public void preStart() throws Exception {
        log.info("pres start consensusJudge actor");
        super.preStart();
    }

    @Override
    public void postStop() throws Exception {
        log.info("post stop consensusJudge actor id:{}", this.getSelf());
        super.postStop();
    }
}
