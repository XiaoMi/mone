package com.xiaomi.mone.monitor.service.alertmanager.client;


import com.xiaomi.mone.monitor.service.alertmanager.client.model.HttpMethodName;
import com.xiaomi.mone.monitor.service.alertmanager.client.util.HttpUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author gaoxihui
 * @date 2022/11/10 2:35 下午
 */
public class Request {
    private String key = null;
    private String secret = null;
    private String method = null;
    private String url = null;
    private String body = null;
    private String fragment = null;
    private Map<String, String> headers = new Hashtable();
    private Map<String, List<String>> queryString = new Hashtable();

    public Request() {
    }

    /** @deprecated */
    @Deprecated
    public String getRegion() {
        return "";
    }

    /** @deprecated */
    @Deprecated
    public String getServiceName() {
        return "";
    }

    public String getKey() {
        return this.key;
    }

    public String getSecrect() {
        return this.secret;
    }

    public HttpMethodName getMethod() {
        return HttpMethodName.valueOf(this.method.toUpperCase());
    }

    public String getBody() {
        return this.body;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    /** @deprecated */
    @Deprecated
    public void setRegion(String region) {
    }

    /** @deprecated */
    @Deprecated
    public void setServiceName(String serviceName) {
    }

    public void setAppKey(String appKey) throws Exception {
        if (null != appKey && !appKey.trim().isEmpty()) {
            this.key = appKey;
        } else {
            throw new Exception("appKey can not be empty");
        }
    }

    public void setAppSecrect(String appSecret) throws Exception {
        if (null != appSecret && !appSecret.trim().isEmpty()) {
            this.secret = appSecret;
        } else {
            throw new Exception("appSecrect can not be empty");
        }
    }

    public void setKey(String appKey) throws Exception {
        if (null != appKey && !appKey.trim().isEmpty()) {
            this.key = appKey;
        } else {
            throw new Exception("appKey can not be empty");
        }
    }

    public void setSecret(String appSecret) throws Exception {
        if (null != appSecret && !appSecret.trim().isEmpty()) {
            this.secret = appSecret;
        } else {
            throw new Exception("appSecrect can not be empty");
        }
    }

    public void setMethod(String method) throws Exception {
        if (null == method) {
            throw new Exception("method can not be empty");
        } else if (!method.equalsIgnoreCase("post") && !method.equalsIgnoreCase("put") && !method.equalsIgnoreCase("patch") && !method.equalsIgnoreCase("delete") && !method.equalsIgnoreCase("get") && !method.equalsIgnoreCase("options") && !method.equalsIgnoreCase("head")) {
            throw new Exception("unsupported method");
        } else {
            this.method = method;
        }
    }

    public String getUrl() {
        String uri = this.url;
        if (this.queryString.size() > 0) {
            uri = uri + "?";
            int loop = 0;
            Iterator entryIterator = this.queryString.entrySet().iterator();

            while(entryIterator.hasNext()) {
                Map.Entry<String, List<String>> entry = (Map.Entry)entryIterator.next();

                for(Iterator entryValueIte = ((List)entry.getValue()).iterator(); entryValueIte.hasNext(); ++loop) {
                    String value = (String)entryValueIte.next();
                    if (loop > 0) {
                        uri = uri + "&";
                    }

                    uri = uri + HttpUtils.urlEncode((String)entry.getKey(), false);
                    uri = uri + "=";
                    uri = uri + HttpUtils.urlEncode(value, false);
                }
            }
        }

        if (this.fragment != null) {
            uri = uri + "#";
            uri = uri + this.fragment;
        }

        return uri;
    }

    public void setUrl(String url) throws Exception {
        if (null != url && !url.trim().isEmpty()) {
            int i = url.indexOf(35);
            if (i >= 0) {
                url = url.substring(0, i);
            }

            i = url.indexOf(63);
            if (i >= 0) {
                String query = url.substring(i + 1, url.length());
                String[] queryArr = query.split("&");
                int queryArrLen = queryArr.length;

                for(int qIndex = 0; qIndex < queryArrLen; ++qIndex) {
                    String item = queryArr[qIndex];
                    String[] spl = item.split("=", 2);
                    String key = spl[0];
                    String value = "";
                    if (spl.length > 1) {
                        value = spl[1];
                    }

                    if (!key.trim().isEmpty()) {
                        key = URLDecoder.decode(key, "UTF-8");
                        value = URLDecoder.decode(value, "UTF-8");
                        this.addQueryStringParam(key, value);
                    }
                }

                url = url.substring(0, i);
            }

            this.url = url;
        } else {
            throw new Exception("url can not be empty");
        }
    }

    public String getPath() {
        String url = this.url;
        int i = url.indexOf("://");
        if (i >= 0) {
            url = url.substring(i + 3);
        }

        i = url.indexOf(47);
        return i >= 0 ? url.substring(i) : "/";
    }

    public String getHost() {
        String url = this.url;
        int i = url.indexOf("://");
        if (i >= 0) {
            url = url.substring(i + 3);
        }

        i = url.indexOf(47);
        if (i >= 0) {
            url = url.substring(0, i);
        }

        return url;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setBody(BufferedReader br) throws IOException {
        String str;
        String wholeStr;
        for(wholeStr = ""; (str = br.readLine()) != null; wholeStr = wholeStr + str) {
        }

        this.body = wholeStr;
    }

    public void addQueryStringParam(String name, String value) throws UnsupportedEncodingException {
        List<String> paramList = (List)this.queryString.get(name);
        if (paramList == null) {
            paramList = new ArrayList();
            this.queryString.put(name, paramList);
        }

        ((List)paramList).add(value);
    }

    public Map<String, List<String>> getQueryStringParams() {
        return this.queryString;
    }

    public String getFragment() {
        return this.fragment;
    }

    public void setFragment(String fragment) throws Exception {
        if (null != fragment && !fragment.trim().isEmpty()) {
            this.fragment = URLEncoder.encode(fragment, "UTF-8");
        } else {
            throw new Exception("fragment can not be empty");
        }
    }

    public void addHeader(String name, String value) {
        if (null != name && !name.trim().isEmpty()) {
            this.headers.put(name, value);
        }

    }
}
