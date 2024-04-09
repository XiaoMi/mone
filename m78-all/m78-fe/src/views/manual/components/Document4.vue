<!--
 * @Description: 
 * @Date: 2024-01-24 11:31:43
 * @LastEditTime: 2024-01-24 18:25:30
-->
<template>
  <div>
    <ul>
      <li>
        <p class="module-dots">
          我的文档：用户已经上传过的文档可以在“我的文档”中查看，文档的解析状态如图中图例所示，包括解析成功、解析中、解析失败
        </p>
        <div class="img-container">
          <img src="./document4-1.jpeg" />
        </div>
      </li>
      <li>
        <p class="module-dots">
          文档收藏：对于常用的文档，用户可以在文档展示区右上角的心型图标处点击收藏，之后可以在“我的收藏”中进行查看
        </p>
        <div class="img-container">
          <img src="./document4-2.png" />
        </div>
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
export default {
  name: 'Document4' //给组件命名
}
</script>
<script setup lang="ts"></script>

<style scoped></style>
