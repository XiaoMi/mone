package run.mone.m78.service.service.fileserver.manager;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import run.mone.m78.service.service.fileserver.manager.IFileServer;

import java.util.Map;


@Component
public class FileServerFactory {

    @Autowired
    private Map<String, IFileServer> cachedFileServerMap;

    public IFileServer getFileServer(String type) {
        if (StringUtils.isEmpty(type) || !cachedFileServerMap.containsKey(type)) {
            throw new IllegalArgumentException("FileServerFactory type is wrong");
        }
        return cachedFileServerMap.get(type);
    }
}
