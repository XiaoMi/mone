package run.mone.m78.service.service.multiModal;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import run.mone.m78.service.dao.entity.M78AsrCostPo;
import run.mone.m78.service.dao.mapper.M78AsrCostMapper;

import javax.annotation.Resource;

import static run.mone.m78.service.dao.entity.table.M78AsrCostPoTableDef.M78_ASR_COST_PO;

@Slf4j
@Service

/**
 * AudioAsrCostService类提供了用于管理ASR平台和产品线的使用时长和使用次数的服务。
 * 该类继承自ServiceImpl，并使用M78AsrCostMapper进行数据库操作。
 *
 * 主要功能包括：
 * 1. 保存或更新指定ASR平台和产品线的使用时长。
 * 2. 保存或更新指定ASR平台和产品线的使用次数。
 *
 * 该类使用了Spring的@Service注解进行服务声明，并使用@Slf4j进行日志记录。
 * 在操作数据库时，处理了可能的插入冲突和异常情况，确保数据的一致性和完整性。
 */

public class AudioAsrCostService extends ServiceImpl<M78AsrCostMapper, M78AsrCostPo> {

    @Resource
    private M78AsrCostMapper m78AsrCostMapper;

    /**
     * 保存或更新
     *
     * @param asrPlatform
     * @param productLine,不同场景下
     * @return
     */
    public void saveOrUpdateUsedTime(String asrPlatform, String productLine, Long usedTime) {
        // 只更新有效时长
        if (usedTime == null || usedTime < 1) {
            return;
        }

        // 毫秒转换成秒，存储
        usedTime = usedTime / 1000;
        Long now = System.currentTimeMillis() / 1000;

        try {
            int updateRows = m78AsrCostMapper.updateUsedTime(asrPlatform, productLine, usedTime, now);
            if (updateRows > 0) {
                return;
            }

            // 更新失败
            // 判断product_line是否存在
            QueryWrapper query = new QueryWrapper().where(M78_ASR_COST_PO.ASR_PLATFORM.eq(asrPlatform))
                    .and(M78_ASR_COST_PO.PRODUCT_LINE.eq(productLine));
            M78AsrCostPo asrCostPo = super.getOne(query);
            if (asrCostPo == null) {
                // 插入
                asrCostPo = M78AsrCostPo.builder()
                        .asrPlatform(asrPlatform)
                        .productLine(productLine)
                        .usedTime(usedTime)
                        .ctime(now)
                        .utime(now).build();

                Boolean succ = super.save(asrCostPo);
            }
        } catch (DuplicateKeyException de) {
            // 报错说明插入冲突，再更新一次
            m78AsrCostMapper.updateUsedTime(asrPlatform, productLine, usedTime, now);
        } catch (Exception e) {
            log.error("m78_asr_cost update used_time failed", e);
        }
    }

    /**
     * 更新或保存使用次数
     * <p>
     * 该方法用于按次数计费的场景，更新指定平台和产品线的使用次数。如果更新失败，则检查该产品线是否存在，
     * 如果不存在则插入新的记录。若插入时发生冲突，则再次尝试更新使用次数。
     *
     * @param asrPlatform ASR平台名称
     * @param productLine 产品线名称
     */
    // 按次数计费的场景，更新使用次数
    public void saveOrUpdateUsedCount(String asrPlatform, String productLine) {
        Long now = System.currentTimeMillis() / 1000;
        // 只更新有效时长
        try {
            int updateRows = m78AsrCostMapper.updateUsedCount(asrPlatform, productLine, now);
            if (updateRows > 0) {
                return;
            }

            // 更新失败
            // 判断product_line是否存在
            QueryWrapper query = new QueryWrapper().where(M78_ASR_COST_PO.ASR_PLATFORM.eq(asrPlatform))
                    .and(M78_ASR_COST_PO.PRODUCT_LINE.eq(productLine));
            M78AsrCostPo asrCostPo = super.getOne(query);
            if (asrCostPo == null) {
                // 插入
                asrCostPo = M78AsrCostPo.builder()
                        .asrPlatform(asrPlatform)
                        .productLine(productLine)
                        .usedCount(1)
                        .ctime(now)
                        .utime(now).build();

                Boolean succ = super.save(asrCostPo);
            }
        } catch (DuplicateKeyException de) {
            // 报错说明插入冲突，再更新一次
            m78AsrCostMapper.updateUsedCount(asrPlatform, productLine, now);
        } catch (Exception e) {
            log.error("m78_asr_cost update used_count failed", e);
        }
    }

}
