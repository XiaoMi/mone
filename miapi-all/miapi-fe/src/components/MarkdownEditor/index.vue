<template>
  <div :id="id" />
</template>

<script lang="ts">
// deps for editor
import 'codemirror/lib/codemirror.css' // codemirror
import 'tui-editor/dist/tui-editor.css' // editor ui
import 'tui-editor/dist/tui-editor-contents.css' // editor content

import Editor from 'tui-editor'
import defaultOptions from './default-options'
import { defineComponent, reactive, toRefs, computed, watch, onMounted, onUnmounted } from "vue"

export default defineComponent({
  props: {
    content: {
      type: String,
      default: ''
    },
    id: {
      type: String,
      required: false,
      default () {
        return 'markdown-editor-' + +new Date() + ((Math.random() * 1000).toFixed(0) + '')
      }
    },
    options: {
      type: Object,
      default () {
        return defaultOptions
      }
    },
    mode: {
      type: String,
      default: 'markdown' // wysiwyg
    },
    height: {
      type: String,
      required: false,
      default: '300px'
    },
    language: {
      type: String,
      required: false,
      default: 'en_US' // https://github.com/nhnent/tui.editor/tree/master/src/js/langs
    }
  },
  setup(props, ctx){
    const state = reactive({
      editor: null,
      id: props.id,
      editorOptions: computed(() => {
        const options = Object.assign({}, defaultOptions, props.options)
        options.initialEditType = props.mode
        options.height = props.height
        options.language = props.language
        return options
      })
    })

    //method
    const initEditor = () => {
      state.editor = new Editor({
        el: document.getElementById(state.id),
        ...state.editorOptions
      })
      if (props.content) {
        state.editor.setValue(props.content)
      }
      state.editor.on('change', () => {
        ctx.emit('changeContent', state.editor.getValue())
      })
    }
    const destroyEditor = () => {
      if (!state.editor) return
      state.editor.off('change')
      state.editor.remove()
    }
    const setValue = (value) => {
      state.editor.setValue(value)
    }
    const getValue = () => {
      return state.editor.getValue()
    }
    const setHtml = (value) => {
      state.editor.setHtml(value)
    }
    const getHtml = () => {
      return state.editor.getHtml()
    }

    watch(() => props.content, (newValue, preValue) => {
      if (newValue !== preValue && newValue !== state.editor.getValue()) {
        state.editor.setValue(newValue)
      }
    })

    watch(() => props.language, (newValue, preValue) => {
      destroyEditor()
      initEditor()
    })

    watch(() => props.height, (newValue) => {
      state.editor.height(newValue)
    })

    watch(() => props.mode, (newValue) => {
      state.editor.changeMode(newValue)
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

<style lang="scss">
  .tui-editor .te-preview-style-vertical .te-md-splitter{
    width: 100%;
  }
</style>
