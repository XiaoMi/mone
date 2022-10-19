<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<style lang="less" scope>
*{
  box-sizing: border-box;
  padding:0;
  margin:0;
}
#app {
  position: fixed;
  width: 100%;
  height: 100%;
}
.text-align-right{
  padding-top:10px;
  text-align: right;
}
.de-active{
  color:#F56C6C;
}
.active{
  color:#67C23A
}
</style>
