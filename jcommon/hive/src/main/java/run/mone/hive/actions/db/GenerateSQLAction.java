
package run.mone.hive.actions.db;

import com.google.common.collect.ImmutableMap;
import run.mone.hive.actions.Action;
import run.mone.hive.common.AiTemplate;
import run.mone.hive.schema.ActionContext;
import run.mone.hive.schema.ActionReq;
import run.mone.hive.schema.Message;

public class GenerateSQLAction extends Action {

    public GenerateSQLAction() {
        super("GenerateSQL", "Generate SQL statements based on user requirements");
        this.function = this::generateSQL;
    }

    private Message generateSQL(ActionReq req, Action action, ActionContext context) {
        // Implement the logic for generating SQL statements
        String sqlStatements = "Here are the SQL statements based on your requirements:\n" +
                               "${requirements}";

        String prompt = AiTemplate.renderTemplate(sqlStatements, ImmutableMap.of("requirements", req.getMessage().getContent()));
        String res = llm.chat(prompt);
        
        return Message.builder()
                .role(req.getRole().getName())
                .content(res)
                .build();
    }
}
