package com.xiaomi.mone.log.manager.service.impl;

import com.xiaomi.mone.log.api.model.vo.UpdateLogProcessCmd;
import com.xiaomi.mone.log.api.service.LogProcessService;
import com.xiaomi.mone.log.common.Config;
import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.dao.MilogLogTailDao;
import com.xiaomi.mone.log.manager.domain.LogProcess;
import com.xiaomi.mone.log.manager.mapper.MilogLogProcessMapper;
import com.xiaomi.mone.log.manager.model.dto.TailLogProcessDTO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogProcessDOMybatis;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class LogProcessServiceImpl implements LogProcessService {

    @Resource
    private MilogLogProcessMapper processMapper;

    @Resource
    private LogProcess logProcess;

    @Resource
    private MilogLogTailDao logtailDao;

    /**
     * 更新日志收集进度
     *
     * @param cmd
     */
    @Override
    public void updateLogProcess(UpdateLogProcessCmd cmd) {
        if (null != logProcess) {
            logProcess.updateLogProcess(cmd);
        }
    }

    public MilogLogProcessDOMybatis getByIdFramework(Long id) {
        return processMapper.selectById(id);
    }

    /**
     * 获取store的日志收集进度
     *
     * @param type
     * @param value
     * @return
     */
    public Result<List<TailLogProcessDTO>> getStoreLogProcess(String type, String value) {
        if (StringUtils.isEmpty(type) || StringUtils.isEmpty(value)) {
            return Result.failParam("type及value不能为空");
        }
        List<TailLogProcessDTO> dtoList;
        switch (type) {
            case "store":
                dtoList = logProcess.getStoreLogProcess(Long.parseLong(value), "");
                break;
            case "tail":
                dtoList = logProcess.getTailLogProcess(Long.parseLong(value), "");
                break;
            case "ip":
                String[] params = value.split(",");
                dtoList = logProcess.getStoreLogProcess(Long.parseLong(params[0]), params[1]);
                break;
            case "tail&ip":
                String[] params2 = value.split(",");
                dtoList = logProcess.getTailLogProcess(Long.parseLong(params2[0]), params2[1]);
                break;
            default:
                return Result.failParam("type类型不合法");
        }
        return Result.success(dtoList);
    }

}
