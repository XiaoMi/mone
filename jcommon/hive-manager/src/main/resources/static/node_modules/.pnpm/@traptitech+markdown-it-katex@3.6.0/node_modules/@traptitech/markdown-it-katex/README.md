# markdown-it-katex
[![npm version](https://badge.fury.io/js/%40traptitech%2Fmarkdown-it-katex.svg)](https://badge.fury.io/js/%40traptitech%2Fmarkdown-it-katex)
![check npm ci & run test](https://github.com/traPtitech/markdown-it-katex/workflows/check%20npm%20ci%20&%20run%20test/badge.svg)
![automatic publish](https://github.com/traPtitech/markdown-it-katex/workflows/automatic%20publish/badge.svg)
[![Dependabot Status](https://api.dependabot.com/badges/status?host=github&repo=traPtitech/markdown-it-katex)](https://dependabot.com)

Add KaTeX rendering to your Markdown

## Usage
```shell
$ npm install @traptitech/markdown-it-katex
```

Including KaTeX CSS is needed.

### Options
- `katex`: You can change KaTeX version by passing the instance.
- `blockClass`: Class added to KaTeX block.
- Any KaTeX Options

```js
md.use(mk, {"blockClass": "math-block", "errorColor" : " #cc0000"});
```

## Examples

### Inline
Surround your LaTeX with a single `$` on each side for inline rendering.
```
$\sqrt{3x-1}+(1+x)^2$
```

### Block
Use two (`$$`) for block rendering. This mode uses bigger symbols and centers
the result.

```
$$\begin{array}{c}

\nabla \times \vec{\mathbf{B}} -\, \frac1c\, \frac{\partial\vec{\mathbf{E}}}{\partial t} &
= \frac{4\pi}{c}\vec{\mathbf{j}}    \nabla \cdot \vec{\mathbf{E}} & = 4 \pi \rho \\

\nabla \times \vec{\mathbf{E}}\, +\, \frac1c\, \frac{\partial\vec{\mathbf{B}}}{\partial t} & = \vec{\mathbf{0}} \\

\nabla \cdot \vec{\mathbf{B}} & = 0

\end{array}$$
```

## Syntax

Math parsing in markdown is designed to agree with the conventions set by pandoc:

    Anything between two $ characters will be treated as TeX math. The opening $ must
    have a non-space character immediately to its right, while the closing $ must
    have a non-space character immediately to its left, and must not be followed
    immediately by a digit. Thus, $20,000 and $30,000 won’t parse as math. If for some
    reason you need to enclose text in literal $ characters, backslash-escape them and
    they won’t be treated as math delimiters.

## Math Syntax Support

KaTeX is based on TeX and LaTeX. Support for both is growing. Here's a list of
currently supported functions:

[Function Support in KaTeX](https://katex.org/docs/supported.html)
