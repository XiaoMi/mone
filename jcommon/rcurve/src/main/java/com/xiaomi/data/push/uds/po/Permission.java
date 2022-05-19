package com.xiaomi.data.push.uds.po;

/**
 * @Author goodjava@qq.com
 * @Date 2021/2/23 15:14
 */
public class Permission {

    /**
     * 是否是request
     */
    public static final int IS_REQUEST= 1 << 0;

    /**
     * 是否是oneway
     */
    public static final int IS_ONWAY= 1 << 1;

}
