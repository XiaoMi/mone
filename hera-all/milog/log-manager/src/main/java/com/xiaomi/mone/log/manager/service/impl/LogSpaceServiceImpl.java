package com.xiaomi.mone.log.manager.service.impl;

import com.xiaomi.mone.log.api.enums.OperateEnum;
import com.xiaomi.mone.log.api.enums.ProjectSourceEnum;
import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.exception.CommonError;
import com.xiaomi.mone.log.manager.common.context.MoneUserContext;
import com.xiaomi.mone.log.manager.convert.SpacePermTreeConvert;
import com.xiaomi.mone.log.manager.dao.MilogLogstoreDao;
import com.xiaomi.mone.log.manager.dao.MilogSpaceDao;
import com.xiaomi.mone.log.manager.domain.IDMDept;
import com.xiaomi.mone.log.manager.domain.Space;
import com.xiaomi.mone.log.manager.domain.Tpc;
import com.xiaomi.mone.log.manager.model.MilogSpaceParam;
import com.xiaomi.mone.log.manager.model.cache.IDMDeptCache;
import com.xiaomi.mone.log.manager.model.dto.MapDTO;
import com.xiaomi.mone.log.manager.model.dto.MilogSpaceDTO;
import com.xiaomi.mone.log.manager.model.dto.SpacePermTreeDTO;
import com.xiaomi.mone.log.manager.model.page.PageInfo;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogStoreDO;
import com.xiaomi.mone.log.manager.model.pojo.MilogSpaceDO;
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
    private MilogSpaceDao milogSpaceDao;

    @Resource
    private MilogLogstoreDao milogLogstoreDao;

    @Resource
    private IdmMoneUserDetailService userService;

    @Resource
    Tpc tpc;

    @Resource
    IDMDept idmDept;

    @Resource
    Space space;

    /**
     * 新建
     *
     * @param param
     * @return
     */
    @Override
    public Result<String> newMilogSpace(MilogSpaceParam param) {
        if (null == param || StringUtils.isBlank(param.getSpaceName())) {
            return new Result<>(CommonError.ParamsError.getCode(), "参数错误");
        }
        if (milogSpaceDao.verifyExistByName(param.getSpaceName())) {
            return new Result<>(CommonError.UnknownError.getCode(), "存在同名spaceName", "");
        }
        MilogSpaceDO ms = new MilogSpaceDO();
        wrapMilogSpace(ms, param);
        wrapBaseCommon(ms, OperateEnum.ADD_OPERATE);
        MilogSpaceDO dbDO = milogSpaceDao.newMilogSpace(ms);
        if (dbDO.getId() == null) {
            return Result.failParam("space未保存成功，请重试");
        }
        com.xiaomi.youpin.infra.rpc.Result tpcResult = tpc.saveSpacePerm(dbDO, MoneUserContext.getCurrentUser().getUser());
        if (tpcResult == null || tpcResult.getCode() != 0) {
            milogSpaceDao.deleteMilogSpace(dbDO.getId());
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
    public Result<PageInfo<MilogSpaceDTO>> getMilogSpaceByPage(String spaceName, Integer page, Integer pagesize) {
        return Result.success(space.getMilogSpaceByPage(spaceName, page, pagesize));
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
     * @param param
     * @return
     */
    @Transactional
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
            com.xiaomi.youpin.infra.rpc.Result tpcResult = this.tpc.updateSpaceTpc(param, MoneUserContext.getCurrentUser().getUser());
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
            List<MilogSpaceDO> spacelist = milogSpaceDao.getAll();
            String limitDeptId;
            for (MilogSpaceDO milogSpace : spacelist) {
                limitDeptId = getUserThreeDeptId(milogSpace.getCreator());
                if (!StringUtils.isEmpty(limitDeptId) && !limitDeptId.contains(milogSpace.getPermDeptId())) {
                    if (milogSpace.getPermDeptId().split("\\,").length > 1) {
                        limitDeptId = milogSpace.getPermDeptId() + "," + limitDeptId;
                    }
                    milogSpace.setPermDeptId(limitDeptId);
                    milogSpaceDao.update(milogSpace);
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
        MilogSpaceDO space = milogSpaceDao.getMilogSpaceById(spaceId);
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
        MilogSpaceDO space = milogSpaceDao.getMilogSpaceById(spaceId);
        if (!permDeptIds.contains(space.getCreateDeptId())) {
            return Result.fail(CommonError.ParamsError.getCode(), "创建部门权限不可被取消");
        }
        space.setPermDeptId(permDeptIds);
        boolean update = milogSpaceDao.update(space);
        return update ? Result.success() : Result.fail(CommonError.UnknownError);
    }

    public MilogSpaceDO buildMiLogSpace(MilogSpaceParam cmd, String appCreator) {
        MilogSpaceDO ms = new MilogSpaceDO();
        wrapMilogSpace(ms, cmd, ProjectSourceEnum.ONE_SOURCE.getSource());
        wrapBaseCommon(ms, OperateEnum.ADD_OPERATE, appCreator);
        return ms;
    }
}
