package com.xiaomi.mone.file;

import java.io.IOException;

/**
 * @author goodjava@qq.com
 * @date 2023/9/20 10:39
 */
public interface ILogFile {

    void readLine() throws IOException;

    void setStop(boolean stop);

    void setReOpen(boolean reOpen);

    void initLogFile(String file, ReadListener listener, long pointer, long lineNumber);

    /**
     * It only needs to be called when an exception occurs and can only be called externally.
     */
    void setExceptionFinish();

    boolean getExceptionFinish();


}
