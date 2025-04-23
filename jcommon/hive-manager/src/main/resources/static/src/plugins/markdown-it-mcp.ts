import MarkdownIt from "markdown-it";
import * as htmlparser2 from "htmlparser2";

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
    const pos = state.bMarks[startLine] + state.tShift[startLine];

    // 检查当前行是否包含<thinking>等标签
    const currentLine = state.src.slice(pos, state.eMarks[startLine]);
    if (
      !(
        currentLine.includes("<terminal>") ||
        currentLine.includes("<use_mcp_tool>") ||
        currentLine.includes("<thinking>") ||
        currentLine.includes("<chat>") ||
        currentLine.includes("<mcp_tool>") ||
        currentLine.includes("<attempt_completion>") ||
        currentLine.includes("<ask_followup_question>") ||
        currentLine.includes("<mcp>") ||
        currentLine.includes("<step>")
      )
    ) {
      return false;
    }

    // 找到对应的结束标签
    let line = startLine;
    let mcpContent = '';
    let tagFound = false;
    
    while (line < endLine) {
      const lineText = state.src.slice(
        state.bMarks[line] + state.tShift[line],
        state.eMarks[line]
      );
      
      if (lineText.includes("</terminal>") ||
          lineText.includes("</use_mcp_tool>") ||
          lineText.includes("</thinking>") ||
          lineText.includes("</chat>") ||
          lineText.includes("</mcp_tool>") ||
          lineText.includes("</attempt_completion>") ||
          lineText.includes("</ask_followup_question>") ||
          lineText.includes("</mcp>") ||
          lineText.includes("</step>")) {
        tagFound = true;
        mcpContent += lineText + '\n';
        break;
      }
      
      mcpContent += lineText + '\n';
      line++;
    }

    if (!tagFound) {
      return false;
    }

    if (silent) return true;

    let html = "";
    let accumulatedText = ""; // 添加文本累积变量
    let startCodeBlock = false;
    const parser = new htmlparser2.Parser({
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
        } else {
          if (startCodeBlock) {
            accumulatedText += `<${name}>`
          } else {
            html += md.utils.escapeHtml(
              `<${name} ${Object.entries(attributes)
                .map(([key, value]) => `${key}="${value}"`)
                .join(" ")}>`
            );
          }
        }
      },
      ontext(text) {
        /*
         * Fires whenever a section of text was processed.
         *
         * Note that this can fire at any point within text and you might
         * have to stitch together multiple pieces.
         */
        const lines = text.split('\n');    
        for (const line of lines) {
          if (line.includes('```')) {
            if (startCodeBlock) {
              startCodeBlock = false;
              html += md.render(accumulatedText);
            } else {
              startCodeBlock = true;
              accumulatedText += `${line}\n`;
            }
          } else if (startCodeBlock){
            accumulatedText += `${line}\n`;
          } else {
            html += md.utils.escapeHtml(`${line}\n`);
          }
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
        } else {
          if (!isImplied) {
            html += md.utils.escapeHtml(`</${tagname}>`);
          }
        }
      },
    },
    {
      xmlMode: true,
    });
    
    parser.write(mcpContent);
    parser.end();

    let token = state.push("html_block", "", 0);
    token.content = html;
    token.map = [startLine, line + 1];

    state.line = line + 1;
    return true;
  }

  md.block.ruler.before("html_block", "mcp", parseMcpBlock, {
    alt: ["paragraph", "reference", "blockquote"],
  });
}
