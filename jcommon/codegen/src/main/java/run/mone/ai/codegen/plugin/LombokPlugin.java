package run.mone.ai.codegen.plugin;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

public class LombokPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        // 该插件不需要任何配置，因此总是返回 true
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        // 为生成的实体类添加 @Data 注解
        FullyQualifiedJavaType dataAnnotation = new FullyQualifiedJavaType("lombok.*");
        topLevelClass.addImportedType(dataAnnotation);
        topLevelClass.addAnnotation("@Data");
        topLevelClass.addAnnotation("@ToString");
        topLevelClass.addAnnotation("@Builder");
        topLevelClass.addAnnotation("@AllArgsConstructor");
        topLevelClass.addAnnotation("@NoArgsConstructor");
        return true;
    }
}

