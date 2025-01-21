package run.mone.local.docean.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.xiaomi.youpin.docean.Ioc;
import lombok.extern.slf4j.Slf4j;
import run.mone.local.docean.fsm.flow.LLMFlow;
import run.mone.local.docean.service.dto.VisionContent;
import run.mone.local.docean.service.dto.VisionMsg;
import run.mone.local.docean.service.dto.VisionReq;
import run.mone.local.docean.tianye.common.CommonConstants;
import run.mone.local.docean.util.ImageUtils;
import run.mone.local.docean.util.PDFUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wmin
 * @date 2024/8/2
 */
@Slf4j
public class LLMImageProxy {

    private LLMFlow llmFlow;

    public LLMImageProxy(LLMFlow llmFlow) {
        this.llmFlow = llmFlow;
    }

    public String imageProxy(String prompt, String model, String rstFormatDefinition, boolean generateCode) {
        if (isLLMImageUnderstand()) {
            ImageService imageService = Ioc.ins().getBean(ImageService.class);
            VisionReq visionReq = getVisionReq(prompt, model, rstFormatDefinition, generateCode);
            return imageService.imageUnderstand(visionReq, llmFlow.getTimeout());
        }
        // todo
        return "";
    }

    public Boolean isLLMImageUnderstand() {
        return llmFlow.getValueFromInputMapWithDefault(CommonConstants.TY_LLM_IMAGE_UNDERSTAND_MARK, false,
                Boolean.class);
    }

    public VisionReq getVisionReq(String prompt, String model, String rstFormatDefinition, boolean generateCode) {
        List<VisionContent> contents = new ArrayList<>();
        llmFlow.getInputMap().forEach((key, value) -> {
            if (value.isTypeImage()) {
                String url = value.getValue().getAsString();
                ImageUtils.ImageData imageData = ImageUtils.downloadImageAsBase64(url);
                VisionContent image = VisionContent.builder().type("image").source(
                        ImmutableMap.of(
                                "type", "base64",
                                "media_type", imageData.getImagePrefix(),
                                "data", imageData.getBase64String()))
                        .build();
                log.info("Image reference found: key={}, value={}", key, value);
                contents.add(image);
            }
        });
        VisionReq req = VisionReq.builder()
                .model(model)
                .cmd("ImageUnderstanding")
                .temperature(llmFlow.getTemperature())
                .promptName(generateCode ? "yingjie" : "json4")
                .params(ImmutableMap.of("input", prompt, "rst_format_definition", rstFormatDefinition))
                .zzToken(llmFlow.getToken())
                .msgs(Lists.newArrayList(VisionMsg.builder().role("user").content(contents).build()))
                .build();
        return req;
    }

}
