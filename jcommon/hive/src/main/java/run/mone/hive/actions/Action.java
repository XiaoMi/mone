package run.mone.hive.actions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import run.mone.hive.llm.BaseLLM;
import run.mone.hive.roles.Role;
import run.mone.hive.schema.Message;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Action {
    protected String systemPrompt;
    protected String name;
    protected String description;
    protected BaseLLM llm;

    @ToString.Exclude
    private Role role;

    protected Action(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }

    public Action(String name, String systemPrompt) {
        this.name = name;
        this.systemPrompt = systemPrompt;
    }

    public CompletableFuture<Message> run() {
        throw new RuntimeException();
    }

    public CompletableFuture<Message> run(Map<String, Object> map) {
        throw new RuntimeException();
    }

    public CompletableFuture<Message> run(Message message) {
        throw new UnsupportedOperationException("This action doesn't support single message execution");
    }

    public CompletableFuture<Message> run(List<Message> history) {
        throw new RuntimeException();
    }
} 