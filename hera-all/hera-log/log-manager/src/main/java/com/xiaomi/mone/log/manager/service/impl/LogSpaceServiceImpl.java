/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.manager.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.google.common.collect.Lists;
import com.xiaomi.mone.log.api.enums.LogStructureEnum;
import com.xiaomi.mone.log.api.enums.MachineRegionEnum;
import com.xiaomi.mone.log.api.enums.OperateEnum;
import com.xiaomi.mone.log.api.enums.ProjectSourceEnum;
import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.exception.CommonError;
import com.xiaomi.mone.log.manager.common.context.MoneUserContext;
import com.xiaomi.mone.log.manager.dao.MilogLogstoreDao;
import com.xiaomi.mone.log.manager.dao.MilogSpaceDao;
import com.xiaomi.mone.log.manager.domain.Tpc;
import com.xiaomi.mone.log.manager.model.MilogSpaceParam;
import com.xiaomi.mone.log.manager.model.convert.MilogSpaceConvert;
import com.xiaomi.mone.log.manager.model.dto.MapDTO;
import com.xiaomi.mone.log.manager.model.dto.MilogSpaceDTO;
import com.xiaomi.mone.log.manager.model.page.PageInfo;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogStoreDO;
import com.xiaomi.mone.log.manager.model.pojo.MilogSpaceDO;
import com.xiaomi.mone.log.manager.service.BaseService;
import com.xiaomi.mone.log.manager.service.LogSpaceService;
import com.xiaomi.mone.tpc.common.enums.NodeUserRelTypeEnum;
import com.xiaomi.mone.tpc.common.enums.UserTypeEnum;
import com.xiaomi.mone.tpc.common.vo.NodeVo;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.common.StringUtils;
import com.xiaomi.youpin.docean.plugin.db.Transactional;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LogSpaceServiceImpl extends BaseService implements LogSpaceService {

    @Resource
    private MilogSpaceDao milogSpaceDao;

    @Resource
    private MilogLogstoreDao milogLogstoreDao;

    @Resource
    private TpcSpaceAuthService spaceAuthService;

    @Resource
    private LogTailServiceImpl logTailService;

    @Resource
    private Tpc tpc;

    /**
     * 新建
     *
     * @param param
     * @return
     */
    @Override
    public Result<String> newMilogSpace(MilogSpaceParam param) {
        if (Objects.isNull(param) || StringUtils.isBlank(param.getSpaceName())) {
            return Result.failParam("参数错误");
        }

        String spaceName = param.getSpaceName();
        if (milogSpaceDao.verifyExistByName(spaceName)) {
            return Result.failParam("存在同名spaceName");
        }

        MilogSpaceDO milogSpaceDO = wrapMilogSpaceDO(param);
        wrapBaseCommon(milogSpaceDO, OperateEnum.ADD_OPERATE);

        MilogSpaceDO dbDO = milogSpaceDao.newMilogSpace(milogSpaceDO);
        if (Objects.isNull(dbDO.getId())) {
            return Result.failParam("space未保存成功，请重试");
        }
        String creator = MoneUserContext.getCurrentUser().getUser();
        List<String> otherAdmins = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(param.getAdmins())) {
            creator = param.getAdmins().get(0);
            if (param.getAdmins().size() > 1) {
                otherAdmins = CollectionUtil.sub(param.getAdmins(), 1, param.getAdmins().size());
            }
        }
        com.xiaomi.youpin.infra.rpc.Result tpcResult = spaceAuthService.saveSpacePerm(dbDO, creator);
        addMemberAsync(dbDO.getId(), otherAdmins);

        if (tpcResult == null || tpcResult.getCode() != 0) {
            milogSpaceDao.deleteMilogSpace(dbDO.getId());
            log.error("新建space未关联权限系统,space:[{}], tpcResult:[{}]", dbDO, tpcResult);
            return Result.failParam("space未关联权限系统");
        }

        return Result.success();
    }

    private void addMemberAsync(Long spaceId, List<String> otherAdmins) {
        if (CollectionUtil.isNotEmpty(otherAdmins)) {
            List<CompletableFuture<Void>> adminAsyncResult = otherAdmins.stream()
                    .map(admin -> CompletableFuture.runAsync(() ->
                            spaceAuthService.addSpaceMember(spaceId, admin, UserTypeEnum.CAS_TYPE.getCode(), NodeUserRelTypeEnum.MANAGER.getCode())))
                    .collect(Collectors.toList());
            CompletableFuture.allOf(adminAsyncResult.toArray(new CompletableFuture[0])).join();
        }
    }

    private MilogSpaceDO wrapMilogSpaceDO(MilogSpaceParam param) {
        MilogSpaceDO milogSpaceDO = new MilogSpaceDO();
        milogSpaceDO.setSpaceName(param.getSpaceName());
        milogSpaceDO.setDescription(param.getDescription());
        return milogSpaceDO;
    }

    /**
     * getById
     *
     * @param id
     * @return
     */
    @Override
    public Result<MilogSpaceDTO> getMilogSpaceById(Long id) {
        if (null == id) {
            return new Result<>(CommonError.ParamsError.getCode(), "id不能为空");
        }
        MilogSpaceDO milogSpace = milogSpaceDao.getMilogSpaceById(id);
        if (null != milogSpace) {
            MilogSpaceDTO milogSpaceDTO = new MilogSpaceDTO();
            milogSpaceDTO.setCreator(milogSpace.getCreator());
            milogSpaceDTO.setCtime(milogSpace.getCtime());
            milogSpaceDTO.setDescription(milogSpace.getDescription());
            milogSpaceDTO.setId(milogSpace.getId());
            milogSpaceDTO.setTenantId(milogSpace.getTenantId());
            milogSpaceDTO.setTenantName("todo");
            milogSpaceDTO.setUtime(milogSpace.getUtime());
            milogSpaceDTO.setSpaceName(milogSpace.getSpaceName());
            return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage(), milogSpaceDTO);
        }
        return new Result<>(CommonError.NOT_EXISTS_DATA.getCode(), CommonError.NOT_EXISTS_DATA.getMessage());
    }

    /**
     * 分页查询
     *
     * @param spaceName
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public Result<PageInfo<MilogSpaceDTO>> getMilogSpaceByPage(String spaceName, Long tenantId, Integer page, Integer pagesize) {
        com.xiaomi.youpin.infra.rpc.Result<PageDataVo<NodeVo>> userPermSpacePage = spaceAuthService.getUserPermSpace(spaceName, page, pagesize);
        PageInfo<MilogSpaceDTO> spaceDTOPageInfo = MilogSpaceConvert.INSTANCE.fromTpcPage(userPermSpacePage.getData());
        return Result.success(spaceDTOPageInfo);
    }

    @Override
    public Result<List<MapDTO<String, Long>>> getMilogSpaces(Long tenantId) {
        int pageNum = 1;
        List<MapDTO<String, Long>> ret = new ArrayList<>();
        List<NodeVo> nodeVos = new ArrayList<>();

        while (true) {
            com.xiaomi.youpin.infra.rpc.Result<PageDataVo<NodeVo>> tpcRes = spaceAuthService.getUserPermSpace("", pageNum, Integer.MAX_VALUE);

            if (tpcRes.getCode() != 0) {
                return Result.fail(CommonError.UNAUTHORIZED);
            }

            List<NodeVo> list = tpcRes.getData() != null ? tpcRes.getData().getList() : null;

            if (CollectionUtils.isEmpty(list)) {
                break;
            }

            nodeVos.addAll(list);
            pageNum++;
        }

        for (NodeVo s : nodeVos) {
            ret.add(new MapDTO<>(s.getNodeName(), s.getOutId()));
        }

        return Result.success(ret);

    }

    /**
     * 更新
     *
     * @param param
     * @return
     */
    @Transactional
    @Override
    public Result<String> updateMilogSpace(MilogSpaceParam param) {
        if (null == param || StringUtils.isBlank(param.getSpaceName())) {
            return new Result<>(CommonError.ParamsError.getCode(), "参数错误", "");
        }
        if (!tpc.hasPerm(MoneUserContext.getCurrentUser(), param.getId())) {
            return Result.fail(CommonError.UNAUTHORIZED);
        }
        if (milogSpaceDao.verifyExistByName(param.getSpaceName(), param.getId())) {
            return new Result<>(CommonError.UnknownError.getCode(), "存在同名spaceName", "");
        }
        MilogSpaceDO milogSpace = milogSpaceDao.queryById(param.getId());
        if (null == milogSpace) {
            return new Result<>(CommonError.ParamsError.getCode(), "logSpace 不存在", "");
        }
        wrapMilogSpace(milogSpace, param);
        wrapBaseCommon(milogSpace, OperateEnum.UPDATE_OPERATE);
        if (milogSpaceDao.update(milogSpace)) {
            com.xiaomi.youpin.infra.rpc.Result tpcResult = spaceAuthService.updateSpaceTpc(param, MoneUserContext.getCurrentUser().getUser());
            if (tpcResult == null || tpcResult.getCode() != 0) {
                log.error("修改space未关联权限系统,space:[{}], tpcResult:[{}]", milogSpace, tpcResult);
                return Result.success("修改space未关联权限系统，请联系服务端效能组");
            }
            return Result.success();
        } else {
            log.warn("[MilogSpaceService.updateMilogSpace] update MilogSpace err,spaceName:{},spaceId:{}", param.getSpaceName(), param.getId());
            return new Result<>(CommonError.UnknownError.getCode(), CommonError.UnknownError.getMessage(), "");
        }
    }

    @Transactional
    @Override
    public Result<String> deleteMilogSpace(Long id) {
        if (null == id) {
            return new Result<>(CommonError.ParamsError.getCode(), "id不能为空", "");
        }
        if (!tpc.hasPerm(MoneUserContext.getCurrentUser(), id)) {
            return Result.fail(CommonError.UNAUTHORIZED);
        }
        MilogSpaceDO milogSpace = milogSpaceDao.getMilogSpaceById(id);
        if (null == milogSpace) {
            return new Result<>(CommonError.ParamsError.getCode(), "logSpace 不存在", "");
        }
        List<MilogLogStoreDO> stores = milogLogstoreDao.getMilogLogstoreBySpaceId(id);
        if (stores != null && stores.size() != 0) {
            return new Result<>(CommonError.ParamsError.getCode(), "该space 下存在store，无法删除", "");
        }
        if (milogSpaceDao.deleteMilogSpace(id)) {
            logTailService.deleteConfigRemote(id, id, MachineRegionEnum.CN_MACHINE.getEn(), LogStructureEnum.SPACE);

            com.xiaomi.youpin.infra.rpc.Result tpcResult = spaceAuthService.deleteSpaceTpc(id, MoneUserContext.getCurrentUser().getUser(), MoneUserContext.getCurrentUser().getUserType());
            if (tpcResult == null || tpcResult.getCode() != 0) {
                log.error("删除space未关联权限系统,space:[{}], tpcResult:[{}]", milogSpace, tpcResult);
                return Result.failParam("删除space未关联权限系统，请联系服务端效能组");
            }
            return Result.success();
        } else {
            log.warn("[MilogSpaceService.deleteMilogSpace] delete MilogSpace err,spaceId:{}", id);
            return Result.failParam("space删除失败，请重试");
        }
    }

    @Override
    public Result<String> setSpacePermission(Long spaceId, String permDeptIds) {
        if (spaceId == null || StringUtils.isEmpty(permDeptIds)) {
            return Result.fail(CommonError.ParamsError);
        }
        MilogSpaceDO space = milogSpaceDao.getMilogSpaceById(spaceId);
        if (!permDeptIds.contains(space.getCreateDeptId())) {
            return Result.fail(CommonError.ParamsError.getCode(), "创建部门权限不可被取消");
        }
        space.setPermDeptId(permDeptIds);
        boolean update = milogSpaceDao.update(space);
        return update ? Result.success() : Result.fail(CommonError.UnknownError);
    }

    @Override
    public MilogSpaceDO buildMiLogSpace(MilogSpaceParam cmd, String appCreator) {
        MilogSpaceDO ms = new MilogSpaceDO();
        wrapMilogSpace(ms, cmd, ProjectSourceEnum.ONE_SOURCE.getSource());
        wrapBaseCommon(ms, OperateEnum.ADD_OPERATE, appCreator);
        return ms;
    }
}
