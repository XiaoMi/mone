//package com.xiaomi.youpin.gwdash.controller;
//
//import com.alibaba.nacos.api.config.annotation.NacosValue;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import com.xiaomi.youpin.gwdash.bo.SessionAccount;
//import com.xiaomi.youpin.gwdash.common.Result;
//import com.xiaomi.youpin.gwdash.dao.model.*;
//import com.xiaomi.youpin.gwdash.service.LoginService;
//import com.xiaomi.youpin.gwdash.service.ProjectEnvService;
//import com.xiaomi.youpin.gwdash.service.ProjectService;
//import com.xiaomi.youpin.gwdash.service.WebStaticResourceService;
//import lombok.extern.slf4j.Slf4j;
//import org.codehaus.plexus.util.StringUtils;
//import org.nutz.dao.Cnd;
//import org.nutz.dao.Dao;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//
//@RestController
//@Slf4j
//@RequestMapping("/api/web")
//public class WebStaticResourceController {
//
//    @Autowired
//    private WebStaticResourceService webStaticResourceService;
//
//    @Autowired
//    private LoginService loginService;
//
//    @Autowired
//    private ProjectService projectService;
//
//    @Autowired
//    private ProjectEnvService projectEnvService;
//
//    @Autowired
//    private Dao dao;
//
//    @NacosValue(value = "${web.compile.images}", autoRefreshed = true)
//    private String nodeImages;
//
//    @RequestMapping(value = "/build/image", method = {RequestMethod.GET})
//    public Result<List<WebNodeImage>> getImages(){
//        if (StringUtils.isEmpty(nodeImages)) {
//            return null;
//        }
//        List<String> images = new Gson().fromJson(nodeImages, new TypeToken<List<String>>() {
//        }.getType());
//        List<WebNodeImage> nodeImages = new ArrayList<>(images.size());
//        for (String image : images) {
//            WebNodeImage nodeImage=new WebNodeImage();
//            nodeImage.setVersion(image.split(":")[1]);
//            nodeImage.setImageName(image);
//            nodeImages.add(nodeImage);
//        }
//        Collections.sort(nodeImages, new Comparator<WebNodeImage>() {
//            @Override
//            public int compare(WebNodeImage a, WebNodeImage b) {
//                String versionA = a.getVersion();
//                String versionB = b.getVersion();
//                String[] va=versionA.split("\\.");
//                String[] vb=versionB.split("\\.");
//                for (int i = 0; i < (Math.max(va.length, vb.length)); i++) {
//                    if (i >= va.length) {
//                        return -1;
//                    }
//                    if (i >= vb.length) {
//                        return 1;
//                    }
//                    if (Integer.parseInt(va[i]) < Integer.parseInt(vb[i])) {
//                        return 1;
//                    } else if (Integer.parseInt(va[i]) > Integer.parseInt(vb[i])) {
//                        return -1;
//                    }
//                }
//                return 0;
//            }
//        });
//        return Result.success(nodeImages);
//    }
//
//    @RequestMapping(value = "/deploy/setting/get", method = {RequestMethod.GET})
//    public Result<ProjectEnvDeploySetting> getSetting(@RequestParam("id") long envId, HttpServletRequest request) {
//        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
//        if (null == projectEnv) {
//            return new Result<>(1, "部署环境不存在", null);
//        }
//        long projectId = projectEnv.getProjectId();
//        SessionAccount account = loginService.getAccountFromSession(request);
//        Project project = dao.fetch(Project.class, Cnd.where("id", "=", projectId));
//        if (null == project) {
//            return new Result<>(6, "对应项目不存在", null);
//        }
//        ProjectEnvDeploySetting envDeploySetting = dao.fetch(ProjectEnvDeploySetting.class, Cnd.where("env_id", "=", envId));
//        if (null == envDeploySetting) {
//            return new Result<>(4, "需配置部署设置", null);
//        }
//        if (!projectService.isOwner(projectId, account)) {
//            envDeploySetting.setAccessKeyEnvKey("");
//            envDeploySetting.setAccessSecretEnvKey("");
//        }
//        return Result.success(envDeploySetting);
//    }
//
//    @RequestMapping(value = "/deploy/setting/update", method = {RequestMethod.POST})
//    public Result<ProjectEnvDeploySetting> updateSetting(@RequestBody ProjectEnvDeploySetting setting, HttpServletRequest request)  {
//        long envId = setting.getEnvId();
//        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
//        if (null == projectEnv) {
//            return new Result<>(1, "部署环境不存在", null);
//        }
//        long projectId = projectEnv.getProjectId();
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (!projectService.isOwner(projectId, account)) {
//            setting.setAccessKeyEnvKey(setting.getAccessKeyEnvKey());
//            setting.setAccessSecretEnvKey(setting.getAccessSecretEnvKey());
//        }
//
//        return Result.success(dao.insertOrUpdate(setting));
//    }
//
//
//    /**
//     * 更改前端构建配置
//     */
//    @RequestMapping(value = "/build/setting/update", method = {RequestMethod.POST})
//    public Result<ProjectEnvBuildSetting> updateSetting(@RequestBody ProjectEnvBuildSetting setting, HttpServletRequest request)  {
//        long envId = setting.getEnvId();
//        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
//        if (null == projectEnv) {
//            return new Result<>(1, "部署环境不存在", null);
//        }
//
//        return Result.success(dao.insertOrUpdate(setting));
//    }
//
//    @RequestMapping(value = "/build/setting/get", method = {RequestMethod.GET})
//    public Result<ProjectEnvBuildSetting> getBuildSetting(@RequestParam("id") long envId, HttpServletRequest request) {
//        ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
//        if (null == projectEnv) {
//            return new Result<>(1, "部署环境不存在", null);
//        }
//        return Result.success(dao.fetch(ProjectEnvBuildSetting.class, Cnd.where("env_id", "=", envId)));
//    }
//}
