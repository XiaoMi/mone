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
  }
}
</script>

<style scoped>
.request-wrap {
	padding:  20px 0 10px 0;
}
.request-wrap .request-content {
	padding: 20px 0 0 0;
}
</style>
