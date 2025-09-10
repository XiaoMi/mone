package run.mone.hive.task;

/**
 * 压缩命令（smol/compact）
 * 对应Cline中的condense命令
 */
public class CompactCommand implements SlashCommand {
    
    @Override
    public String getName() {
        return "compact"; // 也支持smol别名
    }
    
    @Override
    public String getDescription() {
        return "Creates a detailed summary of the conversation to compact the context window";
    }
    
    @Override
    public boolean matches(String input) {
        String trimmed = input.trim();
        return trimmed.startsWith("/compact") || trimmed.startsWith("/smol");
    }
    
    @Override
    public String execute(String input, FocusChainSettings focusChainSettings) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("<explicit_instructions type=\"condense\">\n");
        prompt.append("The user has explicitly asked you to create a detailed summary of the conversation so far, ");
        prompt.append("which will be used to compact the current context window while retaining key information. ");
        prompt.append("The user may have provided instructions or additional information for you to consider when summarizing the conversation.\n");
        prompt.append("Irrespective of whether additional information or instructions are given, you are only allowed ");
        prompt.append("to respond to this message by calling the condense tool.\n\n");
        
        prompt.append("The condense tool is defined below:\n\n");
        prompt.append("Description:\n");
        prompt.append("Your task is to create a detailed summary of the conversation so far, paying close attention ");
        prompt.append("to the user's explicit requests and your previous actions. This summary should be thorough ");
        prompt.append("in capturing technical details, code patterns, and architectural decisions that would be ");
        prompt.append("essential for continuing with the conversation and supporting any continuing tasks.\n");
        prompt.append("The user will be presented with a preview of your generated summary and can choose to use it ");
        prompt.append("to compact their context window or keep chatting in the current conversation.\n");
        prompt.append("Users may refer to this tool as 'smol' or 'compact' as well. You should consider these to be ");
        prompt.append("equivalent to 'condense' when used in a similar context.\n\n");
        
        prompt.append("Parameters:\n");
        prompt.append("- Context: (required) The context to continue the conversation with. If applicable based on the current task, this should include:\n");
        prompt.append("  1. Previous Conversation: High level details about what was discussed throughout the entire conversation with the user.\n");
        prompt.append("  2. Current Work: Describe in detail what was being worked on prior to this request to compact the context window.\n");
        prompt.append("  3. Key Technical Concepts: List all important technical concepts, technologies, coding conventions, and frameworks discussed.\n");
        prompt.append("  4. Relevant Files and Code: If applicable, enumerate specific files and code sections examined, modified, or created for the task continuation.\n");
        prompt.append("  5. Problem Solving: Document problems solved thus far and any ongoing troubleshooting efforts.\n");
        prompt.append("  6. Pending Tasks and Next Steps: Outline all pending tasks and list the next steps for all outstanding work.\n");
        
        if (focusChainSettings != null && focusChainSettings.isEnabled()) {
            prompt.append("- task_progress: (required) The current state of the task_progress list, with completed items marked.\n");
        }
        
        prompt.append("</explicit_instructions>\n");
        
        return prompt.toString();
    }
}
