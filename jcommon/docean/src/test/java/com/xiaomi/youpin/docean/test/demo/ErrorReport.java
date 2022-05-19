package com.xiaomi.youpin.docean.test.demo;

import com.xiaomi.youpin.docean.anno.Component;
import lombok.Data;

/**
 * @Author goodjava@qq.com
 * @Date 2021/9/27 16:28
 */
@Component
@Data
public class ErrorReport {


    private boolean error;

    private String message;

}
