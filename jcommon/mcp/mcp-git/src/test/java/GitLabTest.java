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

    // 测试push
    @Test
    public void testPush() {
        GitLabFunction gitLabFunction = new GitLabFunction();
        System.out.println(gitLabFunction.executePush("your-git-path","test_commit"));
    }
    // 测试创建merge
    @Test
    public void testCreateMerge() {
        GitLabFunction gitLabFunction = new GitLabFunction();
        System.out.println(gitLabFunction.executeCreateMerge("your-source-branch", "your-target-branch", "test_merge", "123"));
    }

    // 测试获取merge
    @Test
    public void testGetMerge() {
        GitLabFunction gitLabFunction = new GitLabFunction();
        System.out.println(gitLabFunction.executeGetMerge("123", "6"));
    }

    // 测试接受merge
    @Test
    public void testAcceptMerge() {
        GitLabFunction gitLabFunction = new GitLabFunction();
        System.out.println(gitLabFunction.executeAcceptMerge("123", "6"));
    }

    // 测试关闭merge
    @Test
    public void testCloseMerge() {
        GitLabFunction gitLabFunction = new GitLabFunction();
        System.out.println(gitLabFunction.executeCloseMerge("123", "7"));
    }
}
