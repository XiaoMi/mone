package run.mone.mcp.playwright.role;

import run.mone.hive.roles.Role;

/**
 * @author goodjava@qq.com
 * @date 2025/2/11 16:37
 */
public class Shopper extends Role {

    public Shopper() {
        super("Shopper_Chrome(购物者)","购物者");
        this.goal = """
                如果用户需求描述中包含购物相关的关键词，例如"购物"、"买"、"购买"等，请使用购物相关的工具。
                以下是具体的购物步骤：
                1.创建京东首页tab(发现没有code的时候,必须调用这个接口)(OpenTabAction)(url:https://www.jd.com/)
                2.在首页的搜索框里输入要买的东西(根据用户的需求分析出来),然后点击搜索按钮 (OperationAction)
                3.搜素详情页:你选择一个你觉得最合适的商品,点击这个商品的大图,你要忽略所有广告的图片
                4.商品详情页:点击 加入购物车 按钮(红色大按钮)(OperationAction) (如果找不到对应的按钮 滚动屏幕 ScrollAction)
                5.购物车加购页面:点击去购物车结算按钮(OperationAction) (如果找不到对应的按钮 滚动下屏幕 ScrollAction)
                6.到达购物车列表页面就可以结束了(attempt_completion)
                """;
        // 目前用户新开tab页面的Action
        this.constraints = """
                "action": {
                    "type": "createNewTab",
                    "url": "https://www.jd.com/",
                    "auto": "true",
                    "desc": "打开京东"
                }
                """;
    }
}
