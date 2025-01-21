<template>
  <div class="inputs-box">
    <el-collapse v-model="activeNames">
      <el-collapse-item name="1">
        <template #title>
          <div class="t-box">
            <div class="t-left">
              <CollapseTitle
                :activeNames="activeNames"
                title="输出"
                :content="titleDes || '输出'"
                tipClass="title-tooltip"
                :showAdd="false"
              />
            </div>
            <div></div>
          </div>
        </template>
        <slot />
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script setup lang="ts">
import { ref, defineProps } from 'vue'
import CollapseTitle from '@/views/workflow/work-flow/components/components/CollapseTitle.vue'

const props = defineProps({
  titleDes: {}
})

const activeNames = ref(['1'])
</script>

<style scoped lang="scss">
.inputs-box {
  width: 100%;
  margin-bottom: 10px;
}
.t-left {
  display: flex;
  align-items: center;
}
.input-item {
  display: flex;
  justify-content: space-between;

  .name-box {
    flex: 1;
  }
}
.val-box {
  display: flex;
}
</style>
