<!--
 * @Description: 
 * @Date: 2024-04-15 16:26:56
 * @LastEditTime: 2024-04-15 17:02:27
-->
<template>
  <div>
    <el-button link @click.stop="showGenFn">
      <i class="iconfont icon-code" style="margin-right: 2px"></i>
      生成代码
    </el-button>
    <el-button link :icon="Edit" @click.stop="showIde">在IDE中编辑</el-button>
  </div>
  <ProbotCodeEditor v-model="showEditor" v-model:code="codeVal" />
  <ProbotCodeGenerator
    v-model="showGenerator"
    @codeGenRes="
      (data: any) => {
        emits('codeGenRes', data)
      }
    "
    :isFlow="props.isFlow"
  />
</template>

<script setup lang="ts">
import { ref, computed, defineProps } from 'vue'
import { Edit } from '@element-plus/icons-vue'
import ProbotCodeEditor from '@/components/ProbotCodeEditor'
import ProbotCodeGenerator from '@/components/ProbotCodeGenerator'

const showEditor = ref(false)
const showGenerator = ref(false)

const switchShowGen = () => {
  showGenerator.value = !showGenerator.value
}
const showGenFn = () => {
  switchShowGen()
}
const switchShowIde = () => {
  showEditor.value = !showEditor.value
}

const showIde = () => {
  switchShowIde()
}

const emits = defineEmits(['update:code', 'codeGenRes'])
const props = defineProps({
  code: {},
  isFlow: {
    default: false
  }
})
const codeVal = computed({
  get() {
    return props.code
  },
  set(val) {
    emits('update:code', val)
  }
})
</script>

<style scoped lang="scss"></style>
