<template>
	<el-form :label-position="isEN ? 'top': 'left'" ref="ruleForm" :rules="rules" label-width="auto" :model="httpParam" >
		<el-row type="flex" justify="space-between" :gutter="20">
			<el-col :span="12">
				<el-form-item prop="apiStatus" :label="`${$i18n.t('ApiClass.apiStatus')}:`">
					<el-select style="width: 100%" v-model="httpParam.apiStatus" :placeholder="$i18n.t('placeholder.pleaseChoose')">
						<el-option v-for="v in Object.keys(apiStatus)" :key="v" :label="apiStatus[v]" :value="Number(v)"></el-option>
					</el-select>
				</el-form-item>
			</el-col>
			<el-col :span="12">
				<el-form-item prop="groupID" :label="`${$i18n.t('ApiClass.category')}:`">
					<el-select disabled style="width: 100%" v-model="httpParam.groupID" :placeholder="$i18n.t('placeholder.pleaseChoose')">
						<el-option v-for="item in groupOption" :key="item.groupID" :label="item.groupName" :value="item.groupID"></el-option>
					</el-select>
				</el-form-item>
			</el-col>
		</el-row>

		<el-row type="flex" justify="space-between" :gutter="20">
			<el-col :span="12">
				<el-form-item prop="apiName" :label="`${$i18n.t('ApiClass.name')}:`">
					<el-input :placeholder="$i18n.t('placeholder.pleaseEnterName')" v-model="httpParam.apiName" autocomplete="off"></el-input>
				</el-form-item>
			</el-col>
			<el-col :span="12">
				<el-form-item prop="apiURI" :label="`${$i18n.t('ApiClass.path')}:`">
					<el-input :placeholder="$i18n.t('placeholder.pleaseEnter')" v-model.trim="httpParam.apiURI">
						<template #prepend>
              <el-select style="width: 100px" v-model="httpParam.apiRequestType" :placeholder="$i18n.t('placeholder.pleaseChoose')">
                <el-option v-for="v in Object.keys(requestType)" :key="v" :label="requestType[v]" :value="Number(v)"></el-option>
              </el-select>
            </template>
					</el-input>
				</el-form-item>
			</el-col>
		</el-row>

		<el-row type="flex" justify="space-between" :gutter="20">
			<el-col :span="24">
				<el-form-item prop="apiDesc" :label="`${$i18n.t('ApiClass.apiDescription')}:`">
					<el-input type="textarea"  :placeholder="$i18n.t('placeholder.enterDesc')" v-model="httpParam.apiDesc"></el-input>
				</el-form-item>
			</el-col>
		</el-row>
	</el-form>
</template>

<script>
import { PROTOCOL, REQUEST_TYPE, API_STATUS, AJAX_SUCCESS_MESSAGE } from '@/views/constant'
import { mapGetters } from 'vuex'

export default {
  name: 'EditBaseInfo',
  data () {
    return {
      isEN: false,
      rules: {
        apiStatus: [
          { required: true, message: this.$i18n.t('placeholder.pleaseSelectStatus'), trigger: ['blur', 'change'] }
        ],
        groupID: [
          { required: true, message: this.$i18n.t('placeholder.pleaseSelectCategory'), trigger: ['blur', 'change'] }
        ],
        apiName: [
          { required: true, message: this.$i18n.t('placeholder.pleaseEnterName'), trigger: ['blur', 'change'] }
        ],
        apiURI: [
          { required: true, message: this.$i18n.t('placeholder.pleaseEnterPath'), trigger: ['blur', 'change'] },
          { validator: (_, value, cb) => {
            if (/(^\s+)|(\s+$)|\s+/g.test(value)) {
              cb(new Error(this.$i18n.t('errorMessage.pathError')))
            } else {
              cb()
            }
          },
          trigger: ['blur', 'change'] }
        ]
      }
    }
  },
  mounted () {
    this.isEN = this.$utils.languageIsEN()
    this.$store.dispatch('apilist.add/changeCanSave', this.$refs.ruleForm)
  },
  computed: {
    ...mapGetters([
      'detailGroupList',
      'httpParam'
    ]),
    apiStatus () {
      return API_STATUS
    },
    groupOption () {
      return this.detailGroupList.filter(item => item.groupID > 0) || []
    },
    requestType () {
      return REQUEST_TYPE
    },
    getGroupName () {
      let groupName = ''
      try {
        groupName = this.groupOption.filter(item => item.groupID === this.httpParam.groupID)[0].groupName
      } catch (error) {}
      return groupName
    }
  }
}
</script>

<style scoped>
.d-detail-headers .el-form {
	padding: 30px 60px 12px;
}
</style>
