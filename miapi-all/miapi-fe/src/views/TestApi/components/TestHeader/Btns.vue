<template>
	<div class="case-test-btns">
		<el-dropdown split-button type="primary" @click="$emit('onTest')">
			{{$i18n.t('btnText.test')}}
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item>
            <el-button :disabled="!selectCaseId" @click="handleSave" text type="primary" size="small">{{$i18n.t('btnText.save')}}</el-button>
          </el-dropdown-item>
          <el-dropdown-item>
            <el-button @click="dialogVisible = true" text type="primary" size="small">{{$i18n.t('btnText.saveAs')}}</el-button>
          </el-dropdown-item>
          <el-dropdown-item v-show="getShowApply">
            <el-tooltip effect="dark" :content="$i18n.t('dubboApplyInfo')" placement="left">
              <el-button :disabled="!apiTestInterfaceName" @click="handleApply" text type="primary" size="small">{{$i18n.t('btnText.apply')}}</el-button>
            </el-tooltip>
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
		</el-dropdown>
		<el-dialog
			:title="$i18n.t('SelectSampleGroup')"
			custom-class="select-group-dialog"
			append-to-body
			destroy-on-close
			v-model="dialogVisible"
			:close-on-press-escape="false"
			:close-on-click-modal=" false"
			width="480px">
			<div>
				<div class="save-case-name">
					<el-form ref="saveDialogName" :model="{caseName}">
            <el-form-item
              prop="caseName"
              :rules="[
                { required: true, message: $i18n.t('placeholder.pleaseEnterUseCaseName'), trigger: ['blur', 'change'] }
              ]"
            >
					    <el-input v-model.trim="caseName" :placeholder="$i18n.t('placeholder.pleaseEnterUseCaseName')"/>
            </el-form-item>
          </el-form>
				</div>
				<el-button class="new-add-btn" @click="handleAddGroup" text type="primary" size="small"><el-icon :size="12"><Plus /></el-icon>{{$i18n.t("createNewGroup")}}</el-button>
				<div class="test-case-content">
					<el-tree
						v-if="list.length"
						v-loading="loading"
						:data="list"
						:props="defaultProps"
						node-key="caseGroupId"
						show-checkbox
						highlight-current
						check-on-click-node
						@check-change="handleCheckChange"
						ref="treeForm"
						accordion>
					</el-tree>
					<div v-else class="empty-group">
						<el-empty>
							<template #description>
                <span>{{$i18n.t('noTestCaseGroup')}}</span>
              </template>
						</el-empty>
					</div>
				</div>
			</div>
			<div class="dialog-footer">
				<el-button @click="dialogVisible = false">{{$i18n.t('btnText.cancel')}}</el-button>
				<el-button :loading="loading" :disabled="!list.length || !selectGroup.caseGroupId || !caseName" type="primary" @click="handleSaveAs">{{$i18n.t('btnText.ok')}}</el-button>
			</div>
		</el-dialog>
	</div>
</template>
<script>
import createCaseGroup from "@/common/createCaseGroup"
import { getCasesByApi, getCasesByProject, saveTestCaseDir } from "@/api/apitest"
import { AJAX_SUCCESS_MESSAGE, PROTOCOL_TYPE } from "@/views/constant"
import { mapGetters } from "vuex"
export default {
  name: "Btns",
  data () {
    return {
      dialogVisible: false,
      loading: false,
      selectGroup: {},
      caseName: "",
      list: [],
      defaultProps: {
        children: 'caseList',
        label: 'caseGroupName',
        id: "caseGroupId"
      }
    }
  },
  computed: {
    ...mapGetters([
      'selectCaseId',
      'apiTestProtocol',
      'apiTestInterfaceName'
    ]),
    getShowApply () {
      return `${this.apiTestProtocol}` === PROTOCOL_TYPE.Dubbo
    }
  },
  props: {
    isGlobal: {
      type: Boolean,
      default: false
    }
  },
  watch: {
    dialogVisible: {
      handler (val) {
        if (val) {
          this.selectGroup = {}
          this.caseName = ""
          this.getList()
          this.$nextTick(() => {
            this.$refs.saveDialogName.validateField("caseName");
          })
        }
      },
      immediate: true
    }
  },
  methods: {
    handleCheckChange (data, checked, indeterminate) {
      if (checked) {
        this.selectGroup = data
        this.$refs.treeForm.setCheckedKeys([data.caseGroupId])
      }
      if (!this.$refs.treeForm.getCheckedKeys().length) {
        this.selectGroup = {}
      }
    },
    handleApply () {
      this.$emit('onApply')
    },
    handleSave () {
      this.$emit('onSave')
    },
    handleSaveAs () {
      this.$emit('onSaveAs', { ...this.selectGroup, caseName: this.caseName })
      this.dialogVisible = false
    },
    handleAddGroup () {
      let that = this
      let apiId = that.$utils.getQuery("apiID")
      let param = {
        projectId: that.$utils.getQuery("projectID"),
        globalCase: that.isGlobal
      }
      if (apiId) {
        param.apiId = apiId
      }
      createCaseGroup(function (name) {
        param.name = name
        saveTestCaseDir(param).then((data) => {
          if (data.message === AJAX_SUCCESS_MESSAGE) {
            that.getList()
          } else {
            that.$message.error(data.message)
          }
        }).catch(e => {})
      })
    },
    getList () {
      this.loading = true
      if (this.isGlobal) {
        return getCasesByProject({
          projectId: this.$utils.getQuery("projectID")
        }).then((data) => {
          if (data.message === AJAX_SUCCESS_MESSAGE) {
            this.list = (data.data || []).map(v => {
              return {
                ...v,
                caseList: []
              }
            })
          } else {
            this.$message.error(data.message)
          }
        }).catch(e => {}).finally(() => {
          this.loading = false
        })
      }
      return getCasesByApi({
        apiId: this.$utils.getQuery("apiID"),
        projectId: this.$utils.getQuery("indexProjectID") || this.$utils.getQuery("projectID")
      }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          this.list = (data.data || []).map(v => {
            return {
              ...v,
              caseList: []
            }
          })
        } else {
          this.$message.error(data.message)
        }
      }).catch(e => {}).finally(() => {
        this.loading = false
      })
    }
  }
}
</script>
<style scoped>
.el-dropdown {
	vertical-align: middle;
	margin-top: 1px;
}
.case-test-btns >>> .el-button-group {
	display: flex;
  margin-top: -1px;
}
.select-group-dialog .empty-group {
	display: flex;
	align-items: center;
	justify-content: center;
}
.select-group-dialog .new-add-btn {
	margin-left: 22px;
}
.select-group-dialog .test-case-content {
	max-height: 300px;
	overflow-y: auto;
  padding: 10px 0;
}
.select-group-dialog .test-case-content::-webkit-scrollbar{
	display: none;
}
.select-group-dialog .save-case-name {
	margin: 0 20px 10px;
}
.select-group-dialog .dialog-footer {
  text-align: right;
}
</style>
