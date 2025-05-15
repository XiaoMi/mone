<template>
  <div class="task-list-container">
    <div class="cyber-grid"></div>
    <div class="circuit-point point-1"></div>
    <div class="circuit-point point-2"></div>
    <div class="dashboard-header">
      <button class="create-btn" @click="handleCreate">+ 创建任务</button>
    </div>

    <div class="table-container">
      <div class="task-table">
        <div v-if="loading" class="loading-container">
          <div class="loading-spinner"></div>
          <span>加载中...</span>
        </div>
        <TransitionGroup name="list" tag="div" v-else>
          <div v-if="taskList.length === 0" :key="'empty'" class="empty-state">
            <div class="empty-content">
              <span class="empty-text">暂无任务数据</span><br/>
              <button class="create-btn" @click="handleCreate">+ 创建任务</button>
            </div>
          </div>

          <div v-for="task in taskList"
               :key="task.id"
               class="task-card"
               @click="handleShowDetail(task)">
            <div class="task-info">
              <div class="task-avatar">
                <div class="task-logo-placeholder">
                  {{ task.title.charAt(0).toUpperCase() }}
                </div>
              </div>
              <div class="task-details">
                <h4>{{task.title}}</h4>
                <p>{{task.description}}</p>
              </div>
            </div>

            <div class="task-status">
              <span class="badge" :class="task.status.toLowerCase()">
                {{task.status}}
              </span>
            </div>

            <div class="activity">
              <span>创建时间：</span>
              <time>{{formatDate(task.ctime)}}</time><br/>
              <span>更新时间：</span>
              <time>{{formatDate(task.utime)}}</time>
            </div>

            <div class="task-actions">
              <button
                class="execute-btn"
                @click.stop="handleExecute(task)"
                :disabled="task.status === 'running'"
              >
                执行
              </button>
              <button
                class="edit-btn"
                @click.stop="handleEdit(task)"
              >
                修改
              </button>
            </div>
          </div>
        </TransitionGroup>
      </div>
    </div>

    <!-- 任务详情抽屉 -->
    <task-detail-drawer
      v-model="drawerVisible"
      :task="selectedTask"
    />

    <!-- 替换原有的el-dialog -->
    <create-task-dialog
      v-model="dialogVisible"
      @submit="handleSubmit"
    />

    <el-dialog
      v-model="editDialogVisible"
      title="修改任务"
      width="400px"
    >
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="任务描述">
          <el-input v-model="editForm.description"></el-input>
        </el-form-item>
        <el-form-item label="Agent">
          <el-select v-model="editForm.serverAgentId" placeholder="请选择">
            <el-option
              v-for="item in agentList"
              :key="item.agent.id"
              :label="item.agent.name"
              :value="item.agent.id"
            />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleEditSubmit">提交</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getTaskById, createTask, getTaskList, executeTask, updateTask } from '@/api/task'
import type { Task, CreateTaskRequest } from '@/api/task'
import TaskDetailDrawer from '@/components/TaskDetailDrawer.vue'
import CreateTaskDialog from '@/components/CreateTaskDialog.vue'
import { useRoute } from 'vue-router'
import { getAgentList } from '@/api/agent'
import { useTheme } from '@/styles/theme/useTheme'

const taskList = ref<Task[]>([])
const loading = ref(false)
const drawerVisible = ref(false)
const selectedTask = ref<Task | null>(null)
const dialogVisible = ref(false)
const editDialogVisible = ref(false)
const route = useRoute()
const editForm = ref<CreateTaskRequest>({
  taskUuid: '',
  clientAgentId: null,
  serverAgentId: null,
  skillId: null,
  title: '',
  description: '',
  status: ''
})
const agentList = ref<any[]>([])

// 获取主题
const { currentTheme } = useTheme()

// 获取任务列表
const fetchTasks = async () => {
  loading.value = true
  try {
    const params: any = {}
    if (route.query.serverAgentId) {
      params.serverAgentId = Number(route.query.serverAgentId)
    }
    const response = await getTaskList(params)
    if (response.data.code === 200) {
      taskList.value = response.data.data || []
    } else {
      ElMessage.error(response.data.message)
    }
  } catch {
    ElMessage.error('获取任务列表失败')
  } finally {
    loading.value = false
  }
}

const handleShowDetail = async (task: Task) => {
  try {
    const response = await getTaskById(task.taskUuid)
    if (response.data.code === 200) {
      selectedTask.value = response.data.data || null
      drawerVisible.value = true
    } else {
      ElMessage.error(response.data.message)
    }
  } catch {
    ElMessage.error('获取任务详情失败')
  }
}

const handleCreate = () => {
  dialogVisible.value = true
}

const handleSubmit = async (form: CreateTaskRequest) => {
  try {
    const response = await createTask(form)
    if (response.data.code === 200) {
      ElMessage.success('创建成功')
      dialogVisible.value = false
      fetchTasks()
    } else {
      ElMessage.error(response.data.message)
    }
  } catch {
    ElMessage.error('创建任务失败')
  }
}

const handleExecute = async (task: Task) => {
  try {
    const response = await executeTask({
      id: task.taskUuid,
      metadata:{
        input: task.description,
        serverAgentId: task.serverAgentId
      }
    })
    if (response.data.code === 200) {
      ElMessage.success('任务执行成功')
      fetchTasks()
    } else {
      ElMessage.error(response.data.message)
    }
  } catch {
    ElMessage.error('执行任务失败')
  }
}

const handleEdit = (task: Task) => {
  editForm.value = { ...task }
  editDialogVisible.value = true
}

const handleEditSubmit = async () => {
  try {
    const response = await updateTask(editForm.value)
    if (response.data.code === 200) {
      ElMessage.success('任务更新成功')
      editDialogVisible.value = false
      fetchTasks()
    } else {
      ElMessage.error(response.data.message)
    }
  } catch {
    ElMessage.error('更新任务失败')
  }
}

const formatDate = (date: string) => {
  return new Date(date).toLocaleString()
}

const getStatusType = (status: string) => {
  const statusMap: Record<string, string> = {
    'running': 'primary',
    'completed': 'success',
    'failed': 'danger',
    'pending': 'warning'
  }
  return statusMap[status] || 'info'
}

// 获取Agent列表
const fetchAgentList = async () => {
  try {
    const response = await getAgentList()
    if (response.data.code === 200) {
      agentList.value = response.data.data || []
    } else {
      ElMessage.error(response.data.message)
    }
  } catch {
    ElMessage.error('获取Agent列表失败')
  }
}

onMounted(() => {
  fetchTasks()
  fetchAgentList()
})
</script>

<style scoped>
.task-list-container {
  width: 100%;
  min-height: 100vh;
  background: var(--el-color-chat-background);
  color: var(--el-color-chat-text);
  padding: 0 20px 12px;
  position: relative;
  overflow: hidden;
}

.cyber-grid {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image:
    linear-gradient(var(--el-color-chat-grid-color) 1px, transparent 1px),
    linear-gradient(90deg, var(--el-color-chat-grid-color) 1px, transparent 1px);
  background-size: 40px 40px;
  animation: gridMove 20s linear infinite;
  transform-origin: center;
  opacity: 0.3;
  pointer-events: none;
}

@keyframes gridMove {
  0% {
    transform: perspective(500px) rotateX(60deg) translateY(0);
  }
  100% {
    transform: perspective(500px) rotateX(60deg) translateY(40px);
  }
}

.circuit-point {
  position: absolute;
  width: 6px;
  height: 6px;
  background: var(--el-color-chat-link-color);
  border-radius: 50%;
  filter: blur(1px);
  box-shadow:
    0 0 10px var(--el-color-chat-link-color),
    0 0 20px var(--el-color-chat-link-color),
    0 0 30px var(--el-color-chat-link-color-light);
  opacity: 0;
  z-index: 0;
}

.circuit-point::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 12px;
  height: 12px;
  background: var(--el-color-chat-link-color-light);
  border-radius: 50%;
  filter: blur(2px);
}

.circuit-point::after {
  content: '';
  position: absolute;
  top: 50%;
  right: 50%;
  width: 40px;
  height: 2px;
  background: linear-gradient(
    270deg,
    var(--el-color-chat-link-color),
    var(--el-color-chat-link-color-light)
  );
  transform-origin: right center;
  transform: translateY(-50%);
  filter: blur(1px);
}

.point-1 {
  bottom: 40px;
  left: 40px;
  animation: movePoint1 15s linear infinite;
}

.point-1::after {
  animation: tailRotate1 15s linear infinite;
}

.point-2 {
  bottom: 40px;
  right: 40px;
  animation: movePoint2 15s linear infinite;
}

.point-2::after {
  animation: tailRotate2 15s linear infinite;
}

@keyframes movePoint1 {
  0% {
    transform: translate(0, 0);
    opacity: 0;
  }
  5% {
    opacity: 1;
  }
  15% {
    transform: translate(0, -160px);
  }
  30% {
    transform: translate(120px, -160px);
  }
  45% {
    transform: translate(120px, -320px);
  }
  60% {
    transform: translate(240px, -320px);
  }
  75% {
    transform: translate(240px, -480px);
  }
  90% {
    transform: translate(360px, -480px);
    opacity: 1;
  }
  95% {
    opacity: 0;
  }
  100% {
    transform: translate(360px, -480px);
    opacity: 0;
  }
}

@keyframes movePoint2 {
  0% {
    transform: translate(0, 0);
    opacity: 0;
  }
  5% {
    opacity: 1;
  }
  15% {
    transform: translate(0, -200px);
  }
  30% {
    transform: translate(-160px, -200px);
  }
  45% {
    transform: translate(-160px, -360px);
  }
  60% {
    transform: translate(-280px, -360px);
  }
  75% {
    transform: translate(-280px, -520px);
  }
  90% {
    transform: translate(-400px, -520px);
    opacity: 1;
  }
  95% {
    opacity: 0;
  }
  100% {
    transform: translate(-400px, -520px);
    opacity: 0;
  }
}

@keyframes tailRotate1 {
  0%, 15% { transform: translateY(-50%) rotate(-90deg); }
  15.1%, 30% { transform: translateY(-50%) rotate(0deg); }
  30.1%, 45% { transform: translateY(-50%) rotate(-90deg); }
  45.1%, 60% { transform: translateY(-50%) rotate(0deg); }
  60.1%, 75% { transform: translateY(-50%) rotate(-90deg); }
  75.1%, 90% { transform: translateY(-50%) rotate(0deg); }
}

@keyframes tailRotate2 {
  0%, 15% { transform: translateY(-50%) rotate(-90deg); }
  15.1%, 30% { transform: translateY(-50%) rotate(180deg); }
  30.1%, 45% { transform: translateY(-50%) rotate(-90deg); }
  45.1%, 60% { transform: translateY(-50%) rotate(180deg); }
  60.1%, 75% { transform: translateY(-50%) rotate(-90deg); }
  75.1%, 90% { transform: translateY(-50%) rotate(180deg); }
}

.dashboard-header {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-bottom: 12px;
}

.title-container {
  position: relative;
}

.animated-underline {
  position: absolute;
  bottom: -10px;
  left: 0;
  width: 0;
  height: 2px;
  background: var(--el-color-background-gradient);
  animation: progressLine 3s ease-in-out infinite;
  box-shadow: 0 0 10px var(--el-color-background-gradient);
}

@keyframes progressLine {
  0% {
    width: 0;
    opacity: 0.6;
    left: 0;
  }
  50% {
    width: 100%;
    opacity: 1;
    left: 0;
  }
  51% {
    width: 100%;
    opacity: 1;
    left: 0;
  }
  100% {
    width: 0;
    opacity: 0.6;
    left: 100%;
  }
}


.table-container {
  height: calc(100vh - 110px);
  overflow-y: auto;
}

.dashboard-header h1 {
  font-family: 'Orbitron', sans-serif;
  font-size: 1.6rem;
  color: transparent;
  display: flex;
  background: var(--el-color-background-gradient) text;
  align-items: center;
  gap: 10px;
}

.create-btn {
  background: var(--el-color-background-gradient);
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  color: var(--el-color-white);
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s;
}

.create-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 0 20px var(--el-color-background-gradient);
}

.task-card {
  background: var(--el-color-chat-window-background);
  border: 1px solid var(--el-color-chat-link-color);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  gap: 20px;
  align-items: center;
  transition: all 0.3s;
  cursor: pointer;
  position: relative;
  z-index: 1;
}

.task-card:hover {
  transform: translateX(4px);
  border-color: var(--el-color-chat-link-color);
  box-shadow: 0 0 20px var(--el-color-chat-link-color-light);
}

.task-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.task-avatar {
  width: 50px;
  height: 50px;
  border-radius: 10px;
  overflow: hidden;
  background: var(--el-color-background-gradient);
}

.task-logo-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: bold;
  color: var(--el-color-white);
}

.task-details h4 {
  font-size: 18px;
  margin-bottom: 5px;
  color: var(--el-color-chat-link-color);
}

.task-details p {
  color: var(--el-color-chat-text-secondary);
  font-size: 14px;
}

.badge {
  padding: 6px 12px;
  border-radius: 4px;
  font-size: 14px;
  display: inline-block;
}

.badge.running {
  background: var(--el-color-chat-link-color-light);
  color: var(--el-color-chat-link-color);
  border: 1px solid var(--el-color-chat-link-color);
}

.badge.completed {
  background: var(--el-color-success-light);
  color: var(--el-color-success);
  border: 1px solid var(--el-color-success);
}

.badge.failed {
  background: var(--el-color-danger-light);
  color: var(--el-color-danger);
  border: 1px solid var(--el-color-danger);
}

.badge.pending {
  background: var(--el-color-warning-light);
  color: var(--el-color-warning);
  border: 1px solid var(--el-color-warning);
}

.empty-state {
  width: 240px;
  height: 80px;
  margin: 120px auto 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-state .empty-text {
  display: inline-block;
  margin-bottom: 24px;
  width: 100%;
  text-align: center;
}

/* 保持原有的抽屉和对话框样式 */
:deep(.el-drawer),
:deep(.el-dialog) {
  background: var(--el-color-chat-window-background);
  backdrop-filter: blur(20px);
  border: 1px solid var(--el-color-chat-link-color);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .task-card {
    grid-template-columns: 1fr;
    gap: 15px;
  }
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: var(--el-color-chat-link-color);
}

.loading-spinner {
  width: 40px;
  height: 40px;
  margin-bottom: 16px;
  border: 3px solid var(--el-color-chat-link-color-light);
  border-top: 3px solid var(--el-color-chat-link-color);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.task-actions {
  display: flex;
  justify-content: flex-end;
}

.execute-btn {
  background: var(--el-color-background-gradient);
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  color: var(--el-color-white);
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s;
}

.execute-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 0 15px var(--el-color-background-gradient);
}

.execute-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  background: var(--el-color-text-secondary);
}

.edit-btn {
  background: var(--el-color-background-gradient);
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  color: #0d1117;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s;
  margin-left: 10px;
}

.edit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 0 20px rgba(0, 240, 255, 0.4);
}
</style>
