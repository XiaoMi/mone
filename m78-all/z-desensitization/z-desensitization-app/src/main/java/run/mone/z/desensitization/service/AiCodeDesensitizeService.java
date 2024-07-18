package run.mone.z.desensitization.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xiaomi.sautumn.serverless.api.http.Http;
import com.xiaomi.sautumn.serverless.api.http.HttpResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wmin
 * @date 2023/11/20
 */
@Service
@Slf4j
public class AiCodeDesensitizeService {

    @Resource
    private Http http;

    public String aiCodeDesensitize(String code){
        String res = code;
        try {
            log.info("aiCodeDesensitize start");
            Map<String,String> map = new HashMap<>();
            map.put("data",code);
            HttpResult httpResult = http.postForm("http://localhost/desensitize_form",map,new HashMap<String, String>() {{
                put("content-type", "application/x-www-form-urlencoded");
            }},"utf-8", 5000);
            if (200==httpResult.getCode() && StringUtils.isNotBlank(httpResult.getContent())){
                JSONObject jsonObject = JSON.parseObject(httpResult.getContent());
                if (0==jsonObject.getInteger("code") && StringUtils.isNotBlank(jsonObject.getString("data"))){
                    res = jsonObject.getString("data");
                    log.info("aiCodeDesensitize done.{}", res);
                }
            } else {
                log.error("aiCodeDesensitize failed.{}", httpResult);
            }
        } catch (Exception e){
            log.error("aiCodeDesensitize error.", e);
        }
        return res;
    }
}
