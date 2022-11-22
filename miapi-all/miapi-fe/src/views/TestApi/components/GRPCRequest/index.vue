<template>
	<div class="grpc-request-container">
		<h5>{{$i18n.t('apiTest.requestConfiguration')}}</h5>
		<el-form :label-position="isEN ? 'top': 'left'" label-width="auto">
      <el-row>
				<el-col :span="10" :offset="1">
					<el-form-item :label="`${$i18n.t('overtimeTime')}:`">
            <el-input :min="0" v-model.number="grpcParams.timeout">
              <template #append>ms</template>
            </el-input>
					</el-form-item>
				</el-col>
			</el-row>
			<el-row>
				<el-col :span="10" :offset="1">
					<el-form-item :label="`${$i18n.t('serviceAddress')}:`">
						<div class="grpc-port">
							<el-input v-model="grpcParams.addrs" placeholder='eg:127.0.0.1:8080,10.11.111.111:8999'/>
							<span class="grpc-prot-tips">{{$i18n.t('multipleAddressesWillRandomlySelectOneCall')}}</span>
						</div>
					</el-form-item>
				</el-col>
			</el-row>
			<el-row>
				<el-col :span="16" :offset="1">
					<el-form-item>
						<template #label>
							<div>
								<p style="text-align: right">{{$i18n.t('parameterBody')}}:</p>
								<p>(JSON)</p>
							</div>
						</template>
						<GRPCResponseJson/>
					</el-form-item>
				</el-col>
			</el-row>
		</el-form>
	</div>
</template>

<script>
import GRPCResponseJson from './json.vue'
import { mapGetters } from 'vuex'
export default {
  name: 'GRPCRequest',
  components: {
    GRPCResponseJson
  },
  computed: {
    ...mapGetters([
      'grpcParams'
    ])
  },
  data () {
    return {
      isEN: false
    }
  },
  mounted () {
    this.isEN = this.$utils.languageIsEN()
  }
}
</script>

<style scoped>
.grpc-request-container {
	background: #fff;
	margin-bottom: 20px;
}
.grpc-request-container h5 {
	font-weight: normal;
	padding: 14px 20px;
	border-bottom: 1px solid #e6e6e6;
	margin-bottom: 20px;
}
.grpc-request-container .grpc-port{
	width: 100%;
}
.grpc-request-container .grpc-port span.grpc-prot-tips {
	display: inline-block;
	width: 700px;
  font-size: 12px;
  color: rgba(0, 0, 0, 0.25);
}
</style>
