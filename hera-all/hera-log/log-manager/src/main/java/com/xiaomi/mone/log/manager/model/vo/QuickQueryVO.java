package com.xiaomi.mone.log.manager.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/4/28 11:16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuickQueryVO implements Serializable {
    private Long spaceId;
    private String spaceName;
    private Long storeId;
    private String storeName;
    private Long tailId;
    private String tailName;
    private Integer isFavourite;
}
