<template>
  <div class="add-remark">
		<el-input :rows="8" type="textarea" :placeholder="`${$i18n.t('placeholder.pleaseEnter')}`" v-model="httpParam.apiRemark" autocomplete="off"></el-input>
  </div>
</template>
<script>
import { mapGetters } from 'vuex'

export default {
  name: 'DetailedDesc',
  data () {
    return {
    }
  },
  computed: {
    ...mapGetters([
      'httpParam'
    ])
  },
  methods: {
  }
}

</script>
<style lang="scss" >
	.add-remark{
	}
</style>
