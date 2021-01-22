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
  <div class="console-box">
      <ul >
        <li v-for="(item,index) in list" :key="index" :class="'line-type-'+item.type">
          <!-- <div class="line-count">{{index}}</div> -->
          <div style='word-break:break-all'>{{item.logStr}}</div>
        </li>
      </ul>
  </div>
</template>

<script>
  export default {
    name:"allConsoleBox",
    computed:{
      list:function(){
        let a = JSON.parse(localStorage.getItem('a'))
        let log=a[0].data.split("\n");
        return log.map((item,index)=>{
          return {
             logStr:item,
             type:this.getLogType(item)
          }
        })
      }
    },
    methods:{
      getLogType(str){
        /*****
         *  0 normal
         *  1 info
         *  2 warning
         *  3 error
         */
        if(str.startsWith(`[INFO]`) || str.startsWith(`[info]`)){
          return 1
        }else if(str.startsWith(`[WARNING]`) || str.startsWith(`[warning]`) ){
          return 2
        }else if(str.startsWith(`[WARN]`) || str.startsWith(`[warn]`)){
          return 2
        }else if(str.startsWith(`[ERROR]`) || str.startsWith(`[error]`)){
          return 3
        }else if(str.startsWith(`[SUCCESS]`)|| str.startsWith(`[success]`)){
          return 4
        }else if(str.indexOf('successfully')>-1){
          return 4
        }else{
          return 0
        }
      }
    },
    directives:{
      autoFixBottom:{
        update(el){
          let scrollHeight=el.scrollHeight;
          let clientHeight=el.clientHeight;
          if(scrollHeight>el.clientHeight){
            setTimeout(()=>{
              el.scrollTop=el.scrollHeight;
            },300)
          }
        }
      }
    }
  }
</script>

<style lang="scss" scoped>
.console-box{
  width: 100%;
  overflow: hidden;
  display: flex;
  flex-flow: column-reverse;
  align-items: baseline;
  ul{
    padding: 0 10px;
    font-size: 14px;
    font-family: 'Courier New', Courier, monospace;
  }
  li{
    display: flex;
    line-height: 1.5;
  }
}
.line-count{
  min-width: 20px;
  text-align: right;
  padding-right:10px;
  color: #909399;
}
.line-type-0{
  color: #909399
}
.line-type-1{
  color: #dedede
}
.line-type-2{
  color: #E6A23C
}
.line-type-3{
  color: #F56C6C;
}
.line-type-4{
  color: #67C23A;
}
</style>