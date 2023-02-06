package com.xiaomi.youpin.gwdash.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * api修改人信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiModifier {

    private Long id;

    private String apiPath;

    private String creatorName;

    private String modifierName;

}
