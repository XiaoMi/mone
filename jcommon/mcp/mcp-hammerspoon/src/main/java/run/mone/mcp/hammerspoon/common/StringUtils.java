package run.mone.mcp.hammerspoon.common;

/**
 * 字符串处理工具类
 * 
 * @author shanwb
 * @date 2025-02-08
 */
public class StringUtils {
    
    /**
     * 转义字符串，用于Lua代码中的字符串参数
     * 
     * @param input 需要转义的输入字符串
     * @return 转义后的字符串
     */
    public static String escapeWxUserName(String input) {
        if (input == null) return "";
        String replace = input.replace("'", "\\'")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
        if(replace.endsWith("@微信")){
            replace = replace.replace("@微信", "");
        }
        return replace.trim();
    }
} 