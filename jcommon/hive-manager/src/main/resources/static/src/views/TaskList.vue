<template>
  <div class="task-list-container">
    <div class="dashboard-header">
      <div class="title-container">
        <h1>⚡ 任务控制中心</h1>
        <div class="animated-underline"></div>
      </div>
      <div class="header-actions">
        <button class="create-btn" @click="handleCreate">+ 创建任务</button>
      </div>
    </div>

    <div class="table-container">
      <div class="task-table">
        <TransitionGroup name="list" tag="div">
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
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getTaskById, createTask, getTaskList } from '@/api/task'
import type { Task, CreateTaskRequest } from '@/api/task'
import TaskDetailDrawer from '@/components/TaskDetailDrawer.vue'
import CreateTaskDialog from '@/components/CreateTaskDialog.vue'
import { useRoute } from 'vue-router'

const taskList = ref<Task[]>([])
const loading = ref(false)
const drawerVisible = ref(false)
const selectedTask = ref<Task | null>(null)
const dialogVisible = ref(false)
const route = useRoute()

// 获取任务列表
const fetchTasks = async () => {
  loading.value = true
  try {
    const response = await getTaskList(Number(route.query.serverAgentId))
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

onMounted(() => {
  fetchTasks()
})
</script>

<style scoped>
.task-list-container {
  min-height: 100vh;
  background: #0d1117;
  color: #fff;
  padding: 20px;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
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
  background: linear-gradient(
    90deg,
    #00f0ff 0%,
    #b400ff 50%,
    #00f0ff 100%
  );
  animation: progressLine 3s ease-in-out infinite;
  box-shadow: 0 0 10px rgba(0, 240, 255, 0.5);
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

.dashboard-header h1 {
  font-family: 'Orbitron', sans-serif;
  font-size: 2rem;
  background: linear-gradient(90deg, #00f0ff, #b400ff);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  display: flex;
  align-items: center;
  gap: 10px;
}

.create-btn {
  background: linear-gradient(135deg, #00f0ff, #b400ff);
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  color: #0d1117;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s;
}

.create-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 0 20px rgba(0, 240, 255, 0.4);
}

.task-card {
  background: rgba(13, 17, 23, 0.7);
  border: 1px solid rgba(0, 240, 255, 0.2);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  display: grid;
  grid-template-columns: 2fr 1fr 1fr;
  gap: 20px;
  align-items: center;
  transition: all 0.3s;
  cursor: pointer;
}

.task-card:hover {
  transform: translateX(4px);
  border-color: rgba(0, 240, 255, 0.4);
  box-shadow: 0 0 20px rgba(0, 240, 255, 0.2);
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
  background: linear-gradient(135deg, #00f0ff, #b400ff);
}

.task-logo-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: bold;
  color: #0d1117;
}

.task-details h4 {
  font-size: 18px;
  margin-bottom: 5px;
  color: #00f0ff;
}

.task-details p {
  color: rgba(255, 255, 255, 0.6);
  font-size: 14px;
}

.badge {
  padding: 6px 12px;
  border-radius: 4px;
  font-size: 14px;
  display: inline-block;
}

.badge.running {
  background: rgba(0, 240, 255, 0.1);
  color: #00f0ff;
  border: 1px solid #00f0ff;
}

.badge.completed {
  background: rgba(0, 255, 136, 0.1);
  color: #00ff88;
  border: 1px solid #00ff88;
}

.badge.failed {
  background: rgba(255, 85, 85, 0.1);
  color: #ff5555;
  border: 1px solid #ff5555;
}

.badge.pending {
  background: rgba(255, 184, 0, 0.1);
  color: #ffb800;
  border: 1px solid #ffb800;
}

.empty-state {
  width: 240px;
  height: 80px;
  margin: 120px auto 0;
  display: flex;
  align-items: center;
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
  background: rgba(13, 17, 23, 0.7);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(0, 240, 255, 0.2);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .task-card {
    grid-template-columns: 1fr;
    gap: 15px;
  }
}
</style>
