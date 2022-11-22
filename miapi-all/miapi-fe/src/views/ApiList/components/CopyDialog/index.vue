<template>
	<div>
		<CopyHttp @onClose="$emit('onClose')" :defaultData="defaultData"/>
  </div>
</template>

<script>
import { GROUP_TYPE } from '../../constant'
import CopyHttp from "./CopyHttp.vue"
import { PROTOCOL_TYPE } from '@/views/constant'

export default {
  name: 'CopyDialog',
  components: {
    CopyHttp
  },
  data () {
    return {
    }
  },
  computed: {
    protocolType () {
      return PROTOCOL_TYPE
    }
  },
  props: {
    defaultData: {
      type: Object,
      default () {
        return { }
      }
    }
  },
  watch: {
    defaultData: {
      handler (newVal) {
      },
      immediate: true,
      deep: true
    }
  },
  mounted () {
  },
  methods: {
  }
}
</script>
