const Md = require('markdown-it')
const mdKatex = require('../index');
const testLoad = require('markdown-it-testgen').load
const path = require('path')
const katex = require('katex')
const { test } = require('uvu')
const assert = require('uvu/assert')

const md = Md().use(mdKatex)

const removeLineBreak = str => str.replace(/\n/g, '')

testLoad(path.join(__dirname, 'fixtures/default.txt'), data => {
  data.fixtures.forEach((fixture, i) =>{

    test(`markdown-it-katex: ${i}`, () => {
      const actual = removeLineBreak(md.render(fixture.first.text))

      const displayMode = fixture.second.text[0] === 'b'
      const classStr = displayMode ? ' class="katex-block "' : ''
      const expected = removeLineBreak(
        `<p${classStr}>${katex.renderToString(fixture.second.text.slice(3), {
          displayMode
        })}</p>`
      )

      assert.is(actual, expected)
    })

  })
})

test.run()
