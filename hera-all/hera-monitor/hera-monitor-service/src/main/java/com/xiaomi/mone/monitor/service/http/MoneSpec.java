package com.xiaomi.mone.monitor.service.http;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author gaoxihui
 * @date 2022/5/30 6:42 下午
 */
@Data
@ToString
public class MoneSpec implements Serializable {

    private String container;
    private String namespace;//命名空间
    private Integer replicas;//当前副本数
    private Integer setReplicas;//扩缩容后的副本书
    private Integer envID;//环境id
    private Long time;

    public void init(){
        time = System.currentTimeMillis();
    }

}
