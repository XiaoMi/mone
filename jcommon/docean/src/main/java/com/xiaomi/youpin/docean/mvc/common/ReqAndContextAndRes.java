package com.xiaomi.youpin.docean.mvc.common;

import com.xiaomi.youpin.docean.mvc.MvcContext;
import com.xiaomi.youpin.docean.mvc.MvcRequest;
import com.xiaomi.youpin.docean.mvc.MvcResponse;
import io.netty.util.Recycler;
import lombok.Data;

/**
 * @author goodjava@qq.com
 * @date 2023/8/26 10:34
 */
@Data
public class ReqAndContextAndRes {

    private MvcRequest request;

    private MvcContext mvcContext;

    private MvcResponse mvcResponse;

    private Recycler.Handle<ReqAndContextAndRes> handle;


    public ReqAndContextAndRes(MvcRequest request, MvcContext mvcContext,MvcResponse res, Recycler.Handle<ReqAndContextAndRes> handle) {
        this.request = request;
        this.mvcContext = mvcContext;
        this.mvcResponse = res;
        this.handle = handle;
    }

    public void recycle() {
        this.request.clear();
        this.mvcContext.clear();
        this.mvcResponse.clear();
        handle.recycle(this);
    }
}
