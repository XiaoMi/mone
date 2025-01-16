package run.mone.m78.service.service.multiModal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.mybatisflex.core.query.QueryWrapper;

import run.mone.m78.service.dao.entity.MultimodalLimitPo;
import run.mone.m78.service.dao.mapper.ChatTopicMapper;
import run.mone.m78.service.dao.mapper.MultiModalLimitMapper;

import javax.annotation.Resource;

/**
 * @author zhangxiaowei6
 * @Date 2024/12/13 16:13
 */
@Service
@Slf4j
public class MultiModalLimitService {
    @Resource
    private MultiModalLimitMapper multiModalLimitMapper;

    public int insertAndReturnCount(MultimodalLimitPo entity) {
        // 查询entity里这个用户这天的这种多模态的调用记录是否存在，存在则count+1，不存在则count=1并插入一条数据
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq("user", entity.getUser())
                .eq("limit_day", entity.getLimitDay())
                .eq("type", entity.getType());
                
        MultimodalLimitPo limitPo = multiModalLimitMapper.selectOneByQuery(queryWrapper);
        if (limitPo != null) {
            limitPo.setCount(limitPo.getCount() + 1);
            multiModalLimitMapper.update(limitPo);
            return limitPo.getCount();
        } else {
            entity.setCount(1);
            multiModalLimitMapper.insert(entity);
            return 1;
        }
    }
}
