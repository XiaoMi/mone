package run.mone.hive.roles.tool;

/**
 * @author goodjava@qq.com
 * @date 2025/4/9 11:07
 */
public interface ITool {

    String getName();

    String description();

    String parameters();

    String usage();

    default String example() {
        return "";
    }


}
