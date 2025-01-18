package run.mone.m78.service.service.im;

import com.google.gson.Gson;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.beans.BeanCopier;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import run.mone.m78.api.IMRecordProvider;
import run.mone.m78.api.bo.im.ExecuteBotReqDTO;
import run.mone.m78.api.bo.im.HasBotReqDTO;
import run.mone.m78.api.bo.im.IMRecordDTO;
import run.mone.m78.api.bo.im.M78IMRelationDTO;
import run.mone.m78.service.dao.entity.M78IMRecordPo;
import run.mone.m78.service.dao.entity.M78IMRelationPo;
import run.mone.m78.service.dao.mapper.M78IMRecordMapper;
import run.mone.m78.service.dao.mapper.M78IMRelationMapper;
import run.mone.m78.service.service.bot.BotService;

import javax.annotation.Resource;
import java.util.List;

import static run.mone.m78.service.dao.entity.table.M78IMRelationPoTableDef.M78_I_M_RELATION_PO;

/**
 * @author zhangping17
 * @description:
 * @date 2024-03-05 15:42
 */
@Slf4j
@DubboService(interfaceClass = IMRecordProvider.class, group = "${dubbo.group}", version = "1.0")
public class IMRecordProviderImpl implements IMRecordProvider {

    @Autowired
    private BotService botService;

    @Resource
    private M78IMRelationMapper m78IMRelationMapper;

    @Resource
    private M78IMRecordMapper m78IMRecordMapper;

    private static final Gson gson = new Gson();

    /**
     * 判断M78IMRelationPo表是否存在账号关联的bot
     * @param reqDTO
     * @return
     */
    @Override
    public List<M78IMRelationDTO> hasBot(HasBotReqDTO reqDTO) {
        QueryWrapper queryWrapper = null;
        if (reqDTO.getImType().equals(1)) {
            //飞书
            //queryWrapper = QueryWrapper.create().eq("creator", reqDTO.getUser()).eq("im_type_id", reqDTO.getImType()).eq("deleted", 0);
            queryWrapper = QueryWrapper.create()
                    .select(M78_I_M_RELATION_PO.DEFAULT_COLUMNS)
                    .from(M78_I_M_RELATION_PO)
                    .where(M78_I_M_RELATION_PO.DELETED.eq(0))
                    .and(M78_I_M_RELATION_PO.IM_TYPE_ID.eq(reqDTO.getImType()))
                    .and(M78_I_M_RELATION_PO.CREATOR.eq(reqDTO.getUser()).or(M78_I_M_RELATION_PO.RELATION_FLAG.eq(reqDTO.getUser())));

        } else if (reqDTO.getImType().equals(2) || reqDTO.getImType().equals(3)) {
            //微信
            queryWrapper = QueryWrapper.create().eq("relation_flag", reqDTO.getUser()).eq("im_type_id", reqDTO.getImType()).eq("deleted", 0);
        }
        List<M78IMRelationPo> list = m78IMRelationMapper.selectListByQuery(queryWrapper);
        if (list != null && list.size() > 0) {
            List<M78IMRelationDTO> result = new java.util.ArrayList<>();
            for (M78IMRelationPo po : list) {
                M78IMRelationDTO dto = new M78IMRelationDTO();
                BeanCopier copier = BeanCopier.create(M78IMRelationPo.class, M78IMRelationDTO.class, false);
                copier.copy(po, dto, null);
                result.add(dto);
            }
            return result;
        }
        return null;
    }

    /**
     * 判断是否有在使用的bot
     * @param imRecordDTO
     * @return
     */
    @Override
    public IMRecordDTO get(IMRecordDTO imRecordDTO) {
        QueryWrapper queryWrapper = null;
        if (imRecordDTO.getImTypeId().equals(1)) {
            //飞书
            queryWrapper = QueryWrapper.create().eq("user_name", imRecordDTO.getUserName()).eq("chat_id", imRecordDTO.getChatId()).eq("im_type_id", imRecordDTO.getImTypeId()).eq("status", imRecordDTO.getStatus());
        } else if (imRecordDTO.getImTypeId().equals(2) || imRecordDTO.getImTypeId().equals(3)) {
            //微信
            queryWrapper = QueryWrapper.create().eq("user_name", imRecordDTO.getUserName()).eq("im_type_id", imRecordDTO.getImTypeId()).eq("status", imRecordDTO.getStatus());
        }
        M78IMRecordPo m78IMRecordPo = m78IMRecordMapper.selectOneByQuery(queryWrapper);
        if (m78IMRecordPo != null) {
            IMRecordDTO result = new IMRecordDTO();
            BeanCopier copier = BeanCopier.create(M78IMRecordPo.class, IMRecordDTO.class, false);
            copier.copy(m78IMRecordPo, result, null);
            if (imRecordDTO.getImTypeId().equals(2)) {
                List<M78IMRelationPo> m78IMRelationPoList = m78IMRelationMapper.selectListByQuery(QueryWrapper.create().eq("bot_id", m78IMRecordPo.getBotId()).eq("im_type_id", 2));
                result.setUserName(m78IMRelationPoList.get(0).getCreator());
            }
            return result;
        }
        return null;
    }

    /**
     * 添加一个bot使用记录，一般是IM端选择了一个bot后，调用该方法
     * @param imRecordDTO
     * @return
     */
    @Override
    public Boolean add(IMRecordDTO imRecordDTO) {
        if (imRecordDTO.getImTypeId().equals(1)) {
            //飞书
            QueryWrapper queryWrapper = QueryWrapper.create()
                    .select(M78_I_M_RELATION_PO.DEFAULT_COLUMNS)
                    .from(M78_I_M_RELATION_PO)
                    .where(M78_I_M_RELATION_PO.BOT_ID.eq(imRecordDTO.getBotId()))
                    .and(M78_I_M_RELATION_PO.IM_TYPE_ID.eq(imRecordDTO.getImTypeId()))
                    .and(M78_I_M_RELATION_PO.CREATOR.eq(imRecordDTO.getUserName()).or(M78_I_M_RELATION_PO.RELATION_FLAG.eq(imRecordDTO.getUserName())));
            //M78IMRelationPo m78IMRelationPo = m78IMRelationMapper.selectOneByQuery(QueryWrapper.create().eq("creator", imRecordDTO.getUserName()).eq("bot_id", imRecordDTO.getBotId()).eq("im_type_id", imRecordDTO.getImTypeId()));
            M78IMRelationPo m78IMRelationPo = m78IMRelationMapper.selectOneByQuery(queryWrapper);
            if (m78IMRelationPo == null) {
                return false;
            }
        } else if (imRecordDTO.getImTypeId().equals(2)) {
            //微信
            M78IMRelationPo m78IMRelationPo = m78IMRelationMapper.selectOneByQuery(QueryWrapper.create().eq("relation_flag", imRecordDTO.getUserName()).eq("bot_id", imRecordDTO.getBotId()).eq("im_type_id", imRecordDTO.getImTypeId()));
            if (m78IMRelationPo == null) {
                return false;
            }
        }

        QueryWrapper queryWrapper = QueryWrapper.create().eq("user_name", imRecordDTO.getUserName()).eq("chat_id", imRecordDTO.getChatId()).eq("bot_id", imRecordDTO.getBotId()).eq("im_type_id", imRecordDTO.getImTypeId()).eq("status", imRecordDTO.getStatus());
        M78IMRecordPo m78IMRecordPo = m78IMRecordMapper.selectOneByQuery(queryWrapper);
        if (m78IMRecordPo == null) {
            BeanCopier copier = BeanCopier.create(IMRecordDTO.class, M78IMRecordPo.class, false);
            M78IMRecordPo record = new M78IMRecordPo();
            copier.copy(imRecordDTO, record, null);
            int count = m78IMRecordMapper.insert(record);
            if (count > 0) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * 退出在使用的bot
     * @param imRecord
     * @return
     */
    @Override
    public Boolean delete(IMRecordDTO imRecord) {
        QueryWrapper queryWrapper = QueryWrapper.create().eq("user_name", imRecord.getUserName()).eq("chat_id", imRecord.getChatId()).eq("im_type_id", imRecord.getImTypeId()).eq("status", imRecord.getStatus());
        M78IMRecordPo m78IMRecordPo = m78IMRecordMapper.selectOneByQuery(queryWrapper);
        m78IMRecordPo.setStatus(1);
        return m78IMRecordMapper.update(m78IMRecordPo) > 0;
    }

    @Override
    public String executeBot(ExecuteBotReqDTO reqDTO) {
        try {
            log.info("executeBot: req:{}", reqDTO);
            return botService.executeBot(null, reqDTO.getBotId(), reqDTO.getInput(), reqDTO.getUsername(), reqDTO.getTopicId()).getData();
        } catch (Exception e) {
            log.error("executeBot: ", e);
            return "bot执行异常";
        }

    }

    @Override
    public String executeBot(String userName, Long botId, String input, String topicId) {
        try {
            log.info("executeBot: userName={}, botId={}, input={}, topicId={}", userName, botId, input, topicId);
            return botService.executeBot(null, botId, input, userName, topicId).getData();
        } catch (Exception e) {
            log.error("executeBot: ", e);
            return "bot执行异常";
        }
    }


}
