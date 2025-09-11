<template>
  <div class="probots-wrap">
    <div class="recommend-container">
      <el-carousel height="300px" motion-blur :interval="6000">
        <el-carousel-item
          v-for="(item, index) in recommendList"
          :key="index"
          :style="' background-image: url(' + item.backgroundUrl + ')'"
          class="recommend-item"
        >
          <div class="recommend-item-reasons">
            <h3>今日推荐：{{ item.title }}</h3>
            <ul>
              <li v-for="(v, i) in item.recommendReasons" :key="i">
                {{ v }}
              </li>
            </ul>

            <div class="recommend-try-btn">
              <BaseInfo
                :data="{
                  avatarUrl: item.botAvatar,
                  name: item.botName,
                  describe: '@' + item.botCuser
                }"
                size="small"
              ></BaseInfo>
              <el-button
                type="primary"
                plain
                @click="handleDetail(item.botId)"
                v-if="item.botPermissions"
                >详情</el-button
              >
              <el-button type="primary" @click="handleTry(item.botId)">立即试用</el-button>
            </div>
          </div>
          <h3 class="small justify-center" text="2xl"></h3>
        </el-carousel-item>
      </el-carousel>
    </div>
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
              :placeholder="t('probot.enterProbotName')"
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
            <el-button type="primary" @click="handleSearch">{{ t('common.search') }}</el-button>
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
import { reactive, ref, watch, onMounted } from 'vue'
import type { FormInstance } from 'element-plus'
import { getProbotList } from '@/api/probot'
import BaseList from '@/components/probot/BaseList.vue'
import { t } from '@/locales'
import { useProbotStore } from '@/stores/probot'
import { useRoute, useRouter } from 'vue-router'
import { recommendCarouselList } from '@/api/probot-list'
import BaseInfo from '@/components/BaseInfo.vue'

const route = useRoute()
const router = useRouter()
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
const recommendList = ref([])

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

watch(
  () => route.query.category,
  (category) => {
    if (route.name === 'AI Probot List') {
      form.value.category = category ? Number(category) : undefined
      getList()
    }
  },
  {
    immediate: true,
    deep: true
  }
)

onMounted(() => {
  recommendCarouselList({
    type: 1,
    pageNum: 1,
    pageSize: 10,
    displayStatus: 1
  }).then((res) => {
    recommendList.value = res.data.list
  })
})

const handleTry = (botId) => {
  const { href } = router.resolve({
    path: '/probot-visit/' + botId
  })
  window.open(href, '_blank')
}

const handleDetail = (botId) => {
  const { href } = router.resolve({
    path: '/probot-view/' + botId
  })
  window.open(href, '_blank')
}
</script>
<style lang="scss" scoped>
.probots-wrap {
  padding: 20px 45px;
  min-width: 1000px;
  .pager {
    display: flex;
    align-items: center;
    justify-content: flex-end;
  }
  .recommend-container {
    padding-bottom: 20px;
    .recommend-item {
      background: linear-gradient(70deg, #ddd, #4e40e5);
      background-size: cover;
      background-repeat: no-repeat;
      background-position: center;
    }
    .recommend-item-reasons {
      width: 45%;
      min-width: 500px;
      background-color: rgba($color: #fff, $alpha: 0.9);
      border-radius: 10px;
      padding: 20px;
      position: relative;
      top: 20px;
      left: 60px;
      line-height: 30px;
      h3 {
        font-size: 18px;
      }
      ul {
        padding-left: 20px;
        padding-bottom: 20px;
      }
      li {
        list-style: disc;
        font-size: 14px;
      }
      .recommend-try-btn {
        border-top: 1px solid #ddd;
        padding-top: 20px;
        display: flex;
        justify-content: center;
        align-items: center;
      }
    }
  }
}
</style>
