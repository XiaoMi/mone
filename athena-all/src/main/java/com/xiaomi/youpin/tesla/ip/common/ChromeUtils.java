package com.xiaomi.youpin.tesla.ip.common;

import com.google.common.net.UrlEscapers;
import com.google.gson.Gson;
import com.intellij.ui.jcef.JBCefBrowser;
import com.xiaomi.youpin.tesla.ip.bo.Req;
import com.xiaomi.youpin.tesla.ip.bo.robot.AiChatMessage;
import com.xiaomi.youpin.tesla.ip.listener.UltrmanTreeKeyAdapter;
import lombok.SneakyThrows;

/**
 * @author goodjava@qq.com
 * @date 2023/6/2 16:44
 */
public class ChromeUtils {

    private static final Gson gson = new Gson();

    @SneakyThrows
    public static void call(String projectName, String method, String param, boolean useReq) {
        if (useReq) {
            param = UrlEscapers.urlFragmentEscaper().escape(gson.toJson(Req.builder().code(500).message(param)));
        } else {
            param = UrlEscapers.urlFragmentEscaper().escape(param);
        }
        JBCefBrowser jbCefBrowser = UltrmanTreeKeyAdapter.browserMap.get(projectName);
        if (null != jbCefBrowser) {
            jbCefBrowser.getCefBrowser().executeJavaScript(method + "('" + param + "');", jbCefBrowser.getCefBrowser().getURL(), 1);
        }
    }


    public static void call(String projectName, String param, int code) {
        call(projectName, param, "", code);
    }

    public static void call(String projectName, String param, String sound, int code) {
        param = UrlEscapers.urlFragmentEscaper().escape(gson.toJson(Req.builder().code(code).message(param).sound(sound)));
        JBCefBrowser jbCefBrowser = UltrmanTreeKeyAdapter.browserMap.get(projectName);
        if (null != jbCefBrowser) {
            jbCefBrowser.getCefBrowser().executeJavaScript("showErrorCode" + "('" + param + "');", jbCefBrowser.getCefBrowser().getURL(), 1);
        }
    }

    public static void call(String projectName, AiChatMessage res) {
        String param = UrlEscapers.urlFragmentEscaper().escape(gson.toJson(res));
        JBCefBrowser jbCefBrowser = UltrmanTreeKeyAdapter.browserMap.get(projectName);
        if (null != jbCefBrowser) {
            jbCefBrowser.getCefBrowser().executeJavaScript("showErrorCode" + "('" + param + "');", jbCefBrowser.getCefBrowser().getURL(), 1);
        }
    }


    public static void call(String projectName, String method, String param) {
        call(projectName, method, param, false);
    }


}
