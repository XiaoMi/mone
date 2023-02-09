package com.xiaomi.miapi.bo;

import lombok.Data;

import java.io.Serializable;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Data
public class FormBo implements Serializable {
    private String paramKey;
    private String paramValue;
}
