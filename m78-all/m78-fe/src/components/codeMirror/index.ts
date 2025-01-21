import { ref } from 'vue'
import 'codemirror/mode/javascript/javascript.js'
import 'codemirror/theme/idea.css'
// import 'codemirror/addon/lint/lint.css'
// require('script-loader!jsonlint')
// import 'codemirror/addon/lint/lint'
// import 'codemirror/addon/lint/json-lint'
import 'codemirror/addon/display/autorefresh'
import 'codemirror/addon/display/placeholder.js'

export default function () {
  const codemirror = ref(null)

  // 校验错误
  const validateData = (): boolean => {
    if (codemirror.value) {
      const el = (codemirror.value as HTMLElement).querySelector('.CodeMirror-lint-marker-error')
      return !!el
    }
    return false
  }

  return {
    validateData,
    cmOptions: {
      lint: false,
      mode: 'text/javascript', // 语言模式
      theme: 'idea', // 主题
      lineNumbers: false, // 显示行号
      smartIndent: true, // 智能缩进
      indentUnit: 2, // 智能缩进单位为4个空格长度
      foldGutter: true, // 启用行槽中的代码折叠
      styleActiveLine: true, // 显示选中行的样式
      autoRefresh: true,
      autoCloseBrackets: true,
      autofocus: true,
      autocorrect: true
    }
  }
}
