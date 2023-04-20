package com.xiaomi.mone.log.common;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Es {

    /**
     * 获取索引后缀
     * @return
     */
    public static String indexPostfix() {
        return "-" + new SimpleDateFormat("yyyy.MM.dd").format(new Date());
    }

}
