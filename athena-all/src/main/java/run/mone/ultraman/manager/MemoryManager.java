package run.mone.ultraman.manager;

/**
 * @author goodjava@qq.com
 * @date 2023/4/18 17:06
 */
public class MemoryManager {

    private MemoryManager() {

    }


    private String module;


    private static MemoryManager ins() {
        return LazyHolder.ins;
    }


    private static class LazyHolder {
        private static MemoryManager ins = new MemoryManager();
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }
}
