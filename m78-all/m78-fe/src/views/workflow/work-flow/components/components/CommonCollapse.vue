<template>
  <div class="inputs-box">
    <el-collapse v-model="activeNames">
      <el-collapse-item name="1">
        <template #title>
          <div class="t-box">
            <div class="t-left">
              <CollapseTitle
                :activeNames="activeNames"
                :title="props.title"
                :content="props.content"
                tipClass="title-tooltip"
                :showAdd="showAdd"
                @add="addFn"
              />
            </div>
          </div>
        </template>
        <slot />
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script setup lang="ts">
import { ref, defineProps, defineEmits } from 'vue'
import CollapseTitle from './CollapseTitle.vue'

const props = defineProps({
  title: {},
  content: {},
  showAdd: {
    default: true
  }
})
const activeNames = ref(['1'])
const emits = defineEmits(['add'])
const addFn = () => {
  if (!props.showAdd) return
  emits('add')
}
</script>

<style scoped>
.inputs-box {
  width: 100%;
  margin-bottom: 10px;
}
.t-box {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.t-left {
  width: 100%;
  display: flex;
  align-items: center;
  flex: 1;
}
.title-tooltip {
  margin-left: 4px;
}
</style>
