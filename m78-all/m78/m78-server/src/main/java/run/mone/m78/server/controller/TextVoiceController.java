package run.mone.m78.server.controller;

import com.xiaomi.mone.http.docs.annotations.HttpApiDoc;
import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import com.xiaomi.mone.http.docs.annotations.MiApiRequestMethod;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import run.mone.m78.api.bo.multiModal.audio.textVoice.TextToVoiceParam;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.exceptions.UserAuthException;
import run.mone.m78.service.service.user.LoginService;
import run.mone.m78.service.service.multiModal.voice.VoiceService;

import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

import static run.mone.m78.api.constant.CommonConstant.API_PREFIX;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_FORBIDDEN;

/**
 * @author zhangxiaowei6
 * @Date 2024/5/8 14:52
 */

// 用于语音转文字与文字转语音
@Slf4j
@RestController
@RequestMapping(value = API_PREFIX + "/textVoice")
public class TextVoiceController {

    @Autowired
    private VoiceService voiceService;

    @Autowired
    private LoginService loginService;

    // 文字转语音,文字的UTF-8编码长度不能超过2048
    @PostMapping(value = "/textToVoice")
    public Result<String> textToVoice(@RequestBody TextToVoiceParam param, HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        return voiceService.textToVoice(param);
    }

    private Map<String, String[]> createRequestParams(String text) {
        String q = text;
        // 音色选择 https://ai.youdao.com/DOCSIRMA/html/tts/api/yyhc/index.html
        String voiceName = "youxiaozhi";
        String format = "mp3";

        return new HashMap<String, String[]>() {{
            put("q", new String[]{q});
            put("voiceName", new String[]{voiceName});
            put("format", new String[]{format});
        }};
    }

    @PostMapping("/base64/to/word")
    @HttpApiDoc(value = "/base64/to/word", method = MiApiRequestMethod.POST, apiName = "bas64语音转文字")
    public Result<String> voiceBase64ToWord(HttpServletRequest request,
                                            @HttpApiDocClassDefine(value = "voice", description = "语音base64") @RequestParam("voice") String voice
    ) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        return voiceService.voiceToWord(voice);
    }

    @PostMapping("/to/word")
    @HttpApiDoc(value = "/to/word", method = MiApiRequestMethod.POST, apiName = "语音文件转文字")
    public Result<String> voiceToWord(HttpServletRequest request,
                                      @HttpApiDocClassDefine(value = "voice", description = "语音文件") @RequestParam("voice") MultipartFile voice
    ) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        return voiceService.voiceToWord(voice);
    }
}
