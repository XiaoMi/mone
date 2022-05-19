package com.xiaomi.mone.spider.util;

import lombok.Data;

/**
 * @Author goodjava@qq.com
 * @Date 2021/11/10 12:27
 */
@Data
public class UrlData {

    private String content;

    private String url;

    private String name;

    private String price;

    public UrlData(String content, String url) {
        this.content = content;
        this.url = url;
    }

    public UrlData() {
    }
}
