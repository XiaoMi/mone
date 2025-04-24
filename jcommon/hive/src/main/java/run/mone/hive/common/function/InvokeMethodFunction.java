package run.mone.hive.common.function;

import org.beetl.core.Context;
import org.beetl.core.Function;

import java.lang.reflect.Method;

/**
 * @author goodjava@qq.com
 * @date 2025/4/10 13:48
 */
public class InvokeMethodFunction implements Function {
    @Override
    public Object call(Object[] paras, Context ctx) {
        if (paras.length < 2) {
            throw new RuntimeException("invoke方法需要至少两个参数：对象和方法名");
        }

        Object target = paras[0];
        String methodName = paras[1].toString();

        if (target == null) {
            return null;
        }

        try {
            // 获取目标对象的类
            Class<?> clazz = target.getClass();

            // 查找指定名称的方法（无参数）
            Method method = clazz.getMethod(methodName);

            // 设置方法可访问（处理私有方法的情况）
            method.setAccessible(true);

            // 调用方法并返回结果
            return method.invoke(target);

        } catch (NoSuchMethodException e) {
            throw new RuntimeException("未找到方法：" + methodName, e);
        } catch (Exception e) {
            throw new RuntimeException("调用方法" + methodName + "失败", e);
        }
    }
}
