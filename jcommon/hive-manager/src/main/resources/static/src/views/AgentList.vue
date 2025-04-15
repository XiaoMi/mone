<template>
  <div class="agent-list-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>Agent列表</span>
          <el-button type="primary" @click="handleCreate">创建Agent</el-button>
        </div>
      </template>

      <el-table :data="agentList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
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

const agentList = ref<Agent[]>([])
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
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
