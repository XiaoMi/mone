package run.mone.z.desensitization.service;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import run.mone.sautumnn.springboot.starter.anno.DubboService;
import run.mone.z.desensitization.api.bo.DesensitizeReq;
import run.mone.z.desensitization.api.service.CodeDesensitizeService;
import run.mone.z.desensitization.dto.DesensitizeRsp;
import run.mone.z.desensitization.service.common.CodeDesensitizeUtils;
import run.mone.z.desensitization.service.common.CodeExtractorUtils;
import run.mone.z.desensitization.service.common.Consts;
import run.mone.z.desensitization.service.common.NewCodeDesensitizeUtils;

import javax.annotation.Resource;

/**
 * @author wmin
 * @date 2023/6/5
 */
@DubboService(interfaceClass = CodeDesensitizeService.class, timeout = 5000, group = "${dubbo.group}", version = "1.0")
@Slf4j
public class CodeDesensitizeServiceImpl implements CodeDesensitizeService {

    @Resource
    private AiCodeDesensitizeService aiCodeDesensitizeService;

    @Resource
    private RecordService recordService;

    private Gson gson = new Gson();
    @Override
    public Result<String> codeDesensitize(String codeSnippet) {
        try {
            String codeSnippetAfterDesensitized = CodeDesensitizeUtils.codeDesensitizeForClass(codeSnippet);
            if (StringUtils.isNoneBlank(codeSnippetAfterDesensitized)){
                return Result.success(codeSnippetAfterDesensitized);
            }
            return Result.fail(GeneralCodes.ParamError, "The code does not comply with the syntax requirements");
        } catch (Exception e) {
            log.error("codeDesensitize error.", e);
            return Result.fail(GeneralCodes.InternalError, HttpResponseStatus.INTERNAL_SERVER_ERROR.reasonPhrase());
        }
    }

    public Result<String> codeDesensitizeWithSpecConfig(DesensitizeReq req) {
        try {
            if (StringUtils.isBlank(req.getLangType())) {
                log.warn("lang type is not specified! currently supported: java / go; WILL USE: java as lang type to desensitize!");
                req.setLangType(Consts.LANG_JAVA);
            }
            String codeSnippetAfterDesensitized = req.getText();
            switch (req.getLangType()) {
                case Consts.LANG_GO:
                    codeSnippetAfterDesensitized = NewCodeDesensitizeUtils.codeDesensitizeForGo(req);
                    break;
                case Consts.LANG_JAVA:
                default:
                    codeSnippetAfterDesensitized = NewCodeDesensitizeUtils.codeDesensitizeForClass(req);
            }
            if (StringUtils.isNoneBlank(codeSnippetAfterDesensitized)){
                return Result.success(codeSnippetAfterDesensitized);
            }
            return Result.fail(GeneralCodes.ParamError, "The code does not comply with the syntax requirements");
        } catch (Exception e) {
            log.error("codeDesensitize error.", e);
            return Result.fail(GeneralCodes.InternalError, HttpResponseStatus.INTERNAL_SERVER_ERROR.reasonPhrase());
        }
    }

    @Override
    public Result<String> textDesensitize(String text) {
        try {
            Pair<Boolean, String> rst =  CodeExtractorUtils.codeExtractor(text);
            if (rst.getKey()){
                String[] index = rst.getValue().split("-");
                String codeSnippet = text.substring(Integer.parseInt(index[0]), Integer.parseInt(index[1])+1);
                Result<String> codeRst = codeDesensitize(codeSnippet);
                if (codeRst.getCode()==0){
                    StringBuilder sb = new StringBuilder(text);
                    sb.replace(Integer.parseInt(index[0]), Integer.parseInt(index[1])+2, codeRst.getData());
                    return Result.success(sb.toString());
                }
                return codeRst;
            }
            return Result.fail(GeneralCodes.ParamError, "codeExtractor error");
        } catch (Exception e) {
            log.error("textDesensitize error.", e);
            return Result.fail(GeneralCodes.InternalError, HttpResponseStatus.INTERNAL_SERVER_ERROR.reasonPhrase());
        }
    }

    @Override
    public Result<String> textDesensitizeWithAi(DesensitizeReq req) {
        long startTime = System.currentTimeMillis();
        Result<String> rst = null;
        DesensitizeRsp rsp = DesensitizeRsp.builder()
                .textBefore(req.getText())
                .username(req.getUsername())
                .build();
        try {
            rst = textDesensitizeWithAiBase(req);
            return rst;
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            rsp.setDurationTime(duration);
            rsp.setStatus(rst.getCode()==0?1:0);
            rsp.setTextAfter(rst.getData());
            log.info("textDesensitizeWithAi status:{},duration:{}", rsp.getStatus(), duration);
            recordService.saveRecord(rsp);
        }
    }

    public Result<String> textDesensitizeWithAiBase(DesensitizeReq req) {
        log.info("textDesensitizeWithAi req:{}", gson.toJson(req));
        Preconditions.checkArgument(null!=req && StringUtils.isNotBlank(req.getText()), "text can not be null");
        String text = req.getText();
        Boolean aiFlag = req.getAiDesensitizeFlag();
        //保险起见，默认本地失败，只要失败就调ai
        Boolean isLocalFailed = true;
        String textAfterDe = text;

        try {
            // 如果需要提取代码，则提取; 否则直接脱敏
            if (req.getNeedExtract() != null && Boolean.TRUE.equals(req.getNeedExtract())) {
                Pair<Boolean, String> rst = CodeExtractorUtils.codeExtractorWithLabel(text);
                log.info("Perform code extraction with res:{}", rst);
                if (rst.getKey()) {
                    String[] index = rst.getValue().split("-");
                    String codeSnippet = text.substring(Integer.parseInt(index[0]), Integer.parseInt(index[1]));
                    log.info("codeSnippet after codeExtractorWithLabel:{}", codeSnippet);
                    req.setText(codeSnippet);
                    Result<String> codeRst = codeDesensitizeWithSpecConfig(req);
                    if (codeRst.getCode() == 0) {
                        StringBuilder sb = new StringBuilder(text);
                        sb.replace(Integer.parseInt(index[0]), Integer.parseInt(index[1]) + 1, codeRst.getData());
                        isLocalFailed = false;
                        textAfterDe = sb.toString();
                    }
                } else {
                    log.error("local textDesensitizeWithAi failed.");
                }
            } else {
                log.info("Perform desensitization directly with req!");
                Result<String> codeRst = codeDesensitizeWithSpecConfig(req);
                isLocalFailed = false;
                textAfterDe = codeRst.getData();
            }
        } catch (Exception e) {
            log.error("textDesensitizeWithAi error.", e);
        }
        //if (BooleanUtils.isTrue(isLocalFailed) || BooleanUtils.isTrue(aiFlag)){
        if (BooleanUtils.isTrue(aiFlag)){
            //call ai http
            textAfterDe = aiCodeDesensitizeService.aiCodeDesensitize(textAfterDe);
        }
        if (BooleanUtils.isTrue(isLocalFailed) && BooleanUtils.isFalse(aiFlag)){
            return Result.fail(GeneralCodes.InternalError, "local desensitization failed, and is configured not to call AI desensitization!");
        }
        return Result.success(textAfterDe);
    }
}
