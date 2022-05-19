package com.xiaomi.youpin.codegen.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class HttpJsonParamsBo extends HttpParamBo implements Serializable {
    private List<HttpJsonParamsBo> childList;
}
