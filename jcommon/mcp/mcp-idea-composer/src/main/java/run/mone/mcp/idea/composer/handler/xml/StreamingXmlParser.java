package run.mone.mcp.idea.composer.handler.xml;

import java.util.ArrayDeque;
import java.util.Deque;

import static run.mone.mcp.idea.composer.handler.xml.CustomStreamTag.ACTION;
import static run.mone.mcp.idea.composer.handler.xml.CustomStreamTag.ARTIFACT;


public class StreamingXmlParser {
    private final XmlParserCallback callback;
    private final StringBuilder tagBuffer;
    private final StringBuilder contentBuffer;
    private final Deque<String> elementStack;
    private ParserState state;

//    public StringBuilder sss = new StringBuilder();

    private enum ParserState {
        CONTENT,    // 正在处理内容
        TAG_START,  // 可能是标签开始
        IN_TAG      // 确认在标签内
    }

    public StreamingXmlParser(XmlParserCallback callback) {
        this.callback = callback;
        this.tagBuffer = new StringBuilder();
        this.contentBuffer = new StringBuilder();
        this.elementStack = new ArrayDeque<>();
        this.state = ParserState.CONTENT;
    }

    public void append(String text) {
//        sss.append(text);
        if (text == null || text.isEmpty()) {
            return;
        }

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            processChar(c, i > 0 ? text.charAt(i - 1) : '\0',
                    i < text.length() - 1 ? text.charAt(i + 1) : '\0');
        }
    }

    private void processChar(char current, char previous, char next) {
        switch (state) {
            case CONTENT:
                if (current == '<' && isStartOfTag(next)) {
                    state = ParserState.TAG_START;
                    flushContent();
                    tagBuffer.append(current);
                } else {
                    contentBuffer.append(current);
                }
                break;

            case TAG_START:
                tagBuffer.append(current);
                if (isValidTagChar(current)) {
                    state = ParserState.IN_TAG;
                } else {
                    // 不是有效的标签，回退到内容处理
                    state = ParserState.CONTENT;
                    contentBuffer.append(tagBuffer);
                    tagBuffer.setLength(0);
                }
                break;

            case IN_TAG:
                tagBuffer.append(current);
                if (current == '>') {
                    processTag(tagBuffer.toString());
                    tagBuffer.setLength(0);
                    state = ParserState.CONTENT;
                }
                break;
        }
    }

    private boolean isStartOfTag(char next) {
        // 检查是否是有效的标签开始
        // boltArtifact 或 boltAction 或 结束标签
        return next == 'b' || next == '/';
    }

    private boolean isValidTagChar(char c) {
        // 检查是否是有效的标签字符
        return Character.isLetterOrDigit(c) || c == '/' || c == '-' || c == '_' || c == '"'
                || c == '=' || c == ' ' || c == '\t' || c == '\n' || c == '\r';
    }

    private void flushContent() {
        if (!contentBuffer.isEmpty()) {
            String content = contentBuffer.toString();
            // 逐字符发送内容
            for (char c : content.toCharArray()) {
                callback.onContentChar(c);
            }
            contentBuffer.setLength(0);
        }
    }

    private void processTag(String tag) {
        try {
            if (tag.startsWith("</")) {
                processEndTag(tag);
            } else {
                processStartTag(tag);
            }
        } catch (Exception e) {
            // 解析错误时，将标签内容作为普通文本处理
            for (char c : tag.toCharArray()) {
                callback.onContentChar(c);
            }
        }
    }

    private void processStartTag(String tag) {
        String tagContent = tag.substring(1, tag.length() - 1).trim();
        String[] parts = tagContent.split("\\s+", 2);
        String tagName = parts[0];

        if (!isValidTagName(tagName)) {
            // 无效的标签名，作为内容处理
            for (char c : tag.toCharArray()) {
                callback.onContentChar(c);
            }
            return;
        }

        CustomStreamTag customStreamTag = CustomStreamTag.getTagByName(tagName);
        switch (customStreamTag) {
            case ARTIFACT:
                elementStack.push(ARTIFACT.getTagName());
                String id = extractAttribute(tag, "id");
                String title = extractAttribute(tag, "title");
                callback.onArtifactStart(id, title);
                break;

            case ACTION:
                elementStack.push(ACTION.getTagName());
                String type = extractAttribute(tag, "type");
                String subType = extractAttribute(tag, "subType");
                String filePath = extractAttribute(tag, "filePath");
                callback.onActionStart(type, subType, filePath);
                break;

            default:
                // 未知标签作为内容处理
                for (char c : tag.toCharArray()) {
                    callback.onContentChar(c);
                }
        }
    }

    private boolean isValidTagName(String tagName) {
        return CustomStreamTag.isValidTagName(tagName);
    }

    private void processEndTag(String tag) {
        String tagName = tag.substring(2, tag.length() - 1).trim();

        if (!elementStack.isEmpty() && elementStack.peek().equals(tagName)) {
            elementStack.pop();
            CustomStreamTag customTag = CustomStreamTag.getTagByName(tagName);
            switch (customTag) {
                case ARTIFACT:
                    callback.onArtifactEnd();
                    break;
                case ACTION:
                    callback.onActionEnd();
                    break;
            }
        } else {
            // 标签不匹配，作为内容处理
            for (char c : tag.toCharArray()) {
                callback.onContentChar(c);
            }
        }
    }

    private String extractAttribute(String tag, String attrName) {
        String search = attrName + "=\"";
        int start = tag.indexOf(search);
        if (start != -1) {
            start += search.length();
            int end = tag.indexOf("\"", start);
            if (end != -1) {
                return tag.substring(start, end);
            }
        }
        return null;
    }

    // 用于测试和调试
    public boolean isInValidState() {
        return elementStack.isEmpty() && state == ParserState.CONTENT;
    }

}