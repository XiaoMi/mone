
package run.mone.hive.actions.db;

import com.google.common.collect.ImmutableMap;
import run.mone.hive.actions.Action;
import run.mone.hive.common.AiTemplate;
import run.mone.hive.schema.ActionContext;
import run.mone.hive.schema.ActionReq;
import run.mone.hive.schema.Message;

public class DesignSchemaAction extends Action {

    public DesignSchemaAction() {
        super("DesignSchema", "Design database schema based on user requirements");
        this.function = this::designSchema;
    }

    private Message designSchema(ActionReq req, Action action, ActionContext context) {
        String schemaDesign = "Here's a proposed schema design based on your requirements:\n" +
                "${requirements}";
        String prompt = AiTemplate.renderTemplate(schemaDesign, ImmutableMap.of("requirements", req.getMessage().getContent()));
        String res = llm(req).syncChat(getRole(), prompt);

        return Message.builder()
                .role(req.getRole().getName())
                .content(res)
                .build();
    }
}
