package run.mone.m78.service.service.card;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import run.mone.m78.api.bo.card.CardBind;
import run.mone.m78.api.bo.card.GetCardBindReq;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.dao.entity.M78CardBindPo;
import run.mone.m78.service.dao.mapper.M78CardBindMapper;

import java.lang.reflect.Type;
import java.util.Map;

import static run.mone.m78.api.bo.card.enums.CardBindTypeEnum.getCardBindTypes;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_INVALID_ARGUMENT;


@Service
@Slf4j

/**
 * M78CardBindService类提供了卡片绑定相关的服务。
 * <p>
 * 该类继承自ServiceImpl，并使用了Spring的@Service注解和Lombok的@Slf4j注解。
 * 主要功能包括：
 * <ul>
 *   <li>绑定卡片：接收一个CardBind对象，验证其有效性，处理重复绑定，并将新的绑定信息保存到数据库。</li>
 *   <li>根据关联ID和类型获取绑定信息：接收一个GetCardBindReq对象，查询并返回对应的绑定信息。</li>
 *   <li>根据ID删除绑定记录：接收一个ID，删除对应的绑定记录。</li>
 * </ul>
 * 该类还包含一个私有方法用于将M78CardBindPo对象转换为CardBind对象。
 */

public class M78CardBindService extends ServiceImpl<M78CardBindMapper, M78CardBindPo> {

    private static final Gson gson = GsonUtils.gson;

    /**
     * 绑定卡片
     * <p>
     * 该方法用于新增卡片绑定，接收一个CardBind类型的参数。方法会验证输入参数的有效性，
     * 如果参数无效则返回失败结果。若存在相同的relateId和type的绑定，则会先删除旧的绑定。
     * 最后将新的绑定信息保存到数据库中。
     *
     * @param cardBind 绑定信息，包含卡片ID、关联ID、类型等
     * @return 绑定操作的结果，成功返回true，失败返回false
     */
    //新增绑定，入参是CardBind类型，不是M78CardBindPo
    @Transactional
    public Result<Boolean> bindCard(CardBind cardBind) {

        if (cardBind.getCardId() == null || cardBind.getCardId() == 0
                || cardBind.getRelateId() == null || cardBind.getRelateId() == 0
                || StringUtils.isEmpty(cardBind.getType())
                || !getCardBindTypes().contains(cardBind.getType())) {
            return Result.fail(STATUS_INVALID_ARGUMENT, "Invalid argument");
        }

        //暂定：每个relateId+type只允许绑定一个card（默认情况），还允许relate挂到bot上后绑定自己另外的card
        Result<CardBind> existBind = getBindInfoByRelateId(GetCardBindReq.builder().botId(cardBind.getBotId()).relateId(cardBind.getRelateId()).type(cardBind.getType()).build());
        if (existBind.getData() != null) {
            deleteById(existBind.getData().getId());
        }

        M78CardBindPo m78CardBindPo = M78CardBindPo.builder()
                .botId(cardBind.getBotId())
                .cardId(cardBind.getCardId())
                .relateId(cardBind.getRelateId())
                .type(cardBind.getType())
                .bindDetail(gson.toJson(cardBind.getBindDetail()))
                .ctime(System.currentTimeMillis())
                .utime(System.currentTimeMillis())
                .build();
        return Result.success(save(m78CardBindPo));
    }

    /**
     * 根据关联ID和类型获取绑定信息
     *
     * @param req 包含查询所需的关联ID、类型和可选的机器人ID
     * @return 返回包含绑定信息的结果，如果参数无效则返回失败状态
     */
    //根据relateId和type查看绑定信息
    public Result<CardBind> getBindInfoByRelateId(GetCardBindReq req) {
        if (req.getRelateId() == null || req.getRelateId() == 0 || req.getType() == null || req.getType().isEmpty()) {
            return Result.fail(STATUS_INVALID_ARGUMENT, "Invalid argument");
        }

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("relate_id", req.getRelateId()).eq("type", req.getType());

        if (req.getBotId() != null && req.getBotId() > 0) {
            queryWrapper.eq("bot_id", req.getBotId());
        }

        M78CardBindPo bindInfo = getOne(queryWrapper);
        return Result.success(convertToCardBind(bindInfo));
    }


    /**
     * 根据ID删除记录
     *
     * @param id 要删除的记录的ID
     * @return 删除操作的结果，包含成功与否的布尔值
     */
    //根据id删除
    public Result<Boolean> deleteById(Long id) {
        if (id == null || id == 0) {
            return Result.fail(STATUS_INVALID_ARGUMENT, "Invalid argument");
        }
        boolean removed = removeById(id);
        return Result.success(removed);
    }


    //M78CardBindPo转为CardBind
    private CardBind convertToCardBind(M78CardBindPo m78CardBindPo) {
        if (m78CardBindPo == null) {
            return null;
        }
        Type mapType = new TypeToken<Map<String, String>>() {
        }.getType();
        return CardBind.builder()
                .id(m78CardBindPo.getId())
                .botId(m78CardBindPo.getBotId())
                .cardId(m78CardBindPo.getCardId())
                .relateId(m78CardBindPo.getRelateId())
                .type(m78CardBindPo.getType())
                .bindDetail(gson.fromJson(m78CardBindPo.getBindDetail(), mapType))
                .ctime(m78CardBindPo.getCtime())
                .utime(m78CardBindPo.getUtime())
                .build();
    }


}
