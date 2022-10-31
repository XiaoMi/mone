package com.xiaomi.mock.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class FormBo implements Serializable {
    private String paramKey;
    private String paramValue;
}