package run.mone.ai.codegen;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.CommentGeneratorConfiguration;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.JDBCConnectionConfiguration;
import org.mybatis.generator.config.JavaClientGeneratorConfiguration;
import org.mybatis.generator.config.JavaModelGeneratorConfiguration;
import org.mybatis.generator.config.ModelType;
import org.mybatis.generator.config.PluginConfiguration;
import org.mybatis.generator.config.SqlMapGeneratorConfiguration;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.internal.DefaultShellCallback;
import run.mone.ai.codegen.bo.FeatureGenerateBo;
import run.mone.ai.codegen.bo.MybatisGeneratorResult;
import run.mone.ai.codegen.holder.MybatisGeneratorHolder;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@Slf4j
public class MybatisGenerator {

    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        FeatureGenerateBo featureGenerateBo = null;
        //方便ai调用的时候,设置表名
        if (args.length > 0) {
            featureGenerateBo = parseArgsAndExtractData(args);
            generateMyBatisFiles(featureGenerateBo);
        }
    }

    // 使用mybatis-generator生成指定表对应的XML、Mapper和Entity类
    public static MybatisGeneratorResult generateMyBatisFiles(FeatureGenerateBo featureGenerateBo) {
        MybatisGeneratorResult result = new MybatisGeneratorResult();
        if (Objects.isNull(featureGenerateBo.getTableName())) {
            log.warn("Table name is empty, cannot generate MyBatis files!");
            return result;
        }
        // 配置MyBatis Generator
        List<String> warnings = new ArrayList<>();
        Configuration config = new Configuration();

        Context context = new Context(ModelType.FLAT);
        context.setId("MyBatis3");
        context.setTargetRuntime("MyBatis3");

        // 配置JDBC连接
        JDBCConnectionConfiguration jdbcConnectionConfiguration = new JDBCConnectionConfiguration();
        jdbcConnectionConfiguration.setDriverClass("com.mysql.cj.jdbc.Driver");
        jdbcConnectionConfiguration.setConnectionURL(featureGenerateBo.getJdbcUrl());
        jdbcConnectionConfiguration.setUserId(featureGenerateBo.getUserName());
        jdbcConnectionConfiguration.setPassword(featureGenerateBo.getPassword());
        context.setJdbcConnectionConfiguration(jdbcConnectionConfiguration);

        // 配置Java模型生成器
        JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();
        javaModelGeneratorConfiguration.setTargetPackage(featureGenerateBo.getMybatisEntityPath());
        javaModelGeneratorConfiguration.setTargetProject(featureGenerateBo.getMybatisDaoModule() + "/src/main/java");
        context.setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);

        // 配置SQL映射生成器
        SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = new SqlMapGeneratorConfiguration();
        sqlMapGeneratorConfiguration.setTargetPackage(featureGenerateBo.getMybatisXMLPath());
        sqlMapGeneratorConfiguration.setTargetProject(featureGenerateBo.getMybatisDaoModule() + "/src/main/resources");
        context.setSqlMapGeneratorConfiguration(sqlMapGeneratorConfiguration);

        // 配置Java客户端生成器
        JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = new JavaClientGeneratorConfiguration();
        javaClientGeneratorConfiguration.setTargetPackage(featureGenerateBo.getMybatisDaoPath());
        javaClientGeneratorConfiguration.setTargetProject(featureGenerateBo.getMybatisDaoModule() + "/src/main/java");
        javaClientGeneratorConfiguration.setConfigurationType("XMLMAPPER");
        context.setJavaClientGeneratorConfiguration(javaClientGeneratorConfiguration);

        // 配置表
        TableConfiguration tableConfiguration = new TableConfiguration(context);
        tableConfiguration.setTableName(featureGenerateBo.getTableName());
        tableConfiguration.setCountByExampleStatementEnabled(false);
        tableConfiguration.setDeleteByExampleStatementEnabled(false);
        tableConfiguration.setSelectByExampleStatementEnabled(false);
        tableConfiguration.setUpdateByExampleStatementEnabled(false);
        context.addTableConfiguration(tableConfiguration);

        // 配置 Comment Generator
        CommentGeneratorConfiguration commentGeneratorConfiguration = new CommentGeneratorConfiguration();
        commentGeneratorConfiguration.addProperty("suppressAllComments", "true");
        context.setCommentGeneratorConfiguration(commentGeneratorConfiguration);

        // 配置自定义插件
        PluginConfiguration lombokConfiguration = new PluginConfiguration();
        lombokConfiguration.setConfigurationType("run.mone.ai.codegen.plugin.LombokPlugin");
        context.addPluginConfiguration(lombokConfiguration);

        PluginConfiguration removeGetSetMethodsPlugin = new PluginConfiguration();
        removeGetSetMethodsPlugin.setConfigurationType("run.mone.ai.codegen.plugin.RemoveGetSetMethodsPlugin");
        context.addPluginConfiguration(removeGetSetMethodsPlugin);

        if(!featureGenerateBo.isOverwrite()) {
            // 使得xml文件不会被覆盖
            PluginConfiguration unmergeXML = new PluginConfiguration();
            unmergeXML.setConfigurationType("org.mybatis.generator.plugins.UnmergeableXmlMappersPlugin");
            context.addPluginConfiguration(unmergeXML);
        }

        // 存储这次生成的类名，以便返回
        PluginConfiguration classNamePlugin = new PluginConfiguration();
        classNamePlugin.setConfigurationType("run.mone.ai.codegen.plugin.ClassNamePlugin");
        context.addPluginConfiguration(classNamePlugin);

        config.addContext(context);

        DefaultShellCallback callback = new DefaultShellCallback(featureGenerateBo.isOverwrite());
        MyBatisGenerator myBatisGenerator;
        try {
            myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
            myBatisGenerator.generate(null);

            // 获取返回结果
            result.setEntity(MybatisGeneratorHolder.entity);
            result.setMapper(MybatisGeneratorHolder.mapper);
        } catch (Exception e) {
            log.error("Error generating MyBatis files", e);
        }

        if (!warnings.isEmpty()) {
            warnings.forEach(log::warn);
        }
        return result;
    }

    private static FeatureGenerateBo parseArgsAndExtractData(String[] args) {
        String jsonStr = args[0];

        jsonStr = new String(Base64.getDecoder().decode(jsonStr));
        log.info("jsonStr:{}", jsonStr);

        FeatureGenerateBo bo = gson.fromJson(jsonStr, FeatureGenerateBo.class);
        log.info("map:{}", bo);
        return bo;
    }
}
