package com.xiaomi.mone.log.manager.domain;

import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.convert.MilogSpaceConvert;
import com.xiaomi.mone.log.manager.model.dto.MilogSpaceDTO;
import com.xiaomi.mone.log.manager.model.page.PageInfo;
import com.xiaomi.mone.tpc.common.vo.NodeVo;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.youpin.docean.anno.Service;

import javax.annotation.Resource;

@Service
public class Space {
    @Resource
    Tpc tpc;

    public PageInfo<MilogSpaceDTO> getMilogSpaceByPage(String spaceName, Integer page, Integer pagesize) {
        com.xiaomi.youpin.infra.rpc.Result<PageDataVo<NodeVo>> tpcRes = tpc.getUserPermSpace(spaceName, page, pagesize);
        return MilogSpaceConvert.INSTANCE.fromTpcPage(tpcRes.getData());
    }
}