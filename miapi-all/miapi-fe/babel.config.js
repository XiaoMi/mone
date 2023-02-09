let prodPlugins = []
if (process.env.NODE_ENV === 'production') {
	prodPlugins.push('transform-remove-console')
}

module.exports = {
  presets: [
    '@vue/cli-plugin-babel/preset'
  ],
  plugins: [
    ...prodPlugins
  ]
}
