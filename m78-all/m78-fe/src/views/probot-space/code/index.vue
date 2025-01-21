<template>
  <div class="code-wrap" v-loading="state.loading">
    <div class="search">
      <el-form :model="state.search" inline ref="formRef" @submit.prevent>
        <el-row :gutter="15">
          <el-form-item :label="t('common.name') + ':'">
            <el-input
              v-model="state.search.name"
              :placeholder="t('codeBase.namePlaceholder')"
              @keyup.enter="handleSearch"
              clearable
              @clear="handleSearch"
            />
          </el-form-item>
          <el-button
            type="primary"
            @click="handleSearch"
            size="default"
            plain
            class="btn-item"
            >{{ t('common.search') }}</el-button
          >
        </el-row>
      </el-form>
      <el-button type="primary" @click="state.editVisible = true" size="default">{{
        t('codeBase.createBtn')
      }}</el-button>
    </div>
    <!-- table空状态状态统一，不用做特殊处理 -->
    <TableList :tableList="state.tableList" @onOk="getList" />
    <div class="pager" v-if="state.total">
      <el-pagination
        small
        background
        layout="prev, pager, next"
        :total="state.total"
        :page-size="state.search.pageSize"
        :v-model:current-page="state.search.pageNum"
        hide-on-single-page
        @change="handleChangePage"
      />
    </div>
  </div>
  <EditCodeGroup v-model="state.editVisible" @onOk="getList" />
</template>

<script lang="ts" setup>
import { codeList } from '@/api/probot-code'
import EditCodeGroup from './EditCodeGroup.vue'
import { reactive, onBeforeMount, watch } from 'vue'
import TableList from './TableList.vue'
import { t } from '@/locales'
import { useRoute } from 'vue-router'

const route = useRoute()
const state = reactive({
  editVisible: false,
  tableList: [],
  search: {
    name: undefined,
    userName: undefined,
    pageSize: 16,
    pageNum: 1
  },
  total: 0,
  loading: true
})

const getList = (page = 1) => {
  state.loading = true
  state.search.pageNum = page
  codeList({
    ...state.search,
    type: 1
  })
    .then((data) => {
      if (data?.data.records?.length) {
        state.tableList = data.data.records || []
        state.total = data.data.totalRow || 0
      } else {
        state.tableList = []
        state.total = 0
      }
    })
    .catch((e) => {
      console.log(e)
    })
    .finally(() => {
      state.loading = false
    })
}

const handleSearch = () => {
  getList(1)
}
const handleChangePage = (page: number) => {
  getList(page)
}
onBeforeMount(() => {
  getList()
})

watch(
  () => route.params.id,
  () => {
    if (route.name === 'AI Probot Space') {
      state.search = {
        name: undefined,
        userName: undefined,
        pageSize: 16,
        pageNum: 1
      }
      getList()
    }
  }
)
</script>

<style lang="scss" scoped>
.code-wrap {
  .search {
    display: flex;
    justify-content: space-between;
    .btn-item {
      margin-bottom: 18px;
    }
  }
  .pager {
    margin-top: 10px;
    display: flex;
    align-items: center;
    justify-content: flex-end;
  }
}
</style>
