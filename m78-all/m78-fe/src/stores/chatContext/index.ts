import { ref } from 'vue'
import { defineStore } from 'pinia'

export interface ChatContextMsg {
  content: string
  role: Chat.Role
}

export interface ChatContext {
  isAllow: boolean
  msgList: ChatContextMsg[]
}

export const useChatContextStore = defineStore('chat-context', () => {
  const chatContext = ref<ChatContext>({
    isAllow: true,
    msgList: []
  })

  function addChatContext(context: ChatContextMsg) {
    const msgList = chatContext.value.msgList
    if (msgList.length < 10) {
      chatContext.value.msgList = [...msgList, context]
    } else {
      msgList.shift()
      chatContext.value.msgList = [...msgList, context]
    }
  }

  function disableContext() {
    chatContext.value.isAllow = false
  }

  function enableContext() {
    chatContext.value.isAllow = true
  }

  function clearContext() {
    chatContext.value.msgList = []
  }

  return { chatContext, addChatContext, disableContext, enableContext, clearContext }
})
