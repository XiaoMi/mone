<!--
 * @Description: 
 * @Date: 2024-10-22 18:48:48
 * @LastEditTime: 2024-10-24 14:29:32
-->
<template>
  <div>
    <span class="markdown-body" v-html="text1" />
    <el-button @click="restore" type="primary">恢复</el-button>
    <span class="markdown-body" v-html="text2" />
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, onBeforeUnmount } from 'vue'
// @ts-ignore
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import { t } from '@/locales'
import mitt from '@/utils/bus'

const props = defineProps({
  data: {
    type: String
  },
  asRawText: {
    type: String
  }
})

const text1 = ref('')
const text2 = ref('')
const clickDataId = ref('')
const restore = () => {
  mitt.emit('DebuggerPageDataClick', clickDataId.value)
}
function highlightBlock(str: string, lang?: string) {
  return `<pre class="code-block-wrapper"><div class="code-block-header"><span class="code-block-header__lang">${lang}</span><span class="code-block-header__copy">${t(
    'chat.copyCode'
  )}</span></div><code class="hljs code-block-body ${lang}">${str}</code></pre>`
}
const mdi = new MarkdownIt({
  html: true,
  linkify: true,
  highlight(code, language) {
    const validLang = !!(language && hljs.getLanguage(language))
    if (validLang) {
      const lang = language ?? ''
      return highlightBlock(hljs.highlight(code, { language: lang }).value, lang)
    }
    return highlightBlock(hljs.highlightAuto(code).value, '')
  }
})
onMounted(() => {
  const arr1 = props.data?.split('<zxw开始>')
  if (arr1) {
    text1.value = mdi.render(arr1[0])
    const arr2 = arr1[1]?.split('<zxw结束>')
    text2.value = arr2[1] ? mdi.render(arr2[1]) : ''
    clickDataId.value = arr2[0].replace(/<id结束>/g, '').trim()
  }
})

onBeforeUnmount(() => {})
</script>

<style scoped></style>
