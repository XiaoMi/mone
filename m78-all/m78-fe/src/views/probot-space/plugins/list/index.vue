<template>
    <div class="plugins-info">
      <div
        class="plugins-info-name status"
        :class="{ online: state.pluginDetail.status === E_PLUGIN.getEnum('PUB', 'key').value }"
      >
        <BaseInfo
          :data="{
            name: state.pluginDetail.pluginOrgName,
            describe: state.pluginDetail.pluginOrgDesc,
            avatarUrl: state.pluginDetail.avatarUrl
          }"
          size="small"
        />
      </div>
      <div class="plugins-info-name">
        <span>{{ t('plugin.workspace') }}：</span>
        <span class="space-name" @click="handleJump">{{
          state.spaceObj?.name || t('plugin.mySpace')
        }}</span>
      </div>
      <div class="plugins-info-btns">
        <el-button size="small" @click="handleEdit">{{ t('common.edit') }}</el-button>
        <el-button size="small" @click.stop="state.visible = true">{{
          t('plugin.addComponentBtn')
        }}</el-button>
        <el-button size="small" @click="handleOnline">{{
          state.pluginDetail.status === E_PLUGIN.getEnum('PUB', 'key').value
            ? t('plugin.takeOffline')
            : t('plugin.publish')
        }}</el-button>
      </div>
    </div>
    <div class="plugins">
      <template v-if="state.pluginDetail?.id">
        <TableList :tableList="state.pluginDetail.plugins" @onOk="getPlugin(route.params.plugin)" />
      </template>
      <el-empty v-else />
    </div>
    <EditComp
      v-model="state.visible"
      :orgId="state.pluginDetail.id"
      @onOk="getPlugin(route.params.plugin)"
    />
    <EditPluginGroup
      v-model="state.editVisible"
      :row="state.eidtRow"
      @onOk="getPlugin(route.params.plugin)"
    />
    <Grounding
      v-model="state.groundingVisible"
      :data="state.groundingData"
      @onOk="handleOnlineFn"
    />
</template>

<script lang="ts" setup>
import TableList from './TableList.vue'
import { useRoute, useRouter } from 'vue-router'
import EditComp from './EditComp.vue'
import { reactive, watch, onMounted } from 'vue'
import { getById, publishOrCancel, type IPluginItem } from '@/api/plugins'
import { ElMessage } from 'element-plus'
import { t } from '@/locales'
import EditPluginGroup from '../EditPluginGroup.vue'
import Grounding from '../Grounding.vue'
import { E_PLUGIN } from '../constants'
import { getWorkspaceList } from '@/api/probot'
import BaseInfo from '@/components/BaseInfo.vue'

const route = useRoute()
const router = useRouter()
const state = reactive<{
  visible: boolean
  editVisible: boolean
  eidtRow: object
  spaceObj: object
  pluginDetail: Partial<IPluginItem>
  groundingVisible: boolean
  groundingData: object
}>({
  editVisible: false,
  visible: false,
  pluginDetail: {},
  eidtRow: {},
  spaceObj: {},
  groundingVisible: false,
  groundingData: {}
})

const handleOnline = () => {
  let publish = false
  if (state.pluginDetail.status !== E_PLUGIN.getEnum('PUB', 'key').value) {
    publish = true
  }
  if (publish) {
    state.groundingVisible = true
    state.groundingData = state.pluginDetail
  } else {
    handleOnlineFn({
      id: state.pluginDetail.id,
      publish
    })
  }
}
const handleOnlineFn = (params: { id?: number; publish: boolean; pluginCategory?: [] }) => {
  publishOrCancel(params).then((data) => {
    if (data.data) {
      ElMessage.success(
        params.publish ? t('plugin.successfullylistedSale') : t('plugin.successfullyTakenOffline')
      )
      getPlugin(route.params.plugin)
    } else {
      ElMessage.error(data.message!)
    }
  })
}
const handleEdit = () => {
  state.editVisible = true
  state.eidtRow = {
    id: state.pluginDetail.id,
    pluginOrgName: state.pluginDetail.pluginOrgName,
    pluginOrgDesc: state.pluginDetail.pluginOrgDesc,
    avatarUrl: state.pluginDetail.avatarUrl
  }
}

const handleJump = () => {
  router.push({
    path: `/probot-space/${state.spaceObj?.id || 0}`,
    query: {
      tab: 'plug'
    }
  })
}

const getPlugin = (id) => {
  getWorkspaceList()
    .then((data) => {
      if (data.data?.length) {
        state.spaceObj = data.data.filter((v) => v.id == route.params.space)[0]
      } else {
        state.spaceObj = {}
      }
    })
    .catch((e) => {
      console.log(e)
    })
  getById({ id })
    .then((data) => {
      if (data.data) {
        state.pluginDetail = data.data
      } else {
        ElMessage.error(data.message!)
      }
    })
    .catch((e) => {
      console.log(e)
    })
}

onMounted(() => {
  if (route.params.plugin) {
    getPlugin(route.params.plugin)
  } else {
    ElMessage.error(t('common.wrong'))
  }
})
</script>

<style lang="scss" scoped>
.plugins-info {
  border-radius: 5px;
  margin: 10px 10px;
  padding: 20px 30px;
  background-color: rgba(255, 255, 255, 0.7);
  display: flex;
  align-items: center;
  justify-content: space-between;
  &-name {
    display: flex;
    align-items: center;
    justify-content: flex-start;
    font-size: 14px;
    flex: 1;
    overflow: hidden;
    position: relative;
    padding-left: 2px;
    .space-name {
      color: #00a9ff;
      cursor: pointer;
    }
    &.status {
      &::before {
        position: absolute;
        left: 0;
        top: -2px;
        content: '';
        width: 10px;
        height: 10px;
        border-radius: 50%;
        z-index: 1;
        border: 2px solid #fff;
        background-color: #c8c9cc;
      }
    }
    &.online {
      &::before {
        background-color: #67c23a;
      }
    }
    span {
      white-space: nowrap;
      &:last-child {
        display: inline-block;
        max-width: 100%;
        overflow: hidden;
        text-overflow: ellipsis;
      }
    }
  }
  &-btns {
    white-space: nowrap;
  }
}
.plugins {
  margin: 0 10px;
  padding: 20px 20px 20px;
  background-color: rgba(255, 255, 255, 0.7);
  border-radius: 5px;
}
</style>
