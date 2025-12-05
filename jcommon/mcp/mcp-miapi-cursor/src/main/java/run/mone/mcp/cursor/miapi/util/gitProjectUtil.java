package run.mone.mcp.cursor.miapi.util;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;

@Slf4j
public class gitProjectUtil {
    public static String cloneRepository(String gitUrl, String projectName, String username, String token) {
        if (gitUrl.isEmpty() || !gitUrl.endsWith(".git")) {
            return "";
        }
        String cloneDirectoryPath = "/home/work/" + projectName;
        if (!FileScanner.createDirectory(cloneDirectoryPath)) {
            return "";
        }
        File file = new File(cloneDirectoryPath);
        try {
            Git.cloneRepository()
                    .setURI(gitUrl)
                    .setDirectory(file)
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, token))
                    .call();
            return file.getAbsolutePath();
        } catch (Exception e) {
            log.error("cloneRepository error: ", e);
        }
        return "";
    }
}
