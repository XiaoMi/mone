<!--
 * @Description: 
 * @Date: 2024-03-06 15:55:12
 * @LastEditTime: 2024-09-24 18:53:11
-->
<template>
  <div class="head">
    <div class="left">
      <BaseInfo
        :data="{
          ...infoData,
          remark: ''
        }"
        size="small"
      >
        <template #top>
          <div class="item-dropdown">
            <el-dropdown @command="handleCommand" split-button type="primary">
              {{ spaceValue || '请选择空间' }}
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-for="item in spaceOptions" :key="item.id" :command="item">{{
                    item.name
                  }}</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </template>
      </BaseInfo>
    </div>
    <div class="right" v-if="infoData?.id != spaceOptions[0]?.id">
      <BaseLink
        name="成员管理"
        @click="userListVisible = true"
        icon="icon-chengyuanguanli"
      ></BaseLink>
      <BaseLink name="编辑" @click="createTeamDialogVisible = true" icon="icon-bianji"></BaseLink>
      <div class="item-btn">
        <el-dropdown>
          <BaseLink name="更多操作" icon="icon-gengduocaozuo"></BaseLink>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="transferDialogVisible = true">转让所有权</el-dropdown-item>
              <el-dropdown-item @click="handleDelete">删除空间</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </div>
  <UserList :visible="userListVisible" :teamInfo="infoData" @onCancel="userListVisible = false" />
  <ProbotTeamDialog v-model="createTeamDialogVisible" :teamInfo="infoData" @onOk="getList" />
  <TransferUser v-model="transferDialogVisible" :teamInfo="infoData" />
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import BaseInfo from '@/components/BaseInfo.vue'
import BaseLink from '@/components/probot/BaseLink.vue'
import UserList from './components/User/UserList.vue'
import ProbotTeamDialog from '@/components/probot/ProbotTeamDialog.vue'
import TransferUser from './components/User/TransferUser.vue'
import { getWorkspaceList } from '@/api/probot'
import { useProbotStore } from '@/stores/probot'
import { removeTeam } from '@/common/probot.ts'

const probotStore = useProbotStore()

const router = useRouter()
const route = useRoute()

const infoData = ref({})
const userListVisible = ref(false)
const createTeamDialogVisible = ref(false)
const transferDialogVisible = ref(false)
const spaceValue = ref('')
const spaceOptions = computed(() => probotStore.workspaceList)

const initInfo = () => {
  if (route.name === 'AI Probot Space') {
    let infoObj = {}
    if (route.params.id) {
      infoObj = spaceOptions.value.find((item) => item.id == route.params.id)
    } else {
      infoObj = spaceOptions.value[0]
    }
    infoData.value = {
      ...infoObj,
      custom: true
    }
    spaceValue.value = infoObj?.name
  }
}
const getList = () => {
  getWorkspaceList()
    .then((res) => {
      if (res?.data?.length) {
        probotStore.setWorkspaceList(res?.data)
        initInfo()
      } else {
        probotStore.setWorkspaceList([])
      }
    })
    .catch((e) => {
      console.log(e)
    })
}

const handleCommand = (command: string | number | object) => {
  spaceValue.value = command.name
  router.push({
    path: '/probot-space/' + command.id
  })
}

const handleDelete = () => {
  removeTeam(infoData.value?.id, () => {
    getList()
    router.push({
      path: '/probot-space/' + spaceOptions?.value[0]?.id
    })
  })
}

watch(
  () => [spaceOptions.value, route.path],
  () => {
    initInfo()
  },
  {
    immediate: true,
    deep: true
  }
)
</script>

<style scoped lang="scss">
.head {
  margin: 0px 10px;
  padding: 9px 20px 9px;
  background-color: rgba(255, 255, 255, 0.7);
  display: flex;
  justify-content: space-between;
  border-radius: 5px;
  .left {
    flex: 1;
    overflow: hidden;
    .item-dropdown {
      font-size: 14px;
      font-weight: bold;
      padding-top: 4px;
      line-height: 20px;
    }
  }
  .right {
    display: flex;
    align-items: center;
    width: 262px;
  }
}
</style>
