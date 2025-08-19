<!--
 * @Description: 
 * @Date: 2024-08-09 17:12:33
 * @LastEditTime: 2024-08-22 16:29:13
-->
<template>
  <BaseFold class="probot-layout">
    <template #left>
      <div class="probot-layout-left">
        <!-- btn -->
        <div class="btn-container">
          <div class="btn-item">
            <el-button type="primary" @click="clickBot"
              ><i class="iconfont icon-plus1"></i>创建Probot</el-button
            >
          </div>
          <div class="btn-item">
            <el-button type="primary" @click="clickFlow"
              ><i class="iconfont icon-plus1"></i>创建工作流</el-button
            >
          </div>
        </div>
        <!-- list -->
        <div class="left-list-container">
          <div :class="['left-list-item', active == 'index' ? 'active' : '']">
            <el-button type="info" text @click="clickIndex"
              ><i class="iconfont icon-wangzhan1"></i> 首页
            </el-button>
          </div>
          <div :class="['left-list-item', active == workspaceList[0]?.id ? 'active' : '']">
            <el-button type="info" text @click="clickMy"
              ><i class="iconfont icon-tuanduikongjian"></i> 我的空间
            </el-button>
          </div>
          <div :class="['left-list-item', active == 'collect' ? 'active' : '']">
            <el-button type="info" text @click="clickCollect"
              ><i class="iconfont icon-star-fill"></i> 我的收藏
            </el-button>
          </div>
        </div>
        <!-- menu -->
        <div class="menu-container">
          <div class="menu-title">广场</div>
          <div :class="['menu-item', active == 'botList' ? 'active' : '']">
            <el-button type="info" text @click="clickBotList"
              ><i class="iconfont icon-APP-robot1"></i> Probot广场
            </el-button>
          </div>
          <div :class="['menu-item', active == 'pluginList' ? 'active' : '']">
            <el-button type="info" text @click="clickPluginList"
              ><i class="iconfont icon-chajianku-chajianku"></i> 插件广场
            </el-button>
          </div>
        </div>
        <!-- team -->
        <div class="team-container">
          <div class="team-title">团队空间</div>
          <ul v-if="workspaceList?.length" class="info-list">
            <li
              v-for="(item, index) in workspaceList"
              :key="index"
              @click="spaceClick(item)"
              :class="[active == item.id ? 'active' : '']"
            >
              <BaseInfo :data="item" size="mini"></BaseInfo>
            </li>
          </ul>
        </div>
      </div>
    </template>
    <template #main> <RouterView></RouterView></template>
  </BaseFold>
  <CreateFlow v-model="showCreate" @createSuc="getList" />
  <CreateProbot v-model="showProbotCreate" @onOk="getProbotList"></CreateProbot>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useProbotStore } from '@/stores/probot'
import { useRoute, useRouter } from 'vue-router'
import BaseInfo from '@/components/BaseInfo.vue'
import { ElMessage } from 'element-plus'
import CreateFlow from '@/views/workflow-list/CreateFlow.vue'
import CreateProbot from '@/views/probot-create/CreateProbot.vue'
import mitt from '@/utils/bus'
import BaseFold from '@/components/probot/BaseFold.vue'

const probotStore = useProbotStore()
const router = useRouter()
const route = useRoute()

const workspaceList = computed(() => probotStore.workspaceList)
const showCreate = ref(false)
const showProbotCreate = ref(false)
const active = ref('')

const clickBot = () => {
  if (workspaceList.value[0]?.id) {
    showProbotCreate.value = true
  } else {
    ElMessage.error('您还没有空间，请在团队空间下创建空间吧～')
  }
}
const clickFlow = () => {
  showCreate.value = !showCreate.value
}
const getList = () => {
  mitt.emit('updateFlowDataList')
}
const getProbotList = () => {
  mitt.emit('updateProbotDataList')
}
const clickIndex = () => {
  active.value = 'index'
  router.push({
    path: '/'
  })
}
const clickMy = () => {
  active.value = workspaceList.value[0].id
  router.push({
    path: '/probot-space/' + workspaceList.value[0].id
  })
}
const clickCollect=()=>{
  active.value = 'collect'
  router.push({
    path: '/probot-my-collect'
  })
}
const clickBotList = () => {
  active.value = 'botList'
  router.push({
    path: '/probot-list'
  })
}
const clickPluginList = () => {
  active.value = 'pluginList'
  router.push({
    path: '/probot-plugin-list'
  })
}
const spaceClick = (item: { id: number }) => {
  active.value = '' + item.id
  router.push({
    path: '/probot-space/' + item.id
  })
}
//监听路由
watch(
  () => route,
  (val) => {
    if (val.params.id) {
      active.value = '' + val.params.id
    } else if (val.path == '/probot-plugin-list') {
      active.value = 'pluginList'
    }else if (val.path == '/probot-my-collect') {
      active.value = 'collect'
    } else if (val.path == '/probot-list') {
      active.value = 'botList'
    } else if (val.path == '/') {
      active.value = 'index'
    } else {
      active.value = ''
    }
  },
  {
    immediate: true,
    deep: true
  }
)
</script>

<style scoped lang="scss">
.probot-layout {
  .probot-layout-left {
    .iconfont {
      margin-right: 10px;
    }
    :deep(.oz-button) {
      width: 100%;
    }
    .active:deep(.oz-button) {
      color: var(--oz-menu-active-color) !important;
      background-color: #eee;
    }
  }
}
.btn-container {
  margin: 0 10px;
  .btn-item {
    margin-bottom: 10px;
  }
}
.left-list-container {
  margin-top: 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ddd;
  .left-list-item {
    :deep(.oz-button.is-text) {
      color: #666;
      justify-content: start;
    }
  }
}
.menu-container {
  margin-top: 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ddd;
  .menu-title {
    font-size: 14px;
    color: #666;
    padding: 10px 4px;
    opacity: 0.8;
    font-weight: bold;
  }
  .menu-item {
    :deep(.oz-button.is-text) {
      color: #666;
      justify-content: start;
    }
  }
}
.team-container {
  margin-top: 10px;
  flex: 1;
  overflow: auto;
  display: flex;
  flex-direction: column;
  .team-title {
    font-size: 14px;
    color: #666;
    padding: 10px 4px;
    opacity: 0.8;
    font-weight: bold;
  }
  .info-list {
    max-height: 250px;
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: auto;
    border-bottom: 1px solid #ddd;
    &::-webkit-scrollbar {
      display: none;
    }
  }
  li {
    margin-bottom: 5px;
    padding: 5px;
    color: hsl(224 71.4% 4.1%);
    display: flex;
    justify-content: space-between;
    cursor: pointer;
    border-radius: 5px;
    &:hover {
      background-color: #f5f7fa;
    }
    &.active {
      background-color: #eee;
      :deep(.name) {
        color: var(--oz-menu-active-color) !important;
      }
    }
  }
}
</style>
