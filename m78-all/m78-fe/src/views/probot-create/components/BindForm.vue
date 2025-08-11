<template>
  <el-dialog v-model="show" title="表绑定" width="1120" :before-close="beforeClose">
    <template #header>
      <div class="my-header">
        <h2>表绑定</h2>
        <GenByAi :workspaceId="workspaceId" @genFinish="genFinish" ref="GenByAiRef" />
      </div>
    </template>
    <el-form ref="formRef" :model="form" label-position="top">
      <el-form-item
        prop="tableName"
        label="数据表名称"
        :rules="[
          {
            required: true,
            message: '数据表名称必填',
            trigger: 'blur'
          }
        ]"
      >
        <el-input v-model="form.tableName" :disabled="editTName" />
      </el-form-item>
      <el-form-item prop="tableDesc" label="数据表描述">
        <el-input v-model="form.tableDesc" :autosize="{ minRows: 3, maxRows: 5 }" type="textarea" />
      </el-form-item>
      <el-form-item prop="demo" label="示例">
        <el-input v-model="form.demo" :autosize="{ minRows: 3, maxRows: 5 }" type="textarea" />
      </el-form-item>
      <ul class="list-ul">
        <li v-for="(item, i) in form?.columnInfoList" :key="i" class="list-li">
          <el-form-item
            :prop="'columnInfoList.' + i + '.name'"
            :rules="{
              required: true,
              message: '存储字段名必填',
              trigger: 'blur'
            }"
          >
            <template #label v-if="i == 0">
              存储字段名称
              <el-tooltip placement="bottom" width="200" effect="light">
                <template #content>
                  <div class="tooltip-save">
                    <p class="desc">
                      定义存储表格的“表头”。开发者定义好存储字段后，用户可在对应字段下存储相关数据。以下为一个读书笔记相关的存储字段示例：
                    </p>
                    <el-table
                      :data="[{ name: 'd' }, { name: 'd' }, { name: 'd' }]"
                      style="width: 100%"
                      size="small"
                    >
                      <el-table-column prop="name" label="Book Title (Sample filed)" width="200" />
                      <el-table-column
                        prop="Chapter"
                        label="Book Title (Sample filed)"
                        width="200"
                      />
                      <el-table-column prop="address" label="Notes (Sample filed)" />
                    </el-table>
                  </div>
                </template>
                <el-icon><Warning /></el-icon>
              </el-tooltip>
            </template>
            <el-input
              v-model="item.name"
              :autosize="{ minRows: 3, maxRows: 5 }"
              style="width: 180px"
            />
          </el-form-item>
          <el-form-item :prop="'columnInfoList.' + i + '.desc'">
            <template #label v-if="i == 0">
              描述
              <el-tooltip
                effect="light"
                content="对存储字段的补充说明，可以是对存储字段的自然语言描述、示例数据，也可以是格式说明等。 如，书名使用《XXX》"
              >
                <el-icon><Warning /></el-icon>
              </el-tooltip>
            </template>
            <el-input
              v-model="item.desc"
              :autosize="{ minRows: 3, maxRows: 5 }"
              style="width: 300px"
            />
          </el-form-item>
          <el-form-item
            :prop="'columnInfoList.' + i + '.type'"
            :rules="{
              required: true,
              message: '数据类型必填',
              trigger: 'blur'
            }"
          >
            <template #label v-if="i == 0">
              数据类型
              <el-tooltip
                effect="light"
                content="选择存储字段对应的数据类型，Bot 将按照开发者选择的数据类型，对用户输入的内容进行处理和保存"
              >
                <el-icon><Warning /></el-icon>
              </el-tooltip>
            </template>
            <DataTypeSel v-model="item.type" />
          </el-form-item>
          <el-form-item prop="desc" style="width: 250px">
            <template #label v-if="i == 0">
              是否必要
              <el-tooltip
                effect="light"
                raw-content
                content="必要字段：用户在保存一行数据时，必须提供对应字段信息，否则无法保存该行数据</br>非必要字段：缺失该字段信息时，一行数据仍可被保存在表中"
              >
                <el-icon><Warning /></el-icon>
              </el-tooltip>
            </template>
            <el-switch
              v-model="item.necessary"
              style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949"
            />
          </el-form-item>
          <el-form-item prop="desc" style="width: 250px">
            <template #label v-if="i == 0">
              是否为主键
              <el-tooltip effect="light" raw-content content="只能选择一列">
                <el-icon><Warning /></el-icon>
              </el-tooltip>
            </template>
            <el-switch
              v-model="item.primaryKey"
              style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949"
              @change="
                (val) => {
                  changePri(i, val)
                }
              "
            />
          </el-form-item>
          <el-form-item prop="desc" :label="i == 0 ? '操作' : ''" style="width: 120px">
            <el-button :icon="Delete" link @click="delList(i)"></el-button>
          </el-form-item>
        </li>
        <li>
          <el-button type="primary" @click="addList" style="width: 250px">+ 新增</el-button>
        </li>
      </ul>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="show = false">取消</el-button>
        <el-button type="primary" @click="confirm" :loading="loading"> 确定 </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, watch, ref, nextTick } from 'vue'
import { updateFormInfo } from '@/api/probot.ts'
import { getDbDetail } from '@/api/probot-db.ts'
import { createDb } from '@/api/probot-db.ts'
import DataTypeSel from './DataTypeSel'
import { Delete } from '@element-plus/icons-vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import GenByAi from './GenByAi'

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true
  },
  workspaceId: {},
  editTName: {}
})
const loading = ref(false)
const GenByAiRef = ref(null)
const emits = defineEmits(['update:modelValue', 'update'])
const form = ref({
  tableName: '',
  columnInfoList: [{ primaryKey: false, necessary: false }],
  tableDesc: ''
})
const formRef = ref(null)

const genFinish = (data) => {
  form.value.tableName = data.tableName
  form.value.tableDesc = data.tableDesc
  form.value.demo = data.demo
  form.value.columnInfoList = data.columnInfoList || []
}
const addList = () => {
  form.value.columnInfoList.push({
    necessary: false,
    primaryKey: false
  })
}
const delList = (i) => {
  form.value.columnInfoList.splice(i, 1)
}
const show = computed({
  get: () => props.modelValue,
  set: (val) => emits('update:modelValue', val)
})

const beforeClose = (done) => {
  GenByAiRef.value?.hidePop()
  nextTick(() => {
    done()
  })
}

const route = useRoute()

const confirm = async () => {
  const valid = await formRef.value.validate()
  if (!valid) return
  loading.value = true
  const params = {
    tableDesc: form.value.tableDesc,
    columnInfoList: form.value.columnInfoList,
    tableName: form.value.tableName,
    botId: route.params.id,
    demo: form.value.demo,
    workspaceId: props.workspaceId,
    id: form.value.id
  }
  const reqFnName = !props.editTName ? createDb : updateFormInfo
  reqFnName(params)
    .then(
      ({ code, message }) => {
        if (code == 0) {
          show.value = false
          ElMessage.success(`${!props.editTName ? '创建' : '编辑'}成功`)
          emits('update')
        } else {
          ElMessage.error(message || '失败')
        }
      },
      (e) => {
        console.log(e)
        ElMessage.error('失败')
      }
    )
    .finally(() => {
      loading.value = false
    })
}

//  编辑回显
const getInit = () => {
  formRef?.value?.resetFields()
  form.value.columnInfoList = [{ primaryKey: false, necessary: false }]
  if (props.editTName) {
    getDbDetail({ tableName: props.editTName }).then((res) => {
      form.value = res.data
      formRef?.value?.clearValidate()
    })
  }
}

// 处理primaryKey
const changePri = (i, val) => {
  if (val) {
    form.value.columnInfoList = form.value.columnInfoList.map((ele, index) => {
      return {
        ...ele,
        primaryKey: index != i ? false : true
      }
    })
  }
}

watch(
  () => show.value,
  (val) => {
    if (!val) return
    getInit()
  },
  {
    immediate: true
  }
)
</script>

<style lang="scss" scoped>
.my-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.pop-btn {
  margin-right: 10px;
}

.list-li {
  display: flex;
  :deep(.oz-form-item + .oz-form-item) {
    margin-left: 20px;
  }
}
.list-ul {
  border: solid 1px #eee;
  padding: 20px;
}
.tooltip-save {
  width: 610px;
  padding: 20px;
  :deep(.oz-table__body-wrapper .cell) {
    color: #fff;
  }
  .desc {
    font-size: 15px;
  }
}
</style>
