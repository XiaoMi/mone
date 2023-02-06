<template>
  <el-select v-model="selected" placeholder="请选择" v-bind="$attrs"
  filterable remote :remote-method="remoteMethod">
    <el-option
      v-for="item in options"
      :key="item.id"
      :label="item.nodeName"
      :value="item.id">
    </el-option>
  </el-select>
</template>
<script>
import { getNodeList } from '@/common/service/list/node';

export default {
  props: {
    value: {},
    otherOption: {},
    query: {},
  },
  computed: {
    selected: {
      get() {
        return this.value;
      },
      set(val) {
        this.$emit('input', val);
        this.$emit('change', val);
      },
    },
    addOption() {
      return this.otherOption;
    },
  },
  watch: {
    addOption(newV, oldV) {
      if (newV.id !== oldV.id) {
        if (this.options.some((item) => item.id === newV.id)) return;
        this.options.push(newV);
      }
    },
  },
  mounted() {
    this.getList({});
  },
  methods: {
    getList(params) {
      getNodeList({ ...params, ...this.query }).then(({ data }) => {
        this.options = data.list;
      });
    },
    remoteMethod(nodeName) {
      const params = { nodeName };
      this.getList(params);
    },
  },
  data() {
    return {
      options: [],
    };
  },
};
</script>
