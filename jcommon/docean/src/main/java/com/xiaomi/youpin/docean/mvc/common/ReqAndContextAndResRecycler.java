package com.xiaomi.youpin.docean.mvc.common;

import com.xiaomi.youpin.docean.mvc.MvcContext;
import com.xiaomi.youpin.docean.mvc.MvcRequest;
import com.xiaomi.youpin.docean.mvc.MvcResponse;
import io.netty.util.Recycler;

/**
 * @author goodjava@qq.com
 * @date 2023/8/26 10:34
 */
public class ReqAndContextAndResRecycler {


    public static final Recycler<ReqAndContextAndRes> RECYCLER = new Recycler<ReqAndContextAndRes>() {
        @Override
        protected ReqAndContextAndRes newObject(Handle<ReqAndContextAndRes> handle) {
            return new ReqAndContextAndRes(new MvcRequest(), new MvcContext(), new MvcResponse(), handle);
        }
    };


}
