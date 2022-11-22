<template>
	<div class="f-feedback-container">
		<el-input
			type="textarea"
			:rows="4"
			placeholder="请输入意见或建议"
			v-model="textarea">
		</el-input>
		<p class="info">注：反馈信息将会推送到研发人员飞书端</p>
		<div class="btns">
			<el-button :disabled="!textarea" type="primary" size="mini">提交</el-button>
		</div>
	</div>
</template>

<script lang="tsx">
import { defineComponent, reactive, toRefs } from "vue"
export default defineComponent({
  setup(){
		const state = reactive({
			textarea: ''
		})
		return {
			...toRefs(state)
		}
	},
})
</script>

<style scoped>
.f-feedback-container{

}
.f-feedback-container .btns {
	text-align: right;
}
.f-feedback-container .btns .el-button {
	padding: 4px 6px;
}
.f-feedback-container .info {
	padding-top: 4px;
	color: #ccc;
	font-size: 12px;
}
</style>
