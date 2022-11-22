<template>
  <div class="doc_detail_wrap">
		<h4><el-icon><Reading /></el-icon>{{$i18n.t('instructionManual')}}</h4>
		<div class="doc_detail_container">
			{{getApiInfo.apiRemark || "无"}}
		</div>
	</div>
</template>
<script>

export default {
  name: 'DocDetail',
  data () {
    return {
    }
  },
  props: {
    getApiInfo: {
      type: Object,
      default () {
        return {
          dubboApiBaseInfo: {}
        }
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
		margin: 10px 20px 16px;
    border: 1px solid #f1f0f0;
    padding: 12px;
    font-size: 14px;
	}
</style>
