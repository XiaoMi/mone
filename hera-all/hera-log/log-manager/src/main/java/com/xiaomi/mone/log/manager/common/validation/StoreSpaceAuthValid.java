package com.xiaomi.mone.log.manager.common.validation;

import com.google.common.collect.Lists;
import com.xiaomi.mone.log.manager.dao.MilogLogstoreDao;
import com.xiaomi.mone.log.manager.dao.MilogSpaceDao;
import com.xiaomi.mone.log.manager.model.bo.StoreSpaceAuth;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogStoreDO;
import com.xiaomi.mone.log.manager.model.pojo.MilogSpaceDO;
import com.xiaomi.youpin.docean.anno.Component;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.common.Constant.SYMBOL_COMMA;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/7/14 16:32
 */
@Slf4j
@Component
public class StoreSpaceAuthValid {

    @Resource
    private MilogLogstoreDao milogLogstoreDao;

    @Resource
    private MilogSpaceDao milogSpaceDao;

    public String validParam(StoreSpaceAuth storeSpaceAuth) {
        List<String> errorInfos = Lists.newArrayList();
        if (null == storeSpaceAuth) {
            errorInfos.add("参数不能为空");
        }
        if (null == storeSpaceAuth.getStoreId()) {
            errorInfos.add("storeId不能为空");
        }
        if (null == storeSpaceAuth.getSpaceId()) {
            errorInfos.add("spaceId不能为空");
        }
        return errorInfos.stream().collect(Collectors.joining(SYMBOL_COMMA));
    }

    public String validStoreAuthData(StoreSpaceAuth storeSpaceAuth) {
        List<String> errorInfos = Lists.newArrayList();
        MilogLogStoreDO milogLogStoreDO = milogLogstoreDao.queryById(storeSpaceAuth.getStoreId());
        if (null == milogLogStoreDO) {
            errorInfos.add("store信息不存在，请检查是否正确");
        }
        MilogSpaceDO milogSpaceDO = milogSpaceDao.queryById(storeSpaceAuth.getSpaceId());
        if (null == milogSpaceDO) {
            errorInfos.add("space信息不存在，请检查是否正确");
        }
        if (storeSpaceAuth.getSpaceId().equals(milogLogStoreDO.getSpaceId())) {
            errorInfos.add("store已经归属了该space了，不能重复授权");
        }
        return errorInfos.stream().collect(Collectors.joining(SYMBOL_COMMA));
    }
}
