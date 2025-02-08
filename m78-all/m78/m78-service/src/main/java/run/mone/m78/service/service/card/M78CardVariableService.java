package run.mone.m78.service.service.card;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import run.mone.m78.api.bo.card.CardVariable;
import run.mone.m78.service.dao.entity.M78CardVariablePo;
import run.mone.m78.service.dao.mapper.M78CardVariableMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static run.mone.m78.service.exceptions.ExCodes.STATUS_INVALID_ARGUMENT;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_NOT_FOUND;

@Service
@Slf4j

/**
 * M78CardVariableService类提供了对卡片变量信息的管理功能。
 * 该类继承自ServiceImpl，并使用了M78CardVariableMapper和M78CardVariablePo进行数据操作。
 *
 * 主要功能包括：
 * - 新增卡片变量信息
 * - 更新卡片变量信息
 * - 根据ID删除卡片变量信息
 * - 根据卡片ID获取对应的卡片变量列表
 *
 * 该类还包含了将CardVariable对象与M78CardVariablePo对象相互转换的辅助方法。
 *
 * 使用了Spring的@Service注解和Lombok的@Slf4j注解。
 */

public class M78CardVariableService extends ServiceImpl<M78CardVariableMapper, M78CardVariablePo> {

    /**
     * 新增卡片信息
     *
     * @param cardVariable 卡片变量信息，类型为CardVariable
     * @param userName     创建者用户名
     * @return 操作结果，成功返回true，失败返回false
     */
    //新增卡片信息，入参是CardVariable，不是M78CardVariablePo
    public Result<Boolean> addCardVariable(CardVariable cardVariable, String userName) {
        Result<List<CardVariable>> existVariables = getCardVariablesByCardId(cardVariable.getCardId());
        if (existVariables.getData() != null && existVariables.getData().size() > 0) {
            Optional<CardVariable> sameName = existVariables.getData().stream().filter(cv -> cv.getName().equals(cardVariable.getName())).findFirst();
            if (sameName.isPresent()) {
                return Result.fail(STATUS_INVALID_ARGUMENT, "Name is duplicated");
            }
        }

        M78CardVariablePo po = convertToPo(cardVariable);
        po.setCreator(userName);
        po.setUpdater(userName);
        po.setCtime(System.currentTimeMillis());
        po.setUtime(System.currentTimeMillis());
        return Result.success(save(po));
    }

    /**
     * 更新卡片变量信息
     *
     * @param cardVariable 要更新的卡片变量对象
     * @param userName     更新操作的用户名
     * @return 更新操作的结果，包含成功与否的布尔值
     * 如果卡片变量未找到，返回失败状态
     * 如果卡片变量名称重复，返回无效参数状态
     */
    //更新卡片信息，入参是CardVariable
    public Result<Boolean> updateCardVariable(CardVariable cardVariable, String userName) {
        M78CardVariablePo existingPo = getById(cardVariable.getId());
        if (existingPo == null) {
            return Result.fail(STATUS_NOT_FOUND, "CardVariable not found");
        }

        Result<List<CardVariable>> existVariables = getCardVariablesByCardId(cardVariable.getCardId());
        if (existVariables.getData() != null && existVariables.getData().size() > 0) {
            List<CardVariable> sameName = existVariables.getData().stream().filter(cv -> cv.getName().equals(cardVariable.getName())).collect(Collectors.toList());
            if (sameName != null && sameName.size() > 0) {
                if (sameName.size() > 1 || sameName.get(0).getId().longValue() != cardVariable.getId().longValue()) {
                    return Result.fail(STATUS_INVALID_ARGUMENT, "Name is duplicated");
                }
            }
        }

        existingPo.setName(cardVariable.getName());
        existingPo.setClassType(cardVariable.getClassType());
        existingPo.setDefaultValue(cardVariable.getDefaultValue());
        existingPo.setUpdater(userName);
        existingPo.setUtime(System.currentTimeMillis());

        boolean res = updateById(existingPo);
        return Result.success(res);
    }

    /**
     * 根据ID删除记录
     *
     * @param id 需要删除的记录的ID
     * @return 删除操作的结果，包含成功与否的布尔值
     */
    //根据Id删除
    public Result<Boolean> deleteById(Long id) {
        return Result.success(removeById(id));
    }

    /**
     * 根据卡片ID获取对应的CardVariable列表
     *
     * @param cardId 卡片的唯一标识符
     * @return 包含CardVariable对象的结果列表
     */
    //根据cardId获取列表，返回CardVariable的列表
    public Result<List<CardVariable>> getCardVariablesByCardId(Long cardId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("card_id", cardId);
        List<M78CardVariablePo> poList = list(queryWrapper);
        List<CardVariable> res = poList.stream().map(po -> convertToCardVariable(po)).collect(Collectors.toList());
        return Result.success(res);
    }

    //CardVariable转M78CardVariablePo
    private M78CardVariablePo convertToPo(CardVariable cardVariable) {
        return M78CardVariablePo.builder()
                .cardId(cardVariable.getCardId())
                .name(cardVariable.getName())
                .classType(cardVariable.getClassType())
                .defaultValue(cardVariable.getDefaultValue())
                .creator(cardVariable.getCreator())
                .updater(cardVariable.getUpdater())
                .ctime(cardVariable.getCtime())
                .utime(cardVariable.getUtime())
                .build();
    }

    //M78CardVariablePo转CardVariable
    private CardVariable convertToCardVariable(M78CardVariablePo po) {
        return CardVariable.builder()
                .id(po.getId())
                .cardId(po.getCardId())
                .name(po.getName())
                .classType(po.getClassType())
                .defaultValue(po.getDefaultValue())
                .creator(po.getCreator())
                .updater(po.getUpdater())
                .ctime(po.getCtime())
                .utime(po.getUtime())
                .build();
    }


}
