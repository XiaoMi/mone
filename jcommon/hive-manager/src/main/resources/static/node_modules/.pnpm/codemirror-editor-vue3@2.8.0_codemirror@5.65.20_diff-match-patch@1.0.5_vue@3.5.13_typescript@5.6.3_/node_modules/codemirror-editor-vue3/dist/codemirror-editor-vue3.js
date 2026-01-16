import { defineComponent as E, ref as k, onMounted as N, markRaw as T, watch as _, unref as y, openBlock as C, createElementBlock as O, computed as I, nextTick as K, shallowRef as Q, getCurrentInstance as X, onBeforeUnmount as Y, normalizeClass as ee, normalizeStyle as te, createBlock as ne, resolveDynamicComponent as re, mergeProps as oe } from "vue";
import "codemirror/lib/codemirror.css";
import "codemirror/addon/fold/foldgutter.css";
import "codemirror/addon/fold/foldcode.js";
import "codemirror/addon/fold/foldgutter.js";
import "codemirror/addon/fold/brace-fold.js";
import "codemirror/addon/selection/active-line.js";
import B from "codemirror";
import "codemirror/addon/merge/merge.css";
import "codemirror/addon/merge/merge.js";
import se from "diff-match-patch";
import "codemirror/addon/mode/simple.js";
!window.CodeMirror && (window.CodeMirror = B);
const b = window.CodeMirror || B, ae = E({
  name: "DefaultMode",
  props: {
    name: {
      type: String,
      default: `cm-textarea-${+/* @__PURE__ */ new Date()}`
    },
    value: {
      type: String,
      default: ""
    },
    content: {
      type: String,
      default: ""
    },
    options: {
      type: Object,
      default: () => ({})
    },
    cminstance: {
      type: Object,
      default: () => null
    },
    placeholder: {
      type: String,
      default: ""
    }
  },
  emits: {
    ready: (e) => e,
    "update:cminstance": (e) => e
  },
  setup(e, { emit: n }) {
    const o = k(), t = k(null), r = () => {
      t.value = T(b.fromTextArea(o.value, e.options)), n("update:cminstance", t.value);
      const s = _(
        () => e.cminstance,
        (l) => {
          var g;
          l && ((g = e.cminstance) == null || g.setValue(e.value || e.content)), n("ready", y(t)), s == null || s();
        },
        { deep: !0 }
      );
    };
    return N(() => {
      r();
    }), {
      textarea: o,
      initialize: r
    };
  }
}), A = (e, n) => {
  const o = e.__vccOpts || e;
  for (const [t, r] of n)
    o[t] = r;
  return o;
}, le = ["name", "placeholder"];
function ie(e, n, o, t, r, s) {
  return C(), O("textarea", {
    ref: "textarea",
    name: e.$props.name,
    placeholder: e.$props.placeholder
  }, null, 8, le);
}
const H = /* @__PURE__ */ A(ae, [["render", ie]]);
window.diff_match_patch = se;
window.DIFF_DELETE = -1;
window.DIFF_INSERT = 1;
window.DIFF_EQUAL = 0;
const ce = E({
  name: "MergeMode",
  props: {
    options: {
      type: Object,
      default: () => ({})
    },
    cminstance: {
      type: Object,
      default: () => ({})
    }
  },
  emits: ["update:cminstance", "ready"],
  setup(e, { emit: n }) {
    const o = k(), t = k(), r = () => {
      o.value = T(b.MergeView(t.value, e.options)), n("update:cminstance", o.value), n("ready", o);
    };
    return N(() => {
      r();
    }), {
      mergeView: t,
      initialize: r
    };
  }
}), ue = { ref: "mergeView" };
function de(e, n, o, t, r, s) {
  return C(), O("div", ue, null, 512);
}
const pe = /* @__PURE__ */ A(ce, [["render", de]]);
var me = /* @__PURE__ */ ((e) => (e.info = "info", e.warning = "warning", e.error = "error", e))(me || {});
function ge() {
  const e = /* @__PURE__ */ new Date(), n = e.getHours() < 10 ? `0${e.getHours()}` : e.getHours(), o = e.getMinutes() < 10 ? `0${e.getMinutes()}` : e.getMinutes(), t = e.getSeconds() < 10 ? `0${e.getSeconds()}` : e.getSeconds();
  return `${n}:${o}:${t}`;
}
function ze(e) {
  return `#link#${JSON.stringify(e)}#link#`;
}
function fe(e) {
  const n = /#link#(.+)#link#/g, o = [];
  let t;
  for (t = n.exec(e); t; ) {
    const r = document.createElement("a"), s = JSON.parse(t[1]), l = Object.entries(s);
    for (const [g, d] of l)
      r.setAttribute(g, d);
    r.className = "editor_custom_link", r.innerHTML = "logDownload", o.push({
      start: t.index,
      end: t.index + t[0].length,
      node: r
    }), t = n.exec(e);
  }
  return o;
}
function He(e = "", n = "info") {
  return `#log<${n}>log#${e}#log<${n}>log#`;
}
function he(e) {
  const n = [];
  function o() {
    const t = /#log<(\w*)>log#((.|\r\n|\n)*?)#log<(\w*)>log#/g;
    let r;
    for (r = t.exec(e); r; ) {
      const l = r[0].replace(/\r\n/g, `
`).split(`
`), d = r[2].replace(/\r\n/g, `
`).split(`
`), p = document.createElement("span"), i = r[1];
      p.className = `c-editor--log__${i}`;
      let u = 0;
      for (let $ = 0; $ < l.length; $++) {
        const a = l[$], m = d[$], v = p.cloneNode(!1);
        v.innerText = m, n.push({
          start: r.index + u,
          end: r.index + u + a.length,
          node: v
        }), u = u + a.length + 1;
      }
      r = t.exec(e);
    }
  }
  return o(), n;
}
function Re(e, n) {
  return `[${ge()}] <${n}> ${e}`;
}
function Be(e, n, o) {
  const r = new Array(Math.max(n || 15, 5)).join(o || "=");
  return `${r}${e}${r}`;
}
const S = [
  {
    regex: /(\[.*?\])([ \t]*)(<error>[ \t])(.+)/,
    token: ["tag", "", "error.strong", "error.strong"],
    sol: !0
    // next: "error",
  },
  {
    regex: /(\[.*?\])([ \t]*)(<info>)(.+)(.?)/,
    token: ["tag", "", "bracket", "bracket", "hr"],
    sol: !0
    // next: "info",
  },
  {
    regex: /(\[.*?\])([ \t]*)(<warning>)(.+)(.?)/,
    token: ["tag", "", "comment", "comment", "hr"],
    sol: !0
    // next: "warning",
  }
];
b.defineSimpleMode("fclog", {
  start: [
    ...S,
    {
      regex: /.*/,
      token: "hr"
    }
  ],
  error: [
    ...S,
    {
      regex: /.*/,
      token: "error.strong"
    }
  ],
  info: [
    ...S,
    {
      regex: /.*/,
      token: "bracket"
    }
  ],
  warning: [
    ...S,
    {
      regex: /.*\[/,
      token: "comment"
    }
  ]
});
b.defineSimpleMode("log", {
  start: [
    {
      regex: /^[=]+[^=]*[=]+/,
      token: "strong"
    },
    {
      regex: /([^\w])([A-Z][\w]*)/,
      token: ["", "string"]
    },
    {
      regex: /(^[A-Z][\w]*)/,
      token: "string"
    }
    // {
    //     regex: /([^\d])([0-9]+)/,
    //     token: [null, 'comment']
    // },
    // {
    //     regex: /(^[0-9]+)/,
    //     token: 'comment'
    // }
  ]
});
const ve = E({
  name: "CodemirrorFclog",
  props: {
    value: {
      type: String,
      default: ""
    },
    name: {
      type: String,
      default: `cm-textarea-${+/* @__PURE__ */ new Date()}`
    },
    options: {
      type: Object,
      default: () => ({})
    },
    cminstance: {
      type: Object,
      default: () => ({})
    },
    placeholder: {
      type: String,
      default: ""
    }
  },
  emits: ["update:cminstance", "ready"],
  setup(e, { emit: n }) {
    const o = k(), t = k(null), r = (l = e.cminstance) => {
      l.getAllMarks().forEach((i) => i.clear());
      const d = l.getValue(), p = [].concat(fe(d)).concat(he(d));
      for (let i = 0; i < p.length; i++) {
        const u = p[i];
        l.markText(l.posFromIndex(u.start), l.posFromIndex(u.end), {
          replacedWith: u.node
        });
      }
    }, s = () => {
      var l;
      t.value = T(b.fromTextArea(o.value, e.options)), n("update:cminstance", y(t)), (l = t.value) == null || l.on("change", r);
    };
    return _(
      () => e.cminstance,
      (l) => {
        var g;
        l && (r(e.cminstance), (g = e.cminstance) == null || g.setValue(e.value), n("ready", t));
      },
      { deep: !0, immediate: !0 }
    ), N(() => {
      s();
    }), {
      initialize: s,
      textarea: o
    };
  }
}), ye = ["name", "placeholder"];
function ke(e, n, o, t, r, s) {
  return C(), O("textarea", {
    ref: "textarea",
    name: e.$props.name,
    placeholder: e.$props.placeholder
  }, null, 8, ye);
}
const we = /* @__PURE__ */ A(ve, [["render", ke]]), P = {
  "update:value": () => !0,
  change: (e, n) => ({ value: e, cm: n }),
  input: () => !0,
  ready: (e) => e
}, xe = [
  "changes",
  "scroll",
  "beforeChange",
  "cursorActivity",
  "keyHandled",
  "inputRead",
  "electricInput",
  "beforeSelectionChange",
  "viewportChange",
  "swapDoc",
  "gutterClick",
  "gutterContextMenu",
  "focus",
  "blur",
  "refresh",
  "optionChange",
  "scrollCursorIntoView",
  "update"
], $e = () => {
  const e = {};
  return xe.forEach((n) => {
    e[n] = (...o) => o;
  }), e;
}, _e = { ...P, ...$e() }, j = {
  mode: "text",
  // Language mode
  theme: "default",
  // Theme
  lineNumbers: !0,
  // Display line number
  smartIndent: !0,
  // Intelligent indentation
  indentUnit: 2,
  // Indentation unit
  styleActiveLine: !0
  // Highlight the current line
};
function be(e) {
  Promise.resolve().then(() => {
    const n = e.getScrollInfo();
    e.scrollTo(n.left, n.height);
  });
}
const Ce = ({
  props: e,
  cminstance: n,
  emit: o,
  internalInstance: t,
  content: r
}) => {
  const s = I(
    () => {
      var d;
      return e.merge ? (d = y(n)) == null ? void 0 : d.editor() : y(n);
    }
  ), l = () => {
    const d = [];
    return Object.keys(t == null ? void 0 : t.vnode.props).forEach((p) => {
      if (p.startsWith("on")) {
        const i = p.replace(p[2], p[2].toLowerCase()).slice(2);
        !P[i] && d.push(i);
      }
    }), d;
  };
  return {
    listenerEvents: () => {
      s.value.on("change", (i) => {
        const u = i.getValue();
        u === r.value && u !== "" || (r.value = u, o("update:value", r.value || ""), o("input", r.value || " "), Promise.resolve().then(() => {
          o("change", r.value, i);
        }), e.keepCursorInEnd && be(i));
      });
      const d = {};
      l().filter((i) => !d[i] && (d[i] = !0)).forEach((i) => {
        s.value.on(i, (...u) => {
          o(i, ...u);
        });
      });
    }
  };
};
function Me({ props: e, cminstance: n, presetRef: o }) {
  const t = k("100%"), r = k("100%"), s = I(
    () => {
      var a;
      return e.merge ? (a = y(n)) == null ? void 0 : a.editor() : y(n);
    }
  ), l = () => {
    K(() => {
      var a;
      (a = s.value) == null || a.refresh();
    });
  }, g = (a) => a ? !(a && isNaN(+a)) : !1, d = (a = e.width, m = e.height) => {
    var M;
    let v = "100%", x = "100%";
    g(a) ? v = `${String(a)}px` : a && (v = a), g(m) ? x = `${String(m)}px` : m && (x = m), t.value = v, r.value = x, console.log("resize", v, x), (M = s.value) == null || M.setSize("100%", "100%");
  }, p = () => {
    var m;
    const a = (m = s.value) == null ? void 0 : m.getWrapperElement();
    a == null || a.remove();
  }, i = () => {
    var m, v, x;
    const a = (m = s.value) == null ? void 0 : m.getDoc().getHistory();
    (v = o.value) == null || v.initialize(), p(), (x = s.value) == null || x.getDoc().setHistory(a);
  }, u = () => {
    const a = document.querySelector(".CodeMirror-gutters");
    return (a == null ? void 0 : a.style.left.replace("px", "")) !== "0";
  };
  return {
    reload: i,
    refresh: l,
    resize: d,
    destroy: p,
    containerWidth: t,
    containerHeight: r,
    reviseStyle: () => {
      if (l(), !u()) return;
      const a = setInterval(() => {
        u() ? l() : clearInterval(a);
      }, 60), m = setTimeout(() => {
        clearInterval(a), clearTimeout(m);
      }, 400);
    }
  };
}
const R = /* @__PURE__ */ E({
  __name: "index",
  props: {
    value: {
      type: String,
      default: ""
    },
    options: {
      type: Object,
      default: () => j
    },
    globalOptions: {
      type: Object,
      default: () => j
    },
    placeholder: {
      type: String,
      default: ""
    },
    border: {
      type: Boolean,
      default: !1
    },
    width: {
      type: [String, Number],
      default: null
    },
    height: {
      type: [String, Number],
      default: null
    },
    originalStyle: {
      type: Boolean,
      default: !1
    },
    keepCursorInEnd: {
      type: Boolean,
      default: !1
    },
    merge: {
      type: Boolean,
      default: !1
    },
    name: {
      type: String,
      default: ""
    },
    marker: {
      type: Function,
      default: () => null
    },
    unseenLines: {
      type: Array,
      default: () => []
    }
  },
  emits: _e,
  setup(e, { expose: n, emit: o }) {
    var V, F, z;
    typeof Object.assign != "function" && Object.defineProperty(Object, "assign", {
      value(c) {
        if (c == null)
          throw new TypeError("Cannot convert undefined or null to object");
        const f = Object(c);
        for (let h = 1; h < arguments.length; h++) {
          const w = arguments[h];
          if (w != null)
            for (const L in w)
              Object.prototype.hasOwnProperty.call(w, L) && (f[L] = w[L]);
        }
        return f;
      },
      writable: !0,
      configurable: !0
    });
    const t = e, r = o, s = k(null), l = k(""), g = Q(H), d = k({
      foldGutter: !0,
      ...j,
      ...t.globalOptions,
      ...t.options,
      gutters: [.../* @__PURE__ */ new Set(["CodeMirror-linenumbers", "CodeMirror-foldgutter", ...((V = t.options) == null ? void 0 : V.gutters) || []])]
    }), p = X(), i = t.name || ((z = (F = p == null ? void 0 : p.parent) == null ? void 0 : F.type) == null ? void 0 : z.name) || void 0, u = k(null), $ = I(() => {
      var c;
      return t.merge ? (c = y(s)) == null ? void 0 : c.editor() : y(s);
    }), { refresh: a, resize: m, destroy: v, containerHeight: x, containerWidth: M, reviseStyle: W } = Me({
      props: t,
      cminstance: s,
      presetRef: u
    }), { listenerEvents: G } = Ce({
      props: t,
      cminstance: s,
      emit: r,
      internalInstance: p,
      content: l
    }), D = () => {
      t.unseenLines !== void 0 && t.marker !== void 0 && t.unseenLines.forEach((c) => {
        var h, w;
        const f = (h = s.value) == null ? void 0 : h.lineInfo(c);
        (w = s.value) == null || w.setGutterMarker(c, "breakpoints", f != null && f.gutterMarkers ? null : t.marker());
      });
    }, J = (c) => {
      var h, w;
      const f = (h = s.value) == null ? void 0 : h.getValue();
      c !== f && ((w = s.value) == null || w.setValue(c), l.value = c, W()), D();
    }, Z = () => {
      G(), D(), m(t.width, t.height), r("ready", s.value), _(
        [() => t.width, () => t.height],
        ([c, f]) => {
          m(c, f);
        },
        { deep: !0 }
      );
    }, q = () => {
      if (t.options.mode === "fclog" || t.options.mode === "log") {
        g.value = we;
        return;
      }
      if (t.merge) {
        g.value = pe;
        return;
      }
      g.value = H;
    };
    return _(
      () => t.options,
      (c) => {
        var f;
        for (const h in t.options)
          (f = $.value) == null || f.setOption(h, y(c[h]));
      },
      { deep: !0 }
    ), _(
      () => t.value,
      (c) => {
        J(c);
      }
    ), _(() => t.merge, q, { immediate: !0 }), Y(() => {
      v();
    }), n({
      cminstance: s,
      resize: m,
      refresh: a,
      destroy: v
    }), (c, f) => (C(), O("div", {
      class: ee(["codemirror-container", {
        merge: c.$props.merge,
        bordered: c.$props.border || c.$props.merge && !t.originalStyle,
        "original-style": t.originalStyle
      }]),
      style: te({
        height: y(x),
        width: y(M)
      })
    }, [
      (C(), ne(re(g.value), oe({
        ref_key: "presetRef",
        ref: u,
        cminstance: s.value,
        "onUpdate:cminstance": f[0] || (f[0] = (h) => s.value = h),
        style: { height: "100%" }
      }, {
        ...c.$props,
        ...c.$attrs,
        options: d.value,
        name: y(i),
        content: l.value
      }, { onReady: Z }), null, 16, ["cminstance"]))
    ], 6));
  }
}), U = (e, n) => (n && n.options && (R.props.globalOptions.default = () => n.options), e.component((n == null ? void 0 : n.componentName) || "Codemirror", R), e), Pe = window.CodeMirror || b, Ue = U, We = U;
function styleInject(css,ref){if(ref===void 0){ref={}}var insertAt=ref.insertAt;if(!css||typeof document==="undefined"){return}var head=document.head||document.getElementsByTagName("head")[0];var style=document.createElement("style");style.type="text/css";if(insertAt==="top"){if(head.firstChild){head.insertBefore(style,head.firstChild)}else{head.appendChild(style)}}else{head.appendChild(style)}if(style.styleSheet){style.styleSheet.cssText=css}else{style.appendChild(document.createTextNode(css))}};styleInject(`.codemirror-container {
  position: relative;
  display: inline-block;
  height: 100%;
  width: fit-content;
  font-size: 13px;
  overflow: hidden;
}
.codemirror-container.bordered {
  border: 1px solid #aaaaaa;
}

.codemirror-container .editor_custom_link {
  cursor: pointer;
  color: #1474f1;
  text-decoration: underline;
}
.codemirror-container .editor_custom_link:hover {
  color: #04b4fa;
}
.codemirror-container:not(.original-style) .CodeMirror-lines .CodeMirror-placeholder.CodeMirror-line-like {
  color: #666;
}
.codemirror-container:not(.original-style) .CodeMirror,
.codemirror-container:not(.original-style) .CodeMirror-merge-pane {
  height: 100%;
  font-family: consolas !important;
}
.codemirror-container:not(.original-style) .CodeMirror-merge,
.codemirror-container:not(.original-style) .CodeMirror-merge-right .CodeMirror {
  height: 100%;
  border: none !important;
}
.codemirror-container:not(.original-style) .c-editor--log__error {
  color: #bb0606;
  font-weight: bold;
}
.codemirror-container:not(.original-style) .c-editor--log__info {
  color: #333333;
  font-weight: bold;
}
.codemirror-container:not(.original-style) .c-editor--log__warning {
  color: #ee9900;
}
.codemirror-container:not(.original-style) .c-editor--log__success {
  color: #669600;
}
.codemirror-container:not(.original-style) .cm-header,
.codemirror-container:not(.original-style) .cm-strong {
  font-weight: bold;
}
`);
export {
  Pe as CodeMirror,
  Ue as GlobalCmComponent,
  We as InstallCodeMirror,
  R as VueCodemirror,
  ze as createLinkMark,
  Re as createLog,
  He as createLogMark,
  Be as createTitle,
  R as default,
  fe as getLinkMarks,
  ge as getLocalTime,
  he as getLogMark,
  me as logErrorType
};
