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
                3.搜素详情页:你选择一个你觉得最合适的商品,点击这个商品的大图,你要忽略所有广告的图片 (OperationAction)
                4.商品详情页:点击 加入购物车 (红色大按钮)(OperationAction) (如果找不到加入购物车按钮 滚动屏幕 ScrollAction)
                5.购物车加购页面:点击 去购物车结算 (红色大按钮)(OperationAction) (如果找不到去购物车结算按钮 滚动屏幕 ScrollAction)
                6.到达结算页面 或者 订单结算月 你都需要调用结束工具(attempt_completion)
                
                如果发现去的页面不能正确完成剩下的步骤,则返回第一步(创建京东首页tab)
                
                """;
    }
}
