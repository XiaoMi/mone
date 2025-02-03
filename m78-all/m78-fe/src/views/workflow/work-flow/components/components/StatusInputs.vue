<template>
  <!-- <template v-if="conditionTypesArr.includes(nodeData.nodeType)">
    {{ resInputs?.inputDetails }}
    <div v-for="(item, i) in resInputs?.inputDetails" :key="item.name" class="condition-item">
      <h5 class="t">条件 {{ i + 1 }}</h5>
      <div class="condition-show">
        <el-tag type="warning" class="input-tag tag-bian">
          <i class="name-i">{{ item.name }} : </i>
          {{ item.value }}
        </el-tag>
        <el-tag type="warning" class="input-tag input-center">{{ retTExt(item.operator) }}</el-tag>
        <el-tag type="warning" class="input-tag tag-bian">
          <template v-if="item.type2 == 'reference'"
            ><i class="name-i">{{ item.name2 }} : </i>{{ item.value2 }}</template
          >
          <template v-else> {{ item.value2 }}</template>
        </el-tag>
      </div>
    </div>
  </template>
  <template v-else> -->
  <template v-if="resInputs?.inputDetails?.length > 0">
    <el-collapse v-model="activeNames">
      <el-collapse-item name="1">
        <template #title>
          输入
          <CopyBtn :arr="resInputs?.inputDetails" />
        </template>
        <div class="outputs" v-for="item in showArr" :key="item.name">
          <template v-if="typeof item.value == 'object'">
            <div>
              <span class="name-i">{{ item.name }}：</span>
              <p class="text-p p-l-15px" v-for="(inner, index) in item.value" :key="index">
                <span class="name-i">{{ index }}：</span>
                <ResItemVal :item="inner" />
              </p>
            </div>
          </template>
          <template v-else>
            <p class="text-p">
              <span class="name-i text-no-wrap">{{ mapNames[item.name] || item.name }}：</span>
              <ResItemVal :item="item.value" />
            </p>
          </template>
        </div>
      </el-collapse-item>
    </el-collapse>
  </template>
  <!-- </template> -->
</template>

<script setup>
import { ref, computed, watch, nextTick } from 'vue'
import { opList, specialNames } from '../../baseInfo'
import CopyBtn from './CopyBtn'
import ResItemVal from './ResItemVal'
import EditOutBtn from './EditOutBtn.vue'

const activeNames = ref(['1'])
const props = defineProps({
  resInputs: {},
  nodeData: {}
})
const mapNames = ref(specialNames)
const retTExt = (op) => {
  const opItem = opList.find((item) => item.value == op)
  return opItem ? opItem.label : op
}
const transResArr = computed(() => {
  const res = props.resInputs?.inputDetails || []
  let resArr = []
  resArr = res.map((item) => {
    let val
    try {
      val = item.name == '$$TY_PLUGIN_DUBBO_PARAMS$$' ? `${item.value}` : JSON.parse(item.value)
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
const step = 5
const showArr = ref([])
const insert = (step, startI = 0, res) => {
  const end = startI + step < res.length ? startI + step : res.length
  const toInsert = res.slice(startI, end)
  // 使用迭代代替递归
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
watch(
  () => transResArr.value,
  (val, oldV) => {
    showArr.value = []
    nextTick(() => {
      insert(step, 0, val || [])
    })
  },
  {
    deep: true,
    immediate: true
  }
)
</script>

<style lang="scss" scoped>
.p-l-15px {
  padding-left: 15px;
}
.condition-item {
  margin-bottom: 10px;
}
.text-no-wrap {
  text-wrap: no-wrap;
}
</style>
