<template>
	<div class="api-list-parent">
		<div id="api-list-scroll" class="api-list-scroll">
			<div class="api-list-content">
				<TableList @turnPage="handleTurnPage"/>
			</div>
		</div>
	</div>
</template>
<script>
import TableList from '../TableList'
export default {
  name: 'ApiList',
  components: {
    TableList
  },
  methods: {
    handleTurnPage () {
      document.querySelector('#api-list-scroll').scrollTo({ top: 0 })
    }
  }
}
</script>
<style scoped>
.api-list-parent {
	padding: 0 20px 0;
  height: calc(100vh - 138px);
}
.api-list-parent .api-list-scroll {
	padding-top: 20px;
	height: 100%;
	overflow-y: auto;
}
.api-list-parent .api-list-content {
	background: #fff;
  padding: 20px;
	min-height: 100%;
}
.api-list-parent .api-list-scroll::-webkit-scrollbar{
	display: none;
}
</style>
