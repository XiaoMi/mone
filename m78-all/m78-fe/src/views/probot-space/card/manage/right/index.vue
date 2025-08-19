<!--
 * @Description: 
 * @Date: 2024-10-17 15:37:47
 * @LastEditTime: 2024-10-22 10:37:19
-->
<template>
  <el-form :model="currentItem.property" ref="formRef">
    <RightContent
      v-if="currentItem.type === 'CARD_ROOT'"
      v-model="currentItem.property"
    ></RightContent>
    <RightTitle v-model="currentItem.property" v-else-if="currentItem.type === 'BASE_COMPONENT_TITLE'"></RightTitle>
    <!-- <RightText v-model="currentItem.property" v-else-if="currentItem.type === 'BASE_COMPONENT_TEXT'"></RightText> -->
    <!-- <RightOuter v-model="currentItem.property" v-else-if="currentItem.type === ''"></RightOuter>
    <RightOuter v-model="currentItem.property" v-else-if="currentItem.type === ''"></RightOuter>
    <RightOuter v-model="currentItem.property" v-else-if="currentItem.type === ''"></RightOuter>
    <RightOuter v-model="currentItem.property" v-else-if="currentItem.type === ''"></RightOuter> -->
    <RightOuter
      v-model="currentItem.property"
      v-else-if="dataTypeMap[currentItem.type]?.customizeType === 'outer'"
    ></RightOuter>
    <RightInner
      v-model="currentItem.property"
      v-else-if="currentItem.type === 'container'"
    ></RightInner>
  </el-form>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import RightContent from './Content.vue'
import RightOuter from './Outer.vue'
import RightTitle from './Title.vue'
// import RightText from './Text.vue'
// import RightButton from './Button.vue'
// import RightImage from './Image.vue'
// import RightIcon from './Icon.vue'
// import RightTag from './Tag.vue'
// import RightRating from './Rating.vue'
// import RightDivider from './Divider.vue'
import RightInner from './Inner.vue'
import { useProbotCardStore } from '@/stores/card'
const cardStore = useProbotCardStore()

const dataTypeMap = computed(() => cardStore.dataTypeMap)
const currentItem = computed(() => cardStore.currentItem)

</script>

<style scoped></style>
