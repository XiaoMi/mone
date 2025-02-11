package run.mone.mcp.playwright.role;

import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.Environment;

/**
 * @author goodjava@qq.com
 * @date 2025/2/7 14:58
 * 购物者
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class Shopper extends ChromeOperator {


    //去京东购物
    public Shopper() {
        super("Shopper", "购物者");
        setEnvironment(new Environment());
        this.goal = """
                购物步骤:(操作推荐)
                1.创建京东首页tab(发现没有code的时候,必须调用这个接口)(OpenTabAction)
                2.在首页的搜索框里输入要买的东西(根据用户的需求分析出来),然后点击搜索按钮 (OperationAction)
                3.搜素详情页:你选择一个你觉得最合适的商品,点击这个商品的大图,你要忽略所有广告的图片
                4.商品详情页:点击 加入购物车 按钮(红色大按钮)(OperationAction) (如果找不到对应的按钮 滚动屏幕 ScrollAction)
                5.购物车加购页面:点击去购物车结算按钮(OperationAction) (如果找不到对应的按钮 滚动下屏幕 ScrollAction)
                6.到达购物车列表页面就可以结束了(attempt_completion)
                
                需要注意的点:
                如果页面信息不全,可以滚动下页面
                """;
    }


}
