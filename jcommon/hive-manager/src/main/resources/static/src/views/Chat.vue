<template>
  <div class="chat-container">
    <div id="particles-js"></div>
    
    <!-- 文件管理器切换按钮 -->
    <div v-if="showFileManagerButton" class="file-manager-toggle" @click="toggleFileManager">
      <el-icon :size="20">
        <component :is="showFileManager ? 'Close' : 'Folder'" />
      </el-icon>
    </div>

    <!-- 主内容区域 - flex布局 -->
    <div class="main-content">
      <!-- 文件管理器 -->
      <transition name="slide-fade">
        <div v-if="showFileManager" class="file-manager-wrapper">
          <FileManagerCore :adapter="fileAdapter" :mode="fileManagerMode" />
        </div>
      </transition>

      <!-- 聊天窗口 -->
      <div class="chat-window-wrapper">
        <ChatWindow
      placeholder="placeholder"
      :messageList="list"
      :isOpen="true"
      :alwaysScrollToBottom="true"
      :onMessageClick="messageClick"
      :onMessageCmd="onMessageCmd"
      :onUserInputSubmit="sendMessage"
      :initCodePrompt="initCodePrompt"
      @scrollToTop="scrollToTop"
      :changeSendMethod="toggleSendMethod"
      :onPlayAudio="onPlayAudio"
      :onClearHistory="onClearHistory"
      :onOffline="onOfflineAgent"
      :onStopMsg="onStopMsg"
      :onSwitchAgent="sendSwitchAgentCommand"
      :onSwitchLlm="sendSwitchLlmCommand"
      :onExecuteMcpCommand="sendMcpCommand"
      :onExecuteSystemCommand="sendSystemCommand"
      @pidAction="onPidAction"
      @onClick2Conversion="onClick2Conversion"
    />
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import ChatWindow from '@/components/Chat/ChatWindow.vue'
import { FileManagerCore, LocalFileSystemAdapter, WebSocketFileSystemAdapter } from '@/components/FileManager'
import { Close, Folder } from '@element-plus/icons-vue'
import { type MessageClickPayload } from "@/components/Chat/messages/HelloMessage.vue";
import { useUserStore } from '@/stores/user'
import { useChatContextStore, type Message } from '@/stores/chat-context'
import { onMounted, onBeforeUnmount, computed, ref, nextTick } from 'vue'
import { getAgentDetail, clearHistory, offlineAgent } from '@/api/agent'
import { useRoute } from 'vue-router'
import { connectWebSocket } from '@/api/wsConnect'
import { streamChat, textToVoice } from '@/api/message'
import { v4 as uuidv4 } from 'uuid'
import { fluxCodeHandler } from '@/components/Chat/common/result-code'
import { useTheme } from '@/styles/theme/useTheme'
import { useFunctionPanelStore } from '@/stores/function-panel'
import { useEditStore } from '@/stores/edit'
import { useAgentConfigStore } from '@/stores/agent-config'

const route = useRoute()
const {
  getChatContext,
  setMessageList,
  addMessage,
  setProject,
  setModule,
  setLoading,
  messageList,
  updateTokenUsage,
  resetTokenUsage,
} = useChatContextStore()
const { user, setAgent, setInstance, getSelectedInstance, getAgent } = useUserStore()
const functionPanelStore = useFunctionPanelStore()
const { setShowFollow } = useEditStore()
const { setAgentConfig, clearAgentConfig } = useAgentConfigStore()
const socket = ref<WebSocket | null>(null)
const uuid = ref<string>(route.query.conversationId as string)
const messageId = ref<string>('')
const sendMethod = ref<string>('ws')
const list = computed(() => {
  return messageList
})

// 文件管理器相关
const showFileManager = ref(false)
const fileManagerMode = ref<'local' | 'websocket'>('local')
const showFileManagerButton = ref(false)

// 获取忽略模式配置
const getIgnorePatterns = (): string[] | undefined => {
  const urlParams = new URLSearchParams(window.location.search)
  const ignoreParam = urlParams.get('ignore')
  return ignoreParam ? ignoreParam.split(',') : undefined
}

const fileAdapter = ref<LocalFileSystemAdapter | WebSocketFileSystemAdapter>(
  new LocalFileSystemAdapter(getIgnorePatterns())
)

const toggleFileManager = () => {
  showFileManager.value = !showFileManager.value
  
  // 首次打开文件管理器时，根据agent连接信息决定使用哪种模式
  if (showFileManager.value && fileManagerMode.value === 'local') {
    initFileManager()
  }
}

// 初始化文件管理器
const initFileManager = () => {
  const agent = getAgent()
  const instance = getSelectedInstance()
  
  // 如果有agent和实例信息，使用WebSocket模式
  if (agent?.name && instance?.ip && instance?.port) {
    fileManagerMode.value = 'websocket'
    
    // 构建WebSocket代理URL
    // 1. 首先构建目标WebSocket地址 (agent的实际连接地址)
    const clientId = `${agent.name}:${agent.group}:${agent.version}:${instance.ip}:${instance.port}`
    const urlParams = new URLSearchParams(window.location.search)
    const targetWsUrl = urlParams.get('wsUrl') || `ws://127.0.0.1:8080/ws/agent/chat`
    const targetWsWithClient = `${targetWsUrl}?clientId=${encodeURIComponent(clientId)}`
    
    // 2. 使用代理连接到目标WebSocket
    const proxyWsUrl = `/api/manager/ws/proxy?wsUrl=${encodeURIComponent(targetWsWithClient)}`
    
    console.log('[FileManager] 使用代理连接:', proxyWsUrl)
    console.log('[FileManager] 目标地址:', targetWsWithClient)
    
    // 支持通过URL参数指定忽略模式
    const ignoreParam = urlParams.get('ignore')
    const customIgnorePatterns = ignoreParam ? ignoreParam.split(',') : undefined
    
    const wsAdapter = new WebSocketFileSystemAdapter(
      proxyWsUrl,
      () => {
        console.log('[FileManager] WebSocket proxy connected')
        // 连接成功后，可以加载默认目录
      },
      () => {
        console.log('[FileManager] WebSocket proxy disconnected')
      },
      (error) => {
        console.error('[FileManager] WebSocket proxy error:', error)
      },
      customIgnorePatterns
    )
    
    // 连接WebSocket
    wsAdapter.connect().then(() => {
      fileAdapter.value = wsAdapter
      console.log('[FileManager] 代理连接成功')
    }).catch((error) => {
      console.error('[FileManager] Failed to connect via proxy:', error)
      // 如果连接失败，回退到本地模式
      fileManagerMode.value = 'local'
      fileAdapter.value = new LocalFileSystemAdapter(getIgnorePatterns())
    })
  } else {
    // 没有agent信息，使用本地模式
    fileManagerMode.value = 'local'
    fileAdapter.value = new LocalFileSystemAdapter(getIgnorePatterns())
  }
}

// 用于存储每个 uuid 对应的未处理数据
const catches = new Map<string, any>()

// 节流函数
function throttle(
  func: (data: any, uuid: string) => void,
  delay: number
): (data: any, uuid: string) => void {
  let timeoutId: ReturnType<typeof setTimeout> | null = null;
  let lastExecTime = 0;

  return (data: any, uuid: string) => {
    catches.set(uuid, (catches.get(uuid) || '') + data)
    const currentTime = Date.now();

    if (currentTime - lastExecTime >= delay) {
      // 立即执行
      Array.from(catches.entries()).forEach(([key, value]) => {
        func(value, key);
      });
      catches.clear();
      lastExecTime = currentTime;
    } else if (timeoutId === null) {
      // 设置延迟执行，确保最后一次调用也能被执行
      timeoutId = setTimeout(() => {
        Array.from(catches.entries()).forEach(([key, value]) => {
          func(value, key);
        });
        catches.clear();
        lastExecTime = Date.now();
        timeoutId = null;
      }, delay - (currentTime - lastExecTime));
    }
  };
}

const throttledFluxCodeHandler = throttle(fluxCodeHandler, 1000);

const toggleSendMethod = (val: string) => {
  sendMethod.value = val
  if (val === 'ws') {
    const agent = getAgent()
    const instance = getSelectedInstance()

    if (
      agent?.name &&
      agent?.group &&
      agent?.version &&
      instance?.ip &&
      typeof instance.port !== 'undefined'
    ) {
      uuid.value = `${agent.name}:${agent.group}:${agent.version}:${instance.ip}:${instance.port}`
    } else {
      console.warn('缺少构建WebSocket UUID所需的Agent或实例信息，将使用已有UUID')
    }

    // 连接websocket
    socket.value = connectWebSocket(
      uuid.value,
      () => {
        console.log('WebSocket connection opened')
        // sendCreateCommand()
      },
      () => {
        console.log('WebSocket connection closed')
      },
      (data: any) => {
        try {
          const usageData = JSON.parse(data.match(/<usage>([\s\S]*?)<\/usage>/)?.[1]?.trim())

          updateTokenUsage(usageData.inputTokens, usageData.outputTokens)
        } catch (e) {
          //
        }
        throttledFluxCodeHandler(data, messageId.value)
      }
    )
  } else {
    // 断开websocket
    socket.value?.close()
  }
}

const scrollToTop = () => {
  // 滚动到顶部
}

const getAgentName = () => {
  const agent = getAgent()
  const name = `stream_${agent!.name}_chat`
  if (agent?.mcpToolMap) {
    try {
      const toolMap = JSON.parse(agent.mcpToolMap)
      const item = Object.values(toolMap)[0]
      const tool = JSON.parse(item as string)
      if (tool.name === name) {
        return tool.name
      }
    } catch (error) {
      // return "";
    }
  }
  return name
}
const onClick2Conversion = ({ id }: { id: string }) => {
  const agent = getAgent()
  let params = {
    message: `/rollback  ${id}`,
    __owner_id__: user?.username,
  }
  clearHistory({
    mapData: {
      outerTag: 'use_mcp_tool',
      server_name: `${agent!.name}:${agent!.group}:${agent!.version}:${getSelectedInstance().ip}:${
        getSelectedInstance().port
      }`,
      tool_name: getAgentName(),
      arguments: JSON.stringify(params),
    },
    conversationId: route.query.conversationId,
    agentId: route.query.serverAgentId,
    agentInstance: getSelectedInstance(),
  })
}

const onClearHistory = () => {
  const agent = getAgent()
  let params = {
    message: `/clear`,
    __owner_id__: user?.username,
  }
  clearHistory({
    mapData: {
      outerTag: 'use_mcp_tool',
      server_name: `${agent!.name}:${agent!.group}:${agent!.version}:${getSelectedInstance().ip}:${
        getSelectedInstance().port
      }`,
      tool_name: getAgentName(),
      arguments: JSON.stringify(params),
    },
    conversationId: route.query.conversationId,
    agentId: route.query.serverAgentId,
    agentInstance: getSelectedInstance(),
  })
}
const onOfflineAgent = () => {
  const agent = getAgent()
  let params = {
    message: `/exit`,
    __owner_id__: user?.username,
  }
  offlineAgent({
    mapData: {
      outerTag: 'use_mcp_tool',
      server_name: `${agent!.name}:${agent!.group}:${agent!.version}:${getSelectedInstance().ip}:${
        getSelectedInstance().port
      }`,
      tool_name: getAgentName(),
      arguments: JSON.stringify(params),
    },
    conversationId: route.query.conversationId,
    agentId: route.query.serverAgentId,
    agentInstance: getSelectedInstance(),
  })
}

const onPidAction = (data: { pid: string; action: string }) => {
  const agent = getAgent()
  let params = {
    message: `${data.action} ${data.pid}`,
    __owner_id__: user?.username,
  }
  clearHistory({
    mapData: {
      outerTag: 'use_mcp_tool',
      server_name: `${agent!.name}:${agent!.group}:${agent!.version}:${getSelectedInstance().ip}:${
        getSelectedInstance().port
      }`,
      tool_name: getAgentName(),
      arguments: JSON.stringify(params),
    },
    conversationId: route.query.conversationId,
    agentId: route.query.serverAgentId,
    agentInstance: getSelectedInstance(),
  })
}
const initCodePrompt = () => {
  setMessageList([])
  //   this.getCodePrompt();
}
const onPlayAudio = (text: string) => {
  try {
    console.log('onPlayAudio', text)
    const agent = getAgent()
    messageId.value = uuidv4()
    let params = {
      message: `语音合成处理以下文本内容："${text}"`,
      __owner_id__: user?.username,
      __web_search__: functionPanelStore.webSearchEnabled || false,
      __rag__: functionPanelStore.ragEnabled || false,
    }
    if (sendMethod.value === 'sse') {
      // sse发送消息
      streamChat(
        {
          mapData: {
            outerTag: 'use_mcp_tool',
            server_name: `${agent!.name}:${agent!.group}:${agent!.version}:${
              getSelectedInstance().ip
            }:${getSelectedInstance().port}`,
            tool_name: getAgentName(),
            arguments: JSON.stringify(params),
          },
          conversationId: route.query.conversationId,
          agentId: route.query.serverAgentId,
          agentInstance: getSelectedInstance(),
        },
        (data: any) => {
          if (data) {
            fluxCodeHandler(data, messageId.value)
          }
        }
      )
    } else {
      // ws发送消息
      socket.value?.send(
        JSON.stringify({
          mapData: {
            outerTag: 'use_mcp_tool',
            server_name: `${agent!.name}:${agent!.group}:${agent!.version}:${getSelectedInstance().ip}:${
              getSelectedInstance().ip
            }:${getSelectedInstance().port}`,
            tool_name: getAgentName(),
            arguments: JSON.stringify(params),
          },
          agentId: route.query.serverAgentId,
          agentInstance: getSelectedInstance(),
        })
      )
    }
  } catch (error) {
    console.error('发送消息失败:', error)
  }
}
const sendCreateCommand = async () => {
  try {
    const agent = getAgent()
    if (!agent) {
      console.error('Agent not found')
      return
    }

    messageId.value = uuidv4()
    const params = {
      message: '/create',
      __owner_id__: user?.username,
      __web_search__: functionPanelStore.webSearchEnabled || false,
      __rag__: functionPanelStore.ragEnabled || false,
    }

    // sse发送消息
    await streamChat(
      {
        mapData: {
          outerTag: 'use_mcp_tool',
          server_name: `${agent.name}:${agent.group}:${agent.version}:${
            getSelectedInstance().ip
          }:${getSelectedInstance().port}`,
          tool_name: getAgentName(),
          arguments: JSON.stringify(params),
        },
        conversationId: route.query.conversationId,
        agentId: route.query.serverAgentId,
        agentInstance: getSelectedInstance(),
      },
      () => {}
    )

    // 发送完/create后，延迟发送/config命令
    setTimeout(() => {
      sendConfigCommand()
    }, 0)
  } catch (error) {
    console.error('发送/create命令失败:', error)
  }
}

const sendConfigCommand = async () => {
  try {
    const agent = getAgent()
    if (!agent) {
      console.error('Agent not found')
      return
    }

    const params = {
      message: '/config',
      __owner_id__: user?.username,
      __web_search__: functionPanelStore.webSearchEnabled || false,
      __rag__: functionPanelStore.ragEnabled || false,
    }

    // sse发送消息
    const response = await streamChat(
      {
        mapData: {
          outerTag: 'use_mcp_tool',
          server_name: `${agent.name}:${agent.group}:${agent.version}:${
            getSelectedInstance().ip
          }:${getSelectedInstance().port}`,
          tool_name: getAgentName(),
          arguments: JSON.stringify(params),
        },
        conversationId: route.query.conversationId,
        agentId: route.query.serverAgentId,
        agentInstance: getSelectedInstance(),
      },
      () => {}
    )
    console.log('config response>>', response)
    handleConfigResponse(response.data)
  } catch (error) {
    console.error('发送/config命令失败:', error)
  }
}

const handleConfigResponse = (rawData: string) => {
  try {
    // 先处理消息显示
    // throttledFluxCodeHandler(data, configMessageId)
    // 去除data:前缀
    let data = ''
    rawData.split('\n').forEach((line) => {
      if (line.startsWith('data:')) {
        data += line.slice(5) + '\n'
      }
    })
    // console.log('处理/config响应数据>>', data)
    
    // 提取tool_result标签中的JSON数据
    const toolResultMatch = data.match(/<tool_result>([\s\S]*?)<\/tool_result>/)
    if (toolResultMatch) {
      const jsonData = JSON.parse(toolResultMatch[1].trim())
      if (jsonData.success && jsonData.data) {
        // 保存agent配置到store
        setAgentConfig(jsonData.data)
        console.log('Agent配置已保存:', jsonData.data.systemInfo?.agentList)
        console.log('LLM选项已保存:', jsonData.data.llmOptions)
      }
    }
  } catch (error) {
    console.error('处理/config响应失败:', error)
  }
}

const sendSwitchAgentCommand = async (agentKey: string) => {
  try {
    const agent = getAgent()
    if (!agent) {
      console.error('Agent not found')
      return
    }

    messageId.value = uuidv4()
    const params = {
      message: `/switch ${agentKey}`,
      __owner_id__: user?.username,
      __web_search__: functionPanelStore.webSearchEnabled || false,
      __rag__: functionPanelStore.ragEnabled || false,
    }

    // sse发送消息
    await streamChat(
      {
        mapData: {
          outerTag: 'use_mcp_tool',
          server_name: `${agent.name}:${agent.group}:${agent.version}:${
            getSelectedInstance().ip
          }:${getSelectedInstance().port}`,
          tool_name: getAgentName(),
          arguments: JSON.stringify(params),
        },
        conversationId: route.query.conversationId,
        agentId: route.query.serverAgentId,
        agentInstance: getSelectedInstance(),
      },
      (data: any) => {
        if (data) {
          throttledFluxCodeHandler(data, messageId.value)
        }
      }
    )
  } catch (error) {
    console.error('发送/switch命令失败:', error)
  }
}

const sendSwitchLlmCommand = async (llmKey: string) => {
  try {
    const agent = getAgent()
    if (!agent) {
      console.error('Agent not found')
      return
    }

    messageId.value = uuidv4()
    const params = {
      message: `/config put llm=${llmKey}`,
      __owner_id__: user?.username,
      __web_search__: functionPanelStore.webSearchEnabled || false,
      __rag__: functionPanelStore.ragEnabled || false,
    }

    // sse发送消息
    await streamChat(
      {
        mapData: {
          outerTag: 'use_mcp_tool',
          server_name: `${agent.name}:${agent.group}:${agent.version}:${
            getSelectedInstance().ip
          }:${getSelectedInstance().port}`,
          tool_name: getAgentName(),
          arguments: JSON.stringify(params),
        },
        conversationId: route.query.conversationId,
        agentId: route.query.serverAgentId,
        agentInstance: getSelectedInstance(),
      },
      (data: any) => {
        if (data) {
          throttledFluxCodeHandler(data, messageId.value)
        }
      }
    )
  } catch (error) {
    console.error('发送/config put llm命令失败:', error)
  }
}

const sendMcpCommand = async (command: string) => {
  try {
    const agent = getAgent()
    if (!agent) {
      console.error('Agent not found')
      return { success: false, error: 'Agent not found' }
    }

    messageId.value = uuidv4()
    const params = {
      message: command,
      __owner_id__: user?.username,
      __web_search__: functionPanelStore.webSearchEnabled || false,
      __rag__: functionPanelStore.ragEnabled || false,
    }

    // sse发送消息
    const response = await streamChat(
      {
        mapData: {
          outerTag: 'use_mcp_tool',
          server_name: `${agent.name}:${agent.group}:${agent.version}:${
            getSelectedInstance().ip
          }:${getSelectedInstance().port}`,
          tool_name: getAgentName(),
          arguments: JSON.stringify(params),
        },
        conversationId: route.query.conversationId,
        agentId: route.query.serverAgentId,
        agentInstance: getSelectedInstance(),
      },
      () => {}
    )

    console.log('MCP命令响应>>', response.data || '')

    return { success: true, output: response?.data || '' }
  } catch (error) {
    console.error('发送MCP命令失败:', error)
    return { success: false, error: error instanceof Error ? error.message : '未知错误' }
  }
}

// 通用的系统命令发送函数（用于 /refresh、/reload 等命令）
const sendSystemCommand = async (command: string, showResponse = false) => {
  try {
    const agent = getAgent()
    if (!agent) {
      console.error('Agent not found')
      return { success: false, error: 'Agent not found' }
    }

    messageId.value = uuidv4()
    const params = {
      message: command,
      __owner_id__: user?.username,
      __web_search__: functionPanelStore.webSearchEnabled || false,
      __rag__: functionPanelStore.ragEnabled || false,
    }

    // sse发送消息
    const response = await streamChat(
      {
        mapData: {
          outerTag: 'use_mcp_tool',
          server_name: `${agent.name}:${agent.group}:${agent.version}:${
            getSelectedInstance().ip
          }:${getSelectedInstance().port}`,
          tool_name: getAgentName(),
          arguments: JSON.stringify(params),
        },
        conversationId: route.query.conversationId,
        agentId: route.query.serverAgentId,
        agentInstance: getSelectedInstance(),
      },
      showResponse ? (data: any) => {
        if (data) {
          throttledFluxCodeHandler(data, messageId.value)
        }
      } : () => {}
    )

    console.log(`${command} 命令已发送`)

    return { success: true, output: response?.data || '' }
  } catch (error) {
    console.error(`发送 ${command} 命令失败:`, error)
    return { success: false, error: error instanceof Error ? error.message : '未知错误' }
  }
}

const sendMessage = async (message: Message) => {
  setShowFollow(true);
  addMessage(message)
  let text = message.data.text
  let image = null
  if (message.type === 'image') {
    text = message.data.content
    image = message.data.text?.split('base64,')[1]
  } else if (message.type === 'audio') {
    // text = `用asr-mcp工具并且使用腾讯云语音识别这个音频文件内容`;
    text = `用speech_to_text工具别这个音频文件内容`
  }
  try {
    const agent = getAgent()
    messageId.value = uuidv4()
    let params: any = {
      message: text,
      __owner_id__: user?.username,
      __web_search__: functionPanelStore.webSearchEnabled || false,
      __rag__: functionPanelStore.ragEnabled || false,
    }
    if (message.type === 'audio') {
      params.voiceBase64 = message.data.content?.split('base64,')[1]
    }
    if (image) {
      params.images = image
    }
    if (message.data.files?.length && message.data.files.length > 0) {
      params.message += `fileName: ${message.data.files[0].name} `
      params.message += ` fileBase64: ${message.data.files[0].input}`
    }
    if (sendMethod.value === 'sse') {
      // sse发送消息
      streamChat(
        {
          mapData: {
            outerTag: 'use_mcp_tool',
            server_name: `${agent!.name}:${agent!.group}:${agent!.version}:${
              getSelectedInstance().ip
            }:${getSelectedInstance().port}`,
            tool_name: getAgentName(),
            arguments: JSON.stringify(params),
          },
          conversationId: route.query.conversationId,
          agentId: route.query.serverAgentId,
          agentInstance: getSelectedInstance(),
        },
        (data: any) => {
          if (data) {
            throttledFluxCodeHandler(data, messageId.value)
          }
        }
      )
    } else {
      // ws发送消息
      socket.value?.send(
        JSON.stringify({
          mapData: {
            outerTag: 'use_mcp_tool',
            server_name: `${agent!.name}:${agent!.group}:${agent!.version}:${
              getSelectedInstance().ip
            }:${getSelectedInstance().port}`,
            tool_name: getAgentName(),
            arguments: JSON.stringify(params),
          },
          agentId: route.query.serverAgentId,
          agentInstance: getSelectedInstance(),
        })
      )
    }
  } catch (error) {
    console.error('发送消息失败:', error)
  }
}

const onStopMsg = () => {
  sendMessage({
    type: 'md',
    author: {
      cname: (user?.cname as any) || '',
      username: user?.username || '',
      avatar: (user?.avatar as any) || '',
    },
    meta: {
      role: 'USER',
    },
    data: {
      text: '/cancel',
    },
  })
}

const messageClick = async (item: MessageClickPayload) => {
  // 发消息
  addMessage({
    type: 'md',
    author: {
      cname: (user?.cname as any) || '',
      username: user?.username || '',
      avatar: (user?.avatar as any) || '',
    },
    meta: {
      role: 'USER',
    },
    data: {
      text: item.text,
    },
  })
  if (item.type === 'question') {
    addMessage(addHelloMessage(item.params.prompt))
  }
}

const addHelloMessage = (item: any): Message => {
  if (item.msg.startsWith('<stock-transaction>') || item.msg.startsWith('<stock-order>')) {
    return {
      type: 'md',
      author: {
        cname: (user?.cname as any) || '',
        username: user?.username || '',
        avatar: (user?.avatar as any) || '',
      },
      meta: {
        role: 'IDEA',
      },
      data: {
        text: item.msg,
      },
    }
  }
  return {
    type: 'hello',
    author: {
      cname: 'this.mioneName',
      username: 'this.mioneName',
      avatar: 'this.mioneUrl',
    },
    meta: {
      role: 'IDEA',
    },
    data: {
      hello: (item.msg || '').replace('${username}', user?.username || '').replace('${version}', `版本`),
      links:
        (item.promptInfoList &&
          item.promptInfoList.map((it: any) => {
            return {
              prefix: it.prefix || '',
              suffix: it.suffix || '',
              label: it.desc || '',
              src: it.src || '',
              value: it.promptName,
              type: it.type,
              params: {
                prompt: it.promptName,
                showDialog: it.showDialog || 'false',
                meta: it.meta,
                desc: it.desc,
              },
            }
          })) ||
        [],
    },
  }
}
const messageDelete = async (item: Message) => {
  const messageList: Message[] = []
  messageList.forEach((it) => {
    if (it !== item) {
      messageList.push(it)
    }
  })
  setMessageList(messageList)
}
const onMessageCmd = async (type: string, item: Message) => {
  switch (type) {
    case 'delete':
      try {
        await messageDelete(item)
      } catch (e) {
        //
      }
      break
    case 'refresh':
      //   await this.resendMessage(item);
      break
    case 'audio':
      try {
        // await this.myPlaySound(item);
      } catch (e) {
        //
      }
      break
    default:
      break
  }
}

onBeforeUnmount(() => {
  setAgent(null)
  setInstance(null)
  initCodePrompt()
  resetTokenUsage()
  clearAgentConfig()
  
  // 清理文件管理器WebSocket连接
  if (fileManagerMode.value === 'websocket' && fileAdapter.value instanceof WebSocketFileSystemAdapter) {
    fileAdapter.value.disconnect()
  }
})

// 获取主题
const { currentTheme } = useTheme()

onMounted(async () => {
  // 检查URL参数中是否有dir=true
  const urlParams = new URLSearchParams(window.location.search)
  showFileManagerButton.value = urlParams.get('dir') === 'true' && urlParams.get('wsUrl') !== null
  
  try {
    // 获取Agent详情
    const { data } = await getAgentDetail(Number(route.query.serverAgentId))
    if (data.code === 200) {
      const agent = data.data?.agent
      setAgent(agent!)
      setInstance(data.data?.instances!)
      addMessage({
        type: 'md',
        author: {
          cname: agent!.name,
          username: agent!.name,
          avatar: `data:image/jpeg;base64,${agent!.image}`,
        },
        meta: {
          role: 'IDEA',
        },
        data: {
          text: `你好，我是 ${agent!.name}，有什么可以帮你的吗？`,
        },
      })

      nextTick(() => {
        // 初始化WebSocket连接
        toggleSendMethod('ws');

        // 自动发送/create命令
        setTimeout(async () => {
          await sendCreateCommand()
          route.query.userInput && sendMessage({
            type: 'md',
            author: {
              cname: user?.cname || '',
              username: user?.username || '',
              avatar: user?.avatar || '',
            },
            meta: {
              role: 'USER',
            },
            data: {
              text: `${route.query.userInput}`,
            },
          })
        }, 1000);
      })
    }
  } catch (error) {
    console.error('获取Agent详情失败:', error)
  }

  // 初始化粒子效果
  window.particlesJS('particles-js', {
    particles: {
      number: {
        value: 80,
        density: {
          enable: true,
          value_area: 800,
        },
      },
      color: {
        value: currentTheme.value.colors.chatParticleColor,
      },
      shape: {
        type: 'circle',
      },
      opacity: {
        value: 0.5,
        random: true,
        anim: {
          enable: true,
          speed: 1,
          opacity_min: 0.1,
          sync: false,
        },
      },
      size: {
        value: 3,
        random: true,
        anim: {
          enable: true,
          speed: 2,
          size_min: 0.1,
          sync: false,
        },
      },
      line_linked: {
        enable: true,
        distance: 150,
        color: currentTheme.value.colors.chatParticleColor,
        opacity: 0.3,
        width: 1,
      },
      move: {
        enable: true,
        speed: 1,
        direction: 'none',
        random: true,
        straight: false,
        out_mode: 'out',
        bounce: false,
        attract: {
          enable: true,
          rotateX: 600,
          rotateY: 1200,
        },
      },
    },
    interactivity: {
      detect_on: 'canvas',
      events: {
        onhover: {
          enable: true,
          mode: 'grab',
        },
        onclick: {
          enable: true,
          mode: 'push',
        },
        resize: true,
      },
      modes: {
        grab: {
          distance: 140,
          line_linked: {
            opacity: 1,
          },
        },
        push: {
          particles_nb: 4,
        },
      },
    },
    retina_detect: true,
  })
})
</script>

<style lang="scss">
.chat-container {
  width: 100%;
  flex: 1;
  padding: 20px;
  background: var(--el-color-chat-background);
  background-image: var(--el-color-chat-background-gradient);
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

#particles-js {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

/* 添加量子背景效果 */
.chat-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(var(--el-color-chat-grid-color) 1px, transparent 1px),
    linear-gradient(90deg, var(--el-color-chat-grid-color) 1px, transparent 1px);
  background-size: 30px 30px;
  animation: gridMove 20s linear infinite;
  z-index: 0;
}

/* 添加发光边框效果 */
.chat-container::after {
  content: '';
  position: absolute;
  top: -2px;
  left: -2px;
  right: -2px;
  bottom: -2px;
  background: var(--el-color-chat-border-glow);
  background-size: 400%;
  z-index: -1;
  filter: blur(5px);
  animation: glowing 20s linear infinite;
}

/* 主内容区域 - flex布局 */
.main-content {
  position: relative;
  z-index: 2;
  display: flex;
  gap: 20px;
  height: 100%;
  width: 100%;
  align-items: stretch;
}

/* 文件管理器切换按钮 */
.file-manager-toggle {
  position: fixed;
  top: 80px;
  right: 30px;
  width: 50px;
  height: 50px;
  background: var(--el-bg-color);
  backdrop-filter: blur(10px);
  border-radius: 50%;
  border: 1px solid var(--el-border-color);
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 1000;
  transition: all 0.3s ease;
}

.file-manager-toggle:hover {
  transform: scale(1.1);
  box-shadow: 0 0 30px var(--el-color-primary-light-5);
  border-color: var(--el-color-primary);
}

.file-manager-toggle .el-icon {
  color: var(--el-color-primary);
}

/* 文件管理器容器 */
.file-manager-wrapper {
  flex: 1;
  min-width: 0;
  background: var(--el-bg-color);
  backdrop-filter: blur(10px);
  border-radius: 15px;
  border: 1px solid var(--el-border-color);
  box-shadow: 0 0 30px rgba(0, 0, 0, 0.2);
  overflow: hidden;
  display: flex;
  flex-direction: column;

  :deep(.file-manager) {
    height: 100%;
    
    .header {
      h2 {
        font-size: 18px;
        margin-bottom: 12px;
      }
      
      padding: 16px;
    }

    .content {
      flex: 1;
      overflow: hidden;
      display: flex;
      
      .file-list {
        width: 100%;
      }
    }
  }
}

/* 聊天窗口容器 */
.chat-window-wrapper {
  flex: 1;
  min-width: 0;
  display: flex;
  justify-content: center;
  align-items: stretch;
}

/* 滑入滑出动画 */
.slide-fade-enter-active {
  transition: all 0.3s ease-out;
}

.slide-fade-leave-active {
  transition: all 0.3s ease-in;
}

.slide-fade-enter-from {
  transform: translateX(-20px);
  opacity: 0;
  width: 0;
}

.slide-fade-leave-to {
  transform: translateX(-20px);
  opacity: 0;
  width: 0;
}

.chat-container .sc-chat-window {
  width: 100%;
  max-width: 1200px;
  height: 100%;
  background: var(--el-color-chat-window-background);
  backdrop-filter: blur(10px);
  border-radius: 15px;
  position: relative;
}

/* 添加量子光环效果 */
.quantum-ring {
  position: absolute;
  border-radius: 50%;
  border: 1px solid var(--el-color-chat-link-color);
  z-index: 1;
}

.ring-1 {
  width: 300px;
  height: 300px;
  top: -150px;
  right: -150px;
  animation: rotate 30s linear infinite;
}

.ring-2 {
  width: 200px;
  height: 200px;
  bottom: -100px;
  left: -100px;
  animation: rotate 20s linear infinite reverse;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

@keyframes gridMove {
  0% {
    transform: translateY(0);
  }
  100% {
    transform: translateY(30px);
  }
}

@keyframes glowing {
  0% {
    background-position: 0 0;
  }
  50% {
    background-position: 400% 0;
  }
  100% {
    background-position: 0 0;
  }
}

// .light #particles-js {
//   display: none;
// }

// .dark #particles-js {
//   display: none;
// }
</style>
