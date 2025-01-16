package run.mone.m78.codegen;

import com.google.gson.reflect.TypeToken;
import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import run.mone.m78.service.common.GsonUtils;

import java.lang.reflect.Type;
import java.util.Base64;
import java.util.Map;


/**
 * @author wmin
 * @author goodjava@qq.com
 * @date 2024/2/2
 * <p>
 * 给bot暴露的口,方便生成一些框架代码
 */
@Slf4j
public class CodeGen {

    private static String tableName = "m78_bot_execute_record";

    private static String type = "create_code";

    private static String sql = "";


    //生成代码的位置
    private static String dir = "/Users/zhangzhiyong/IdeaProjects/ai/m78/m78-service/src/main/java";

    private static String controllerDir = "/Users/zhangzhiyong/IdeaProjects/ai/m78/m78-server/src/main/java";

    private static String basePackage = "run.mone.m78.service";

    //String dir = "/Users/wmin/Documents/xiaomi-project/m78/m78-service/src/main/java";
    //String dir = "/Users/hoho/IdeaProjects/m78/m78-service/src/main/java";


    /**
     * 创建 HikariDataSource 并设置相关连接信息
     * 创建 GlobalConfig 对象并通过特定方式创建
     * 创建 Generator 对象并调用 generate 方法
     */
    public static void main(String[] args) {
        //方便ai调用的时候,设置表名
        if (args.length > 0) {
            parseArgsAndExtractData(args);
        }

        //配置数据源
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:4100/m78?characterEncoding=utf-8");
        dataSource.setUsername("your_name");
        dataSource.setPassword("your_pwd");

        //创建mapper相关代码
        if (type.equals("create_code")) {
            //创建配置内容，两种风格都可以。
            GlobalConfig globalConfig = createGlobalConfigUseStyle1();
            //通过 datasource 和 globalConfig 创建代码生成器
            Generator generator = new Generator(dataSource, globalConfig);
            //生成代码
            generator.generate();
            return;
        }

        //创建table
        if (type.equals("create_table")) {
            JdbcTemplate jt = new JdbcTemplate(dataSource);
            jt.update(sql);
        }
    }

    private static void parseArgsAndExtractData(String[] args) {
        String jsonStr = args[0];
        Type typeOfT = new TypeToken<Map<String, String>>() {
        }.getType();

        jsonStr = new String(Base64.getDecoder().decode(jsonStr));
        log.info("jsonStr:{}", jsonStr);

        Map<String, String> map = GsonUtils.gson.fromJson(jsonStr, typeOfT);
        log.info("map:{}", map);
        tableName = map.get("tableName");
        sql = map.get("sql");
        type = map.get("type");
    }

    public static GlobalConfig createGlobalConfigUseStyle1() {
        GlobalConfig globalConfig = new GlobalConfig();
        //设置根包
        globalConfig.setBasePackage(basePackage);
        //表的名字
        globalConfig.setGenerateTable(tableName);
        globalConfig.setTablePrefix("");
        //设置生成 entity 并启用 Lombok
        globalConfig.setEntityGenerateEnable(true);
        globalConfig.setEntityWithLombok(true);
        globalConfig.setServiceGenerateEnable(true);
        globalConfig.setServiceImplGenerateEnable(true);
        globalConfig.enableController();

        //生成entity mapper service controller
        globalConfig.setMapperGenerateEnable(true);
        globalConfig.getEntityConfig().setSourceDir(dir);
        globalConfig.getMapperConfig().setSourceDir(dir);
        globalConfig.getServiceConfig().setSourceDir(dir);
        globalConfig.getServiceImplConfig().setSourceDir(dir);
        globalConfig.getControllerConfig().setSourceDir(controllerDir);

        globalConfig.setEntityPackage(basePackage + ".dao.entity");
        globalConfig.setMapperPackage(basePackage + ".dao.mapper");
        globalConfig.setServicePackage(basePackage + ".service");
        globalConfig.setServiceImplPackage(basePackage + ".service");
        globalConfig.setControllerPackage("run.mone.m78.server.controller");

        return globalConfig;
    }

}
