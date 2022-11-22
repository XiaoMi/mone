<template>
	<div class="m-modify-user">
		<el-form label-position="left" label-width="auto">
			<el-row type="flex" justify="center">
				<el-col :span="18">
					<el-form-item :label="$i18n.t('userList.role')">
						<el-select style="width: 100%" class="user-list" :filterable="true" v-model="selectedRole" :placeholder="$i18n.t('placeholder.pleaseChoose')">
							<el-option
								v-for="item in roleOptions"
								:key="item.value"
								:label="item.name"
								:value="item.value">
							</el-option>
						</el-select>
					</el-form-item>
				</el-col>
			</el-row>
			<el-row type="flex" justify="center">
				<el-col :span="18">
					<el-form-item :label="$i18n.t('userList.name')">
						<el-select style="width: 100%" size="default" class="user-list" :filterable="true" v-model="value" multiple :placeholder="$i18n.t('placeholder.pleaseChoose')">
							<el-option
								v-for="item in options"
								:key="item.value"
								:label="item.label"
								:value="item.value">
							</el-option>
						</el-select>
					</el-form-item>
				</el-col>
			</el-row>
			<el-form-item>
				<div class="footer-btns">
					<el-button size="mini" @click="handleclose">{{$i18n.t('btnText.cancel')}}</el-button>
					<el-button :disabled="!value.length" size="mini" type="primary" @click="handleok">{{$i18n.t('btnText.ok')}}</el-button>
				</div>
			</el-form-item>
		</el-form>
	</div>
</template>

<script>

import { getAllPartnerList } from '@/api/projectuser'
import { AJAX_SUCCESS_MESSAGE } from '@/views/constant'

export default {
  name: 'ModifyUser',
  data () {
    return {
      options: [],
      value: [],
      selectedRole: undefined
    }
  },
  props: {
    userData: {
      type: Object,
      default () {
        return {}
      }
    },
    role: {
      type: Object,
      default () {
        return {}
      }
    }
  },
  watch: {
    selectedRole (val) {
      if (val) {
        this.value = this.userData[val].map(v => {
          return Object.keys(v)[0]
        })
      }
    }
  },
  computed: {
    roleOptions () {
      return Object.keys(this.role).map(v => {
        return {
          value: v,
          name: this.role[v]
        }
      })
    }
  },
  mounted () {
    getAllPartnerList().then((data) => {
      if (data.message === AJAX_SUCCESS_MESSAGE) {
        this.options = (data.data || []).map((item) => {
          return {
            value: Object.keys(item)[0],
            label: Object.values(item)[0]
          }
        })
      }
    }).catch(e => {})
  },
  methods: {
    handleok () {
      this.$emit('onSubmit', { selectedRole: this.selectedRole, value: this.value })
    },
    handleclose () {
      this.$emit('handleClose')
    }
  }
}
</script>

<style lang="scss" scoped>
	.m-modify-user .footer-btns {
    width: 100%;
		text-align: right;
	}
</style>
