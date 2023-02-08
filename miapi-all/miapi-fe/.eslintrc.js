
module.exports = {
  root: true,
  env: {
    node: true
  },
  parserOptions: {
	  parser: "@typescript-eslint/parser",
    sourceType: "module"
  },
  plugins: [
    '@typescript-eslint',
  ],
  extends: [
    'plugin:vue/recommended',
    'plugin:@typescript-eslint/recommended',
    '@vue/standard'
  ],
  // 'extends': [
  //   'plugin:vue/essential',
  //   '@vue/standard'
  // ],
  rules: {
    'no-console': process.env.NODE_ENV === 'production' ? 'error' : 'off',
    'no-unused-vars': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'quotes': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    "indent": 2,
    "no-tabs": 'off',
    "no-mixed-spaces-and-tabs": 'off',
    "prefer-promise-reject-errors": 'off',
    // 'no-mixed-operators': 'off'
  },
  // parserOptions: {
  //   parser: 'babel-eslint'
  // },
  globals: {
    "serverEnv": true,
    "userInfo": true
  }
}