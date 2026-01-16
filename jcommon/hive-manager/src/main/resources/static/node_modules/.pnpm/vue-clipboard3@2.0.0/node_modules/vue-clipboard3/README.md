# vue-clipboard3

Easily copy to clipboard in Vue 3 (composition-api) using clipboard.js 📋

Thanks to [vue-clipboard2](https://github.com/Inndy/vue-clipboard2) for inspiration!

## Install

`yarn add vue-clipboard3`

or

`npm install --save vue-clipboard3`

## About

For use with **Vue 3** and the **Composition API**. I decided to keep in line with the Vue 3 spirit and not make a directive out of this (if you want a vue directive, please make a pull request). I think it makes more sense and provides more clarity to just use this as a method in the `setup()` function.

Keep it simple.

## Usage

### Simple

```html
<template lang="html">
  <button @click="copy">Copy!</button>
</template>

<script lang="ts">
import { defineComponent } from '@vue/composition-api'
import useClipboard from 'vue-clipboard3'

export default defineComponent({
  setup() {
    const { toClipboard } = useClipboard()

    const copy = async () => {
      try {
        await toClipboard('Any text you like')
        console.log('Copied to clipboard')
      } catch (e) {
        console.error(e)
      }
    }

    return { copy }
  }
})
</script>
```

### With ref

```html
<template lang="html">
  <div>
    <input type="text" v-model="text">
    <button @click="copy">Copy!</button>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from '@vue/composition-api'
import useClipboard from 'vue-clipboard3'

export default defineComponent({
  setup() {
    const { toClipboard } = useClipboard()

    const text = ref('')

    const copy = async () => {
      try {
        await toClipboard(text.value)
        console.log('Copied to clipboard')
      } catch (e) {
        console.error(e)
      }
    }

    return { copy, text }
  }
})
</script>
```

## API

```ts
useClipboard(options: Options)
```

```ts
interface Options {
  /** Fixes IE by appending element to body. Defaults to true. */
  appendToBody: boolean
}
```

returns an object with a single key: `toClipboard`

```ts
toClipboard(text: string, container?: HTMLElement)
```

requires that you pass in at least one argument that is a string. This is the text to be copied to the clipboard. The second optional argument is a html element that will be used as the container internally when using clipboard.js.

### Contribution

PRs and issues welcome!

```shell
git clone https://github.com/JamieCurnow/vue-clipboard3.git
cd vue-clipboard3
yarn install
yarn watch
```

### License

[MIT License](https://github.com/JamieCurnow/vue-clipboard3/blob/main/LICENSE)
