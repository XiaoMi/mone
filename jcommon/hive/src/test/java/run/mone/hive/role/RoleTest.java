package run.mone.hive.role;

import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import run.mone.hive.Team;
import run.mone.hive.actions.writer.WriteAction;
import run.mone.hive.common.RoleType;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.context.Context;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.hive.roles.Coordinator;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.Teacher;
import run.mone.hive.roles.Writer;
import run.mone.hive.schema.Message;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;


/**
 * @author goodjava@qq.com
 * @date 2024/12/29 16:17
 */
public class RoleTest {

    @SneakyThrows
    @Test
    public void testReactorRole() {
        LLM llm = new LLM(LLMConfig.builder().debug(false).build());
        CountDownLatch latch = new CountDownLatch(1);
        ReactorRole role = new ReactorRole("minzai", latch, llm);
        role.run();
        String msg = "1+1=?";
        role.getRc().getNews().put(Message.builder().content(msg).data(msg).build());
        latch.await();
    }


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
        LLM llm = new LLM(LLMConfig.builder().llmProvider(LLMProvider.DOUBAO_DEEPSEEK_V3).build());
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


    //不是团队,就是一个Agent 直接执行
    @SneakyThrows
    @Test
    public void testWriter2() {
        LLM llm = new LLM(LLMConfig.builder().llmProvider(LLMProvider.DOUBAO_DEEPSEEK_V3).build());
        Context context = new Context();
        context.setDefaultLLM(llm);
        Writer writer = new Writer("鲁迅");
        writer.setGoal("写一篇200字的有关秋天的作文");
        writer.setProfile("你是一名优秀的作家");
        writer.setConstraints("作文水平大概是高考的水平");
        writer.setLlm(llm);
        writer.setActions(new WriteAction());
        writer.putMessage(Message.builder().content("写一篇200字的有关秋天的作文")
                .role(RoleType.user.name())
                .causeBy("鲁迅")//处理者
                .build());
        CompletableFuture<Message> future = writer.run();
        future.get();
    }


    //辩论,人类决定是否退出
//     @Test
//     public void testHumanAndDebatorDebate() {
//         LLM llm = new LLM(LLMConfig.builder().debug(false).build());
//         Context context = new Context();
//         context.setDefaultLLM(llm);
//         Team team = new Team(context);

//         Human human = new Human("Human", "A human participant in the debate");
//         Debator debatorA = new Debator("DebatorA", "First debator", "DebatorB", llm);
//         Debator debatorB = new Debator("DebatorB", "Second debator", "DebatorA", llm);

// //        team.hire(Lists.newArrayList(human, debatorA, debatorB));
//         team.hire(Lists.newArrayList(debatorA, debatorB));

//         String debateTopic = "是否应该禁止在公共场所使用手机?";
//         Message initialMessage = Message.builder()
//                 .id(java.util.UUID.randomUUID().toString())
//                 .sentFrom("user")
//                 .sendTo(List.of("DebatorA", "DebatorB"))
//                 .content("请就以下话题展开辩论: " + debateTopic)
//                 .build();

//         team.publishMessage(initialMessage);

//         for (int i = 0; i < 2; i++) {
//             System.out.println("Round " + (i + 1) + ":");
//             team.run(1);
// //            System.out.println(team.getEnv().getDebugHistory());

//             human.putMessage(Message.builder().content("是否继续辩论?输入'continue'继续,或'stop'结束辩论。").build());
//             Message humanConfirmation = human.react().join();

//             if (!"continue".equalsIgnoreCase(humanConfirmation.getContent().trim())) {
//                 System.out.println("Human chose to end the debate.");
//                 break;
//             }
//         }

//     }


}