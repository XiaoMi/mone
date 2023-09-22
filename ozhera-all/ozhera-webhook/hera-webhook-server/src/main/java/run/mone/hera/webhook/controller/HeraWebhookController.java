package run.mone.hera.webhook.controller;

import com.alibaba.fastjson2.JSONObject;
import io.fabric8.kubernetes.api.model.admission.v1beta1.AdmissionResponse;
import io.fabric8.kubernetes.api.model.admission.v1beta1.AdmissionReview;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import run.mone.hera.webhook.domain.JsonPatch;
import run.mone.hera.webhook.service.HeraWebhookService;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

/**
 * @author dingtao
 * @date 2023/4/11 14:40
 */
@RestController
@Slf4j
public class HeraWebhookController {

    @Autowired
    private HeraWebhookService heraWebhookService;

    @RequestMapping(value = "/hera-env-v1", method = RequestMethod.POST)
    public AdmissionReview heraEnvV1(@RequestBody String admissionReview) {
        log.info("hera webhook get request body : "+admissionReview);
        JSONObject admissionReviewJson = JSONObject.parseObject(admissionReview);
        JSONObject admissionReviewRequestJson = admissionReviewJson.getJSONObject("request");
        String kind = admissionReviewRequestJson.getJSONObject("kind").getString("kind");
        String uid = admissionReviewRequestJson.getString("uid");
        String patchsJson = null;
        if("Pod".equals(kind)){
            List<JsonPatch> patchs = heraWebhookService.setPodEnv(admissionReviewRequestJson);
            if(patchs != null || patchs.size() > 0){
                patchsJson = JSONObject.toJSONString(patchs);
            }
        }
        final AdmissionReview admissionReviewResp = new AdmissionReview();
        admissionReviewResp.setKind(admissionReviewJson.getString("kind"));
        admissionReviewResp.setApiVersion(admissionReviewJson.getString("apiVersion"));
        final AdmissionResponse admissionResponse = new AdmissionResponse();
        admissionResponse.setAllowed(true);
        admissionResponse.setUid(uid);
        if(StringUtils.isNotEmpty(patchsJson)){
            log.info("patch json is : "+patchsJson);
            admissionResponse.setPatch(Base64.getEncoder().encodeToString(patchsJson.getBytes(StandardCharsets.UTF_8)));
            admissionResponse.setPatchType("JSONPatch");
        }
        admissionReviewResp.setResponse(admissionResponse);
        return admissionReviewResp;
    }
}
