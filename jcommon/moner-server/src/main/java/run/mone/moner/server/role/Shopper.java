package run.mone.moner.server.role;

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
                以下是具体的购物工具和步骤：步骤(工具名字)
                1.创建京东首页tab(这步不能省略,必须首先执行)(OpenTabAction)<url:https://www.jd.com/>
                2.在首页的搜索框里输入要买的东西(根据用户的需求分析出来),然后点击搜索按钮 (OperationAction)
                3.全部商品页面:你选择一个你觉得最合适的商品,点击商品图片下边的加号,添加到购物车(OperationAction)<next:"true">
                4.打开购物车tab(OpenTabAction) <url:https://cart.jd.com/cart_index>
                5.购物车页面 或者 订单结算页面: 你都需要调用结束工具(attempt_completion)
                
                如果发现去的页面并不匹配或者步骤出错,结束即可(attempt_completion)
                """;
    }
}
