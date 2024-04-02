<template>
  <div class="comp-wrap">
    <div class="base-info">
      <p>创建人：{{ state.userName }}</p>
      <p>创建时间：{{ state.createTime }}</p>
      <p>状态：{{ props.status === E_PLUGIN.getEnum('PUB', 'key').value ? '已下架' : '已上架' }}</p>
    </div>

    <p class="desc">描述：{{ state.desc }}</p>
    <div class="header">
      <strong>请求头</strong>
      <el-table :data="state.tableHeader">
        <el-table-column prop="name" label="键" width="180" />
        <el-table-column prop="value" label="值" />
      </el-table>
    </div>
    <div class="input">
      <strong>输入参数</strong>
      <el-table :data="state.inputHeader">
        <el-table-column prop="name" label="参数名" width="180" />
        <el-table-column prop="desc" label="参数说明" />
      </el-table>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { reactive, watch } from 'vue'
import { E_PLUGIN } from '@/views/probot/my-space/plugins/constants.ts'
const props = defineProps({
  item: {
    type: Object,
    default() {
      return {}
    }
  }
})
const state = reactive({
  desc: '',
  method: '',
  userName: '',
  createTime: '',
  tableHeader: [],
  inputHeader: []
})
watch(
  () => props.item,
  (val) => {
    if (val.id) {
      state.desc = val.desc
      state.method = val.botPluginMeta.http_method
      state.userName = val.userName
      state.createTime = val.createTime
      state.tableHeader = Object.keys(val.botPluginMeta.http_headers || {}).map((k) => ({
        name: k,
        value: val.botPluginMeta.http_headers[k]
      }))
      state.inputHeader = val.botPluginMeta.input
    }
  },
  {
    deep: true,
    immediate: true
  }
)
</script>
<style lang="scss" scoped>
.comp-wrap {
  height: 100%;
  overflow-y: auto;
  &::-webkit-scrollbar {
    display: none;
  }
  .base-info {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 20px;
    p {
      font-size: 14px;
    }
  }
  strong {
    font-size: 16px;
    margin-bottom: 8px;
    display: inline-block;
  }
  .desc {
    font-size: 14px;
    margin-bottom: 20px;
  }
  .header {
    margin-bottom: 20px;
  }
  &:deep(.oz-table) {
    background-color: transparent;
    tr {
      background-color: transparent;
      &:hover td.oz-table__cell {
        background-color: transparent;
      }
      th {
        background-color: transparent;
        border-bottom: 1px solid #dedbdb;
        .cell {
          color: #333;
          font-size: 12px;
        }
      }
      td {
        background-color: transparent;
        border-bottom: 1px solid #dedbdb;
        padding: 12px 0;
      }
    }
    .oz-table__inner-wrapper::before {
      background-color: transparent;
    }
  }
}
</style>
