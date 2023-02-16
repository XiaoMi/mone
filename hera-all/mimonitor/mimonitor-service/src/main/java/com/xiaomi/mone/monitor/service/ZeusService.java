package com.xiaomi.mone.monitor.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xiaomi.mone.monitor.utils.AesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nutz.json.Json;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author zhangxiaowei6
 *
 */

@Slf4j
@Service
public class ZeusService {

    /**
     * zeus appid
     */
    private static final String ZEUS_APP_ID = "xxx";
    /**
     * zeus appkey
     */
    private static final String ZEUS_APP_KEY = "xxx";
    /**
     * zeus staging salt
     */
    private static final String ZEUS_STAGING_SALT = "xxx";
    /**
     * zeus online salt
     */
    private static final String ZEUS_ONLINE_SALT = "xxx";
    /**
     * zeus staging url
     */
    private static final String ZEUS_STAGING_URL = "xxx";
    /**
     * zeus online url
     */
    private static final String ZEUS_ONLINE_URL = "xxx";
    /**
     * zeus grafana url
     */
    private static final String ZEUS_GRAFANA_URL = "xxx";
    /**
     * zeus online 偏移量
     */
    private static final String ZEUS_ONLINE_IV = "xxx";
    /**
     * zeus staging 偏移量
     */
    private static final String ZEUS_STAGING_IV = "xxx";

    @Value("${server.type}")
    private String serverType;

    private final Gson gson = new Gson();

    //返回加密的sign
    private String getSign(String email) {
        if (StringUtils.isEmpty(email)) {
            return "";
        }
        String originStr = ZEUS_APP_ID + ZEUS_APP_KEY + email;
        return DigestUtils.md5DigestAsHex(originStr.getBytes());
    }

    //返回域名
    private String getZeusUrl() {
        if ("dev".equals(serverType) || "staging".equals(serverType)) {
            return ZEUS_STAGING_URL;
        } else if ("online".equals(serverType)) {
            return ZEUS_ONLINE_URL;
        } else {
            return "";
        }
    }

    //返回Aes后的密码
    private String getAesPwd(String pwd) {
        String salt = "";
        String iv = "";
        if ("dev".equals(serverType) || "staging".equals(serverType)) {
            salt = ZEUS_STAGING_SALT;
            iv = ZEUS_STAGING_IV;
        } else if ("online".equals(serverType)) {
            iv = ZEUS_ONLINE_IV;
            salt = ZEUS_ONLINE_SALT;
        } else {
            return "";
        }
        try {
            return AesUtil.encrypt(pwd,salt,iv);
        } catch (Exception e) {
            log.error(e.toString());
            return "";
        }
    }
    public String requestZeusGrafana(String user,String domainPort,String userName,String pwd) {
        //记录访问
        log.info("requestZeusGrafana user {},domainPort {}, userName {}, password {}", user, domainPort, userName,pwd);
        //获取加密pwd
        pwd = getAesPwd(pwd);
        System.out.println("pwd: "+pwd);
        //合成sign
        String sign = getSign(user);
        String zeusUrl = getZeusUrl();
        if (StringUtils.isEmpty(pwd) || StringUtils.isEmpty(sign) || StringUtils.isEmpty(zeusUrl)) {
            log.warn("param error pwd {} sign {} zeusUrl {}",pwd,sign,zeusUrl);
            return "";
        }
        StringBuilder requestUrl = new StringBuilder(zeusUrl).append(ZEUS_GRAFANA_URL).append("/")
                .append(domainPort).append("?").append("sign=").append(sign).append("&appId=").append(ZEUS_APP_ID)
                .append("&email=").append(user).append("&username=").append(userName).append("&password=").append(pwd);
        System.out.println(requestUrl.toString());
        try {
            String zeusResult = innerRequestZeus(null,requestUrl.toString(),"GET");
            System.out.println(zeusResult);
            JsonObject zeusResultJson = gson.fromJson(zeusResult, JsonObject.class);
            String message = zeusResultJson.get("message").getAsString();
            if (! "success".equals(message)) {
                log.error("requestZeusGrafana error message {}",message);
                return "";
            }
            JsonObject data = zeusResultJson.get("data").getAsJsonObject();
            String instanceGrafanaUrl = data.get("instanceGrafanaUrl").getAsString();
            System.out.println();
            System.out.println(zeusResult);
            return instanceGrafanaUrl;
        }catch (Exception e) {
            log.error("requestZeusGrafana error message {}",e.toString());
            return "";
        }
    }

    private String innerRequestZeus(String data, String url, String method) {
        try {
            URL requestUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) requestUrl.openConnection();
            PrintWriter out = null;
            //设置URLConnection的参数和普通的请求属性
            conn.setRequestProperty("Expect", "");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod(method);
            conn.connect();
            if ("POST".equals(method)) {
                //POST请求
                BufferedWriter out1 = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
                out1.write(data);
                out1.flush();
                out1.close();
            }
            InputStream is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String finalStr = "";
            String str = "";
            while ((str = br.readLine()) != null) {
                finalStr = new String(str.getBytes(), "UTF-8");
            }
            is.close();
            conn.disconnect();
            return finalStr;
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }

    }
}