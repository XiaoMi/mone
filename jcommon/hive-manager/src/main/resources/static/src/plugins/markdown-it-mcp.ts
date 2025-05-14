import MarkdownIt from "markdown-it";
// import * as htmlparser2 from "htmlparser2";
import { SimpleHtmlParser } from "./simple-html-parser";

export function markdownItMcp(md: MarkdownIt) {
  if (!md.block || !md.block.ruler) {
    console.warn("markdown-it-mcp: block ruler not found");
    return;
  }

  function parseMcpBlock(
    state: any,
    startLine: number,
    endLine: number,
    silent: boolean
  ): boolean {
    if (silent) return true;

    const pos = state.bMarks[startLine] + state.tShift[startLine];
    // 获取完整内容
    const mcpContent = state.src.slice(pos);

    // 检查当前行是否包含<thinking>等标签
    if (
      !(
        mcpContent.includes("<terminal>") ||
        mcpContent.includes("<use_mcp_tool>") ||
        mcpContent.includes("<thinking>") ||
        mcpContent.includes("<chat>") ||
        mcpContent.includes("<mcp_tool>") ||
        mcpContent.includes("<attempt_completion>") ||
        mcpContent.includes("<ask_followup_question>") ||
        mcpContent.includes("<mcp>") ||
        mcpContent.includes("<step>") ||
        mcpContent.includes("<file>") ||
        mcpContent.includes("<file_operation>") ||
        mcpContent.includes("<execute>")
      )
    ) {
      return false;
    }

    let html = "";
    let accumulatedText = ""; // 添加文本累积变量
    let startCodeBlock = false;
    const parser = new SimpleHtmlParser({
      onopentag(name, attributes) {
        /*
         * This fires when a new tag is opened.
         *
         * If you don't need an aggregated `attributes` object,
         * have a look at the `onopentagname` and `onattribute` events.
         */
        if (name === "thinking") {
          html += `
          <div class="thinking-block">
            <div class="thinking-header">
              <i class="fa-solid fa-lightbulb"></i>
              <span>思考过程</span>
            </div>
            <div class="thinking-content is-active">`;
        } else if (name === "chat") {
          html += `
          <div class="thinking-block">
            <div class="thinking-header">
              <i class="fa-solid fa-lightbulb"></i>
              <span>聊天</span>
            </div>
            <div class="thinking-content is-active">`;
        } else if (name === "message") {
        } else if (name === "terminal") {
          html += `
            <div class="terminal">`;
        } else if (name === "use_mcp_tool") {
          html += `
            <div class="mcp-tool-block">`;
        } else if (name === "server_name") {
          html += `
            <div class="mcp-tool-header">
              <i class="fa-solid fa-robot"></i>
              <span class="server-name">`;
        } else if (name === "tool_name") {
          html += `
            <i class="fa-solid fa-wrench"></i>
            <span class="tool-name">`;
        } else if (name === "arguments") {
          html += `
            <div class="mcp-tool-content">
              <div class="arguments">
                <pre><code>`;
        } else if (name === "attempt_completion") {
          html += `
          <div class="attempt-completion-block">
            <div class="completion-result">
              <div class="result-header">
                <i class="fa-solid fa-circle-check"></i>
                <span>执行完成</span>
              </div>
            </div>`;
        } else if (name === "result") {
          html += `<div class="result-content">`;
        } else if (name === "command") {
          html += `<div class="completion-command">
            <div class="command-header">
              <i class="fa-solid fa-terminal"></i>
              <span>命令</span>
            </div>
            <div class="command-content">
              <pre><code>`;
        } else if (name === "ask_followup_question") {
          html += `
            <div class="ask-followup-block">
              <div class="followup-header">
                <i class="fa-solid fa-question"></i>
                <span>追问</span>
              </div>`;
        } else if (name === "question") {
          html += `<div class="followup-content">`;
        } else if (name === "step") {
          html += `<div class="step-block">
              <div class="step-header">
                <i class="fa-solid fa-step-forward"></i>
                <span>步骤</span>
              </div>
              <div class="step-content">`;
        } else if (name === "file") {
          html += `
            <div class="file-block">
              <div class="file-header">
                <i class="fa-solid fa-file"></i>
                <span>文件操作</span>
              </div>
              <div class="file-content">`;
        } else if (name === "file_operation") {
          html += `
            <div class="file-operation-block">
              <div class="file-operation-header">
                <i class="fa-solid fa-file-code"></i>
                <span>文件操作</span>
              </div>
              <div class="file-operation-content">`;
        } else if (name === "execute") {
          html += `
            <div class="execute-block">
              <div class="execute-header">
                <i class="fa-solid fa-terminal"></i>
                <span>执行命令</span>
              </div>
              <div class="execute-content">`;
        } else if (name === "operation" || name === "path" || name === "content" || name === "r" || name === "working_directory" || name === "timeout") {
          html += `<div class="${name}-section">`;
        } else {
          if (startCodeBlock) {
            accumulatedText += `<${name}>`
          } else {
            console.log("unhandled tag", name,attributes);
            html += md.utils.escapeHtml(
              `<${name} ${Object.entries(attributes)
                .map(([key, value]) => `${key}="${value}"`)
                .join(" ")}>`
            );
          }
        }
      },
      ontext(text) {
        text = text.replace(/```(\w*)\n/g, '').replace(/\n```/g, '');
        // 匹配所有 voice 类型 JSON
        const regex = /({[^{}]*"result"\s*:\s*"([^"]+)"[^{}]*"toolMsgType"\s*:\s*"voice"[^{}]*})/g;
        let lastIndex = 0;
        let match;
        while ((match = regex.exec(text)) !== null) {
          // 输出前面的普通文本
          if (match.index > lastIndex) {
            const normalText = text.slice(lastIndex, match.index);
            html += md.utils.escapeHtml(normalText);
          }
          // 尝试解析 JSON
          try {
            const obj = JSON.parse(match[1]);
            if (obj && obj.result && obj.toolMsgType === "voice") {
              html += `<audio controls src="data:audio/wav;base64,${obj.result}"></audio>`;
            } else {
              html += md.utils.escapeHtml(match[0]);
            }
          } catch (e) {
            html += md.utils.escapeHtml(match[0]);
          }
          lastIndex = regex.lastIndex;
        }
        // 剩余部分
        if (lastIndex < text.length) {
          html += md.utils.escapeHtml(text.slice(lastIndex));
        }
      },
      onclosetag(tagname, isImplied) {
        /*
         * Fires when a tag is closed.
         *
         * You can rely on this event only firing when you have received an
         * equivalent opening tag before. Closing tags without corresponding
         * opening tags will be ignored.
         */
        if (tagname === "thinking") {
          html += `</div></div>`;
        } else if (tagname === "chat") {
          html += `</div></div>`;
        } else if (tagname === "message") {
        } else if (tagname === "terminal") {
          html += `</div>`;
        } else if (tagname === "use_mcp_tool") {
          html += `</div>`;
        } else if (tagname === "server_name") {
          html += `</span>`;
        } else if (tagname === "tool_name") {
          html += `</span></div>`;
        } else if (tagname === "arguments") {
          html += `</code></pre></div></div>`;
        } else if (tagname === "attempt_completion") {
          html += `</div>`;
        } else if (tagname === "result") {
          html += `</div>`;
        } else if (tagname === "command") {
          html += `</code></pre></div></div>`;
        } else if (tagname === "ask_followup_question") {
          html += `</div>`;
        } else if (tagname === "question") {
          html += `</div>`;
        } else if (tagname === "step") {
          html += `</div></div>`;
        } else if (tagname === "file") {
          html += `</div></div>`;
        } else if (tagname === "file_operation") {
          html += `</div></div>`;
        } else if (tagname === "execute") {
          html += `</div></div>`;
        } else if (tagname === "operation" || tagname === "path" || tagname === "content" || tagname === "r" || tagname === "working_directory" || tagname === "timeout") {
          html += `</div>`;
        } else {
          // if (!isImplied) {
          //   html += md.utils.escapeHtml(`</${tagname}>`);
          // } else {
          //   console.log("unhandled tag", tagname);
          // }
        }
      },
    });

    parser.write(mcpContent);
    parser.end();

    let token = state.push("html_block", "", 0);
    token.content = html;
    token.map = [startLine, endLine];

    state.line = endLine;
    return true;
  }

  md.block.ruler.before("html_block", "mcp", parseMcpBlock, {
    alt: ["paragraph", "reference", "blockquote"],
  });
}
