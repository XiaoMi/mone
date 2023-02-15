package com.xiaomi.youpin.codecheck.test.code;

/**
 * @author goodjava@qq.com
 * @date 2023/2/15 14:32
 * 用来测试的类
 */
public class Code1 {

    private String pwd = "123";

    private String pwd2;

    public String call() {
        Ds ds = new Ds();
        ds.setUserName("test");
        ds.setPassword("");
        ds.setPassword("ppppp");
        ds.setPassword(pwd);
        ds.setPassword(pwd2);
        String p = "123456789";
        ds.setPassword(p);
        System.out.println(ds);
        return ds.toString();
    }

}
