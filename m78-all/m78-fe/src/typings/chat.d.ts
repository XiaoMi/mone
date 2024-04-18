declare namespace Chat {
  type Role = 'USER' | 'SYSTEM' | 'ASSISTANT'

  interface ChatContext {
    content: string
    role: Role
  }

  interface Chat {
    id: string | number
    dateTime: string
    text: string
    inversion?: boolean
    error?: boolean
    showCursor?: boolean
    loading?: boolean
    conversationOptions?: ConversationRequest | null
    requestOptions: {
      prompt: string
      chatContext: ChatContext[] | null
      options?: ConversationRequest | null
    }
  }

  interface KnowledgeConfig {
    knowledgeBaseId: string
    fileIdList: string[]
  }

  interface History {
    uuid: number
    title: string
    isEdit: boolean
    knowledgeConfig?: KnowledgeConfig
  }

  interface ChatState {
    active: number | null
    homeInput: string
    usingContext: boolean
    history: History[]
    chat: {
      uuid: number
      data: Chat[]
    }[]
  }

  interface ConversationRequest {
    conversationId?: string
    parentMessageId?: string
  }

  interface ConversationResponse {
    conversationId: string
    detail: {
      choices: { finish_reason: string; index: number; logprobs: any; text: string }[]
      created: number
      id: string
      model: string
      object: string
      usage: { completion_tokens: number; prompt_tokens: number; total_tokens: number }
    }
    id: string
    parentMessageId: string
    role: string
    text: string
  }
}
