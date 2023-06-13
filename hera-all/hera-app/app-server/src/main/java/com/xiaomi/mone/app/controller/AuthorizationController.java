package com.xiaomi.mone.app.controller;


import com.alibaba.nacos.client.config.utils.MD5;

/**
 * @author gaoxihui
 * @date 2023/6/12 4:02 下午
 */
public class AuthorizationController {

    public void test(){
        String aa = MD5.getInstance().getMD5String("aa");
        String sign = MD5.getInstance().getMD5String("aa"+aa+System.currentTimeMillis());
        System.out.println(sign);
    }

    public static void main(String[] args) {
        String aa = MD5.getInstance().getMD5String("aa");
        String sign = MD5.getInstance().getMD5String("aa"+aa+System.currentTimeMillis());
        System.out.println(sign);
    }
}
