//package com.xiaomi.youpin.gwdash.service;
//
//import com.google.gson.Gson;
//import com.google.gson.JsonObject;
//import com.xiaomi.cloud.utils.HttpMethodName;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import com.xiaomi.cloud.client.Client;
//import com.xiaomi.cloud.client.Request;
//import org.apache.http.Header;
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.methods.HttpRequestBase;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.util.EntityUtils;
//
//
//import java.io.IOException;
//
//@Service
//@Slf4j
//public class IAMService {
//
//    @Value("${iam.domain}")
//    private String iamDomain;
//    @Value("${iam.mione.path}")
//    private String defaultTreePPath;
//    @Value("${iam.ak}")
//    private String cloudAk;
//    @Value("${iam.sk}")
//    private String cloudSk;
//
//    public String getFullTreePath(String treeName){
//        return defaultTreePPath +"."+treeName;
//    }
//
//    public String getParentTreePath(){
//        return defaultTreePPath;
//    }
//
//    public void deleteTreeNode(String treeId){
//        // 如果已经存在则直接返回
//        if((describeTreeNode(treeId)) == null){
//            log.debug("treeNode not exists");
//        }
//        // 如果不存在
//        Request request = getRequest(HttpMethodName.DELETE, getUrl(IAMInterfaceEnum.DELETE_URL,treeId));
//        executeRequest(request,true);
//    }
//
//    public IAMTreeModel queryOrSaveTreeNode(String treeName,String treeCnName){
//        treeName = treeName.replaceAll("_","-");
//        log.info("queryOrSaveTreeNode treeName:[{}], treeCnName:[{}]", treeName, treeCnName);
//        String treeFullName = getFullTreePath(treeName);
//        IAMTreeModel iamTreeModel = null;
//        // 如果已经存在则直接返回
//        if((iamTreeModel = describeTreeNode(treeFullName)) != null){
//            log.debug("treeNode already exists, treeNode : [{}]", iamTreeModel);
//            return iamTreeModel;
//        }
//        // 如果不存在
//        Request request = getRequest(HttpMethodName.POST, getUrl(IAMInterfaceEnum.SAVE_URL,getParentTreePath()));
//        JsonObject jsonObject = new JsonObject();
//        jsonObject.addProperty("name", treeName);
//        jsonObject.addProperty("cname", treeCnName);
//
//        request.setBody(jsonObject.toString());
//
//        // 执行查询请求
//        return executeRequest(request,false);
//    }
//
//    public IAMTreeModel describeTreeNode(String treeInfo){
//        Request request = getRequest(HttpMethodName.GET, getUrl(IAMInterfaceEnum.QUERY_URL,treeInfo));
//        // 执行查询请求
//        return executeRequest(request,false);
//    }
//
//    private IAMTreeModel executeRequest(Request request, boolean isDelete){
//        IAMTreeModel iamTreeModel = null;
//        CloseableHttpClient client = null;
//        try {
//            //Sign the request.
//            HttpRequestBase signedRequest = Client.sign(request);
//
//            //Send the request.
//            client = HttpClients.custom().build();
//            HttpResponse response = client.execute(signedRequest);
//
//            //Print the status line of the response.
//            log.debug(response.getStatusLine().toString());
//
//            //Print the header fields of the response.
//            Header[] resHeaders = response.getAllHeaders();
//            for (Header h : resHeaders) {
//                log.debug(h.getName() + ":" + h.getValue());
//            }
//
//            //Print the body of the response.
//            HttpEntity resEntity = response.getEntity();
//            if (resEntity != null) {
//                JsonObject tokenJson = null;
//                try {
//                    String result = EntityUtils.toString(resEntity, "UTF-8");
//
//                    log.info("executeRequest request:[{}] response : [{}]", new Gson().toJson(request), result);
//
//                    tokenJson = new Gson().fromJson(result, JsonObject.class);
//
//                    int code = tokenJson.get("code").getAsInt();
//                    if(code == 200){
//                        if(!isDelete){
//                            JsonObject data = tokenJson.get("data").getAsJsonObject();
//                            iamTreeModel = new Gson().fromJson(data, IAMTreeModel.class);
//                            return iamTreeModel;
//                        }else{
//                            return null;
//                        }
//                    }
//                }catch (Exception e){
//                    log.error(" executeRequest result parse failture ", e);
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error(" executeRequest service failture ", e);
//        } finally {
//            try {
//                if (client != null) {
//                    client.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return iamTreeModel;
//    }
//
//    private Request getRequest(HttpMethodName method,String url){
//        log.debug("getRequest sk:[{}], ak:[{}]", cloudSk, cloudAk);
//        //Create a new request.
//        Request request = new Request();
//        try {
//            request.setKey(cloudAk);
//            request.setSecret(cloudSk);
//            request.setMethod(method.name());
//            request.setUrl(url);
//            request.addHeader("Content-Type", "text/plain");
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("request init failture");
//        }
//        return request;
//    }
//
//    private String getUrl(IAMInterfaceEnum interfaceEnum,String treeInfo){
//        return iamDomain + interfaceEnum.describeUrlWithTreeInfo(interfaceEnum,treeInfo);
//    }
//
//    public class IAMTreeModel{
//        private long id;
//        private String createTime;
//        private String updateTime;
//        private String name;
//        private String cname;
//        private String type;
//        private String pid;
//        private String path;
//
//
//        public long getId() {
//            return id;
//        }
//
//        public void setId(long id) {
//            this.id = id;
//        }
//
//        public String getCreateTime() {
//            return createTime;
//        }
//
//        public void setCreateTime(String createTime) {
//            this.createTime = createTime;
//        }
//
//        public String getUpdateTime() {
//            return updateTime;
//        }
//
//        public void setUpdateTime(String updateTime) {
//            this.updateTime = updateTime;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//
//        public String getCname() {
//            return cname;
//        }
//
//        public void setCname(String cname) {
//            this.cname = cname;
//        }
//
//        public String getType() {
//            return type;
//        }
//
//        public void setType(String type) {
//            this.type = type;
//        }
//
//        public String getPid() {
//            return pid;
//        }
//
//        public void setPid(String pid) {
//            this.pid = pid;
//        }
//
//        public String getPath() {
//            return path;
//        }
//
//        public void setPath(String path) {
//            this.path = path;
//        }
//
//        @Override
//        public String toString() {
//            return "IAMTreeModel{" +
//                    "id=" + id +
//                    ", createTime='" + createTime + '\'' +
//                    ", updateTime='" + updateTime + '\'' +
//                    ", name='" + name + '\'' +
//                    ", cname='" + cname + '\'' +
//                    ", type='" + type + '\'' +
//                    ", pid='" + pid + '\'' +
//                    ", path=" + path +
//                    '}';
//        }
//    }
//
//    enum IAMInterfaceEnum {
//
//        QUERY_URL("/api/v1/trees/")
//        ,SAVE_URL("/api/v1/trees/")
//        ,DELETE_URL("/api/v1/trees/")
//        ;
//
//        private String url;
//
//        IAMInterfaceEnum(String url) {
//            this.url = url;
//        }
//
//        public String getUrl() {
//            return url;
//        }
//
//        public void setUrl(String url) {
//            this.url = url;
//        }
//
//        public String describeUrlWithTreeInfo(IAMInterfaceEnum interfaceEnum, String treeInfo){
//            return interfaceEnum.getUrl()+treeInfo;
//        }
//
//    }
//}
//
//
//
