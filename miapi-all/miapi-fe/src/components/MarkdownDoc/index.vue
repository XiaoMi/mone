<template>
  <div :id="id" />
</template>

<script lang="ts">
import Viewer from 'tui-editor/dist/tui-editor-Viewer'
import 'codemirror/lib/codemirror.css' // codemirror
import 'tui-editor/dist/tui-editor.css' // editor ui
import 'tui-editor/dist/tui-editor-contents.css' //

import { defineComponent, reactive, computed, toRefs, watch, onMounted, onUnmounted } from "vue"

export default defineComponent({
  props: {
		content:{
			type: String,
			default: ''
		},
    id: {
      type: String,
      required: false,
      default() {
        return 'markdown-editor-' + +new Date() + ((Math.random() * 1000).toFixed(0) + '')
      }
    },
    height: {
      type: String,
      required: false,
      default: '300px'
    },
    language: {
      type: String,
      required: false,
      default: 'zh_US' // https://github.com/nhnent/tui.editor/tree/master/src/js/langs
    }
  },
  setup(props, ctx){
    const state = reactive({
      editor: null,
      id: props.id,
      editorOptions: computed(() => {
        const options = {
          height: props.height,
          language: props.language
        }
        return options
      })
    })

    //method
    const initEditor = () => {
      state.editor = new Viewer({
        el: document.getElementById(state.id),
        ...state.editorOptions
      })
      if (props.content) {
        state.editor.setValue(props.content)
      }
    }
    const destroyEditor = () => {
      if (!state.editor) return
      state.editor.remove()
    }

    watch(() => props.content, (val) => {
      state.editor.setValue(val)
    })

    onMounted(() => {
      initEditor()
    })

    onUnmounted(() => {
      destroyEditor()
    })

    return {
      ...toRefs(state)
    }
  }
})
</script>
