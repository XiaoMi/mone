package com.xiaomi.mone.tpc.org;

import com.xiaomi.mone.tpc.common.param.NodeOrgQryParam;
import com.xiaomi.mone.tpc.common.vo.OrgInfoVo;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.mone.tpc.common.vo.ResponseCode;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @project: mi-tpc
 * @author: zgf
 * @date: 2022/3/5 20:18
 */
@Slf4j
@Component("orgHelper")
@ConditionalOnExpression("'${org.type}'.equals('default')")
public class DefaultOrgHelper extends OrgHelper {

    /**
     * 部门信息查询
     * @param param
     * @return
     */
    @Override
    public ResultVo<PageDataVo<OrgInfoVo>> list(NodeOrgQryParam param) {
        PageDataVo<OrgInfoVo> pageData = param.buildPageDataVo();
        OrgInfoVo topOrg = new OrgInfoVo();
        topOrg.setIdPath("group1/group2");
        topOrg.setNamePath("XX公司/一级部门");
        List<OrgInfoVo> allOrg = new ArrayList<>();
        allOrg.add(topOrg);
        pageData.setTotal(allOrg.size());
        pageData.setList(allOrg);
        return ResponseCode.SUCCESS.build(pageData);
    }

    /**
     * 部门信息查询
     * @param account
     * @return
     */
    @Override
    public OrgInfoVo get(String account) {
        OrgInfoVo info = new OrgInfoVo();
        info.setIdPath("group1/group2");
        info.setNamePath("XX公司/一级部门");
        return info;
    }

}
