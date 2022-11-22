<template>
	<div class="add-api-wrap">
		<div class="add-api-container">
			<div class="step-wrap">
				<ul>
					<li :class="{active: addApiStep === 1}">
						<span>1</span>
						<strong>{{$i18n.t('basicInformation')}}</strong>
						<div class="line"></div>
					</li>
					<li :class="{active: addApiStep === 2}">
						<span>2</span>
						<strong>{{$i18n.t('requestData')}}</strong>
						<div class="line"></div>
					</li>
					<li>
						<span>3</span>
						<strong>{{$i18n.t('finish')}}</strong>
					</li>
				</ul>
			</div>
			<div class="form-container">
				<Step1 v-if="addApiStep === 1"/>
				<Step2 v-else />
			</div>
		</div>
	</div>
</template>

<script>
import Step1 from './Step1'
import Step2 from './Step2'
import { PROTOCOL_TYPE } from '@/views/constant'
import { mapGetters } from 'vuex'

export default {
  name: 'AddApi',
  components: {
    Step1,
    Step2
  },
  data () {
    return {
      apiProtocol: ''
    }
  },
  mounted () {
    this.apiProtocol = this.$utils.getQuery('apiProtocol')
  },
  computed: {
    ...mapGetters([
      'addApiStep',
      'addApiProtocol'
    ]),
    protocol_type () {
      return PROTOCOL_TYPE
    }
  }
}
</script>

<style scoped>
.add-api-wrap {
	height: calc(100vh - 128px);
	overflow-y: auto;
}
.add-api-wrap::-webkit-scrollbar{
	display: none;
}
.add-api-container {
	margin: 20px 20px 0;
}
.add-api-container .step-wrap {
	margin-bottom: 20px;
	background: #fff;
	border-radius: 3px;
	height: 80px;
	display: flex;
	align-items: center;
	justify-content: center;
}
.add-api-container .step-wrap ul {
	display: flex;
	align-items: center;
	justify-content: flex-start;
}
.add-api-container .step-wrap ul li {
	display: flex;
	align-items: center;
	justify-content: flex-start;
}
.add-api-container .step-wrap ul li span{
	display: inline-block;
	width: 32px;
	height: 32px;
	border-radius: 50%;
	color: rgba(0, 0, 0, 0.24);
	font-size: 16px;
	border: 1px solid rgba(0, 0, 0, 0.24);
	line-height: 30px;
	text-align: center;
}
.add-api-container .step-wrap ul li strong{
	font-size: 16px;
	padding: 0 16px 0 8px;
	color: rgba(0, 0, 0, 0.44);
	font-weight: normal;
}
.add-api-container .step-wrap ul li .line {
	width: 248px;
	height: 1px;
	background: rgba(0, 0, 0, 0.15);
	margin-right: 16px;
}
.add-api-container .step-wrap ul li.active span{
	color: #fff;
	background: #108EE9;
	border-color: #108EE9;
}
.add-api-container .step-wrap ul li.active strong{
	color: rgba(0, 0, 0, 0.84);
	font-weight: bold;
}
.add-api-container .form-container {
	background: #fff;
	border-radius: 3px;
}
</style>
