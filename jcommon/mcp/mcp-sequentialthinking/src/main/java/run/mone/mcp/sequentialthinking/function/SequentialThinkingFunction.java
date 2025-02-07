package run.mone.mcp.sequentialthinking.function;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.sequentialthinking.model.ProcessResult;
import run.mone.mcp.sequentialthinking.model.ThoughtData;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.fasterxml.jackson.databind.ObjectMapper;

@Data
@Slf4j
public class SequentialThinkingFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private String name = "sequentialthinking";

    private String desc = """
            A detailed tool for dynamic and reflective problem-solving through thoughts.
            This tool helps analyze problems through a flexible thinking process that can adapt and evolve.
            Each thought can build on, question, or revise previous insights as understanding deepens.

            When to use this tool:
            - Breaking down complex problems into steps
            - Planning and design with room for revision
            - Analysis that might need course correction
            - Problems where the full scope might not be clear initially
            - Problems that require a multi-step solution
            - Tasks that need to maintain context over multiple steps
            - Situations where irrelevant information needs to be filtered out

            Key features:
            - You can adjust total_thoughts up or down as you progress
            - You can question or revise previous thoughts
            - You can add more thoughts even after reaching what seemed like the end
            - You can express uncertainty and explore alternative approaches
            - Not every thought needs to build linearly - you can branch or backtrack
            - Generates a solution hypothesis
            - Verifies the hypothesis based on the Chain of Thought steps
            - Repeats the process until satisfied
            - Provides a correct answer

            Parameters explained:
            - thought: Your current thinking step, which can include:
            * Regular analytical steps
            * Revisions of previous thoughts
            * Questions about previous decisions
            * Realizations about needing more analysis
            * Changes in approach
            * Hypothesis generation
            * Hypothesis verification
            - next_thought_needed: True if you need more thinking, even if at what seemed like the end
            - thought_number: Current number in sequence (can go beyond initial total if needed)
            - total_thoughts: Current estimate of thoughts needed (can be adjusted up/down)
            - is_revision: A boolean indicating if this thought revises previous thinking
            - revises_thought: If is_revision is true, which thought number is being reconsidered
            - branch_from_thought: If branching, which thought number is the branching point
            - branch_id: Identifier for the current branch (if any)
            - needs_more_thoughts: If reaching end but realizing more thoughts needed

            You should:
            1. Start with an initial estimate of needed thoughts, but be ready to adjust
            2. Feel free to question or revise previous thoughts
            3. Don't hesitate to add more thoughts if needed, even at the "end"
            4. Express uncertainty when present
            5. Mark thoughts that revise previous thinking or branch into new paths
            6. Ignore information that is irrelevant to the current step
            7. Generate a solution hypothesis when appropriate
            8. Verify the hypothesis based on the Chain of Thought steps
            9. Repeat the process until satisfied with the solution
            10. Provide a single, ideally correct answer as the final output
            11. Only set next_thought_needed to false when truly done and a satisfactory answer is reached
            """;

    private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "thought": {
                        "type": "string",
                        "description": "Your current thinking step"
                    },
                    "nextThoughtNeeded": {
                        "type": "boolean",
                        "description": "Whether another thought step is needed"
                    },
                    "thoughtNumber": {
                        "type": "integer",
                        "description": "Current thought number",
                        "minimum": 1
                    },
                    "totalThoughts": {
                        "type": "integer",
                        "description": "Estimated total thoughts needed",
                        "minimum": 1
                    },
                    "isRevision": {
                        "type": "boolean",
                        "description": "Whether this revises previous thinking"
                    },
                    "revisesThought": {
                        "type": "integer",
                        "description": "Which thought is being reconsidered",
                        "minimum": 1
                    },
                    "branchFromThought": {
                        "type": "integer",
                        "description": "Branching point thought number",
                        "minimum": 1
                    },
                    "branchId": {
                        "type": "string",
                        "description": "Branch identifier"
                    },
                    "needsMoreThoughts": {
                        "type": "boolean",
                        "description": "If more thoughts are needed"
                    }
                },
                "required": ["thought", "nextThoughtNeeded", "thoughtNumber", "totalThoughts"]
            }
            """;


    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> arguments) {
        try {
            String thought = arguments.get("thought").toString();
            Integer thoughtNumber = (Integer) arguments.get("thoughtNumber");
            Integer totalThoughts = (Integer) arguments.get("totalThoughts");
            Boolean nextThoughtNeeded = (Boolean) arguments.get("nextThoughtNeeded");

            Boolean isRevision = arguments.get("isRevision") != null ? (Boolean) arguments.get("isRevision") : null;
            Integer revisesThought = arguments.get("revisesThought") != null ? (Integer) arguments.get("revisesThought") : null;
            Integer branchFromThought = arguments.get("branchFromThought") != null ? (Integer) arguments.get("branchFromThought") : null;
            String branchId = arguments.get("branchId") != null ? (String) arguments.get("branchId") : null;
            Boolean needsMoreThoughts = arguments.get("needsMoreThoughts") != null ? (Boolean) arguments.get("needsMoreThoughts") : null;

            ThoughtData thoughtData = ThoughtData.builder()
                .thought(thought)
                .thoughtNumber(thoughtNumber)
                .totalThoughts(totalThoughts)
                .nextThoughtNeeded(nextThoughtNeeded)
                .isRevision(isRevision)
                .revisesThought(revisesThought)
                .branchFromThought(branchFromThought)
                .branchId(branchId)
                .needsMoreThoughts(needsMoreThoughts)
                .build();

            ProcessResult result = processThought(thoughtData);

            return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent(new ObjectMapper().writeValueAsString(result))),
                false
            );
        } catch (Exception e) {
            log.error("Failed to process thought", e);
            return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("Failed to process thought: " + e.getMessage())),
                true
            );
        }
    }

    private final List<ThoughtData> thoughtHistory = new ArrayList<>();
    private final Map<String, List<ThoughtData>> branches = new HashMap<>();
    
    // ANSI颜色代码
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RESET = "\u001B[0m";

    public ProcessResult processThought(ThoughtData input) {
        try {
            validateThoughtData(input);
            
            // 调整总思维数如果需要
            if (input.getThoughtNumber() > input.getTotalThoughts()) {
                input.setTotalThoughts(input.getThoughtNumber());
            }

            // 添加到历史记录
            thoughtHistory.add(input);

            // 处理分支
            if (input.getBranchFromThought() != null && input.getBranchId() != null) {
                branches.computeIfAbsent(input.getBranchId(), k -> new ArrayList<>())
                       .add(input);
            }

            // 格式化并输出思维
            String formattedThought = formatThought(input);
            log.info(formattedThought);

            // 返回处理结果
            return ProcessResult.builder()
                    .thoughtNumber(input.getThoughtNumber())
                    .totalThoughts(input.getTotalThoughts())
                    .nextThoughtNeeded(input.isNextThoughtNeeded())
                    .branches(new ArrayList<>(branches.keySet()))
                    .thoughtHistoryLength(thoughtHistory.size())
                    .formattedThought(formattedThought)
                    .build();

        } catch (Exception e) {
            return ProcessResult.builder()
                    .error(e.getMessage())
                    .status("failed")
                    .build();
        }
    }

    private void validateThoughtData(ThoughtData input) {
        if (input.getThought() == null || input.getThought().trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid thought: must not be empty");
        }
        if (input.getThoughtNumber() < 1) {
            throw new IllegalArgumentException("Invalid thoughtNumber: must be positive");
        }
        if (input.getTotalThoughts() < 1) {
            throw new IllegalArgumentException("Invalid totalThoughts: must be positive");
        }
    }

    private String formatThought(ThoughtData thoughtData) {
        String prefix;
        String context = "";

        // 确定前缀和上下文
        if (Boolean.TRUE.equals(thoughtData.getIsRevision())) {
            prefix = ANSI_YELLOW + "🔄 Revision" + ANSI_RESET;
            context = String.format(" (revising thought %d)", thoughtData.getRevisesThought());
        } else if (thoughtData.getBranchFromThought() != null) {
            prefix = ANSI_GREEN + "🌿 Branch" + ANSI_RESET;
            context = String.format(" (from thought %d, ID: %s)", 
                thoughtData.getBranchFromThought(), thoughtData.getBranchId());
        } else {
            prefix = ANSI_BLUE + "💭 Thought" + ANSI_RESET;
        }

        String header = String.format("%s %d/%d%s", 
            prefix, thoughtData.getThoughtNumber(), thoughtData.getTotalThoughts(), context);
        
        // 创建边框
        int maxLength = Math.max(header.length(), thoughtData.getThought().length()) + 4;
        String border = "─".repeat(maxLength);

        return String.format("\n┌%s┐\n│ %s │\n├%s┤\n│ %-" + maxLength + "s │\n└%s┘",
            border, header, border, thoughtData.getThought(), border);
    }

}