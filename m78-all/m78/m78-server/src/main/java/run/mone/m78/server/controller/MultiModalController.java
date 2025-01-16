package run.mone.m78.server.controller;

import com.xiaomi.mone.http.docs.annotations.HttpApiModule;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import run.mone.m78.api.bo.ListResult;
import run.mone.m78.api.bo.multiModal.image.BotImageResult;
import run.mone.m78.api.bo.multiModal.M78MultiModalHistoryInfo;
import run.mone.m78.api.bo.multiModal.audio.AudioToTextReq;
import run.mone.m78.api.bo.multiModal.image.*;
import run.mone.m78.api.enums.MultiModalCmdTypeEnum;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.exceptions.UserAuthException;
import run.mone.m78.service.service.fileserver.FileUtils;
import run.mone.m78.service.service.fileserver.RemoteFileService;
import run.mone.m78.service.service.multiModal.AudioModalService;
import run.mone.m78.service.service.multiModal.ImageModalService;
import run.mone.m78.service.service.multiModal.MultiModalHistoryService;
import run.mone.m78.service.service.user.LoginService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static run.mone.m78.api.constant.CommonConstant.API_PREFIX;
import static run.mone.m78.api.enums.ImageTypeEnum.AVATAR;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_FORBIDDEN;
import static run.mone.m78.service.service.fileserver.FileUtils.IMAGE_TYPE_PREFIX;

@Slf4j
@RestController
@RequestMapping(value = API_PREFIX + "/multiModal")
@HttpApiModule(value = "MultiModalController", apiController = MultiModalController.class)
public class MultiModalController {

    @Resource
    private ImageModalService imageModalService;

    @Resource
    private RemoteFileService fileService;

    @Autowired
    private LoginService loginService;

    @Resource
    private MultiModalHistoryService modalHistoryService;

    @Resource
    private AudioModalService audioModalService;

    @RequestMapping(value = "/imageUnderstanding", method = RequestMethod.POST)
    public Result<String> imageUnderstanding(HttpServletRequest request,
                                             @RequestBody ImageUnderstandingReq imageUnderstandingReq) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        imageUnderstandingReq.setUserName(account.getUsername());
        imageUnderstandingReq.setCmdType(MultiModalCmdTypeEnum.IMAGE_UNDERSTAND.getCode());
        return imageModalService.imageUnderstanding(imageUnderstandingReq);
    }

    @RequestMapping(value = "/artWord", method = RequestMethod.POST)
    public Result<String> artWord(HttpServletRequest request,
                                             @RequestBody ArtWordReq artWordReq) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        artWordReq.setUserName(account.getUsername());
        artWordReq.setCmdType(MultiModalCmdTypeEnum.ART_WORD.getCode());
        return imageModalService.artWord(artWordReq);
    }

    @RequestMapping(value = "/backgroundGen", method = RequestMethod.POST)
    public Result<String> backgroundGen(HttpServletRequest request,
                                             @RequestBody BackgroundGenReq backgroundGenReq) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        backgroundGenReq.setUserName(account.getUsername());
        backgroundGenReq.setCmdType(MultiModalCmdTypeEnum.BACKGROUND_GEN.getCode());
        return imageModalService.backgroundGen(backgroundGenReq);
    }

    @RequestMapping(value = "/sketchToImage", method = RequestMethod.POST)
    public Result<String> sketchToImage(HttpServletRequest request,
                                             @RequestBody SketchToImageReq sketchToImageReq) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        sketchToImageReq.setUserName(account.getUsername());
        sketchToImageReq.setCmdType(MultiModalCmdTypeEnum.SKETCH_TO_IMAGE.getCode());
        return imageModalService.sketchToImage(sketchToImageReq);
    }

    @RequestMapping(value = "/textAndImage", method = RequestMethod.POST)
    public Result<String> textAndImage(HttpServletRequest request,
                                        @RequestBody TextAndImageReq textAndImageReq) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        textAndImageReq.setUserName(account.getUsername());
        textAndImageReq.setCmdType(MultiModalCmdTypeEnum.TEXT_AND_IMAGE.getCode());
        return imageModalService.textAndImage(textAndImageReq);
    }

    @RequestMapping(value = "/styleRepaint", method = RequestMethod.POST)
    public Result<String> styleRepaint(HttpServletRequest request,
                                        @RequestBody StyleRepaintReq styleRepaintReq) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        styleRepaintReq.setUserName(account.getUsername());
        styleRepaintReq.setCmdType(MultiModalCmdTypeEnum.STYLE_REPAINT.getCode());
        return imageModalService.styleRepaint(styleRepaintReq);
    }

    @RequestMapping(value = "/textToImage", method = RequestMethod.POST)
    public Result<String> textToImage(HttpServletRequest request,
                                        @RequestBody TextToImageReq textToImageReq) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        textToImageReq.setUserName(account.getUsername());
        textToImageReq.setCmdType(MultiModalCmdTypeEnum.TEXT_TO_IMAGE.getCode());
        return imageModalService.textToImage(textToImageReq);
    }

    @GetMapping(value = "/avatar")
    public Result<BotImageResult> generateAvatar(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "desc") String desc,
            @RequestParam(value = "type", defaultValue = "normal") String type,
            HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("User not logged in while trying to get agent list");
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        String imageBas64 = imageModalService.genAvatar(name, desc, type);
        if (imageBas64 == null) {
            return Result.fail(GeneralCodes.InternalError, "generate image error");
        }
        // 缩小图片比例为100 * 100
        imageBas64 = IMAGE_TYPE_PREFIX + FileUtils.resizeImageBase64(imageBas64, 100, 100);
        String stringResult = fileService.uploadImageFileByBase64(AVATAR, imageBas64, true);
        if (stringResult != null) {
            BotImageResult result = BotImageResult.builder()
                    .base64(imageBas64)
                    .url(stringResult)
                    .build();
            return Result.success(result);
        }
        return Result.fail(GeneralCodes.InternalError, "upload oss error");
    }

    //根据task id查询
	@RequestMapping(value = "/taskInfo", method = RequestMethod.GET)
	public Result<M78MultiModalHistoryInfo> getTaskInfo(HttpServletRequest request,
                                                        @RequestParam("taskId") String taskId) {
	    SessionAccount account = loginService.getAccountFromSession(request);
	    if (account == null) {
	        return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
	    }
	    return modalHistoryService.getByTaskId(taskId);
	}

    //分页查询list
	@RequestMapping(value = "/historyList", method = RequestMethod.POST)
	public Result<ListResult<M78MultiModalHistoryInfo>> getHistoryList(HttpServletRequest request, @RequestBody HistoryQryReq req) {
	    SessionAccount account = loginService.getAccountFromSession(request);
	    if (account == null) {
	        return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
	    }
        req.setUserName(account.getUsername());
	    return modalHistoryService.getHistoryList(req);
	}

    //根据historyIds获取详情
	@RequestMapping(value = "/historyDetailsByIds", method = RequestMethod.POST)
	public Result<List<M78MultiModalHistoryInfo>> historyDetailsByIds(HttpServletRequest request, @RequestBody List<Long> historyIds) {
	    SessionAccount account = loginService.getAccountFromSession(request);
	    if (account == null) {
	        return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
	    }
	    return modalHistoryService.getHistoryListByIds(historyIds);
	}

    //根据ids删除
	@RequestMapping(value = "/deleteByIds", method = RequestMethod.POST)
	public Result<List<Long>> deleteByIds(HttpServletRequest request, @RequestBody List<Long> ids) {
	    SessionAccount account = loginService.getAccountFromSession(request);
	    if (account == null) {
	        return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
	    }
	    return modalHistoryService.deleteByIds(ids);
	}


    @RequestMapping(value = "/workChartGen", method = RequestMethod.POST)
    public Result<String> workChartGen(HttpServletRequest request,
                                             @RequestBody WorkChartReq workChartReq) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        workChartReq.setUserName(account.getUsername());
        workChartReq.setCmdType(MultiModalCmdTypeEnum.WORK_CHART.getCode());
        return imageModalService.workChartReqGen(workChartReq);
    }

    // asr语音识别
    @RequestMapping(value = "/audioToText", method = RequestMethod.POST)
    public Result<String> audioToText(HttpServletRequest request,
                                      @RequestParam("audioFile") MultipartFile audioFile,
                                      @RequestParam("asrPlatform") String asrPlatform) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        AudioToTextReq audioToTextReq = AudioToTextReq.builder()
                .audioFile(audioFile)
                .asrPlatform(asrPlatform).build();

        return audioModalService.audioToText(audioToTextReq, account.getUsername());
    }

    // asr语音识别
    @RequestMapping(value = "/audioToTextPlatform", method = RequestMethod.POST)
    public Result<List<String>> getAudioToTextPlatform(HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        return audioModalService.getAllAsrPlatformList();
    }


}

