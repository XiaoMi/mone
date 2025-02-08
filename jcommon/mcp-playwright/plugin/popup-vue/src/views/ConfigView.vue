<template>
  <div class="config-page">
    <el-button type="primary" @click="showCreateDialog">
      {{ $t('config.create') }}
    </el-button>

    <el-table :data="configs" v-loading="loading">
      <el-table-column prop="selector" :label="$t('config.selector')" />
      <el-table-column prop="key" :label="$t('config.key')" />
      <el-table-column prop="value" :label="$t('config.value')" />
      <el-table-column prop="description" :label="$t('config.description')" />
      <el-table-column :label="$t('common.actions')">
        <template #default="{ row }">
          <el-button type="primary" @click="handleEdit(row)">
            {{ $t('common.edit') }}
          </el-button>
          <el-button type="danger" @click="handleDelete(row)">
            {{ $t('common.delete') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog
      :title="isEdit ? $t('config.edit') : $t('config.create')"
      v-model="dialogVisible"
    >
      <el-form :model="form" ref="formRef">
        <el-form-item :label="$t('config.selector')" prop="selector">
          <el-input v-model="form.selector" />
        </el-form-item>
        <el-form-item :label="$t('config.key')" prop="key">
          <el-input v-model="form.key" />
        </el-form-item>
        <el-form-item :label="$t('config.value')" prop="value">
          <el-input v-model="form.value" />
        </el-form-item>
        <el-form-item :label="$t('config.description')" prop="description">
          <el-input v-model="form.description" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">
          {{ $t('common.cancel') }}
        </el-button>
        <el-button type="primary" @click="handleSubmit">
          {{ $t('common.confirm') }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { configApi } from '../api/config'
import type { SelectorConfig } from '../model/config'

const configs = ref<SelectorConfig[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref<SelectorConfig>({
  selector: '',
  key: '',
  value: '',
  description: ''
})

const loadConfigs = async () => {
  loading.value = true
  try {
    const { data } = await configApi.list()
    configs.value = data
  } catch (error) {
    ElMessage.error('加载配置失败, ' + error)
  } finally {
    loading.value = false
  }
}

const showCreateDialog = () => {
  isEdit.value = false
  form.value = {
    selector: '',
    key: '',
    value: '',
    description: ''
  }
  dialogVisible.value = true
}

const handleEdit = (row: SelectorConfig) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = async (row: SelectorConfig) => {
  if (!row.id) return
  
  try {
    await ElMessageBox.confirm('确认删除该配置？')
    await configApi.delete(row.id)
    ElMessage.success('删除成功')
    loadConfigs()
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

const handleSubmit = async () => {
  try {
    if (isEdit.value) {
      await configApi.update(form.value)
    } else {
      await configApi.create(form.value)
    }
    ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
    dialogVisible.value = false
    loadConfigs()
  } catch (error) {
    ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
  }
}

onMounted(() => {
  loadConfigs()
})
</script> 