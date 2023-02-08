<template>
	<section class="import-json-container">
		<div :class="{'form-item': true, isEn: isEN}">
			<dl>
				<dt>{{$i18n.t('importMethod')}}:</dt>
				<dd>
					<el-select style="width: 244px" v-model="importMethodValue" disabled :placeholder="$i18n.t('placeholder.pleaseChoose')">
						<el-option
							disabled
							v-for="item in importMethod"
							:key="item.value"
							:label="item.label"
							:value="item.value">
						</el-option>
					</el-select>
				</dd>
			</dl>
			<span>{{$i18n.t('swaggerImportMethodInfo')}}</span>
		</div>
		<div :class="{'form-item': true, isEn: isEN}">
			<dl>
				<dt>{{$i18n.t('randomData')}}:</dt>
				<dd>
					<el-select style="width: 244px" v-model="randomGen">
						<el-option
							v-for="k in Object.keys(randomDataOption)"
							:key="k"
							:label="randomDataOption[k].label"
							:value="randomDataOption[k].value">
						</el-option>
					</el-select>
				</dd>
			</dl>
			<span>{{$i18n.t('randomDataInfo')}}</span>
		</div>
		<!-- <div :class="{'form-item': true, isEn: isEN}">
			<dl>
				<dt>选择分类:</dt>
				<dd>
					<el-select style="width: 244px" size="small" v-model="importGroup" placeholder="请选择分类">
						<el-option
							v-for="item in groupList"
							:key="item.groupID"
							:label="item.groupName"
							:value="item.groupID">
						</el-option>
					</el-select>
				</dd>
			</dl>
			<span>默认按照文件中的分类导入，文件中若无分类，则按照选择的分类导入</span>
		</div> -->
		<div :class="{'form-item': true, isEn: isEN}">
			<dl>
				<dt>{{$i18n.t('syncSettings')}}:</dt>
				<dd>
					<el-select style="width: 244px" v-model="forceUpdate" :placeholder="$i18n.t('placeholder.pleaseChoose')">
						<el-option
							v-for="item in importSet"
							:key="item.value"
							:label="item.label"
							:value="item.value">
						</el-option>
					</el-select>
				</dd>
			</dl>
			<span>{{$i18n.t('normalMode')}}：{{$i18n.t('notImportExistingInterfaces')}}。{{$i18n.t('fullCoverage')}}：{{$i18n.t('notImportExistingInterfacesTips')}}</span>
		</div>
		<div :class="{'form-item': true, isEn: isEN}">
			<dl>
				<dt style="align-self: flex-start">{{$i18n.t('dataUpload')}}:</dt>
				<dd>
					<el-upload
						action="./"
						:on-change="handleChange"
						drag
						:show-file-list="false"
						:multiple="false"
						:auto-upload="false"
						accept=".json">
						<el-icon color="#ccc" :size="72"><upload-filled /></el-icon>
						<div class="el-upload__text">
							<p>{{$i18n.t('clickDragUpload')}}</p>
							<p>{{$i18n.t('swaggerImportSupport2')}}</p>
						</div>
					</el-upload>
				</dd>
			</dl>
		</div>
		<el-dialog
			v-model="dialogVisible"
			:show-close="false"
			:close-on-press-escape="false"
			:close-on-click-modal="false"
			destroy-on-close
			custom-class="import-json-success-dialog"
			width="345px">
			<template #title><el-icon :size="16"><SuccessFilled /></el-icon><span>{{$i18n.t('importedSuccessfully')}}</span></template>
			<div class="import-json-success-dialog-info">
				{{$i18n.t('importInfoDialog.first')}}{{apiCount}}{{$i18n.t('importInfoDialog.sec')}}
			</div>
			<div class="btns">
				<el-button @click="handleLook">{{$i18n.t('btnText.check')}}</el-button>
				<el-button type="primary" @click="dialogVisible = false">{{$i18n.t('btnText.close')}}</el-button>
			</div>
		</el-dialog>
	</section>
</template>

<script>
import { mapGetters } from 'vuex'
import { importSwaggerApi } from '@/api/summary.import'
import { AJAX_SUCCESS_MESSAGE } from '@/views/constant'
import { PATH } from '@/router/constant'
import i18n from "@/lang"

const Random_Data = {
	"yes": {
		value: true,
		label: i18n.t("open"),
	},
	"no":  {
		value: false,
		label: i18n.t("close"),
	}
}

export default {
  name: 'ImportJson',
  data () {
    return {
      isEN: false,
      importGroup: undefined,
      importMethodValue: 'swagger',
      forceUpdate: 0,
			randomGen: Random_Data.yes.value,
			randomDataOption: Random_Data,
      importMethod: [{
        label: 'Swagger',
        value: 'swagger'
      }],
      importSet: [{
        label: this.$i18n.t('normalMode'),
        value: 0
      }, {
        label: this.$i18n.t('fullCoverage'),
        value: 1
      }],
      dialogVisible: false,
      apiCount: 0
    }
  },
  computed: {
    ...mapGetters([
      'groupList'
    ])
  },
  watch: {
    groupList: {
      handler (val) {
        if (val && val.length) {
          this.importGroup = val[0].groupID
        }
      },
      immediate: true,
      deep: true
    }
  },
  mounted () {
    this.isEN = this.$utils.languageIsEN()
  },
  methods: {
    handleChange (file) {
      let that = this
      let reader = new FileReader()
      reader.onload = function () {
        if (reader.result) {
          that.handleSubmit(reader.result)
        } else {
          this.$message.error(this.$i18n.t('errorMessage.fileReadFailed'))
        }
      }
      reader.onerror = function () {
        this.$message.error(this.$i18n.t('errorMessage.fileReadFailed'))
      }
      reader.readAsText(file.raw)
    },
    handleSubmit (swaggerData) {
      if (!swaggerData) {
        this.$message.error(this.$i18n.t('errorMessage.fileContentFormatIncorrect'))
        return
      }
      importSwaggerApi({
        swaggerData: swaggerData,
        projectID: this.$utils.getQuery('projectID'),
        forceUpdate: this.forceUpdate,
				randomGen: this.randomGen
      }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
      		this.apiCount = data.data
      		this.dialogVisible = true
        }
      }).catch(e => {})
    },
    handleLook () {
      let projectID = this.$utils.getQuery('projectID')
      this.$store.dispatch('apilist/groupList', projectID).then(() => {
      	this.$store.dispatch('apilist.group/getGroupViewList', projectID)
    		this.$router.push({ path: PATH.API, query: { projectID } })
      }).catch(e => {}).finally(() => {
        this.dialogVisible = false
      })
    }
  }
}
</script>
<style scoped>
.import-json-container {
	padding: 60px 40px 30px;
}
.import-json-container .form-item,
.import-json-container .form-item dl {
	display: flex;
	align-items: center;
	justify-content: flex-start;
}
.import-json-container .form-item {
	margin-bottom: 30px;
	flex-wrap: wrap;
}
.import-json-container .form-item.isEn {
	flex-direction: column;
	align-items: flex-start;
}
.import-json-container .form-item dl{
	margin-right: 16px;
}
.import-json-container .form-item.isEn dl {
	flex-direction: column;
	align-items: flex-start;
}
.import-json-container .form-item dl dt {
	font-size: 14px;
	color: rgba(0, 0, 0, 0.85);
	margin-right: 20px;
}
.import-json-container .form-item>span{
	display: inline-block;
	font-size: 12px;
	color: rgba(0, 0, 0, 0.4);
}
.import-json-container .form-item >>> .el-upload__text p {
	font-size: 14px;
	color: rgba(0, 0, 0, 0.65);
	margin: 10px 0 10px;
}
.import-json-container .form-item >>> .el-upload .el-upload-dragger {
	width: 616px;
	height: 230px;
}
.import-json-container .form-item >>> .el-upload__text p:last-child {
	color: rgba(0, 0, 0, 0.44);
}
.import-json-container >>> .import-json-success-dialog .el-dialog__header {
	font-size: 14px;
	color: rgba(0, 0, 0, 0.65);
	font-weight: bold;
	display: flex;
	align-items: center;
	justify-content: flex-start;
}
.import-json-container >>> .import-json-success-dialog .el-dialog__header i {
	font-size: 24px;
	color: #4ac74e;
	margin-right: 8px;
}
.import-json-container >>> .import-json-success-dialog .import-json-success-dialog-info {
	font-size: 14px;
	color: rgba(0, 0, 0, 0.43);
	padding-left: 26px;
}
.import-json-container >>> .import-json-success-dialog .btns {
	margin: 20px 0 0 0;
	text-align: right;
}
</style>
