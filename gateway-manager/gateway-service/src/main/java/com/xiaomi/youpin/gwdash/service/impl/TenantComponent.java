package com.xiaomi.youpin.gwdash.service.impl;

import com.google.common.collect.ImmutableMap;
import com.xiaomi.mone.tpc.api.service.MetaDataFacade;
import com.xiaomi.mone.tpc.api.service.NodeFacade;
import com.xiaomi.mone.tpc.common.enums.NodeTypeEnum;
import com.xiaomi.mone.tpc.common.enums.OutIdTypeEnum;
import com.xiaomi.mone.tpc.common.enums.UserTypeEnum;
import com.xiaomi.mone.tpc.common.param.FlagAddParam;
import com.xiaomi.mone.tpc.common.param.FlagQryParam;
import com.xiaomi.mone.tpc.common.param.NodeQryParam;
import com.xiaomi.mone.tpc.common.vo.FlagVo;
import com.xiaomi.mone.tpc.common.vo.NodeVo;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.youpin.gwdash.bo.SessionAccount;
import com.xiaomi.youpin.gwdash.bo.TenementParam;
import com.xiaomi.youpin.gwdash.context.TenementContext;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2022/9/29 21:53
 */
@Slf4j
@Component
public class TenantComponent {

    @Value("${parent.out.id}")
    private long parentOutId;

    //@Reference(check = false,interfaceClass = NodeFacade.class, group = "staging", version = "1.0")
    private NodeFacade nodeFacade;

    //@Reference(check = false,group = "staging", interfaceClass = MetaDataFacade.class, version = "1.0")
    private MetaDataFacade metaDataFacade;


    /**
     * list.add(ImmutableMap.of("value", "1", "label", "中国区"));
     * list.add(ImmutableMap.of("value", "2", "label", "有品"));
     *
     * @param account
     * @return
     */
    public List<Map<String, String>> getTenementList(String account) {
        List<Map<String, String>> list = new ArrayList<>();
        list.add(ImmutableMap.of("value", "1", "label", "中国区"));
        list.add(ImmutableMap.of("value", "2", "label", "有品"));
        return list;
//        PageDataVo<NodeVo> data = getTenantInfo(account);
//        log.info("{}", data);
//        return data.getList().stream().map(it -> ImmutableMap.of("value", String.valueOf(it.getOutId()), "label", it.getNodeName())).collect(Collectors.toList());
    }


    /**
     * 使用system用户就能获取系统级别的信息
     *
     * @param account
     * @return
     */
    public PageDataVo<NodeVo> getTenantInfo(String account) {
        NodeQryParam param = new NodeQryParam();
        param.setAccount(account);
        param.setUserType(UserTypeEnum.CAS_TYPE.ordinal());
        param.setParentOutId(parentOutId);
        param.setType(NodeTypeEnum.PRO_SUB_GROUP.getCode());
        param.setParentOutIdType(OutIdTypeEnum.PROJECT.getCode());
        param.setStatus(0);
        Result<PageDataVo<NodeVo>> res = nodeFacade.list(param);
        return res.getData();
    }


    public String getTenement() {
        if (1==1){
            return "1";
        }
        SessionAccount account = TenementContext.getContext().get();
        if (null != account) {
            String tenant = account.getTenant();
            if (StringUtils.isNotEmpty(tenant)) {
                return tenant;
            }
        }
        throw new RuntimeException();
    }


    public void setTenantToMetadata(SessionAccount account, TenementParam param) {
        FlagAddParam addParam = new FlagAddParam();
        addParam.setParentId(parentOutId);
        addParam.setFlagName(account.getUsername());
        addParam.setDesc("tenant setting");
        addParam.setFlagKey("tenant");
        addParam.setFlagVal(param.getTenement());
        addParam.setAccount(account.getUsername());
        addParam.setUserType(UserTypeEnum.CAS_TYPE.getCode());
        //metaDataFacade.add(addParam);
    }

    public String getTenantFromMetadata(String userName) {
        if (1==1){
            return "1";
        }
        FlagQryParam qryParam = new FlagQryParam();
        qryParam.setParentId(parentOutId);
        qryParam.setFlagName(userName);
        qryParam.setAccount(userName);
        qryParam.setUserType(UserTypeEnum.CAS_TYPE.getCode());
        qryParam.setPager(false);
        qryParam.setPage(0);
        qryParam.setPageSize(1);
        qryParam.setFlagKey("tenant");
        PageDataVo<FlagVo> res = metaDataFacade.list(qryParam).getData();
        if (null == res.getList()) {
            return "";
        }
        return res.getList().get(0).getFlagVal();
    }

}
