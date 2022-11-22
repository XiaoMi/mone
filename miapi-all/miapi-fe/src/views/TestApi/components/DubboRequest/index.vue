<template>
	<div class="dubbo-request-container">
		<h5>{{$i18n.t('apiTest.requestConfiguration')}}</h5>
		<el-form :label-position="isEN ? 'top': 'right'" label-width="auto">
			<el-row>
				<el-col :span="10" :offset="1">
					<el-form-item :label="`${$i18n.t('serviceVersion')}:`">
						<el-input v-model="version" placeholder="eg:1.0.1"/>
					</el-form-item>
				</el-col>
			</el-row>
      <el-row>
				<el-col :span="10" :offset="1">
					<el-form-item :label="`${$i18n.t('overtimeTime')}:`">
            <el-input :min="0" v-model.number="timeout">
              <template #append>ms</template>
            </el-input>
					</el-form-item>
				</el-col>
			</el-row>
      <el-row>
				<el-col :span="10" :offset="1">
					<el-form-item :label="`${$i18n.t('resetNum')}:`">
						<el-input v-model.number="retries" :placeholder="$i18n.t('placeholder.pleaseEnterNumberRetries')"/>
					</el-form-item>
				</el-col>
			</el-row>
			<el-row>
				<el-col :span="10" :offset="1">
					<el-form-item :label="`${$i18n.t('apiTest.ipPort')}:`">
						<div class="dubbo-port">
							<el-input v-model="addr" placeholder="eg:ip:port"/>
							<span class="dubbo-prot-tips">{{$i18n.t('needDirectionallyDefaultNacosService')}}</span>
						</div>
					</el-form-item>
				</el-col>
			</el-row>
      <el-row>
				<el-col :span="10" :offset="1">
					<el-form-item :label="`${$i18n.t('grouping')}:`">
						<el-input v-model="group" :placeholder="$i18n.t('placeholder.PleaseFillGroup')"/>
					</el-form-item>
				</el-col>
			</el-row>
			<el-row>
				<el-col :span="16" :offset="1">
					<el-form-item :label="`${$i18n.t('table.parameterType')}:`">
						<el-input v-model="paramType" placeholder='eg:["int","java.util.list","java.lang.String"]'/>
					</el-form-item>
				</el-col>
			</el-row>
      <el-row>
				<el-col :span="16" :offset="1">
					<el-form-item :label="`${$i18n.t('genericParameter')}:`">
						<el-checkbox v-model="isGenParam"></el-checkbox>
					</el-form-item>
				</el-col>
			</el-row>
			<el-row>
				<el-col :span="16" :offset="1">
					<el-form-item>
						<template #label>
              <p style="text-align: right">{{$i18n.t('apiTest.parameterList')}}:</p>
							<p>({{$i18n.t('apiTest.jsonArray')}})</p>
            </template>
						<DubboResponseJson/>
					</el-form-item>
				</el-col>
			</el-row>
      <el-row>
				<el-col :span="16" :offset="1">
					<el-form-item :label="`${$i18n.t('whetherCarryAttachment')}:`">
						<el-switch
              v-model="attachment"
              active-color="#108EE9"
              inactive-color="#ccc">
            </el-switch>
					</el-form-item>
				</el-col>
			</el-row>
      <el-row v-if="attachment">
				<el-col :span="20" :offset="1">
					<el-form-item label="attachment:">
						<Attachment/>
					</el-form-item>
				</el-col>
			</el-row>
      <el-row>
				<el-col :span="16" :offset="1">
					<el-form-item :label="`${$i18n.t('carryTag')}:`">
						<el-switch
              v-model="hasTag"
              active-color="#108EE9"
              inactive-color="#ccc">
            </el-switch>
					</el-form-item>
				</el-col>
			</el-row>
      <el-row v-if="hasTag">
				<el-col :span="10" :offset="1">
					<el-form-item label="tag:">
						<el-input v-model="dubboTag" :placeholder="$i18n.t('placeholder.pleaseEnter')"/>
					</el-form-item>
				</el-col>
			</el-row>
		</el-form>
	</div>
</template>

<script>
import DubboResponseJson from './json.vue'
import { mapGetters } from 'vuex'
import Attachment from './attachment.vue'
export default {
  name: 'DubboRequest',
  components: {
    DubboResponseJson,
    Attachment
  },
  computed: {
    ...mapGetters([
      'apiTestVersion',
      'apiTestAddr',
      'apiTestRetries',
      'apiTestParamType',
      'apiTestDubboTimeout',
      'apiTestIsGenParam',
      'hasAttachment',
      'dubboTestTag',
      'apiTestGroup'
    ])
  },
  props: {
    isGlobal: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      isEN: false,
      version: '',
      addr: '',
      paramType: '',
      group: undefined,
      timeout: 5000,
      retries: 1,
      isGenParam: false,
      attachment: false,
      hasTag: false,
      dubboTag: undefined
    }
  },
  watch: {
    hasTag: {
      handler (val, old) {
        if (val !== old) {
          this.dubboTag = undefined
        }
      }
    },
    dubboTag: {
      handler (val, old) {
        if (val !== old) {
          this.$store.dispatch('apitest/changeApiTestTarget', { dubboTag: val })
        }
      },
      immediate: true
    },
    dubboTestTag: {
      handler (val, old) {
        if (val !== old) {
          this.dubboTag = val
        }
      },
      immediate: true
    },
    hasAttachment: {
      handler (val, old) {
        if (val !== old) {
          this.attachment = val
        }
      },
      immediate: true
    },
    apiTestParamType: {
      handler (val, old) {
        if (val !== old) {
          this.paramType = val
        }
      },
      immediate: true
    },
    apiTestIsGenParam: {
      handler (val, old) {
        if (val !== old) {
          this.isGenParam = val
        }
      },
      immediate: true
    },
    apiTestRetries: {
      handler (val, old) {
        if (val !== old) {
          this.retries = val
        }
      },
      immediate: true
    },
    apiTestAddr: {
      handler (val, old) {
        if (val !== old) {
          this.addr = val
        }
      },
      immediate: true
    },
    apiTestVersion: {
      handler (val, old) {
        if (val !== old) {
          this.version = val
        }
      },
      immediate: true
    },
    apiTestGroup: {
      handler (val, old) {
        if (val !== old) {
          this.group = val
        }
      },
      immediate: true
    },
    apiTestDubboTimeout: {
      handler (val, old) {
        if (val !== old) {
          this.timeout = val
        }
      },
      immediate: true
    },
    attachment: {
      handler (val, old) {
        if (val !== old) {
    			this.$store.dispatch('apitest/changeApiTestTarget', { hasAttachment: val })
        }
      },
      immediate: true
    },
    isGenParam: {
      handler (val, old) {
        if (val !== old) {
    			this.$store.dispatch('apitest/changeApiTestTarget', { isGenParam: val })
        }
      },
      immediate: true
    },
    timeout: {
      handler (val, old) {
        if (val !== old) {
    			this.$store.dispatch('apitest/changeApiTestTarget', { timeout: val })
        }
      },
      immediate: true
    },
    version: {
      handler (val, old) {
        if (val !== old) {
    			this.$store.dispatch('apitest/changeApiTestTarget', { version: val })
        }
      },
      immediate: true
    },
    retries: {
      handler (val, old) {
        if (val !== old) {
    			this.$store.dispatch('apitest/changeApiTestTarget', { retries: val })
        }
      },
      immediate: true
    },
    addr: {
      handler (val, old) {
        if (val !== old) {
    			this.$store.dispatch('apitest/changeApiTestTarget', { addr: val })
        }
      },
      immediate: true
    },
    group: {
      handler (val, old) {
        if (val !== old) {
    			this.$store.dispatch('apitest/changeApiTestTarget', { group: val })
        }
      },
      immediate: true
    },
    paramType: {
      handler (val, old) {
        if (val !== old) {
    			this.$store.dispatch('apitest/changeApiTestTarget', { paramType: val })
        }
      },
      immediate: true
    }
  },
  mounted () {
    this.isEN = this.$utils.languageIsEN()
  }
}
</script>

<style scoped>
.dubbo-request-container {
	background: #fff;
	margin-bottom: 20px;
}
.dubbo-request-container h5 {
	font-weight: normal;
	padding: 14px 20px;
	border-bottom: 1px solid #e6e6e6;
	margin-bottom: 20px;
}
.dubbo-request-container .dubbo-port{
  width: 100%;
}
.dubbo-request-container .dubbo-port span.dubbo-prot-tips {
	display: inline-block;
	width: 700px;
  font-size: 12px;
  color: rgba(0, 0, 0, 0.25);
}
</style>
