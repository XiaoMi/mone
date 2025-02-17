package run.mone.hive.actions;

import com.google.common.base.Joiner;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import run.mone.hive.llm.LLM;
import run.mone.hive.roles.Role;
import run.mone.hive.schema.Expr;
import run.mone.hive.schema.Message;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import run.mone.hive.common.JsonUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;

import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActionNode {

    private static final String TAG = "CONTENT";
    private static final String LANGUAGE_CONSTRAINT = "Language: Please use the same language as Human INPUT.";
    private static final String FORMAT_CONSTRAINT = String.format("Format: output wrapped inside [%s][/%s] like format example, nothing else.", TAG, TAG);


    // Action Context
    private String schema;  // raw/json/markdown
    private String context;  // all necessary context info
    private LLM llm;    // LLM with ask interface
    private Map<String, ActionNode> children;

    // Action Input
    private String key;     // node key/name
    private Function<Object, Object> func;  // associated function or LLM call
    private Map<String, Type> params;       // parameter types
    private Type expectedType;              // expected return type
    private String instruction;             // instructions to follow
    private Object example;                 // example for in-context learning

    // Action Output
    private String content;
    private Object instructContent;

    // For ActionGraph
    private List<ActionNode> prevs;  // previous nodes
    private List<ActionNode> nexts;  // next nodes

    // Review and Revise modes
    private ReviewMode reviewMode;
    private ReviseMode reviseMode;
    private FillMode fillMode;

    //输入的json格式
    private JsonElement input;

    //输出的json格式
    private JsonElement output;

    private List<Expr> exprs = new ArrayList<>();

    private Role role;


    public ActionNode(String key, Type expectedType, String instruction) {
        this(key, expectedType, instruction, "", null);
    }

    public ActionNode(String key, Type expectedType, String instruction, Role role) {
        this(key, expectedType, instruction, "", role);
    }


    public ActionNode(String key, Type expectedType, String instruction, String context, Role role) {
        this.key = key;
        this.role = role;
        this.expectedType = expectedType;
        this.instruction = instruction;
        this.content = context;
        this.children = new HashMap<>();
        this.prevs = new ArrayList<>();
        this.nexts = new ArrayList<>();
        this.reviewMode = ReviewMode.AUTO;
        this.reviseMode = ReviseMode.AUTO;
        this.fillMode = FillMode.SINGLE_FILL;
    }

    private LLM llm() {
        if (this.llm != null) {
            return this.llm;
        }
        return this.role.getLlm();
    }

    public CompletableFuture<Message> run() {
        log.info("execute action node:{}", this.key);
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 1. Generate content using LLM
                String prompt = generatePrompt();
                log.info("execute action:{} \nprompt:\n{}", this.key, prompt);
                String rawContent = llm().ask(prompt).join();

                // 2. Extract and validate content
                content = extractContent(rawContent);
                validateContent();

                JsonElement jt = JsonUtils.gson.toJsonTree(content);
                if (jt.isJsonPrimitive()) {
                    this.output = new JsonObject();
                    ((JsonObject) this.output).add("data", jt);
                }
                if (jt.isJsonArray()) {
                    this.output = jt;
                }
                if (jt.isJsonObject()) {
                    this.output = jt;
                }

                log.info("execute action node:{} res:\n{}", this.key, content);

                return Message.builder()
                        .content(content)
                        .instructContent(Joiner.on(":").join("user", instruction))
                        .role("ActionNode")
                        .causeBy(this.getClass().getName())
                        .build();

            } catch (Exception e) {
                log.error("Error running ActionNode: {}", e.getMessage());
                throw new RuntimeException("ActionNode execution failed", e);
            }
        });
    }

    private String generatePrompt() {
        StringBuilder prompt = new StringBuilder();
        prompt.append("## context\n").append(context).append("\n\n");
        prompt.append("-----\n\n");
        prompt.append("## format example\n").append(example == null ? "[CONTENT]$result[/CONTENT]" : example).append("\n\n");
        prompt.append("## instruction\n").append(instruction).append("\n\n");
        prompt.append("## constraint\n").append(LANGUAGE_CONSTRAINT).append("\n").append(FORMAT_CONSTRAINT).append("\n\n");
        prompt.append("## action\n").append("Follow instructions of nodes, generate output and make sure it follows the format example.");
        return prompt.toString();
    }

    private String extractContent(String rawContent) {
        String pattern = String.format("\\[%s\\](.*?)\\[/%s\\]", TAG, TAG);
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern, java.util.regex.Pattern.DOTALL);
        java.util.regex.Matcher m = r.matcher(rawContent);
        if (m.find()) {
            return m.group(1).trim();
        }
        throw new RuntimeException("Content format error");
    }

    private void validateContent() {
        if (StringUtils.isEmpty(content)) {
            throw new RuntimeException("Empty content");
        }
    }


    private CompletableFuture<String> revise() {
        // Implement revision logic based on reviseMode
        return CompletableFuture.completedFuture(content);
    }

    public void addNext(ActionNode node) {
        if (!nexts.contains(node)) {
            nexts.add(node);
            node.getPrevs().add(this);
        }
    }

    public void addPrev(ActionNode node) {
        if (!prevs.contains(node)) {
            prevs.add(node);
            node.getNexts().add(this);
        }
    }

    public void removeNext(ActionNode node) {
        nexts.remove(node);
        node.getPrevs().remove(this);
    }

    public void removePrev(ActionNode node) {
        prevs.remove(node);

        node.getNexts().remove(this);
    }

    /**
     * Extracts a value from input or output JsonElement using the given expression.
     *
     * @param isInput    true if extracting from input, false if extracting from output
     * @param expression The expression to navigate the JSON structure (e.g., "["friends"][0].name")
     * @return The extracted JsonElement, or null if not found
     */
    public JsonElement extractValue(boolean isInput, String expression) {
        JsonElement targetJson = isInput ? this.input : this.output;
        if (targetJson == null) {
            return null;
        }
        return JsonUtils.extractValue(targetJson, expression);
    }

}

enum ReviewMode {
    HUMAN,
    AUTO
}

enum ReviseMode {
    HUMAN,          // human revise
    HUMAN_REVIEW,   // human-review and auto-revise
    AUTO            // auto-review and auto-revise
}

enum FillMode {
    CODE_FILL,
    XML_FILL,
    SINGLE_FILL
} 