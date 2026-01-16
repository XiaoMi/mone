msgdown
======

A simple message formatter for bold, strikethrough, underline, sub, sup, italic and code.


![msgdown](https://img.shields.io/travis/mattmezza/msgdown.svg?style=for-the-badge)

![logo](https://github.com/mattmezza/msgdown/blob/master/logo.png)

### Installation

`yarn add msgdown`

### Usage

```js
const down = require('msgdown')

const msg = `*Attention:* /msgdown/^TM^ is great¡1¡. _Use it_ or ~don\'t~ it is \`console.log('up to you!')\``
const html = down(msg)
```

`html` will contain exactly this:

```html
<strong>Attention:</strong> <em>msgdown</em><sup>TM</sup> is great<sub>1</sub>. <u>Use it</u> or <del>don\'t</del> it is <code>console.log('up to you!')</code>
```

#### Custom tokens

By default `msgdown` will use these tokens:

```js
const defaultTokens = {
  bold: {delimiter: '*', tag: 'strong'},
  italic: {delimiter: '/', tag: 'em'},
  underline: {delimiter: '_', tag: 'u'},
  strike: {delimiter: '~', tag: 'del'},
  code: {delimiter: '`', tag: 'code'},
  sup: {delimiter: '^', tag: 'sup'},
  sub: {delimiter: '¡', tag: 'sub'}
}
```

You can override every and each of the tokens by passing in your custom token object on every function call:

```js
const html = down('ßI am bold with a bß', {bold: {delmiter: 'ß', tag: 'b'}})
```

The above `html` will contain `<b>I am bold with a b</b>`.

### Testing

There is a test file linked to `yarn test`. Should be enough even though enough is never enough.

### Development

Maybe add more tokens? Maybe add pattern matching instead of plain one char matching? Maybe something else? You name it...

### Author

Matteo Merola <mattmezza@gmail.com>
