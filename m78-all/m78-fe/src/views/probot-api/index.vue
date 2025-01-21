<!--
 * @Description: 
 * @Date: 2024-08-12 15:03:48
 * @LastEditTime: 2024-08-15 22:40:16
-->
<template>
    <div class="probot-api">
      <div class="probot-apikey-btn">
        <el-button @click="dialogVisible = true" type="primary"> API密钥 </el-button>
      </div>
      <div class="probot-api-content">
        <Bot v-if="route.query.botId"></Bot>
        <Workflow v-else-if="route.query.flowId"></Workflow>
        <Knowledge v-else-if="route.query.knowledgeBaseId"></Knowledge>
      </div>
    </div>
    <el-dialog v-model="dialogVisible" title="API密钥" width="600">
      <div>
        <div class="create-btn">
          <el-button type="primary" plain @click="create" :disabled="createLoading"
            >创建密钥</el-button
          >
        </div>
        <el-table :data="tableData" style="width: 100%">
          <el-table-column prop="apiKey" label="密钥" width="180" />
          <el-table-column prop="createTime" label="创建时间" width="180">
            <template #default="scope">
              {{ dateFormat(scope.row.createTime, 'yyyy-mm-dd hh:MM:ss') }}
            </template>
          </el-table-column>
          <el-table-column prop="creator" label="创建人" />
          <el-table-column fixed="right" label="操作" min-width="70">
            <template #default="scope">
              <el-button link type="primary" size="small" @click="handleDelete(scope.row.id)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { getByTypeIdAndType, apikeyCreate, apikeyDelete } from '@/api/probot-api'
import { useRoute } from 'vue-router'
import dateFormat from 'dateformat'
import { ElMessage, ElMessageBox } from 'element-plus'
import Bot from './Bot.vue'
import Workflow from './Workflow.vue'
import Knowledge from './Knowledge.vue'

const route = useRoute()
const dialogVisible = ref(false)
const tableData = ref('')
const createLoading = ref(false)

onMounted(() => {
  getList()
})
const getList = () => {
  getByTypeIdAndType({
    typeId: route?.query?.workspaceId,
    type: 1
  }).then((res) => {
    console.log('res', res)
    tableData.value = res.data
  })
}

const create = () => {
  apikeyCreate({
    typeId: route?.query?.workspaceId,
    type: 1
  }).then((res) => {
    if (res.data) {
      ElMessage({
        message: '创建成功.',
        type: 'success'
      })
    }
    getList()
  })
}
const handleDelete = (id) => {
  ElMessageBox.confirm('确定删除吗?')
    .then(() => {
      apikeyDelete({
        typeId: route?.query?.workspaceId,
        type: 1,
        id: id + ''
      }).then((res) => {
        if (res.data) {
          ElMessage({
            message: '删除成功.',
            type: 'success'
          })
        }
        getList()
      })
    })
    .catch(() => {
      // catch error
    })
}
</script>

<style scoped lang="scss">
.probot-api {
  max-width: 1200px;
  margin:0 auto;
  padding: 20px;
}
.probot-apikey-btn {
  display: flex;
  justify-content: flex-end;
}
.probot-api-content {
  padding: 20px 0px;
  border-radius: 5px;
}
.create-btn {
  padding-right: 10px;
  padding-bottom: 10px;
  display: flex;
  justify-content: flex-end;
}
</style>
<style lang="scss">
.pb10 {
  padding-bottom: 10px;
}
.pb20 {
  padding-bottom: 20px;
}
.fsize18 {
  font-size: 18px;
}
</style>
