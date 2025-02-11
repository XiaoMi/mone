import MarkdownIt from 'markdown-it'

// 辅助函数：提取XML标签内容
function extractXmlContent(text: string, tagName: string): string {
  // 修改正则表达式以更精确地匹配XML标签
  const regex = new RegExp(`<${tagName}>([\\s\\S]*?)<\\/${tagName}>`, 'g')
  const matches = text.matchAll(regex)
  for (const match of matches) {
    if (match[1]) {
      // 移除开头和结尾的空白字符，但保留内部的格式
      return match[1].replace(/^\s+|\s+$/g, '')
    }
  }
  return ''
}

// 添加新的辅助函数来检查和补全未闭合的标签
function tryAutoCloseTag(content: string[], tagName: string): string[] {
  const lastLine = content[content.length - 1] || '';
  if (!lastLine.includes(`</${tagName}>`)) {
    // 如果最后一行不包含闭合标签，添加闭合标签
    content.push(`</${tagName}>`);
  }
  return content;
}

// 修改 parseStreamBlock 函数
function parseStreamBlock(state: any, startLine: number, endLine: number, tagName: string): {
  content: string[];
  endLine: number;
  found: boolean;
  preText: string;
} {
  let line = startLine;
  let content: string[] = [];
  let startContent = false;
  let foundEnd = false;
  let preText = '';
  let maxLines = 100; // 设置最大行数限制，防止无限循环
  let currentLine = 0;

  while (line < endLine && currentLine < maxLines) {
    const pos = state.bMarks[line] + state.tShift[line];
    const max = state.eMarks[line];
    const lineText = state.src.slice(pos, max);

    if (!startContent) {
      if (lineText.includes(`<${tagName}>`)) {
        const parts = lineText.split(`<${tagName}>`);
        preText += parts[0];
        // 处理开始标签后的内容
        if (parts[1]) {
          if (parts[1].includes(`</${tagName}>`)) {
            // 如果同一行包含闭合标签
            const endParts = parts[1].split(`</${tagName}>`);
            content.push(endParts[0]);
            foundEnd = true;
            break;
          } else {
            content.push(parts[1]);
          }
        }
        startContent = true;
      } else {
        preText += lineText.trim() ? `<p>${state.md.utils.escapeHtml(lineText)}</p>` : '';
      }
    } else {
      if (lineText.includes(`</${tagName}>`)) {
        content.push(lineText.split(`</${tagName}>`)[0]);
        foundEnd = true;
        break;
      }
      content.push(lineText);
    }
    line++;
    currentLine++;
  }

  // 如果没有找到闭合标签，尝试自动补全
  if (!foundEnd && startContent) {
    content = tryAutoCloseTag(content, tagName);
    foundEnd = true;
  }

  // 清理内容中可能存在的多余标签
  content = content.map(line => {
    return line
      .replace(new RegExp(`<${tagName}>`, 'g'), '') // 移除多余的开始标签
      .replace(new RegExp(`</${tagName}>`, 'g'), ''); // 移除多余的结束标签
  });

  return {
    content,
    preText,
    endLine: line,
    found: startContent // 只要找到开始标签就认为是有效的
  };
}

export function markdownItMcp(md: MarkdownIt) {
  if (!md.block || !md.block.ruler) {
    console.warn('markdown-it-mcp: block ruler not found');
    return;
  }

  // 修改 parseThinkingBlock 函数
  function parseThinkingBlock(state: any, startLine: number, endLine: number, silent: boolean): boolean {
    const pos = state.bMarks[startLine] + state.tShift[startLine];

    // 检查是否包含<thinking>标签
    if (!state.src.slice(pos).includes('<thinking>')) {
      return false;
    }

    if (silent) return true;

    const { content, preText, endLine: newEndLine, found } = parseStreamBlock(state, startLine, endLine, 'thinking');

    if (!found) {
      return false;
    }

    let html = `
      ${preText}
      <div class="thinking-block">
        <div class="thinking-header">
          <i class="fa-solid fa-lightbulb"></i>
          <span>思考过程</span>
        </div>
        <div class="thinking-content is-active">
          ${md.render(content.join('\n'))}
        </div>
      </div>
    `;

    let token = state.push('html_block', '', 0);
    token.content = html;
    token.map = [startLine, newEndLine + 1];

    state.line = newEndLine + 1;
    return true;
  }

  // 修改 parseMcpToolBlock 函数
  function parseMcpToolBlock(state: any, startLine: number, endLine: number, silent: boolean): boolean {
    const pos = state.bMarks[startLine] + state.tShift[startLine];

    // 检查是否包含<use_mcp_tool>标签
    if (!state.src.slice(pos).includes('<use_mcp_tool>')) {
      return false;
    }

    if (silent) return true;

    const { content: mcpContent, preText, endLine: newEndLine, found } = parseStreamBlock(state, startLine, endLine, 'use_mcp_tool');

    if (!found) {
      return false;
    }

    const fullContent = mcpContent.join('\n');
    let serverName = '';
    let toolName = '';
    let argsContent = '';

    // 使用字符串流解析处理子标签
    const content = fullContent;
    let currentTag: 'server_name' | 'tool_name' | 'arguments' | null = null;
    let currentContent = '';
    let i = 0;

    while (i < content.length) {
      // 检查开始标签
      if (content.slice(i).startsWith('<server_name>')) {
        currentTag = 'server_name';
        i += '<server_name>'.length;
        continue;
      } else if (content.slice(i).startsWith('<tool_name>')) {
        currentTag = 'tool_name';
        i += '<tool_name>'.length;
        continue;
      } else if (content.slice(i).startsWith('<arguments>')) {
        currentTag = 'arguments';
        i += '<arguments>'.length;
        continue;
      }

      // 检查结束标签
      if (content.slice(i).startsWith('</server_name>')) {
        if (currentTag === 'server_name') {
          serverName = currentContent.trim();
        }
        currentContent = '';
        currentTag = null;
        i += '</server_name>'.length;
        continue;
      } else if (content.slice(i).startsWith('</tool_name>')) {
        if (currentTag === 'tool_name') {
          toolName = currentContent.trim();
        }
        currentContent = '';
        currentTag = null;
        i += '</tool_name>'.length;
        continue;
      } else if (content.slice(i).startsWith('</arguments>')) {
        if (currentTag === 'arguments') {
          argsContent = currentContent;
        }
        currentContent = '';
        currentTag = null;
        i += '</arguments>'.length;
        continue;
      }

      // 收集当前标签内的内容
      if (currentTag) {
        currentContent += content[i];
      }
      i++;
    }

    // 处理未闭合的标签
    if (currentTag) {
      if (currentTag === 'server_name') {
        serverName = currentContent.trim();
      } else if (currentTag === 'tool_name') {
        toolName = currentContent.trim();
      } else if (currentTag === 'arguments') {
        argsContent = currentContent;
      }
    }

    let html = `
      ${preText}
      <div class="mcp-tool-block">
        <div class="mcp-tool-header">
          <i class="fa-solid fa-robot"></i>
          <span class="server-name">${md.utils.escapeHtml(serverName || '')}</span>
          <i class="fa-solid fa-wrench"></i>
          <span class="tool-name">${md.utils.escapeHtml(toolName || '')}</span>
        </div>
        <div class="mcp-tool-content">
          <div class="arguments">
            <pre><code>${argsContent ? md.utils.escapeHtml(argsContent) : ''}</code></pre>
          </div>
        </div>
      </div>
    `;

    let token = state.push('html_block', '', 0);
    token.content = html;
    token.map = [startLine, newEndLine + 1];

    state.line = newEndLine + 1;
    return true;
  }

  // 修改 parseAttemptCompletionBlock 函数
  function parseAttemptCompletionBlock(state: any, startLine: number, endLine: number, silent: boolean): boolean {
    const pos = state.bMarks[startLine] + state.tShift[startLine];

    if (!state.src.slice(pos).includes('<attempt_completion>')) {
      return false;
    }

    if (silent) return true;

    const { content: completionContent, preText, endLine: newEndLine, found } = parseStreamBlock(state, startLine, endLine, 'attempt_completion');

    if (!found) {
      return false;
    }

    const fullContent = completionContent.join('\n');
    let result = '';
    let command = '';

    // 使用流式解析处理子标签
    const lines = fullContent.split('\n');
    let currentTag: 'result' | 'command' | null = null;
    let currentContent: string[] = [];

    for (const line of lines) {
      if (line.includes('<result>')) {
        currentTag = 'result';
        continue;
      } else if (line.includes('<command>')) {
        currentTag = 'command';
        continue;
      } else if (line.includes('</result>') || line.includes('</command>')) {
        // 当遇到结束标签时，保存当前内容
        if (currentTag === 'result') {
          result = currentContent.join('\n');
        } else if (currentTag === 'command') {
          command = currentContent.join('\n');
        }
        currentContent = [];
        currentTag = null;
        continue;
      }

      // 收集当前标签内的内容
      if (currentTag && line.trim()) {
        currentContent.push(line);
      }
    }

    // 如果最后一个标签没有正确关闭，保存已收集的内容
    if (currentTag === 'result' && currentContent.length > 0) {
      result = currentContent.join('\n');
    } else if (currentTag === 'command' && currentContent.length > 0) {
      command = currentContent.join('\n');
    }

    // 如果没有找到任何标签，将所有非空内容作为 result
    if (!result && !command) {
      result = completionContent.filter(line => line.trim()).join('\n');
    }

    let html = `
      ${preText}
      <div class="attempt-completion-block">
        <div class="completion-result">
          <div class="result-header">
            <i class="fa-solid fa-circle-check"></i>
            <span>执行完成</span>
          </div>
          <div class="result-content">
            ${md.render(result)}
          </div>
        </div>
        ${command ? `
          <div class="completion-command">
            <div class="command-header">
              <i class="fa-solid fa-terminal"></i>
              <span>命令</span>
            </div>
            <div class="command-content">
              <pre><code>${md.utils.escapeHtml(command)}</code></pre>
            </div>
          </div>
        ` : ''}
      </div>
    `;

    let token = state.push('html_block', '', 0);
    token.content = html;
    token.map = [startLine, newEndLine + 1];

    state.line = newEndLine + 1;
    return true;
  }

  // 添加新的解析函数处理 ask_followup_question 标签
  function parseAskFollowupQuestionBlock(state: any, startLine: number, endLine: number, silent: boolean): boolean {
    const pos = state.bMarks[startLine] + state.tShift[startLine];

    if (!state.src.slice(pos).includes('<ask_followup_question>')) {
      return false;
    }

    if (silent) return true;

    const { content: questionContent, preText, endLine: newEndLine, found } = parseStreamBlock(state, startLine, endLine, 'ask_followup_question');

    if (!found) {
      return false;
    }

    const fullContent = questionContent.join('\n');
    // 提取 question 标签内容
    let question = extractXmlContent(fullContent, 'question');

    // 如果没有找到 question 标签，使用整个内容
    if (!question) {
      question = fullContent;
    }

    let html = `
      ${preText}
      <div class="ask-followup-block">
        <div class="followup-header">
          <i class="fa-solid fa-question"></i>
          <span>追问</span>
        </div>
        <div class="followup-content">
          ${md.render(question)}
        </div>
      </div>
    `;

    let token = state.push('html_block', '', 0);
    token.content = html;
    token.map = [startLine, newEndLine + 1];

    state.line = newEndLine + 1;
    return true;
  }

  md.block.ruler.before('fence', 'thinking', parseThinkingBlock)
  md.block.ruler.before('fence', 'mcp_tool', parseMcpToolBlock)
  md.block.ruler.before('fence', 'attempt_completion', parseAttemptCompletionBlock)
  md.block.ruler.before('fence', 'ask_followup_question', parseAskFollowupQuestionBlock)
}
