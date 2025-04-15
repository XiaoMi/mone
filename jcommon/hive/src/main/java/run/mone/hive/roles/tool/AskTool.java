package run.mone.hive.roles.tool;

/**
 * @author goodjava@qq.com
 * @date 2025/4/9 11:09
 */
public class AskTool implements ITool{

    @Override
    public boolean completed() {
        return true;
    }

    @Override
    public String getName() {
        return "ask_followup_question";
    }

    @Override
    public String description() {
        return "Ask the user a question to gather additional information needed to complete the task. This tool should be used when you encounter ambiguities, need clarification, or require more details to proceed effectively. It allows for interactive problem-solving by enabling direct communication with the user. Use this tool judiciously to maintain a balance between gathering necessary information and avoiding excessive back-and-forth.If you notice that the recent communications haven't resolved the issue, you should also consider using this tool.";
    }

    @Override
    public String parameters() {
        return """
                - question: (required) The question to ask the user. This should be a clear, specific question that addresses the information you need.
               """;
    }

    @Override
    public String usage() {
        return """
            <ask_followup_question>
            <question>Your question here</question>
            </ask_followup_question>
            """;
    }
}
