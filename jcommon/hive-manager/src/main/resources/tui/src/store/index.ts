import { create } from 'zustand'
import type { User, Agent, AgentInstance, Message, Screen, Task } from '../types'

interface AppState {
  // Auth
  user: User | null
  token: string | null
  setUser: (user: User | null) => void
  setToken: (token: string | null) => void

  // Navigation
  currentScreen: Screen
  setScreen: (screen: Screen) => void

  // Agent
  selectedAgent: Agent | null
  selectedInstance: AgentInstance | null
  setAgent: (agent: Agent | null) => void
  setInstance: (instance: AgentInstance | null) => void

  // Messages
  messages: Message[]
  addMessage: (message: Message) => void
  setMessages: (messages: Message[]) => void
  clearMessages: () => void

  // Loading states
  loading: boolean
  setLoading: (loading: boolean) => void
}

export const useAppStore = create<AppState>((set) => ({
  // Auth
  user: null,
  token: null,
  setUser: (user) => set({ user }),
  setToken: (token) => set({ token }),

  // Navigation
  currentScreen: 'login',
  setScreen: (screen) => set({ currentScreen: screen }),

  // Agent
  selectedAgent: null,
  selectedInstance: null,
  setAgent: (agent) => set({ selectedAgent: agent }),
  setInstance: (instance) => set({ selectedInstance: instance }),

  // Messages
  messages: [],
  addMessage: (message) => set((state) => ({
    messages: [...state.messages, message]
  })),
  setMessages: (messages) => set({ messages }),
  clearMessages: () => set({ messages: [] }),

  // Loading
  loading: false,
  setLoading: (loading) => set({ loading }),
}))
