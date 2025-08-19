<template>
  <div class="probots-wrap">
    <div class="probots-filter">
      <div class="probots-form">
        <el-form ref="formRef" :model="state.search" inline>
          <el-row :gutter="15">
            <el-form-item props="name" label="名称:">
              <el-input
                :placeholder="t('probot.enterProbotName')"
                v-model="state.search.name"
                @keyup.enter="handleSearch"
                clearable
                @clear="handleSearch"
              />
            </el-form-item>
            <el-form-item :label="`${t('probot.userManager.status')}:`" props="status">
              <el-select
                style="width: 120px"
                :placeholder="t('probot.userManager.selectStatus')"
                v-model="state.search.status"
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
            <el-form-item props="category" label="类型:">
              <el-select
                style="width: 120px"
                :placeholder="t('probot.userManager.selectType')"
                v-model="state.search.category"
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
            <el-button type="primary" plain @click="handleSearch">{{
              t('common.search')
            }}</el-button>
          </el-row>
        </el-form>
      </div>
      <div class="head-right-container" v-if="route.name != 'AI Probot My Collect'">
        <el-button type="primary" plain @click="handleImport">{{ t('common.import') }}</el-button>
        <el-button type="primary" @click="handleCreate">{{
          t('probot.userManager.createBotBtn')
        }}</el-button>
      </div>
    </div>
    <template v-if="state.tableList?.length">
      <ul class="space-list">
        <li v-for="(item, index) in state.tableList" :key="index" @click="handleVisit(item)">
          <div class="head">
            <BaseInfo
              :data="{
                ...item,
                describe: item.botInfo.remark || '----',
                name: item.botInfo.name || '----',
                avatarUrl: item.botInfo.avatarUrl || '10'
              }"
              size="small"
            >
              <template #default>
                <div class="user">@{{ item.botInfo.creator }}</div>
                <p>
                  {{ t('probot.userManager.lastUpdate') }}:{{
                    item.botInfo.updateTime
                      ? moment(item.botInfo.updateTime).format('YYYY-MM-DD')
                      : '----'
                  }}
                </p>
              </template>
            </BaseInfo>
          </div>
          <div class="foot" v-if="route.name != 'AI Probot My Collect'">
              <el-button link size="small" type="primary" @click.stop="handleExport(item)">
                导出
              </el-button>
              <el-button link size="small" type="primary" @click.stop="handleJump(item)">
                编辑
              </el-button>
              <el-button
                link
                size="small"
                type="primary"
                @click.stop="handleCopy(item)"
                :disabled="copyDisabled"
              >
                复制
              </el-button>
              <el-button
                link
                size="small"
                type="primary"
                @click.stop="handleDelete(item)"
                :disabled="removeDisabled"
              >
                删除
              </el-button>
              <el-button link size="small" type="primary" @click.stop="handleApi(item)">
                API
              </el-button>
              <el-button link size="small" type="primary" @click.stop="handleLog(item)">
                日志
              </el-button>
              <el-button
                link
                size="small"
                type="primary"
                @click.stop="handleDeleteCollect(item)"
                :disabled="removeDisabled"
                v-if="route.name == 'AI Probot My Collect'"
              >
                取消收藏
              </el-button>
          </div>
        </li>
      </ul>
    </template>
    <el-empty v-else />
    <div class="pager" v-if="state.total">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="state.total"
        :page-size="state.search.pageSize"
        :v-model:current-page="state.search.pageNum"
        hide-on-single-page
        @change="handleChangePage"
      />
    </div>
    <ImportDialog v-model="importDialogVisible" @callback="importDialogCallback"></ImportDialog>
    <CreateProbot
      v-model="showProbotCreate"
      @onOk="getList"
      :probotCreateType="probotCreateType"
    ></CreateProbot>
  </div>
</template>

<script lang="ts" setup>
import { onBeforeMount, reactive, watch, ref } from 'vue'
import { getProbotList, deleteBot, getUserWorkspaceRole } from '@/api/probot'
import { deleteCollect } from '@/api/probot-visit'
import BaseInfo from '@/components/BaseInfo.vue'
import moment from 'moment'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { t } from '@/locales'
import { useProbotStore } from '@/stores/probot'
import useClipboard from 'vue-clipboard3'
import ImportDialog from '../components/ImportDialog.vue'
import mitt from '@/utils/bus'
import CreateProbot from '@/views/probot-create/CreateProbot.vue'
import { createBot } from '@/api/probot'

const { toClipboard } = useClipboard()
const probotStore = useProbotStore()

const router = useRouter()
const route = useRoute()
const copyDisabled = ref(false)
const removeDisabled = ref(false)
const importDialogVisible = ref(false)
const showProbotCreate = ref(false)
const probotCreateType = ref('')

const state = reactive({
  tableList: [],
  options: [],
  search: {
    status: '',
    category: '',
    name: '',
    pageSize: 12,
    pageNum: 1
  },
  total: 0,
  loading: true
})
mitt.on('updateProbotDataList', async () => {
  getList()
})

const getList = (page = 1) => {
  state.loading = true
  state.search.pageNum = page
  getProbotList({
    ...state.search,
    isMyCollect: route.name == 'AI Probot My Collect' ? true : false,
    workspaceId: route.name == 'AI Probot Space' ? route?.params?.id : ''
  })
    .then(({ data }) => {
      if (data?.records?.length) {
        state.tableList = data?.records
        state.total = data?.totalRow
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

const handleImport = async () => {
  // 请求剪贴板权限
  const permission = await navigator.permissions.query({ name: 'clipboard-read' })
  if (permission.state === 'granted' || permission.state === 'prompt') {
    // 读取剪贴板内容
    const text = await navigator.clipboard.readText()
    //导入
    try {
      if (text) {
        toImport(text)
      } else {
        importDialogVisible.value = true
      }
    } catch (error) {
      importDialogVisible.value = true
    }
  }
}

const importDialogCallback = (str: string) => {
  handleExport(str).then(() => {
    toImport(str)
  })
}

const toImport = (text) => {
  const params = JSON.parse(text)
  params.botInfo.name = params.botInfo.name + '_copy'
  params.botInfo.workspaceId = route?.params?.id
  delete params.botInfo.id
  delete params.botSetting.id
  delete params.botSetting.botId
  createBot(params).then((res) => {
    if (res.data) {
      ElMessage.success(t('common.importSuccess'))
    }
    getList()
  })
}

const handleVisit = (item: any) => {
  router.push({
    path: '/probot-visit/' + item.botId
  })
}
const handleJump = (item: any) => {
  router.push({
    path: '/probot-edit/' + item.botId
  })
}

const handleExport = (item: any) => {
  return toClipboard(JSON.stringify(item))
    .then(() => {
      ElMessage.success(t('common.copySuccess'))
    })
    .catch(() => {
      ElMessage.error(t('common.copyError'))
    })
}

const handleCopy = (item: any) => {
  router.push({
    path: '/probot-edit/' + item.botId,
    query: {
      copy: 1
    }
  })
}

const handleCreate = () => {
  showProbotCreate.value = true
  probotCreateType.value = ''
}

const handleDelete = (item: any) => {
  ElMessageBox.confirm(
    t('probot.userManager.deleteBot', { name: item.botInfo.name }),
    t('probot.userManager.warning'),
    {
      confirmButtonText: t('common.confirm'),
      cancelButtonText: t('common.cancle'),
      type: 'warning'
    }
  )
    .then((action) => {
      if (action === 'confirm') {
        deleteBot({ botId: item.botId })
          .then((res) => {
            if (res.data) {
              ElMessage.success(t('common.deleteSuccess'))
              getList()
            } else {
              ElMessage.error(res.message)
            }
          })
          .catch((e) => {
            console.log(e)
          })
      }
    })
    .catch(() => {})
}
const handleLog=(item)=>{
  const { href } = router.resolve({
    path: '/probot-log',
    query: {
      workspaceId: route?.params?.id,
      botId: item.botId
    }
  })
  window.open(href, '_blank')
}
const handleApi = (item) => {
  const { href } = router.resolve({
    path: '/probot-api',
    query: {
      workspaceId: route?.params?.id,
      botId: item.botId
    }
  })
  window.open(href, '_blank')
}
const handleDeleteCollect = (item) => {
  deleteCollect({
    type: '0',
    collectId: item.botId
  }).then((res) => {
    if (res.code === 0) {
      getList()
    } else {
      ElMessage.error(res.message)
    }
  })
}

const handleChangePage = (page: number) => {
  getList(page)
}
onBeforeMount(() => {
  getList()
  getRole()
})
const getRole = () => {
  if (route?.params?.id) {
    getUserWorkspaceRole({
      workspaceId: route?.params?.id
    }).then((res) => {
      // roleCode -1 就是查无此人，0是普通用户 1是管理员 2是owner
      // 删除的权限至少管理员，复制的至少用户～
      copyDisabled.value = res.data?.roleCode >= 0 ? false : true
      removeDisabled.value = res.data?.roleCode >= 1 ? false : true
    })
  }
}

watch(
  () => route.params.id,
  () => {
    if (route.name === 'AI Probot Space' || route.name == 'AI Probot My Collect') {
      getList()
      getRole()
    }
  }
)
</script>

<style lang="scss" scoped>
.probots-wrap {
  .probots-filter {
    display: flex;
    justify-content: space-between;
  }

  .probots-form {
    display: flex;
  }
  ul.space-list {
    padding-top: 10px;
    width: 100%;
    display: flex;
    flex-wrap: wrap;
    justify-content: flex-start;
    li {
      cursor: pointer;
      width: 24%;
      margin-bottom: 20px;
      margin-right: 1.33333%;
      padding: 20px 20px;
      box-shadow:
        (0 0 #0000, 0 0 #0000),
        (0 0 #0000, 0 0 #0000),
        0 10px 15px -3px rgba(0, 0, 0, 0.1),
        0 4px 6px -4px rgba(0, 0, 0, 0.1);
      background-color: hsl(0 0% 100%);
      color: hsl(224 71.4% 4.1%);
      border: 1px solid rgb(229, 231, 235);
      border-radius: 10px;
      &:nth-child(4n) {
        margin-right: 0;
      }
      .head {
        padding-bottom: 10px;
      }
      .foot {
        display: flex;
        padding: 10px 0px 0;
        align-items: center;
        border-top: 1px solid #eee;
      }

      .user {
        font-size: 14px;
        color: #666;
        padding-bottom: 8px;
      }
      .foot {
        font-size: 13px;
        color: #666;
        .oz-button + .oz-button {
          margin-left: 2px;
        }
      }
    }
  }
  .pager {
    display: flex;
    align-items: center;
    justify-content: flex-end;
  }
}
</style>
