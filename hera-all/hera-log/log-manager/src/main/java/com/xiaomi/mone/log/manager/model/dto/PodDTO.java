package com.xiaomi.mone.log.manager.model.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PodDTO {
    /**
     * pod名称
     */
    private String podName;
    /**
     * pod IP
     */
    private String podIP;
    /**
     * 物理机名称
     */
    private String nodeName;
    /**
     * 物理机IP
     */
    private String nodeIP;
}
