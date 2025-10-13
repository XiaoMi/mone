package run.mone.hive.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;

@Slf4j
public class RemoteFileUtils {

    /**
     * 上传文件到远程服务器
     *
     * @param fileName    文件名
     * @param fileContent Base64编码的文件内容
     * @return 上传结果
     * @throws IOException 如果上传失败
     */
    public static String uploadFile(String fileName, String fileContent) throws IOException {
        if (fileContent == null || fileContent.isEmpty()) {
            throw new IOException("文件内容不能为空");
        }

        String url = String.format("%s/upload?name=%s&userKey=%s&userSecret=%s&token=%s", getHost(), fileName, getUserKey(), getUserSecret(), getToken());
        byte[] content = java.util.Base64.getDecoder().decode(fileContent);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new ByteArrayEntity(content));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode >= 200 && statusCode < 300) {
                    return "文件上传成功";
                } else {
                    throw new IOException("文件上传失败，状态码: " + statusCode);
                }
            }
        }
    }

    /**
     * 列出远程文件或目录
     *
     * @param dirName 目录路径
     * @return 文件列表结果
     * @throws IOException 如果操作失败
     */
    public static String listFiles(String dirName, boolean recursive) throws IOException {
        if (dirName == null || dirName.isEmpty()) {
            return null;
        }
        if (dirName.startsWith(File.separator)) {
            dirName = dirName.substring(1);
        }
        String url = String.format("%s/list?directory=%s&userKey=%s&userSecret=%s&token=%s&circle=%s", getHost(), dirName, getUserKey(), getUserSecret(), getToken(), String.valueOf(recursive));

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : "";
            }
        }
    }

    /**
     * 删除远程文件
     *
     * @param fileName 要删除的文件名
     * @return 删除结果
     * @throws IOException 如果删除失败
     */
    public static String deleteFile(String fileName) throws IOException {
        String url = String.format("%s/delete?name=%s&userKey=%s&userSecret=%s&token=%s", getHost(), fileName, getUserKey(), getUserSecret(), getToken());

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : "";
            }
        }
    }

    /**
     * 创建远程目录
     *
     * @param directoryPath 目录路径
     * @return 创建结果
     * @throws IOException 如果创建失败
     */
    public static String createDirectory(String directoryPath) throws IOException {
        String url = String.format("%s/createDir?directory=%s&userKey=%s&userSecret=%s&token=%s", getHost(), directoryPath, getUserKey(), getUserSecret(), getToken());

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : "";
            }
        }
    }

    /**
     * 删除远程目录
     *
     * @param directoryPath 目录路径
     * @return 删除结果
     * @throws IOException 如果删除失败
     */
    public static String deleteDirectory(String directoryPath) throws IOException {
        String url = String.format("%s/deleteDir?directory=%s&userKey=%s&userSecret=%s&token=%s", getHost(), directoryPath, getUserKey(), getUserSecret(), getToken());

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : "";
            }
        }
    }

    /**
     * 获取文件下载URL
     *
     * @param fileName 文件名
     * @return 下载URL
     */
    public static String getDownloadUrl(String fileName) {
        String url = String.format("%s/download?name=%s&userKey=%s&userSecret=%s&token=%s", getHost(), fileName, getUserKey(), getUserSecret(), getToken());
        return String.format("<download_file fileName=\"%s\" fileUrl=\"%s\">%s</download_file>", fileName, url, url);
    }

    /**
     * 获取远程文件内容
     *
     * @param fileName 文件名
     * @return 文件内容
     * @throws IOException 如果获取失败
     */
    public static String getRemoteFileContent(String fileName) throws IOException {
        String url = String.format("%s/download?name=%s&userKey=%s&userSecret=%s&token=%s", getHost(), fileName, getUserKey(), getUserSecret(), getToken());

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode >= 200 && statusCode < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : "";
                } else {
                    throw new IOException("获取文件内容失败，状态码: " + statusCode);
                }
            }
        }
    }

    /**
     * 搜索远程文件
     *
     * @param directoryPath 目录路径
     * @param regex         正则表达式
     * @param filePattern   文件模式
     * @return 搜索结果
     * @throws IOException 如果搜索失败
     */
    public static String searchFiles(String directoryPath, String regex, String filePattern) throws IOException {
        if (directoryPath == null || directoryPath.isEmpty()) {
            throw new IOException("目录路径不能为空");
        }

        if (directoryPath.startsWith(File.separator)) {
            directoryPath = directoryPath.substring(1);
        }

        String url = String.format("%s/search?directory=%s&userKey=%s&userSecret=%s&token=%s&regex=%s&filePattern=%s",
                getHost(),
                directoryPath,
                getUserKey(),
                getUserSecret(),
                getToken(),
                regex != null ? regex : "",
                filePattern != null ? filePattern : "");

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode >= 200 && statusCode < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : "";
                } else {
                    throw new IOException("搜索文件失败，状态码: " + statusCode);
                }
            }
        }
    }

    /**
     * 获取API主机地址
     *
     * @return API主机地址
     */
    private static String getHost() {
        return System.getenv().getOrDefault("REMOTE_FILE_API_HOST", "http://127.0.0.1:9777");
    }

    /**
     * 获取用户Key
     *
     * @return 用户Key
     */
    private static String getUserKey() {
        return System.getenv().getOrDefault("REMOTE_FILE_USER_KEY", "wangmin");
    }

    /**
     * 获取用户Secret
     *
     * @return 用户Secret
     */
    private static String getUserSecret() {
        return System.getenv().getOrDefault("REMOTE_FILE_USER_SECRET", "123456");
    }

    /**
     * 获取API令牌
     *
     * @return API令牌
     */
    private static String getToken() {
        return System.getenv().getOrDefault("REMOTE_FILE_API_TOKEN", "1");
    }

}
