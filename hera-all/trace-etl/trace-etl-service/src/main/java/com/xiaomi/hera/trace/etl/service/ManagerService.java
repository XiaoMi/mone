package com.xiaomi.hera.trace.etl.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaomi.hera.trace.etl.api.service.TraceEtlService;
import com.xiaomi.hera.trace.etl.domain.HeraTraceConfigVo;
import com.xiaomi.hera.trace.etl.domain.HeraTraceEtlConfig;
import com.xiaomi.hera.trace.etl.domain.PageData;
import com.xiaomi.hera.trace.etl.mapper.HeraTraceEtlConfigMapper;
import com.xiaomi.hera.trace.etl.util.pool.AsyncNotify;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * @Description 通过bootstrap工程BeanConfig初始化，以免不需要的工程启动报错
 * @Author dingtao
 * @Date 2022/4/18 3:31 下午
 */
@Slf4j
public class ManagerService {

    @Autowired
    private AsyncNotify asyncNotify;

    private TraceEtlService traceEtlService;

    private HeraTraceEtlConfigMapper heraTraceEtlConfigMapper;

    public ManagerService(HeraTraceEtlConfigMapper heraTraceEtlConfigMapper) {
        this.heraTraceEtlConfigMapper = heraTraceEtlConfigMapper;
    }

    public ManagerService(HeraTraceEtlConfigMapper heraTraceEtlConfigMapper, TraceEtlService traceEtlService) {
        this.heraTraceEtlConfigMapper = heraTraceEtlConfigMapper;
        this.traceEtlService = traceEtlService;
    }

    public List<HeraTraceEtlConfig> getAll(HeraTraceConfigVo vo) {
        return heraTraceEtlConfigMapper.getAll(vo);
    }

    public PageData<List<HeraTraceEtlConfig>> getAllPage(HeraTraceConfigVo vo) {
        PageData<List<HeraTraceEtlConfig>> pageData = new PageData<>();
        pageData.setPage(vo.getPage());
        pageData.setPageSize(vo.getPageSize());
        PageHelper.startPage(vo.getPage(), vo.getPageSize());
        Page<HeraTraceEtlConfig> all = heraTraceEtlConfigMapper.getAllPage(vo.getUser());
        PageInfo<HeraTraceEtlConfig> heraTraceEtlConfigPageInfo = new PageInfo<>(all);
        pageData.setTotal(heraTraceEtlConfigPageInfo.getTotal());
        pageData.setPages(heraTraceEtlConfigPageInfo.getPages());
        pageData.setList(heraTraceEtlConfigPageInfo.getList());
        return pageData;
    }

    public HeraTraceEtlConfig getByBaseInfoId(Integer baseInfoId) {
        return heraTraceEtlConfigMapper.getByBaseInfoId(baseInfoId);
    }

    public HeraTraceEtlConfig getById(Integer id) {
        return heraTraceEtlConfigMapper.selectByPrimaryKey(id);
    }

    public Result insertOrUpdate(HeraTraceEtlConfig config, String user) {
        Date now = new Date();
        int i = 0;
        if (config.getId() == null) {
            // 校验是否存在
            HeraTraceEtlConfig byBaseInfoId = heraTraceEtlConfigMapper.getByBaseInfoId(config.getBaseInfoId());
            if(byBaseInfoId != null){
                return Result.fail(GeneralCodes.InternalError, "项目配置已存在，请勿重复添加");
            }
            config.setCreateTime(now);
            config.setUpdateTime(now);
            config.setCreateUser(user);
            i = heraTraceEtlConfigMapper.insertSelective(config);
            if (i > 0) {
                asyncNotify.submit(() -> {
                    try {
                        traceEtlService.insertConfig(config);
                    } catch (Exception e) {
                        log.error("insert sync etl error : ", e);
                    }
                });
            }
        } else {
            config.setUpdateTime(now);
            config.setUpdateUser(user);
            i = heraTraceEtlConfigMapper.updateByPrimaryKeySelective(config);
            if (i > 0) {
                asyncNotify.submit(() -> {
                    try {
                        traceEtlService.updateConfig(config);
                    } catch (Exception e) {
                        log.error("update sync etl error : ", e);
                    }
                });
            }
        }
        return i > 0 ? Result.success(null) : Result.fail(GeneralCodes.InternalError, "操作失败");
    }

    public int delete(HeraTraceEtlConfig config) {
        HeraTraceEtlConfig heraTraceEtlConfig = heraTraceEtlConfigMapper.selectByPrimaryKey(config.getId());
        int i = heraTraceEtlConfigMapper.deleteByPrimaryKey(config.getId());
        if (i > 0) {
            asyncNotify.submit(() -> {
                try {
                    traceEtlService.deleteConfig(heraTraceEtlConfig);
                } catch (Exception e) {
                    log.error("delete sync etl error : ", e);
                }
            });
        }
        return i;
    }
}
