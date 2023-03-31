package com.xiaomi.mone.log.manager.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author milog
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MilogSpaceParam {
    private Long id;
    private Long tenantId;
    private String spaceName;
    private String description;
    private String permDeptId;
}
