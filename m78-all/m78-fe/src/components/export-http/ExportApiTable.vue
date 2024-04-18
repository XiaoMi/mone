<template>
  <CommonHistory
    @closeHistory="closeHistory"
    :historyData="{
      title: t('excle.exportApi')
    }"
  >
    <template #content>
      <div class="list-box-container" v-loading="loading">
        <div v-if="tableData.length > 0" class="list-container">
          <ul class="list-box">
            <li v-for="item in tableData" :key="item.id" class="list-item">
              <span :type="retType(item)" size="small" link class="tag-btn">
                {{ item.name }}
              </span>
              <div class="btn-box">
                <el-tooltip effect="dark" :content="t('common.copy')" placement="top">
                  <el-button link size="small" @click="copyItem(item)" type="primary">
                    <el-icon class="action-icon">
                      <CopyDocument />
                    </el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip effect="dark" :content="t('common.delete')" placement="top">
                  <el-button link size="small" @click="deleteItem(item)" type="primary">
                    <el-icon class="action-icon">
                      <Delete />
                    </el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip effect="dark" :content="t('common.edit')" placement="top">
                  <el-button link size="small" @click="editItem(item)" type="primary">
                    <el-icon class="action-icon">
                      <Edit />
                    </el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip effect="dark" :content="t('excle.testQuery')" placement="top">
                  <el-button link size="small" @click="testItem(item)" type="primary">
                    <i class="iconfont icon-tiaoshi action-icon"></i>
                  </el-button>
                </el-tooltip>
              </div>
            </li>
          </ul>
        </div>
        <p v-else class="center-class margin-top-10">{{ t('common.noData') }}</p>
      </div>
    </template>
  </CommonHistory>
  <CreateExportApi v-model="showDialog" :itemId="itemId" @update="init" />
  <el-drawer title="调试" v-model="showTest" size="700px">
    <JsonView v-model="jsonData" />
  </el-drawer>
</template>
<script setup lang="ts">
import CommonHistory from '@/components/CommonHistory.vue'
import { getApiList, delHttpApi, testHttpApi } from '@/api/excle'
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { computed } from 'vue'
import { useExcleStore } from '@/stores/doc'
import { t } from '@/locales'
import CreateExportApi from './CreateExportApi'
import 'element-plus/theme-chalk/src/message-box.scss'
import JsonView from './JsonView.vue'
import { copyToClip } from '@/utils/copy'

const docStore = useExcleStore()
const docStatusArr = computed(() => docStore.value)

const retType = (listItem) => {
  const filer = docStatusArr.value.filter((item) => listItem.status === item.code)
  if (filer.length > 0) {
    return filer[0]?.tagType
  } else {
    return 'danger'
  }
}
const showDialog = ref(false)
const itemId = ref(null)
const props = defineProps({
  dataType: {
    type: String,
    default: 'history'
  }
})
const loading = ref(false)
const emits = defineEmits(['closeFn', 'delItemSuccess'])
const closeHistory = () => {
  emits('closeFn')
}
const tableData = ref([])

const showTest = ref(false)

const init = () => {
  loading.value = true
  getApiList()
    .then(({ code, data }) => {
      if (code != 0) return
      if (data && Array.isArray(data)) {
        tableData.value = data
      } else {
        data = []
      }
    })
    .finally(() => {
      loading.value = false
    })
}
const delApi = (row) => {
  loading.value = true
  delHttpApi(row.id)
    .then((res) => {
      if (res.code != 0) return
      ElMessage.success(t('common.deleteSuccess'))
      emits('delItemSuccess', row.id)
      init()
    })
    .catch(() => {
      ElMessage.error(t('common.deleteFail'))
    })
    .finally(() => {
      loading.value = false
    })
}
const deleteItem = (row) => {
  ElMessageBox.confirm(t('common.confirmDel'), 'Warning', {
    type: 'warning',
    confirmButtonText: t('common.yes'),
    cancelButtonText: t('common.no'),
    title: t('common.delete')
  }).then(async () => {
    delApi(row)
  })
}
const editItem = (row) => {
  itemId.value = row.id
  showDialog.value = true
}
const jsonData = ref()
const testItem = (row) => {
  testHttpApi(row).then(({ data }) => {
    jsonData.value = data
    showTest.value = true
  })
}

const copyItem = async (row) => {
  try {
    await copyToClip(row.curl)
    ElMessage.success(t('common.copySuccess'))
  } catch {
    ElMessage.error(t('common.copyError'))
  }
}
onMounted(() => {
  init()
})
defineExpose({
  init
})
</script>
<style lang="scss" scoped>
.list-box-container {
  height: calc(100% - 37px);
  .list-container {
    height: calc(100% - 20px);
    overflow: auto;
  }
  .list-box {
    border: solid 1px #eee;
    border-radius: 3px;
  }
}
.list-item {
  padding: 0 5px;
  border-bottom: solid 1px #eee;
  height: 40px;
  line-height: 40px;
  display: flex;
  .btn-box {
    display: inline-block;
    vertical-align: top;
  }
}
.list-item:hover {
  background: #f4f4f4;
}
.list-item:last-child {
  border: none;
}

.center-class {
  padding: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  border: solid 1px #dcdfe6;
  border-radius: 3px;
  color: #909399;
}
.tag-btn {
  flex: 1;
  font-size: 14px;
  border: none;
  cursor: not-allowed;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.action-icon {
  font-size: 15px;
}
</style>
