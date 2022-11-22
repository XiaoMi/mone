<template>
	<div class="x5-container">
		<el-form :label-position="isEN ? 'top': 'right'" inline label-width="auto" :model="X5Param">
			<el-row :gutter="20">
				<el-col :span="24">
					<el-form-item prop="useX5Filter" :label="$i18n.t('useX5Protocol')">
						<el-checkbox v-model="X5Param.useX5Filter"></el-checkbox>
					</el-form-item>
				</el-col>
			</el-row>
			<el-row v-if="X5Param.useX5Filter" :gutter="20">
				<el-col :span="24">
					<el-form-item prop="appID" label="appID:">
						<el-input v-model="X5Param.appID" placeholder="appID"></el-input>
					</el-form-item>
				</el-col>
			</el-row>
			<el-row v-if="X5Param.useX5Filter" :gutter="20">
				<el-col :span="24">
					<el-form-item prop="appkey" label="appkey:">
						<el-input v-model="X5Param.appkey" placeholder="appkey"></el-input>
					</el-form-item>
				</el-col>
			</el-row>
      <el-row v-if="X5Param.useX5Filter" :gutter="20">
				<el-col :span="24">
					<el-form-item prop="x5Method" label="method:">
						<el-input v-model="X5Param.x5Method" placeholder="method">
              <!-- <template #append>
                (选填)
              </template> -->
            </el-input>
            <span class="optional">(选填)</span>
					</el-form-item>
				</el-col>
			</el-row>
		</el-form>
	</div>
</template>

<script>
import { mapGetters } from "vuex"
export default {
  name: "X5Auth",
  data () {
    return {
      isEN: false,
      rules: {
        appID: [
          { required: true, message: this.$i18n.t('errorMessage.mustBeNumber'), trigger: ['blur', 'change'] },
          { validator: (_, value, cb) => {
            if (!/\d/g.test(value)) {
              cb(new Error(this.$i18n.t('errorMessage.mustBeNumber')))
            } else {
              cb()
            }
          },
          trigger: ['blur', 'change'] }
        ]
      }
    }
  },
  beforeMount () {
    this.isEN = this.$utils.languageIsEN()
  },
  computed: {
    ...mapGetters([
      'X5Param'
    ])
  },
  watch: {
    "X5Param.useX5Filter": {
      handler (val) {
        if (!val) {
          this.$store.dispatch('apitest/changeX5Param', {
            appID: undefined,
            appkey: undefined,
            x5Method: undefined
          })
        }
      },
      immediate: true,
      deep: true
    }
  }
}
</script>
<style scoped>
.x5-container {
	margin: 20px 20px 0;
}
.x5-container >>> .el-input {
	width: 300px;
}
.x5-container .optional {
  color: #938e8e;
  padding-left: 10px;
}
</style>
