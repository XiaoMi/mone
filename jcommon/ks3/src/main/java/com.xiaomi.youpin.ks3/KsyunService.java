package com.xiaomi.youpin.ks3;

import com.ksyun.ks3.AutoAbortInputStream;
import com.ksyun.ks3.dto.CannedAccessControlList;
import com.ksyun.ks3.dto.GetObjectResult;
import com.ksyun.ks3.dto.Ks3Object;
import com.ksyun.ks3.dto.ResponseHeaderOverrides;
import com.ksyun.ks3.exception.serviceside.NotFoundException;
import com.ksyun.ks3.http.HttpClientConfig;
import com.ksyun.ks3.service.Ks3;
import com.ksyun.ks3.service.Ks3Client;
import com.ksyun.ks3.service.Ks3ClientConfig;
import com.ksyun.ks3.service.request.GetObjectRequest;
import com.ksyun.ks3.service.request.HeadObjectRequest;
import com.ksyun.ks3.service.request.PutObjectRequest;
import com.xiaomi.data.push.client.HttpClientV2;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * @author liuyuchong
 */
@Slf4j
public class KsyunService {

    private static final Logger logger = LoggerFactory.getLogger(KsyunService.class);

    private Ks3 ks3;

    private static final String BUCKET_NAME = "localhost";

    private static final String ROOT_FOLDER = "youpin-arch";

    private static final String DEFAULT_ENDPOINT = "localhost";

    private static final String Version = "0.0.1:2020-03-09";

    @Setter
    private String AccessKeyID;

    @Setter
    private String AccessKeySecret;

    private boolean useSDK;

    private String ftpServerUrl;

    @Setter
    private String token;


    @PostConstruct
    public void init() {
        log.info("KsyunService version:{}", Version);
        Ks3ClientConfig config = new Ks3ClientConfig();
        //此处以北京region为例
        config.setEndpoint(DEFAULT_ENDPOINT);
        config.setDomainMode(false);
        config.setProtocol(Ks3ClientConfig.PROTOCOL.http);
        config.setPathStyleAccess(false);
        HttpClientConfig hconfig = new HttpClientConfig();
        hconfig.setMaxRetry(0);
        hconfig.setConnectionTimeOut((int) TimeUnit.SECONDS.toMillis(3));
        hconfig.setSocketTimeOut((int) TimeUnit.SECONDS.toMillis(2));
        config.setHttpClientConfig(hconfig);
        ks3 = new Ks3Client(AccessKeyID, AccessKeySecret, config);
    }

    public KsyunService() {
        useSDK = true;
    }

    public KsyunService(String accessKeyID, String accessKeySecret) {
        this.AccessKeyID = accessKeyID;
        this.AccessKeySecret = accessKeySecret;
        useSDK = true;
    }

    public KsyunService(String ftpServerUrl) {
        this.ftpServerUrl = ftpServerUrl;
        useSDK = false;
    }

    /**
     * 获取文件
     */
    public byte[] getFileByKey(String key) {
        if (useSDK) {
            return getBytesBySDK(key);
        } else {
            return HttpClientV2.download(ftpServerUrl + "/download?name=" + key + "&token=" + token, 3000);
        }
    }

    /**
     * 直接写入文件,避免二次拷贝
     *
     * @param key
     * @param file
     */
    public boolean getFileByKey(String key, File file) {
        if (useSDK) {
            byte[] data = getBytesBySDK(key);
            try {
                Files.write(Paths.get(file.getPath()), data);
                return true;
            } catch (IOException e) {
                log.warn("error:{}", e.getMessage());
            }
        } else {
            HttpClientV2.download(ftpServerUrl + "/download?name=" + key + "&token=" + token, 3000, file);
            return true;
        }
        return false;
    }

    private byte[] getBytesBySDK(String key) {
        GetObjectRequest request = new GetObjectRequest(BUCKET_NAME, ROOT_FOLDER + "/" + key);

        //重写返回的header
        ResponseHeaderOverrides overrides = new ResponseHeaderOverrides();
        overrides.setContentType("text/html");
        request.setOverrides(overrides);

        //只接受数据的0-10字节。通过控制该项可以实现分块下载
        GetObjectResult result = ks3.getObject(request);
        Ks3Object object = result.getObject();

        //获取object的输入流
        AutoAbortInputStream autoAbortInputStream = object.getObjectContent();
        return InputStreamToByte(autoAbortInputStream);
    }

    /**
     * 获取文件 可配置超时时间(ms)
     */
    public byte[] getFileByKey(String key, int timeout) {
        if (useSDK) {
            return getBytesBySDK(key);
        } else {
            return HttpClientV2.download(ftpServerUrl + "/download?name=" + key + "&token=" + token, timeout);
        }
    }

    /**
     * 输入流转字节流
     */
    private byte[] InputStreamToByte(InputStream in) {
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int ch;
        try {
            while ((ch = in.read(buffer)) != -1) {
                bytestream.write(buffer, 0, ch);
            }
            return bytestream.toByteArray();
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        } finally {
            try {
                bytestream.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }


    /**
     * 上传文件
     */
    public String uploadFile(String key, File file, int expireSeconds) {
        if (useSDK) {
            //将new File("<filePath>")这个文件上传至<bucket名称>这个存储空间下，并命名为<key>
            PutObjectRequest request = new PutObjectRequest(BUCKET_NAME,
                    ROOT_FOLDER + "/" + key, file);

            //上传一个私密文件
            request.setCannedAcl(CannedAccessControlList.Private);
            ks3.putObject(request);
            return getAccessUrl(key, expireSeconds);
        } else {
            try {
                HttpClientV2.upload(ftpServerUrl + "/upload?name=" + key + "&token=" + token, Files.readAllBytes(file.toPath()));
                String url = ftpServerUrl + "/download?name=" + key + "&token=" + token;
                return url;
            } catch (IOException e) {
                log.error(e.getMessage());
            }
            return key;
        }
    }


    /**
     * 生成文件访问地址,expireSeconds秒后过期
     */
    public String getAccessUrl(String key, int expireSeconds) {

        try {
            //判断文件是否存在
            HeadObjectRequest request = new HeadObjectRequest(BUCKET_NAME, ROOT_FOLDER + "/" + key);
            ks3.headObject(request);
        } catch (NotFoundException e) {
            logger.error(key + "   not exist");
            return null;
        }

        String url;
        //生成一个在expireSeconds秒后过期的外链
        try {
            url = ks3.generatePresignedUrl(BUCKET_NAME, ROOT_FOLDER + "/" + key, expireSeconds);
        } catch (Exception e) {
            return null;
        }
        return url;
    }

}
