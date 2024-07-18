package com.xiaomi.youpin.tesla.ip.bo.robot;

/**
 * @author goodjava@qq.com
 * @date 2023/12/1 13:56
 */
public enum MessageType {

    //纯粹的String
    string,
    //里边是ItemData(就是一个选择列表)
    list,

    //kev -value (key都是固定好的,value用户自己填写)
    map,

    //只有是还是否
    bool,



}
