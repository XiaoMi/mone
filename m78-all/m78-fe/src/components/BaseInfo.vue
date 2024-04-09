<template>
  <div :class="'base-info ' + props.size">
    <div class="left">
      <template v-if="props?.data?.avatarUrl">
        <img v-if="props.data.avatarUrl?.includes('http')" :src="props.data.avatarUrl" />
        <BaseIcon
          v-else
          :index="props.data.avatarUrl"
          :size="props.iconSize || props.size"
        ></BaseIcon>
      </template>
    </div>
    <div class="right">
      <slot name="top" v-if="props.data.custom"></slot>
      <div v-else class="name">
        <el-tooltip effect="dark" :content="props.data.name" placement="top">
          <span>{{ props.data.name }}</span>
        </el-tooltip>
      </div>
      <div class="describe" v-if="props.data.describe || props.data.remark || props.data.desc">
        <el-tooltip
          effect="dark"
          :content="props.data.describe || props.data.remark || props.data.desc"
          placement="top"
        >
          <span>{{ props.data.describe || props.data.remark || props.data.desc }}</span>
        </el-tooltip>
      </div>
      <slot name="bottom" v-else></slot>
      <slot></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import BaseIcon from '@/components/BaseIcon.vue'

const props = defineProps({
  data: {
    type: Object,
    default: {
      name: String,
      describe: String
    }
  },
  size: {
    type: String,
    default: ''
  },
  iconSize: {
    type: String,
    default: ''
  }
})
</script>

<style scoped lang="scss">
.base-info {
  display: flex;
  justify-content: space-between;
  width: 100%;
  flex: 1;
  .left {
    box-shadow:
      (0 0 #ddd, 0 0 #ddd),
      (0 0 #ddd, 0 0 #ddd),
      0 5px 7px -1px rgba(0, 0, 0, 0.1),
      0 2px 3px -2px rgba(0, 0, 0, 0.1);
    display: flex;
    align-items: center;
    border-radius: 10px;
    width: 62px;
    height: 62px;
    overflow: hidden;

    img {
      width: 100%;
      height: 100%;
    }
  }
  .right {
    flex: 1;
    padding-left: 10px;
    color: hsl(224 71.4% 4.1%);
    text-align: left;
    width: calc(100% - 82px);
    .name,
    .describe {
      span {
        display: inline-block;
        max-width: 100%;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
    .name {
      font-size: 14px;
      font-weight: 600;
      line-height: 20px;
      color: rgb(51 65 85);
      height: 25px;
    }
    .describe {
      font-size: 14px;
      line-height: 20px;
      color: rgb(107, 114, 128);
      width: 100%;
    }
  }
  &.small {
    .left {
      width: 40px;
      height: 40px;
    }
    .right {
      padding-left: 10px;
      .name {
        padding-bottom: 4px;
        font-size: 14px;
        line-height: 20px;
      }
      .describe {
        font-size: 14px;
        line-height: 20px;
      }
    }
  }
  &.large {
    .left {
      width: 100px;
      height: 100px;
    }
    .right {
      padding-left: 20px;
      .name {
        line-height: 28px;
        padding-bottom: 4px;
        font-size: 20px;
      }
      .describe {
        line-height: 24px;
        font-weight: 500;
      }
    }
  }
}
</style>
