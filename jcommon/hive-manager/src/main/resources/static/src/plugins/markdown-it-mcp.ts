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
        mcpContent.includes("<download_file") ||
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
        mcpContent.includes("<execute>") ||
        mcpContent.includes("<execute_command>") ||
        mcpContent.includes("<read_file>") ||
        mcpContent.includes("<replace_in_file>") ||
        mcpContent.includes("<search_files>") ||
        mcpContent.includes("<write_to_file>") ||
        mcpContent.includes("<list_files>") ||
        mcpContent.includes("<tool_result>")
      )
    ) {
      return false;
    }

    console.log("mcpContent", mcpContent);

    let html = "";
    let isDownloadFile = false;
    const tagStack: string[] = []; // 标签栈，用于跟踪当前正在处理的标签

    // 辅助函数：获取当前标签栈顶的标签
    const getCurrentTag = () => tagStack[tagStack.length - 1];
    // // 辅助函数：获取父级标签
    // const getParentTag = () => tagStack[tagStack.length - 2];
    // // 辅助函数：检查是否在指定标签内
    // const isInsideTag = (tagName: string) => tagStack.includes(tagName);

    const parser = new SimpleHtmlParser({
      onopentag(name, attributes) {
        console.log("onopentag", name, attributes);
        tagStack.push(name); // 将标签推入栈中
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
        } else if (name === "read_file") {
          html += `
            <div class="read-file-block">
              <div class="read-file-header">
                <i class="fa-solid fa-file-text"></i>
                <span>读取文件</span>
              </div>
              <div class="read-file-content">`;
        } else if (name === "replace_in_file") {
          html += `
            <div class="replace-in-file-block">
              <div class="replace-in-file-header">
                <i class="fa-solid fa-edit"></i>
                <span>文件替换</span>
              </div>
              <div class="replace-in-file-content">`;
        } else if (name === "diff") {
          html += `<div class="diff-section">
            <div class="diff-header">
              <i class="fa-solid fa-code-compare"></i>
              <span>代码差异</span>
            </div>
            <div class="diff-content">
              <pre><code>`;
        } else if (name === "search_files") {
          html += `
            <div class="search-files-block">
              <div class="search-files-header">
                <i class="fa-solid fa-search"></i>
                <span>文件搜索</span>
              </div>
              <div class="search-files-content">`;
        } else if (name === "write_to_file") {
          html += `
            <div class="write-to-file-block">
              <div class="write-to-file-header">
                <i class="fa-solid fa-pen-to-square"></i>
                <span>文件写入</span>
              </div>
              <div class="write-to-file-content">`;
        } else if (name === "regex") {
          html += `<div class="regex-section">
            <span class="regex-label">正则表达式：</span>
            <span class="regex-value">`;
        } else if (name === "file_pattern") {
          html += `<div class="file-pattern-section">
            <span class="file-pattern-label">文件模式：</span>
            <span class="file-pattern-value">`;
        } else if (name === "list_files") {
          html += `
            <div class="list-files-block">
              <div class="list-files-header">
                <i class="fa-solid fa-folder"></i>
                <span>文件列表</span>
              </div>
              <div class="list-files-content">`;
        } else if (name === "recursive") {
          html += `<div class="recursive-section">
            <span class="recursive-label">递归：</span>
            <span class="recursive-value">`;
        } else if (name === "execute_command") {
          html += `
            <div class="execute-command-block">
              <div class="execute-command-header">
                <i class="fa-solid fa-play-circle"></i>
                <span>执行命令</span>
              </div>
              <div class="execute-command-content">`;
        } else if (name === "requires_approval") {
          html += `<div class="requires-approval-section">
            <span class="approval-label">需要确认：</span>
            <span class="approval-value">`;
        } else if (name === "task_progress") {
          html += `<div class="task-progress-section">
            <div class="task-progress-header">
              <i class="fa-solid fa-tasks"></i>
              <span>任务进度</span>
            </div>
            <div class="task-progress-content">`;
        } else if (name === "tool_result") {
          html += `
            <div class="tool-result-block">
              <div class="tool-result-header">
                <i class="fa-solid fa-code"></i>
                <span>工具结果</span>
              </div>
              <div class="tool-result-content">`;
        } else if (name === "operation" || name === "path" || name === "content" || name === "r" || name === "working_directory" || name === "timeout") {
          html += `<div class="${name}-section">`;
        } else if (name === "download_file") {
          isDownloadFile = true;
          html += `<div class="file-url-section">
            <div class="file-url-header">
              <i class="fa-solid fa-file"></i>
              <span>文件下载</span>
            </div>
            <div class="file-url-content">
              <a class="file-url-link" href="javascript:;" data-name="${attributes.fileName}" data-url="${attributes.fileUrl}">${attributes.fileName}</a>`;
        } else {
          console.log("unhandled tag", name,attributes);
            html += md.utils.escapeHtml(
              `<${name} ${Object.entries(attributes)
                .map(([key, value]) => `${key}="${value}"`)
                .join(" ")}>`
          );
        }
      },
      ontext(text) {
        const tagName = getCurrentTag();
        if (tagName === "tool_result") {
          try {
            // 验证是否为有效的 JSON
            JSON.parse(text);
            // 生成一个特殊的标记，包含原始 JSON 数据
            html += `<div class="vue3-json-viewer-placeholder" data-json="${md.utils.escapeHtml(text).replace(/"/g, '&quot;')}"></div>`;
          } catch (e) {
            // 不是合法 JSON，作为普通文本处理
            console.warn("tool_result 解析 JSON 失败，作为普通文本处理", e);
            html += `<pre><code class="language-json">${md.utils.escapeHtml(text)}</code></pre>`;
          }
          return;
        }
        if (isDownloadFile) {
          return;
        }
        text = text.replace(/```(\w*)\n/g, '').replace(/\n```/g, '');
        html += md.utils.escapeHtml(text);
        // 匹配所有 voice 类型 JSON
        // const regex = /({[^{}]*"result"\s*:\s*"([^"]+)"[^{}]*"toolMsgType"\s*:\s*"voice"[^{}]*})/g;
        // let lastIndex = 0;
        // let match;
        // while ((match = regex.exec(text)) !== null) {
        //   // 输出前面的普通文本
        //   if (match.index > lastIndex) {
        //     const normalText = text.slice(lastIndex, match.index);
        //     html += md.utils.escapeHtml(normalText);
        //   }
        //   // 尝试解析 JSON
        //   try {
        //     const obj = JSON.parse(match[1]);
        //     if (obj && obj.result && obj.toolMsgType === "voice") {
        //       html += `<audio controls src="data:audio/wav;base64,${obj.result}"></audio>`;
        //     } else {
        //       html += md.utils.escapeHtml(match[0]);
        //     }
        //   } catch (e) {
        //     html += md.utils.escapeHtml(match[0]);
        //   }
        //   lastIndex = regex.lastIndex;
        // }
        // 剩余部分
        // if (lastIndex < text.length) {
        //  html += md.utils.escapeHtml(text.slice(lastIndex));
        // }
      },
      onclosetag(tagname) {
        /*
         * Fires when a tag is closed.
         *
         * You can rely on this event only firing when you have received an
         * equivalent opening tag before. Closing tags without corresponding
         * opening tags will be ignored.
         */
        const poppedTag = tagStack.pop(); // 从栈中弹出标签
        console.log("onclosetag", tagname, "popped:", poppedTag, "stack:", tagStack);

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
        } else if (tagname === "read_file") {
          html += `</div></div>`;
        } else if (tagname === "replace_in_file") {
          html += `</div></div>`;
        } else if (tagname === "diff") {
          html += `</code></pre></div></div>`;
        } else if (tagname === "search_files") {
          html += `</div></div>`;
        } else if (tagname === "write_to_file") {
          html += `</div></div>`;
        } else if (tagname === "regex") {
          html += `</span></div>`;
        } else if (tagname === "file_pattern") {
          html += `</span></div>`;
        } else if (tagname === "list_files") {
          html += `</div></div>`;
        } else if (tagname === "recursive") {
          html += `</span></div>`;
        } else if (tagname === "execute_command") {
          html += `</div></div>`;
        } else if (tagname === "requires_approval") {
          html += `</span></div>`;
        } else if (tagname === "task_progress") {
          html += `</div></div>`;
        } else if (tagname === "tool_result") {
          html += `</div></div>`;
        } else if (tagname === "operation" || tagname === "path" || tagname === "content" || tagname === "r" || tagname === "working_directory" || tagname === "timeout") {
          html += `</div>`;
        } else if (tagname === "download_file") {
          isDownloadFile = false;
          html += `</div></div>`;
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

    console.log("Generated HTML:", html);

    const token = state.push("html_block", "", 0);
    token.content = html;
    token.map = [startLine, endLine];

    state.line = endLine;
    return true;
  }

  md.block.ruler.before("html_block", "mcp", parseMcpBlock, {
    alt: ["paragraph", "reference", "blockquote"],
  });
}
