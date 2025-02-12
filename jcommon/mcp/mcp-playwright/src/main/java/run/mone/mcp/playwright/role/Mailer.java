package run.mone.mcp.playwright.role;

import run.mone.hive.roles.Role;

/**
 * @author goodjava@qq.com
 * @date 2025/2/12 10:09
 */
public class Mailer extends Role {

    public Mailer() {

        super("Mail_Chrome工具(查看邮件)","邮件查看者");
        this.goal = """
                如果用户需求描述中包含购物相关的关键词，例如"看下最新邮件"、"邮件"等，请使用购物相关的工具。
                以下是具体的购物步骤：
                1.创建qq邮箱首页tab(发现没有code的时候,必须调用这个接口)(OpenTabAction)
                2.点击收信 (OperationAction)
                3.刷新页面(RefreshAfterClick)
                3.邮件详情页:查看下信的邮件标题(如果找不到对应的按钮 滚动屏幕 ScrollAction)
                4.汇总内容(attempt_completion)
                """;
        // 目前用户新开tab页面的Action
        this.constraints = """
                "action": {
                    "type": "createNewTab",
                    "url": "https://mail.qq.com/",
                    "auto": "true",
                    "desc": "打开QQ邮箱"
                }
                """;

    }
}
