package run.mone.m78.service.service.user;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import run.mone.m78.service.bo.category.CategoryVo;
import run.mone.m78.service.bo.user.UserCollectReq;
import run.mone.m78.service.common.enums.UserCollectType;
import run.mone.m78.service.dao.entity.M78UserCollect;
import run.mone.m78.service.dao.mapper.M78UserCollectMapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zhangxiaowei6
 * @Date 2024/3/15 16:20
 */

@Component
@Slf4j
public class UserCollectService extends ServiceImpl<M78UserCollectMapper, M78UserCollect> {

    public Result<Boolean> applyCollect(String userName, UserCollectReq req) {
        try {
            //type校验
            if (!UserCollectType.isValid(req.getType())) {
                return Result.fromException(new Exception("type不合法"));
            }
            //先查是否收藏过
            List<M78UserCollect> collectList = super.list(QueryWrapper.create().eq("deleted", 0).eq("type",
                    req.getType()).eq("collect_id", req.getCollectId()).eq("username", userName)).stream().toList();
            if (!collectList.isEmpty()) {
                return Result.fromException(new Exception("已经收藏过了"));
            }

            List<M78UserCollect> newCollectlist = super.list(QueryWrapper.create().eq("deleted", 1).eq("type",
                    req.getType()).eq("collect_id", req.getCollectId()).eq("username", userName)).stream().toList();
            if (newCollectlist.size() == 1) {
                //收藏过，后来取消了，此次重新收藏则将deleted重新置位0
                M78UserCollect userCollect = newCollectlist.getFirst();
                userCollect.setDeleted(0);
                userCollect.setUpdateTime(LocalDateTime.now());
                userCollect.setUpdater(userName);
                return Result.success(super.updateById(userCollect));
            }

            M78UserCollect userCollect = M78UserCollect.builder().type(req.getType()).username(userName)
                    .collectId(req.getCollectId()).creator(userName).createTime((LocalDateTime.now()))
                    .updateTime((LocalDateTime.now())).updater(userName).build();
            boolean res = this.save(userCollect);
            log.info("applyCollect userName: {},req: {} res: {}", userName, req, res);
            return Result.success(res);
        } catch (Exception e) {
            log.error("applyCollect error:{}", e);
            return null;
        }
    }

    public Result<Boolean> isCollect(String userName, UserCollectReq req) {
        log.info("isCollect userName: {},req: {}", userName, req);
        try {
            //type校验
            if (!UserCollectType.isValid(req.getType())) {
                return Result.fromException(new Exception("type不合法"));
            }
            return Result.success((long) super.list(QueryWrapper.create().eq("deleted", 0).eq("type",
                    req.getType()).eq("collect_id", req.getCollectId()).eq("username", userName)).size() > 0);
        } catch (Exception e) {
            log.error("isCollect error:{}", e);
            return Result.fromException(e);
        }
    }

    public Result<Boolean> deleteCollect(String userName, UserCollectReq req) {
        log.info("deleteCollect userName: {},req: {}", userName, req);
        //将deleted置为1
        try {
            //type校验
            if (!UserCollectType.isValid(req.getType())) {
                return Result.fromException(new Exception("type不合法"));
            }
            //先查是否有,unique(name,type,collect_id)
            List<M78UserCollect> list = super.list(QueryWrapper.create().eq("deleted", 0).eq("type",
                    req.getType()).eq("collect_id", req.getCollectId()).eq("username", userName)).stream().toList();
            if (list.size() != 1) {
                return Result.fromException(new Exception("还未收藏过或数据异常"));
            }
            M78UserCollect userCollect = list.getFirst();
            userCollect.setDeleted(1);
            userCollect.setUpdateTime(LocalDateTime.now());
            userCollect.setUpdater(userName);
            boolean res = super.updateById(userCollect);
            return Result.success(res);
        } catch (Exception e) {
            log.error("deleteCollect error: {}", e);
            return Result.fromException(e);
        }
    }

}
