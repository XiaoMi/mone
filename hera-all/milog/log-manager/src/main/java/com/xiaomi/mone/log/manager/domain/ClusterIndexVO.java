package com.xiaomi.mone.log.manager.domain;

import lombok.*;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/12/28 20:32
 */
@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ClusterIndexVO {
    private Long clusterId;
    private String indexName;
}
