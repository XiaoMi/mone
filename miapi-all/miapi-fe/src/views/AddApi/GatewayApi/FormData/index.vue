<template>
	<div class="gateway-baseinfo-form">
		<el-row type="flex" justify="center" :gutter="20">
			<el-col :span="20">
				<el-form-item prop="name" :label="`${$i18n.t('ApiClass.name')}:`">
					<el-input :style="styleWidth" disabled :placeholder="`${$i18n.t('placeholder.pleaseEnter')}`" v-model="gatewayParam.name">
					</el-input>
				</el-form-item>
			</el-col>
		</el-row>

		<el-row type="flex" justify="center" :gutter="20">
			<el-col :span="20">
				<el-form-item :label="`${$i18n.t('appName')}:`">
					<el-input :style="styleWidth" disabled :placeholder="`${$i18n.t('placeholder.pleaseEnter')}`" v-model="gatewayParam.application">
					</el-input>
				</el-form-item>
			</el-col>
		</el-row>

		<el-row type="flex" justify="center" :gutter="20">
			<el-col :span="20">
				<el-form-item prop="httpMethod" :label="`${$i18n.t('ApiClass.requestName')}:`">
					<el-select :style="styleWidth" disabled v-model="gatewayParam.httpMethod" :placeholder="`${$i18n.t('placeholder.pleaseChoose')}`">
						<el-option v-for="(name, value) in requestType" :key="value" :label="name" :value="value"></el-option>
					</el-select>
				</el-form-item>
			</el-col>
		</el-row>

		<el-row type="flex" justify="center" :gutter="20">
			<el-col :span="20">
				<el-form-item :label="`API ${$i18n.t('description')}:`">
					<el-input :style="styleWidth" disabled type="textarea" :placeholder="`${$i18n.t('placeholder.pleaseEnter')}`" v-model="gatewayParam.description">
					</el-input>
				</el-form-item>
			</el-col>
		</el-row>

		<el-row type="flex" justify="center" :gutter="20">
			<el-col :span="20">
				<el-form-item prop="routeType" :label="`${$i18n.t('routingType')}:`">
					<el-select :style="styleWidth" disabled v-model="gatewayParam.routeType">
						<el-option v-for="key in Object.keys(routing_type)" :key="routing_type[key]" :label="routing_type[key]" :value="Number(key)"></el-option>
					</el-select>
				</el-form-item>
			</el-col>
		</el-row>

		<el-row v-if="gatewayParam.routeType===0" type="flex" justify="center" :gutter="20">
			<el-col :span="20">
				<el-form-item prop="path" :label="`${$i18n.t('path')}:`">
					<el-input :style="styleWidth" disabled :placeholder="`${$i18n.t('placeholder.pleaseEnter')}`" v-model="gatewayParam.path">
					</el-input>
				</el-form-item>
			</el-col>
		</el-row>

		<el-row v-if="gatewayParam.routeType!==0" type="flex" justify="center" :gutter="20">
			<el-col :span="20">
				<el-form-item prop="methodName" :label="`${$i18n.t('ApiClass.methodName')}:`">
					<el-input :style="styleWidth" disabled :placeholder="`${$i18n.t('placeholder.pleaseEnter')}`" v-model="gatewayParam.methodName">
					</el-input>
				</el-form-item>
			</el-col>
		</el-row>

		<el-row v-if="gatewayParam.routeType!==0" type="flex" justify="center" :gutter="20">
			<el-col :span="20">
				<el-form-item prop="serviceName" :label="`${$i18n.t('ApiClass.serviceName')}:`">
					<el-input :style="styleWidth" disabled type="textarea" :placeholder="`${$i18n.t('placeholder.pleaseEnter')}`" v-model="gatewayParam.serviceName">
					</el-input>
				</el-form-item>
			</el-col>
		</el-row>

		<el-row v-if="gatewayParam.routeType!==0" type="flex" justify="center" :gutter="20">
			<el-col :span="20">
				<el-form-item :label="`${$i18n.t('serviceGrouping')}:`">
					<el-input :style="styleWidth" disabled :placeholder="`${$i18n.t('placeholder.pleaseEnter')}`" v-model="gatewayParam.serviceGroup">
					</el-input>
				</el-form-item>
			</el-col>
		</el-row>

		<el-row v-if="gatewayParam.routeType!==0" type="flex" justify="center" :gutter="20">
			<el-col :span="20">
				<el-form-item :label="`${$i18n.t('serviceVersion')}:`">
					<el-input :style="styleWidth" disabled :placeholder="`${$i18n.t('placeholder.pleaseEnter')}`" v-model="gatewayParam.serviceVersion">
					</el-input>
				</el-form-item>
			</el-col>
		</el-row>

		<el-row type="flex" justify="center" :gutter="20">
			<el-col :span="20">
				<el-form-item :label="`${$i18n.t('overtimeTime')}:`">
					<el-input :style="styleWidth" disabled :placeholder="`${$i18n.t('placeholder.pleaseEnter')}`" v-model="gatewayParam.timeout">
						<template #append>ms</template>
					</el-input>
				</el-form-item>
			</el-col>
		</el-row>
	</div>
</template>
<script>
import { mapGetters } from 'vuex'
import { ROUTING_TYPE } from '@/views/constant'
export default {
  name: 'GatewayForm',
  data () {
    return {
      styleWidth: {
        width: '450px'
      }
    }
  },
  computed: {
    ...mapGetters([
      'gatewayParam'
    ]),
    requestType () {
      return {
        '0': 'POST',
        '1': 'GET'
      }
    },
    routing_type () {
      return ROUTING_TYPE
    }
  }
}
</script>
<style scoped>
.gateway-baseinfo-form >>> .el-row{
	margin-bottom: 10px;
}
</style>
