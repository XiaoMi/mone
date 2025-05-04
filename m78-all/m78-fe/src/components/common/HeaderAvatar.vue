<script setup lang="ts">
import { ref, computed, watch, onMounted, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { useSettingStore } from '@/stores/settings'
import type { SettingsState } from '@/stores/settings/helper'
import { UserFilled, WarningFilled } from '@element-plus/icons-vue'
import { t } from '@/locales'
import HeaderAvatarModel from './HeaderAvatarModel.vue'
import { useRoute, useRouter } from 'vue-router'
import { logout as doLogout } from '@/api'

const isPro = ref(true)
const route = useRoute()
const router = useRouter()

const settingStore = useSettingStore()
const userStore = useUserStore()

const avatar = computed(() => userStore.userInfo.avatar)
const name = computed(() => userStore.userInfo.name)
const zToken = computed(() => userStore.userInfo.ztoken)

const dialogVisible = ref<boolean>(false)
const temperature = ref(settingStore.temperature ?? 0.5)
const state = reactive({
  active: '/'
})

const handleCommand = (command: string | number | object) => {
  if (command === 'settings') {
    dialogVisible.value = true
  } else if (command === 'classification') {
    router.push({
      path: '/probot-classification'
    })
  } else if (command === 'logout') {
    logout()
  } else if (command === 'carousel') {
    router.push({
      path: '/probot-carousel'
    })
  } else if (command === 'platformStatistics') {
    router.push({
      path: '/probot-platformStatistics'
    })
  } else if (command === 'gitAccount') {
    router.push({
      path: '/git-account'
    })
  } else if (command === 'EmployeeManage') {
    router.push({
      path: '/employee-manage'
    })
  } else if (command == 'agent') {
    router.push({
      path: '/agent'
    })
  } else if (command == 'ide') {
    console.log('ide')
  }
}

const getCookie = (name: string) => {
  const strcookie = document.cookie //获取cookie字符串
  const arrcookie = strcookie.split('; ') //分割
  //遍历匹配
  for (let i = 0; i < arrcookie.length; i++) {
    var arr = arrcookie[i].split('=')
    if (arr[0] == name) {
      return arr[1]
    }
  }
  return ''
}

const logout = () => {
  doLogout(getCookie('TPC_TOKEN'))
}

// function updateZToken(token: string) {
//   userStore.setZToken(token)
//   ElMessage.success(t('zToken.success'))
//   fetchModels(token)
// }

function updateSettings(options: Partial<SettingsState>) {
  settingStore.updateSetting(options)
  ElMessage.success(t('common.success'))
}

onMounted(() => {
  state.active = route.path
})

//监听路由
watch(
  () => route,
  (val) => {
    state.active = val.path
  },
  {
    immediate: true,
    deep: true
  }
)
</script>

<template>
  <el-dropdown trigger="hover" @command="handleCommand">
    <el-avatar v-if="avatar" :size="46" :src="avatar" />
    <el-avatar v-else-if="name" :size="46">{{
      name.toLocaleUpperCase().substring(0, 1)
    }}</el-avatar>
    <el-avatar v-else :size="46" :icon="UserFilled" />
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item command="settings">{{ t('setting.setting') }}</el-dropdown-item>
        <el-dropdown-item command="classification">{{
          t('setting.classification')
        }}</el-dropdown-item>
        <el-dropdown-item command="carousel" v-if="userStore.userInfo.admin">{{
          t('setting.carousel')
        }}</el-dropdown-item>
        <el-dropdown-item command="platformStatistics" v-if="userStore.userInfo.admin && !isPro">{{
          t('setting.platformStatistics')
        }}</el-dropdown-item>
        <el-dropdown-item command="gitAccount">Gitlab账号</el-dropdown-item>
        <!-- <el-dropdown-item command="agent">AI Agent</el-dropdown-item> -->
        <!-- <el-dropdown-item command="ide">AI Ide</el-dropdown-item> -->
        <el-dropdown-item command="logout">登出</el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
  <el-dialog v-model="dialogVisible" :title="t('setting.setting')" width="80%">
    <el-form label-width="120px">
      <el-form-item label="z Token">
        <div class="el-form-item">
          <el-input disabled :model-value="zToken" />
          <!-- <el-icon><WarningFilled /></el-icon> -->
          <el-popover trigger="hover">
            <template #reference>
              <el-button :icon="WarningFilled" text></el-button>
            </template>
            <template #default>
              <span
                >去<el-link type="primary" href="https://z.ozx.yling.top/z/info" target="_blank"
                  >创建</el-link
                >z token</span
              >
            </template>
          </el-popover>
          <!-- <el-button text type="primary" @click="updateZToken(zToken)">{{
            t('common.save')
          }}</el-button> -->
        </div>
      </el-form-item>
      <el-form-item :label="t('setting.model')">
        <div class="el-form-item">
          <HeaderAvatarModel></HeaderAvatarModel>
        </div>
      </el-form-item>
      <el-form-item label="Temperature">
        <div class="el-form-item">
          <el-slider v-model="temperature" :min="0" :max="1" />
          <el-button text type="primary" @click="updateSettings({ temperature })">{{
            t('common.save')
          }}</el-button>
        </div>
      </el-form-item>
    </el-form>
  </el-dialog>
</template>

<style lang="scss" scoped>
.el-form-item {
  display: flex;
  width: 100%;
}
</style>
