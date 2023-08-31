package com.xiaomi.mone.tpc.api.service;

import com.xiaomi.mone.tpc.common.param.*;
import com.xiaomi.mone.tpc.common.vo.FlagVo;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.youpin.infra.rpc.Result;

public interface MetaDataFacade {

    Result<FlagVo> add(FlagAddParam param);
    Result<FlagVo> edit(FlagEditParam param);
    Result delete(FlagDeleteParam param);
    Result<PageDataVo<FlagVo>> list(FlagQryParam param);

    /**
     * 不存在就插入，存在就更新
     * parent_id and flag_name and flag_key 作为唯一的
     * @param param
     * @return
     */
    Result<FlagVo> save(FlagAddParam param);

    /**
     * 查询
     * parent_id and flag_name and flag_key 查询条件
     * @param param
     * @return
     */
    Result<FlagVo> getOne(FlagQryOneParam param);

}