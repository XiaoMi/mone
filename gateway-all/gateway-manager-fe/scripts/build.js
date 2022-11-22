const { execSync } = require('child_process')
const env = process.argv.slice(2)[0]
execSync(
  `cross-env APP_VERSION=${+new Date()} vue-cli-service build --mode ${env}`,
  { stdio: [0, 1, 2] }
)
