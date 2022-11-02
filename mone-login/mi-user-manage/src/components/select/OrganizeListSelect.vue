<template>
  <el-select v-model="user" placeholder="请输入部门名称"
    style="width:360px"
    filterable
    remote
    value-key="idPath"
    :remote-method="remoteMethod">
    <el-option
      v-for="item in options"
      :key="item.idPath"
      :label="item.namePath"
      :value="item">
    </el-option>
  </el-select>
</template>
<script>
import { getOrgList } from '@/common/service/list/node';

export default {
  props: {
    value: {},
  },
  data() {
    return {
      options: [],
      timer: null,
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
    user: {
      handler(newV, oldV) {
        if (newV && newV !== oldV) {
          if (this.options?.length === 0) {
            this.options.push(newV);
          }
        }
      },
      immediate: true,
      deep: true,
    },
  },
  mounted() {
    this.getLsit((this.user && this.user.namePath) || '');
  },
  methods: {
    remoteMethod(val) {
      if (this.timer) {
        clearTimeout(this.timer);
      }
      this.timer = setTimeout(() => {
        this.getLsit(val);
        clearTimeout(this.timer);
        this.timer = null;
      }, 2000);
    },
    getLsit(val) {
      getOrgList({ orgName: val }).then(({ data }) => {
        this.options = data.list || [];
      });
    },
  },
};
</script>
