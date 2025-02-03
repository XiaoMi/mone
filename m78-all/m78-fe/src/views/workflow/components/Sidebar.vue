<template>
  <div class="h-full sidebar">
    <el-tabs v-model="activeName">
      <el-tab-pane label="基础节点" name="node">
        <div class="nodes h-full">
          <NodeTem
            v-for="item in keysArr"
            :key="item"
            :title="nodeBaseRef[item].title"
            :img="nodeBaseRef[item].imgSrc"
            :desc="nodeBaseRef[item].desc"
            @onDragStart="
              (e) => {
                onDragStartFn(e, item, null)
              }
            "
            @addClick="addClickFn(item)"
            :showDetail="props.showDetail"
            :draggable="draggable"
          />
        </div>
      </el-tab-pane>

      <el-tab-pane label="插件" name="plugin">
        <div class="h-full plugin-box">
          <el-input
            v-model="reqParams.name"
            placeholder="请输入插件名"
            class="input-with-select"
            @keyup.enter="handleEnter"
          >
            <template #append>
              <el-button :icon="Search" @click="handleEnter" class="search-btn" />
            </template>
          </el-input>
          <el-empty description="暂无插件" v-if="plugins.length == 0" />
          <div
            v-if="activeName == 'plugin'"
            class="plugins"
            v-infinite-scroll="load"
            v-loading="loading"
          >
            <PluginItem
              v-for="plugin of plugins"
              :key="plugin.id"
              :id="plugin.id"
              :name="plugin.name"
              :desc="plugin.desc"
              @onDragStart="
                (e) => {
                  onDragStartFn(e, 'plugin', plugin)
                }
              "
              @addClick="addPluginFlow(plugin)"
              :showDetail="props.showDetail"
              :draggable="draggable"
            />
          </div>
        </div>
      </el-tab-pane>
      <el-tab-pane label="flow" name="flow">
        <div class="h-full plugin-box">
          <FlowList
            :activeName="activeName"
            :draggable="draggable"
            @onDragStart="
              (val) => {
                emits('onDragStart', val)
              }
            "
            @addClick="addPluginFlow"
          />
        </div>
      </el-tab-pane>
      <el-tab-pane label="多模态" name="model">
        <div class="nodes h-full">
          <!-- <div v-for="item in modelsBase" :key="item">{{ item }}</div> -->
          <NodeTem
            v-for="item in modelsArr"
            :key="item"
            :title="nodeBaseRef[item].title"
            :img="nodeBaseRef[item].imgSrc"
            :desc="nodeBaseRef[item].desc"
            @onDragStart="
              (e) => {
                onDragStartFn(e, item, null)
              }
            "
            @addClick="addClickFn(item)"
            :showDetail="props.showDetail"
            :draggable="draggable"
          />
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script setup lang="ts">
import { ref, computed, shallowReactive, defineProps } from 'vue'
import NodeTem from './NodeTem.vue'
import PluginItem from './PluginItem.vue'
import { nodesBase, showSidebar, modelsBase } from '../work-flow/baseInfo.js'
import { getPluginsSearch } from '@/api/workflow'
import { Search } from '@element-plus/icons-vue'
import FlowList from './components/FlowList.vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const nodeBaseRef = ref(nodesBase)
const activeName = ref('node')
const plugins = ref<
  {
    id: string
    name: string
    desc: string
    userName?: string
    apiUrl?: string
    featureRouterId?: string
    meta?: any
  }[]
>([])
const props = defineProps({
  showDetail: {
    default: true
  },
  draggable: {
    default: true,
    type: Boolean
  }
})

const emits = defineEmits(['onDragStart', 'addClick', 'addPluginFlow'])
const onDragStartFn = (e, type, info) => {
  emits('onDragStart', { e, type, info })
}

const addClickFn = (type) => {
  emits('addClick', type)
}

const addPluginFlow = (pluginFlow) => {
  emits('addPluginFlow', pluginFlow)
}

const keysArr = computed(() => {
  return showSidebar
})

const modelsArr = computed(() => {
  const res = Object.keys(nodesBase).filter((item) => {
    return nodesBase[item]?.isModel
  })
  return res
})

const noMore = ref(true)
const reqParams = ref({
  pageNum: 1,
  pageSize: 20,
  name: ''
})

const handleEnter = (val) => {
  reqParams.value.pageNum = 1
  plugins.value = []
  getPlugins()
}

const load = () => {
  if (noMore.value) return
  const { pageNum } = reqParams.value
  reqParams.value.pageNum = pageNum + 1
  getPlugins()
}

const loading = ref(true)
const getPlugins = async () => {
  loading.value = true
  const { code, data } = await getPluginsSearch(reqParams.value)
  if (code === 0 && Array.isArray(data?.records)) {
    const newVal = data.records.map((it) => {
      return {
        id: `${it.id}`,
        // 原始类型
        originalType: it.type,
        type: 'plugin',
        name: it.name,
        desc: it.desc || '',
        apiUrl: it.apiUrl || '',
        meta: JSON.parse(it.meta || '{}'),
        botPluginMeta: it.botPluginMeta
      }
    })
    noMore.value = newVal.length < reqParams.value.pageSize ? true : false
    plugins.value = [...plugins.value, ...newVal]
  } else {
    plugins.value = []
  }
  loading.value = false
}
getPlugins()
</script>
<style lang="scss" scoped>
.sidebar {
  width: 329px;
  // padding: 10px 16px;
  border-right: solid 1px #e6e6e9;
  background: #f7f7fa;
  overflow: hidden;
  :deep(.oz-tabs) {
    height: 100%;
    .oz-tabs__header {
      padding: 10px 16px 0 16px;
      margin: 0;
    }
    .oz-tabs__content {
      height: calc(100% - 50px);
      .oz-tab-pane {
        height: 100%;
      }
    }
  }

  .nodes {
    width: 100%;
    overflow-y: auto;
    padding: 10px 16px 0 16px;
  }
  .plugin-box {
    .input-with-select {
      padding: 15px 16px 10px 16px;
    }
    .plugins {
      width: 100%;
      height: calc(100% - 57px);
      overflow-y: auto;
      padding: 0 16px;
    }
  }
  .node-item {
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 6px 8px 0 rgba(29, 28, 35, 0.06);
    cursor: -webkit-grab;
    cursor: grab;
    font-size: 12px;
    line-height: 16px;
    overflow: hidden;
    padding: 16px;
    margin-top: 10px;
  }
  .top-p {
    display: flex;
    justify-content: space-between;
    align-items: center;
    .title {
      font-size: 14px;
      color: #1c1f23;
      font-weight: 500;
      vertical-align: top;
      margin-left: 4px;
    }
    .header {
      width: 16px;
      height: 16px;
    }
  }

  .desc {
    color: rgba(28, 31, 35, 0.6);
    text-align: left;
    margin-top: 5px;
  }
}
</style>
