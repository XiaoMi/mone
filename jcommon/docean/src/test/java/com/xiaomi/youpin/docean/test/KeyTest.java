package com.xiaomi.youpin.docean.test;

import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.cert.CertificateException;

/**
 * @author goodjava@qq.com
 * @date 2023/8/17 11:45
 */
public class KeyTest {


    @Test
    public void test1() throws CertificateException, IOException {
        SelfSignedCertificate certificate = new SelfSignedCertificate("zzy.com");
        File file = certificate.privateKey();
        byte[] data = (Files.readAllBytes(Paths.get(file.getPath())));
        System.out.println(new String(data));
        Files.write(Paths.get("/Users/zhangzhiyong/key/zzy.com/private"),data);

        Files.write(Paths.get("/Users/zhangzhiyong/key/zzy.com/public"),Files.readAllBytes(Paths.get(certificate.certificate().toURI())));
    }
}
