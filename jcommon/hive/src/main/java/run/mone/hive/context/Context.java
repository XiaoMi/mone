package run.mone.hive.context;

import lombok.Data;
import run.mone.hive.Team;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.BaseLLM;
import run.mone.hive.utils.CostManager;

import java.util.Map;

@Data
public class Context {

    private BaseLLM defaultLLM;

    private CostManager costManager = new CostManager();

    private String language;

    private String teachingLanguage;

    private Map<String, Object> kwargs;

    private Team team;


    public BaseLLM llm() {
        return defaultLLM;
    }

    public BaseLLM llmWithCostManagerFromLLMConfig(LLMConfig config) {
        return null;
    }

    public Object serialize() {
        return null;
    }

    public void deserialize(Map<String, Object> context) {

    }
}