package run.mone.mcp.playwright.common;

/**
 * @author goodjava@qq.com
 * @date 2025/2/8 11:06
 */
public class Const {


    public static final String pause = """
            
            <action type="pause">
            </action>
            
            """;

    public static final String actionTemplate = """
            <action type="%s">
            %s
            </action>
            """;

}
