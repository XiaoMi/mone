<template>
	<div class="codemirror-container">
    <!-- <el-image v-if="showFullScreen" @click="handleFullScreen" :class="{'full-screen': true, fullScreen}" :src="fullPng"></el-image> -->
		<textarea ref="codeMirrorContainer" v-model="initContent"></textarea>
	</div>
</template>
<script>
import { defineComponent, watch, onBeforeUnmount, onMounted, reactive, toRefs, ref } from "vue"
import CodeMirror from 'codemirror'

import "codemirror/lib/codemirror.css"
import 'codemirror/addon/lint/lint.css'
import "codemirror/addon/hint/show-hint.css"

// 主题
import "codemirror/theme/base16-dark.css"
import "codemirror/theme/material.css"

// js校验
import 'codemirror/addon/lint/lint'
import 'codemirror/addon/lint/javascript-lint'
import 'codemirror/addon/lint/json-lint'

// js提示
// import "codemirror/addon/hint/show-hint"
// import "codemirror/addon/hint/anyword-hint"
// import 'codemirror/addon/hint/javascript-hint'

import 'codemirror/addon/display/fullscreen'

// 括号显示匹配
import 'codemirror/addon/edit/matchbrackets.js'
import 'codemirror/addon/selection/active-line.js'
// 括号、引号编辑和删除时成对出现
import 'codemirror/addon/edit/closebrackets.js'
import fullPng from "@/assets/fullsc.png"

let jsObj = {
  'text/javascript': import('codemirror/mode/javascript/javascript.js'),
  'text/x-csrc': import('codemirror/mode/clike/clike.js'),
  'text/x-c++src': import('codemirror/mode/clike/clike.js'),
  'text/x-java': import('codemirror/mode/clike/clike.js'),
  'text/x-go': import('codemirror/mode/go/go.js'),
  'text/html': import('codemirror/mode/htmlmixed/htmlmixed.js'),
  'text/x-python': import('codemirror/mode/python/python.js'),
  'text/x-sql': import('codemirror/mode/sql/sql.js'),
  'text/x-vue': import('codemirror/mode/vue/vue.js'),
  'application/xml': import('codemirror/mode/xml/xml.js'),
  'text/x-yaml': import('codemirror/mode/yaml/yaml.js')
}

export default defineComponent({
  name: 'codeMirror',
  props: {
    content: {
      default: () => {
        return {}
      }
    },
    codeMirrorOptions: {
      type: Object,
      default: () => {
        return {}
      }
    },
    codeInit: {
      type: Boolean,
      default: () => {
        return true
      }
    }
  },
  setup(props, context){
    const codeMirrorContainer = ref('');
    const state = reactive({
      jsonEditor: null,
      fullScreen: false,
      showFullScreen: false,
      initContent: "",
      options: { // https://tun6.com/code_mirror/config/
        tabSize: 2,
        indentUnit: 2,
        mode: "text/javascript", // text/x-csrc text/x-c++src text/x-go text/html text/x-java text/javascript text/x-python text/x-sql script/x-vue application/xml text/x-yaml
        theme: "base16-dark",
        readOnly: false,
        lineNumbers: true,
        lineWrapping: true,
        dragDrop: false,
        line: true,
        lint: true,
        gutters: ["CodeMirror-lint-markers"],
        smartIndent: true,
        selfContain: true,
        spellcheck: true,
        matchBrackets: true, // 括号匹配显示
        autoCloseBrackets: true, // 输入和退格时成对
        styleActiveLine: true, // 当前行高亮
        autoRefresh: true, // 自动刷新
        autocorrect: false,
        hintOptions: {
          completeSingle: false
        }
      }
    })

    const handleInitCode = () => {
      let options = Object.assign({}, state.options, props.codeMirrorOptions)
      state.showFullScreen = !options.readOnly
      jsObj[options.mode].then(() => {
        state.jsonEditor = CodeMirror.fromTextArea(codeMirrorContainer.value, {
          ...options
        })
        state.jsonEditor.on('change', cm => {
          context.emit('json-change', cm.getValue())
        })
        // if (!options.readOnly) {
        //   state.jsonEditor.on('inputRead', () => {
        //     state.jsonEditor.showHint()
        //   })
        // }
      })
    }
    watch(() => props.content, (val) => {
      if (!state.jsonEditor) {
        return
      }
      const editorValue = state.jsonEditor.getValue()
      if (val !== editorValue) {
        try {
          state.jsonEditor.setValue(val)
        } catch (error) {
          state.jsonEditor.setValue(JSON.stringify(val))
        }
      }
    })

    watch(props.codeMirrorOptions, (val) => {
      if (!state.jsonEditor) {
          return
        }
        Object.keys(val).forEach(key => {
          state.jsonEditor.setOption(key, val[key])
        })
    }, {
      deep: true
    })

    watch(() => props.codeInit, (val) => {
      if (val) {
        state.initContent = props.content
        handleInitCode()
      }
    }, {
      immediate: true,
      deep: true
    })

    onBeforeUnmount(() => {
      if (state.jsonEditor) {
        state.jsonEditor.toTextArea()
        state.jsonEditor = null
      }
    })

    const  handleFullScreen = () => {
      state.fullScreen = !state.fullScreen
      state.jsonEditor.setOption("fullScreen", state.fullScreen)
    }

    return {
      handleFullScreen,
      codeMirrorContainer,
      fullPng,
      ...toRefs(state)
    }
  }
})
</script>
<style scoped>
.codemirror-container{
  position: relative;
}
.codemirror-container >>> .CodeMirror-fullscreen {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  height: auto;
  z-index: 200;
}
.codemirror-container .full-screen {
  position: absolute;
  right: 10px;
  top: 10px;
  z-index: 201;
  cursor: pointer;
  width: 20px;
  height: 20px;
}
.codemirror-container .full-screen.fullScreen {
  position: fixed;
}
</style>
