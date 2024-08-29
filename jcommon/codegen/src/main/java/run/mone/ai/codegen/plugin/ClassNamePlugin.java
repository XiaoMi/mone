package run.mone.ai.codegen.plugin;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.config.Context;
import run.mone.ai.codegen.holder.MybatisGeneratorHolder;
import org.mybatis.generator.api.JavaFormatter;

import java.util.List;

public class ClassNamePlugin extends PluginAdapter {

    private JavaFormatter javaFormatter;

    @Override
    public void setContext(Context context) {
        super.setContext(context);
        this.javaFormatter = context.getJavaFormatter();
    }

    @Override
    public boolean validate(List<String> warnings) {
        // 这里可以添加一些验证逻辑
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String formattedContent = javaFormatter.getFormattedContent(topLevelClass);
        MybatisGeneratorHolder.entity = formattedContent;
        return true;
    }


    @Override
    public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
        String formattedContent = javaFormatter.getFormattedContent(interfaze);
        MybatisGeneratorHolder.mapper = formattedContent;
        return true;
    }
}

