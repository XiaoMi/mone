![mp3-mediarecorder header](https://user-images.githubusercontent.com/8850410/72912434-eb674580-3d3b-11ea-8ffc-aa754b8af9d8.png)

# 🎙 mp3-mediarecorder

[![Build Status](https://travis-ci.com/elsmr/mp3-mediarecorder.svg?branch=master)](https://travis-ci.com/elsmr/mp3-mediarecorder) [![NPM Version](https://badge.fury.io/js/mp3-mediarecorder.svg?style=flat)](https://npmjs.org/package/mp3-mediarecorder) [![Live demo](https://img.shields.io/badge/live%20demo-available-blue.svg)](https://mp3-mediarecorder.elsmr.dev)

A [MediaRecorder](https://developer.mozilla.org/en-US/docs/Web/API/MediaRecorder) [ponyfill](https://ponyfill.com) that records audio as mp3. It uses the great [Kagami/vmsg](https://github.com/Kagami/vmsg) library under the hood to encode mp3 audio in WebAssembly using [LAME](http://lame.sourceforge.net/).

View the [live demo](https://mp3-mediarecorder.elsmr.dev)

## Features

-   Standard [MediaRecorder](https://developer.mozilla.org/en-US/docs/Web/API/MediaRecorder) API
-   Audio encoding off the main thread using Web Workers
-   Consistent MP3 file output in all supported browsers
-   High quality type definitions
-   9kB main library
-   80kB Web Worker with WebAssembly module (Loaded async)

## Browser Support

-   Chrome 57+
-   Firefox 52+
-   Safari 11+
-   Edge 16+

## Installation

Install with npm or yarn.

```shell
yarn add mp3-mediarecorder
```

If you don't want to set up a build environment, you can get mp3-mediarecorder from a CDN like unpkg.com and it will be globally available through the window.mp3MediaRecorder object.

```html
<script src="https://unpkg.com/mp3-mediarecorder"></script>
```

## Usage

We'll have two files: `index.js` and `worker.js`. The first is what we import from our app, so it runs on the main thread — it imports our worker (using worker-loader or workerize-loader) and passes it to `Mp3MediaRecorder` to create a recorder instance around it.

### index.js

```ts
import { Mp3MediaRecorder } from 'mp3-mediarecorder';
import Mp3RecorderWorker from 'workerize-loader!./worker';

const recorder = new Mp3MediaRecorder(
    mediaStream, // MediaStream instance
    { worker: Mp3RecorderWorker() }
);
recorder.start(); // 🎉
```

In most cases the MediaStream instance will come from the [getUserMedia API](https://developer.mozilla.org/en-US/docs/Web/API/MediaDevices/getUserMedia). For a usage example, see [here](https://github.com/elsmr/mp3-mediarecorder/blob/master/examples/react/src/App.js#L18-L19).

### worker.js

```ts
import { initMp3MediaEncoder } from 'mp3-mediarecorder/worker';

initMp3MediaEncoder({ vmsgWasmUrl: '/url/to/vmsg.wasm' });
```

The second file is our worker code, which runs in the background thread. Here we import `initMp3MediaEncoder` from `mp3-mediarecorder/worker`. This sets things up to communicate with the main thread.

## API

### module:mp3-mediarecorder

#### Mp3MediaRecorder

Mp3MediaRecorder is a class that has the same API as the standard [MediaRecorder](https://developer.mozilla.org/en-US/docs/Web/API/MediaRecorder). If you want to see the full API please check out [the documentation on MDN](https://developer.mozilla.org/en-US/docs/Web/API/MediaRecorder).

**Constructor parameters**

The Mp3MediaRecorder constructor parameters differ from the standard API.

-   `mediaStream: MediaStream` An instance of **[MediaStream](https://developer.mozilla.org/en-US/docs/Web/API/MediaStream)** (eg: from [getUserMedia](https://developer.mozilla.org/en-US/docs/Web/API/MediaDevices/getUserMedia))

-   `options: Mp3MediaRecorderOptions`
    -   `worker: Worker` An instantiated **[Web Worker](https://developer.mozilla.org/docs/Web/JavaScript)** (eg: `new Worker('./worker.js')`)
    -   `audioContext?: AudioContext`An instantiated **[AudioContext](https://developer.mozilla.org/docs/Web/JavaScript)** (eg: `new AudioContext()`)
        This might be useful if you want to full control over the AudioContext. Chrome and Safari limit the number of AudioContext objects.

**Example**

```ts
const recorder = new Mp3MediaRecorder(
    mediaStream, // MediaStream instance
    {
        worker: Mp3RecorderWorker(),
        // Optionally supply your own AudioContext
        audioContext: new AudioContext(),
    }
);
```

### module:mp3-mediarecorder/worker

The [Web Worker](https://developer.mozilla.org/docs/Web/JavaScript) side the of the recorder. The worker will communicate with the main thread to encode the mp3 file.

#### initMp3MediaEncoder

Sets up the communication with the main thread.

**Parameters**

-   `vmsgWasmUrl: string` The URL of the `vmsg.wasm` file.
    This could be self-hosted or from a CDN. The Worker fill fetch this URL and instantiate a WebAssembly module from it.

**Example**

```ts
import { initMp3MediaEncoder } from 'mp3-mediarecorder/worker';

initMp3MediaEncoder({ vmsgWasmUrl: '/url/to/vmsg.wasm' });
```

## Why

Browser support for MediaRecorder is [lacking](https://caniuse.com/#feat=mediarecorder).

Even in browsers with support for MediaRecorder, the available audio formats differ between browsers, and are not always compatible with other browsers. MP3 is the only audio format that can be played [by all modern browsers](https://developer.mozilla.org/en-US/docs/Web/HTML/Supported_media_formats#Browser_compatibility).

[Kagami/vmsg](https://github.com/Kagami/vmsg) is a great library but I needed something that doesn't include a UI and/or getUserMedia code.

## Limitations

-   In Safari, pause and resume does not work (see [#60](https://github.com/elsmr/mp3-mediarecorder/issues/60))
-   The `dataavailable` event only fires once, when encoding is complete. `MediaRecorder.start` ignores its optional `timeSlice` argument. As a result,`MediaRecorder.requestData` does not trigger a `dataavailable` event
-   `bitsPerSecond` is not configurable, the `MediaRecorder` constructor will ignore this option.

## Develop

```
yarn dev
```

A development version of the demo will be served on http://localhost:1234.

## Related

-   [Kagami/vmsg](https://github.com/Kagami/vmsg): Use this library if you want a more complete microphone recording library with a built-in UI
