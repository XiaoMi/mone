<template>
  <el-select v-model="user" placeholder="请输入资源名称"
    filterable
    value-key="idPath">
    <el-option
      v-for="item in options"
      :key="item.id"
      :label="item.resourceName"
      :value="item.id">
    </el-option>
  </el-select>
</template>
<script>
import { getResourcePool } from '@/common/service/list/resource';

export default {
  props: {
    value: {},
    type: {},
    relNodeId: {},
  },
  data() {
    return {
      options: [],
    };
  },
  computed: {
    user: {
      get() {
        return this.value;
      },
      set(val) {
        this.$emit('input', val);
      },
    },

  },
  watch: {
    user(newV, oldV) {
      if (newV !== oldV) {
        if (this.options?.length === 0) {
          this.options.push(newV);
        }
      }
    },
    type(newV, oldV) {
      if (newV !== oldV) {
        this.$emit('input', '');
        this.getLsit();
      }
    },
  },
  mounted() {
    this.getLsit();
  },
  methods: {
    getLsit() {
      const params = {
        type: this.type,
        relNodeId: this.relNodeId,
        resourceName: '',
      };
      getResourcePool(params).then(({ data }) => {
        this.options = data.list || [];
      });
    },
  },
};
</script>
