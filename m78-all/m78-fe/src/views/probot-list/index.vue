<template>
  <div class="probots-wrap">
    <el-form ref="formRef" :model="form" status-icon :size="formSize">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-form-item prop="status" :label="`${t('probot.userManager.status')}:`">
            <el-select
              v-model="form.status"
              style="width: 100%"
              :placeholder="t('probot.userManager.selectStatus')"
              @change="handleSearch"
              clearable
            >
              <el-option
                v-for="item in probotStore.statusList"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item prop="name" label="Probot名称:">
            <el-input
              v-model="form.name"
              :placeholder="t('probot.userManager.enterProbotName')"
              @keyup.enter="handleSearch"
              clearable
              @clear="handleSearch"
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item props="category" label="类型:">
            <el-select
              v-model="form.category"
              style="width: 100%"
              :placeholder="t('probot.userManager.selectType')"
              @change="handleSearch"
              clearable
            >
              <el-option
                v-for="item in probotStore.categoryList['1']"
                :key="item?.id"
                :label="item?.name"
                :value="item?.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item>
            <el-button type="primary" @click="handleSearch">{{ t('probot.search') }}</el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <BaseList type="probot" :loading="botLoading" :data="botList"></BaseList>
    <div class="pager" v-if="state.total">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="state.total"
        :page-size="state.pageSize"
        :v-model:current-page="state.pageNum"
        :hide-on-single-page="true"
        @change="handleChangePage"
      />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onBeforeMount, watch } from 'vue'
import type { FormInstance } from 'element-plus'
import { getProbotList } from '@/api/probot'
import BaseList from '@/components/BaseList.vue'
import { t } from '@/locales'
import { useProbotStore } from '@/stores/probot'
import { useRoute } from 'vue-router'

const route = useRoute()
const probotStore = useProbotStore()
const formSize = ref('large') //default\large
const formRef = ref<FormInstance>()
const form = ref({
  status: '',
  name: '',
  category: undefined
})

const botLoading = ref(false)
const botList = ref([])

const state = reactive({
  pageNum: 1,
  pageSize: 12,
  total: 0
})

const getList = (page = 1) => {
  state.pageNum = page
  botLoading.value = true
  getProbotList({
    ...form.value,
    pageNum: state.pageNum,
    pageSize: state.pageSize
  })
    .then((res) => {
      if (res?.data?.records?.length) {
        botList.value = res?.data?.records || []
        state.total = res.data.totalRow || 0
        document.querySelector('.page-wrap')?.scrollTo(0, 0)
      } else {
        botList.value = []
        state.total = 0
      }
    })
    .catch((e) => {
      console.log(e)
    })
    .finally(() => {
      botLoading.value = false
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
  () => route.query,
  ({ category }) => {
    form.value.category = category ? Number(category) : undefined
    getList()
  },
  {
    immediate: true,
    deep: true
  }
)
</script>
<style lang="scss" scoped>
.probots-wrap {
  width: 1200px;
  margin: 0 auto;
  padding: 20px;
  .pager {
    display: flex;
    align-items: center;
    justify-content: flex-end;
  }
}
</style>
