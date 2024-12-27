package com.xiaomi.youpin.tesla.ip.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author caobaoyu
 * @description: 请求Z平台的请求参数
 * @date 2023-04-21 15:43
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ZRequestPram implements Serializable {

    private Integer pageSize;

    private Integer pageNum;

    private Integer type;

    private String info;

    private String name;

    private Integer structure;

    private Boolean collectionOnly;

    private String token;

    private Long tags;

    private Boolean sortByUsedTimes;


}
