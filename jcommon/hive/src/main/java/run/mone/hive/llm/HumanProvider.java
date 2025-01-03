package run.mone.hive.llm;

import lombok.extern.slf4j.Slf4j;
import run.mone.hive.common.Constants;
import run.mone.hive.configs.LLMConfig;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Humans provide themselves as a 'model', which actually takes in human input as its response.
 * This enables replacing LLM anywhere in the framework with a human, thus introducing human interaction.
 */
@Slf4j
public class HumanProvider extends LLM {


    private final ExecutorService executor;

    public HumanProvider(LLMConfig config) {
        super(config);
        this.executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Synchronous method to get human input
     */
    public String ask(String msg, int timeout) {
        log.info("{}", msg);
        Scanner scanner = new Scanner(System.in);
        String response = scanner.nextLine();

        if ("exit".equalsIgnoreCase(response) || "quit".equalsIgnoreCase(response)) {
            System.exit(0);
        }

        return response;
    }


    /**
     * Implementation of abstract chat method from BaseLLM
     */
    @Override
    public String chat(String prompt) {
        return ask(prompt, Constants.DEFAULT_TIMEOUT);
    }


    /**
     * Clean up resources when provider is no longer needed
     */
    public void close() {
    }
} 