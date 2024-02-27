package com.xiaomi.youpin.gitlab;

import com.xiaomi.youpin.gitlab.bo.*;
import org.junit.Test;

import java.util.Arrays;

/**
 * @Author wmin
 * @Date 2021-09-01
 */
public class GitlabTest {
    private Gitlab gitlab = new Gitlab();

    @Test
    public void createBranch() {
        String token = "";
        BaseResponse branch = gitlab.createBranch("wangmin17%2Fbootdemo", "test-0747-demo",
                "master", token);
        System.out.println(branch);
    }

    @Test
    public void deleteBranch() {
        String token = "";
        gitlab.deleteBranch("wangmin17%2Fbootdemo", "master-test00", token);
    }

    @Test
    public void mergeBranch() {
        String token = "";
        BaseResponse merge = gitlab.createMerge("wangmin17%2Fbootdemo", "test-01",
                "target-branch", "test merge", token);
        System.out.println(merge);
    }

    @Test
    public void acceptMerge() {
        String token = "";
        BaseResponse merge = gitlab.acceptMerge("wangmin17%2Fbootdemo", "6", token);
        System.out.println(merge);
    }

    @Test
    public void addHook() {
        String token = "";
        GitWebhook gitWebhook = new GitWebhook();
        gitWebhook.setId("wangmin17%2Fbootdemo");
        gitWebhook.setUrl("http://a.b.b");
        //gitlab.addHook(gitWebhook,  "2BULSU-2LLQrizxVXC8m");
        gitWebhook.setHook_id("252279");
        //System.out.println(gitlab.editHook(gitWebhook, "2BULSU-2LLQrizxVXC8m"));
        System.out.println(gitlab.deleteHook(gitWebhook, token));
        //System.out.println(merge);
    }

    @Test
    public void testGetProject() {
        String project = gitlab.getProjectByAddress("https://xxx.com", "xx", "yy", "zz");
        System.out.println(project);
    }

    @Test
    public void mergeBranchWithHost() {
        BaseResponse merge = gitlab.createMerge("https://xxx.com", "1",
                "xx", "yy", "test merge", "zz");
        System.out.println(merge);
    }

    @Test
    public void acceptMergeWithHost() {
        BaseResponse merge = gitlab.acceptMerge("https://xxx.com","1", "1", "xx");
        System.out.println(merge);
    }

    @Test
    public void closeMergeWithHost() {
        BaseResponse merge = gitlab.closeMerge("https://xx.com","1", "2", "3");
        System.out.println(merge);
    }

    @Test
    public void getMergeWithHost() {
        BaseResponse merge = gitlab.getMerge("https://xx.com","1", "1",
                "3");
        System.out.println(merge);
    }

    @Test
    public void createBranchWithHost() {
        BaseResponse branch = gitlab.createBranch("https://xxx.com", "1", "test",
                "test1", "xx");
        System.out.println(branch);
    }

    @Test
    public void getMergeChangeWithHost() {
        BaseResponse merge = gitlab.getMergeChange("https://xxx.com","1", "3",
                "xxx");
        System.out.println(merge);
    }

    @Test
    public void getCommitIds() {
        String commitIds = gitlab.getCommitInfoByBranch("https://xxx.com", "1", "test", "xxx");
        System.out.println(commitIds);
    }

    @Test
    public void getBranchInfo() {
        String branchInfo = gitlab.getBranchInfo("https://xxx.com","1","master","xxx");
        System.out.println(branchInfo);
    }

    @Test
    public void getRepositoryCompare() {
        String repositoryCompare = gitlab.getRepositoryCompare("https://xxx.com", "1", "xxx", "test2", "test1");
        System.out.println(repositoryCompare);
    }

    @Test
    public void getDomainByIP() {
        gitlab.getDomainByIP("https://xxx.com","xx","", Arrays.asList("127.0.0.1"),"token");
    }


    @Test
    public void getProjectCode() {

        gitlab.getDomainByIP("https://xxx.com","xx","", Arrays.asList("127.0.0.1"),"token");
    }


}
