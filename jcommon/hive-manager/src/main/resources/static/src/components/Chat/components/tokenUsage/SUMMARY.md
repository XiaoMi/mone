# Token Usage 组件实现总结

## 📁 文件结构

```
src/components/Chat/components/tokenUsage/
├── index.vue                    # 主组件：Token使用量显示组件
├── README.md                    # 组件文档和特性说明
├── USAGE.md                     # 详细使用指南和API文档
├── TestPage.vue                 # 测试页面，展示各种使用场景
├── demo.vue                     # 简单演示组件
├── ChatWindowWithToken.vue      # 聊天窗口集成示例
└── TokenUsageWrapper.vue        # 包装器组件
```

## 🎯 核心功能实现

### 1. TokenUsage 主组件 (`index.vue`)
- ✅ 进度条样式的Token使用量显示
- ✅ 支持左侧已使用量、中间进度条、右侧总量布局
- ✅ 动态进度条颜色（绿色→黄色→红色）
- ✅ 数字格式化（K、M单位显示）
- ✅ 可选详细信息显示
- ✅ 响应式设计，适配移动端
- ✅ 流畅的进度条动画效果
- ✅ 支持多主题（成功、警告、危险）

### 2. Store集成 (`chat-context.ts`)
```typescript
// 新增的Token使用量接口
export interface TokenUsage {
  usedTokens: number;
  totalTokens: number;
  inputTokens: number;
  outputTokens: number;
  lastUpdate: Date;
}

// 新增的Store方法
- updateTokenUsage(inputTokens, outputTokens) // 更新Token使用量
- setTotalTokens(total)                       // 设置总Token量
- resetTokenUsage()                           // 重置Token使用量
- getTokenUsagePercentage()                   // 获取使用率百分比
```

## 🎨 设计特性

### 视觉效果
- **进度条渐变色**：根据使用率自动变色
- **闪烁动画**：进度条有流光效果
- **数字格式化**：大数字自动转换为K、M单位
- **响应式布局**：移动端自适应

### 交互体验
- **点击显示详情**：可以查看详细Token信息
- **主题切换**：支持多种颜色主题
- **实时更新**：Token使用量实时更新动画

## 🔧 使用方式

### 基础使用
```vue
<TokenUsage 
  :used-tokens="25000" 
  :total-tokens="100000" 
/>
```

### 完整功能
```vue
<TokenUsage 
  :used-tokens="chatStore.tokenUsage.usedTokens" 
  :total-tokens="chatStore.tokenUsage.totalTokens" 
  :show-details="true"
  theme="default"
  @click="showTokenDetails"
/>
```

### Store集成
```javascript
// 更新Token使用量
chatStore.updateTokenUsage(inputTokens, outputTokens)

// 获取使用率
const percentage = chatStore.getTokenUsagePercentage()
```

## 📊 组件Props

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `usedTokens` | `number` | `0` | 已使用Token数量 |
| `totalTokens` | `number` | `100000` | Token总量 |
| `showDetails` | `boolean` | `false` | 显示详细信息 |
| `theme` | `string` | `'default'` | 主题样式 |

## 🎯 实际应用场景

1. **聊天界面顶部**：显示当前会话Token使用情况
2. **用户仪表盘**：显示账户Token余额和使用统计
3. **API调用监控**：实时监控API Token消耗
4. **费用控制**：帮助用户控制Token使用成本

## 🚀 接下来的步骤

### 集成到现有项目
1. 将组件导入到需要的页面
2. 在Chat相关组件中集成Token显示
3. 在API调用时更新Token使用量
4. 添加Token不足的提醒逻辑

### 示例集成代码
```vue
<!-- 在ChatWindow.vue中 -->
<template>
  <div class="chat-window">
    <!-- 添加Token使用量显示 -->
    <div class="token-header">
      <TokenUsage 
        :used-tokens="chatStore.tokenUsage.usedTokens" 
        :total-tokens="chatStore.tokenUsage.totalTokens" 
        :show-details="true"
      />
    </div>
    
    <!-- 原有聊天内容 -->
    <div class="chat-content">
      <!-- ... -->
    </div>
  </div>
</template>

<script setup>
import { useChatContextStore } from '@/stores/chat-context'
import TokenUsage from './components/tokenUsage/index.vue'

const chatStore = useChatContextStore()

// 在发送消息时更新Token使用量
const sendMessage = async (message) => {
  const response = await apiCall(message)
  
  // 更新Token使用量
  chatStore.updateTokenUsage(
    response.usage.prompt_tokens,
    response.usage.completion_tokens
  )
}
</script>
```

## ✨ 特色功能

1. **智能颜色变化**：使用率不同自动变色
2. **流畅动画**：进度条更新有平滑过渡
3. **数据持久化**：集成到Pinia Store
4. **响应式设计**：适配各种屏幕尺寸
5. **可扩展性**：支持自定义主题和样式

这个Token使用量组件现在已经完全实现了你要求的功能：左侧显示已使用Token量，中间是进度条显示使用占比，右侧显示总Token量。组件具有良好的视觉效果、交互体验和扩展性。