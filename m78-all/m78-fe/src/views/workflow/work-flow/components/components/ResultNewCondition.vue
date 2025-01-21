<template>
  <div class="new-condtiion-res">
    <div v-for="item in conditionsArr" :key="item.index" class="condition-item">
      <div class="t">
        <h1 class="t-text">
          条件分支<i>({{ item.index }})</i
          ><i v-if="item.index > runIndex" class="not-run">未执行</i>
        </h1>
      </div>
      <!-- 如果错误了status=3 -->
      <div v-if="(item.index <= runIndex && resOutputs.status != 3) || resOutputs.status == 3">
        <ul v-for="(list, i) in item.listArr" :key="i" class="inner-item">
          <div class="r-box" v-if="list.conditionRelationship">
            <ConditionLabelSel v-model="list.conditionRelationship" :disabled="true" />
          </div>
          <li class="list-li">
            <div class="show-text">
              <div class="content">
                <ConditionReferName :name="list.name" :nodes="nodes" :nodeData="nodeData" />
                <i class="val-i">{{ list.value }}</i>
              </div>
            </div>
            <OperatorText :opVal="list.operator" />
            <div class="show-text" :class="emptyOps.includes(list.operator) ? 'empty' : ''">
              <div class="content" v-show="!emptyOps.includes(list.operator)">
                <ConditionReferName
                  :name="list.name2"
                  :nodes="nodes"
                  v-if="list.name2"
                  :nodeData="nodeData"
                />
                <i class="val-i">{{ list.value2 }}</i>
              </div>
            </div>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
import OperatorText from './OperatorText.vue'
import ConditionReferName from './ConditionReferName.vue'
import ConditionLabelSel from './ConditionLabelSel'
import { ref, defineProps, computed } from 'vue'
import { switchEmptyOps } from '@/views/workflow/common/base.js'
import { extractNumber } from '@/views/workflow/common/init-node.js'

const emptyOps = ref(switchEmptyOps)
const props = defineProps({
  nodes: {},
  resInputs: {},
  nodeData: {},
  resOutputs: {}
})

const runIndex = computed(() => {
  if (props.resOutputs.status == 3) {
    // 如果失败了，无法确定执行到了哪一步，将所有的输入都展示
    return props.resInputs.inputDetails.length
  }
  const findResult = props.resOutputs.outputDetails.find((it) => it.name == 'result')
  if (findResult) {
    const str = findResult?.value
    const num = extractNumber(str) // 如果没拿到数字，则会返回null,则表示走到了else
    return num == null ? props.resInputs.inputDetails.length : num
  } else {
    return props.resInputs.inputDetails.length
  }
})

const conditionsArr = computed(() => {
  // 旧的条件选择，返回一条
  if (props.nodeData.nodeType == 'precondition') {
    return [
      {
        index: 1,
        listArr: props.resInputs.inputDetails
      }
    ]
  } else {
    const res = props.resInputs?.inputDetails || []
    // 过滤出有conditionIndex
    const filterNoIndex = res.filter((it) => it.conditionIndex)
    // 获取所有的index值
    const numArr = filterNoIndex.map((it) => it.conditionIndex)
    // index值排序并去重
    const keyArr = sortAndUnique(numArr)
    // 装配成适合的数组
    const newArr = []
    keyArr.forEach((it) => {
      newArr.push({
        index: it,
        listArr: res.filter((item) => item.conditionIndex == it)
      })
    })
    return newArr
  }
})

// 将数组从小到大排除并去重
const sortAndUnique = (arr) => {
  // 将字符串数组转换为数字数组
  const numArr = arr.map(Number)

  // 使用 Set 去重，然后转回数组并排序
  const uniqueSortedArr = [...new Set(numArr)].sort((a, b) => a - b)

  // 将结果转回字符串数组
  return uniqueSortedArr.map(String)
}

// 测试
const input = ['2', '1', '2']
console.log(sortAndUnique(input))
</script>

<style lang="scss" scoped>
.t {
  display: flex;
}
.t-text {
  font-size: 14px;
  font-weight: 600;
  margin-right: 10px;
}
.list-li {
  width: 470px;
  background: #f7f7fa;
  border: 1px solid rgba(29, 28, 35, 0.16);
  border-radius: 12px;
  display: flex;
  justify-content: space-around;
  align-items: stretch; /* 改为 stretch */
}

.show-text {
  width: 170px;
  background: rgba(46, 46, 56, 0.04);
  border: 1px solid rgba(29, 28, 35, 0.08);
  border-radius: 8px;
  max-height: 272px;
  min-height: 32px;
  overflow-y: auto;
  padding: 6px 12px;
  display: flex;
  font-size: 14px;
  font-weight: 400;
  line-height: 20px;
  word-break: break-all;
  margin: 12px 0;
  overflow-wrap: break-word;
  word-wrap: break-word;
  word-break: break-all;
}
.show-text.empty {
  background: transparent;
  border: none;
}
.content {
  display: inline-block;
  white-space: normal;
}
.content i {
  display: inline;
}

.new-condtiion-res {
  padding-bottom: 20px;
}
.condition-item + .condition-item {
  margin-top: 20px;
}
.inner-item + .inner-item {
  margin-top: 10px;
}
.val-i {
  // white-space: break-spaces;
}
.r-box {
  padding-bottom: 10px;
  // text-align: center;
  :deep(.oz-input) {
    width: 50px !important;
    .oz-input__inner {
      -webkit-text-fill-color: #1d1c23;
      // color: #1d1c23;
    }
    .oz-input__suffix {
      display: none;
    }
    .oz-input__wrapper {
      background: #fff;
    }
  }
}
.not-run {
  margin-left: 5px;
  color: var(--oz-color-warning);
}
</style>
