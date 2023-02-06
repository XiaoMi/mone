<template>
	<div class="step-1">
		<el-form :label-position="isEN ? 'top': 'right'" :rules="rules" label-width="86px" :model="{apiProtocol}" >
			<el-row type="flex" justify="center" :gutter="20">
				<el-col class="api-sort" :span="20">
					<el-form-item prop="apiProtocol" :label="`${$i18n.t('ApiClass.type')}:`">
						<el-radio-group v-model="apiProtocol">
							<el-radio-button v-for="key in Object.keys(protocol)" :key="key" :label="key">{{protocol[key]}}</el-radio-button>
						</el-radio-group>
					</el-form-item>
				</el-col>
			</el-row>
		</el-form>
		<HttpBaseInfo v-if="apiProtocol === protocolType.HTTP"/>
		<DubboBaseInfo v-else-if="apiProtocol === protocolType.Dubbo"/>
		<GatewayBaseInfo v-else-if="apiProtocol === protocolType.Gateway"/>
		<GrpcBaseInfo v-else-if="apiProtocol === protocolType.Grpc"/>
	</div>
</template>

<script>
import HttpBaseInfo from '../HttpApi/BaseInfo'
import DubboBaseInfo from '../DubboApi/BaseInfo'
import GatewayBaseInfo from '../GatewayApi/BaseInfo'
import { PROTOCOL, REQUEST_TYPE, PROTOCOL_TYPE } from '@/views/constant'
import GrpcBaseInfo from '../GrpcApi/BaseInfo'
import { mapGetters } from 'vuex'

export default {
  name: 'Step1',
  components: {
    HttpBaseInfo,
    DubboBaseInfo,
    GatewayBaseInfo,
    GrpcBaseInfo
  },
  data () {
    return {
      apiProtocol: undefined,
      isEN: false,
      rules: {
        apiProtocol: [
          { required: true, message: '请输选择API类型', trigger: ['blur', 'change'] }
        ]
      }
    }
  },
  computed: {
    ...mapGetters([
      'groupID',
      'groupList',
      'addApiProtocol',
      'httpParam'
    ]),
    protocol () {
      return PROTOCOL
    },
    requestType () {
      return REQUEST_TYPE
    },
    protocolType () {
      return PROTOCOL_TYPE
    },
    projectID () {
      return this.$utils.getQuery('projectID')
    }
  },
  watch: {
    apiProtocol: {
      handler (val) {
        if (!val) {
          return
        }
        // 修改各个对象中的api类型
        if (val === PROTOCOL_TYPE.HTTP) {
          this.$store.dispatch('apilist.add/changeHttpParam', { apiProtocol: val })
        }
        this.$store.dispatch('apilist.add/changeAddApiProtocol', val)
      },
      immediate: true
    }
  },
  beforeMount () {
    this.isEN = this.$utils.languageIsEN()
    this.apiProtocol = this.addApiProtocol
  }
}
</script>

<style scoped>
.step-1 {
	padding: 40px 0 20px;
}
.step-1 .api-sort >>> .el-form-item__label-wrap{
	margin-left: 6px;
}
.step-1 >>> .el-form>.el-row {
  margin-bottom: 10px;
}
.step-1 >>> .el-radio-group>label {
  min-width: 110px;
}
.step-1 >>> .el-radio-group>label.is-active .el-radio-button__inner{
  color: #1890FF;
  background: #fff;
}
.step-1 >>> .el-radio-group>label .el-radio-button__inner{
  width: 100%;
}
</style>
