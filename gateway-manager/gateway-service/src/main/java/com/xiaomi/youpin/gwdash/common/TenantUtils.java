package com.xiaomi.youpin.gwdash.common;

import com.xiaomi.youpin.gwdash.service.impl.TenantComponent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author goodjava@qq.com
 * @date 2022/10/10 09:51
 */
@Component
public class TenantUtils {

    @Resource
    private TenantComponent component;

    public String getTenant(HttpServletRequest request) {
        String tenant = request.getHeader("gw-tenant-id");
        if (StringUtils.isEmpty(tenant)) {
            //tenant = String.valueOf(component.getTenantInfo("system").getList().get(0).getOutId());
            tenant = "1";
        }
        return tenant;
    }
}
