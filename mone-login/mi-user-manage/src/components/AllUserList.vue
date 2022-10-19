<template>
<div>
  <el-select v-model="user" placeholder="请选择用户"
    v-bind="$attrs"
    filterable
    remote
    :remote-method="remoteMethod">
    <el-option
      v-for="item in options"
      :key="item.id"
      :label="$retUserNameType(item.account ,item.type)"
      :value="item.id">
    </el-option>
  </el-select>
  </div>
</template>
<script>
import { getAllUser } from '@/common/service/list/common-req';

export default {
  props: {
    value: {},
    initAccount: {},
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
    account() {
      return this.initAccount;
    },
  },
  mounted() {
    this.getLsit(this.account || '');
  },
  methods: {
    remoteMethod(val) {
      this.getLsit(val);
    },
    getLsit(val) {
      getAllUser({ userAcc: val }).then(({ data }) => {
        this.options = data.list || [];
      });
    },
  },
};
</script>
