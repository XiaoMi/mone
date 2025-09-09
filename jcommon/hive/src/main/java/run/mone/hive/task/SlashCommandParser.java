package run.mone.hive.task;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 斜杠命令解析器
 * 对应Cline中的parseSlashCommands函数
 */
public class SlashCommandParser {
    
    // 支持的默认命令
    private static final Set<String> SUPPORTED_DEFAULT_COMMANDS = Set.of(
        "newtask", "smol", "compact", "newrule", "reportbug", "deep-planning"
    );
    
    // 注册的命令
    private final Map<String, SlashCommand> registeredCommands = new HashMap<>();
    
    // 标签模式，用于匹配XML标签内的命令
    private final List<TagPattern> tagPatterns = Arrays.asList(
        new TagPattern("task", Pattern.compile("<task>(\\s*/([a-zA-Z0-9_.-]+))(\\s+.+?)?\\s*</task>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL)),
        new TagPattern("feedback", Pattern.compile("<feedback>(\\s*/([a-zA-Z0-9_.-]+))(\\s+.+?)?\\s*</feedback>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL)),
        new TagPattern("answer", Pattern.compile("<answer>(\\s*/([a-zA-Z0-9_.-]+))(\\s+.+?)?\\s*</answer>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL)),
        new TagPattern("user_message", Pattern.compile("<user_message>(\\s*/([a-zA-Z0-9_.-]+))(\\s+.+?)?\\s*</user_message>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL))
    );
    
    public SlashCommandParser() {
        // 注册默认命令
        registerDefaultCommands();
    }
    
    /**
     * 注册默认命令
     */
    private void registerDefaultCommands() {
        registerCommand(new DeepPlanningCommand());
        registerCommand(new NewTaskCommand());
        registerCommand(new CompactCommand());
        registerCommand(new NewRuleCommand());
        registerCommand(new ReportBugCommand());
    }
    
    /**
     * 注册命令
     */
    public void registerCommand(SlashCommand command) {
        registeredCommands.put(command.getName(), command);
    }
    
    /**
     * 解析斜杠命令
     * @param text 输入文本
     * @param focusChainSettings Focus Chain设置
     * @return 解析结果
     */
    public ParseResult parseSlashCommands(String text, FocusChainSettings focusChainSettings) {
        // 检查每个标签模式
        for (TagPattern tagPattern : tagPatterns) {
            Matcher matcher = tagPattern.pattern.matcher(text);
            
            if (matcher.find()) {
                // match.group(1) 是带有前导空格的命令 (例如 " /newtask")
                // match.group(2) 是命令名称 (例如 "newtask")
                String commandName = matcher.group(2);
                
                // 优先处理默认命令
                if (SUPPORTED_DEFAULT_COMMANDS.contains(commandName)) {
                    SlashCommand command = registeredCommands.get(commandName);
                    if (command != null) {
                        int fullMatchStartIndex = matcher.start();
                        
                        // 找到完整匹配中斜杠命令的位置
                        String fullMatch = matcher.group(0);
                        String commandWithWhitespace = matcher.group(1);
                        int relativeStartIndex = fullMatch.indexOf(commandWithWhitespace);
                        
                        // 计算原始字符串中的绝对索引
                        int slashCommandStartIndex = fullMatchStartIndex + relativeStartIndex;
                        int slashCommandEndIndex = slashCommandStartIndex + commandWithWhitespace.length();
                        
                        // 移除斜杠命令并在消息顶部添加自定义指令
                        String textWithoutSlashCommand = text.substring(0, slashCommandStartIndex) + 
                                                       text.substring(slashCommandEndIndex);
                        String processedText = command.execute(text, focusChainSettings) + textWithoutSlashCommand;
                        
                        return new ParseResult(processedText, commandName.equals("newrule"));
                    }
                }
                
                // TODO: 处理本地和全局工作流程命令
                // 这里可以扩展处理用户自定义的工作流程命令
            }
        }
        
        // 没有找到命令，返回原始文本
        return new ParseResult(text, false);
    }
    
    /**
     * 标签模式类
     */
    private static class TagPattern {
        final String tag;
        final Pattern pattern;
        
        TagPattern(String tag, Pattern pattern) {
            this.tag = tag;
            this.pattern = pattern;
        }
    }
    
    /**
     * 解析结果类
     */
    public static class ParseResult {
        private final String processedText;
        private final boolean needsClinerulesFileCheck;
        
        public ParseResult(String processedText, boolean needsClinerulesFileCheck) {
            this.processedText = processedText;
            this.needsClinerulesFileCheck = needsClinerulesFileCheck;
        }
        
        public String getProcessedText() {
            return processedText;
        }
        
        public boolean needsClinerulesFileCheck() {
            return needsClinerulesFileCheck;
        }
    }
    
    /**
     * 获取所有注册的命令
     */
    public Map<String, SlashCommand> getRegisteredCommands() {
        return new HashMap<>(registeredCommands);
    }
}
