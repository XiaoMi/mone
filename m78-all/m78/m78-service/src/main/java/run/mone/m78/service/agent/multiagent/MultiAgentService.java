package run.mone.m78.service.agent.multiagent;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.util.Timeout;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import run.mone.m78.service.agent.bo.Discussant;
import run.mone.m78.service.agent.bo.StartDiscussionMessage;
import run.mone.m78.service.agent.multiagent.message.FinalSummary;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2024/9/6 16:35
 * 多agent 系统
 */
@Service
@Slf4j
public class MultiAgentService {

    private ActorSystem system;

    /**
     * 初始化方法，在依赖注入完成后自动调用
     * <p>
     * 该方法用于初始化日志记录和创建ActorSystem实例。
     */

    @PostConstruct
    public void init() {
        log.info("init");
        system = ActorSystem.create("ConsensusSystem");
    }

    /**
     * 发起讨论并返回最终总结
     *
     * @param list        参与讨论的讨论者列表
     * @param topic       讨论的主题
     * @param summaryHint 总结提示信息
     * @return 最终讨论总结，如果发生错误则返回null
     */
    public FinalSummary discuss(List<Discussant> list, String topic, String summaryHint) {
        String id = UUID.randomUUID().toString();
        ActorRef coordinator = null;
        try {
            coordinator = system.actorOf(Props.create(CoordinatorActor.class, 4, CoordinatorActor.DEFAULT_COORDINATOR_ID), "coordinator_" + id);
            Future<Object> future = Patterns.ask(coordinator, new StartDiscussionMessage(list.get(0), list.get(1), topic, summaryHint), Timeout.apply(Duration.create(3, TimeUnit.MINUTES)));
            FinalSummary result = (FinalSummary) Await.result(future, Duration.create(3, TimeUnit.MINUTES));
            return result;
        } catch (Exception e) {
            log.error("等待讨论结果时出错: " + e.getMessage());
        } finally {
            system.stop(coordinator);
        }
        return null;
    }


}
