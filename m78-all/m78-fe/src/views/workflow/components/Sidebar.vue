<template>
  <div class="h-full overflow-auto sidebar">
    <el-tabs v-model="activeName">
      <el-tab-pane label="基础节点" name="node">
        <div class="nodes">
          <NodeTem
            v-for="item in keysArr"
            :key="item"
            :title="nodeBaseRef[item].title"
            :img="nodeBaseRef[item].imgSrc"
            @onDragStart="
              (e) => {
                onDragStartFn(e, item, null)
              }
            "
            @addClick="addClickFn(item)"
          />
        </div>
      </el-tab-pane>
      <el-tab-pane label="插件" name="plugin">
        <div>
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
            @addClick="addPlugin(plugin)"
          />
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script setup lang="ts">
import { ref, computed, shallowReactive } from 'vue'
import NodeTem from './NodeTem.vue'
import PluginItem from './PluginItem.vue'
import { nodesBase } from '../work-flow/baseInfo.js'
import { getPluginList } from '@/api/workflow'

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
const emits = defineEmits(['onDragStart', 'addClick'])
const onDragStartFn = (e, type, info) => {
  emits('onDragStart', { e, type, info })
}
const addClickFn = (type) => {
  emits('addClick', type)
}

const addPlugin = (plugin) => {
  emits('addPlugin', plugin)
}
// 在sidebar 不展示的nodeType类型
const notShow = ['begin', 'plugin', 'end', 'nodeif', 'nodeelse']

const keysArr = computed(() => Object.keys(nodesBase).filter((item) => notShow.indexOf(item) < 0))

const getPlugins = async () => {
  const { code, data } = await getPluginList()
  if (code === 0 && Array.isArray(data?.records)) {
    plugins.value = data.records.map((it) => {
      return {
        id: `${it.id}`,
        type: 'plugin',
        name: it.name,
        desc: it.desc || '',
        apiUrl: it.apiUrl || '',
        meta: JSON.parse(it.meta || '{}'),
        botPluginMeta: it.botPluginMeta
      }
    })
  } else {
    plugins.value = []
  }
}

getPlugins()
</script>
<style lang="scss" scoped>
.sidebar {
  width: 329px;
  padding: 10px 16px;
  border-right: solid 1px #e6e6e9;
  background: #f7f7fa;
  .nodes {
    width: 100%;
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
    margin-bottom: 10px;
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
