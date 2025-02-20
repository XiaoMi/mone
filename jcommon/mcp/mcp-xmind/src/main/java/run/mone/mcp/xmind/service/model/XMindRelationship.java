package run.mone.mcp.xmind.service.model;

import lombok.Data;

@Data
public class XMindRelationship {
    private String id;
    private String end1Id;
    private String end2Id;
    private String title;

    public XMindRelationship(String id, String end1Id, String end2Id, String title) {
        this.id = id;
        this.end1Id = end1Id;
        this.end2Id = end2Id;
        this.title = title;
    }
}