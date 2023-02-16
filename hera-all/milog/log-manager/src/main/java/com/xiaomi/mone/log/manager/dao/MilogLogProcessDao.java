package com.xiaomi.mone.log.manager.dao;

import com.xiaomi.mone.log.api.model.vo.UpdateLogProcessCmd;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogProcessDO;
import com.xiaomi.youpin.docean.anno.Service;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.sql.Sql;

import javax.annotation.Resource;
import java.util.List;

@Service
public class MilogLogProcessDao {
    @Resource
    private NutDao dao;

    /**
     * 获取agent监控的日志详情
     * @param agentId
     * @return
     */
    public List<MilogLogProcessDO> queryByAgentId(long agentId) {
        return dao.query(MilogLogProcessDO.class, Cnd.where("agent_id", "IN", agentId));
    }

    public List<MilogLogProcessDO> queryByIp(String ip) {
        return dao.query(MilogLogProcessDO.class, Cnd.where("ip", "=", ip));
    }

    /**
     * 批量保存
     * @param doList
     */
    public List<MilogLogProcessDO> bulkSave(List<MilogLogProcessDO> doList) {
        return dao.insert(doList);
    }

    /**
     * 删除日志收集进度
     * @param tailId
     */
    public void deleteByTailId(Long tailId) {
        dao.clear(MilogLogProcessDO.class, Cnd.where("tailId", "=", tailId));
    }

    /**
     * 批量更新日志收集进度
     *
     * @param cmdList
     */
    public void updateProcessBatch(List<UpdateLogProcessCmd> cmdList) {
//        if (cmdList == null || cmdList.isEmpty()) {
//            return;
//        }
//        Sql sql = Sqls.create("UPDATE milog_log_process SET file_row_number = @fileRowNumber, pointer = @pointer, collect_time = @collectTime, utime = @utime WHERE ip = @ip AND tailId = @tailId");
//        long current = System.currentTimeMillis();
//        for (UpdateLogProcessCmd cmd : cmdList) {
//            sql.params()
//                    .set("fileRowNumber", cmd.getFileRowNumber())
//                    .set("pointer", cmd.getPointer())
//                    .set("collectTime", cmd.getCollectTime())
//                    .set("utime", current)
//                    .set("ip", cmd.getIp())
//                    .set("tailId", cmd.getTailId());
//            sql.addBatch();
//        }
//        dao.execute(sql);
    }
}
