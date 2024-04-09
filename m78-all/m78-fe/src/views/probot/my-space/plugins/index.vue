<template>
  <div class="plugin-wrap" v-loading="state.loading">
    <div class="search">
      <el-form :model="state.search" inline>
        <el-row :gutter="15">
          <el-form-item label="名称:">
            <el-input
              v-model="state.search.name"
              :placeholder="t('plugin.enterPluginName')"
              @keyup.enter="handleSearch"
              clearable
              @clear="handleSearch"
            />
          </el-form-item>
          <el-form-item :label="`${t('plugin.username')}:`">
            <el-input
              v-model="state.search.userName"
              :placeholder="t('plugin.enterUserName')"
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
            color="#40a3ff"
            >{{ t('plugin.search') }}</el-button
          >
        </el-row>
      </el-form>
      <el-button
        type="primary"
        @click="state.editVisible = true"
        size="default"
        v-if="route.name === 'AI Probot Space'"
        >{{ t('plugin.createPluginBtn') }}</el-button
      >
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
  <EditPluginGroup v-model="state.editVisible" @onOk="getList" />
</template>

<script lang="ts" setup>
import { pluginList, type IPluginItem } from '@/api/plugins'
import EditPluginGroup from './EditPluginGroup.vue'
import { reactive, onBeforeMount, watch } from 'vue'
import TableList from './TableList.vue'
import Lodash from 'lodash'
import { t } from '@/locales'
import { useRoute } from 'vue-router'

const route = useRoute()
const state = reactive({
  editVisible: false,
  tableList: [] as Array<IPluginItem>,
  search: {
    name: undefined,
    userName: undefined,
    pageSize: 16,
    pageNum: 1,
    workspaceId: route.params.id
  },
  total: 0,
  loading: true
})

const getList = (page = 1) => {
  state.loading = true
  state.search.pageNum = page
  pluginList(state.search)
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

const loadList = Lodash.debounce(getList, 500)

const handleSearch = () => {
  loadList(1)
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
    state.search = {
      name: undefined,
      userName: undefined,
      pageSize: 16,
      pageNum: 1,
      workspaceId: route.params.id
    }
    getList()
  }
)
</script>

<style lang="scss" scoped>
.plugin-wrap {
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
