<!--
 * @Description:
 * @Date: 2024-03-04 16:35:15
 * @LastEditTime: 2024-03-28 17:09:56
-->
<template>
  <div class="probot-flow-form">
    <div class="flow-plug">
      <BaseGroup
        class="head-container"
        title="表绑定"
        tooltip="暂定"
        :btn="{ name: '绑定', icon: 'icon-plus1', click: addPlug, size: 'small' }"
      ></BaseGroup>
      <ul class="list-container" v-if="tables.length">
        <li v-for="(item, index) in tables" :key="index" @click="handleJump(item)">
          <div class="left">
            <BaseInfo class="py-[20px]" :data="item" size="small"></BaseInfo>
          </div>
          <div class="right">
            <div class="flex flex-row-reverse">
              <div>
                <el-button type="primary" @click.stop="edit">编辑</el-button>
                <el-button type="primary" @click.stop="removeItem(item)">删除</el-button>
              </div>
            </div>
          </div>
        </li>
      </ul>
      <el-empty v-else description="给你的Probot绑定表吧" :image-size="80"></el-empty>
    </div>
    <BindForm v-model="showBind" :formData="formData" @update="updateTables" />
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import BindForm from './BindForm.vue'
import BaseInfo from '@/components/BaseInfo.vue'
import BaseGroup from '../components/BaseGroup.vue'
import { createDataSource, getTableDetail } from '@/api/data-source'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const showBind = ref(false)
const router = useRouter()
const props = defineProps({
  formData: {
    type: Object,
    required: true
  }
})

const tables = ref([])

const addPlug = () => {
  showBind.value = true
}

const removeItem = (item: any) => {
  tables.value = tables.value.filter((it) => it.id !== item.id)
}

const edit = () => {
  showBind.value = true
}

const updateTables = (tableList) => {
  tables.value = tableList
}

const handleJump = (item) => {
  getTableDetail(item.tableName)
    .then((data) => {
      if (data.message === 'ok') {
        return createDataSource({
          host: data.data.connectionInfo?.host,
          port: data.data.connectionInfo?.port,
          database: data.data.connectionInfo?.database,
          user: data.data.connectionInfo?.user,
          pwd: data.data.connectionInfo?.pwd,
          jdbcUrl: data.data.connectionInfo?.jdbcUrl
        })
      } else {
        ElMessage.error(data.message!)
        return Promise.reject()
      }
    })
    .then((response) => {
      if (response.message === 'ok') {
        router.push({
          path: '/data-source',
          query: {
            id: response.data?.id,
            tableName: item.tableName
          }
        })
      } else {
        ElMessage.error(response.message!)
      }
    })
    .catch((e) => {
      console.log(e)
    })
}

defineExpose({
  tables
})

watch(
  () => props.formData.tableList,
  (tableList, preTableList) => {
    if (tableList !== preTableList) {
      tables.value = tableList.map((it) => {
        return {
          ...it,
          name: it.tableName
        }
      })
    }
  }
)
</script>

<style scoped lang="scss">
.head-container {
  padding-bottom: 10px;
}
.list-container {
  border-top: 1px solid #ddd;
  li {
    border-bottom: 1px solid #ddd;
    display: flex;
    align-items: center;
    cursor: pointer;

    .left {
      width: 20%;
    }
    .right {
      flex: 1;
    }
  }
}
.flow-workflow {
  padding-top: 20px;
  .list-container li {
    padding: 10px 0;
  }
}

.child-container {
  border-bottom: 1px solid #ddd;
  display: flex;
  justify-content: space-between;
  padding: 10px 5px;
  font-size: 14px;
  line-break: 20px;
  color: rgba(0, 0, 0, 0.7);
  &:last-child {
    border: none;
  }
}
.btn-container {
  display: flex;
  justify-content: space-between;
  width: 300px;
  float: right;
}
</style>
