package com.xiaomi.mone.monitor.service.model;

import lombok.Data;

/**
 * @author gaoxihui
 * @date 2022/2/18 11:37 上午
 * 接入hera应用对应的环境
 */
@Data
public class Env {
    String name;

    public Env(){

    }
    public Env(String name){
        this.name = name;
    }
}
