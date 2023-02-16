package com.xiaomi.mone.monitor.service.impl;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.xiaomi.mone.monitor.service.MiFeiShuService;
import com.xiaomi.youpin.feishu.FeiShu;
import com.xiaomi.youpin.feishu.bo.ContentBo;
import com.xiaomi.youpin.feishu.bo.MsgBatchSendRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service(registry = "registryConfig",interfaceClass = MiFeiShuService.class, group = "${dubbo.group}")
public class MiFeiShuServiceImpl implements MiFeiShuService {

    private FeiShu feiShu;

    @Value("${feishu.monitor.appId}")
    private String appId;
    @NacosValue("${feishu.monitor.appSecret:noconfig}")
    private String appSecret;

    @PostConstruct
    private void init() {
        feiShu = new FeiShu(appId, appSecret);
    }

    //批量查询用户手机号
    public List<String> getBatchPhoneByUserNames(List<String> userNames){
        if (CollectionUtils.isEmpty(userNames)) {
            log.error("username is null");
            return Arrays.asList();
        }
        try {
            log.info("getBatchPhoneByUserNames userNames:{}", userNames);
            List<String> openIds = new ArrayList<>();
            userNames.stream().forEach(it ->{
                openIds.add(feiShu.getOpenIdIdByEmail(it + "@xiaomi.com"));
            });
            if (CollectionUtils.isEmpty(openIds)) {
                log.error("根据username:{}查询openId is null", userNames);
                return Arrays.asList();
            }
            return feiShu.getBatchPhone(openIds);

        } catch (Exception e){
            log.error("FeiShuServiceImpl.getBatchPhoneByUserNames username:{} e:{}", userNames, e.getMessage());
            return Arrays.asList();
        }

    }

    public boolean batchSendMsg(List<String> userNames, String msg) {
        boolean flag = false;
        MsgBatchSendRequest request = new MsgBatchSendRequest();
        ContentBo content = new ContentBo();
        content.setText(msg);
        request.setContent(content);
        List<String> users = new ArrayList<>();
        userNames.stream().distinct().forEach(userName -> {
            String openIdId = feiShu.getOpenIdIdByEmail(userName + "@xiaomi.com");
            if (StringUtils.isNotBlank(openIdId)){
                users.add(openIdId);
            } else {
                log.info("MiFeiShuServiceImpl.batchSendMsg 根据userName:{}未查询到openIdId", userName);
            }
        });
        if (users.size()>0){
            request.setOpen_ids(users);
            flag = feiShu.batchSendMsg(request);
        }
        log.info("MiFeiShuServiceImpl.batchSendMsg userNames:{}, msg:{} users.size:{} rst:{}", userNames, msg, users.size(), flag);
        return flag;
    }

}
