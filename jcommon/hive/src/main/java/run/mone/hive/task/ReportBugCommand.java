package run.mone.hive.task;

/**
 * 报告错误命令
 * 对应Cline中的reportbug命令
 */
public class ReportBugCommand implements SlashCommand {
    
    @Override
    public String getName() {
        return "reportbug";
    }
    
    @Override
    public String getDescription() {
        return "Reports a bug or issue with detailed context information";
    }
    
    @Override
    public boolean matches(String input) {
        return input.trim().startsWith("/reportbug");
    }
    
    @Override
    public String execute(String input, FocusChainSettings focusChainSettings) {
        return "<explicit_instructions type=\"report_bug\">\n" +
               "The user wants to report a bug or issue. You should gather relevant information about the problem " +
               "including error messages, steps to reproduce, expected vs actual behavior, and system context. " +
               "Help them create a comprehensive bug report that can be used to investigate and resolve the issue.\n" +
               "</explicit_instructions>\n";
    }
}
