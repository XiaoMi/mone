<template>
  <div class="node-item" :draggable="true" @dragstart="onDragStartFn($event)">
    <p class="top-p">
      <div>
        <img v-if="img" :src="img" alt="33" class="header" />
        <span class="title">{{ title }}</span>
      </div>
      <el-button link class="add-btn" @click="addNode">
        <el-icon color="#4d53e8"><Plus /></el-icon>
      </el-button>
    </p>
    <p class="desc">{{ desc }}</p>
  </div>
</template>

<script setup>
const props=defineProps({
  img:{},
  title:{
    default:'标题'
  },
  desc: {
    default: '调用大语言模型，使用变量和提示词生成回复'
  }
})

const emits= defineEmits(['onDragStart','addClick'])
const onDragStartFn=(e) => {
  emits('onDragStart',e);
}
const addNode=()=>{
  emits('addClick');
}
</script>

<style lang="scss" scoped>
.node-item {
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 6px 8px 0 rgba(29, 28, 35, 0.06);
    cursor: -webkit-grab;
    cursor: grab;
    font-size: 12px;
    line-height: 16px;
    overflow: hidden;
    padding: 16px;
    margin-bottom: 10px;
  }
  .top-p {
    display: flex;
    justify-content: space-between;
    align-items: center;
    .title{
      font-size: 14px;
      color:#1c1f23;
      font-weight: 500;
      vertical-align: top;
      margin-left: 4px;
    }
    .header {
    width: 16px;
    height: 16px;
  }
  }

  .desc {
    color: rgba(28, 31, 35, 0.6);
    text-align: left;
    margin-top: 5px;
  }
</style>
