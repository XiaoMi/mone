package com.xiaomi.mone.tpc.common.param;

import com.xiaomi.mone.tpc.common.enums.ApplyTypeEnum;
import com.xiaomi.mone.tpc.common.util.GsonUtil;
import lombok.Data;
import lombok.ToString;

import java.util.Map;

@Data
@ToString(callSuper = true)
public abstract class BaseApplyParam extends BaseParam {

    private Integer type;
    private Map<String, Object> typeParam;
    private ArgCheck argParam;

    @Override
    public boolean argCheck() {
        if (typeParam == null) {
            return false;
        }
        ApplyTypeEnum applyTypeEnum = ApplyTypeEnum.getEnum(type);
        if (applyTypeEnum == null) {
            return false;
        }
        argParam = (ArgCheck) GsonUtil.gsonToBean(GsonUtil.gsonString(typeParam), applyTypeEnum.getCls());
        if (!argParam.argCheck()) {
            return false;
        }
        return argCheckV2();
    }

    public abstract boolean argCheckV2();
}
