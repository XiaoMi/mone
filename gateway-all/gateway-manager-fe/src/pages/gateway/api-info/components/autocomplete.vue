<template>
  <el-autocomplete
      v-model="data"
      :fetch-suggestions="querySearchAsync"
      :value-key="valueKey"
      clearable
      :trigger-on-focus="triggerFocus"
      :placeholder="placeholder"
      @input="inputValue"
      @select="handleSelect"
  />
</template>
<script>
export default {
  props: {
    value: {},
    index: Number,
    ajaxName: String,
    selectChange: {},
    valueKey: {
      type: String,
      default: 'value'
    },
    triggerFocus: {
      type: Boolean,
      default: false
    }, // 是否在输入框 focus 时显示建议列表
    fetch: Boolean,
    options: Array, // 选项数据
    param: Object, // 请求参数
    placeholder: {
      type: String,
      default: '请输入内容'
    }
  },
  watch: {
    value: {
      handler (newVal) {
        this.data = newVal
      },
      immediate: true,
      deep: true
    }
  },
  data () {
    return {
      data: ''
    }
  },
  mounted () {},
  methods: {
    querySearchAsync (query, cb) {
      clearTimeout(this.timeout)
      if (!this.fetch) {
        const results = query ? this.options.filter(this.createFilter(query)) : this.options
        cb && cb(results)
        return false
      }
      console.log(query)
    },
    createFilter (queryString) {
      return (restaurant) => {
        return (restaurant.value.toLowerCase().includes(queryString.toLowerCase()))
      }
    },
    handleSelect (val) {
      this.$emit('input', val[this.valueKey])
    },
    inputValue (val) {
      this.$emit('input', val)
      this.$emit('inputValue', this.index)
    }
  }
}
</script>
