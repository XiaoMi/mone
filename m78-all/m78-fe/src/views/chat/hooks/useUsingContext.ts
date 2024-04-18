import { computed } from 'vue'
import { ElMessage } from 'element-plus'
import 'element-plus/theme-chalk/src/message.scss'
import { t } from '@/locales'
import { useChatStore } from '@/stores/chat'

export function useUsingContext() {
  const chatStore = useChatStore()
  const usingContext = computed<boolean>(() => chatStore.usingContext)

  function toggleUsingContext() {
    chatStore.setUsingContext(!usingContext.value)
    if (usingContext.value) ElMessage.success(t('chat.turnOnContext'))
    else ElMessage.warning(t('chat.turnOffContext'))
  }

  return {
    usingContext,
    toggleUsingContext
  }
}
