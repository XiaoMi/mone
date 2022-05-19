package com.xiaomi.mone.file;

import lombok.Data;

/**
 * @Author goodjava@qq.com
 * @Date 2021/6/22 12:30
 */
@Data
public class ReadEvent {

    private ReadResult readResult;

    public ReadEvent(ReadResult readResult) {
        this.readResult = readResult;
    }
}
