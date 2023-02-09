<template>
	<div class="detail-testapi-container">
		<TestHeader/>
		<div id="test-wrap" class="test-wrap">
      <DubboRequest v-if="apiTestProtocol === protocol_type.Dubbo"/>
      <GRPCRequest v-else-if="apiTestProtocol === protocol_type.Grpc"/>
			<RequestComp v-else/>
      <X5Comp  v-if="apiTestProtocol === protocol_type.HTTP || apiTestProtocol === protocol_type.Gateway"/>
			<ReturnComp/>
		</div>
    <TestCase :isGlobal="false"/>
	</div>
</template>

<script>
import TestHeader from '@/views/TestApi/components/TestHeader'
import RequestComp from '@/views/TestApi/components/RequestComp'
import DubboRequest from '@/views/TestApi/components/DubboRequest'
import ReturnComp from '@/views/TestApi/components/ReturnComp'
import GRPCRequest from "@/views/TestApi/components/GRPCRequest"
import X5Comp from "@/views/TestApi/components/X5"
import { mapGetters } from 'vuex'
import { PROTOCOL_TYPE } from '@/views/constant'
import TestCase from "@/components/TestCase"

export default {
  name: 'ApiTest',
  components: {
    TestHeader,
    RequestComp,
    DubboRequest,
    ReturnComp,
    X5Comp,
    GRPCRequest,
    TestCase
  },
  computed: {
    ...mapGetters([
      'apiTestProtocol'
    ]),
    protocol_type () {
      return PROTOCOL_TYPE
    }
  },
  mounted () {
    this.$store.dispatch('apitest/changeApiTestTarget', { apiTestProtocol: String(this.$utils.getQuery('apiProtocol')) })
  }
}
</script>

<style scoped>
.detail-testapi-container .test-wrap {
	margin: 0 20px 0;
  padding-top: 20px;
	height: calc(100vh - 198px);
	overflow-y: auto;
}
.detail-testapi-container .test-wrap >>> .el-tabs__header {
  padding: 0;
}
.detail-testapi-container .test-wrap::-webkit-scrollbar {
  display: none;
}
</style>
