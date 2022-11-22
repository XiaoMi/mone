<template>
	<div class="dele-project-dialog">
		<el-alert
			:title="$i18n.t('deleteDialogContent')"
			type="warning"
			:closable="false"
			class="dele-alert"
			show-icon>
		</el-alert>
		<p>{{$i18n.t('deleteConfirmOper')}}</p>
		<el-input v-model.trim="input" :placeholder="$i18n.t('placeholder.pleaseEnter')"/>
		<div class="btns">
			<el-button @click="$emit('onCancel')">{{$i18n.t('btnText.cancel')}}</el-button>
			<el-button :disabled="!input" @click="handleDelProject" type="primary">{{$i18n.t('btnText.ok')}}</el-button>
		</div>
	</div>
</template>
<script>
import { deleteProject } from '@/api/main'
import { PATH } from '@/router/constant'
import { AJAX_SUCCESS_MESSAGE } from '@/views/constant'
import { mapGetters } from 'vuex'

export default {
  name: 'DeleDialog',
  data () {
    return {
      input: ''
    }
  },
  computed: {
    ...mapGetters([
      'projectDetail'
    ])
  },
  methods: {
    handleDelProject () {
      if (this.projectDetail.projectName === this.input) {
        deleteProject({ projectID: this.$utils.getQuery('projectID') }).then((data) => {
          if (data.message === AJAX_SUCCESS_MESSAGE) {
            this.$emit('onCancel')
            this.$message.success(this.$i18n.t('successDeleted'))
            this.$router.push({ path: PATH.HOME })
          }
        }).catch(e => {})
      } else {
        this.$message.error(this.$i18n.t('errorMessage.projectNameNotMatch'))
      }
    }
  }
}
</script>
<style scoped>
.dele-project-dialog .dele-alert {
	border: 1px solid rgba(255, 229, 143, 1);
	border-radius: 4px;
	background: rgba(255, 251, 230, 1);
	color: rgba(0, 0, 0, 0.65);
	font-size: 14px;
	margin: 10px 0 20px;
}
.dele-project-dialog > p {
	font-size: 14px;
	color: rgba(0, 0, 0, 0.85);
	margin-bottom: 12px;
}
.dele-project-dialog .btns {
	text-align: right;
	margin-top: 20px;
}
</style>
