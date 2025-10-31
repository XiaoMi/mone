package run.mone.moner.server.role.tool;

import run.mone.hive.roles.tool.ITool;

public class ProcessActionTool implements ITool {

    @Override
    public String getName() {
        return "ProcessAction";
    }

    @Override
    public String description() {
        return "流程TOOL：当你不能解决问题时提出追问；当任务全部结束时返回最终结果。";
    }

    @Override
    public String parameters() {
        return """
                - ask_followup_question: (use when blocked) Provide a clarifying question
                - attempt_completion: (use when done) Provide final result and optional command
                """;
    }

    @Override
    public String usage() {
        return """
                <ask_followup_question>
                <question>Your question here</question>
                </ask_followup_question>
                
                <attempt_completion>
                <result>
                Your final result description here
                </result>
                <command>Command to demonstrate result (optional)</command>
                </attempt_completion>
                """;
    }
}

