package run.mone.hive.actions;

import lombok.*;
import run.mone.hive.common.TriFunction;
import run.mone.hive.llm.LLM;
import run.mone.hive.roles.Role;
import run.mone.hive.schema.ActionContext;
import run.mone.hive.schema.ActionReq;
import run.mone.hive.schema.Message;

import java.util.concurrent.CompletableFuture;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Action {

    protected String prompt;

    protected String name;

    protected String description;

    protected LLM llm;

    protected TriFunction<ActionReq, Action, ActionContext, Message> function = (req, action, context) -> Message.builder().content(this.getClass().getName()).build();

    @ToString.Exclude
    private Role role;


    public Action(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public CompletableFuture<Message> run(ActionReq req, ActionContext context) {
        Message msg = this.function.apply(req, this, context);
        msg.setRole(req.getRole().getName());
        return CompletableFuture.supplyAsync(() -> msg);
    }

    protected LLM llm(ActionReq req) {
        if (this.llm != null) {
            return this.llm;
        }
        return req.getRole().getLlm();
    }

}