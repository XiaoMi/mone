package run.mone.m78.server.controller;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.xiaomi.mone.http.docs.annotations.HttpApiDoc;
import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import com.xiaomi.mone.http.docs.annotations.HttpApiModule;
import com.xiaomi.mone.http.docs.annotations.MiApiRequestMethod;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import run.mone.m78.api.bo.bot.BotCreateOneSentenceReq;
import run.mone.m78.api.bo.bot.ImTypeDTO;
import run.mone.m78.api.bo.bot.ReqBotListDto;
import run.mone.m78.api.enums.UserRoleEnum;
import run.mone.m78.server.config.auth.RoleControl;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.dao.entity.M78ImType;
import run.mone.m78.service.dao.mapper.M78ImTypeMapper;
import run.mone.m78.service.dto.*;
import run.mone.m78.service.dto.presetQuestion.PresetQuestionRes;
import run.mone.m78.service.exceptions.UserAuthException;
import run.mone.m78.service.service.bot.BotService;
import run.mone.m78.service.service.plugins.BotPluginService;
import run.mone.m78.service.service.user.LoginService;
import run.mone.m78.service.service.workspace.WorkspaceService;
import run.mone.m78.service.vo.BotSimpleVo;
import run.mone.m78.service.vo.BotVo;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static run.mone.m78.api.constant.CommonConstant.API_PREFIX;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_FORBIDDEN;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-03-01 16:34
 */
@Slf4j
@RestController
@RequestMapping(value = API_PREFIX + "/bot")
@HttpApiModule(value = "BotController", apiController = BotController.class)
public class BotController {

    @Autowired
    private BotService botService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private BotPluginService pluginService;

    @Autowired
    private M78ImTypeMapper m78ImTypeMapper;

    @Autowired
    private WorkspaceService workspaceService;

    @RoleControl(role = UserRoleEnum.USER)
    @PostMapping("/create")
    @HttpApiDoc(value = "/api/v1/bot/create", method = MiApiRequestMethod.POST, apiName = "创建机器人")
    public Result<Long> createBot(HttpServletRequest request,
                                  @RequestBody BotDto botDto) {
        SessionAccount account = loginService.getAccountFromSession(request);
        log.info("Creating bot: {} user: {}", botDto, account);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        String username = account.getUsername();
        return Result.success(botService.createBot(username, botDto));
    }

    @RoleControl(role = UserRoleEnum.USER)
    @PostMapping("/createByOneSentence")
    @HttpApiDoc(value = "/api/v1/bot/createByOneSentence", method = MiApiRequestMethod.POST, apiName = "一句话创建机器人")
    public Result<Long> createByOneSentence(HttpServletRequest request,
                                            @RequestBody BotCreateOneSentenceReq sentence) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        String username = account.getUsername();
        return botService.createByOneSentence(sentence.getSentence(), username);
    }

    //执行某个Bot(class)
    @PostMapping("/execute")
    @HttpApiDoc(value = "/api/v1/bot/execute", method = MiApiRequestMethod.POST, apiName = "执行机器人")
    public Result<String> executeBot(HttpServletRequest request, @RequestParam("botId") Long botId, @RequestParam("input") String input, @RequestParam("topicId") String topicId) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        String username = account.getUsername();
        return botService.executeBot(null, botId, input, username, topicId);
    }

    //清空和某个Bot的所有对话(class)
    @PostMapping("/clearConversations")
    @HttpApiDoc(value = "/api/v1/bot/clearConversations", method = MiApiRequestMethod.POST, apiName = "清空机器人对话")
    public Result<Boolean> clearBotConversations(HttpServletRequest request, @RequestParam("botId") Long botId, @RequestParam("topicId") String topicId) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        String username = account.getUsername();
        return Result.success(botService.clearAllConversationsWithBot(botId, username, topicId));
    }


    @RoleControl(role = UserRoleEnum.USER)
    @PostMapping("/update")
    @HttpApiDoc(value = "/api/v1/bot/update", method = MiApiRequestMethod.POST, apiName = "更新机器人")
    public Result<Boolean> updateBot(HttpServletRequest request, @RequestBody BotDto botDto) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        String username = account.getUsername();
        return Result.success(botService.updateBot(username, botDto));
    }

    @PostMapping("/delete")
    @RoleControl(role = UserRoleEnum.ADMIN, strategy = "bot")
    @HttpApiDoc(value = "/api/v1/bot/delete", method = MiApiRequestMethod.POST, apiName = "删除机器人")
    public Result<Boolean> deleteBot(HttpServletRequest request, @HttpApiDocClassDefine(value = "botId", description = "机器人id") @RequestParam("botId") Long botId) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        String username = account.getUsername();
        return Result.success(botService.deleteBot(username, botId));
    }

    @GetMapping("/listBySpaceId")
    @HttpApiDoc(value = "/api/v1/bot/listBySpaceId", method = MiApiRequestMethod.GET, apiName = "获取工作空间下所有机器人")
    public Result<List<BotVo>> listBotBySpaceId(HttpServletRequest request, @HttpApiDocClassDefine(value = "workspaceId", description = "工作空间id") Long workspaceId) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        return Result.success(botService.listBotBySpaceId(workspaceId));
    }

    @GetMapping("/listAllBotsInMySpace")
    @HttpApiDoc(value = "/api/v1/bot/listAllBotsInMySpace", method = MiApiRequestMethod.GET, apiName = "获取自己工作空间下所有机器人")
    public Result<List<BotVo>> listAllBotsInMySpace(HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        return Result.success(botService.listAllBotsInMySpace(account));
    }

    @PostMapping("/list")
    @HttpApiDoc(value = "/api/v1/bot/list", method = MiApiRequestMethod.POST, apiName = "获取机器人")
    public Result<Page<BotVo>> listBot(HttpServletRequest request,
                                       @RequestBody ReqBotListDto reqBotListDto) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        if (ObjectUtils.isNotEmpty(reqBotListDto.getWorkspaceId())) {
            Integer userRole = workspaceService.getWorkspaceRole(account, reqBotListDto.getWorkspaceId());
            if (userRole == null || userRole < UserRoleEnum.USER.getCode()) {
                return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
            }
        }

        return Result.success(botService.listBot(reqBotListDto, account.getUsername()));
    }

    @GetMapping("/getDetail")
    @RoleControl(role = UserRoleEnum.USER, strategy = "bot", permissions = true)
    @HttpApiDoc(value = "/api/v1/bot/getDetail", method = MiApiRequestMethod.GET, apiName = "获取机器人详情")
    public Result<BotVo> getBotDetail(HttpServletRequest request, @HttpApiDocClassDefine(value = "botId", description = "机器人id") @RequestParam("botId") Long botId) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        return Result.success(botService.getBotDetail(account.getUsername(), botId, false, null, null));
    }

    @GetMapping("/getBotSimpleInfo")
    @HttpApiDoc(value = "/api/v1/bot/getBotSimpleInfo", method = MiApiRequestMethod.GET, apiName = "获取机器人简单信息")
    public Result<BotSimpleVo> getBotSimpleInfo(HttpServletRequest request, @HttpApiDocClassDefine(value = "botId", description = "机器人id") @RequestParam("botId") Long botId) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        return Result.success(botService.getBotSimpleInfo(botId));
    }

    @PostMapping("/category/bind")
    @HttpApiDoc(value = "/api/v1/bot/category/bind", method = MiApiRequestMethod.POST, apiName = "绑定机器人分类")
    public Result<Boolean> bindBotCategory(HttpServletRequest request, @HttpApiDocClassDefine(value = "botId", description = "机器人id") Long botId, @HttpApiDocClassDefine(value = "categoryId", description = "分类id") Long categoryId) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        return Result.success(botService.bindBotCategory(botId, categoryId));
    }

    @PostMapping("/publish")
    @HttpApiDoc(value = "/api/v1/bot/publish", method = MiApiRequestMethod.POST, apiName = "发布机器人")
    public Result<Boolean> publishBot(HttpServletRequest request, @RequestBody BotPublishDto publishDto) {
        log.info("publish in publishDto:{}", GsonUtils.gson.toJson(publishDto));
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        String username = account.getUsername();
        return Result.success(botService.publishBot(username, publishDto));
    }

    @GetMapping("/publish/aiRecordNotes")
    public Result<String> aiRecordNotes(HttpServletRequest request,
                                        @RequestParam("botId") Long botId) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        String username = account.getUsername();
        return Result.success(botService.aiRecordNotes(username, botId));
    }

    @GetMapping("/index")
    @HttpApiDoc(value = "/api/v1/bot/index", method = MiApiRequestMethod.GET, apiName = "首页信息")
    public Result<BotIndexListDto> listBotIndex() {

        List<BotBaseInfoDto> botList = botService.mostUsedList();
        List<BotBaseInfoDto> pluginList = pluginService.mostUsedList();

        BotIndexListDto indexDto = new BotIndexListDto();
        indexDto.setBotList(botList == null ? new ArrayList<>() : botList);
        indexDto.setPluginList(pluginList == null ? new ArrayList<>() : pluginList);
        return Result.success(indexDto);
    }

    @GetMapping("/imType")
    @HttpApiDoc(value = "/api/v1/bot/imType", method = MiApiRequestMethod.GET, apiName = "im类型")
    public Result<List<ImTypeDTO>> listImType() {
        List<M78ImType> list = m78ImTypeMapper.selectListByQuery(new QueryWrapper().eq("deleted", 0));
        if (list == null || list.isEmpty()) {
            return Result.success(new ArrayList<>());
        }
        List<ImTypeDTO> imTypeDTOS = list.stream().map(m78ImType -> {
            ImTypeDTO imTypeDTO = new ImTypeDTO();
            imTypeDTO.setId(m78ImType.getId());
            imTypeDTO.setName(m78ImType.getName());
            return imTypeDTO;
        }).collect(Collectors.toList());
        return Result.success(imTypeDTOS);
    }


    @PostMapping("/promptTurbo")
    @HttpApiDoc(value = "/api/v1/bot/promptTurbo", method = MiApiRequestMethod.POST, apiName = "优化prompt")
    public ResponseEntity<SseEmitter> promptTurbo(HttpServletRequest request, @RequestParam("prompt") String prompt) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return ResponseEntity.status(403).body(null);
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-Accel-Buffering", "no");
        httpHeaders.setCacheControl(CacheControl.noCache());
        SseEmitter emitter = botService.promptTurboStream(prompt, account.getUsername());
        return ResponseEntity.ok().contentType(MediaType.TEXT_EVENT_STREAM).headers(httpHeaders).body(emitter);
    }

    @GetMapping("/getPresetQuestion")
    @HttpApiDoc(value = "/api/v1/bot/getPresetQuestion", method = MiApiRequestMethod.GET, apiName = "获取bot预设问题")
    public Result<PresetQuestionRes> getPresetQuestion(HttpServletRequest request,
                                                       @RequestParam("botId") Long botId,
                                                       @RequestParam("topicId") String topicId,
                                                       @RequestParam("input") String input) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        String username = account.getUsername();
        return botService.getPresetQuestion(username, botId, input, topicId);
    }
    @GetMapping("/getAsrTypes")
    @HttpApiDoc(value = "/api/v1/bot/getAsrTypes", method = MiApiRequestMethod.GET, apiName = "获取ASR类型")
    public Result<Map<String,String>> getAsrTypes(HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        return Result.success(Map.of("xiaoai","小爱","tencent","腾讯"));
    }

}
