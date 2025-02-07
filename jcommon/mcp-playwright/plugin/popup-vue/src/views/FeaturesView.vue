<template>
  <div class="features-page">
    <div class="button-container">
      <el-button @click="showTabs">📑 显示所有标签页</el-button>
      <el-button @click="captureFullPage">📸 截取整页</el-button>
      <el-button @click="captureVisible">📷 截取当前屏幕</el-button>
      <el-button @click="autoScroll">⬇️ 自动滚动</el-button>
      <el-button @click="toggleSnowEffect">❄️ 下雪特效</el-button>
      <el-button @click="toggleBorders">🔲 添加元素边框</el-button>
      <el-button @click="toggleMouseTracker">🔴 虚拟鼠标跟踪</el-button>
      <el-button @click="redrawDomTree">🔄 重绘DOM树</el-button>
      <el-button @click="viewDomTree">🌳 查看DOM树</el-button>
      <el-button @click="getRecentHistory">📜 最近历史记录</el-button>
      <el-button @click="getBookmarkStats">📚 书签统计信息</el-button>
      <el-button @click="testError">⚠️ 测试错误</el-button>
      <el-button @click="actionTest">🎯 测试操作序列</el-button>
      <el-button @click="scrollOneScreen">📜 滚动一屏</el-button>

      <!-- 坐标输入组 -->
      <div class="coordinates-input-group">
        <el-row :gutter="10">
          <el-col :span="12">
            <el-input v-model="coordinates.x" placeholder="X" type="number">
              <template #prepend>X:</template>
            </el-input>
          </el-col>
          <el-col :span="12">
            <el-input v-model="coordinates.y" placeholder="Y" type="number">
              <template #prepend>Y:</template>
            </el-input>
          </el-col>
        </el-row>
        <el-button @click="moveToCoordinates">🎯 移动到坐标</el-button>
      </div>

      <!-- 选择器输入组 -->
      <div class="selector-input-group">
        <el-input v-model="selectorInput" placeholder="请输入要移动到的元素的CSS选择器" />
        <el-button @click="moveToSelector">🎯 移动到指定元素</el-button>

        <div class="action-execute-group">
          <el-select v-model="actionType">
            <el-option label="点击" value="click" />
            <el-option label="回车" value="enter" />
            <el-option label="输入内容" value="fill" />
          </el-select>
          <el-input v-model="actionSelector" placeholder="请输入要操作的元素的CSS选择器" />
          <el-input
            v-if="actionType === 'fill'"
            v-model="fillContent"
            placeholder="输入要填写的内容"
          />
          <el-button @click="executeAction">▶️ 执行操作</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

// 状态变量
const coordinates = ref({ x: 0, y: 0 })
const selectorInput = ref('')
const actionType = ref('click')
const actionSelector = ref('')
const fillContent = ref('')

// 方法定义
const showTabs = async () => {
  try {
    const tabs = await chrome.tabs.query({ currentWindow: true })
    ElMessage.success('已获取标签页列表')
  } catch (error) {
    ElMessage.error('获取标签页失败')
  }
}

// 在这里实现其他方法...
const captureFullPage = () => {
  chrome.runtime.sendMessage({ type: 'captureFullPage' })
}

const captureVisible = () => {
  chrome.runtime.sendMessage({ type: 'captureVisible' })
}

const autoScroll = () => {
  chrome.runtime.sendMessage({ type: 'autoScroll' })
}

const toggleSnowEffect = () => {
  chrome.runtime.sendMessage({ type: 'toggleSnowEffect' })
}

const toggleBorders = () => {
  chrome.runtime.sendMessage({ type: 'toggleBorders' })
}

const toggleMouseTracker = () => {
  chrome.runtime.sendMessage({ type: 'toggleMouseTracker' })
}

const redrawDomTree = () => {
  chrome.runtime.sendMessage({ type: 'redrawDomTree' })
}

const viewDomTree = () => {
  chrome.runtime.sendMessage({ type: 'viewDomTree' })
}

const getRecentHistory = () => {
  chrome.runtime.sendMessage({ type: 'getRecentHistory' })
}

const getBookmarkStats = () => {
  chrome.runtime.sendMessage({ type: 'getBookmarkStats' })
}

const testError = () => {
  chrome.runtime.sendMessage({ type: 'testError' })
}

const actionTest = () => {
  chrome.runtime.sendMessage({ type: 'actionTest' })
}

const scrollOneScreen = () => {
  chrome.runtime.sendMessage({ type: 'scrollOneScreen' })
}

const moveToCoordinates = () => {
  chrome.runtime.sendMessage({
    type: 'moveToCoordinates',
    coordinates: coordinates.value
  })
}

const moveToSelector = () => {
  chrome.runtime.sendMessage({
    type: 'moveToSelector',
    selector: selectorInput.value
  })
}

const executeAction = () => {
  chrome.runtime.sendMessage({
    type: 'executeAction',
    actionType: actionType.value,
    selector: actionSelector.value,
    content: fillContent.value
  })
}
</script>

<style scoped>
.features-page {
  padding: 20px;
}

.button-container {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.coordinates-input-group,
.selector-input-group {
  margin-top: 20px;
  padding: 15px;
  border: 1px solid var(--el-border-color);
  border-radius: 4px;
}

.action-execute-group {
  margin-top: 10px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
</style>
