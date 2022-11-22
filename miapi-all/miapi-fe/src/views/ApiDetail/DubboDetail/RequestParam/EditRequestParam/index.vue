<template>
	<div class="request-wrap">
			<div class="request-content">
				<ReqParam/>
			</div>
	</div>
</template>

<script>
import ReqParam from './ReqParam'

export default {
  name: 'EditRequestParam',
  components: {
    ReqParam
  },
  data () {
    return {
    }
  },
  computed: {
  }
}
</script>

<style scoped>
.request-wrap .request-content {
	padding: 20px 0 0 0;
}
</style>
