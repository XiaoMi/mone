package run.mone.mcp.feishu.service;

import com.lark.oapi.core.Config;
import com.lark.oapi.service.docx.v1.DocxService;
import com.lark.oapi.service.docx.v1.model.*;
import com.lark.oapi.service.drive.v1.DriveService;
import com.lark.oapi.service.drive.v1.model.ListFileReq;
import com.lark.oapi.service.drive.v1.model.ListFileResp;
import com.lark.oapi.service.drive.v1.model.PatchPermissionPublicReq;
import com.lark.oapi.service.drive.v1.model.PermissionPublicRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import run.mone.mcp.feishu.model.DocBlock;
import run.mone.mcp.feishu.model.DocContent;
import run.mone.mcp.feishu.model.Files;
import run.mone.mcp.feishu.util.FeishuHttpClient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class FeishuDocService {

    private final DocxService docxService;
    private final DriveService driveService;
    private final FeishuHttpClient httpClient;

    public FeishuDocService() {
        String appId = System.getenv("LARK_APP_ID");
        String appSecret = System.getenv("LARK_APP_SECRET");

        if (appId == null || appSecret == null) {
            throw new IllegalStateException("FEISHU_APP_ID and FEISHU_APP_SECRET system properties must be set");
        }

        Config config = new Config();
        config.setAppId(appId);
        config.setAppSecret(appSecret);
        this.docxService = new DocxService(config);
        this.driveService = new DriveService(config);
        this.httpClient = new FeishuHttpClient(appId, appSecret);
    }

    public String getRootFolderToken() throws Exception {
        Map<String, Object> response = httpClient.get("/drive/explorer/v2/root_folder/meta", null);
        Map<String, Object> data = (Map<String, Object>) response.get("data");
        return (String) data.get("token");
    }

    public List<Files> getDocumentFiles() throws Exception {
        ListFileReq req = ListFileReq.newBuilder()
                .orderBy("EditedTime")
                .pageSize(10)
                .direction("DESC")
                .build();

        // 发起请求
        ListFileResp resp = driveService.file().list(req);
        return Arrays.stream(resp.getData().getFiles()).map(i -> Files.builder()
                        .url(i.getUrl())
                        .type(i.getType())
                        .name(i.getName())
                        .token(i.getToken())
                        .build())
                .toList();
    }

    public DocContent createDocument(String title, String folderToken) throws Exception {
        CreateDocumentReqBody reqBody = CreateDocumentReqBody.newBuilder()
                .title(title)
                .folderToken(folderToken)
                .build();

        CreateDocumentReq req = CreateDocumentReq.newBuilder()
                .createDocumentReqBody(reqBody)
                .build();

        CreateDocumentResp resp = docxService.document().create(req);

        // 创建请求对象
        PatchPermissionPublicReq patchPermissionPublicReq = PatchPermissionPublicReq.newBuilder()
                .permissionPublicRequest(PermissionPublicRequest.newBuilder()
                        .externalAccess(true)
                        .securityEntity("anyone_can_view")
                        .commentEntity("anyone_can_view")
                        .shareEntity("anyone")
                        .linkShareEntity("tenant_editable")
                        .inviteExternal(true)
                        .build())
                .build();

        driveService.permissionPublic().patch(patchPermissionPublicReq);

        return new DocContent()
                .setDocumentId(resp.getData().getDocument().getDocumentId())
                .setTitle(title)
                .setFolderToken(folderToken);
    }

    public DocBlock createDocumentBlock(DocBlock block) throws Exception {
        // 1. 获取文档的根块
        ListDocumentBlockReq listReq = ListDocumentBlockReq.newBuilder()
                .documentId(block.getDocId())
                .build();
        ListDocumentBlockResp listResp = docxService.documentBlock().list(listReq);

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

        CreateDocumentBlockChildrenResp createResp = docxService.documentBlockChildren().create(createReq);
        if (!createResp.success()) {
            throw new IllegalStateException("Failed to create document block: " + createResp.getMsg());
        }
        return block;
    }


    public DocContent getDocument(String documentId) throws Exception {
        GetDocumentReq req = GetDocumentReq.newBuilder()
                .documentId(documentId)
                .build();

        GetDocumentResp resp = docxService.document().get(req);
        Document doc = resp.getData().getDocument();

        return new DocContent()
                .setDocumentId(doc.getDocumentId())
                .setTitle(doc.getTitle());

    }

    public Files getFileInfo(String documentId) throws Exception {
        List<Files> files = getDocumentFiles();
        return files.stream()
                .filter(file -> file.getToken().equals(documentId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("File not found with documentId: " + documentId));
    }

} 