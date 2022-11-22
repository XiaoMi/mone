module.exports = {
  moduleFileExtensions: [
    'ts',
    'tsx'
  ],
  transform: {
    '^.+\\.tsx?$': 'ts-jest'
  },
  globals: {
    'ts-jest': {
      babelConfig: true
    }
  }
}
