package com.xiaomi.youpin.gwdash.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.youpin.gwdash.bo.FeiShuCard;
import com.xiaomi.youpin.gwdash.common.AuditingEnum;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.common.TemplateUtils;
import com.xiaomi.youpin.gwdash.config.DepartmentConfig;
import com.xiaomi.youpin.gwdash.config.DubboConfig;
import com.xiaomi.youpin.gwdash.dao.model.TAuditing;
import com.xiaomi.youpin.gwdash.dao.model.TCustomeConfig;
import com.xiaomi.youpin.gwdash.service.impl.TenantComponent;
import io.micrometer.core.instrument.util.StringEscapeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.dubbo.config.annotation.Service;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Slf4j
@Service(group="${owner.dubbo.group}", interfaceClass = AuditingServiceAPI.class, timeout = 2000)
public class AuditingService implements AuditingServiceAPI {

    @Autowired
    private FeiShuService feiShuService;

    @Autowired
    private CustomConfigService customConfigService;
//
    @Autowired
    private Dao dao;

    @Autowired
    private GroupInfoService groupService;

    @Autowired
    private AuditingService auditingService;

    @Autowired
    private TenantComponent tenementComponent;

    @NacosValue("${feishu.verify.token}")
    private String verifyTonken;

    @Value("${user.dev}")
    private String userDev;


    @Autowired
    private DubboConfig dubboConfig;

    public Result<Boolean> applyApiGroup(String name, String username, String tenant, String groups, String groupNames) {
        Gson gson = new Gson();
        auditingService.updateGroups(username, tenant, gson.fromJson(groups, new TypeToken<List<Long>>(){}.getType()));

        return Result.success(true);
    }

    public Result<Boolean> applyApiGroupToFeishu(String name, String username, String groups, String groupNames) {
        TAuditing tAuditing = new TAuditing();
        long now = System.currentTimeMillis();
        tAuditing.setOperator(username);
        groupNames = StringEscapeUtils.escapeJson(groupNames.replace("\"", ""));
        Map<String, String> attachment = Maps.newHashMap();
        attachment.put("username", username);
        attachment.put("name", name);
        attachment.put("groups", groups);
        attachment.put("groupNames", groupNames);
        attachment.put("userdev", DepartmentConfig.department(userDev));
        attachment.put("gwgroup", dubboConfig.getOwnerDubboGroup());
        attachment.put("tenant", tenementComponent.getTenement());
        tAuditing.setAttachment(attachment);
        tAuditing.setType(AuditingEnum.APIGROUP.getType());
        tAuditing.setCtime(now);
        tAuditing.setUtime(now);
        tAuditing = dao.insert(tAuditing);
        if (tAuditing.getId() == 0) {
            return Result.success(false);
        }
        attachment.put("id", "" + tAuditing.getId());
        TCustomeConfig tCustomeConfig = getConfig();
        if (null == tCustomeConfig) {
            return Result.success(false);
        }
        String content = TemplateUtils.processTemplate(tCustomeConfig.getContent(), attachment);
        log.debug("applyApiGroupToFeishu content:[{}]", content);
        feiShuService.sendAuditCard(content);
        return Result.success(true);
    }

    private TCustomeConfig getConfig(){
        TCustomeConfig tCustomeConfig = customConfigService.get("apply_api_group");
        return tCustomeConfig;
    }

    @Override
    public Map<String, Object> groupApply(String feishuCardJson){
        log.info("groupApply group:[{}], feishuCardJson:[{}]",dubboConfig.getDubboGroup(), feishuCardJson);
        FeiShuCard feiShuCard =  JSONObject.parseObject(feishuCardJson, FeiShuCard.class);
        String type = feiShuCard.getType();
        if ("url_verification".equals(type)) {
            Map<String, Object> map = Maps.newHashMap();
            map.put("challenge", feiShuCard.getChallenge());
            return map;
        }
        return handler(feiShuCard);
    }

    public Map<String, Object> handler(FeiShuCard param) {
        Map<String, Object> action = param.getAction();
        Map<String, String> params = (Map<String, String>) action.get("value");
        String type = params.get("type");

        if (AuditingEnum.APIGROUP.getType().equals(type)) {
            return handleApiGroupApply(param);
        }

        return Maps.newHashMap();
    }

    private Map<String, Object> handleApiGroupApply(FeiShuCard param) {
        Map<String, Object> action = param.getAction();
        Map<String, String> params = (Map<String, String>) action.get("value");
        long id = new Long(params.get("id"));
        TAuditing tAuditing = dao.fetch(TAuditing.class, id);
        if (null == tAuditing) {
            return Maps.newHashMap();
        }
        String rAction = params.get("key");
        Map<String, String > info = tAuditing.getAttachment();
        String tenant = info.get("tenant");
        if (!("agree".equals(rAction))) {
            feiShuService.sendMsg2Person(tAuditing.getOperator(), "api group角色申请不通过");
            TCustomeConfig tCustomeConfig = customConfigService.get("apply_api_group_error");
            if (null == tCustomeConfig) {
                return Maps.newHashMap();
            }
            String err = TemplateUtils.processTemplate(tCustomeConfig.getContent(), info);
            Map<String, Object> s = new Gson().fromJson(err, Map.class);
            return s;
        }
        List<Long> gids = new Gson().fromJson(info.get("groups"), new TypeToken<List<Long>>(){}.getType());
        if(updateGroups(info.get("username"), tenant, gids)) {
            feiShuService.sendMsg2Person(tAuditing.getOperator(),"api group申请通过");
            TCustomeConfig tCustomeConfig = customConfigService.get("apply_api_group_success");
            if (null == tCustomeConfig) {
                return Maps.newHashMap();
            }
            String suc = TemplateUtils.processTemplate(tCustomeConfig.getContent(), info);
            Map<String, Object> s = new Gson().fromJson(suc, Map.class);
            return s;
        };
        return Maps.newHashMap();
    }

    public boolean updateGroups(String userName, String tenant, List<Long> gids){
        return groupService.updateGroup(userName, tenant, gids).getData();
    }

    /**
     * 飞书消息卡片安全校验
     */
    public boolean getCardVerifyCode(HttpServletRequest request,String requestBody){

            String signature = request.getHeader("X-Lark-Signature");
            String s1 =  (request.getHeader("X-Lark-Request-Timestamp") + request.getHeader("X-Lark-Request-Nonce") + verifyTonken);
            String s2 = s1 + requestBody;
            String s =  DigestUtils.sha1Hex(s2);
            if (s.equals(signature)){
                return true;
            }
        return false;
    }
}
