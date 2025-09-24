// 定义支持的标签类型
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
  | 'result'
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
  | 'tool_result';

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

export class SimpleHtmlParser {
  private readonly supportedTags: Set<string>;
  private readonly callbacks: ParserCallbacks;
  private buffer: string = '';
  private currentTag: string | null = null;
  private currentAttributes: Record<string, string> = {};
  private line: number = 1;
  private column: number = 1;
  private offset: number = 0;

  constructor(callbacks: ParserCallbacks) {
    this.callbacks = callbacks;
    this.supportedTags = new Set([
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
      'result',
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
      'tool_result'
    ]);
  }

  private updatePosition(char: string) {
    if (char === '\n') {
      this.line++;
      this.column = 1;
    } else {
      this.column++;
    }
    this.offset += char.length;
  }

  private getPosition(): Position {
    return {
      line: this.line,
      column: this.column,
      offset: this.offset
    };
  }

  private parseAttributes(attributeString: string): Record<string, string> {
    const attributes: Record<string, string> = {};
    const regex = /([a-zA-Z][\w-]*)(=(['"])([^'"]*)\3)?/g;
    let match;

    while ((match = regex.exec(attributeString)) !== null) {
      const [, name, , , value] = match;
      attributes[name] = value || '';
    }

    return attributes;
  }

  private handleOpenTag(tag: string, attributeString: string = '', originalTag: string, location: TagLocation) {
    if (this.supportedTags.has(tag)) {
      this.currentTag = tag;
      this.currentAttributes = this.parseAttributes(attributeString);
      this.callbacks.onopentag?.(tag, this.currentAttributes, location);
    } else {
      // 对于不支持的标签，直接输出原始字符串
      this.callbacks.ontext?.(originalTag, location);
    }
  }

  private handleCloseTag(tag: string, originalTag: string, location: TagLocation) {
    if (this.supportedTags.has(tag)) {
      this.callbacks.onclosetag?.(tag, location);
      this.currentTag = null;
      this.currentAttributes = {};
    } else {
      // 对于不支持的标签，直接输出原始字符串
      this.callbacks.ontext?.(originalTag, location);
    }
  }

  private handleText(text: string, location: TagLocation) {
    this.callbacks.ontext?.(text, location);
  }

  public write(html: string) {
    let i = 0;
    this.line = 1;
    this.column = 1;
    this.offset = 0;

    while (i < html.length) {
      const startPos = this.getPosition();

      if (html[i] === '<') {
        if (html[i + 1] === '/') {
          // 处理结束标签
          const endTagMatch = html.slice(i).match(/<\/([^>]+)>/);
          if (endTagMatch) {
            const tag = endTagMatch[1].trim();
            const originalTag = endTagMatch[0];

            // 更新位置信息
            const endPos = { ...startPos };
            for (let j = 0; j < originalTag.length; j++) {
              this.updatePosition(originalTag[j]);
            }

            this.handleCloseTag(tag, originalTag, {
              start: startPos,
              end: this.getPosition(),
              source: originalTag
            });

            i += endTagMatch[0].length;
            continue;
          } else {
            // 处理不完整的结束标签
            this.handleText('<', {
              start: startPos,
              end: this.getPosition(),
              source: '<'
            });
            this.updatePosition('<');
            i++;
            continue;
          }
        } else {
          // 处理开始标签
          const startTagMatch = html.slice(i).match(/<([^\s>]+)([^>]*)>/);
          if (startTagMatch) {
            const tag = startTagMatch[1].trim();
            const attributes = startTagMatch[2].trim();
            const originalTag = startTagMatch[0];

            // 更新位置信息
            const endPos = { ...startPos };
            for (let j = 0; j < originalTag.length; j++) {
              this.updatePosition(originalTag[j]);
            }

            this.handleOpenTag(tag, attributes, originalTag, {
              start: startPos,
              end: this.getPosition(),
              source: originalTag
            });

            i += startTagMatch[0].length;
            continue;
          } else {
            // 处理不完整的开始标签
            this.handleText('<', {
              start: startPos,
              end: this.getPosition(),
              source: '<'
            });
            this.updatePosition('<');
            i++;
            continue;
          }
        }
      }

      // 处理文本内容
      let textEnd = html.indexOf('<', i);
      if (textEnd === -1) textEnd = html.length;
      const text = html.slice(i, textEnd);

      if (text) {
        const textLocation = {
          start: this.getPosition(),
          end: { ...this.getPosition() },
          source: text
        };

        // 更新位置信息
        for (let j = 0; j < text.length; j++) {
          this.updatePosition(text[j]);
        }
        textLocation.end = this.getPosition();

        this.handleText(text, textLocation);
      }
      i = textEnd;
    }
  }

  public end() {
    // 清理任何剩余的状态
    this.buffer = '';
    this.currentTag = null;
    this.currentAttributes = {};
    this.line = 1;
    this.column = 1;
    this.offset = 0;
  }
}
