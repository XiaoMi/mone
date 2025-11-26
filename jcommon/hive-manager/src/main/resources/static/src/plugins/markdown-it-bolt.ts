import MarkdownIt from 'markdown-it'
import StateBlock from 'markdown-it/lib/rules_block/state_block'

// 在文件顶部添加 escapeHtml 函数
function escapeHtml(text: string): string {
  return text
    .replace(/`/g, '&grave;')
}

// 在 escapeHtml 函数后添加 unescapeHtml 函数
function unescapeHtml(text: string): string {
  return text
    .replace(/&amp;/g, '&')
    .replace(/&grave;/g, '`');
}

// 1. 创建类型定义，提高代码可读性和类型安全性
interface BoltAction {
  type: string;
  subType?: string;
  filePath?: string;
  content: string;
  attributes: string;
}

// 2. 将HTML模板抽离为常量
const HTML_TEMPLATES = {
  fileAction: (action: BoltAction) => `
    <div class="bolt-action" ${action.attributes}>
      <div class="bolt-action-header bolt-action-header-toggle">
        <span class="bolt-action-status">
          <span class="bolt-status-loading"></span>
        </span>
        <span class="bolt-action-header-text">
          <span class="bolt-action-item-text">${escapeHtml(action.subType || '')}</span>
          <span class="bolt-action-item-content">${escapeHtml(action.filePath || '')}</span>
        </span>
        <span class="bolt-action-toggle">
          <span class="chevron-right"></span>
        </span>
      </div>
      <div class="bolt-action-content"></div>
    </div>
  `
};

export function markdownItBolt(md: MarkdownIt) {
  // 添加新的 block 规则
  md.block.ruler.before('html_block', 'bolt', parseBoltBlock, {
    alt: ['paragraph', 'reference', 'blockquote']
  })

  // 处理bolt标签的核心逻辑
  function parseBoltBlock(state: StateBlock, startLine: number, endLine: number, silent: boolean) {
    let pos = state.bMarks[startLine] + state.tShift[startLine]
    let max = state.eMarks[startLine]

    // 检查是否以<boltArtifact开头
    if (!state.src.slice(pos).trim().startsWith('<boltArtifact')) {
      return false
    }

    if (silent) return true

    let line = startLine
    let content = []
    let artifactAttrs = ''
    let inAction = false
    let actionAttrs = ''
    let actionContent = ''

    // 提取 boltArtifact 的特定属性
    const artifactId = extractSpecificAttribute(state.src.slice(pos, max), 'id')
    const artifactTitle = extractSpecificAttribute(state.src.slice(pos, max), 'title')
    artifactAttrs = extractAttributes(state.src.slice(pos, max))

    while (line < endLine) {
      const originalPos = state.bMarks[line]
      pos = state.bMarks[line] + state.tShift[line]
      max = state.eMarks[line]
      let lineText = state.src.slice(originalPos, max)
      
      if (lineText.includes('</boltArtifact>')) {
        break
      }

      if (lineText.includes('<purpose>')) {
        inAction = false
        line++
        continue
      }

      if (lineText.includes('<boltAction')) {
        inAction = true
        actionAttrs = extractAttributes(lineText)
        const actionType = extractSpecificAttribute(actionAttrs, 'type')
        const actionSubType = extractSpecificAttribute(actionAttrs, 'subType') || ''
        const actionFilepath = extractSpecificAttribute(actionAttrs, 'filePath')

        // 如果 type 是 file，直接使用 filepath 作为内容并立即结束当前 action
        if (actionType === 'file' || actionType === 'write_to_file') {
          actionContent = ''

          content.push(HTML_TEMPLATES.fileAction({
            type: actionType,
            subType: actionSubType,
            filePath: actionFilepath,
            content: actionContent,
            attributes: actionAttrs
          }))
          line++
          continue
        }

        line++
        continue
      }

      if (lineText.includes('</boltAction>')) {
        // 根据 type 处理不同的内容格式
        const actionType = extractSpecificAttribute(actionAttrs, 'type')
        const actionSubType = extractSpecificAttribute(actionAttrs, 'subType') || ''
        let formattedContent = actionContent

        if (actionType === 'shell') {
          const codeBlock = `\`\`\`\n${actionContent.trim()}\n\`\`\``
          const parsedCode = md.render(codeBlock)

          content.push(`<div class="bolt-action" ${actionAttrs}>
            <div class="bolt-action-header">
              <span class="bolt-action-status">
                <span class="bolt-status-success"></span>
              </span>
              <div class="bolt-action-header-text">
                <span class="bolt-action-item-text">${actionSubType} Command</span>
              </div>
            </div>
            <div class="bolt-action-content">${parsedCode}</div>
          </div>`)
        } else if (actionType === 'start'){
          const codeBlock = `\`\`\`\n${actionContent.trim()}\n\`\`\``
          const parsedCode = md.render(codeBlock)

          content.push(`<div class="bolt-action" ${actionAttrs}>
            <div class="bolt-action-header">
              <span class="bolt-action-status">
                <span class="bolt-status-success"></span>
              </span>
              <div class="bolt-action-header-text">
                <span class="bolt-action-item-text">START Application</span>
              </div>
            </div>
            <div class="bolt-action-content">${parsedCode}</div>
          </div>`)
        } else if (actionType === 'file') {
          if (actionSubType === 'analysis') {
            content.pop() as string
          } else {
            const text = content.pop() as string
            const filePath = extractSpecificAttribute(actionAttrs, 'filePath')
            const fileExt = filePath.split('.').pop() || ''

            // 使用 markdown-it 解析代码块
            const codeBlock = `\`\`\`${fileExt}\n${escapeHtml(actionContent)}\n\`\`\``
            let parsedCode = md.render(codeBlock)

            // 对解析后的代码进行反转义
            parsedCode = unescapeHtml(parsedCode)

            const updatedText = text
              .replace('bolt-status-loading', 'bolt-status-success')
              .replace(
                '<div class="bolt-action-content"></div>',
                `<div class="bolt-action-content">${parsedCode}</div>`
              )
            content.push(updatedText)
          }
        } else if (actionType === 'write_to_file') {
          const text = content.pop() as string
          const filePath = extractSpecificAttribute(actionAttrs, 'filePath')
          const fileExt = filePath.split('.').pop() || ''

          // 使用 markdown-it 解析代码块
          const codeBlock = `\`\`\`${fileExt}\n${escapeHtml(actionContent)}\n\`\`\``
          let parsedCode = md.render(codeBlock)

          // 对解析后的代码进行反转义
          parsedCode = unescapeHtml(parsedCode)

          const updatedText = text
            .replace('bolt-status-loading', 'bolt-status-success')
            .replace(
              '<div class="bolt-action-content"></div>',
              `<div class="bolt-action-content">${parsedCode}</div>`
            )
          content.push(updatedText)
        }

        inAction = false
        actionContent = ''
        line++
        continue
      }

      if (inAction) {
        actionContent += lineText + '\n'
      }

      line++
    }

    // 4. 使用更简洁的数组方法处理内容
    const processedContent = content
      .map(item => item.includes('class="bolt-action"')
        ? `<div class="bolt-action-row">${item.replace('class="bolt-action"', 'class="bolt-action-item"')}</div>`
        : item
      )
      .join('');

    // 生成最终的HTML
    let html = `
      <div class="bolt-artifact" ${artifactAttrs}>
        <div class="bolt-artifact-header">
          <h3 class="bolt-artifact-title">${artifactTitle} (${artifactId})</h3>
        </div>
        <div class="bolt-artifact-content">
          ${processedContent}
        </div>
      </div>
    `

    // 创建token
    let token = state.push('html_block', '', 0)
    token.content = html
    token.map = [startLine, line]

    state.line = line + 1
    return true
  }

  // 辅助函数:提取标签属性
  function extractAttributes(text: string): string {
    let attrs = ''
    const matches = text.match(/\s+(\w+)="([^"]*?)"/g)
    if (matches) {
      attrs = matches.join('')
    }
    return attrs
  }

  // 新增辅助函数：提取特定属性
  function extractSpecificAttribute(text: string, attrName: string): string {
    const match = text.match(new RegExp(`${attrName}="([^"]*?)"`))
    return match ? match[1] : ''
  }
}
