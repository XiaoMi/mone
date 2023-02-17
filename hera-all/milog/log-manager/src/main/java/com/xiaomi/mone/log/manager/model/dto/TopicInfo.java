package com.xiaomi.mone.log.manager.model.dto;

import lombok.*;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/7/14 17:54
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicInfo {
    private String orgId;
    private String name;
    private Integer queueTotalCount;
}
