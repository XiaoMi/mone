package com.xiaomi.youpin.gateway.sidecar;

import com.xiaomi.youpin.gateway.filter.FilterContext;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2022/6/20 22:41
 */
@Data
public class FilterHttpData implements Serializable {

    private FilterContext filterContext;

    private ApiInfo apiInfo;

    private FilterRequest request;

}
