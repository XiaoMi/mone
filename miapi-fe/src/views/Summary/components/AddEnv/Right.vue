<template>
	<section class="add-env-right-container">
		<div v-if="envData.id" class="env-base-info">
			<div :class="{'env-base-info-item': true, isEn: isEN}">
				<span>{{$i18n.t('environmentName')}}:</span>
				<el-input v-model="envData.envName" :placeholder="$i18n.t('placeholder.pleaseEnterEnvironmentName')"/>
			</div>
			<div :class="{'env-base-info-item': true, isEn: isEN}">
				<span>{{$i18n.t('environmentalDomainName')}}:</span>
				<el-input :placeholder="$i18n.t('placeholder.pleaseEnter')" v-model="url">
					<template #prepend>
            <el-select style="width: 90px" v-model="agreement">
              <el-option label="http://" value="http://"></el-option>
              <el-option label="https://" value="https://"></el-option>
            </el-select>
          </template>
				</el-input>
			</div>
		</div>
		<div v-if="envData.id" class="env-request-info">
			<el-tabs v-model="activeName">
				<el-tab-pane label="Headers" name="first"><Headers/></el-tab-pane>
			</el-tabs>
		</div>
		<div v-if="envData.id" class="bnts">
			<el-button @click="handleSubmit" type="primary">{{$i18n.t('btnText.save')}}</el-button>
		</div>
		<Empty v-else/>
	</section>
</template>
<script>
import Headers from './Headers.vue'
import { mapGetters } from 'vuex'
import Empty from '@/components/Empty'
import { addApiEnv, editApiEnv } from '@/api/apitest'
import { AJAX_SUCCESS_MESSAGE } from '@/views/constant'

export default {
  name: 'EnvRightContent',
  components: {
    Headers,
    Empty
  },
  computed: {
    ...mapGetters([
      'envData'
    ])
  },
  data () {
    return {
      isEN: false,
      activeName: 'first',
      agreement: 'http://',
      url: ''
    }
  },
  watch: {
    url (val) {
      this.$store.dispatch('addEnv/changeAddEnvData', {
        httpDomain: `${this.agreement}${val}`
      })
    },
    agreement (val) {
      this.$store.dispatch('addEnv/changeAddEnvData', {
        httpDomain: `${val}${this.url}`
      })
    },
    'envData.httpDomain': {
      handler (val) {
        if (val !== `${this.agreement}${this.url}`) {
          if (val.indexOf('//') !== -1) {
            this.url = val.split('//')[1]
            this.agreement = `${val.split('//')[0]}//`
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
      let projectID = this.$utils.getQuery('projectID')
      let headers = this.envData.headers.filter(v => !!v.headerName && !!v.headerValue)
      if (!this.envData.isAdd) {
        editApiEnv({
          id: this.envData.id,
          envName: this.envData.envName,
          httpDomain: this.envData.httpDomain,
          headers: JSON.stringify(headers),
          envDesc: '',
          projectID
        }).then((data) => {
          if (data.message === AJAX_SUCCESS_MESSAGE) {
            this.$message.success(this.$i18n.t('successfullyModified'))
            this.$store.dispatch('apitest/getHostEnvList', projectID)
          }
        }).catch(e => {})
      } else {
        addApiEnv({
          envName: this.envData.envName,
          httpDomain: this.envData.httpDomain,
          headers: JSON.stringify(headers),
          envDesc: '',
          projectID
        }).then((data) => {
          if (data.message === AJAX_SUCCESS_MESSAGE) {
            this.$message.success(this.$i18n.t('savedSuccessfully'))
            this.$store.dispatch('apitest/getHostEnvList', projectID)
          }
        }).catch(e => {})
      }
    }
  }
}
</script>
<style scoped>
.add-env-right-container {
	height: 100%;
	overflow-y: auto;
	padding: 30px 20px 20px 40px;
	width: calc(100% - 212px);
}
.add-env-right-container::-webkit-scrollbar{
	display: none;
}
.add-env-right-container .env-base-info .env-base-info-item {
	display: flex;
	align-items: center;
	justify-content: flex-start;
	margin-bottom: 30px;
}
.add-env-right-container .env-base-info .env-base-info-item.isEn {
  flex-direction: column;
  align-items: flex-start;
}
.add-env-right-container .env-base-info .env-base-info-item span {
	font-size: 14px;
	color: rgba(0, 0, 0, 0.85);
	margin-right: 8px;
	white-space: nowrap;
	display: inline-block;
}
.add-env-right-container .env-base-info .env-base-info-item .el-input{
	width: 460px;
}
.add-env-right-container .env-request-info {
	border: 1px solid #e6e6e6;
	width: 100%;
}
.add-env-right-container .env-request-info >>> .el-tabs__header {
	padding: 0 30px;
}
.add-env-right-container .env-request-info >>> .el-tabs__header .el-tabs__item {
	height: 46px;
	line-height: 46px;
}
.add-env-right-container .bnts {
	text-align: center;
	padding-top: 40px;
}
</style>
