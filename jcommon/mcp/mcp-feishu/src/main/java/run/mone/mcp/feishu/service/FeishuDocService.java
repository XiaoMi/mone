package run.mone.mcp.feishu.service;

import com.lark.oapi.Client;

import com.lark.oapi.core.request.RequestOptions;
import com.lark.oapi.service.docx.v1.model.*;
import com.lark.oapi.service.drive.v1.model.*;
import com.lark.oapi.service.drive.v2.model.PatchPermissionPublicReq;
import com.lark.oapi.service.drive.v2.model.PatchPermissionPublicResp;
import com.lark.oapi.service.drive.v2.model.PermissionPublic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import run.mone.mcp.feishu.model.DocBlock;
import run.mone.mcp.feishu.model.DocContent;
import run.mone.mcp.feishu.model.FileInfo;
import run.mone.mcp.feishu.model.Files;

import java.util.Arrays;
import java.util.List;


@Slf4j
@Service
public class FeishuDocService {

    private final Client client;

    public FeishuDocService() {
        String appId = System.getenv("LARK_APP_ID");
        String appSecret = System.getenv("LARK_APP_SECRET");

        if (appId == null || appSecret == null) {
            throw new IllegalStateException("FEISHU_APP_ID and FEISHU_APP_SECRET system properties must be set");
        }

        client = Client.newBuilder(appId, appSecret).build();
    }

    public List<Files> getDocumentFiles(String userAccessToken) throws Exception {
        ListFileReq req = ListFileReq.newBuilder()
                .orderBy("EditedTime")
                .pageSize(10)
                .direction("DESC")
                .build();

        // 发起请求
        ListFileResp resp = client.drive().v1().file().list(req, RequestOptions.newBuilder()
                .userAccessToken(userAccessToken)
                .build());
        return Arrays.stream(resp.getData().getFiles()).map(i -> Files.builder()
                        .url(i.getUrl())
                        .type(i.getType())
                        .name(i.getName())
                        .token(i.getToken())
                        .build())
                .toList();
    }

    public DocContent createDocument(String title, String userAccessToken) throws Exception {
        CreateDocumentReqBody reqBody = CreateDocumentReqBody.newBuilder()
                .title(title)
                .build();
        CreateDocumentReq req = CreateDocumentReq.newBuilder().createDocumentReqBody(reqBody).build();

        CreateDocumentResp resp = client.docx().v1().document().create(req, RequestOptions.newBuilder()
                .userAccessToken(userAccessToken)      //用用户身份创建
                .build());
        String documentId = resp.getData().getDocument().getDocumentId();

        // 创建请求对象
        PermissionPublic per = PermissionPublic.newBuilder()
                .externalAccessEntity("open")
                .securityEntity("anyone_can_view")
                .commentEntity("anyone_can_view")
                .shareEntity("anyone")
                .manageCollaboratorEntity("collaborator_can_view")
                .linkShareEntity("tenant_readable")
                .copyEntity("anyone_can_view")
                .build();
        PatchPermissionPublicReq patchReq = PatchPermissionPublicReq.newBuilder()
                .type("docx")
                .token(documentId)
                .permissionPublic(per)
                .build();
        PatchPermissionPublicResp patchResp = client.drive().v2().permissionPublic().patch(patchReq, RequestOptions.newBuilder()
                .userAccessToken(userAccessToken)
                .build());
        return new DocContent()
                .setDocumentId(resp.getData().getDocument().getDocumentId())
                .setTitle(title)
                .setUrl("https://mi.feishu.cn/docx/" + documentId);
    }

    public DocBlock createDocumentBlock(DocBlock block) throws Exception {
        // 1. 获取文档的根块
        ListDocumentBlockReq listReq = ListDocumentBlockReq.newBuilder()
                .documentId(block.getDocId())
                .build();
        ListDocumentBlockResp listResp = client.docx().v1().documentBlock().list(listReq);

        Block[] items = listResp.getData().getItems();
        Block root = items[0];
        block.setParentId(root.getBlockId());
        if (listResp.getData().getItems() == null) {
            throw new IllegalStateException("No blocks found in document");
        }


        // 2. 创建新块
        Block[] feishuBlocks = block.toFeishuBlocks();

        CreateDocumentBlockChildrenReqBody reqBody = CreateDocumentBlockChildrenReqBody.newBuilder()
                .children(feishuBlocks)
                .build();

        CreateDocumentBlockChildrenReq createReq = CreateDocumentBlockChildrenReq.newBuilder()
                .documentId(block.getDocId())
                .blockId(block.getParentId())
                .createDocumentBlockChildrenReqBody(reqBody)
                .build();
        CreateDocumentBlockChildrenResp createResp = client.docx().v1().documentBlockChildren().create(createReq);
        if (!createResp.success()) {
            throw new IllegalStateException("Failed to create document block: " + createResp.getMsg());
        }
        return block;
    }


    public FileInfo getFileBaseInfo(String documentId) throws Exception {
        GetDocumentReq req = GetDocumentReq.newBuilder().documentId(documentId).build();
        GetDocumentResp getDocumentResp = client.docx().v1().document().get(req);
        FileInfo fileInfo = new FileInfo();
        if (getDocumentResp.success()) {
            Document document = getDocumentResp.getData().getDocument();
            fileInfo.setTitle(document.getTitle());
            fileInfo.setRevision_id(document.getRevisionId());
            fileInfo.setDocumentId(document.getDocumentId());
        }
        return fileInfo;
    }

    public String getFileContent(String documentId) throws Exception {
        RawContentDocumentReq req = RawContentDocumentReq.newBuilder().documentId(documentId).lang(0).build();
        RawContentDocumentResp resp = client.docx().v1().document().rawContent(req);
        if (!resp.success()) {
            return "获取文件内容失败！";
        }
        String content = "";
        if (resp.getCode() == 0){
            content = resp.getData().getContent();
        }
        return content;
    }




}