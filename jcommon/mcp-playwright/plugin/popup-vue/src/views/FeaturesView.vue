<template>
	<div class="features-page">
		<el-row :gutter="20">
			<!-- 左侧功能区 -->
			<el-col :span="16">
				<el-card class="feature-card">
					<template #header>
						<div class="card-header">
							<span>🛠️ 常用功能</span>
						</div>
					</template>
					<el-space wrap>
						<el-button type="primary" @click="showTabs">
							<el-icon><Document /></el-icon>显示标签页
						</el-button>
						<el-button type="success" @click="captureFullPage">
							<el-icon><Camera /></el-icon>截取整页
						</el-button>
						<el-button type="success" @click="captureVisible">
							<el-icon><PictureFilled /></el-icon>截取屏幕
						</el-button>
						<el-button type="warning" @click="autoScroll">
							<el-icon><Bottom /></el-icon>自动滚动
						</el-button>
						<el-button type="info" @click="toggleSnowEffect">
							<el-icon><Sugar /></el-icon>下雪特效
						</el-button>
						<el-button type="info" @click="toggleBorders">
							<el-icon><Grid /></el-icon>元素边框
						</el-button>
						<el-button type="warning" @click="toggleMouseTracker">
							<el-icon><Pointer /></el-icon>鼠标跟踪
						</el-button>
						<el-button type="primary" @click="redrawDomTree">
							<el-icon><Refresh /></el-icon>{{ isRedrawingDomTree ? '重绘中...' : '重绘DOM树' }}
						</el-button>
						<el-button type="success" @click="viewDomTree">
							<el-icon><Share /></el-icon>查看DOM树
						</el-button>
						<el-button type="info" @click="getRecentHistory">
							<el-icon><Clock /></el-icon>历史记录
						</el-button>
						<el-button type="warning" @click="getBookmarkStats">
							<el-icon><Collection /></el-icon>书签统计
						</el-button>
						<el-button type="danger" @click="testError">
							<el-icon><Warning /></el-icon>测试错误
						</el-button>
						<el-button type="primary" @click="actionTest">
							<el-icon><VideoPlay /></el-icon>测试序列
						</el-button>
						<el-button type="info" @click="scrollOneScreen">
							<el-icon><Bottom /></el-icon>滚动一屏
						</el-button>
					</el-space>

					<!-- 标签页列表 -->
					<div v-if="tabsList.length" class="tabs-list">
						<el-scrollbar height="200px">
							<el-list>
								<el-list-item v-for="(tab, index) in tabsList" :key="index">
									{{ index + 1 }}. {{ tab.title }}
								</el-list-item>
							</el-list>
						</el-scrollbar>
					</div>

					<!-- 消息历史 -->
					<div class="message-history">
						<div class="message-header">
							<h3>消息记录</h3>
							<el-button type="primary" size="small" @click="clearMessages">清除</el-button>
						</div>
						<el-scrollbar height="200px">
							<div class="realtime-messages">
								<div v-for="(msg, index) in messages" :key="index" class="message-item">
									<div class="message-time">{{ msg.timestamp }}</div>
									<pre class="message-content">{{ msg.content }}</pre>
								</div>
							</div>
						</el-scrollbar>
					</div>
				</el-card>
			</el-col>

			<!-- 右侧控制区 -->
			<el-col :span="8">
				<el-card class="control-card">
					<template #header>
						<div class="card-header">
							<span>🎯 操作控制</span>
						</div>
					</template>

					<el-form label-position="top">
						<!-- 坐标控制 -->
						<el-form-item label="坐标移动">
							<el-row :gutter="10">
								<el-col :span="11">
									<el-input-number
										v-model="coordinates.x"
										:controls="false"
										placeholder="X坐标"
									/>
								</el-col>
								<el-col :span="11">
									<el-input-number
										v-model="coordinates.y"
										:controls="false"
										placeholder="Y坐标"
									/>
								</el-col>
								<el-col :span="2">
									<el-button type="primary" circle @click="moveToCoordinates">
										<el-icon><Position /></el-icon>
									</el-button>
								</el-col>
							</el-row>
						</el-form-item>

						<!-- 选择器控制 -->
						<el-form-item label="元素选择器">
							<el-input
								v-model="selectorInput"
								placeholder="输入CSS选择器"
								:suffix-icon="Aim"
								@keyup.enter="moveToSelector"
							/>
							<el-button type="primary" class="full-width" @click="moveToSelector">
								<el-icon><Aim /></el-icon>定位元素
							</el-button>
						</el-form-item>

						<!-- 动作执行 -->
						<el-form-item label="执行动作">
							<el-select v-model="actionType" class="full-width">
								<el-option label="点击元素" value="click">
									<el-icon><Pointer /></el-icon>点击
								</el-option>
								<el-option label="按下回车" value="enter">
									<el-icon><Switch /></el-icon>回车
								</el-option>
								<el-option label="输入内容" value="fill">
									<el-icon><Edit /></el-icon>输入
								</el-option>
							</el-select>

							<el-input
								v-model="actionSelector"
								placeholder="目标元素选择器"
								class="margin-top"
							/>

							<el-input
								v-if="actionType === 'fill'"
								v-model="fillContent"
								placeholder="要输入的内容"
								class="margin-top"
							/>

							<el-button type="success" class="full-width margin-top" @click="executeAction">
								<el-icon><VideoPlay /></el-icon>执行操作
							</el-button>
						</el-form-item>
					</el-form>
				</el-card>
			</el-col>
		</el-row>
	</div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
	Document,
	Camera,
	PictureFilled,
	Bottom,
	Sugar,
	Grid,
	Pointer,
	Refresh,
	Share,
	Clock,
	Collection,
	Warning,
	Position,
	Aim,
	Edit,
	Switch,
	VideoPlay
} from '@element-plus/icons-vue'
import MoneyEffect from '../../../moneyEffect'
import { MouseTracker } from '../../../mouseTracker'
import errorManager from '../../../errorManager'
import bookmarkManager from '../../../managers/bookmarkManager'
import historyManager from '../../../managers/historyManager'

// 状态变量
const coordinates = ref({ x: 0, y: 0 })
const selectorInput = ref('')
const actionType = ref('click')
const actionSelector = ref('')
const fillContent = ref('')
const tabsList = ref([])
const messages = ref([])

// 方法定义
const showTabs = async () => {
	try {
		const tabs = await chrome.tabs.query({ currentWindow: true })
		tabsList.value = tabs
		ElMessage.success('已获取标签页列表')
	} catch (error) {
		ElMessage.error('获取标签页失败')
	}
}

const clearMessages = () => {
	messages.value = []
	chrome.runtime.sendMessage({ type: 'clearMessageHistory' })
}

const captureFullPage = async () => {
	try {
		const [tab] = await chrome.tabs.query({ active: true, currentWindow: true })
		if (!tab.id) return

		ElMessage.info('正在截取整页...')
		await chrome.runtime.sendMessage({ type: 'captureFullPage' })
		ElMessage.success('截图成功')
	} catch (error) {
		ElMessage.error('截图失败')
	}
}

const captureVisible = async () => {
	try {
		const [tab] = await chrome.tabs.query({ active: true, currentWindow: true })
		if (!tab.id) return

		ElMessage.info('正在截取屏幕...')
		const dataUrl = await chrome.tabs.captureVisibleTab()
		ElMessage.success('截图成功')
	} catch (error) {
		ElMessage.error('截图失败')
	}
}

let isTracking = false
const toggleMouseTracker = async () => {
	try {
		const [tab] = await chrome.tabs.query({ active: true, currentWindow: true })
		if (!tab.id) return

		isTracking = !isTracking
		if (isTracking) {
			await MouseTracker.injectTracker(tab.id)
			ElMessage.success('已开启鼠标跟踪')
		} else {
			await MouseTracker.removeTracker(tab.id)
			ElMessage.success('已关闭鼠标跟踪')
		}
	} catch (error) {
		ElMessage.error('鼠标跟踪操作失败')
	}
}

const toggleSnowEffect = async () => {
	try {
		const isEffectOn = await MoneyEffect.toggleEffect()
		ElMessage.success(isEffectOn ? '已开启下雪特效' : '已关闭下雪特效')
	} catch (error) {
		ElMessage.error('下雪特效切换失败')
	}
}

const autoScroll = () => {
	chrome.runtime.sendMessage({ type: 'autoScroll' })
}

const toggleBorders = () => {
	chrome.runtime.sendMessage({ type: 'toggleBorders' })
}

const isRedrawingDomTree = ref(false)
const redrawDomTree = async () => {

	try {
		// 获取当前活动标签页
		const [tab] = await chrome.tabs.query({ active: true, currentWindow: true })
		if (!tab.id) return

		if (!isRedrawingDomTree.value) {
			// 开始重绘
			isRedrawingDomTree.value = true

			// 清除之前的高亮
			await chrome.scripting.executeScript({
				target: { tabId: tab.id },
				func: () => {
					const container = document.getElementById('playwright-highlight-container')
					if (container) {
						container.remove()
					}
				}
			})

			// 重新执行buildDomTree
			await chrome.scripting.executeScript({
				target: { tabId: tab.id },
				files: ['buildDomTree.js']
			})

			// 执行buildDomTree函数来重新渲染高亮并获取返回数据
			const [{result: domTreeData}] = await chrome.scripting.executeScript({
				target: { tabId: tab.id },
				func: (args) => {
					const buildDomTreeFunc = window['buildDomTree']
					if (buildDomTreeFunc) {
						return buildDomTreeFunc(args)
					} else {
						throw new Error('buildDomTree函数未找到')
					}
				},
				args: [{ doHighlightElements: true, focusHighlightIndex: -1, viewportExpansion: 0 }]
			})

			// 将数据存储到 chrome.storage
			await chrome.storage.local.set({ lastDomTreeData: domTreeData })
			console.log('DOM树数据已保存:', domTreeData)

			// isRedrawingDomTree.value = false
			ElMessage.success('重绘成功')
		} else {
			// 取消重绘
			await chrome.scripting.executeScript({
				target: { tabId: tab.id },
				func: () => {
					const container = document.getElementById('playwright-highlight-container')
					if (container) {
						container.remove()
					}
				}
			})

			isRedrawingDomTree.value = false
			ElMessage.info('已取消重绘')
		}

	} catch (error) {
		console.error('重绘操作失败:', error)
		ElMessage.error(`操作失败: ${error}`)
		isRedrawingDomTree.value = false
	}
}

const viewDomTree = () => {
	chrome.windows.create({
    url: 'tree-viewer.html',
		type: 'popup',
		width: 800,
		height: 600
	})
}

const getRecentHistory = async () => {
	try {
		const recentHistory = await historyManager.getRecentHistory(3)
		recentHistory.forEach((item, index) => {
			const timestamp = new Date(item.lastVisitTime).toLocaleString()
			errorManager.info(`最近访问 ${index + 1}: ${item.title}\n链接: ${item.url}\n时间: ${timestamp}`)
		})
		ElMessage.success('已获取最近历史记录')
	} catch (error) {
		ElMessage.error('获取历史记录失败')
	}
}

const getBookmarkStats = async () => {
	try {
		const stats = await bookmarkManager.getBookmarkStats()
		errorManager.info('=== 书签统计信息 ===')
		errorManager.info(`总书签数: ${stats.totalBookmarks}`)
		errorManager.info(`总文件夹数: ${stats.totalFolders}`)

		if (stats.mostRecentBookmark) {
			const recentDate = new Date(stats.mostRecentBookmark.dateAdded).toLocaleString()
			errorManager.info(`最近添加的书签: ${stats.mostRecentBookmark.title}\n添加时间: ${recentDate}`)
		}

		if (stats.oldestBookmark) {
			const oldestDate = new Date(stats.oldestBookmark.dateAdded).toLocaleString()
			errorManager.info(`最早添加的书签: ${stats.oldestBookmark.title}\n添加时间: ${oldestDate}`)
		}

		errorManager.info(`平均文件夹深度: ${stats.averageDepth}`)
		ElMessage.success('书签统计信息已生成')
	} catch (error) {
		ElMessage.error('获取书签统计信息失败')
	}
}

const testError = () => {
	errorManager.info('This is an info message')
	errorManager.warning('This is a warning message')
	errorManager.error('This is an error message')

	try {
		throw new Error('This is a simulated error')
	} catch (e) {
		errorManager.error('Caught a simulated error', e)
	}

	errorManager.fatal('This is a fatal error message')
}

const actionTest = async () => {
	try {
		const [tab] = await chrome.tabs.query({ active: true, currentWindow: true })
		if (!tab.id) return

		ElMessage.info('开始执行测试序列...')
		await chrome.runtime.sendMessage({ type: 'actionTest' })

		// 使用一个变量来控制使用哪个搜索引擎
		const searchEngine = 'baidu' // 或 'bing'
		if (searchEngine === 'baidu') {
			await chrome.runtime.sendMessage({
				type: 'executeAction',
				actionType: 'fill',
				selector: '#kw',
				content: '大熊猫'
			})
			await chrome.runtime.sendMessage({
				type: 'executeAction',
				actionType: 'click',
				selector: '#su'
			})
		} else if (searchEngine === 'bing') {
			await chrome.runtime.sendMessage({
				type: 'executeAction',
				actionType: 'fill',
				selector: '#sb_form_q',
				content: '大熊猫'
			})
			await chrome.runtime.sendMessage({
				type: 'executeAction',
				actionType: 'enter',
				selector: '#sb_form_q'
			})
		}
		ElMessage.success('测试序列执行完成')
	} catch (error) {
		ElMessage.error('测试序列执行失败')
	}
}

const scrollOneScreen = async () => {
	try {
		const [tab] = await chrome.tabs.query({ active: true, currentWindow: true })
		if (!tab.id) return

		ElMessage.info('开始滚动...')
		await chrome.runtime.sendMessage({ type: 'scrollOneScreen' })

		// 注入 scrollManager.js
		await chrome.scripting.executeScript({
			target: { tabId: tab.id },
			files: ['managers/scrollManager.js']
		})

		// 执行滚动操作
		await chrome.scripting.executeScript({
			target: { tabId: tab.id },
			func: async () => {
				const scrollManager = await import(chrome.runtime.getURL('managers/scrollManager.js'))
					.then(module => module.default)
				await scrollManager.scrollOneScreen('down', { behavior: 'smooth' })
			}
		})
		ElMessage.success('滚动完成')
	} catch (error) {
		ElMessage.error('滚动失败')
		console.error('滚动失败:', error)
	}
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

// 监听来自 background 的消息
chrome.runtime.onMessage.addListener((message) => {
	if (message.type === 'newWebSocketMessage') {
		const content = message?.message?.data || {}
		const formattedContent = typeof content === 'object' ? content['data']?.toString() : content

		messages.value.unshift({
			timestamp: message.message.timestamp,
			content: formattedContent
				?.replace(/</g, '&lt;')
				.replace(/>/g, '&gt;')
				.replace(/\n/g, '<br>')
				.replace(/\s/g, '&nbsp;')
		})

		// 限制消息数量
		if (messages.value.length > 100) {
			messages.value.pop()
		}
	} else if (message.type === 'mousePosition') {
		coordinates.value = {
			x: message.x,
			y: message.y
		}
	} else if (message.type === 'elementSelector') {
		selectorInput.value = message.selector
		actionSelector.value = message.selector
		ElMessage.success('已更新选择器')
	}
})
</script>

<style scoped>
.features-page {
	padding: 20px;
	background-color: var(--el-bg-color-page);
	min-height: 100%;
}

.feature-card,
.control-card {
	height: 100%;
	box-shadow: var(--el-box-shadow-light);
	transition: all 0.3s ease;
}

.feature-card:hover,
.control-card:hover {
	box-shadow: var(--el-box-shadow);
}

.card-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	font-weight: bold;
}

.full-width {
	width: 100%;
}

.margin-top {
	margin-top: 12px;
}

:deep(.el-form-item__label) {
	font-weight: bold;
	color: var(--el-text-color-primary);
}

:deep(.el-input-number) {
	width: 100%;
}

:deep(.el-space) {
	flex-wrap: wrap;
}

:deep(.el-button) {
	display: flex;
	align-items: center;
	gap: 5px;
}

:deep(.el-select) {
	width: 100%;
}

:deep(.el-card__header) {
	border-bottom: 2px solid var(--el-border-color-light);
	padding: 15px 20px;
}

:deep(.el-form-item:last-child) {
	margin-bottom: 0;
}

.tabs-list {
	margin-top: 20px;
	border: 1px solid var(--el-border-color);
	border-radius: 4px;
	background-color: var(--el-bg-color);
}

.message-history {
	margin-top: 20px;
	border: 1px solid var(--el-border-color);
	border-radius: 4px;
	background-color: var(--el-bg-color);
}

.message-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 10px;
	border-bottom: 1px solid var(--el-border-color);
}

.message-header h3 {
	margin: 0;
	font-size: 16px;
	color: var(--el-text-color-primary);
}

.realtime-messages {
	padding: 10px;
}

.message-item {
	margin-bottom: 10px;
	padding: 10px;
	border-radius: 4px;
	background-color: var(--el-bg-color-page);
}

.message-time {
	font-size: 12px;
	color: var(--el-text-color-secondary);
	margin-bottom: 5px;
}

.message-content {
	margin: 0;
	white-space: pre-wrap;
	word-break: break-all;
	font-family: monospace;
	font-size: 12px;
	color: var(--el-text-color-primary);
}
</style>
