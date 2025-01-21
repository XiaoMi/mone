package run.mone.local.docean.service;

import com.xiaomi.data.push.client.HttpClientV5;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.config.Config;
import lombok.extern.slf4j.Slf4j;
import run.mone.local.docean.service.dto.VisionReq;
import run.mone.local.docean.service.exceptions.GenericServiceException;
import run.mone.local.docean.util.GsonUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangxiaowei6
 * @Date 2024/12/5 16:58
 */

@Service
@Slf4j
public class PDFService {
    public String PDFUnderstand(VisionReq req, int timeout){
        try {
            String aiProxy = ((Config) Ioc.ins().getBean(Config.class)).get("ai.proxy", "");
            Map<String, String> header = new HashMap<>();
            header.put("Content-Type", "application/json");
            String res = HttpClientV5.post(aiProxy + "/api/z-proxy/multiModal/pdf", GsonUtils.gson.toJson(req), header, timeout);
            log.info("imageUnderstand res:{}", res);
            return res;
        } catch (Exception e){
            throw new GenericServiceException(-1, "call ai proxy error");
        }
    }
}
