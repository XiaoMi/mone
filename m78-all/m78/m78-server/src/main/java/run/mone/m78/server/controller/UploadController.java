package run.mone.m78.server.controller;

import com.xiaomi.mone.http.docs.annotations.HttpApiDoc;
import com.xiaomi.mone.http.docs.annotations.HttpApiModule;
import com.xiaomi.mone.http.docs.annotations.MiApiRequestMethod;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import run.mone.m78.api.enums.ImageTypeEnum;
import run.mone.m78.service.bo.aifile.AiFile;
import run.mone.m78.service.bo.user.SessionAccount;
import run.mone.m78.service.dao.entity.M78FileManagement;
import run.mone.m78.service.exceptions.UserAuthException;
import run.mone.m78.service.service.fileserver.AiFileService;
import run.mone.m78.service.service.fileserver.FileUtils;
import run.mone.m78.service.service.fileserver.RemoteFileService;
import run.mone.m78.service.service.user.LoginService;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static run.mone.m78.api.constant.CommonConstant.API_PREFIX;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_FORBIDDEN;

/**
 * @author wmin
 * @date 2024/1/16
 */
@Slf4j
@RestController
@RequestMapping(value = API_PREFIX + "/file")
@HttpApiModule(value = "UploadController", apiController = UploadController.class)
public class UploadController {


    @Autowired
    private LoginService loginService;

    @Autowired
    private RemoteFileService fileService;

    @Autowired
    private AiFileService aiFileService;

    //上传文件，post接口，入参@RequestParam("file") MultipartFile file，调用aiFileService
    @RequestMapping(value = "/upload/ai", method = RequestMethod.POST)
    public Result<List<AiFile>> uploadAiFile(HttpServletRequest request,
                                             @RequestParam("files") MultipartFile[] files,
                                             @RequestParam(name = "aiType", defaultValue = "moonshot") String aiType) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("User not logged in while trying to upload AI file");
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        if (null == files || files.length == 0) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        List<AiFile> list = new ArrayList<>();
        String aiTypeTmp = StringUtils.isEmpty(aiType) ? "moonshot" : aiType;
        IntStream.range(0,files.length).forEach(it->{
            AiFile aiFile;
            switch (aiTypeTmp) {
                case "moonshot": {
                    aiFile = aiFileService.uploadFileToMoonshot(files[it]).getData();
                    break;
                }
                default:
                    aiFile = aiFileService.uploadFileToMoonshot(files[it]).getData();
            }
            list.add(aiFile);
        });

        if (list.size() > 0) {
            return Result.success(list);
        }
        return Result.fail(GeneralCodes.InternalError, "upload AI file error");
    }

    /**
     * 上传文件
     *
     * @param request
     * @param file
     * @return
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public Result<String> upload(HttpServletRequest request,
                                 @RequestParam("file") MultipartFile file) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("User not logged in while trying to get agent list");
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        if (file.isEmpty()) {
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }
        String url = fileService.uploadFile(file);
        if (url != null) {
            return Result.success(url);
        }
        return Result.fail(GeneralCodes.InternalError, "upload file error");
    }

    /**
     * 头像上传
     *
     * @param base64
     * @param request
     * @return
     */
    @PostMapping(value = "/image/avatar/upload")
    public Result<String> uploadBotImage(String base64, HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("User not logged in while trying to get agent list");
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        String resizeImage = FileUtils.resizeImageBase64(base64, 100, 100);
        if (StringUtils.isEmpty(resizeImage)) {
            return Result.fail(GeneralCodes.InternalError, "resize image error");
        }
        String url = fileService.uploadImageFileByBase64(ImageTypeEnum.AVATAR, resizeImage, true);
        if (url != null) {
            return Result.success(url);
        }
        return Result.fail(GeneralCodes.InternalError, "upload image error");
    }

    /**
     * 图片上传，注意：不需要resize
     *
     * @param base64
     * @param isInner
     * @param request
     * @return
     */
    @PostMapping(value = "/image/upload")
    @HttpApiDoc(apiName = "图片上传", value = "", method = MiApiRequestMethod.POST, description = "图片上传")
    public Result<String> uploadBotImageNoResize(String base64, Boolean isInner, HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("User not logged in while trying to get agent list");
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        String url = fileService.uploadImageFileByBase64(ImageTypeEnum.NORMAL_IMAGE, base64, isInner);
        if (url != null) {
            return Result.success(url);
        }
        return Result.fail(GeneralCodes.InternalError, "upload image error");
    }

    /**
     * 图片上传，注意：不需要resize
     *
     * @param base64
     * @param isInner
     * @param request
     * @return
     */
    @PostMapping(value = "/pdf/upload")
    @HttpApiDoc(apiName = "PDF上传", value = "", method = MiApiRequestMethod.POST, description = "PDF上传")
    public Result<String> uploadBotPDF(String base64, Boolean isInner, HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (account == null) {
            log.warn("User not logged in while trying to get agent list");
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        String url = fileService.uploadPDFFileByBase64(ImageTypeEnum.PDF, base64,"pdf", isInner);
        if (url != null) {
            return Result.success(url);
        }
        return Result.fail(GeneralCodes.InternalError, "upload pdf error");
    }


    //获取文件从M78FileManagement中,根据id(class)
    @PostMapping(value = "/get/content")
    @HttpApiDoc(apiName = "获取file内容", value = "", method = MiApiRequestMethod.POST, description = "获取file内容")
    public Result<M78FileManagement> getFileFromM78FileManagementById(String id) {
        SessionAccount account = loginService.getAccountFromSession();
        if (account == null) {
            log.warn("User not logged in while trying to get file from M78FileManagement");
            return Result.fail(STATUS_FORBIDDEN, new UserAuthException().getMessage());
        }

        Result<M78FileManagement> result = aiFileService.getFileFromM78FileManagementById(id);
        if (result != null && result.getCode() == 0) {
            return Result.success(result.getData());
        }
        return Result.fail(GeneralCodes.InternalError, "get file from M78FileManagement error");
    }


}

