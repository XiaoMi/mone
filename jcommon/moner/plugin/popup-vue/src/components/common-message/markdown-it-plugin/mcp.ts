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

    // 检查是否包含<thinking>标签
    let text = state.src.slice(pos);
    if (
      !(
        text.includes("<thinking>") ||
        text.includes("<chat>") ||
        text.includes("<use_mcp_tool>") ||
        text.includes("<attempt_completion>") ||
        text.includes("<ask_followup_question>") ||
        text.includes("<mcp>") ||
        text.includes("<action ")
      )
    ) {
      return false;
    }

    if (silent) return true;

    console.log("mcp: \n", text);
    text = text.replace(/```[A-Za-z1-9]*/g, "");
    let html = "";
    let actionContent = ""; // 用于跟踪action标签的内容
    let isInAction = false; // 用于跟踪是否在action标签内

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
        } else if (name === "action") {
          isInAction = true;
          actionContent = ""; // 重置内容
          // 暂时不生成HTML,等待确认是否有内容
        } else {
          html += md.utils.escapeHtml(`<${name} ${Object.entries(attributes)
            .map(([key, value]) => `${key}="${value}"`)
            .join(" ")}>`);
        }
      },
      ontext(text) {
         /*
         * Fires whenever a section of text was processed.
         *
         * Note that this can fire at any point within text and you might
         * have to stitch together multiple pieces.
         */
        // console.log("-->", text);
        if (isInAction) {
          actionContent += text.trim();
        } else {
          html += md.utils.escapeHtml(text);
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
        } else if (tagname === "action") {
          isInAction = false;
          // 只有在有内容时才生成action块的HTML
          if (actionContent.trim()) {
            html += `
              <div class="action-block">
                <div class="action-header">
                  <i class="fa-solid fa-terminal"></i>
                  <span>动作</span>
                </div>
                <div class="action-content">
                  ${actionContent}
                </div>
              </div>`;
          }
        } else {
          if (!isImplied) {
            html += md.utils.escapeHtml(`</${tagname}>`);
          }
        }
      },
    }, {
      xmlMode: true,
    });
    parser.write(
      text
    );
    parser.end();

    let token = state.push("html_block", "", 0);
    token.content = html;
    token.map = [startLine, endLine + 1];

    state.line = endLine + 1;

    return true;
  }

  md.block.ruler.before("fence", "mcp", parseMcpBlock, {
    alt: ["paragraph", "reference", "blockquote"],
  });
}
