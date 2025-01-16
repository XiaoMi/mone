package run.mone.m78.server.controller;

import com.google.common.base.Preconditions;
import com.xiaomi.mone.http.docs.annotations.HttpApiDoc;
import com.xiaomi.mone.http.docs.annotations.HttpApiModule;
import com.xiaomi.mone.http.docs.annotations.MiApiRequestMethod;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import run.mone.m78.api.bo.plugins.BotPluginOrgDTO;
import run.mone.m78.api.bo.plugins.OfficialByAdminReq;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.dao.entity.M78BotPluginOrg;
import run.mone.m78.service.dto.PluginPublishDto;
import run.mone.m78.service.exceptions.UserAuthException;
import run.mone.m78.service.service.plugins.BotPluginOrgService;
import run.mone.m78.service.service.user.LoginService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static run.mone.m78.api.constant.CommonConstant.API_PREFIX;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_FORBIDDEN;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_INTERNAL_ERROR;

/**
 * @author HawickMason@xiaomi.com
 * @date 3/13/24 17:01
 */
@Slf4j
@RestController
@RequestMapping(value = API_PREFIX + "/botplugin/org")
@HttpApiModule(value = "PluginOrgController", apiController = PluginOrgController.class)
public class PluginOrgController {


    @Resource
    private BotPluginOrgService botPluginOrgService;

    @Autowired
    private LoginService loginService;

    //保存或更新M78BotPluginOrg, 使用DTO (class)
    @PostMapping(value = "/saveOrUpdate")
    @HttpApiDoc(apiName = "保存或更新插件", value = "/api/v1/botplugin/org/saveOrUpdate", method = MiApiRequestMethod.POST, description = "保存或更新插件")
    public Result<Long> saveOrUpdatePluginOrg(@RequestBody BotPluginOrgDTO pluginOrgDTO, HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        Preconditions.checkArgument(pluginOrgDTO != null);
        M78BotPluginOrg pluginOrg = M78BotPluginOrg.fromDTO(pluginOrgDTO);

        return botPluginOrgService.saveOrUpdateM78BotPluginOrg(account.getUsername(), pluginOrg, pluginOrgDTO.getPluginCategory());

    }

    //导入M78BotPluginOrg, 使用DTO (class)
    @PostMapping(value = "/import")
    @HttpApiDoc(apiName = "导入插件", value = "/api/v1/botplugin/org/import", method = MiApiRequestMethod.POST, description = "导入插件")
    public Result<Boolean> importPluginOrg(@RequestBody BotPluginOrgDTO pluginOrgDTO, HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        Preconditions.checkArgument(pluginOrgDTO != null);
        M78BotPluginOrg pluginOrg = M78BotPluginOrg.fromDTO(pluginOrgDTO);

        return botPluginOrgService.importM78BotPluginOrg(account.getUsername(), pluginOrg);

    }

    // 根据id删除M78BotPluginOrg
    @PostMapping(value = "/deleteById")
    @HttpApiDoc(apiName = "根据id删除插件", value = "/api/v1/botplugin/org/deleteById", method = MiApiRequestMethod.POST, description = "根据id删除插件")
    public Result<Boolean> deletePluginOrgById(@RequestParam Long id, HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        return botPluginOrgService.deletePluginOrgById(id);
    }

    // 根据id获取M78BotPluginOrg，使用DTO (class)
    @PostMapping(value = "/getById")
    @HttpApiDoc(apiName = "根据id获取插件", value = "/api/v1/botplugin/org/getById", method = MiApiRequestMethod.POST, description = "根据id获取插件")
    public Result<BotPluginOrgDTO> getPluginOrgById(@RequestParam Long id, HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        Result<BotPluginOrgDTO> pluginOrgResult = botPluginOrgService.getPluginOrgById(id);
        if (GeneralCodes.OK.getCode() != pluginOrgResult.getCode()) {
            return Result.fail(STATUS_INTERNAL_ERROR, pluginOrgResult.getMessage());
        }
        return pluginOrgResult;
    }

    // 发布或取消发布M78BotPluginOrg
    @PostMapping(value = "/publishOrCancel")
    @HttpApiDoc(apiName = "上线或下线插件", value = "/api/v1/botplugin/org/publishOrCancel", method = MiApiRequestMethod.POST, description = "发布或取消发布M78BotPluginOrg")
    public Result<Boolean> publishOrCancelPluginOrg(@RequestBody PluginPublishDto dto, HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        if(dto == null || dto.getId() == null || dto.getPublish() == null){
            return Result.fail(GeneralCodes.ParamError, "param is empty");
        }
        return botPluginOrgService.pubOrCancelPluginOrg(dto.getId(), dto.getPublish(), dto.getPluginCategory());
    }
    //admin给插件打上官方标识
    @PostMapping(value = "/markOfficialByAdmin")
    @HttpApiDoc(apiName = "admin是否打上官方标识", value = "/api/v1/botplugin/org/markOfficialByAdmin", method = MiApiRequestMethod.POST, description = "发布或取消发布M78BotPluginOrg")
    public Result<Boolean> markOfficialByAdmin(HttpServletRequest request,
                                               @RequestBody OfficialByAdminReq officialByAdminReq){
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        if (!account.isAdmin()){
            return Result.fail(GeneralCodes.NotAuthorized,"not authorized");
        }
        return botPluginOrgService.markOfficialByAdmin(officialByAdminReq);
    }
}
