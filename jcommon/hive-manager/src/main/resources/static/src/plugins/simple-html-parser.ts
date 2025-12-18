/**
 * 简单 HTML 解析器
 * 用于解析特定标签的 HTML 内容，支持流式解析和位置跟踪
 */

// ==================== 类型定义 ====================

type SupportedTag =
  | 'terminal'
  | 'use_mcp_tool'
  | 'thinking'
  | 'chat'
  | 'mcp_tool'
  | 'attempt_completion'
  | 'ask_followup_question'
  | 'message'
  | 'mcp'
  | 'command'
  | 'step'
  | 'result'
  | 'question'
  | 'server_name'
  | 'tool_name'
  | 'arguments'
  | 'file'
  | 'operation'
  | 'list_files'
  | 'recursive'
  | 'task_progress'
  | 'path'
  | 'content'
  | 'r'
  | 'file_operation'
  | 'execute'
  | 'execute_command'
  | 'requires_approval'
  | 'read_file'
  | 'replace_in_file'
  | 'diff'
  | 'search_files'
  | 'write_to_file'
  | 'regex'
  | 'file_pattern'
  | 'working_directory'
  | 'timeout'
  | 'tool_result'
  | 'list_code_definition_names'
  | 'pid'
  | 'terminal_append'
  | 'process_pid'
  | 'process_content';

interface Position {
  line: number;    // 行号
  column: number;  // 列号
  offset: number;  // 字符偏移量
}

interface TagLocation {
  start: Position;
  end: Position;
  source: string;  // 原始文本
}

interface ParserCallbacks {
  onopentag?: (name: string, attributes: Record<string, string>, location: TagLocation) => void;
  ontext?: (text: string, location: TagLocation) => void;
  onclosetag?: (name: string, location: TagLocation) => void;
}

// ==================== 常量定义 ====================

/**
 * 支持的标签列表
 */
const SUPPORTED_TAGS = [
  'hive-msg-id',
  'usage',
  'query',
  'action',
  'memory',
  'metadata',
  'terminal',
  'download_file',
  'use_mcp_tool',
  'thinking',
  'chat',
  'mcp_tool',
  'attempt_completion',
  'ask_followup_question',
  'message',
  'mcp',
  'command',
  'step',
  'result',
  'question',
  'server_name',
  'tool_name',
  'arguments',
  'file',
  'operation',
  'list_files',
  'recursive',
  'task_progress',
  'path',
  'content',
  'r',
  'file_operation',
  'execute',
  'execute_command',
  'requires_approval',
  'read_file',
  'replace_in_file',
  'diff',
  'search_files',
  'write_to_file',
  'regex',
  'file_pattern',
  'working_directory',
  'timeout',
  'tool_result',
  'list_code_definition_names',
  'pid',
  'terminal_append',
  'process_pid',
  'process_content',
  'notification',
  'tool_img'
] as const;

/**
 * 正则表达式：匹配 HTML 属性
 */
const ATTRIBUTE_REGEX = /([a-zA-Z][\w-]*)(=(['"])([^'"]*)\3)?/g;

/**
 * 正则表达式：匹配开始标签
 */
const START_TAG_REGEX = /<([^\s>]+)([^>]*)>/;

/**
 * 正则表达式：匹配结束标签
 */
const END_TAG_REGEX = /<\/([^>]+)>/;

/**
 * 正则表达式：匹配所有 HTML 标签（包括开始、结束和自闭合标签）
 * 匹配格式：<tag>、</tag>、<tag/>、<tag />
 * 使用更严格的匹配，确保每个标签独立匹配，不会跨标签匹配
 * 
 * 正则说明：
 * - < : 标签开始
 * - (\/?) : 可选的 / (用于结束标签或自闭合标签)
 * - ([a-zA-Z][\w-]*) : 标签名（必须以字母开头，可包含字母、数字、下划线、连字符）
 * - ((?:\s+[^>]*)?) : 可选的属性部分（空格后跟非>字符）
 * - (\/?) : 可选的 / (用于自闭合标签)
 * - > : 标签结束
 */
// 使用更简单的正则，确保每个标签独立匹配
// 对于结束标签：</tag> 匹配为 match[1]='/', match[2]='tag', match[3]='', match[4]=''
// 对于开始标签：<tag> 匹配为 match[1]='', match[2]='tag', match[3]='', match[4]=''
// 对于带属性的标签：<tag attr="value"> 匹配为 match[1]='', match[2]='tag', match[3]=' attr="value"', match[4]=''
// 对于自闭合标签：<tag/> 匹配为 match[1]='', match[2]='tag', match[3]='', match[4]='/'
const HTML_TAG_REGEX = /<(\/?)([a-zA-Z][\w-]*)([^>]*?)(\/?)>/g;

/**
 * HTML 注释开始标记
 */
const COMMENT_START = '<!--';

/**
 * HTML 注释结束标记
 */
const COMMENT_END = '-->';

// ==================== 主类 ====================

export class SimpleHtmlParser {
  private readonly supportedTags: Set<string>;
  private readonly callbacks: ParserCallbacks;
  
  // 位置跟踪
  private line: number = 1;
  private column: number = 1;
  private offset: number = 0;
  
  // 标签状态
  private currentTag: string | null = null;
  private currentAttributes: Record<string, string> = {};
  private currentTagStack: string[] = [];
  
  // tool_result 标签特殊处理：记录是否在 tool_result 标签内
  private isInToolResult: boolean = false;
  
  // notification 标签特殊处理：记录是否在 notification 标签内
  private isInNotification: boolean = false;

  constructor(callbacks: ParserCallbacks) {
    this.callbacks = callbacks;
    this.supportedTags = new Set(SUPPORTED_TAGS);
  }

  // ==================== 位置管理 ====================

  /**
   * 更新解析位置
   */
  private updatePosition(char: string): void {
    if (char === '\n') {
      this.line++;
      this.column = 1;
    } else {
      this.column++;
    }
    this.offset += char.length;
  }

  /**
   * 批量更新位置信息
   */
  private updatePositionForText(text: string): void {
    for (const char of text) {
      this.updatePosition(char);
    }
  }

  /**
   * 获取当前位置
   */
  private getPosition(): Position {
    return {
      line: this.line,
      column: this.column,
      offset: this.offset
    };
  }

  /**
   * 创建标签位置信息
   */
  private createLocation(start: Position, end: Position, source: string): TagLocation {
    return { start, end, source };
  }

  // ==================== 属性解析 ====================

  /**
   * 解析标签属性字符串
   */
  private parseAttributes(attributeString: string): Record<string, string> {
    const attributes: Record<string, string> = {};
    const regex = new RegExp(ATTRIBUTE_REGEX);
    let match;

    while ((match = regex.exec(attributeString)) !== null) {
      const [, name, , , value] = match;
      attributes[name] = value || '';
    }

    return attributes;
  }

  // ==================== 标签处理 ====================

  /**
   * 检查是否是标准的 HTML 标签名
   * 标准 HTML 标签名规则：
   * - 必须以字母开头
   * - 可以包含字母、数字、连字符、下划线
   * - 不能包含空格或其他特殊字符
   */
  private isValidHtmlTag(tagName: string): boolean {
    if (!tagName || tagName.length === 0) {
      return false;
    }
    // 必须以字母开头
    if (!/^[a-zA-Z]/.test(tagName)) {
      return false;
    }
    // 只能包含字母、数字、连字符、下划线
    return /^[a-zA-Z][\w-]*$/.test(tagName);
  }

  /**
   * 处理开始标签
   */
  private handleOpenTag(
    tag: string,
    attributeString: string,
    originalTag: string,
    location: TagLocation
  ): void {
    this.currentTagStack.push(tag);
    this.currentTag = tag;
    this.currentAttributes = this.parseAttributes(attributeString);
    
    // 如果是 tool_result 标签，设置标志
    if (tag === 'tool_result') {
      this.isInToolResult = true;
    }
    
    // 如果是 notification 标签，设置标志
    if (tag === 'notification') {
      this.isInNotification = true;
    }
    
    this.callbacks.onopentag?.(tag, this.currentAttributes, location);
  }

  /**
   * 更新当前标签状态（从栈中恢复）
   */
  private updateCurrentTagState(): void {
    if (this.currentTagStack.length > 0) {
      this.currentTag = this.currentTagStack[this.currentTagStack.length - 1];
    } else {
      this.currentTag = null;
      this.currentAttributes = {};
    }
  }

  /**
   * 处理结束标签
   */
  private handleCloseTag(tag: string, originalTag: string, location: TagLocation): void {
    // 栈为空，没有对应的开始标签
    if (this.currentTagStack.length === 0) {
      this.callbacks.ontext?.(originalTag, location);
      return;
    }

    const lastTag = this.currentTagStack[this.currentTagStack.length - 1];
    
    // 标签匹配，正常关闭
    if (lastTag === tag) {
      this.currentTagStack.pop();
      this.callbacks.onclosetag?.(tag, location);
      
      // 如果关闭的是 tool_result 标签，清除标志
      if (tag === 'tool_result') {
        this.isInToolResult = false;
      }
      
      // 如果关闭的是 notification 标签，清除标志
      if (tag === 'notification') {
        this.isInNotification = false;
      }
      
      this.updateCurrentTagState();
      return;
    }

    // 标签不匹配，尝试容错处理
    const matchingIndex = this.currentTagStack.lastIndexOf(tag);
    if (matchingIndex !== -1) {
      // 找到匹配的标签，关闭所有中间的标签
      const tagsToClose = this.currentTagStack.splice(matchingIndex);
      for (let i = tagsToClose.length - 1; i >= 0; i--) {
        const closingTag = tagsToClose[i];
        this.callbacks.onclosetag?.(closingTag, location);
        
        // 如果关闭的是 tool_result 标签，清除标志
        if (closingTag === 'tool_result') {
          this.isInToolResult = false;
        }
        
        // 如果关闭的是 notification 标签，清除标志
        if (closingTag === 'notification') {
          this.isInNotification = false;
        }
      }
      this.updateCurrentTagState();
    } else {
      // 没有找到匹配的标签，作为普通文本处理
      this.callbacks.ontext?.(originalTag, location);
    }
  }

  /**
   * 处理文本内容
   */
  private handleText(text: string, location: TagLocation): void {
    this.callbacks.ontext?.(text, location);
  }

  // ==================== 注释处理 ====================

  /**
   * 处理 HTML 注释
   */
  private processComment(html: string, startIndex: number, startPos: Position): number {
    const commentEndIndex = html.indexOf(COMMENT_END, startIndex + COMMENT_START.length);
    
    if (commentEndIndex !== -1) {
      // 完整的注释
      const comment = html.slice(startIndex, commentEndIndex + COMMENT_END.length);
      const endPos = this.getPosition();
      
      this.updatePositionForText(comment);
      const location = this.createLocation(startPos, this.getPosition(), comment);
      this.handleText(comment, location);
      
      return commentEndIndex + COMMENT_END.length;
    } else {
      // 不完整的注释，作为普通文本处理
      this.handleText('<', this.createLocation(startPos, this.getPosition(), '<'));
      this.updatePosition('<');
      return startIndex + 1;
    }
  }

  // ==================== 标签解析 ====================

  /**
   * 处理开始标签
   */
  private processStartTag(html: string, startIndex: number, startPos: Position): number {
    const match = html.slice(startIndex).match(START_TAG_REGEX);
    
    if (!match) {
      // 不完整的开始标签
      this.handleText('<', this.createLocation(startPos, this.getPosition(), '<'));
      this.updatePosition('<');
      return startIndex + 1;
    }

    const tag = match[1].trim();
    const attributes = match[2].trim();
    const originalTag = match[0];
    
    // 更新位置信息
    this.updatePositionForText(originalTag);
    const location = this.createLocation(startPos, this.getPosition(), originalTag);
    
    // 检查是否是标准的 HTML 标签
    if (!this.isValidHtmlTag(tag)) {
      // 不是标准的 HTML 标签，作为文本处理
      this.handleText(originalTag, location);
      return startIndex + originalTag.length;
    }

    if (this.supportedTags.has(tag)) {
      // 支持的标签，正常处理
      this.handleOpenTag(tag, attributes, originalTag, location);
    } else {
      // 不支持的标签，作为文本处理
      this.handleText(originalTag, location);
    }

    return startIndex + originalTag.length;
  }

  /**
   * 处理结束标签
   */
  private processEndTag(html: string, startIndex: number, startPos: Position): number {
    const match = html.slice(startIndex).match(END_TAG_REGEX);
    
    if (!match) {
      // 不完整的结束标签
      this.handleText('<', this.createLocation(startPos, this.getPosition(), '<'));
      this.updatePosition('<');
      return startIndex + 1;
    }

    const tag = match[1].trim();
    const originalTag = match[0];
    
    this.updatePositionForText(originalTag);
    const location = this.createLocation(startPos, this.getPosition(), originalTag);

    if (this.supportedTags.has(tag)) {
      // 支持的标签，正常处理
      this.handleCloseTag(tag, originalTag, location);
    } else {
      // 不支持的标签，作为文本处理
      this.handleText(originalTag, location);
    }

    return startIndex + originalTag.length;
  }

  /**
   * 处理普通文本内容
   */
  private processText(html: string, startIndex: number): number {
    const textEndIndex = html.indexOf('<', startIndex);
    const endIndex = textEndIndex !== -1 ? textEndIndex : html.length;
    const text = html.slice(startIndex, endIndex);

    if (text) {
      const startPos = this.getPosition();
      this.updatePositionForText(text);
      const location = this.createLocation(startPos, this.getPosition(), text);
      this.handleText(text, location);
    }

    return endIndex;
  }

  // ==================== 公共 API ====================

  /**
   * 写入 HTML 内容进行解析
   * 先使用正则提取所有标签，然后进行解析
   * 
   * 嵌套处理机制：
   * 1. 使用 currentTagStack 栈来跟踪嵌套的标签结构
   * 2. 开始标签推入栈，结束标签从栈中弹出
   * 3. 支持容错处理：如果结束标签不匹配，会查找栈中是否有匹配的标签
   * 4. 自闭合标签不推入栈，直接触发开始和结束事件
   * 
   * 示例嵌套结构：
   * <div><span>text</span></div> 
   * - <div> 推入栈
   * - <span> 推入栈
   * - </span> 弹出 <span>
   * - </div> 弹出 <div>
   */
  public write(html: string): void {
    // 重置位置信息（但保留标签栈以支持流式解析）
    this.line = 1;
    this.column = 1;
    this.offset = 0;

    // 使用正则表达式提取所有标签和文本片段
    // 注意：每次 write 调用都创建新的正则对象，确保 lastIndex 从 0 开始
    const regex = new RegExp(HTML_TAG_REGEX);
    let lastIndex = 0;
    let match;

    while ((match = regex.exec(html)) !== null) {
      const matchIndex = match.index;
      const matchLength = match[0].length;
      
      // 防止无限循环：如果匹配位置没有前进，跳出循环
      if (matchIndex === lastIndex && matchLength === 0) {
        break;
      }
      
      // 处理标签之前的文本内容
      if (matchIndex > lastIndex) {
        const text = html.slice(lastIndex, matchIndex);
        if (text) {
          const startPos = this.getPosition();
          this.updatePositionForText(text);
          const location = this.createLocation(startPos, this.getPosition(), text);
          this.handleText(text, location);
        }
      }

      // 检查是否在 tool_result 标签内
      if (this.isInToolResult) {
        // 在 tool_result 内，检查是否是 tool_result 结束标签或 tool_img 标签
        const isEndTag = match[1] === '/';
        const tagName = (match[2] || '').trim();
        
        if (isEndTag && tagName === 'tool_result') {
          // 这是 tool_result 的结束标签，正常处理
          const startPos = this.getPosition();
          const fullTag = match[0];
          this.updatePositionForText(fullTag);
          const location = this.createLocation(startPos, this.getPosition(), fullTag);
          this.handleCloseTag(tagName, fullTag, location);
          lastIndex = matchIndex + matchLength;
          continue;
        } else if (tagName === 'tool_img' || (isEndTag && tagName === 'tool_img')) {
          // tool_img 标签在 tool_result 内也要正常解析，不作为纯文本处理
          // 继续后续的正常处理流程，不在这里 continue
        } else {
          // 在 tool_result 内的其他标签，作为纯文本处理
          const startPos = this.getPosition();
          const fullTag = match[0];
          this.updatePositionForText(fullTag);
          const location = this.createLocation(startPos, this.getPosition(), fullTag);
          this.handleText(fullTag, location);
          lastIndex = matchIndex + matchLength;
          continue;
        }
      }
      
      // 检查是否在 notification 标签内
      if (this.isInNotification) {
        // 在 notification 内，检查是否是 notification 结束标签
        const isEndTag = match[1] === '/';
        const tagName = (match[2] || '').trim();
        
        if (isEndTag && tagName === 'notification') {
          // 这是 notification 的结束标签，正常处理
          const startPos = this.getPosition();
          const fullTag = match[0];
          this.updatePositionForText(fullTag);
          const location = this.createLocation(startPos, this.getPosition(), fullTag);
          this.handleCloseTag(tagName, fullTag, location);
          lastIndex = matchIndex + matchLength;
          continue;
        } else {
          // 在 notification 内的其他标签，作为纯文本处理
          const startPos = this.getPosition();
          const fullTag = match[0];
          this.updatePositionForText(fullTag);
          const location = this.createLocation(startPos, this.getPosition(), fullTag);
          this.handleText(fullTag, location);
          lastIndex = matchIndex + matchLength;
          continue;
        }
      }
      
      // 检查是否是 HTML 注释
      if (html.slice(matchIndex, matchIndex + COMMENT_START.length) === COMMENT_START) {
        const commentEndIndex = html.indexOf(COMMENT_END, matchIndex + COMMENT_START.length);
        if (commentEndIndex !== -1) {
          const comment = html.slice(matchIndex, commentEndIndex + COMMENT_END.length);
          const startPos = this.getPosition();
          this.updatePositionForText(comment);
          const commentLocation = this.createLocation(startPos, this.getPosition(), comment);
          this.handleText(comment, commentLocation);
          lastIndex = commentEndIndex + COMMENT_END.length;
          // 更新正则的 lastIndex，确保下次从正确位置开始匹配
          regex.lastIndex = lastIndex;
          continue;
        }
      }

      // 处理当前标签
      const startPos = this.getPosition();
      const isEndTag = match[1] === '/';
      // 确保标签名正确提取（去除可能的空白字符）
      const tagName = (match[2] || '').trim();
      // 属性部分：如果是结束标签，match[3] 应该为空
      const attributes = isEndTag ? '' : (match[3] || '').trim();
      const isSelfClosing = match[4] === '/';
      const fullTag = match[0];
      
      // 验证匹配的标签是否完整（应该以 > 结尾）
      if (!fullTag.endsWith('>')) {
        // 匹配不完整，作为文本处理
        this.handleText(fullTag, this.createLocation(startPos, this.getPosition(), fullTag));
        lastIndex = matchIndex + matchLength;
        continue;
      }

      this.updatePositionForText(fullTag);
      const location = this.createLocation(startPos, this.getPosition(), fullTag);

      // 检查是否是标准的 HTML 标签
      if (!this.isValidHtmlTag(tagName)) {
        // 不是标准的 HTML 标签，作为文本处理
        this.handleText(fullTag, location);
        lastIndex = matchIndex + matchLength;
        continue;
      }

      // 处理标准 HTML 标签
      if (isEndTag) {
        // 结束标签 - 处理嵌套标签的关闭
        if (this.supportedTags.has(tagName)) {
          this.handleCloseTag(tagName, fullTag, location);
        } else {
          this.handleText(fullTag, location);
        }
      } else if (isSelfClosing) {
        // 自闭合标签（如 <br/>）- 同时触发开始和结束事件，但不推入栈
        if (this.supportedTags.has(tagName)) {
          // 自闭合标签不推入栈，直接触发开始和结束事件
          this.currentTag = tagName;
          this.currentAttributes = this.parseAttributes(attributes);
          this.callbacks.onopentag?.(tagName, this.currentAttributes, location);
          this.callbacks.onclosetag?.(tagName, location);
          // 恢复之前的标签状态
          this.updateCurrentTagState();
        } else {
          this.handleText(fullTag, location);
        }
      } else {
        // 开始标签 - 推入栈以支持嵌套
        if (this.supportedTags.has(tagName)) {
          this.handleOpenTag(tagName, attributes, fullTag, location);
        } else {
          this.handleText(fullTag, location);
        }
      }

      lastIndex = matchIndex + matchLength;
    }

    // 处理剩余的文本内容
    if (lastIndex < html.length) {
      const text = html.slice(lastIndex);
      if (text) {
        const startPos = this.getPosition();
        this.updatePositionForText(text);
        const location = this.createLocation(startPos, this.getPosition(), text);
        this.handleText(text, location);
      }
    }
  }

  /**
   * 结束解析，清理所有未闭合的标签
   */
  public end(): void {
    // 处理所有未闭合的标签（容错处理）
    if (this.currentTagStack.length > 0) {
      const currentPosition = this.getPosition();
      while (this.currentTagStack.length > 0) {
        const tag = this.currentTagStack.pop()!;
        const location = this.createLocation(
          currentPosition,
          currentPosition,
          `</${tag}>`
        );
        this.callbacks.onclosetag?.(tag, location);
      }
    }

    // 清理所有状态
    this.reset();
  }

  /**
   * 重置解析器状态
   */
  private reset(): void {
    this.currentTag = null;
    this.currentAttributes = {};
    this.currentTagStack = [];
    this.isInToolResult = false;
    this.isInNotification = false;
    this.line = 1;
    this.column = 1;
    this.offset = 0;
  }

  // ==================== 状态查询 ====================

  /**
   * 检查当前是否在标签内
   * @returns 如果当前在标签内，返回当前标签名；否则返回 null
   */
  public isInTag(): string | null {
    return this.currentTag;
  }

  /**
   * 获取当前标签栈的深度
   * @returns 当前嵌套的标签数量
   */
  public getTagStackDepth(): number {
    return this.currentTagStack.length;
  }

  /**
   * 获取当前标签栈的副本
   * @returns 当前标签栈的数组副本
   */
  public getTagStack(): string[] {
    return [...this.currentTagStack];
  }
}
