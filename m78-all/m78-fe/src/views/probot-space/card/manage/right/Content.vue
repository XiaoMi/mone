<!--
 * @Description: 
 * @Date: 2024-09-19 10:08:05
 * @LastEditTime: 2024-10-16 20:44:46
-->
<template>
  <Container>
    <template #base>
      <Background v-model="property.background"></Background>
      <LineSpace v-model="property.form.rowGap"></LineSpace>
      <Padding v-model="property.form.padding"></Padding>
    </template>
    <template v-if="property.visibilitySetting" #high>
      <Visible v-model="property.visibilitySetting"></Visible>
    </template>
  </Container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import Container from './Container.vue'
import Background from './components/Background.vue'
import LineSpace from './components/LineSpace.vue'
import Padding from './components/Padding.vue'
import Visible from './components/Visible.vue'

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
