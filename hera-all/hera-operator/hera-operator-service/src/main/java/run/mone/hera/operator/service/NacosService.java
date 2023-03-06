package run.mone.hera.operator.service;

import com.alibaba.nacos.api.config.ConfigFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;
import run.mone.hera.operator.common.FileUtils;

import java.io.File;

/**
 * @Description
 * @Author dingtao
 * @Date 2023/2/23 6:02 PM
 */
@Service
@Slf4j
public class NacosService {

    public void publishNacosConfig(String nacosAddr, String directory) {
        try {
            ConfigService configService = ConfigFactory.createConfigService(nacosAddr);
            String path = this.getClass().getResource(directory).getPath();
            File dir = new File(path);
            File[] files = dir.listFiles();
            for (File file : files) {
                String name = file.getName();
                String[] split = name.substring(0, name.indexOf(".properties")).split("_#_");
                configService.publishConfig(split[0], split[1], FileUtils.fileToString(file));
            }
        } catch (Throwable t) {
            log.error("publish nacos config error", t);
        }
    }

}
