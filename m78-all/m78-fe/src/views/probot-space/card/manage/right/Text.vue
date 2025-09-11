<!--
 * @Description: 
 * @Date: 2024-10-17 15:29:26
 * @LastEditTime: 2024-10-22 10:30:36
-->
<template>
  <Container>
    <template #data>
      <FormItem prop="" title="内容" :require="true" style="width: 100%">
        <el-input
          type="textarea"
          :autosize="{ minRows: 1, maxRows: 8 }"
          v-model="property.content.value"
        ></el-input>
      </FormItem>
    </template>
    <template #base>
      <!-- <Color v-model="property.operate"></Color> -->
    </template>
    <template #operate>
      <Operate v-model="property.operate"></Operate>
    </template>
    <template v-if="property.visibilitySetting" #high>
      <LoopRendering v-model="property.loopRending"></LoopRendering>
      <Visible v-model="property.visibilitySetting"></Visible>
    </template>
  </Container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import Container from './Container.vue'
import FormItem from './components/FormItem.vue'
// import Color from './components/Color.vue'
import Operate from './components/Operate.vue'
import Visible from './components/Visible.vue'
import LoopRendering from './components/LoopRendering.vue'

const props = defineProps({
  modelValue: {
    type: Object,
    required: true
  }
})
const emits = defineEmits(['update:modelValue'])
const property = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})
</script>
<style lang="scss" scoped></style>
