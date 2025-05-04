function isBracketsBalanced(input: string, index: number): number {
  const stack: string[] = []
  const bracketsMap: { [key: string]: string } = {
    '[': ']',
    '{': '}'
  }

  for (let i = index; i < input.length; i++) {
    const char = input[i]

    if (bracketsMap[char]) {
      // 如果是左括号，压入栈中
      stack.push(char)
    } else if (char === ']' || char === '}') {
      // 如果是右括号，检查栈顶元素是否匹配
      if (stack.length === 0) {
        // 栈为空，说明没有相对应的左括号，不匹配
        return i
      }

      const last = stack.pop()
      const expected = bracketsMap[last!] // 获取栈中括号期望匹配的右括号
      if (char !== expected) {
        // 如果当前右括号与栈顶左括号不匹配，说明整个序列不匹配
        return -2
      }
    } else {
      i++
    }
  }

  // 如果栈为空，说明所有括号都正确配对
  if (stack.length === 0) return input.length
  return -1
}

export function getJSON(input: string) {
  for (let i = 0; i < input.length; i++) {
    const char = input[i]
    if (char === '{') {
      const j = isBracketsBalanced(input, i)
      if (j > 0) {
        const str1 = input.substring(i, j + 1)
        console.log(str1)
        i = j
      }
    }
  }
}
