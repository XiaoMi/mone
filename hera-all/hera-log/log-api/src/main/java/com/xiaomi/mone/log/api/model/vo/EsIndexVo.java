package com.xiaomi.mone.log.api.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Auther: wtt
 * @Date: 2022/5/12 12:57
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EsIndexVo {
    private Integer logTypeCode;
    private String logTypeName;
    private List<String> esIndexList;
}
