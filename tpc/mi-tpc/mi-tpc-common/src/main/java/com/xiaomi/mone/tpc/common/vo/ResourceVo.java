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
public class ResourceVo implements Serializable {
    private Long id;
    private Integer type;
    private Integer status;
    private String desc;
    private String content;
    private Long createrId;
    private String createrAcc;
    private Integer createrType;
    private Long updaterId;
    private String updaterAcc;
    private Integer updaterType;
    private Long createTime;
    private Long updateTime;
    private Long poolNodeId;
    private String poolNodeName;
    private Long relNodeId;
    private String relNodeName;
    private Long applyId;
    private String resourceName;
    private String key1;
    private String key2;
    private Integer envFlag;
    private boolean edit = true;
    private String nodeCode;

    private Integer isOpenKc;

    private String sid;

    private String kcUser;

    private String mfa;

    private Integer region;

    /**
     * 根据资源类型解析content
     * @param <T>
     * @return
     */
    public <T extends ArgCheck> T parserContent() {
        if (StringUtils.isBlank(content)) {
            return null;
        }
        ResourceTypeEnum typeEnum = ResourceTypeEnum.getEnum(type);
        if (typeEnum == null) {
            return null;
        }
        return (T)GsonUtil.gsonToBean(content, typeEnum.getClazz());
    }
}
