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
							<el-icon><Refresh /></el-icon>重绘DOM树
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
					</el-space>
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
</style>
