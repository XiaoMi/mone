const { defineConfig } = require('@vue/cli-service');

module.exports = defineConfig({
  transpileDependencies: true,
  productionSourceMap: true,
  configureWebpack: {
    devtool: 'source-map',
  },
});
