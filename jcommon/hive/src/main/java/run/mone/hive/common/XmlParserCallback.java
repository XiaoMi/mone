package run.mone.hive.common;

/**
 * @author goodjava@qq.com
 * @date 2024/11/20 15:19
 */
public interface XmlParserCallback {
    // boltArtifact相关回调
    void onArtifactStart(String id, String title);
    void onArtifactEnd();

    // boltAction相关回调
    void onActionStart(String type,String subType, String filePath);
    void onActionEnd();

    // content的逐字符回调
    void onContentChar(char c);
}
