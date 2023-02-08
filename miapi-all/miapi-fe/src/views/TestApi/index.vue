<template>
	<div class="testapi-container">
		<TestHeader :isGlobal="true"/>
		<div id="test-wrap" class="test-wrap">
      <DubboRequest v-if="apiTestProtocol === protocol_type.Dubbo"/>
      <GRPCRequest v-else-if="apiTestProtocol === protocol_type.Grpc"/>
			<RequestComp v-else-if="apiTestProtocol === protocol_type.Gateway || apiTestProtocol === protocol_type.HTTP"/>
      <X5Comp  v-if="apiTestProtocol === protocol_type.HTTP || apiTestProtocol === protocol_type.Gateway"/>
			<ReturnComp/>
		</div>
    <TestCase :isGlobal="true"/>
	</div>
</template>

<script>
import TestHeader from './components/TestHeader'
import RequestComp from './components/RequestComp'
import ReturnComp from './components/ReturnComp'
import DubboRequest from './components/DubboRequest'
import { mapGetters } from 'vuex'
import { PROTOCOL_TYPE } from '@/views/constant'
import GRPCRequest from "./components/GRPCRequest"
import X5Comp from "./components/X5"
import TestCase from "@/components/TestCase"

export default {
  name: 'TestApi',
  components: {
    TestHeader,
    RequestComp,
    ReturnComp,
    DubboRequest,
    GRPCRequest,
    X5Comp,
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
  beforeCreate () {
    this.$store.dispatch('apitest/resetApiTest')
    let serviceName = decodeURIComponent(this.$utils.getQuery('serviceName'))
    if (this.$utils.getQuery('serviceName')) {
      let arr = []
      if (serviceName.indexOf(':') !== -1) {
        arr = serviceName.split(':')
      } else {
        arr.push(serviceName)
      }
      let obj = {
        isRequestEnd: new Date().getTime(),
        interfaceName: arr[1] || arr[0] ,
        apiTestProtocol: PROTOCOL_TYPE.Dubbo
      }
      if (arr.length === 4) {
        obj.version = arr[2]
        obj.group = arr[3]
      } else if (/\d/.test(arr[2])) {
        obj.version = arr[2]
        obj.group = ''
      } else {
        obj.group = arr[2]
        obj.version = ''
      }
      this.$store.dispatch('apitest/changeApiTestTarget', obj)
    }
  },
  beforeUnmount () {
    this.$store.dispatch('apitest/changeApiTestTarget', { apiTestProtocol: undefined })
  }
}
</script>

<style scoped>
.testapi-container .test-wrap {
	margin: 0 20px 0;
	height: calc(100vh - 166px);
	overflow-y: auto;
  padding-top: 20px;
}
.testapi-container .test-wrap::-webkit-scrollbar {
  display: none;
}
</style>
