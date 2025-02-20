package run.mone.mcp.xmind.service.model;

import java.util.List;
import lombok.Data;

@Data
public class XMindNode {
    private String title;
    private String id;
    private List<XMindNode> children;
    private String taskStatus;
    private XMindNotes notes;
    private String href;
    private List<String> labels;
    private String sheetTitle;
    private List<XMindCallout> callouts;
    private List<XMindRelationship> relationships;
}