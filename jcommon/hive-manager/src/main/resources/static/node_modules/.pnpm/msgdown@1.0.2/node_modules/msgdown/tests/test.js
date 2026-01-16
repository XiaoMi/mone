const test = require('tape')
const fmt = require('../src/msgdown')

test('Should properly format bold.', t => {
  t.equal(fmt('*pippo*'), '<strong>pippo</strong>')
  t.equal(fmt('*pippo* due'), '<strong>pippo</strong> due')
  t.equal(fmt('uno *pippo* due'), 'uno <strong>pippo</strong> due')
  t.equal(fmt('uno *pippo*'), 'uno <strong>pippo</strong>')
  t.end()
})

test('Should properly format italic.', t => {
  t.equal(fmt('/pippo/'), '<em>pippo</em>')
  t.equal(fmt('/pippo/ due'), '<em>pippo</em> due')
  t.equal(fmt('uno /pippo/ due'), 'uno <em>pippo</em> due')
  t.equal(fmt('uno /pippo/'), 'uno <em>pippo</em>')
  t.end()
})

test('Should properly format underline.', t => {
  t.equal(fmt('_pippo_'), '<u>pippo</u>')
  t.equal(fmt('_pippo_ due'), '<u>pippo</u> due')
  t.equal(fmt('uno _pippo_ due'), 'uno <u>pippo</u> due')
  t.equal(fmt('uno _pippo_'), 'uno <u>pippo</u>')
  t.end()
})

test('Should properly format strike through.', t => {
  t.equal(fmt('~pippo~'), '<del>pippo</del>')
  t.equal(fmt('~pippo~ due'), '<del>pippo</del> due')
  t.equal(fmt('uno ~pippo~ due'), 'uno <del>pippo</del> due')
  t.equal(fmt('uno ~pippo~'), 'uno <del>pippo</del>')
  t.end()
})

test('Should properly format code.', t => {
  t.equal(fmt('`pippo`'), '<code>pippo</code>')
  t.equal(fmt('`pippo` due'), '<code>pippo</code> due')
  t.equal(fmt('uno `pippo` due'), 'uno <code>pippo</code> due')
  t.equal(fmt('uno `pippo`'), 'uno <code>pippo</code>')
  t.end()
})

test('Should properly format sub.', t => {
  t.equal(fmt('¡pippo¡'), '<sub>pippo</sub>')
  t.equal(fmt('¡pippo¡ due'), '<sub>pippo</sub> due')
  t.equal(fmt('uno ¡pippo¡ due'), 'uno <sub>pippo</sub> due')
  t.equal(fmt('uno ¡pippo¡'), 'uno <sub>pippo</sub>')
  t.end()
})

test('Should properly format sup.', t => {
  t.equal(fmt('^pippo^'), '<sup>pippo</sup>')
  t.equal(fmt('^pippo^ due'), '<sup>pippo</sup> due')
  t.equal(fmt('uno ^pippo^ due'), 'uno <sup>pippo</sup> due')
  t.equal(fmt('uno ^pippo^'), 'uno <sup>pippo</sup>')
  t.end()
})

test('Should properly format with custom tokens.', t => {
  const customTokens = {
    bold: { delimiter: '#', tag: 'strong' },
    italic: { delimiter: '%', tag: 'em' },
    underline: { delimiter: '…', tag: 'u' },
    strike: { delimiter: '—', tag: 'del' },
    code: { delimiter: '&', tag: 'code' }
  }

  t.equal(fmt('#pippo#', customTokens), '<strong>pippo</strong>')
  t.equal(fmt('%pippo%', customTokens), '<em>pippo</em>')
  t.equal(fmt('…pippo…', customTokens), '<u>pippo</u>')
  t.equal(fmt('—pippo—', customTokens), '<del>pippo</del>')
  t.equal(fmt('&pippo&', customTokens), '<code>pippo</code>')
  t.equal(fmt('^pippo^', customTokens), '<sup>pippo</sup>')
  t.equal(fmt('¡pippo¡', customTokens), '<sub>pippo</sub>')
  t.end()
})

test('Should not parse anything.', t => {
  [
    'user_name',
    'https://www.dio.it/',
    'dir/',
    'first^',
    '~5',
    '5*4=20'
  ].map(s => {
    t.equal(fmt(s), s)
  })
  t.end()
})

test('Should not epic fail.', t => {
  const msg = `*Attention:* /msgdown/^TM^ is great¡1¡. _Use it_ or ~don't~ it is \`console.log('up to you!')\``
  const oracle = `<strong>Attention:</strong> <em>msgdown</em><sup>TM</sup> is great<sub>1</sub>. <u>Use it</u> or <del>don't</del> it is <code>console.log('up to you!')</code>`
  t.equal(fmt(msg), oracle)
  t.end()
})
