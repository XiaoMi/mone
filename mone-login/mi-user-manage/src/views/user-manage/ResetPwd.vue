<template>
  <div class="regist-box">
    <div class="left-img" :style="{ backgroundImage: 'url(' + passwordImg + ')' }">
    </div>
    <div class="main-wrap">
      <vue-particles
        class="back-img"
        color="#dedede"
      >
      </vue-particles>
      <el-form :model="formData" class="form-box" ref="ruleForm" :rules="rules">
      <div class="title">
        {{resetSuc?'重置成功':'重置密码'}}
      </div>
      <p class="tips" > {{resetSuc?'快去登录吧！':'请输入密码'}}</p>
      <template v-if="!resetSuc">
        <el-form-item prop="account" >
          <el-input v-model="formData.account" placeholder="邮箱"
          class="register-input" :disabled="true"></el-input>
        </el-form-item>
        <el-form-item prop="newPwd" >
          <el-input v-model="formData.newPwd" placeholder="密码" class="register-input"
          show-password></el-input>
        </el-form-item>
        <el-form-item class="btn-box">
          <el-button type="primary"
            @click="submitForm('ruleForm')" class="regist-btn"  >重置密码</el-button>
        </el-form-item>
      </template>
    </el-form>
    </div>
  </div>
</template>
<script>
import passwordImg from '@/assets/login-page/password.jpeg';
import { checkAcountLogin, resetPwd } from '@/common/service/list/user-manage';

export default {
  data() {
    // eslint-disable-next-line consistent-return
    const validateAcount = (rule, value, callback) => {
      if (!value) {
        return callback(new Error('请输入邮箱'));
      }
      const params = {
        type: 0,
        account: value,
      };
      checkAcountLogin(params).then(({ data }) => {
        if (data) {
          if (data.code !== 0) {
            callback(new Error(data.message));
          } else {
            callback();
          }
        } else {
          callback();
        }
      });
    };
    return {
      resetSuc: false,
      passwordImg,
      formData: {
        account: '',
        newPwd: '',
        type: 0,
      },
      rules: {
        account: [
          { validator: validateAcount, trigger: 'blur' },
        ],
        newPwd: [
          { required: true, message: '请输入密码', trigger: 'blur' },
        ],
      },
    };
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.sendRegist();
        }
        console.log('error submit!!');
        return false;
      });
    },
    sendRegist() {
      // const formDom = this.$refs.ruleForm;
      const { token } = this.$route.query;
      const params = { ...this.formData, token };
      resetPwd(params).then(() => {
        this.resetSuc = true;
      });
    },
  },
  mounted() {
    const { account, type } = this.$route.query;
    this.formData.account = account;
    this.formData.type = type;
  },
};
</script>
<style lang="less" scoped>
.regist-box{
  height:100%;
  width:100%;
  position: absolute;
  .main-wrap{
    position: relative;
    margin-left: 480px;
    height: 100%;
    .form-box{
      width: 480px;
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -60%);
      .title{
        font-size: 44px;
        margin-bottom: 10px;
      }
      .tips{
        font-size: 14px;
        color: #c0c4cc;
        margin-bottom: 20px;
      }
    }

  }

}

.register-input{
  border-color: #aaa;
  /deep/ .el-input__inner{
    height: 52px;
    font-size: 20px;
  }
}
.regist-btn{
  width:100%;
  height:60px;
  font-size: 24px;
  background: #3487ff;
  border-color: #3487ff;
}
.btn-box{
  padding-top:10px;
}
.back-img{
  height: 100%;
  width:100%;
  position: absolute;
}
.left-img{
  position: fixed;
  top: 0;
  left: 0;
  height: 100%;
  width: 480px;
  float: left;
  overflow: hidden;
  background-repeat: no-repeat;
  background-position: center;
  background-size: cover;
}
</style>
