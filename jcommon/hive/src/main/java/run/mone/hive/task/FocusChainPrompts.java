package run.mone.hive.task;

/**
 * Focus Chain相关的提示词模板
 * 对应Cline中的各种prompt
 */
public class FocusChainPrompts {
    
    /**
     * 自动TODO列表管理的系统提示
     */
    public static final String TODO_LIST_TEMPLATE_TEXT = 
        "AUTOMATIC TODO LIST MANAGEMENT\n\n" +
        "The system automatically manages todo lists to help track task progress:\n\n" +
        "- Every 10th API request, you will be prompted to review and update the current todo list if one exists\n" +
        "- When switching from PLAN MODE to ACT MODE, you should create a comprehensive todo list for the task\n" +
        "- Todo list updates should be done silently using the task_progress parameter - do not announce these updates to the user\n" +
        "- Use standard Markdown checklist format: \"- [ ]\" for incomplete items and \"- [x]\" for completed items\n" +
        "- The system will automatically include todo list context in your prompts when appropriate\n" +
        "- Focus on creating actionable, meaningful steps rather than granular technical details";
    
    /**
     * Plan到Act模式切换时的强制创建指令
     */
    public static String getListInstructionsInitial() {
        return "\n# TODO LIST CREATION REQUIRED - ACT MODE ACTIVATED\n\n" +
               "**You've just switched from PLAN MODE to ACT MODE!**\n\n" +
               "**IMMEDIATE ACTION REQUIRED:**\n" +
               "1. Create a comprehensive todo list in your NEXT tool call\n" +
               "2. Use the task_progress parameter to provide the list\n" +
               "3. Format each item using markdown checklist syntax:\n" +
               "\t- [ ] For tasks to be done\n" +
               "\t- [x] For any tasks already completed\n\n" +
               "**Your todo list should include:**\n" +
               "   - All major implementation steps\n" +
               "   - Testing and validation tasks\n" +
               "   - Documentation updates if needed\n" +
               "   - Final verification steps\n\n" +
               "**Example format:**\n" +
               "   - [ ] Set up project structure\n" +
               "   - [ ] Implement core functionality\n" +
               "   - [ ] Add error handling\n" +
               "   - [ ] Write tests\n" +
               "   - [ ] Test implementation\n" +
               "   - [ ] Document changes\n\n" +
               "**Remember:** Keeping the todo list updated helps track progress and ensures nothing is missed.";
    }
    
    /**
     * 推荐但不强制要求的列表指令
     */
    public static String getListInstructionsRecommended() {
        return "\n# TODO LIST UPDATE RECOMMENDED\n\n" +
               "It's been a while since the todo list was last updated. Consider reviewing and updating your progress:\n\n" +
               "**If you have a current todo list:**\n" +
               "- Review completed items and mark them with [x]\n" +
               "- Add any new tasks that have emerged\n" +
               "- Update the task_progress parameter with the current state\n\n" +
               "**If you don't have a todo list yet:**\n" +
               "- Consider creating one to help track progress\n" +
               "- Use the task_progress parameter to provide the list\n" +
               "- Focus on key remaining tasks and milestones\n\n" +
               "**Format:** Use standard Markdown checklist syntax (- [ ] for incomplete, - [x] for completed)";
    }
    
    /**
     * Plan模式下的指令
     */
    public static String getListInstructionsPlanMode() {
        return "\n# PLAN MODE - TODO LIST PLANNING\n\n" +
               "You are currently in PLAN MODE. Focus on:\n\n" +
               "**Planning Activities:**\n" +
               "- Analyze the user's request thoroughly\n" +
               "- Break down complex tasks into manageable steps\n" +
               "- Consider dependencies and prerequisites\n" +
               "- Create or refine the todo list structure\n\n" +
               "**Todo List Guidelines:**\n" +
               "- Use task_progress parameter to update the planning checklist\n" +
               "- Focus on high-level planning steps\n" +
               "- Prepare for transition to ACT MODE\n" +
               "- Ensure all major components are identified";
    }
    
    /**
     * 上下文总结时保持任务进度的指令
     */
    public static String getSummarizeTaskProgressInstructions() {
        return "Updating task progress:\n" +
               "There is an optional task_progress parameter which you should use to provide an updated checklist " +
               "to keep the user informed of the latest state of the progress for this task. You should always return " +
               "the most up to date version of the checklist if there is already an existing checklist. If no " +
               "task_progress list was included in the previous context, you should NOT create a new task_progress " +
               "list - do not return a new task_progress list if one does not already exist.";
    }
    
    /**
     * 获取带有Focus Chain支持的总结任务提示
     */
    public static String getSummarizeTaskPrompt(boolean focusChainEnabled) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("<explicit_instructions type=\"summarize_task\">\n");
        prompt.append("The current conversation is rapidly running out of context. Now, your urgent task is to create a comprehensive detailed summary of the conversation so far, paying close attention to the user's explicit requests and your previous actions.\n");
        prompt.append("This summary should be thorough in capturing technical details, code patterns, and architectural decisions that would be essential for continuing development work without losing context.\n\n");
        
        prompt.append("You have only two options: If you are immediately prepared to call the attempt_completion tool, and have completed all items in your task_progress list, you may call attempt_completion at this time. If you are not prepared to call the attempt_completion tool, and have not completed all items in your task_progress list, you must call the summarize_task tool.\n\n");
        
        prompt.append("You MUST ONLY respond to this message by using either the attempt_completion tool or the summarize_task tool call.\n\n");
        
        if (focusChainEnabled) {
            prompt.append(getSummarizeTaskProgressInstructions()).append("\n\n");
        }
        
        prompt.append("Usage:\n");
        prompt.append("<summarize_task>\n");
        prompt.append("<context>Your detailed summary</context>\n");
        if (focusChainEnabled) {
            prompt.append("<task_progress>task_progress list here</task_progress>\n");
        }
        prompt.append("</summarize_task>\n");
        prompt.append("</explicit_instructions>");
        
        return prompt.toString();
    }
    
    /**
     * 生成继续提示
     */
    public static String getContinuationPrompt(String summaryText) {
        return "Based on the summary above, continue with the task. Focus on the next steps outlined in the summary and maintain the same approach and context established in the previous conversation.";
    }
}
