package run.mone.agentx.agent;

import org.springframework.stereotype.Component;
import run.mone.hive.llm.LLM;
import run.mone.hive.roles.ReactorRole;

import java.util.concurrent.CountDownLatch;

/**
 * @author goodjava@qq.com
 * @date 2025/4/14 18:01
 * 每个系统都会有一个Agent,用来决定一些有弹性的事情
 */
public class ManagerAgent extends ReactorRole {

    public ManagerAgent(String name, CountDownLatch countDownLatch, LLM llm) {
        super("AgentManager", null, llm);
    }



}
