package com.xiaomi.youpin.docean.exception;

/**
 * @author goodjava@qq.com
 * @date 2020/6/20
 */
public class DoceanException extends RuntimeException {

    public DoceanException(Throwable ex) {
        super(ex);
    }


    public DoceanException() {
    }

    public DoceanException(String m) {
        super(m);
    }
}
