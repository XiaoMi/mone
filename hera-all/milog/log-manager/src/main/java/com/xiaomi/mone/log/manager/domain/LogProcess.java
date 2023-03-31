package com.xiaomi.mone.log.manager.domain;

import com.google.common.collect.Lists;
import com.xiaomi.mone.log.api.model.vo.AgentLogProcessDTO;
import com.xiaomi.mone.log.api.model.vo.TailLogProcessDTO;
import com.xiaomi.mone.log.api.model.vo.UpdateLogProcessCmd;
import com.xiaomi.mone.log.api.service.LogProcessCollector;
import com.xiaomi.mone.log.manager.dao.MilogLogTailDao;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogTailDo;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.common.StringUtils;
import com.xiaomi.youpin.docean.plugin.dubbo.anno.Reference;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class LogProcess {

    @Resource
    private MilogLogTailDao logtailDao;

    @Getter
    private Map<String, List<UpdateLogProcessCmd.CollectDetail>> tailProgressMap = new ConcurrentHashMap<>(256);

    @Reference(interfaceClass = LogProcessCollector.class, group = "$dubbo.env.group", check = false, timeout = 14000)
    private LogProcessCollector logProcessCollector;

    /**
     * 更新日志收集进度
     *
     * @param cmd
     */
    public void updateLogProcess(UpdateLogProcessCmd cmd) {
        log.debug("[LogProcess.updateLogProcess] cmd:{} ", cmd);
        if (cmd == null || StringUtils.isEmpty(cmd.getIp())) {
            return;
        }
        tailProgressMap.put(cmd.getIp(), cmd.getCollectList());
    }

    /**
     * 获取agent日志收集进度
     *
     * @param ip
     * @return
     */
    public List<AgentLogProcessDTO> getAgentLogProcess(String ip) {
        return logProcessCollector.getAgentLogProcess(ip);
    }

    /**
     * 获取tail的日志收集进度
     *
     * @param tailId
     * @return
     */
    public List<TailLogProcessDTO> getTailLogProcess(Long tailId, String targetIp) {
        if (tailId == null) {
            return Lists.newArrayList();
        }
        MilogLogTailDo logTail = logtailDao.queryById(tailId);
        if (null == logTail) {
            return Lists.newArrayList();
        }
        return logProcessCollector.getTailLogProcess(tailId, logTail.getTail(), targetIp);
    }

    /**
     * 获取store的日志收集进度
     *
     * @param storeId
     * @return
     */
    public List<TailLogProcessDTO> getStoreLogProcess(Long storeId, String targetIp) {
        if (storeId == null) {
            return new ArrayList<>();
        }
        List<MilogLogTailDo> logtailList = logtailDao.getMilogLogtailByStoreId(storeId);
        List<TailLogProcessDTO> dtoList = new ArrayList<>();
        List<TailLogProcessDTO> processList;
        for (MilogLogTailDo milogLogtailDo : logtailList) {
            processList = getTailLogProcess(milogLogtailDo.getId(), targetIp);
            if (!processList.isEmpty()) {
                dtoList.addAll(processList);
            }
        }
        return dtoList;
    }

    public List<UpdateLogProcessCmd.CollectDetail> getColProcessImperfect(Double progressRation) {
        return logProcessCollector.getColProcessImperfect(progressRation);
    }
}
