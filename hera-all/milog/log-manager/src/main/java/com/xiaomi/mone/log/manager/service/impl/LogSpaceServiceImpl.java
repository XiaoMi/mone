package com.xiaomi.mone.log.manager.service.impl;

import com.xiaomi.mone.log.api.enums.OperateEnum;
import com.xiaomi.mone.log.api.enums.ProjectSourceEnum;
import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.exception.CommonError;
import com.xiaomi.mone.log.manager.common.context.MoneUserContext;
import com.xiaomi.mone.log.manager.model.convert.SpaceConvert;
import com.xiaomi.mone.log.manager.model.convert.SpacePermTreeConvert;
import com.xiaomi.mone.log.manager.dao.LogstoreDao;
import com.xiaomi.mone.log.manager.dao.SpaceDao;
import com.xiaomi.mone.log.manager.domain.IDMDept;
import com.xiaomi.mone.log.manager.domain.Tpc;
import com.xiaomi.mone.log.manager.model.bo.CreateOrUpdateSpaceCmd;
import com.xiaomi.mone.log.manager.model.cache.IDMDeptCache;
import com.xiaomi.mone.log.manager.model.dto.MapDTO;
import com.xiaomi.mone.log.manager.model.dto.MilogSpaceDTO;
import com.xiaomi.mone.log.manager.model.dto.SpacePermTreeDTO;
import com.xiaomi.mone.log.manager.model.page.PageInfo;
import com.xiaomi.mone.log.manager.model.pojo.LogSpaceDO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogStoreDO;
import com.xiaomi.mone.log.manager.service.BaseService;
import com.xiaomi.mone.log.manager.service.LogSpaceService;
import com.xiaomi.mone.log.manager.user.IdmMoneUserDetailService;
import com.xiaomi.mone.log.manager.user.UseDetailInfo;
import com.xiaomi.mone.tpc.common.vo.NodeVo;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.common.StringUtils;
import com.xiaomi.youpin.docean.plugin.db.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LogSpaceServiceImpl extends BaseService implements LogSpaceService {

    @Resource
    private SpaceDao spaceDao;

    @Resource
    private LogstoreDao milogLogstoreDao;

    @Resource
    private IdmMoneUserDetailService userService;

    @Resource
    Tpc tpc;

    @Resource
    IDMDept idmDept;

    /**
     * 新建
     *
     * @param cmd
     * @return
     */
    @Override
    public Result<String> newMilogSpace(CreateOrUpdateSpaceCmd cmd) {
        if (null == cmd || StringUtils.isBlank(cmd.getSpaceName())) {
            return new Result<>(CommonError.ParamsError.getCode(), "参数错误");
        }
        if (spaceDao.verifyExistByName(cmd.getSpaceName())) {
            return new Result<>(CommonError.UnknownError.getCode(), "存在同名spaceName", "");
        }
        LogSpaceDO spaceDO = SpaceConvert.INSTANCE.toDO(cmd);
        wrapBaseCommon(spaceDO, OperateEnum.ADD_OPERATE);
        LogSpaceDO dbDO = spaceDao.newMilogSpace(spaceDO);
        if (dbDO.getId() == null) {
            return Result.failParam("space未保存成功，请重试");
        }
        com.xiaomi.youpin.infra.rpc.Result tpcResult = tpc.saveSpacePerm(dbDO, MoneUserContext.getCurrentUser());
        if (tpcResult == null || tpcResult.getCode() != 0) {
            spaceDao.deleteMilogSpace(dbDO.getId());
            log.error("新建space未关联权限系统,space:[{}], tpcResult:[{}]", dbDO, tpcResult);
            return Result.failParam("space未关联权限系统");
        }
        return Result.success();
    }

    /**
     * getById
     *
     * @param id
     * @return
     */
    public Result<MilogSpaceDTO> getMilogSpaceById(Long id) {

        if (null == id) {
            return new Result<>(CommonError.ParamsError.getCode(), "id不能为空");
        }
        LogSpaceDO milogSpace = spaceDao.getMilogSpaceById(id);
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
    public Result<PageInfo<MilogSpaceDTO>> getMilogSpaceByPage(String spaceName, Integer page, Integer pagesize) {
        com.xiaomi.youpin.infra.rpc.Result<PageDataVo<NodeVo>> tpcRes = tpc.getUserPermSpace(spaceName, page, pagesize);
        if (tpcRes.getCode() != 0) {
            return Result.fail(CommonError.UNAUTHORIZED);
        }
        return Result.success(SpaceConvert.INSTANCE.fromTpcPage(tpcRes.getData()));
    }

    public Result<List<MapDTO<String, Long>>> getMilogSpaces() {
        com.xiaomi.youpin.infra.rpc.Result<PageDataVo<NodeVo>> tpcRes = tpc.getUserPermSpace(null, 1, Integer.MAX_VALUE);
        if (tpcRes.getCode() != 0) {
            return Result.fail(CommonError.UNAUTHORIZED);
        }
        List<MapDTO<String, Long>> ret = new ArrayList<>();
        if (tpcRes.getData() == null || tpcRes.getData().getList() == null || tpcRes.getData().getList().isEmpty()) {
            return Result.success(ret);
        }
        List<NodeVo> list = tpcRes.getData().getList();
        for (NodeVo s : list) {
            ret.add(new MapDTO<>(s.getNodeName(), s.getOutId()));
        }
        return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage(), ret);
    }

    /**
     * 更新
     *
     * @param cmd
     * @return
     */
    @Transactional
    public Result<String> updateMilogSpace(CreateOrUpdateSpaceCmd cmd) {
        if (null == cmd || StringUtils.isBlank(cmd.getSpaceName())) {
            return new Result<>(CommonError.ParamsError.getCode(), "参数错误", "");
        }
        if (!tpc.hasPerm(MoneUserContext.getCurrentUser(), cmd.getId())) {
            return Result.fail(CommonError.UNAUTHORIZED);
        }
        if (spaceDao.verifyExistByName(cmd.getSpaceName(), cmd.getId())) {
            return new Result<>(CommonError.UnknownError.getCode(), "存在同名spaceName", "");
        }
        if (cmd.getId() == null) {
            return new Result<>(CommonError.ParamsError.getCode(), "logSpace 不存在", "");
        }
        LogSpaceDO spaceDO = SpaceConvert.INSTANCE.toDO(cmd);
        wrapBaseCommon(spaceDO, OperateEnum.UPDATE_OPERATE);
        boolean updateRes = spaceDao.update(spaceDO);
        if (updateRes) {
            com.xiaomi.youpin.infra.rpc.Result tpcResult = this.tpc.updateSpaceTpc(cmd, MoneUserContext.getCurrentUser());
            if (tpcResult == null || tpcResult.getCode() != 0) {
                log.error("修改space未关联权限系统,space:[{}], tpcResult:[{}]", spaceDO, tpcResult);
                return Result.success("修改space未关联权限系统，请联系服务端效能组");
            }
            return Result.success();
        } else {
            log.warn("[MilogSpaceService.updateMilogSpace] update MilogSpace err,spaceName:{},spaceId:{}", cmd.getSpaceName(), cmd.getId());
            return new Result<>(CommonError.UnknownError.getCode(), CommonError.UnknownError.getMessage(), "");
        }
    }

    @Transactional
    public Result<String> deleteMilogSpace(Long id) {
        if (null == id) {
            return new Result<>(CommonError.ParamsError.getCode(), "id不能为空", "");
        }
        if (!tpc.hasPerm(MoneUserContext.getCurrentUser(), id)) {
            return Result.fail(CommonError.UNAUTHORIZED);
        }
        LogSpaceDO milogSpace = spaceDao.getMilogSpaceById(id);
        if (null == milogSpace) {
            return new Result<>(CommonError.ParamsError.getCode(), "logSpace 不存在", "");
        }
        List<MilogLogStoreDO> stores = milogLogstoreDao.getMilogLogstoreBySpaceId(id);
        if (stores != null && stores.size() != 0) {
            return new Result<>(CommonError.ParamsError.getCode(), "该space 下存在store，无法删除", "");
        }
        if (spaceDao.deleteMilogSpace(id)) {
            com.xiaomi.youpin.infra.rpc.Result tpcResult = tpc.deleteSpaceTpc(id, MoneUserContext.getCurrentUser());
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

    /**
     * 刷新sapce的部门ID字段
     */
    public void refreshSpaceDeptId() {
        try {
            List<LogSpaceDO> spacelist = spaceDao.getAll();
            String limitDeptId;
            for (LogSpaceDO milogSpace : spacelist) {
                limitDeptId = getUserThreeDeptId(milogSpace.getCreator());
                if (!StringUtils.isEmpty(limitDeptId) && !limitDeptId.contains(milogSpace.getPermDeptId())) {
                    if (milogSpace.getPermDeptId().split("\\,").length > 1) {
                        limitDeptId = milogSpace.getPermDeptId() + "," + limitDeptId;
                    }
                    milogSpace.setPermDeptId(limitDeptId);
                    spaceDao.update(milogSpace);
                    log.info("[MilogSpaceService.refreshSpaceDeptId] milogspace[{}]deptId已修改", milogSpace);
                }
            }
        } catch (Exception e) {
            log.error("[MilogSpaceService.refreshSpaceDeptId] has exception {}", e.getMessage());
        }
    }

    // 获取查看space权限的部门ID
    private String getUserThreeDeptId(String userName) {
        UseDetailInfo user = userService.queryUserByUserName(userName);
        if (user == null) {
            return null;
        }
        List<UseDetailInfo.DeptDescriptor> deptList = user.getFullDeptDescriptorList();

        List<UseDetailInfo.DeptDescriptor> collect = deptList.stream().filter(dpet -> "3".equals(dpet.getLevel())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collect)) {
            collect = deptList.stream().filter(dpet -> "2".equals(dpet.getLevel())).collect(Collectors.toList());
        }
        if (CollectionUtils.isEmpty(collect)) {
            collect = deptList.stream().filter(dpet -> "1".equals(dpet.getLevel())).collect(Collectors.toList());
        }
        return collect.isEmpty() ? null : collect.get(0).getDeptId();
    }

    public Result<SpacePermTreeDTO> getSpacecPermission(Long spaceId) {
        if (spaceId == null) {
            return Result.fail(CommonError.ParamsError);
        }
        LogSpaceDO space = spaceDao.getMilogSpaceById(spaceId);
        SpacePermTreeDTO dto = new SpacePermTreeDTO();
        dto.setCheckId(space.getPermDeptId());
        dto.setCreateDeptId(space.getCreateDeptId());
        IDMDeptCache deptCache = idmDept.getDeptCache();
        dto.setTreeData(SpacePermTreeConvert.INSTANCE.fromCache(deptCache));
        return Result.success(dto);
    }

    public Result<String> setSpacePermission(Long spaceId, String permDeptIds) {
        if (spaceId == null || StringUtils.isEmpty(permDeptIds)) {
            return Result.fail(CommonError.ParamsError);
        }
        LogSpaceDO space = spaceDao.getMilogSpaceById(spaceId);
        if (!permDeptIds.contains(space.getCreateDeptId())) {
            return Result.fail(CommonError.ParamsError.getCode(), "创建部门权限不可被取消");
        }
        space.setPermDeptId(permDeptIds);
        boolean update = spaceDao.update(space);
        return update ? Result.success() : Result.fail(CommonError.UnknownError);
    }

    public LogSpaceDO buildMiLogSpace(CreateOrUpdateSpaceCmd cmd, String appCreator) {
        LogSpaceDO ms = new LogSpaceDO();
        wrapMilogSpace(ms, cmd, ProjectSourceEnum.ONE_SOURCE.getSource());
        wrapBaseCommon(ms, OperateEnum.ADD_OPERATE, appCreator);
        return ms;
    }
}
