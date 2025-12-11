package run.mone.hive.roles.tool;

/**
 * 文件操作模式枚举
 *
 * @author goodjava@qq.com
 * @date 2025/12/11
 */
public enum FileOperationMode {
    /**
     * 本地文件系统模式
     * 直接操作本地文件系统
     */
    LOCAL,

    /**
     * 远程 HTTP 模式
     * 通过 HTTP API 操作远程文件系统
     */
    REMOTE_HTTP,

    /**
     * 远程 WebSocket 模式
     * 通过 WebSocket 连接操作远程文件系统
     */
    REMOTE_WS;

    /**
     * 从旧的 boolean isRemote 参数转换为新的模式
     *
     * @param isRemote 是否远程
     * @return 对应的文件操作模式
     */
    public static FileOperationMode fromLegacy(boolean isRemote) {
        return isRemote ? REMOTE_HTTP : LOCAL;
    }

    /**
     * 判断是否为远程模式
     *
     * @return true 如果是任意远程模式
     */
    public boolean isRemote() {
        return this == REMOTE_HTTP || this == REMOTE_WS;
    }
}