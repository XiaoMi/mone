package run.mone.hive.actions;

import run.mone.hive.common.Constants;
import run.mone.hive.llm.HumanProvider;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.memory.Memory;
import run.mone.hive.schema.ActionContext;
import run.mone.hive.schema.ActionReq;
import run.mone.hive.schema.Message;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class HumanConfirmAction extends Action {

    private final HumanProvider llm;

    public HumanConfirmAction() {

        super("HumanConfirm", "Human confirmation action");
        this.llm = new HumanProvider(new LLMConfig());
    }


    @Override
    public CompletableFuture<Message> run(ActionReq map, ActionContext context) {
        String prompt = getLastMessageContent(map);

        return CompletableFuture.supplyAsync(() -> {
            String response = llm.ask(prompt, 0);
            return Message.builder()
                    .id(UUID.randomUUID().toString())
                    .content(response)
                    .build();
        });
    }

    private static String getLastMessageContent(ActionReq map) {
        String prompt = "";
        Memory memory = map.getMemory();
        Message msg = memory.getStorage().get(memory.getStorage().size() - 1);
        prompt = msg.getContent();
        return prompt + "\n";
    }
}