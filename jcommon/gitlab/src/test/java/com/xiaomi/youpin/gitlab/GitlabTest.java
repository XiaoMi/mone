package com.xiaomi.youpin.gitlab;

import com.xiaomi.youpin.gitlab.bo.BaseResponse;
import com.xiaomi.youpin.gitlab.bo.GitWebhook;
import com.xiaomi.youpin.gitlab.bo.GitlabBranch;
import com.xiaomi.youpin.gitlab.bo.GitlabMerge;
import org.junit.Test;
/**
 * @Author wmin
 * @Date 2021-09-01
 */
public class GitlabTest {
    private Gitlab gitlab = new Gitlab();

    @Test
    public void createBranch() {
        String token = "";
        BaseResponse branch = gitlab.createBranch("wangmin17%2Fbootdemo", "test-0747-demo", "master", token);
        System.out.println(branch);
    }

    @Test
    public void deleteBranch() {
        String token = "";
        gitlab.deleteBranch("wangmin17%2Fbootdemo", "master-test00",  token);
    }

    @Test
    public void mergeBranch() {
        String token = "";
        BaseResponse merge = gitlab.createMerge("wangmin17%2Fbootdemo", "test-01", "target-branch", "test merge", token);
        System.out.println(merge);
    }

    @Test
    public void acceptMerge() {
        String token = "";
        BaseResponse merge = gitlab.acceptMerge("wangmin17%2Fbootdemo", "6",  token);
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
}
