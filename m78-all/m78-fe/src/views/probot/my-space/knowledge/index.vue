<template>
  <div class="knowledge-wrap" v-if="route.name === 'AI Probot Space'">
    <div class="knowledge-filter">
      <div class="knowledge-form">
        <el-form :model="form" inline>
          <el-row :gutter="15">
            <el-form-item label="名称:">
              <el-input
                v-model="form.knowledgeBaseName"
                placeholder="请输入知识库名称"
                @keyup.enter="handleSearch"
                clearable
                @clear="handleSearch"
              />
            </el-form-item>
            <el-form-item label="创建人:">
              <el-input
                v-model="form.creator"
                placeholder="创建人"
                @keyup.enter="handleSearch"
                clearable
                @clear="handleSearch"
              />
            </el-form-item>
            <el-button type="primary" plain color="#40a3ff" @click="handleSearch">查询</el-button>
          </el-row>
        </el-form>
      </div>
      <div>
        <el-button type="primary" @click="create">新建知识库</el-button>
      </div>
    </div>
    <div class="knowledge-table">
      <el-table :data="tableData" style="width: 100%">
        <el-table-column label="知识库名称" width="220" v-slot="{ row, $index }">
          <BaseInfo
            :data="{
              name: row.knowledgeBaseName || '----',
              describe: row.remark,
              avatarUrl: row.avatarUrl || $index
            }"
            size="small"
          />
        </el-table-column>
        <el-table-column prop="type" label="类型" />
        <el-table-column prop="creator" label="创建人" />
        <el-table-column prop="gmtCreate" label="创建时间" v-slot="{ row }">
          {{ dateFormat(row.gmtCreate, 'yyyy-mm-dd HH:MM:ss') }}</el-table-column
        >
        <el-table-column prop="gmtModified" label="修改时间" v-slot="{ row }">
          {{ dateFormat(row.gmtModified, 'yyyy-mm-dd HH:MM:ss') }}</el-table-column
        >
        <el-table-column fixed="right" label="操作" v-slot="{ row }" width="180px">
          <el-button
            type="primary"
            size="small"
            class="btn-item"
            @click="edit(row)"
            text
            :disabled="!row.self"
            >编辑</el-button
          >
          <el-button type="primary" size="small" class="btn-item" @click="manage(row)" text
            >知识库管理</el-button
          >
          <el-button
            type="primary"
            size="small"
            class="btn-item"
            @click="remove(row, getList)"
            text
            :disabled="!row.self"
            >移除</el-button
          >
        </el-table-column>
      </el-table>
    </div>
    <knowledgeDialog
      v-model="knowledgeVisible"
      :data="knowledgeData"
      @onOk="getList"
    ></knowledgeDialog>
  </div>
  <el-empty v-else description="程序员正在抓紧开发中..." />
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { getKnowledgeList } from '@/api/probot-knowledge'
import dateFormat from 'dateformat'
import BaseInfo from '@/components/BaseInfo.vue'
import knowledgeDialog from './knowledgeDialog.vue'
import { useRouter, useRoute } from 'vue-router'
import remove from './remove'

const workSpaceId = computed(() => {
  return route.params.id as string
})

const form = reactive({
  creator: '',
  knowledgeBaseName: ''
})
const tableData = ref([])
const knowledgeVisible = ref(false)
const knowledgeData = ref({})

const router = useRouter()
const route = useRoute()

const handleSearch = () => {
  getList()
}
const create = () => {
  knowledgeVisible.value = true
  knowledgeData.value = {}
}
const edit = (row: any) => {
  knowledgeVisible.value = true
  knowledgeData.value = {
    ...row
  }
}
const manage = (row) => {
  router.push({
    path: `/probot-knowledge-manage/${row.id}/${row.knowledgeBaseId}`
  })
}

const getList = async () => {
  const { code, data } = await getKnowledgeList({
    ...form,
    workSpaceId: workSpaceId.value
  })
  if (code === 0) {
    tableData.value = data?.records
  } else {
    tableData.value = []
  }
}

onMounted(() => {
  getList()
})

watch(
  () => route.params.id,
  () => {
    getList()
  }
)
</script>

<style lang="scss" scoped>
.knowledge-filter {
  display: flex;
  justify-content: space-between;
}
.knowledge-form {
  display: flex;
}
.knowledge-table {
  .btn-item {
    padding-left: 4px;
    padding-right: 4px;
  }
}
</style>
