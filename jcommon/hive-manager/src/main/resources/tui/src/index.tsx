#!/usr/bin/env node
import 'dotenv/config'
import React from 'react'
import { render } from 'ink'
import { App } from './App.js'

// Clear terminal
console.clear()

// Log environment variables in debug mode
if (process.env.DEBUG === 'true') {
  console.log('Environment loaded:')
  console.log('  API_BASE_URL:', process.env.API_BASE_URL)
  console.log('  WS_BASE_URL:', process.env.WS_BASE_URL)
  console.log('')
}

// Render the app
const { unmount, waitUntilExit } = render(<App />)

// Handle exit
process.on('SIGINT', () => {
  unmount()
  process.exit(0)
})

process.on('SIGTERM', () => {
  unmount()
  process.exit(0)
})

// Wait for the app to exit
waitUntilExit().then(() => {
  process.exit(0)
})
