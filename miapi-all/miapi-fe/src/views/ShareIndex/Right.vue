<template>
	<div class="right-container">
    <ShareDocDetail v-if="shareShowIndexDoc.content"/>
		<template v-else-if="shareApiDetail.protocol">
      <RightTop/>
		  <RightContent/>
    </template>
    <template v-else>
      <Empty/>
    </template>
	</div>
</template>

<script>
import RightTop from './RightTop.vue'
import RightContent from './Content.vue'
import ShareDocDetail from './DocDetail.vue'
import { mapGetters } from 'vuex'
import Empty from "@/components/Empty"
export default {
  name: 'Right',
  components: {
    RightTop,
    RightContent,
    ShareDocDetail,
    Empty
  },
  computed: {
    ...mapGetters([
      'shareShowIndexDoc',
      'shareApiDetail'
    ])
  }
}
</script>
<style scoped>
.right-container{
  width: calc(100vw - 300px);
}
</style>
