package com.xiaomi.youpin.tesla.ip.framework.flex;

import com.intellij.psi.*;
import org.apache.commons.lang3.tuple.Pair;

/**
 * @author goodjava@qq.com
 * @date 2024/1/29 13:53
 * <p>
 * 对mybatis flex 框架的一个适配
 */
public class Flex {

    //获取ServiceImpl中的第二个泛型(也就是数据库中对应的类)
    public static String findAndProcessServiceImplWithGeneric(PsiClass psiClass) {
        PsiReferenceList extendsList = psiClass.getExtendsList();
        if (extendsList != null) {
            for (PsiJavaCodeReferenceElement referenceElement : extendsList.getReferenceElements()) {
                PsiClass superClass = (PsiClass) referenceElement.resolve();
                if (superClass != null && "ServiceImpl".equals(superClass.getName())) {
                    // 获取泛型参数
                    PsiReferenceParameterList parameterList = referenceElement.getParameterList();
                    if (parameterList != null) {
                        PsiType[] typeArguments = parameterList.getTypeArguments();
                        if (typeArguments.length == 2) {
                            PsiType secondTypeArgument = typeArguments[1];
                            if (secondTypeArgument instanceof PsiClassType) {
                                PsiClassType classType = (PsiClassType) secondTypeArgument;
                                PsiClass pc = classType.resolve();
                                if (pc != null) {
                                    return pc.getText();
                                }
                            }
                        }
                    }
                }
            }
        }
        return "";
    }


    //public interface E extends BaseMapper<L>  (获取到L)
    public static Pair<String, String> getBaseMapperGenericTypeNameAndText(PsiClass psiClass) {
        PsiReferenceList extendsList = psiClass.getExtendsList();
        if (extendsList != null) {
            for (PsiJavaCodeReferenceElement referenceElement : extendsList.getReferenceElements()) {
                PsiElement resolved = referenceElement.resolve();
                if (resolved instanceof PsiClass) {
                    PsiClass baseMapperInterface = (PsiClass) resolved;
                    // 检查是否是 BaseMapper 接口
                    if ("BaseMapper".equals(baseMapperInterface.getName())) {
                        // 获取 BaseMapper 接口的泛型参数
                        PsiType[] typeArguments = referenceElement.getTypeParameters();
                        if (typeArguments.length == 1) {
                            PsiType typeArgument = typeArguments[0];
                            if (typeArgument instanceof PsiClassType) {
                                PsiClassType classTypeArgument = (PsiClassType) typeArgument;
                                PsiClass poClass = classTypeArgument.resolve();
                                if (poClass != null) {
                                    return Pair.of(poClass.getQualifiedName(), poClass.getText());
                                }
                            }
                        }
                    }
                }
            }
        }
        return Pair.of("", "");
    }

}
