package run.mone.hive.context;

import lombok.Data;
import run.mone.hive.Team;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.utils.CostManager;

import java.util.Map;

@Data
public class Context {

    private LLM defaultLLM;

    private CostManager costManager = new CostManager();

    private String language;

    private String teachingLanguage;

    private Map<String, Object> kwargs;

    private Team team;


    public LLM llm() {
        return defaultLLM;
    }

    public LLM llmWithCostManagerFromLLMConfig(LLMConfig config) {
        return null;
    }

    public Object serialize() {
        return null;
    }

    public void deserialize(Map<String, Object> context) {

    }
}