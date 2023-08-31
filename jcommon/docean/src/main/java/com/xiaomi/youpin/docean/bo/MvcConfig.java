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
     * 是否使用cglib
     */
    private boolean useCglib;

    /**
     * 是否允许跨域
     */
    private boolean allowCross;

    /**
     * 返回结果不包装
     */
    private boolean responseOriginalValue;

    private int poolSize = 200;

    /**
     * 是否支持下载
     */
    private boolean download;

}
