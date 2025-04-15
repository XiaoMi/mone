<template>
  <div class="agent-list-container">
    <el-card class="box-card custom-card">
      <template #header>
        <div class="card-header">
          <span>Agent列表</span>
          <el-button 
            class="create-btn custom-btn" 
            type="primary" 
            @click="handleCreate"
          >
            创建Agent
          </el-button>
        </div>
      </template>

      <div class="future-table">
        <div class="table-header">
          <div class="header-cell" style="width: 80px">ID</div>
          <div class="header-cell flex-1">名称</div>
          <div class="header-cell flex-1">描述</div>
          <div class="header-cell" style="width: 180px">创建时间</div>
          <div class="header-cell" style="width: 200px">操作</div>
        </div>
        
        <TransitionGroup 
          name="list" 
          tag="div" 
          class="table-body"
        >
          <div 
            v-for="agent in agentList" 
            :key="agent.id" 
            class="table-row"
            :class="{'hover-effect': true}"
          >
            <div class="cell" style="width: 80px">
              <div class="id-badge">{{agent.id}}</div>
            </div>
            <div class="cell flex-1">
              <div class="name-container">
                {{agent.name}}
              </div>
            </div>
            <div class="cell flex-1">{{agent.description}}</div>
            <div class="cell" style="width: 180px">
              {{formatDate(agent.createdAt)}}
            </div>
            <div class="cell actions" style="width: 200px">
              <el-button 
                class="custom-btn edit"
                @click="handleEdit(agent)"
              >
                编辑
              </el-button>
              <el-button 
                class="custom-btn delete"
                @click="handleDelete(agent)"
              >
                删除
              </el-button>
            </div>
          </div>
        </TransitionGroup>
      </div>
    </el-card>

    <!-- 创建/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'create' ? '创建Agent' : '编辑Agent'"
      width="500px"
    >
      <el-form :model="agentForm" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="agentForm.name" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="agentForm.description" type="textarea" />
        </el-form-item>
        <el-form-item label="Agent URL">
          <el-input v-model="agentForm.agentUrl" />
        </el-form-item>
        <el-form-item label="公开">
          <el-switch v-model="agentForm.isPublic" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Service } from '@/common/req'

interface Agent {
  id: number
  name: string
  description: string
  createdAt: string
  agentUrl: string
  isPublic: boolean
}

const agentList = ref<Agent[]>([{
  id: 1,
  name: 'Agent 1',
  description: 'Agent 1 description',
  createdAt: '2024-01-01',
  agentUrl: 'http://localhost:8080/agent1',
  isPublic: true
}])
const loading = ref(false)
const dialogVisible = ref(false)
const dialogType = ref<'create' | 'edit'>('create')
const agentForm = ref({
  id: 0,
  name: '',
  description: '',
  agentUrl: '',
  isPublic: false
})

const fetchAgents = async () => {
  loading.value = true
  try {
    const response = await Service.get('/v1/agents')
    agentList.value = response.data
  } catch {
    ElMessage.error('获取Agent列表失败')
  } finally {
    loading.value = false
  }
}

const handleCreate = () => {
  dialogType.value = 'create'
  agentForm.value = {
    id: 0,
    name: '',
    description: '',
    agentUrl: '',
    isPublic: false
  }
  dialogVisible.value = true
}

const handleEdit = (row: Agent) => {
  dialogType.value = 'edit'
  agentForm.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = async (row: Agent) => {
  try {
    await ElMessageBox.confirm('确定要删除这个Agent吗？', '提示', {
      type: 'warning'
    })
    await Service.delete(`/v1/agents/${row.id}`)
    ElMessage.success('删除成功')
    fetchAgents()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSubmit = async () => {
  try {
    if (dialogType.value === 'create') {
      await Service.post('/v1/agents', {
        name: agentForm.value.name,
        description: agentForm.value.description,
        agentUrl: agentForm.value.agentUrl,
        isPublic: agentForm.value.isPublic
      })
      ElMessage.success('创建成功')
    } else {
      await Service.put(`/v1/agents/update/${agentForm.value.id}`, agentForm.value)
      ElMessage.success('更新成功')
    }
    dialogVisible.value = false
    fetchAgents()
  } catch {
    ElMessage.error(dialogType.value === 'create' ? '创建失败' : '更新失败')
  }
}

const formatDate = (date: string) => {
  return new Date(date).toLocaleString()
}

onMounted(() => {
  fetchAgents()
})
</script>

<style scoped>

.agent-list-container {
  padding: 20px;
  background: linear-gradient(135deg, #23a6d5 0%, #23d5ab 100%);  /* 更深的黑色背景 */
  min-height: 100vh;
  position: relative;
  overflow: hidden;
}

/* 更暗的网格背景 */
.agent-list-container::before {
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

.future-table {
  background: rgba(13, 17, 23, 0.4);
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid rgba(48, 54, 61, 0.2);
  position: relative;
}

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
  animation: scanline 3s linear infinite;
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

.name-container {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #ffffff;
}

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

.delete {
  background: linear-gradient(135deg, #da3633 0%, #b62824 100%);
  margin-left: 12px;
}

/* 添加新的动画关键帧 */
@keyframes scanline {
  0% { transform: translateY(-100%); }
  100% { transform: translateY(100vh); }
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 优化列表动画 */
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

@keyframes pulse {
  0% {
    transform: scale(1);
    opacity: 0.8;
  }
  50% {
    transform: scale(1.8);
    opacity: 0;
  }
  100% {
    transform: scale(1);
    opacity: 0;
  }
}

/* 修改创建按钮样式 */
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

/* 添加自定义对话框样式 */
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

:deep(.el-dialog__footer) {
  border-top: 1px solid rgba(48, 54, 61, 0.2);
  padding: 20px;
}

:deep(.el-switch__core) {
  border-color: rgba(48, 54, 61, 0.4);
  background: rgba(22, 27, 34, 0.4);
}

:deep(.el-switch.is-checked .el-switch__core) {
  background: linear-gradient(135deg, #23a6d5 0%, #23d5ab 100%);
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
