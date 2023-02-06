<template>
  <div class="regist-box">
    <div class="left-img" :style="{ backgroundImage: 'url(' + password + ')' }">
    </div>
    <div class="main-wrap">
      <vue-particles
        class="back-img"
        color="#dedede"
      >
      </vue-particles>
      <el-form :model="formData" class="form-box" ref="ruleForm" :rules="rules">
      <div class="title">
        {{sendReset?'已发送':'重置密码'}}
      </div>
      <p class="tips" v-if="sendReset">
        我们向邮箱 {{formData.account}} 发送了一封含有重置密码链接的邮件。请登录网页版邮箱查看，如长时间没有收到邮件，请检查你的垃圾邮件文件夹。
      </p>
      <p class="tips" v-else>请输入邮箱重置你的密码</p>
      <template v-if="!sendReset">
        <el-form-item prop="account" >
          <el-input v-model="formData.account" placeholder="邮箱" class="register-input"></el-input>
        </el-form-item>
      </template>
      <el-form-item class="btn-box">
        <el-button type="primary"
          @click="toLogin" class="regist-btn" v-if="sendReset">登录</el-button>
        <el-button type="primary"
          @click="submitForm('ruleForm')" class="regist-btn"  v-else>重置密码</el-button>
      </el-form-item>
    </el-form>
    </div>
  </div>
</template>
<script>
import { checkAcountLogin, findPwd } from '@/common/service/list/user-manage';
import password from '@/assets/login-page/password.jpeg';

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
      sendReset: false,
      password,
      formData: {
        account: '',
        type: 0,
      },
      rules: {
        account: [
          { validator: validateAcount, trigger: 'blur' },
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
      findPwd(params).then(() => {
        this.sendReset = true;
      }).catch((err) => {
        console.log('err', err);
      });
    },
    toLogin() {
      const { redirectUrl } = this.$route.query;
      this.$router.push({
        name: 'UserLogin',
        query: {
          redirectUrl,
        },
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
