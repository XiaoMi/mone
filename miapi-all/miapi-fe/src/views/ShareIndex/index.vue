<template>
	<div class="share-index-container">
		<LeftMenu/>
		<Right/>
	</div>
</template>

<script>
import { getIndexPageInfo, getOuterIndexPageInfo } from '@/api/apiindex'
import LeftMenu from './LeftMenu.vue'
import { AJAX_SUCCESS_MESSAGE } from '@/views/constant'
import Right from './Right.vue'
import { mapGetters } from 'vuex'
import { isExtranet } from "@/utils"
export default {
  name: 'ShareIndex',
  components: {
    LeftMenu,
    Right
  },
  data () {
    return {

    }
  },
  beforeCreate () {
    let ids = this.$utils.getQuery('ids')
    if (!ids) {
      return
    }
    let arr = ids.split(',')
    if (!arr.length) {
      this.$message.error(this.$i18n.t('errorMessage.parameterError'))
      return
    }
    arr = arr.map(v => Number(v))
    if (isExtranet) {
      getOuterIndexPageInfo({
        indexIDs: JSON.stringify(arr)
      }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          this.$store.dispatch('shareindex/changePageInfoList', data.data)
          this.$store.dispatch('shareindex/changeInitPageInfoList', data.data)
        }
      }).catch(e => {})
    } else {
      getIndexPageInfo({
        indexIDs: JSON.stringify(arr),
        projectID: this.$utils.getQuery('project')
      }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          this.$store.dispatch('shareindex/changePageInfoList', data.data)
          this.$store.dispatch('shareindex/changeInitPageInfoList', data.data)
        }
      }).catch(e => {})
    }
  }
}
</script>

<style scoped>
.share-index-container{
	display: flex;
	align-items: flex-start;
	justify-content: flex-start;
}
</style>
