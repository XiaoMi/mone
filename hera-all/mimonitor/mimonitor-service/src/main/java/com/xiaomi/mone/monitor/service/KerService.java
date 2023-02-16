package com.xiaomi.mone.monitor.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author zhangxiaowei6
 *
 */

@Slf4j
@Service
public class KerService {
    //请求ker监控service
    /**
     * mis staging url
     */
    private static final String MIS_STAGING_URL = "https://localhost/";
    /**
     * mis online url
     */
    private static final String MIS_ONLINE_URL = "https://localhost/";
    /**
     * mis ker grafana url
     */
    private static final String MIS_KER_GRAFANA_URL = "apixx";
    /**
     *
     */
    private static final String KER_GRAFANA_URL_PREFIX = "https://localhost";

    private final Gson gson = new Gson();

    @Value("${server.type}")
    private String serverType;

    private String getMisUrl() {
        if ("dev".equals(serverType) || "staging".equals(serverType)) {
            return MIS_STAGING_URL;
        } else if ("online".equals(serverType)) {
            return MIS_ONLINE_URL;
        } else {
            return "";
        }
    }

    //请求mis接口 ，拼接ker grafana url
    public String requestKerGrafana(String auth) {
        log.info("requestKerGrafana auth: {}",auth);
        if (StringUtils.isEmpty(auth)) {
            return "";
        }
        String misUrl = getMisUrl();
        if (StringUtils.isEmpty(misUrl)) {
            log.error("requestKerGrafana misurl is empty auth:{} , env: {}",auth,serverType);
            return "";
        }
        StringBuilder requestUrl = new StringBuilder(misUrl).append(MIS_KER_GRAFANA_URL).append("?auth=").append(auth);
        System.out.println(requestUrl);
        try {
            String requestResult = innerRequestMis(null,requestUrl.toString(),"GET");
            System.out.println(requestResult);
            JsonObject misResultJson = gson.fromJson(requestResult, JsonObject.class);
            //data值为ker namespace的值，如果没找到或者不是ker 则为空
            String data = misResultJson.get("data").getAsString();
            if (StringUtils.isEmpty(data)) {
                log.info("requestKerGrafana requestMis not ker or no such namespace misUrl:{} requestData:{} responseData:{}",requestUrl,auth,misResultJson);
                return "";
            }
            return  KER_GRAFANA_URL_PREFIX+data;
        }catch (Exception e) {
            log.error("requestKerGrafana requestMis error misUrl:{} requestData:{}",requestUrl,auth);
            return "";
        }
    }

    private String innerRequestMis(String data, String url, String method) {
        try {
            URL requestUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) requestUrl.openConnection();
            PrintWriter out = null;
            //设置URLConnection的参数和普通的请求属性
            conn.setRequestProperty("Expect", "");
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestProperty("Connection","keep-alive");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setReadTimeout(100000);
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
