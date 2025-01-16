package run.mone.m78.service.service.card;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import run.mone.m78.api.bo.ListResult;
import run.mone.m78.api.bo.card.*;
import run.mone.m78.api.bo.workspace.WorkspaceInfoResp;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.common.enums.UserCollectType;
import run.mone.m78.service.dao.entity.M78CardElementPo;
import run.mone.m78.service.dao.entity.M78CardPo;
import run.mone.m78.service.dao.entity.M78UserCollect;
import run.mone.m78.service.dao.mapper.M78CardElementMapper;
import run.mone.m78.service.dao.mapper.M78CardMapper;
import run.mone.m78.service.dao.mapper.M78UserCollectMapper;
import run.mone.m78.service.service.workspace.WorkspaceService;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static run.mone.m78.api.bo.card.enums.CardOfficialEnum.NOT_OFFICIAL;
import static run.mone.m78.api.bo.card.enums.CardOfficialEnum.OFFICIAL;
import static run.mone.m78.api.bo.card.enums.CardStatusEnum.NOT_PUBLISHED;
import static run.mone.m78.api.bo.card.enums.CardStatusEnum.PUBLISHED;
import static run.mone.m78.api.bo.card.enums.ContentTypeEnum.EXPRESSION;
import static run.mone.m78.api.bo.card.enums.ElementTypeEnum.CARD_ROOT;
import static run.mone.m78.service.exceptions.ExCodes.*;

@Service
@Slf4j

/**
 * M78CardService类提供了对卡片信息的管理功能，包括获取卡片详情、新增卡片、更新卡片、发布卡片、删除卡片以及列出卡片等操作。
 * 该类继承自ServiceImpl，并使用了Spring的@Service注解和@Slf4j注解。
 *
 * 主要功能包括：
 * - 根据卡片ID获取卡片详情
 * - 新增卡片信息
 * - 更新卡片信息
 * - 更新卡片的官方状态
 * - 发布卡片
 * - 添加卡片详情
 * - 更新卡片详情
 * - 根据ID删除记录
 * - 列出卡片信息
 * - 获取用户的卡片列表
 *
 * 该类依赖于多个Mapper和Service类来完成数据库操作和业务逻辑处理。
 */

public class M78CardService extends ServiceImpl<M78CardMapper, M78CardPo> {

    private static final Gson gson = GsonUtils.gson;

    @Resource
    private M78CardElementMapper cardElementMapper;

    @Resource
    private M78CardVariableService cardVariableService;

    @Autowired
    private WorkspaceService workspaceService;

    @Resource
    private M78UserCollectMapper userCollectMapper;

    /**
     * 根据卡片ID获取卡片详情
     *
     * @param cardId            卡片的唯一标识符
     * @param needUsedVariables 是否需要返回已使用的变量
     * @return 包含卡片详情的结果对象
     */
    //根据cardid获取详情
    public Result<CardDetailRes> getCardDetail(Long cardId, Boolean needUsedVariables) {
        M78CardPo cardPo = getById(cardId);
        if (cardPo == null) {
            return Result.fail(STATUS_NOT_FOUND, "Card not found");
        }

        List<CardVariable> variables = cardVariableService.getCardVariablesByCardId(cardId).getData();
        Map<String, CardVariable> variableMap = (variables != null && variables.size() > 0)
                ? variables.stream().collect(Collectors.toMap(CardVariable::getName, it -> it, (existing, replacement) -> existing))
                : Collections.emptyMap();

        List<M78CardElementPo> cardElementPos = cardElementMapper.selectListByQuery(new QueryWrapper().eq("card_id", cardId));
        if (cardElementPos == null || cardElementPos.size() == 0) {
            return Result.success(CardDetailRes.builder().card(poToCard(cardPo)).cardVariableMap(variableMap).build());
        }
        Optional<M78CardElementPo> rootPo = cardElementPos.stream().filter(it -> CARD_ROOT.getElementType().equals(it.getType())).findFirst();
        if (!rootPo.isPresent()) {
            return Result.fail(STATUS_INTERNAL_ERROR, "Card_ROOT not found");
        }

        Map<String, CardElement> cardElementMap = cardElementPos.stream()
                .collect(Collectors.toMap(M78CardElementPo::getUniqueKey, element -> poToCardElement(element), (existing, replacement) -> {
                    return existing;
                }));

        Map<String, CardVariable> usedCardVariableMap = new HashMap<>();
        if (needUsedVariables) {
            cardElementMap.entrySet().stream().filter(it -> it.getValue().getProperty() != null).forEach(it -> {
                Content content = it.getValue().getProperty().getContent();
                if (content != null && EXPRESSION.contentType.equals(content.getType())) {
                    String variableName = removeDollar(content.getValue());
                    if (variableMap.containsKey(variableName)) {
                        usedCardVariableMap.put(variableName, variableMap.get(variableName));
                    }
                }
            });
        }

        CardDetailRes cardDetailRes = new CardDetailRes();
        cardDetailRes.setCard(poToCard(cardPo));
        cardDetailRes.setElementMap(cardElementMap);
        cardDetailRes.setCardVariableMap(variableMap);
        cardDetailRes.setUsedCardVariableMap(usedCardVariableMap);
        cardDetailRes.setRootUniqueKey(rootPo.get().getUniqueKey());
        return Result.success(cardDetailRes);
    }

    /**
     * 新增卡片信息
     *
     * @param card     包含卡片信息的Card对象
     * @param userName 创建者的用户名
     * @return 操作结果，返回一个包含布尔值的Result对象，表示卡片是否成功新增
     */
    //新增卡片信息，入参是Card
    public Result<Boolean> addCardBasic(Card card, String userName) {
        M78CardPo po = M78CardPo.builder()
                .name(card.getName())
                .elementId(card.getElementId())
                .workspaceId(card.getWorkspaceId())
                .type(card.getType())
                .status(NOT_PUBLISHED.getCode())
                .official(NOT_OFFICIAL.getCode())
                .description(card.getDescription())
                .creator(userName)
                .updater(userName)
                .ctime(System.currentTimeMillis())
                .utime(System.currentTimeMillis())
                .build();
        return Result.success(save(po));
    }

    /**
     * 更新卡片信息
     *
     * @param card     要更新的卡片信息
     * @param userName 更新操作的用户名
     * @return 更新操作的结果，包含成功与否的布尔值
     */
    //更新卡片信息，入参是Card
    public Result<Boolean> updateCardBasic(Card card, String userName) {
        M78CardPo existingPo = getById(card.getId());
        if (existingPo == null) {
            return Result.fail(STATUS_NOT_FOUND, "Card not found");
        }

        existingPo.setWorkspaceId(card.getWorkspaceId());
        existingPo.setUpdater(userName);
        existingPo.setUtime(System.currentTimeMillis());
        existingPo.setDescription(card.getDescription());
        existingPo.setName(card.getName());
        existingPo.setStatus(card.getStatus());
        boolean res = updateById(existingPo);
        return Result.success(res);
    }

    /**
     * 更新卡片的官方状态
     *
     * @param cardId     卡片的唯一标识符
     * @param isOfficial 是否为官方状态
     * @param userName   更新操作的用户名
     * @return 更新操作的结果，包含成功与否的布尔值
     */
    //更新卡片是否是官方
    @Transactional
    public Result<Boolean> updateCardOfficialStatus(Long cardId, Boolean isOfficial, String userName) {
        M78CardPo existingPo = getById(cardId);
        if (existingPo == null) {
            return Result.fail(STATUS_NOT_FOUND, "Card not found");
        }
        existingPo.setOfficial(isOfficial ? OFFICIAL.getCode() : NOT_OFFICIAL.getCode());
        existingPo.setUpdater(userName);
        existingPo.setUtime(System.currentTimeMillis());
        boolean res = updateById(existingPo);
        return Result.success(res);
    }

    /**
     * 发布卡片
     *
     * @param cardId   卡片的唯一标识符
     * @param userName 更新卡片的用户名
     * @return 发布操作的结果，包含成功与否的布尔值
     */
    //发布卡片
    @Transactional
    public Result<Boolean> publishCard(Long cardId, String userName) {
        M78CardPo existingPo = getById(cardId);
        if (existingPo == null) {
            return Result.fail(STATUS_NOT_FOUND, "Card not found");
        }
        existingPo.setStatus(PUBLISHED.getCode());
        existingPo.setUpdater(userName);
        existingPo.setUtime(System.currentTimeMillis());
        boolean res = updateById(existingPo);
        return Result.success(res);
    }

    /**
     * 添加卡片详情
     *
     * @param req      包含卡片详情的请求对象
     * @param userName 更新卡片的用户名
     * @return 操作结果，成功返回true
     * @throws IllegalArgumentException 如果卡片不存在或请求参数无效
     */

    @Transactional
    public Result<Boolean> addCardDetail(AddOrUpdateCardDetailReq req, String userName) {
        M78CardPo existingPo = getById(req.getCardId());
        if (existingPo == null) {
            return Result.fail(STATUS_NOT_FOUND, "Card not found");
        }
        if (req.getElementMap() == null) {
            return Result.fail(STATUS_INVALID_ARGUMENT, "Invalid argument");
        }
        if (!req.getElementMap().entrySet().stream().filter(it -> CARD_ROOT.getElementType().equals(it.getValue().getType())).findFirst().isPresent()) {
            return Result.fail(STATUS_INVALID_ARGUMENT, "Invalid argument, Card type must exists!");
        }

        List<M78CardElementPo> poList = req.getElementMap().entrySet().stream().map(it -> {
            CardElement cardElement = it.getValue();
            M78CardElementPo po = M78CardElementPo.builder()
                    .cardId(req.getCardId())
                    .workspaceId(req.getWorkspaceId())
                    .type(cardElement.getType())
                    .children(cardElement.getChildren() == null ? "" : gson.toJson(cardElement.getChildren()))
                    .property(cardElement.getProperty() == null ? "" : gson.toJson(cardElement.getProperty()))
                    .uniqueKey(cardElement.getUniqueKey())
                    .build();
            cardElementMapper.insert(po);
            return po;
        }).collect(Collectors.toList());

        Optional<M78CardElementPo> rootPo = poList.stream().filter(it -> CARD_ROOT.getElementType().equals(it.getType())).findFirst();

        existingPo.setElementId(rootPo.get().getId());
        existingPo.setUpdater(userName);
        existingPo.setUtime(System.currentTimeMillis());
        updateById(existingPo);

        return Result.success(true);
    }

    /**
     * 更新卡片详情
     *
     * @param req      包含卡片详情的请求对象
     * @param userName 更新操作的用户名
     * @return 更新操作的结果，成功返回true，失败返回相应的错误信息
     */
    @Transactional
    public Result<Boolean> updateCardDetail(AddOrUpdateCardDetailReq req, String userName) {
        M78CardPo existingPo = getById(req.getCardId());
        if (existingPo == null) {
            return Result.fail(STATUS_NOT_FOUND, "Card not found");
        }
        if (req.getElementMap() == null) {
            return Result.fail(STATUS_INVALID_ARGUMENT, "Invalid argument");
        }
        if (!req.getElementMap().entrySet().stream().filter(it -> CARD_ROOT.getElementType().equals(it.getValue().getType())).findFirst().isPresent()) {
            return Result.fail(STATUS_INVALID_ARGUMENT, "Invalid argument, Card type must exists!");
        }

        List<M78CardElementPo> oldPoList = cardElementMapper.selectListByQuery(new QueryWrapper().eq("card_id", req.getCardId()));
        Map<Long, M78CardElementPo> oldElements = oldPoList.stream().collect(Collectors.toMap(it -> it.getId(), it -> it));
        List<Long> existIds = req.getElementMap().entrySet().stream().map(it -> it.getValue().getId()).collect(Collectors.toList());
        List<Long> toDeleteElementIds = oldPoList.stream().filter(po -> !existIds.contains(po.getId())).map(M78CardElementPo::getId).collect(Collectors.toList());
        if (toDeleteElementIds != null && toDeleteElementIds.size() > 0) {
            cardElementMapper.deleteByQuery(new QueryWrapper().in("id", toDeleteElementIds));
        }
        List<M78CardElementPo> poList = req.getElementMap().entrySet().stream().map(it -> {
            CardElement cardElement = it.getValue();
            if (cardElement.getId() == null || cardElement.getCardId() == 0) {
                //add
                M78CardElementPo po = M78CardElementPo.builder()
                        .cardId(req.getCardId())
                        .workspaceId(req.getWorkspaceId())
                        .type(cardElement.getType())
                        .children(cardElement.getChildren() == null ? "" : gson.toJson(cardElement.getChildren()))
                        .property(cardElement.getProperty() == null ? "" : gson.toJson(cardElement.getProperty()))
                        .uniqueKey(cardElement.getUniqueKey())
                        .build();
                cardElementMapper.insert(po);
                return po;
            }
            if (oldElements.containsKey(cardElement.getId())) {
                //update
                M78CardElementPo po = oldElements.get(cardElement.getId());
                po.setWorkspaceId(req.getWorkspaceId());
                po.setType(cardElement.getType());
                po.setUniqueKey(cardElement.getUniqueKey());
                po.setChildren(cardElement.getChildren() == null ? "" : gson.toJson(cardElement.getChildren()));
                po.setProperty(cardElement.getProperty() == null ? "" : gson.toJson(cardElement.getProperty()));
                cardElementMapper.update(po);
                return po;
            }

            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        Optional<M78CardElementPo> rootPo = poList.stream().filter(it -> it != null && CARD_ROOT.getElementType().equals(it.getType())).findFirst();
        existingPo.setElementId(rootPo.get().getId());
        existingPo.setUpdater(userName);
        existingPo.setUtime(System.currentTimeMillis());
        updateById(existingPo);

        return Result.success(true);
    }


    /**
     * 根据ID删除记录
     *
     * @param id 记录的唯一标识符
     * @return 删除操作的结果，包含成功与否的布尔值
     */
    //根据Id删除
    public Result<Boolean> deleteById(Long id) {
        return Result.success(removeById(id));
    }

    /**
     * 列出卡片信息
     *
     * @param req     请求参数，包含查询条件和分页信息
     * @param account 当前会话的用户账户信息
     * @return 返回包含卡片列表的结果
     */
    //list接口，入参是CardListReq, 返回结果是Result<ListResult<Card>>
    public Result<ListResult<Card>> listCards(CardListReq req, SessionAccount account) {
        QueryWrapper queryWrapper = new QueryWrapper();
        if (req.getName() != null && !req.getName().isEmpty()) {
            queryWrapper.like("name", req.getName());
        }
        if (req.getWorkspaceId() != null && req.getWorkspaceId() > 0) {
            queryWrapper.eq("workspace_id", req.getWorkspaceId());
        }
        if (StringUtils.isNotEmpty(req.getType())) {
            queryWrapper.eq("type", req.getType());
        }
        //只能看到已发布的
        queryWrapper.eq("status", PUBLISHED.getCode());
        if (req.getOfficial() != null) {
            queryWrapper.eq("official", req.getOfficial());
        }
        if (req.getUserName() != null && !req.getUserName().isEmpty()) {
            queryWrapper.like("creator", req.getUserName());
        }
        if (req.getOrderBy() != null && !req.getOrderBy().isEmpty()) {
            queryWrapper.orderBy(req.getOrderBy(), req.isAsc());
        } else {
            queryWrapper.orderBy("ctime", false);
        }

        if (req.isMyCollection()) {
            List<M78UserCollect> m78UserCollects = userCollectMapper.selectListByQuery(
                    QueryWrapper.create().eq("type", UserCollectType.CARD.getCode()).eq("username", account.getUsername()));
            List<Long> colleationList = m78UserCollects.stream().map(M78UserCollect::getId).toList();
            if (colleationList.isEmpty()) {
                return Result.success(new ListResult<Card>());
            }
            queryWrapper.in("id", colleationList);
        }
        Page<M78CardPo> page = new Page<>(req.getPage(), req.getPageSize());
        Page<M78CardPo> resultPage = page(page, queryWrapper);
        List<Card> cardList = resultPage.getRecords().stream()
                .map(po -> poToCard(po))
                .collect(Collectors.toList());

        ListResult<Card> listResult = new ListResult<>();
        listResult.setTotalPage(resultPage.getTotalPage());
        listResult.setList(cardList);
        listResult.setPage(req.getPage());
        listResult.setPageSize(req.getPageSize());

        return Result.success(listResult);
    }

    private Card poToCard(M78CardPo po) {
        return Card.builder()
                .id(po.getId())
                .name(po.getName())
                .elementId(po.getElementId())
                .workspaceId(po.getWorkspaceId())
                .type(po.getType())
                .official(po.getOfficial())
                .status(po.getStatus())
                .description(po.getDescription())
                .creator(po.getCreator())
                .updater(po.getUpdater())
                .ctime(po.getCtime())
                .utime(po.getUtime())
                .build();
    }

    //po转CardElement
    private CardElement poToCardElement(M78CardElementPo po) {
        Property property = StringUtils.isEmpty(po.getProperty())
                ? null
                : gson.fromJson(po.getProperty(), Property.class);

        List<String> children = StringUtils.isEmpty(po.getChildren())
                ? null
                : gson.fromJson(po.getChildren(), new TypeToken<List<String>>() {
        }.getType());

        return CardElement.builder()
                .id(po.getId())
                .workspaceId(po.getWorkspaceId())
                .cardId(po.getCardId())
                .uniqueKey(po.getUniqueKey())
                .type(po.getType())
                .property(property)
                .children(children)
                .build();
    }

    /**
     * 去除字符串中的${}格式，返回其中的内容
     *
     * @param input 输入的字符串，可能包含${}格式
     * @return 去除${}后的字符串，如果输入为null或空字符串，则返回原输入
     */
    //String的格式是${xxx}, 去除${}, 返回xxx
    public static String removeDollar(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        if (input.startsWith("${") && input.endsWith("}")) {
            return input.substring(2, input.length() - 1);
        }

        return input;
    }


    /**
     * 获取用户的卡片列表
     *
     * @param req     请求参数，包括卡片名称、工作区ID、状态、类型、官方标识、排序字段及分页信息
     * @param account 当前会话的用户账户信息
     * @return 包含卡片列表及分页信息的结果
     */
    public Result<ListResult<Card>> myListCards(CardListReq req, SessionAccount account) {
        QueryWrapper queryWrapper = new QueryWrapper();
        if (req.getName() != null && !req.getName().isEmpty()) {
            queryWrapper.like("name", req.getName());
        }
        //如果有workspace_id，那就只能查workspace_id下的card，如果没有就查属于用户所在的workspace下以及用户创建的card
        if (req.getWorkspaceId() != null && req.getWorkspaceId() > 0) {
            queryWrapper.eq("workspace_id", req.getWorkspaceId());
        } else {
            List<WorkspaceInfoResp> workspaceInfoRespList = workspaceService.workspaceList(account, null);
            List<Long> workspaceList = workspaceInfoRespList.stream().map(WorkspaceInfoResp::getId).toList();
            queryWrapper.and(queryWrapper1 -> {
                queryWrapper1.in("workspace_id", workspaceList);
                queryWrapper1.or(queryWrapper2 -> {
                    queryWrapper2.eq("creator", account.getUsername());
                });
            });
        }
        if (req.getStatus() != null) {
            queryWrapper.eq("status", req.getStatus());
        }
        if (StringUtils.isNotEmpty(req.getType())) {
            queryWrapper.eq("type", req.getType());
        }
        if (req.getOfficial() != null) {
            queryWrapper.eq("official", req.getOfficial());
        }
        if (req.getOrderBy() != null && !req.getOrderBy().isEmpty()) {
            queryWrapper.orderBy(req.getOrderBy(), req.isAsc());
        } else {
            queryWrapper.orderBy("ctime", false);
        }
        Page<M78CardPo> page = new Page<>(req.getPage(), req.getPageSize());
        Page<M78CardPo> resultPage = page(page, queryWrapper);
        List<Card> cardList = resultPage.getRecords().stream()
                .map(po -> poToCard(po))
                .collect(Collectors.toList());

        ListResult<Card> listResult = new ListResult<>();
        listResult.setTotalPage(resultPage.getTotalPage());
        listResult.setList(cardList);
        listResult.setPage(req.getPage());
        listResult.setPageSize(req.getPageSize());

        return Result.success(listResult);
    }

}
