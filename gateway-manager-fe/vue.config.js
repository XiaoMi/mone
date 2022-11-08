/*
 *  Copyright 2020 Xiaomi
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */
const path = require('path')
// const { BundleAnalyzerPlugin } = require('webpack-bundle-analyzer')
var HardSourceWebpackPlugin = require('hard-source-webpack-plugin')
const { CleanWebpackPlugin } = require('clean-webpack-plugin')

// 拼接路径
function resolve (dir) {
  return path.join(__dirname, dir)
}

const publicPath = '/gw-manager-static/'

const proxySubArr = ['^/api']
let proxySubObj = {}
proxySubArr.forEach(it => {
  proxySubObj[it] = {
    target: 'https://devops.nr.lingdong.cn',
    ws: true,
    changeOrigin: true
  }
})

const devPlugins = [new HardSourceWebpackPlugin()]
const prodPlugins = [
  new CleanWebpackPlugin({
    verbose: true,
    protectWebpackAssets: false,
    cleanAfterEveryBuildPatterns: ['**/*.js.map']
  })
]

module.exports = {
  publicPath,
  lintOnSave: false,
  productionSourceMap: true,
  devServer: {
    publicPath,
    openPage: 'gateway/index',
    compress: true,
    disableHostCheck: true,
    proxy: {
      ...proxySubObj
    }
  },
  configureWebpack: {
    devtool: (process.env.NODE_ENV === 'development') ? 'source-map' : 'hidden-source-map',
    plugins: (process.env.NODE_ENV === 'development') ? devPlugins : prodPlugins,
    // const plugins = config.plugins
    /****
     *  ele 和 vue使用cdn
     */
    output: {
      chunkFilename: 'js/[id].[chunkhash].js',
      filename: 'js/[name].[hash].js'
    },
    performance: (process.env.NODE_ENV === 'development') ? {
      hints: 'warning',
      maxAssetSize: 1024 * 1024
    } : {},
    optimization: {
      splitChunks: {
        automaticNameDelimiter: '-',
        cacheGroups: {
          vuePluginAndBabel: {
            test: /[\\/]node_modules[\\/](vue|core-js)/,
            chunks: 'all'
          },
          commons: {
            test: /[\\/]node_modules[\\/](codemirror|lodash)/,
            name: 'vendors',
            chunks: 'all'
          },
          extraPlugin: {
            test: /[\\/]node_modules[\\/](better-scroll|axios|js-md5)/,
            chunks: 'all',
            enforce: true
          },
          default: {
            minChunks: 2,
            reuseExistingChunk: true
          }
        }
      }
    }
  },
  // 默认设置: https://github.com/vuejs/vue-cli/tree/dev/packages/%40vue/cli-service/lib/config/base.js
  chainWebpack: config => {
    config.plugin('html').tap(args => {
      return args
    })

    // 解决 cli3 热更新失效 https://github.com/vuejs/vue-cli/issues/1559
    config.resolve
      .symlinks(true)
    // markdown
    config.module
      .rule('md')
      .test(/\.md$/)
      .use('text-loader')
      .loader('text-loader')
      .end()
    // i18n
    config.module
      .rule('i18n')
      .resourceQuery(/blockType=i18n/)
      .use('i18n')
      .loader('@kazupon/vue-i18n-loader')
      .end()
    // svg
    const svgRule = config.module.rule('svg')
    svgRule.uses.clear()
    svgRule
      .include
      .add(resolve('src/assets/svg-icons/icons'))
      .end()
      .use('svg-sprite-loader')
      .loader('svg-sprite-loader')
      .options({
        symbolId: 'd2-[name]'
      })
      .end()
    // image exclude
    const imagesRule = config.module.rule('images')
    imagesRule
      .test(/\.(png|jpe?g|gif|webp|svg)(\?.*)?$/)
      .exclude
      .add(resolve('src/assets/svg-icons/icons'))
      .end()
    // 重新设置 alias
    config.resolve.alias
      .set('@', resolve('src'))
    // babel-polyfill 加入 entry
    const entry = config.entry('app')
    entry
      .add('babel-polyfill')
      .end()
  }
}
