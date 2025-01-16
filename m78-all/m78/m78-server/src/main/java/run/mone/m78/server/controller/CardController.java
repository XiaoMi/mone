package run.mone.m78.server.controller;

import com.xiaomi.mone.http.docs.annotations.HttpApiModule;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import run.mone.m78.api.bo.ListResult;
import run.mone.m78.api.bo.card.*;
import run.mone.m78.api.bo.card.enums.CardTypeEnum;
import run.mone.m78.api.bo.card.enums.CardVariableClassTypeEnum;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.exceptions.UserAuthException;
import run.mone.m78.service.service.card.M78CardBindService;
import run.mone.m78.service.service.card.M78CardService;
import run.mone.m78.service.service.card.M78CardVariableService;
import run.mone.m78.service.service.user.LoginService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

import static run.mone.m78.api.bo.card.enums.BackgroundTypeEnum.getBackgroundTypeMap;
import static run.mone.m78.api.bo.card.enums.CardStatusEnum.getCardStatusMap;
import static run.mone.m78.api.bo.card.enums.CardVariableClassTypeEnum.getAllClassTypes;
import static run.mone.m78.api.bo.card.enums.ClickEventTypeEnum.getClickEventTypeMap;
import static run.mone.m78.api.bo.card.enums.ElementTypeEnum.*;
import static run.mone.m78.api.bo.card.enums.OperatorEnum.*;
import static run.mone.m78.api.bo.card.enums.PositionEnum.*;
import static run.mone.m78.api.bo.card.enums.VisibilityTypeEnum.getVisibilityTypeMap;
import static run.mone.m78.api.bo.card.enums.VisibilityValueTypeEnum.getValueTypeMap;
import static run.mone.m78.api.constant.CommonConstant.API_PREFIX;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_FORBIDDEN;

@Slf4j
@RestController
@RequestMapping(value = API_PREFIX + "/card")
@HttpApiModule(value = "CardController", apiController = CardController.class)
public class CardController {

    @Autowired
    private LoginService loginService;

    @Resource
    private M78CardVariableService cardVariableService;

    @Resource
    private M78CardBindService cardBindService;

    @Resource
    private M78CardService cardService;

    /********
     * card *
     * ******
     */

    //新增一个空card
    @RequestMapping(value = "/addCardBasic", method = RequestMethod.POST)
    public Result<Boolean> addCardBasic(HttpServletRequest request, @RequestBody Card card) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        return cardService.addCardBasic(card, account.getUsername());
    }

    //更新一个card
    @RequestMapping(value = "/updateCardBasic", method = RequestMethod.POST)
    public Result<Boolean> updateCardBasic(HttpServletRequest request, @RequestBody Card card) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        return cardService.updateCardBasic(card, account.getUsername());
    }

    //发布
    @RequestMapping(value = "/publishCard", method = RequestMethod.POST)
    public Result<Boolean> publishCard(HttpServletRequest request, @RequestParam Long cardId) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        return cardService.publishCard(cardId, account.getUsername());
    }

    //标记官方
    @RequestMapping(value = "/officialCard", method = RequestMethod.POST)
    public Result<Boolean> officialCard(HttpServletRequest request, @RequestParam Long cardId) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null || !account.isAdmin()) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        return cardService.updateCardOfficialStatus(cardId, true, account.getUsername());
    }

    //去除标记官方
    @RequestMapping(value = "/notOfficialCard", method = RequestMethod.POST)
    public Result<Boolean> notOfficialCard(HttpServletRequest request, @RequestParam Long cardId) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null || !account.isAdmin()) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        return cardService.updateCardOfficialStatus(cardId, false, account.getUsername());
    }

    //新增card明细
    @RequestMapping(value = "/addCardDetail", method = RequestMethod.POST)
    public Result<Boolean> addCardDetail(HttpServletRequest request, @RequestBody AddOrUpdateCardDetailReq req) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        return cardService.addCardDetail(req, account.getUsername());
    }

    //更新Card明细
    @RequestMapping(value = "/updateCardDetail", method = RequestMethod.POST)
    public Result<Boolean> updateCardDetail(HttpServletRequest request, @RequestBody AddOrUpdateCardDetailReq req) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        return cardService.updateCardDetail(req, account.getUsername());
    }

    //返回Result<Map<String, String>>, 无入参
    @RequestMapping(value = "/getCardTypes", method = RequestMethod.GET)
    public Result<Map<String, String>> getCardTypes(HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        return Result.success(CardTypeEnum.getCardTypeMap());
    }

    //返回Result<Map<Integer, String>>，无入参，返回cardStatus
    @RequestMapping(value = "/getCardStatus", method = RequestMethod.GET)
    public Result<Map<Integer, String>> getCardStatus(HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        return Result.success(getCardStatusMap());
    }


    //删除一个card
    @RequestMapping(value = "/deleteCard", method = RequestMethod.POST)
    public Result<Boolean> deleteCard(HttpServletRequest request, @RequestParam Long id) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        return cardService.deleteById(id);
    }

    //根据cardid获取详情
    @RequestMapping(value = "/getCardDetail", method = RequestMethod.GET)
    public Result<CardDetailRes> getCardDetail(HttpServletRequest request,
                                               @RequestParam Long cardId,
                                               @RequestParam(defaultValue = "false") Boolean needUsedVariables) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        return cardService.getCardDetail(cardId, needUsedVariables);
    }

    //列表查询card
    @RequestMapping(value = "/listCards", method = RequestMethod.POST)
    public Result<ListResult<Card>> listCards(HttpServletRequest request, @RequestBody CardListReq req) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        return cardService.listCards(req, account);
    }

    //列表查询我创建的card
    @RequestMapping(value = "/myListCards", method = RequestMethod.POST)
    public Result<ListResult<Card>> ListCards(HttpServletRequest request, @RequestBody CardListReq req) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        return cardService.myListCards(req, account);
    }


    @RequestMapping(value = "/getVisibilityTypes", method = RequestMethod.GET)
    public Result<Map<String, String>> getVisibilityTypes(HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        return Result.success(getVisibilityTypeMap());
    }

    @RequestMapping(value = "/getClickEventTypes", method = RequestMethod.GET)
    public Result<Map<String, String>> getClickEventTypes(HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        return Result.success(getClickEventTypeMap());
    }

    @RequestMapping(value = "/getVisibilityValueTypes", method = RequestMethod.GET)
    public Result<Map<String, String>> getVisibilityValueTypes(HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        return Result.success(getValueTypeMap());
    }

    @RequestMapping(value = "/getBackgroundTypes", method = RequestMethod.GET)
    public Result<Map<String, String>> getBackgroundTypes(HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        return Result.success(getBackgroundTypeMap());
    }

    @RequestMapping(value = "/getPositions", method = RequestMethod.GET)
    public Result<Map<String, String>> getPositions(HttpServletRequest request, @RequestParam(defaultValue = "All") String type) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        if ("All".equals(type)) {
            return Result.success(getAllPositionMap());
        }
        if ("Horizontal".equals(type)) {
            return Result.success(getHorizontalPositionMap());
        }
        if ("Vertical".equals(type)) {
            return Result.success(getVerticalPositionMap());
        }

        return Result.success(getAllPositionMap());
    }

    @RequestMapping(value = "/getElementTypes", method = RequestMethod.GET)
    public Result<List<String>> getElementTypes(HttpServletRequest request, @RequestParam(defaultValue = "All") String type) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        if ("All".equals(type)) {
            return Result.success(getAllElementTypes());
        }
        if ("LAYOUT".equals(type)) {
            return Result.success(getLayoutElementTypes());
        }
        if ("BASE_COMPONENT".equals(type)) {
            return Result.success(getBaseComponentElementTypes());
        }
        if ("FORM".equals(type)) {
            return Result.success(getFormElementTypes());
        }

        return Result.success(getAllElementTypes());
    }

    @RequestMapping(value = "/getOperators", method = RequestMethod.GET)
    public Result<Map<String, String>> getOperators(HttpServletRequest request, @RequestParam(defaultValue = "String") String type) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        if (CardVariableClassTypeEnum.String.getClassType().equals(type)) {
            return Result.success(getStringOperatorMap());
        }
        if (CardVariableClassTypeEnum.Boolean.getClassType().equals(type)) {
            return Result.success(getBooleanOperatorMap());
        }
        if (CardVariableClassTypeEnum.Number.getClassType().equals(type)) {
            return Result.success(getNumberOperatorMap());
        }
        if (CardVariableClassTypeEnum.Array.getClassType().equals(type)) {
            return Result.success(getArrayOperatorMap());
        }
        if (CardVariableClassTypeEnum.Object.getClassType().equals(type)) {
            return Result.success(getObjectOperatorMap());
        }

        return Result.success(null);
    }


    /************
     * CardBind *
     * **********
     */

    //新增一个绑定
    @RequestMapping(value = "/cardBind/bind", method = RequestMethod.POST)
    public Result<Boolean> bindCard(HttpServletRequest request, @RequestBody CardBind cardBind) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        return cardBindService.bindCard(cardBind);
    }

    //根据id删除绑定
    @RequestMapping(value = "/cardBind/deleteById", method = RequestMethod.POST)
    public Result<Boolean> deleteCardBindById(HttpServletRequest request, @RequestParam Long id) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        return cardBindService.deleteById(id);
    }

    //根据GetCardBindReq查询cardbind
    @RequestMapping(value = "/cardBind/getByRelateId", method = RequestMethod.POST)
    public Result<CardBind> getCardBindInfo(HttpServletRequest request, @RequestBody GetCardBindReq req) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        return cardBindService.getBindInfoByRelateId(req);
    }

    /****************
     * CardVariable *
     * **************
     */

    //新增一个cardVariable
    @RequestMapping(value = "/addCardVariable", method = RequestMethod.POST)
    public Result<Boolean> addCardVariable(HttpServletRequest request, @RequestBody CardVariable cardVariable) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        return cardVariableService.addCardVariable(cardVariable, account.getUsername());
    }

    //更新一个cardVariable
    @RequestMapping(value = "/updateCardVariable", method = RequestMethod.POST)
    public Result<Boolean> updateCardVariable(HttpServletRequest request, @RequestBody CardVariable cardVariable) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        return cardVariableService.updateCardVariable(cardVariable, account.getUsername());
    }

    //根据cardId查询cardVariable
    @RequestMapping(value = "/getCardVariablesByCardId", method = RequestMethod.GET)
    public Result<List<CardVariable>> getCardVariablesByCardId(HttpServletRequest request, @RequestParam Long cardId) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        return cardVariableService.getCardVariablesByCardId(cardId);
    }

    //根据Id删除cardVariable
    @RequestMapping(value = "/deleteCardVariableById", method = RequestMethod.POST)
    public Result<Boolean> deleteCardVariableById(HttpServletRequest request, @RequestParam Long id) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        return cardVariableService.deleteById(id);
    }

    //获取cardVariableClassType的list
    @RequestMapping(value = "/getCardVariableClassTypes", method = RequestMethod.GET)
    public Result<List<String>> getCardVariableClassTypes(HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        return Result.success(getAllClassTypes());
    }


}