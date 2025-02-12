package run.mone.mcp.playwright.role;

import run.mone.hive.roles.Role;

/**
 * @author goodjava@qq.com
 * @date 2025/2/12 11:47
 */
public class Summarizer extends Role {

    public Summarizer() {

        super("Summarizer_Chrome","总结当前页面内容");
        this.goal = """
                用户提出想总结当前页面内容时使用此角色定义的工具链
                使用Tool的步骤：
                1.向chrome发送请求当前页面内容的申请(GetContentAction)
                2.总结页面内容(attempt_completion)
                """;

    }
}
