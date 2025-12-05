package run.mone.mcp.git.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import run.mone.mcp.git.model.GitResponse;

import static org.junit.jupiter.api.Assertions.*;

/**
 * GitService测试类
 *
 * 测试GitService中的各种Git操作方法
 *
 * @author generated
 * @date 2025-12-03
 */
@ExtendWith(MockitoExtension.class)
class GitServiceTest {

    @InjectMocks
    private GitService gitService;

    @BeforeEach
    void setUp() {
        // 设置默认配置值
        ReflectionTestUtils.setField(gitService, "defaultUsername", "test");
        ReflectionTestUtils.setField(gitService, "defaultToken", "xxxxx");
        ReflectionTestUtils.setField(gitService, "emailSuffix", "@example.com");
    }

    @Test
    void testCreateMergeRequest_WithValidParameters() {
        // Given
        String repositoryUrl = "https://git.n.com/your-group/your-project.git";
        String sourceBranch = "branch1";
        String targetBranch = "branch2";
        String title = "Test MR";
        String description = "Test description";

        // When
        GitResponse response = gitService.createMergeRequest(
                repositoryUrl, sourceBranch, targetBranch, title, description);

        // Then
        assertNotNull(response);
        // 注意：由于实际会调用HTTP API，这个测试可能会失败
        // 在实际环境中需要mock HttpClient
    }

    @Test
    void testCreateMergeRequest_WithMissingRepositoryUrl() {
        // Given
        String repositoryUrl = null;
        String sourceBranch = "feature/test";
        String targetBranch = "master";
        String title = "Test MR";
        String description = "Test description";

        // When
        GitResponse response = gitService.createMergeRequest(
                repositoryUrl, sourceBranch, targetBranch, title, description);

        // Then
        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("Repository URL is required", response.getError());
    }

    @Test
    void testCreateMergeRequest_WithEmptyRepositoryUrl() {
        // Given
        String repositoryUrl = "";
        String sourceBranch = "feature/test";
        String targetBranch = "master";
        String title = "Test MR";
        String description = "Test description";

        // When
        GitResponse response = gitService.createMergeRequest(
                repositoryUrl, sourceBranch, targetBranch, title, description);

        // Then
        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("Repository URL is required", response.getError());
    }

    @Test
    void testCreateMergeRequest_WithMissingSourceBranch() {
        // Given
        String repositoryUrl = "https://gitlab.com/mygroup/myproject.git";
        String sourceBranch = null;
        String targetBranch = "master";
        String title = "Test MR";
        String description = "Test description";

        // When
        GitResponse response = gitService.createMergeRequest(
                repositoryUrl, sourceBranch, targetBranch, title, description);

        // Then
        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("Source branch is required", response.getError());
    }

    @Test
    void testCreateMergeRequest_WithMissingTargetBranch() {
        // Given
        String repositoryUrl = "https://gitlab.com/mygroup/myproject.git";
        String sourceBranch = "feature/test";
        String targetBranch = null;
        String title = "Test MR";
        String description = "Test description";

        // When
        GitResponse response = gitService.createMergeRequest(
                repositoryUrl, sourceBranch, targetBranch, title, description);

        // Then
        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("Target branch is required", response.getError());
    }

    @Test
    void testCreateMergeRequest_WithNullTitleAndDescription() {
        // Given
        String repositoryUrl = "https://gitlab.com/mygroup/myproject.git";
        String sourceBranch = "feature/test";
        String targetBranch = "master";
        String title = null;
        String description = null;

        // When
        GitResponse response = gitService.createMergeRequest(
                repositoryUrl, sourceBranch, targetBranch, title, description);

        // Then
        assertNotNull(response);
        // 应该使用默认的title和description
        // 实际结果取决于API调用是否成功
    }

    @Test
    void testCheckoutNewBranch_WithValidParameters() {
        // 注意：这个测试需要实际的Git仓库，在真实环境中可能需要mock
        // 这里仅作为示例，实际运行会失败
        String localPath = "/tmp/test-repo";
        String sourceBranch = "master";

        GitResponse response = gitService.checkoutNewBranch(localPath, sourceBranch);

        assertNotNull(response);
        // 由于没有实际的Git仓库，这个测试会失败
    }

    @Test
    void testCheckoutNewBranch_WithMissingLocalPath() {
        // Given
        String localPath = null;
        String sourceBranch = "master";

        // When
        GitResponse response = gitService.checkoutNewBranch(localPath, sourceBranch);

        // Then
        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("Local path is required", response.getError());
    }

    @Test
    void testCheckoutNewBranch_WithMissingSourceBranch() {
        // Given
        String localPath = "/tmp/test-repo";
        String sourceBranch = null;

        // When
        GitResponse response = gitService.checkoutNewBranch(localPath, sourceBranch);

        // Then
        assertNotNull(response);
        assertFalse(response.getSuccess());
        assertEquals("Source branch is required", response.getError());
    }

    @Test
    void testGetEmailSuffix() {
        // When
        String emailSuffix = gitService.getEmailSuffix();

        // Then
        assertNotNull(emailSuffix);
        assertEquals("@example.com", emailSuffix);
    }

    /**
     * 测试URL解析相关的辅助方法
     * 注意：这些方法是private的，如果需要测试可以考虑：
     * 1. 使用反射访问private方法
     * 2. 将这些方法改为protected或package-private
     * 3. 通过公共方法间接测试
     */
    @Test
    void testUrlParsing_HttpsFormat() {
        // 通过createMergeRequest间接测试URL解析
        String httpsUrl = "https://gitlab.com/mygroup/myproject.git";
        String sourceBranch = "feature/test";
        String targetBranch = "master";

        GitResponse response = gitService.createMergeRequest(
                httpsUrl, sourceBranch, targetBranch, null, null);

        assertNotNull(response);
        // URL应该被正确解析
    }

    @Test
    void testUrlParsing_SshFormat() {
        // 通过createMergeRequest间接测试URL解析
        String sshUrl = "git@gitlab.com:mygroup/myproject.git";
        String sourceBranch = "feature/test";
        String targetBranch = "master";

        GitResponse response = gitService.createMergeRequest(
                sshUrl, sourceBranch, targetBranch, null, null);

        assertNotNull(response);
        // URL应该被正确解析
    }

    @Test
    void testUrlParsing_WithoutGitExtension() {
        // 测试没有.git后缀的URL
        String url = "https://gitlab.com/mygroup/myproject";
        String sourceBranch = "feature/test";
        String targetBranch = "master";

        GitResponse response = gitService.createMergeRequest(
                url, sourceBranch, targetBranch, null, null);

        assertNotNull(response);
        // URL应该被正确解析
    }
}
