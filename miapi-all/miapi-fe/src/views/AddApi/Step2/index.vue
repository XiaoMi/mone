<template>
	<div class="step-2">
		<div class="add-title add-request-params">
			<h4>{{addApiProtocol === protocolType.HTTP ? $i18n.t('requestParameter') : (addApiProtocol === protocolType.Dubbo ? $i18n.t('ApiClass.parameters') : $i18n.t('requestParameter'))}}</h4>
			<div class="add-content">
				<EditRequestParams v-if="addApiProtocol === protocolType.HTTP"/>
				<DubboRequestParams v-else-if="addApiProtocol === protocolType.Dubbo"/>
				<GatewayRequestParams v-else-if="addApiProtocol === protocolType.Gateway"/>
			</div>
		</div>
		<div class="add-title add-returns">
			<h4>{{$i18n.t('returnParameter')}}</h4>
			<div class="add-content">
				<EditReturnParam v-if="addApiProtocol === protocolType.HTTP"/>
				<DubboReturnParam v-else-if="addApiProtocol === protocolType.Dubbo"/>
				<GatewayReturnParam v-else-if="addApiProtocol === protocolType.Gateway"/>
			</div>
		</div>
		<div class="add-title add-remarks">
			<h4>{{$i18n.t('instructionManual')}}</h4>
			<div class="add-content">
				<DetailedDesc v-if="addApiProtocol === protocolType.HTTP"/>
				<DubboDetailedDesc v-else-if="addApiProtocol === protocolType.Dubbo"/>
				<GatewayDetailedDesc v-else-if="addApiProtocol === protocolType.Gateway"/>
			</div>
		</div>
		<div class="btns">
			<el-button @click="handleSubmit" type="primary">{{$i18n.t('btnText.submit')}}</el-button>
			<el-button @click="handleBack">{{$i18n.t('btnText.previous')}}</el-button>
		</div>
	</div>
</template>

<script>
import EditRequestParams from '@/views/ApiDetail/HttpDetail/RequestParam/EditRequestParam'
import EditReturnParam from '@/views/ApiDetail/HttpDetail/ReturnParam/EditReturnParam'
import DetailedDesc from '../HttpApi/DetailedDesc'

import DubboRequestParams from '@/views/ApiDetail/DubboDetail/RequestParam/EditRequestParam'
import DubboReturnParam from '@/views/ApiDetail/DubboDetail/ReturnParam/EditReturnParam'
import DubboDetailedDesc from '../DubboApi/DetailedDesc'

import GatewayRequestParams from '@/views/ApiDetail/GatewayDetail/RequestParam/EditRequestParam'
import GatewayReturnParam from '@/views/ApiDetail/GatewayDetail/ReturnParam/EditReturnParam'
import GatewayDetailedDesc from '../GatewayApi/DetailedDesc'
import { PROTOCOL_TYPE } from '@/views/constant'
import { mapGetters } from 'vuex'

export default {
  name: 'Step2',
  components: {
    EditRequestParams,
    EditReturnParam,
    DetailedDesc,
    DubboRequestParams,
    DubboReturnParam,
    DubboDetailedDesc,
    GatewayRequestParams,
    GatewayReturnParam,
    GatewayDetailedDesc
  },
  data () {
    return {}
  },
  computed: {
    ...mapGetters([
      'addApiProtocol'
    ]),
    protocolType () {
      return PROTOCOL_TYPE
    }
  },
  methods: {
    handleBack () {
      this.$store.dispatch('apilist.add/changeStep', 1)
    },
    handleSubmit () {
      this.$store.dispatch('apilist.add/handleSubmit')
    }
  }
}
</script>

<style scoped>
.step-2 {
}
.step-2 .add-title{
	background: #fff;
	margin-bottom: 10px;
}
.step-2 .add-content{
	padding: 0 20px 0;
}
.step-2 .add-title:last-child{
	margin-bottom: 0;
}
.step-2 .add-title h4 {
	padding: 16px;
	font-size: 16px;
}
.step-2 .btns {
	padding: 10px 20px 30px;
}
</style>
