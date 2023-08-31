package com.xiaomi.mone.tpc.common.param;

import com.xiaomi.mone.tpc.common.vo.EnumData;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

@Data
@ToString
public class ApplyOuterParam implements ArgCheck {
    private String service;
    private String method;
    private String version;
    private String group;
    private String regAddress;
    private Map<String, Object> arg;
    private List<EnumData<String, String>> show;

    @Override
    public boolean argCheck() {
        if (StringUtils.isBlank(service)) {
            return false;
        }
        if (StringUtils.isBlank(method)) {
            return false;
        }
        if (arg == null || arg.isEmpty()) {
            return false;
        }
        if (show == null || show.isEmpty()) {
            return false;
        }
        return true;
    }
}
