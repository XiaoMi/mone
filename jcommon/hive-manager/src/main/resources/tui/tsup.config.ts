import { defineConfig } from 'tsup'

export default defineConfig({
  entry: ['src/index.tsx'],
  format: ['esm'],
  dts: true,
  splitting: false,
  sourcemap: true,
  clean: true,
  shims: true,
  banner: {
    js: '#!/usr/bin/env node',
  },
})
