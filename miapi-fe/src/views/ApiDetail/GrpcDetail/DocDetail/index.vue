<template>
  <div class="doc_detail_wrap">
		<h4>{{$i18n.t('instructionManual')}}</h4>
		<div class="doc_detail_container">
			<el-input v-if="isEditDetail" :rows="8" type="textarea" :placeholder="$i18n.t('placeholder.noDescription')" v-model="updateGrpcParam.apiRemark" autocomplete="off"></el-input>
			<el-input v-else readonly :rows="8" type="textarea" :placeholder="$i18n.t('placeholder.noDescription')" v-model="getApiInfo.apiRemark" autocomplete="off"></el-input>
		</div>
	</div>
</template>
<script>

import { mapGetters } from 'vuex'

export default {
  name: 'DocDetail',
  data () {
    return {
    }
  },
  props: {
    row: {
      type: Object,
      default () {
        return {}
      }
    }
  },
  computed: {
    ...mapGetters([
      'isEditDetail',
      'updateGrpcParam',
      'apiDetail'
    ]),
    getApiInfo () {
      return this.$store.getters.apiDetail ? this.$store.getters.apiDetail : {
        apiRemark: ""
      }
    }
  },
  mounted () {
  },
  methods: {
  }
}
</script>
<style lang="scss" scoped>
	.doc_detail_container{
		.empty{
			font-size: 14px;
			text-align: center;
			padding-bottom: 8px;
		}
		padding: 16px;
	}
</style>
