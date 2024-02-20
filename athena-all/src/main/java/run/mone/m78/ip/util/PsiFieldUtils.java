package run.mone.m78.ip.util;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import run.mone.m78.ip.bo.AnnoInfo;
import run.mone.m78.ip.bo.AnnoMember;
import run.mone.m78.ip.bo.FieldInfo;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2023/6/21 16:25
 */
public class PsiFieldUtils {


    //根据PsiClass获取字段信息列表
    public static List<FieldInfo> list(PsiClass psiClass) {
        return Arrays.stream(psiClass.getFields()).map(it -> {
            FieldInfo info = new FieldInfo();
            info.setName(it.getName());
            info.setClassType(it.getType().getCanonicalText());
            List<AnnoInfo> annoList = Arrays.stream(it.getAnnotations()).map(it2 -> {
                AnnoInfo annoInfo = new AnnoInfo();
                annoInfo.setName(it2.getQualifiedName());
                Map<String, AnnoMember> m = it2.getAttributes().stream().map(it3 -> {
                    String key = it3.getAttributeName();
                    String value = it2.findAttributeValue(key).getText();
                    return AnnoMember.builder().key(key).value(value).build();
                }).collect(Collectors.toMap(key -> key.getKey(), value -> value));
                annoInfo.setMembers(m);
                return annoInfo;
            }).collect(Collectors.toList());
            info.setAnnoList(annoList);
            return info;
        }).collect(Collectors.toList());
    }


    //重命名项目中的指定字段
    public static void rename(Project project, PsiField field, String newName) {
        PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);
        PsiField newField = factory.createField(newName, field.getType());
        newField.setInitializer(field.getInitializer());
        newField.getModifierList().replace(field.getModifierList());
        field.replace(newField);
    }


}
