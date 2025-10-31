package run.mone.hive.task;

/**
 * 斜杠命令接口
 * 对应Cline中的SlashCommand概念
 */
public interface SlashCommand {
    
    /**
     * 获取命令名称
     */
    String getName();
    
    /**
     * 获取命令描述
     */
    String getDescription();
    
    /**
     * 执行命令并返回处理后的提示词
     * @param input 用户输入的完整文本
     * @param focusChainSettings Focus Chain设置
     * @return 处理后的提示词
     */
    String execute(String input, FocusChainSettings focusChainSettings);
    
    /**
     * 检查是否匹配该命令
     * @param input 用户输入
     * @return 是否匹配
     */
    boolean matches(String input);
}
