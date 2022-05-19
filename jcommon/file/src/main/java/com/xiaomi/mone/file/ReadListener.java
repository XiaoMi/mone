package com.xiaomi.mone.file;

/**
 * @Author goodjava@qq.com
 * @Date 2021/6/22 12:28
 */
public interface ReadListener {


    void onEvent(ReadEvent event);

    boolean isContinue(String line);

}
