<template>
  <div class="h-full flex flex-col">
    <div class="pb-[10px] shrink-0 flex justify-items-center justify-between">
      <div>
        <h3>
          <span>数据库：{{ db?.database || '请选择数据库' }}</span>
        </h3>
      </div>
      <div>
        <el-button
          :disabled="disabledSBtn"
          v-if="props.tableName && showSave"
          type="primary"
          size="small"
          @click="save"
          >保存</el-button
        >
        <MetaData
          v-model="dialogVisible"
          :metaValue="props.db?.customKnowledge"
          @updateMetaValue="updateMetaValue"
        />
      </div>
    </div>
    <div class="flex-1 flex overflow-x-auto">
      <Sheet @change="change" :json="props.json"></Sheet>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import Sheet from '@/components/sheet/index.vue'
import MetaData from './MetaData.vue'
import { updateMeta, updateTableDatas } from '@/api/data-source'
import { ElMessage } from 'element-plus'

const emits = defineEmits(['updateTableDatas'])

const props = defineProps<{
  db: DataSource.DB | null
  tableName: string
  json: Record<string, string>[]
}>()

const disabledSBtn = ref(false)
const dialogVisible = ref(false)
const updateData = ref<{
  insert: any[]
  delete: any[]
  update: any[]
}>({
  insert: [],
  delete: [],
  update: []
})

const showSave = computed(() => {
  return (
    updateData.value.insert.length ||
    updateData.value.delete.length ||
    updateData.value.update.length
  )
})

const change = (data: { insert: any[]; update: any[]; delete: any[] }) => {
  console.log('change', data, props.tableName)
  updateData.value = data
}

const save = async () => {
  disabledSBtn.value = true
  let allSuccess = true
  try {
    for (const item of updateData.value.insert) {
      const { code } = await updateTableDatas({
        datasourceId: props.db!.id,
        tableName: props.tableName,
        operationType: 'INSERT',
        id: item.id,
        newData: {
          ...item
        }
      })
      if (code != 0) {
        allSuccess = false
      }
    }
    for (const item of updateData.value.delete) {
      const { code } = await updateTableDatas({
        datasourceId: props.db!.id,
        tableName: props.tableName,
        operationType: 'DELETE',
        id: item.id
      })
      if (code != 0) {
        allSuccess = false
      }
    }
    for (const item of updateData.value.update) {
      const { code } = await updateTableDatas({
        datasourceId: props.db!.id,
        tableName: props.tableName,
        operationType: 'UPDATE',
        id: item.id,
        updateData: {
          ...item
        }
      })
      if (code != 0) {
        allSuccess = false
      }
    }
    if (allSuccess) {
      ElMessage.success('成功')
      emits('updateTableDatas')
      updateData.value = {
        insert: [],
        update: [],
        delete: []
      }
    } else {
      ElMessage.error('失败')
    }
  } catch (e) {
    console.error(e)
    ElMessage.error('失败')
  } finally {
    disabledSBtn.value = false
  }
}

const updateMetaValue = (metaValue: string) => {
  if (!props.db) {
    ElMessage.error('需要先选择数据库')
    return
  }
  updateMeta({
    id: props.db.id,
    customKnowledge: metaValue || ''
  }).then((res) => {
    if (res.code != 0) {
      ElMessage.error('更新失败')
      return
    }
    dialogVisible.value = false
  })
}
</script>
