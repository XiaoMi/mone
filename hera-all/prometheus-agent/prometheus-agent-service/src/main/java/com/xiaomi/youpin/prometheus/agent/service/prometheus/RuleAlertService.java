package com.xiaomi.youpin.prometheus.agent.service.prometheus;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xiaomi.youpin.feishu.FeiShu;
import com.xiaomi.youpin.prometheus.agent.Commons;
import com.xiaomi.youpin.prometheus.agent.Impl.RuleAlertDao;
import com.xiaomi.youpin.prometheus.agent.entity.RuleAlertEntity;
import com.xiaomi.youpin.prometheus.agent.enums.RuleAlertStatusEnum;
import com.xiaomi.youpin.prometheus.agent.param.alert.RuleAlertParam;
import com.xiaomi.youpin.prometheus.agent.result.Result;
import com.xiaomi.youpin.prometheus.agent.enums.ErrorCode;
import com.xiaomi.youpin.prometheus.agent.result.alertManager.AlertManagerFireResult;
import com.xiaomi.youpin.prometheus.agent.result.alertManager.Alerts;
import com.xiaomi.youpin.prometheus.agent.result.alertManager.GroupLabels;
import com.xiaomi.youpin.prometheus.agent.service.FeishuService;
import com.xiaomi.youpin.prometheus.agent.service.alarmContact.FeishuAlertContact;
import com.xiaomi.youpin.prometheus.agent.service.alarmContact.MailAlertContact;
import com.xiaomi.youpin.prometheus.agent.util.DateUtil;
import com.xiaomi.youpin.prometheus.agent.util.FreeMarkerUtil;
import com.xiaomi.youpin.prometheus.agent.util.MailUtil;
import com.xiaomi.youpin.prometheus.agent.vo.PageDataVo;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nutz.lang.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class RuleAlertService {
    @Autowired
    RuleAlertDao dao;

    @Autowired
    FeishuAlertContact feishuAlertContact;

    @Autowired
    MailAlertContact mailAlertContact;

    @NacosValue(value = "${hera.alertmanager.url}", autoRefreshed = true)
    private String silenceUrl;
    @NacosValue(value = "${hera.alert.type}", autoRefreshed = true)
    private String alertTYPE;

    public static final Gson gson = new Gson();

    public Result CreateRuleAlert(RuleAlertParam param) {
        log.info("RuleAlertService.CreateRuleAlert  param : {}", gson.toJson(param));
        try {
            if (param.getPromCluster() == null) {
                param.setPromCluster(Commons.DEFAULT_ALERT_PROM_CLUSTER);
            }
            RuleAlertEntity ruleAlertEntity = new RuleAlertEntity();
            ruleAlertEntity.setName(param.getAlert() + "-" + param.getCname());
            ruleAlertEntity.setCname(param.getCname());
            ruleAlertEntity.setExpr(param.getExpr());
            ruleAlertEntity.setLabels(transLabel2String(param.getLabels()));
            ruleAlertEntity.setAnnotation(transAnnotation2String(param.getAnnotations()));
            ruleAlertEntity.setAlertFor(param.getForTime());
            ruleAlertEntity.setEnv(Strings.join(",", param.getEnv()));
            ruleAlertEntity.setEnabled(param.getEnabled() == null ? 1 : param.getEnabled());
            ruleAlertEntity.setPriority(transPriority2Integer(param.getPriority()));
            ruleAlertEntity.setCreatedBy(String.join(",", param.getAlert_member()));
            ruleAlertEntity.setCreatedTime(new Date());
            ruleAlertEntity.setUpdatedTime(new Date());
            ruleAlertEntity.setDeletedBy("");
            ruleAlertEntity.setPromCluster(param.getPromCluster());
            ruleAlertEntity.setStatus(RuleAlertStatusEnum.PENDING.getDesc());
            ruleAlertEntity.setType("0");
            if (param.getAlert_member() != null) {
                ruleAlertEntity.setAlertMember(Strings.join(",", param.getAlert_member()));
            }
            ruleAlertEntity.setAlertAtPeople(Strings.join(",", param.getAlert_at_people()));
            ruleAlertEntity.setAlert_group(param.getGroup() == null ? "example" : param.getGroup());

            log.info("RuleAlertService.CreateRuleAlert ruleAlertEntity:{}", gson.toJson(ruleAlertEntity));
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
            if (param.getForTime() != null) {
                data.setAlertFor(param.getForTime());
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
            if (param.getAlert_at_people() != null) {
                data.setAlertAtPeople(Strings.join(",", param.getAlert_at_people()));
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

    //TODO:通过不同模板 创建不同飞书卡片
    //TODO:通过不同type，构造飞书、邮件等报警触达
    public Result SendAlert(String body) {
        JsonObject jsonObject = gson.fromJson(body, JsonObject.class);
        log.info("SendAlert jsonObject:{}", gson.toJson(jsonObject));
        AlertManagerFireResult fireResult = gson.fromJson(body, AlertManagerFireResult.class);
        //分type构建报警触达
        switch (alertTYPE) {
            case "feishu":
                feishuAlertContact.Reach(fireResult);
                break;
            case "mail":
                mailAlertContact.Reach(fireResult);
                break;
            default:
                feishuAlertContact.Reach(fireResult);
        }
        return Result.success("发送告警");
    }

    //TODO: 提供给alertManagerClient使用的临时方法，以后需要重构
    public List<RuleAlertEntity> GetAllRuleAlertList() {
        log.info("RuleAlertService.GetAllRuleAlertList");
        List<RuleAlertEntity> list = dao.GetAllRuleAlertList();
        return list;
    }

    //将labelmap转换成string
    private String transLabel2String(Map<String, String> labels) {
        String res = "";
        try {
            for (Map.Entry<String, String> entry : labels.entrySet()) {
                res += entry.getKey() + "=" + entry.getValue() + ",";
            }
            return res.substring(0, res.length() - 1);
        } catch (Exception e) {
            log.error("transLabel2String error:{}", e.getMessage());
            return "";
        }
    }

    //将annotationMap转换成json string
    private String transAnnotation2String(Map<String, String> annotations) {
        return gson.toJson(annotations);
//        String res = "";
//        for (Map.Entry<String, String> entry : annotations.entrySet()) {
//            res += entry.getKey() + "=" + entry.getValue() + ",";
//        }
//        return res.substring(0, res.length() - 1);
    }

    private int transPriority2Integer(String priority) {
        try {
            String[] ps = priority.split("P");
            if (ps.length == 2) {
                return Integer.parseInt(ps[1]);
            } else {
                //默认P2
                return 2;
            }
        } catch (Exception e) {
            log.error("transPriority2Integer error:{}", e.getMessage());
            return 2;
        }
    }


}
