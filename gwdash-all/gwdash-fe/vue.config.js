/*
 * Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

const path = require('path')
// const { BundleAnalyzerPlugin } = require('webpack-bundle-analyzer')

const cdn = require('./cdn.js')
// 拼接路径
function resolve (dir) {
  return path.join(__dirname, dir)
}

// 基础路径 注意发布之前要先修改这里
let baseUrl = '/'
const proxySubArr = ['^/api', '^/ws', '^/open', '^/upload']
let proxySubObj = {}
proxySubArr.forEach(it => {
  proxySubObj[it] = {
    target: 'http://xx_replace_xx',
    ws: true,
    changeOrigin: true
  }
})
module.exports = {
  publicPath: baseUrl, // 根据你的实际情况更改这里
  lintOnSave: false,
  devServer: {
    publicPath: baseUrl, // 和 baseUrl 保持一致
    openPage: 'gwdash/index',
    compress: true,
    disableHostCheck: true,
    proxy: {
      ...proxySubObj,
      '^https://xx_replace_xx': {
        target: 'https://xx_replace_xx',
        ws: true,
        changeOrigin: true
      },
      '^/gwdash': {
        target: 'http://xx_replace_xx',
        bypass: function (req, res, proxyOptions) {
          const query = req.query
          if (query.ticket) return
          if (req.headers.accept.indexOf('html') !== -1) {
            return '/index.html'
          }
        }
      }
    }
  },
  configureWebpack: config => {
    // const plugins = config.plugins
    /****
     *  ele 和 vue使用cdn
     */

    config.output.chunkFilename = '[id].[chunkhash].js'
    config.output.chunkFilename = '[id].[chunkhash].js'
    config.output.filename = '[name].[hash].js'

    if (process.env.NODE_ENV === 'development') { // 为开发环境修改配置
      config.devtool = 'eval-source-map'
      config.performance = {
        hints: 'warning',
        maxAssetSize: 1024 * 1024
      }
      // plugins.push(new BundleAnalyzerPlugin({ analyzerPort: 8919 }))
    }
    config.optimization = {
      splitChunks: {
        automaticNameDelimiter: '-',
        cacheGroups: {
          // vueBase: {
          //   test: /[\\/]node_modules[\\/](vue|core-js)/,
          //   chunks: "all",
          // },
          // elementui: {
          //   test: /[\\/]node_modules[\\/](element-ui)/,
          //   chunks: "all",

          // },
          // commons: {
          //   test: /[\\/]node_modules[\\/](codemirror|lodash|sockjs|)/,
          //   chunks: 'all',
          //   enforce: true

          // },
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
          // elementui: {
          //   test: /[\\/]node_modules[\\/](element-ui)/,
          //   chunks: "all",
          //   enforce:true
          // },
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
      args[0].cdn = cdn
      return args
    })

    // config.plugin('webpack-bundle-analyzer')
    // .use(require('webpack-bundle-analyzer').BundleAnalyzerPlugin)

    config.externals({
      'jquery': 'jQuery',
      'jsplumb': 'jsPlumb',
      'vue': 'Vue',
      'element-ui': 'ELEMENT'
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
