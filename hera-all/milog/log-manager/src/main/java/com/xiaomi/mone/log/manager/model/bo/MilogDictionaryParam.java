package com.xiaomi.mone.log.manager.model.bo;

import lombok.Data;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/28 10:00
 */
@Data
public class MilogDictionaryParam {
    private List<Integer> codes;
    private Long middlewareId;
    private String nameEn;
}
