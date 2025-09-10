package run.mone.hive.task;

/**
 * 新任务命令
 * 对应Cline中的newtask命令
 */
public class NewTaskCommand implements SlashCommand {
    
    @Override
    public String getName() {
        return "newtask";
    }
    
    @Override
    public String getDescription() {
        return "Creates a new task with the provided context";
    }
    
    @Override
    public boolean matches(String input) {
        return input.trim().startsWith("/newtask");
    }
    
    @Override
    public String execute(String input, FocusChainSettings focusChainSettings) {
        return "<explicit_instructions type=\"new_task\">\n" +
               "The user wants to create a new task. You should use the new_task tool to create a new task " +
               "with the context they provided. The new task will replace the current conversation.\n" +
               "</explicit_instructions>\n";
    }
}
