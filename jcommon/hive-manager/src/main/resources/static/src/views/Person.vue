<template>
  <div class="user-info">
    <div class="image">
      <el-image :src="props.agent.img" alt="用户头像" />
      <h2>{{ props.agent.name }}</h2>
    </div>
    <dl>
      <dt>年龄:</dt>
      <dd>{{ props.agent.age }}</dd>
    </dl>
    <dl>
      <dt>性别:</dt>
      <dd>{{ props.agent.gender }}</dd>
    </dl>
    <dl>
      <dt>个性:</dt>
      <dd>{{ props.agent.personality }}</dd>
    </dl>
    <dl>
      <dt>描述:</dt>
      <dd>{{ props.agent.description }}</dd>
    </dl>
  </div>
</template>

<script setup lang="ts">
const props = defineProps({
  agent: Object,
});
</script>

<style lang="scss">
.user-info {
  font-family: cursive;
  .image {
    display: flex;
    align-items: center;
    justify-content: flex-start;
    gap: 12px;
    margin-bottom: 12px;
    .el-image {  
      width: 48px;
      border-radius: 50%;
    }
  }
  dl {
    display: flex;
    align-items: flex-start;
    justify-content: flex-start;
    gap: 12px;
    font-size: 13px;
    margin-bottom: 4px;
    dt {
      font-weight: bold;
      white-space: nowrap;
    }
  }
}
</style>
