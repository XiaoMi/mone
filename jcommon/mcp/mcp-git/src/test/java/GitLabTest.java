import com.xiaomi.youpin.gitlab.Gitlab;
import org.junit.jupiter.api.Test;
import run.mone.mcp.git.function.GitLabFunction;

/**
 * @author zhangxiaowei6
 * @Date 2025/2/7 15:39
 */

public class GitLabTest {
    // 测试创建仓库
    @Test
    public void testCreateRepository() {
        GitLabFunction gitLabFunction = new GitLabFunction();
        gitLabFunction.executeCreateRepository("test", "testGroupName");
    }

    // 测试搜索仓库
    @Test
    public void testSearchRepository() {
        GitLabFunction gitLabFunction = new GitLabFunction();
        gitLabFunction.executeSearchRepositories("testGroupName", "test");
    }

    // 测试获取分支
    @Test
    public void testGetBranch() {
        GitLabFunction gitLabFunction = new GitLabFunction();
        System.out.println(gitLabFunction.executeGetBranch("master", "123"));
    }

    // 测试创建分支
    @Test
    public void testCreateBranch() {
        GitLabFunction gitLabFunction = new GitLabFunction();
        System.out.println(gitLabFunction.executeCreateBranch("test_branch", "123", "master"));
    }

    // 测试删除分支
    @Test
    public void testDeleteBranch() {
        GitLabFunction gitLabFunction = new GitLabFunction();
        System.out.println(gitLabFunction.executeDeleteBranch("test_branch", "123"));
    }
}
