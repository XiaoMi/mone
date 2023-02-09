const path = require('path')
// const { BundleAnalyzerPlugin } = require('webpack-bundle-analyzer')
// 拼接路径
function resolve (dir) {
  return path.join(__dirname, dir)
}

const name = 'Admin' // page title

module.exports = {
  lintOnSave: false,
  publicPath: '/',
  // devServer: {
  //   compress: true,
  //   disableHostCheck: true,
  //   proxy: {
  //     '^/dev': {
  //       target: 'xx',
  //       // ws: true,
  //       changeOrigin: true, // 允许跨域
  //       pathRewrite: {
  //         '^/dev': ''
  //       }
  //     }
  //   }
  // },
  pwa: {
    iconPaths: {
     favicon32   : 'favicon.png',
     favicon16   : 'favicon.png',
     appleTouchIcon: 'favicon.png',
     maskIcon   : 'favicon.png',
     msTileImage  : 'favicon.png'
    }
  },
  configureWebpack: {
    // provide the app's title in webpack's name field, so that
    // it can be accessed in index.html to inject the correct title.
    name: name,
    resolve: {
      extensions: ['.vue', '.ts', '.tsx', '.js', '.jsx'],
      alias: {
        '@': resolve('src')
      }
    }
  },
  chainWebpack (config) {
    config.plugins.delete('preload') // TODO: need test
    config.plugins.delete('prefetch') // TODO: need test

    // set svg-sprite-loader
    config.module
      .rule('svg')
      .exclude.add(resolve('src/icons'))
      .end()
    config.module
      .rule('icons')
      .test(/\.svg$/)
      .include.add(resolve('src/icons'))
      .end()
      .use('svg-sprite-loader')
      .loader('svg-sprite-loader')
      .options({
        symbolId: 'icon-[name]'
      })
      .end()

    config
      // https://webpack.js.org/configuration/devtool/#development
      .when(process.env.NODE_ENV === 'development',
        config => config.devtool('cheap-source-map')
      )

    config
      .when(process.env.NODE_ENV !== 'development',
        config => {
          // config
          //   .plugin('ScriptExtHtmlWebpackPlugin')
          //   .after('html')
          //   .use('script-ext-html-webpack-plugin', [{
          //   // `runtime` must same as runtimeChunk name. default is `runtime`
          //     inline: /runtime\..*\.js$/
          //   }])
          //   .end()
          config
            .optimization.splitChunks({
              chunks: 'all',
              cacheGroups: {
                libs: {
                  name: 'chunk-libs',
                  test: /[\\/]node_modules[\\/]/,
                  priority: 10,
                  chunks: 'initial' // only package third parties that are initially dependent
                },
                elementUI: {
                  name: 'chunk-elementUI', // split elementUI into a single package
                  priority: 20, // the weight needs to be larger than libs and app or it will be packaged into libs or app
                  test: /[\\/]node_modules[\\/]_?element-plus(.*)/ // in order to adapt to cnpm
                },
                commons: {
                  name: 'chunk-commons',
                  test: resolve('src/components'), // can customize your rules
                  minChunks: 3, //  minimum common number
                  priority: 5,
                  reuseExistingChunk: true
                }
              }
            })
          config.optimization.runtimeChunk('single')
        }
      )
  }
}
