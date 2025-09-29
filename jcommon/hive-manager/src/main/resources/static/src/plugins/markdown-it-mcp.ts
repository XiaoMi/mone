import MarkdownIt from "markdown-it";
// import * as htmlparser2 from "htmlparser2";
import { SimpleHtmlParser } from "./simple-html-parser";

// 全局存储已存在的 PID 组件内容（只存储内容，不直接渲染）
const existingPidComponents = new Map<string, string[]>();



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
        mcpContent.includes("<list_code_definition_names>") ||
        mcpContent.includes("<tool_result>") ||
        mcpContent.includes("<pid>") ||
        mcpContent.includes("<terminal_append>")
      )
    ) {
      return false;
    }

    console.log("mcpContent", mcpContent);

    let html = "";
    let isDownloadFile = false;
    const tagStack: string[] = []; // 标签栈，用于跟踪当前正在处理的标签
    const processedPidsInThisParse = new Set<string>(); // 跟踪当前解析中已处理的PID

    // 用于 terminal_append 功能的变量
    let currentPid = "";
    let currentContent = "";

    // 辅助函数：获取当前标签栈顶的标签
    const getCurrentTag = () => tagStack[tagStack.length - 1];
    let toolResult: string = ""

    const parser = new SimpleHtmlParser({
      onopentag(name, attributes) {
        // console.log("onopentag", name, attributes);
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
        } else if (name === "memory") {
          html += `
          <div class="thinking-block">
            <div class="thinking-header">
              <i class="fa-solid fa-lightbulb"></i>
              <span>记忆过程</span>
            </div>
            <div class="thinking-content is-active">`;
        } else if (name === "message") {
        } else if (name === "action") {
          html+= `<div style="display:none;">`;
        } else if (name === "query") {
          html+= `<div>`;
        } else if (name === "metadata") {
          html+= `<div style="display:none;">`;
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
        } else if (name === "list_code_definition_names") {
          html += `
            <div class="list-code-definition-names-block">
              <div class="list-code-definition-names-header">
                <i class="fa-solid fa-code"></i>
                <span>代码定义列表</span>
              </div>
              <div class="list-code-definition-names-content">
                <div class="path-section">
                  <pre><code>`;
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
          toolResult = "";
          html += `
            <div class="tool-result-block">
              <div class="tool-result-header">
                <i class="fa-solid fa-code"></i>
                <span>工具结果</span>
                <i class="toggle-icon fa-solid fa-chevron-down"></i>
              </div>
              <div class="tool-result-content">`;
        } else if (name === "pid") {
          html += `<span class="pid-buttons-container process-running" data-pid="" title="进程控制面板">`;
        } else if (name === "terminal_append") {
          // terminal_append 标签开始，准备处理进程追加
          html += `<!-- terminal_append_start -->`;
        } else if (name === "process_pid") {
          // process_pid 标签，暂时不输出内容，等待获取 PID 值
        } else if (name === "process_content") {
          // process_content 标签，暂时不输出内容，等待获取内容
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
          html += `${md.utils.escapeHtml(text)}`
          // try {
          //   // 验证是否为有效的 JSON
          //   console.log("tool_result text:", text);
          //   const parsedJson = JSON.parse(text);
          //   // 直接显示格式化的 JSON 代码块
          //   html += `${md.utils.escapeHtml(JSON.stringify(parsedJson, null, 2))}`;
          // } catch (e) {
          //   // 不是合法 JSON，作为普通文本处理
          //   console.warn("tool_result 解析 JSON 失败，作为普通文本处理", e);
          //   html += `${md.utils.escapeHtml(text)}`;
          // }
          return;
        } else if (tagName === "pid") {
          // 处理 pid 标签内容，生成三个按钮
          const pidValue = text.trim();
          html = html.replace('data-pid=""', `data-pid="${md.utils.escapeHtml(pidValue)}"`);
          html += `
            <button class="pid-kill-button" data-pid="${md.utils.escapeHtml(pidValue)}" data-action="kill" title="终止进程 PID: ${md.utils.escapeHtml(pidValue)}">
              <i class="fa-solid fa-power-off"></i>
              <span>杀死进程</span>
              <small>${md.utils.escapeHtml(pidValue)}</small>
            </button>
            <button class="pid-detach-button" data-pid="${md.utils.escapeHtml(pidValue)}" data-action="detach" title="将进程 PID: ${md.utils.escapeHtml(pidValue)} 转为后台运行">
              <i class="fa-solid fa-arrow-up-right-from-square"></i>
              <span>后台运行</span>
              <small>${md.utils.escapeHtml(pidValue)}</small>
            </button>
            <button class="pid-view-logs-button" data-pid="${md.utils.escapeHtml(pidValue)}" onclick="showPidLogs('${md.utils.escapeHtml(pidValue)}')" title="查看进程 PID: ${md.utils.escapeHtml(pidValue)} 的日志">
              <i class="fa-solid fa-file-lines"></i>
              <span>查看日志</span>
              <small>${md.utils.escapeHtml(pidValue)}</small>
            </button>
          `;
          return;
        } else if (tagName === "process_pid") {
          // 获取进程 PID
          currentPid = text.trim();
          return;
        } else if (tagName === "process_content") {
          // 获取进程内容
          currentContent = text.trim();
          return;
        } else if (tagName === "task_progress") {
          // 处理任务进度内容，转换为列表
          html += md.render(text);
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
        if (tagname !== poppedTag) {
          console.warn(`标签不匹配: expected </${poppedTag}>, but got </${tagname}>`);
        }
        // console.log("onclosetag", tagname, "popped:", poppedTag, "stack:", tagStack);

        if (tagname === "thinking") {
          html += `</div></div>`;
        } else if (tagname === "chat") {
          html += `</div></div>`;
        } else if (tagname === "memory") {
          html += `</div></div>`;
        } else if (tagname === "message") {
        } else if (tagname === "terminal") {
          html += `</div>`;
        } else if (tagname === "use_mcp_tool") {
          html += `</div>`;
        } else if (tagname === "action") {
           html += `</div>`;
        } else if (tagname === "metadata") {
           html += `</div>`;
        } else if (tagname === "query") {
           html += `</div>`;
        }else if (tagname === "server_name") {
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
        } else if (tagname === "list_code_definition_names") {
          html += `</code></pre></div></div></div>`;
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
        } else if (tagname === "pid") {
          html += `</span>`;
        } else if (tagname === "terminal_append") {
          // 处理 terminal_append 标签关闭 - 只存储内容，不直接渲染
          if (currentPid && currentContent) {
            const contentLines = currentContent.split('\n');

            // 检查当前解析过程中是否已经处理过这个PID
            const pidAlreadyProcessedInThisParse = processedPidsInThisParse.has(currentPid);

            if (existingPidComponents.has(currentPid)) {
              // 已存在组件，追加新内容
              const existingLines = existingPidComponents.get(currentPid) || [];
              const allLines = [...existingLines, ...contentLines];
              // 只保留最新的100条
              const limitedLines = allLines.slice(-100);
              // console.log('Updating existing PID:', currentPid, 'with lines:', limitedLines);
              existingPidComponents.set(currentPid, limitedLines);

              if (!pidAlreadyProcessedInThisParse) {
                // // 如果当前解析中还没有输出过这个PID的组件，输出完整组件
                const componentHtml = `
                <div class="terminal-process-block" data-pid="${md.utils.escapeHtml(currentPid)}">
                  <div class="terminal-process-header">
                    <i class="fa-solid fa-terminal"></i>
                    <span>进程 ${md.utils.escapeHtml(currentPid)} (${limitedLines.length}条日志)</span>
                    <button class="terminal-toggle-btn" type="button" title="查看日志">
                      <i class="fa-solid fa-eye"></i>
                      查看日志
                    </button>
                  </div>
                  <div class="terminal-process-content" style="display: none;">
                    <!-- 内容将在点击时动态加载 -->
                  </div>
                </div>`;
                html += componentHtml;
                processedPidsInThisParse.add(currentPid);
              } else {
                // 输出更新指令（不包含实际内容）
                html += `<div class="terminal-append-update" data-pid="${md.utils.escapeHtml(currentPid)}" data-new-lines="${contentLines.length}"></div>`;
              }
            } else {
              // 第一次遇到此PID，只输出占位组件
              const limitedLines = contentLines.slice(-100);
              console.log('Creating new PID:', currentPid, 'with lines:', limitedLines);
              existingPidComponents.set(currentPid, limitedLines);

              const componentHtml = `
              <div class="terminal-process-block" data-pid="${md.utils.escapeHtml(currentPid)}">
                <div class="terminal-process-header">
                  <i class="fa-solid fa-terminal"></i>
                  <span>进程 ${md.utils.escapeHtml(currentPid)} (${limitedLines.length}条日志)</span>
                  <button class="terminal-toggle-btn" type="button" title="查看日志">
                    <i class="fa-solid fa-eye"></i>
                    查看日志
                  </button>
                </div>
                <div class="terminal-process-content" style="display: none;">
                  <!-- 内容将在点击时动态加载 -->
                </div>
              </div>`;
              html += componentHtml;
              processedPidsInThisParse.add(currentPid);
            }

            // 重置变量
            currentPid = "";
            currentContent = "";
          }
        } else if (tagname === "process_pid") {
          // process_pid 标签关闭，不需要额外处理
        } else if (tagname === "process_content") {
          // process_content 标签关闭，不需要额外处理
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

    // 不再清空映射，保持内容在内存中
    // console.log("Generated HTML:", html);

    const token = state.push("html_block", "", 0);
    token.content = html;
    token.map = [startLine, endLine];

    state.line = endLine;
    return true;
  }

  md.block.ruler.before("html_block", "mcp", parseMcpBlock, {
    alt: ["paragraph", "reference", "blockquote"],
  });

  // 暴露获取PID内容的全局函数
  (window as any).getTerminalContent = (pid: string): string[] => {
    console.log('getTerminalContent called with pid:', pid);
    console.log('existingPidComponents:', existingPidComponents);
    const content = existingPidComponents.get(pid) || [];
    console.log('returning content:', content);
    return content;
  };

  (window as any).updateTerminalLineCount = (pid: string): number => {
    const count = existingPidComponents.get(pid)?.length || 0;
    console.log('updateTerminalLineCount for pid:', pid, 'count:', count);
    return count;
  };

  (window as any).showPidLogs = (pid: string) => {
    const lines = existingPidComponents.get(pid) || [];

    // 创建弹框
    const modal = document.createElement('div');
    modal.className = 'pid-log-modal';
    modal.innerHTML = `
      <div class="pid-log-modal-content">
        <div class="pid-log-modal-header">
          <h3>PID ${pid} 日志内容</h3>
          <button class="pid-log-modal-close" onclick="this.closest('.pid-log-modal').remove()">&times;</button>
        </div>
        <div class="pid-log-modal-body">
          <pre class="pid-log-content">${lines.length > 0 ? lines.join('\n') : '暂无日志内容'}</pre>
        </div>
        <div class="pid-log-modal-footer">
          <span class="pid-log-count">共 ${lines.length} 条日志</span>
          <button class="pid-log-modal-close-btn" onclick="this.closest('.pid-log-modal').remove()">关闭</button>
        </div>
      </div>
    `;

    // 添加点击背景关闭功能
    modal.addEventListener('click', (e) => {
      if (e.target === modal) {
        modal.remove();
      }
    });

    // 添加到页面
    document.body.appendChild(modal);

    console.log('Showing logs for PID:', pid, 'lines:', lines.length);
  };
}
