package com.xiaomi.mone.monitor.service.http;

import com.alibaba.nacos.client.logger.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author gaoxihui
 * @date 2021/7/22 2:16 下午
 */
@Slf4j
@Service
public class RestTemplateService {
    @Autowired
    RestTemplate restTemplate;

    public String getHttp(String url,JSONObject param){
        log.info("RestTemplateService.getHttp url:{}, param:{}",url,param);
        String result = null;
        try {
            if(param != null && !param.isEmpty()){
                url = expandURL(url,param);
            }
            result = restTemplate.getForObject(url, String.class, param);
            log.info("RestTemplateService.getHttp url : {}, param : {},result : {} ",url,param,result);
        } catch (RestClientException e) {
            log.error("RestTemplateService.getHttp error : {} ",e.getMessage(),e);
        }

        return result;
    }

    public String getHttpM(String url, Map map){
        log.info("RestTemplateService.getHttp url:{}, map:{}",url,map);
        String result = null;
        try {
            if(!CollectionUtils.isEmpty(map)){
                url = expandURLByMap(url,map);
            }
            result = restTemplate.getForObject(url, String.class, map);
            log.info("RestTemplateService.getHttp url : {}, map : {},result : {} ",url,map,result);
        } catch (RestClientException e) {
            log.error("RestTemplateService.getHttp error : {} ",e.getMessage(),e);
        }

        return result;
    }

    public String getHttpMPost(String url, com.alibaba.fastjson.JSONObject param, MediaType mediaType){

        log.info("RestTemplateService.getHttpMPost url:{},param:{},mediaType:{}",url,param,mediaType);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);

        HttpEntity<com.alibaba.fastjson.JSONObject> request = new HttpEntity<>(param, headers);
        // 发送post请求，并打印结果，以String类型接收响应结果JSON字符串
        String result = null;
        try {
            result = restTemplate.postForObject(url,request,String.class);
            log.info("RestTemplateService.getHttpMPost url : {}, param : {},result : {} ",url,param,result);
        } catch (RestClientException e) {
            log.error("RestTemplateService.getHttpMPost error : {} ",e.getMessage(),e);
        }

        return result;
    }

    private static String expandURL(String url, JSONObject jsonObject) {

        StringBuilder sb = new StringBuilder(url);
        sb.append("?");
        Set<String> keys = jsonObject.keySet();
        for (String key : keys) {
            sb.append(key).append("=").append(jsonObject.get(key)).append("&");
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }

    private static String expandURLByMap(String url, Map map) {

        StringBuilder sb = new StringBuilder(url);
        sb.append("?");
        Set<Map.Entry<String,String>> keys = map.entrySet();
        for (Map.Entry<String,String> entry : keys) {
            sb.append(entry.getKey()).append("=").append("{").append(entry.getKey()).append("}").append("&");
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }
}
