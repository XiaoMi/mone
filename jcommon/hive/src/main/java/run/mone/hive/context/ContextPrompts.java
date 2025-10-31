package run.mone.hive.context;

import run.mone.hive.task.FocusChainSettings;

/**
 * 上下文管理相关的Prompt模板
 * 移植自Cline的contextManagement.ts
 */
public class ContextPrompts {
    
    /**
     * 生成任务总结的prompt
     * 对应Cline中的summarizeTask函数
     */
    public static String summarizeTask(FocusChainSettings focusChainSettings) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("<explicit_instructions type=\"summarize_task\">\n");
        prompt.append("The current conversation is rapidly running out of context. Now, your urgent task is to create a comprehensive detailed summary of the conversation so far, paying close attention to the user's explicit requests and your previous actions.\n");
        prompt.append("This summary should be thorough in capturing technical details, code patterns, and architectural decisions that would be essential for continuing development work without losing context.\n\n");
        
        prompt.append("You have only two options: If you are immediately prepared to call the attempt_completion tool, and have completed all items in your task_progress list, you may call attempt_completion at this time. If you are not prepared to call the attempt_completion tool, and have not completed all items in your task_progress list, you must call the summarize_task tool.\n\n");
        
        prompt.append("You MUST ONLY respond to this message by using either the attempt_completion tool or the summarize_task tool call.\n\n");
        
        prompt.append("When responding with the summarize_task tool call, follow these instructions:\n\n");
        
        prompt.append("Before providing your final summary, wrap your analysis in <thinking> tags to organize your thoughts and ensure you've covered all necessary points. In your analysis process:\n");
        prompt.append("1. Chronologically analyze each message and section of the conversation. For each section thoroughly identify:\n");
        prompt.append("   - The user's explicit requests and intents\n");
        prompt.append("   - Your approach to addressing the user's requests\n");
        prompt.append("   - Key decisions, technical concepts and code patterns\n");
        prompt.append("   - Specific details like file names, full code snippets, function signatures, file edits, etc\n");
        prompt.append("2. Double-check for technical accuracy and completeness, addressing each required element thoroughly.\n\n");
        
        prompt.append("Your summary should include the following sections:\n");
        prompt.append("1. Primary Request and Intent: Capture all of the user's explicit requests and intents in detail\n");
        prompt.append("2. Key Technical Concepts: List all important technical concepts, technologies, and frameworks discussed.\n");
        prompt.append("3. Files and Code Sections: Enumerate specific files and code sections examined, modified, or created. Pay special attention to the most recent messages and include full code snippets where applicable and include a summary of why this file read or edit is important.\n");
        prompt.append("4. Problem Solving: Document problems solved and any ongoing troubleshooting efforts.\n");
        prompt.append("5. Pending Tasks: Outline any pending tasks that you have explicitly been asked to work on.\n");
        prompt.append("6. Task Evolution: If the user provided additional requests or modified the original task during the conversation, document this progression:\n");
        prompt.append("   - Task Modifications: [Chronological list of how the user redirected or modified the work since the original task]\n");
        prompt.append("   - Current Active Task: [What the user most recently asked to work on]\n");
        prompt.append("   - Context for Changes: [Why the task evolved - user feedback, new requirements, etc. (Include direct quotes from user messages that caused task changes to prevent drift after context compacting)]\n");
        prompt.append("7. Current Work: Describe in detail precisely what was being worked on immediately before this summary request, paying special attention to the most recent messages from both user and assistant. Include file names and code snippets where applicable.\n");
        prompt.append("8. Next Step: List the next step that you will take that is related to the most recent work you were doing. IMPORTANT: ensure that this step is DIRECTLY in line with the user's explicit requests, and the task you were working on immediately before this summary request. If your last task was concluded, then only list next steps if they are explicitly in line with the users request. Do not start on tangential requests without confirming with the user first.\n");
        prompt.append("                       If there is a next step, include direct quotes from the most recent conversation showing exactly what task you were working on and where you left off. This should be verbatim to ensure there's no drift in task interpretation.\n");
        prompt.append("9. You should pay special attention to the most recent user message, as it indicates the user's most recent intent.\n\n");
        
        // Focus Chain相关内容
        if (focusChainSettings != null && focusChainSettings.isEnabled()) {
            prompt.append("Updating task progress:\n");
            prompt.append("There is an optional task_progress parameter which you should use to provide an updated checklist to keep the user informed of the latest state of the progress for this task. You should always return the most up to date version of the checklist if there is already an existing checklist. If no task_progress list was included in the previous context, you should NOT create a new task_progress list - do not return a new task_progress list if one does not already exist.\n\n");
        }
        
        prompt.append("Usage:\n");
        prompt.append("<summarize_task>\n");
        prompt.append("<context>Your detailed summary</context>\n");
        if (focusChainSettings != null && focusChainSettings.isEnabled()) {
            prompt.append("<task_progress>task_progress list here</task_progress>\n");
        }
        prompt.append("</summarize_task>\n\n");
        
        prompt.append("Here's an example of how your output should be structured:\n\n");
        prompt.append("<example>\n");
        prompt.append("<thinking>\n");
        prompt.append("[Your thought process, ensuring all points are covered thoroughly and accurately]\n");
        prompt.append("</thinking>\n");
        prompt.append("<summarize_task>\n");
        prompt.append("<context>\n");
        prompt.append("1. Primary Request and Intent:\n");
        prompt.append("   [Detailed description]\n");
        prompt.append("2. Key Technical Concepts:\n");
        prompt.append("   - [Concept 1]\n");
        prompt.append("   - [Concept 2]\n");
        prompt.append("   - [...]\n");
        prompt.append("3. Files and Code Sections:\n");
        prompt.append("   - [File Name 1]\n");
        prompt.append("      - [Summary of why this file is important]\n");
        prompt.append("      - [Summary of the changes made to this file, if any]\n");
        prompt.append("      - [Important Code Snippet]\n");
        prompt.append("   - [File Name 2]\n");
        prompt.append("      - [Important Code Snippet]\n");
        prompt.append("   - [...]\n");
        prompt.append("4. Problem Solving:\n");
        prompt.append("   [Description of solved problems and ongoing troubleshooting]\n");
        prompt.append("5. Pending Tasks:\n");
        prompt.append("   - [Task 1]\n");
        prompt.append("   - [Task 2]\n");
        prompt.append("   - [...]\n");
        prompt.append("6. Current Work:\n");
        prompt.append("   [Precise description of current work]\n");
        prompt.append("7. Optional Next Step:\n");
        prompt.append("   [Optional Next step to take]\n");
        prompt.append("</context>\n");
        
        if (focusChainSettings != null && focusChainSettings.isEnabled()) {
            prompt.append("<task_progress>\n");
            prompt.append("- [x] Completed task example\n");
            prompt.append("- [x] Completed task example\n");
            prompt.append("- [ ] Remaining task example\n");
            prompt.append("- [ ] Remaining task example\n");
            prompt.append("</task_progress>\n");
        }
        
        prompt.append("</summarize_task>\n");
        prompt.append("</example>\n\n");
        prompt.append("</explicit_instructions>\n");
        
        return prompt.toString();
    }
    
    /**
     * 生成继续对话的prompt
     * 对应Cline中的continuationPrompt函数
     */
    public static String continuationPrompt(String summaryText) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("This session is being continued from a previous conversation that ran out of context. The conversation is summarized below:\n");
        prompt.append(summaryText).append(".\n\n");
        
        prompt.append("Please continue the conversation from where we left it off without asking the user any further questions. Continue with the last task that you were asked to work on. Pay special attention to the most recent user message when responding rather than the initial task message, if applicable.\n");
        prompt.append("If the most recent user's message starts with \"/newtask\", \"/smol\", \"/compact\", \"/newrule\", or \"/reportbug\", you should indicate to the user that they will need to run this command again.\n");
        
        return prompt.toString();
    }
    
    /**
     * 上下文截断通知
     */
    public static String contextTruncationNotice() {
        return "[NOTE] Some previous conversation history with the user has been removed to maintain optimal context window length. " +
               "The initial user task has been retained for continuity, while intermediate conversation history has been removed. " +
               "Keep this in mind as you continue assisting the user. Pay special attention to the user's latest messages.";
    }
    
    /**
     * 重复文件读取通知
     */
    public static String duplicateFileReadNotice() {
        return "[[NOTE] This file read has been removed to save space in the context window. " +
               "Refer to the latest file read for the most up to date version of this file.]";
    }
    
    /**
     * 处理第一条用户消息的截断
     */
    public static String processFirstUserMessageForTruncation(String originalContent) {
        final int MAX_CHARS = 400000;
        
        if (originalContent.length() <= MAX_CHARS) {
            return originalContent;
        }
        
        String truncated = originalContent.substring(0, MAX_CHARS);
        return truncated + "\n\n[[NOTE] This message was truncated past this point to preserve context window space.]";
    }
    
    /**
     * 压缩响应
     */
    public static String condenseResponse() {
        return "The user has accepted the condensed conversation summary you generated. This summary covers important details " +
               "of the historical conversation with the user which has been truncated.\n" +
               "<explicit_instructions type=\"condense_response\">" +
               "It's crucial that you respond by ONLY asking the user what you should work on next. You should NOT take any " +
               "initiative or make any assumptions about continuing with work. For example you should NOT suggest file changes " +
               "or attempt to read any files.\n" +
               "When asking the user what you should work on next, you can reference information in the summary which was just " +
               "generated. However, you should NOT reference information outside of what's contained in the summary for this response. " +
               "Keep this response CONCISE." +
               "</explicit_instructions>";
    }
}
