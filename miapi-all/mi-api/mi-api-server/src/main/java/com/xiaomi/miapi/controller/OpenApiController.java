package com.xiaomi.miapi.controller;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.xiaomi.miapi.api.service.MiApiDataService;
import com.xiaomi.miapi.api.service.bo.DubboDocDataBo;
import com.xiaomi.miapi.api.service.bo.HttpDocDataBo;
import com.xiaomi.miapi.api.service.bo.SidecarDocDataBo;
import com.xiaomi.miapi.bo.DubboApiUpdateNotifyBo;
import com.xiaomi.miapi.bo.HttpApiUpdateNotifyBo;
import com.xiaomi.miapi.dto.ProjectApisDTO;
import com.xiaomi.miapi.dto.UrlDTO;
import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.common.exception.CommonError;
import com.xiaomi.miapi.service.*;
import com.xiaomi.miapi.util.RedisUtil;
import com.xiaomi.youpin.ks3.KsyunService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author dongzhenxing
 * @date 2023/02/08
 * provide open api
 */
@Controller
@RequestMapping("/OpenApi")
@Slf4j
public class OpenApiController {

    @Autowired
    private DubboApiService dubboApiService;

    @Autowired
    private HttpApiService httpApiService;

    @Autowired
    private ApiService apiService;

    @Autowired
    private GatewayApiService gatewayApiService;

    @Autowired
    private ApiHistoryService apiHistoryService;

    @Autowired
    private ApiTestService apiTestService;

    @Autowired
    private ApiIndexService apiIndexService;

    @Autowired
    private MiApiDataService miApiDataService;

    private KsyunService ksyunService;

    @NacosValue("${ks3.AccessKeyID}")
    private String accesskey;

    @NacosValue("${ks3.AccessKeySecret}")
    private String accessSecret;

    @Autowired
    private RedisUtil redisUtil;

    @PostConstruct
    private void init() {
        ksyunService = new KsyunService();
        ksyunService.setAccessKeyID(accesskey);
        ksyunService.setAccessKeySecret(accessSecret);
        ksyunService.init();
    }

    @RequestMapping(value = "/ping", method = RequestMethod.POST)
    @ResponseBody
    public void ping() {
        System.out.print("ping success");
    }

    @RequestMapping(value = "/dubboApiUpdateNotify", method = RequestMethod.POST)
    @ResponseBody
    public void dubboApiUpdateNotify(DubboApiUpdateNotifyBo bo) {
        try {
            dubboApiService.dubboApiUpdateNotify(bo);
        } catch (Exception e) {
            log.error("dubboApiUpdateNotify error,serviceName:{},ip:{},port:{},error:{}", bo.getModuleClassName(), bo.getIp(), bo.getPort(), e.getMessage());
        }
    }

    /**
     * accept dubbo api doc data
     */
    @RequestMapping(value = "/pushDubboDocData", method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> pushDubboDocData(@RequestBody DubboDocDataBo dubboDocDataBo) {
        log.info("pushDubboDocData,address:{}", dubboDocDataBo.getAddress());
        try {
            miApiDataService.pushServiceDocDataToMiApi(dubboDocDataBo);
        } catch (Exception e) {
            log.error("pushDubboDocData failed,error:{}", e.getMessage());
        }
        return Result.success(true);
    }

    /**
     * accept http api doc data notify
     */
    @ResponseBody
    @RequestMapping(value = "/httpApiUpdateNotify", method = RequestMethod.POST)
    public void httpApiUpdateNotify(HttpApiUpdateNotifyBo bo) {
        try {
            httpApiService.httpApiUpdateNotify(bo);
        } catch (Exception e) {
            log.error("httpApiUpdateNotify error,serviceName:{},ip:{},port:{},error:{}", bo.getApiController(), bo.getIp(), bo.getPort(), e.getMessage());
        }
    }

    /**
     * accept http api doc data
     */
    @RequestMapping(value = "/pushHttpDocData", method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> pushHttpDocData(@RequestBody HttpDocDataBo httpDocDataBo) {
        log.info("pushHttpDocData,address:{}", httpDocDataBo.getAddress());
        try {
            miApiDataService.pushServiceDocDataToMiApi(httpDocDataBo);
        } catch (Exception e) {
            log.error("pushHttpDocData failed,error:{}", e.getMessage());
        }
        return Result.success(true);
    }


    /**
     * accept sidecar api doc data
     */
    @RequestMapping(value = "/pushSidecarDocData", method = RequestMethod.POST)
    @ResponseBody
    public Result<Boolean> pushSidecarDocData(@RequestBody SidecarDocDataBo sidecarDocDataBo) {
        log.info("pushSidecarDocData,address:{}", sidecarDocDataBo.getAddress());
        try {
            miApiDataService.pushServiceDocDataToMiApi(sidecarDocDataBo);
        } catch (Exception e) {
            log.error("pushSidecarDocData failed,error:{}", e.getMessage());
        }
        return Result.success(true);
    }



    @ResponseBody
    @RequestMapping(value = "/apiUpdateExample", method = RequestMethod.POST)
    public Map<String, String> apiUpdateExample(Integer apiID, Integer compareID
    ) {
        return apiHistoryService.compareTwoVersionApi(apiID, compareID);
    }

    @ResponseBody
    @RequestMapping(value = "/uploadImage", method = RequestMethod.POST)
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        String url = "";
        try {
            File tmpFile = File.createTempFile("upload-file-", file.getOriginalFilename());
            Path path = tmpFile.toPath();
            OutputStream os = Files.newOutputStream(path);
            os.write(file.getBytes());
            os.flush();
            String fileMd5 = DigestUtils.md5DigestAsHex(Files.newInputStream(path));
            log.info("uploadImage md5: {}", fileMd5);
            url = ksyunService.uploadFile("/mi-api/doc/file/" + fileMd5, tmpFile, 60 * 60 * 24 * 23999);
            boolean success = tmpFile.delete();
            if (!success) {
                log.warn("UploadService delete temp file failed for file with original name: " + file.getOriginalFilename());
            }
        } catch (IOException e) {
            log.info("UploadService#uploadFile: {}", e.getMessage());
        }
        return Result.success(url);
    }

    @ResponseBody
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public Result<String> uploadFile(HttpServletResponse response,
                                     @RequestParam("file") MultipartFile file){
        String url = "";
        try {
            File tmpFile = File.createTempFile("upload-file-", file.getOriginalFilename());
            OutputStream os = Files.newOutputStream(tmpFile.toPath());
            os.write(file.getBytes());
            os.flush();
            url = ksyunService.uploadFile("/mi-api/mi-doc/file/" + file.getOriginalFilename(), tmpFile, 60 * 60 * 24 * 23999);
            boolean success = tmpFile.delete();
            if (!success) {
                log.warn("UploadService delete temp file failed for file with original name: " + file.getOriginalFilename());
            }
        } catch (IOException e) {
            log.info("UploadService#uploadFile: {}", e.getMessage());
        }
        return Result.success(url);
    }

    @RequestMapping(value = "/getGatewayApiInfoByUrl", method = RequestMethod.POST)
    @ResponseBody
    public Result<List<Map<String, Object>>> getGatewayApiInfoByUrl(HttpServletRequest request,
                                                                    HttpServletResponse response,
                                                                    @RequestBody List<UrlDTO> urls
    ) {
        if (Objects.isNull(urls)) {
            return Result.fail(CommonError.InvalidParamError);
        }
        return gatewayApiService.getGatewayApiDetailByUrl(urls);
    }

    @RequestMapping(value = "/getApiListByProjectId", method = RequestMethod.POST)
    @ResponseBody
    public Result<List<Map<String, Object>>> getApiListByProjectId(@RequestBody ProjectApisDTO dto) {
        return apiService.getApiListByProjectId(dto);
    }

    /**
     * the API index page data
     */
    @ResponseBody
    @RequestMapping(value = "/getIndexPageInfo", method = RequestMethod.POST)
    public Result<List<Map<String, Object>>> getIndexPageInfo(String indexIDs) {
        return apiIndexService.getIndexPageInfo(indexIDs);
    }
}
