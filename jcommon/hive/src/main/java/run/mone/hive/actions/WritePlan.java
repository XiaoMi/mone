package run.mone.hive.actions;

import lombok.extern.slf4j.Slf4j;
import run.mone.hive.llm.LLM;
import run.mone.hive.schema.AiMessage;
import run.mone.hive.schema.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


/**
 * @author goodjava@qq.com
 * 规划任务
 */
@Slf4j
public class WritePlan extends Action {

    private static final String SYSTEM_PROMPT = """
        You are a helpful AI assistant that helps break down tasks into smaller, manageable subtasks.
        For the given user requirement, create a step-by-step plan with clear instructions for each task.
        Each task should be focused and achievable.
        
        Format the response as JSON with the following structure:
        {
            "tasks": [
                        {
                            "task_id": "1",
                            "dependent_task_ids": [],
                            "instruction": "设计用户实体类(User.java)和数据访问接口(UserRepository.java)",
                            "task_type": "CODING"
                        },
                        {
                            "task_id": "2",
                            "dependent_task_ids": ["1"],
                            "instruction": "实现用户服务层(UserService.java)包含登录和注册逻辑",
                            "task_type": "CODING"
                        },
                        {
                            "task_id": "3",
                            "dependent_task_ids": ["2"],
                            "instruction": "开发REST API接口(UserController.java)",
                            "task_type": "CODING"
                        },
                        {
                            "task_id": "4",
                            "dependent_task_ids": ["1", "2"],
                            "instruction": "编写用户服务单元测试(UserServiceTest.java)",
                            "task_type": "TESTING"
                        }
            ]
        }
        """;

    private static final String USER_PROMPT = """
        Based on the following context and requirements, create a plan with maximum %d tasks:
        
        %s
        
        Remember to:
        1. Break down complex tasks into smaller, manageable steps
        2. Ensure each task has a clear, specific goal
        3. Order tasks logically with dependencies in mind
        4. Include appropriate task types (CODE_REVIEW, CODE_TESTING, CODE_WRITING, etc.)
        """;

    public WritePlan(LLM llm) {
        this.setPrompt(SYSTEM_PROMPT);
        this.llm = llm;
    }

    public WritePlan() {
        this.setPrompt(SYSTEM_PROMPT);
    }



    public CompletableFuture<Message> run(List<Message> context, int maxTasks) {
        String contextStr = context.stream()
                .map(Message::getContent)
                .reduce("", (a, b) -> a + "\n" + b);

        String prompt = String.format(USER_PROMPT, maxTasks, contextStr);
        Message userMessage = new Message(prompt, "user");

        return CompletableFuture.supplyAsync(() -> {
            try {
                String response = generateChatResponse(userMessage);
                return new Message(response, "assistant", WritePlan.class.getName());
            } catch (Exception e) {
                log.error("Error in WritePlan execution", e);
                throw new RuntimeException("Failed to generate plan", e);
            }
        });
    }

    private String generateChatResponse(Message userMessage) {
        List<AiMessage> list = new ArrayList<>();
        list.add(AiMessage.builder().role("system").content(SYSTEM_PROMPT).build());
        list.add(AiMessage.builder().role("user").content(userMessage.getContent()).build());
        return llm.chat(list);
    }
} 