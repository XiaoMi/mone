/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.mischedule.util;

import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.*;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 
 * <p>
 *  utils
 */

@Slf4j
public class RepoUtil {

    /**
     * 根据git地址{gitUrl}和分支{branch}，把代码pull至指定目录{gitPath}
     */
    public static Pair<Boolean, String> cloneRepository(String gitUrl, final String branch, File gitPath, String username, String password) {
        try {
            Git git = Git.cloneRepository()
                .setURI(gitUrl)
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password))
                    .setTransportConfigCallback(getTransportConfigCallback())
                .setDirectory(gitPath)
                .setCloneAllBranches(true)
                .call();

            Repository repository = git.getRepository();
            String branchName = repository.getBranch();

            ObjectId objectId = null;
            if (!branch.equals(branchName)) {
                List<Ref> branchList = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
                branchList = branchList.stream().filter(it -> {
                    return it.getName().endsWith("/" + branch);
                }).collect(Collectors.toList());
                int len = branchList.size();
                if (len > 0) {
                    objectId = branchList.get(0).getObjectId();
                } else {
                    objectId = repository.resolve(branch);
                }

                if (null != objectId) {
                    RevWalk revWalk = new RevWalk(repository);
                    RevCommit commit = revWalk.parseCommit(objectId);
                    git.checkout().setStartPoint(commit).setCreateBranch(true).setName(commit.getName()).call();
                } else {
                    return Pair.of(false, "分支不存在");
                }
            }

            // 释放资源
            git.getRepository().close();
            log.info("cloneRepository {} branch:{}", gitUrl, branch);
            return Pair.of(true, "");
        } catch (Exception e) {
            log.error("cloneRepository {}", e);
            return Pair.of(false, e.getMessage());
        }
    }

    private static TransportConfigCallback getTransportConfigCallback() {
        final SshSessionFactory sshSessionFactory = new JschConfigSessionFactory() {
            @Override
            protected void configure(OpenSshConfig.Host host, Session session) {
                session.setConfig("StrictHostKeyChecking", "no");
            }
        };

        return new TransportConfigCallback() {
            @Override
            public void configure(Transport transport) {
                if (transport instanceof TransportHttp) {
                    return;
                }
                SshTransport sshTransport = (SshTransport) transport;
                sshTransport.setSshSessionFactory(sshSessionFactory);
            }
        };
    }


    public static File delTemp(String tempGitPath) {
        File tempGitDir = new File(tempGitPath);
        if (tempGitDir.exists()) {
            try {
                FileUtils.deleteDirectory(tempGitDir);
            } catch (Exception e) {
                log.error("delTemp {}", e.getMessage());
            }
        }
        return tempGitDir;
    }
}
