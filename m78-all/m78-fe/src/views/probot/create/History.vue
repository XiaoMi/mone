<!--
 * @Description:
 * @Date: 2024-03-14 19:25:26
 * @LastEditTime: 2024-03-14 20:05:10
-->
<template>
  <div>
    <el-table :data="props?.data?.publishRecordDTOS" style="width: 100%">
      <el-table-column prop="id" label="id" />
      <el-table-column label="发布平台" v-slot="{ row }">
        <BaseIconPlatform
          v-for="(item, index) in row.publishImChannel ? JSON.parse(row.publishImChannel) : []"
          :key="index"
          :iconType="item"
        >
        </BaseIconPlatform>
      </el-table-column>
      <el-table-column label="发布时间" v-slot="{ row }">
        {{ dateFormat(row.publishTime, 'yyyy-mm-dd HH:MM:ss') }}
      </el-table-column>
      <el-table-column prop="publisher" label="发布人" />
      <el-table-column label="发布记录" v-slot="{ row }">
        {{ row.versionRecord }}
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import dateFormat from 'dateformat'
import BaseIconPlatform from '@/views/probot/components/BaseIconPlatform.vue'

const props = defineProps({
  data: {
    type: Object,
    default: () => ({})
  }
})
</script>

<style scoped></style>
