<!--
 * @Description: 
 * @Date: 2024-03-14 19:25:26
 * @LastEditTime: 2024-08-15 22:56:49
-->
<template>
  <div>
    <div class="publish-calendar">
      <h3>发布日历</h3>
      <BaseCommitLog :data="data?.publishRecordDTOS"></BaseCommitLog>
      <h3>历史列表</h3>
      <el-table :data="props?.data?.publishRecordDTOS" style="width: 100%">
        <el-table-column prop="id" label="id" />
        <!-- <el-table-column prop="botId" label="botId" /> -->
        <el-table-column label="发布平台" v-slot="{ row }">
          <BaseIconPlatform
            v-for="(item, index) in row.publishImChannel ? JSON.parse(row.publishImChannel) : []"
            :key="index"
            :iconType="item"
          >
          </BaseIconPlatform>
        </el-table-column>
        <!-- <el-table-column prop="botSnapshot" label="botSnapshot" show-overflow-tooltip /> -->
        <el-table-column label="发布时间" v-slot="{ row }">
          {{ dateFormat(row.publishTime, 'yyyy-mm-dd HH:MM:ss') }}
        </el-table-column>
        <el-table-column prop="publisher" label="发布人" />
        <el-table-column label="发布记录" v-slot="{ row }">
          {{ row.versionRecord }}
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import dateFormat from 'dateformat'
import BaseIconPlatform from '@/components/probot/BaseIconPlatform.vue'
import BaseCommitLog from './BaseCommitLog.vue'

const props = defineProps({
  data: {
    type: Object,
    default: () => ({})
  }
})
</script>

<style scoped lang="scss">
.publish-calendar {
  padding-bottom: 10px;
  h3 {
    padding-top: 10px;
    padding-bottom: 10px;
    font-size: 14px;
  }
}
</style>
