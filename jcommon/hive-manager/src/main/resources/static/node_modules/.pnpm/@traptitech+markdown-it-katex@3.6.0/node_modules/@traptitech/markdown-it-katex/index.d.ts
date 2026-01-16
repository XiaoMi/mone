import Md from "markdown-it"
import Katex, { KatexOptions } from "katex"

interface Options extends KatexOptions {
  katex?: typeof Katex
  blockClass?: string
}

declare function katex(md: Md, options: Options): void

export = katex
