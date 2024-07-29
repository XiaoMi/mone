package run.mone.ai.codegen;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import run.mone.ai.codegen.bo.FeatureGenerateType;
import run.mone.ai.codegen.bo.FeatureGenerateBo;
import run.mone.ai.codegen.util.TemplateUtils;

import java.lang.reflect.Type;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author goodjava@qq.com, HawickMason@xiaomi.com
 * @date 7/12/24 9:44 AM
 */
@Slf4j
public class FeatureGenerator {

    public static void main(String[] args) {
        Map<String, String> map;
        if (args.length > 0) {
            map = parseArgsAndExtractData(args);
            String type = getGenType(map);
            if (type == null) {
                return;
            }
            FeatureGenerateType generateType = FeatureGenerateType.getGenerateTypeByCode(Integer.parseInt(type));
            FeatureGenerateBo featureGenerateBo = mapToGenBo(map);
            switch (generateType) {
                case CODE_WITH_GENERATOR:
                case TABLE:
                    generateWithGenerator(featureGenerateBo);
                    break;
                case CODE_WITH_TEMPLATE:
                    generateByTemplate(featureGenerateBo);
                    break;
                default:
                    log.info("did not match any gen type, will do nothing...");
            }
        }
    }

    private static String getGenType(Map<String, String> map) {
        String type = map.get("type");
        if (StringUtils.isBlank(type)) {
            log.error("generate type is not specified!");
            return null;
        }
        if (!StringUtils.isNumeric(type)) {
            log.error("generate type is not valid, must be number! See @FeatureGenerateType.code");
            return null;
        }
        return type;
    }

    private static FeatureGenerateBo mapToGenBo(Map<String, String> map) {
        FeatureGenerateBo.FeatureGenerateBoBuilder builder = FeatureGenerateBo.builder();
        if (map.containsKey("tableName")) {
            builder.tableName(map.get("tableName"));
        }
        if (map.containsKey("testName")) {
            builder.sql(map.get("testName"));
        }
        if (map.containsKey("sql")) {
            builder.sql(map.get("sql"));
        }
        if (map.containsKey("jdbcUrl")) {
            builder.jdbcUrl(map.get("jdbcUrl"));
        }
        if (map.containsKey("userName")) {
            builder.userName(map.get("userName"));
        }
        if (map.containsKey("password")) {
            builder.password(map.get("password"));
        }
        if (map.containsKey("basePackage")) {
            builder.basePackage(map.get("basePackage"));
        }
        if (map.containsKey("className")) {
            builder.className(map.get("className"));
        }
        if (map.containsKey("auth")) {
            builder.auth(map.get("auth"));
        }
        if (map.containsKey("basePath")) {
            builder.basePath(map.get("basePath"));
        }
        if (map.containsKey("serverModulePath")) {
            builder.serverModulePath(map.get("serverModulePath"));
        }
        if (map.containsKey("serviceModulePath")) {
            builder.serviceModulePath(map.get("serviceModulePath"));
        }
        if (map.containsKey("apiModulePath")) {
            builder.apiModulePath(map.get("apiModulePath"));
        }
        if (map.containsKey("createPojo")) {
            builder.createPojo(Boolean.parseBoolean(map.get("createPojo")));
        }
        if (map.containsKey("createVo")) {
            builder.createVo(Boolean.parseBoolean(map.get("createVo")));
        }
        if (map.containsKey("createTransfer")) {
            builder.createTransfer(Boolean.parseBoolean(map.get("createTransfer")));
        }
        if (map.containsKey("createTest")) {
            builder.createTest(Boolean.parseBoolean(map.get("createTest")));
        }
        if (map.containsKey("createController")) {
            builder.createController(Boolean.parseBoolean(map.get("createController")));
        }
        return builder.build();
    }

    public static void generateWithGenerator(FeatureGenerateBo featureGenerateBo) {

        // 类型检查
        if (Objects.isNull(featureGenerateBo.getType())) {
            log.warn("Empty generation type, will do noting!");
            return;
        }
        FeatureGenerateType featureGenType = featureGenerateBo.getType();
        if (FeatureGenerateType.CODE_WITH_TEMPLATE != featureGenType
                && FeatureGenerateType.TABLE != featureGenType) {
            log.warn("generate type:{} is not match with current call", featureGenType);
            return;
        }

        //配置数据源
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(featureGenerateBo.getJdbcUrl());
        dataSource.setUsername(featureGenerateBo.getUserName());
        dataSource.setPassword(featureGenerateBo.getPassword());

        //创建mapper相关代码
        if (FeatureGenerateType.CODE_WITH_TEMPLATE == featureGenType) {
            GlobalConfig globalConfig = createGlobalConfigUseStyle(featureGenerateBo);
            Generator generator = new Generator(dataSource, globalConfig);
            generator.generate();
            return;
        }

        //创建table
        if (FeatureGenerateType.TABLE == featureGenType) {
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
            String cn = featureGenerateBo.getTestName();
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

    private static Map<String, String> parseArgsAndExtractData(String[] args) {
        if (StringUtils.isBlank(args[0])) {
            log.warn("no valid input params, will do nothing!");
        }
        String jsonStr = args[0];
        Type typeOfT = new TypeToken<Map<String, String>>() {
        }.getType();

        jsonStr = new String(Base64.getDecoder().decode(jsonStr));
        log.info("jsonStr:{}", jsonStr);

        Map<String, String> map = new Gson().fromJson(jsonStr, typeOfT);
        log.info("map:{}", map);
        return map;
    }
}
