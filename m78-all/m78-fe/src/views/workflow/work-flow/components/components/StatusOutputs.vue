<template>
  <template v-if="arr?.length > 0">
    <el-collapse v-model="activeNames">
      <el-collapse-item name="1">
        <template #title>
          输出
          <CopyBtn :arr="arr" />
        </template>
        <div class="outputs" v-for="item in arr" :key="item.name">
          <template v-if="typeof item.value == 'object'">
            <span class="name-i font-14">{{ item.name }}</span>
            <p class="text-p p-l-15" v-for="(itemV, index) in item.value" :key="index">
              <span class="name-i">{{ index }}：</span>
              <span class="out-text">
                <JsonViewer :value="itemV" theme="jv-light" />
              </span>
            </p>
          </template>
          <template v-else>
            <p class="text-p">
              <span class="name-i">{{ item.name }}：</span>
              <span class="out-text"> {{ item.value }}</span>
            </p>
          </template>
        </div>
      </el-collapse-item>
    </el-collapse>
  </template>
  <div v-if="nodeData.coreSetting?.type == 'content'">
    <h6 class="item-t">回答内容</h6>
    <div class="outputs">
      <p class="text-p">{{ resOutputs.answerContent }}</p>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import CopyBtn from './CopyBtn'

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
const arr = computed(() => {
  const res = props.resOutputs?.outputDetails || []
  let resArr = []
  resArr = res.map((item) => {
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
</script>

<style lang="scss" scoped>
.font-14 {
  font-size: 14px;
  font-weight: 500;
}
.p-l-15 {
  padding-left: 15px;
}
</style>
