package run.mone.hive.actions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import run.mone.hive.llm.LLM;
import run.mone.hive.roles.Role;
import run.mone.hive.schema.ActionReq;
import run.mone.hive.schema.Message;

import java.util.concurrent.CompletableFuture;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Action {
    protected String systemPrompt;
    protected String name;
    protected String description;
    protected LLM llm;

    @ToString.Exclude
    private Role role;

    protected Action(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }

    public Action(String name, String systemPrompt) {
        this.name = name;
        this.systemPrompt = systemPrompt;
    }


    public CompletableFuture<Message> run(ActionReq map) {
        throw new RuntimeException();
    }

}