<template>
  <div class="regist-box">
    <div class="left-img" :style="{ backgroundImage: 'url(' + backImg + ')' }">
    </div>
    <div class="main-wrap">
      <vue-particles
        class="back-img"
        color="#dedede"
      >
      </vue-particles>
      <el-form :model="formData" class="form-box" ref="ruleForm" :rules="rules">
      <div class="title">
        欢迎注册
      </div>
      <el-form-item prop="account">
        <el-input v-model="formData.account" placeholder="邮箱" class="register-input"></el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input v-model="formData.password" placeholder="密码" class="register-input"
        show-password></el-input>
      </el-form-item>
      <el-form-item prop="name">
        <el-input v-model="formData.name" placeholder="昵称" class="register-input"></el-input>
      </el-form-item>
      <el-form-item class="btn-box">
        <el-button type="primary"
          @click="submitForm('ruleForm')" class="regist-btn">立即注册</el-button>
      </el-form-item>
    </el-form>
    </div>

  </div>
</template>
<script>
import backImg from '@/assets/back.jpeg';
import { checkAcount, registMember } from '@/common/service/list/user-manage';

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
      checkAcount(params).then(({ data }) => {
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
      backImg,
      formData: {
        account: '',
        type: 0,
        password: '',
        name: '',
      },
      rules: {
        account: [
          { validator: validateAcount, trigger: 'blur' },
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
        ],
        name: [
          { required: true, message: '请输入昵称', trigger: 'blur' },
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
      const params = { ...this.formData, pageUrl: window.location.href };

      registMember(params).then(({ data }) => {
        console.log('注册成功', data);
        const { redirectUrl } = this.$route.query;
        this.$router.push({
          name: 'UserLogin',
          query: {
            redirectUrl,
          },
        });
      });
    },
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
        margin-bottom: 30px;
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
