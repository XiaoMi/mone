<template>
  <div class="tag-box">
    <div v-for="item in statusVal" :key="item.code" class="item-box">
      <el-tag
        :type="item.tagType"
        size="small"
        class="item-tag"
        effect="dark"
        :class="item.tagClass"
      ></el-tag>
      <span>{{ t(item.text) }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useExcleStore } from '@/stores/doc'
import { t } from '@/locales'

const docStore = useExcleStore()

const statusVal = computed(() => docStore.value)
</script>

<style scoped lang="scss">
.tag-box {
  display: flex;
  flex-direction: row-reverse;
  padding: 5px 3px 0 3px;
  margin-bottom: 5px;
}
.item-box {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-left: 10px;
}
.item-tag {
  margin-right: 2px;
  height: 12px;
  width: 12px;
  padding: 0;
}
.item-tag.normal {
  background: #636569;
  border-color: #636569;
}
</style>
