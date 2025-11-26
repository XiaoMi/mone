<template>
  <div class="mcp-config-editor">
    <div class="editor-header">
      <h3>编辑 MCP 配置</h3>
      <div class="editor-actions">
        <el-button @click="handleCancel" size="small">取消</el-button>
        <el-button type="primary" @click="handleSave" size="small">保存</el-button>
      </div>
    </div>
    <div class="editor-content">
      <!-- 尝试使用CodeMirror -->
      <div v-if="useCodeMirror" class="codemirror-wrapper">
        <CodemirrorEditor
          v-model:value="configContent"
          :options="cmOptions"
          placeholder="请输入 MCP 配置 JSON 内容..."
          class="config-editor"
          :style="{ height: '400px' }"
        />
      </div>
      <!-- 后备方案：普通textarea -->
      <div v-else class="textarea-wrapper">
        <el-input
          v-model="configContent"
          type="textarea"
          :rows="20"
          placeholder="请输入 MCP 配置 JSON 内容..."
          class="config-textarea"
        />
      </div>
      <div class="editor-controls">
        <el-button size="small" @click="toggleEditor">
          切换到 {{ useCodeMirror ? 'Textarea' : 'CodeMirror' }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, nextTick, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useTheme } from '@/styles/theme/useTheme'
import CodemirrorEditor from 'codemirror-editor-vue3'

// 导入 CodeMirror 5 的必要文件
import 'codemirror/addon/display/placeholder.js'
import 'codemirror/mode/javascript/javascript.js'
import 'codemirror/theme/material.css'
import 'codemirror/theme/dracula.css'
import 'codemirror/theme/monokai.css'
import 'codemirror/theme/eclipse.css'
import 'codemirror/addon/edit/closebrackets.js'
import 'codemirror/addon/edit/matchbrackets.js'
import 'codemirror/addon/fold/foldcode.js'
import 'codemirror/addon/fold/foldgutter.js'
import 'codemirror/addon/fold/brace-fold.js'
import 'codemirror/addon/fold/foldgutter.css'
import 'codemirror/addon/lint/lint.js'
import 'codemirror/addon/lint/lint.css'
import 'codemirror/addon/lint/json-lint.js'

// 声明 jsonlint 模块类型
declare module 'jsonlint-mod' {
  const jsonlint: {
    parse: (text: string) => any
  }
  export default jsonlint
}

import jsonlint from 'jsonlint-mod'

// 添加 CodeMirror 类型声明
interface CodeMirrorInstance {
  foldCode: (pos: { line: number; ch: number }) => void
  getCursor: () => { line: number; ch: number }
  somethingSelected: () => boolean
  indentSelection: (direction: string) => void
  replaceSelection: (text: string, collapse?: string) => void
}

// 添加 jsonlint 到全局对象
if (typeof window !== 'undefined') {
  window.jsonlint = jsonlint
}

// Props定义
interface Props {
  modelValue: boolean
  initialContent?: string
}

// Emits定义
interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'save', content: string): void
  (e: 'cancel'): void
}

const props = withDefaults(defineProps<Props>(), {
  initialContent: ''
})

const emit = defineEmits<Emits>()

// 获取主题
const { currentTheme } = useTheme()

const configContent = ref('')
const useCodeMirror = ref(true)

// CodeMirror 5 选项配置
const cmOptions = computed(() => {
  const isDark = currentTheme.value.name === 'dark' || currentTheme.value.name === 'cyberpunk'

  return {
    mode: {
      name: 'javascript',
      json: true
    }, // JSON 模式
    theme: isDark ? 'dracula' : 'eclipse', // 根据主题选择
    lineNumbers: true,
    lineWrapping: true,
    foldGutter: true,
    gutters: ['CodeMirror-linenumbers', 'CodeMirror-foldgutter', 'CodeMirror-lint-markers'],
    autoCloseBrackets: true,
    matchBrackets: true,
    showCursorWhenSelecting: true,
    indentUnit: 2,
    tabSize: 2,
    indentWithTabs: false,
    smartIndent: true,
    lint: true,
    extraKeys: {
      'Ctrl-Space': 'autocomplete',
      'Ctrl-/': 'toggleComment',
      'Cmd-/': 'toggleComment',
      'Ctrl-Q': function(cm: CodeMirrorInstance) { cm.foldCode(cm.getCursor()); },
      'Cmd-Q': function(cm: CodeMirrorInstance) { cm.foldCode(cm.getCursor()); },
      'Tab': function(cm: CodeMirrorInstance) {
        if (cm.somethingSelected()) {
          cm.indentSelection('add');
        } else {
          cm.replaceSelection('  ', 'end');
        }
      },
      'Ctrl-S': () => handleSave(),
      'Cmd-S': () => handleSave()
    },
    styleActiveLine: true,
    cursorBlinkRate: 530,
    workTime: 200,
    workDelay: 300
  }
})

// 监听初始内容变化
watch(() => props.initialContent, (newContent) => {
  if (newContent && newContent !== configContent.value) {
    configContent.value = newContent
  }
}, { immediate: true })

// 切换编辑器模式
const toggleEditor = () => {
  useCodeMirror.value = !useCodeMirror.value
}

// 处理保存
const handleSave = async () => {
  try {
    // 验证JSON格式
    if (configContent.value.trim()) {
      JSON.parse(configContent.value)
    }

    emit('save', configContent.value)
    ElMessage.success('配置保存成功')
  } catch (error) {
    if (error instanceof SyntaxError) {
      ElMessage.error('JSON格式错误，请检查配置内容')
    } else {
      ElMessage.error('保存配置失败')
    }
    console.error(error)
  }
}

// 处理取消
const handleCancel = () => {
  emit('cancel')
  configContent.value = ''
}

// 导出方法供父组件调用
defineExpose({
  setContent: (content: string) => {
    configContent.value = content
  },
  getContent: () => configContent.value,
  clearContent: () => {
    configContent.value = ''
  }
})
</script>

<style lang="scss" scoped>
.mcp-config-editor {
  height: 100%;
  display: flex;
  flex-direction: column;

  .editor-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid v-bind('currentTheme.colors.borderColorLight');

    h3 {
      margin: 0;
      color: v-bind('currentTheme.colors.textPrimary');
      font-size: 18px;
      font-weight: 600;
    }

    .editor-actions {
      display: flex;
      gap: 8px;
    }
  }

  .editor-content {
    flex: 1;

    .codemirror-wrapper {
      .config-editor {
        height: 400px;
        width: 100%;
        font-family: 'Fira Code', 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
        border: 1px solid v-bind('currentTheme.colors.borderColorLight');
        border-radius: 6px;
        overflow: hidden;

        :deep(.CodeMirror) {
          height: 100%;
          background: v-bind('currentTheme.colors.fillColor') !important;
          color: v-bind('currentTheme.colors.textPrimary') !important;
          font-family: 'Fira Code', 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
          font-size: 13px;
          line-height: 1.6;

          &.CodeMirror-focused {
            outline: none;
            border-color: v-bind('currentTheme.colors.primary');
          }

          .CodeMirror-lines {
            padding: 16px;
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
          }

          .CodeMirror-cursor {
            border-left: 1px solid v-bind('currentTheme.colors.primary') !important;
          }

          .CodeMirror-selected {
            background: v-bind('currentTheme.colors.chatBorderGlow') !important;
          }

          .CodeMirror-matchingbracket {
            background: v-bind('currentTheme.colors.success') !important;
            color: v-bind('currentTheme.colors.background') !important;
          }

          .CodeMirror-nonmatchingbracket {
            background: v-bind('currentTheme.colors.danger') !important;
            color: v-bind('currentTheme.colors.background') !important;
          }

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

          .CodeMirror-activeline-background {
            background: v-bind('currentTheme.colors.fillColorLight') !important;
          }

          .CodeMirror-lint-marker-error {
            background: v-bind('currentTheme.colors.danger') !important;
            border-radius: 2px;
            color: v-bind('currentTheme.colors.background') !important;
            cursor: pointer;
          }

          .CodeMirror-lint-marker-warning {
            background: v-bind('currentTheme.colors.warning') !important;
            border-radius: 2px;
            color: v-bind('currentTheme.colors.background') !important;
            cursor: pointer;
          }

          .CodeMirror-lint-tooltip {
            background: v-bind('currentTheme.colors.chatWindowBackground') !important;
            border: 1px solid v-bind('currentTheme.colors.borderColorLight') !important;
            border-radius: 6px;
            color: v-bind('currentTheme.colors.textPrimary') !important;
            font-family: inherit;
            font-size: 12px;
            padding: 8px 12px;
            box-shadow: 0 4px 12px v-bind('currentTheme.colors.chatBorderGlow') !important;
            backdrop-filter: blur(10px);
          }

          .CodeMirror-search-field {
            background: v-bind('currentTheme.colors.fillColor') !important;
            border: 1px solid v-bind('currentTheme.colors.borderColorLight') !important;
            color: v-bind('currentTheme.colors.textPrimary') !important;
          }

          .CodeMirror-search-field:focus {
            border-color: v-bind('currentTheme.colors.primary') !important;
            outline: none;
          }
        }
      }
    }

    .textarea-wrapper {
      .config-textarea {
        :deep(.el-textarea__inner) {
          background: v-bind('currentTheme.colors.fillColor');
          border: 1px solid v-bind('currentTheme.colors.borderColorLight');
          color: v-bind('currentTheme.colors.textPrimary');
          font-family: 'Fira Code', 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
          font-size: 13px;
          line-height: 1.6;
          border-radius: 6px;
          resize: none;

          &:focus {
            border-color: v-bind('currentTheme.colors.primary');
            box-shadow: 0 0 0 2px v-bind('currentTheme.colors.chatBorderGlow');
          }

          &::placeholder {
            color: v-bind('currentTheme.colors.textSecondary');
          }
        }
      }
    }

    .editor-controls {
      margin-top: 12px;
      text-align: right;
    }
  }

  // Element Plus 组件样式覆盖
  :deep(.el-button) {
    border-color: v-bind('currentTheme.colors.borderColorLight');
    color: v-bind('currentTheme.colors.textPrimary');
    background: v-bind('currentTheme.colors.fillColor');
    transition: all 0.3s ease;

    &:hover {
      border-color: v-bind('currentTheme.colors.chatLinkColor');
      color: v-bind('currentTheme.colors.chatLinkColor');
      background: v-bind('currentTheme.colors.fillColorLight');
    }

    &.el-button--primary {
      background: v-bind('currentTheme.colors.primary');
      border-color: v-bind('currentTheme.colors.primary');
      color: v-bind('currentTheme.colors.background');

      &:hover {
        background: v-bind('currentTheme.colors.chatLinkColor');
        border-color: v-bind('currentTheme.colors.chatLinkColor');
      }
    }

    &.el-button--small {
      font-size: 12px;
      padding: 6px 12px;
    }
  }
}
</style>
