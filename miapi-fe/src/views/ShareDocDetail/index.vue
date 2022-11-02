<template>
	<div class="share-doc-container watermarked">
		<DocDetail v-if="row.documentID" :row="row"/>
	</div>
</template>

<script>
import DocDetail from '@/views/Summary/components/DocList/DocDetail'
import drawWaterMark from '@/common/waterMark'
import { mapGetters } from 'vuex'
export default {
  name: 'ShareDocDetail',
  components: {
    DocDetail
  },
  data () {
    return {
      row: {
        documentID: 0
      }
    }
  },
  computed: {
    ...mapGetters([
      'selfUserInfo'
    ])
  },
  watch: {
    'selfUserInfo.name': {
      handler (val) {
        if (val) {
          const option = {
            content: val,
            className: 'watermarked'
          }
          drawWaterMark(option)
        }
      },
      immediate: true,
      deep: true
    }
  },
  mounted () {
    this.row.documentID = this.$utils.getQuery('documentID')
    this.row.projectName = this.$utils.getQuery('projectName')
  }
}
</script>
<style scoped>
.share-doc-container {
	background: #fff;
	height: 100%;
}
</style>