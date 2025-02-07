package run.mone.m78.server.controller;

import com.xiaomi.mone.http.docs.annotations.HttpApiModule;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.dto.ApiKeyDTO;
import run.mone.m78.service.exceptions.UserAuthException;
import run.mone.m78.service.service.api.ApiKeyService;
import run.mone.m78.service.service.user.LoginService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static run.mone.m78.api.constant.CommonConstant.API_PREFIX;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_BAD_REQUEST;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_FORBIDDEN;

@Slf4j
@RestController
@RequestMapping(value = API_PREFIX + "/apikey")
@HttpApiModule(value = "ApiKeyController", apiController = ApiKeyController.class)
public class ApiKeyController {

    @Autowired
    private ApiKeyService apiKeyService;

    @Autowired
    private LoginService loginService;

    @PostMapping("/create")
    public Result<Boolean> createWorkspace(HttpServletRequest request, @RequestBody ApiKeyDTO apiKeyDTO) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        String username = account.getUsername();
        apiKeyDTO.setCreator(username);
        return Result.success(apiKeyService.createApiKey(apiKeyDTO));
    }

    //调用deleteApiKeyById
    @PostMapping("/delete")
    public Result<Boolean> deleteApiKeyById(HttpServletRequest request, @RequestParam Long id) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        boolean result = apiKeyService.deleteApiKeyById(id);
        return result ? Result.success(true) : Result.fail(STATUS_BAD_REQUEST, "Failed to delete API key");
    }

    //调用getApiKeysByApiKey
    @GetMapping("/getByApiKey")
    public Result<List<ApiKeyDTO>> getApiKeysByApiKey(HttpServletRequest request, @RequestParam String apiKey) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        List<ApiKeyDTO> apiKeys = apiKeyService.getApiKeysByApiKey(apiKey);
        return Result.success(apiKeys);
    }


    //调用getApiKeysByTypeIdAndType
    @GetMapping("/getByTypeIdAndType")
    public Result<List<ApiKeyDTO>> getApiKeysByTypeIdAndType(HttpServletRequest request, @RequestParam Long typeId, @RequestParam Integer type) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        List<ApiKeyDTO> apiKeys = apiKeyService.getApiKeysByTypeIdAndType(typeId, type);
        return Result.success(apiKeys);
    }

}
