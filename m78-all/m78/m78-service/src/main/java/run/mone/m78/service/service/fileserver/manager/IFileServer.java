package run.mone.m78.service.service.fileserver.manager;

import java.io.File;

public interface IFileServer {

    String uploadFile(String key, File file, int expireSeconds, boolean isInner);

    byte[] downloadFile(String downloadKey);

    boolean deleteFile(String key);

}
