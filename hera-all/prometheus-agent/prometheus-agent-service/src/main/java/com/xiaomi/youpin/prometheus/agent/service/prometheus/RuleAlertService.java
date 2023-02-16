package com.xiaomi.youpin.prometheus.agent.service.prometheus;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xiaomi.youpin.prometheus.agent.Commons;
import com.xiaomi.youpin.prometheus.agent.Impl.RuleAlertDao;
import com.xiaomi.youpin.prometheus.agent.entity.RuleAlertEntity;
import com.xiaomi.youpin.prometheus.agent.enums.RuleAlertStatusEnum;
import com.xiaomi.youpin.prometheus.agent.param.alert.RuleAlertParam;
import com.xiaomi.youpin.prometheus.agent.result.Result;
import com.xiaomi.youpin.prometheus.agent.enums.ErrorCode;
import com.xiaomi.youpin.prometheus.agent.vo.PageDataVo;
import lombok.extern.slf4j.Slf4j;
import org.nutz.lang.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RuleAlertService {
    @Autowired
    RuleAlertDao dao;

    public static final Gson gson = new Gson();

    public Result CreateRuleAlert(RuleAlertParam param) {
        log.info("RuleAlertService.CreateRuleAlert  param : {}", gson.toJson(param));
        try {
            if (param.getPromCluster() == null) {
                param.setPromCluster(Commons.DEFAULT_ALERT_PROM_CLUSTER);
            }
            RuleAlertEntity ruleAlertEntity = new RuleAlertEntity();
            ruleAlertEntity.setName(param.getAlert());
            ruleAlertEntity.setCname(param.getCname());
            ruleAlertEntity.setExpr(param.getExpr());
            ruleAlertEntity.setLabels(transLabel2String(param.getLabels()));
            ruleAlertEntity.setAnnotation(transAnnotation2String(param.getAnnotations()));
            ruleAlertEntity.setAlertFor(param.getFor());
            ruleAlertEntity.setEnv(Strings.join(",", param.getEnv()));
            ruleAlertEntity.setEnabled(param.getEnabled() == null ? 1 : param.getEnabled());
            ruleAlertEntity.setPriority(transPriority2Integer(param.getPriority()));
            ruleAlertEntity.setCreatedBy("xxx");  //TODO: 以后改造成真实用户
            ruleAlertEntity.setCreatedTime(new Date());
            ruleAlertEntity.setUpdatedTime(new Date());
            ruleAlertEntity.setDeletedBy("");
            ruleAlertEntity.setPromCluster(param.getPromCluster());
            ruleAlertEntity.setStatus(RuleAlertStatusEnum.PENDING.getDesc());
            ruleAlertEntity.setType("0");
            ruleAlertEntity.setAlertMember(Strings.join(",", param.getAlert_member()));
            ruleAlertEntity.setAlertAtPeople(Strings.join(",", param.getAlert_at_people()));
            ruleAlertEntity.setAlert_group(param.getGroup() == null ? "example" : param.getGroup());

            Long id = dao.CreateRuleAlert(ruleAlertEntity);
            log.info("RuleAlertService.CreateRuleAlert  res : {}", id);
            return Result.success(id);
        } catch (Exception e) {
            log.error("RuleAlertService.CreateRuleAlert  fail, name: {},message: {}", param.getAlert(), e.getMessage());
            return Result.fail(ErrorCode.unknownError, e.getMessage());
        }
    }

    public Result UpdateRuleAlert(String id, RuleAlertParam param) {
        log.info("RuleAlertService.UpdateRuleAlert  param : {}", gson.toJson(param));

        // 增量替换，允许修改字段：cname、expr、for、labels、annotations、priority、env、alertMember
        try {
            RuleAlertEntity data = dao.GetRuleAlert(id);

            if (param.getCname() != null) {
                data.setCname(param.getCname());
            }
            if (param.getExpr() != null) {
                data.setExpr(param.getExpr());
            }
            if (param.getFor() != null) {
                data.setAlertFor(param.getFor());
            }
            if (param.getLabels() != null) {
                data.setLabels(transLabel2String(param.getLabels()));
            }
            if (param.getAnnotations() != null) {
                data.setAnnotation(transAnnotation2String(param.getAnnotations()));
            }
            if (param.getPriority() != null) {
                data.setPriority(transPriority2Integer(param.getPriority()));
            }
            if (param.getEnv() != null) {
                data.setEnv(Strings.join(",", param.getEnv()));
            }
            if (param.getAlert_member() != null) {
                data.setAlertMember(Strings.join(",", param.getAlert_member()));
            }
            data.setUpdatedTime(new Date());

            String res = dao.UpdateRuleAlert(id, data);
            return Result.success(res);
        } catch (Exception e) {
            log.error("RuleAlertService.UpdateRuleAlert fail param : {}", gson.toJson(param));
            return Result.fail(ErrorCode.unknownError, e.getMessage());
        }
    }

    public Result DeleteRuleAlert(String id) {
        log.info("RuleAlertService.DeleteRuleAlert id : {}", id);

        try {
            int res = dao.DeleteRuleAlert(id);
            if (res != 1) {
                return Result.fail(ErrorCode.OperationFailed);
            }
            log.info("RuleAlertService.DeleteRuleAlert res : {}", res);
            return Result.success(res);
        } catch (Exception e) {
            log.error("RuleAlertService.DeleteRuleAlert fail, id : {}", id);
            return Result.fail(ErrorCode.unknownError, e.getMessage());
        }
    }

    public Result GetRuleAlert(String id) {
        log.info("RuleAlertService.GetRuleAlert id : {}", id);
        RuleAlertEntity ruleAlertEntity = dao.GetRuleAlert(id);
        log.info("RuleAlertService.GetRuleAlert res : {}", gson.toJson(ruleAlertEntity));
        return Result.success(ruleAlertEntity);
    }

    public Result GetRuleAlertList(Integer pageSize, Integer pageNo) {
        log.info("RuleAlertService.GetRuleAlertList pageSize : {} pageNo : {}", pageSize, pageNo);
        List<RuleAlertEntity> lists = dao.GetRuleAlertList(pageSize, pageNo);
        PageDataVo<RuleAlertEntity> pdo = new PageDataVo<RuleAlertEntity>();
        pdo.setPageNo(pageNo);
        pdo.setPageSize(pageSize);
        pdo.setTotal(dao.CountRuleAlert());
        pdo.setList(lists);
        log.info("RuleAlertService.GetRuleAlertList count : {}", pdo.getTotal());
        return Result.success(pdo);
    }

    public Result EnabledRuleAlert(String id, String enabled) {
        log.info("RuleAlertService.EnabledRuleAlert id : {} enabled : {}", id, enabled);
        try {
            RuleAlertEntity ruleAlertEntity = dao.GetRuleAlert(id);
            if (ruleAlertEntity == null) {
                return Result.fail(ErrorCode.NO_DATA_FOUND);
            }
            ruleAlertEntity.setEnabled(Integer.parseInt(enabled));
            String res = dao.UpdateRuleAlert(id, ruleAlertEntity);
            return Result.success(res);
        } catch (Exception e) {
            log.error("RuleAlertService.EnabledRuleAlert fail id:{}", id);
            return Result.fail(ErrorCode.unknownError, e.getMessage());
        }
    }

    public Result SendAlert(String body) {
        JsonObject jsonObject = gson.fromJson(body, JsonObject.class);
        JsonArray alerts = jsonObject.get("alerts").getAsJsonArray();
        JsonObject groupLabels = jsonObject.get("groupLabels").getAsJsonObject();
        String alertName = groupLabels.get("alertname").getAsString();
        log.info("SendAlert begin send AlertName :{}", alertName);
        String silenceUrl = jsonObject.get("externalURL").getAsString();
        //查表看负责人
        String[] principals = dao.GetRuleAlertAtPeople(alertName);
        StringBuilder finalAlert = new StringBuilder();
        finalAlert.append("报警名称: ").append(alertName).append("\r\n");
        for (JsonElement element : alerts
        ) {
            //todo:整理数据
            JsonObject singleAlert = element.getAsJsonObject();
            // JsonObject annotations = singleAlert.get("annotations").getAsJsonObject();
            // String summary = annotations.get("summary").getAsString();
            // finalAlert.append(summary);
            finalAlert.append("\r\n");
        }
        finalAlert.append("报警静默请点击").append(silenceUrl).append("\r\n");
        log.info("SendAlert success AlertName:{}", alertName);
        return Result.success("发送告警：" + alertName);
    }

    //TODO: 提供给alertManagerClient使用的临时方法，以后需要重构
    public List<RuleAlertEntity> GetAllRuleAlertList() {
        List<RuleAlertEntity> list = dao.GetAllRuleAlertList();
        return list;
    }

    //将labelmap转换成string
    private String transLabel2String(Map<String, String> labels) {
        String res = "";
        for (Map.Entry<String, String> entry : labels.entrySet()) {
            res += entry.getKey() + "=" + entry.getValue() + ",";
        }
        return res.substring(0, res.length() - 1);
    }

    //将annotationMap转换成string
    private String transAnnotation2String(Map<String, String> annotations) {
        String res = "";
        for (Map.Entry<String, String> entry : annotations.entrySet()) {
            res += entry.getKey() + "=" + entry.getValue() + ",";
        }
        return res.substring(0, res.length() - 1);
    }

    private int transPriority2Integer(String priority) {
        String[] ps = priority.split("P");
        if (ps.length == 2) {
            return Integer.parseInt(ps[1]);
        } else {
            //默认P2
            return 2;
        }
    }


}
