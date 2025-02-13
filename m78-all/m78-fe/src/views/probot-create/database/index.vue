<template>
  <div class="probot-flow-form">
    <div class="flow-plug">
      <BaseGroup
        class="head-container"
        title="表绑定"
        tooltip="暂定"
        :btn="{
          name: '创建',
          icon: 'icon-plus1',
          click: addPlug,
          size: 'small',
          disabled: props.disabled
        }"
      ></BaseGroup>
      <ul class="list-container" v-if="tables.length">
        <li v-for="(item, index) in tables" :key="index" @click="handleJump(item)">
          <div class="left">
            <BaseInfo class="py-[20px]" :data="item" size="small"></BaseInfo>
          </div>
          <div class="right">
            <div class="flex flex-row-reverse">
              <div @click.stop>
                <el-checkbox
                  v-model="item.checked"
                  class="check-box"
                  @change="
                    (val) => {
                      changeCheck(val, item)
                    }
                  "
                  >绑定</el-checkbox
                >
                <el-button type="primary" @click.stop="edit(item)" size="small">编辑</el-button>
                <el-button type="warning" @click.stop="removeItem(item)" size="small"
                  >删除</el-button
                >
              </div>
            </div>
          </div>
        </li>
      </ul>
      <el-empty v-else description="给你的Probot绑定表吧" :image-size="80"></el-empty>
    </div>
    <BindForm
      v-model="showBind"
      @update="updateTables"
      :workspaceId="workspaceId"
      :editTName="editTName"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, computed } from 'vue'
import BindForm from '../components/BindForm.vue'
import BaseInfo from '@/components/BaseInfo.vue'
import BaseGroup from '../components/BaseGroup.vue'
import { createDataSource, getTableDetail } from '@/api/data-source'
import { getDbList, delDb } from '@/api/probot-db'
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'
import { useProbotStore } from '@/stores/probot'
import { t } from '@/locales'

const showBind = ref(false)
const router = useRouter()
const route = useRoute()
const props = defineProps({
  formData: {
    type: Object,
    required: true
  },
  disabled: {
    type: Boolean,
    default: false
  }
})
const probotStore = useProbotStore()
const workspaceId = computed(() => probotStore.workspaceId)
const tables = ref([])
const selTableList = computed(() => props.formData.tableList)

const addPlug = () => {
  editTName.value = null
  showBind.value = true
}

const removeItem = ({ id }) => {
  ElMessageBox.confirm(t('common.confirmDel'), 'Warning', {
    type: 'warning',
    confirmButtonText: t('common.yes'),
    cancelButtonText: t('common.no'),
    title: t('common.delete')
  })
    .then(async () => {
      delDb({ id }).then(({ code, message }) => {
        if (code == 0) {
          ElMessage.success('删除成功！')
          getList()
        } else {
          ElMessage.error(message)
        }
      })
    })
    .catch(() => {})
}
const editTName = ref(null)
const edit = (item) => {
  editTName.value = item.tableName
  showBind.value = true
}

const updateTables = () => {
  getList()
  // tables.value = tableList
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
// 当props的tableList 变化时候更新一下
watch(
  () => selTableList.value,
  (val) => {
    tables.value = tables.value.map((item) => {
      const checked = val?.find((bindItem) => bindItem.id == item.id) ? true : false
      return {
        ...item,
        checked
      }
    })
  },
  {
    immediate: true
  }
)

const getList = () => {
  getDbList({ workspaceId: workspaceId.value }).then((res) => {
    const list = res.data || []
    tables.value = list.map((item) => {
      return {
        ...item,
        name: item.tableName,
        desc: item.tableDesc,
        checked: selTableList.value?.find((bindItem) => bindItem.id == item.id) ? true : false,
        avatarUrl: '12'
      }
    })
  })
}
const changeCheck = (val, curI) => {
  if (val) {
    // 如果是选中了新的，则其他的所有的都不能被选中
    tables.value = tables.value.map((item) => {
      return {
        ...item,
        checked: curI.id == item.id ? true : false
      }
    })
  }
  ElMessage.warning('记得点击“保存Probots”按钮哦~')
}

onMounted(() => {
  if (workspaceId.value) {
    getList()
  }
})

watch(
  () => workspaceId.value,
  (val) => {
    if (val) {
      getList()
    }
  }
)

defineExpose({
  tables
})
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
      display: flex;
      align-items: center;
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
.check-box {
  margin-right: 20px;
  vertical-align: middle;
}
</style>
