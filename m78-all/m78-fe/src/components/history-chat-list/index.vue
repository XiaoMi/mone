<template>
  <div class="chat-his-list-box" v-loading="loading">
    <p class="title">
      <span class="title-text">
        {{ t('excle.chatRecords') }}
      </span>
      <span @click="switchShow" class="expand-btn">
        <el-icon class="expand-icon"> <ArrowUp v-if="showList" /><ArrowDown v-else /></el-icon>
      </span>
    </p>
    <div class="list-box" v-if="showList">
      <div class="list">
        <p v-if="list.length == 0" class="empty-box">{{ t('excle.noChatRecords') }}</p>
        <div v-for="tag in list" :key="tag.id" v-else class="tag-item-box">
          <div class="tag-item">
            <span @click="clickTag(tag)">{{ tag.content }}</span>
            <el-dropdown
              class="dropdown-chat-item"
              @command="
                (command) => {
                  handleCommand(command, tag)
                }
              "
              size="small"
            >
              <i class="iconfont icon-shenglvehao more-icon"></i>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="edit">{{ t('common.edit') }}</el-dropdown-item>
                  <el-dropdown-item command="delete">{{ t('excle.delete') }}</el-dropdown-item>
                  <el-dropdown-item command="exportHttp">{{
                    t('excle.exportHttp')
                  }}</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </div>
    </div>
  </div>
  <el-dialog
    v-model="dialogVisible"
    :title="t(`excle.${dialogType == 'edit' ? 'editChatHis' : 'exportHttp'}`)"
    width="700"
  >
    <el-form ref="ruleFormRef" :model="ruleForm" label-position="left">
      <el-form-item
        :label="t(`excle.${dialogType == 'edit' ? 'content' : 'exportHttpName'}`)"
        prop="content"
        :rules="{
          required: true,
          message: t('excle.enterText'),
          trigger: 'blur'
        }"
      >
        <el-input v-model="ruleForm.content" style="width: 100%" />
      </el-form-item>
      <div class="conditons-div">
        <p class="conditons-title">{{ t('excle.conditions') }}</p>
        <div class="list">
          <div
            v-for="(condition, index) in ruleForm.conditions"
            :key="index"
            class="condition-item"
          >
            <el-form-item
              label="Key"
              :prop="'conditions.' + index + '.key'"
              :rules="{
                required: true,
                message: '请输入Key',
                trigger: 'blur'
              }"
            >
              <el-input v-model="condition.key" />
            </el-form-item>
            <el-form-item
              label="Value"
              :prop="'conditions.' + index + '.value'"
              :rules="{
                required: true,
                message: '请输入Value',
                trigger: 'blur'
              }"
            >
              <el-input v-model="condition.value" />
            </el-form-item>
            <div class="btns">
              <el-icon class="btn-icon" @click="removeCondition(index)"><CircleClose /></el-icon>
            </div>
          </div>
        </div>
      </div>

      <el-collapse v-model="activeNames" v-if="dialogType == 'edit'">
        <el-collapse-item :title="t('excle.advancedSettings')" name="1">
          <el-form-item label="" prop="mappingContent">
            <el-input v-model="ruleForm.mappingContent" type="textarea" autosize />
          </el-form-item>
        </el-collapse-item>
      </el-collapse>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">{{ t('excle.cancle') }}</el-button>
        <el-button type="primary" @click="addCondition">{{ t('excle.addConditions') }} </el-button>
        <el-button type="primary" @click="confirmEdit">{{ t('excle.confirm') }}</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { delChat, getChatInfoApi, updateChatInfoApi, createHttpApi } from '@/api/excle'
import { ElMessage } from 'element-plus'
import { t } from '@/locales'

const props = defineProps({
  uuid: {},
  list: {
    type: Array,
    default: () => []
  }
})
const dialogVisible = ref(false)
const emits = defineEmits(['selHis', 'delHisChatCb', 'editHisChatCb'])
const loading = ref(false)

const ruleForm = ref({
  content: '',
  mappingContent: '',
  conditions: [
    {
      key: '',
      value: ''
    }
  ]
})
const ruleFormRef = ref()
const activeNames = ref(1)
const switchShowDialog = () => {
  dialogVisible.value = !dialogVisible.value
}
const clickTag = (tag) => {
  emits('selHis', tag.content)
}
const closeTag = (tag) => {
  delChat(tag.id).then(({ code }) => {
    if (code != 0) return
    emits('delHisChatCb')
  })
}
const editTag = (tag) => {
  switchShowDialog()
  getChatInfoApi(tag.id).then(({ data, code, message }) => {
    if (code != 0) {
      ElMessage.error(message)
      return
    }
    const { conditions } = data
    const arr = conditions.map((item) => {
      const key = Object.keys(item)[0]
      return {
        key: key,
        value: item[key]
      }
    })
    ruleForm.value = {
      ...data,
      conditions: arr
    }

    ruleFormRef.value.resetFields()
  })
}
const editId = ref(null)
const exportHttp = (tag) => {
  editId.value = tag.id
  ruleFormRef.value?.resetFields()
  switchShowDialog()
}
const dialogType = ref('edit')
const handleCommand = (command, tag) => {
  if (command == 'delete') {
    closeTag(tag)
  } else if (command == 'edit' || command == 'exportHttp') {
    dialogType.value = command
    if (command == 'edit') {
      editTag(tag)
    } else {
      exportHttp(tag)
    }
  }
}
const editSubmit = () => {
  const params = {
    ...ruleForm.value,
    conditions: ruleForm.value.conditions.map((item) => {
      return {
        [item.key]: item.value
      }
    })
  }
  updateChatInfoApi(params).then(({ code, message }) => {
    if (code != 0) {
      ElMessage.error(message)
      return
    }
    emits('editHisChatCb')
    switchShowDialog()
    ElMessage.success(t('common.editSuccess'))
  })
}
const exporHttpSubmit = () => {
  const { content, conditions } = ruleForm.value
  const obj = {}
  conditions.forEach((item) => {
    obj[item.key] = item.value
  })
  const params = {
    name: content,
    reqData: obj,
    labelId: editId.value
  }
  createHttpApi(params).then(({ code, message }) => {
    if (code != 0) {
      ElMessage.error(message)
      return
    }
    emits('editHisChatCb')
    switchShowDialog()
    ElMessage.success(t('common.addSuccess'))
  })
}
const confirmEdit = () => {
  ruleFormRef.value.validate((valid) => {
    if (valid) {
      dialogType.value == 'edit' ? editSubmit() : exporHttpSubmit()
    } else {
      return false
    }
  })
}
const removeCondition = (index) => {
  ruleForm.value.conditions.splice(index, 1)
}
const addCondition = () => {
  ruleForm.value.conditions.push({
    key: '',
    value: ''
  })
}
const showList = ref(true)
const switchShow = () => {
  showList.value = !showList.value
}
</script>

<style scoped lang="scss">
.tag-item-box {
  display: inline-block;
}
.tag-item {
  cursor: pointer;
  margin: 0 7px 7px 0;
  color: #606060;
  border: solid 1px #80d4ff;
  background: #fff;
  white-space: pre-wrap;
  height: auto;
  line-height: 14px;
  padding: 3px 0 3px 3px;
  border-radius: 2px;
  display: flex;
  justify-content: space-between;
}
.more-icon {
  color: #909399;
  border-radius: 9px;
  outline: none;
  font-size: 12px;
  margin: 1px 3px 0 3px;
  color: #00a9ff;
}
.chat-his-list-box {
  margin-top: 10px;
  border: solid 1px #606060;
  border-radius: 5px;
}

.empty-box {
  text-align: center;
  color: #909399;
  font-size: 12px;
  padding: 20px 0;
}
.conditons-title {
  padding: 0 10px 10px 5px;
}
.condition-item {
  display: flex;
  justify-content: space-between;
  :deep(.oz-form-item) {
    flex: 1;
  }
}
.conditons-div {
  display: flex;
  align-items: baseline;
  .list {
    flex: 1;
  }
}
.btn-icon {
  font-size: 20px;
}
.list-box {
  padding: 0 10px;
  max-height: 150px;
  overflow: auto;
}
.title {
  height: 40px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 10px;
  .title-text,
  .expand-icon {
    font-size: 14px;
  }
}

.expand-btn {
  cursor: pointer;
  padding: 5px;
}
.btns {
  padding: 7px 0 0 7px;
}
</style>
