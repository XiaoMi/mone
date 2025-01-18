package run.mone.m78.service.service.multiModal;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.data.push.client.HttpClientV2;
import com.xiaomi.data.push.client.HttpClientV6;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import run.mone.m78.api.bo.multiModal.image.*;
import run.mone.m78.service.common.Config;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.dao.entity.M78MultiModalHistoryPo;
import run.mone.m78.service.service.base.ChatgptService;
import run.mone.m78.service.service.base.ProxyAiService;
import run.mone.m78.service.service.fileserver.FileUtils;
import run.mone.m78.service.utils.NetUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static run.mone.m78.api.constant.CommonConstant.GENERATE_PIC_TIMEOUT;
import static run.mone.m78.api.constant.PromptConstant.PROMPT_WORK_CHART_MERMAID_GEN;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_INTERNAL_ERROR;

@Service
@Slf4j

/**
 * ImageModalService类提供了多种图像生成和处理功能，包括生成头像、处理图像理解请求、生成工作图表、处理艺术字请求、生成背景图、将草图转换为图像、处理文本和图像请求、重新绘制样式以及将文本转换为图像。
 * 该类依赖于多个外部服务和配置，如MultiModalHistoryService、ChatgptService、GsonUtils等。
 * 主要功能包括：
 * - 初始化服务配置
 * - 根据不同的请求类型生成相应的图像或图表
 * - 处理异步和同步的图像生成请求
 * - 提供图像的Base64编码转换
 * - 记录和更新多模态历史记录
 *
 * 该类使用了Spring的@Service注解进行标注，表明它是一个服务类，并使用了@Slf4j注解来进行日志记录。
 */

public class ImageModalService {

    private static Gson gson = GsonUtils.gson;

    private static List<String> TEXT_TO_IMAGE_STYLE = Arrays.asList("<photography>", "<portrait>", "<3d cartoon>", "<anime>", "<oil painting>", "<watercolor>", "<sketch>", "<chinese painting>", "<flat illustration>");

    @Value("${multiModal.notifyByTaskId.uri:}")
    private String multiModalNotifyByTaskIdUri;

    @Resource
    private MultiModalHistoryService modalHistoryService;

    @Resource
    private ChatgptService chatgptService;

    @Value("${server.port}")
    private String httpPort;

    @Value("${word.to.picture.url}")
    private String wordToPictureUrl;

    private String multiModalNotifyByTaskIdUrl;

    /**
     * 初始化方法，在对象创建后自动调用。
     * 获取本地IP地址并构建multiModalNotifyByTaskIdUrl。
     * 如果获取IP地址失败，记录错误日志。
     */
    @PostConstruct
    public void init() {
        try {
            String localIp = NetUtils.getLocalHost();
            multiModalNotifyByTaskIdUrl = "http://" + localIp + ":" + httpPort + multiModalNotifyByTaskIdUri;
            log.info("multiModalNotifyByTaskIdUrl:{}", multiModalNotifyByTaskIdUrl);
        } catch (Exception e) {
            log.error("UnknownHostException ", e);
        }
    }

    /**
     * 根据机器人名称和描述生成头像
     *
     * @param botName 机器人的名称
     * @param botDesc 机器人的描述
     * @param type    生成头像的类型
     * @return 生成的头像的Base64编码字符串，如果生成失败则返回null
     */
    public String genAvatar(String botName, String botDesc, String type) {

        if ("wanx".equals(type)) {
            TextToImageReq req = TextToImageReq.builder()
                    .input(getPrompt(botName, botDesc))
                    .size("1024*1024")
                    .cmd("TextToImage")
                    .style(getRandomTextToImageStyle())
                    .num(1).build();
            req.setModel("wanx_image");
            req.setAsyncCall(false);
            String answer = syncWanxImage(req).getData();
            List<String> urls = gson.fromJson(answer, new TypeToken<List<String>>() {
            }.getType());
            return getFileBase64ByUrl(urls.get(0));
        }

        Map<String, String> requestBodyMap = new HashMap<>();
        requestBodyMap.put("prompt", getPrompt(botName, botDesc));
        String body = GsonUtils.gson.toJson(requestBodyMap);

        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");

        try {
            String result = HttpClientV6.post(wordToPictureUrl, body, header, GENERATE_PIC_TIMEOUT);
            if (result != null) {
                JsonObject jsonObject = GsonUtils.gson.fromJson(result, JsonObject.class);
                int code = jsonObject.get("code").getAsInt();
                if (code == 0) {
                    return jsonObject.get("data").getAsString();
                } else {
                    log.error("request generate image error, result is : " + result);
                }
            }
        } catch (Throwable t) {
            log.error("request generate image error , ", t);
        }
        return null;
    }

    private String getPrompt(String botName, String botDesc) {
        if (botDesc.length() > 100) {
            botDesc = botDesc.substring(0, 100);
        }
        String template = "为应用生成一个图标。应用的名称是：%s，应用的描述是：%s。";
        String prompt = String.format(template, botName, botDesc);
        return prompt;
    }

    private ImmutableMap<String, String> imageMsg(String url) {
        String type = FileUtils.getFileTypeFromUrl(url);
        if (StringUtils.isEmpty(type)) {
            return ImmutableMap.of("type", "unknown");
        }

        return ImmutableMap.of("type", "base64", "media_type", "image/" + type, "data", getFileBase64ByUrl(url));
    }

    /**
     * 处理图像理解请求
     *
     * @param imageUnderstandingReq 图像理解请求对象，包含图像URL列表、模型、用户名、命令和输入等信息
     * @return 包含图像理解结果的Result对象，如果处理成功返回结果字符串，否则返回空字符串
     */
    public Result<String> imageUnderstanding(ImageUnderstandingReq imageUnderstandingReq) {
        List content = imageUnderstandingReq.getImageUrls().stream().map(image -> {
            return ImmutableMap.of("type", "image",
                    "source", imageMsg(image));
        }).collect(Collectors.toList());

        Map<String, Object> req = new HashMap<>();
        req.put("model", imageUnderstandingReq.getModel());
        req.put("userName", imageUnderstandingReq.getUserName());
        req.put("cmd", imageUnderstandingReq.getCmd());
        req.put("input", imageUnderstandingReq.getInput());
        req.put("temperature", 0.2);
        req.put("msgs", Arrays.asList(ImmutableMap.of("role", "user", "content", content)));
        req.put("zzToken", Config.zToken);

        M78MultiModalHistoryPo newPo = buildM78MultiModalHistoryPo(imageUnderstandingReq);
        newPo.setRunStatus(0);
        Result<Integer> historyId = modalHistoryService.insert(newPo, false);

        Map<String, String> res = ProxyAiService.multiModalImage(GsonUtils.gson.toJson(req));
        if (res != null && StringUtils.isNotBlank(res.get("answer"))) {
            String answer = res.get("answer").toString();
            newPo.setRunStatus(1);
            newPo.setMultiModalResourceOutput(Arrays.asList(answer));
            log.info("execute done.modalHistory save rst:{}", modalHistoryService.update(newPo));
            return Result.success(answer);
        }
        return Result.success("");
    }

    /**
     * 处理工作图表请求并生成相应的图表数据
     *
     * @param workChartReq 包含图表请求信息的对象
     * @return 包含生成的图表数据的结果对象，如果生成失败则返回错误信息
     */
    public Result<String> workChartReqGen(WorkChartReq workChartReq) {
        Map<String, String> chatgptParams = ImmutableMap.of(
                "chartType", workChartReq.getChartType(),
                "input", workChartReq.getInput());
        Result<String> aiRes = chatgptService.call(PROMPT_WORK_CHART_MERMAID_GEN, chatgptParams, "mermaid", workChartReq.getModel());
        if (aiRes.getCode() == 0 && StringUtils.isNotBlank(aiRes.getData())) {
            M78MultiModalHistoryPo po = buildM78MultiModalHistoryPo(workChartReq);
            po.setRunStatus(1);
            po.setMultiModalResourceOutput(Arrays.asList(aiRes.getData()));
            log.info("workChartReqGen done.modalHistory save rst:{}", modalHistoryService.insert(po, false));
            return Result.success(aiRes.getData());
        }
        return Result.fail(STATUS_INTERNAL_ERROR, "");
    }

    /**
     * 处理艺术字请求
     *
     * @param artWordReq 艺术字请求对象，包含请求参数
     * @return 异步处理结果，包含字符串类型的结果
     */
    public Result<String> artWord(ArtWordReq artWordReq) {
        artWordReq.setZzToken(Config.zToken);
        return asyncWanxImage(artWordReq);
    }

    /**
     * 生成背景图
     *
     * @param backgroundGenReq 背景生成请求对象，包含生成背景图所需的参数
     * @return 异步生成背景图的结果
     */
    public Result<String> backgroundGen(BackgroundGenReq backgroundGenReq) {
        backgroundGenReq.setZzToken(Config.zToken);
        return asyncWanxImage(backgroundGenReq);
    }

    /**
     * 将草图转换为图像
     *
     * @param sketchToImageReq 包含草图信息的请求对象
     * @return 包含转换结果的Result对象
     */
    public Result<String> sketchToImage(SketchToImageReq sketchToImageReq) {
        sketchToImageReq.setZzToken(Config.zToken);
        return asyncWanxImage(sketchToImageReq);
    }

    /**
     * 处理文本和图像请求
     *
     * @param textAndImageReq 文本和图像请求对象，包含请求的详细信息
     * @return 异步处理结果，包含处理后的字符串结果
     */
    public Result<String> textAndImage(TextAndImageReq textAndImageReq) {
        textAndImageReq.setZzToken(Config.zToken);
        return asyncWanxImage(textAndImageReq);
    }

    /**
     * 重新绘制样式
     *
     * @param styleRepaintReq 样式重新绘制请求对象
     * @return 异步处理结果，包含字符串类型的结果
     */
    public Result<String> styleRepaint(StyleRepaintReq styleRepaintReq) {
        styleRepaintReq.setZzToken(Config.zToken);
        return asyncWanxImage(styleRepaintReq);
    }

    /**
     * 将文本转换为图像
     *
     * @param textToImageReq 包含文本转换为图像请求的参数
     * @return 包含图像结果的Result对象
     */
    public Result<String> textToImage(TextToImageReq textToImageReq) {
        textToImageReq.setZzToken(Config.zToken);
        return asyncWanxImage(textToImageReq);
    }

    private Result<String> asyncWanxImage(BaseReq baseReq) {
        baseReq.setCallbackUrl(multiModalNotifyByTaskIdUrl);
        baseReq.setGenerateInnerImageUrl(false);
        String req = GsonUtils.gson.toJson(baseReq);
        Map<String, String> res = ProxyAiService.multiModalImage(req);
        M78MultiModalHistoryPo po = buildM78MultiModalHistoryPo(baseReq);
        if (res != null && StringUtils.isNotBlank(res.get("taskId"))) {
            po.setRunStatus(0);
            po.setTaskId(res.get("taskId"));
            log.info("submit success. taskId:{}, modalHistory save rst:{}", res.get("taskId"),
                    modalHistoryService.insert(po, true));
            return Result.success(res.get("taskId"));
        }
        return Result.success("");
    }

    private Result<String> syncWanxImage(BaseReq baseReq) {
        baseReq.setGenerateInnerImageUrl(false);
        String req = GsonUtils.gson.toJson(baseReq);
        Map<String, String> res = ProxyAiService.multiModalImage(req);
        if (res != null && StringUtils.isNotBlank(res.get("answer"))) {

            log.info("submit success. answer:{}", res.get("answer"));
            return Result.success(res.get("answer"));
        }
        return Result.success("");
    }

    //给定url,获取Base64字符串
    private static String getFileBase64ByUrl(String url) {
        byte[] fileBytes = HttpClientV2.download(url, 3000);
        if (fileBytes == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(fileBytes);
    }

    private M78MultiModalHistoryPo buildM78MultiModalHistoryPo(BaseReq baseReq) {
        return M78MultiModalHistoryPo.builder()
                .aiModel(baseReq.getModel())
                .userName(baseReq.getUserName())
                .type(baseReq.getCmdType())
                .setting(baseReq).build();
    }

    /**
     * 随机从TEXT_TO_IMAGE_STYLE列表中取一个值并返回
     *
     * @return 随机选择的TEXT_TO_IMAGE_STYLE列表中的值
     */
    //随机从TEXT_TO_IMAGE_STYLE取一个值并返回
    public String getRandomTextToImageStyle() {
        int randomIndex = new Random().nextInt(TEXT_TO_IMAGE_STYLE.size());
        return TEXT_TO_IMAGE_STYLE.get(randomIndex);
    }


}
