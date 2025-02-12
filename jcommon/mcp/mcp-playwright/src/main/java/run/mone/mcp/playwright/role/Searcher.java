package run.mone.mcp.playwright.role;

import run.mone.hive.roles.Role;

/**
 * @author goodjava@qq.com
 * @date 2025/2/11 14:54
 */
public class Searcher extends Role {

    public Searcher() {
        super("Searcher_Chrome工具", "网络搜索者");
        this.goal = """
                如果用户需求描述中包含购物相关的关键词，例如"查找"、"搜索"、"google"等，请使用搜索相关的工具。
                搜索步骤:(操作推荐)
                1.创建google tab(发现没有code的时候,必须调用这个接口)(OpenTabAction)
                2.在google的页面搜索框里输入要查询的东西(根据用户的需求分析出来),然后点击搜索按钮 (OperationAction)
                3.搜索页:找到一个维基百科的页面,点击链接进入
                4.维基百科页面:收集页面信息,到这个页面就结束了
                """;
        // 目前用户新开tab页面的Action
        this.constraints = """
                "action": {
                    "type": "createNewTab",
                    "url": "https://www.google.com/",
                    "auto": "true",
                    "desc": "打开google"
                }
                """;
    }


}
