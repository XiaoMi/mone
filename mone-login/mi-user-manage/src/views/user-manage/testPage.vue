<template>
  <div class="to-audio">
    <el-form :inline="true" :model="reqParams" class="demo-form-inline">
      <el-form-item label="工单名">
        <el-input v-model="reqParams.applyName" clearable ></el-input>
      </el-form-item>
      <el-form-item label="类型">
        <ApplyTypeSelect v-model="reqParams.type" clearable />
      </el-form-item>
      <el-form-item label="状态" v-if="switchShowStatus">
        <ApplyStatusSelect v-model="reqParams.status" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="getList">查询</el-button>
      </el-form-item>
    </el-form>
    <el-table :data="list" style="width: 100%" v-loading="loading">
      <el-table-column prop="applyName" label="工单名称">
        <template slot-scope="scope">
          <el-button @click="toDetail(scope.row)" type="text" size="small"
            >{{ scope.row.applyName }}
          </el-button>
        </template>
      </el-table-column>
      <el-table-column prop="type" label="类型">
        <template slot-scope="scope">
          <ApplyType :type="scope.row.type" />
        </template>
      </el-table-column>
      <el-table-column prop="desc" label="描述"> </el-table-column>
      <el-table-column prop="createrAcc" label="申请人"> </el-table-column>
      <el-table-column prop="status" label="状态" v-if="switchShowStatus">
        <template slot-scope="scope">
          <ApplyStatus :status="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-button @click="toDetail(scope.row)" type="text" size="small"
            >详情
          </el-button>
          <!-- <BtnsClass
            :applyDetailProp="scope.row"
            btnType="text"
            style="margin-left:10px"
            @getList="getList"
            class="list-opions"/> -->
        </template>
      </el-table-column>
    </el-table>
    <div class="text-align-right">
      <el-pagination
        background
        @current-change="handleCurrentChange"
        :current-page="reqParams.page"
        :page-size="reqParams.pageSize"
        layout="total, prev, pager, next"
        :total="total"
      >
      </el-pagination>
    </div>

  </div>
</template>
<script>
import {
  getOrderList,
} from '@/common/service/list/work-order';
import ApplyStatus from '@/components/ApplyStatus.vue';
import ApplyType from '@/components/ApplyType.vue';
import ApplyTypeSelect from '@/components/select/ApplyTypeSelect.vue';
import ApplyStatusSelect from '@/components/select/ApplyStatusSelect.vue';
import { mapState } from 'vuex';
// import BtnsClass from '@/views/work-order/BtnsClass.vue';

export default {
  components: {
    ApplyType,
    ApplyStatus,
    ApplyTypeSelect,
    ApplyStatusSelect,
    // BtnsClass,
  },
  data() {
    return {
      loading: false,
      showCreate: false,
      detailId: '',
      list: [],
      total: 0,
      reqParams: {
        pager: true,
        page: 1,
        pageSize: 20,
        myApply: true,
        applyName: '',
        type: null,
        status: null,
      },
    };
  },
  props: {
    showStatus: {
      default: true,
    },
    status: {
      default: '',
    },
  },
  watch: {
    $route(to, from) {
      if (to.name !== from.name) {
        this.getInitList();
      }
    },
  },
  computed: {
    ...mapState({
      ApplyStatusEnum: (state) => state.NodeModule.ApplyStatusEnum,
    }),
    switchShowStatus() {
      return this.showStatus;
    },
  },
  mounted() {
    this.reqParams = { ...this.reqParams, status: this.status };
    this.getInitList();
  },
  methods: {
    getInitList() {
      this.reqParams = {
        ...this.reqParams,
        myApply: this.$route.name === 'MyApply',
      };
      this.getList();
    },

    getList() {
      this.loading = true;
      getOrderList(this.reqParams)
        .then(({ data }) => {
          this.list = data.list || [];
          this.total = data.total;
          this.loading = false;
        })
        .catch(() => {
          this.loading = false;
        });
    },
    handleCurrentChange(page) {
      this.reqParams = { ...this.reqParams, ...{ page } };
      this.getList();
    },
    toDetail(item) {
      this.$router.push({ name: 'OrderDetail', params: { applyId: item.id } });
    },
  },
};
</script>
<style lang="less" scoped>
.to-audio {
  padding: 20px;
}
.list-opions{
  ::v-deep button{
    margin-bottom:0;
  }
}
</style>
