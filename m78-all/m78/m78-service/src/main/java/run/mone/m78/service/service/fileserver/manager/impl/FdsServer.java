package run.mone.m78.service.service.fileserver.manager.impl;

import com.xiaomi.infra.galaxy.fds.client.FDSClientConfiguration;
import com.xiaomi.infra.galaxy.fds.client.GalaxyFDS;
import com.xiaomi.infra.galaxy.fds.client.GalaxyFDSClient;
import com.xiaomi.infra.galaxy.fds.client.credential.BasicFDSCredential;
import com.xiaomi.infra.galaxy.fds.client.credential.GalaxyFDSCredential;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import run.mone.m78.service.service.fileserver.manager.IFileServer;

import javax.annotation.PostConstruct;
import java.io.File;
import java.net.URI;
import java.util.Date;

@Component
@Slf4j
public class FdsServer implements IFileServer {

    private static final Date expireData = new Date(2099, 1, 1);

    @Value("${fds.access.key}")
    private String accessKey;

    @Value("${fds.access.ppwwdd}")
    private String accessSecret;

    @Value("${fds.access.endpoint}")
    private String endpoint;

    @Value("${fds.bucket.name}")
    private String bucketName;

    @Value("${fds.bucket.inner.name}")
    private String bucketInnerName;

    private static GalaxyFDS fdsClient;

    @PostConstruct
    private void init() {
        GalaxyFDSCredential credential = new BasicFDSCredential(accessKey, accessSecret);
        FDSClientConfiguration fdsConfig = new FDSClientConfiguration(endpoint); // 设置FDS endpoint;此处是北京2(C3)
        log.info("new FDSClientConfiguration return fdsConfig:{}", fdsConfig);
        fdsConfig.enableHttps(true); // 要不要启用https
        fdsConfig.enableCdnForUpload(false); // 上传走不走CDN
        fdsConfig.enableCdnForDownload(false); // 下载走不走CDN

        fdsClient = new GalaxyFDSClient(credential, fdsConfig);
    }

    @Override
    public String uploadFile(String key, File file, int expireSeconds, boolean inInner) {
        log.info("FdsServer#uploadFile key={}", key);
        try {

            if (inInner) {
                fdsClient.putObject(bucketInnerName, key, file);
                URI uri = fdsClient.generatePresignedUri(bucketInnerName, key, expireData);
                return uri.toString();
            } else {
                fdsClient.putObject(bucketName, key, file);
                URI uri = fdsClient.generatePresignedUri(bucketName, key, expireData);
                return uri.toString();
            }

        } catch (Exception e) {
            log.error("Failed to upload file to FDS, msg: {}", e.getMessage());
        }

        return "";
    }

    @Override
    public byte[] downloadFile(String downloadKey) {
        log.info("FdsServer#downloadFile key={}", downloadKey);
        //todo
        return null;
    }

    @Override
    public boolean deleteFile(String key) {
        log.info("FdsServer#deleteFile key={}", key);
        //todo
        return false;
    }

    public static String expireUrl(String url, String bucketName, String key) {
        if (!url.contains("fds")) {
            return url;
        }
        try {
            URI uri = fdsClient.generatePresignedUri(bucketName, key, expireData);
            return uri.toString();
        } catch (Exception e) {
            log.error("FdsServer.expireUrl, error: {}", e.getMessage());
        }
        return url;
    }

}
