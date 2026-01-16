module.exports = {
  runtimeCompiler: true,
  css: {
    loaderOptions: {
      less: {
        modifyVars: {},
      },
      postcss: {
        plugins: [],
      },
    },
    extract: false,
  },
  publicPath: process.env.NODE_ENV === "production" ? "/flowchart-vue" : "/"
};