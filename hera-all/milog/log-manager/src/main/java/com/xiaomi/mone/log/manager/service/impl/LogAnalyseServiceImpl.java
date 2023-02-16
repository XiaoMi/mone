package com.xiaomi.mone.log.manager.service.impl;

import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.exception.CommonError;
import com.xiaomi.mone.log.manager.common.context.MoneUserContext;
import com.xiaomi.mone.log.manager.model.convert.DGRefConvert;
import com.xiaomi.mone.log.manager.model.convert.DashboardConvert;
import com.xiaomi.mone.log.manager.model.convert.GraphConvert;
import com.xiaomi.mone.log.manager.model.convert.GraphTypeConvert;
import com.xiaomi.mone.log.manager.dao.LogstoreDao;
import com.xiaomi.mone.log.manager.domain.AnalyseLog;
import com.xiaomi.mone.log.manager.mapper.MilogAnalyseDashboardGraphRefMapper;
import com.xiaomi.mone.log.manager.mapper.MilogAnalyseDashboardMapper;
import com.xiaomi.mone.log.manager.mapper.MilogAnalyseGraphMapper;
import com.xiaomi.mone.log.manager.mapper.MilogAnalyseGraphTypeMapper;
import com.xiaomi.mone.log.manager.model.dto.*;
import com.xiaomi.mone.log.manager.model.pojo.*;
import com.xiaomi.mone.log.manager.model.vo.*;
import com.xiaomi.mone.log.manager.service.LogAnalyseService;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class LogAnalyseServiceImpl implements LogAnalyseService {
    @Resource
    private MilogAnalyseDashboardGraphRefMapper dgRefMapper;

    @Resource
    private MilogAnalyseDashboardMapper dashboardMapper;

    @Resource
    private MilogAnalyseGraphMapper graphMapper;

    @Resource
    private MilogAnalyseGraphTypeMapper graphTypeMapper;

    @Resource
    private AnalyseLog analyseLog;

    @Resource
    private LogstoreDao logstoreDao;

    private List<String> ignoreKeyList = new ArrayList<>();

    {
        ignoreKeyList.add("message");
        ignoreKeyList.add("logsource");
        ignoreKeyList.add("timestamp");
        ignoreKeyList.add("traceId");
        ignoreKeyList.add("line");
    }

    public Result<DashboardDTO> getDashboardGraph(LogAnalyseQuery logAnalyseQuery) {
        List<LogAnalyseDashboardDO> dashboardList = dashboardMapper.getByStoreId(logAnalyseQuery.getStoreId());
        if (dashboardList == null || dashboardList.isEmpty()) {
            return Result.success();
        }
        LogAnalyseDashboardDO dashboard = dashboardList.get(0);
        List<DashboardGraphDTO> dashboardGraphList = graphMapper.getDashboardGraph(dashboard.getId());
        return Result.success(DashboardConvert.INSTANCE.fromDO(dashboard, dashboardGraphList));
    }

    public Result<Long> createGraph(CreateGraphCmd cmd) {
        LogAnalyseGraphDO graphDO = GraphConvert.INSTANCE.toDO(cmd);
        graphDO.setCreator(MoneUserContext.getCurrentUser().getUser());
        graphDO.setUpdater(MoneUserContext.getCurrentUser().getUser());
        long thisTime = System.currentTimeMillis();
        graphDO.setCreateTime(thisTime);
        graphDO.setUpdateTime(thisTime);
        graphMapper.insert(graphDO);
        return Result.success(graphDO.getId());
    }

    public Result<Boolean> updateGraph(UpdateGraphCmd cmd) {
        LogAnalyseGraphDO graphDO = GraphConvert.INSTANCE.toDO(cmd);
        graphDO.setUpdater(MoneUserContext.getCurrentUser().getUser());
        graphDO.setUpdateTime(System.currentTimeMillis());
        int res = graphMapper.updateById(graphDO);
        return res == 1 ? Result.success(true) : Result.fail(CommonError.ParamsError);
    }

    public Result<Boolean> deleteGraph(Long graphId) {
        graphMapper.deleteById(graphId);
        dgRefMapper.deleteGraphRef(graphId);
        return Result.success(true);
    }

    public Result<List<GraphDTO>> searchGraph(GraphQuery query) {
        List<GraphDTO> graphList = graphMapper.search(query);
        return Result.success(graphList);
    }

    public Result<Boolean> ref(DGRefCmd cmd) {
        Long refed = dgRefMapper.isRefed(cmd.getDashboardId(), cmd.getGraphId());
        if (refed == null || refed == 0) {
            int res = dgRefMapper.insert(DGRefConvert.INSTANCE.toDo(cmd));
            return res == 1 ? Result.success(true) : Result.fail(CommonError.ParamsError);
        }
        return Result.fail(CommonError.ParamsError.getCode(), "图表已存在此仪表盘");
    }

    public Result<Boolean> delRef(DGRefDelCmd cmd) {
        int res = dgRefMapper.delRef(cmd.getDashboardId(), cmd.getGraphId());
        return res == 1 ? Result.success(true) : Result.fail(CommonError.ParamsError);
    }

    public Result<Boolean> updateRef(DGRefUpdateCmd cmd) {
        if (cmd.getDashboardId() == null || cmd.getGraphList() == null || cmd.getGraphList().isEmpty()) {
            return Result.fail(CommonError.ParamsError);
        }
        for (DGRefUpdateCmd.DGRefDetailUpdateCmd graph : cmd.getGraphList()) {
            LogDashboardGraphRefDO refOld = dgRefMapper.getRef(cmd.getDashboardId(), graph.getGraphId());
            if (refOld == null) {
                return Result.fail(CommonError.ParamsError);
            }
            LogDashboardGraphRefDO refNew = DGRefConvert.INSTANCE.toDo(graph);
            refNew.setId(refOld.getId());
            dgRefMapper.updateById(refNew);
        }
        return Result.success(true);
    }

    public Result<LogAnalyseDataDTO> data(LogAnalyseDataQuery query) throws IOException {
        LogAnalyseDataDTO dto = analyseLog.getData(query);
        return Result.success(dto);
    }

    public Result<LogAnalyseDataDTO> dataPre(LogAnalyseDataPreQuery query) throws IOException {
        LogAnalyseDataDTO dto = analyseLog.getData(query);
        return Result.success(dto);
    }

    public Result<Long> createDashboard(CreateDashboardCmd cmd) {
        LogAnalyseDashboardDO dashboardDO = DashboardConvert.INSTANCE.toDO(cmd);
        dashboardDO.setCreateTime(System.currentTimeMillis());
        dashboardDO.setCreator(MoneUserContext.getCurrentUser().getUser());

        // TODO 设置默认名字，后期需要多个dashboard再更改
        MilogLogStoreDO logstore = logstoreDao.queryById(cmd.getStoreId());
        if (logstore == null) {
            return Result.failParam("创建失败, logstore不存在");
        }
        dashboardDO.setName(logstore.getLogstoreName() + "仪表盘");

        dashboardMapper.insert(dashboardDO);
        return Result.success(dashboardDO.getId());
    }

    public Result<List<GraphTypeDTO>> type() {
        List<LogAnalyseGraphTypeDO> gtaphTypeDOList = graphTypeMapper.selectList(null);
        List<GraphTypeDTO> graphTypeDTOList = GraphTypeConvert.INSTANCE.toDTOList(gtaphTypeDOList);
        return Result.success(graphTypeDTOList);
    }


    public Result<List<String>> supportKye(Long storeId) {
        if (storeId == null) {
            return Result.fail(CommonError.ParamsError);
        }
        MilogLogStoreDO logStore = logstoreDao.queryById(storeId);
        if (logStore == null) {
            return Result.fail(CommonError.ParamsError);
        }
        String[] keyDescripArray = logStore.getKeyList().split(",");
        List<String> keyList = new ArrayList<>();
        String key;
        for (String keyDes : keyDescripArray) {
            key = keyDes.split(":")[0];
            if (ignoreKeyList.contains(key)) {
                continue;
            }
            keyList.add(key);
        }
        return Result.success(keyList);
    }
}
