package run.mone.hive.examples;

import run.mone.hive.actions.UserRequirement;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.context.Context;
import run.mone.hive.llm.LLM;
import run.mone.hive.roles.Teacher;
import run.mone.hive.schema.Message;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TeacherDemo {

    public static void main(String[] args) {
        try {
            // 创建上下文
            Context context = new Context();
            context.setDefaultLLM(new LLM(new LLMConfig()) {
                @Override
                public String chat(String prompt) {
                    return "chat";
                }
            });
            context.setLanguage("中文");
            context.setTeachingLanguage("中文");

            // 初始化教师角色
            Teacher teacher = new Teacher("Teacher");
            
            // 准备课程内容
            String lesson = """
                UNIT 1 Making New Friends
                
                Target Students: Grade 3 students
                Time Duration: 45 minutes
                
                Main Goals:
                1. Learn basic greetings in English
                2. Practice self-introduction
                3. Develop communication skills
                """;

            // 创建消息并执行
            Message message = Message.builder()
                .content(lesson)
                .role("user")
                .causeBy(UserRequirement.class.getName())
                .build();

            teacher.putMessage(message);
            // 运行并获取结果
            Message result = teacher.run().join();
            
            // 打印结果
            System.out.println("=== Teaching Plan Generated ===");
            System.out.println(result.getContent());
            System.out.println("========== End ==========");

        } catch (Exception e) {
            log.error("Error running TeacherDemo", e);
        }
    }
} 