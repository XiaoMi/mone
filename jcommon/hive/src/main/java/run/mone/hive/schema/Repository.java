package run.mone.hive.schema;

/**
 * @author goodjava@qq.com
 * @date 2024/12/25 17:16
 */
public interface Repository {

    String getSystemDesign(String filename);
    String getTask(String filename);
    String getSourceCode(String filename);

}
