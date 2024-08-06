package run.mone.ai.codegen;

import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import run.mone.ai.codegen.bo.FeatureGeneratType;
import run.mone.ai.codegen.bo.FeatureGenerateBo;
import run.mone.ai.codegen.util.TemplateUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author goodjava@qq.com, HawickMason@xiaomi.com
 * @date 7/12/24 9:44 AM
 */
@Slf4j
public class FeatureGenerator {

    public static void generateWithTemplate(FeatureGenerateBo featureGenerateBo) {

        // 类型检查
        if (Objects.isNull(featureGenerateBo.getType())) {
            log.warn("Empty generation type, will do noting!");
            return;
        }
        FeatureGeneratType featureGenType = featureGenerateBo.getType();
        if (FeatureGeneratType.CODE_WITH_TEMPLATE != featureGenType
                && FeatureGeneratType.TABLE != featureGenType) {
            log.warn("generate type:{} is not match with current call", featureGenType);
            return;
        }

        //配置数据源
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setJdbcUrl(featureGenerateBo.getJdbcUrl());
        dataSource.setUsername(featureGenerateBo.getUserName());
        dataSource.setPassword(featureGenerateBo.getPassword());

        //创建mapper相关代码
        if (FeatureGeneratType.CODE_WITH_TEMPLATE == featureGenType) {
            GlobalConfig globalConfig = createGlobalConfigUseStyle(featureGenerateBo);
            Generator generator = new Generator(dataSource, globalConfig);
            generator.generate();
            return;
        }

        //创建table
        if (FeatureGeneratType.TABLE == featureGenType) {
            JdbcTemplate jt = new JdbcTemplate(dataSource);
            jt.update(featureGenerateBo.getSql());
        }
    }

    public static GlobalConfig createGlobalConfigUseStyle(FeatureGenerateBo featureGenerateBo) {
        GlobalConfig globalConfig = new GlobalConfig();
        //设置根包
        String basePackage = featureGenerateBo.getBasePackage();
        globalConfig.setBasePackage(basePackage);
        //表的名字
        globalConfig.setGenerateTable(featureGenerateBo.getTableName());
        globalConfig.setTablePrefix("");
        //设置生成 entity 并启用 Lombok
        globalConfig.setEntityGenerateEnable(true);
        globalConfig.setEntityWithLombok(true);
        globalConfig.setServiceGenerateEnable(true);
        globalConfig.setServiceImplGenerateEnable(true);
        globalConfig.enableController();

        //生成entity mapper service controller
        String dir = featureGenerateBo.getBasePath();
        globalConfig.setMapperGenerateEnable(true);
        globalConfig.getEntityConfig().setSourceDir(dir);
        globalConfig.getMapperConfig().setSourceDir(dir);
        globalConfig.getServiceConfig().setSourceDir(dir);
        globalConfig.getServiceImplConfig().setSourceDir(dir);
        globalConfig.getControllerConfig().setSourceDir(dir);

        globalConfig.setEntityPackage(basePackage + ".dao.entity");
        globalConfig.setMapperPackage(basePackage + ".dao.mapper");
        globalConfig.setServicePackage(basePackage + ".service");
        globalConfig.setServiceImplPackage(basePackage + ".service");
        globalConfig.setControllerPackage(basePackage + ".controller");

        return globalConfig;
    }

    public static void generateByTemplate(FeatureGenerateBo featureGenerateBo) {
        String className = featureGenerateBo.getClassName();
        if (StringUtils.isEmpty(className)) {
            return;
        }
        Map<String, Object> data = new HashMap<>();
        data.put("className", className);
        data.put("author", "goodjava@qq.com");

        String basePath = featureGenerateBo.getBasePath();
        String basePackage = featureGenerateBo.getBasePackage();
        String midPath = replaceDotWithSlash(basePackage);
        log.info("generate by template with base path:{}", basePath);
        // 调用方法并获取结果
        if (featureGenerateBo.isCreatePojo()) {
            String pojo = TemplateUtils.renderTemplateFromFile("tlp/pojo.java", data);
            TemplateUtils.writeStringToFile(pojo, getFilePath(basePath, featureGenerateBo.getServiceModulePath(), midPath, "/model/po/", className, ".java"));
        }

        if (featureGenerateBo.isCreateVo()) {
            String vo = TemplateUtils.renderTemplateFromFile("tlp/vo.java", data);
            TemplateUtils.writeStringToFile(vo, getFilePath(basePath, featureGenerateBo.getApiModulePath(), midPath, "/api/vo/", className, "VO.java"));
        }

        if (featureGenerateBo.isCreateTransfer()) {
            String transfer = TemplateUtils.renderTemplateFromFile("tlp/transfer.java", data);
            TemplateUtils.writeStringToFile(transfer, getFilePath(basePath, featureGenerateBo.getServiceModulePath(), midPath, "/model/transfer/", className, "Transfer.java"));
        }

        if (featureGenerateBo.isCreateService()) {
            String service = TemplateUtils.renderTemplateFromFile("tlp/service.java", data);
            TemplateUtils.writeStringToFile(service, getFilePath(basePath, featureGenerateBo.getServiceModulePath(), midPath, "/service/", className, "Service.java"));
        }

        if (featureGenerateBo.isCreateTest()) {
            String cn = className + "ServiceTest.java";
            String test = TemplateUtils.renderTemplateFromFile("tlp/test.java", data);
            TemplateUtils.writeStringToFile(test, basePath + "/" + featureGenerateBo.getServerModulePath() + "/src/test/java/run/mone/test/service/" + cn);
        }

        if (featureGenerateBo.isCreateController()) {
            String controller = TemplateUtils.renderTemplateFromFile("tlp/controller.java", data);
            TemplateUtils.writeStringToFile(controller, getFilePath(basePath, featureGenerateBo.getServerModulePath(), midPath, "/controller/", className, "Controller.java"));
        }
    }

    private static String getFilePath(String basePath, String modulePath, String midPath, String subPath, String className, String suffix) {
        return basePath + "/" + modulePath + "/src/main/java/" + midPath + subPath + className + suffix;
    }

    // 将字符串中的"."替换为"/"
    public static String replaceDotWithSlash(String input) {
        if (input == null) {
            return null;
        }
        return input.replace(".", "/");
    }
}
