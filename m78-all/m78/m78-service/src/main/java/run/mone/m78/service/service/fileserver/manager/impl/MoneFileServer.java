package run.mone.m78.service.service.fileserver.manager.impl;

import com.xiaomi.youpin.ks3.KsyunService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import run.mone.m78.service.service.fileserver.manager.IFileServer;

import javax.annotation.PostConstruct;
import java.io.File;

@Component
@Slf4j
public class MoneFileServer implements IFileServer {

    @Value("${mone.file.server.url}")
    private String fileServerUrl;

    @Value("${mone.file.server.token}")
    private String token;

    private KsyunService remoteMoneFileServer;

    @PostConstruct
    private void init() {
        remoteMoneFileServer = new KsyunService(fileServerUrl);
        remoteMoneFileServer.setToken(token);
    }

    @Override
    public String uploadFile(String key, File file, int expireSeconds, boolean isInner) {
        log.info("MoneFileServer#uploadFile key={}", key);
        return remoteMoneFileServer.uploadFile(key, file, expireSeconds);
    }

    @Override
    public byte[] downloadFile(String downloadKey) {
        log.info("MoneFileServer#downloadFile key={}", downloadKey);
        return remoteMoneFileServer.getFileByKey(downloadKey);
    }

    @Override
    public boolean deleteFile(String key) {
        log.info("MoneFileServer#deleteFile key={}", key);
        //todo
        return false;
    }

}
