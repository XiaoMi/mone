[![GitHub stars](https://img.shields.io/github/stars/RennZhang/codemirror-editor-vue3)](https://github.com/RennZhang/codemirror-editor-vue3/stargazers)
[![npm downloads](https://img.shields.io/npm/dt/codemirror-editor-vue3)](https://www.npmjs.com/package/codemirror-editor-vue3)
[![GitHub issues](https://img.shields.io/github/issues/RennZhang/codemirror-editor-vue3)](https://github.com/RennZhang/codemirror-editor-vue3/issues)
[![GitHub forks](https://img.shields.io/github/forks/RennZhang/codemirror-editor-vue3)](https://github.com/RennZhang/codemirror-editor-vue3/network)
[![GitHub last commit](https://img.shields.io/github/last-commit/RennZhang/codemirror-editor-vue3)](https://github.com/RennZhang/codemirror-editor-vue3)
[![license](https://img.shields.io/github/license/RennZhang/codemirror-editor-vue3)](https://github.com/RennZhang/codemirror-editor-vue3)

# Introduction

[简体中文](https://rennzhang.github.io/codemirror-editor-vue3/zh-CN/guide/getting-started)

The codemirror component of vue3. This component is developed based on [Codemirror 5](http://codemirror.net/5/) and only
vue3 is supported.

In addition to the officially supported modes, the log output presentation mode is added, out of the box, but not
necessarily suitable for all scenarios.

For complete documentation and more cases, please check
[codemirror-editor-vue3 docs](https://RennZhang.github.io/codemirror-editor-vue3/).

## Install

```bash
npm install codemirror-editor-vue3 codemirror@^5 -S
```

```bash
yarn add codemirror-editor-vue3 codemirror@">=5.64.0 <6"
```

```bash
pnpm i codemirror-editor-vue3 codemirror@^5 -S
```

> If your project requires Typescript support, you will also need to install the '@types/codemirror' dependency.

```bash
npm install @types/codemirror -D
```

## Register global component

> **Do not recommend global registration components**, which will result in the type of prompt on the template that
> cannot be properly obtained.

`main.js:`

```js
import { createApp } from "vue";
import App from "./App.vue";
import { InstallCodeMirror } from "codemirror-editor-vue3";

const app = createApp(App);
app.use(InstallCodeMirror);
app.mount("#app");
```

The global registered component name is Codemirror or you can customize a component name, for example:

```js
app.use(InstallCodeMirror, { componentName: "customName" });
```

## Use in components

```vue
<template>
  <Codemirror
    v-model:value="code"
    :options="cmOptions"
    border
    placeholder="test placeholder"
    :height="200"
    @change="change"
  />
</template>

<script>
import Codemirror from "codemirror-editor-vue3";

// placeholder
import "codemirror/addon/display/placeholder.js";

// language
import "codemirror/mode/javascript/javascript.js";
// placeholder
import "codemirror/addon/display/placeholder.js";
// theme
import "codemirror/theme/dracula.css";

import { ref } from "vue";
export default {
  components: { Codemirror },
  setup() {
    const code = ref(`
var i = 0;
for (; i < 9; i++) {
  console.log(i);
  // more statements
}`);

    return {
      code,
      cmOptions: {
        mode: "text/javascript", // Language mode
        theme: "dracula", // Theme
      },
    };
  },
};
</script>
```

## Language highlighting

> You can click on the following link to view corresponding language cases

- [javascript](https://rennzhang.github.io/codemirror-editor-vue3/example?lang=javascript)
- [json](https://rennzhang.github.io/codemirror-editor-vue3/example?lang=json)
- [css](https://rennzhang.github.io/codemirror-editor-vue3/example?lang=css)
- [html](https://rennzhang.github.io/codemirror-editor-vue3/example?lang=html)
- [apl](https://rennzhang.github.io/codemirror-editor-vue3/example?lang=apl)
- [yaml](https://rennzhang.github.io/codemirror-editor-vue3/example?lang=yaml)

More cases are gradually being added, and you can also refer to
[document](https://rennzhang.github.io/codemirror-editor-vue3/guide/lang) to achieve more language modes.

## Component Props

[cm_config_url]: https://codemirror.net/doc/manual.html#config
[cm_editor_type_url]: https://codemirror.net/doc/manual.html#config
[default_options_url]: https://github.com/RennZhang/codemirror-editor-vue3/blob/main/packages/src/config/index.ts#L68

| name                |                                                                description                                                                | type                                      |                default                 |
| ------------------- | :---------------------------------------------------------------------------------------------------------------------------------------: | :---------------------------------------- | :------------------------------------: |
| **value(v-model)**  |                                                              Editor content                                                               | `string`                                  |                   ""                   |
| **options**         |                                           [Configuration options of codemirror5][cm_config_url]                                           | [EditorConfiguration][cm_editor_type_url] | [DEFAULT_OPTIONS][default_options_url] |
| **placeholder**     |                                     Editor placeholder content to introduce codemirror related files                                      | `string`                                  |                   ""                   |
| **border**          |                                                     Whether to display editor borders                                                     | `boolean`                                 |                `false`                 |
| **width**           |                                                                   width                                                                   | `string`                                  |                `100%  `                |
| **height**          |                                                                  height                                                                   | `string`                                  |                `100%  `                |
| **original-style**  | Using the original style, disable the second modification of the style for this component (but does not affect width, height, and border) | ` boolean`                                |                `false`                 |
| **KeepCursorInEnd** |                                              Always keep the mouse position on the last line                                              | `boolean`                                 |                `false`                 |
| **merge**           |                                               merge mode, can also be used as diff pattern                                                | `boolean`                                 |                `false`                 |

## Events

### Component Events

> The following three are only the events encapsulated by this component. Please refer to more events
> [Codemirror Events](./events#codemirror-events)

| event name |             description             | params                                |
| ---------- | :---------------------------------: | :------------------------------------ |
| `change`   |      value or instance changes      | `(value: string, cm: Editor) => void` |
| `input`    |                input                | `(value: string) => void`             |
| `ready`    | The Codemirror component is mounted | `(cm: Editor) => void;`               |

---

### Codemirror Events

The following events are official events of Codemirror5. You can refer to the official documents for details
[Codemirror Event](https://codemirror.net/doc/manual.html#events)，You can use this component to bind events directly
through components, for example：

```vue {8-10}
<Codemirror
  v-model:value="code"
  :options="{ mode: 'text/x-vue', theme: 'default' }"
  border
  placeholder="test-placeholder"
  :height="200"
  @change="onChange"
  @blur="onBlur"
  @focus="onFocus"
  @scroll="onScroll"
/>
```

> All event names are as follows：

- `changes`
- `scroll`
- `beforeChange`
- `cursorActivity`
- `keyHandled`
- `inputRead`
- `electricInput`
- `beforeSelectionChange`
- `viewportChange`
- `swapDoc`
- `gutterClick`
- `gutterContextMenu`
- `focus`
- `blur`
- `refresh`
- `optionChange`
- `scrollCursorIntoView`
- `update`
