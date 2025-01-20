<template>
  <div class="panel">
    <div class="panel-header pointer" @click="toggle">
      <div class="panel-header-header">
        <div class="header-icon">
          <i class="img iconfont icon-tubiao"></i>
        </div>
        <div class="header-name">
          <span>{{ props.name }}</span>
        </div>
      </div>
      <div class="panel-header-footer">
        <div class="icon">
          <el-icon v-if="fold"><ArrowDown /></el-icon>
          <el-icon v-else><ArrowUp /></el-icon>
        </div>
      </div>
    </div>
    <div class="panel-body" v-if="!fold">
      <NodeTem
        :title="props.name"
        img=""
        :desc="props.desc"
        @onDragStart="dragStart"
        @addClick="addClick"
        :draggable="draggable"
        :showDetail="showDetail"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import NodeTem from './NodeTem.vue'

const emits = defineEmits(['onDragStart', 'addClick'])

const props = defineProps<{
  id: string
  name: string
  desc: string
  showDetail: {
    default: true
    type: Boolean
  }
  draggable: {
    default: true
    type: Boolean
  }
}>()

const addClick = () => {
  emits('addClick')
}

const fold = ref(false)

const toggle = () => {
  fold.value = !fold.value
}

const dragStart = (e) => {
  emits('onDragStart', e)
}
</script>

<style scoped lang="scss">
.pointer {
  cursor: pointer;
}

.icon {
  display: flex;
  justify-items: center;
}

.panel {
  padding-top: 5px;
  margin-top: 10px;
  &-header {
    display: flex;
    justify-content: space-between;
    justify-items: center;

    &-header {
      display: flex;
      justify-items: center;
      padding: 0 16px 10px 0;

      .header-icon {
        display: flex;
        justify-items: center;

        margin-right: 8px;

        .img {
          height: 16px;
          width: 16px;
        }
      }

      .header-name {
        display: flex;
        justify-items: center;
        font-size: 14px;
        font-weight: 700;
      }
    }

    &-footer {
      display: flex;
      justify-items: center;
    }
  }

  &-content {
    margin-top: 8px;
  }
}
.card {
  &-header {
    display: flex;
    padding-bottom: 16px;
    justify-content: space-between;
  }

  &-body {
  }
}
</style>
