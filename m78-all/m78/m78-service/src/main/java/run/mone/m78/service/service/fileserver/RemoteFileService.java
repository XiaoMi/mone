package run.mone.m78.service.service.fileserver;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import run.mone.m78.api.enums.ImageTypeEnum;
import run.mone.m78.service.service.fileserver.manager.FileServerFactory;
import run.mone.m78.service.service.fileserver.manager.IFileServer;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

@Slf4j
@Service

/**
 * RemoteFileService类提供了文件和图片上传的服务。
 * 该类使用了不同的文件服务器类型来处理文件的上传，并支持通过Base64编码上传图片文件。
 *
 * <p>主要功能包括：
 * <ul>
 *   <li>上传文件并返回文件的URL地址</li>
 *   <li>通过Base64编码上传图片文件并返回图片的URL地址</li>
 * </ul>
 *
 * <p>该类依赖于IFileServer接口和FileServerFactory工厂类来实现文件服务器的具体操作。
 *
 * <p>注意：该类在初始化时会根据配置文件中的文件服务器类型来选择具体的文件服务器实现。
 *
 * <p>异常处理：
 * <ul>
 *   <li>如果文件为空或文件名为空，会抛出IllegalArgumentException</li>
 *   <li>如果文件传输失败，会抛出RuntimeException</li>
 * </ul>
 *
 * <p>使用了@Slf4j注解来记录日志信息。
 *
 * @see IFileServer
 * @see FileServerFactory
 */

public class RemoteFileService {

    public static final String AVATAR_PATH = "/tmp/work/image/avatar/";
    public static final String PDF_PATH = "/tmp/work/pdf/";

    public static final String FILE_PATH = "/tmp/work/file/";

    private IFileServer fileServer;

    private static final int TEN_YEAYS_SECONDS = 3600 * 24 * 30 * 12 * 10;

    @Value("${fileserver.type}")
    private String type;

    @Autowired
    private FileServerFactory fileServerFactory;

    @PostConstruct
    private void init() {
        log.info("useFileServer type: {}", type);
        fileServer = fileServerFactory.getFileServer(type);
    }

    /**
     * 上传文件
     *
     * @param file 要上传的文件
     * @return 文件上传后的URL地址，如果上传失败返回null
     * @throws IllegalArgumentException 如果文件为空或文件名为空
     * @throws RuntimeException         如果文件传输失败
     */
    //上传文件，入参是MultipartFile file
    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("file is empty");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new IllegalArgumentException("file name is empty");
        }

        try {
            byte[] bytes = file.getBytes();
            File destFile = FileUtils.convertByteToFile(bytes, FILE_PATH + System.currentTimeMillis() + "/" + fileName);
            String url = fileServer.uploadFile(fileName, destFile, TEN_YEAYS_SECONDS, true);
            if (fileName.equals(url)) {
                destFile.delete();
                return null;
            }

            destFile.delete();
            return url;
        } catch (IOException e) {
            log.error("Failed to transfer file", e);
            throw new RuntimeException("Failed to transfer file", e);
        }
    }

    /**
     * 通过Base64编码上传图片文件
     *
     * @param avatarType 图片类型枚举
     * @param base64     Base64编码的图片数据
     * @param isInner    是否为内部上传
     * @return 上传结果的字符串表示
     */
    public String uploadImageFileByBase64(ImageTypeEnum avatarType, String base64, boolean isInner) {
        return uploadImageFileByBase64(avatarType, base64, FileUtils.getImageTypeFromBase64(base64), isInner);
    }

    public String uploadImageFileByBase64(ImageTypeEnum avatarType, String base64, String fileType, boolean isInner) {
        if (StringUtils.isEmpty(base64)) {
            throw new IllegalArgumentException("image base64 is empty");
        }
        if (!FileUtils.isBase64Type(base64)) {
            throw new IllegalArgumentException("image format is not allow");
        }

        String fileName = FileUtils.getFileName(avatarType, fileType);
        File file = FileUtils.convertBase64ToFile(base64, AVATAR_PATH + fileName);
        if (file == null) {
            return null;
        }
        String url = fileServer.uploadFile(fileName, file, TEN_YEAYS_SECONDS, isInner);
        if (fileName.equals(url)) {
            file.delete();
            return null;
        }
        // 上传成功后删除本地文件
        file.delete();
        return url;

    }

    public String uploadPDFFileByBase64(ImageTypeEnum avatarType, String base64, String fileType, boolean isInner) {
        if (StringUtils.isEmpty(base64)) {
            throw new IllegalArgumentException("image base64 is empty");
        }
        if (!FileUtils.isPDF64Type(base64)) {
            throw new IllegalArgumentException("image format is not allow");
        }

        String fileName = FileUtils.getFileName(avatarType, fileType);
        File file = FileUtils.convertBase64ToFile(base64, PDF_PATH + fileName);
        if (file == null) {
            return null;
        }
        String url = fileServer.uploadFile(fileName, file, TEN_YEAYS_SECONDS, isInner);
        if (fileName.equals(url)) {
            file.delete();
            return null;
        }
        // 上传成功后删除本地文件
        file.delete();
        return url;

    }


}
