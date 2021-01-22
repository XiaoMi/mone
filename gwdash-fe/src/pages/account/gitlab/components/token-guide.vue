<!--
  Copyright 2020 Xiaomi

     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.
  -->

<template>
  <div class="guide">
      <div class="guide-top"></div>
      <div class="guide-head">创建token引导【token将用于获取代码权限实现项目自动化服务】：</div>
      <div class="guide-one">
          <div class="word">
              1、进入gitLab <a target="_blank" style="color: #409EFF" title="点击直达" href="https://git.n.xiaomi.com/profile/personal_access_tokens">Personal Access Tokens</a>页面
          </div>
          <div class="image">
              <img src="https://img.youpin.mi-img.com/comment/a9e1c06d26761ed2c03a9414e7fca6af.png?w=1275&h=912" alt="">
          </div>
      </div>
      <div class="guide-two">
          <div class="word">
              2、创建Personal Access Tokens
          </div>
          <div class="image">
                <img src="https://img.youpin.mi-img.com/comment/e230acf3187cc74ef448853a1ca1d2dc.png?w=1272&h=910" alt="">
          </div>
      </div>
      <div class="guide-three">
          <div class="word">
              3、创建成功，获得Your New Personal Access Token
          </div>
          <div class="image">
              <img src="https://img.youpin.mi-img.com/comment/39b2bb65db9b91a22bf1d11ffd58ab1b.png?w=1264&h=911" alt="">
          </div>
      </div>
      <div class="guide-three">
          <div class="word">
              4、点击页面“新增”按钮，添加此token值
          </div>
          <div class="image">
              <img src="https://img.youpin.mi-img.com/comment/389bc91caa477d948a28ba70cda8222b.png?w=1604&h=948" alt="">
          </div>
      </div>
      <div class="guide-footer">感谢阅读<i class="fa fa-smile-o" aria-hidden="true" style="font-size:18px; color:#E6A23C; margin-left:8px"></i></div>
  </div>
</template>

<script>
export default {

}
</script>

<style lang="scss" scoped>
.guide {
    border: 1px solid #eee;
    padding: 10px;
    box-shadow: rgba(0, 0, 0, 0.12) 0px 2px 4px, rgba(0, 0, 0, 0.04) 0px 0px 6px;
    position: relative;
    &-top {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        background-color: #409EFF;
        height: 3px;
        border-top-left-radius: 8px;
        border-top-right-radius: 8px;
    }
    &-head {
        font-size: 18px;
        color: #909399;
        font-weight: bold;
        margin-top: 10px;
        margin-left: 5px;
    }
    &-one,&-two,&-three {
         margin: 16px 0 26px 0;
         padding: 10px;
         border: 1px solid #eee;
         border-radius: 6px;
    }
    &-footer {
        font-size: 17px;
        color: #909399;
        font-weight: bold;
        text-align: center;
        margin-bottom: 8px;
        display: flex;
        justify-content: center;
        align-items: center;
    }
}
.word {
   font-size: 17px;
   color: #909399;
   font-weight: bold;
   margin-bottom: 8px;
}
.image img{
    width: 930px;
    height: 600px;
}
</style>