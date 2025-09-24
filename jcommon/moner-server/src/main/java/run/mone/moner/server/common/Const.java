package run.mone.moner.server.common;

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

    public static final String ROLE = "_role_";

    public static final String ROLE_EXIT = "___exit___";
}
