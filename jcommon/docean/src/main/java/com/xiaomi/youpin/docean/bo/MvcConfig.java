package com.xiaomi.youpin.docean.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2022/4/9 16:36
 */
@Data
public class MvcConfig implements Serializable {

    /**
     * Whether to use cglib
     */
    private boolean useCglib;

    /**
     * Allow cross-origin
     */
    private boolean allowCross;

    /**
     * The return result is not wrapped.
     */
    private boolean responseOriginalValue;

    private int poolSize = 200;

    /**
     * Do you support downloading
     */
    private boolean download;

    /**
     * whether to use coroutines
     */
    private boolean virtualThread;

}
