import * as monaco from 'monaco-editor'
import jsonWorker from 'monaco-editor/esm/vs/language/json/json.worker?worker'
import cssWorker from 'monaco-editor/esm/vs/language/css/css.worker?worker'
import htmlWorker from 'monaco-editor/esm/vs/language/html/html.worker?worker'
import tsWorker from 'monaco-editor/esm/vs/language/typescript/ts.worker?worker'
import EditorWorker from 'monaco-editor/esm/vs/editor/editor.worker?worker'
​
// @ts-ignore: worker
self.MonacoEnvironment = {
  getWorker(_: string, label: string) {
    if (label === 'json') {
      return new jsonWorker()
    }
    if (['css', 'scss', 'less'].includes(label)) {
      return new cssWorker()
    }
    if (['html', 'handlebars', 'razor'].includes(label)) {
      return new htmlWorker()
    }
    if (['typescript', 'javascript'].includes(label)) {
      return new tsWorker()
    }
    return new EditorWorker()
  }
}
monaco.languages.register({ id: 'java' })

monaco.languages.registerHoverProvider("java", {
	provideHover: function (model, position) {
    const lineText = model.getLineContent(position.lineNumber).trim()
    console.log('lineText', lineText)
    if (lineText.startsWith('//')) {
      return Promise.resolve().then(() => {
        return {
          range: new monaco.Range(
            position.lineNumber,
            position.column,
            model.getLineCount(),
            model.getLineMaxColumn(model.getLineCount())
          ),
          contents: [{
            value: '**提示**'
          }, {
            value: '```\n在注释行使用ctrl-g可自动根据注释生成代码\n```'
          }]
        }
      })
    }
		return null
	},
})

monaco.languages.registerCompletionItemProvider('java', {
  provideCompletionItems: function(model, position) {
    const lineNumber = position.lineNumber
    const column = position.column
    return {
      suggestions: [
        {
          label: 'method',
          kind: monaco.languages.CompletionItemKind.Method,
          insertText: 'method()',
          range: new monaco.Range(lineNumber, column, lineNumber, column)
        }
      ]
    }
  }
})