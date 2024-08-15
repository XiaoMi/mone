package run.mone.ai.codegen.plugin;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.Iterator;
import java.util.List;

public class RemoveGetSetMethodsPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        // 该插件不需要任何配置，因此总是返回 true
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        // 移除 get 和 set 方法
        Iterator<Method> methodIterator = topLevelClass.getMethods().iterator();
        while (methodIterator.hasNext()) {
            Method method = methodIterator.next();
            if (method.getName().startsWith("get") || method.getName().startsWith("set")) {
                methodIterator.remove();
            }
        }
        return true;
    }
}

