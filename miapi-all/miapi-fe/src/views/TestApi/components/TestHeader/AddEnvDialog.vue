<template>
	<div class="add-env-dialog">
		<el-form ref="addEnvForm" hide-required-asterisk :label-position="isEN ? 'top': 'left'" label-width="auto" :model="{env,url,agreement,desc}" :rules="rules">
			<el-row type="flex" justify="center">
				<el-col :span="21" :pull="1">
					<el-form-item prop="env" :label="`${$i18n.t('environmentName')}:`">
						<el-input v-model="env" :placeholder="$i18n.t('placeholder.pleaseEnterEnvironmentName')"/>
					</el-form-item>
				</el-col>
			</el-row>
			<el-row type="flex" justify="center">
				<el-col :span="21" :pull="1">
					<el-form-item prop="url" :label="`${$i18n.t('environmentalDomainName')}:`">
						<el-input :placeholder="$i18n.t('placeholder.pleaseEnter')" v-model="url">
							<template #prepend>
                <el-select style="width: 76px" v-model="agreement">
                  <el-option label="http" value="http"></el-option>
                  <el-option label="https" value="https"></el-option>
                </el-select>
              </template>
						</el-input>
					</el-form-item>
				</el-col>
			</el-row>
			<!-- <el-row type="flex" justify="center">
				<el-col :span="21" :pull="1">
					<el-form-item label="环境备注:">
						<el-input v-model="desc" :rows="3" type="textarea" size="small" placeholder="请输入环境备注(选填)"/>
					</el-form-item>
				</el-col>
			</el-row> -->
		</el-form>
		<div class="btns">
			<el-button @click="$emit('onCancel')">{{$i18n.t('btnText.cancel')}}</el-button>
			<el-button type="primary" @click="handleSubmit">{{$i18n.t('btnText.ok')}}</el-button>
		</div>
	</div>
</template>

<script>
import { addApiEnv, editApiEnv } from '@/api/apitest'
import { AJAX_SUCCESS_MESSAGE } from '@/views/constant'
export default {
  name: 'AddEnvDialog',
  data () {
    return {
      isEN: false,
      env: undefined,
      url: undefined,
      agreement: 'http',
      desc: '',
      rules: {
        env: [
          { required: true, message: this.$i18n.t('placeholder.pleaseEnterEnvironmentName'), trigger: 'blur' }
        ],
        url: [
          { required: true, message: this.$i18n.t('placeholder.pleaseEnterAddress'), trigger: 'blur' },
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
  props: {
    defaultEnv: {
      type: Object,
      default () {
        return {
          envDesc: undefined,
          envName: undefined,
          httpDomain: "",
          id: 0,
          projectId: 0
        }
      }
    }
  },
  watch: {
    defaultEnv: {
      handler (val) {
        if (val && val.id) {
          let arr = []
          if (val.httpDomain.indexOf('//') !== -1) {
            arr = val.httpDomain.split('//')
          }
          this.env = val.envName
          this.desc = val.envDesc
          if (arr.length) {
            this.url = arr[1]
            this.agreement = arr[0].split(':')[0]
          }
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
    handleSubmit () {
      this.$refs.addEnvForm.validate((valid) => {
        if (valid) {
          if (this.defaultEnv && this.defaultEnv.id) {
            editApiEnv({
              id: this.defaultEnv.id,
              envName: this.env,
              httpDomain: `${this.agreement}://${this.url}`,
              envDesc: this.desc,
              projectID: this.$utils.getQuery('projectID')
            }).then((data) => {
              if (data.message === AJAX_SUCCESS_MESSAGE) {
                this.$emit('onCancel')
                this.$emit('onOk')
              }
            }).catch(e => {})
          } else {
            addApiEnv({
              envName: this.env,
              httpDomain: `${this.agreement}://${this.url}`,
              envDesc: this.desc,
              projectID: this.$utils.getQuery('projectID')
            }).then((data) => {
              if (data.message === AJAX_SUCCESS_MESSAGE) {
                this.$emit('onCancel')
                this.$emit('onOk')
              }
            }).catch(e => {})
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.add-env-dialog {
	padding-top: 14px;
}
.add-env-dialog .btns {
	text-align: right;
}
</style>
