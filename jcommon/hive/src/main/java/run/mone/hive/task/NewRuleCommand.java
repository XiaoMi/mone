package run.mone.hive.task;

/**
 * 新规则命令
 * 对应Cline中的newrule命令
 */
public class NewRuleCommand implements SlashCommand {
    
    @Override
    public String getName() {
        return "newrule";
    }
    
    @Override
    public String getDescription() {
        return "Creates a new rule in the .clinerules directory";
    }
    
    @Override
    public boolean matches(String input) {
        return input.trim().startsWith("/newrule");
    }
    
    @Override
    public String execute(String input, FocusChainSettings focusChainSettings) {
        return "<explicit_instructions type=\"new_rule\">\n" +
               "The user wants to create a new rule. You should use the new_rule tool to create a new rule file " +
               "in the .clinerules directory with the content they provided. This will help customize Cline's behavior " +
               "for their specific project or preferences.\n" +
               "</explicit_instructions>\n";
    }
}
