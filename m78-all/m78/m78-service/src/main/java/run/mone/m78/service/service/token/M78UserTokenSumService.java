package run.mone.m78.service.service.token;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import run.mone.m78.service.dao.entity.M78UserTokenSum;
import run.mone.m78.service.dao.mapper.M78UserTokenSumMapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 服务层实现。
 *
 * @author hoho
 * @since 2024-09-20
 */
@Service
public class M78UserTokenSumService extends ServiceImpl<M78UserTokenSumMapper, M78UserTokenSum> {

    /**
     * 更新用户的代币总数
     *
     * @param user      用户名
     * @param costToken 增加的代币数量
     *                  <p>
     *                  该方法首先尝试获取指定用户的代币记录，如果不存在则创建一条新的记录。然后，将传入的代币数量累加到用户的总代币数中，并更新最后修改时间。最后，保存或更新用户的代币记录。
     */
    public void updateTokenSum(String user, Long costToken) {
        M78UserTokenSum m78UserTokenSum = getOneOpt(QueryWrapper.create()
                .eq("user", user))
                .orElse(new M78UserTokenSum(null, user, 0L, LocalDateTime.now()));
        m78UserTokenSum.setSumToken(m78UserTokenSum.getSumToken() + costToken);
        m78UserTokenSum.setUpdateDate(LocalDateTime.now());
        this.saveOrUpdate(m78UserTokenSum);
    }

    /**
     * 列出花费token最多的topN用户
     *
     * @param topN 需要列出的用户数量
     * @return 花费token最多的用户列表
     */
    // 列出花费token最多的topN用户
    public List<M78UserTokenSum> listTopNUsersByTokenSum(int topN) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.orderBy("sum_token", false).limit(topN);
        return list(queryWrapper);
    }


}
