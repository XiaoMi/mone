<template>
  <div class="pagination">
    <div><slot></slot></div>
    <el-pagination
      small
      background
      v-model:currentPage="currentPage"
      v-model:page-size="pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :page-sizes="[10, 20, 30, 40]"
      @size-change="sizeChange"
      :total="total"
      @current-change="currentChange"
    />
  </div>
</template>

<script setup lang="ts">
import { defineProps, defineEmits, ref, watch } from 'vue'

/**  传递过来的props和emit */
const props = defineProps({
  pageInfo: {
    type: Object,
    default: () => ({})
  }
})
const emit = defineEmits(['handlePage'])

/**  data */
const currentPage = ref(props.pageInfo.currentPage || 1)
const pageSize = ref(props.pageInfo.pageSize || 10)
const total = ref(props.pageInfo.total || 0)

/**  methods */
// 页码改版
const sizeChange = (val: number) => {
  pageSize.value = val
  emit('handlePage', { pageSize: val, currentPage: 1 })
}
// 当前页改变
const currentChange = (val: number) => {
  if (total.value) {
    currentPage.value = val
    emit('handlePage', { currentPage: val })
  }
}

/**  watch */
//监听
watch(
  () => props.pageInfo,
  (newVal) => {
    if (currentPage.value !== newVal.currentPage) {
      currentPage.value = newVal.currentPage //这里得改变并没有触发currentChange事件
    }
    if (total.value !== newVal.total) total.value = newVal.total
  },
  {
    immediate: true,
    deep: true
  }
)
</script>

<style lang="scss" scoped>
//分页
.pagination {
  display: flex;
  justify-content: space-between;
  padding-top: 10px;

  .el-pagination.el-pagination--small .el-input--small,
  .el-pagination.el-pagination--small .el-input__inner {
    height: auto !important;
  }
}
</style>
