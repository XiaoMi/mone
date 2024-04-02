<template>
  <div v-if="route.query.id">
    <template v-if="state.plugin.id">
      <visit :detailData="state.plugin" />
      <div class="tab-container"><Tab :list="state.plugin.plugins || []" /></div>
    </template>
  </div>
  <el-empty v-else description="参数错误"></el-empty>
</template>
<script lang="ts" setup>
import { getById } from '@/api/plugins'
import { ElMessage } from 'element-plus'
import { onBeforeMount, reactive } from 'vue'
import { useRoute } from 'vue-router'
import visit from './visit/index.vue'
import Tab from './Tab.vue'

const route = useRoute()
const state = reactive({
  plugin: {}
})

const getDetail = () => {
  let id = route.query.id
  if (!id) {
    return
  }
  getById({
    id
  })
    .then((data) => {
      if (data.message === 'ok') {
        state.plugin = data.data
      } else {
        ElMessage.error(data.message!)
      }
    })
    .catch((e) => {
      console.log(e)
    })
}
onBeforeMount(() => {
  getDetail()
})
</script>

<style lang="scss" scoped>
.tab-container {
  margin: 30px 140px 0;
  height: calc(100vh - 117px);
  overflow: hidden;
}
</style>
