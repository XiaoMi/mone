package run.mone.hive.role;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import run.mone.hive.Team;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.context.Context;
import run.mone.hive.llm.BaseLLM;
import run.mone.hive.roles.Debator;
import run.mone.hive.roles.Developer;
import run.mone.hive.roles.HumanRole;
import run.mone.hive.roles.Writer;
import run.mone.hive.schema.Message;
import run.mone.hive.schema.RoleContext;

import java.util.List;


/**
 * @author goodjava@qq.com
 * @date 2024/12/29 16:17
 */
public class RoleTest {

    @Test
    public void testWriter() {
        BaseLLM llm = new BaseLLM(LLMConfig.builder().debug(false).build());
        Context context = new Context();
        context.setDefaultLLM(llm);
        Team team = new Team(context);

        Writer writer = new Writer("鲁迅", "writer", llm);

        team.hire(Lists.newArrayList(writer));
        team.runProject("写一篇200字的有关足球的作文", "user", "鲁迅");
        team.run(1);
        System.out.println(team.getEnv().getDebugHistory());
    }


    //辩论,人类决定是否退出
    @Test
    public void testHumanAndDebatorDebate() {
        BaseLLM llm = new BaseLLM(LLMConfig.builder().debug(false).build());
        Context context = new Context();
        context.setDefaultLLM(llm);
        Team team = new Team(context);

        HumanRole human = new HumanRole("Human", "A human participant in the debate");
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

        team.receiveMessage(initialMessage);

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


    @Test
    public void testLoginFunctionality() {
        BaseLLM llm = new BaseLLM(LLMConfig.builder().debug(false).json(true).build());
        Context context = new Context();
        context.setDefaultLLM(llm);
        Team team = new Team(context);

        Developer developer = new Developer("DevRole", "Software Developer", "", "",role->{
            role.setLlm(llm);
        });
        developer.getRc().setReactMode(RoleContext.ReactMode.PLAN_AND_ACT);
        team.hire(Lists.newArrayList(developer));

        Message initialMessage = Message.builder()
                .id(java.util.UUID.randomUUID().toString())
                .sentFrom("user")
                .sendTo(List.of("DevRole"))
                .content("Create a login functionality for our web application")
                .build();

        team.receiveMessage(initialMessage);

        // Run the planning and execution process
        team.run(1);
    }


}