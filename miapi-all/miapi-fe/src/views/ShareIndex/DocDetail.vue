<template>
  <div class="share-doc-container">
		<div class="share-doc-wrap">
			<h4>{{$i18n.t('title')}}: {{shareShowIndexDoc.title}}</h4>
			<MarkdownDoc :content="shareShowIndexDoc.content"/>
		</div>
	</div>
</template>
<script>

import MarkdownDoc from '@/components/MarkdownDoc'
import { mapGetters } from 'vuex'

export default {
  name: 'ShareDocDetail',
  components: { MarkdownDoc },
  computed: {
    ...mapGetters([
      'shareShowIndexDoc'
    ])
  }
}
</script>
<style lang="scss" scoped>
.share-doc-container {
	padding: 20px 20px 0;
}
.share-doc-container .share-doc-wrap{
	background: #fff;
	height: calc(100vh - 86px);
	overflow-y: auto;
	padding: 20px;
}
.share-doc-container .share-doc-wrap::-webkit-scrollbar{
	display: none;
}
.share-doc-container .share-doc-wrap h4 {
	border-bottom: 1px solid #EBEEF5;
	padding-bottom: 10px;
	font-size: 14px;
}
</style>
