package run.mone.hive.actions;

import lombok.*;
import run.mone.hive.llm.LLM;
import run.mone.hive.roles.Role;
import run.mone.hive.schema.ActionReq;
import run.mone.hive.schema.Message;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Action {

    protected String prompt;

    protected String name;

    protected String description;

    protected LLM llm;

    protected BiFunction<ActionReq, Action, String> function = (req, action) -> this.getClass().getName();

    @ToString.Exclude
    private Role role;

    protected Action(String systemPrompt) {
        this.prompt = systemPrompt;
    }

    public Action(String name, String systemPrompt) {
        this.name = name;
        this.prompt = systemPrompt;
    }


    public CompletableFuture<Message> run(ActionReq map) {
        throw new RuntimeException();
    }

}