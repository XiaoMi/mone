<template>
	<el-form :label-position="isEN ? 'top': 'left'" ref="ruleForm" :rules="rules" label-width="auto" :model="updateGrpcParam" >
		<el-row type="flex" justify="space-between" :gutter="20">
			<el-col :span="16">
				<el-form-item prop="apiDesc" :label="`${$i18n.t('serviceDescription')}:`">
					<el-input type="textarea"  :placeholder="$i18n.t('placeholder.enterDesc')" v-model="updateGrpcParam.apiDesc"></el-input>
				</el-form-item>
			</el-col>
		</el-row>
	</el-form>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'EditBaseInfo',
  data () {
    return {
      isEN: false,
      rules: {
        apiPath: [
          { required: true, message: this.$i18n.t('placeholder.pleaseEnterPath'), trigger: ['blur', 'change'] }
        ]
      }
    }
  },
  mounted () {
    this.isEN = this.$utils.languageIsEN()
    this.$store.dispatch('apilist.add/changeCanSave', this.$refs.ruleForm)
  },
  computed: {
    ...mapGetters([
      'updateGrpcParam'
    ])
  }
}
</script>

<style scoped>
.d-detail-headers .el-form {
	padding: 30px 60px 12px;
}
</style>
