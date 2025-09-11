<!--
 * @Description: 
 * @Date: 2024-03-06 17:00:09
 * @LastEditTime: 2024-10-09 10:34:11
-->
<template>
  <div :class="'base-info ' + props.size">
    <div class="left" v-if="props?.data?.avatarUrl!==false">
      <template v-if="props?.data?.avatarUrl">
        <img v-if="props.data.avatarUrl?.includes('http')" :src="props.data.avatarUrl" />
        <BaseIcon
          v-else
          :index="props.data.avatarUrl"
          :size="props.iconSize || props.size"
        ></BaseIcon>
      </template>
    </div>
    <div class="center">
      <template v-if="props.size != 'mini'">
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
            <span >{{ props.data.describe || props.data.remark || props.data.desc }}</span>
          </el-tooltip>
        </div>
        <slot name="bottom" v-else></slot>
        <slot></slot>
      </template>
      <div v-else class="mini-content">
        <slot name="before"></slot>
        <span class="name">{{ props.data.name }}</span>
        <slot></slot>
      </div>
    </div>
    <slot name="right"></slot>
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
    background-color: #f1f1f1;
    display: flex;
    align-items: center;
    border-radius: 10px;
    width: 62px;
    height: 62px;
    overflow: hidden;
    margin-right: 10px;

    img {
      width: 100%;
      height: 100%;
    }
  }
  .center {
    flex: 1;
    color: hsl(224 71.4% 4.1%);
    text-align: left;
    width: calc(100% - 82px);
    .name,
    .describe {
      overflow:hidden;
      width: 100%;
      span {
        display: inline-block;
        width: 100%;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
    .name {
      font-size: 14px;
      font-weight: 600;
      height: 20px;
      line-height: 20px;
      color: rgb(51 65 85);
    }
    .describe {
      font-size: 14px;
      line-height: 20px;
      height: 20px;
      color: rgb(107, 114, 128);
      width: 100%;
    }
  }
  &.mini {
    .left {
      width: 20px;
      height: 20px;
      border-radius: 5px;
    }
    .center {
      .name {
        font-size: 14px;
        line-height: 20px;
        height: 20px;
        font-weight: normal;
        padding: 0 4px;
      }
      .describe {
        font-size: 13px;
        line-height: 20px;
      }
      .mini-content {
        font-size: 13px;
        line-height: 20px;
        color: #666;
      }
    }
  }
  &.small {
    .left {
      width: 40px;
      height: 40px;
    }
    .center {
      .name {
        font-size: 14px;
        line-height: 20px;
      }
      .describe {
        font-size: 13px;
        line-height: 20px;
      }
    }
  }
  &.large {
    .left {
      width: 100px;
      height: 100px;
    }
    .center {
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
