<template>
  <div v-if="nodeData.nodeType == 'precondition'">
    <div v-for="(item, i) in resInputs?.inputDetails" :key="item.name">
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
  </div>
  <template v-if="nodeData.nodeType != 'precondition'">
    <template v-if="resInputs?.inputDetails?.length > 0">
      <el-collapse v-model="activeNames">
        <el-collapse-item name="1">
          <template #title>
            输入
            <CopyBtn :arr="resInputs?.inputDetails" />
          </template>
          <div class="outputs" v-for="item in resInputs?.inputDetails" :key="item.name">
            <p class="text-p">
              <span v-if="nodeData.nodeType == 'knowledge'">{{ mapNames[item.name] }}</span>
              <span class="name-i" v-else>{{ item.name }}：</span>
              <span class="out-text">
                <JsonViewer :value="item.value" sort theme="jv-light" />
              </span>
            </p>
          </div>
        </el-collapse-item>
      </el-collapse>
    </template>
  </template>
</template>

<script setup>
import { ref } from 'vue'
import { opList } from '../../baseInfo'
import CopyBtn from './CopyBtn'

const activeNames = ref(['1'])
const props = defineProps({
  resInputs: {},
  nodeData: {}
})
const mapNames = ref({
  $$TY_KNOWLEDGE_QUERY$$: 'Query'
})
const retTExt = (op) => {
  const opItem = opList.find((item) => item.value == op)
  return opItem ? opItem.label : op
}
</script>

<style lang="scss" scoped></style>
