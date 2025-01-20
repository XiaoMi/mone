<!--
 * @Description: 
 * @Date: 2024-09-19 10:54:46
 * @LastEditTime: 2024-10-21 15:30:00
-->
<template>
  <div class="tab-select">
    <ul>
      <li
        :class="value == key ? 'active' : ''"
        @click="liClick(key)"
        v-for="(item, key) in listData"
        :key="key"
        :style="'width:' + 100 / Object.keys(listData).length + '%'"
      >
        {{ item }}
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  list: {
    type: [Function, Object, Array],
    default: () => ({})
  }
})

const emits = defineEmits(['update:modelValue','change'])

const value = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})

const listData = ref({})
onMounted(async () => {
  if (typeof props.list === 'function') {
    listData.value = await props.list()
  } else {
    listData.value = props.list
  }
})

const liClick = (val: string) => {
  value.value = val
  emits('change')
}
</script>

<style scoped lang="scss">
.tab-select {
  width: 100%;
  height: 30px;
  + .tab-select {
    margin-top: 10px;
  }
  ul {
    width: 100%;
    height: 30px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    border: 1px solid #ddd;
    padding: 0px 5px;
    border-radius: 5px;
    background-color: #fff;
  }
  li {
    height: 24px;
    line-height: 24px;
    text-align: center;
    border-radius: 5px;
    cursor: pointer;
    color: #666;
    font-size: 13px;
    &.active {
      background-color: rgba(186, 192, 255, 0.2);
      color: rgba(78, 64, 229, 1);
    }
  }
}
</style>
