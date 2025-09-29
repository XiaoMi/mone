# TokenUsage 组件

一个用于显示Token使用量的Vue 3组件，采用进度条样式，支持多种主题和详细信息显示。

## 功能特性

- 📊 直观的进度条显示Token使用情况
- 🎨 支持多种主题色彩（成功、警告、危险）
- 📱 响应式设计，适配移动端
- ✨ 流畅的动画效果
- 🔢 智能数字格式化（K、M单位）
- 📋 可选的详细信息显示

## 使用方法

### 基础使用

```vue
<template>
  <TokenUsage 
    :used-tokens="25000" 
    :total-tokens="100000" 
  />
</template>

<script setup>
import TokenUsage from '@/components/Chat/components/tokenUsage/index.vue'
</script>
```

### 显示详细信息

```vue
<template>
  <TokenUsage 
    :used-tokens="65000" 
    :total-tokens="100000" 
    :show-details="true"
  />
</template>
```

### 自定义主题

```vue
<template>
  <TokenUsage 
    :used-tokens="95000" 
    :total-tokens="100000" 
    theme="danger"
    :show-details="true"
  />
</template>
```

## Props

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `usedTokens` | `number` | `0` | 已使用的Token数量 |
| `totalTokens` | `number` | `100000` | Token总量 |
| `showDetails` | `boolean` | `false` | 是否显示详细信息 |
| `theme` | `'default' \| 'success' \| 'warning' \| 'danger'` | `'default'` | 主题样式 |

## 自定义样式

组件使用CSS变量，支持Element Plus的主题系统：

```scss
:root {
  --el-color-primary: #409eff;
  --el-color-success: #67c23a;
  --el-color-warning: #e6a23c;
  --el-color-danger: #f56c6c;
}
```

## 实际集成示例

在聊天组件中使用：

```vue
<template>
  <div class="chat-container">
    <!-- 聊天头部 -->
    <div class="chat-header">
      <TokenUsage 
        :used-tokens="chatStore.usedTokens" 
        :total-tokens="chatStore.totalTokens" 
        :show-details="true"
        @click="showTokenDetails"
      />
    </div>
    
    <!-- 聊天内容 -->
    <div class="chat-content">
      <!-- 聊天消息 -->
    </div>
  </div>
</template>

<script setup>
import { useChatStore } from '@/stores/chat-context'
import TokenUsage from '@/components/Chat/components/tokenUsage/index.vue'

const chatStore = useChatStore()

const showTokenDetails = () => {
  // 显示Token使用详情弹框
  console.log('Token详情:', {
    used: chatStore.usedTokens,
    total: chatStore.totalTokens,
    remaining: chatStore.totalTokens - chatStore.usedTokens
  })
}
</script>
```

## 样式定制

可以通过CSS变量或覆盖样式来定制外观：

```scss
.token-usage-container {
  --progress-height: 10px;
  --border-radius: 12px;
  --padding: 20px;
}

// 自定义进度条颜色
.progress-fill.progress-custom {
  background: linear-gradient(90deg, #667eea, #764ba2);
}
```

## 注意事项

1. 确保传入的Token数值为非负数
2. `totalTokens` 不应为0，避免除零错误
3. 组件会自动处理超出总量的情况（进度条最大100%）
4. 数字格式化会将大于1000的数字转换为K、M单位

## 更新日志

- v1.0.0: 初始版本，支持基础Token使用量显示
- 支持进度条动画和主题切换
- 支持响应式设计和详细信息显示