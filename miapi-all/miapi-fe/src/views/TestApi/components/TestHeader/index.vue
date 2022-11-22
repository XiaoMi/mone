<template>
	<DubboHeader :isGlobal="isGlobal" v-if="apiTestProtocol === protocol_type.Dubbo"/>
  <GRPCHeader :isGlobal="isGlobal" v-else-if="apiTestProtocol === protocol_type.Grpc"/>
	<HttpHeader :isGlobal="isGlobal" v-else/>
</template>

<script>
import { PROTOCOL_TYPE } from '@/views/constant'
import HttpHeader from './HttpHeader.vue'
import DubboHeader from './DubboHeader.vue'
import GRPCHeader from './GRPCHeader.vue'
import { mapGetters } from 'vuex'

export default {
  name: 'TestHeader',
  components: {
    HttpHeader,
    GRPCHeader,
    DubboHeader
  },
  props: {
    isGlobal: {
      type: Boolean,
      default: false
    }
  },
  computed: {
    ...mapGetters([
      'apiTestProtocol'
    ]),
    protocol_type () {
      return PROTOCOL_TYPE
    }
  }
}
</script>

<style scoped>
.test-header-container {
	padding: 10px 20px;
	background: #fff;
	border-bottom: 1px solid #e6e6e6;
  background: rgba(230, 247, 255, 1);
}
</style>
