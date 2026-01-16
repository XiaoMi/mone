# Steno [![](http://img.shields.io/npm/dm/steno.svg?style=flat)](https://www.npmjs.org/package/steno) [![Node.js CI](https://github.com/typicode/steno/actions/workflows/node.js.yml/badge.svg)](https://github.com/typicode/steno/actions/workflows/node.js.yml)

> Specialized fast async file writer

**Steno** makes writing to the same file often/concurrently fast and safe.

Used in [lowdb](https://github.com/typicode/lowdb).

_https://en.wikipedia.org/wiki/Stenotype_

## Features

- ⚡ Fast (see benchmark)
- 🐦 Lightweight (~6kb)
- 👍 ⚛️ Safe: No partial writes (writes are atomic)
- 👍 🏁 Safe: No race conditions (writes are ordered even if they're async)

## Usage

```javascript
import { Writer } from 'steno'

// Create a singleton writer
const file = new Writer('file.txt')

// Use it in the rest of your code
async function save() {
  await file.write('some data')
}
```

## Benchmark

`npm run benchmark` (see `src/benchmark.ts`)

```
Write 1KB data to the same file x 1000
  fs     :   62ms
  steno  :    1ms

Write 1MB data to the same file x 1000
  fs     : 2300ms
  steno  :    5ms
```

_Steno uses a smart queue and avoids unnecessary writes._

## License

MIT - [Typicode](https://github.com/typicode)
