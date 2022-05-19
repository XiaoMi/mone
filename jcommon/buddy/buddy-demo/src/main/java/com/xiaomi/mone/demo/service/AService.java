package com.xiaomi.mone.demo.service;


import io.opentelemetry.extension.annotations.WithSpan;

/**
 * @Author goodjava@qq.com
 * @Date 2021/8/4 13:58
 */
public class AService {


    @WithSpan
    public String hi() {
        return "hi";
    }

}
