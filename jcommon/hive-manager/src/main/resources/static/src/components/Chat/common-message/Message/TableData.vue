<!--
 * @Description: 
 * @Date: 2024-05-29 17:19:40
 * @LastEditTime: 2024-06-07 12:29:16
-->
<template>
  <div>
    <div>{{ text }}</div>
    <el-table :data="table" max-height="250" style="width: 100%" stripe border>
      <el-table-column
        :prop="item"
        :label="item"
        v-for="(item, key) in tableKey"
        min-width="180"
        :key="key"
      >
        <template #default="scope">
          <el-button
            v-if="item == 'id' || item == 'pipelineId'"
            link
            type="primary"
          >
            {{ scope.row[item] }}
          </el-button>
          <div v-else>
            {{ scope.row[item] }}
          </div>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue'

const props = defineProps({
  data: {
    type: String
  }
})

const text = ref('')
const table = ref([])
const tableKey = ref([])
const emits = defineEmits(['enterFn'])

onMounted(() => {
  const arr = props.data?.split('---table---')
  if (arr) {
    text.value = arr[0]
    const tableData = JSON.parse(arr[1]).data
    if (tableData) {
      const tableResult = JSON.parse(tableData).result
      if (tableResult) {
        table.value = JSON.parse(tableResult)
        if (table.value.length) {
          tableKey.value = Object.keys(table.value[0])
          console.log('tableKey.value', tableKey.value)
        }
      }
    }
  }
})

onBeforeUnmount(() => {})
</script>

<style scoped></style>
