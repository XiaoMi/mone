package com.xiaomi.youpin.docean.plugin.dmesh;

import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 */
@Data
public class MeshResponse implements Serializable {

    private int code;

    private String message;

    private String data;

}
