package com.xiaomi.miapi.controller;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.alibaba.nacos.api.exception.NacosException;
import com.xiaomi.miapi.common.Consts;
import com.xiaomi.miapi.common.bo.DubboApiUpdateNotifyBo;
import com.xiaomi.miapi.common.bo.DubboTestBo;
import com.xiaomi.miapi.common.bo.HttpApiUpdateNotifyBo;
import com.xiaomi.miapi.common.dto.ProjectApisDTO;
import com.xiaomi.miapi.common.dto.UrlDTO;
import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.common.exception.CommonError;
import com.xiaomi.miapi.service.*;
import com.xiaomi.miapi.util.SessionAccount;
import com.xiaomi.youpin.ks3.KsyunService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    private KsyunService ksyunService;

    @NacosValue("${ks3.AccessKeyID}")
    private String accesskey;

    @NacosValue("${ks3.AccessKeySecret}")
    private String accessSecret;

    @PostConstruct
    private void init() {
        ksyunService = new KsyunService();
        ksyunService.setAccessKeyID(accesskey);
        ksyunService.setAccessKeySecret(accessSecret);
        ksyunService.init();
    }

    @RequestMapping(value = "/ping", method = RequestMethod.POST)
    @ResponseBody
    public void ping(HttpServletRequest request,
                     HttpServletResponse response
    ) throws IOException, InterruptedException {
        System.out.print("ping success");
    }

    /**
     * dubbo api 更新通知
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/dubboApiUpdateNotify", method = RequestMethod.POST)
    @ResponseBody
    public void dubboApiUpdateNotify(HttpServletRequest request,
                                     HttpServletResponse response,
                                     DubboApiUpdateNotifyBo bo
    ) throws IOException, InterruptedException {
        if (dubboApiService.dubboApiUpdateNotify(bo).getCode() != 0) {
            log.warn("dubboApiUpdateNotify error,serviceName:{},ip:{},port:{}", bo.getModuleClassName(), bo.getIp(), bo.getPort());
        }
    }

    /**
     * http api 更新通知
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/httpApiUpdateNotify", method = RequestMethod.POST)
    public void httpApiUpdateNotify(HttpServletRequest request,
                                    HttpServletResponse response,
                                    HttpApiUpdateNotifyBo bo
    ) throws IOException, InterruptedException {
        if (httpApiService.httpApiUpdateNotify(bo).getCode() != 0) {
            log.warn("httpApiUpdateNotify error,serviceName:{},ip:{},port:{}", bo.getApiController(), bo.getIp(), bo.getPort());
        }
    }

    /**
     * 接口更新示例
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/apiUpdateExample", method = RequestMethod.POST)
    public Map<String, String> apiUpdateExample(HttpServletRequest request,
                                                HttpServletResponse response,
                                                Integer apiID, Integer compareID
    ) throws IOException, InterruptedException {
        return apiHistoryService.compareTwoVersionApi(apiID, compareID);
    }

    /**
     * 上传图片
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/uploadImage", method = RequestMethod.POST)
    public Result<String> uploadImage(HttpServletResponse response,
                                      @RequestParam("file") MultipartFile file, HttpServletRequest request
    ) throws IOException, InterruptedException {
        String url = "";
        try {
            File tmpFile = File.createTempFile("upload-file-", file.getOriginalFilename());
            OutputStream os = new FileOutputStream(tmpFile);
            os.write(file.getBytes());
            os.flush();
            String fileMd5 = DigestUtils.md5DigestAsHex(new FileInputStream(tmpFile));
            log.info("uploadImage md5: {}", fileMd5);
            url = ksyunService.uploadFile("/mi-api/doc/file/" + fileMd5, tmpFile, 60 * 60 * 24 * 23999);
            boolean success = tmpFile.delete();
            if (!success) {
                log.warn("UploadService delete temp file failed for file with original name: " + file.getOriginalFilename());
            }
        } catch (IOException e) {
            log.info("UploadService#uploadFile: {}", e);
        }
        return Result.success(url);
    }

    /**
     * 上传文件
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public Result<String> uploadFile(HttpServletResponse response,
                                      @RequestParam("file") MultipartFile file, HttpServletRequest request
    ) throws IOException, InterruptedException {
        String url = "";
        try {
            File tmpFile = File.createTempFile("upload-file-", file.getOriginalFilename());
            OutputStream os = new FileOutputStream(tmpFile);
            os.write(file.getBytes());
            os.flush();
            url = ksyunService.uploadFile("/mi-api/mi-doc/file/" + file.getOriginalFilename(), tmpFile, 60 * 60 * 24 * 23999);
            boolean success = tmpFile.delete();
            if (!success) {
                log.warn("UploadService delete temp file failed for file with original name: " + file.getOriginalFilename());
            }
        } catch (IOException e) {
            log.info("UploadService#uploadFile: {}", e);
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

    @RequestMapping(value = "/openDubboTest", method = RequestMethod.POST)
    @ResponseBody
    public Result<Object> openDubboTest(@RequestBody DubboTestBo dubboTestBo) throws NacosException {
        return apiTestService.dubboTest(dubboTestBo,"testUser",99999);
    }

    /**
     * 具体集合API页面数据
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getIndexPageInfo", method = RequestMethod.POST)
    public Result<List<Map<String,Object>>> getIndexPageInfo(HttpServletRequest request,
                                                             HttpServletResponse response,
                                                             String indexIDs
    ) {
        return apiIndexService.getIndexPageInfo(indexIDs);
    }
}
