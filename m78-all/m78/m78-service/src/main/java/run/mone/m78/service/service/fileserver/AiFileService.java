package run.mone.m78.service.service.fileserver;

import com.google.gson.Gson;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import run.mone.m78.service.bo.aifile.AiFile;
import run.mone.m78.service.bo.file.moonshot.MoonshotContentRes;
import run.mone.m78.service.bo.file.moonshot.MoonshotUploadRes;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.dao.entity.M78FileManagement;
import run.mone.m78.service.service.fileserver.manager.FileServerFactory;
import run.mone.m78.service.service.fileserver.manager.IFileServer;
import run.mone.m78.service.service.fileserver.store.M78FileManagementService;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static run.mone.m78.service.exceptions.ExCodes.STATUS_INTERNAL_ERROR;
import static run.mone.m78.service.exceptions.ExCodes.STATUS_NOT_FOUND;

@Slf4j
@Service

/**
 * AiFileService类提供了与文件管理相关的服务，主要包括上传文件到Moonshot服务器和从M78FileManagement中获取文件。
 *
 * 该类使用了Spring的@Service注解，表明它是一个服务类，并且使用了@Slf4j注解来进行日志记录。
 *
 * 主要功能：
 * 1. 上传文件到Moonshot服务器，并在上传成功后删除文件。
 * 2. 根据ID从M78FileManagement中获取文件。
 *
 * 依赖：
 * - FileServerFactory：用于获取文件服务器实例。
 * - M78FileManagementService：用于管理M78FileManagement实体。
 * - Gson：用于JSON解析。
 *
 * 常量：
 * - FILE_PATH：文件存储的临时路径。
 *
 * 异常处理：
 * - 在上传文件和获取文件过程中，处理了可能的异常情况并记录了错误日志。
 */

public class AiFileService {

    private static Gson gson = GsonUtils.gson;

    public static final String FILE_PATH = "/tmp/work/file/ai/";

    @Autowired
    private FileServerFactory fileServerFactory;


    @Resource
    private M78FileManagementService m78FileManagementService;


    private Boolean deleteFileFromMoonshot(String key) {
        return fileServerFactory.getFileServer("moonshotServer").deleteFile(key);
    }

    /**
     * 上传文件到Moonshot服务器
     *
     * @param file 要上传的文件
     * @return 包含上传文件信息的结果对象
     * @throws IllegalArgumentException 如果文件为空或文件名为空
     * @throws RuntimeException         如果文件传输失败
     */
    //上传文件，入参是MultipartFile file
    public Result<AiFile> uploadFileToMoonshot(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("file is empty");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new IllegalArgumentException("file name is empty");
        }

        File destFile = null;
        IFileServer fileServer = fileServerFactory.getFileServer("moonshotServer");
        try {
            //上传
            byte[] bytes = file.getBytes();
            destFile = FileUtils.convertByteToFile(bytes, FILE_PATH + System.currentTimeMillis() + "/" + fileName);
            String uploadRes = fileServer.uploadFile(fileName, destFile, 0, true);
            MoonshotUploadRes moonshotUploadRes = gson.fromJson(uploadRes, MoonshotUploadRes.class);

            if (!"ok".equals(moonshotUploadRes.getStatus())) {
                return Result.fail(STATUS_INTERNAL_ERROR, moonshotUploadRes.getStatusDetails());
            }

            //解析
            String key = moonshotUploadRes.getId();
            byte[] byteRes = fileServer.downloadFile(key);
            String fileContent = new String(byteRes, StandardCharsets.UTF_8);
            MoonshotContentRes moonshotContentRes = gson.fromJson(fileContent, MoonshotContentRes.class);
            String content = moonshotContentRes.getContent();

            //存储content
            int id = saveContentWithMoonshotId(content, moonshotUploadRes);

            //删除
            fileServer.deleteFile(key);
            return Result.success(AiFile.builder().id(id).name(fileName).content(content).build());
        } catch (IOException e) {
            log.error("Failed to transfer file", e);
            throw new RuntimeException("Failed to transfer file", e);
        } finally {
            if (destFile != null && destFile.exists()) {
                destFile.delete();
            }
        }
    }

    private int saveContentWithMoonshotId(String content, MoonshotUploadRes moonshotUploadRes) {
        //存储content
        M78FileManagement fileManagement = new M78FileManagement();
        fileManagement.setContent(content);
        fileManagement.setMoonshotId(moonshotUploadRes.getId());
        m78FileManagementService.save(fileManagement);
        return fileManagement.getId();
    }

    /**
     * 根据ID从M78FileManagement中获取文件
     *
     * @param id 文件的唯一标识符
     * @return 包含文件信息的Result对象，如果文件不存在则返回失败的Result对象
     * @throws IllegalArgumentException 如果ID为空或null
     */
    //获取文件从M78FileManagement中,根据id(class)
    public Result<M78FileManagement> getFileFromM78FileManagementById(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("id is empty");
        }

        M78FileManagement fileManagement = m78FileManagementService.getById(id);
        if (fileManagement == null) {
            return Result.fail(STATUS_NOT_FOUND, "File not found");
        }

        return Result.success(fileManagement);
    }


}
