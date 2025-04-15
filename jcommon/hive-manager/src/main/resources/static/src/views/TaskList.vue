<template>
  <div class="task-list-container">
    <el-card class="box-card custom-card">
      <template #header>
        <div class="card-header">
          <span>任务列表</span>
          <el-button 
            class="create-btn custom-btn" 
            type="primary" 
            @click="handleCreate"
          >
            创建任务
          </el-button>
        </div>
      </template>

      <div class="future-table">
        <div class="table-header">
          <div class="header-cell" style="width: 80px">ID</div>
          <div class="header-cell flex-1">名称</div>
          <div class="header-cell flex-1">描述</div>
          <div class="header-cell" style="width: 120px">状态</div>
          <div class="header-cell" style="width: 180px">创建时间</div>
          <!-- <div class="header-cell" style="width: 200px">操作</div> -->
        </div>
        
        <TransitionGroup 
          name="list" 
          tag="div" 
          class="table-body"
        >
          <div 
            v-if="taskList.length === 0" 
            :key="'empty'" 
            class="empty-state"
          >
            <div class="empty-content">
              <span class="empty-text">暂无任务数据</span>
            </div>
          </div>
          <div 
            v-for="task in taskList" 
            :key="task.id" 
            class="table-row"
            :class="{'hover-effect': true}"
          >
            <div class="cell" style="width: 80px">
              <div class="id-badge">{{task.id}}</div>
            </div>
            <div class="cell flex-1">
              <div class="name-container">
                <span 
                  class="task-name"
                  @click="handleShowDetail(task)"
                >
                  {{task.title}}
                </span>
              </div>
            </div>
            <div class="cell flex-1">{{task.description}}</div>
            <div class="cell" style="width: 120px">
              <el-tag type="primary">
                {{task.status}}
              </el-tag>
            </div>
            <div class="cell" style="width: 180px">
              {{formatDate(task.ctime)}}
            </div>
            <!-- <div class="cell actions" style="width: 200px">
              <el-button 
                class="custom-btn edit"
                @click="handleShowDetail(task)"
              >
                详情
              </el-button>
            </div> -->
          </div>
        </TransitionGroup>
      </div>
    </el-card>

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
/* 更新任务列表容器样式 */
.task-list-container {
  padding: 20px;
  background: linear-gradient(135deg, #23a6d5 0%, #23d5ab 100%);
  min-height: 100vh;
  position: relative;
  overflow: hidden;
}

/* 添加网格背景 */
.task-list-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    linear-gradient(rgba(255, 255, 255, 0.1) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.1) 1px, transparent 1px);
  background-size: 40px 40px;
  pointer-events: none;
}

/* 更新卡片样式 */
.custom-card {
  background: rgba(13, 17, 23, 0.5);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(48, 54, 61, 0.2);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  border-radius: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #ffffff;
  font-size: 1.2em;
  text-shadow: 0 0 10px rgba(88, 166, 255, 0.3);
}

/* 更新抽屉样式 */
:deep(.el-drawer) {
  background: rgba(13, 17, 23, 0.7);
  backdrop-filter: blur(20px);
  border-left: 1px solid rgba(48, 54, 61, 0.2);
}

:deep(.el-drawer__header) {
  color: #ffffff;
  margin-bottom: 20px;
  border-bottom: 1px solid rgba(48, 54, 61, 0.2);
  padding-bottom: 20px;
}

/* 更新任务详情样式 */
.task-detail {
  padding: 20px;
  color: #ffffff;
}

.detail-item {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.detail-item label {
  width: 120px;
  color: #31e8f9;
  font-weight: 500;
}

.detail-item span {
  color: #ffffff;
}

/* 更新表格样式 */
.future-table {
  background: rgba(13, 17, 23, 0.4);
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid rgba(48, 54, 61, 0.2);
  position: relative;
}

/* 添加扫描线动画 */
.future-table::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, 
    rgba(99, 179, 237, 0) 0%,
    rgba(99, 179, 237, 0.8) 50%,
    rgba(99, 179, 237, 0) 100%
  );
  /* animation: scanline 3s linear infinite; */
}

.table-header {
  display: flex;
  background: linear-gradient(90deg, rgba(22, 27, 34, 0.4) 0%, rgba(28, 33, 40, 0.4) 100%);
  padding: 16px;
  color: #31e8f9;
  font-weight: 500;
  border-bottom: 1px solid rgba(48, 54, 61, 0.2);
}

.header-cell {
  padding: 0 12px;
}

.table-body {
  position: relative;
}

.table-row {
  display: flex;
  padding: 16px;
  border-bottom: 1px solid rgba(48, 54, 61, 0.2);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  color: #ffffff;
}

/* 表格行悬停效果 */
.table-row::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, 
    transparent 0%,
    rgba(99, 179, 237, 0.1) 50%,
    transparent 100%
  );
  transform: translateX(-100%);
  transition: transform 0.5s ease;
}

.table-row:hover::before {
  transform: translateX(100%);
}

.hover-effect:hover {
  transform: translateX(4px) scale(1.01);
  background: rgba(48, 54, 61, 0.2);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
}

.cell {
  padding: 0 12px;
  display: flex;
  align-items: center;
  color: #ffffff;
}

.flex-1 {
  flex: 1;
}

/* 更新任务名称样式 */
.task-name {
  cursor: pointer;
  color: #31e8f9;
  transition: all 0.3s ease;
}

.task-name:hover {
  text-shadow: 0 0 10px rgba(49, 232, 249, 0.5);
  text-decoration: underline;
}

/* 空状态样式 */
.empty-state {
  padding: 60px 0;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.empty-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.empty-text {
  color: #ffffff;
  font-size: 16px;
  text-shadow: 0 0 10px rgba(49, 232, 249, 0.3);
}

/* 动画关键帧 */
@keyframes scanline {
  0% { transform: translateY(-100%); }
  100% { transform: translateY(100vh); }
}

/* 列表动画 */
.list-enter-active {
  transition: all 0.6s cubic-bezier(0.4, 0, 0.2, 1);
}

.list-leave-active {
  transition: all 0.6s cubic-bezier(0.4, 0, 0.2, 1);
  position: absolute;
  width: 100%;
}

.list-enter-from {
  opacity: 0;
  transform: translateX(-50px) scale(0.9);
}

.list-leave-to {
  opacity: 0;
  transform: translateX(50px) scale(0.9);
}

/* 修改操作按钮样式 */
.custom-btn {
  border: none;
  padding: 8px 20px;
  border-radius: 8px;
  color: white;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.custom-btn::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 150%;
  height: 150%;
  background: rgba(255, 255, 255, 0.2);
  transform: translate(-50%, -50%) rotate(45deg) scale(0);
  transition: transform 0.6s ease;
}

.custom-btn:hover::before {
  transform: translate(-50%, -50%) rotate(45deg) scale(1);
}

.edit {
  background: linear-gradient(135deg, #238636 0%, #1b6b2c 100%);
}

/* 创建按钮样式 */
.create-btn {
  background: linear-gradient(135deg, #23a6d5 0%, #23d5ab 100%) !important;
  border: none !important;
  padding: 10px 24px !important;
  border-radius: 8px !important;
  font-weight: 500 !important;
  letter-spacing: 0.5px;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(35, 166, 213, 0.3);
}

.create-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(35, 166, 213, 0.4);
}

.create-btn::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 150%;
  height: 150%;
  background: rgba(255, 255, 255, 0.2);
  transform: translate(-50%, -50%) rotate(45deg) scale(0);
  transition: transform 0.6s ease;
}

.create-btn:hover::before {
  transform: translate(-50%, -50%) rotate(45deg) scale(1);
}

/* 对话框样式 */
:deep(.el-dialog) {
  background: rgba(13, 17, 23, 0.7);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(48, 54, 61, 0.2);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  border-radius: 16px;
}

:deep(.el-dialog__header) {
  border-bottom: 1px solid rgba(48, 54, 61, 0.2);
  padding: 20px;
}

:deep(.el-dialog__title) {
  color: #fff;
  font-size: 1.2em;
  text-shadow: 0 0 10px rgba(49, 232, 249, 0.3);
}

:deep(.el-dialog__body) {
  color: #ffffff;
  padding: 30px 20px;
}

:deep(.el-form-item__label) {
  color: #fff;
}

:deep(.el-input__wrapper) {
  background: rgba(22, 27, 34, 0.4);
  border: 1px solid rgba(49, 232, 249, 0.3);
  box-shadow: 0 0 10px rgba(49, 232, 249, 0.1);
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover) {
  border-color: rgba(49, 232, 249, 0.8);
  box-shadow: 0 0 15px rgba(49, 232, 249, 0.2);
}

:deep(.el-input__wrapper.is-focus) {
  border-color: #31e8f9;
  box-shadow: 0 0 20px rgba(49, 232, 249, 0.3);
}

:deep(.el-input__inner) {
  color: #ffffff;
}

:deep(.el-textarea__inner) {
  background: rgba(22, 27, 34, 0.4);
  border: 1px solid rgba(49, 232, 249, 0.3);
  box-shadow: 0 0 10px rgba(49, 232, 249, 0.1);
  color: #ffffff;
  transition: all 0.3s ease;
}

:deep(.el-textarea__inner:hover) {
  border-color: rgba(49, 232, 249, 0.8);
  box-shadow: 0 0 15px rgba(49, 232, 249, 0.2);
}

:deep(.el-textarea__inner:focus) {
  border-color: #31e8f9;
  box-shadow: 0 0 20px rgba(49, 232, 249, 0.3);
}


/* 弹窗按钮样式 */
:deep(.el-dialog__footer .el-button) {
  border: none;
  padding: 8px 20px;
  border-radius: 8px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

:deep(.el-dialog__footer .el-button--default) {
  background: rgba(48, 54, 61, 0.5);
  color: #ffffff;
  border: 1px solid rgba(49, 232, 249, 0.3);
}

:deep(.el-dialog__footer .el-button--default:hover) {
  background: rgba(48, 54, 61, 0.7);
  border-color: rgba(49, 232, 249, 0.8);
  box-shadow: 0 0 15px rgba(49, 232, 249, 0.2);
  transform: translateY(-2px);
}

:deep(.el-dialog__footer .el-button--primary) {
  background: linear-gradient(135deg, #23a6d5 0%, #23d5ab 100%);
  border: none;
  box-shadow: 0 4px 15px rgba(35, 166, 213, 0.3);
}

:deep(.el-dialog__footer .el-button--primary:hover) {
  box-shadow: 0 6px 20px rgba(35, 166, 213, 0.4);
  transform: translateY(-2px);
}

:deep(.el-dialog__footer .el-button::before) {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 150%;
  height: 150%;
  background: rgba(255, 255, 255, 0.2);
  transform: translate(-50%, -50%) rotate(45deg) scale(0);
  transition: transform 0.6s ease;
}

:deep(.el-dialog__footer .el-button:hover::before) {
  transform: translate(-50%, -50%) rotate(45deg) scale(1);
}
</style>
