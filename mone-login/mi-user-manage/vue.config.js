const { defineConfig } = require('@vue/cli-service');
// const timeStamp = new Date().getTime();

module.exports = defineConfig({
  transpileDependencies: true,
  productionSourceMap: true,
  configureWebpack: {
    devtool: 'source-map',
  },
});
