package com.xiaomi.mone.tpc.common.vo;

import com.xiaomi.mone.tpc.common.enums.ResourceTypeEnum;
import com.xiaomi.mone.tpc.common.param.ArgCheck;
import com.xiaomi.mone.tpc.common.util.GsonUtil;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 17:12
 */
@ToString
@Data
public class ProjectVo implements Serializable {

    private Long id;
    private String name;
    private String type;

}
