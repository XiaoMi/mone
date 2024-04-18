<template>
  <el-drawer size="80%" direction="rtl" v-model="dialogVisible" :title="title">
    <div class="pb-[20px] flex flex-row-reverse">
      <el-button type="primary" size="small" @click="addCol">新增字段</el-button>
    </div>
    <el-table :data="tableCols">
      <el-table-column prop="columnName" label="列名">
        <template #default="scope">
          <el-input size="small" v-model="scope.row.columnName"></el-input>
        </template>
      </el-table-column>
      <el-table-column prop="columnType" label="数据类型">
        <template #default="scope">
          <el-input size="small" v-model="scope.row.columnType"></el-input>
        </template>
      </el-table-column>
      <el-table-column prop="nullable" label="非空">
        <template #default="scope">
          <el-input size="small" v-model="scope.row.nullable"></el-input>
        </template>
      </el-table-column>
      <el-table-column prop="defaultValue" label="默认">
        <template #default="scope">
          <el-input size="small" v-model="scope.row.defaultValue"></el-input>
        </template>
      </el-table-column>
      <el-table-column prop="columnComment" label="注释">
        <template #default="scope">
          <el-input size="small" v-model="scope.row.columnComment"></el-input>
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="操作" width="120">
        <template #default="scope">
          <el-button link :icon="Upload" size="small" @click="saveTableCol(scope.row)"></el-button>
          <el-popconfirm
            v-if="scope.row.operationType == 'MODIFY'"
            confirm-button-text="是"
            cancel-button-text="否"
            icon-color="#626AEF"
            title="确定要删该列么?"
            @confirm="deleteTableCol(scope.row.columnName)"
            @cancel="() => {}"
          >
            <template #reference>
              <el-button link :icon="Delete"></el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
  </el-drawer>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { Delete, Upload } from '@element-plus/icons-vue'
import { getTableCols, updateTableCol } from '@/api/data-source'
import { ElMessage } from 'element-plus'

interface TableCol {
  columnAvatar: string
  columnComment: string
  columnName: string
  columnType: string
  operationType: 'ADD' | 'DROP' | 'MODIFY'
  nullable: string
  defaultValue: string
}

const props = defineProps<{
  databaseId: string
  tableName: string
  modelValue: boolean
}>()

const tableCols = ref<TableCol[]>([])

const emits = defineEmits(['update:modelValue'])

const title = computed(() => {
  return props.tableName + '表的列'
})
const dialogVisible = computed({
  get() {
    return props.modelValue
  },
  set(dialogVisible) {
    emits('update:modelValue', dialogVisible)
  }
})

const addCol = () => {
  tableCols.value.unshift({
    columnAvatar: '',
    columnComment: '',
    columnName: '',
    columnType: '',
    operationType: 'ADD',
    nullable: 'YES',
    defaultValue: ''
  })
}

const deleteTableCol = async (columnName: string) => {
  const { code } = await updateTableCol({
    datasourceId: props.databaseId,
    tableName: props.tableName,
    columnOperations: [
      {
        operationType: 'DROP',
        columnName
      }
    ]
  })
  if (code == 0) {
    ElMessage.success('成功')
  } else {
    ElMessage.error('失败')
  }
}

const saveTableCol = async (col: TableCol) => {
  const { code } = await updateTableCol({
    datasourceId: props.databaseId,
    tableName: props.tableName,
    columnOperations: [
      {
        ...col
      }
    ]
  })
  if (code == 0) {
    ElMessage.success('成功')
  } else {
    ElMessage.error('失败')
  }
}

const fetchTableCols = async () => {
  tableCols.value = []
  const { code, data } = await getTableCols({
    datasourceId: props.databaseId,
    tableName: props.tableName
  })
  if (code == 0 && Array.isArray(data)) {
    tableCols.value = data.map((it) => {
      return {
        ...it,
        operationType: 'MODIFY'
      }
    })
  } else {
    tableCols.value = []
  }
}

const init = async () => {
  await fetchTableCols()
}

init()
</script>
