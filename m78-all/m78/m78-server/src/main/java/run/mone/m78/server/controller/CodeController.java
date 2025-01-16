package run.mone.m78.server.controller;

import com.google.gson.Gson;
import com.mybatisflex.core.paginate.Page;
import com.xiaomi.mone.http.docs.annotations.HttpApiDoc;
import com.xiaomi.mone.http.docs.annotations.HttpApiModule;
import com.xiaomi.mone.http.docs.annotations.MiApiRequestMethod;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import run.mone.m78.api.bo.code.CodeDTO;
import run.mone.m78.api.bo.code.CodeReq;
import run.mone.m78.api.bo.code.GenerateMethodParam;
import run.mone.m78.api.bo.code.ReqCodeListDto;
import run.mone.m78.service.bo.code.Code;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.common.MappingUtils;
import run.mone.m78.service.dao.entity.M78Code;
import run.mone.m78.service.service.code.CodeService;
import run.mone.m78.service.service.code.M78CodeService;
import run.mone.m78.service.service.user.LoginService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static run.mone.m78.api.constant.CommonConstant.API_PREFIX;
import static run.mone.m78.service.exceptions.ExCodes.*;


@Slf4j
@RestController
@RequestMapping(value = API_PREFIX + "/code")
@HttpApiModule(value = "CodeController", apiController = CodeController.class)
public class CodeController {

    private static Gson gson = GsonUtils.gson;

    @Autowired
    private CodeService codeService;

    @Autowired
    private LoginService loginService;

    @Resource
    private M78CodeService m78CodeService;

    @RequestMapping(value = {"/generateMethod/stream"}, method = RequestMethod.POST, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> generateMethodStream(HttpServletRequest request,
                                                           @RequestBody GenerateMethodParam generateMethodParam) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-Accel-Buffering", "no");
        httpHeaders.setCacheControl(CacheControl.noCache());

        SessionAccount account = loginService.getAccountFromSession(request);
        generateMethodParam.setUser(account.getUsername());
        SseEmitter emitter = codeService.generateMethodStream(generateMethodParam);

        return ResponseEntity.ok().contentType(MediaType.TEXT_EVENT_STREAM).headers(httpHeaders).body(emitter);
    }

    // 根据输入内容生成代码
    @PostMapping("/generate")
    @HttpApiDoc(apiName = "生成M78Code", value = "/api/v1/code/generate", method = MiApiRequestMethod.POST, description = "生成M78Code")
    public Result<M78Code> generateM78Code(HttpServletRequest request, @RequestBody CodeReq codeReq) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("User not logged in while trying to saveOrUpdateM78Code");
            return Result.fail(STATUS_FORBIDDEN, "User not authenticated");
        }
        if (StringUtils.isBlank(codeReq.getComment())) {
            log.warn("Empty incoming comment, will do nothing!");
            return Result.fail(STATUS_NOT_FOUND, "Empty incoming comment, will do nothing! Pls insert comment to generate code!");
        }
        Result<M78Code> res = m78CodeService.generateCodeByPrompt(codeReq.getComment(), codeReq.getModel(), account.getUsername(), codeReq.getSaveDB());
        log.info("generate m78 code res:{}", res);
        return Result.success(res.getData());
    }

    //保存或更新M78Code (project)
    @PostMapping("/saveOrUpdate")
    @HttpApiDoc(apiName = "保存或更新M78Code", value = "/api/v1/code/saveOrUpdate", method = MiApiRequestMethod.POST, description = "保存或更新M78Code")
    public Result<Boolean> saveOrUpdateM78Code(HttpServletRequest request, @RequestBody CodeDTO m78Code) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("User not logged in while trying to saveOrUpdateM78Code");
            return Result.fail(STATUS_FORBIDDEN, "User not authenticated");
        }
        long now = System.currentTimeMillis();
        if (m78Code.getId() != null) {
            M78Code existingCode = m78CodeService.getM78CodeByIdAndCreator(m78Code.getId(), account.getUsername()).getData();
            if (existingCode != null) {
                BeanUtils.copyProperties(m78Code, existingCode, "id", "creator");
                existingCode.setCode(gson.fromJson(gson.toJson(m78Code.getCode()), Code.class));
                existingCode.setUtime(now);
                return m78CodeService.updateM78Code(existingCode);
            } else {
                return Result.fail(STATUS_NOT_FOUND, "Code not found or you're not the creator");
            }
        } else {
            m78Code.setCreator(account.getUsername());
            m78Code.setCtime(now);
            m78Code.setUtime(now);
            return m78CodeService.createM78Code(MappingUtils.map(m78Code, M78Code.class));
        }
    }

    //根据id删除某个M78Code (class)
    @PostMapping("/deleteById")
    @HttpApiDoc(apiName = "根据id删除M78Code", value = "/api/v1/code/deleteById", method = MiApiRequestMethod.POST, description = "根据id删除M78Code, id参数放在url中")
    public Result<Boolean> deleteM78CodeById(HttpServletRequest request, @RequestParam Long id) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("User not logged in while trying to delete M78Code");
            return Result.fail(STATUS_FORBIDDEN, "User not authenticated");
        }
        return m78CodeService.deleteCodeByIdAndCreator(id, account.getUsername());
    }

    // 根据id查询某个M78Code,返回结果转化为CodeDTO (class)
    @PostMapping("/getById")
    @HttpApiDoc(apiName = "根据id查询M78Code", value = "/api/v1/code/getById", method = MiApiRequestMethod.POST, description = "根据id查询M78Code, id参数放在url中")
    public Result<CodeDTO> getM78CodeById(HttpServletRequest request, @RequestParam Long id) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("User not logged in while trying to get M78Code");
            return Result.fail(STATUS_FORBIDDEN, "User not authenticated");
        }
        Result<M78Code> m78CodeResult = m78CodeService.getM78CodeByIdAndCreator(id, account.getUsername());
        if (GeneralCodes.OK.getCode() == m78CodeResult.getCode() && m78CodeResult.getData() != null) {
            CodeDTO codeDTO = MappingUtils.map(m78CodeResult.getData(), CodeDTO.class);
            return Result.success(codeDTO);
        } else {
            return Result.fail(STATUS_INTERNAL_ERROR, m78CodeResult.getMessage());
        }
    }

    @PostMapping("/list")
    @HttpApiDoc(apiName = "列表查询", value = "/api/v1/code/list", method = MiApiRequestMethod.POST)
    public Result<Page<CodeDTO>> getM78CodeById(HttpServletRequest request, @RequestBody ReqCodeListDto reqCodeListDto) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("User not logged in while trying to get M78Code");
            return Result.fail(STATUS_FORBIDDEN, "User not authenticated");
        }

        return Result.success(m78CodeService.listCode(reqCodeListDto, account.getUsername()));
    }


    // 根据userName查询某个M78Code,返回结果转化为CodeDTO (class)
    @PostMapping("/getByUserName")
    @HttpApiDoc(apiName = "根据userName查询M78Code", value = "/api/v1/code/getByUserName", method = MiApiRequestMethod.POST, description = "根据userName查询M78Code, userName参数放在url中")
    public Result<CodeDTO> getM78CodeByUserName(HttpServletRequest request, @RequestParam String userName) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("User not logged in while trying to get M78Code by userName");
            return Result.fail(STATUS_FORBIDDEN, "User not authenticated");
        }
        Result<List<M78Code>> m78CodeListResult = m78CodeService.getM78CodeByCreator(userName);
        if (GeneralCodes.OK.getCode() == m78CodeListResult.getCode() && m78CodeListResult.getData() != null && !m78CodeListResult.getData().isEmpty()) {
            CodeDTO codeDTO = MappingUtils.map(m78CodeListResult.getData().get(0), CodeDTO.class);
            return Result.success(codeDTO);
        } else {
            return Result.fail(STATUS_NOT_FOUND, "Code not found for the given userName or no access rights");
        }
    }
}
