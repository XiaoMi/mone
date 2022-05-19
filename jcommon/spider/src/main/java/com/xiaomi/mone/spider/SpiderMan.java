package com.xiaomi.mone.spider;

import com.xiaomi.mone.spider.util.UrlData;

import java.util.concurrent.CopyOnWriteArrayList;

public interface SpiderMan {

    CopyOnWriteArrayList<UrlData> getList(String type);

    void closeDriver();
}
