package com.xiaomi.mone.rcurve.test;

import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 1/22/21
 */
@Data
public class Obj implements Serializable {

    private int id;

    private String name;

    private byte[] data;
}
