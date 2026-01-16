# vmsg [![npm](https://img.shields.io/npm/v/vmsg.svg)](https://www.npmjs.com/package/vmsg)

vmsg is a small library for creating voice messages. While traditional
way of communicating on the web is via text, sometimes it's easier or
rather funnier to express your thoughts just by saying it. Of course it
doesn't require any special support: record your voice with some
standard program, upload to file hosting and share the link. But why
bother with all of that tedious stuff if you can do the same in browser
with a few clicks.

:confetti_ball: :tada: **[DEMO](https://kagami.github.io/vmsg/)** :tada: :confetti_ball:

## Features

* No dependencies, framework-agnostic, can be easily added to any site
* Small: ~73kb gzipped WASM module and ~3kb gzipped JS + CSS
* Uses MP3 format which is widely supported
* Works in all latest browsers

## Supported browsers

* Chrome 32+
* Firefox 27+
* Safari 11+
* Edge 12+

## Usage

```
npm install vmsg --save
```

```js
import { record } from "vmsg";

someButton.onclick = function() {
  record(/* {wasmURL: "/static/js/vmsg.wasm"} */).then(blob => {
    console.log("Recorded MP3", blob);
    // Can be used like this:
    //
    // const form = new FormData();
    // form.append("file[]", blob, "record.mp3");
    // fetch("/upload.php", {
    //   credentials: "include",
    //   method: "POST",
    //   body: form,
    // }).then(resp => {
    // });
  });
};
```

That's it! Don't forget to include [vmsg.css](vmsg.css) and
[vmsg.wasm](vmsg.wasm) in your project. For browsers without WebAssembly
support you need to also include
[wasm-polyfill.js](https://github.com/Kagami/wasm-polyfill.js).

See [demo](demo) directory for a more feasible example.

A minimal React example for using Recorder with your own UI can be found [here](https://codesandbox.io/s/v67oz43lm7).

See also [non React demo](https://github.com/addpipe/simple-vmsg-demo) and [Recording mp3 audio in HTML5 using vmsg](https://addpipe.com/blog/recording-mp3-audio-in-html5-using-vmsg-a-webassembly-library-based-on-lame/) article.

## Development

1. Install [Emscripten SDK](https://github.com/juj/emsdk).
2. Install latest LLVM, Clang and LLD with WebAssembly backend, fix
   `LLVM_ROOT` variable of Emscripten config.
3. Make sure you have a standard GNU development environment.
4. Activate emsdk environment.
5. ```bash
   git clone --recurse-submodules https://github.com/Kagami/vmsg.git && cd vmsg
   make clean all
   npm install
   npm start
   ```

These instructions are very basic because there're a lot of systems with
different conventions. Docker image would probably be provided to fix it.

## Technical details for nerds

vmsg uses LAME encoder underneath compiled with Emscripten to
WebAssembly module. LAME build is optimized for size, weights only
little more than 70kb gzipped and can be super-efficiently fetched and
parsed by browser. [It's like a small image.](https://twitter.com/wycats/status/942908325775077376)

Access to microphone is implemented with Web Audio API, data samples
sent to Web Worker which is responsibe for loading WebAssembly module
and calling LAME API.

Module is produced with modern LLVM WASM backend and LLD linker which
should become standard soon, also vmsg has own tiny WASM runtime instead
of Emscripten's to decrease overall size and simplify architecture.
Worker code is included in the main JS module so end-user has to care
only about 3 files: `vmsg.js`, `vmsg.css` and `vmsg.wasm`. CSS can be
inlined too but IMO that would be ugly.

In order to support browsers without WebAssembly,
[WebAssembly polyfill](https://github.com/Kagami/wasm-polyfill.js) is
being used. It translates binary module into semantically-equivalent
JavaScript on the fly (almost asm.js compatible but doesn't fully
validate yet) so we don't need separate asm.js build and can use
standard WebAssembly API. It's not as effecient but for audio encoding
should be enough.

**See also:** [Creating WebAssembly-powered library for modern web](https://hackernoon.com/creating-webassembly-powered-library-for-modern-web-846da334f8fc) article.

## Why not MediaRecorder?

[MediaStream Recording API](https://developer.mozilla.org/en-US/docs/Web/API/MediaStream_Recording_API)
is great but:

* Works only in Firefox and Chrome
* Provides little to no options, e.g. VBR quality can't be specified
* Firefox/Chrome encode only to Opus which can't be natively played in Safari and Edge

## But you can use e.g. ogv.js polyfill!

* It make things more complicated, now you need both encoder and decoder
* Opus gives you ~2x bitrate win but for 500kb per minute files it's not that much
* MP3 is much more widespread, so even while compression is not best compatibility matters

## License

vmsg is licensed under [CC0](COPYING).  
LAME is licensed under [LGPL](https://github.com/Kagami/lame-svn/blob/master/lame/COPYING).  
MP3 patents seems to [have expired since April 23, 2017](https://en.wikipedia.org/wiki/LAME#Patents_and_legal_issues).
