package com.xiaomi.mock.common;

import com.xiaomi.youpin.docean.mvc.HttpResponseUtils;
import com.xiaomi.youpin.docean.mvc.MvcContext;

import java.util.Map;

public class HeaderUtil {
    public static final String CONTENT_TYPE= "content-type";

    public static void setContentTypeUtf8(MvcContext context){
        Map<String,String> map = context.getResHeaders();
        map.put(CONTENT_TYPE, HttpResponseUtils.ContentTypeJson);
        context.setResHeaders(map);
    }

    public static void setCrosHeader(MvcContext context){
        Map<String,String> res = context.getResHeaders();
        res.put("Access-Control-Allow-Credentials", "true");
        res.put("Access-Control-Allow-Origin", "*");
        res.put("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
        res.put("Access-Control-Allow-Headers", "Authorization,Origin,X-Requested-With,Content-Type,Accept,"
                + "content-Type,origin,x-requested-with,content-type,accept,authorization,token,id,X-Custom-Header,X-Cookie,Connection,User-Agent,Cookie,*");
        res.put("Access-Control-Request-Headers", "Authorization,Origin, X-Requested-With,content-Type,Accept");
        res.put("Access-Control-Expose-Headers", "*");
        context.setResHeaders(res);
    }
}
