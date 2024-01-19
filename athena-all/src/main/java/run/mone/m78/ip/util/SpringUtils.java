package run.mone.m78.ip.util;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.Nullable;

/**
 * @author goodjava@qq.com
 * @date 2023/8/10 10:39
 */
public class SpringUtils {


    public static boolean isSpringClass(PsiClass pc) {
        @Nullable PsiAnnotation anno = pc.getAnnotation("org.springframework.stereotype.Service");
        if (null != anno) {
            return true;
        }
        return false;
    }


}
