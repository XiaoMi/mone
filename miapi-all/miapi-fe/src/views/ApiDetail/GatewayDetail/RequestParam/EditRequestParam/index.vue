<template>
	<div class="request-wrap">
		 <el-radio-group v-model="type" size="small">
				<el-radio-button :label="radioTypes.QUERY"></el-radio-button>
				<el-radio-button :label="radioTypes.HEADER"></el-radio-button>
			</el-radio-group>
			<div class="request-content">
				<ReqParam v-if="type === radioTypes.QUERY"/>
				<ReqHeader v-else-if="type === radioTypes.HEADER"/>
			</div>
	</div>
</template>

<script>
import ReqParam from './ReqParam'
import ReqHeader from './ReqHeader'

const RADIO_TYPES = {
  QUERY: 'Query',
  HEADER: 'Header'
}

export default {
  name: 'EditRequestParam',
  components: {
    ReqParam,
    ReqHeader
  },
  data () {
    return {
      type: RADIO_TYPES.QUERY
    }
  },
  computed: {
    radioTypes () {
      return RADIO_TYPES
    }
  }
}
</script>

<style scoped>
.request-wrap .request-content {
	padding: 20px 0 0 0;
}
</style>
