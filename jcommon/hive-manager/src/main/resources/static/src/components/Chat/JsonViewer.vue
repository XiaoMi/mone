<template>
  <div class="json-viewer">
    <div v-if="props.content" class="my-codemirror-container">
      <CodemirrorEditor
        :value="formattedContent"
        :options="cmOptions"
        :placeholder="placeholder"
        class="json-editor"
      />
    </div>
    <div v-else class="empty-content">
      <span>{{ placeholder || '暂无内容' }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useTheme } from '@/styles/theme/useTheme'
import CodemirrorEditor from 'codemirror-editor-vue3'

// 导入 CodeMirror 5 的必要文件
import 'codemirror/lib/codemirror.css'
import 'codemirror/mode/javascript/javascript.js'
import 'codemirror/theme/material.css'
import 'codemirror/theme/dracula.css'
import 'codemirror/theme/eclipse.css'
import 'codemirror/addon/fold/foldcode.js'
import 'codemirror/addon/fold/foldgutter.js'
import 'codemirror/addon/fold/brace-fold.js'
import 'codemirror/addon/fold/foldgutter.css'

// Props 定义
interface Props {
  content?: string
  placeholder?: string
  height?: string
  showLineNumbers?: boolean
  enableFolding?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  content: '',
  placeholder: '暂无内容',
  height: '120px',
  showLineNumbers: false,
  enableFolding: false
})

// 获取主题
const { currentTheme } = useTheme()

// 格式化 JSON 字符串
const formattedContent = computed(() => {
  if (!props.content) return ''
  try {
    const parsed = JSON.parse(props.content)
    return JSON.stringify(parsed, null, 2)
  } catch {
    return props.content
  }
})

// CodeMirror 5 配置
const cmOptions = computed(() => {
  const isDark = currentTheme.value.name === 'dark' || currentTheme.value.name === 'cyberpunk'

  const gutters = ['CodeMirror-linenumbers']
  if (props.enableFolding) {
    gutters.push('CodeMirror-foldgutter')
  }

  return {
    mode: {
      name: 'javascript',
      json: true
    },
    theme: isDark ? 'dracula' : 'eclipse',
    lineNumbers: props.showLineNumbers,
    lineWrapping: true,
    readOnly: true, // 只读模式
    foldGutter: props.enableFolding,
    gutters,
    styleActiveLine: false, // 只读模式下不需要高亮当前行
    cursorBlinkRate: -1, // 隐藏光标
    indentUnit: 2,
    tabSize: 2,
    smartIndent: true
  }
})
</script>

<style lang="scss" scoped>
.json-viewer {
  .my-codemirror-container {
    border: 1px solid v-bind('currentTheme.colors.borderColorLight');
    border-radius: 8px;
    display: block;
    width: 100%;
    height: 200px;
    overflow: hidden;
    background: v-bind('currentTheme.colors.background');

    .json-editor {
      font-family: 'Fira Code', 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
      font-size: 12px;
      width: 100%;

      :deep(.CodeMirror) {
        background: v-bind('currentTheme.colors.fillColor') !important;
        color: v-bind('currentTheme.colors.textPrimary') !important;
        font-family: 'Fira Code', 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
        font-size: 12px;
        line-height: 1.6;
        border: none;

        .CodeMirror-lines {
          padding: 12px;
        }

        .CodeMirror-scroll {
          background: v-bind('currentTheme.colors.fillColor') !important;
        }

        .CodeMirror-gutter {
          background: v-bind('currentTheme.colors.fillColorLight') !important;
          border-right: 1px solid v-bind('currentTheme.colors.borderColorLight') !important;
        }

        .CodeMirror-linenumber {
          color: v-bind('currentTheme.colors.textSecondary') !important;
          font-size: 11px;
        }

        .CodeMirror-foldmarker {
          background: v-bind('currentTheme.colors.chatLinkColor') !important;
          color: v-bind('currentTheme.colors.background') !important;
          text-shadow: none;
          border: none;
          border-radius: 3px;
          padding: 1px 3px;
        }

        .CodeMirror-foldgutter {
          width: 16px;
        }

        .CodeMirror-foldgutter-open,
        .CodeMirror-foldgutter-folded {
          color: v-bind('currentTheme.colors.chatLinkColor') !important;
          cursor: pointer;
        }

        .CodeMirror-foldgutter-open:hover,
        .CodeMirror-foldgutter-folded:hover {
          color: v-bind('currentTheme.colors.primary') !important;
        }

        // JSON 语法高亮
        .cm-string {
          color: v-bind('currentTheme.colors.success') !important;
        }

        .cm-number {
          color: v-bind('currentTheme.colors.warning') !important;
        }

        .cm-property {
          color: v-bind('currentTheme.colors.primary') !important;
        }

        .cm-keyword {
          color: v-bind('currentTheme.colors.danger') !important;
        }

        .cm-atom {
          color: v-bind('currentTheme.colors.info') !important;
        }

        .CodeMirror-placeholder {
          color: v-bind('currentTheme.colors.textSecondary') !important;
          font-style: italic;
        }

        // 滚动条样式
        &::-webkit-scrollbar {
          width: 6px;
          height: 6px;
        }

        &::-webkit-scrollbar-track {
          background: v-bind('currentTheme.colors.fillColorLighter');
          border-radius: 3px;
        }

        &::-webkit-scrollbar-thumb {
          background: v-bind('currentTheme.colors.chatLinkColor');
          border-radius: 3px;
          opacity: 0.6;

          &:hover {
            opacity: 0.8;
          }
        }
      }
    }
  }

  .empty-content {
    padding: 20px;
    text-align: center;
    color: v-bind('currentTheme.colors.textSecondary');
    font-style: italic;
    background: v-bind('currentTheme.colors.fillColorLighter');
    border: 1px solid v-bind('currentTheme.colors.borderColorLight');
    border-radius: 8px;
    font-size: 13px;
  }
}
</style>
