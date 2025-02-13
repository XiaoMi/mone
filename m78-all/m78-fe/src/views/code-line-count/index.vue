<template>
  <div class="page-statistics">
    <el-table :data="tableData" border style="width: 100%">
      <el-table-column prop="username" label="用户" />
      <el-table-column prop="codeLinesCount" label="代码行数" />
    </el-table>
    <div class="pagination">
      <div></div>
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 40, 80]"
        small
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { getUserCodeLines } from '@/api/code-line-count'

const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const tableData = ref<
  {
    username: string
    codeLinesCount: number
  }[]
>([])

const handleSizeChange = (pageSize: number) => {
  currentPage.value = 1
  fetchUserCodeLines(currentPage.value, pageSize)
}

const handleCurrentChange = (page: number) => {
  fetchUserCodeLines(page, pageSize.value)
}

const fetchUserCodeLines = async (page: number, pageSize: number) => {
  const { data } = await getUserCodeLines({
    currentPage: page,
    pageSize: pageSize
  })

  if (data.code == 0 && data.data) {
    tableData.value = data.data.records || []
    total.value = data.data.totalRow ?? 0
  } else {
    tableData.value = []
    total.value = 0
  }
}

const init = async () => {
  await fetchUserCodeLines(currentPage.value, pageSize.value)
}

init()
</script>

<style scoped lang="scss">
.page-statistics {
  padding: 20px;
}
.pagination {
  display: flex;
  justify-content: space-between;
  padding-top: 10px;
}
</style>
