package com.xiaomi.data.push.antlr.drink;

/**
 * Created by zhangzhiyong on 07/06/2018.
 */
public class DrinkListenerImpl extends DrinkBaseListener {

    @Override
    public void enterDrink(DrinkParser.DrinkContext ctx) {
        System.out.println(ctx.getText());
        super.enterDrink(ctx);
    }
}
