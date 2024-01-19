package run.mone.ultraman.common;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2023/4/20 23:01
 */
public class CodeUtils {

    public static String format(String code) {
        return code.replaceFirst("\n+", "").replaceAll("import.*\n","");
    }

    public static String formatRemoveEnter(String code) {
        return code.replaceFirst("\n+", "");
    }

    public static String formatRemoveEnter1(String code) {
        return code.replaceFirst("：\n+", "");
    }

    public static List<String> getImportList(String code) {
        return Arrays.stream(code.split("\n|;")).filter(it->it.startsWith("import")).map(it->it.replaceAll(";|import| ","")).collect(Collectors.toList());
    }

    public static List<String> getImportList2(String code) {
        return Arrays.stream(code.split("\n|;")).filter(it->it.startsWith("import")).map(it->it.replaceAll(";|import","")).collect(Collectors.toList());
    }

    public static String getComment(String code) {
        return code.replaceAll("/\\*\\*|\\*/| +\\* ","").replaceAll("<p>","\n");
    }


    public static void main(String[] args) {
        String code = " /**\n" +
                "     * 方法作用:\n" +
                "     * 对两个整数进行求和操作\n" +
                "     * <p>\n" +
                "     * 方法参数:\n" +
                "     * 参数a和b分别为两个整数\n" +
                "     * <p>\n" +
                "     * 方法返回类型:\n" +
                "     * int\n" +
                "     * <p>\n" +
                "     * 方法逻辑:\n" +
                "     * 将参数a和参数b相加,并返回结果\n" +
                "     */";

        System.out.println(getComment(code));
    }

}
