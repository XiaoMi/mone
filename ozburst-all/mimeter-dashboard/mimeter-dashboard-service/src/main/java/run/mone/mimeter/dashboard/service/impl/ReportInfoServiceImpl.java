package run.mone.mimeter.dashboard.service.impl;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.bo.report.ErrRateBo;
import run.mone.mimeter.dashboard.bo.report.ReportInfoBo;
import run.mone.mimeter.dashboard.bo.scene.SceneSnapshotBo;
import run.mone.mimeter.dashboard.mapper.ReportInfoMapper;
import run.mone.mimeter.dashboard.pojo.ReportInfo;
import run.mone.mimeter.dashboard.pojo.ReportInfoExample;
import run.mone.mimeter.dashboard.service.ReportInfoService;
import run.mone.mimeter.dashboard.service.SceneSnapshotService;
import run.mone.mimeter.dashboard.common.util.Utility;

import java.util.*;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkArgument;
import static run.mone.mimeter.dashboard.bo.common.Constants.*;

/**
 * @author dongzhenxing
 * @version 1.0
 * @since 2022/6/22
 */
@Slf4j
@Service
public class ReportInfoServiceImpl implements ReportInfoService {

    private final String logPrefix = "[ReportInfoService]";

    @Autowired
    private ReportInfoMapper reportInfoMapper;

    private static final Gson gson = new Gson();

    @Autowired
    private SceneSnapshotService sceneSnapshotService;

    private ReportInfoExample buildListReportExample(String tenant, String keyword, Integer pageNo, Integer pageSize) {
        ReportInfoExample example = new ReportInfoExample();
        ReportInfoExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(keyword)) {
            if (StringUtils.isNumeric(keyword)) {
                criteria.andSceneIdEqualTo(Long.parseLong(keyword));
            } else {
                criteria.andCreateByLike("%"+keyword+"%");
                example.or(example.createCriteria().andReportNameLike("%"+keyword+"%"));
            }
        }
        criteria.andFinishTimeIsNotNull();
        Utility.handlePagination(example, pageSize, pageNo);
        example.setOrderByClause("id desc");
        return example;
    }

    private static Consumer<ReportInfo> writeBoConsumer(List<ReportInfoBo> resp) {
        return (po) -> {
            ReportInfoBo bo = new ReportInfoBo();
            BeanUtils.copyProperties(po, bo);
            ErrRateBo errRate = gson.fromJson(po.getTotalStatAnalysisEventList(), ErrRateBo.class);
            if (errRate != null){
                double successRate = 100.00 - Double.parseDouble(errRate.getTotalErrRate());

                bo.setSuccessRate(format2(successRate));
            }
            resp.add(bo);
        };
    }

    @Override
    public Result<List<ReportInfoBo>> listReports(String tenant, String keyword, Integer pageNo, Integer pageSize) {
        return this.reportInfosFromExample(this.buildListReportExample(tenant, keyword, pageNo, pageSize));
    }

    private Result<List<ReportInfoBo>> reportInfosFromExample(ReportInfoExample example) {
        List<ReportInfoBo> list = new ArrayList<>();
        this.reportInfoMapper.selectByExampleWithBLOBs(example).forEach(writeBoConsumer(list));
        return Result.success(list);
    }

    @Override
    public Result<Long> countReports(String tenant, String keyword, Integer pageNo, Integer pageSize) {
        return Result.success(this.reportInfoMapper.countByExample(this.buildListReportExample(tenant, keyword, pageNo, pageSize)));
    }

    @Override
    public Result<ReportInfoBo> getReportDetails(Long sceneId, String reportId) {
        ReportInfoExample example = new ReportInfoExample();
        ReportInfoExample.Criteria criteria = example.createCriteria();
        criteria.andReportIdEqualTo(reportId);

        if (sceneId != null && sceneId > 0) {
            criteria.andSceneIdEqualTo(sceneId);
        }
        List<ReportInfo> list = this.reportInfoMapper.selectByExampleWithBLOBs(example);

        if (list.isEmpty()) {
            return Result.success(null);
        }
        ReportInfoBo bo = new ReportInfoBo();
        BeanUtils.copyProperties(list.get(0), bo);
        if (bo.getFinishTime() == null || bo.getDuration() == null || bo.getDuration() == 0) {
            long tmpFinishTime;
            if (bo.getDuration() == null || bo.getDuration() == 0) {
                tmpFinishTime = bo.getCreateTime().getTime() + 600000;
            } else {
                tmpFinishTime = bo.getCreateTime().getTime() + bo.getDuration() + 1000;
            }
            bo.setFinishTime(tmpFinishTime);

            list.get(0).setStatus(REPORT_STATUS_INACTIVE);
            try {
                reportInfoMapper.updateByPrimaryKey(list.get(0));
            } catch (Exception e) {
                log.error("error:{}",e.getMessage());
            }
        }
        bo.setTotalStatAnalysis(list.get(0).getTotalStatAnalysisEventList());
        return Result.success(bo);
    }

    @Override
    public Result<String> createReport(ReportInfoBo bo) {
        checkArgument(bo != null && bo.checkCreate(), this.logPrefix + "createReport invalid input");

        if (StringUtils.isBlank(bo.getSnapshotId())) {
            Result<SceneSnapshotBo> result = this.sceneSnapshotService.getSceneSnapshotByScene(bo.getSceneId());
            String snapshotId = bo.getSnapshotId();
            SceneSnapshotBo snapshotData = result.getData();

            if (result.getData() != null) {
                snapshotId = snapshotData.getSnapshotId();
            }
            checkArgument(StringUtils.isNotBlank(snapshotId), this.logPrefix + "createReport empty snapshot id");
            bo.setSnapshotId(snapshotId);
        }
        // passed from task service submit task
        String reportId = bo.getReportId();
        bo.setReportId(reportId);
        ReportInfo po = new ReportInfo();
        BeanUtils.copyProperties(bo, po);
        if (this.reportInfoMapper.insertSelective(po) <= 0) {
            return Result.success("");
        }
        return Result.success(reportId);
    }

    @Override
    public Result<Boolean> updateReport(ReportInfoBo bo) {
        checkArgument(bo != null && bo.checkUpdate(), this.logPrefix + "updateReport invalid input");
        ReportInfo po = new ReportInfo();
        BeanUtils.copyProperties(bo, po);

        ReportInfoExample example = new ReportInfoExample();
        ReportInfoExample.Criteria criteria = example.createCriteria();
        criteria.andReportIdEqualTo(bo.getReportId());

        if (bo.getSceneId() != null && bo.getSceneId() > 0) {
            criteria.andSceneIdEqualTo(bo.getSceneId());
        }
        return Result.success(this.reportInfoMapper.updateByExampleSelective(po, example) > 0);
    }

    @Override
    public Result<Integer> removeReports(List<Long> reportIds) {
        checkArgument(reportIds != null && !reportIds.isEmpty(),
                this.logPrefix + "empty or inconsistent input");

        ReportInfoExample example = new ReportInfoExample();
        ReportInfoExample.Criteria criteria = example.createCriteria();
        criteria.andIdIn(reportIds);
        reportInfoMapper.deleteByExample(example);
        return Result.success(1);
    }

    public static String format2(double value) {
        /*
         * %.2f % 表示 小数点前任意位数 2 表示两位小数 格式后的结果为 f 表示浮点型
         */
        return new Formatter().format("%.2f", value).toString();
    }
}
