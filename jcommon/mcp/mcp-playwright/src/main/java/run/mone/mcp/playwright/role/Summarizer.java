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
                用户提出想总结当前页面内容时调用此工具
                以下是具体的购物步骤：
                1.向chrome发送请求当前页面内容的申请(GetContentAction)
                2.总结页面内容后就可以结束了(attempt_completion)
                """;

    }
}
