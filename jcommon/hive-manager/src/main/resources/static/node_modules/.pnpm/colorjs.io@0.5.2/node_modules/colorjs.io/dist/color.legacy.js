var commonjsGlobal = typeof globalThis !== 'undefined' ? globalThis : typeof window !== 'undefined' ? window : typeof global !== 'undefined' ? global : typeof self !== 'undefined' ? self : {};

var es_array_push = {};

var global$1;
var hasRequiredGlobal;

function requireGlobal () {
	if (hasRequiredGlobal) return global$1;
	hasRequiredGlobal = 1;
	var check = function (it) {
	  return it && it.Math === Math && it;
	};

	// https://github.com/zloirock/core-js/issues/86#issuecomment-115759028
	global$1 =
	  // eslint-disable-next-line es/no-global-this -- safe
	  check(typeof globalThis == 'object' && globalThis) ||
	  check(typeof window == 'object' && window) ||
	  // eslint-disable-next-line no-restricted-globals -- safe
	  check(typeof self == 'object' && self) ||
	  check(typeof commonjsGlobal == 'object' && commonjsGlobal) ||
	  check(typeof commonjsGlobal == 'object' && commonjsGlobal) ||
	  // eslint-disable-next-line no-new-func -- fallback
	  (function () { return this; })() || Function('return this')();
	return global$1;
}

var objectGetOwnPropertyDescriptor = {};

var fails;
var hasRequiredFails;

function requireFails () {
	if (hasRequiredFails) return fails;
	hasRequiredFails = 1;
	fails = function (exec) {
	  try {
	    return !!exec();
	  } catch (error) {
	    return true;
	  }
	};
	return fails;
}

var descriptors;
var hasRequiredDescriptors;

function requireDescriptors () {
	if (hasRequiredDescriptors) return descriptors;
	hasRequiredDescriptors = 1;
	var fails = requireFails();

	// Detect IE8's incomplete defineProperty implementation
	descriptors = !fails(function () {
	  // eslint-disable-next-line es/no-object-defineproperty -- required for testing
	  return Object.defineProperty({}, 1, { get: function () { return 7; } })[1] !== 7;
	});
	return descriptors;
}

var functionBindNative;
var hasRequiredFunctionBindNative;

function requireFunctionBindNative () {
	if (hasRequiredFunctionBindNative) return functionBindNative;
	hasRequiredFunctionBindNative = 1;
	var fails = requireFails();

	functionBindNative = !fails(function () {
	  // eslint-disable-next-line es/no-function-prototype-bind -- safe
	  var test = (function () { /* empty */ }).bind();
	  // eslint-disable-next-line no-prototype-builtins -- safe
	  return typeof test != 'function' || test.hasOwnProperty('prototype');
	});
	return functionBindNative;
}

var functionCall;
var hasRequiredFunctionCall;

function requireFunctionCall () {
	if (hasRequiredFunctionCall) return functionCall;
	hasRequiredFunctionCall = 1;
	var NATIVE_BIND = requireFunctionBindNative();

	var call = Function.prototype.call;

	functionCall = NATIVE_BIND ? call.bind(call) : function () {
	  return call.apply(call, arguments);
	};
	return functionCall;
}

var objectPropertyIsEnumerable = {};

var hasRequiredObjectPropertyIsEnumerable;

function requireObjectPropertyIsEnumerable () {
	if (hasRequiredObjectPropertyIsEnumerable) return objectPropertyIsEnumerable;
	hasRequiredObjectPropertyIsEnumerable = 1;
	var $propertyIsEnumerable = {}.propertyIsEnumerable;
	// eslint-disable-next-line es/no-object-getownpropertydescriptor -- safe
	var getOwnPropertyDescriptor = Object.getOwnPropertyDescriptor;

	// Nashorn ~ JDK8 bug
	var NASHORN_BUG = getOwnPropertyDescriptor && !$propertyIsEnumerable.call({ 1: 2 }, 1);

	// `Object.prototype.propertyIsEnumerable` method implementation
	// https://tc39.es/ecma262/#sec-object.prototype.propertyisenumerable
	objectPropertyIsEnumerable.f = NASHORN_BUG ? function propertyIsEnumerable(V) {
	  var descriptor = getOwnPropertyDescriptor(this, V);
	  return !!descriptor && descriptor.enumerable;
	} : $propertyIsEnumerable;
	return objectPropertyIsEnumerable;
}

var createPropertyDescriptor;
var hasRequiredCreatePropertyDescriptor;

function requireCreatePropertyDescriptor () {
	if (hasRequiredCreatePropertyDescriptor) return createPropertyDescriptor;
	hasRequiredCreatePropertyDescriptor = 1;
	createPropertyDescriptor = function (bitmap, value) {
	  return {
	    enumerable: !(bitmap & 1),
	    configurable: !(bitmap & 2),
	    writable: !(bitmap & 4),
	    value: value
	  };
	};
	return createPropertyDescriptor;
}

var functionUncurryThis;
var hasRequiredFunctionUncurryThis;

function requireFunctionUncurryThis () {
	if (hasRequiredFunctionUncurryThis) return functionUncurryThis;
	hasRequiredFunctionUncurryThis = 1;
	var NATIVE_BIND = requireFunctionBindNative();

	var FunctionPrototype = Function.prototype;
	var call = FunctionPrototype.call;
	var uncurryThisWithBind = NATIVE_BIND && FunctionPrototype.bind.bind(call, call);

	functionUncurryThis = NATIVE_BIND ? uncurryThisWithBind : function (fn) {
	  return function () {
	    return call.apply(fn, arguments);
	  };
	};
	return functionUncurryThis;
}

var classofRaw;
var hasRequiredClassofRaw;

function requireClassofRaw () {
	if (hasRequiredClassofRaw) return classofRaw;
	hasRequiredClassofRaw = 1;
	var uncurryThis = requireFunctionUncurryThis();

	var toString = uncurryThis({}.toString);
	var stringSlice = uncurryThis(''.slice);

	classofRaw = function (it) {
	  return stringSlice(toString(it), 8, -1);
	};
	return classofRaw;
}

var indexedObject;
var hasRequiredIndexedObject;

function requireIndexedObject () {
	if (hasRequiredIndexedObject) return indexedObject;
	hasRequiredIndexedObject = 1;
	var uncurryThis = requireFunctionUncurryThis();
	var fails = requireFails();
	var classof = requireClassofRaw();

	var $Object = Object;
	var split = uncurryThis(''.split);

	// fallback for non-array-like ES3 and non-enumerable old V8 strings
	indexedObject = fails(function () {
	  // throws an error in rhino, see https://github.com/mozilla/rhino/issues/346
	  // eslint-disable-next-line no-prototype-builtins -- safe
	  return !$Object('z').propertyIsEnumerable(0);
	}) ? function (it) {
	  return classof(it) === 'String' ? split(it, '') : $Object(it);
	} : $Object;
	return indexedObject;
}

var isNullOrUndefined;
var hasRequiredIsNullOrUndefined;

function requireIsNullOrUndefined () {
	if (hasRequiredIsNullOrUndefined) return isNullOrUndefined;
	hasRequiredIsNullOrUndefined = 1;
	// we can't use just `it == null` since of `document.all` special case
	// https://tc39.es/ecma262/#sec-IsHTMLDDA-internal-slot-aec
	isNullOrUndefined = function (it) {
	  return it === null || it === undefined;
	};
	return isNullOrUndefined;
}

var requireObjectCoercible;
var hasRequiredRequireObjectCoercible;

function requireRequireObjectCoercible () {
	if (hasRequiredRequireObjectCoercible) return requireObjectCoercible;
	hasRequiredRequireObjectCoercible = 1;
	var isNullOrUndefined = requireIsNullOrUndefined();

	var $TypeError = TypeError;

	// `RequireObjectCoercible` abstract operation
	// https://tc39.es/ecma262/#sec-requireobjectcoercible
	requireObjectCoercible = function (it) {
	  if (isNullOrUndefined(it)) throw new $TypeError("Can't call method on " + it);
	  return it;
	};
	return requireObjectCoercible;
}

var toIndexedObject;
var hasRequiredToIndexedObject;

function requireToIndexedObject () {
	if (hasRequiredToIndexedObject) return toIndexedObject;
	hasRequiredToIndexedObject = 1;
	// toObject with fallback for non-array-like ES3 strings
	var IndexedObject = requireIndexedObject();
	var requireObjectCoercible = requireRequireObjectCoercible();

	toIndexedObject = function (it) {
	  return IndexedObject(requireObjectCoercible(it));
	};
	return toIndexedObject;
}

var isCallable;
var hasRequiredIsCallable;

function requireIsCallable () {
	if (hasRequiredIsCallable) return isCallable;
	hasRequiredIsCallable = 1;
	// https://tc39.es/ecma262/#sec-IsHTMLDDA-internal-slot
	var documentAll = typeof document == 'object' && document.all;

	// `IsCallable` abstract operation
	// https://tc39.es/ecma262/#sec-iscallable
	// eslint-disable-next-line unicorn/no-typeof-undefined -- required for testing
	isCallable = typeof documentAll == 'undefined' && documentAll !== undefined ? function (argument) {
	  return typeof argument == 'function' || argument === documentAll;
	} : function (argument) {
	  return typeof argument == 'function';
	};
	return isCallable;
}

var isObject;
var hasRequiredIsObject;

function requireIsObject () {
	if (hasRequiredIsObject) return isObject;
	hasRequiredIsObject = 1;
	var isCallable = requireIsCallable();

	isObject = function (it) {
	  return typeof it == 'object' ? it !== null : isCallable(it);
	};
	return isObject;
}

var getBuiltIn;
var hasRequiredGetBuiltIn;

function requireGetBuiltIn () {
	if (hasRequiredGetBuiltIn) return getBuiltIn;
	hasRequiredGetBuiltIn = 1;
	var global = requireGlobal();
	var isCallable = requireIsCallable();

	var aFunction = function (argument) {
	  return isCallable(argument) ? argument : undefined;
	};

	getBuiltIn = function (namespace, method) {
	  return arguments.length < 2 ? aFunction(global[namespace]) : global[namespace] && global[namespace][method];
	};
	return getBuiltIn;
}

var objectIsPrototypeOf;
var hasRequiredObjectIsPrototypeOf;

function requireObjectIsPrototypeOf () {
	if (hasRequiredObjectIsPrototypeOf) return objectIsPrototypeOf;
	hasRequiredObjectIsPrototypeOf = 1;
	var uncurryThis = requireFunctionUncurryThis();

	objectIsPrototypeOf = uncurryThis({}.isPrototypeOf);
	return objectIsPrototypeOf;
}

var engineUserAgent;
var hasRequiredEngineUserAgent;

function requireEngineUserAgent () {
	if (hasRequiredEngineUserAgent) return engineUserAgent;
	hasRequiredEngineUserAgent = 1;
	engineUserAgent = typeof navigator != 'undefined' && String(navigator.userAgent) || '';
	return engineUserAgent;
}

var engineV8Version;
var hasRequiredEngineV8Version;

function requireEngineV8Version () {
	if (hasRequiredEngineV8Version) return engineV8Version;
	hasRequiredEngineV8Version = 1;
	var global = requireGlobal();
	var userAgent = requireEngineUserAgent();

	var process = global.process;
	var Deno = global.Deno;
	var versions = process && process.versions || Deno && Deno.version;
	var v8 = versions && versions.v8;
	var match, version;

	if (v8) {
	  match = v8.split('.');
	  // in old Chrome, versions of V8 isn't V8 = Chrome / 10
	  // but their correct versions are not interesting for us
	  version = match[0] > 0 && match[0] < 4 ? 1 : +(match[0] + match[1]);
	}

	// BrowserFS NodeJS `process` polyfill incorrectly set `.v8` to `0.0`
	// so check `userAgent` even if `.v8` exists, but 0
	if (!version && userAgent) {
	  match = userAgent.match(/Edge\/(\d+)/);
	  if (!match || match[1] >= 74) {
	    match = userAgent.match(/Chrome\/(\d+)/);
	    if (match) version = +match[1];
	  }
	}

	engineV8Version = version;
	return engineV8Version;
}

var symbolConstructorDetection;
var hasRequiredSymbolConstructorDetection;

function requireSymbolConstructorDetection () {
	if (hasRequiredSymbolConstructorDetection) return symbolConstructorDetection;
	hasRequiredSymbolConstructorDetection = 1;
	/* eslint-disable es/no-symbol -- required for testing */
	var V8_VERSION = requireEngineV8Version();
	var fails = requireFails();
	var global = requireGlobal();

	var $String = global.String;

	// eslint-disable-next-line es/no-object-getownpropertysymbols -- required for testing
	symbolConstructorDetection = !!Object.getOwnPropertySymbols && !fails(function () {
	  var symbol = Symbol('symbol detection');
	  // Chrome 38 Symbol has incorrect toString conversion
	  // `get-own-property-symbols` polyfill symbols converted to object are not Symbol instances
	  // nb: Do not call `String` directly to avoid this being optimized out to `symbol+''` which will,
	  // of course, fail.
	  return !$String(symbol) || !(Object(symbol) instanceof Symbol) ||
	    // Chrome 38-40 symbols are not inherited from DOM collections prototypes to instances
	    !Symbol.sham && V8_VERSION && V8_VERSION < 41;
	});
	return symbolConstructorDetection;
}

var useSymbolAsUid;
var hasRequiredUseSymbolAsUid;

function requireUseSymbolAsUid () {
	if (hasRequiredUseSymbolAsUid) return useSymbolAsUid;
	hasRequiredUseSymbolAsUid = 1;
	/* eslint-disable es/no-symbol -- required for testing */
	var NATIVE_SYMBOL = requireSymbolConstructorDetection();

	useSymbolAsUid = NATIVE_SYMBOL
	  && !Symbol.sham
	  && typeof Symbol.iterator == 'symbol';
	return useSymbolAsUid;
}

var isSymbol;
var hasRequiredIsSymbol;

function requireIsSymbol () {
	if (hasRequiredIsSymbol) return isSymbol;
	hasRequiredIsSymbol = 1;
	var getBuiltIn = requireGetBuiltIn();
	var isCallable = requireIsCallable();
	var isPrototypeOf = requireObjectIsPrototypeOf();
	var USE_SYMBOL_AS_UID = requireUseSymbolAsUid();

	var $Object = Object;

	isSymbol = USE_SYMBOL_AS_UID ? function (it) {
	  return typeof it == 'symbol';
	} : function (it) {
	  var $Symbol = getBuiltIn('Symbol');
	  return isCallable($Symbol) && isPrototypeOf($Symbol.prototype, $Object(it));
	};
	return isSymbol;
}

var tryToString;
var hasRequiredTryToString;

function requireTryToString () {
	if (hasRequiredTryToString) return tryToString;
	hasRequiredTryToString = 1;
	var $String = String;

	tryToString = function (argument) {
	  try {
	    return $String(argument);
	  } catch (error) {
	    return 'Object';
	  }
	};
	return tryToString;
}

var aCallable;
var hasRequiredACallable;

function requireACallable () {
	if (hasRequiredACallable) return aCallable;
	hasRequiredACallable = 1;
	var isCallable = requireIsCallable();
	var tryToString = requireTryToString();

	var $TypeError = TypeError;

	// `Assert: IsCallable(argument) is true`
	aCallable = function (argument) {
	  if (isCallable(argument)) return argument;
	  throw new $TypeError(tryToString(argument) + ' is not a function');
	};
	return aCallable;
}

var getMethod;
var hasRequiredGetMethod;

function requireGetMethod () {
	if (hasRequiredGetMethod) return getMethod;
	hasRequiredGetMethod = 1;
	var aCallable = requireACallable();
	var isNullOrUndefined = requireIsNullOrUndefined();

	// `GetMethod` abstract operation
	// https://tc39.es/ecma262/#sec-getmethod
	getMethod = function (V, P) {
	  var func = V[P];
	  return isNullOrUndefined(func) ? undefined : aCallable(func);
	};
	return getMethod;
}

var ordinaryToPrimitive;
var hasRequiredOrdinaryToPrimitive;

function requireOrdinaryToPrimitive () {
	if (hasRequiredOrdinaryToPrimitive) return ordinaryToPrimitive;
	hasRequiredOrdinaryToPrimitive = 1;
	var call = requireFunctionCall();
	var isCallable = requireIsCallable();
	var isObject = requireIsObject();

	var $TypeError = TypeError;

	// `OrdinaryToPrimitive` abstract operation
	// https://tc39.es/ecma262/#sec-ordinarytoprimitive
	ordinaryToPrimitive = function (input, pref) {
	  var fn, val;
	  if (pref === 'string' && isCallable(fn = input.toString) && !isObject(val = call(fn, input))) return val;
	  if (isCallable(fn = input.valueOf) && !isObject(val = call(fn, input))) return val;
	  if (pref !== 'string' && isCallable(fn = input.toString) && !isObject(val = call(fn, input))) return val;
	  throw new $TypeError("Can't convert object to primitive value");
	};
	return ordinaryToPrimitive;
}

var sharedStore = {exports: {}};

var isPure;
var hasRequiredIsPure;

function requireIsPure () {
	if (hasRequiredIsPure) return isPure;
	hasRequiredIsPure = 1;
	isPure = false;
	return isPure;
}

var defineGlobalProperty;
var hasRequiredDefineGlobalProperty;

function requireDefineGlobalProperty () {
	if (hasRequiredDefineGlobalProperty) return defineGlobalProperty;
	hasRequiredDefineGlobalProperty = 1;
	var global = requireGlobal();

	// eslint-disable-next-line es/no-object-defineproperty -- safe
	var defineProperty = Object.defineProperty;

	defineGlobalProperty = function (key, value) {
	  try {
	    defineProperty(global, key, { value: value, configurable: true, writable: true });
	  } catch (error) {
	    global[key] = value;
	  } return value;
	};
	return defineGlobalProperty;
}

var hasRequiredSharedStore;

function requireSharedStore () {
	if (hasRequiredSharedStore) return sharedStore.exports;
	hasRequiredSharedStore = 1;
	var IS_PURE = requireIsPure();
	var globalThis = requireGlobal();
	var defineGlobalProperty = requireDefineGlobalProperty();

	var SHARED = '__core-js_shared__';
	var store = sharedStore.exports = globalThis[SHARED] || defineGlobalProperty(SHARED, {});

	(store.versions || (store.versions = [])).push({
	  version: '3.36.0',
	  mode: IS_PURE ? 'pure' : 'global',
	  copyright: '© 2014-2024 Denis Pushkarev (zloirock.ru)',
	  license: 'https://github.com/zloirock/core-js/blob/v3.36.0/LICENSE',
	  source: 'https://github.com/zloirock/core-js'
	});
	return sharedStore.exports;
}

var shared;
var hasRequiredShared;

function requireShared () {
	if (hasRequiredShared) return shared;
	hasRequiredShared = 1;
	var store = requireSharedStore();

	shared = function (key, value) {
	  return store[key] || (store[key] = value || {});
	};
	return shared;
}

var toObject;
var hasRequiredToObject;

function requireToObject () {
	if (hasRequiredToObject) return toObject;
	hasRequiredToObject = 1;
	var requireObjectCoercible = requireRequireObjectCoercible();

	var $Object = Object;

	// `ToObject` abstract operation
	// https://tc39.es/ecma262/#sec-toobject
	toObject = function (argument) {
	  return $Object(requireObjectCoercible(argument));
	};
	return toObject;
}

var hasOwnProperty_1;
var hasRequiredHasOwnProperty;

function requireHasOwnProperty () {
	if (hasRequiredHasOwnProperty) return hasOwnProperty_1;
	hasRequiredHasOwnProperty = 1;
	var uncurryThis = requireFunctionUncurryThis();
	var toObject = requireToObject();

	var hasOwnProperty = uncurryThis({}.hasOwnProperty);

	// `HasOwnProperty` abstract operation
	// https://tc39.es/ecma262/#sec-hasownproperty
	// eslint-disable-next-line es/no-object-hasown -- safe
	hasOwnProperty_1 = Object.hasOwn || function hasOwn(it, key) {
	  return hasOwnProperty(toObject(it), key);
	};
	return hasOwnProperty_1;
}

var uid;
var hasRequiredUid;

function requireUid () {
	if (hasRequiredUid) return uid;
	hasRequiredUid = 1;
	var uncurryThis = requireFunctionUncurryThis();

	var id = 0;
	var postfix = Math.random();
	var toString = uncurryThis(1.0.toString);

	uid = function (key) {
	  return 'Symbol(' + (key === undefined ? '' : key) + ')_' + toString(++id + postfix, 36);
	};
	return uid;
}

var wellKnownSymbol;
var hasRequiredWellKnownSymbol;

function requireWellKnownSymbol () {
	if (hasRequiredWellKnownSymbol) return wellKnownSymbol;
	hasRequiredWellKnownSymbol = 1;
	var global = requireGlobal();
	var shared = requireShared();
	var hasOwn = requireHasOwnProperty();
	var uid = requireUid();
	var NATIVE_SYMBOL = requireSymbolConstructorDetection();
	var USE_SYMBOL_AS_UID = requireUseSymbolAsUid();

	var Symbol = global.Symbol;
	var WellKnownSymbolsStore = shared('wks');
	var createWellKnownSymbol = USE_SYMBOL_AS_UID ? Symbol['for'] || Symbol : Symbol && Symbol.withoutSetter || uid;

	wellKnownSymbol = function (name) {
	  if (!hasOwn(WellKnownSymbolsStore, name)) {
	    WellKnownSymbolsStore[name] = NATIVE_SYMBOL && hasOwn(Symbol, name)
	      ? Symbol[name]
	      : createWellKnownSymbol('Symbol.' + name);
	  } return WellKnownSymbolsStore[name];
	};
	return wellKnownSymbol;
}

var toPrimitive;
var hasRequiredToPrimitive;

function requireToPrimitive () {
	if (hasRequiredToPrimitive) return toPrimitive;
	hasRequiredToPrimitive = 1;
	var call = requireFunctionCall();
	var isObject = requireIsObject();
	var isSymbol = requireIsSymbol();
	var getMethod = requireGetMethod();
	var ordinaryToPrimitive = requireOrdinaryToPrimitive();
	var wellKnownSymbol = requireWellKnownSymbol();

	var $TypeError = TypeError;
	var TO_PRIMITIVE = wellKnownSymbol('toPrimitive');

	// `ToPrimitive` abstract operation
	// https://tc39.es/ecma262/#sec-toprimitive
	toPrimitive = function (input, pref) {
	  if (!isObject(input) || isSymbol(input)) return input;
	  var exoticToPrim = getMethod(input, TO_PRIMITIVE);
	  var result;
	  if (exoticToPrim) {
	    if (pref === undefined) pref = 'default';
	    result = call(exoticToPrim, input, pref);
	    if (!isObject(result) || isSymbol(result)) return result;
	    throw new $TypeError("Can't convert object to primitive value");
	  }
	  if (pref === undefined) pref = 'number';
	  return ordinaryToPrimitive(input, pref);
	};
	return toPrimitive;
}

var toPropertyKey;
var hasRequiredToPropertyKey;

function requireToPropertyKey () {
	if (hasRequiredToPropertyKey) return toPropertyKey;
	hasRequiredToPropertyKey = 1;
	var toPrimitive = requireToPrimitive();
	var isSymbol = requireIsSymbol();

	// `ToPropertyKey` abstract operation
	// https://tc39.es/ecma262/#sec-topropertykey
	toPropertyKey = function (argument) {
	  var key = toPrimitive(argument, 'string');
	  return isSymbol(key) ? key : key + '';
	};
	return toPropertyKey;
}

var documentCreateElement;
var hasRequiredDocumentCreateElement;

function requireDocumentCreateElement () {
	if (hasRequiredDocumentCreateElement) return documentCreateElement;
	hasRequiredDocumentCreateElement = 1;
	var global = requireGlobal();
	var isObject = requireIsObject();

	var document = global.document;
	// typeof document.createElement is 'object' in old IE
	var EXISTS = isObject(document) && isObject(document.createElement);

	documentCreateElement = function (it) {
	  return EXISTS ? document.createElement(it) : {};
	};
	return documentCreateElement;
}

var ie8DomDefine;
var hasRequiredIe8DomDefine;

function requireIe8DomDefine () {
	if (hasRequiredIe8DomDefine) return ie8DomDefine;
	hasRequiredIe8DomDefine = 1;
	var DESCRIPTORS = requireDescriptors();
	var fails = requireFails();
	var createElement = requireDocumentCreateElement();

	// Thanks to IE8 for its funny defineProperty
	ie8DomDefine = !DESCRIPTORS && !fails(function () {
	  // eslint-disable-next-line es/no-object-defineproperty -- required for testing
	  return Object.defineProperty(createElement('div'), 'a', {
	    get: function () { return 7; }
	  }).a !== 7;
	});
	return ie8DomDefine;
}

var hasRequiredObjectGetOwnPropertyDescriptor;

function requireObjectGetOwnPropertyDescriptor () {
	if (hasRequiredObjectGetOwnPropertyDescriptor) return objectGetOwnPropertyDescriptor;
	hasRequiredObjectGetOwnPropertyDescriptor = 1;
	var DESCRIPTORS = requireDescriptors();
	var call = requireFunctionCall();
	var propertyIsEnumerableModule = requireObjectPropertyIsEnumerable();
	var createPropertyDescriptor = requireCreatePropertyDescriptor();
	var toIndexedObject = requireToIndexedObject();
	var toPropertyKey = requireToPropertyKey();
	var hasOwn = requireHasOwnProperty();
	var IE8_DOM_DEFINE = requireIe8DomDefine();

	// eslint-disable-next-line es/no-object-getownpropertydescriptor -- safe
	var $getOwnPropertyDescriptor = Object.getOwnPropertyDescriptor;

	// `Object.getOwnPropertyDescriptor` method
	// https://tc39.es/ecma262/#sec-object.getownpropertydescriptor
	objectGetOwnPropertyDescriptor.f = DESCRIPTORS ? $getOwnPropertyDescriptor : function getOwnPropertyDescriptor(O, P) {
	  O = toIndexedObject(O);
	  P = toPropertyKey(P);
	  if (IE8_DOM_DEFINE) try {
	    return $getOwnPropertyDescriptor(O, P);
	  } catch (error) { /* empty */ }
	  if (hasOwn(O, P)) return createPropertyDescriptor(!call(propertyIsEnumerableModule.f, O, P), O[P]);
	};
	return objectGetOwnPropertyDescriptor;
}

var objectDefineProperty = {};

var v8PrototypeDefineBug;
var hasRequiredV8PrototypeDefineBug;

function requireV8PrototypeDefineBug () {
	if (hasRequiredV8PrototypeDefineBug) return v8PrototypeDefineBug;
	hasRequiredV8PrototypeDefineBug = 1;
	var DESCRIPTORS = requireDescriptors();
	var fails = requireFails();

	// V8 ~ Chrome 36-
	// https://bugs.chromium.org/p/v8/issues/detail?id=3334
	v8PrototypeDefineBug = DESCRIPTORS && fails(function () {
	  // eslint-disable-next-line es/no-object-defineproperty -- required for testing
	  return Object.defineProperty(function () { /* empty */ }, 'prototype', {
	    value: 42,
	    writable: false
	  }).prototype !== 42;
	});
	return v8PrototypeDefineBug;
}

var anObject;
var hasRequiredAnObject;

function requireAnObject () {
	if (hasRequiredAnObject) return anObject;
	hasRequiredAnObject = 1;
	var isObject = requireIsObject();

	var $String = String;
	var $TypeError = TypeError;

	// `Assert: Type(argument) is Object`
	anObject = function (argument) {
	  if (isObject(argument)) return argument;
	  throw new $TypeError($String(argument) + ' is not an object');
	};
	return anObject;
}

var hasRequiredObjectDefineProperty;

function requireObjectDefineProperty () {
	if (hasRequiredObjectDefineProperty) return objectDefineProperty;
	hasRequiredObjectDefineProperty = 1;
	var DESCRIPTORS = requireDescriptors();
	var IE8_DOM_DEFINE = requireIe8DomDefine();
	var V8_PROTOTYPE_DEFINE_BUG = requireV8PrototypeDefineBug();
	var anObject = requireAnObject();
	var toPropertyKey = requireToPropertyKey();

	var $TypeError = TypeError;
	// eslint-disable-next-line es/no-object-defineproperty -- safe
	var $defineProperty = Object.defineProperty;
	// eslint-disable-next-line es/no-object-getownpropertydescriptor -- safe
	var $getOwnPropertyDescriptor = Object.getOwnPropertyDescriptor;
	var ENUMERABLE = 'enumerable';
	var CONFIGURABLE = 'configurable';
	var WRITABLE = 'writable';

	// `Object.defineProperty` method
	// https://tc39.es/ecma262/#sec-object.defineproperty
	objectDefineProperty.f = DESCRIPTORS ? V8_PROTOTYPE_DEFINE_BUG ? function defineProperty(O, P, Attributes) {
	  anObject(O);
	  P = toPropertyKey(P);
	  anObject(Attributes);
	  if (typeof O === 'function' && P === 'prototype' && 'value' in Attributes && WRITABLE in Attributes && !Attributes[WRITABLE]) {
	    var current = $getOwnPropertyDescriptor(O, P);
	    if (current && current[WRITABLE]) {
	      O[P] = Attributes.value;
	      Attributes = {
	        configurable: CONFIGURABLE in Attributes ? Attributes[CONFIGURABLE] : current[CONFIGURABLE],
	        enumerable: ENUMERABLE in Attributes ? Attributes[ENUMERABLE] : current[ENUMERABLE],
	        writable: false
	      };
	    }
	  } return $defineProperty(O, P, Attributes);
	} : $defineProperty : function defineProperty(O, P, Attributes) {
	  anObject(O);
	  P = toPropertyKey(P);
	  anObject(Attributes);
	  if (IE8_DOM_DEFINE) try {
	    return $defineProperty(O, P, Attributes);
	  } catch (error) { /* empty */ }
	  if ('get' in Attributes || 'set' in Attributes) throw new $TypeError('Accessors not supported');
	  if ('value' in Attributes) O[P] = Attributes.value;
	  return O;
	};
	return objectDefineProperty;
}

var createNonEnumerableProperty;
var hasRequiredCreateNonEnumerableProperty;

function requireCreateNonEnumerableProperty () {
	if (hasRequiredCreateNonEnumerableProperty) return createNonEnumerableProperty;
	hasRequiredCreateNonEnumerableProperty = 1;
	var DESCRIPTORS = requireDescriptors();
	var definePropertyModule = requireObjectDefineProperty();
	var createPropertyDescriptor = requireCreatePropertyDescriptor();

	createNonEnumerableProperty = DESCRIPTORS ? function (object, key, value) {
	  return definePropertyModule.f(object, key, createPropertyDescriptor(1, value));
	} : function (object, key, value) {
	  object[key] = value;
	  return object;
	};
	return createNonEnumerableProperty;
}

var makeBuiltIn = {exports: {}};

var functionName;
var hasRequiredFunctionName;

function requireFunctionName () {
	if (hasRequiredFunctionName) return functionName;
	hasRequiredFunctionName = 1;
	var DESCRIPTORS = requireDescriptors();
	var hasOwn = requireHasOwnProperty();

	var FunctionPrototype = Function.prototype;
	// eslint-disable-next-line es/no-object-getownpropertydescriptor -- safe
	var getDescriptor = DESCRIPTORS && Object.getOwnPropertyDescriptor;

	var EXISTS = hasOwn(FunctionPrototype, 'name');
	// additional protection from minified / mangled / dropped function names
	var PROPER = EXISTS && (function something() { /* empty */ }).name === 'something';
	var CONFIGURABLE = EXISTS && (!DESCRIPTORS || (DESCRIPTORS && getDescriptor(FunctionPrototype, 'name').configurable));

	functionName = {
	  EXISTS: EXISTS,
	  PROPER: PROPER,
	  CONFIGURABLE: CONFIGURABLE
	};
	return functionName;
}

var inspectSource;
var hasRequiredInspectSource;

function requireInspectSource () {
	if (hasRequiredInspectSource) return inspectSource;
	hasRequiredInspectSource = 1;
	var uncurryThis = requireFunctionUncurryThis();
	var isCallable = requireIsCallable();
	var store = requireSharedStore();

	var functionToString = uncurryThis(Function.toString);

	// this helper broken in `core-js@3.4.1-3.4.4`, so we can't use `shared` helper
	if (!isCallable(store.inspectSource)) {
	  store.inspectSource = function (it) {
	    return functionToString(it);
	  };
	}

	inspectSource = store.inspectSource;
	return inspectSource;
}

var weakMapBasicDetection;
var hasRequiredWeakMapBasicDetection;

function requireWeakMapBasicDetection () {
	if (hasRequiredWeakMapBasicDetection) return weakMapBasicDetection;
	hasRequiredWeakMapBasicDetection = 1;
	var global = requireGlobal();
	var isCallable = requireIsCallable();

	var WeakMap = global.WeakMap;

	weakMapBasicDetection = isCallable(WeakMap) && /native code/.test(String(WeakMap));
	return weakMapBasicDetection;
}

var sharedKey;
var hasRequiredSharedKey;

function requireSharedKey () {
	if (hasRequiredSharedKey) return sharedKey;
	hasRequiredSharedKey = 1;
	var shared = requireShared();
	var uid = requireUid();

	var keys = shared('keys');

	sharedKey = function (key) {
	  return keys[key] || (keys[key] = uid(key));
	};
	return sharedKey;
}

var hiddenKeys;
var hasRequiredHiddenKeys;

function requireHiddenKeys () {
	if (hasRequiredHiddenKeys) return hiddenKeys;
	hasRequiredHiddenKeys = 1;
	hiddenKeys = {};
	return hiddenKeys;
}

var internalState;
var hasRequiredInternalState;

function requireInternalState () {
	if (hasRequiredInternalState) return internalState;
	hasRequiredInternalState = 1;
	var NATIVE_WEAK_MAP = requireWeakMapBasicDetection();
	var global = requireGlobal();
	var isObject = requireIsObject();
	var createNonEnumerableProperty = requireCreateNonEnumerableProperty();
	var hasOwn = requireHasOwnProperty();
	var shared = requireSharedStore();
	var sharedKey = requireSharedKey();
	var hiddenKeys = requireHiddenKeys();

	var OBJECT_ALREADY_INITIALIZED = 'Object already initialized';
	var TypeError = global.TypeError;
	var WeakMap = global.WeakMap;
	var set, get, has;

	var enforce = function (it) {
	  return has(it) ? get(it) : set(it, {});
	};

	var getterFor = function (TYPE) {
	  return function (it) {
	    var state;
	    if (!isObject(it) || (state = get(it)).type !== TYPE) {
	      throw new TypeError('Incompatible receiver, ' + TYPE + ' required');
	    } return state;
	  };
	};

	if (NATIVE_WEAK_MAP || shared.state) {
	  var store = shared.state || (shared.state = new WeakMap());
	  /* eslint-disable no-self-assign -- prototype methods protection */
	  store.get = store.get;
	  store.has = store.has;
	  store.set = store.set;
	  /* eslint-enable no-self-assign -- prototype methods protection */
	  set = function (it, metadata) {
	    if (store.has(it)) throw new TypeError(OBJECT_ALREADY_INITIALIZED);
	    metadata.facade = it;
	    store.set(it, metadata);
	    return metadata;
	  };
	  get = function (it) {
	    return store.get(it) || {};
	  };
	  has = function (it) {
	    return store.has(it);
	  };
	} else {
	  var STATE = sharedKey('state');
	  hiddenKeys[STATE] = true;
	  set = function (it, metadata) {
	    if (hasOwn(it, STATE)) throw new TypeError(OBJECT_ALREADY_INITIALIZED);
	    metadata.facade = it;
	    createNonEnumerableProperty(it, STATE, metadata);
	    return metadata;
	  };
	  get = function (it) {
	    return hasOwn(it, STATE) ? it[STATE] : {};
	  };
	  has = function (it) {
	    return hasOwn(it, STATE);
	  };
	}

	internalState = {
	  set: set,
	  get: get,
	  has: has,
	  enforce: enforce,
	  getterFor: getterFor
	};
	return internalState;
}

var hasRequiredMakeBuiltIn;

function requireMakeBuiltIn () {
	if (hasRequiredMakeBuiltIn) return makeBuiltIn.exports;
	hasRequiredMakeBuiltIn = 1;
	var uncurryThis = requireFunctionUncurryThis();
	var fails = requireFails();
	var isCallable = requireIsCallable();
	var hasOwn = requireHasOwnProperty();
	var DESCRIPTORS = requireDescriptors();
	var CONFIGURABLE_FUNCTION_NAME = requireFunctionName().CONFIGURABLE;
	var inspectSource = requireInspectSource();
	var InternalStateModule = requireInternalState();

	var enforceInternalState = InternalStateModule.enforce;
	var getInternalState = InternalStateModule.get;
	var $String = String;
	// eslint-disable-next-line es/no-object-defineproperty -- safe
	var defineProperty = Object.defineProperty;
	var stringSlice = uncurryThis(''.slice);
	var replace = uncurryThis(''.replace);
	var join = uncurryThis([].join);

	var CONFIGURABLE_LENGTH = DESCRIPTORS && !fails(function () {
	  return defineProperty(function () { /* empty */ }, 'length', { value: 8 }).length !== 8;
	});

	var TEMPLATE = String(String).split('String');

	var makeBuiltIn$1 = makeBuiltIn.exports = function (value, name, options) {
	  if (stringSlice($String(name), 0, 7) === 'Symbol(') {
	    name = '[' + replace($String(name), /^Symbol\(([^)]*)\).*$/, '$1') + ']';
	  }
	  if (options && options.getter) name = 'get ' + name;
	  if (options && options.setter) name = 'set ' + name;
	  if (!hasOwn(value, 'name') || (CONFIGURABLE_FUNCTION_NAME && value.name !== name)) {
	    if (DESCRIPTORS) defineProperty(value, 'name', { value: name, configurable: true });
	    else value.name = name;
	  }
	  if (CONFIGURABLE_LENGTH && options && hasOwn(options, 'arity') && value.length !== options.arity) {
	    defineProperty(value, 'length', { value: options.arity });
	  }
	  try {
	    if (options && hasOwn(options, 'constructor') && options.constructor) {
	      if (DESCRIPTORS) defineProperty(value, 'prototype', { writable: false });
	    // in V8 ~ Chrome 53, prototypes of some methods, like `Array.prototype.values`, are non-writable
	    } else if (value.prototype) value.prototype = undefined;
	  } catch (error) { /* empty */ }
	  var state = enforceInternalState(value);
	  if (!hasOwn(state, 'source')) {
	    state.source = join(TEMPLATE, typeof name == 'string' ? name : '');
	  } return value;
	};

	// add fake Function#toString for correct work wrapped methods / constructors with methods like LoDash isNative
	// eslint-disable-next-line no-extend-native -- required
	Function.prototype.toString = makeBuiltIn$1(function toString() {
	  return isCallable(this) && getInternalState(this).source || inspectSource(this);
	}, 'toString');
	return makeBuiltIn.exports;
}

var defineBuiltIn;
var hasRequiredDefineBuiltIn;

function requireDefineBuiltIn () {
	if (hasRequiredDefineBuiltIn) return defineBuiltIn;
	hasRequiredDefineBuiltIn = 1;
	var isCallable = requireIsCallable();
	var definePropertyModule = requireObjectDefineProperty();
	var makeBuiltIn = requireMakeBuiltIn();
	var defineGlobalProperty = requireDefineGlobalProperty();

	defineBuiltIn = function (O, key, value, options) {
	  if (!options) options = {};
	  var simple = options.enumerable;
	  var name = options.name !== undefined ? options.name : key;
	  if (isCallable(value)) makeBuiltIn(value, name, options);
	  if (options.global) {
	    if (simple) O[key] = value;
	    else defineGlobalProperty(key, value);
	  } else {
	    try {
	      if (!options.unsafe) delete O[key];
	      else if (O[key]) simple = true;
	    } catch (error) { /* empty */ }
	    if (simple) O[key] = value;
	    else definePropertyModule.f(O, key, {
	      value: value,
	      enumerable: false,
	      configurable: !options.nonConfigurable,
	      writable: !options.nonWritable
	    });
	  } return O;
	};
	return defineBuiltIn;
}

var objectGetOwnPropertyNames = {};

var mathTrunc;
var hasRequiredMathTrunc;

function requireMathTrunc () {
	if (hasRequiredMathTrunc) return mathTrunc;
	hasRequiredMathTrunc = 1;
	var ceil = Math.ceil;
	var floor = Math.floor;

	// `Math.trunc` method
	// https://tc39.es/ecma262/#sec-math.trunc
	// eslint-disable-next-line es/no-math-trunc -- safe
	mathTrunc = Math.trunc || function trunc(x) {
	  var n = +x;
	  return (n > 0 ? floor : ceil)(n);
	};
	return mathTrunc;
}

var toIntegerOrInfinity;
var hasRequiredToIntegerOrInfinity;

function requireToIntegerOrInfinity () {
	if (hasRequiredToIntegerOrInfinity) return toIntegerOrInfinity;
	hasRequiredToIntegerOrInfinity = 1;
	var trunc = requireMathTrunc();

	// `ToIntegerOrInfinity` abstract operation
	// https://tc39.es/ecma262/#sec-tointegerorinfinity
	toIntegerOrInfinity = function (argument) {
	  var number = +argument;
	  // eslint-disable-next-line no-self-compare -- NaN check
	  return number !== number || number === 0 ? 0 : trunc(number);
	};
	return toIntegerOrInfinity;
}

var toAbsoluteIndex;
var hasRequiredToAbsoluteIndex;

function requireToAbsoluteIndex () {
	if (hasRequiredToAbsoluteIndex) return toAbsoluteIndex;
	hasRequiredToAbsoluteIndex = 1;
	var toIntegerOrInfinity = requireToIntegerOrInfinity();

	var max = Math.max;
	var min = Math.min;

	// Helper for a popular repeating case of the spec:
	// Let integer be ? ToInteger(index).
	// If integer < 0, let result be max((length + integer), 0); else let result be min(integer, length).
	toAbsoluteIndex = function (index, length) {
	  var integer = toIntegerOrInfinity(index);
	  return integer < 0 ? max(integer + length, 0) : min(integer, length);
	};
	return toAbsoluteIndex;
}

var toLength;
var hasRequiredToLength;

function requireToLength () {
	if (hasRequiredToLength) return toLength;
	hasRequiredToLength = 1;
	var toIntegerOrInfinity = requireToIntegerOrInfinity();

	var min = Math.min;

	// `ToLength` abstract operation
	// https://tc39.es/ecma262/#sec-tolength
	toLength = function (argument) {
	  var len = toIntegerOrInfinity(argument);
	  return len > 0 ? min(len, 0x1FFFFFFFFFFFFF) : 0; // 2 ** 53 - 1 == 9007199254740991
	};
	return toLength;
}

var lengthOfArrayLike;
var hasRequiredLengthOfArrayLike;

function requireLengthOfArrayLike () {
	if (hasRequiredLengthOfArrayLike) return lengthOfArrayLike;
	hasRequiredLengthOfArrayLike = 1;
	var toLength = requireToLength();

	// `LengthOfArrayLike` abstract operation
	// https://tc39.es/ecma262/#sec-lengthofarraylike
	lengthOfArrayLike = function (obj) {
	  return toLength(obj.length);
	};
	return lengthOfArrayLike;
}

var arrayIncludes;
var hasRequiredArrayIncludes;

function requireArrayIncludes () {
	if (hasRequiredArrayIncludes) return arrayIncludes;
	hasRequiredArrayIncludes = 1;
	var toIndexedObject = requireToIndexedObject();
	var toAbsoluteIndex = requireToAbsoluteIndex();
	var lengthOfArrayLike = requireLengthOfArrayLike();

	// `Array.prototype.{ indexOf, includes }` methods implementation
	var createMethod = function (IS_INCLUDES) {
	  return function ($this, el, fromIndex) {
	    var O = toIndexedObject($this);
	    var length = lengthOfArrayLike(O);
	    if (length === 0) return !IS_INCLUDES && -1;
	    var index = toAbsoluteIndex(fromIndex, length);
	    var value;
	    // Array#includes uses SameValueZero equality algorithm
	    // eslint-disable-next-line no-self-compare -- NaN check
	    if (IS_INCLUDES && el !== el) while (length > index) {
	      value = O[index++];
	      // eslint-disable-next-line no-self-compare -- NaN check
	      if (value !== value) return true;
	    // Array#indexOf ignores holes, Array#includes - not
	    } else for (;length > index; index++) {
	      if ((IS_INCLUDES || index in O) && O[index] === el) return IS_INCLUDES || index || 0;
	    } return !IS_INCLUDES && -1;
	  };
	};

	arrayIncludes = {
	  // `Array.prototype.includes` method
	  // https://tc39.es/ecma262/#sec-array.prototype.includes
	  includes: createMethod(true),
	  // `Array.prototype.indexOf` method
	  // https://tc39.es/ecma262/#sec-array.prototype.indexof
	  indexOf: createMethod(false)
	};
	return arrayIncludes;
}

var objectKeysInternal;
var hasRequiredObjectKeysInternal;

function requireObjectKeysInternal () {
	if (hasRequiredObjectKeysInternal) return objectKeysInternal;
	hasRequiredObjectKeysInternal = 1;
	var uncurryThis = requireFunctionUncurryThis();
	var hasOwn = requireHasOwnProperty();
	var toIndexedObject = requireToIndexedObject();
	var indexOf = requireArrayIncludes().indexOf;
	var hiddenKeys = requireHiddenKeys();

	var push = uncurryThis([].push);

	objectKeysInternal = function (object, names) {
	  var O = toIndexedObject(object);
	  var i = 0;
	  var result = [];
	  var key;
	  for (key in O) !hasOwn(hiddenKeys, key) && hasOwn(O, key) && push(result, key);
	  // Don't enum bug & hidden keys
	  while (names.length > i) if (hasOwn(O, key = names[i++])) {
	    ~indexOf(result, key) || push(result, key);
	  }
	  return result;
	};
	return objectKeysInternal;
}

var enumBugKeys;
var hasRequiredEnumBugKeys;

function requireEnumBugKeys () {
	if (hasRequiredEnumBugKeys) return enumBugKeys;
	hasRequiredEnumBugKeys = 1;
	// IE8- don't enum bug keys
	enumBugKeys = [
	  'constructor',
	  'hasOwnProperty',
	  'isPrototypeOf',
	  'propertyIsEnumerable',
	  'toLocaleString',
	  'toString',
	  'valueOf'
	];
	return enumBugKeys;
}

var hasRequiredObjectGetOwnPropertyNames;

function requireObjectGetOwnPropertyNames () {
	if (hasRequiredObjectGetOwnPropertyNames) return objectGetOwnPropertyNames;
	hasRequiredObjectGetOwnPropertyNames = 1;
	var internalObjectKeys = requireObjectKeysInternal();
	var enumBugKeys = requireEnumBugKeys();

	var hiddenKeys = enumBugKeys.concat('length', 'prototype');

	// `Object.getOwnPropertyNames` method
	// https://tc39.es/ecma262/#sec-object.getownpropertynames
	// eslint-disable-next-line es/no-object-getownpropertynames -- safe
	objectGetOwnPropertyNames.f = Object.getOwnPropertyNames || function getOwnPropertyNames(O) {
	  return internalObjectKeys(O, hiddenKeys);
	};
	return objectGetOwnPropertyNames;
}

var objectGetOwnPropertySymbols = {};

var hasRequiredObjectGetOwnPropertySymbols;

function requireObjectGetOwnPropertySymbols () {
	if (hasRequiredObjectGetOwnPropertySymbols) return objectGetOwnPropertySymbols;
	hasRequiredObjectGetOwnPropertySymbols = 1;
	// eslint-disable-next-line es/no-object-getownpropertysymbols -- safe
	objectGetOwnPropertySymbols.f = Object.getOwnPropertySymbols;
	return objectGetOwnPropertySymbols;
}

var ownKeys;
var hasRequiredOwnKeys;

function requireOwnKeys () {
	if (hasRequiredOwnKeys) return ownKeys;
	hasRequiredOwnKeys = 1;
	var getBuiltIn = requireGetBuiltIn();
	var uncurryThis = requireFunctionUncurryThis();
	var getOwnPropertyNamesModule = requireObjectGetOwnPropertyNames();
	var getOwnPropertySymbolsModule = requireObjectGetOwnPropertySymbols();
	var anObject = requireAnObject();

	var concat = uncurryThis([].concat);

	// all object keys, includes non-enumerable and symbols
	ownKeys = getBuiltIn('Reflect', 'ownKeys') || function ownKeys(it) {
	  var keys = getOwnPropertyNamesModule.f(anObject(it));
	  var getOwnPropertySymbols = getOwnPropertySymbolsModule.f;
	  return getOwnPropertySymbols ? concat(keys, getOwnPropertySymbols(it)) : keys;
	};
	return ownKeys;
}

var copyConstructorProperties;
var hasRequiredCopyConstructorProperties;

function requireCopyConstructorProperties () {
	if (hasRequiredCopyConstructorProperties) return copyConstructorProperties;
	hasRequiredCopyConstructorProperties = 1;
	var hasOwn = requireHasOwnProperty();
	var ownKeys = requireOwnKeys();
	var getOwnPropertyDescriptorModule = requireObjectGetOwnPropertyDescriptor();
	var definePropertyModule = requireObjectDefineProperty();

	copyConstructorProperties = function (target, source, exceptions) {
	  var keys = ownKeys(source);
	  var defineProperty = definePropertyModule.f;
	  var getOwnPropertyDescriptor = getOwnPropertyDescriptorModule.f;
	  for (var i = 0; i < keys.length; i++) {
	    var key = keys[i];
	    if (!hasOwn(target, key) && !(exceptions && hasOwn(exceptions, key))) {
	      defineProperty(target, key, getOwnPropertyDescriptor(source, key));
	    }
	  }
	};
	return copyConstructorProperties;
}

var isForced_1;
var hasRequiredIsForced;

function requireIsForced () {
	if (hasRequiredIsForced) return isForced_1;
	hasRequiredIsForced = 1;
	var fails = requireFails();
	var isCallable = requireIsCallable();

	var replacement = /#|\.prototype\./;

	var isForced = function (feature, detection) {
	  var value = data[normalize(feature)];
	  return value === POLYFILL ? true
	    : value === NATIVE ? false
	    : isCallable(detection) ? fails(detection)
	    : !!detection;
	};

	var normalize = isForced.normalize = function (string) {
	  return String(string).replace(replacement, '.').toLowerCase();
	};

	var data = isForced.data = {};
	var NATIVE = isForced.NATIVE = 'N';
	var POLYFILL = isForced.POLYFILL = 'P';

	isForced_1 = isForced;
	return isForced_1;
}

var _export;
var hasRequired_export;

function require_export () {
	if (hasRequired_export) return _export;
	hasRequired_export = 1;
	var global = requireGlobal();
	var getOwnPropertyDescriptor = requireObjectGetOwnPropertyDescriptor().f;
	var createNonEnumerableProperty = requireCreateNonEnumerableProperty();
	var defineBuiltIn = requireDefineBuiltIn();
	var defineGlobalProperty = requireDefineGlobalProperty();
	var copyConstructorProperties = requireCopyConstructorProperties();
	var isForced = requireIsForced();

	/*
	  options.target         - name of the target object
	  options.global         - target is the global object
	  options.stat           - export as static methods of target
	  options.proto          - export as prototype methods of target
	  options.real           - real prototype method for the `pure` version
	  options.forced         - export even if the native feature is available
	  options.bind           - bind methods to the target, required for the `pure` version
	  options.wrap           - wrap constructors to preventing global pollution, required for the `pure` version
	  options.unsafe         - use the simple assignment of property instead of delete + defineProperty
	  options.sham           - add a flag to not completely full polyfills
	  options.enumerable     - export as enumerable property
	  options.dontCallGetSet - prevent calling a getter on target
	  options.name           - the .name of the function if it does not match the key
	*/
	_export = function (options, source) {
	  var TARGET = options.target;
	  var GLOBAL = options.global;
	  var STATIC = options.stat;
	  var FORCED, target, key, targetProperty, sourceProperty, descriptor;
	  if (GLOBAL) {
	    target = global;
	  } else if (STATIC) {
	    target = global[TARGET] || defineGlobalProperty(TARGET, {});
	  } else {
	    target = global[TARGET] && global[TARGET].prototype;
	  }
	  if (target) for (key in source) {
	    sourceProperty = source[key];
	    if (options.dontCallGetSet) {
	      descriptor = getOwnPropertyDescriptor(target, key);
	      targetProperty = descriptor && descriptor.value;
	    } else targetProperty = target[key];
	    FORCED = isForced(GLOBAL ? key : TARGET + (STATIC ? '.' : '#') + key, options.forced);
	    // contained in target
	    if (!FORCED && targetProperty !== undefined) {
	      if (typeof sourceProperty == typeof targetProperty) continue;
	      copyConstructorProperties(sourceProperty, targetProperty);
	    }
	    // add a flag to not completely full polyfills
	    if (options.sham || (targetProperty && targetProperty.sham)) {
	      createNonEnumerableProperty(sourceProperty, 'sham', true);
	    }
	    defineBuiltIn(target, key, sourceProperty, options);
	  }
	};
	return _export;
}

var isArray;
var hasRequiredIsArray;

function requireIsArray () {
	if (hasRequiredIsArray) return isArray;
	hasRequiredIsArray = 1;
	var classof = requireClassofRaw();

	// `IsArray` abstract operation
	// https://tc39.es/ecma262/#sec-isarray
	// eslint-disable-next-line es/no-array-isarray -- safe
	isArray = Array.isArray || function isArray(argument) {
	  return classof(argument) === 'Array';
	};
	return isArray;
}

var arraySetLength;
var hasRequiredArraySetLength;

function requireArraySetLength () {
	if (hasRequiredArraySetLength) return arraySetLength;
	hasRequiredArraySetLength = 1;
	var DESCRIPTORS = requireDescriptors();
	var isArray = requireIsArray();

	var $TypeError = TypeError;
	// eslint-disable-next-line es/no-object-getownpropertydescriptor -- safe
	var getOwnPropertyDescriptor = Object.getOwnPropertyDescriptor;

	// Safari < 13 does not throw an error in this case
	var SILENT_ON_NON_WRITABLE_LENGTH_SET = DESCRIPTORS && !function () {
	  // makes no sense without proper strict mode support
	  if (this !== undefined) return true;
	  try {
	    // eslint-disable-next-line es/no-object-defineproperty -- safe
	    Object.defineProperty([], 'length', { writable: false }).length = 1;
	  } catch (error) {
	    return error instanceof TypeError;
	  }
	}();

	arraySetLength = SILENT_ON_NON_WRITABLE_LENGTH_SET ? function (O, length) {
	  if (isArray(O) && !getOwnPropertyDescriptor(O, 'length').writable) {
	    throw new $TypeError('Cannot set read only .length');
	  } return O.length = length;
	} : function (O, length) {
	  return O.length = length;
	};
	return arraySetLength;
}

var doesNotExceedSafeInteger;
var hasRequiredDoesNotExceedSafeInteger;

function requireDoesNotExceedSafeInteger () {
	if (hasRequiredDoesNotExceedSafeInteger) return doesNotExceedSafeInteger;
	hasRequiredDoesNotExceedSafeInteger = 1;
	var $TypeError = TypeError;
	var MAX_SAFE_INTEGER = 0x1FFFFFFFFFFFFF; // 2 ** 53 - 1 == 9007199254740991

	doesNotExceedSafeInteger = function (it) {
	  if (it > MAX_SAFE_INTEGER) throw $TypeError('Maximum allowed index exceeded');
	  return it;
	};
	return doesNotExceedSafeInteger;
}

var hasRequiredEs_array_push;

function requireEs_array_push () {
	if (hasRequiredEs_array_push) return es_array_push;
	hasRequiredEs_array_push = 1;
	var $ = require_export();
	var toObject = requireToObject();
	var lengthOfArrayLike = requireLengthOfArrayLike();
	var setArrayLength = requireArraySetLength();
	var doesNotExceedSafeInteger = requireDoesNotExceedSafeInteger();
	var fails = requireFails();

	var INCORRECT_TO_LENGTH = fails(function () {
	  return [].push.call({ length: 0x100000000 }, 1) !== 4294967297;
	});

	// V8 <= 121 and Safari <= 15.4; FF < 23 throws InternalError
	// https://bugs.chromium.org/p/v8/issues/detail?id=12681
	var properErrorOnNonWritableLength = function () {
	  try {
	    // eslint-disable-next-line es/no-object-defineproperty -- safe
	    Object.defineProperty([], 'length', { writable: false }).push();
	  } catch (error) {
	    return error instanceof TypeError;
	  }
	};

	var FORCED = INCORRECT_TO_LENGTH || !properErrorOnNonWritableLength();

	// `Array.prototype.push` method
	// https://tc39.es/ecma262/#sec-array.prototype.push
	$({ target: 'Array', proto: true, arity: 1, forced: FORCED }, {
	  // eslint-disable-next-line no-unused-vars -- required for `.length`
	  push: function push(item) {
	    var O = toObject(this);
	    var len = lengthOfArrayLike(O);
	    var argCount = arguments.length;
	    doesNotExceedSafeInteger(len + argCount);
	    for (var i = 0; i < argCount; i++) {
	      O[len] = arguments[i];
	      len++;
	    }
	    setArrayLength(O, len);
	    return len;
	  }
	});
	return es_array_push;
}

requireEs_array_push();

// A is m x n. B is n x p. product is m x p.
function multiplyMatrices(A, B) {
  let m = A.length;
  if (!Array.isArray(A[0])) {
    // A is vector, convert to [[a, b, c, ...]]
    A = [A];
  }
  if (!Array.isArray(B[0])) {
    // B is vector, convert to [[a], [b], [c], ...]]
    B = B.map(x => [x]);
  }
  let p = B[0].length;
  let B_cols = B[0].map((_, i) => B.map(x => x[i])); // transpose B
  let product = A.map(row => B_cols.map(col => {
    let ret = 0;
    if (!Array.isArray(row)) {
      for (let c of col) {
        ret += row * c;
      }
      return ret;
    }
    for (let i = 0; i < row.length; i++) {
      ret += row[i] * (col[i] || 0);
    }
    return ret;
  }));
  if (m === 1) {
    product = product[0]; // Avoid [[a, b, c, ...]]
  }
  if (p === 1) {
    return product.map(x => x[0]); // Avoid [[a], [b], [c], ...]]
  }
  return product;
}

/**
 * Check if a value is a string (including a String object)
 * @param {*} str - Value to check
 * @returns {boolean}
 */
function isString(str) {
  return type(str) === "string";
}

/**
 * Determine the internal JavaScript [[Class]] of an object.
 * @param {*} o - Value to check
 * @returns {string}
 */
function type(o) {
  let str = Object.prototype.toString.call(o);
  return (str.match(/^\[object\s+(.*?)\]$/)[1] || "").toLowerCase();
}
function serializeNumber(n, _ref) {
  let {
    precision,
    unit
  } = _ref;
  if (isNone(n)) {
    return "none";
  }
  return toPrecision(n, precision) + (unit !== null && unit !== void 0 ? unit : "");
}

/**
 * Check if a value corresponds to a none argument
 * @param {*} n - Value to check
 * @returns {boolean}
 */
function isNone(n) {
  return Number.isNaN(n) || n instanceof Number && (n === null || n === void 0 ? void 0 : n.none);
}

/**
 * Replace none values with 0
 */
function skipNone(n) {
  return isNone(n) ? 0 : n;
}

/**
 * Round a number to a certain number of significant digits
 * @param {number} n - The number to round
 * @param {number} precision - Number of significant digits
 */
function toPrecision(n, precision) {
  if (n === 0) {
    return 0;
  }
  let integer = ~~n;
  let digits = 0;
  if (integer && precision) {
    digits = ~~Math.log10(Math.abs(integer)) + 1;
  }
  const multiplier = 10.0 ** (precision - digits);
  return Math.floor(n * multiplier + 0.5) / multiplier;
}
const angleFactor = {
  deg: 1,
  grad: 0.9,
  rad: 180 / Math.PI,
  turn: 360
};

/**
* Parse a CSS function, regardless of its name and arguments
* @param String str String to parse
* @return {{name, args, rawArgs}}
*/
function parseFunction(str) {
  if (!str) {
    return;
  }
  str = str.trim();
  const isFunctionRegex = /^([a-z]+)\((.+?)\)$/i;
  const isNumberRegex = /^-?[\d.]+$/;
  const unitValueRegex = /%|deg|g?rad|turn$/;
  const singleArgument = /\/?\s*(none|[-\w.]+(?:%|deg|g?rad|turn)?)/g;
  let parts = str.match(isFunctionRegex);
  if (parts) {
    // It is a function, parse args
    let args = [];
    parts[2].replace(singleArgument, ($0, rawArg) => {
      let match = rawArg.match(unitValueRegex);
      let arg = rawArg;
      if (match) {
        let unit = match[0];
        // Drop unit from value
        let unitlessArg = arg.slice(0, -unit.length);
        if (unit === "%") {
          // Convert percentages to 0-1 numbers
          arg = new Number(unitlessArg / 100);
          arg.type = "<percentage>";
        } else {
          // Multiply angle by appropriate factor for its unit
          arg = new Number(unitlessArg * angleFactor[unit]);
          arg.type = "<angle>";
          arg.unit = unit;
        }
      } else if (isNumberRegex.test(arg)) {
        // Convert numerical args to numbers
        arg = new Number(arg);
        arg.type = "<number>";
      } else if (arg === "none") {
        arg = new Number(NaN);
        arg.none = true;
      }
      if ($0.startsWith("/")) {
        // It's alpha
        arg = arg instanceof Number ? arg : new Number(arg);
        arg.alpha = true;
      }
      if (typeof arg === "object" && arg instanceof Number) {
        arg.raw = rawArg;
      }
      args.push(arg);
    });
    return {
      name: parts[1].toLowerCase(),
      rawName: parts[1],
      rawArgs: parts[2],
      // An argument could be (as of css-color-4):
      // a number, percentage, degrees (hue), ident (in color())
      args
    };
  }
}
function last(arr) {
  return arr[arr.length - 1];
}
function interpolate(start, end, p) {
  if (isNaN(start)) {
    return end;
  }
  if (isNaN(end)) {
    return start;
  }
  return start + (end - start) * p;
}
function interpolateInv(start, end, value) {
  return (value - start) / (end - start);
}
function mapRange(from, to, value) {
  return interpolate(to[0], to[1], interpolateInv(from[0], from[1], value));
}
function parseCoordGrammar(coordGrammars) {
  return coordGrammars.map(coordGrammar => {
    return coordGrammar.split("|").map(type => {
      type = type.trim();
      let range = type.match(/^(<[a-z]+>)\[(-?[.\d]+),\s*(-?[.\d]+)\]?$/);
      if (range) {
        let ret = new String(range[1]);
        ret.range = [+range[2], +range[3]];
        return ret;
      }
      return type;
    });
  });
}

/**
 * Clamp value between the minimum and maximum
 * @param {number} min minimum value to return
 * @param {number} val the value to return if it is between min and max
 * @param {number} max maximum value to return
 * @returns number
 */
function clamp(min, val, max) {
  return Math.max(Math.min(max, val), min);
}

/**
 * Copy sign of one value to another.
 * @param {number} - to number to copy sign to
 * @param {number} - from number to copy sign from
 * @returns number
 */
function copySign(to, from) {
  return Math.sign(to) === Math.sign(from) ? to : -to;
}

/**
 * Perform pow on a signed number and copy sign to result
 * @param {number} - base the base number
 * @param {number} - exp the exponent
 * @returns number
 */
function spow(base, exp) {
  return copySign(Math.abs(base) ** exp, base);
}

/**
 * Perform a divide, but return zero if the numerator is zero
 * @param {number} n - the numerator
 * @param {number} d - the denominator
 * @returns number
 */
function zdiv(n, d) {
  return d === 0 ? 0 : n / d;
}

/**
 * Perform a bisect on a sorted list and locate the insertion point for
 * a value in arr to maintain sorted order.
 * @param {number[]} arr - array of sorted numbers
 * @param {number} value - value to find insertion point for
 * @param {number} lo - used to specify a the low end of a subset of the list
 * @param {number} hi - used to specify a the high end of a subset of the list
 * @returns number
 */
function bisectLeft(arr, value) {
  let lo = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : 0;
  let hi = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : arr.length;
  while (lo < hi) {
    const mid = lo + hi >> 1;
    if (arr[mid] < value) {
      lo = mid + 1;
    } else {
      hi = mid;
    }
  }
  return lo;
}

var util = /*#__PURE__*/Object.freeze({
	__proto__: null,
	bisectLeft: bisectLeft,
	clamp: clamp,
	copySign: copySign,
	interpolate: interpolate,
	interpolateInv: interpolateInv,
	isNone: isNone,
	isString: isString,
	last: last,
	mapRange: mapRange,
	multiplyMatrices: multiplyMatrices,
	parseCoordGrammar: parseCoordGrammar,
	parseFunction: parseFunction,
	serializeNumber: serializeNumber,
	skipNone: skipNone,
	spow: spow,
	toPrecision: toPrecision,
	type: type,
	zdiv: zdiv
});

/**
 * A class for adding deep extensibility to any piece of JS code
 */
class Hooks {
  add(name, callback, first) {
    if (typeof arguments[0] != "string") {
      // Multiple hooks
      for (var name in arguments[0]) {
        this.add(name, arguments[0][name], arguments[1]);
      }
      return;
    }
    (Array.isArray(name) ? name : [name]).forEach(function (name) {
      this[name] = this[name] || [];
      if (callback) {
        this[name][first ? "unshift" : "push"](callback);
      }
    }, this);
  }
  run(name, env) {
    this[name] = this[name] || [];
    this[name].forEach(function (callback) {
      callback.call(env && env.context ? env.context : env, env);
    });
  }
}

/**
 * The instance of {@link Hooks} used throughout Color.js
 */
const hooks = new Hooks();

var _globalThis$process;
// Global defaults one may want to configure
var defaults = {
  gamut_mapping: "css",
  precision: 5,
  deltaE: "76",
  // Default deltaE method
  verbose: (globalThis === null || globalThis === void 0 || (_globalThis$process = globalThis.process) === null || _globalThis$process === void 0 || (_globalThis$process = _globalThis$process.env) === null || _globalThis$process === void 0 || (_globalThis$process = _globalThis$process.NODE_ENV) === null || _globalThis$process === void 0 ? void 0 : _globalThis$process.toLowerCase()) !== "test",
  warn: function warn(msg) {
    if (this.verbose) {
      var _globalThis$console, _globalThis$console$w;
      globalThis === null || globalThis === void 0 || (_globalThis$console = globalThis.console) === null || _globalThis$console === void 0 || (_globalThis$console$w = _globalThis$console.warn) === null || _globalThis$console$w === void 0 || _globalThis$console$w.call(_globalThis$console, msg);
    }
  }
};

var es_error_cause = {};

var functionApply;
var hasRequiredFunctionApply;

function requireFunctionApply () {
	if (hasRequiredFunctionApply) return functionApply;
	hasRequiredFunctionApply = 1;
	var NATIVE_BIND = requireFunctionBindNative();

	var FunctionPrototype = Function.prototype;
	var apply = FunctionPrototype.apply;
	var call = FunctionPrototype.call;

	// eslint-disable-next-line es/no-reflect -- safe
	functionApply = typeof Reflect == 'object' && Reflect.apply || (NATIVE_BIND ? call.bind(apply) : function () {
	  return call.apply(apply, arguments);
	});
	return functionApply;
}

var functionUncurryThisAccessor;
var hasRequiredFunctionUncurryThisAccessor;

function requireFunctionUncurryThisAccessor () {
	if (hasRequiredFunctionUncurryThisAccessor) return functionUncurryThisAccessor;
	hasRequiredFunctionUncurryThisAccessor = 1;
	var uncurryThis = requireFunctionUncurryThis();
	var aCallable = requireACallable();

	functionUncurryThisAccessor = function (object, key, method) {
	  try {
	    // eslint-disable-next-line es/no-object-getownpropertydescriptor -- safe
	    return uncurryThis(aCallable(Object.getOwnPropertyDescriptor(object, key)[method]));
	  } catch (error) { /* empty */ }
	};
	return functionUncurryThisAccessor;
}

var isPossiblePrototype;
var hasRequiredIsPossiblePrototype;

function requireIsPossiblePrototype () {
	if (hasRequiredIsPossiblePrototype) return isPossiblePrototype;
	hasRequiredIsPossiblePrototype = 1;
	var isObject = requireIsObject();

	isPossiblePrototype = function (argument) {
	  return isObject(argument) || argument === null;
	};
	return isPossiblePrototype;
}

var aPossiblePrototype;
var hasRequiredAPossiblePrototype;

function requireAPossiblePrototype () {
	if (hasRequiredAPossiblePrototype) return aPossiblePrototype;
	hasRequiredAPossiblePrototype = 1;
	var isPossiblePrototype = requireIsPossiblePrototype();

	var $String = String;
	var $TypeError = TypeError;

	aPossiblePrototype = function (argument) {
	  if (isPossiblePrototype(argument)) return argument;
	  throw new $TypeError("Can't set " + $String(argument) + ' as a prototype');
	};
	return aPossiblePrototype;
}

var objectSetPrototypeOf;
var hasRequiredObjectSetPrototypeOf;

function requireObjectSetPrototypeOf () {
	if (hasRequiredObjectSetPrototypeOf) return objectSetPrototypeOf;
	hasRequiredObjectSetPrototypeOf = 1;
	/* eslint-disable no-proto -- safe */
	var uncurryThisAccessor = requireFunctionUncurryThisAccessor();
	var anObject = requireAnObject();
	var aPossiblePrototype = requireAPossiblePrototype();

	// `Object.setPrototypeOf` method
	// https://tc39.es/ecma262/#sec-object.setprototypeof
	// Works with __proto__ only. Old v8 can't work with null proto objects.
	// eslint-disable-next-line es/no-object-setprototypeof -- safe
	objectSetPrototypeOf = Object.setPrototypeOf || ('__proto__' in {} ? function () {
	  var CORRECT_SETTER = false;
	  var test = {};
	  var setter;
	  try {
	    setter = uncurryThisAccessor(Object.prototype, '__proto__', 'set');
	    setter(test, []);
	    CORRECT_SETTER = test instanceof Array;
	  } catch (error) { /* empty */ }
	  return function setPrototypeOf(O, proto) {
	    anObject(O);
	    aPossiblePrototype(proto);
	    if (CORRECT_SETTER) setter(O, proto);
	    else O.__proto__ = proto;
	    return O;
	  };
	}() : undefined);
	return objectSetPrototypeOf;
}

var proxyAccessor;
var hasRequiredProxyAccessor;

function requireProxyAccessor () {
	if (hasRequiredProxyAccessor) return proxyAccessor;
	hasRequiredProxyAccessor = 1;
	var defineProperty = requireObjectDefineProperty().f;

	proxyAccessor = function (Target, Source, key) {
	  key in Target || defineProperty(Target, key, {
	    configurable: true,
	    get: function () { return Source[key]; },
	    set: function (it) { Source[key] = it; }
	  });
	};
	return proxyAccessor;
}

var inheritIfRequired;
var hasRequiredInheritIfRequired;

function requireInheritIfRequired () {
	if (hasRequiredInheritIfRequired) return inheritIfRequired;
	hasRequiredInheritIfRequired = 1;
	var isCallable = requireIsCallable();
	var isObject = requireIsObject();
	var setPrototypeOf = requireObjectSetPrototypeOf();

	// makes subclassing work correct for wrapped built-ins
	inheritIfRequired = function ($this, dummy, Wrapper) {
	  var NewTarget, NewTargetPrototype;
	  if (
	    // it can work only with native `setPrototypeOf`
	    setPrototypeOf &&
	    // we haven't completely correct pre-ES6 way for getting `new.target`, so use this
	    isCallable(NewTarget = dummy.constructor) &&
	    NewTarget !== Wrapper &&
	    isObject(NewTargetPrototype = NewTarget.prototype) &&
	    NewTargetPrototype !== Wrapper.prototype
	  ) setPrototypeOf($this, NewTargetPrototype);
	  return $this;
	};
	return inheritIfRequired;
}

var toStringTagSupport;
var hasRequiredToStringTagSupport;

function requireToStringTagSupport () {
	if (hasRequiredToStringTagSupport) return toStringTagSupport;
	hasRequiredToStringTagSupport = 1;
	var wellKnownSymbol = requireWellKnownSymbol();

	var TO_STRING_TAG = wellKnownSymbol('toStringTag');
	var test = {};

	test[TO_STRING_TAG] = 'z';

	toStringTagSupport = String(test) === '[object z]';
	return toStringTagSupport;
}

var classof;
var hasRequiredClassof;

function requireClassof () {
	if (hasRequiredClassof) return classof;
	hasRequiredClassof = 1;
	var TO_STRING_TAG_SUPPORT = requireToStringTagSupport();
	var isCallable = requireIsCallable();
	var classofRaw = requireClassofRaw();
	var wellKnownSymbol = requireWellKnownSymbol();

	var TO_STRING_TAG = wellKnownSymbol('toStringTag');
	var $Object = Object;

	// ES3 wrong here
	var CORRECT_ARGUMENTS = classofRaw(function () { return arguments; }()) === 'Arguments';

	// fallback for IE11 Script Access Denied error
	var tryGet = function (it, key) {
	  try {
	    return it[key];
	  } catch (error) { /* empty */ }
	};

	// getting tag from ES6+ `Object.prototype.toString`
	classof = TO_STRING_TAG_SUPPORT ? classofRaw : function (it) {
	  var O, tag, result;
	  return it === undefined ? 'Undefined' : it === null ? 'Null'
	    // @@toStringTag case
	    : typeof (tag = tryGet(O = $Object(it), TO_STRING_TAG)) == 'string' ? tag
	    // builtinTag case
	    : CORRECT_ARGUMENTS ? classofRaw(O)
	    // ES3 arguments fallback
	    : (result = classofRaw(O)) === 'Object' && isCallable(O.callee) ? 'Arguments' : result;
	};
	return classof;
}

var toString;
var hasRequiredToString;

function requireToString () {
	if (hasRequiredToString) return toString;
	hasRequiredToString = 1;
	var classof = requireClassof();

	var $String = String;

	toString = function (argument) {
	  if (classof(argument) === 'Symbol') throw new TypeError('Cannot convert a Symbol value to a string');
	  return $String(argument);
	};
	return toString;
}

var normalizeStringArgument;
var hasRequiredNormalizeStringArgument;

function requireNormalizeStringArgument () {
	if (hasRequiredNormalizeStringArgument) return normalizeStringArgument;
	hasRequiredNormalizeStringArgument = 1;
	var toString = requireToString();

	normalizeStringArgument = function (argument, $default) {
	  return argument === undefined ? arguments.length < 2 ? '' : $default : toString(argument);
	};
	return normalizeStringArgument;
}

var installErrorCause;
var hasRequiredInstallErrorCause;

function requireInstallErrorCause () {
	if (hasRequiredInstallErrorCause) return installErrorCause;
	hasRequiredInstallErrorCause = 1;
	var isObject = requireIsObject();
	var createNonEnumerableProperty = requireCreateNonEnumerableProperty();

	// `InstallErrorCause` abstract operation
	// https://tc39.es/proposal-error-cause/#sec-errorobjects-install-error-cause
	installErrorCause = function (O, options) {
	  if (isObject(options) && 'cause' in options) {
	    createNonEnumerableProperty(O, 'cause', options.cause);
	  }
	};
	return installErrorCause;
}

var errorStackClear;
var hasRequiredErrorStackClear;

function requireErrorStackClear () {
	if (hasRequiredErrorStackClear) return errorStackClear;
	hasRequiredErrorStackClear = 1;
	var uncurryThis = requireFunctionUncurryThis();

	var $Error = Error;
	var replace = uncurryThis(''.replace);

	var TEST = (function (arg) { return String(new $Error(arg).stack); })('zxcasd');
	// eslint-disable-next-line redos/no-vulnerable -- safe
	var V8_OR_CHAKRA_STACK_ENTRY = /\n\s*at [^:]*:[^\n]*/;
	var IS_V8_OR_CHAKRA_STACK = V8_OR_CHAKRA_STACK_ENTRY.test(TEST);

	errorStackClear = function (stack, dropEntries) {
	  if (IS_V8_OR_CHAKRA_STACK && typeof stack == 'string' && !$Error.prepareStackTrace) {
	    while (dropEntries--) stack = replace(stack, V8_OR_CHAKRA_STACK_ENTRY, '');
	  } return stack;
	};
	return errorStackClear;
}

var errorStackInstallable;
var hasRequiredErrorStackInstallable;

function requireErrorStackInstallable () {
	if (hasRequiredErrorStackInstallable) return errorStackInstallable;
	hasRequiredErrorStackInstallable = 1;
	var fails = requireFails();
	var createPropertyDescriptor = requireCreatePropertyDescriptor();

	errorStackInstallable = !fails(function () {
	  var error = new Error('a');
	  if (!('stack' in error)) return true;
	  // eslint-disable-next-line es/no-object-defineproperty -- safe
	  Object.defineProperty(error, 'stack', createPropertyDescriptor(1, 7));
	  return error.stack !== 7;
	});
	return errorStackInstallable;
}

var errorStackInstall;
var hasRequiredErrorStackInstall;

function requireErrorStackInstall () {
	if (hasRequiredErrorStackInstall) return errorStackInstall;
	hasRequiredErrorStackInstall = 1;
	var createNonEnumerableProperty = requireCreateNonEnumerableProperty();
	var clearErrorStack = requireErrorStackClear();
	var ERROR_STACK_INSTALLABLE = requireErrorStackInstallable();

	// non-standard V8
	var captureStackTrace = Error.captureStackTrace;

	errorStackInstall = function (error, C, stack, dropEntries) {
	  if (ERROR_STACK_INSTALLABLE) {
	    if (captureStackTrace) captureStackTrace(error, C);
	    else createNonEnumerableProperty(error, 'stack', clearErrorStack(stack, dropEntries));
	  }
	};
	return errorStackInstall;
}

var wrapErrorConstructorWithCause;
var hasRequiredWrapErrorConstructorWithCause;

function requireWrapErrorConstructorWithCause () {
	if (hasRequiredWrapErrorConstructorWithCause) return wrapErrorConstructorWithCause;
	hasRequiredWrapErrorConstructorWithCause = 1;
	var getBuiltIn = requireGetBuiltIn();
	var hasOwn = requireHasOwnProperty();
	var createNonEnumerableProperty = requireCreateNonEnumerableProperty();
	var isPrototypeOf = requireObjectIsPrototypeOf();
	var setPrototypeOf = requireObjectSetPrototypeOf();
	var copyConstructorProperties = requireCopyConstructorProperties();
	var proxyAccessor = requireProxyAccessor();
	var inheritIfRequired = requireInheritIfRequired();
	var normalizeStringArgument = requireNormalizeStringArgument();
	var installErrorCause = requireInstallErrorCause();
	var installErrorStack = requireErrorStackInstall();
	var DESCRIPTORS = requireDescriptors();
	var IS_PURE = requireIsPure();

	wrapErrorConstructorWithCause = function (FULL_NAME, wrapper, FORCED, IS_AGGREGATE_ERROR) {
	  var STACK_TRACE_LIMIT = 'stackTraceLimit';
	  var OPTIONS_POSITION = IS_AGGREGATE_ERROR ? 2 : 1;
	  var path = FULL_NAME.split('.');
	  var ERROR_NAME = path[path.length - 1];
	  var OriginalError = getBuiltIn.apply(null, path);

	  if (!OriginalError) return;

	  var OriginalErrorPrototype = OriginalError.prototype;

	  // V8 9.3- bug https://bugs.chromium.org/p/v8/issues/detail?id=12006
	  if (!IS_PURE && hasOwn(OriginalErrorPrototype, 'cause')) delete OriginalErrorPrototype.cause;

	  if (!FORCED) return OriginalError;

	  var BaseError = getBuiltIn('Error');

	  var WrappedError = wrapper(function (a, b) {
	    var message = normalizeStringArgument(IS_AGGREGATE_ERROR ? b : a, undefined);
	    var result = IS_AGGREGATE_ERROR ? new OriginalError(a) : new OriginalError();
	    if (message !== undefined) createNonEnumerableProperty(result, 'message', message);
	    installErrorStack(result, WrappedError, result.stack, 2);
	    if (this && isPrototypeOf(OriginalErrorPrototype, this)) inheritIfRequired(result, this, WrappedError);
	    if (arguments.length > OPTIONS_POSITION) installErrorCause(result, arguments[OPTIONS_POSITION]);
	    return result;
	  });

	  WrappedError.prototype = OriginalErrorPrototype;

	  if (ERROR_NAME !== 'Error') {
	    if (setPrototypeOf) setPrototypeOf(WrappedError, BaseError);
	    else copyConstructorProperties(WrappedError, BaseError, { name: true });
	  } else if (DESCRIPTORS && STACK_TRACE_LIMIT in OriginalError) {
	    proxyAccessor(WrappedError, OriginalError, STACK_TRACE_LIMIT);
	    proxyAccessor(WrappedError, OriginalError, 'prepareStackTrace');
	  }

	  copyConstructorProperties(WrappedError, OriginalError);

	  if (!IS_PURE) try {
	    // Safari 13- bug: WebAssembly errors does not have a proper `.name`
	    if (OriginalErrorPrototype.name !== ERROR_NAME) {
	      createNonEnumerableProperty(OriginalErrorPrototype, 'name', ERROR_NAME);
	    }
	    OriginalErrorPrototype.constructor = WrappedError;
	  } catch (error) { /* empty */ }

	  return WrappedError;
	};
	return wrapErrorConstructorWithCause;
}

var hasRequiredEs_error_cause;

function requireEs_error_cause () {
	if (hasRequiredEs_error_cause) return es_error_cause;
	hasRequiredEs_error_cause = 1;
	/* eslint-disable no-unused-vars -- required for functions `.length` */
	var $ = require_export();
	var global = requireGlobal();
	var apply = requireFunctionApply();
	var wrapErrorConstructorWithCause = requireWrapErrorConstructorWithCause();

	var WEB_ASSEMBLY = 'WebAssembly';
	var WebAssembly = global[WEB_ASSEMBLY];

	// eslint-disable-next-line es/no-error-cause -- feature detection
	var FORCED = new Error('e', { cause: 7 }).cause !== 7;

	var exportGlobalErrorCauseWrapper = function (ERROR_NAME, wrapper) {
	  var O = {};
	  O[ERROR_NAME] = wrapErrorConstructorWithCause(ERROR_NAME, wrapper, FORCED);
	  $({ global: true, constructor: true, arity: 1, forced: FORCED }, O);
	};

	var exportWebAssemblyErrorCauseWrapper = function (ERROR_NAME, wrapper) {
	  if (WebAssembly && WebAssembly[ERROR_NAME]) {
	    var O = {};
	    O[ERROR_NAME] = wrapErrorConstructorWithCause(WEB_ASSEMBLY + '.' + ERROR_NAME, wrapper, FORCED);
	    $({ target: WEB_ASSEMBLY, stat: true, constructor: true, arity: 1, forced: FORCED }, O);
	  }
	};

	// https://tc39.es/ecma262/#sec-nativeerror
	exportGlobalErrorCauseWrapper('Error', function (init) {
	  return function Error(message) { return apply(init, this, arguments); };
	});
	exportGlobalErrorCauseWrapper('EvalError', function (init) {
	  return function EvalError(message) { return apply(init, this, arguments); };
	});
	exportGlobalErrorCauseWrapper('RangeError', function (init) {
	  return function RangeError(message) { return apply(init, this, arguments); };
	});
	exportGlobalErrorCauseWrapper('ReferenceError', function (init) {
	  return function ReferenceError(message) { return apply(init, this, arguments); };
	});
	exportGlobalErrorCauseWrapper('SyntaxError', function (init) {
	  return function SyntaxError(message) { return apply(init, this, arguments); };
	});
	exportGlobalErrorCauseWrapper('TypeError', function (init) {
	  return function TypeError(message) { return apply(init, this, arguments); };
	});
	exportGlobalErrorCauseWrapper('URIError', function (init) {
	  return function URIError(message) { return apply(init, this, arguments); };
	});
	exportWebAssemblyErrorCauseWrapper('CompileError', function (init) {
	  return function CompileError(message) { return apply(init, this, arguments); };
	});
	exportWebAssemblyErrorCauseWrapper('LinkError', function (init) {
	  return function LinkError(message) { return apply(init, this, arguments); };
	});
	exportWebAssemblyErrorCauseWrapper('RuntimeError', function (init) {
	  return function RuntimeError(message) { return apply(init, this, arguments); };
	});
	return es_error_cause;
}

requireEs_error_cause();

const WHITES = {
  // for compatibility, the four-digit chromaticity-derived ones everyone else uses
  D50: [0.3457 / 0.3585, 1.00000, (1.0 - 0.3457 - 0.3585) / 0.3585],
  D65: [0.3127 / 0.3290, 1.00000, (1.0 - 0.3127 - 0.3290) / 0.3290]
};
function getWhite(name) {
  if (Array.isArray(name)) {
    return name;
  }
  return WHITES[name];
}

// Adapt XYZ from white point W1 to W2
function adapt$2(W1, W2, XYZ) {
  let options = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : {};
  W1 = getWhite(W1);
  W2 = getWhite(W2);
  if (!W1 || !W2) {
    throw new TypeError(`Missing white point to convert ${!W1 ? "from" : ""}${!W1 && !W2 ? "/" : ""}${!W2 ? "to" : ""}`);
  }
  if (W1 === W2) {
    // Same whitepoints, no conversion needed
    return XYZ;
  }
  let env = {
    W1,
    W2,
    XYZ,
    options
  };
  hooks.run("chromatic-adaptation-start", env);
  if (!env.M) {
    if (env.W1 === WHITES.D65 && env.W2 === WHITES.D50) {
      env.M = [[1.0479297925449969, 0.022946870601609652, -0.05019226628920524], [0.02962780877005599, 0.9904344267538799, -0.017073799063418826], [-0.009243040646204504, 0.015055191490298152, 0.7518742814281371]];
    } else if (env.W1 === WHITES.D50 && env.W2 === WHITES.D65) {
      env.M = [[0.955473421488075, -0.02309845494876471, 0.06325924320057072], [-0.0283697093338637, 1.0099953980813041, 0.021041441191917323], [0.012314014864481998, -0.020507649298898964, 1.330365926242124]];
    }
  }
  hooks.run("chromatic-adaptation-end", env);
  if (env.M) {
    return multiplyMatrices(env.M, env.XYZ);
  } else {
    throw new TypeError("Only Bradford CAT with white points D50 and D65 supported for now.");
  }
}

const noneTypes = new Set(["<number>", "<percentage>", "<angle>"]);

/**
 * Validates the coordinates of a color against a format's coord grammar and
 * maps the coordinates to the range or refRange of the coordinates.
 * @param {ColorSpace} space - Colorspace the coords are in
 * @param {object} format - the format object to validate against
 * @param {string} name - the name of the color function. e.g. "oklab" or "color"
 * @returns {object[]} - an array of type metadata for each coordinate
 */
function coerceCoords(space, format, name, coords) {
  let types = Object.entries(space.coords).map((_ref, i) => {
    let [id, coordMeta] = _ref;
    let coordGrammar = format.coordGrammar[i];
    let arg = coords[i];
    let providedType = arg === null || arg === void 0 ? void 0 : arg.type;

    // Find grammar alternative that matches the provided type
    // Non-strict equals is intentional because we are comparing w/ string objects
    let type;
    if (arg.none) {
      type = coordGrammar.find(c => noneTypes.has(c));
    } else {
      type = coordGrammar.find(c => c == providedType);
    }

    // Check that each coord conforms to its grammar
    if (!type) {
      // Type does not exist in the grammar, throw
      let coordName = coordMeta.name || id;
      throw new TypeError(`${providedType !== null && providedType !== void 0 ? providedType : arg.raw} not allowed for ${coordName} in ${name}()`);
    }
    let fromRange = type.range;
    if (providedType === "<percentage>") {
      fromRange || (fromRange = [0, 1]);
    }
    let toRange = coordMeta.range || coordMeta.refRange;
    if (fromRange && toRange) {
      coords[i] = mapRange(fromRange, toRange, coords[i]);
    }
    return type;
  });
  return types;
}

/**
 * Convert a CSS Color string to a color object
 * @param {string} str
 * @param {object} [options]
 * @param {object} [options.meta] - Object for additional information about the parsing
 * @returns {Color}
 */
function parse(str) {
  var _String;
  let {
    meta
  } = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : {};
  let env = {
    "str": (_String = String(str)) === null || _String === void 0 ? void 0 : _String.trim()
  };
  hooks.run("parse-start", env);
  if (env.color) {
    return env.color;
  }
  env.parsed = parseFunction(env.str);
  if (env.parsed) {
    // Is a functional syntax
    let name = env.parsed.name;
    if (name === "color") {
      // color() function
      let id = env.parsed.args.shift();
      // Check against both <dashed-ident> and <ident> versions
      let alternateId = id.startsWith("--") ? id.substring(2) : `--${id}`;
      let ids = [id, alternateId];
      let alpha = env.parsed.rawArgs.indexOf("/") > 0 ? env.parsed.args.pop() : 1;
      for (let space of ColorSpace.all) {
        let colorSpec = space.getFormat("color");
        if (colorSpec) {
          var _colorSpec$ids;
          if (ids.includes(colorSpec.id) || (_colorSpec$ids = colorSpec.ids) !== null && _colorSpec$ids !== void 0 && _colorSpec$ids.filter(specId => ids.includes(specId)).length) {
            // From https://drafts.csswg.org/css-color-4/#color-function
            // If more <number>s or <percentage>s are provided than parameters that the colorspace takes, the excess <number>s at the end are ignored.
            // If less <number>s or <percentage>s are provided than parameters that the colorspace takes, the missing parameters default to 0. (This is particularly convenient for multichannel printers where the additional inks are spot colors or varnishes that most colors on the page won’t use.)
            const coords = Object.keys(space.coords).map((_, i) => env.parsed.args[i] || 0);
            let types;
            if (colorSpec.coordGrammar) {
              types = coerceCoords(space, colorSpec, "color", coords);
            }
            if (meta) {
              Object.assign(meta, {
                formatId: "color",
                types
              });
            }
            if (colorSpec.id.startsWith("--") && !id.startsWith("--")) {
              defaults.warn(`${space.name} is a non-standard space and not currently supported in the CSS spec. ` + `Use prefixed color(${colorSpec.id}) instead of color(${id}).`);
            }
            if (id.startsWith("--") && !colorSpec.id.startsWith("--")) {
              defaults.warn(`${space.name} is a standard space and supported in the CSS spec. ` + `Use color(${colorSpec.id}) instead of prefixed color(${id}).`);
            }
            return {
              spaceId: space.id,
              coords,
              alpha
            };
          }
        }
      }

      // Not found
      let didYouMean = "";
      let registryId = id in ColorSpace.registry ? id : alternateId;
      if (registryId in ColorSpace.registry) {
        var _ColorSpace$registry$;
        // Used color space id instead of color() id, these are often different
        let cssId = (_ColorSpace$registry$ = ColorSpace.registry[registryId].formats) === null || _ColorSpace$registry$ === void 0 || (_ColorSpace$registry$ = _ColorSpace$registry$.color) === null || _ColorSpace$registry$ === void 0 ? void 0 : _ColorSpace$registry$.id;
        if (cssId) {
          didYouMean = `Did you mean color(${cssId})?`;
        }
      }
      throw new TypeError(`Cannot parse color(${id}). ` + (didYouMean || "Missing a plugin?"));
    } else {
      for (let space of ColorSpace.all) {
        // color space specific function
        let format = space.getFormat(name);
        if (format && format.type === "function") {
          let alpha = 1;
          if (format.lastAlpha || last(env.parsed.args).alpha) {
            alpha = env.parsed.args.pop();
          }
          let coords = env.parsed.args;
          let types;
          if (format.coordGrammar) {
            types = coerceCoords(space, format, name, coords);
          }
          if (meta) {
            Object.assign(meta, {
              formatId: format.name,
              types
            });
          }
          return {
            spaceId: space.id,
            coords,
            alpha
          };
        }
      }
    }
  } else {
    // Custom, colorspace-specific format
    for (let space of ColorSpace.all) {
      for (let formatId in space.formats) {
        let format = space.formats[formatId];
        if (format.type !== "custom") {
          continue;
        }
        if (format.test && !format.test(env.str)) {
          continue;
        }
        let color = format.parse(env.str);
        if (color) {
          var _color$alpha;
          (_color$alpha = color.alpha) !== null && _color$alpha !== void 0 ? _color$alpha : color.alpha = 1;
          if (meta) {
            meta.formatId = formatId;
          }
          return color;
        }
      }
    }
  }

  // If we're here, we couldn't parse
  throw new TypeError(`Could not parse ${str} as a color. Missing a plugin?`);
}

/**
 * Resolves a color reference (object or string) to a plain color object
 * @param {Color | {space, coords, alpha} | string | Array<Color | {space, coords, alpha} | string> } color
 * @returns {{space, coords, alpha} | Array<{space, coords, alpha}}>
 */
function getColor(color) {
  if (Array.isArray(color)) {
    return color.map(getColor);
  }
  if (!color) {
    throw new TypeError("Empty color reference");
  }
  if (isString(color)) {
    color = parse(color);
  }

  // Object fixup
  let space = color.space || color.spaceId;
  if (!(space instanceof ColorSpace)) {
    // Convert string id to color space object
    color.space = ColorSpace.get(space);
  }
  if (color.alpha === undefined) {
    color.alpha = 1;
  }
  return color;
}

const ε$7 = .000075;

/**
 * Class to represent a color space
 */
class ColorSpace {
  constructor(options) {
    var _options$coords, _ref, _options$white, _options$formats, _this$formats$color;
    this.id = options.id;
    this.name = options.name;
    this.base = options.base ? ColorSpace.get(options.base) : null;
    this.aliases = options.aliases;
    if (this.base) {
      this.fromBase = options.fromBase;
      this.toBase = options.toBase;
    }

    // Coordinate metadata

    let coords = (_options$coords = options.coords) !== null && _options$coords !== void 0 ? _options$coords : this.base.coords;
    for (let name in coords) {
      if (!("name" in coords[name])) {
        coords[name].name = name;
      }
    }
    this.coords = coords;

    // White point

    let white = (_ref = (_options$white = options.white) !== null && _options$white !== void 0 ? _options$white : this.base.white) !== null && _ref !== void 0 ? _ref : "D65";
    this.white = getWhite(white);

    // Sort out formats

    this.formats = (_options$formats = options.formats) !== null && _options$formats !== void 0 ? _options$formats : {};
    for (let name in this.formats) {
      let format = this.formats[name];
      format.type || (format.type = "function");
      format.name || (format.name = name);
    }
    if (!((_this$formats$color = this.formats.color) !== null && _this$formats$color !== void 0 && _this$formats$color.id)) {
      var _this$formats$color2;
      this.formats.color = {
        ...((_this$formats$color2 = this.formats.color) !== null && _this$formats$color2 !== void 0 ? _this$formats$color2 : {}),
        id: options.cssId || this.id
      };
    }

    // Gamut space

    if (options.gamutSpace) {
      // Gamut space explicitly specified
      this.gamutSpace = options.gamutSpace === "self" ? this : ColorSpace.get(options.gamutSpace);
    } else {
      // No gamut space specified, calculate a sensible default
      if (this.isPolar) {
        // Do not check gamut through polar coordinates
        this.gamutSpace = this.base;
      } else {
        this.gamutSpace = this;
      }
    }

    // Optimize inGamut for unbounded spaces
    if (this.gamutSpace.isUnbounded) {
      this.inGamut = (coords, options) => {
        return true;
      };
    }

    // Other stuff
    this.referred = options.referred;

    // Compute ancestors and store them, since they will never change
    Object.defineProperty(this, "path", {
      value: getPath(this).reverse(),
      writable: false,
      enumerable: true,
      configurable: true
    });
    hooks.run("colorspace-init-end", this);
  }
  inGamut(coords) {
    let {
      epsilon = ε$7
    } = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : {};
    if (!this.equals(this.gamutSpace)) {
      coords = this.to(this.gamutSpace, coords);
      return this.gamutSpace.inGamut(coords, {
        epsilon
      });
    }
    let coordMeta = Object.values(this.coords);
    return coords.every((c, i) => {
      let meta = coordMeta[i];
      if (meta.type !== "angle" && meta.range) {
        if (Number.isNaN(c)) {
          // NaN is always in gamut
          return true;
        }
        let [min, max] = meta.range;
        return (min === undefined || c >= min - epsilon) && (max === undefined || c <= max + epsilon);
      }
      return true;
    });
  }
  get isUnbounded() {
    return Object.values(this.coords).every(coord => !("range" in coord));
  }
  get cssId() {
    var _this$formats;
    return ((_this$formats = this.formats) === null || _this$formats === void 0 || (_this$formats = _this$formats.color) === null || _this$formats === void 0 ? void 0 : _this$formats.id) || this.id;
  }
  get isPolar() {
    for (let id in this.coords) {
      if (this.coords[id].type === "angle") {
        return true;
      }
    }
    return false;
  }
  getFormat(format) {
    if (typeof format === "object") {
      format = processFormat(format, this);
      return format;
    }
    let ret;
    if (format === "default") {
      // Get first format
      ret = Object.values(this.formats)[0];
    } else {
      ret = this.formats[format];
    }
    if (ret) {
      ret = processFormat(ret, this);
      return ret;
    }
    return null;
  }

  /**
   * Check if this color space is the same as another color space reference.
   * Allows proxying color space objects and comparing color spaces with ids.
   * @param {string | ColorSpace} space ColorSpace object or id to compare to
   * @returns {boolean}
   */
  equals(space) {
    if (!space) {
      return false;
    }
    return this === space || this.id === space || this.id === space.id;
  }
  to(space, coords) {
    if (arguments.length === 1) {
      const color = getColor(space);
      [space, coords] = [color.space, color.coords];
    }
    space = ColorSpace.get(space);
    if (this.equals(space)) {
      // Same space, no change needed
      return coords;
    }

    // Convert NaN to 0, which seems to be valid in every coordinate of every color space
    coords = coords.map(c => Number.isNaN(c) ? 0 : c);

    // Find connection space = lowest common ancestor in the base tree
    let myPath = this.path;
    let otherPath = space.path;
    let connectionSpace, connectionSpaceIndex;
    for (let i = 0; i < myPath.length; i++) {
      if (myPath[i].equals(otherPath[i])) {
        connectionSpace = myPath[i];
        connectionSpaceIndex = i;
      } else {
        break;
      }
    }
    if (!connectionSpace) {
      // This should never happen
      throw new Error(`Cannot convert between color spaces ${this} and ${space}: no connection space was found`);
    }

    // Go up from current space to connection space
    for (let i = myPath.length - 1; i > connectionSpaceIndex; i--) {
      coords = myPath[i].toBase(coords);
    }

    // Go down from connection space to target space
    for (let i = connectionSpaceIndex + 1; i < otherPath.length; i++) {
      coords = otherPath[i].fromBase(coords);
    }
    return coords;
  }
  from(space, coords) {
    if (arguments.length === 1) {
      const color = getColor(space);
      [space, coords] = [color.space, color.coords];
    }
    space = ColorSpace.get(space);
    return space.to(this, coords);
  }
  toString() {
    return `${this.name} (${this.id})`;
  }
  getMinCoords() {
    let ret = [];
    for (let id in this.coords) {
      var _range$min;
      let meta = this.coords[id];
      let range = meta.range || meta.refRange;
      ret.push((_range$min = range === null || range === void 0 ? void 0 : range.min) !== null && _range$min !== void 0 ? _range$min : 0);
    }
    return ret;
  }
  static registry = {};

  // Returns array of unique color spaces
  static get all() {
    return [...new Set(Object.values(ColorSpace.registry))];
  }
  static register(id, space) {
    if (arguments.length === 1) {
      space = arguments[0];
      id = space.id;
    }
    space = this.get(space);
    if (this.registry[id] && this.registry[id] !== space) {
      throw new Error(`Duplicate color space registration: '${id}'`);
    }
    this.registry[id] = space;

    // Register aliases when called without an explicit ID.
    if (arguments.length === 1 && space.aliases) {
      for (let alias of space.aliases) {
        this.register(alias, space);
      }
    }
    return space;
  }

  /**
   * Lookup ColorSpace object by name
   * @param {ColorSpace | string} name
   */
  static get(space) {
    if (!space || space instanceof ColorSpace) {
      return space;
    }
    let argType = type(space);
    if (argType === "string") {
      // It's a color space id
      let ret = ColorSpace.registry[space.toLowerCase()];
      if (!ret) {
        throw new TypeError(`No color space found with id = "${space}"`);
      }
      return ret;
    }
    for (var _len = arguments.length, alternatives = new Array(_len > 1 ? _len - 1 : 0), _key = 1; _key < _len; _key++) {
      alternatives[_key - 1] = arguments[_key];
    }
    if (alternatives.length) {
      return ColorSpace.get(...alternatives);
    }
    throw new TypeError(`${space} is not a valid color space`);
  }

  /**
   * Get metadata about a coordinate of a color space
   *
   * @static
   * @param {Array | string} ref
   * @param {ColorSpace | string} [workingSpace]
   * @return {Object}
   */
  static resolveCoord(ref, workingSpace) {
    let coordType = type(ref);
    let space, coord;
    if (coordType === "string") {
      if (ref.includes(".")) {
        // Absolute coordinate
        [space, coord] = ref.split(".");
      } else {
        // Relative coordinate
        [space, coord] = [, ref];
      }
    } else if (Array.isArray(ref)) {
      [space, coord] = ref;
    } else {
      // Object
      space = ref.space;
      coord = ref.coordId;
    }
    space = ColorSpace.get(space);
    if (!space) {
      space = workingSpace;
    }
    if (!space) {
      throw new TypeError(`Cannot resolve coordinate reference ${ref}: No color space specified and relative references are not allowed here`);
    }
    coordType = type(coord);
    if (coordType === "number" || coordType === "string" && coord >= 0) {
      // Resolve numerical coord
      let meta = Object.entries(space.coords)[coord];
      if (meta) {
        return {
          space,
          id: meta[0],
          index: coord,
          ...meta[1]
        };
      }
    }
    space = ColorSpace.get(space);
    let normalizedCoord = coord.toLowerCase();
    let i = 0;
    for (let id in space.coords) {
      var _meta$name;
      let meta = space.coords[id];
      if (id.toLowerCase() === normalizedCoord || ((_meta$name = meta.name) === null || _meta$name === void 0 ? void 0 : _meta$name.toLowerCase()) === normalizedCoord) {
        return {
          space,
          id,
          index: i,
          ...meta
        };
      }
      i++;
    }
    throw new TypeError(`No "${coord}" coordinate found in ${space.name}. Its coordinates are: ${Object.keys(space.coords).join(", ")}`);
  }
  static DEFAULT_FORMAT = {
    type: "functions",
    name: "color"
  };
}
function getPath(space) {
  let ret = [space];
  for (let s = space; s = s.base;) {
    ret.push(s);
  }
  return ret;
}
function processFormat(format) {
  let {
    coords
  } = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : {};
  if (format.coords && !format.coordGrammar) {
    format.type || (format.type = "function");
    format.name || (format.name = "color");

    // Format has not been processed
    format.coordGrammar = parseCoordGrammar(format.coords);
    let coordFormats = Object.entries(coords).map((_ref2, i) => {
      let [id, coordMeta] = _ref2;
      // Preferred format for each coord is the first one
      let outputType = format.coordGrammar[i][0];
      let fromRange = coordMeta.range || coordMeta.refRange;
      let toRange = outputType.range,
        suffix = "";

      // Non-strict equals intentional since outputType could be a string object
      if (outputType == "<percentage>") {
        toRange = [0, 100];
        suffix = "%";
      } else if (outputType == "<angle>") {
        suffix = "deg";
      }
      return {
        fromRange,
        toRange,
        suffix
      };
    });
    format.serializeCoords = (coords, precision) => {
      return coords.map((c, i) => {
        let {
          fromRange,
          toRange,
          suffix
        } = coordFormats[i];
        if (fromRange && toRange) {
          c = mapRange(fromRange, toRange, c);
        }
        c = serializeNumber(c, {
          precision,
          unit: suffix
        });
        return c;
      });
    };
  }
  return format;
}

var xyz_d65 = new ColorSpace({
  id: "xyz-d65",
  name: "XYZ D65",
  coords: {
    x: {
      name: "X"
    },
    y: {
      name: "Y"
    },
    z: {
      name: "Z"
    }
  },
  white: "D65",
  formats: {
    color: {
      ids: ["xyz-d65", "xyz"]
    }
  },
  aliases: ["xyz"]
});

/**
 * Convenience class for RGB color spaces
 * @extends {ColorSpace}
 */
class RGBColorSpace extends ColorSpace {
  /**
   * Creates a new RGB ColorSpace.
   * If coords are not specified, they will use the default RGB coords.
   * Instead of `fromBase()` and `toBase()` functions,
   * you can specify to/from XYZ matrices and have `toBase()` and `fromBase()` automatically generated.
   * @param {*} options - Same options as {@link ColorSpace} plus:
   * @param {number[][]} options.toXYZ_M - Matrix to convert to XYZ
   * @param {number[][]} options.fromXYZ_M - Matrix to convert from XYZ
   */
  constructor(options) {
    var _options$referred;
    if (!options.coords) {
      options.coords = {
        r: {
          range: [0, 1],
          name: "Red"
        },
        g: {
          range: [0, 1],
          name: "Green"
        },
        b: {
          range: [0, 1],
          name: "Blue"
        }
      };
    }
    if (!options.base) {
      options.base = xyz_d65;
    }
    if (options.toXYZ_M && options.fromXYZ_M) {
      var _options$toBase, _options$fromBase;
      (_options$toBase = options.toBase) !== null && _options$toBase !== void 0 ? _options$toBase : options.toBase = rgb => {
        let xyz = multiplyMatrices(options.toXYZ_M, rgb);
        if (this.white !== this.base.white) {
          // Perform chromatic adaptation
          xyz = adapt$2(this.white, this.base.white, xyz);
        }
        return xyz;
      };
      (_options$fromBase = options.fromBase) !== null && _options$fromBase !== void 0 ? _options$fromBase : options.fromBase = xyz => {
        xyz = adapt$2(this.base.white, this.white, xyz);
        return multiplyMatrices(options.fromXYZ_M, xyz);
      };
    }
    (_options$referred = options.referred) !== null && _options$referred !== void 0 ? _options$referred : options.referred = "display";
    super(options);
  }
}

/**
 * Get the coordinates of a color in any color space
 * @param {Color} color
 * @param {string | ColorSpace} [space = color.space] The color space to convert to. Defaults to the color's current space
 * @returns {number[]} The color coordinates in the given color space
 */
function getAll(color, space) {
  color = getColor(color);
  if (!space || color.space.equals(space)) {
    // No conversion needed
    return color.coords.slice();
  }
  space = ColorSpace.get(space);
  return space.from(color);
}

function get(color, prop) {
  color = getColor(color);
  let {
    space,
    index
  } = ColorSpace.resolveCoord(prop, color.space);
  let coords = getAll(color, space);
  return coords[index];
}

function setAll(color, space, coords) {
  color = getColor(color);
  space = ColorSpace.get(space);
  color.coords = space.to(color.space, coords);
  return color;
}
setAll.returns = "color";

// Set properties and return current instance
function set(color, prop, value) {
  color = getColor(color);
  if (arguments.length === 2 && type(arguments[1]) === "object") {
    // Argument is an object literal
    let object = arguments[1];
    for (let p in object) {
      set(color, p, object[p]);
    }
  } else {
    if (typeof value === "function") {
      value = value(get(color, prop));
    }
    let {
      space,
      index
    } = ColorSpace.resolveCoord(prop, color.space);
    let coords = getAll(color, space);
    coords[index] = value;
    setAll(color, space, coords);
  }
  return color;
}
set.returns = "color";

var XYZ_D50 = new ColorSpace({
  id: "xyz-d50",
  name: "XYZ D50",
  white: "D50",
  base: xyz_d65,
  fromBase: coords => adapt$2(xyz_d65.white, "D50", coords),
  toBase: coords => adapt$2("D50", xyz_d65.white, coords)
});

// κ * ε  = 2^3 = 8
const ε$6 = 216 / 24389; // 6^3/29^3 == (24/116)^3
const ε3$1 = 24 / 116;
const κ$4 = 24389 / 27; // 29^3/3^3

let white$4 = WHITES.D50;
var lab = new ColorSpace({
  id: "lab",
  name: "Lab",
  coords: {
    l: {
      refRange: [0, 100],
      name: "Lightness"
    },
    a: {
      refRange: [-125, 125]
    },
    b: {
      refRange: [-125, 125]
    }
  },
  // Assuming XYZ is relative to D50, convert to CIE Lab
  // from CIE standard, which now defines these as a rational fraction
  white: white$4,
  base: XYZ_D50,
  // Convert D50-adapted XYX to Lab
  //  CIE 15.3:2004 section 8.2.1.1
  fromBase(XYZ) {
    // compute xyz, which is XYZ scaled relative to reference white
    let xyz = XYZ.map((value, i) => value / white$4[i]);

    // now compute f
    let f = xyz.map(value => value > ε$6 ? Math.cbrt(value) : (κ$4 * value + 16) / 116);
    return [116 * f[1] - 16,
    // L
    500 * (f[0] - f[1]),
    // a
    200 * (f[1] - f[2]) // b
    ];
  },
  // Convert Lab to D50-adapted XYZ
  // Same result as CIE 15.3:2004 Appendix D although the derivation is different
  // http://www.brucelindbloom.com/index.html?Eqn_RGB_XYZ_Matrix.html
  toBase(Lab) {
    // compute f, starting with the luminance-related term
    let f = [];
    f[1] = (Lab[0] + 16) / 116;
    f[0] = Lab[1] / 500 + f[1];
    f[2] = f[1] - Lab[2] / 200;

    // compute xyz
    let xyz = [f[0] > ε3$1 ? Math.pow(f[0], 3) : (116 * f[0] - 16) / κ$4, Lab[0] > 8 ? Math.pow((Lab[0] + 16) / 116, 3) : Lab[0] / κ$4, f[2] > ε3$1 ? Math.pow(f[2], 3) : (116 * f[2] - 16) / κ$4];

    // Compute XYZ by scaling xyz by reference white
    return xyz.map((value, i) => value * white$4[i]);
  },
  formats: {
    "lab": {
      coords: ["<number> | <percentage>", "<number> | <percentage>[-1,1]", "<number> | <percentage>[-1,1]"]
    }
  }
});

function constrain(angle) {
  return (angle % 360 + 360) % 360;
}
function adjust(arc, angles) {
  if (arc === "raw") {
    return angles;
  }
  let [a1, a2] = angles.map(constrain);
  let angleDiff = a2 - a1;
  if (arc === "increasing") {
    if (angleDiff < 0) {
      a2 += 360;
    }
  } else if (arc === "decreasing") {
    if (angleDiff > 0) {
      a1 += 360;
    }
  } else if (arc === "longer") {
    if (-180 < angleDiff && angleDiff < 180) {
      if (angleDiff > 0) {
        a1 += 360;
      } else {
        a2 += 360;
      }
    }
  } else if (arc === "shorter") {
    if (angleDiff > 180) {
      a1 += 360;
    } else if (angleDiff < -180) {
      a2 += 360;
    }
  }
  return [a1, a2];
}

var lch = new ColorSpace({
  id: "lch",
  name: "LCH",
  coords: {
    l: {
      refRange: [0, 100],
      name: "Lightness"
    },
    c: {
      refRange: [0, 150],
      name: "Chroma"
    },
    h: {
      refRange: [0, 360],
      type: "angle",
      name: "Hue"
    }
  },
  base: lab,
  fromBase(Lab) {
    // Convert to polar form
    let [L, a, b] = Lab;
    let hue;
    const ε = 0.02;
    if (Math.abs(a) < ε && Math.abs(b) < ε) {
      hue = NaN;
    } else {
      hue = Math.atan2(b, a) * 180 / Math.PI;
    }
    return [L,
    // L is still L
    Math.sqrt(a ** 2 + b ** 2),
    // Chroma
    constrain(hue) // Hue, in degrees [0 to 360)
    ];
  },
  toBase(LCH) {
    // Convert from polar form
    let [Lightness, Chroma, Hue] = LCH;
    // Clamp any negative Chroma
    if (Chroma < 0) {
      Chroma = 0;
    }
    // Deal with NaN Hue
    if (isNaN(Hue)) {
      Hue = 0;
    }
    return [Lightness,
    // L is still L
    Chroma * Math.cos(Hue * Math.PI / 180),
    // a
    Chroma * Math.sin(Hue * Math.PI / 180) // b
    ];
  },
  formats: {
    "lch": {
      coords: ["<number> | <percentage>", "<number> | <percentage>", "<number> | <angle>"]
    }
  }
});

// deltaE2000 is a statistically significant improvement
// and is recommended by the CIE and Idealliance
// especially for color differences less than 10 deltaE76
// but is wicked complicated
// and many implementations have small errors!
// DeltaE2000 is also discontinuous; in case this
// matters to you, use deltaECMC instead.

const Gfactor = 25 ** 7;
const π$1 = Math.PI;
const r2d = 180 / π$1;
const d2r$1 = π$1 / 180;
function pow7(x) {
  // Faster than x ** 7 or Math.pow(x, 7)

  const x2 = x * x;
  const x7 = x2 * x2 * x2 * x;
  return x7;
}
function deltaE2000 (color, sample) {
  let {
    kL = 1,
    kC = 1,
    kH = 1
  } = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};
  [color, sample] = getColor([color, sample]);

  // Given this color as the reference
  // and the function parameter as the sample,
  // calculate deltaE 2000.

  // This implementation assumes the parametric
  // weighting factors kL, kC and kH
  // for the influence of viewing conditions
  // are all 1, as sadly seems typical.
  // kL should be increased for lightness texture or noise
  // and kC increased for chroma noise

  let [L1, a1, b1] = lab.from(color);
  let C1 = lch.from(lab, [L1, a1, b1])[1];
  let [L2, a2, b2] = lab.from(sample);
  let C2 = lch.from(lab, [L2, a2, b2])[1];

  // Check for negative Chroma,
  // which might happen through
  // direct user input of LCH values

  if (C1 < 0) {
    C1 = 0;
  }
  if (C2 < 0) {
    C2 = 0;
  }
  let Cbar = (C1 + C2) / 2; // mean Chroma

  // calculate a-axis asymmetry factor from mean Chroma
  // this turns JND ellipses for near-neutral colors back into circles
  let C7 = pow7(Cbar);
  let G = 0.5 * (1 - Math.sqrt(C7 / (C7 + Gfactor)));

  // scale a axes by asymmetry factor
  // this by the way is why there is no Lab2000 colorspace
  let adash1 = (1 + G) * a1;
  let adash2 = (1 + G) * a2;

  // calculate new Chroma from scaled a and original b axes
  let Cdash1 = Math.sqrt(adash1 ** 2 + b1 ** 2);
  let Cdash2 = Math.sqrt(adash2 ** 2 + b2 ** 2);

  // calculate new hues, with zero hue for true neutrals
  // and in degrees, not radians

  let h1 = adash1 === 0 && b1 === 0 ? 0 : Math.atan2(b1, adash1);
  let h2 = adash2 === 0 && b2 === 0 ? 0 : Math.atan2(b2, adash2);
  if (h1 < 0) {
    h1 += 2 * π$1;
  }
  if (h2 < 0) {
    h2 += 2 * π$1;
  }
  h1 *= r2d;
  h2 *= r2d;

  // Lightness and Chroma differences; sign matters
  let ΔL = L2 - L1;
  let ΔC = Cdash2 - Cdash1;

  // Hue difference, getting the sign correct
  let hdiff = h2 - h1;
  let hsum = h1 + h2;
  let habs = Math.abs(hdiff);
  let Δh;
  if (Cdash1 * Cdash2 === 0) {
    Δh = 0;
  } else if (habs <= 180) {
    Δh = hdiff;
  } else if (hdiff > 180) {
    Δh = hdiff - 360;
  } else if (hdiff < -180) {
    Δh = hdiff + 360;
  } else {
    defaults.warn("the unthinkable has happened");
  }

  // weighted Hue difference, more for larger Chroma
  let ΔH = 2 * Math.sqrt(Cdash2 * Cdash1) * Math.sin(Δh * d2r$1 / 2);

  // calculate mean Lightness and Chroma
  let Ldash = (L1 + L2) / 2;
  let Cdash = (Cdash1 + Cdash2) / 2;
  let Cdash7 = pow7(Cdash);

  // Compensate for non-linearity in the blue region of Lab.
  // Four possibilities for hue weighting factor,
  // depending on the angles, to get the correct sign
  let hdash;
  if (Cdash1 * Cdash2 === 0) {
    hdash = hsum; // which should be zero
  } else if (habs <= 180) {
    hdash = hsum / 2;
  } else if (hsum < 360) {
    hdash = (hsum + 360) / 2;
  } else {
    hdash = (hsum - 360) / 2;
  }

  // positional corrections to the lack of uniformity of CIELAB
  // These are all trying to make JND ellipsoids more like spheres

  // SL Lightness crispening factor
  // a background with L=50 is assumed
  let lsq = (Ldash - 50) ** 2;
  let SL = 1 + 0.015 * lsq / Math.sqrt(20 + lsq);

  // SC Chroma factor, similar to those in CMC and deltaE 94 formulae
  let SC = 1 + 0.045 * Cdash;

  // Cross term T for blue non-linearity
  let T = 1;
  T -= 0.17 * Math.cos((hdash - 30) * d2r$1);
  T += 0.24 * Math.cos(2 * hdash * d2r$1);
  T += 0.32 * Math.cos((3 * hdash + 6) * d2r$1);
  T -= 0.20 * Math.cos((4 * hdash - 63) * d2r$1);

  // SH Hue factor depends on Chroma,
  // as well as adjusted hue angle like deltaE94.
  let SH = 1 + 0.015 * Cdash * T;

  // RT Hue rotation term compensates for rotation of JND ellipses
  // and Munsell constant hue lines
  // in the medium-high Chroma blue region
  // (Hue 225 to 315)
  let Δθ = 30 * Math.exp(-1 * ((hdash - 275) / 25) ** 2);
  let RC = 2 * Math.sqrt(Cdash7 / (Cdash7 + Gfactor));
  let RT = -1 * Math.sin(2 * Δθ * d2r$1) * RC;

  // Finally calculate the deltaE, term by term as root sume of squares
  let dE = (ΔL / (kL * SL)) ** 2;
  dE += (ΔC / (kC * SC)) ** 2;
  dE += (ΔH / (kH * SH)) ** 2;
  dE += RT * (ΔC / (kC * SC)) * (ΔH / (kH * SH));
  return Math.sqrt(dE);
  // Yay!!!
}

// Recalculated for consistent reference white
// see https://github.com/w3c/csswg-drafts/issues/6642#issuecomment-943521484
const XYZtoLMS_M$1 = [[0.8190224379967030, 0.3619062600528904, -0.1288737815209879], [0.0329836539323885, 0.9292868615863434, 0.0361446663506424], [0.0481771893596242, 0.2642395317527308, 0.6335478284694309]];
// inverse of XYZtoLMS_M
const LMStoXYZ_M$1 = [[1.2268798758459243, -0.5578149944602171, 0.2813910456659647], [-0.0405757452148008, 1.1122868032803170, -0.0717110580655164], [-0.0763729366746601, -0.4214933324022432, 1.5869240198367816]];
const LMStoLab_M = [[0.2104542683093140, 0.7936177747023054, -0.0040720430116193], [1.9779985324311684, -2.4285922420485799, 0.4505937096174110], [0.0259040424655478, 0.7827717124575296, -0.8086757549230774]];
// LMStoIab_M inverted
const LabtoLMS_M = [[1.0000000000000000, 0.3963377773761749, 0.2158037573099136], [1.0000000000000000, -0.1055613458156586, -0.0638541728258133], [1.0000000000000000, -0.0894841775298119, -1.2914855480194092]];
var OKLab = new ColorSpace({
  id: "oklab",
  name: "Oklab",
  coords: {
    l: {
      refRange: [0, 1],
      name: "Lightness"
    },
    a: {
      refRange: [-0.4, 0.4]
    },
    b: {
      refRange: [-0.4, 0.4]
    }
  },
  // Note that XYZ is relative to D65
  white: "D65",
  base: xyz_d65,
  fromBase(XYZ) {
    // move to LMS cone domain
    let LMS = multiplyMatrices(XYZtoLMS_M$1, XYZ);

    // non-linearity
    let LMSg = LMS.map(val => Math.cbrt(val));
    return multiplyMatrices(LMStoLab_M, LMSg);
  },
  toBase(OKLab) {
    // move to LMS cone domain
    let LMSg = multiplyMatrices(LabtoLMS_M, OKLab);

    // restore linearity
    let LMS = LMSg.map(val => val ** 3);
    return multiplyMatrices(LMStoXYZ_M$1, LMS);
  },
  formats: {
    "oklab": {
      coords: ["<percentage> | <number>", "<number> | <percentage>[-1,1]", "<number> | <percentage>[-1,1]"]
    }
  }
});

// More accurate color-difference formulae
// than the simple 1976 Euclidean distance in CIE Lab

function deltaEOK (color, sample) {
  [color, sample] = getColor([color, sample]);

  // Given this color as the reference
  // and a sample,
  // calculate deltaEOK, term by term as root sum of squares
  let [L1, a1, b1] = OKLab.from(color);
  let [L2, a2, b2] = OKLab.from(sample);
  let ΔL = L1 - L2;
  let Δa = a1 - a2;
  let Δb = b1 - b2;
  return Math.sqrt(ΔL ** 2 + Δa ** 2 + Δb ** 2);
}

const ε$5 = .000075;

/**
 * Check if a color is in gamut of either its own or another color space
 * @return {Boolean} Is the color in gamut?
 */
function inGamut(color, space) {
  let {
    epsilon = ε$5
  } = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};
  color = getColor(color);
  if (!space) {
    space = color.space;
  }
  space = ColorSpace.get(space);
  let coords = color.coords;
  if (space !== color.space) {
    coords = space.from(color);
  }
  return space.inGamut(coords, {
    epsilon
  });
}

function clone(color) {
  return {
    space: color.space,
    coords: color.coords.slice(),
    alpha: color.alpha
  };
}

/**
 * Euclidean distance of colors in an arbitrary color space
 */
function distance(color1, color2) {
  let space = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : "lab";
  space = ColorSpace.get(space);

  // Assume getColor() is called on color in space.from()
  let coords1 = space.from(color1);
  let coords2 = space.from(color2);
  return Math.sqrt(coords1.reduce((acc, c1, i) => {
    let c2 = coords2[i];
    if (isNaN(c1) || isNaN(c2)) {
      return acc;
    }
    return acc + (c2 - c1) ** 2;
  }, 0));
}

function deltaE76(color, sample) {
  // Assume getColor() is called in the distance function
  return distance(color, sample, "lab");
}

// More accurate color-difference formulae
// than the simple 1976 Euclidean distance in Lab

// CMC by the Color Measurement Committee of the
// Bradford Society of Dyeists and Colorsts, 1994.
// Uses LCH rather than Lab,
// with different weights for L, C and H differences
// A nice increase in accuracy for modest increase in complexity
const π = Math.PI;
const d2r = π / 180;
function deltaECMC (color, sample) {
  let {
    l = 2,
    c = 1
  } = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};
  [color, sample] = getColor([color, sample]);

  // Given this color as the reference
  // and a sample,
  // calculate deltaE CMC.

  // This implementation assumes the parametric
  // weighting factors l:c are 2:1
  // which is typical for non-textile uses.

  let [L1, a1, b1] = lab.from(color);
  let [, C1, H1] = lch.from(lab, [L1, a1, b1]);
  let [L2, a2, b2] = lab.from(sample);
  let C2 = lch.from(lab, [L2, a2, b2])[1];

  // let [L1, a1, b1] = color.getAll(lab);
  // let C1 = color.get("lch.c");
  // let H1 = color.get("lch.h");
  // let [L2, a2, b2] = sample.getAll(lab);
  // let C2 = sample.get("lch.c");

  // Check for negative Chroma,
  // which might happen through
  // direct user input of LCH values

  if (C1 < 0) {
    C1 = 0;
  }
  if (C2 < 0) {
    C2 = 0;
  }

  // we don't need H2 as ΔH is calculated from Δa, Δb and ΔC

  // Lightness and Chroma differences
  // These are (color - sample), unlike deltaE2000
  let ΔL = L1 - L2;
  let ΔC = C1 - C2;
  let Δa = a1 - a2;
  let Δb = b1 - b2;

  // weighted Hue difference, less for larger Chroma difference

  let H2 = Δa ** 2 + Δb ** 2 - ΔC ** 2;
  // due to roundoff error it is possible that, for zero a and b,
  // ΔC > Δa + Δb is 0, resulting in attempting
  // to take the square root of a negative number

  // trying instead the equation from Industrial Color Physics
  // By Georg A. Klein

  // let ΔH = ((a1 * b2) - (a2 * b1)) / Math.sqrt(0.5 * ((C2 * C1) + (a2 * a1) + (b2 * b1)));
  // console.log({ΔH});
  // This gives the same result to 12 decimal places
  // except it sometimes NaNs when trying to root a negative number

  // let ΔH = Math.sqrt(H2); we never actually use the root, it gets squared again!!

  // positional corrections to the lack of uniformity of CIELAB
  // These are all trying to make JND ellipsoids more like spheres

  // SL Lightness crispening factor, depends entirely on L1 not L2
  let SL = 0.511; // linear portion of the Y to L transfer function
  if (L1 >= 16) {
    // cubic portion
    SL = 0.040975 * L1 / (1 + 0.01765 * L1);
  }

  // SC Chroma factor
  let SC = 0.0638 * C1 / (1 + 0.0131 * C1) + 0.638;

  // Cross term T for blue non-linearity
  let T;
  if (Number.isNaN(H1)) {
    H1 = 0;
  }
  if (H1 >= 164 && H1 <= 345) {
    T = 0.56 + Math.abs(0.2 * Math.cos((H1 + 168) * d2r));
  } else {
    T = 0.36 + Math.abs(0.4 * Math.cos((H1 + 35) * d2r));
  }
  // console.log({T});

  // SH Hue factor also depends on C1,
  let C4 = Math.pow(C1, 4);
  let F = Math.sqrt(C4 / (C4 + 1900));
  let SH = SC * (F * T + 1 - F);

  // Finally calculate the deltaE, term by term as root sume of squares
  let dE = (ΔL / (l * SL)) ** 2;
  dE += (ΔC / (c * SC)) ** 2;
  dE += H2 / SH ** 2;
  // dE += (ΔH / SH)  ** 2;
  return Math.sqrt(dE);
  // Yay!!!
}

const Yw$1 = 203; // absolute luminance of media white

var XYZ_Abs_D65 = new ColorSpace({
  // Absolute CIE XYZ, with a D65 whitepoint,
  // as used in most HDR colorspaces as a starting point.
  // SDR spaces are converted per BT.2048
  // so that diffuse, media white is 203 cd/m²
  id: "xyz-abs-d65",
  cssId: "--xyz-abs-d65",
  name: "Absolute XYZ D65",
  coords: {
    x: {
      refRange: [0, 9504.7],
      name: "Xa"
    },
    y: {
      refRange: [0, 10000],
      name: "Ya"
    },
    z: {
      refRange: [0, 10888.3],
      name: "Za"
    }
  },
  base: xyz_d65,
  fromBase(XYZ) {
    // Make XYZ absolute, not relative to media white
    // Maximum luminance in PQ is 10,000 cd/m²
    // Relative XYZ has Y=1 for media white
    return XYZ.map(v => Math.max(v * Yw$1, 0));
  },
  toBase(AbsXYZ) {
    // Convert to media-white relative XYZ
    return AbsXYZ.map(v => Math.max(v / Yw$1, 0));
  }
});

const b$1 = 1.15;
const g = 0.66;
const n$1 = 2610 / 2 ** 14;
const ninv$1 = 2 ** 14 / 2610;
const c1$2 = 3424 / 2 ** 12;
const c2$2 = 2413 / 2 ** 7;
const c3$2 = 2392 / 2 ** 7;
const p = 1.7 * 2523 / 2 ** 5;
const pinv = 2 ** 5 / (1.7 * 2523);
const d = -0.56;
const d0 = 1.6295499532821566E-11;
const XYZtoCone_M = [[0.41478972, 0.579999, 0.0146480], [-0.2015100, 1.120649, 0.0531008], [-0.0166008, 0.264800, 0.6684799]];
// XYZtoCone_M inverted
const ConetoXYZ_M = [[1.9242264357876067, -1.0047923125953657, 0.037651404030618], [0.35031676209499907, 0.7264811939316552, -0.06538442294808501], [-0.09098281098284752, -0.3127282905230739, 1.5227665613052603]];
const ConetoIab_M = [[0.5, 0.5, 0], [3.524000, -4.066708, 0.542708], [0.199076, 1.096799, -1.295875]];
// ConetoIab_M inverted
const IabtoCone_M = [[1, 0.1386050432715393, 0.05804731615611886], [0.9999999999999999, -0.1386050432715393, -0.05804731615611886], [0.9999999999999998, -0.09601924202631895, -0.8118918960560388]];
var Jzazbz = new ColorSpace({
  id: "jzazbz",
  name: "Jzazbz",
  coords: {
    jz: {
      refRange: [0, 1],
      name: "Jz"
    },
    az: {
      refRange: [-0.5, 0.5]
    },
    bz: {
      refRange: [-0.5, 0.5]
    }
  },
  base: XYZ_Abs_D65,
  fromBase(XYZ) {
    // First make XYZ absolute, not relative to media white
    // Maximum luminance in PQ is 10,000 cd/m²
    // Relative XYZ has Y=1 for media white
    // BT.2048 says media white Y=203 at PQ 58

    let [Xa, Ya, Za] = XYZ;

    // modify X and Y
    let Xm = b$1 * Xa - (b$1 - 1) * Za;
    let Ym = g * Ya - (g - 1) * Xa;

    // move to LMS cone domain
    let LMS = multiplyMatrices(XYZtoCone_M, [Xm, Ym, Za]);

    // PQ-encode LMS
    let PQLMS = LMS.map(function (val) {
      let num = c1$2 + c2$2 * (val / 10000) ** n$1;
      let denom = 1 + c3$2 * (val / 10000) ** n$1;
      return (num / denom) ** p;
    });

    // almost there, calculate Iz az bz
    let [Iz, az, bz] = multiplyMatrices(ConetoIab_M, PQLMS);
    // console.log({Iz, az, bz});

    let Jz = (1 + d) * Iz / (1 + d * Iz) - d0;
    return [Jz, az, bz];
  },
  toBase(Jzazbz) {
    let [Jz, az, bz] = Jzazbz;
    let Iz = (Jz + d0) / (1 + d - d * (Jz + d0));

    // bring into LMS cone domain
    let PQLMS = multiplyMatrices(IabtoCone_M, [Iz, az, bz]);

    // convert from PQ-coded to linear-light
    let LMS = PQLMS.map(function (val) {
      let num = c1$2 - val ** pinv;
      let denom = c3$2 * val ** pinv - c2$2;
      let x = 10000 * (num / denom) ** ninv$1;
      return x; // luminance relative to diffuse white, [0, 70 or so].
    });

    // modified abs XYZ
    let [Xm, Ym, Za] = multiplyMatrices(ConetoXYZ_M, LMS);

    // restore standard D50 relative XYZ, relative to media white
    let Xa = (Xm + (b$1 - 1) * Za) / b$1;
    let Ya = (Ym + (g - 1) * Xa) / g;
    return [Xa, Ya, Za];
  },
  formats: {
    // https://drafts.csswg.org/css-color-hdr/#Jzazbz
    "color": {
      coords: ["<number> | <percentage>", "<number> | <percentage>[-1,1]", "<number> | <percentage>[-1,1]"]
    }
  }
});

var jzczhz = new ColorSpace({
  id: "jzczhz",
  name: "JzCzHz",
  coords: {
    jz: {
      refRange: [0, 1],
      name: "Jz"
    },
    cz: {
      refRange: [0, 1],
      name: "Chroma"
    },
    hz: {
      refRange: [0, 360],
      type: "angle",
      name: "Hue"
    }
  },
  base: Jzazbz,
  fromBase(jzazbz) {
    // Convert to polar form
    let [Jz, az, bz] = jzazbz;
    let hue;
    const ε = 0.0002; // chromatic components much smaller than a,b

    if (Math.abs(az) < ε && Math.abs(bz) < ε) {
      hue = NaN;
    } else {
      hue = Math.atan2(bz, az) * 180 / Math.PI;
    }
    return [Jz,
    // Jz is still Jz
    Math.sqrt(az ** 2 + bz ** 2),
    // Chroma
    constrain(hue) // Hue, in degrees [0 to 360)
    ];
  },
  toBase(jzczhz) {
    // Convert from polar form
    // debugger;
    return [jzczhz[0],
    // Jz is still Jz
    jzczhz[1] * Math.cos(jzczhz[2] * Math.PI / 180),
    // az
    jzczhz[1] * Math.sin(jzczhz[2] * Math.PI / 180) // bz
    ];
  }
});

// More accurate color-difference formulae
// than the simple 1976 Euclidean distance in Lab

// Uses JzCzHz, which has improved perceptual uniformity
// and thus a simple Euclidean root-sum of ΔL² ΔC² ΔH²
// gives good results.

function deltaEJz (color, sample) {
  [color, sample] = getColor([color, sample]);

  // Given this color as the reference
  // and a sample,
  // calculate deltaE in JzCzHz.
  let [Jz1, Cz1, Hz1] = jzczhz.from(color);
  let [Jz2, Cz2, Hz2] = jzczhz.from(sample);

  // Lightness and Chroma differences
  // sign does not matter as they are squared.
  let ΔJ = Jz1 - Jz2;
  let ΔC = Cz1 - Cz2;

  // length of chord for ΔH
  if (Number.isNaN(Hz1) && Number.isNaN(Hz2)) {
    // both undefined hues
    Hz1 = 0;
    Hz2 = 0;
  } else if (Number.isNaN(Hz1)) {
    // one undefined, set to the defined hue
    Hz1 = Hz2;
  } else if (Number.isNaN(Hz2)) {
    Hz2 = Hz1;
  }
  let Δh = Hz1 - Hz2;
  let ΔH = 2 * Math.sqrt(Cz1 * Cz2) * Math.sin(Δh / 2 * (Math.PI / 180));
  return Math.sqrt(ΔJ ** 2 + ΔC ** 2 + ΔH ** 2);
}

const c1$1 = 3424 / 4096;
const c2$1 = 2413 / 128;
const c3$1 = 2392 / 128;
const m1$1 = 2610 / 16384;
const m2 = 2523 / 32;
const im1 = 16384 / 2610;
const im2 = 32 / 2523;

// The matrix below includes the 4% crosstalk components
// and is from the Dolby "What is ICtCp" paper"
const XYZtoLMS_M = [[0.3592832590121217, 0.6976051147779502, -0.0358915932320290], [-0.1920808463704993, 1.1004767970374321, 0.0753748658519118], [0.0070797844607479, 0.0748396662186362, 0.8433265453898765]];
// linear-light Rec.2020 to LMS, again with crosstalk
// rational terms from Jan Fröhlich,
// Encoding High Dynamic Range andWide Color Gamut Imagery, p.97
// and ITU-R BT.2124-0 p.2
/*
const Rec2020toLMS_M = [
	[ 1688 / 4096,  2146 / 4096,   262 / 4096 ],
	[  683 / 4096,  2951 / 4096,   462 / 4096 ],
	[   99 / 4096,   309 / 4096,  3688 / 4096 ]
];
*/
// this includes the Ebner LMS coefficients,
// the rotation, and the scaling to [-0.5,0.5] range
// rational terms from Fröhlich p.97
// and ITU-R BT.2124-0 pp.2-3
const LMStoIPT_M = [[2048 / 4096, 2048 / 4096, 0], [6610 / 4096, -13613 / 4096, 7003 / 4096], [17933 / 4096, -17390 / 4096, -543 / 4096]];

// inverted matrices, calculated from the above
const IPTtoLMS_M = [[0.9999999999999998, 0.0086090370379328, 0.1110296250030260], [0.9999999999999998, -0.0086090370379328, -0.1110296250030259], [0.9999999999999998, 0.5600313357106791, -0.3206271749873188]];
/*
const LMStoRec2020_M = [
	[ 3.4375568932814012112,   -2.5072112125095058195,   0.069654319228104608382],
	[-0.79142868665644156125,   1.9838372198740089874,  -0.19240853321756742626 ],
	[-0.025646662911506476363, -0.099240248643945566751, 1.1248869115554520431  ]
];
*/
const LMStoXYZ_M = [[2.0701522183894223, -1.3263473389671563, 0.2066510476294053], [0.3647385209748072, 0.6805660249472273, -0.0453045459220347], [-0.0497472075358123, -0.0492609666966131, 1.1880659249923042]];

// Only the PQ form of ICtCp is implemented here. There is also an HLG form.
// from Dolby, "WHAT IS ICTCP?"
// https://professional.dolby.com/siteassets/pdfs/ictcp_dolbywhitepaper_v071.pdf
// and
// Dolby, "Perceptual Color Volume
// Measuring the Distinguishable Colors of HDR and WCG Displays"
// https://professional.dolby.com/siteassets/pdfs/dolby-vision-measuring-perceptual-color-volume-v7.1.pdf
var ictcp = new ColorSpace({
  id: "ictcp",
  name: "ICTCP",
  // From BT.2100-2 page 7:
  // During production, signal values are expected to exceed the
  // range E′ = [0.0 : 1.0]. This provides processing headroom and avoids
  // signal degradation during cascaded processing. Such values of E′,
  // below 0.0 or exceeding 1.0, should not be clipped during production
  // and exchange.
  // Values below 0.0 should not be clipped in reference displays (even
  // though they represent “negative” light) to allow the black level of
  // the signal (LB) to be properly set using test signals known as “PLUGE”
  coords: {
    i: {
      refRange: [0, 1],
      // Constant luminance,
      name: "I"
    },
    ct: {
      refRange: [-0.5, 0.5],
      // Full BT.2020 gamut in range [-0.5, 0.5]
      name: "CT"
    },
    cp: {
      refRange: [-0.5, 0.5],
      name: "CP"
    }
  },
  base: XYZ_Abs_D65,
  fromBase(XYZ) {
    // move to LMS cone domain
    let LMS = multiplyMatrices(XYZtoLMS_M, XYZ);
    return LMStoICtCp(LMS);
  },
  toBase(ICtCp) {
    let LMS = ICtCptoLMS(ICtCp);
    return multiplyMatrices(LMStoXYZ_M, LMS);
  }
});
function LMStoICtCp(LMS) {
  // apply the PQ EOTF
  // we can't ever be dividing by zero because of the "1 +" in the denominator
  let PQLMS = LMS.map(function (val) {
    let num = c1$1 + c2$1 * (val / 10000) ** m1$1;
    let denom = 1 + c3$1 * (val / 10000) ** m1$1;
    return (num / denom) ** m2;
  });

  // LMS to IPT, with rotation for Y'C'bC'r compatibility
  return multiplyMatrices(LMStoIPT_M, PQLMS);
}
function ICtCptoLMS(ICtCp) {
  let PQLMS = multiplyMatrices(IPTtoLMS_M, ICtCp);

  // From BT.2124-0 Annex 2 Conversion 3
  let LMS = PQLMS.map(function (val) {
    let num = Math.max(val ** im2 - c1$1, 0);
    let denom = c2$1 - c3$1 * val ** im2;
    return 10000 * (num / denom) ** im1;
  });
  return LMS;
}

// Delta E in ICtCp space,
// which the ITU calls Delta E ITP, which is shorter
// formulae from ITU Rec. ITU-R BT.2124-0

function deltaEITP (color, sample) {
  [color, sample] = getColor([color, sample]);

  // Given this color as the reference
  // and a sample,
  // calculate deltaE in ICtCp
  // which is simply the Euclidean distance

  let [I1, T1, P1] = ictcp.from(color);
  let [I2, T2, P2] = ictcp.from(sample);

  // the 0.25 factor is to undo the encoding scaling in Ct
  // the 720 is so that 1 deltaE = 1 JND
  // per  ITU-R BT.2124-0 p.3

  return 720 * Math.sqrt((I1 - I2) ** 2 + 0.25 * (T1 - T2) ** 2 + (P1 - P2) ** 2);
}

const white$3 = WHITES.D65;
const adaptedCoef = 0.42;
const adaptedCoefInv = 1 / adaptedCoef;
const tau = 2 * Math.PI;
const cat16 = [[0.401288, 0.650173, -0.051461], [-0.250268, 1.204414, 0.045854], [-0.002079, 0.048952, 0.953127]];
const cat16Inv = [[1.8620678550872327, -1.0112546305316843, 0.14918677544445175], [0.38752654323613717, 0.6214474419314753, -0.008973985167612518], [-0.015841498849333856, -0.03412293802851557, 1.0499644368778496]];
const m1 = [[460.0, 451.0, 288.0], [460.0, -891.0, -261.0], [460.0, -220.0, -6300.0]];
const surroundMap = {
  dark: [0.8, 0.525, 0.8],
  dim: [0.9, 0.59, 0.9],
  average: [1, 0.69, 1]
};
const hueQuadMap = {
  // Red, Yellow, Green, Blue, Red
  h: [20.14, 90.00, 164.25, 237.53, 380.14],
  e: [0.8, 0.7, 1.0, 1.2, 0.8],
  H: [0.0, 100.0, 200.0, 300.0, 400.0]
};
const rad2deg = 180 / Math.PI;
const deg2rad$1 = Math.PI / 180;
function adapt$1(coords, fl) {
  const temp = coords.map(c => {
    const x = spow(fl * Math.abs(c) * 0.01, adaptedCoef);
    return 400 * copySign(x, c) / (x + 27.13);
  });
  return temp;
}
function unadapt(adapted, fl) {
  const constant = 100 / fl * 27.13 ** adaptedCoefInv;
  return adapted.map(c => {
    const cabs = Math.abs(c);
    return copySign(constant * spow(cabs / (400 - cabs), adaptedCoefInv), c);
  });
}
function hueQuadrature(h) {
  let hp = constrain(h);
  if (hp <= hueQuadMap.h[0]) {
    hp += 360;
  }
  const i = bisectLeft(hueQuadMap.h, hp) - 1;
  const [hi, hii] = hueQuadMap.h.slice(i, i + 2);
  const [ei, eii] = hueQuadMap.e.slice(i, i + 2);
  const Hi = hueQuadMap.H[i];
  const t = (hp - hi) / ei;
  return Hi + 100 * t / (t + (hii - hp) / eii);
}
function invHueQuadrature(H) {
  let Hp = (H % 400 + 400) % 400;
  const i = Math.floor(0.01 * Hp);
  Hp = Hp % 100;
  const [hi, hii] = hueQuadMap.h.slice(i, i + 2);
  const [ei, eii] = hueQuadMap.e.slice(i, i + 2);
  return constrain((Hp * (eii * hi - ei * hii) - 100 * hi * eii) / (Hp * (eii - ei) - 100 * eii));
}
function environment(refWhite, adaptingLuminance, backgroundLuminance, surround, discounting) {
  const env = {};
  env.discounting = discounting;
  env.refWhite = refWhite;
  env.surround = surround;
  const xyzW = refWhite.map(c => {
    return c * 100;
  });

  // The average luminance of the environment in `cd/m^2cd/m` (a.k.a. nits)
  env.la = adaptingLuminance;
  // The relative luminance of the nearby background
  env.yb = backgroundLuminance;
  // Absolute luminance of the reference white.
  const yw = xyzW[1];

  // Cone response for reference white
  const rgbW = multiplyMatrices(cat16, xyzW);

  // Surround: dark, dim, and average
  surround = surroundMap[env.surround];
  const f = surround[0];
  env.c = surround[1];
  env.nc = surround[2];
  const k = 1 / (5 * env.la + 1);
  const k4 = k ** 4;

  // Factor of luminance level adaptation
  env.fl = k4 * env.la + 0.1 * (1 - k4) * (1 - k4) * Math.cbrt(5 * env.la);
  env.flRoot = env.fl ** 0.25;
  env.n = env.yb / yw;
  env.z = 1.48 + Math.sqrt(env.n);
  env.nbb = 0.725 * env.n ** -0.2;
  env.ncb = env.nbb;

  // Degree of adaptation calculating if not discounting
  // illuminant (assumed eye is fully adapted)
  const d = discounting ? 1 : Math.max(Math.min(f * (1 - 1 / 3.6 * Math.exp((-env.la - 42) / 92)), 1), 0);
  env.dRgb = rgbW.map(c => {
    return interpolate(1, yw / c, d);
  });
  env.dRgbInv = env.dRgb.map(c => {
    return 1 / c;
  });

  // Achromatic response
  const rgbCW = rgbW.map((c, i) => {
    return c * env.dRgb[i];
  });
  const rgbAW = adapt$1(rgbCW, env.fl);
  env.aW = env.nbb * (2 * rgbAW[0] + rgbAW[1] + 0.05 * rgbAW[2]);

  // console.log(env);

  return env;
}

// Pre-calculate everything we can with the viewing conditions
const viewingConditions$1 = environment(white$3, 64 / Math.PI * 0.2, 20, "average", false);
function fromCam16(cam16, env) {
  // These check ensure one, and only one attribute for a
  // given category is provided.
  if (!(cam16.J !== undefined ^ cam16.Q !== undefined)) {
    throw new Error("Conversion requires one and only one: 'J' or 'Q'");
  }
  if (!(cam16.C !== undefined ^ cam16.M !== undefined ^ cam16.s !== undefined)) {
    throw new Error("Conversion requires one and only one: 'C', 'M' or 's'");
  }

  // Hue is absolutely required
  if (!(cam16.h !== undefined ^ cam16.H !== undefined)) {
    throw new Error("Conversion requires one and only one: 'h' or 'H'");
  }

  // Black
  if (cam16.J === 0.0 || cam16.Q === 0.0) {
    return [0.0, 0.0, 0.0];
  }

  // Break hue into Cartesian components
  let hRad = 0.0;
  if (cam16.h !== undefined) {
    hRad = constrain(cam16.h) * deg2rad$1;
  } else {
    hRad = invHueQuadrature(cam16.H) * deg2rad$1;
  }
  const cosh = Math.cos(hRad);
  const sinh = Math.sin(hRad);

  // Calculate `Jroot` from one of the lightness derived coordinates.
  let Jroot = 0.0;
  if (cam16.J !== undefined) {
    Jroot = spow(cam16.J, 1 / 2) * 0.1;
  } else if (cam16.Q !== undefined) {
    Jroot = 0.25 * env.c * cam16.Q / ((env.aW + 4) * env.flRoot);
  }

  // Calculate the `t` value from one of the chroma derived coordinates
  let alpha = 0.0;
  if (cam16.C !== undefined) {
    alpha = cam16.C / Jroot;
  } else if (cam16.M !== undefined) {
    alpha = cam16.M / env.flRoot / Jroot;
  } else if (cam16.s !== undefined) {
    alpha = 0.0004 * cam16.s ** 2 * (env.aW + 4) / env.c;
  }
  const t = spow(alpha * Math.pow(1.64 - Math.pow(0.29, env.n), -0.73), 10 / 9);

  // Eccentricity
  const et = 0.25 * (Math.cos(hRad + 2) + 3.8);

  // Achromatic response
  const A = env.aW * spow(Jroot, 2 / env.c / env.z);

  // Calculate red-green and yellow-blue components
  const p1 = 5e4 / 13 * env.nc * env.ncb * et;
  const p2 = A / env.nbb;
  const r = 23 * (p2 + 0.305) * zdiv(t, 23 * p1 + t * (11 * cosh + 108 * sinh));
  const a = r * cosh;
  const b = r * sinh;

  // Calculate back from cone response to XYZ
  const rgb_c = unadapt(multiplyMatrices(m1, [p2, a, b]).map(c => {
    return c * 1 / 1403;
  }), env.fl);
  return multiplyMatrices(cat16Inv, rgb_c.map((c, i) => {
    return c * env.dRgbInv[i];
  })).map(c => {
    return c / 100;
  });
}
function toCam16(xyzd65, env) {
  // Cone response
  const xyz100 = xyzd65.map(c => {
    return c * 100;
  });
  const rgbA = adapt$1(multiplyMatrices(cat16, xyz100).map((c, i) => {
    return c * env.dRgb[i];
  }), env.fl);

  // Calculate hue from red-green and yellow-blue components
  const a = rgbA[0] + (-12 * rgbA[1] + rgbA[2]) / 11;
  const b = (rgbA[0] + rgbA[1] - 2 * rgbA[2]) / 9;
  const hRad = (Math.atan2(b, a) % tau + tau) % tau;

  // Eccentricity
  const et = 0.25 * (Math.cos(hRad + 2) + 3.8);
  const t = 5e4 / 13 * env.nc * env.ncb * zdiv(et * Math.sqrt(a ** 2 + b ** 2), rgbA[0] + rgbA[1] + 1.05 * rgbA[2] + 0.305);
  const alpha = spow(t, 0.9) * Math.pow(1.64 - Math.pow(0.29, env.n), 0.73);

  // Achromatic response
  const A = env.nbb * (2 * rgbA[0] + rgbA[1] + 0.05 * rgbA[2]);
  const Jroot = spow(A / env.aW, 0.5 * env.c * env.z);

  // Lightness
  const J = 100 * spow(Jroot, 2);

  // Brightness
  const Q = 4 / env.c * Jroot * (env.aW + 4) * env.flRoot;

  // Chroma
  const C = alpha * Jroot;

  // Colorfulness
  const M = C * env.flRoot;

  // Hue
  const h = constrain(hRad * rad2deg);

  // Hue quadrature
  const H = hueQuadrature(h);

  // Saturation
  const s = 50 * spow(env.c * alpha / (env.aW + 4), 1 / 2);

  // console.log({J: J, C: C, h: h, s: s, Q: Q, M: M, H: H});

  return {
    J: J,
    C: C,
    h: h,
    s: s,
    Q: Q,
    M: M,
    H: H
  };
}

// Provided as a way to directly evaluate the CAM16 model
// https://observablehq.com/@jrus/cam16: reference implementation
// https://arxiv.org/pdf/1802.06067.pdf: Nico Schlömer
// https://onlinelibrary.wiley.com/doi/pdf/10.1002/col.22324: hue quadrature
// https://www.researchgate.net/publication/318152296_Comprehensive_color_solutions_CAM16_CAT16_and_CAM16-UCS
// Results compared against: https://github.com/colour-science/colour
var cam16 = new ColorSpace({
  id: "cam16-jmh",
  cssId: "--cam16-jmh",
  name: "CAM16-JMh",
  coords: {
    j: {
      refRange: [0, 100],
      name: "J"
    },
    m: {
      refRange: [0, 105.0],
      name: "Colorfulness"
    },
    h: {
      refRange: [0, 360],
      type: "angle",
      name: "Hue"
    }
  },
  base: xyz_d65,
  fromBase(xyz) {
    const cam16 = toCam16(xyz, viewingConditions$1);
    return [cam16.J, cam16.M, cam16.h];
  },
  toBase(cam16) {
    return fromCam16({
      J: cam16[0],
      M: cam16[1],
      h: cam16[2]
    }, viewingConditions$1);
  }
});

const white$2 = WHITES.D65;
const ε$4 = 216 / 24389; // 6^3/29^3 == (24/116)^3
const κ$3 = 24389 / 27; // 29^3/3^3

function toLstar(y) {
  // Convert XYZ Y to L*

  const fy = y > ε$4 ? Math.cbrt(y) : (κ$3 * y + 16) / 116;
  return 116.0 * fy - 16.0;
}
function fromLstar(lstar) {
  // Convert L* back to XYZ Y

  return lstar > 8 ? Math.pow((lstar + 16) / 116, 3) : lstar / κ$3;
}
function fromHct(coords, env) {
  // Use Newton's method to try and converge as quick as possible or
  // converge as close as we can. While the requested precision is achieved
  // most of the time, it may not always be achievable. Especially past the
  // visible spectrum, the algorithm will likely struggle to get the same
  // precision. If, for whatever reason, we cannot achieve the accuracy we
  // seek in the allotted iterations, just return the closest we were able to
  // get.

  let [h, c, t] = coords;
  let xyz = [];
  let j = 0;

  // Shortcut out for black
  if (t === 0) {
    return [0.0, 0.0, 0.0];
  }

  // Calculate the Y we need to target
  let y = fromLstar(t);

  // A better initial guess yields better results. Polynomials come from
  // curve fitting the T vs J response.
  if (t > 0) {
    j = 0.00379058511492914 * t ** 2 + 0.608983189401032 * t + 0.9155088574762233;
  } else {
    j = 9.514440756550361e-06 * t ** 2 + 0.08693057439788597 * t - 21.928975842194614;
  }

  // Threshold of how close is close enough, and max number of attempts.
  // More precision and more attempts means more time spent iterating. Higher
  // required precision gives more accuracy but also increases the chance of
  // not hitting the goal. 2e-12 allows us to convert round trip with
  // reasonable accuracy of six decimal places or more.
  const threshold = 2e-12;
  const max_attempts = 15;
  let attempt = 0;
  let last = Infinity;

  // Try to find a J such that the returned y matches the returned y of the L*
  while (attempt <= max_attempts) {
    xyz = fromCam16({
      J: j,
      C: c,
      h: h
    }, env);

    // If we are within range, return XYZ
    // If we are closer than last time, save the values
    const delta = Math.abs(xyz[1] - y);
    if (delta < last) {
      if (delta <= threshold) {
        return xyz;
      }
      last = delta;
    }

    // f(j_root) = (j ** (1 / 2)) * 0.1
    // f(j) = ((f(j_root) * 100) ** 2) / j - 1 = 0
    // f(j_root) = Y = y / 100
    // f(j) = (y ** 2) / j - 1
    // f'(j) = (2 * y) / j
    j = j - (xyz[1] - y) * j / (2 * xyz[1]);
    attempt += 1;
  }

  // We could not acquire the precision we desired,
  // return our closest attempt.
  return fromCam16({
    J: j,
    C: c,
    h: h
  }, env);
}
function toHct(xyz, env) {
  // Calculate HCT by taking the L* of CIE LCh D65 and CAM16 chroma and hue.

  const t = toLstar(xyz[1]);
  if (t === 0.0) {
    return [0.0, 0.0, 0.0];
  }
  const cam16 = toCam16(xyz, viewingConditions);
  return [constrain(cam16.h), cam16.C, t];
}

// Pre-calculate everything we can with the viewing conditions
const viewingConditions = environment(white$2, 200 / Math.PI * fromLstar(50.0), fromLstar(50.0) * 100, "average", false);

// https://material.io/blog/science-of-color-design
// This is not a port of the material-color-utilities,
// but instead implements the full color space as described,
// combining CAM16 JCh and Lab D65. This does not clamp conversion
// to HCT to specific chroma bands and provides support for wider
// gamuts than Google currently supports and does so at a greater
// precision (> 8 bits back to sRGB).
// This implementation comes from https://github.com/facelessuser/coloraide
// which is licensed under MIT.
var hct = new ColorSpace({
  id: "hct",
  name: "HCT",
  coords: {
    h: {
      refRange: [0, 360],
      type: "angle",
      name: "Hue"
    },
    c: {
      refRange: [0, 145],
      name: "Colorfulness"
    },
    t: {
      refRange: [0, 100],
      name: "Tone"
    }
  },
  base: xyz_d65,
  fromBase(xyz) {
    return toHct(xyz);
  },
  toBase(hct) {
    return fromHct(hct, viewingConditions);
  },
  formats: {
    color: {
      id: "--hct",
      coords: ["<number> | <angle>", "<percentage> | <number>", "<percentage> | <number>"]
    }
  }
});

const deg2rad = Math.PI / 180;
const ucsCoeff = [1.00, 0.007, 0.0228];

/**
* Convert HCT chroma and hue (CAM16 JMh colorfulness and hue) using UCS logic for a and b.
* @param {number[]} coords - HCT coordinates.
* @return {number[]}
*/
function convertUcsAb(coords) {
  // We want the distance between the actual color.
  // If chroma is negative, it will throw off our calculations.
  // Normally, converting back to the base and forward will correct it.
  // If we have a negative chroma after this, then we have a color that
  // cannot resolve to positive chroma.
  if (coords[1] < 0) {
    coords = hct.fromBase(hct.toBase(coords));
  }

  // Only in extreme cases (usually outside the visible spectrum)
  // can the input value for log become negative.
  // Avoid domain error by forcing a zero result via "max" if necessary.
  const M = Math.log(Math.max(1 + ucsCoeff[2] * coords[1] * viewingConditions.flRoot, 1.0)) / ucsCoeff[2];
  const hrad = coords[0] * deg2rad;
  const a = M * Math.cos(hrad);
  const b = M * Math.sin(hrad);
  return [coords[2], a, b];
}

/**
* Color distance using HCT.
* @param {Color} color - Color to compare.
* @param {Color} sample - Color to compare.
* @return {number[]}
*/
function deltaEHCT (color, sample) {
  [color, sample] = getColor([color, sample]);
  let [t1, a1, b1] = convertUcsAb(hct.from(color));
  let [t2, a2, b2] = convertUcsAb(hct.from(sample));

  // Use simple euclidean distance with a and b using UCS conversion
  // and LCh lightness (HCT tone).
  return Math.sqrt((t1 - t2) ** 2 + (a1 - a2) ** 2 + (b1 - b2) ** 2);
}

var deltaEMethods = {
  deltaE76,
  deltaECMC,
  deltaE2000,
  deltaEJz,
  deltaEITP,
  deltaEOK,
  deltaEHCT
};

/**
 * Calculate the epsilon to 2 degrees smaller than the specified JND.
 * @param {Number} jnd - The target "just noticeable difference".
 * @returns {Number}
 */
function calcEpsilon(jnd) {
  // Calculate the epsilon to 2 degrees smaller than the specified JND.

  const order = !jnd ? 0 : Math.floor(Math.log10(Math.abs(jnd)));
  // Limit to an arbitrary value to ensure value is never too small and causes infinite loops.
  return Math.max(parseFloat(`1e${order - 2}`), 1e-6);
}
const GMAPPRESET = {
  "hct": {
    method: "hct.c",
    jnd: 2,
    deltaEMethod: "hct",
    blackWhiteClamp: {}
  },
  "hct-tonal": {
    method: "hct.c",
    jnd: 0,
    deltaEMethod: "hct",
    blackWhiteClamp: {
      channel: "hct.t",
      min: 0,
      max: 100
    }
  }
};

/**
 * Force coordinates to be in gamut of a certain color space.
 * Mutates the color it is passed.
 * @param {Object|string} options object or spaceId string
 * @param {string} options.method - How to force into gamut.
 *        If "clip", coordinates are just clipped to their reference range.
 *        If "css", coordinates are reduced according to the CSS 4 Gamut Mapping Algorithm.
 *        If in the form [colorSpaceId].[coordName], that coordinate is reduced
 *        until the color is in gamut. Please note that this may produce nonsensical
 *        results for certain coordinates (e.g. hue) or infinite loops if reducing the coordinate never brings the color in gamut.
 * @param {ColorSpace|string} options.space - The space whose gamut we want to map to
 * @param {string} options.deltaEMethod - The delta E method to use while performing gamut mapping.
 *        If no method is specified, delta E 2000 is used.
 * @param {Number} options.jnd - The "just noticeable difference" to target.
 * @param {Object} options.blackWhiteClamp - Used to configure SDR black and clamping.
 *        "channel" indicates the "space.channel" to use for determining when to clamp.
 *        "min" indicates the lower limit for black clamping and "max" indicates the upper
 *        limit for white clamping.
 */

function toGamut(color) {
  let {
    method = defaults.gamut_mapping,
    space = undefined,
    deltaEMethod = "",
    jnd = 2,
    blackWhiteClamp = {}
  } = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : {};
  color = getColor(color);
  if (isString(arguments[1])) {
    space = arguments[1];
  } else if (!space) {
    space = color.space;
  }
  space = ColorSpace.get(space);

  // 3 spaces:
  // color.space: current color space
  // space: space whose gamut we are mapping to
  // mapSpace: space with the coord we're reducing

  if (inGamut(color, space, {
    epsilon: 0
  })) {
    return color;
  }
  let spaceColor;
  if (method === "css") {
    spaceColor = toGamutCSS(color, {
      space
    });
  } else {
    if (method !== "clip" && !inGamut(color, space)) {
      if (Object.prototype.hasOwnProperty.call(GMAPPRESET, method)) {
        ({
          method,
          jnd,
          deltaEMethod,
          blackWhiteClamp
        } = GMAPPRESET[method]);
      }

      // Get the correct delta E method
      let de = deltaE2000;
      if (deltaEMethod !== "") {
        for (let m in deltaEMethods) {
          if ("deltae" + deltaEMethod.toLowerCase() === m.toLowerCase()) {
            de = deltaEMethods[m];
            break;
          }
        }
      }
      let clipped = toGamut(to(color, space), {
        method: "clip",
        space
      });
      if (de(color, clipped) > jnd) {
        // Clamp to SDR white and black if required
        if (Object.keys(blackWhiteClamp).length === 3) {
          let channelMeta = ColorSpace.resolveCoord(blackWhiteClamp.channel);
          let channel = get(to(color, channelMeta.space), channelMeta.id);
          if (isNone(channel)) {
            channel = 0;
          }
          if (channel >= blackWhiteClamp.max) {
            return to({
              space: "xyz-d65",
              coords: WHITES["D65"]
            }, color.space);
          } else if (channel <= blackWhiteClamp.min) {
            return to({
              space: "xyz-d65",
              coords: [0, 0, 0]
            }, color.space);
          }
        }

        // Reduce a coordinate of a certain color space until the color is in gamut
        let coordMeta = ColorSpace.resolveCoord(method);
        let mapSpace = coordMeta.space;
        let coordId = coordMeta.id;
        let mappedColor = to(color, mapSpace);
        // If we were already in the mapped color space, we need to resolve undefined channels
        mappedColor.coords.forEach((c, i) => {
          if (isNone(c)) {
            mappedColor.coords[i] = 0;
          }
        });
        let bounds = coordMeta.range || coordMeta.refRange;
        let min = bounds[0];
        let ε = calcEpsilon(jnd);
        let low = min;
        let high = get(mappedColor, coordId);
        while (high - low > ε) {
          let clipped = clone(mappedColor);
          clipped = toGamut(clipped, {
            space,
            method: "clip"
          });
          let deltaE = de(mappedColor, clipped);
          if (deltaE - jnd < ε) {
            low = get(mappedColor, coordId);
          } else {
            high = get(mappedColor, coordId);
          }
          set(mappedColor, coordId, (low + high) / 2);
        }
        spaceColor = to(mappedColor, space);
      } else {
        spaceColor = clipped;
      }
    } else {
      spaceColor = to(color, space);
    }
    if (method === "clip" // Dumb coord clipping
    // finish off smarter gamut mapping with clip to get rid of ε, see #17
    || !inGamut(spaceColor, space, {
      epsilon: 0
    })) {
      let bounds = Object.values(space.coords).map(c => c.range || []);
      spaceColor.coords = spaceColor.coords.map((c, i) => {
        let [min, max] = bounds[i];
        if (min !== undefined) {
          c = Math.max(min, c);
        }
        if (max !== undefined) {
          c = Math.min(c, max);
        }
        return c;
      });
    }
  }
  if (space !== color.space) {
    spaceColor = to(spaceColor, color.space);
  }
  color.coords = spaceColor.coords;
  return color;
}
toGamut.returns = "color";

// The reference colors to be used if lightness is out of the range 0-1 in the
// `Oklch` space. These are created in the `Oklab` space, as it is used by the
// DeltaEOK calculation, so it is guaranteed to be imported.
const COLORS = {
  WHITE: {
    space: OKLab,
    coords: [1, 0, 0]
  },
  BLACK: {
    space: OKLab,
    coords: [0, 0, 0]
  }
};

/**
 * Given a color `origin`, returns a new color that is in gamut using
 * the CSS Gamut Mapping Algorithm. If `space` is specified, it will be in gamut
 * in `space`, and returned in `space`. Otherwise, it will be in gamut and
 * returned in the color space of `origin`.
 * @param {Object} origin
 * @param {Object} options
 * @param {ColorSpace|string} options.space
 * @returns {Color}
 */
function toGamutCSS(origin) {
  let {
    space
  } = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : {};
  const JND = 0.02;
  const ε = 0.0001;
  origin = getColor(origin);
  if (!space) {
    space = origin.space;
  }
  space = ColorSpace.get(space);
  const oklchSpace = ColorSpace.get("oklch");
  if (space.isUnbounded) {
    return to(origin, space);
  }
  const origin_OKLCH = to(origin, oklchSpace);
  let L = origin_OKLCH.coords[0];

  // return media white or black, if lightness is out of range
  if (L >= 1) {
    const white = to(COLORS.WHITE, space);
    white.alpha = origin.alpha;
    return to(white, space);
  }
  if (L <= 0) {
    const black = to(COLORS.BLACK, space);
    black.alpha = origin.alpha;
    return to(black, space);
  }
  if (inGamut(origin_OKLCH, space, {
    epsilon: 0
  })) {
    return to(origin_OKLCH, space);
  }
  function clip(_color) {
    const destColor = to(_color, space);
    const spaceCoords = Object.values(space.coords);
    destColor.coords = destColor.coords.map((coord, index) => {
      if ("range" in spaceCoords[index]) {
        const [min, max] = spaceCoords[index].range;
        return clamp(min, coord, max);
      }
      return coord;
    });
    return destColor;
  }
  let min = 0;
  let max = origin_OKLCH.coords[1];
  let min_inGamut = true;
  let current = clone(origin_OKLCH);
  let clipped = clip(current);
  let E = deltaEOK(clipped, current);
  if (E < JND) {
    return clipped;
  }
  while (max - min > ε) {
    const chroma = (min + max) / 2;
    current.coords[1] = chroma;
    if (min_inGamut && inGamut(current, space, {
      epsilon: 0
    })) {
      min = chroma;
    } else {
      clipped = clip(current);
      E = deltaEOK(clipped, current);
      if (E < JND) {
        if (JND - E < ε) {
          break;
        } else {
          min_inGamut = false;
          min = chroma;
        }
      } else {
        max = chroma;
      }
    }
  }
  return clipped;
}

/**
 * Convert to color space and return a new color
 * @param {Object|string} space - Color space object or id
 * @param {Object} options
 * @param {boolean} options.inGamut - Whether to force resulting color in gamut
 * @returns {Color}
 */
function to(color, space) {
  let {
    inGamut
  } = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};
  color = getColor(color);
  space = ColorSpace.get(space);
  let coords = space.from(color);
  let ret = {
    space,
    coords,
    alpha: color.alpha
  };
  if (inGamut) {
    ret = toGamut(ret, inGamut === true ? undefined : inGamut);
  }
  return ret;
}
to.returns = "color";

var es_array_unshift = {};

var deletePropertyOrThrow;
var hasRequiredDeletePropertyOrThrow;

function requireDeletePropertyOrThrow () {
	if (hasRequiredDeletePropertyOrThrow) return deletePropertyOrThrow;
	hasRequiredDeletePropertyOrThrow = 1;
	var tryToString = requireTryToString();

	var $TypeError = TypeError;

	deletePropertyOrThrow = function (O, P) {
	  if (!delete O[P]) throw new $TypeError('Cannot delete property ' + tryToString(P) + ' of ' + tryToString(O));
	};
	return deletePropertyOrThrow;
}

var hasRequiredEs_array_unshift;

function requireEs_array_unshift () {
	if (hasRequiredEs_array_unshift) return es_array_unshift;
	hasRequiredEs_array_unshift = 1;
	var $ = require_export();
	var toObject = requireToObject();
	var lengthOfArrayLike = requireLengthOfArrayLike();
	var setArrayLength = requireArraySetLength();
	var deletePropertyOrThrow = requireDeletePropertyOrThrow();
	var doesNotExceedSafeInteger = requireDoesNotExceedSafeInteger();

	// IE8-
	var INCORRECT_RESULT = [].unshift(0) !== 1;

	// V8 ~ Chrome < 71 and Safari <= 15.4, FF < 23 throws InternalError
	var properErrorOnNonWritableLength = function () {
	  try {
	    // eslint-disable-next-line es/no-object-defineproperty -- safe
	    Object.defineProperty([], 'length', { writable: false }).unshift();
	  } catch (error) {
	    return error instanceof TypeError;
	  }
	};

	var FORCED = INCORRECT_RESULT || !properErrorOnNonWritableLength();

	// `Array.prototype.unshift` method
	// https://tc39.es/ecma262/#sec-array.prototype.unshift
	$({ target: 'Array', proto: true, arity: 1, forced: FORCED }, {
	  // eslint-disable-next-line no-unused-vars -- required for `.length`
	  unshift: function unshift(item) {
	    var O = toObject(this);
	    var len = lengthOfArrayLike(O);
	    var argCount = arguments.length;
	    if (argCount) {
	      doesNotExceedSafeInteger(len + argCount);
	      var k = len;
	      while (k--) {
	        var to = k + argCount;
	        if (k in O) O[to] = O[k];
	        else deletePropertyOrThrow(O, to);
	      }
	      for (var j = 0; j < argCount; j++) {
	        O[j] = arguments[j];
	      }
	    } return setArrayLength(O, len + argCount);
	  }
	});
	return es_array_unshift;
}

requireEs_array_unshift();

/**
 * Generic toString() method, outputs a color(spaceId ...coords) function, a functional syntax, or custom formats defined by the color space
 * @param {Object} options
 * @param {number} options.precision - Significant digits
 * @param {boolean} options.inGamut - Adjust coordinates to fit in gamut first? [default: false]
 */
function serialize(color) {
  var _ref, _color$space$getForma;
  let {
    precision = defaults.precision,
    format = "default",
    inGamut: inGamut$1 = true,
    ...customOptions
  } = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : {};
  let ret;
  color = getColor(color);
  let formatId = format;
  format = (_ref = (_color$space$getForma = color.space.getFormat(format)) !== null && _color$space$getForma !== void 0 ? _color$space$getForma : color.space.getFormat("default")) !== null && _ref !== void 0 ? _ref : ColorSpace.DEFAULT_FORMAT;

  // The assignment to coords and inGamut needs to stay in the order they are now
  // The order of the assignment was changed as a workaround for a bug in Next.js
  // See this issue for details: https://github.com/color-js/color.js/issues/260

  let coords = color.coords.slice(); // clone so we can manipulate it

  inGamut$1 || (inGamut$1 = format.toGamut);
  if (inGamut$1 && !inGamut(color)) {
    // FIXME what happens if the color contains NaNs?
    coords = toGamut(clone(color), inGamut$1 === true ? undefined : inGamut$1).coords;
  }
  if (format.type === "custom") {
    customOptions.precision = precision;
    if (format.serialize) {
      ret = format.serialize(coords, color.alpha, customOptions);
    } else {
      throw new TypeError(`format ${formatId} can only be used to parse colors, not for serialization`);
    }
  } else {
    // Functional syntax
    let name = format.name || "color";
    if (format.serializeCoords) {
      coords = format.serializeCoords(coords, precision);
    } else {
      if (precision !== null) {
        coords = coords.map(c => {
          return serializeNumber(c, {
            precision
          });
        });
      }
    }
    let args = [...coords];
    if (name === "color") {
      var _format$ids;
      // If output is a color() function, add colorspace id as first argument
      let cssId = format.id || ((_format$ids = format.ids) === null || _format$ids === void 0 ? void 0 : _format$ids[0]) || color.space.id;
      args.unshift(cssId);
    }
    let alpha = color.alpha;
    if (precision !== null) {
      alpha = serializeNumber(alpha, {
        precision
      });
    }
    let strAlpha = color.alpha >= 1 || format.noAlpha ? "" : `${format.commas ? "," : " /"} ${alpha}`;
    ret = `${name}(${args.join(format.commas ? ", " : " ")}${strAlpha})`;
  }
  return ret;
}

// convert an array of linear-light rec2020 values to CIE XYZ
// using  D65 (no chromatic adaptation)
// http://www.brucelindbloom.com/index.html?Eqn_RGB_XYZ_Matrix.html
// 0 is actually calculated as  4.994106574466076e-17
const toXYZ_M$5 = [[0.6369580483012914, 0.14461690358620832, 0.1688809751641721], [0.2627002120112671, 0.6779980715188708, 0.05930171646986196], [0.000000000000000, 0.028072693049087428, 1.060985057710791]];

// from ITU-R BT.2124-0 Annex 2 p.3
const fromXYZ_M$5 = [[1.716651187971268, -0.355670783776392, -0.253366281373660], [-0.666684351832489, 1.616481236634939, 0.0157685458139111], [0.017639857445311, -0.042770613257809, 0.942103121235474]];
var REC2020Linear = new RGBColorSpace({
  id: "rec2020-linear",
  cssId: "--rec2020-linear",
  name: "Linear REC.2020",
  white: "D65",
  toXYZ_M: toXYZ_M$5,
  fromXYZ_M: fromXYZ_M$5
});

// import sRGB from "./srgb.js";

const α = 1.09929682680944;
const β = 0.018053968510807;
var REC2020 = new RGBColorSpace({
  id: "rec2020",
  name: "REC.2020",
  base: REC2020Linear,
  // Non-linear transfer function from Rec. ITU-R BT.2020-2 table 4
  toBase(RGB) {
    return RGB.map(function (val) {
      if (val < β * 4.5) {
        return val / 4.5;
      }
      return Math.pow((val + α - 1) / α, 1 / 0.45);
    });
  },
  fromBase(RGB) {
    return RGB.map(function (val) {
      if (val >= β) {
        return α * Math.pow(val, 0.45) - (α - 1);
      }
      return 4.5 * val;
    });
  }
});

const toXYZ_M$4 = [[0.4865709486482162, 0.26566769316909306, 0.1982172852343625], [0.2289745640697488, 0.6917385218365064, 0.079286914093745], [0.0000000000000000, 0.04511338185890264, 1.043944368900976]];
const fromXYZ_M$4 = [[2.493496911941425, -0.9313836179191239, -0.40271078445071684], [-0.8294889695615747, 1.7626640603183463, 0.023624685841943577], [0.03584583024378447, -0.07617238926804182, 0.9568845240076872]];
var P3Linear = new RGBColorSpace({
  id: "p3-linear",
  cssId: "--display-p3-linear",
  name: "Linear P3",
  white: "D65",
  toXYZ_M: toXYZ_M$4,
  fromXYZ_M: fromXYZ_M$4
});

// This is the linear-light version of sRGB
// as used for example in SVG filters
// or in Canvas

// This matrix was calculated directly from the RGB and white chromaticities
// when rounded to 8 decimal places, it agrees completely with the official matrix
// see https://github.com/w3c/csswg-drafts/issues/5922
const toXYZ_M$3 = [[0.41239079926595934, 0.357584339383878, 0.1804807884018343], [0.21263900587151027, 0.715168678767756, 0.07219231536073371], [0.01933081871559182, 0.11919477979462598, 0.9505321522496607]];

// This matrix is the inverse of the above;
// again it agrees with the official definition when rounded to 8 decimal places
const fromXYZ_M$3 = [[3.2409699419045226, -1.537383177570094, -0.4986107602930034], [-0.9692436362808796, 1.8759675015077202, 0.04155505740717559], [0.05563007969699366, -0.20397695888897652, 1.0569715142428786]];
var sRGBLinear = new RGBColorSpace({
  id: "srgb-linear",
  name: "Linear sRGB",
  white: "D65",
  toXYZ_M: toXYZ_M$3,
  fromXYZ_M: fromXYZ_M$3
});

/* List of CSS color keywords
 * Note that this does not include currentColor, transparent,
 * or system colors
 */

// To produce: Visit https://www.w3.org/TR/css-color-4/#named-colors
// and run in the console:
// copy($$("tr", $(".named-color-table tbody")).map(tr => `"${tr.cells[2].textContent.trim()}": [${tr.cells[4].textContent.trim().split(/\s+/).map(c => c === "0"? "0" : c === "255"? "1" : c + " / 255").join(", ")}]`).join(",\n"))
var KEYWORDS = {
  "aliceblue": [240 / 255, 248 / 255, 1],
  "antiquewhite": [250 / 255, 235 / 255, 215 / 255],
  "aqua": [0, 1, 1],
  "aquamarine": [127 / 255, 1, 212 / 255],
  "azure": [240 / 255, 1, 1],
  "beige": [245 / 255, 245 / 255, 220 / 255],
  "bisque": [1, 228 / 255, 196 / 255],
  "black": [0, 0, 0],
  "blanchedalmond": [1, 235 / 255, 205 / 255],
  "blue": [0, 0, 1],
  "blueviolet": [138 / 255, 43 / 255, 226 / 255],
  "brown": [165 / 255, 42 / 255, 42 / 255],
  "burlywood": [222 / 255, 184 / 255, 135 / 255],
  "cadetblue": [95 / 255, 158 / 255, 160 / 255],
  "chartreuse": [127 / 255, 1, 0],
  "chocolate": [210 / 255, 105 / 255, 30 / 255],
  "coral": [1, 127 / 255, 80 / 255],
  "cornflowerblue": [100 / 255, 149 / 255, 237 / 255],
  "cornsilk": [1, 248 / 255, 220 / 255],
  "crimson": [220 / 255, 20 / 255, 60 / 255],
  "cyan": [0, 1, 1],
  "darkblue": [0, 0, 139 / 255],
  "darkcyan": [0, 139 / 255, 139 / 255],
  "darkgoldenrod": [184 / 255, 134 / 255, 11 / 255],
  "darkgray": [169 / 255, 169 / 255, 169 / 255],
  "darkgreen": [0, 100 / 255, 0],
  "darkgrey": [169 / 255, 169 / 255, 169 / 255],
  "darkkhaki": [189 / 255, 183 / 255, 107 / 255],
  "darkmagenta": [139 / 255, 0, 139 / 255],
  "darkolivegreen": [85 / 255, 107 / 255, 47 / 255],
  "darkorange": [1, 140 / 255, 0],
  "darkorchid": [153 / 255, 50 / 255, 204 / 255],
  "darkred": [139 / 255, 0, 0],
  "darksalmon": [233 / 255, 150 / 255, 122 / 255],
  "darkseagreen": [143 / 255, 188 / 255, 143 / 255],
  "darkslateblue": [72 / 255, 61 / 255, 139 / 255],
  "darkslategray": [47 / 255, 79 / 255, 79 / 255],
  "darkslategrey": [47 / 255, 79 / 255, 79 / 255],
  "darkturquoise": [0, 206 / 255, 209 / 255],
  "darkviolet": [148 / 255, 0, 211 / 255],
  "deeppink": [1, 20 / 255, 147 / 255],
  "deepskyblue": [0, 191 / 255, 1],
  "dimgray": [105 / 255, 105 / 255, 105 / 255],
  "dimgrey": [105 / 255, 105 / 255, 105 / 255],
  "dodgerblue": [30 / 255, 144 / 255, 1],
  "firebrick": [178 / 255, 34 / 255, 34 / 255],
  "floralwhite": [1, 250 / 255, 240 / 255],
  "forestgreen": [34 / 255, 139 / 255, 34 / 255],
  "fuchsia": [1, 0, 1],
  "gainsboro": [220 / 255, 220 / 255, 220 / 255],
  "ghostwhite": [248 / 255, 248 / 255, 1],
  "gold": [1, 215 / 255, 0],
  "goldenrod": [218 / 255, 165 / 255, 32 / 255],
  "gray": [128 / 255, 128 / 255, 128 / 255],
  "green": [0, 128 / 255, 0],
  "greenyellow": [173 / 255, 1, 47 / 255],
  "grey": [128 / 255, 128 / 255, 128 / 255],
  "honeydew": [240 / 255, 1, 240 / 255],
  "hotpink": [1, 105 / 255, 180 / 255],
  "indianred": [205 / 255, 92 / 255, 92 / 255],
  "indigo": [75 / 255, 0, 130 / 255],
  "ivory": [1, 1, 240 / 255],
  "khaki": [240 / 255, 230 / 255, 140 / 255],
  "lavender": [230 / 255, 230 / 255, 250 / 255],
  "lavenderblush": [1, 240 / 255, 245 / 255],
  "lawngreen": [124 / 255, 252 / 255, 0],
  "lemonchiffon": [1, 250 / 255, 205 / 255],
  "lightblue": [173 / 255, 216 / 255, 230 / 255],
  "lightcoral": [240 / 255, 128 / 255, 128 / 255],
  "lightcyan": [224 / 255, 1, 1],
  "lightgoldenrodyellow": [250 / 255, 250 / 255, 210 / 255],
  "lightgray": [211 / 255, 211 / 255, 211 / 255],
  "lightgreen": [144 / 255, 238 / 255, 144 / 255],
  "lightgrey": [211 / 255, 211 / 255, 211 / 255],
  "lightpink": [1, 182 / 255, 193 / 255],
  "lightsalmon": [1, 160 / 255, 122 / 255],
  "lightseagreen": [32 / 255, 178 / 255, 170 / 255],
  "lightskyblue": [135 / 255, 206 / 255, 250 / 255],
  "lightslategray": [119 / 255, 136 / 255, 153 / 255],
  "lightslategrey": [119 / 255, 136 / 255, 153 / 255],
  "lightsteelblue": [176 / 255, 196 / 255, 222 / 255],
  "lightyellow": [1, 1, 224 / 255],
  "lime": [0, 1, 0],
  "limegreen": [50 / 255, 205 / 255, 50 / 255],
  "linen": [250 / 255, 240 / 255, 230 / 255],
  "magenta": [1, 0, 1],
  "maroon": [128 / 255, 0, 0],
  "mediumaquamarine": [102 / 255, 205 / 255, 170 / 255],
  "mediumblue": [0, 0, 205 / 255],
  "mediumorchid": [186 / 255, 85 / 255, 211 / 255],
  "mediumpurple": [147 / 255, 112 / 255, 219 / 255],
  "mediumseagreen": [60 / 255, 179 / 255, 113 / 255],
  "mediumslateblue": [123 / 255, 104 / 255, 238 / 255],
  "mediumspringgreen": [0, 250 / 255, 154 / 255],
  "mediumturquoise": [72 / 255, 209 / 255, 204 / 255],
  "mediumvioletred": [199 / 255, 21 / 255, 133 / 255],
  "midnightblue": [25 / 255, 25 / 255, 112 / 255],
  "mintcream": [245 / 255, 1, 250 / 255],
  "mistyrose": [1, 228 / 255, 225 / 255],
  "moccasin": [1, 228 / 255, 181 / 255],
  "navajowhite": [1, 222 / 255, 173 / 255],
  "navy": [0, 0, 128 / 255],
  "oldlace": [253 / 255, 245 / 255, 230 / 255],
  "olive": [128 / 255, 128 / 255, 0],
  "olivedrab": [107 / 255, 142 / 255, 35 / 255],
  "orange": [1, 165 / 255, 0],
  "orangered": [1, 69 / 255, 0],
  "orchid": [218 / 255, 112 / 255, 214 / 255],
  "palegoldenrod": [238 / 255, 232 / 255, 170 / 255],
  "palegreen": [152 / 255, 251 / 255, 152 / 255],
  "paleturquoise": [175 / 255, 238 / 255, 238 / 255],
  "palevioletred": [219 / 255, 112 / 255, 147 / 255],
  "papayawhip": [1, 239 / 255, 213 / 255],
  "peachpuff": [1, 218 / 255, 185 / 255],
  "peru": [205 / 255, 133 / 255, 63 / 255],
  "pink": [1, 192 / 255, 203 / 255],
  "plum": [221 / 255, 160 / 255, 221 / 255],
  "powderblue": [176 / 255, 224 / 255, 230 / 255],
  "purple": [128 / 255, 0, 128 / 255],
  "rebeccapurple": [102 / 255, 51 / 255, 153 / 255],
  "red": [1, 0, 0],
  "rosybrown": [188 / 255, 143 / 255, 143 / 255],
  "royalblue": [65 / 255, 105 / 255, 225 / 255],
  "saddlebrown": [139 / 255, 69 / 255, 19 / 255],
  "salmon": [250 / 255, 128 / 255, 114 / 255],
  "sandybrown": [244 / 255, 164 / 255, 96 / 255],
  "seagreen": [46 / 255, 139 / 255, 87 / 255],
  "seashell": [1, 245 / 255, 238 / 255],
  "sienna": [160 / 255, 82 / 255, 45 / 255],
  "silver": [192 / 255, 192 / 255, 192 / 255],
  "skyblue": [135 / 255, 206 / 255, 235 / 255],
  "slateblue": [106 / 255, 90 / 255, 205 / 255],
  "slategray": [112 / 255, 128 / 255, 144 / 255],
  "slategrey": [112 / 255, 128 / 255, 144 / 255],
  "snow": [1, 250 / 255, 250 / 255],
  "springgreen": [0, 1, 127 / 255],
  "steelblue": [70 / 255, 130 / 255, 180 / 255],
  "tan": [210 / 255, 180 / 255, 140 / 255],
  "teal": [0, 128 / 255, 128 / 255],
  "thistle": [216 / 255, 191 / 255, 216 / 255],
  "tomato": [1, 99 / 255, 71 / 255],
  "turquoise": [64 / 255, 224 / 255, 208 / 255],
  "violet": [238 / 255, 130 / 255, 238 / 255],
  "wheat": [245 / 255, 222 / 255, 179 / 255],
  "white": [1, 1, 1],
  "whitesmoke": [245 / 255, 245 / 255, 245 / 255],
  "yellow": [1, 1, 0],
  "yellowgreen": [154 / 255, 205 / 255, 50 / 255]
};

let coordGrammar = Array(3).fill("<percentage> | <number>[0, 255]");
let coordGrammarNumber = Array(3).fill("<number>[0, 255]");
var sRGB = new RGBColorSpace({
  id: "srgb",
  name: "sRGB",
  base: sRGBLinear,
  fromBase: rgb => {
    // convert an array of linear-light sRGB values in the range 0.0-1.0
    // to gamma corrected form
    // https://en.wikipedia.org/wiki/SRGB
    return rgb.map(val => {
      let sign = val < 0 ? -1 : 1;
      let abs = val * sign;
      if (abs > 0.0031308) {
        return sign * (1.055 * abs ** (1 / 2.4) - 0.055);
      }
      return 12.92 * val;
    });
  },
  toBase: rgb => {
    // convert an array of sRGB values in the range 0.0 - 1.0
    // to linear light (un-companded) form.
    // https://en.wikipedia.org/wiki/SRGB
    return rgb.map(val => {
      let sign = val < 0 ? -1 : 1;
      let abs = val * sign;
      if (abs <= 0.04045) {
        return val / 12.92;
      }
      return sign * ((abs + 0.055) / 1.055) ** 2.4;
    });
  },
  formats: {
    "rgb": {
      coords: coordGrammar
    },
    "rgb_number": {
      name: "rgb",
      commas: true,
      coords: coordGrammarNumber,
      noAlpha: true
    },
    "color": {/* use defaults */},
    "rgba": {
      coords: coordGrammar,
      commas: true,
      lastAlpha: true
    },
    "rgba_number": {
      name: "rgba",
      commas: true,
      coords: coordGrammarNumber
    },
    "hex": {
      type: "custom",
      toGamut: true,
      test: str => /^#([a-f0-9]{3,4}){1,2}$/i.test(str),
      parse(str) {
        if (str.length <= 5) {
          // #rgb or #rgba, duplicate digits
          str = str.replace(/[a-f0-9]/gi, "$&$&");
        }
        let rgba = [];
        str.replace(/[a-f0-9]{2}/gi, component => {
          rgba.push(parseInt(component, 16) / 255);
        });
        return {
          spaceId: "srgb",
          coords: rgba.slice(0, 3),
          alpha: rgba.slice(3)[0]
        };
      },
      serialize: function (coords, alpha) {
        let {
          collapse = true // collapse to 3-4 digit hex when possible?
        } = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};
        if (alpha < 1) {
          coords.push(alpha);
        }
        coords = coords.map(c => Math.round(c * 255));
        let collapsible = collapse && coords.every(c => c % 17 === 0);
        let hex = coords.map(c => {
          if (collapsible) {
            return (c / 17).toString(16);
          }
          return c.toString(16).padStart(2, "0");
        }).join("");
        return "#" + hex;
      }
    },
    "keyword": {
      type: "custom",
      test: str => /^[a-z]+$/i.test(str),
      parse(str) {
        str = str.toLowerCase();
        let ret = {
          spaceId: "srgb",
          coords: null,
          alpha: 1
        };
        if (str === "transparent") {
          ret.coords = KEYWORDS.black;
          ret.alpha = 0;
        } else {
          ret.coords = KEYWORDS[str];
        }
        if (ret.coords) {
          return ret;
        }
      }
    }
  }
});

var P3 = new RGBColorSpace({
  id: "p3",
  cssId: "display-p3",
  name: "P3",
  base: P3Linear,
  // Gamma encoding/decoding is the same as sRGB
  fromBase: sRGB.fromBase,
  toBase: sRGB.toBase
});

// Default space for CSS output. Code in Color.js makes this wider if there's a DOM available
defaults.display_space = sRGB;
let supportsNone;
if (typeof CSS !== "undefined" && CSS.supports) {
  // Find widest supported color space for CSS
  for (let space of [lab, REC2020, P3]) {
    let coords = space.getMinCoords();
    let color = {
      space,
      coords,
      alpha: 1
    };
    let str = serialize(color);
    if (CSS.supports("color", str)) {
      defaults.display_space = space;
      break;
    }
  }
}

/**
 * Returns a serialization of the color that can actually be displayed in the browser.
 * If the default serialization can be displayed, it is returned.
 * Otherwise, the color is converted to Lab, REC2020, or P3, whichever is the widest supported.
 * In Node.js, this is basically equivalent to `serialize()` but returns a `String` object instead.
 *
 * @export
 * @param {{space, coords} | Color | string} color
 * @param {*} [options={}] Options to be passed to serialize()
 * @param {ColorSpace | string} [options.space = defaults.display_space] Color space to use for serialization if default is not supported
 * @returns {String} String object containing the serialized color with a color property containing the converted color (or the original, if no conversion was necessary)
 */
function display(color) {
  let {
    space = defaults.display_space,
    ...options
  } = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : {};
  let ret = serialize(color, options);
  if (typeof CSS === "undefined" || CSS.supports("color", ret) || !defaults.display_space) {
    ret = new String(ret);
    ret.color = color;
  } else {
    // If we're here, what we were about to output is not supported
    let fallbackColor = color;

    // First, check if the culprit is none values
    let hasNone = color.coords.some(isNone) || isNone(color.alpha);
    if (hasNone) {
      var _supportsNone;
      // Does the browser support none values?
      if (!((_supportsNone = supportsNone) !== null && _supportsNone !== void 0 ? _supportsNone : supportsNone = CSS.supports("color", "hsl(none 50% 50%)"))) {
        // Nope, try again without none
        fallbackColor = clone(color);
        fallbackColor.coords = fallbackColor.coords.map(skipNone);
        fallbackColor.alpha = skipNone(fallbackColor.alpha);
        ret = serialize(fallbackColor, options);
        if (CSS.supports("color", ret)) {
          // We're done, now it's supported
          ret = new String(ret);
          ret.color = fallbackColor;
          return ret;
        }
      }
    }

    // If we're here, the color function is not supported
    // Fall back to fallback space
    fallbackColor = to(fallbackColor, space);
    ret = new String(serialize(fallbackColor, options));
    ret.color = fallbackColor;
  }
  return ret;
}

function equals(color1, color2) {
  color1 = getColor(color1);
  color2 = getColor(color2);
  return color1.space === color2.space && color1.alpha === color2.alpha && color1.coords.every((c, i) => c === color2.coords[i]);
}

/**
 * Relative luminance
 */
function getLuminance(color) {
  // Assume getColor() is called on color in get()
  return get(color, [xyz_d65, "y"]);
}
function setLuminance(color, value) {
  // Assume getColor() is called on color in set()
  set(color, [xyz_d65, "y"], value);
}
function register$2(Color) {
  Object.defineProperty(Color.prototype, "luminance", {
    get() {
      return getLuminance(this);
    },
    set(value) {
      setLuminance(this, value);
    }
  });
}

var luminance = /*#__PURE__*/Object.freeze({
	__proto__: null,
	getLuminance: getLuminance,
	register: register$2,
	setLuminance: setLuminance
});

// WCAG 2.0 contrast https://www.w3.org/TR/WCAG20-TECHS/G18.html
// Simple contrast, with fixed 5% viewing flare contribution
// Symmetric, does not matter which is foreground and which is background

function contrastWCAG21(color1, color2) {
  color1 = getColor(color1);
  color2 = getColor(color2);
  let Y1 = Math.max(getLuminance(color1), 0);
  let Y2 = Math.max(getLuminance(color2), 0);
  if (Y2 > Y1) {
    [Y1, Y2] = [Y2, Y1];
  }
  return (Y1 + .05) / (Y2 + .05);
}

// APCA 0.0.98G
// https://github.com/Myndex/apca-w3
// see also https://github.com/w3c/silver/issues/643


// exponents
const normBG = 0.56;
const normTXT = 0.57;
const revTXT = 0.62;
const revBG = 0.65;

// clamps
const blkThrs = 0.022;
const blkClmp = 1.414;
const loClip = 0.1;
const deltaYmin = 0.0005;

// scalers
// see https://github.com/w3c/silver/issues/645
const scaleBoW = 1.14;
const loBoWoffset = 0.027;
const scaleWoB = 1.14;
function fclamp(Y) {
  if (Y >= blkThrs) {
    return Y;
  }
  return Y + (blkThrs - Y) ** blkClmp;
}
function linearize(val) {
  let sign = val < 0 ? -1 : 1;
  let abs = Math.abs(val);
  return sign * Math.pow(abs, 2.4);
}

// Not symmetric, requires a foreground (text) color, and a background color
function contrastAPCA(background, foreground) {
  foreground = getColor(foreground);
  background = getColor(background);
  let S;
  let C;
  let Sapc;

  // Myndex as-published, assumes sRGB inputs
  let R, G, B;
  foreground = to(foreground, "srgb");
  // Should these be clamped to in-gamut values?

  // Calculates "screen luminance" with non-standard simple gamma EOTF
  // weights should be from CSS Color 4, not the ones here which are via Myndex and copied from Lindbloom
  [R, G, B] = foreground.coords;
  let lumTxt = linearize(R) * 0.2126729 + linearize(G) * 0.7151522 + linearize(B) * 0.0721750;
  background = to(background, "srgb");
  [R, G, B] = background.coords;
  let lumBg = linearize(R) * 0.2126729 + linearize(G) * 0.7151522 + linearize(B) * 0.0721750;

  // toe clamping of very dark values to account for flare
  let Ytxt = fclamp(lumTxt);
  let Ybg = fclamp(lumBg);

  // are we "Black on White" (dark on light), or light on dark?
  let BoW = Ybg > Ytxt;

  // why is this a delta, when Y is not perceptually uniform?
  // Answer: it is a noise gate, see
  // https://github.com/LeaVerou/color.js/issues/208
  if (Math.abs(Ybg - Ytxt) < deltaYmin) {
    C = 0;
  } else {
    if (BoW) {
      // dark text on light background
      S = Ybg ** normBG - Ytxt ** normTXT;
      C = S * scaleBoW;
    } else {
      // light text on dark background
      S = Ybg ** revBG - Ytxt ** revTXT;
      C = S * scaleWoB;
    }
  }
  if (Math.abs(C) < loClip) {
    Sapc = 0;
  } else if (C > 0) {
    // not clear whether Woffset is loBoWoffset or loWoBoffset
    // but they have the same value
    Sapc = C - loBoWoffset;
  } else {
    Sapc = C + loBoWoffset;
  }
  return Sapc * 100;
}

// Michelson  luminance contrast
// the relation between the spread and the sum of the two luminances
// Symmetric, does not matter which is foreground and which is background
// No black level compensation for flare.

function contrastMichelson(color1, color2) {
  color1 = getColor(color1);
  color2 = getColor(color2);
  let Y1 = Math.max(getLuminance(color1), 0);
  let Y2 = Math.max(getLuminance(color2), 0);
  if (Y2 > Y1) {
    [Y1, Y2] = [Y2, Y1];
  }
  let denom = Y1 + Y2;
  return denom === 0 ? 0 : (Y1 - Y2) / denom;
}

// Weber luminance contrast
// The difference between the two luminances divided by the lower luminance
// Symmetric, does not matter which is foreground and which is background
// No black level compensation for flare.


// the darkest sRGB color above black is #000001 and this produces
// a plain Weber contrast of ~45647.
// So, setting the divide-by-zero result at 50000 is a reasonable
// max clamp for the plain Weber
const max = 50000;
function contrastWeber(color1, color2) {
  color1 = getColor(color1);
  color2 = getColor(color2);
  let Y1 = Math.max(getLuminance(color1), 0);
  let Y2 = Math.max(getLuminance(color2), 0);
  if (Y2 > Y1) {
    [Y1, Y2] = [Y2, Y1];
  }
  return Y2 === 0 ? max : (Y1 - Y2) / Y2;
}

// CIE Lightness difference, as used by Google Material Design
// Google HCT Tone is the same as CIE Lightness
// https://material.io/blog/science-of-color-design

function contrastLstar(color1, color2) {
  color1 = getColor(color1);
  color2 = getColor(color2);
  let L1 = get(color1, [lab, "l"]);
  let L2 = get(color2, [lab, "l"]);
  return Math.abs(L1 - L2);
}

// κ * ε  = 2^3 = 8
const ε$3 = 216 / 24389; // 6^3/29^3 == (24/116)^3
const ε3 = 24 / 116;
const κ$2 = 24389 / 27; // 29^3/3^3

let white$1 = WHITES.D65;
var lab_d65 = new ColorSpace({
  id: "lab-d65",
  name: "Lab D65",
  coords: {
    l: {
      refRange: [0, 100],
      name: "Lightness"
    },
    a: {
      refRange: [-125, 125]
    },
    b: {
      refRange: [-125, 125]
    }
  },
  // Assuming XYZ is relative to D65, convert to CIE Lab
  // from CIE standard, which now defines these as a rational fraction
  white: white$1,
  base: xyz_d65,
  // Convert D65-adapted XYZ to Lab
  //  CIE 15.3:2004 section 8.2.1.1
  fromBase(XYZ) {
    // compute xyz, which is XYZ scaled relative to reference white
    let xyz = XYZ.map((value, i) => value / white$1[i]);

    // now compute f
    let f = xyz.map(value => value > ε$3 ? Math.cbrt(value) : (κ$2 * value + 16) / 116);
    return [116 * f[1] - 16,
    // L
    500 * (f[0] - f[1]),
    // a
    200 * (f[1] - f[2]) // b
    ];
  },
  // Convert Lab to D65-adapted XYZ
  // Same result as CIE 15.3:2004 Appendix D although the derivation is different
  // http://www.brucelindbloom.com/index.html?Eqn_RGB_XYZ_Matrix.html
  toBase(Lab) {
    // compute f, starting with the luminance-related term
    let f = [];
    f[1] = (Lab[0] + 16) / 116;
    f[0] = Lab[1] / 500 + f[1];
    f[2] = f[1] - Lab[2] / 200;

    // compute xyz
    let xyz = [f[0] > ε3 ? Math.pow(f[0], 3) : (116 * f[0] - 16) / κ$2, Lab[0] > 8 ? Math.pow((Lab[0] + 16) / 116, 3) : Lab[0] / κ$2, f[2] > ε3 ? Math.pow(f[2], 3) : (116 * f[2] - 16) / κ$2];

    // Compute XYZ by scaling xyz by reference white
    return xyz.map((value, i) => value * white$1[i]);
  },
  formats: {
    "lab-d65": {
      coords: ["<number> | <percentage>", "<number> | <percentage>[-1,1]", "<number> | <percentage>[-1,1]"]
    }
  }
});

// Delta Phi Star perceptual lightness contrast
// See https://github.com/Myndex/deltaphistar
// The (difference between two Lstars each raised to phi) raised to (1/phi)
// Symmetric, does not matter which is foreground and which is background

const phi = Math.pow(5, 0.5) * 0.5 + 0.5; // Math.phi can be used if Math.js

function contrastDeltaPhi(color1, color2) {
  color1 = getColor(color1);
  color2 = getColor(color2);
  let Lstr1 = get(color1, [lab_d65, "l"]);
  let Lstr2 = get(color2, [lab_d65, "l"]);
  let deltaPhiStar = Math.abs(Math.pow(Lstr1, phi) - Math.pow(Lstr2, phi));
  let contrast = Math.pow(deltaPhiStar, 1 / phi) * Math.SQRT2 - 40;
  return contrast < 7.5 ? 0.0 : contrast;
}

var contrastMethods = /*#__PURE__*/Object.freeze({
	__proto__: null,
	contrastAPCA: contrastAPCA,
	contrastDeltaPhi: contrastDeltaPhi,
	contrastLstar: contrastLstar,
	contrastMichelson: contrastMichelson,
	contrastWCAG21: contrastWCAG21,
	contrastWeber: contrastWeber
});

function contrast(background, foreground) {
  let o = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};
  if (isString(o)) {
    o = {
      algorithm: o
    };
  }
  let {
    algorithm,
    ...rest
  } = o;
  if (!algorithm) {
    let algorithms = Object.keys(contrastMethods).map(a => a.replace(/^contrast/, "")).join(", ");
    throw new TypeError(`contrast() function needs a contrast algorithm. Please specify one of: ${algorithms}`);
  }
  background = getColor(background);
  foreground = getColor(foreground);
  for (let a in contrastMethods) {
    if ("contrast" + algorithm.toLowerCase() === a.toLowerCase()) {
      return contrastMethods[a](background, foreground, rest);
    }
  }
  throw new TypeError(`Unknown contrast algorithm: ${algorithm}`);
}

// Chromaticity coordinates
function uv(color) {
  // Assumes getAll() calls getColor() on color
  let [X, Y, Z] = getAll(color, xyz_d65);
  let denom = X + 15 * Y + 3 * Z;
  return [4 * X / denom, 9 * Y / denom];
}
function xy(color) {
  // Assumes getAll() calls getColor() on color
  let [X, Y, Z] = getAll(color, xyz_d65);
  let sum = X + Y + Z;
  return [X / sum, Y / sum];
}
function register$1(Color) {
  // no setters, as lightness information is lost
  // when converting color to chromaticity
  Object.defineProperty(Color.prototype, "uv", {
    get() {
      return uv(this);
    }
  });
  Object.defineProperty(Color.prototype, "xy", {
    get() {
      return xy(this);
    }
  });
}

var chromaticity = /*#__PURE__*/Object.freeze({
	__proto__: null,
	register: register$1,
	uv: uv,
	xy: xy
});

function deltaE(c1, c2) {
  let o = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};
  if (isString(o)) {
    o = {
      method: o
    };
  }
  let {
    method = defaults.deltaE,
    ...rest
  } = o;
  for (let m in deltaEMethods) {
    if ("deltae" + method.toLowerCase() === m.toLowerCase()) {
      return deltaEMethods[m](c1, c2, rest);
    }
  }
  throw new TypeError(`Unknown deltaE method: ${method}`);
}

function lighten(color) {
  let amount = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : .25;
  let space = ColorSpace.get("oklch", "lch");
  let lightness = [space, "l"];
  return set(color, lightness, l => l * (1 + amount));
}
function darken(color) {
  let amount = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : .25;
  let space = ColorSpace.get("oklch", "lch");
  let lightness = [space, "l"];
  return set(color, lightness, l => l * (1 - amount));
}

var variations = /*#__PURE__*/Object.freeze({
	__proto__: null,
	darken: darken,
	lighten: lighten
});

/**
 * Functions related to color interpolation
 */

/**
 * Return an intermediate color between two colors
 * Signatures: mix(c1, c2, p, options)
 *             mix(c1, c2, options)
 *             mix(color)
 * @param {Color | string} c1 The first color
 * @param {Color | string} [c2] The second color
 * @param {number} [p=.5] A 0-1 percentage where 0 is c1 and 1 is c2
 * @param {Object} [o={}]
 * @return {Color}
 */
function mix(c1, c2) {
  let p = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : .5;
  let o = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : {};
  [c1, c2] = [getColor(c1), getColor(c2)];
  if (type(p) === "object") {
    [p, o] = [.5, p];
  }
  let r = range(c1, c2, o);
  return r(p);
}

/**
 *
 * @param {Color | string | Function} c1 The first color or a range
 * @param {Color | string} [c2] The second color if c1 is not a range
 * @param {Object} [options={}]
 * @return {Color[]}
 */
function steps(c1, c2) {
  let options = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};
  let colorRange;
  if (isRange(c1)) {
    // Tweaking existing range
    [colorRange, options] = [c1, c2];
    [c1, c2] = colorRange.rangeArgs.colors;
  }
  let {
    maxDeltaE,
    deltaEMethod,
    steps = 2,
    maxSteps = 1000,
    ...rangeOptions
  } = options;
  if (!colorRange) {
    [c1, c2] = [getColor(c1), getColor(c2)];
    colorRange = range(c1, c2, rangeOptions);
  }
  let totalDelta = deltaE(c1, c2);
  let actualSteps = maxDeltaE > 0 ? Math.max(steps, Math.ceil(totalDelta / maxDeltaE) + 1) : steps;
  let ret = [];
  if (maxSteps !== undefined) {
    actualSteps = Math.min(actualSteps, maxSteps);
  }
  if (actualSteps === 1) {
    ret = [{
      p: .5,
      color: colorRange(.5)
    }];
  } else {
    let step = 1 / (actualSteps - 1);
    ret = Array.from({
      length: actualSteps
    }, (_, i) => {
      let p = i * step;
      return {
        p,
        color: colorRange(p)
      };
    });
  }
  if (maxDeltaE > 0) {
    // Iterate over all stops and find max deltaE
    let maxDelta = ret.reduce((acc, cur, i) => {
      if (i === 0) {
        return 0;
      }
      let ΔΕ = deltaE(cur.color, ret[i - 1].color, deltaEMethod);
      return Math.max(acc, ΔΕ);
    }, 0);
    while (maxDelta > maxDeltaE) {
      // Insert intermediate stops and measure maxDelta again
      // We need to do this for all pairs, otherwise the midpoint shifts
      maxDelta = 0;
      for (let i = 1; i < ret.length && ret.length < maxSteps; i++) {
        let prev = ret[i - 1];
        let cur = ret[i];
        let p = (cur.p + prev.p) / 2;
        let color = colorRange(p);
        maxDelta = Math.max(maxDelta, deltaE(color, prev.color), deltaE(color, cur.color));
        ret.splice(i, 0, {
          p,
          color: colorRange(p)
        });
        i++;
      }
    }
  }
  ret = ret.map(a => a.color);
  return ret;
}

/**
 * Interpolate to color2 and return a function that takes a 0-1 percentage
 * @param {Color | string | Function} color1 The first color or an existing range
 * @param {Color | string} [color2] If color1 is a color, this is the second color
 * @param {Object} [options={}]
 * @returns {Function} A function that takes a 0-1 percentage and returns a color
 */
function range(color1, color2) {
  let options = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : {};
  if (isRange(color1)) {
    // Tweaking existing range
    let [r, options] = [color1, color2];
    return range(...r.rangeArgs.colors, {
      ...r.rangeArgs.options,
      ...options
    });
  }
  let {
    space,
    outputSpace,
    progression,
    premultiplied
  } = options;
  color1 = getColor(color1);
  color2 = getColor(color2);

  // Make sure we're working on copies of these colors
  color1 = clone(color1);
  color2 = clone(color2);
  let rangeArgs = {
    colors: [color1, color2],
    options
  };
  if (space) {
    space = ColorSpace.get(space);
  } else {
    space = ColorSpace.registry[defaults.interpolationSpace] || color1.space;
  }
  outputSpace = outputSpace ? ColorSpace.get(outputSpace) : space;
  color1 = to(color1, space);
  color2 = to(color2, space);

  // Gamut map to avoid areas of flat color
  color1 = toGamut(color1);
  color2 = toGamut(color2);

  // Handle hue interpolation
  // See https://github.com/w3c/csswg-drafts/issues/4735#issuecomment-635741840
  if (space.coords.h && space.coords.h.type === "angle") {
    let arc = options.hue = options.hue || "shorter";
    let hue = [space, "h"];
    let [θ1, θ2] = [get(color1, hue), get(color2, hue)];
    // Undefined hues must be evaluated before hue fix-up to properly
    // calculate hue arcs between undefined and defined hues.
    // See https://github.com/w3c/csswg-drafts/issues/9436#issuecomment-1746957545
    if (isNaN(θ1) && !isNaN(θ2)) {
      θ1 = θ2;
    } else if (isNaN(θ2) && !isNaN(θ1)) {
      θ2 = θ1;
    }
    [θ1, θ2] = adjust(arc, [θ1, θ2]);
    set(color1, hue, θ1);
    set(color2, hue, θ2);
  }
  if (premultiplied) {
    // not coping with polar spaces yet
    color1.coords = color1.coords.map(c => c * color1.alpha);
    color2.coords = color2.coords.map(c => c * color2.alpha);
  }
  return Object.assign(p => {
    p = progression ? progression(p) : p;
    let coords = color1.coords.map((start, i) => {
      let end = color2.coords[i];
      return interpolate(start, end, p);
    });
    let alpha = interpolate(color1.alpha, color2.alpha, p);
    let ret = {
      space,
      coords,
      alpha
    };
    if (premultiplied) {
      // undo premultiplication
      ret.coords = ret.coords.map(c => c / alpha);
    }
    if (outputSpace !== space) {
      ret = to(ret, outputSpace);
    }
    return ret;
  }, {
    rangeArgs
  });
}
function isRange(val) {
  return type(val) === "function" && !!val.rangeArgs;
}
defaults.interpolationSpace = "lab";
function register(Color) {
  Color.defineFunction("mix", mix, {
    returns: "color"
  });
  Color.defineFunction("range", range, {
    returns: "function<color>"
  });
  Color.defineFunction("steps", steps, {
    returns: "array<color>"
  });
}

var interpolation = /*#__PURE__*/Object.freeze({
	__proto__: null,
	isRange: isRange,
	mix: mix,
	range: range,
	register: register,
	steps: steps
});

var HSL = new ColorSpace({
  id: "hsl",
  name: "HSL",
  coords: {
    h: {
      refRange: [0, 360],
      type: "angle",
      name: "Hue"
    },
    s: {
      range: [0, 100],
      name: "Saturation"
    },
    l: {
      range: [0, 100],
      name: "Lightness"
    }
  },
  base: sRGB,
  // Adapted from https://drafts.csswg.org/css-color-4/better-rgbToHsl.js
  fromBase: rgb => {
    let max = Math.max(...rgb);
    let min = Math.min(...rgb);
    let [r, g, b] = rgb;
    let [h, s, l] = [NaN, 0, (min + max) / 2];
    let d = max - min;
    if (d !== 0) {
      s = l === 0 || l === 1 ? 0 : (max - l) / Math.min(l, 1 - l);
      switch (max) {
        case r:
          h = (g - b) / d + (g < b ? 6 : 0);
          break;
        case g:
          h = (b - r) / d + 2;
          break;
        case b:
          h = (r - g) / d + 4;
      }
      h = h * 60;
    }

    // Very out of gamut colors can produce negative saturation
    // If so, just rotate the hue by 180 and use a positive saturation
    // see https://github.com/w3c/csswg-drafts/issues/9222
    if (s < 0) {
      h += 180;
      s = Math.abs(s);
    }
    if (h >= 360) {
      h -= 360;
    }
    return [h, s * 100, l * 100];
  },
  // Adapted from https://en.wikipedia.org/wiki/HSL_and_HSV#HSL_to_RGB_alternative
  toBase: hsl => {
    let [h, s, l] = hsl;
    h = h % 360;
    if (h < 0) {
      h += 360;
    }
    s /= 100;
    l /= 100;
    function f(n) {
      let k = (n + h / 30) % 12;
      let a = s * Math.min(l, 1 - l);
      return l - a * Math.max(-1, Math.min(k - 3, 9 - k, 1));
    }
    return [f(0), f(8), f(4)];
  },
  formats: {
    "hsl": {
      coords: ["<number> | <angle>", "<percentage>", "<percentage>"]
    },
    "hsla": {
      coords: ["<number> | <angle>", "<percentage>", "<percentage>"],
      commas: true,
      lastAlpha: true
    }
  }
});

// The Hue, Whiteness Blackness (HWB) colorspace
// See https://drafts.csswg.org/css-color-4/#the-hwb-notation
// Note that, like HSL, calculations are done directly on
// gamma-corrected sRGB values rather than linearising them first.

var HSV = new ColorSpace({
  id: "hsv",
  name: "HSV",
  coords: {
    h: {
      refRange: [0, 360],
      type: "angle",
      name: "Hue"
    },
    s: {
      range: [0, 100],
      name: "Saturation"
    },
    v: {
      range: [0, 100],
      name: "Value"
    }
  },
  base: HSL,
  // https://en.wikipedia.org/wiki/HSL_and_HSV#Interconversion
  fromBase(hsl) {
    let [h, s, l] = hsl;
    s /= 100;
    l /= 100;
    let v = l + s * Math.min(l, 1 - l);
    return [h,
    // h is the same
    v === 0 ? 0 : 200 * (1 - l / v),
    // s
    100 * v];
  },
  // https://en.wikipedia.org/wiki/HSL_and_HSV#Interconversion
  toBase(hsv) {
    let [h, s, v] = hsv;
    s /= 100;
    v /= 100;
    let l = v * (1 - s / 2);
    return [h,
    // h is the same
    l === 0 || l === 1 ? 0 : (v - l) / Math.min(l, 1 - l) * 100, l * 100];
  },
  formats: {
    color: {
      id: "--hsv",
      coords: ["<number> | <angle>", "<percentage> | <number>", "<percentage> | <number>"]
    }
  }
});

// The Hue, Whiteness Blackness (HWB) colorspace
// See https://drafts.csswg.org/css-color-4/#the-hwb-notation
// Note that, like HSL, calculations are done directly on
// gamma-corrected sRGB values rather than linearising them first.

var hwb = new ColorSpace({
  id: "hwb",
  name: "HWB",
  coords: {
    h: {
      refRange: [0, 360],
      type: "angle",
      name: "Hue"
    },
    w: {
      range: [0, 100],
      name: "Whiteness"
    },
    b: {
      range: [0, 100],
      name: "Blackness"
    }
  },
  base: HSV,
  fromBase(hsv) {
    let [h, s, v] = hsv;
    return [h, v * (100 - s) / 100, 100 - v];
  },
  toBase(hwb) {
    let [h, w, b] = hwb;

    // Now convert percentages to [0..1]
    w /= 100;
    b /= 100;

    // Achromatic check (white plus black >= 1)
    let sum = w + b;
    if (sum >= 1) {
      let gray = w / sum;
      return [h, 0, gray * 100];
    }
    let v = 1 - b;
    let s = v === 0 ? 0 : 1 - w / v;
    return [h, s * 100, v * 100];
  },
  formats: {
    "hwb": {
      coords: ["<number> | <angle>", "<percentage> | <number>", "<percentage> | <number>"]
    }
  }
});

// convert an array of linear-light a98-rgb values to CIE XYZ
// http://www.brucelindbloom.com/index.html?Eqn_RGB_XYZ_Matrix.html
// has greater numerical precision than section 4.3.5.3 of
// https://www.adobe.com/digitalimag/pdfs/AdobeRGB1998.pdf
// but the values below were calculated from first principles
// from the chromaticity coordinates of R G B W
const toXYZ_M$2 = [[0.5766690429101305, 0.1855582379065463, 0.1882286462349947], [0.29734497525053605, 0.6273635662554661, 0.07529145849399788], [0.02703136138641234, 0.07068885253582723, 0.9913375368376388]];
const fromXYZ_M$2 = [[2.0415879038107465, -0.5650069742788596, -0.34473135077832956], [-0.9692436362808795, 1.8759675015077202, 0.04155505740717557], [0.013444280632031142, -0.11836239223101838, 1.0151749943912054]];
var A98Linear = new RGBColorSpace({
  id: "a98rgb-linear",
  cssId: "--a98-rgb-linear",
  name: "Linear Adobe® 98 RGB compatible",
  white: "D65",
  toXYZ_M: toXYZ_M$2,
  fromXYZ_M: fromXYZ_M$2
});

var a98rgb = new RGBColorSpace({
  id: "a98rgb",
  cssId: "a98-rgb",
  name: "Adobe® 98 RGB compatible",
  base: A98Linear,
  toBase: RGB => RGB.map(val => Math.pow(Math.abs(val), 563 / 256) * Math.sign(val)),
  fromBase: RGB => RGB.map(val => Math.pow(Math.abs(val), 256 / 563) * Math.sign(val))
});

// convert an array of  prophoto-rgb values to CIE XYZ
// using  D50 (so no chromatic adaptation needed afterwards)
// matrix cannot be expressed in rational form, but is calculated to 64 bit accuracy
// see https://github.com/w3c/csswg-drafts/issues/7675
const toXYZ_M$1 = [[0.79776664490064230, 0.13518129740053308, 0.03134773412839220], [0.28807482881940130, 0.71183523424187300, 0.00008993693872564], [0.00000000000000000, 0.00000000000000000, 0.82510460251046020]];
const fromXYZ_M$1 = [[1.34578688164715830, -0.25557208737979464, -0.05110186497554526], [-0.54463070512490190, 1.50824774284514680, 0.02052744743642139], [0.00000000000000000, 0.00000000000000000, 1.21196754563894520]];
var ProPhotoLinear = new RGBColorSpace({
  id: "prophoto-linear",
  cssId: "--prophoto-rgb-linear",
  name: "Linear ProPhoto",
  white: "D50",
  base: XYZ_D50,
  toXYZ_M: toXYZ_M$1,
  fromXYZ_M: fromXYZ_M$1
});

const Et = 1 / 512;
const Et2 = 16 / 512;
var prophoto = new RGBColorSpace({
  id: "prophoto",
  cssId: "prophoto-rgb",
  name: "ProPhoto",
  base: ProPhotoLinear,
  toBase(RGB) {
    // Transfer curve is gamma 1.8 with a small linear portion
    return RGB.map(v => v < Et2 ? v / 16 : v ** 1.8);
  },
  fromBase(RGB) {
    return RGB.map(v => v >= Et ? v ** (1 / 1.8) : 16 * v);
  }
});

var oklch = new ColorSpace({
  id: "oklch",
  name: "Oklch",
  coords: {
    l: {
      refRange: [0, 1],
      name: "Lightness"
    },
    c: {
      refRange: [0, 0.4],
      name: "Chroma"
    },
    h: {
      refRange: [0, 360],
      type: "angle",
      name: "Hue"
    }
  },
  white: "D65",
  base: OKLab,
  fromBase(oklab) {
    // Convert to polar form
    let [L, a, b] = oklab;
    let h;
    const ε = 0.0002; // chromatic components much smaller than a,b

    if (Math.abs(a) < ε && Math.abs(b) < ε) {
      h = NaN;
    } else {
      h = Math.atan2(b, a) * 180 / Math.PI;
    }
    return [L,
    // OKLab L is still L
    Math.sqrt(a ** 2 + b ** 2),
    // Chroma
    constrain(h) // Hue, in degrees [0 to 360)
    ];
  },
  // Convert from polar form
  toBase(oklch) {
    let [L, C, h] = oklch;
    let a, b;

    // check for NaN hue
    if (isNaN(h)) {
      a = 0;
      b = 0;
    } else {
      a = C * Math.cos(h * Math.PI / 180);
      b = C * Math.sin(h * Math.PI / 180);
    }
    return [L, a, b];
  },
  formats: {
    "oklch": {
      coords: ["<percentage> | <number>", "<number> | <percentage>[0,1]", "<number> | <angle>"]
    }
  }
});

let white = WHITES.D65;
const ε$2 = 216 / 24389; // 6^3/29^3 == (24/116)^3
const κ$1 = 24389 / 27; // 29^3/3^3
const [U_PRIME_WHITE, V_PRIME_WHITE] = uv({
  space: xyz_d65,
  coords: white
});
var Luv = new ColorSpace({
  id: "luv",
  name: "Luv",
  coords: {
    l: {
      refRange: [0, 100],
      name: "Lightness"
    },
    // Reference ranges from https://facelessuser.github.io/coloraide/colors/luv/
    u: {
      refRange: [-215, 215]
    },
    v: {
      refRange: [-215, 215]
    }
  },
  white: white,
  base: xyz_d65,
  // Convert D65-adapted XYZ to Luv
  // https://en.wikipedia.org/wiki/CIELUV#The_forward_transformation
  fromBase(XYZ) {
    let xyz = [skipNone(XYZ[0]), skipNone(XYZ[1]), skipNone(XYZ[2])];
    let y = xyz[1];
    let [up, vp] = uv({
      space: xyz_d65,
      coords: xyz
    });

    // Protect against XYZ of [0, 0, 0]
    if (!Number.isFinite(up) || !Number.isFinite(vp)) {
      return [0, 0, 0];
    }
    let L = y <= ε$2 ? κ$1 * y : 116 * Math.cbrt(y) - 16;
    return [L, 13 * L * (up - U_PRIME_WHITE), 13 * L * (vp - V_PRIME_WHITE)];
  },
  // Convert Luv to D65-adapted XYZ
  // https://en.wikipedia.org/wiki/CIELUV#The_reverse_transformation
  toBase(Luv) {
    let [L, u, v] = Luv;

    // Protect against division by zero and NaN Lightness
    if (L === 0 || isNone(L)) {
      return [0, 0, 0];
    }
    u = skipNone(u);
    v = skipNone(v);
    let up = u / (13 * L) + U_PRIME_WHITE;
    let vp = v / (13 * L) + V_PRIME_WHITE;
    let y = L <= 8 ? L / κ$1 : Math.pow((L + 16) / 116, 3);
    return [y * (9 * up / (4 * vp)), y, y * ((12 - 3 * up - 20 * vp) / (4 * vp))];
  },
  formats: {
    color: {
      id: "--luv",
      coords: ["<number> | <percentage>", "<number> | <percentage>[-1,1]", "<number> | <percentage>[-1,1]"]
    }
  }
});

var LCHuv = new ColorSpace({
  id: "lchuv",
  name: "LChuv",
  coords: {
    l: {
      refRange: [0, 100],
      name: "Lightness"
    },
    c: {
      refRange: [0, 220],
      name: "Chroma"
    },
    h: {
      refRange: [0, 360],
      type: "angle",
      name: "Hue"
    }
  },
  base: Luv,
  fromBase(Luv) {
    // Convert to polar form
    let [L, u, v] = Luv;
    let hue;
    const ε = 0.02;
    if (Math.abs(u) < ε && Math.abs(v) < ε) {
      hue = NaN;
    } else {
      hue = Math.atan2(v, u) * 180 / Math.PI;
    }
    return [L,
    // L is still L
    Math.sqrt(u ** 2 + v ** 2),
    // Chroma
    constrain(hue) // Hue, in degrees [0 to 360)
    ];
  },
  toBase(LCH) {
    // Convert from polar form
    let [Lightness, Chroma, Hue] = LCH;
    // Clamp any negative Chroma
    if (Chroma < 0) {
      Chroma = 0;
    }
    // Deal with NaN Hue
    if (isNaN(Hue)) {
      Hue = 0;
    }
    return [Lightness,
    // L is still L
    Chroma * Math.cos(Hue * Math.PI / 180),
    // u
    Chroma * Math.sin(Hue * Math.PI / 180) // v
    ];
  },
  formats: {
    color: {
      id: "--lchuv",
      coords: ["<number> | <percentage>", "<number> | <percentage>", "<number> | <angle>"]
    }
  }
});

/*
Adapted from: https://github.com/hsluv/hsluv-javascript/blob/14b49e6cf9a9137916096b8487a5372626b57ba4/src/hsluv.ts

Copyright (c) 2012-2022 Alexei Boronine

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

const ε$1 = 216 / 24389; // 6^3/29^3 == (24/116)^3
const κ = 24389 / 27; // 29^3/3^3

const m_r0 = fromXYZ_M$3[0][0];
const m_r1 = fromXYZ_M$3[0][1];
const m_r2 = fromXYZ_M$3[0][2];
const m_g0 = fromXYZ_M$3[1][0];
const m_g1 = fromXYZ_M$3[1][1];
const m_g2 = fromXYZ_M$3[1][2];
const m_b0 = fromXYZ_M$3[2][0];
const m_b1 = fromXYZ_M$3[2][1];
const m_b2 = fromXYZ_M$3[2][2];
function distanceFromOriginAngle(slope, intercept, angle) {
  const d = intercept / (Math.sin(angle) - slope * Math.cos(angle));
  return d < 0 ? Infinity : d;
}
function calculateBoundingLines(l) {
  const sub1 = Math.pow(l + 16, 3) / 1560896;
  const sub2 = sub1 > ε$1 ? sub1 : l / κ;
  const s1r = sub2 * (284517 * m_r0 - 94839 * m_r2);
  const s2r = sub2 * (838422 * m_r2 + 769860 * m_r1 + 731718 * m_r0);
  const s3r = sub2 * (632260 * m_r2 - 126452 * m_r1);
  const s1g = sub2 * (284517 * m_g0 - 94839 * m_g2);
  const s2g = sub2 * (838422 * m_g2 + 769860 * m_g1 + 731718 * m_g0);
  const s3g = sub2 * (632260 * m_g2 - 126452 * m_g1);
  const s1b = sub2 * (284517 * m_b0 - 94839 * m_b2);
  const s2b = sub2 * (838422 * m_b2 + 769860 * m_b1 + 731718 * m_b0);
  const s3b = sub2 * (632260 * m_b2 - 126452 * m_b1);
  return {
    r0s: s1r / s3r,
    r0i: s2r * l / s3r,
    r1s: s1r / (s3r + 126452),
    r1i: (s2r - 769860) * l / (s3r + 126452),
    g0s: s1g / s3g,
    g0i: s2g * l / s3g,
    g1s: s1g / (s3g + 126452),
    g1i: (s2g - 769860) * l / (s3g + 126452),
    b0s: s1b / s3b,
    b0i: s2b * l / s3b,
    b1s: s1b / (s3b + 126452),
    b1i: (s2b - 769860) * l / (s3b + 126452)
  };
}
function calcMaxChromaHsluv(lines, h) {
  const hueRad = h / 360 * Math.PI * 2;
  const r0 = distanceFromOriginAngle(lines.r0s, lines.r0i, hueRad);
  const r1 = distanceFromOriginAngle(lines.r1s, lines.r1i, hueRad);
  const g0 = distanceFromOriginAngle(lines.g0s, lines.g0i, hueRad);
  const g1 = distanceFromOriginAngle(lines.g1s, lines.g1i, hueRad);
  const b0 = distanceFromOriginAngle(lines.b0s, lines.b0i, hueRad);
  const b1 = distanceFromOriginAngle(lines.b1s, lines.b1i, hueRad);
  return Math.min(r0, r1, g0, g1, b0, b1);
}
var hsluv = new ColorSpace({
  id: "hsluv",
  name: "HSLuv",
  coords: {
    h: {
      refRange: [0, 360],
      type: "angle",
      name: "Hue"
    },
    s: {
      range: [0, 100],
      name: "Saturation"
    },
    l: {
      range: [0, 100],
      name: "Lightness"
    }
  },
  base: LCHuv,
  gamutSpace: sRGB,
  // Convert LCHuv to HSLuv
  fromBase(lch) {
    let [l, c, h] = [skipNone(lch[0]), skipNone(lch[1]), skipNone(lch[2])];
    let s;
    if (l > 99.9999999) {
      s = 0;
      l = 100;
    } else if (l < 0.00000001) {
      s = 0;
      l = 0;
    } else {
      let lines = calculateBoundingLines(l);
      let max = calcMaxChromaHsluv(lines, h);
      s = c / max * 100;
    }
    return [h, s, l];
  },
  // Convert HSLuv to LCHuv
  toBase(hsl) {
    let [h, s, l] = [skipNone(hsl[0]), skipNone(hsl[1]), skipNone(hsl[2])];
    let c;
    if (l > 99.9999999) {
      l = 100;
      c = 0;
    } else if (l < 0.00000001) {
      l = 0;
      c = 0;
    } else {
      let lines = calculateBoundingLines(l);
      let max = calcMaxChromaHsluv(lines, h);
      c = max / 100 * s;
    }
    return [l, c, h];
  },
  formats: {
    color: {
      id: "--hsluv",
      coords: ["<number> | <angle>", "<percentage> | <number>", "<percentage> | <number>"]
    }
  }
});

/*
Adapted from: https://github.com/hsluv/hsluv-javascript/blob/14b49e6cf9a9137916096b8487a5372626b57ba4/src/hsluv.ts

Copyright (c) 2012-2022 Alexei Boronine

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/


fromXYZ_M$3[0][0];
fromXYZ_M$3[0][1];
fromXYZ_M$3[0][2];
fromXYZ_M$3[1][0];
fromXYZ_M$3[1][1];
fromXYZ_M$3[1][2];
fromXYZ_M$3[2][0];
fromXYZ_M$3[2][1];
fromXYZ_M$3[2][2];
function distanceFromOrigin(slope, intercept) {
  return Math.abs(intercept) / Math.sqrt(Math.pow(slope, 2) + 1);
}
function calcMaxChromaHpluv(lines) {
  let r0 = distanceFromOrigin(lines.r0s, lines.r0i);
  let r1 = distanceFromOrigin(lines.r1s, lines.r1i);
  let g0 = distanceFromOrigin(lines.g0s, lines.g0i);
  let g1 = distanceFromOrigin(lines.g1s, lines.g1i);
  let b0 = distanceFromOrigin(lines.b0s, lines.b0i);
  let b1 = distanceFromOrigin(lines.b1s, lines.b1i);
  return Math.min(r0, r1, g0, g1, b0, b1);
}
var hpluv = new ColorSpace({
  id: "hpluv",
  name: "HPLuv",
  coords: {
    h: {
      refRange: [0, 360],
      type: "angle",
      name: "Hue"
    },
    s: {
      range: [0, 100],
      name: "Saturation"
    },
    l: {
      range: [0, 100],
      name: "Lightness"
    }
  },
  base: LCHuv,
  gamutSpace: "self",
  // Convert LCHuv to HPLuv
  fromBase(lch) {
    let [l, c, h] = [skipNone(lch[0]), skipNone(lch[1]), skipNone(lch[2])];
    let s;
    if (l > 99.9999999) {
      s = 0;
      l = 100;
    } else if (l < 0.00000001) {
      s = 0;
      l = 0;
    } else {
      let lines = calculateBoundingLines(l);
      let max = calcMaxChromaHpluv(lines);
      s = c / max * 100;
    }
    return [h, s, l];
  },
  // Convert HPLuv to LCHuv
  toBase(hsl) {
    let [h, s, l] = [skipNone(hsl[0]), skipNone(hsl[1]), skipNone(hsl[2])];
    let c;
    if (l > 99.9999999) {
      l = 100;
      c = 0;
    } else if (l < 0.00000001) {
      l = 0;
      c = 0;
    } else {
      let lines = calculateBoundingLines(l);
      let max = calcMaxChromaHpluv(lines);
      c = max / 100 * s;
    }
    return [l, c, h];
  },
  formats: {
    color: {
      id: "--hpluv",
      coords: ["<number> | <angle>", "<percentage> | <number>", "<percentage> | <number>"]
    }
  }
});

const Yw = 203; // absolute luminance of media white, cd/m²
const n = 2610 / 2 ** 14;
const ninv = 2 ** 14 / 2610;
const m = 2523 / 2 ** 5;
const minv = 2 ** 5 / 2523;
const c1 = 3424 / 2 ** 12;
const c2 = 2413 / 2 ** 7;
const c3 = 2392 / 2 ** 7;
var rec2100Pq = new RGBColorSpace({
  id: "rec2100pq",
  cssId: "rec2100-pq",
  name: "REC.2100-PQ",
  base: REC2020Linear,
  toBase(RGB) {
    // given PQ encoded component in range [0, 1]
    // return media-white relative linear-light
    return RGB.map(function (val) {
      let x = (Math.max(val ** minv - c1, 0) / (c2 - c3 * val ** minv)) ** ninv;
      return x * 10000 / Yw; // luminance relative to diffuse white, [0, 70 or so].
    });
  },
  fromBase(RGB) {
    // given media-white relative linear-light
    // returnPQ encoded component in range [0, 1]
    return RGB.map(function (val) {
      let x = Math.max(val * Yw / 10000, 0); // absolute luminance of peak white is 10,000 cd/m².
      let num = c1 + c2 * x ** n;
      let denom = 1 + c3 * x ** n;
      return (num / denom) ** m;
    });
  }
});

// FIXME see https://github.com/LeaVerou/color.js/issues/190

const a = 0.17883277;
const b = 0.28466892; // 1 - (4 * a)
const c = 0.55991073; // 0.5 - a * Math.log(4 *a)

const scale = 3.7743; // Place 18% grey at HLG 0.38, so media white at 0.75

var rec2100Hlg = new RGBColorSpace({
  id: "rec2100hlg",
  cssId: "rec2100-hlg",
  name: "REC.2100-HLG",
  referred: "scene",
  base: REC2020Linear,
  toBase(RGB) {
    // given HLG encoded component in range [0, 1]
    // return media-white relative linear-light
    return RGB.map(function (val) {
      // first the HLG EOTF
      // ITU-R BT.2390-10 p.30 section
      // 6.3 The hybrid log-gamma electro-optical transfer function (EOTF)
      // Then scale by 3 so media white is 1.0
      if (val <= 0.5) {
        return val ** 2 / 3 * scale;
      }
      return (Math.exp((val - c) / a) + b) / 12 * scale;
    });
  },
  fromBase(RGB) {
    // given media-white relative linear-light
    // where diffuse white is 1.0,
    // return HLG encoded component in range [0, 1]
    return RGB.map(function (val) {
      // first scale to put linear-light media white at 1/3
      val /= scale;
      // now the HLG OETF
      // ITU-R BT.2390-10 p.23
      // 6.1 The hybrid log-gamma opto-electronic transfer function (OETF)
      if (val <= 1 / 12) {
        return Math.sqrt(3 * val);
      }
      return a * Math.log(12 * val - b) + c;
    });
  }
});

const CATs = {};
hooks.add("chromatic-adaptation-start", env => {
  if (env.options.method) {
    env.M = adapt(env.W1, env.W2, env.options.method);
  }
});
hooks.add("chromatic-adaptation-end", env => {
  if (!env.M) {
    env.M = adapt(env.W1, env.W2, env.options.method);
  }
});
function defineCAT(_ref) {
  let {
    id,
    toCone_M,
    fromCone_M
  } = _ref;
  // Use id, toCone_M, fromCone_M like variables
  CATs[id] = arguments[0];
}
function adapt(W1, W2) {
  let id = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : "Bradford";
  // adapt from a source whitepoint or illuminant W1
  // to a destination whitepoint or illuminant W2,
  // using the given chromatic adaptation transform (CAT)
  // debugger;
  let method = CATs[id];
  let [ρs, γs, βs] = multiplyMatrices(method.toCone_M, W1);
  let [ρd, γd, βd] = multiplyMatrices(method.toCone_M, W2);

  // all practical illuminants have non-zero XYZ so no division by zero can occur below
  let scale = [[ρd / ρs, 0, 0], [0, γd / γs, 0], [0, 0, βd / βs]];
  // console.log({scale});

  let scaled_cone_M = multiplyMatrices(scale, method.toCone_M);
  let adapt_M = multiplyMatrices(method.fromCone_M, scaled_cone_M);
  // console.log({scaled_cone_M, adapt_M});
  return adapt_M;
}
defineCAT({
  id: "von Kries",
  toCone_M: [[0.4002400, 0.7076000, -0.0808100], [-0.2263000, 1.1653200, 0.0457000], [0.0000000, 0.0000000, 0.9182200]],
  fromCone_M: [[1.8599363874558397, -1.1293816185800916, 0.21989740959619328], [0.3611914362417676, 0.6388124632850422, -0.000006370596838649899], [0, 0, 1.0890636230968613]]
});
defineCAT({
  id: "Bradford",
  // Convert an array of XYZ values in the range 0.0 - 1.0
  // to cone fundamentals
  toCone_M: [[0.8951000, 0.2664000, -0.1614000], [-0.7502000, 1.7135000, 0.0367000], [0.0389000, -0.0685000, 1.0296000]],
  // and back
  fromCone_M: [[0.9869929054667121, -0.14705425642099013, 0.15996265166373122], [0.4323052697233945, 0.5183602715367774, 0.049291228212855594], [-0.00852866457517732, 0.04004282165408486, 0.96848669578755]]
});
defineCAT({
  id: "CAT02",
  // with complete chromatic adaptation to W2, so D = 1.0
  toCone_M: [[0.7328000, 0.4296000, -0.1624000], [-0.7036000, 1.6975000, 0.0061000], [0.0030000, 0.0136000, 0.9834000]],
  fromCone_M: [[1.0961238208355142, -0.27886900021828726, 0.18274517938277307], [0.4543690419753592, 0.4735331543074117, 0.07209780371722911], [-0.009627608738429355, -0.00569803121611342, 1.0153256399545427]]
});
defineCAT({
  id: "CAT16",
  toCone_M: [[0.401288, 0.650173, -0.051461], [-0.250268, 1.204414, 0.045854], [-0.002079, 0.048952, 0.953127]],
  // the extra precision is needed to avoid roundtripping errors
  fromCone_M: [[1.862067855087233, -1.0112546305316845, 0.14918677544445172], [0.3875265432361372, 0.6214474419314753, -0.008973985167612521], [-0.01584149884933386, -0.03412293802851557, 1.0499644368778496]]
});
Object.assign(WHITES, {
  // whitepoint values from ASTM E308-01 with 10nm spacing, 1931 2 degree observer
  // all normalized to Y (luminance) = 1.00000
  // Illuminant A is a tungsten electric light, giving a very warm, orange light.
  A: [1.09850, 1.00000, 0.35585],
  // Illuminant C was an early approximation to daylight: illuminant A with a blue filter.
  C: [0.98074, 1.000000, 1.18232],
  // The daylight series of illuminants simulate natural daylight.
  // The color temperature (in degrees Kelvin/100) ranges from
  // cool, overcast daylight (D50) to bright, direct sunlight (D65).
  D55: [0.95682, 1.00000, 0.92149],
  D75: [0.94972, 1.00000, 1.22638],
  // Equal-energy illuminant, used in two-stage CAT16
  E: [1.00000, 1.00000, 1.00000],
  // The F series of illuminants represent fluorescent lights
  F2: [0.99186, 1.00000, 0.67393],
  F7: [0.95041, 1.00000, 1.08747],
  F11: [1.00962, 1.00000, 0.64350]
});

// The ACES whitepoint
// see TB-2018-001 Derivation of the ACES White Point CIE Chromaticity Coordinates
// also https://github.com/ampas/aces-dev/blob/master/documents/python/TB-2018-001/aces_wp.py
// Similar to D60
WHITES.ACES = [0.32168 / 0.33767, 1.00000, (1.00000 - 0.32168 - 0.33767) / 0.33767];

// convert an array of linear-light ACEScc values to CIE XYZ
const toXYZ_M = [[0.6624541811085053, 0.13400420645643313, 0.1561876870049078], [0.27222871678091454, 0.6740817658111484, 0.05368951740793705], [-0.005574649490394108, 0.004060733528982826, 1.0103391003129971]];
const fromXYZ_M = [[1.6410233796943257, -0.32480329418479, -0.23642469523761225], [-0.6636628587229829, 1.6153315916573379, 0.016756347685530137], [0.011721894328375376, -0.008284441996237409, 0.9883948585390215]];
var ACEScg = new RGBColorSpace({
  id: "acescg",
  cssId: "--acescg",
  name: "ACEScg",
  // ACEScg – A scene-referred, linear-light encoding of ACES Data
  // https://docs.acescentral.com/specifications/acescg/
  // uses the AP1 primaries, see section 4.3.1 Color primaries
  coords: {
    r: {
      range: [0, 65504],
      name: "Red"
    },
    g: {
      range: [0, 65504],
      name: "Green"
    },
    b: {
      range: [0, 65504],
      name: "Blue"
    }
  },
  referred: "scene",
  white: WHITES.ACES,
  toXYZ_M,
  fromXYZ_M
});

// export default Color;

const ε = 2 ** -16;

// the smallest value which, in the 32bit IEEE 754 float encoding,
// decodes as a non-negative value
const ACES_min_nonzero = -0.35828683;

// brightest encoded value, decodes to 65504
const ACES_cc_max = (Math.log2(65504) + 9.72) / 17.52; // 1.468

var acescc = new RGBColorSpace({
  id: "acescc",
  cssId: "--acescc",
  name: "ACEScc",
  // see S-2014-003 ACEScc – A Logarithmic Encoding of ACES Data
  // https://docs.acescentral.com/specifications/acescc/
  // uses the AP1 primaries, see section 4.3.1 Color primaries

  // Appendix A: "Very small ACES scene referred values below 7 1/4 stops
  // below 18% middle gray are encoded as negative ACEScc values.
  // These values should be preserved per the encoding in Section 4.4
  // so that all positive ACES values are maintained."
  coords: {
    r: {
      range: [ACES_min_nonzero, ACES_cc_max],
      name: "Red"
    },
    g: {
      range: [ACES_min_nonzero, ACES_cc_max],
      name: "Green"
    },
    b: {
      range: [ACES_min_nonzero, ACES_cc_max],
      name: "Blue"
    }
  },
  referred: "scene",
  base: ACEScg,
  // from section 4.4.2 Decoding Function
  toBase(RGB) {
    const low = (9.72 - 15) / 17.52; // -0.3014

    return RGB.map(function (val) {
      if (val <= low) {
        return (2 ** (val * 17.52 - 9.72) - ε) * 2; // very low values, below -0.3014
      } else if (val < ACES_cc_max) {
        return 2 ** (val * 17.52 - 9.72);
      } else {
        // val >= ACES_cc_max
        return 65504;
      }
    });
  },
  // Non-linear encoding function from S-2014-003, section 4.4.1 Encoding Function
  fromBase(RGB) {
    return RGB.map(function (val) {
      if (val <= 0) {
        return (Math.log2(ε) + 9.72) / 17.52; // -0.3584
      } else if (val < ε) {
        return (Math.log2(ε + val * 0.5) + 9.72) / 17.52;
      } else {
        // val >= ε
        return (Math.log2(val) + 9.72) / 17.52;
      }
    });
  }
  // encoded media white (rgb 1,1,1) => linear  [ 222.861, 222.861, 222.861 ]
  // encoded media black (rgb 0,0,0) => linear [ 0.0011857, 0.0011857, 0.0011857]
});

var spaces = /*#__PURE__*/Object.freeze({
	__proto__: null,
	A98RGB: a98rgb,
	A98RGB_Linear: A98Linear,
	ACEScc: acescc,
	ACEScg: ACEScg,
	CAM16_JMh: cam16,
	HCT: hct,
	HPLuv: hpluv,
	HSL: HSL,
	HSLuv: hsluv,
	HSV: HSV,
	HWB: hwb,
	ICTCP: ictcp,
	JzCzHz: jzczhz,
	Jzazbz: Jzazbz,
	LCH: lch,
	LCHuv: LCHuv,
	Lab: lab,
	Lab_D65: lab_d65,
	Luv: Luv,
	OKLCH: oklch,
	OKLab: OKLab,
	P3: P3,
	P3_Linear: P3Linear,
	ProPhoto: prophoto,
	ProPhoto_Linear: ProPhotoLinear,
	REC_2020: REC2020,
	REC_2020_Linear: REC2020Linear,
	REC_2100_HLG: rec2100Hlg,
	REC_2100_PQ: rec2100Pq,
	XYZ_ABS_D65: XYZ_Abs_D65,
	XYZ_D50: XYZ_D50,
	XYZ_D65: xyz_d65,
	sRGB: sRGB,
	sRGB_Linear: sRGBLinear
});

/**
 * Class that represents a color
 */
class Color {
  /**
   * Creates an instance of Color.
   * Signatures:
   * - `new Color(stringToParse)`
   * - `new Color(otherColor)`
   * - `new Color({space, coords, alpha})`
   * - `new Color(space, coords, alpha)`
   * - `new Color(spaceId, coords, alpha)`
   */
  constructor() {
    let color;
    for (var _len = arguments.length, args = new Array(_len), _key = 0; _key < _len; _key++) {
      args[_key] = arguments[_key];
    }
    if (args.length === 1) {
      color = getColor(args[0]);
    }
    let space, coords, alpha;
    if (color) {
      space = color.space || color.spaceId;
      coords = color.coords;
      alpha = color.alpha;
    } else {
      // default signature new Color(ColorSpace, array [, alpha])
      [space, coords, alpha] = args;
    }
    Object.defineProperty(this, "space", {
      value: ColorSpace.get(space),
      writable: false,
      enumerable: true,
      configurable: true // see note in https://262.ecma-international.org/8.0/#sec-proxy-object-internal-methods-and-internal-slots-get-p-receiver
    });
    this.coords = coords ? coords.slice() : [0, 0, 0];

    // Clamp alpha to [0, 1]
    this.alpha = alpha > 1 || alpha === undefined ? 1 : alpha < 0 ? 0 : alpha;

    // Convert "NaN" to NaN
    for (let i = 0; i < this.coords.length; i++) {
      if (this.coords[i] === "NaN") {
        this.coords[i] = NaN;
      }
    }

    // Define getters and setters for each coordinate
    for (let id in this.space.coords) {
      Object.defineProperty(this, id, {
        get: () => this.get(id),
        set: value => this.set(id, value)
      });
    }
  }
  get spaceId() {
    return this.space.id;
  }
  clone() {
    return new Color(this.space, this.coords, this.alpha);
  }
  toJSON() {
    return {
      spaceId: this.spaceId,
      coords: this.coords,
      alpha: this.alpha
    };
  }
  display() {
    for (var _len2 = arguments.length, args = new Array(_len2), _key2 = 0; _key2 < _len2; _key2++) {
      args[_key2] = arguments[_key2];
    }
    let ret = display(this, ...args);

    // Convert color object to Color instance
    ret.color = new Color(ret.color);
    return ret;
  }

  /**
   * Get a color from the argument passed
   * Basically gets us the same result as new Color(color) but doesn't clone an existing color object
   */
  static get(color) {
    if (color instanceof Color) {
      return color;
    }
    for (var _len3 = arguments.length, args = new Array(_len3 > 1 ? _len3 - 1 : 0), _key3 = 1; _key3 < _len3; _key3++) {
      args[_key3 - 1] = arguments[_key3];
    }
    return new Color(color, ...args);
  }
  static defineFunction(name, code) {
    let o = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : code;
    let {
      instance = true,
      returns
    } = o;
    let func = function () {
      let ret = code(...arguments);
      if (returns === "color") {
        ret = Color.get(ret);
      } else if (returns === "function<color>") {
        let f = ret;
        ret = function () {
          let ret = f(...arguments);
          return Color.get(ret);
        };
        // Copy any function metadata
        Object.assign(ret, f);
      } else if (returns === "array<color>") {
        ret = ret.map(c => Color.get(c));
      }
      return ret;
    };
    if (!(name in Color)) {
      Color[name] = func;
    }
    if (instance) {
      Color.prototype[name] = function () {
        for (var _len4 = arguments.length, args = new Array(_len4), _key4 = 0; _key4 < _len4; _key4++) {
          args[_key4] = arguments[_key4];
        }
        return func(this, ...args);
      };
    }
  }
  static defineFunctions(o) {
    for (let name in o) {
      Color.defineFunction(name, o[name], o[name]);
    }
  }
  static extend(exports) {
    if (exports.register) {
      exports.register(Color);
    } else {
      // No register method, just add the module's functions
      for (let name in exports) {
        Color.defineFunction(name, exports[name]);
      }
    }
  }
}
Color.defineFunctions({
  get,
  getAll,
  set,
  setAll,
  to,
  equals,
  inGamut,
  toGamut,
  distance,
  toString: serialize
});
Object.assign(Color, {
  util,
  hooks,
  WHITES,
  Space: ColorSpace,
  spaces: ColorSpace.registry,
  parse,
  // Global defaults one may want to configure
  defaults
});

for (let key of Object.keys(spaces)) {
  ColorSpace.register(spaces[key]);
}

var es_reflect_toStringTag = {};

var setToStringTag;
var hasRequiredSetToStringTag;

function requireSetToStringTag () {
	if (hasRequiredSetToStringTag) return setToStringTag;
	hasRequiredSetToStringTag = 1;
	var defineProperty = requireObjectDefineProperty().f;
	var hasOwn = requireHasOwnProperty();
	var wellKnownSymbol = requireWellKnownSymbol();

	var TO_STRING_TAG = wellKnownSymbol('toStringTag');

	setToStringTag = function (target, TAG, STATIC) {
	  if (target && !STATIC) target = target.prototype;
	  if (target && !hasOwn(target, TO_STRING_TAG)) {
	    defineProperty(target, TO_STRING_TAG, { configurable: true, value: TAG });
	  }
	};
	return setToStringTag;
}

var hasRequiredEs_reflect_toStringTag;

function requireEs_reflect_toStringTag () {
	if (hasRequiredEs_reflect_toStringTag) return es_reflect_toStringTag;
	hasRequiredEs_reflect_toStringTag = 1;
	var $ = require_export();
	var global = requireGlobal();
	var setToStringTag = requireSetToStringTag();

	$({ global: true }, { Reflect: {} });

	// Reflect[@@toStringTag] property
	// https://tc39.es/ecma262/#sec-reflect-@@tostringtag
	setToStringTag(global.Reflect, 'Reflect', true);
	return es_reflect_toStringTag;
}

requireEs_reflect_toStringTag();

// Add space accessors to existing color spaces
for (let id in ColorSpace.registry) {
  addSpaceAccessors(id, ColorSpace.registry[id]);
}

// Add space accessors to color spaces not yet created
hooks.add("colorspace-init-end", space => {
  var _space$aliases;
  addSpaceAccessors(space.id, space);
  (_space$aliases = space.aliases) === null || _space$aliases === void 0 || _space$aliases.forEach(alias => {
    addSpaceAccessors(alias, space);
  });
});
function addSpaceAccessors(id, space) {
  let propId = id.replace(/-/g, "_");
  Object.defineProperty(Color.prototype, propId, {
    // Convert coords to coords in another colorspace and return them
    // Source colorspace: this.spaceId
    // Target colorspace: id
    get() {
      let ret = this.getAll(id);
      if (typeof Proxy === "undefined") {
        // If proxies are not supported, just return a static array
        return ret;
      }

      // Enable color.spaceId.coordName syntax
      return new Proxy(ret, {
        has: (obj, property) => {
          try {
            ColorSpace.resolveCoord([space, property]);
            return true;
          } catch (e) {}
          return Reflect.has(obj, property);
        },
        get: (obj, property, receiver) => {
          if (property && typeof property !== "symbol" && !(property in obj)) {
            let {
              index
            } = ColorSpace.resolveCoord([space, property]);
            if (index >= 0) {
              return obj[index];
            }
          }
          return Reflect.get(obj, property, receiver);
        },
        set: (obj, property, value, receiver) => {
          if (property && typeof property !== "symbol" && !(property in obj) || property >= 0) {
            let {
              index
            } = ColorSpace.resolveCoord([space, property]);
            if (index >= 0) {
              obj[index] = value;

              // Update color.coords
              this.setAll(id, obj);
              return true;
            }
          }
          return Reflect.set(obj, property, value, receiver);
        }
      });
    },
    // Convert coords in another colorspace to internal coords and set them
    // Target colorspace: this.spaceId
    // Source colorspace: id
    set(coords) {
      this.setAll(id, coords);
    },
    configurable: true,
    enumerable: true
  });
}

// Import all modules of Color.js
Color.extend(deltaEMethods);
Color.extend({
  deltaE
});
Object.assign(Color, {
  deltaEMethods
});
Color.extend(variations);
Color.extend({
  contrast
});
Color.extend(chromaticity);
Color.extend(luminance);
Color.extend(interpolation);
Color.extend(contrastMethods);

export { Color as default };
//# sourceMappingURL=color.legacy.js.map
