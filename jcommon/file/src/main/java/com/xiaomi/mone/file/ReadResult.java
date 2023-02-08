package com.xiaomi.mone.file;

import lombok.Data;

import java.util.List;

/**
 * @author goodjava@qq.com
 */
@Data
public class ReadResult {

    private List<String> lines;

    private long pointer;

    private Long fileMaxPointer;

    private long lineNumber;

    private boolean over;

}
