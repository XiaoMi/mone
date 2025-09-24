<!--
 * @Description: 
 * @Date: 2024-07-29 16:16:12
 * @LastEditTime: 2024-07-29 19:31:17
-->
<template>
  <el-select
    v-model="val"
    placeholder="请选择"
    filterable
    style="width: 100%"
    allow-create
    default-first-option
    @focus="getList"
  >
    <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value">
    </el-option>
  </el-select>
</template>
<script lang="ts" setup>
import { computed, reactive, ref, watch } from 'vue'
// import { nacosMethodsApi } from "@/plugin/axios/new/modules/common/common.js";

const props = defineProps({
  modelValue: {},
  methodQueryVal: {}
})

const emits = defineEmits(['update:modelValue', 'change'])
const val = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
    emits('change', v)
  }
})

const options = ref([])
const getOptions = (serviceName) => {
  // nacosMethodsApi({ serviceName }).then((res) => {
  //   options.value = res.map((item) => {
  //     return {
  //       value: item,
  //       label: item,
  //     };
  //   });
  // });
}
const getList = () => {
  console.log('this.methodQueryVal', props.methodQueryVal)
  getOptions(props.methodQueryVal)
}

watch(
  () => props.methodQueryVal,
  (val) => {
    if (val) {
      getOptions(val)
    }
  },
  {
    immediate: true,
    deep: true
  }
)
</script>
