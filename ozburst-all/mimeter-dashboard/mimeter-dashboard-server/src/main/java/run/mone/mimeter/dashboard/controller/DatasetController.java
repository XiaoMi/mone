package run.mone.mimeter.dashboard.controller;

import com.xiaomi.mone.http.docs.annotations.HttpApiDoc;
import com.xiaomi.mone.http.docs.annotations.HttpApiModule;
import com.xiaomi.mone.http.docs.annotations.MiApiRequestMethod;
import lombok.extern.slf4j.Slf4j;
import run.mone.mimeter.dashboard.bo.common.Result;
import run.mone.mimeter.dashboard.bo.dataset.*;
import run.mone.mimeter.dashboard.common.SessionAccount;
import run.mone.mimeter.dashboard.exception.CommonError;
import run.mone.mimeter.dashboard.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import run.mone.mimeter.dashboard.service.DatasetService;
import run.mone.mimeter.dashboard.service.impl.LoginService;
import run.mone.mimeter.dashboard.service.impl.UploadService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

import static run.mone.mimeter.dashboard.exception.CommonError.InvalidParamError;

@Slf4j
@RestController
@RequestMapping("/api/bench/dataset")
@HttpApiModule(value = "DatasetController", apiController = DatasetController.class)
public class DatasetController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private DatasetService datasetService;


    @HttpApiDoc(apiName = "数据源新增", value = "/api/bench/dataset/new", method = MiApiRequestMethod.POST, description = "数据源新增")
    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public Result<Integer> newDataset(HttpServletRequest request,
                                      @RequestBody DatasetDto param) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[DatasetController.newDataset] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }
        param.setCreator(account.getUsername());
        param.setUpdater(account.getUsername());
        param.setTenant(account.getTenant());
        log.info("[DatasetController.newDataset] param: {}", param);
        return datasetService.newDataset(param);
    }

    @HttpApiDoc(apiName = "数据源批量新增", value = "/api/bench/dataset/multiNew", method = MiApiRequestMethod.POST, description = "数据源批量新增")
    @RequestMapping(value = "/multiNew", method = RequestMethod.POST)
    public Result<List<Integer>> multiNewDataset(HttpServletRequest request,
                                           @RequestBody List<DatasetDto> param) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[DatasetController.multiNewDataset] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }
        param.forEach(it -> {
            it.setCreator(account.getUsername());
            it.setUpdater(account.getUsername());
            it.setTenant(account.getTenant());
        });

        log.info("[DatasetController.multiNewDataset] param: {}", param);
        return datasetService.multiNewDataset(param);
    }

    @HttpApiDoc(apiName = "数据源更新", value = "/api/bench/dataset/update", method = MiApiRequestMethod.POST, description = "数据源更新")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Result<Boolean> updateDataset(HttpServletRequest request,
                                         @RequestBody DatasetDto param) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[DatasetController.updateDataset] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }
        param.setUpdater(account.getUsername());
        param.setTenant(account.getTenant());

        log.info("[DatasetController.updateDataset] param: {}", param);
        return datasetService.updateDataset(param);
    }

    @HttpApiDoc(apiName = "数据源批量更新", value = "/api/bench/dataset/multiUpdate", method = MiApiRequestMethod.POST, description = "数据源批量更新")
    @RequestMapping(value = "/multiUpdate", method = RequestMethod.POST)
    public Result<Boolean> multiUpdateDataset(HttpServletRequest request,
                                         @RequestBody List<DatasetDto> param) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[DatasetController.multiUpdateDataset] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }
        param.forEach(it -> {
            it.setUpdater(account.getUsername());
            it.setTenant(account.getTenant());
        });

        log.info("[DatasetController.multiUpdateDataset] param: {}", param);
        return datasetService.multiUpdateDataset(param);
    }

    @HttpApiDoc(apiName = "数据源列表", value = "/api/bench/dataset/list", method = MiApiRequestMethod.POST, description = "数据源列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public Result<DatasetList> getDatasetList(HttpServletRequest request,
                                              @RequestBody GetDatasetListReq param) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[DatasetController.getDatasetList] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }

        log.info("[DatasetController.getDatasetList] param: {}", param);
        if (param == null) {
            return Result.fail(InvalidParamError);
        }
        param.setTenant(account.getTenant());
        return datasetService.getDatasetList(param);
    }

    @HttpApiDoc(apiName = "根据数据源ids获取参数列表", value = "/api/bench/dataset/getParamDataByIds", method = MiApiRequestMethod.POST, description = "根据数据源ids获取参数列表")
    @RequestMapping(value = "/getParamDataByIds", method = RequestMethod.POST)
    public Result<List<ParamData>> getParamDataByIds(HttpServletRequest request,
                                              @RequestBody List<Integer> ids) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[DatasetController.getParamDataByIds] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }

        log.info("[DatasetController.getParamDataByIds] param: {}", ids);
        if (ids == null) {
            return Result.fail(InvalidParamError);
        }
        return datasetService.getParamDataByIds(ids);
    }

    @HttpApiDoc(apiName = "数据源删除", value = "/api/bench/dataset/del", method = MiApiRequestMethod.DELETE, description = "数据源删除")
    @RequestMapping(value = "/del", method = RequestMethod.DELETE)
    public Result<Boolean> delDataset(HttpServletRequest request,
                                      @RequestParam("id") int id) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[DatasetController.delDataset] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }

        log.info("[DatasetController.delDataset] param: {}", id);
        return datasetService.delDataset(id);
    }

    @HttpApiDoc(apiName = "数据源批量删除", value = "/api/bench/dataset/multiDel", method = MiApiRequestMethod.POST, description = "数据源批量删除")
    @RequestMapping(value = "/multiDel", method = RequestMethod.POST)
    public Result<Boolean> multiDelDataset(HttpServletRequest request,
                                           @RequestBody List<Integer> ids) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[DatasetController.multiDelDataset] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }

        log.info("[DatasetController.multiDelDataset] param: {}", ids);
        return datasetService.multiDelDataset(ids);
    }

    @HttpApiDoc(apiName = "获取数据源详情", value = "/api/bench/dataset/detail", method = MiApiRequestMethod.GET, description = "获取数据源详情")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public Result<DatasetDto> detail(HttpServletRequest request,
                                     @RequestParam("id") int id) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[DatasetController.detail] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }

        log.info("[DatasetController.detail] param: {}", id);
        return datasetService.getDatasetById(id);
    }

    @HttpApiDoc(apiName = "上传文件数据源", value = "/api/bench/dataset/upload", method = MiApiRequestMethod.POST, description = "上传文件数据源")
    @RequestMapping(value="/upload", method = RequestMethod.POST)
    public Result<UploadFileRes> upload(HttpServletRequest request,
                                        @RequestParam("file") MultipartFile file) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[DatasetController.upload] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }

        if (file.isEmpty()) {
            throw new CommonException(CommonError.EmptyFileError);
        }
        return uploadService.save(file);
    }

    @HttpApiDoc(apiName = "预览文件", value = "/api/bench/dataset/file/preview", method = MiApiRequestMethod.POST, description = "预览文件")
    @RequestMapping(value="/file/preview", method = RequestMethod.POST)
    public Result<PreviewFileRes> filePreview(HttpServletRequest request,
                                              @RequestParam("id") int id) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[DatasetController.filePreview] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }

        log.info("[DatasetController.filePreview] id: {}", id);
        return datasetService.filePreview(id);
    }

    @HttpApiDoc(apiName = "添加使用场景", value = "/api/bench/dataset/bindScene", method = MiApiRequestMethod.POST, description = "添加使用场景")
    @RequestMapping(value="/bindScene", method = RequestMethod.POST)
    public Result<Integer> bindScene(HttpServletRequest request,
                                     @RequestBody DatasetSceneRelationDto datasetSceneRelationDto) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[DatasetController.bindScene] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }

        log.info("[DatasetController.bindScene] param: {}", datasetSceneRelationDto);
        return datasetService.bindScene(datasetSceneRelationDto.getDatasetId(), datasetSceneRelationDto.getSceneId());
    }

    @HttpApiDoc(apiName = "解绑场景", value = "/api/bench/dataset/unbindScene", method = MiApiRequestMethod.POST, description = "解绑场景")
    @RequestMapping(value="/unbindScene", method = RequestMethod.POST)
    public Result<Integer> unbindScene(HttpServletRequest request,
                                       @RequestBody DatasetSceneRelationDto datasetSceneRelationDto) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[DatasetController.unbindScene] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }

        log.info("[DatasetController.unbindScene] param: {}", datasetSceneRelationDto);
        return datasetService.unbindScene(datasetSceneRelationDto.getDatasetId(), datasetSceneRelationDto.getSceneId());
    }

    @HttpApiDoc(apiName = "场景编辑--文件数据源详情", value = "/api/bench/dataset/sceneFileDetail", method = MiApiRequestMethod.POST, description = "场景编辑--文件数据源详情")
    @RequestMapping(value="/sceneFileDetail", method = RequestMethod.POST)
    public Result<SceneFileDetailRes> sceneFileDetail(HttpServletRequest request,
                                                      @RequestParam("sceneId") int sceneId) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (Objects.isNull(account)) {
            log.warn("[DatasetController.sceneFileDetail] current user not have valid account info in session");
            return Result.fail(CommonError.UnknownUser);
        }

        log.info("[DatasetController.sceneFileDetail] id: {}", sceneId);
        return datasetService.sceneFileDetail(sceneId);
    }

}
