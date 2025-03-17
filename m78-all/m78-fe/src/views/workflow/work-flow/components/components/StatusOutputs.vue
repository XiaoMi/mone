<template>
  <template v-if="showArr?.length > 0">
    <el-collapse v-model="activeNames">
      <el-collapse-item name="1">
        <template #title>
          输出
          <CopyBtn :arr="showArr" />
        </template>
        <div class="outputs">
          <template v-for="item in showArr" :key="item.name">
            <OutputItem :resItem="item" :nodeData="nodeData" />
          </template>
        </div>
      </el-collapse-item>
    </el-collapse>
  </template>
  <div v-if="nodeData.coreSetting?.type == 'content'">
    <h6 class="item-t">
      回答内容
      <el-button type="primary" :icon="DocumentCopy" link @click.stop="copyRes"></el-button>
    </h6>
    <div class="outputs">
      <p class="text-p">{{ resOutputs.answerContent }}</p>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch, nextTick } from 'vue'
import { DocumentCopy } from '@element-plus/icons-vue'
import useClipboard from 'vue-clipboard3'
import CopyBtn from './CopyBtn'
import ResItemVal from './ResItemVal'
import { ElMessage } from 'element-plus'
import EditOutBtn from './EditOutBtn.vue'
import OutputItem from './OutputItem.vue'

const strArr = ref(['string', 'String'])
const activeNames = ref(['1'])
const props = defineProps({
  nodeData: {
    type: Object,
    default: () => {}
  },
  resOutputs: {
    type: Object,
    default: () => {}
  }
})
const hideItems = ['$$TY_SUB_FLOW_RECORD_ID$$', '$$TY_CODE_LOG$$']
const transResArr = computed(() => {
  const res = props.resOutputs?.outputDetails || []
  // 将flowRecordId过滤掉，不展示
  const firlterRes = res.filter((it) => !hideItems.includes(it.name))
  let resArr = []
  resArr = firlterRes.map((item) => {
    let val
    try {
      val = JSON.parse(item.value)
    } catch (error) {
      val = item.value
    }
    return {
      ...item,
      value: val
    }
  })

  return resArr
})

const showArr = ref([])
const insert = (step, startI = 0, res) => {
  // const end = startI + step < res.length ? startI + step : res.length
  // const toInsert = res.slice(startI, end)
  // // 使用迭代代替递归
  // const insertFn = (start, toInsert) => {
  //   // 已经循环结束了
  //   if (start >= toInsert.length) return
  //   const newStep = Math.min(step, toInsert.length - start)
  //   window.requestAnimationFrame(() => {
  //     showArr.value.push(...toInsert.slice(start, start + step))
  //     insertFn(start + newStep, toInsert)
  //   })
  // }
  // insertFn(0, toInsert)
  showArr.value = res
}

// 如果变化了
watch(
  () => transResArr.value,
  (val) => {
    if (val) {
      showArr.value = []
      nextTick(() => {
        insert(5, 0, val || [])
      })
    }
  },
  {
    deep: true,
    immediate: true
  }
)
const { toClipboard } = useClipboard()

const copyRes = async () => {
  try {
    await toClipboard(props.resOutputs.answerContent)
    ElMessage.success('复制成功')
  } catch (e) {
    ElMessage.warning('您的浏览器不支持复制：', e)
  }
}
</script>

<style lang="scss" scoped>
.p-l-15px {
  padding-left: 15px;
}
</style>
