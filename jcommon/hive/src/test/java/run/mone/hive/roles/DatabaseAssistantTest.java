package run.mone.hive.roles;

import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import run.mone.hive.Environment;
import run.mone.hive.actions.db.DesignSchemaAction;
import run.mone.hive.actions.db.ModifyDataAction;
import run.mone.hive.actions.db.QueryDataAction;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.schema.ActionContext;
import run.mone.hive.schema.ActionReq;
import run.mone.hive.schema.Message;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class DatabaseAssistantTest {

    private DatabaseAssistant databaseAssistant;

    @BeforeEach
    void setUp() {
        databaseAssistant = new DatabaseAssistant();
        databaseAssistant.setLlm(new LLM(LLMConfig.builder().build()));
        databaseAssistant.setEnvironment(new Environment());
        databaseAssistant.init();
    }

    @SneakyThrows
    @Test
    public void testRole() {
        this.databaseAssistant.putMessage(Message.builder().role("user").sendTo(Lists.newArrayList("DatabaseAssistant")).content("查询id为1的用户信息").build());
        this.databaseAssistant.run();
        System.in.read();
    }

    @Test
    void testDesignSchemaAction() {
        DesignSchemaAction action = (DesignSchemaAction) databaseAssistant.getActions().stream()
                .filter(a -> a instanceof DesignSchemaAction)
                .findFirst()
                .orElse(null);

        assertNotNull(action, "DesignSchemaAction should be present");

        Message message = Message.builder()
                .content("设计一个用户表的schema")
                .build();

        String result = action.run(ActionReq.builder().role(Role.builder().name("user").build()).message(message).build(), ActionContext.builder().build()).join().getContent();
        assertNotNull(result);
    }



    @Test
    void testQueryDataAction() {
        QueryDataAction action = (QueryDataAction) databaseAssistant.getActions().stream()
                .filter(a -> a instanceof QueryDataAction)
                .findFirst()
                .orElse(null);
        assertNotNull(action, "QueryDataAction should be present");
        ActionReq req = ActionReq.builder().role(Role.builder().name("user").build()).message(Message.builder().role("user").content("查询id为1的用户信息").build())
                .build();
        String result = action.run(req, ActionContext.builder().build()).join().getContent();
        assertNotNull(result);
    }


    @Test
    void testModifyDataAction() {
        ModifyDataAction action = (ModifyDataAction) databaseAssistant.getActions().stream()
                .filter(a -> a instanceof ModifyDataAction)
                .findFirst()
                .orElse(null);
        assertNotNull(action, "ModifyDataAction should be present");
        ActionReq req = ActionReq.builder().role(Role.builder().name("user").build()).message(Message.builder().role("user").content("更新id为1的用户名为'John'").build())
                .build();
        String result = action.run(req, ActionContext.builder().build()).join().getContent();
        assertNotNull(result);
    }

}