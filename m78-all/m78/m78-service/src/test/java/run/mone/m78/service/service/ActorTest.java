package run.mone.m78.service.service;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import run.mone.m78.service.agent.bo.Discussant;
import run.mone.m78.service.agent.multiagent.AgentActor;
import run.mone.m78.service.agent.multiagent.MultiAgentService;
import run.mone.m78.service.agent.multiagent.message.BotParam;
import run.mone.m78.service.agent.multiagent.message.FinalSummary;

/**
 * @author goodjava@qq.com
 * @date 2024/9/6 18:30
 */
public class ActorTest {

    @SneakyThrows
    @Test
    public void test1() {
        MultiAgentService service = new MultiAgentService();
        service.init();
        System.in.read();
    }


    @SneakyThrows
    @Test
    public void test2() {
        String topic = "电商系统接口api";
        Discussant xiaoming = Discussant.builder().name("丁仔")
                .role("vue前端")
                .initialView("1.你需要列出后端需要实现哪些功能 2.你会充分采纳后端的意见 3.如果有些接口你觉得你实现更好,你可以建议给后端").build();
        Discussant xiaogang = Discussant.builder().name("阿巴阿巴")
                .role("java后端 你每次都会列出接口信息")
                .initialView("1.你会根据前端的功能需求,列出具体的接口,比如/book/add 2.你会采纳前端提的需求,并给他生成接口 3.如果前端要求你去掉某个接口,你尽量同意")
                .build();

        MultiAgentService service = new MultiAgentService();
        service.init();
        FinalSummary res = service.discuss(Lists.newArrayList(xiaoming, xiaogang), topic, "帮我分析下聊天记录,然后汇总下他们达成共识的接口列表");
        System.out.println(res);
        System.in.read();
    }


    @SneakyThrows
    @Test
    public void test3() {
        ActorSystem system = ActorSystem.create("System");
        ActorRef actor = system.actorOf(AgentActor.props("a"));
        actor.tell(BotParam.builder().botId("130335").input("1+1=?").build(),ActorRef.noSender());
        System.in.read();
    }

    @SneakyThrows
    @Test
    public void test4() {
        String topic = "人的本性到底是善还是恶?";
        Discussant yellowEyeBrow = Discussant.builder()
                .name("黄眉怪")
                .role("妖怪,之前也是佛前弟子")
//                .model("moonshot_32k")
                // .botId("130467")
                .initialView("人之初性本恶, 人应顺应本心，及时行乐，不要妄图改别人性")
                .build();
        Discussant sinoXenic = Discussant.builder()
                .name("金蝉子")
                .role("佛学大成者，如来弟子")
                // .botId("130466")
//                .model("gpt4_o")
                .initialView("人性有好有坏，但应看到人性中的闪光点，积极引导善的部分")
                .build();

        MultiAgentService service = new MultiAgentService();
        service.init();
        FinalSummary res = service.discuss(Lists.newArrayList(yellowEyeBrow, sinoXenic), topic, "帮我分析下聊天记录,得出推论!");
        System.out.println(res);
        System.in.read();
    }

}
