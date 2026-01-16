"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.record = record;
exports.default = exports.Form = exports.Recorder = void 0;

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

/* eslint-disable */
function pad2(n) {
  n |= 0;
  return n < 10 ? "0".concat(n) : "".concat(Math.min(n, 99));
}

function inlineWorker() {
  // TODO(Kagami): Cache compiled module in IndexedDB? It works in FF
  // and Edge, see: https://github.com/mdn/webassembly-examples/issues/4
  // Though gzipped WASM module currently weights ~70kb so it should be
  // perfectly cached by the browser itself.
  function fetchAndInstantiate(url, imports) {
    if (!WebAssembly.instantiateStreaming) return fetchAndInstantiateFallback(url, imports);
    var req = fetch(url, {
      credentials: "same-origin"
    });
    return WebAssembly.instantiateStreaming(req, imports).catch(function (err) {
      // https://github.com/Kagami/vmsg/issues/11
      if (err.message && err.message.indexOf("Argument 0 must be provided and must be a Response") > 0) {
        return fetchAndInstantiateFallback(url, imports);
      } else {
        throw err;
      }
    });
  }

  function fetchAndInstantiateFallback(url, imports) {
    return new Promise(function (resolve, reject) {
      var req = new XMLHttpRequest();
      req.open("GET", url);
      req.responseType = "arraybuffer";

      req.onload = function () {
        resolve(WebAssembly.instantiate(req.response, imports));
      };

      req.onerror = reject;
      req.send();
    });
  } // Must be in sync with emcc settings!


  var TOTAL_STACK = 5 * 1024 * 1024;
  var TOTAL_MEMORY = 16 * 1024 * 1024;
  var WASM_PAGE_SIZE = 64 * 1024;
  var memory = null;
  var dynamicTop = TOTAL_STACK; // TODO(Kagami): Grow memory?

  function sbrk(increment) {
    var oldDynamicTop = dynamicTop;
    dynamicTop += increment;
    return oldDynamicTop;
  } // TODO(Kagami): LAME calls exit(-1) on internal error. Would be nice
  // to provide custom DEBUGF/ERRORF for easier debugging. Currenty
  // those functions do nothing.


  function exit(status) {
    postMessage({
      type: "internal-error",
      data: status
    });
  }

  var FFI = null;
  var ref = null;
  var pcm_l = null;

  function vmsg_init(rate) {
    ref = FFI.vmsg_init(rate);
    if (!ref) return false;
    var pcm_l_ref = new Uint32Array(memory.buffer, ref, 1)[0];
    pcm_l = new Float32Array(memory.buffer, pcm_l_ref);
    return true;
  }

  function vmsg_encode(data) {
    pcm_l.set(data);
    return FFI.vmsg_encode(ref, data.length) >= 0;
  }

  function vmsg_flush() {
    if (FFI.vmsg_flush(ref) < 0) return null;
    var mp3_ref = new Uint32Array(memory.buffer, ref + 4, 1)[0];
    var size = new Uint32Array(memory.buffer, ref + 8, 1)[0];
    var mp3 = new Uint8Array(memory.buffer, mp3_ref, size);
    var blob = new Blob([mp3], {
      type: "audio/mpeg"
    });
    FFI.vmsg_free(ref);
    ref = null;
    pcm_l = null;
    return blob;
  } // https://github.com/brion/min-wasm-fail


  function testSafariWebAssemblyBug() {
    var bin = new Uint8Array([0, 97, 115, 109, 1, 0, 0, 0, 1, 6, 1, 96, 1, 127, 1, 127, 3, 2, 1, 0, 5, 3, 1, 0, 1, 7, 8, 1, 4, 116, 101, 115, 116, 0, 0, 10, 16, 1, 14, 0, 32, 0, 65, 1, 54, 2, 0, 32, 0, 40, 2, 0, 11]);
    var mod = new WebAssembly.Module(bin);
    var inst = new WebAssembly.Instance(mod, {}); // test storing to and loading from a non-zero location via a parameter.
    // Safari on iOS 11.2.5 returns 0 unexpectedly at non-zero locations

    return inst.exports.test(4) !== 0;
  }

  onmessage = function onmessage(e) {
    var msg = e.data;

    switch (msg.type) {
      case "init":
        var _msg$data = msg.data,
            wasmURL = _msg$data.wasmURL,
            shimURL = _msg$data.shimURL;
        Promise.resolve().then(function () {
          if (self.WebAssembly && !testSafariWebAssemblyBug()) {
            delete self.WebAssembly;
          }

          if (!self.WebAssembly) {
            importScripts(shimURL);
          }

          memory = new WebAssembly.Memory({
            initial: TOTAL_MEMORY / WASM_PAGE_SIZE,
            maximum: TOTAL_MEMORY / WASM_PAGE_SIZE
          });
          return {
            memory: memory,
            pow: Math.pow,
            exit: exit,
            powf: Math.pow,
            exp: Math.exp,
            sqrtf: Math.sqrt,
            cos: Math.cos,
            log: Math.log,
            sin: Math.sin,
            sbrk: sbrk
          };
        }).then(function (Runtime) {
          return fetchAndInstantiate(wasmURL, {
            env: Runtime
          });
        }).then(function (wasm) {
          FFI = wasm.instance.exports;
          postMessage({
            type: "init",
            data: null
          });
        }).catch(function (err) {
          postMessage({
            type: "init-error",
            data: err.toString()
          });
        });
        break;

      case "start":
        if (!vmsg_init(msg.data)) return postMessage({
          type: "error",
          data: "vmsg_init"
        });
        break;

      case "data":
        if (!vmsg_encode(msg.data)) return postMessage({
          type: "error",
          data: "vmsg_encode"
        });
        break;

      case "stop":
        var blob = vmsg_flush();
        if (!blob) return postMessage({
          type: "error",
          data: "vmsg_flush"
        });
        postMessage({
          type: "stop",
          data: blob
        });
        break;
    }
  };
}

var Recorder =
/*#__PURE__*/
function () {
  function Recorder() {
    var opts = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : {};
    var onStop = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : null;

    _classCallCheck(this, Recorder);

    // Can't use relative URL in blob worker, see:
    // https://stackoverflow.com/a/22582695
    this.wasmURL = new URL(opts.wasmURL || "/static/js/vmsg.wasm", location).href;
    this.shimURL = new URL(opts.shimURL || "/static/js/wasm-polyfill.js", location).href;
    this.onStop = onStop;
    this.pitch = opts.pitch || 0;
    this.stream = null;
    this.audioCtx = null;
    this.gainNode = null;
    this.pitchFX = null;
    this.encNode = null;
    this.worker = null;
    this.workerURL = null;
    this.blob = null;
    this.blobURL = null;
    this.resolve = null;
    this.reject = null;
    Object.seal(this);
  }

  _createClass(Recorder, [{
    key: "close",
    value: function close() {
      if (this.encNode) this.encNode.disconnect();
      if (this.encNode) this.encNode.onaudioprocess = null;
      if (this.stream) this.stopTracks();
      if (this.audioCtx) this.audioCtx.close();

      if (this.worker) {
        this.worker.terminate();
        this.worker = null;
      }

      if (this.workerURL) URL.revokeObjectURL(this.workerURL);
      if (this.blobURL) URL.revokeObjectURL(this.blobURL);
    } // Without pitch shift:
    //   [sourceNode] -> [gainNode] -> [encNode] -> [audioCtx.destination]
    //                                     |
    //                                     -> [worker]
    // With pitch shift:
    //   [sourceNode] -> [gainNode] -> [pitchFX] -> [encNode] -> [audioCtx.destination]
    //                                                  |
    //                                                  -> [worker]

  }, {
    key: "initAudio",
    value: function initAudio() {
      var _this = this;

      var getUserMedia = navigator.mediaDevices && navigator.mediaDevices.getUserMedia ? function (constraints) {
        return navigator.mediaDevices.getUserMedia(constraints);
      } : function (constraints) {
        var oldGetUserMedia = navigator.webkitGetUserMedia || navigator.mozGetUserMedia;

        if (!oldGetUserMedia) {
          return Promise.reject(new Error("getUserMedia is not implemented in this browser"));
        }

        return new Promise(function (resolve, reject) {
          oldGetUserMedia.call(navigator, constraints, resolve, reject);
        });
      };
      return getUserMedia({
        audio: true
      }).then(function (stream) {
        _this.stream = stream;
        var audioCtx = _this.audioCtx = new (window.AudioContext || window.webkitAudioContext)();
        var sourceNode = audioCtx.createMediaStreamSource(stream);
        var gainNode = _this.gainNode = (audioCtx.createGain || audioCtx.createGainNode).call(audioCtx);
        gainNode.gain.value = 1;
        sourceNode.connect(gainNode);
        var pitchFX = _this.pitchFX = new Jungle(audioCtx);
        pitchFX.setPitchOffset(_this.pitch);
        var encNode = _this.encNode = (audioCtx.createScriptProcessor || audioCtx.createJavaScriptNode).call(audioCtx, 0, 1, 1);
        pitchFX.output.connect(encNode);
        gainNode.connect(_this.pitch === 0 ? encNode : pitchFX.input);
      });
    }
  }, {
    key: "initWorker",
    value: function initWorker() {
      var _this2 = this;

      if (this.worker) return Promise.resolve(); // https://stackoverflow.com/a/19201292

      var blob = new Blob(["(", inlineWorker.toString(), ")()"], {
        type: "application/javascript"
      });
      var workerURL = this.workerURL = URL.createObjectURL(blob);
      var worker = this.worker = new Worker(workerURL);
      var wasmURL = this.wasmURL,
          shimURL = this.shimURL;
      worker.postMessage({
        type: "init",
        data: {
          wasmURL: wasmURL,
          shimURL: shimURL
        }
      });
      return new Promise(function (resolve, reject) {
        worker.onmessage = function (e) {
          var msg = e.data;

          switch (msg.type) {
            case "init":
              resolve();
              break;

            case "init-error":
              _this2.close();

              reject(new Error(msg.data));
              break;
            // TODO(Kagami): Error handling.

            case "error":
            case "internal-error":
              _this2.close();

              console.error("Worker error:", msg.data);
              if (_this2.reject) _this2.reject(msg.data);
              break;

            case "stop":
              _this2.blob = msg.data;
              _this2.blobURL = URL.createObjectURL(msg.data);
              if (_this2.onStop) _this2.onStop();
              if (_this2.resolve) _this2.resolve(_this2.blob);
              break;
          }
        };
      });
    }
  }, {
    key: "init",
    value: function init() {
      return this.initAudio().then(this.initWorker.bind(this));
    }
  }, {
    key: "startRecording",
    value: function startRecording() {
      var _this3 = this;

      if (!this.stream) throw new Error("missing audio initialization");
      if (!this.worker) throw new Error("missing worker initialization");
      this.blob = null;
      if (this.blobURL) URL.revokeObjectURL(this.blobURL);
      this.blobURL = null;
      this.resolve = null;
      this.reject = null;
      this.worker.postMessage({
        type: "start",
        data: this.audioCtx.sampleRate
      });

      this.encNode.onaudioprocess = function (e) {
        var samples = e.inputBuffer.getChannelData(0);

        _this3.worker.postMessage({
          type: "data",
          data: samples
        });
      };

      this.encNode.connect(this.audioCtx.destination);
    }
  }, {
    key: "stopRecording",
    value: function stopRecording() {
      var _this4 = this;

      if (!this.stream) throw new Error("missing audio initialization");
      if (!this.worker) throw new Error("missing worker initialization");
      this.encNode.disconnect();
      this.encNode.onaudioprocess = null;
      this.stopTracks();
      this.audioCtx.close();
      this.worker.postMessage({
        type: "stop",
        data: null
      });
      return new Promise(function (resolve, reject) {
        _this4.resolve = resolve;
        _this4.reject = reject;
      });
    }
  }, {
    key: "stopTracks",
    value: function stopTracks() {
      // Might be missed in Safari and old FF/Chrome per MDN.
      if (this.stream.getTracks) {
        // Hide browser's recording indicator.
        this.stream.getTracks().forEach(function (track) {
          return track.stop();
        });
      }
    }
  }]);

  return Recorder;
}();

exports.Recorder = Recorder;

var Form =
/*#__PURE__*/
function () {
  function Form() {
    var _this5 = this;

    var opts = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : {};
    var resolve = arguments.length > 1 ? arguments[1] : undefined;
    var reject = arguments.length > 2 ? arguments[2] : undefined;

    _classCallCheck(this, Form);

    this.recorder = new Recorder(opts, this.onStop.bind(this));
    this.resolve = resolve;
    this.reject = reject;
    this.backdrop = null;
    this.popup = null;
    this.recordBtn = null;
    this.stopBtn = null;
    this.timer = null;
    this.audio = null;
    this.saveBtn = null;
    this.tid = 0;
    this.start = 0;
    Object.seal(this);
    this.recorder.initAudio().then(function () {
      return _this5.drawInit();
    }).then(function () {
      return _this5.recorder.initWorker();
    }).then(function () {
      return _this5.drawAll();
    }).catch(function (err) {
      return _this5.drawError(err);
    });
  }

  _createClass(Form, [{
    key: "drawInit",
    value: function drawInit() {
      var _this6 = this;

      if (this.backdrop) return;
      var backdrop = this.backdrop = document.createElement("div");
      backdrop.className = "vmsg-backdrop";
      backdrop.addEventListener("click", function () {
        return _this6.close(null);
      });
      var popup = this.popup = document.createElement("div");
      popup.className = "vmsg-popup";
      popup.addEventListener("click", function (e) {
        return e.stopPropagation();
      });
      var progress = document.createElement("div");
      progress.className = "vmsg-progress";

      for (var i = 0; i < 3; i++) {
        var progressDot = document.createElement("div");
        progressDot.className = "vmsg-progress-dot";
        progress.appendChild(progressDot);
      }

      popup.appendChild(progress);
      backdrop.appendChild(popup);
      document.body.appendChild(backdrop);
    }
  }, {
    key: "drawTime",
    value: function drawTime(msecs) {
      var secs = Math.round(msecs / 1000);
      this.timer.textContent = pad2(secs / 60) + ":" + pad2(secs % 60);
    }
  }, {
    key: "drawAll",
    value: function drawAll() {
      var _this7 = this;

      this.drawInit();
      this.clearAll();
      var recordRow = document.createElement("div");
      recordRow.className = "vmsg-record-row";
      this.popup.appendChild(recordRow);
      var recordBtn = this.recordBtn = document.createElement("button");
      recordBtn.className = "vmsg-button vmsg-record-button";
      recordBtn.textContent = "●";
      recordBtn.title = "Start Recording";
      recordBtn.addEventListener("click", function () {
        return _this7.startRecording();
      });
      recordRow.appendChild(recordBtn);
      var stopBtn = this.stopBtn = document.createElement("button");
      stopBtn.className = "vmsg-button vmsg-stop-button";
      stopBtn.style.display = "none";
      stopBtn.textContent = "■";
      stopBtn.title = "Stop Recording";
      stopBtn.addEventListener("click", function () {
        return _this7.stopRecording();
      });
      recordRow.appendChild(stopBtn);
      var audio = this.audio = new Audio();
      audio.autoplay = true;
      var timer = this.timer = document.createElement("span");
      timer.className = "vmsg-timer";
      timer.title = "Preview Recording";
      timer.addEventListener("click", function () {
        if (audio.paused) {
          if (_this7.recorder.blobURL) {
            audio.src = _this7.recorder.blobURL;
          }
        } else {
          audio.pause();
        }
      });
      this.drawTime(0);
      recordRow.appendChild(timer);
      var saveBtn = this.saveBtn = document.createElement("button");
      saveBtn.className = "vmsg-button vmsg-save-button";
      saveBtn.textContent = "✓";
      saveBtn.title = "Save Recording";
      saveBtn.disabled = true;
      saveBtn.addEventListener("click", function () {
        return _this7.close(_this7.recorder.blob);
      });
      recordRow.appendChild(saveBtn);
      var gainWrapper = document.createElement("div");
      gainWrapper.className = "vmsg-slider-wrapper vmsg-gain-slider-wrapper";
      var gainSlider = document.createElement("input");
      gainSlider.className = "vmsg-slider vmsg-gain-slider";
      gainSlider.setAttribute("type", "range");
      gainSlider.min = 0;
      gainSlider.max = 2;
      gainSlider.step = 0.2;
      gainSlider.value = 1;

      gainSlider.onchange = function () {
        var gain = +gainSlider.value;
        _this7.recorder.gainNode.gain.value = gain;
      };

      gainWrapper.appendChild(gainSlider);
      this.popup.appendChild(gainWrapper);
      var pitchWrapper = document.createElement("div");
      pitchWrapper.className = "vmsg-slider-wrapper vmsg-pitch-slider-wrapper";
      var pitchSlider = document.createElement("input");
      pitchSlider.className = "vmsg-slider vmsg-pitch-slider";
      pitchSlider.setAttribute("type", "range");
      pitchSlider.min = -1;
      pitchSlider.max = 1;
      pitchSlider.step = 0.2;
      pitchSlider.value = this.recorder.pitch;

      pitchSlider.onchange = function () {
        var pitch = +pitchSlider.value;

        _this7.recorder.pitchFX.setPitchOffset(pitch);

        _this7.recorder.gainNode.disconnect();

        _this7.recorder.gainNode.connect(pitch === 0 ? _this7.recorder.encNode : _this7.recorder.pitchFX.input);
      };

      pitchWrapper.appendChild(pitchSlider);
      this.popup.appendChild(pitchWrapper);
      recordBtn.focus();
    }
  }, {
    key: "drawError",
    value: function drawError(err) {
      console.error(err);
      this.drawInit();
      this.clearAll();
      var error = document.createElement("div");
      error.className = "vmsg-error";
      error.textContent = err.toString();
      this.popup.appendChild(error);
    }
  }, {
    key: "clearAll",
    value: function clearAll() {
      if (!this.popup) return;
      this.popup.innerHTML = "";
    }
  }, {
    key: "close",
    value: function close(blob) {
      if (this.audio) this.audio.pause();
      if (this.tid) clearTimeout(this.tid);
      this.recorder.close();
      this.backdrop.remove();

      if (blob) {
        this.resolve(blob);
      } else {
        this.reject(new Error("No record made"));
      }
    }
  }, {
    key: "onStop",
    value: function onStop() {
      this.recordBtn.style.display = "";
      this.stopBtn.style.display = "none";
      this.stopBtn.disabled = false;
      this.saveBtn.disabled = false;
    }
  }, {
    key: "startRecording",
    value: function startRecording() {
      this.audio.pause();
      this.start = Date.now();
      this.updateTime();
      this.recordBtn.style.display = "none";
      this.stopBtn.style.display = "";
      this.saveBtn.disabled = true;
      this.stopBtn.focus();
      this.recorder.startRecording();
    }
  }, {
    key: "stopRecording",
    value: function stopRecording() {
      clearTimeout(this.tid);
      this.tid = 0;
      this.stopBtn.disabled = true;
      this.recordBtn.focus();
      this.recorder.stopRecording();
    }
  }, {
    key: "updateTime",
    value: function updateTime() {
      var _this8 = this;

      // NOTE(Kagami): We can do this in `onaudioprocess` but that would
      // run too often and create unnecessary DOM updates.
      this.drawTime(Date.now() - this.start);
      this.tid = setTimeout(function () {
        return _this8.updateTime();
      }, 300);
    }
  }]);

  return Form;
}();

exports.Form = Form;
var shown = false;
/**
 * Record a new voice message.
 *
 * @param {Object=} opts - Options
 * @param {string=} opts.wasmURL - URL of the module
 *                                 ("/static/js/vmsg.wasm" by default)
 * @param {string=} opts.shimURL - URL of the WebAssembly polyfill
 *                                 ("/static/js/wasm-polyfill.js" by default)
 * @param {number=} opts.pitch - Initial pitch shift ([-1, 1], 0 by default)
 * @return {Promise.<Blob>} A promise that contains recorded blob when fulfilled.
 */

function record(opts) {
  return new Promise(function (resolve, reject) {
    if (shown) throw new Error("Record form is already opened");
    shown = true;
    new Form(opts, resolve, reject); // Use `.finally` once it's available in Safari and Edge.
  }).then(function (result) {
    shown = false;
    return result;
  }, function (err) {
    shown = false;
    throw err;
  });
}
/**
 * All available public items.
 */


var _default = {
  Recorder: Recorder,
  Form: Form,
  record: record
}; // Borrowed from and slightly modified:
// https://github.com/cwilso/Audio-Input-Effects/blob/master/js/jungle.js
// Copyright 2012, Google Inc.
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are
// met:
//
//     * Redistributions of source code must retain the above copyright
// notice, this list of conditions and the following disclaimer.
//     * Redistributions in binary form must reproduce the above
// copyright notice, this list of conditions and the following disclaimer
// in the documentation and/or other materials provided with the
// distribution.
//     * Neither the name of Google Inc. nor the names of its
// contributors may be used to endorse or promote products derived from
// this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

exports.default = _default;
var delayTime = 0.100;
var fadeTime = 0.050;
var bufferTime = 0.100;

function createFadeBuffer(context, activeTime, fadeTime) {
  var length1 = activeTime * context.sampleRate;
  var length2 = (activeTime - 2 * fadeTime) * context.sampleRate;
  var length = length1 + length2;
  var buffer = context.createBuffer(1, length, context.sampleRate);
  var p = buffer.getChannelData(0);
  var fadeLength = fadeTime * context.sampleRate;
  var fadeIndex1 = fadeLength;
  var fadeIndex2 = length1 - fadeLength; // 1st part of cycle

  for (var i = 0; i < length1; ++i) {
    var value;

    if (i < fadeIndex1) {
      value = Math.sqrt(i / fadeLength);
    } else if (i >= fadeIndex2) {
      value = Math.sqrt(1 - (i - fadeIndex2) / fadeLength);
    } else {
      value = 1;
    }

    p[i] = value;
  } // 2nd part


  for (var i = length1; i < length; ++i) {
    p[i] = 0;
  }

  return buffer;
}

function createDelayTimeBuffer(context, activeTime, fadeTime, shiftUp) {
  var length1 = activeTime * context.sampleRate;
  var length2 = (activeTime - 2 * fadeTime) * context.sampleRate;
  var length = length1 + length2;
  var buffer = context.createBuffer(1, length, context.sampleRate);
  var p = buffer.getChannelData(0); // 1st part of cycle

  for (var i = 0; i < length1; ++i) {
    if (shiftUp) // This line does shift-up transpose
      p[i] = (length1 - i) / length;else // This line does shift-down transpose
      p[i] = i / length1;
  } // 2nd part


  for (var i = length1; i < length; ++i) {
    p[i] = 0;
  }

  return buffer;
}

function Jungle(context) {
  this.context = context; // Create nodes for the input and output of this "module".

  var input = (context.createGain || context.createGainNode).call(context);
  var output = (context.createGain || context.createGainNode).call(context);
  this.input = input;
  this.output = output; // Delay modulation.

  var mod1 = context.createBufferSource();
  var mod2 = context.createBufferSource();
  var mod3 = context.createBufferSource();
  var mod4 = context.createBufferSource();
  this.shiftDownBuffer = createDelayTimeBuffer(context, bufferTime, fadeTime, false);
  this.shiftUpBuffer = createDelayTimeBuffer(context, bufferTime, fadeTime, true);
  mod1.buffer = this.shiftDownBuffer;
  mod2.buffer = this.shiftDownBuffer;
  mod3.buffer = this.shiftUpBuffer;
  mod4.buffer = this.shiftUpBuffer;
  mod1.loop = true;
  mod2.loop = true;
  mod3.loop = true;
  mod4.loop = true; // for switching between oct-up and oct-down

  var mod1Gain = (context.createGain || context.createGainNode).call(context);
  var mod2Gain = (context.createGain || context.createGainNode).call(context);
  var mod3Gain = (context.createGain || context.createGainNode).call(context);
  mod3Gain.gain.value = 0;
  var mod4Gain = (context.createGain || context.createGainNode).call(context);
  mod4Gain.gain.value = 0;
  mod1.connect(mod1Gain);
  mod2.connect(mod2Gain);
  mod3.connect(mod3Gain);
  mod4.connect(mod4Gain); // Delay amount for changing pitch.

  var modGain1 = (context.createGain || context.createGainNode).call(context);
  var modGain2 = (context.createGain || context.createGainNode).call(context);
  var delay1 = (context.createDelay || context.createDelayNode).call(context);
  var delay2 = (context.createDelay || context.createDelayNode).call(context);
  mod1Gain.connect(modGain1);
  mod2Gain.connect(modGain2);
  mod3Gain.connect(modGain1);
  mod4Gain.connect(modGain2);
  modGain1.connect(delay1.delayTime);
  modGain2.connect(delay2.delayTime); // Crossfading.

  var fade1 = context.createBufferSource();
  var fade2 = context.createBufferSource();
  var fadeBuffer = createFadeBuffer(context, bufferTime, fadeTime);
  fade1.buffer = fadeBuffer;
  fade2.buffer = fadeBuffer;
  fade1.loop = true;
  fade2.loop = true;
  var mix1 = (context.createGain || context.createGainNode).call(context);
  var mix2 = (context.createGain || context.createGainNode).call(context);
  mix1.gain.value = 0;
  mix2.gain.value = 0;
  fade1.connect(mix1.gain);
  fade2.connect(mix2.gain); // Connect processing graph.

  input.connect(delay1);
  input.connect(delay2);
  delay1.connect(mix1);
  delay2.connect(mix2);
  mix1.connect(output);
  mix2.connect(output); // Start

  var t = context.currentTime + 0.050;
  var t2 = t + bufferTime - fadeTime;
  mod1.start(t);
  mod2.start(t2);
  mod3.start(t);
  mod4.start(t2);
  fade1.start(t);
  fade2.start(t2);
  this.mod1 = mod1;
  this.mod2 = mod2;
  this.mod1Gain = mod1Gain;
  this.mod2Gain = mod2Gain;
  this.mod3Gain = mod3Gain;
  this.mod4Gain = mod4Gain;
  this.modGain1 = modGain1;
  this.modGain2 = modGain2;
  this.fade1 = fade1;
  this.fade2 = fade2;
  this.mix1 = mix1;
  this.mix2 = mix2;
  this.delay1 = delay1;
  this.delay2 = delay2;
  this.setDelay(delayTime);
}

Jungle.prototype.setDelay = function (delayTime) {
  this.modGain1.gain.setTargetAtTime(0.5 * delayTime, 0, 0.010);
  this.modGain2.gain.setTargetAtTime(0.5 * delayTime, 0, 0.010);
};

Jungle.prototype.setPitchOffset = function (mult) {
  if (mult > 0) {
    // pitch up
    this.mod1Gain.gain.value = 0;
    this.mod2Gain.gain.value = 0;
    this.mod3Gain.gain.value = 1;
    this.mod4Gain.gain.value = 1;
  } else {
    // pitch down
    this.mod1Gain.gain.value = 1;
    this.mod2Gain.gain.value = 1;
    this.mod3Gain.gain.value = 0;
    this.mod4Gain.gain.value = 0;
  }

  this.setDelay(delayTime * Math.abs(mult));
};
