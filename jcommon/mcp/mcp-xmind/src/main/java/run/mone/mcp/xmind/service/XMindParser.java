package run.mone.mcp.xmind.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import run.mone.mcp.xmind.service.model.XMindNode;
import run.mone.mcp.xmind.service.model.XMindRelationship;
import run.mone.mcp.xmind.service.model.XMindCallout;
import run.mone.mcp.xmind.service.model.XMindNotes;

public class XMindParser {
    private final Path filePath;
    private final ObjectMapper objectMapper;

    public XMindParser(String filePath) {
        this.filePath = Paths.get(filePath).toAbsolutePath().normalize();
        this.objectMapper = new ObjectMapper();
    }

    public List<XMindNode> parse() throws IOException {
        String contentJson = extractContentJson();
        return parseContentJson(contentJson);
    }

    private String extractContentJson() throws IOException {
        try (ZipFile zipFile = new ZipFile(filePath.toFile())) {
            FileHeader contentEntry = zipFile.getFileHeader("content.json");
            if (contentEntry == null) {
                throw new IOException("content.json not found in XMind file");
            }
            return new String(zipFile.getInputStream(contentEntry).readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private List<XMindNode> parseContentJson(String jsonContent) throws IOException {
        List<XMindNode> allNodes = new ArrayList<>();
        var sheets = objectMapper.readTree(jsonContent);
        
        for (var sheet : sheets) {
            var rootTopic = sheet.get("rootTopic");
            var title = Optional.ofNullable(sheet.get("title"))
                    .map(node -> node.asText())
                    .orElse("Untitled Map");
            
            XMindNode rootNode = processNode(rootTopic, title);
            
            // 处理关系
            if (sheet.has("relationships")) {
                var relationships = new ArrayList<XMindRelationship>();
                var rels = sheet.get("relationships");
                for (var rel : rels) {
                    relationships.add(new XMindRelationship(
                        rel.get("id").asText(),
                        rel.get("end1Id").asText(),
                        rel.get("end2Id").asText(),
                        Optional.ofNullable(rel.get("title"))
                            .map(node -> node.asText())
                            .orElse(null)
                    ));
                }
                rootNode.setRelationships(relationships);
            }
            
            allNodes.add(rootNode);
        }
        
        return allNodes;
    }

    private XMindNode processNode(JsonNode node, String sheetTitle) {
        XMindNode processedNode = new XMindNode();
        processedNode.setTitle(node.get("title").asText());
        processedNode.setId(node.get("id").asText());
        processedNode.setSheetTitle(sheetTitle);

        // 处理链接和标签
        Optional.ofNullable(node.get("href"))
            .ifPresent(href -> processedNode.setHref(href.asText()));
        
        if (node.has("labels")) {
            List<String> labels = new ArrayList<>();
            node.get("labels").forEach(label -> labels.add(label.asText()));
            processedNode.setLabels(labels);
        }

        // 处理标注
        if (node.has("children") && node.get("children").has("callout")) {
            List<XMindCallout> callouts = new ArrayList<>();
            node.get("children").get("callout").forEach(callout -> 
                callouts.add(new XMindCallout(callout.get("title").asText()))
            );
            processedNode.setCallouts(callouts);
        }

        // 处理笔记
        if (node.has("notes") && node.get("notes").has("plain")) {
            XMindNotes notes = new XMindNotes();
            notes.setContent(node.get("notes").get("plain").get("content").asText());
            processedNode.setNotes(notes);
        }

        // 处理任务状态
        if (node.has("extensions")) {
            node.get("extensions").forEach(ext -> {
                if ("org.xmind.ui.task".equals(ext.get("provider").asText()) &&
                    ext.has("content") && ext.get("content").has("status")) {
                    processedNode.setTaskStatus(ext.get("content").get("status").asText());
                }
            });
        }

        // 处理子节点
        if (node.has("children") && node.get("children").has("attached")) {
            List<XMindNode> children = new ArrayList<>();
            node.get("children").get("attached").forEach(child ->
                children.add(processNode(child, sheetTitle))
            );
            processedNode.setChildren(children);
        }

        return processedNode;
    }
}