package run.mone.test;

import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.ColumnConfig;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.mybatisflex.codegen.config.TableDefConfig;
import com.zaxxer.hikari.HikariDataSource;


/**
 * @author wmin
 * @date 2024/2/2
 */
public class DbCodegen {

    private static String tableName = "";

    public static void main(String[] args) {
        //配置数据源
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://localhost:13306/mione_z?characterEncoding=utf-8");
        dataSource.setUsername("X");
        dataSource.setPassword("X");

        //创建配置内容，两种风格都可以。
        GlobalConfig globalConfig = createGlobalConfigUseStyle1();
        //GlobalConfig globalConfig = createGlobalConfigUseStyle2();

        //通过 datasource 和 globalConfig 创建代码生成器
        Generator generator = new Generator(dataSource, globalConfig);

        //生成代码
        generator.generate();
    }

    public static GlobalConfig createGlobalConfigUseStyle1() {
        //创建配置内容
        GlobalConfig globalConfig = new GlobalConfig();

        //设置根包
        globalConfig.setBasePackage("run.mone.knowledge.service.dao");

        //设置表前缀和只生成哪些表
        globalConfig.setGenerateTable("v_knowledge_vector_detail", "v_knowledge_vector_meta");

        //设置生成 entity 并启用 Lombok
        globalConfig.setEntityGenerateEnable(true);
        globalConfig.setEntityWithLombok(true);
        //设置项目的JDK版本，项目的JDK为14及以上时建议设置该项，小于14则可以不设置
        //globalConfig.setJdkVersion(17);

        //设置生成 mapper
        globalConfig.setMapperGenerateEnable(true);

        String dir = "/Users/wmin/Documents/xiaomi-project/private-knowledge/private-knowledge-service/src/main/java";
//        String dir = "/Users/zhangzhiyong/IdeaProjects/ai/m78/m78-service/src/main/java";

        globalConfig.getEntityConfig().setSourceDir(dir);
        globalConfig.getMapperConfig().setSourceDir(dir);


        //可以单独配置某个列
//        ColumnConfig columnConfig = new ColumnConfig();
//        columnConfig.setColumnName("tenant_id");
//        columnConfig.setLarge(true);
//        columnConfig.setVersion(true);
//        globalConfig.setColumnConfig("tb_account", columnConfig);

//        TableDefConfig tableDefConfig = new TableDefConfig();
//        tableDefConfig.setClassPrefix("KnowledgeVector");
        globalConfig.getTableDefConfig().setClassPrefix("KnowledgeVector");
        return globalConfig;
    }


}
