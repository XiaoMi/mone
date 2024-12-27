<!--
 * @Description: 
 * @Date: 2024-01-24 11:31:43
 * @LastEditTime: 2024-01-24 18:21:06
-->
<template>
  <div>
    <ul>
      <li>
        <p class="module-dots">
          上传：文档分析的第一步就是要上传待处理/分析的excel文档，如下图操作即可。
        </p>
        <div class="img-container">
          <img src="./document2-1.png" />
          <img src="./document2-2.png" />
          <img src="./document2-3.png" />
        </div>
      </li>
      <li>
        <p class="module-dots">
          元信息描述：用户对于文档中数据的含义、数据间的关联性描述，能够做为元信息/上下文，更好的帮助AI
          Document助手理解文档数据内容，产出更合理、准确的分析/统计结果。可以通过文档区域的右上角“Meta元数据”按钮，添加当前文档想要关联的元数据，如下图所示。
        </p>
        <div class="img-container">
          <img src="./document2-4.png" />
        </div>
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
export default {
  name: 'Document2' //给组件命名
}
</script>
<script setup lang="ts"></script>

<style scoped></style>
