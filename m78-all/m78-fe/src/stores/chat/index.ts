import { defineStore } from 'pinia'
import {
  getChatMessageList,
  addChatMessage,
  delChatMessage,
  clearChatMessage,
  addChat as createChat,
  getChatList,
  delChat,
  updateChat
} from '@/api/chat'
import { getLocalState, setLocalState } from './helper'

export const useChatStore = defineStore('chat-store', {
  state: (): Chat.ChatState => getLocalState(),

  getters: {
    getChatHistoryByCurrentActive(state: Chat.ChatState) {
      const index = state.history.findIndex((item) => item.uuid === state.active)
      if (index !== -1) return state.history[index]
      return null
    },

    getChatByUuid(state: Chat.ChatState) {
      return (uuid?: number) => {
        if (uuid) return state.chat.find((item) => item.uuid === uuid)?.data ?? []
        return state.chat.find((item) => item.uuid === state.active)?.data ?? []
      }
    }
  },

  actions: {
    updateHomeInput(prompt: string) {
      this.homeInput = prompt
      this.recordState()
    },

    setUsingContext(context: boolean) {
      this.usingContext = context
      this.recordState()
    },

    async addHistory(form: { title: string }, chatData: Chat.Chat[] = []) {
      const { code, data } = await createChat({
        title: form.title,
        description: ''
      })
      if (code == 0 && data) {
        const uuid = data.id
        this.history.unshift({
          uuid,
          title: form.title,
          isEdit: false
        })
        this.chat.unshift({ uuid: uuid, data: chatData })
        this.active = uuid
        this.recordState()
      }

      return code
    },

    async updateHistory(uuid: number, edit: Partial<Chat.History>) {
      const index = this.history.findIndex((item) => item.uuid === uuid)
      if (index !== -1) {
        const history = this.history[index]
        await updateChat({
          id: uuid,
          title: history.title,
          knowledgeConfig:
            history.knowledgeConfig || edit.knowledgeConfig
              ? {
                  ...history?.knowledgeConfig,
                  ...edit?.knowledgeConfig
                }
              : null
        })
        this.history[index] = { ...history, ...edit }
        this.recordState()
      }
    },

    async initHistory() {
      const that = this
      that.history = []
      that.chat = []
      try {
        const { code, data } = await getChatList()
        if (code == 0 && Array.isArray(data)) {
          const history = (that.history = data
            .map((item) => {
              return {
                title: item.title,
                uuid: item.id,
                knowledgeConfig: item.knowledgeConfig,
                isEdit: false
              }
            })
            .reverse())
          if (history.findIndex((item) => item.uuid == this.active) < 0) {
            this.active = history[0]?.uuid
          }
          for (const it of history) {
            const { code, data } = await getChatMessageList({
              topicId: it.uuid
            })
            if (code == 0 && Array.isArray(data)) {
              const chatList = data.map((it) => {
                //  将历史数据loading全部置为false
                return { ...JSON.parse(it.meta.$$FeChat || '{}'), id: it.id, loading: false }
              })
              // console.log('chatList:', chatList)
              this.chat.unshift({ uuid: it.uuid, data: chatList })
            } else {
              this.chat.unshift({ uuid: it.uuid, data: [] })
            }
          }
        }
      } catch (e) {
        console.error(e)
      }
      this.recordState()
    },

    async deleteHistory(index: number) {
      const history = this.history[index]
      await delChat({
        topicId: history.uuid
      })

      this.history.splice(index, 1)
      this.chat.splice(index, 1)

      if (this.history.length === 0) {
        this.active = null
        this.recordState()
        return
      }

      if (index > 0 && index <= this.history.length) {
        const uuid = this.history[index - 1].uuid
        this.active = uuid
        this.recordState()
        return
      }

      if (index === 0) {
        if (this.history.length > 0) {
          const uuid = this.history[0].uuid
          this.active = uuid
          this.recordState()
        }
      }

      if (index > this.history.length) {
        const uuid = this.history[this.history.length - 1].uuid
        this.active = uuid
        this.recordState()
      }
    },

    async setActive(uuid: number) {
      this.active = uuid
      return await this.recordState()
    },

    getChatByUuidAndIndex(uuid: number, index: number) {
      if (!uuid || uuid === 0) {
        if (this.chat.length) return this.chat[0].data[index]
        return null
      }
      const chatIndex = this.chat.findIndex((item) => item.uuid === uuid)
      if (chatIndex !== -1) return this.chat[chatIndex].data[index]
      return null
    },

    addChatByUuid(uuid: number, chat: Chat.Chat) {
      if (!uuid || uuid === 0) {
        if (this.history.length === 0) {
          const uuid = Date.now()
          this.history.push({ uuid, title: chat.text, isEdit: false })
          this.chat.push({ uuid, data: [chat] })
          this.active = uuid
          this.recordState()
        } else {
          this.chat[0].data.push(chat)
          if (this.history[0].title === 'New Chat') this.history[0].title = chat.text
          this.recordState()
        }
      }

      const index = this.chat.findIndex((item) => item.uuid === uuid)
      if (index !== -1) {
        this.chat[index].data.push(chat)
        if (this.history[index].title === 'New Chat') this.history[index].title = chat.text
        this.recordState()
      }
    },

    async updateChatMessageByUuid(uuid: number, index: number, chat: Chat.Chat, role: Chat.Role) {
      const { code, data } = await addChatMessage({
        topicId: uuid,
        message: chat.text,
        messageRole: role,
        meta: {
          $$FeChat: JSON.stringify(chat)
        }
      })
      console.log(data)
      if (code == 0 && data) {
        chat.id = data.id
        // this.updateChatByUuid(uuid, index, chat)
      }
    },

    updateChatByUuid(uuid: number, index: number, chat: Chat.Chat) {
      if (!uuid || uuid === 0) {
        if (this.chat.length) {
          this.chat[0].data[index] = chat
          this.recordState()
        }
        return
      }

      const chatIndex = this.chat.findIndex((item) => item.uuid === uuid)
      if (chatIndex !== -1) {
        this.chat[chatIndex].data[index] = chat
        this.recordState()
      }
    },

    updateChatSomeByUuid(uuid: number, index: number, chat: Partial<Chat.Chat>) {
      if (!uuid || uuid === 0) {
        if (this.chat.length) {
          this.chat[0].data[index] = { ...this.chat[0].data[index], ...chat }
          this.recordState()
        }
        return
      }

      const chatIndex = this.chat.findIndex((item) => item.uuid === uuid)
      if (chatIndex !== -1) {
        this.chat[chatIndex].data[index] = { ...this.chat[chatIndex].data[index], ...chat }
        this.recordState()
      }
    },

    async deleteChatByUuid(uuid: number, index: number) {
      if (!uuid || uuid === 0) {
        if (this.chat.length) {
          this.chat[0].data.splice(index, 1)
          this.recordState()
        }
        return
      }

      const chatIndex = this.chat.findIndex((item) => item.uuid === uuid)
      if (chatIndex !== -1) {
        const chat = this.chat[chatIndex].data[index]
        await delChatMessage({
          messageId: chat.id
        })
        this.chat[chatIndex].data.splice(index, 1)
        this.recordState()
      }
    },

    async clearChatByUuid(uuid: number) {
      const { code } = await clearChatMessage({
        topicId: uuid
      })
      if (code == 0) {
        if (!uuid || uuid === 0) {
          if (this.chat.length) {
            this.chat[0].data = []
            this.recordState()
          }
          return
        }

        const index = this.chat.findIndex((item) => item.uuid === uuid)
        if (index !== -1) {
          this.chat[index].data = []
          this.recordState()
        }
      }
    },

    recordState() {
      setLocalState(this.$state)
    }
  }
})
