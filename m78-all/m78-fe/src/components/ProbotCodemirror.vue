<!--
 * @Description: 
 * @Date: 2024-04-15 16:15:54
 * @LastEditTime: 2024-07-09 15:22:56
-->
<template>
  <VMonacoEditor
    :language="language"
    v-model="codeVal"
    @editor-mounted="editorMounted"
    :readOnly="readOnly"
  ></VMonacoEditor>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import * as monaco from 'monaco-editor'
import VMonacoEditor from '@/components/monaco-editor/index.vue'

const emits = defineEmits(['update:modelValue'])
const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  readOnly: {
    default: false
  }
})
const codeVal = computed({
  get() {
    return props.modelValue
  },
  set(val) {
    emits('update:modelValue', val)
  }
})
const language = ref('java')
const editorMounted = (editor: monaco.editor.IStandaloneCodeEditor) => {
  console.log('editor实例加载完成', editor, props.modelValue)
}
</script>
