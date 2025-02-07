package run.mone.m78.common;

import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
@Slf4j
public class URIParser {
    public static String getQueryParamValue(URI uri, String paramName) {
        try {
            String query = uri.getQuery();
            if (query!= null) {
                String[] params = query.split("&");
                for (String param : params) {
                    String[] keyValue = param.split("=");
                    if (keyValue.length == 2 && keyValue[0].equals(paramName)) {
                        return keyValue[1];
                    }
                }
            }
            return null;
        } catch (Exception e) {
            log.error("解析请求参数异常uri={},paramName={}",uri,paramName);
            return null;
        }
    }
    public static Map<String,String> getQueryParamValues(URI uri) {
        try {
            HashMap<String, String> paramValues = new HashMap<>();
            String query = uri.getQuery();
            if (query!= null) {
                String[] params = query.split("&");
                for (String param : params) {
                    String[] keyValue = param.split("=");
                    if(keyValue.length == 1){
                        paramValues.put(keyValue[0],null);
                    }
                    if (keyValue.length == 2 ) {
                        paramValues.put(keyValue[0],keyValue[1]);
                    }
                }
            }
            return paramValues;
        } catch (Exception e) {
            log.error("解析请求参数异常uri={}",uri);
            return null;
        }
    }

}