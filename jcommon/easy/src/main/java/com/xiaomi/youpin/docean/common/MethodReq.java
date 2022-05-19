package com.xiaomi.youpin.docean.common;

import lombok.Data;

/**
 * @author goodjava@qq.com
 * @date 1/24/21
 */
@Data
public class MethodReq {

    private String methodName;

    private String[] paramTypes;

    private byte serializeType;

    private byte[][] byteParams;

    private String[] params;

}
