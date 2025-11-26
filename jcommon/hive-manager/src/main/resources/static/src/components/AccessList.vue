<template>
  <div class="access-list">
    <!-- 创建 Access 按钮 -->
    <div class="access-header">
        <h3 class="access-title">Access 列表</h3>
        <el-button type="primary" @click="showCreateDialog" class="create-access-btn" size="small">创建 Access</el-button>
    </div>

    <!-- Access 列表表格 -->
    <el-table :data="accessData" style="width: 100%; margin-top: 20px">
        <el-table-column prop="id" label="ID" width="50"/>
      <el-table-column prop="accessApp" label="调用方" />
      <el-table-column prop="description" label="描述" />
      <el-table-column prop="accessKey" label="Access Key" />
      <el-table-column prop="ctime" label="创建时间" >
        <template #default="{ row }">
          {{ formatDate(row.ctime) }}
        </template>
      </el-table-column>
      <el-table-column prop="utime" label="更新时间" >
        <template #default="{ row }">
          {{ formatDate(row.utime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="80">
        <template #default="{ row }">
          <el-button type="danger" size="small" text @click="handleDelete(row.id)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 创建 Access 对话框 -->
    <el-dialog 
    v-model="dialogVisible" 
    title="创建 Access" 
    width="50%"
    append-to-body
    class="access-dialog"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="调用方" prop="accessAppId">
          <el-select v-model="form.accessAppId" placeholder="请选择调用方" style="width: 100%">
            <el-option
              v-for="user in userList"
              :key="user.id"
              :label="user.username"
              :value="user.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { accessList, createAccess, deleteAccess } from '@/api/agent'
import { getUserList } from '@/api/user'
import type { Access } from '@/api/agent'
const props = defineProps<{
  agentId: number
}>()

const accessData = ref<Access[]>([])
const dialogVisible = ref(false)
const formRef = ref()
const form = ref({
  agentId: 0,
  accessApp: '',
  description: '',
  accessAppId: ''
})

const rules = {
  accessAppId: [{ required: true, message: '请选择调用方', trigger: 'blur' }],
  description: [{ required: true, message: '请输入描述', trigger: 'blur' }]
}

const userList = ref<any[]>([])

watch(() => dialogVisible.value, (val) => {
    if (!val) {
        form.value = {
            agentId: 0,
            accessApp: '',
            description: '',
            accessAppId: ""
        }
        formRef.value.resetFields()
    }
})

// 获取 Access 列表
const fetchAccessList = async () => {
  try {
    const res = await accessList(props.agentId, {
      agentId: props.agentId,
      accessApp: '',
      description: ''
    })
    if (res.data.code === 200) {
      accessData.value = res.data.data || []
    } else {
      ElMessage.error('获取 Access 列表失败')
    }
  } catch (error) {
    ElMessage.error('获取 Access 列表失败')
  }
}

// 获取用户列表
const fetchUserList = async () => {
  try {
    const res = await getUserList()
    if (res.data.code === 200) {
      userList.value = res.data.data || []
    } else {
      ElMessage.error('获取用户列表失败')
    }
  } catch (error) {
    ElMessage.error('获取用户列表失败')
  }
}

onMounted(() => {
  fetchUserList()
})

// 显示创建对话框
const showCreateDialog = () => {
  form.value = {
    agentId: props.agentId,
    accessApp: '',
    description: '',
    accessAppId: ""
  }
  dialogVisible.value = true
}

// 创建 Access
const handleCreate = async () => {
    formRef.value.validate(async(valid: boolean) => {
        if (valid) {
          form.value.accessApp = userList.value.find(user => user.id == form.value.accessAppId)?.username
            const res = await createAccess(form.value)
            if (res.data.code === 200) {
                ElMessage.success('创建成功')
                dialogVisible.value = false
                fetchAccessList()
            } else {
                ElMessage.error('创建失败')
            }
        }
    })
}

// 删除 Access
const handleDelete = async (agentId: number) => {

  try {
    await ElMessageBox.confirm('确认删除该 Access？', '提示', {
      type: 'warning'
    })
    const res = await deleteAccess(agentId)
    if (res.data.code === 200) {
      ElMessage.success('删除成功')
      fetchAccessList()
    } else {
      ElMessage.error('删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

watch(() => props.agentId, (val) => {
    if (val) {
        form.value.agentId = val
        fetchAccessList()
    }
}, { immediate: true })

const formatDate = (date: string) => {
  return new Date(date).toLocaleString()
}

</script>

<style scoped>
.access-list {
  margin-top: 24px;
    padding: 20px;
    background: rgba(48, 54, 61, 0.2);
    border-radius: 8px;
}
.access-header {
    display: flex;
    justify-content: space-between;
}
.access-header h3 {
    color: #31e8f9;
    font-size: 18px;
    margin-bottom: 16px;
    text-shadow: 0 0 10px rgba(49, 232, 249, 0.3);
}
.create-access-btn {
    background: transparent;
    border: 1px solid #31e8f9;
    color: #31e8f9;
    transition: all 0.3s ease;
    
    &:hover {
      background: rgba(49, 232, 249, 0.1);
      border-color: rgba(49, 232, 249, 0.8);
      box-shadow: 0 0 15px rgba(49, 232, 249, 0.2);
      transform: translateY(-2px);
    }
  }
:deep(.el-table) {
    margin-top: 0 !important;
    background: transparent;
    --el-table-tr-bg-color: transparent;
    --el-table-border-color: rgba(49, 232, 249, 0.2);
    --el-table-header-bg-color: rgba(48, 54, 61, 0.3);
    --el-table-header-text-color: #31e8f9;
    --el-table-text-color: #ffffff;
    --el-table-row-hover-bg-color: transparent;
  }
</style>
