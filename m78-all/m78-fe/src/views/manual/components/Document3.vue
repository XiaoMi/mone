<!--
 * @Description: 
 * @Date: 2024-01-24 11:31:43
 * @LastEditTime: 2024-01-24 18:23:45
-->
<template>
  <div>
    <ul>
      <li>
        <p class="module-dots">
          交互式分析：AI
          Document助手通过“对话”式的交互提问，即可完成对上传的excel文档完成分析，如下图所示，可针对自身需求多多提问、多多探索～
        </p>
        <div class="img-container">
          <img src="./document3-1.png" />
        </div>
      </li>
      <li>
        <p class="module-dots">
          历史提问：所有当前文档关联的“聊天”（问过的问题）都会被记录下来，方便后续快速获取结果/使用
        </p>
        <div class="img-container">
          <img src="./document3-2.png" />
        </div>
      </li>
      <li>
        <p class="module-dots">
          提问定制：对于“聊天记录”中记录的自然语言问题内容解析出的查询条件，可以做进一步的定制与修改，如下图所示（点击单个聊天记录中的"..."标记，会弹出相应的操作菜单，选择"编辑"即可）
        </p>
        <div class="img-container">
          <img src="./document3-3.png" />
          <img src="./document3-4.png" />
        </div>
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
export default {
  name: 'Document3' //给组件命名
}
</script>
<script setup lang="ts"></script>

<style scoped></style>
