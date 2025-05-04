<template>
  <div class="flows-box">
    <el-input
      v-model="reqParams.name"
      placeholder="请输入flow名称"
      class="input-with-select"
      @keyup.enter="handleEnter"
    >
      <template #append>
        <el-button :icon="Search" @click="handleEnter" class="search-btn" />
      </template>
    </el-input>
    <el-empty description="暂无flow" v-if="flows.length == 0" />
    <div v-if="activeName == 'flow'" class="flows" v-infinite-scroll="load" v-loading="loading">
      <NodeFlowTem
        v-for="item in flows"
        :key="item.id"
        :desc="item.desc"
        @onDragStart="
          (e) => {
            onDragStartFn(e, 'subFlow', item)
          }
        "
        @addClick="addClickFn(item)"
        :showDetail="props.showDetail"
        :draggable="draggable"
      >
        <BaseInfo :data="item?.flowBaseInfo" size="small" />
      </NodeFlowTem>
    </div>
  </div>
</template>

<script setup lang="ts">
import { getPluginsSearch } from '@/api/workflow'
import { Search } from '@element-plus/icons-vue'
import NodeFlowTem from '../NodeFlowTem.vue'
import { ref, defineEmits, computed } from 'vue'
import { getFlowList } from '@/api/workflow'
import BaseInfo from '@/components/BaseInfo.vue'
import { useRoute } from 'vue-router'

const emits = defineEmits(['onDragStartFn', 'addClick'])
const flows = ref([])
const noMore = ref(false)
const loading = ref(false)
const reqParams = ref({
  pageNum: 1,
  pageSize: 20,
  name: ''
})
const props = defineProps({
  activeName: {},
  draggable: {
    default: true
  }
})
const onDragStartFn = (e, type, info) => {
  emits('onDragStart', { e, type, info })
}
const handleEnter = (val) => {
  reqParams.value.pageNum = 1
  flows.value = []
  getList()
}
const route = useRoute()
const flowId = computed(() => route.params.id)
const getList = async () => {
  loading.value = true
  const { code, data } = await getFlowList(reqParams.value)
  if (code === 0 && Array.isArray(data?.records)) {
    // 将自己过滤掉
    const filterArr = data.records.filter((it) => `${it.flowBaseInfo.id}` != flowId.value)
    const newVal = filterArr.map((it) => {
      return {
        ...it,
        ...it.flowBaseInfo,
        type: 'subFlow'
      }
    })
    noMore.value = data.records.length < reqParams.value.pageSize ? true : false
    flows.value = [...flows.value, ...newVal]
  } else {
    flows.value = []
  }
  loading.value = false
}
const load = () => {
  if (noMore.value) return
  const { pageNum } = reqParams.value
  reqParams.value.pageNum = pageNum + 1
  getList()
}

getList()

const addClickFn = (flow) => {
  emits('addClick', flow)
}
</script>

<style lang="scss" scoped>
.flows-box {
  height: 100%;
  .input-with-select {
    padding: 15px 16px 10px 16px;
  }
  .flows {
    width: 100%;
    height: calc(100% - 57px);
    overflow-y: auto;
    padding: 0 16px;
  }
  :deep(.base-info .describe) {
    width: 160px;
  }
}
</style>
