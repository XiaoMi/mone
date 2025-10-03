package run.mone.hive.task;

/**
 * 斜杠命令测试类
 * 用于验证/init命令是否正确集成
 */
public class SlashCommandTest {
    
    public static void main(String[] args) {
        // 测试斜杠命令解析器
        SlashCommandParser parser = new SlashCommandParser();
        FocusChainSettings settings = new FocusChainSettings();
        
        // 测试/init命令
        String testInput = "/init";
        SlashCommandParser.ParseResult result = parser.parseSlashCommands(testInput, settings);
        
        System.out.println("原始输入: " + testInput);
        System.out.println("解析结果: " + result.getProcessedText());
        System.out.println("是否包含init指令: " + result.getProcessedText().contains("explicit_instructions type=\"init\""));
        
        // 测试普通输入
        String normalInput = "请帮我分析这个项目";
        SlashCommandParser.ParseResult normalResult = parser.parseSlashCommands(normalInput, settings);
        
        System.out.println("\n普通输入: " + normalInput);
        System.out.println("解析结果: " + normalResult.getProcessedText());
        System.out.println("是否保持不变: " + normalResult.getProcessedText().equals(normalInput));
    }
}