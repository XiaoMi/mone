package run.mone.hive.role;

import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import run.mone.hive.Team;
import run.mone.hive.actions.AnalyzeArchitecture;
import run.mone.hive.actions.programmer.WriteCode;
import run.mone.hive.actions.WriteDesign;
import run.mone.hive.actions.python.ExecutePythonCode;
import run.mone.hive.actions.python.FixPythonBug;
import run.mone.hive.actions.python.WritePythonCode;
import run.mone.hive.actions.writer.WriteAction;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.context.Context;
import run.mone.hive.llm.LLM;
import run.mone.hive.roles.*;
import run.mone.hive.schema.Message;
import run.mone.hive.schema.RoleContext;

import java.util.List;


/**
 * @author goodjava@qq.com
 * @date 2024/12/29 16:17
 */
public class RoleTest {


    @SneakyThrows
    @Test
    public void testCoordinator() {
        LLM llm = new LLM(LLMConfig.builder().debug(false).build());
        Context context = new Context();
        context.setDefaultLLM(llm);
        Team team = new Team(context);
        Writer writer = new Writer("Writer");
        writer.setActions(new WriteAction());
        team.hire(new Teacher("Teacher"), writer, new Coordinator());
        team.publishMessage(Message.builder().role("user").content("帮我写一篇文章,有关秋天的.").sendTo(Lists.newArrayList("Coordinator")).build());
        team.run(2);
        System.in.read();
    }

    @Test
    public void testWriter() {
        LLM llm = new LLM(LLMConfig.builder().debug(false).build());
        Context context = new Context();
        context.setDefaultLLM(llm);
        Team team = new Team(context);

        Writer writer = new Writer("鲁迅");
        writer.setLlm(llm);

        team.hire(Lists.newArrayList(writer));
        team.runProject("写一篇200字的有关足球的作文", "user", "鲁迅");
        team.run(1);
        System.out.println(team.getEnv().getDebugHistory());
    }


    @SneakyThrows
    @Test
    public void testPython() {
        Engineer engineer = new Engineer();
        engineer.setLlm(new LLM(LLMConfig.builder().build()));
        engineer.getRc().setReactMode(RoleContext.ReactMode.BY_ORDER);
        engineer.setActions(new WritePythonCode(), new ExecutePythonCode(), new FixPythonBug());
        engineer.putMessage(Message.builder().content("编写一个计算两数和的函数,thx").sendTo(Lists.newArrayList("Engineer")).build());
        engineer.run().get();
    }


    /**
     * 测试写代码
     */
    @Test
    public void testWriteCode() {
        LLM llm = new LLM(LLMConfig.builder().debug(false).build());
        Context context = new Context();
        context.setDefaultLLM(llm);
        Team team = new Team(context);
        team.hire(new Architect().setActions(new AnalyzeArchitecture()), new Design().setActions(new WriteDesign()), new Engineer().setActions(new WriteCode()));

        Message message = Message.builder()
                .id(java.util.UUID.randomUUID().toString())
                .role("Human")
                .sentFrom("user")
                .sendTo(List.of("Architect"))
                .content("帮我开发一个java的登录模块")
                .build();

        team.publishMessage(message);

        team.run(3);
        System.out.println(team);
    }


    //辩论,人类决定是否退出
    @Test
    public void testHumanAndDebatorDebate() {
        LLM llm = new LLM(LLMConfig.builder().debug(false).build());
        Context context = new Context();
        context.setDefaultLLM(llm);
        Team team = new Team(context);

        Human human = new Human("Human", "A human participant in the debate");
        Debator debatorA = new Debator("DebatorA", "First debator", "DebatorB", llm);
        Debator debatorB = new Debator("DebatorB", "Second debator", "DebatorA", llm);

//        team.hire(Lists.newArrayList(human, debatorA, debatorB));
        team.hire(Lists.newArrayList(debatorA, debatorB));

        String debateTopic = "是否应该禁止在公共场所使用手机?";
        Message initialMessage = Message.builder()
                .id(java.util.UUID.randomUUID().toString())
                .sentFrom("user")
                .sendTo(List.of("DebatorA", "DebatorB"))
                .content("请就以下话题展开辩论: " + debateTopic)
                .build();

        team.publishMessage(initialMessage);

        for (int i = 0; i < 2; i++) {
            System.out.println("Round " + (i + 1) + ":");
            team.run(1);
//            System.out.println(team.getEnv().getDebugHistory());

            human.putMessage(Message.builder().content("是否继续辩论?输入'continue'继续,或'stop'结束辩论。").build());
            Message humanConfirmation = human.react().join();

            if (!"continue".equalsIgnoreCase(humanConfirmation.getContent().trim())) {
                System.out.println("Human chose to end the debate.");
                break;
            }
        }

    }


}