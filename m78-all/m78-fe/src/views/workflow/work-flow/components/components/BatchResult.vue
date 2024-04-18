<template>
  <div class="llm-batch-box">
    <div class="btach-nums" v-if="resOutputs.status != 3">
      <h6 class="item-t">选择要查看的批次结果</h6>
      <ul class="nums-ul">
        <li
          v-for="item in numbers"
          :key="item"
          class="batch-num"
          :class="[
            resOutputs?.outputDetails &&
            resOutputs?.outputDetails[0]?.value &&
            JSON.parse(resOutputs?.outputDetails[0]?.value)?.length - item >= 0
              ? ''
              : 'disabled-li',
            item == activeNum ? 'active-li' : ''
          ]"
          @click="clickNum(item)"
        >
          {{ item }}
        </li>
      </ul>
    </div>
    <el-collapse v-model="activeNames">
      <el-collapse-item name="1" v-if="inputs?.length > 0">
        <template #title>
          输入
          <CopyBtn :arr="inputs" :batchNum="activeNum" :isBatch="true" />
        </template>
        <BatchResItem :showArr="inputs" :activeNum="activeNum" />
      </el-collapse-item>
    </el-collapse>
    <StatusError :resOutputs="resOutputs" v-if="resOutputs.status == 3" />
    <el-collapse v-model="activeNames2" v-if="ouputs?.length > 0">
      <el-collapse-item title="" name="1">
        <template #title>
          输出
          <CopyBtn :arr="ouputs" :batchNum="activeNum" :isBatch="true" />
        </template>
        <BatchResItem :showArr="ouputs" :activeNum="activeNum" />
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import StatusError from './StatusError'
import CopyBtn from './CopyBtn'
import BatchResItem from './BatchResItem'

const props = defineProps({
  batchRes: {},
  nodeData: {},
  resInputs: {},
  resOutputs: {}
})

const activeNames = ref(['1'])
const activeNames2 = ref(['1'])
const activeNum = ref(1)
const clickNum = (item) => {
  const res = ouputs.value && ouputs.value[0]?.value[item - 1]
  if (typeof res != 'undefined') {
    activeNum.value = item
  }
}

const numbers = computed(() => {
  const num = props.nodeData.maxNum
  const numbers = []
  for (var i = 1; i <= num; i++) {
    numbers.push(i)
  }
  return numbers
})
const inputs = computed(() => {
  return props.resInputs?.inputDetails?.map((item) => {
    let value
    try {
      value = JSON.parse(item.value)
    } catch (error) {
      value = item.value
    }
    return {
      ...item,
      value
    }
  })
})
const ouputs = computed(() => {
  return props.resOutputs?.outputDetails?.map((item) => {
    let value
    try {
      value = JSON.parse(item.value)
    } catch (error) {
      value = item.value
    }
    return {
      ...item,
      value
    }
  })
})
</script>

<style lang="scss" scoped>
.nums-ul {
  display: flex;
}
.batch-num {
  padding: 8px 12px;
  border: solid 1px #9e9e9e;
  border-radius: 6px;
  color: #333333;
  font-weight: 500;
  cursor: pointer;
}
.batch-num + .batch-num {
  margin-left: 10px;
}
.disabled-li {
  background: #e7e7e7;
  border-color: #e7e7e7;
}
.active-li {
  border-color: #4e53e8;
}
.p-l-15 {
  padding-left: 15px;
}
.out-text {
  display: flex;
}
.btach-nums {
  padding-bottom: 10px;
}
</style>
