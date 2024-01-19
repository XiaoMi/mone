package run.mone.m78.ip.util;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2023/8/11 09:46
 */
public class TestUtils {


    public static List<String> junitImportList = Lists.newArrayList("import org.junit.Test", "import org.junit.Assert");

    public static List<String> jupiterImportList = Lists.newArrayList("import org.junit.jupiter.api.Test", "import static org.junit.jupiter.api.Assertions.assertEquals", "import static org.junit.jupiter.api.Assertions.assertNotNull");


    public static boolean isJupiter() {
        if (isClassPresent("org.junit.Test")) {
            return false;
        } else if (isClassPresent("org.junit.jupiter.api.Test")) {
            return true;
        } else {
            return false;
        }
    }

    public static List<String> getImportList(String version) {
        if (version.equals("unknow")) {
            return Lists.newArrayList();
        }
        if (version.equals("junit")) {
            return junitImportList;
        }
        if (version.equals("jupiter")) {
            return junitImportList;
        }
        return Lists.newArrayList();
    }


    private static boolean isClassPresent(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}
