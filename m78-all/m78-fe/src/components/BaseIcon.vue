<!--
 * @Description:
 * @Date: 2024-03-06 14:29:51
 * @LastEditTime: 2024-08-09 19:37:07
-->
<template>
  <div :class="'base-icon icon-item' + props.index + ' ' + props.size">
    <el-image v-if="isImage" :src="props.index" fit="cover"></el-image>
    <i v-else :class="'iconfont ' + uploadIcon[Number(props.index)]"></i>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps({
  index: {
    type: String,
    default: String(Math.floor(Math.random() * 10))
  },
  size: {
    type: String,
    default: '' //large small
  }
})

const isImage = computed(() => {
  if (typeof props.index === 'string' && props.index?.startsWith('http')) return true
  return false
})

// 图标
const uploadIcon = [
  'icon-copy3',
  'icon-tubiao_yundong',
  'icon-tubiao23',
  'icon-tubiao-dingwei',
  'icon-tubiao2',
  'icon-tubiao1',
  'icon-tubiao',
  'icon-tubiao-2',
  'icon-tubiao-1',
  'icon-tubiao-',
  'icon-tuandui',
  'icon-aixin1',
  'icon-db-table'
]
</script>

<style scoped lang="scss">
.base-icon {
  width: 62px;
  height: 62px;
  border-radius: 10px;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;

  .iconfont {
    font-size: 40px;
  }
  &.mini {
    width: 20px;
    height: 20px;
    border-radius: 5px;
    .iconfont {
      font-size: 14px;
    }
  }
  &.small {
    width: 40px;
    height: 40px;
    .iconfont {
      font-size: 24px;
    }
  }
  &.large {
    width: 100px;
    height: 100px;
    .iconfont {
      font-size: 60px;
    }
  }
  &.icon-item0 {
    background-color: rgb(223, 245, 249);
    background-image: radial-gradient(180px at -5% -20%, #ffecd2, #fcb69f);
    .iconfont {
      color: #fff;
    }
  }
  &.icon-item1 {
    background-color: rgb(223, 245, 249);
    background-image: radial-gradient(180px at -5% -20%, #f6d365, #fda085);
    .iconfont {
      color: #fff;
    }
  }
  &.icon-item2 {
    background-color: rgb(223, 245, 249);
    background-image: radial-gradient(180px at -5% -20%, #fbc2eb, #a6c1ee);
    .iconfont {
      color: #fff;
    }
  }
  &.icon-item3 {
    background-color: rgb(223, 245, 249);
    background-image: radial-gradient(180px at -5% -20%, #a1c4fd, #c2e9fb);
    .iconfont {
      color: #fff;
    }
  }
  &.icon-item4 {
    background-color: rgb(223, 245, 249);
    background-image: radial-gradient(180px at -5% -20%, #d4fc79, #96e6a1);
    .iconfont {
      color: #fff;
    }
  }
  &.icon-item5 {
    background-color: rgb(223, 245, 249);
    background-image: radial-gradient(180px at -5% -20%, #e0c3fc, #8ec5fc);
    .iconfont {
      color: #fff;
    }
  }
  &.icon-item6 {
    background-color: rgb(223, 245, 249);
    background-image: radial-gradient(180px at -5% -20%, #ffecd2, #fcb69f);
    .iconfont {
      color: #fff;
    }
  }
  &.icon-item7 {
    background-color: rgb(223, 245, 249);
    background-image: radial-gradient(180px at -5% -20%, #fad0c4, #ffd1ff);
    .iconfont {
      color: #fff;
    }
  }
  &.icon-item8 {
    background-color: rgb(223, 245, 249);
    background-image: radial-gradient(180px at -5% -20%, #a18cd1, #fbc2eb);
    .iconfont {
      color: #fff;
    }
  }
  &.icon-item9 {
    background-color: rgb(223, 245, 249);
    background-image: radial-gradient(180px at -5% -20%, #ff9a9e, #fad0c4);
    .iconfont {
      color: #fff;
    }
  }
  &.icon-item10 {
    background-color: rgb(223, 245, 249);
    background-image: radial-gradient(180px at -5% -20%, #f6d365, #fda085);
    .iconfont {
      color: #fff;
    }
  }
  &.icon-item11 {
    background-image: linear-gradient(
      to right,
      #ff8177 0%,
      #ff867a 0%,
      #ff8c7f 21%,
      #f99185 52%,
      #cf556c 100%
    );
    .iconfont {
      color: #fff;
    }
  }
  &.icon-item12 {
    background-color: rgb(223, 245, 249);
    background-image: radial-gradient(180px at -5% -20%, #52c1fa, #dbefff);
    .iconfont {
      color: #fff;
    }
  }
}
</style>
