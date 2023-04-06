package com.xiaomi.mone.log.manager.service;

import com.google.common.collect.Lists;
import com.xiaomi.mone.app.api.response.AppBaseInfo;
import com.xiaomi.mone.log.common.Config;
import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.exception.CommonError;
import com.xiaomi.mone.log.manager.dao.MilogLogstoreDao;
import com.xiaomi.mone.log.manager.dao.MilogLogTailDao;
import com.xiaomi.mone.log.manager.dao.MilogStoreSpaceAuthDao;
import com.xiaomi.mone.log.manager.model.dto.MapDTO;
import com.xiaomi.mone.log.manager.model.dto.MilogSpaceTreeDTO;
import com.xiaomi.mone.log.manager.model.dto.UnAccessAppDTO;
import com.xiaomi.mone.log.manager.model.dto.ValueDTO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogStoreDO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogTailDo;
import com.xiaomi.mone.log.manager.model.pojo.MilogStoreSpaceAuth;
import com.xiaomi.mone.log.manager.service.impl.HeraAppServiceImpl;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HeralogHomePageService {
    @Resource
    private MilogLogTailDao milogLogtailDao;

    @Resource
    private HeraAppServiceImpl heraAppService;

    @Resource
    private MilogLogstoreDao milogLogstoreDao;

    @Resource
    private MilogStoreSpaceAuthDao milogStoreSpaceAuthDao;

    private List<ValueDTO<String>> milogpattern;

    {
        String pattern = Config.ins().get("milogpattern", "");
        String[] split = pattern.split(",");
        ArrayList<ValueDTO<String>> valueDTOS = new ArrayList<>();
        for (String s : split) {
            valueDTOS.add(new ValueDTO<>(s));
        }
        milogpattern = valueDTOS;
    }

    public Result<Map<String, Object>> milogAccess() {
        Long total = heraAppService.getAppCount();
        int access = milogLogtailDao.appCount();
        HashMap<String, Object> map = new HashMap<>();
        map.put("total", total);
        map.put("access", access);
        return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage(), map);
    }

    public Result<List<UnAccessAppDTO>> unAccessAppList() {
        List<AppBaseInfo> appBaseInfos = heraAppService.queryAllExistsApp();
        Map<Integer, String> appMap = appBaseInfos.stream().collect(Collectors.toMap(AppBaseInfo::getId, AppBaseInfo::getAppName));
        List<Integer> hasAccessAppId = milogLogtailDao.queryAllAppId();
        ArrayList<UnAccessAppDTO> list = new ArrayList<>();
        for (Map.Entry<Integer, String> app : appMap.entrySet()) {
            if (!hasAccessAppId.contains(app.getKey())) {
                list.add(new UnAccessAppDTO(app.getKey().longValue(), app.getValue()));
            }
        }
        return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage(), list);
    }

    public Result<List<MilogSpaceTreeDTO>> getMilogSpaceTree(Long spaceId) {
        List<MilogLogStoreDO> stores = getMilogLogStoreDOS(spaceId);
        List<MilogSpaceTreeDTO> spaceTreeDTOS = stores.stream().map(milogLogstoreDO -> {
            Long logstoreDOId = milogLogstoreDO.getId();
            MilogSpaceTreeDTO milogSpaceTreeDTO = new MilogSpaceTreeDTO();
            milogSpaceTreeDTO.setLabel(milogLogstoreDO.getLogstoreName());
            milogSpaceTreeDTO.setValue(logstoreDOId);
            List<MilogLogTailDo> logTailDos = milogLogtailDao.getMilogLogtailByStoreId(logstoreDOId);
            if (CollectionUtils.isNotEmpty(logTailDos)) {
                List<MapDTO<String, Long>> collect = logTailDos.stream()
                        .map(logTailDo -> {
                            MapDTO<String, Long> mapDTO = new MapDTO();
                            mapDTO.setValue(logTailDo.getId());
                            mapDTO.setLabel(logTailDo.getTail());
                            return mapDTO;
                        }).collect(Collectors.toList());
                milogSpaceTreeDTO.setChildren(collect);
            }
            return milogSpaceTreeDTO;
        }).collect(Collectors.toList());
        return Result.success(spaceTreeDTOS);
    }

    /**
     * 查询原来归属于space的store，查询授权的store
     *
     * @param spaceId
     * @return
     */
    @Nullable
    private List<MilogLogStoreDO> getMilogLogStoreDOS(Long spaceId) {
        List<MilogLogStoreDO> storeDOS = Lists.newArrayList();
        List<MilogLogStoreDO> stores = milogLogstoreDao.getMilogLogstoreBySpaceId(spaceId);
        List<MilogStoreSpaceAuth> storeSpaceAuths = milogStoreSpaceAuthDao.queryStoreIdsBySpaceId(spaceId);
        if (CollectionUtils.isNotEmpty(stores)) {
            storeDOS = stores;
        }
        if (CollectionUtils.isNotEmpty(storeSpaceAuths)) {
            List<MilogLogStoreDO> collect = storeSpaceAuths.stream()
                    .map(storeSpaceAuth -> milogLogstoreDao.queryById(storeSpaceAuth.getStoreId()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            storeDOS.addAll(collect);
        }
        return storeDOS;
    }

    public Result<List<ValueDTO<String>>> getMiloglogAccessPattern() {
        return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage(), milogpattern);
    }
}
