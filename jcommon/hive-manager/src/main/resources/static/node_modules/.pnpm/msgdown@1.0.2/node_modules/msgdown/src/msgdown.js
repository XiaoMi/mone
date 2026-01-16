const defaultTokens = {
  bold: { delimiter: '*', tag: 'strong' },
  italic: { delimiter: '/', tag: 'em' },
  underline: { delimiter: '_', tag: 'u' },
  strike: { delimiter: '~', tag: 'del' },
  code: { delimiter: '`', tag: 'code' },
  sup: { delimiter: '^', tag: 'sup' },
  sub: { delimiter: '¡', tag: 'sub' }
}

const openTag = tag => `<${tag}>`
const closeTag = tag => `</${tag}>`
const encloseTag = (text, tag) => `${openTag(tag)}${text}${closeTag(tag)}`
const parseToken = (text, stopDelimiter) => {
  let index = 0
  let content = ''
  while (index < text.length && text[index] !== stopDelimiter) {
    content += text[index]
    index++
  }
  if (index === text.length) {
    return ''
  } else {
    return content
  }
}

module.exports = (text, tokens = defaultTokens) => {
  tokens = Object.assign({}, defaultTokens, tokens)
  let html = ''
  let index = 0

  const consumeChar = () => {
    html += text[index]
    index++
  }
  const consumeAll = (textRemaining, delimiter) => {
    html += delimiter + textRemaining
    index += textRemaining.length
  }
  const consumeToken = token => {
    index++
    if (text[index] === ' ') {
      html += token.delimiter
      consumeChar()
    } else {
      const textRemaining = text.substr(index)
      const tokenContent = parseToken(textRemaining, token.delimiter)
      if (tokenContent.length === 0) {
        consumeAll(textRemaining, token.delimiter)
      } else {
        html += encloseTag(tokenContent, token.tag)
        index += tokenContent.length + 1
      }
    }
  }

  while (index < text.length) {
    const charCurrent = text[index]
    if (charCurrent === tokens.bold.delimiter) {
      consumeToken(tokens.bold)
    } else if (charCurrent === tokens.italic.delimiter) {
      consumeToken(tokens.italic)
    } else if (charCurrent === tokens.underline.delimiter) {
      consumeToken(tokens.underline)
    } else if (charCurrent === tokens.strike.delimiter) {
      consumeToken(tokens.strike)
    } else if (charCurrent === tokens.code.delimiter) {
      consumeToken(tokens.code)
    } else if (charCurrent === tokens.sup.delimiter) {
      consumeToken(tokens.sup)
    } else if (charCurrent === tokens.sub.delimiter) {
      consumeToken(tokens.sub)
    } else {
      consumeChar()
    }
  }

  return html
}
