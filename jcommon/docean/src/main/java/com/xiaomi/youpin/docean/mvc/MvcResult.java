package com.xiaomi.youpin.docean.mvc;

import lombok.Data;

import java.util.Map;

/**
 * @author goodjava@qq.com
 */
@Data
public class MvcResult<D> {


    private int code;

    private String message;

    private D data;

    private Map<String,String> attachements;


}
