var DemoPlugin = function() {
  "use strict";
  /**
  * @vue/shared v3.5.30
  * (c) 2018-present Yuxi (Evan) You and Vue contributors
  * @license MIT
  **/
  var _a;
  // @__NO_SIDE_EFFECTS__
  function makeMap(str) {
    const map2 = /* @__PURE__ */ Object.create(null);
    for (const key of str.split(",")) map2[key] = 1;
    return (val) => val in map2;
  }
  const EMPTY_OBJ = {};
  const EMPTY_ARR = [];
  const NOOP = () => {
  };
  const NO = () => false;
  const isOn = (key) => key.charCodeAt(0) === 111 && key.charCodeAt(1) === 110 && // uppercase letter
  (key.charCodeAt(2) > 122 || key.charCodeAt(2) < 97);
  const isModelListener = (key) => key.startsWith("onUpdate:");
  const extend = Object.assign;
  const remove = (arr, el) => {
    const i = arr.indexOf(el);
    if (i > -1) {
      arr.splice(i, 1);
    }
  };
  const hasOwnProperty$d = Object.prototype.hasOwnProperty;
  const hasOwn = (val, key) => hasOwnProperty$d.call(val, key);
  const isArray$1 = Array.isArray;
  const isMap = (val) => toTypeString(val) === "[object Map]";
  const isSet = (val) => toTypeString(val) === "[object Set]";
  const isDate = (val) => toTypeString(val) === "[object Date]";
  const isFunction$1 = (val) => typeof val === "function";
  const isString = (val) => typeof val === "string";
  const isSymbol$1 = (val) => typeof val === "symbol";
  const isObject$1 = (val) => val !== null && typeof val === "object";
  const isPromise = (val) => {
    return (isObject$1(val) || isFunction$1(val)) && isFunction$1(val.then) && isFunction$1(val.catch);
  };
  const objectToString$1 = Object.prototype.toString;
  const toTypeString = (value) => objectToString$1.call(value);
  const toRawType = (value) => {
    return toTypeString(value).slice(8, -1);
  };
  const isPlainObject$1 = (val) => toTypeString(val) === "[object Object]";
  const isIntegerKey = (key) => isString(key) && key !== "NaN" && key[0] !== "-" && "" + parseInt(key, 10) === key;
  const isReservedProp = /* @__PURE__ */ makeMap(
    // the leading comma is intentional so empty string "" is also included
    ",key,ref,ref_for,ref_key,onVnodeBeforeMount,onVnodeMounted,onVnodeBeforeUpdate,onVnodeUpdated,onVnodeBeforeUnmount,onVnodeUnmounted"
  );
  const cacheStringFunction = (fn) => {
    const cache2 = /* @__PURE__ */ Object.create(null);
    return (str) => {
      const hit = cache2[str];
      return hit || (cache2[str] = fn(str));
    };
  };
  const camelizeRE = /-\w/g;
  const camelize = cacheStringFunction(
    (str) => {
      return str.replace(camelizeRE, (c2) => c2.slice(1).toUpperCase());
    }
  );
  const hyphenateRE = /\B([A-Z])/g;
  const hyphenate = cacheStringFunction(
    (str) => str.replace(hyphenateRE, "-$1").toLowerCase()
  );
  const capitalize = cacheStringFunction((str) => {
    return str.charAt(0).toUpperCase() + str.slice(1);
  });
  const toHandlerKey = cacheStringFunction(
    (str) => {
      const s = str ? `on${capitalize(str)}` : ``;
      return s;
    }
  );
  const hasChanged = (value, oldValue) => !Object.is(value, oldValue);
  const invokeArrayFns = (fns, ...arg) => {
    for (let i = 0; i < fns.length; i++) {
      fns[i](...arg);
    }
  };
  const def = (obj, key, value, writable = false) => {
    Object.defineProperty(obj, key, {
      configurable: true,
      enumerable: false,
      writable,
      value
    });
  };
  const looseToNumber = (val) => {
    const n = parseFloat(val);
    return isNaN(n) ? val : n;
  };
  const toNumber = (val) => {
    const n = isString(val) ? Number(val) : NaN;
    return isNaN(n) ? val : n;
  };
  let _globalThis;
  const getGlobalThis = () => {
    return _globalThis || (_globalThis = typeof globalThis !== "undefined" ? globalThis : typeof self !== "undefined" ? self : typeof window !== "undefined" ? window : typeof global !== "undefined" ? global : {});
  };
  function normalizeStyle(value) {
    if (isArray$1(value)) {
      const res = {};
      for (let i = 0; i < value.length; i++) {
        const item = value[i];
        const normalized = isString(item) ? parseStringStyle(item) : normalizeStyle(item);
        if (normalized) {
          for (const key in normalized) {
            res[key] = normalized[key];
          }
        }
      }
      return res;
    } else if (isString(value) || isObject$1(value)) {
      return value;
    }
  }
  const listDelimiterRE = /;(?![^(]*\))/g;
  const propertyDelimiterRE = /:([^]+)/;
  const styleCommentRE = /\/\*[^]*?\*\//g;
  function parseStringStyle(cssText) {
    const ret = {};
    cssText.replace(styleCommentRE, "").split(listDelimiterRE).forEach((item) => {
      if (item) {
        const tmp = item.split(propertyDelimiterRE);
        tmp.length > 1 && (ret[tmp[0].trim()] = tmp[1].trim());
      }
    });
    return ret;
  }
  function normalizeClass(value) {
    let res = "";
    if (isString(value)) {
      res = value;
    } else if (isArray$1(value)) {
      for (let i = 0; i < value.length; i++) {
        const normalized = normalizeClass(value[i]);
        if (normalized) {
          res += normalized + " ";
        }
      }
    } else if (isObject$1(value)) {
      for (const name in value) {
        if (value[name]) {
          res += name + " ";
        }
      }
    }
    return res.trim();
  }
  const specialBooleanAttrs = `itemscope,allowfullscreen,formnovalidate,ismap,nomodule,novalidate,readonly`;
  const isSpecialBooleanAttr = /* @__PURE__ */ makeMap(specialBooleanAttrs);
  function includeBooleanAttr(value) {
    return !!value || value === "";
  }
  function looseCompareArrays(a, b) {
    if (a.length !== b.length) return false;
    let equal = true;
    for (let i = 0; equal && i < a.length; i++) {
      equal = looseEqual(a[i], b[i]);
    }
    return equal;
  }
  function looseEqual(a, b) {
    if (a === b) return true;
    let aValidType = isDate(a);
    let bValidType = isDate(b);
    if (aValidType || bValidType) {
      return aValidType && bValidType ? a.getTime() === b.getTime() : false;
    }
    aValidType = isSymbol$1(a);
    bValidType = isSymbol$1(b);
    if (aValidType || bValidType) {
      return a === b;
    }
    aValidType = isArray$1(a);
    bValidType = isArray$1(b);
    if (aValidType || bValidType) {
      return aValidType && bValidType ? looseCompareArrays(a, b) : false;
    }
    aValidType = isObject$1(a);
    bValidType = isObject$1(b);
    if (aValidType || bValidType) {
      if (!aValidType || !bValidType) {
        return false;
      }
      const aKeysCount = Object.keys(a).length;
      const bKeysCount = Object.keys(b).length;
      if (aKeysCount !== bKeysCount) {
        return false;
      }
      for (const key in a) {
        const aHasKey = a.hasOwnProperty(key);
        const bHasKey = b.hasOwnProperty(key);
        if (aHasKey && !bHasKey || !aHasKey && bHasKey || !looseEqual(a[key], b[key])) {
          return false;
        }
      }
    }
    return String(a) === String(b);
  }
  function looseIndexOf(arr, val) {
    return arr.findIndex((item) => looseEqual(item, val));
  }
  const isRef$1 = (val) => {
    return !!(val && val["__v_isRef"] === true);
  };
  const toDisplayString = (val) => {
    return isString(val) ? val : val == null ? "" : isArray$1(val) || isObject$1(val) && (val.toString === objectToString$1 || !isFunction$1(val.toString)) ? isRef$1(val) ? toDisplayString(val.value) : JSON.stringify(val, replacer, 2) : String(val);
  };
  const replacer = (_key, val) => {
    if (isRef$1(val)) {
      return replacer(_key, val.value);
    } else if (isMap(val)) {
      return {
        [`Map(${val.size})`]: [...val.entries()].reduce(
          (entries, [key, val2], i) => {
            entries[stringifySymbol(key, i) + " =>"] = val2;
            return entries;
          },
          {}
        )
      };
    } else if (isSet(val)) {
      return {
        [`Set(${val.size})`]: [...val.values()].map((v) => stringifySymbol(v))
      };
    } else if (isSymbol$1(val)) {
      return stringifySymbol(val);
    } else if (isObject$1(val) && !isArray$1(val) && !isPlainObject$1(val)) {
      return String(val);
    }
    return val;
  };
  const stringifySymbol = (v, i = "") => {
    var _a2;
    return (
      // Symbol.description in es2019+ so we need to cast here to pass
      // the lib: es2016 check
      isSymbol$1(v) ? `Symbol(${(_a2 = v.description) != null ? _a2 : i})` : v
    );
  };
  /**
  * @vue/reactivity v3.5.30
  * (c) 2018-present Yuxi (Evan) You and Vue contributors
  * @license MIT
  **/
  let activeEffectScope;
  class EffectScope {
    // TODO isolatedDeclarations "__v_skip"
    constructor(detached = false) {
      this.detached = detached;
      this._active = true;
      this._on = 0;
      this.effects = [];
      this.cleanups = [];
      this._isPaused = false;
      this.__v_skip = true;
      this.parent = activeEffectScope;
      if (!detached && activeEffectScope) {
        this.index = (activeEffectScope.scopes || (activeEffectScope.scopes = [])).push(
          this
        ) - 1;
      }
    }
    get active() {
      return this._active;
    }
    pause() {
      if (this._active) {
        this._isPaused = true;
        let i, l;
        if (this.scopes) {
          for (i = 0, l = this.scopes.length; i < l; i++) {
            this.scopes[i].pause();
          }
        }
        for (i = 0, l = this.effects.length; i < l; i++) {
          this.effects[i].pause();
        }
      }
    }
    /**
     * Resumes the effect scope, including all child scopes and effects.
     */
    resume() {
      if (this._active) {
        if (this._isPaused) {
          this._isPaused = false;
          let i, l;
          if (this.scopes) {
            for (i = 0, l = this.scopes.length; i < l; i++) {
              this.scopes[i].resume();
            }
          }
          for (i = 0, l = this.effects.length; i < l; i++) {
            this.effects[i].resume();
          }
        }
      }
    }
    run(fn) {
      if (this._active) {
        const currentEffectScope = activeEffectScope;
        try {
          activeEffectScope = this;
          return fn();
        } finally {
          activeEffectScope = currentEffectScope;
        }
      }
    }
    /**
     * This should only be called on non-detached scopes
     * @internal
     */
    on() {
      if (++this._on === 1) {
        this.prevScope = activeEffectScope;
        activeEffectScope = this;
      }
    }
    /**
     * This should only be called on non-detached scopes
     * @internal
     */
    off() {
      if (this._on > 0 && --this._on === 0) {
        activeEffectScope = this.prevScope;
        this.prevScope = void 0;
      }
    }
    stop(fromParent) {
      if (this._active) {
        this._active = false;
        let i, l;
        for (i = 0, l = this.effects.length; i < l; i++) {
          this.effects[i].stop();
        }
        this.effects.length = 0;
        for (i = 0, l = this.cleanups.length; i < l; i++) {
          this.cleanups[i]();
        }
        this.cleanups.length = 0;
        if (this.scopes) {
          for (i = 0, l = this.scopes.length; i < l; i++) {
            this.scopes[i].stop(true);
          }
          this.scopes.length = 0;
        }
        if (!this.detached && this.parent && !fromParent) {
          const last = this.parent.scopes.pop();
          if (last && last !== this) {
            this.parent.scopes[this.index] = last;
            last.index = this.index;
          }
        }
        this.parent = void 0;
      }
    }
  }
  function getCurrentScope() {
    return activeEffectScope;
  }
  let activeSub;
  const pausedQueueEffects = /* @__PURE__ */ new WeakSet();
  class ReactiveEffect {
    constructor(fn) {
      this.fn = fn;
      this.deps = void 0;
      this.depsTail = void 0;
      this.flags = 1 | 4;
      this.next = void 0;
      this.cleanup = void 0;
      this.scheduler = void 0;
      if (activeEffectScope && activeEffectScope.active) {
        activeEffectScope.effects.push(this);
      }
    }
    pause() {
      this.flags |= 64;
    }
    resume() {
      if (this.flags & 64) {
        this.flags &= -65;
        if (pausedQueueEffects.has(this)) {
          pausedQueueEffects.delete(this);
          this.trigger();
        }
      }
    }
    /**
     * @internal
     */
    notify() {
      if (this.flags & 2 && !(this.flags & 32)) {
        return;
      }
      if (!(this.flags & 8)) {
        batch(this);
      }
    }
    run() {
      if (!(this.flags & 1)) {
        return this.fn();
      }
      this.flags |= 2;
      cleanupEffect(this);
      prepareDeps(this);
      const prevEffect = activeSub;
      const prevShouldTrack = shouldTrack;
      activeSub = this;
      shouldTrack = true;
      try {
        return this.fn();
      } finally {
        cleanupDeps(this);
        activeSub = prevEffect;
        shouldTrack = prevShouldTrack;
        this.flags &= -3;
      }
    }
    stop() {
      if (this.flags & 1) {
        for (let link = this.deps; link; link = link.nextDep) {
          removeSub(link);
        }
        this.deps = this.depsTail = void 0;
        cleanupEffect(this);
        this.onStop && this.onStop();
        this.flags &= -2;
      }
    }
    trigger() {
      if (this.flags & 64) {
        pausedQueueEffects.add(this);
      } else if (this.scheduler) {
        this.scheduler();
      } else {
        this.runIfDirty();
      }
    }
    /**
     * @internal
     */
    runIfDirty() {
      if (isDirty(this)) {
        this.run();
      }
    }
    get dirty() {
      return isDirty(this);
    }
  }
  let batchDepth = 0;
  let batchedSub;
  let batchedComputed;
  function batch(sub, isComputed = false) {
    sub.flags |= 8;
    if (isComputed) {
      sub.next = batchedComputed;
      batchedComputed = sub;
      return;
    }
    sub.next = batchedSub;
    batchedSub = sub;
  }
  function startBatch() {
    batchDepth++;
  }
  function endBatch() {
    if (--batchDepth > 0) {
      return;
    }
    if (batchedComputed) {
      let e = batchedComputed;
      batchedComputed = void 0;
      while (e) {
        const next = e.next;
        e.next = void 0;
        e.flags &= -9;
        e = next;
      }
    }
    let error;
    while (batchedSub) {
      let e = batchedSub;
      batchedSub = void 0;
      while (e) {
        const next = e.next;
        e.next = void 0;
        e.flags &= -9;
        if (e.flags & 1) {
          try {
            ;
            e.trigger();
          } catch (err) {
            if (!error) error = err;
          }
        }
        e = next;
      }
    }
    if (error) throw error;
  }
  function prepareDeps(sub) {
    for (let link = sub.deps; link; link = link.nextDep) {
      link.version = -1;
      link.prevActiveLink = link.dep.activeLink;
      link.dep.activeLink = link;
    }
  }
  function cleanupDeps(sub) {
    let head;
    let tail = sub.depsTail;
    let link = tail;
    while (link) {
      const prev = link.prevDep;
      if (link.version === -1) {
        if (link === tail) tail = prev;
        removeSub(link);
        removeDep(link);
      } else {
        head = link;
      }
      link.dep.activeLink = link.prevActiveLink;
      link.prevActiveLink = void 0;
      link = prev;
    }
    sub.deps = head;
    sub.depsTail = tail;
  }
  function isDirty(sub) {
    for (let link = sub.deps; link; link = link.nextDep) {
      if (link.dep.version !== link.version || link.dep.computed && (refreshComputed(link.dep.computed) || link.dep.version !== link.version)) {
        return true;
      }
    }
    if (sub._dirty) {
      return true;
    }
    return false;
  }
  function refreshComputed(computed2) {
    if (computed2.flags & 4 && !(computed2.flags & 16)) {
      return;
    }
    computed2.flags &= -17;
    if (computed2.globalVersion === globalVersion) {
      return;
    }
    computed2.globalVersion = globalVersion;
    if (!computed2.isSSR && computed2.flags & 128 && (!computed2.deps && !computed2._dirty || !isDirty(computed2))) {
      return;
    }
    computed2.flags |= 2;
    const dep = computed2.dep;
    const prevSub = activeSub;
    const prevShouldTrack = shouldTrack;
    activeSub = computed2;
    shouldTrack = true;
    try {
      prepareDeps(computed2);
      const value = computed2.fn(computed2._value);
      if (dep.version === 0 || hasChanged(value, computed2._value)) {
        computed2.flags |= 128;
        computed2._value = value;
        dep.version++;
      }
    } catch (err) {
      dep.version++;
      throw err;
    } finally {
      activeSub = prevSub;
      shouldTrack = prevShouldTrack;
      cleanupDeps(computed2);
      computed2.flags &= -3;
    }
  }
  function removeSub(link, soft = false) {
    const { dep, prevSub, nextSub } = link;
    if (prevSub) {
      prevSub.nextSub = nextSub;
      link.prevSub = void 0;
    }
    if (nextSub) {
      nextSub.prevSub = prevSub;
      link.nextSub = void 0;
    }
    if (dep.subs === link) {
      dep.subs = prevSub;
      if (!prevSub && dep.computed) {
        dep.computed.flags &= -5;
        for (let l = dep.computed.deps; l; l = l.nextDep) {
          removeSub(l, true);
        }
      }
    }
    if (!soft && !--dep.sc && dep.map) {
      dep.map.delete(dep.key);
    }
  }
  function removeDep(link) {
    const { prevDep, nextDep } = link;
    if (prevDep) {
      prevDep.nextDep = nextDep;
      link.prevDep = void 0;
    }
    if (nextDep) {
      nextDep.prevDep = prevDep;
      link.nextDep = void 0;
    }
  }
  let shouldTrack = true;
  const trackStack = [];
  function pauseTracking() {
    trackStack.push(shouldTrack);
    shouldTrack = false;
  }
  function resetTracking() {
    const last = trackStack.pop();
    shouldTrack = last === void 0 ? true : last;
  }
  function cleanupEffect(e) {
    const { cleanup } = e;
    e.cleanup = void 0;
    if (cleanup) {
      const prevSub = activeSub;
      activeSub = void 0;
      try {
        cleanup();
      } finally {
        activeSub = prevSub;
      }
    }
  }
  let globalVersion = 0;
  class Link {
    constructor(sub, dep) {
      this.sub = sub;
      this.dep = dep;
      this.version = dep.version;
      this.nextDep = this.prevDep = this.nextSub = this.prevSub = this.prevActiveLink = void 0;
    }
  }
  class Dep {
    // TODO isolatedDeclarations "__v_skip"
    constructor(computed2) {
      this.computed = computed2;
      this.version = 0;
      this.activeLink = void 0;
      this.subs = void 0;
      this.map = void 0;
      this.key = void 0;
      this.sc = 0;
      this.__v_skip = true;
    }
    track(debugInfo) {
      if (!activeSub || !shouldTrack || activeSub === this.computed) {
        return;
      }
      let link = this.activeLink;
      if (link === void 0 || link.sub !== activeSub) {
        link = this.activeLink = new Link(activeSub, this);
        if (!activeSub.deps) {
          activeSub.deps = activeSub.depsTail = link;
        } else {
          link.prevDep = activeSub.depsTail;
          activeSub.depsTail.nextDep = link;
          activeSub.depsTail = link;
        }
        addSub(link);
      } else if (link.version === -1) {
        link.version = this.version;
        if (link.nextDep) {
          const next = link.nextDep;
          next.prevDep = link.prevDep;
          if (link.prevDep) {
            link.prevDep.nextDep = next;
          }
          link.prevDep = activeSub.depsTail;
          link.nextDep = void 0;
          activeSub.depsTail.nextDep = link;
          activeSub.depsTail = link;
          if (activeSub.deps === link) {
            activeSub.deps = next;
          }
        }
      }
      return link;
    }
    trigger(debugInfo) {
      this.version++;
      globalVersion++;
      this.notify(debugInfo);
    }
    notify(debugInfo) {
      startBatch();
      try {
        if (false) ;
        for (let link = this.subs; link; link = link.prevSub) {
          if (link.sub.notify()) {
            ;
            link.sub.dep.notify();
          }
        }
      } finally {
        endBatch();
      }
    }
  }
  function addSub(link) {
    link.dep.sc++;
    if (link.sub.flags & 4) {
      const computed2 = link.dep.computed;
      if (computed2 && !link.dep.subs) {
        computed2.flags |= 4 | 16;
        for (let l = computed2.deps; l; l = l.nextDep) {
          addSub(l);
        }
      }
      const currentTail = link.dep.subs;
      if (currentTail !== link) {
        link.prevSub = currentTail;
        if (currentTail) currentTail.nextSub = link;
      }
      link.dep.subs = link;
    }
  }
  const targetMap = /* @__PURE__ */ new WeakMap();
  const ITERATE_KEY = /* @__PURE__ */ Symbol(
    ""
  );
  const MAP_KEY_ITERATE_KEY = /* @__PURE__ */ Symbol(
    ""
  );
  const ARRAY_ITERATE_KEY = /* @__PURE__ */ Symbol(
    ""
  );
  function track(target, type, key) {
    if (shouldTrack && activeSub) {
      let depsMap = targetMap.get(target);
      if (!depsMap) {
        targetMap.set(target, depsMap = /* @__PURE__ */ new Map());
      }
      let dep = depsMap.get(key);
      if (!dep) {
        depsMap.set(key, dep = new Dep());
        dep.map = depsMap;
        dep.key = key;
      }
      {
        dep.track();
      }
    }
  }
  function trigger$1(target, type, key, newValue, oldValue, oldTarget) {
    const depsMap = targetMap.get(target);
    if (!depsMap) {
      globalVersion++;
      return;
    }
    const run = (dep) => {
      if (dep) {
        {
          dep.trigger();
        }
      }
    };
    startBatch();
    if (type === "clear") {
      depsMap.forEach(run);
    } else {
      const targetIsArray = isArray$1(target);
      const isArrayIndex = targetIsArray && isIntegerKey(key);
      if (targetIsArray && key === "length") {
        const newLength = Number(newValue);
        depsMap.forEach((dep, key2) => {
          if (key2 === "length" || key2 === ARRAY_ITERATE_KEY || !isSymbol$1(key2) && key2 >= newLength) {
            run(dep);
          }
        });
      } else {
        if (key !== void 0 || depsMap.has(void 0)) {
          run(depsMap.get(key));
        }
        if (isArrayIndex) {
          run(depsMap.get(ARRAY_ITERATE_KEY));
        }
        switch (type) {
          case "add":
            if (!targetIsArray) {
              run(depsMap.get(ITERATE_KEY));
              if (isMap(target)) {
                run(depsMap.get(MAP_KEY_ITERATE_KEY));
              }
            } else if (isArrayIndex) {
              run(depsMap.get("length"));
            }
            break;
          case "delete":
            if (!targetIsArray) {
              run(depsMap.get(ITERATE_KEY));
              if (isMap(target)) {
                run(depsMap.get(MAP_KEY_ITERATE_KEY));
              }
            }
            break;
          case "set":
            if (isMap(target)) {
              run(depsMap.get(ITERATE_KEY));
            }
            break;
        }
      }
    }
    endBatch();
  }
  function getDepFromReactive(object, key) {
    const depMap = targetMap.get(object);
    return depMap && depMap.get(key);
  }
  function reactiveReadArray(array) {
    const raw = /* @__PURE__ */ toRaw(array);
    if (raw === array) return raw;
    track(raw, "iterate", ARRAY_ITERATE_KEY);
    return /* @__PURE__ */ isShallow(array) ? raw : raw.map(toReactive);
  }
  function shallowReadArray(arr) {
    track(arr = /* @__PURE__ */ toRaw(arr), "iterate", ARRAY_ITERATE_KEY);
    return arr;
  }
  function toWrapped(target, item) {
    if (/* @__PURE__ */ isReadonly(target)) {
      return /* @__PURE__ */ isReactive(target) ? toReadonly(toReactive(item)) : toReadonly(item);
    }
    return toReactive(item);
  }
  const arrayInstrumentations = {
    __proto__: null,
    [Symbol.iterator]() {
      return iterator(this, Symbol.iterator, (item) => toWrapped(this, item));
    },
    concat(...args) {
      return reactiveReadArray(this).concat(
        ...args.map((x) => isArray$1(x) ? reactiveReadArray(x) : x)
      );
    },
    entries() {
      return iterator(this, "entries", (value) => {
        value[1] = toWrapped(this, value[1]);
        return value;
      });
    },
    every(fn, thisArg) {
      return apply$1(this, "every", fn, thisArg, void 0, arguments);
    },
    filter(fn, thisArg) {
      return apply$1(
        this,
        "filter",
        fn,
        thisArg,
        (v) => v.map((item) => toWrapped(this, item)),
        arguments
      );
    },
    find(fn, thisArg) {
      return apply$1(
        this,
        "find",
        fn,
        thisArg,
        (item) => toWrapped(this, item),
        arguments
      );
    },
    findIndex(fn, thisArg) {
      return apply$1(this, "findIndex", fn, thisArg, void 0, arguments);
    },
    findLast(fn, thisArg) {
      return apply$1(
        this,
        "findLast",
        fn,
        thisArg,
        (item) => toWrapped(this, item),
        arguments
      );
    },
    findLastIndex(fn, thisArg) {
      return apply$1(this, "findLastIndex", fn, thisArg, void 0, arguments);
    },
    // flat, flatMap could benefit from ARRAY_ITERATE but are not straight-forward to implement
    forEach(fn, thisArg) {
      return apply$1(this, "forEach", fn, thisArg, void 0, arguments);
    },
    includes(...args) {
      return searchProxy(this, "includes", args);
    },
    indexOf(...args) {
      return searchProxy(this, "indexOf", args);
    },
    join(separator) {
      return reactiveReadArray(this).join(separator);
    },
    // keys() iterator only reads `length`, no optimization required
    lastIndexOf(...args) {
      return searchProxy(this, "lastIndexOf", args);
    },
    map(fn, thisArg) {
      return apply$1(this, "map", fn, thisArg, void 0, arguments);
    },
    pop() {
      return noTracking(this, "pop");
    },
    push(...args) {
      return noTracking(this, "push", args);
    },
    reduce(fn, ...args) {
      return reduce(this, "reduce", fn, args);
    },
    reduceRight(fn, ...args) {
      return reduce(this, "reduceRight", fn, args);
    },
    shift() {
      return noTracking(this, "shift");
    },
    // slice could use ARRAY_ITERATE but also seems to beg for range tracking
    some(fn, thisArg) {
      return apply$1(this, "some", fn, thisArg, void 0, arguments);
    },
    splice(...args) {
      return noTracking(this, "splice", args);
    },
    toReversed() {
      return reactiveReadArray(this).toReversed();
    },
    toSorted(comparer) {
      return reactiveReadArray(this).toSorted(comparer);
    },
    toSpliced(...args) {
      return reactiveReadArray(this).toSpliced(...args);
    },
    unshift(...args) {
      return noTracking(this, "unshift", args);
    },
    values() {
      return iterator(this, "values", (item) => toWrapped(this, item));
    }
  };
  function iterator(self2, method, wrapValue) {
    const arr = shallowReadArray(self2);
    const iter = arr[method]();
    if (arr !== self2 && !/* @__PURE__ */ isShallow(self2)) {
      iter._next = iter.next;
      iter.next = () => {
        const result = iter._next();
        if (!result.done) {
          result.value = wrapValue(result.value);
        }
        return result;
      };
    }
    return iter;
  }
  const arrayProto$1 = Array.prototype;
  function apply$1(self2, method, fn, thisArg, wrappedRetFn, args) {
    const arr = shallowReadArray(self2);
    const needsWrap = arr !== self2 && !/* @__PURE__ */ isShallow(self2);
    const methodFn = arr[method];
    if (methodFn !== arrayProto$1[method]) {
      const result2 = methodFn.apply(self2, args);
      return needsWrap ? toReactive(result2) : result2;
    }
    let wrappedFn = fn;
    if (arr !== self2) {
      if (needsWrap) {
        wrappedFn = function(item, index) {
          return fn.call(this, toWrapped(self2, item), index, self2);
        };
      } else if (fn.length > 2) {
        wrappedFn = function(item, index) {
          return fn.call(this, item, index, self2);
        };
      }
    }
    const result = methodFn.call(arr, wrappedFn, thisArg);
    return needsWrap && wrappedRetFn ? wrappedRetFn(result) : result;
  }
  function reduce(self2, method, fn, args) {
    const arr = shallowReadArray(self2);
    const needsWrap = arr !== self2 && !/* @__PURE__ */ isShallow(self2);
    let wrappedFn = fn;
    let wrapInitialAccumulator = false;
    if (arr !== self2) {
      if (needsWrap) {
        wrapInitialAccumulator = args.length === 0;
        wrappedFn = function(acc, item, index) {
          if (wrapInitialAccumulator) {
            wrapInitialAccumulator = false;
            acc = toWrapped(self2, acc);
          }
          return fn.call(this, acc, toWrapped(self2, item), index, self2);
        };
      } else if (fn.length > 3) {
        wrappedFn = function(acc, item, index) {
          return fn.call(this, acc, item, index, self2);
        };
      }
    }
    const result = arr[method](wrappedFn, ...args);
    return wrapInitialAccumulator ? toWrapped(self2, result) : result;
  }
  function searchProxy(self2, method, args) {
    const arr = /* @__PURE__ */ toRaw(self2);
    track(arr, "iterate", ARRAY_ITERATE_KEY);
    const res = arr[method](...args);
    if ((res === -1 || res === false) && /* @__PURE__ */ isProxy(args[0])) {
      args[0] = /* @__PURE__ */ toRaw(args[0]);
      return arr[method](...args);
    }
    return res;
  }
  function noTracking(self2, method, args = []) {
    pauseTracking();
    startBatch();
    const res = (/* @__PURE__ */ toRaw(self2))[method].apply(self2, args);
    endBatch();
    resetTracking();
    return res;
  }
  const isNonTrackableKeys = /* @__PURE__ */ makeMap(`__proto__,__v_isRef,__isVue`);
  const builtInSymbols = new Set(
    /* @__PURE__ */ Object.getOwnPropertyNames(Symbol).filter((key) => key !== "arguments" && key !== "caller").map((key) => Symbol[key]).filter(isSymbol$1)
  );
  function hasOwnProperty$c(key) {
    if (!isSymbol$1(key)) key = String(key);
    const obj = /* @__PURE__ */ toRaw(this);
    track(obj, "has", key);
    return obj.hasOwnProperty(key);
  }
  class BaseReactiveHandler {
    constructor(_isReadonly = false, _isShallow = false) {
      this._isReadonly = _isReadonly;
      this._isShallow = _isShallow;
    }
    get(target, key, receiver) {
      if (key === "__v_skip") return target["__v_skip"];
      const isReadonly2 = this._isReadonly, isShallow2 = this._isShallow;
      if (key === "__v_isReactive") {
        return !isReadonly2;
      } else if (key === "__v_isReadonly") {
        return isReadonly2;
      } else if (key === "__v_isShallow") {
        return isShallow2;
      } else if (key === "__v_raw") {
        if (receiver === (isReadonly2 ? isShallow2 ? shallowReadonlyMap : readonlyMap : isShallow2 ? shallowReactiveMap : reactiveMap).get(target) || // receiver is not the reactive proxy, but has the same prototype
        // this means the receiver is a user proxy of the reactive proxy
        Object.getPrototypeOf(target) === Object.getPrototypeOf(receiver)) {
          return target;
        }
        return;
      }
      const targetIsArray = isArray$1(target);
      if (!isReadonly2) {
        let fn;
        if (targetIsArray && (fn = arrayInstrumentations[key])) {
          return fn;
        }
        if (key === "hasOwnProperty") {
          return hasOwnProperty$c;
        }
      }
      const res = Reflect.get(
        target,
        key,
        // if this is a proxy wrapping a ref, return methods using the raw ref
        // as receiver so that we don't have to call `toRaw` on the ref in all
        // its class methods
        /* @__PURE__ */ isRef(target) ? target : receiver
      );
      if (isSymbol$1(key) ? builtInSymbols.has(key) : isNonTrackableKeys(key)) {
        return res;
      }
      if (!isReadonly2) {
        track(target, "get", key);
      }
      if (isShallow2) {
        return res;
      }
      if (/* @__PURE__ */ isRef(res)) {
        const value = targetIsArray && isIntegerKey(key) ? res : res.value;
        return isReadonly2 && isObject$1(value) ? /* @__PURE__ */ readonly(value) : value;
      }
      if (isObject$1(res)) {
        return isReadonly2 ? /* @__PURE__ */ readonly(res) : /* @__PURE__ */ reactive(res);
      }
      return res;
    }
  }
  class MutableReactiveHandler extends BaseReactiveHandler {
    constructor(isShallow2 = false) {
      super(false, isShallow2);
    }
    set(target, key, value, receiver) {
      let oldValue = target[key];
      const isArrayWithIntegerKey = isArray$1(target) && isIntegerKey(key);
      if (!this._isShallow) {
        const isOldValueReadonly = /* @__PURE__ */ isReadonly(oldValue);
        if (!/* @__PURE__ */ isShallow(value) && !/* @__PURE__ */ isReadonly(value)) {
          oldValue = /* @__PURE__ */ toRaw(oldValue);
          value = /* @__PURE__ */ toRaw(value);
        }
        if (!isArrayWithIntegerKey && /* @__PURE__ */ isRef(oldValue) && !/* @__PURE__ */ isRef(value)) {
          if (isOldValueReadonly) {
            return true;
          } else {
            oldValue.value = value;
            return true;
          }
        }
      }
      const hadKey = isArrayWithIntegerKey ? Number(key) < target.length : hasOwn(target, key);
      const result = Reflect.set(
        target,
        key,
        value,
        /* @__PURE__ */ isRef(target) ? target : receiver
      );
      if (target === /* @__PURE__ */ toRaw(receiver)) {
        if (!hadKey) {
          trigger$1(target, "add", key, value);
        } else if (hasChanged(value, oldValue)) {
          trigger$1(target, "set", key, value);
        }
      }
      return result;
    }
    deleteProperty(target, key) {
      const hadKey = hasOwn(target, key);
      target[key];
      const result = Reflect.deleteProperty(target, key);
      if (result && hadKey) {
        trigger$1(target, "delete", key, void 0);
      }
      return result;
    }
    has(target, key) {
      const result = Reflect.has(target, key);
      if (!isSymbol$1(key) || !builtInSymbols.has(key)) {
        track(target, "has", key);
      }
      return result;
    }
    ownKeys(target) {
      track(
        target,
        "iterate",
        isArray$1(target) ? "length" : ITERATE_KEY
      );
      return Reflect.ownKeys(target);
    }
  }
  class ReadonlyReactiveHandler extends BaseReactiveHandler {
    constructor(isShallow2 = false) {
      super(true, isShallow2);
    }
    set(target, key) {
      return true;
    }
    deleteProperty(target, key) {
      return true;
    }
  }
  const mutableHandlers = /* @__PURE__ */ new MutableReactiveHandler();
  const readonlyHandlers = /* @__PURE__ */ new ReadonlyReactiveHandler();
  const shallowReactiveHandlers = /* @__PURE__ */ new MutableReactiveHandler(true);
  const shallowReadonlyHandlers = /* @__PURE__ */ new ReadonlyReactiveHandler(true);
  const toShallow = (value) => value;
  const getProto = (v) => Reflect.getPrototypeOf(v);
  function createIterableMethod(method, isReadonly2, isShallow2) {
    return function(...args) {
      const target = this["__v_raw"];
      const rawTarget = /* @__PURE__ */ toRaw(target);
      const targetIsMap = isMap(rawTarget);
      const isPair = method === "entries" || method === Symbol.iterator && targetIsMap;
      const isKeyOnly = method === "keys" && targetIsMap;
      const innerIterator = target[method](...args);
      const wrap = isShallow2 ? toShallow : isReadonly2 ? toReadonly : toReactive;
      !isReadonly2 && track(
        rawTarget,
        "iterate",
        isKeyOnly ? MAP_KEY_ITERATE_KEY : ITERATE_KEY
      );
      return extend(
        // inheriting all iterator properties
        Object.create(innerIterator),
        {
          // iterator protocol
          next() {
            const { value, done } = innerIterator.next();
            return done ? { value, done } : {
              value: isPair ? [wrap(value[0]), wrap(value[1])] : wrap(value),
              done
            };
          }
        }
      );
    };
  }
  function createReadonlyMethod(type) {
    return function(...args) {
      return type === "delete" ? false : type === "clear" ? void 0 : this;
    };
  }
  function createInstrumentations(readonly2, shallow) {
    const instrumentations = {
      get(key) {
        const target = this["__v_raw"];
        const rawTarget = /* @__PURE__ */ toRaw(target);
        const rawKey = /* @__PURE__ */ toRaw(key);
        if (!readonly2) {
          if (hasChanged(key, rawKey)) {
            track(rawTarget, "get", key);
          }
          track(rawTarget, "get", rawKey);
        }
        const { has } = getProto(rawTarget);
        const wrap = shallow ? toShallow : readonly2 ? toReadonly : toReactive;
        if (has.call(rawTarget, key)) {
          return wrap(target.get(key));
        } else if (has.call(rawTarget, rawKey)) {
          return wrap(target.get(rawKey));
        } else if (target !== rawTarget) {
          target.get(key);
        }
      },
      get size() {
        const target = this["__v_raw"];
        !readonly2 && track(/* @__PURE__ */ toRaw(target), "iterate", ITERATE_KEY);
        return target.size;
      },
      has(key) {
        const target = this["__v_raw"];
        const rawTarget = /* @__PURE__ */ toRaw(target);
        const rawKey = /* @__PURE__ */ toRaw(key);
        if (!readonly2) {
          if (hasChanged(key, rawKey)) {
            track(rawTarget, "has", key);
          }
          track(rawTarget, "has", rawKey);
        }
        return key === rawKey ? target.has(key) : target.has(key) || target.has(rawKey);
      },
      forEach(callback, thisArg) {
        const observed = this;
        const target = observed["__v_raw"];
        const rawTarget = /* @__PURE__ */ toRaw(target);
        const wrap = shallow ? toShallow : readonly2 ? toReadonly : toReactive;
        !readonly2 && track(rawTarget, "iterate", ITERATE_KEY);
        return target.forEach((value, key) => {
          return callback.call(thisArg, wrap(value), wrap(key), observed);
        });
      }
    };
    extend(
      instrumentations,
      readonly2 ? {
        add: createReadonlyMethod("add"),
        set: createReadonlyMethod("set"),
        delete: createReadonlyMethod("delete"),
        clear: createReadonlyMethod("clear")
      } : {
        add(value) {
          const target = /* @__PURE__ */ toRaw(this);
          const proto = getProto(target);
          const rawValue = /* @__PURE__ */ toRaw(value);
          const valueToAdd = !shallow && !/* @__PURE__ */ isShallow(value) && !/* @__PURE__ */ isReadonly(value) ? rawValue : value;
          const hadKey = proto.has.call(target, valueToAdd) || hasChanged(value, valueToAdd) && proto.has.call(target, value) || hasChanged(rawValue, valueToAdd) && proto.has.call(target, rawValue);
          if (!hadKey) {
            target.add(valueToAdd);
            trigger$1(target, "add", valueToAdd, valueToAdd);
          }
          return this;
        },
        set(key, value) {
          if (!shallow && !/* @__PURE__ */ isShallow(value) && !/* @__PURE__ */ isReadonly(value)) {
            value = /* @__PURE__ */ toRaw(value);
          }
          const target = /* @__PURE__ */ toRaw(this);
          const { has, get: get2 } = getProto(target);
          let hadKey = has.call(target, key);
          if (!hadKey) {
            key = /* @__PURE__ */ toRaw(key);
            hadKey = has.call(target, key);
          }
          const oldValue = get2.call(target, key);
          target.set(key, value);
          if (!hadKey) {
            trigger$1(target, "add", key, value);
          } else if (hasChanged(value, oldValue)) {
            trigger$1(target, "set", key, value);
          }
          return this;
        },
        delete(key) {
          const target = /* @__PURE__ */ toRaw(this);
          const { has, get: get2 } = getProto(target);
          let hadKey = has.call(target, key);
          if (!hadKey) {
            key = /* @__PURE__ */ toRaw(key);
            hadKey = has.call(target, key);
          }
          get2 ? get2.call(target, key) : void 0;
          const result = target.delete(key);
          if (hadKey) {
            trigger$1(target, "delete", key, void 0);
          }
          return result;
        },
        clear() {
          const target = /* @__PURE__ */ toRaw(this);
          const hadItems = target.size !== 0;
          const result = target.clear();
          if (hadItems) {
            trigger$1(
              target,
              "clear",
              void 0,
              void 0
            );
          }
          return result;
        }
      }
    );
    const iteratorMethods = [
      "keys",
      "values",
      "entries",
      Symbol.iterator
    ];
    iteratorMethods.forEach((method) => {
      instrumentations[method] = createIterableMethod(method, readonly2, shallow);
    });
    return instrumentations;
  }
  function createInstrumentationGetter(isReadonly2, shallow) {
    const instrumentations = createInstrumentations(isReadonly2, shallow);
    return (target, key, receiver) => {
      if (key === "__v_isReactive") {
        return !isReadonly2;
      } else if (key === "__v_isReadonly") {
        return isReadonly2;
      } else if (key === "__v_raw") {
        return target;
      }
      return Reflect.get(
        hasOwn(instrumentations, key) && key in target ? instrumentations : target,
        key,
        receiver
      );
    };
  }
  const mutableCollectionHandlers = {
    get: /* @__PURE__ */ createInstrumentationGetter(false, false)
  };
  const shallowCollectionHandlers = {
    get: /* @__PURE__ */ createInstrumentationGetter(false, true)
  };
  const readonlyCollectionHandlers = {
    get: /* @__PURE__ */ createInstrumentationGetter(true, false)
  };
  const shallowReadonlyCollectionHandlers = {
    get: /* @__PURE__ */ createInstrumentationGetter(true, true)
  };
  const reactiveMap = /* @__PURE__ */ new WeakMap();
  const shallowReactiveMap = /* @__PURE__ */ new WeakMap();
  const readonlyMap = /* @__PURE__ */ new WeakMap();
  const shallowReadonlyMap = /* @__PURE__ */ new WeakMap();
  function targetTypeMap(rawType) {
    switch (rawType) {
      case "Object":
      case "Array":
        return 1;
      case "Map":
      case "Set":
      case "WeakMap":
      case "WeakSet":
        return 2;
      default:
        return 0;
    }
  }
  function getTargetType(value) {
    return value["__v_skip"] || !Object.isExtensible(value) ? 0 : targetTypeMap(toRawType(value));
  }
  // @__NO_SIDE_EFFECTS__
  function reactive(target) {
    if (/* @__PURE__ */ isReadonly(target)) {
      return target;
    }
    return createReactiveObject(
      target,
      false,
      mutableHandlers,
      mutableCollectionHandlers,
      reactiveMap
    );
  }
  // @__NO_SIDE_EFFECTS__
  function shallowReactive(target) {
    return createReactiveObject(
      target,
      false,
      shallowReactiveHandlers,
      shallowCollectionHandlers,
      shallowReactiveMap
    );
  }
  // @__NO_SIDE_EFFECTS__
  function readonly(target) {
    return createReactiveObject(
      target,
      true,
      readonlyHandlers,
      readonlyCollectionHandlers,
      readonlyMap
    );
  }
  // @__NO_SIDE_EFFECTS__
  function shallowReadonly(target) {
    return createReactiveObject(
      target,
      true,
      shallowReadonlyHandlers,
      shallowReadonlyCollectionHandlers,
      shallowReadonlyMap
    );
  }
  function createReactiveObject(target, isReadonly2, baseHandlers, collectionHandlers, proxyMap) {
    if (!isObject$1(target)) {
      return target;
    }
    if (target["__v_raw"] && !(isReadonly2 && target["__v_isReactive"])) {
      return target;
    }
    const targetType = getTargetType(target);
    if (targetType === 0) {
      return target;
    }
    const existingProxy = proxyMap.get(target);
    if (existingProxy) {
      return existingProxy;
    }
    const proxy = new Proxy(
      target,
      targetType === 2 ? collectionHandlers : baseHandlers
    );
    proxyMap.set(target, proxy);
    return proxy;
  }
  // @__NO_SIDE_EFFECTS__
  function isReactive(value) {
    if (/* @__PURE__ */ isReadonly(value)) {
      return /* @__PURE__ */ isReactive(value["__v_raw"]);
    }
    return !!(value && value["__v_isReactive"]);
  }
  // @__NO_SIDE_EFFECTS__
  function isReadonly(value) {
    return !!(value && value["__v_isReadonly"]);
  }
  // @__NO_SIDE_EFFECTS__
  function isShallow(value) {
    return !!(value && value["__v_isShallow"]);
  }
  // @__NO_SIDE_EFFECTS__
  function isProxy(value) {
    return value ? !!value["__v_raw"] : false;
  }
  // @__NO_SIDE_EFFECTS__
  function toRaw(observed) {
    const raw = observed && observed["__v_raw"];
    return raw ? /* @__PURE__ */ toRaw(raw) : observed;
  }
  function markRaw(value) {
    if (!hasOwn(value, "__v_skip") && Object.isExtensible(value)) {
      def(value, "__v_skip", true);
    }
    return value;
  }
  const toReactive = (value) => isObject$1(value) ? /* @__PURE__ */ reactive(value) : value;
  const toReadonly = (value) => isObject$1(value) ? /* @__PURE__ */ readonly(value) : value;
  // @__NO_SIDE_EFFECTS__
  function isRef(r) {
    return r ? r["__v_isRef"] === true : false;
  }
  // @__NO_SIDE_EFFECTS__
  function ref(value) {
    return createRef(value, false);
  }
  // @__NO_SIDE_EFFECTS__
  function shallowRef(value) {
    return createRef(value, true);
  }
  function createRef(rawValue, shallow) {
    if (/* @__PURE__ */ isRef(rawValue)) {
      return rawValue;
    }
    return new RefImpl(rawValue, shallow);
  }
  class RefImpl {
    constructor(value, isShallow2) {
      this.dep = new Dep();
      this["__v_isRef"] = true;
      this["__v_isShallow"] = false;
      this._rawValue = isShallow2 ? value : /* @__PURE__ */ toRaw(value);
      this._value = isShallow2 ? value : toReactive(value);
      this["__v_isShallow"] = isShallow2;
    }
    get value() {
      {
        this.dep.track();
      }
      return this._value;
    }
    set value(newValue) {
      const oldValue = this._rawValue;
      const useDirectValue = this["__v_isShallow"] || /* @__PURE__ */ isShallow(newValue) || /* @__PURE__ */ isReadonly(newValue);
      newValue = useDirectValue ? newValue : /* @__PURE__ */ toRaw(newValue);
      if (hasChanged(newValue, oldValue)) {
        this._rawValue = newValue;
        this._value = useDirectValue ? newValue : toReactive(newValue);
        {
          this.dep.trigger();
        }
      }
    }
  }
  function unref(ref2) {
    return /* @__PURE__ */ isRef(ref2) ? ref2.value : ref2;
  }
  const shallowUnwrapHandlers = {
    get: (target, key, receiver) => key === "__v_raw" ? target : unref(Reflect.get(target, key, receiver)),
    set: (target, key, value, receiver) => {
      const oldValue = target[key];
      if (/* @__PURE__ */ isRef(oldValue) && !/* @__PURE__ */ isRef(value)) {
        oldValue.value = value;
        return true;
      } else {
        return Reflect.set(target, key, value, receiver);
      }
    }
  };
  function proxyRefs(objectWithRefs) {
    return /* @__PURE__ */ isReactive(objectWithRefs) ? objectWithRefs : new Proxy(objectWithRefs, shallowUnwrapHandlers);
  }
  class ObjectRefImpl {
    constructor(_object, _key, _defaultValue) {
      this._object = _object;
      this._key = _key;
      this._defaultValue = _defaultValue;
      this["__v_isRef"] = true;
      this._value = void 0;
      this._raw = /* @__PURE__ */ toRaw(_object);
      let shallow = true;
      let obj = _object;
      if (!isArray$1(_object) || !isIntegerKey(String(_key))) {
        do {
          shallow = !/* @__PURE__ */ isProxy(obj) || /* @__PURE__ */ isShallow(obj);
        } while (shallow && (obj = obj["__v_raw"]));
      }
      this._shallow = shallow;
    }
    get value() {
      let val = this._object[this._key];
      if (this._shallow) {
        val = unref(val);
      }
      return this._value = val === void 0 ? this._defaultValue : val;
    }
    set value(newVal) {
      if (this._shallow && /* @__PURE__ */ isRef(this._raw[this._key])) {
        const nestedRef = this._object[this._key];
        if (/* @__PURE__ */ isRef(nestedRef)) {
          nestedRef.value = newVal;
          return;
        }
      }
      this._object[this._key] = newVal;
    }
    get dep() {
      return getDepFromReactive(this._raw, this._key);
    }
  }
  class GetterRefImpl {
    constructor(_getter) {
      this._getter = _getter;
      this["__v_isRef"] = true;
      this["__v_isReadonly"] = true;
      this._value = void 0;
    }
    get value() {
      return this._value = this._getter();
    }
  }
  // @__NO_SIDE_EFFECTS__
  function toRef(source, key, defaultValue) {
    if (/* @__PURE__ */ isRef(source)) {
      return source;
    } else if (isFunction$1(source)) {
      return new GetterRefImpl(source);
    } else if (isObject$1(source) && arguments.length > 1) {
      return propertyToRef(source, key, defaultValue);
    } else {
      return /* @__PURE__ */ ref(source);
    }
  }
  function propertyToRef(source, key, defaultValue) {
    return new ObjectRefImpl(source, key, defaultValue);
  }
  class ComputedRefImpl {
    constructor(fn, setter, isSSR) {
      this.fn = fn;
      this.setter = setter;
      this._value = void 0;
      this.dep = new Dep(this);
      this.__v_isRef = true;
      this.deps = void 0;
      this.depsTail = void 0;
      this.flags = 16;
      this.globalVersion = globalVersion - 1;
      this.next = void 0;
      this.effect = this;
      this["__v_isReadonly"] = !setter;
      this.isSSR = isSSR;
    }
    /**
     * @internal
     */
    notify() {
      this.flags |= 16;
      if (!(this.flags & 8) && // avoid infinite self recursion
      activeSub !== this) {
        batch(this, true);
        return true;
      }
    }
    get value() {
      const link = this.dep.track();
      refreshComputed(this);
      if (link) {
        link.version = this.dep.version;
      }
      return this._value;
    }
    set value(newValue) {
      if (this.setter) {
        this.setter(newValue);
      }
    }
  }
  // @__NO_SIDE_EFFECTS__
  function computed$1(getterOrOptions, debugOptions, isSSR = false) {
    let getter;
    let setter;
    if (isFunction$1(getterOrOptions)) {
      getter = getterOrOptions;
    } else {
      getter = getterOrOptions.get;
      setter = getterOrOptions.set;
    }
    const cRef = new ComputedRefImpl(getter, setter, isSSR);
    return cRef;
  }
  const INITIAL_WATCHER_VALUE = {};
  const cleanupMap = /* @__PURE__ */ new WeakMap();
  let activeWatcher = void 0;
  function onWatcherCleanup(cleanupFn, failSilently = false, owner = activeWatcher) {
    if (owner) {
      let cleanups = cleanupMap.get(owner);
      if (!cleanups) cleanupMap.set(owner, cleanups = []);
      cleanups.push(cleanupFn);
    }
  }
  function watch$1(source, cb, options = EMPTY_OBJ) {
    const { immediate, deep, once, scheduler: scheduler2, augmentJob, call: call2 } = options;
    const reactiveGetter = (source2) => {
      if (deep) return source2;
      if (/* @__PURE__ */ isShallow(source2) || deep === false || deep === 0)
        return traverse(source2, 1);
      return traverse(source2);
    };
    let effect2;
    let getter;
    let cleanup;
    let boundCleanup;
    let forceTrigger = false;
    let isMultiSource = false;
    if (/* @__PURE__ */ isRef(source)) {
      getter = () => source.value;
      forceTrigger = /* @__PURE__ */ isShallow(source);
    } else if (/* @__PURE__ */ isReactive(source)) {
      getter = () => reactiveGetter(source);
      forceTrigger = true;
    } else if (isArray$1(source)) {
      isMultiSource = true;
      forceTrigger = source.some((s) => /* @__PURE__ */ isReactive(s) || /* @__PURE__ */ isShallow(s));
      getter = () => source.map((s) => {
        if (/* @__PURE__ */ isRef(s)) {
          return s.value;
        } else if (/* @__PURE__ */ isReactive(s)) {
          return reactiveGetter(s);
        } else if (isFunction$1(s)) {
          return call2 ? call2(s, 2) : s();
        } else ;
      });
    } else if (isFunction$1(source)) {
      if (cb) {
        getter = call2 ? () => call2(source, 2) : source;
      } else {
        getter = () => {
          if (cleanup) {
            pauseTracking();
            try {
              cleanup();
            } finally {
              resetTracking();
            }
          }
          const currentEffect = activeWatcher;
          activeWatcher = effect2;
          try {
            return call2 ? call2(source, 3, [boundCleanup]) : source(boundCleanup);
          } finally {
            activeWatcher = currentEffect;
          }
        };
      }
    } else {
      getter = NOOP;
    }
    if (cb && deep) {
      const baseGetter = getter;
      const depth = deep === true ? Infinity : deep;
      getter = () => traverse(baseGetter(), depth);
    }
    const scope = getCurrentScope();
    const watchHandle = () => {
      effect2.stop();
      if (scope && scope.active) {
        remove(scope.effects, effect2);
      }
    };
    if (once && cb) {
      const _cb = cb;
      cb = (...args) => {
        _cb(...args);
        watchHandle();
      };
    }
    let oldValue = isMultiSource ? new Array(source.length).fill(INITIAL_WATCHER_VALUE) : INITIAL_WATCHER_VALUE;
    const job = (immediateFirstRun) => {
      if (!(effect2.flags & 1) || !effect2.dirty && !immediateFirstRun) {
        return;
      }
      if (cb) {
        const newValue = effect2.run();
        if (deep || forceTrigger || (isMultiSource ? newValue.some((v, i) => hasChanged(v, oldValue[i])) : hasChanged(newValue, oldValue))) {
          if (cleanup) {
            cleanup();
          }
          const currentWatcher = activeWatcher;
          activeWatcher = effect2;
          try {
            const args = [
              newValue,
              // pass undefined as the old value when it's changed for the first time
              oldValue === INITIAL_WATCHER_VALUE ? void 0 : isMultiSource && oldValue[0] === INITIAL_WATCHER_VALUE ? [] : oldValue,
              boundCleanup
            ];
            oldValue = newValue;
            call2 ? call2(cb, 3, args) : (
              // @ts-expect-error
              cb(...args)
            );
          } finally {
            activeWatcher = currentWatcher;
          }
        }
      } else {
        effect2.run();
      }
    };
    if (augmentJob) {
      augmentJob(job);
    }
    effect2 = new ReactiveEffect(getter);
    effect2.scheduler = scheduler2 ? () => scheduler2(job, false) : job;
    boundCleanup = (fn) => onWatcherCleanup(fn, false, effect2);
    cleanup = effect2.onStop = () => {
      const cleanups = cleanupMap.get(effect2);
      if (cleanups) {
        if (call2) {
          call2(cleanups, 4);
        } else {
          for (const cleanup2 of cleanups) cleanup2();
        }
        cleanupMap.delete(effect2);
      }
    };
    if (cb) {
      if (immediate) {
        job(true);
      } else {
        oldValue = effect2.run();
      }
    } else if (scheduler2) {
      scheduler2(job.bind(null, true), true);
    } else {
      effect2.run();
    }
    watchHandle.pause = effect2.pause.bind(effect2);
    watchHandle.resume = effect2.resume.bind(effect2);
    watchHandle.stop = watchHandle;
    return watchHandle;
  }
  function traverse(value, depth = Infinity, seen) {
    if (depth <= 0 || !isObject$1(value) || value["__v_skip"]) {
      return value;
    }
    seen = seen || /* @__PURE__ */ new Map();
    if ((seen.get(value) || 0) >= depth) {
      return value;
    }
    seen.set(value, depth);
    depth--;
    if (/* @__PURE__ */ isRef(value)) {
      traverse(value.value, depth, seen);
    } else if (isArray$1(value)) {
      for (let i = 0; i < value.length; i++) {
        traverse(value[i], depth, seen);
      }
    } else if (isSet(value) || isMap(value)) {
      value.forEach((v) => {
        traverse(v, depth, seen);
      });
    } else if (isPlainObject$1(value)) {
      for (const key in value) {
        traverse(value[key], depth, seen);
      }
      for (const key of Object.getOwnPropertySymbols(value)) {
        if (Object.prototype.propertyIsEnumerable.call(value, key)) {
          traverse(value[key], depth, seen);
        }
      }
    }
    return value;
  }
  /**
  * @vue/runtime-core v3.5.30
  * (c) 2018-present Yuxi (Evan) You and Vue contributors
  * @license MIT
  **/
  const stack$1 = [];
  let isWarning = false;
  function warn$1$1(msg2, ...args) {
    if (isWarning) return;
    isWarning = true;
    pauseTracking();
    const instance = stack$1.length ? stack$1[stack$1.length - 1].component : null;
    const appWarnHandler = instance && instance.appContext.config.warnHandler;
    const trace = getComponentTrace();
    if (appWarnHandler) {
      callWithErrorHandling(
        appWarnHandler,
        instance,
        11,
        [
          // eslint-disable-next-line no-restricted-syntax
          msg2 + args.map((a) => {
            var _a2, _b;
            return (_b = (_a2 = a.toString) == null ? void 0 : _a2.call(a)) != null ? _b : JSON.stringify(a);
          }).join(""),
          instance && instance.proxy,
          trace.map(
            ({ vnode }) => `at <${formatComponentName(instance, vnode.type)}>`
          ).join("\n"),
          trace
        ]
      );
    } else {
      const warnArgs = [`[Vue warn]: ${msg2}`, ...args];
      if (trace.length && // avoid spamming console during tests
      true) {
        warnArgs.push(`
`, ...formatTrace(trace));
      }
      console.warn(...warnArgs);
    }
    resetTracking();
    isWarning = false;
  }
  function getComponentTrace() {
    let currentVNode = stack$1[stack$1.length - 1];
    if (!currentVNode) {
      return [];
    }
    const normalizedStack = [];
    while (currentVNode) {
      const last = normalizedStack[0];
      if (last && last.vnode === currentVNode) {
        last.recurseCount++;
      } else {
        normalizedStack.push({
          vnode: currentVNode,
          recurseCount: 0
        });
      }
      const parentInstance = currentVNode.component && currentVNode.component.parent;
      currentVNode = parentInstance && parentInstance.vnode;
    }
    return normalizedStack;
  }
  function formatTrace(trace) {
    const logs = [];
    trace.forEach((entry, i) => {
      logs.push(...i === 0 ? [] : [`
`], ...formatTraceEntry(entry));
    });
    return logs;
  }
  function formatTraceEntry({ vnode, recurseCount }) {
    const postfix = recurseCount > 0 ? `... (${recurseCount} recursive calls)` : ``;
    const isRoot = vnode.component ? vnode.component.parent == null : false;
    const open = ` at <${formatComponentName(
      vnode.component,
      vnode.type,
      isRoot
    )}`;
    const close = `>` + postfix;
    return vnode.props ? [open, ...formatProps(vnode.props), close] : [open + close];
  }
  function formatProps(props) {
    const res = [];
    const keys2 = Object.keys(props);
    keys2.slice(0, 3).forEach((key) => {
      res.push(...formatProp(key, props[key]));
    });
    if (keys2.length > 3) {
      res.push(` ...`);
    }
    return res;
  }
  function formatProp(key, value, raw) {
    if (isString(value)) {
      value = JSON.stringify(value);
      return raw ? value : [`${key}=${value}`];
    } else if (typeof value === "number" || typeof value === "boolean" || value == null) {
      return raw ? value : [`${key}=${value}`];
    } else if (/* @__PURE__ */ isRef(value)) {
      value = formatProp(key, /* @__PURE__ */ toRaw(value.value), true);
      return raw ? value : [`${key}=Ref<`, value, `>`];
    } else if (isFunction$1(value)) {
      return [`${key}=fn${value.name ? `<${value.name}>` : ``}`];
    } else {
      value = /* @__PURE__ */ toRaw(value);
      return raw ? value : [`${key}=`, value];
    }
  }
  function callWithErrorHandling(fn, instance, type, args) {
    try {
      return args ? fn(...args) : fn();
    } catch (err) {
      handleError(err, instance, type);
    }
  }
  function callWithAsyncErrorHandling(fn, instance, type, args) {
    if (isFunction$1(fn)) {
      const res = callWithErrorHandling(fn, instance, type, args);
      if (res && isPromise(res)) {
        res.catch((err) => {
          handleError(err, instance, type);
        });
      }
      return res;
    }
    if (isArray$1(fn)) {
      const values = [];
      for (let i = 0; i < fn.length; i++) {
        values.push(callWithAsyncErrorHandling(fn[i], instance, type, args));
      }
      return values;
    }
  }
  function handleError(err, instance, type, throwInDev = true) {
    const contextVNode = instance ? instance.vnode : null;
    const { errorHandler, throwUnhandledErrorInProduction } = instance && instance.appContext.config || EMPTY_OBJ;
    if (instance) {
      let cur = instance.parent;
      const exposedInstance = instance.proxy;
      const errorInfo = `https://vuejs.org/error-reference/#runtime-${type}`;
      while (cur) {
        const errorCapturedHooks = cur.ec;
        if (errorCapturedHooks) {
          for (let i = 0; i < errorCapturedHooks.length; i++) {
            if (errorCapturedHooks[i](err, exposedInstance, errorInfo) === false) {
              return;
            }
          }
        }
        cur = cur.parent;
      }
      if (errorHandler) {
        pauseTracking();
        callWithErrorHandling(errorHandler, null, 10, [
          err,
          exposedInstance,
          errorInfo
        ]);
        resetTracking();
        return;
      }
    }
    logError(err, type, contextVNode, throwInDev, throwUnhandledErrorInProduction);
  }
  function logError(err, type, contextVNode, throwInDev = true, throwInProd = false) {
    if (throwInProd) {
      throw err;
    } else {
      console.error(err);
    }
  }
  const queue = [];
  let flushIndex = -1;
  const pendingPostFlushCbs = [];
  let activePostFlushCbs = null;
  let postFlushIndex = 0;
  const resolvedPromise = /* @__PURE__ */ Promise.resolve();
  let currentFlushPromise = null;
  function nextTick(fn) {
    const p2 = currentFlushPromise || resolvedPromise;
    return fn ? p2.then(this ? fn.bind(this) : fn) : p2;
  }
  function findInsertionIndex(id) {
    let start = flushIndex + 1;
    let end = queue.length;
    while (start < end) {
      const middle = start + end >>> 1;
      const middleJob = queue[middle];
      const middleJobId = getId(middleJob);
      if (middleJobId < id || middleJobId === id && middleJob.flags & 2) {
        start = middle + 1;
      } else {
        end = middle;
      }
    }
    return start;
  }
  function queueJob(job) {
    if (!(job.flags & 1)) {
      const jobId = getId(job);
      const lastJob = queue[queue.length - 1];
      if (!lastJob || // fast path when the job id is larger than the tail
      !(job.flags & 2) && jobId >= getId(lastJob)) {
        queue.push(job);
      } else {
        queue.splice(findInsertionIndex(jobId), 0, job);
      }
      job.flags |= 1;
      queueFlush();
    }
  }
  function queueFlush() {
    if (!currentFlushPromise) {
      currentFlushPromise = resolvedPromise.then(flushJobs);
    }
  }
  function queuePostFlushCb(cb) {
    if (!isArray$1(cb)) {
      if (activePostFlushCbs && cb.id === -1) {
        activePostFlushCbs.splice(postFlushIndex + 1, 0, cb);
      } else if (!(cb.flags & 1)) {
        pendingPostFlushCbs.push(cb);
        cb.flags |= 1;
      }
    } else {
      pendingPostFlushCbs.push(...cb);
    }
    queueFlush();
  }
  function flushPreFlushCbs(instance, seen, i = flushIndex + 1) {
    for (; i < queue.length; i++) {
      const cb = queue[i];
      if (cb && cb.flags & 2) {
        if (instance && cb.id !== instance.uid) {
          continue;
        }
        queue.splice(i, 1);
        i--;
        if (cb.flags & 4) {
          cb.flags &= -2;
        }
        cb();
        if (!(cb.flags & 4)) {
          cb.flags &= -2;
        }
      }
    }
  }
  function flushPostFlushCbs(seen) {
    if (pendingPostFlushCbs.length) {
      const deduped = [...new Set(pendingPostFlushCbs)].sort(
        (a, b) => getId(a) - getId(b)
      );
      pendingPostFlushCbs.length = 0;
      if (activePostFlushCbs) {
        activePostFlushCbs.push(...deduped);
        return;
      }
      activePostFlushCbs = deduped;
      for (postFlushIndex = 0; postFlushIndex < activePostFlushCbs.length; postFlushIndex++) {
        const cb = activePostFlushCbs[postFlushIndex];
        if (cb.flags & 4) {
          cb.flags &= -2;
        }
        if (!(cb.flags & 8)) cb();
        cb.flags &= -2;
      }
      activePostFlushCbs = null;
      postFlushIndex = 0;
    }
  }
  const getId = (job) => job.id == null ? job.flags & 2 ? -1 : Infinity : job.id;
  function flushJobs(seen) {
    try {
      for (flushIndex = 0; flushIndex < queue.length; flushIndex++) {
        const job = queue[flushIndex];
        if (job && !(job.flags & 8)) {
          if (false) ;
          if (job.flags & 4) {
            job.flags &= ~1;
          }
          callWithErrorHandling(
            job,
            job.i,
            job.i ? 15 : 14
          );
          if (!(job.flags & 4)) {
            job.flags &= ~1;
          }
        }
      }
    } finally {
      for (; flushIndex < queue.length; flushIndex++) {
        const job = queue[flushIndex];
        if (job) {
          job.flags &= -2;
        }
      }
      flushIndex = -1;
      queue.length = 0;
      flushPostFlushCbs();
      currentFlushPromise = null;
      if (queue.length || pendingPostFlushCbs.length) {
        flushJobs();
      }
    }
  }
  let currentRenderingInstance = null;
  let currentScopeId = null;
  function setCurrentRenderingInstance(instance) {
    const prev = currentRenderingInstance;
    currentRenderingInstance = instance;
    currentScopeId = instance && instance.type.__scopeId || null;
    return prev;
  }
  function withCtx(fn, ctx2 = currentRenderingInstance, isNonScopedSlot) {
    if (!ctx2) return fn;
    if (fn._n) {
      return fn;
    }
    const renderFnWithContext = (...args) => {
      if (renderFnWithContext._d) {
        setBlockTracking(-1);
      }
      const prevInstance = setCurrentRenderingInstance(ctx2);
      let res;
      try {
        res = fn(...args);
      } finally {
        setCurrentRenderingInstance(prevInstance);
        if (renderFnWithContext._d) {
          setBlockTracking(1);
        }
      }
      return res;
    };
    renderFnWithContext._n = true;
    renderFnWithContext._c = true;
    renderFnWithContext._d = true;
    return renderFnWithContext;
  }
  function withDirectives(vnode, directives) {
    if (currentRenderingInstance === null) {
      return vnode;
    }
    const instance = getComponentPublicInstance(currentRenderingInstance);
    const bindings = vnode.dirs || (vnode.dirs = []);
    for (let i = 0; i < directives.length; i++) {
      let [dir, value, arg, modifiers = EMPTY_OBJ] = directives[i];
      if (dir) {
        if (isFunction$1(dir)) {
          dir = {
            mounted: dir,
            updated: dir
          };
        }
        if (dir.deep) {
          traverse(value);
        }
        bindings.push({
          dir,
          instance,
          value,
          oldValue: void 0,
          arg,
          modifiers
        });
      }
    }
    return vnode;
  }
  function invokeDirectiveHook(vnode, prevVNode, instance, name) {
    const bindings = vnode.dirs;
    const oldBindings = prevVNode && prevVNode.dirs;
    for (let i = 0; i < bindings.length; i++) {
      const binding = bindings[i];
      if (oldBindings) {
        binding.oldValue = oldBindings[i].value;
      }
      let hook = binding.dir[name];
      if (hook) {
        pauseTracking();
        callWithAsyncErrorHandling(hook, instance, 8, [
          vnode.el,
          binding,
          vnode,
          prevVNode
        ]);
        resetTracking();
      }
    }
  }
  function provide(key, value) {
    if (currentInstance) {
      let provides = currentInstance.provides;
      const parentProvides = currentInstance.parent && currentInstance.parent.provides;
      if (parentProvides === provides) {
        provides = currentInstance.provides = Object.create(parentProvides);
      }
      provides[key] = value;
    }
  }
  function inject(key, defaultValue, treatDefaultAsFactory = false) {
    const instance = getCurrentInstance();
    if (instance || currentApp) {
      let provides = currentApp ? currentApp._context.provides : instance ? instance.parent == null || instance.ce ? instance.vnode.appContext && instance.vnode.appContext.provides : instance.parent.provides : void 0;
      if (provides && key in provides) {
        return provides[key];
      } else if (arguments.length > 1) {
        return treatDefaultAsFactory && isFunction$1(defaultValue) ? defaultValue.call(instance && instance.proxy) : defaultValue;
      } else ;
    }
  }
  const ssrContextKey$1 = /* @__PURE__ */ Symbol.for("v-scx");
  const useSSRContext = () => {
    {
      const ctx2 = inject(ssrContextKey$1);
      return ctx2;
    }
  };
  function watchEffect(effect2, options) {
    return doWatch(effect2, null, options);
  }
  function watch(source, cb, options) {
    return doWatch(source, cb, options);
  }
  function doWatch(source, cb, options = EMPTY_OBJ) {
    const { immediate, deep, flush, once } = options;
    const baseWatchOptions = extend({}, options);
    const runsImmediately = cb && immediate || !cb && flush !== "post";
    let ssrCleanup;
    if (isInSSRComponentSetup) {
      if (flush === "sync") {
        const ctx2 = useSSRContext();
        ssrCleanup = ctx2.__watcherHandles || (ctx2.__watcherHandles = []);
      } else if (!runsImmediately) {
        const watchStopHandle = () => {
        };
        watchStopHandle.stop = NOOP;
        watchStopHandle.resume = NOOP;
        watchStopHandle.pause = NOOP;
        return watchStopHandle;
      }
    }
    const instance = currentInstance;
    baseWatchOptions.call = (fn, type, args) => callWithAsyncErrorHandling(fn, instance, type, args);
    let isPre = false;
    if (flush === "post") {
      baseWatchOptions.scheduler = (job) => {
        queuePostRenderEffect(job, instance && instance.suspense);
      };
    } else if (flush !== "sync") {
      isPre = true;
      baseWatchOptions.scheduler = (job, isFirstRun) => {
        if (isFirstRun) {
          job();
        } else {
          queueJob(job);
        }
      };
    }
    baseWatchOptions.augmentJob = (job) => {
      if (cb) {
        job.flags |= 4;
      }
      if (isPre) {
        job.flags |= 2;
        if (instance) {
          job.id = instance.uid;
          job.i = instance;
        }
      }
    };
    const watchHandle = watch$1(source, cb, baseWatchOptions);
    if (isInSSRComponentSetup) {
      if (ssrCleanup) {
        ssrCleanup.push(watchHandle);
      } else if (runsImmediately) {
        watchHandle();
      }
    }
    return watchHandle;
  }
  function instanceWatch(source, value, options) {
    const publicThis = this.proxy;
    const getter = isString(source) ? source.includes(".") ? createPathGetter(publicThis, source) : () => publicThis[source] : source.bind(publicThis, publicThis);
    let cb;
    if (isFunction$1(value)) {
      cb = value;
    } else {
      cb = value.handler;
      options = value;
    }
    const reset = setCurrentInstance(this);
    const res = doWatch(getter, cb.bind(publicThis), options);
    reset();
    return res;
  }
  function createPathGetter(ctx2, path) {
    const segments = path.split(".");
    return () => {
      let cur = ctx2;
      for (let i = 0; i < segments.length && cur; i++) {
        cur = cur[segments[i]];
      }
      return cur;
    };
  }
  const TeleportEndKey = /* @__PURE__ */ Symbol("_vte");
  const isTeleport = (type) => type.__isTeleport;
  const isTeleportDisabled = (props) => props && (props.disabled || props.disabled === "");
  const isTeleportDeferred = (props) => props && (props.defer || props.defer === "");
  const isTargetSVG = (target) => typeof SVGElement !== "undefined" && target instanceof SVGElement;
  const isTargetMathML = (target) => typeof MathMLElement === "function" && target instanceof MathMLElement;
  const resolveTarget = (props, select) => {
    const targetSelector = props && props.to;
    if (isString(targetSelector)) {
      if (!select) {
        return null;
      } else {
        const target = select(targetSelector);
        return target;
      }
    } else {
      return targetSelector;
    }
  };
  const TeleportImpl = {
    name: "Teleport",
    __isTeleport: true,
    process(n1, n2, container, anchor, parentComponent, parentSuspense, namespace2, slotScopeIds, optimized, internals) {
      const {
        mc: mountChildren,
        pc: patchChildren,
        pbc: patchBlockChildren,
        o: { insert, querySelector, createText, createComment }
      } = internals;
      const disabled = isTeleportDisabled(n2.props);
      let { shapeFlag, children, dynamicChildren } = n2;
      if (n1 == null) {
        const placeholder = n2.el = createText("");
        const mainAnchor = n2.anchor = createText("");
        insert(placeholder, container, anchor);
        insert(mainAnchor, container, anchor);
        const mount2 = (container2, anchor2) => {
          if (shapeFlag & 16) {
            mountChildren(
              children,
              container2,
              anchor2,
              parentComponent,
              parentSuspense,
              namespace2,
              slotScopeIds,
              optimized
            );
          }
        };
        const mountToTarget = () => {
          const target = n2.target = resolveTarget(n2.props, querySelector);
          const targetAnchor = prepareAnchor(target, n2, createText, insert);
          if (target) {
            if (namespace2 !== "svg" && isTargetSVG(target)) {
              namespace2 = "svg";
            } else if (namespace2 !== "mathml" && isTargetMathML(target)) {
              namespace2 = "mathml";
            }
            if (parentComponent && parentComponent.isCE) {
              (parentComponent.ce._teleportTargets || (parentComponent.ce._teleportTargets = /* @__PURE__ */ new Set())).add(target);
            }
            if (!disabled) {
              mount2(target, targetAnchor);
              updateCssVars(n2, false);
            }
          }
        };
        if (disabled) {
          mount2(container, mainAnchor);
          updateCssVars(n2, true);
        }
        if (isTeleportDeferred(n2.props)) {
          n2.el.__isMounted = false;
          queuePostRenderEffect(() => {
            mountToTarget();
            delete n2.el.__isMounted;
          }, parentSuspense);
        } else {
          mountToTarget();
        }
      } else {
        if (isTeleportDeferred(n2.props) && n1.el.__isMounted === false) {
          queuePostRenderEffect(() => {
            TeleportImpl.process(
              n1,
              n2,
              container,
              anchor,
              parentComponent,
              parentSuspense,
              namespace2,
              slotScopeIds,
              optimized,
              internals
            );
          }, parentSuspense);
          return;
        }
        n2.el = n1.el;
        n2.targetStart = n1.targetStart;
        const mainAnchor = n2.anchor = n1.anchor;
        const target = n2.target = n1.target;
        const targetAnchor = n2.targetAnchor = n1.targetAnchor;
        const wasDisabled = isTeleportDisabled(n1.props);
        const currentContainer = wasDisabled ? container : target;
        const currentAnchor = wasDisabled ? mainAnchor : targetAnchor;
        if (namespace2 === "svg" || isTargetSVG(target)) {
          namespace2 = "svg";
        } else if (namespace2 === "mathml" || isTargetMathML(target)) {
          namespace2 = "mathml";
        }
        if (dynamicChildren) {
          patchBlockChildren(
            n1.dynamicChildren,
            dynamicChildren,
            currentContainer,
            parentComponent,
            parentSuspense,
            namespace2,
            slotScopeIds
          );
          traverseStaticChildren(n1, n2, true);
        } else if (!optimized) {
          patchChildren(
            n1,
            n2,
            currentContainer,
            currentAnchor,
            parentComponent,
            parentSuspense,
            namespace2,
            slotScopeIds,
            false
          );
        }
        if (disabled) {
          if (!wasDisabled) {
            moveTeleport(
              n2,
              container,
              mainAnchor,
              internals,
              1
            );
          } else {
            if (n2.props && n1.props && n2.props.to !== n1.props.to) {
              n2.props.to = n1.props.to;
            }
          }
        } else {
          if ((n2.props && n2.props.to) !== (n1.props && n1.props.to)) {
            const nextTarget = n2.target = resolveTarget(
              n2.props,
              querySelector
            );
            if (nextTarget) {
              moveTeleport(
                n2,
                nextTarget,
                null,
                internals,
                0
              );
            }
          } else if (wasDisabled) {
            moveTeleport(
              n2,
              target,
              targetAnchor,
              internals,
              1
            );
          }
        }
        updateCssVars(n2, disabled);
      }
    },
    remove(vnode, parentComponent, parentSuspense, { um: unmount2, o: { remove: hostRemove } }, doRemove) {
      const {
        shapeFlag,
        children,
        anchor,
        targetStart,
        targetAnchor,
        target,
        props
      } = vnode;
      if (target) {
        hostRemove(targetStart);
        hostRemove(targetAnchor);
      }
      doRemove && hostRemove(anchor);
      if (shapeFlag & 16) {
        const shouldRemove = doRemove || !isTeleportDisabled(props);
        for (let i = 0; i < children.length; i++) {
          const child = children[i];
          unmount2(
            child,
            parentComponent,
            parentSuspense,
            shouldRemove,
            !!child.dynamicChildren
          );
        }
      }
    },
    move: moveTeleport,
    hydrate: hydrateTeleport
  };
  function moveTeleport(vnode, container, parentAnchor, { o: { insert }, m: move2 }, moveType = 2) {
    if (moveType === 0) {
      insert(vnode.targetAnchor, container, parentAnchor);
    }
    const { el, anchor, shapeFlag, children, props } = vnode;
    const isReorder = moveType === 2;
    if (isReorder) {
      insert(el, container, parentAnchor);
    }
    if (!isReorder || isTeleportDisabled(props)) {
      if (shapeFlag & 16) {
        for (let i = 0; i < children.length; i++) {
          move2(
            children[i],
            container,
            parentAnchor,
            2
          );
        }
      }
    }
    if (isReorder) {
      insert(anchor, container, parentAnchor);
    }
  }
  function hydrateTeleport(node, vnode, parentComponent, parentSuspense, slotScopeIds, optimized, {
    o: { nextSibling, parentNode, querySelector, insert, createText }
  }, hydrateChildren) {
    function hydrateAnchor(target2, targetNode) {
      let targetAnchor = targetNode;
      while (targetAnchor) {
        if (targetAnchor && targetAnchor.nodeType === 8) {
          if (targetAnchor.data === "teleport start anchor") {
            vnode.targetStart = targetAnchor;
          } else if (targetAnchor.data === "teleport anchor") {
            vnode.targetAnchor = targetAnchor;
            target2._lpa = vnode.targetAnchor && nextSibling(vnode.targetAnchor);
            break;
          }
        }
        targetAnchor = nextSibling(targetAnchor);
      }
    }
    function hydrateDisabledTeleport(node2, vnode2) {
      vnode2.anchor = hydrateChildren(
        nextSibling(node2),
        vnode2,
        parentNode(node2),
        parentComponent,
        parentSuspense,
        slotScopeIds,
        optimized
      );
    }
    const target = vnode.target = resolveTarget(
      vnode.props,
      querySelector
    );
    const disabled = isTeleportDisabled(vnode.props);
    if (target) {
      const targetNode = target._lpa || target.firstChild;
      if (vnode.shapeFlag & 16) {
        if (disabled) {
          hydrateDisabledTeleport(node, vnode);
          hydrateAnchor(target, targetNode);
          if (!vnode.targetAnchor) {
            prepareAnchor(
              target,
              vnode,
              createText,
              insert,
              // if target is the same as the main view, insert anchors before current node
              // to avoid hydrating mismatch
              parentNode(node) === target ? node : null
            );
          }
        } else {
          vnode.anchor = nextSibling(node);
          hydrateAnchor(target, targetNode);
          if (!vnode.targetAnchor) {
            prepareAnchor(target, vnode, createText, insert);
          }
          hydrateChildren(
            targetNode && nextSibling(targetNode),
            vnode,
            target,
            parentComponent,
            parentSuspense,
            slotScopeIds,
            optimized
          );
        }
      }
      updateCssVars(vnode, disabled);
    } else if (disabled) {
      if (vnode.shapeFlag & 16) {
        hydrateDisabledTeleport(node, vnode);
        vnode.targetStart = node;
        vnode.targetAnchor = nextSibling(node);
      }
    }
    return vnode.anchor && nextSibling(vnode.anchor);
  }
  const Teleport = TeleportImpl;
  function updateCssVars(vnode, isDisabled2) {
    const ctx2 = vnode.ctx;
    if (ctx2 && ctx2.ut) {
      let node, anchor;
      if (isDisabled2) {
        node = vnode.el;
        anchor = vnode.anchor;
      } else {
        node = vnode.targetStart;
        anchor = vnode.targetAnchor;
      }
      while (node && node !== anchor) {
        if (node.nodeType === 1) node.setAttribute("data-v-owner", ctx2.uid);
        node = node.nextSibling;
      }
      ctx2.ut();
    }
  }
  function prepareAnchor(target, vnode, createText, insert, anchor = null) {
    const targetStart = vnode.targetStart = createText("");
    const targetAnchor = vnode.targetAnchor = createText("");
    targetStart[TeleportEndKey] = targetAnchor;
    if (target) {
      insert(targetStart, target, anchor);
      insert(targetAnchor, target, anchor);
    }
    return targetAnchor;
  }
  const leaveCbKey = /* @__PURE__ */ Symbol("_leaveCb");
  const enterCbKey$1 = /* @__PURE__ */ Symbol("_enterCb");
  function useTransitionState() {
    const state = {
      isMounted: false,
      isLeaving: false,
      isUnmounting: false,
      leavingVNodes: /* @__PURE__ */ new Map()
    };
    onMounted(() => {
      state.isMounted = true;
    });
    onBeforeUnmount(() => {
      state.isUnmounting = true;
    });
    return state;
  }
  const TransitionHookValidator = [Function, Array];
  const BaseTransitionPropsValidators = {
    mode: String,
    appear: Boolean,
    persisted: Boolean,
    // enter
    onBeforeEnter: TransitionHookValidator,
    onEnter: TransitionHookValidator,
    onAfterEnter: TransitionHookValidator,
    onEnterCancelled: TransitionHookValidator,
    // leave
    onBeforeLeave: TransitionHookValidator,
    onLeave: TransitionHookValidator,
    onAfterLeave: TransitionHookValidator,
    onLeaveCancelled: TransitionHookValidator,
    // appear
    onBeforeAppear: TransitionHookValidator,
    onAppear: TransitionHookValidator,
    onAfterAppear: TransitionHookValidator,
    onAppearCancelled: TransitionHookValidator
  };
  const recursiveGetSubtree = (instance) => {
    const subTree = instance.subTree;
    return subTree.component ? recursiveGetSubtree(subTree.component) : subTree;
  };
  const BaseTransitionImpl = {
    name: `BaseTransition`,
    props: BaseTransitionPropsValidators,
    setup(props, { slots }) {
      const instance = getCurrentInstance();
      const state = useTransitionState();
      return () => {
        const children = slots.default && getTransitionRawChildren(slots.default(), true);
        if (!children || !children.length) {
          return;
        }
        const child = findNonCommentChild(children);
        const rawProps = /* @__PURE__ */ toRaw(props);
        const { mode } = rawProps;
        if (state.isLeaving) {
          return emptyPlaceholder(child);
        }
        const innerChild = getInnerChild$1(child);
        if (!innerChild) {
          return emptyPlaceholder(child);
        }
        let enterHooks = resolveTransitionHooks(
          innerChild,
          rawProps,
          state,
          instance,
          // #11061, ensure enterHooks is fresh after clone
          (hooks) => enterHooks = hooks
        );
        if (innerChild.type !== Comment) {
          setTransitionHooks(innerChild, enterHooks);
        }
        let oldInnerChild = instance.subTree && getInnerChild$1(instance.subTree);
        if (oldInnerChild && oldInnerChild.type !== Comment && !isSameVNodeType(oldInnerChild, innerChild) && recursiveGetSubtree(instance).type !== Comment) {
          let leavingHooks = resolveTransitionHooks(
            oldInnerChild,
            rawProps,
            state,
            instance
          );
          setTransitionHooks(oldInnerChild, leavingHooks);
          if (mode === "out-in" && innerChild.type !== Comment) {
            state.isLeaving = true;
            leavingHooks.afterLeave = () => {
              state.isLeaving = false;
              if (!(instance.job.flags & 8)) {
                instance.update();
              }
              delete leavingHooks.afterLeave;
              oldInnerChild = void 0;
            };
            return emptyPlaceholder(child);
          } else if (mode === "in-out" && innerChild.type !== Comment) {
            leavingHooks.delayLeave = (el, earlyRemove, delayedLeave) => {
              const leavingVNodesCache = getLeavingNodesForType(
                state,
                oldInnerChild
              );
              leavingVNodesCache[String(oldInnerChild.key)] = oldInnerChild;
              el[leaveCbKey] = () => {
                earlyRemove();
                el[leaveCbKey] = void 0;
                delete enterHooks.delayedLeave;
                oldInnerChild = void 0;
              };
              enterHooks.delayedLeave = () => {
                delayedLeave();
                delete enterHooks.delayedLeave;
                oldInnerChild = void 0;
              };
            };
          } else {
            oldInnerChild = void 0;
          }
        } else if (oldInnerChild) {
          oldInnerChild = void 0;
        }
        return child;
      };
    }
  };
  function findNonCommentChild(children) {
    let child = children[0];
    if (children.length > 1) {
      for (const c2 of children) {
        if (c2.type !== Comment) {
          child = c2;
          break;
        }
      }
    }
    return child;
  }
  const BaseTransition = BaseTransitionImpl;
  function getLeavingNodesForType(state, vnode) {
    const { leavingVNodes } = state;
    let leavingVNodesCache = leavingVNodes.get(vnode.type);
    if (!leavingVNodesCache) {
      leavingVNodesCache = /* @__PURE__ */ Object.create(null);
      leavingVNodes.set(vnode.type, leavingVNodesCache);
    }
    return leavingVNodesCache;
  }
  function resolveTransitionHooks(vnode, props, state, instance, postClone) {
    const {
      appear,
      mode,
      persisted = false,
      onBeforeEnter,
      onEnter,
      onAfterEnter,
      onEnterCancelled,
      onBeforeLeave,
      onLeave,
      onAfterLeave,
      onLeaveCancelled,
      onBeforeAppear,
      onAppear,
      onAfterAppear,
      onAppearCancelled
    } = props;
    const key = String(vnode.key);
    const leavingVNodesCache = getLeavingNodesForType(state, vnode);
    const callHook2 = (hook, args) => {
      hook && callWithAsyncErrorHandling(
        hook,
        instance,
        9,
        args
      );
    };
    const callAsyncHook = (hook, args) => {
      const done = args[1];
      callHook2(hook, args);
      if (isArray$1(hook)) {
        if (hook.every((hook2) => hook2.length <= 1)) done();
      } else if (hook.length <= 1) {
        done();
      }
    };
    const hooks = {
      mode,
      persisted,
      beforeEnter(el) {
        let hook = onBeforeEnter;
        if (!state.isMounted) {
          if (appear) {
            hook = onBeforeAppear || onBeforeEnter;
          } else {
            return;
          }
        }
        if (el[leaveCbKey]) {
          el[leaveCbKey](
            true
            /* cancelled */
          );
        }
        const leavingVNode = leavingVNodesCache[key];
        if (leavingVNode && isSameVNodeType(vnode, leavingVNode) && leavingVNode.el[leaveCbKey]) {
          leavingVNode.el[leaveCbKey]();
        }
        callHook2(hook, [el]);
      },
      enter(el) {
        if (leavingVNodesCache[key] === vnode) return;
        let hook = onEnter;
        let afterHook = onAfterEnter;
        let cancelHook = onEnterCancelled;
        if (!state.isMounted) {
          if (appear) {
            hook = onAppear || onEnter;
            afterHook = onAfterAppear || onAfterEnter;
            cancelHook = onAppearCancelled || onEnterCancelled;
          } else {
            return;
          }
        }
        let called = false;
        el[enterCbKey$1] = (cancelled) => {
          if (called) return;
          called = true;
          if (cancelled) {
            callHook2(cancelHook, [el]);
          } else {
            callHook2(afterHook, [el]);
          }
          if (hooks.delayedLeave) {
            hooks.delayedLeave();
          }
          el[enterCbKey$1] = void 0;
        };
        const done = el[enterCbKey$1].bind(null, false);
        if (hook) {
          callAsyncHook(hook, [el, done]);
        } else {
          done();
        }
      },
      leave(el, remove2) {
        const key2 = String(vnode.key);
        if (el[enterCbKey$1]) {
          el[enterCbKey$1](
            true
            /* cancelled */
          );
        }
        if (state.isUnmounting) {
          return remove2();
        }
        callHook2(onBeforeLeave, [el]);
        let called = false;
        el[leaveCbKey] = (cancelled) => {
          if (called) return;
          called = true;
          remove2();
          if (cancelled) {
            callHook2(onLeaveCancelled, [el]);
          } else {
            callHook2(onAfterLeave, [el]);
          }
          el[leaveCbKey] = void 0;
          if (leavingVNodesCache[key2] === vnode) {
            delete leavingVNodesCache[key2];
          }
        };
        const done = el[leaveCbKey].bind(null, false);
        leavingVNodesCache[key2] = vnode;
        if (onLeave) {
          callAsyncHook(onLeave, [el, done]);
        } else {
          done();
        }
      },
      clone(vnode2) {
        const hooks2 = resolveTransitionHooks(
          vnode2,
          props,
          state,
          instance,
          postClone
        );
        if (postClone) postClone(hooks2);
        return hooks2;
      }
    };
    return hooks;
  }
  function emptyPlaceholder(vnode) {
    if (isKeepAlive(vnode)) {
      vnode = cloneVNode(vnode);
      vnode.children = null;
      return vnode;
    }
  }
  function getInnerChild$1(vnode) {
    if (!isKeepAlive(vnode)) {
      if (isTeleport(vnode.type) && vnode.children) {
        return findNonCommentChild(vnode.children);
      }
      return vnode;
    }
    if (vnode.component) {
      return vnode.component.subTree;
    }
    const { shapeFlag, children } = vnode;
    if (children) {
      if (shapeFlag & 16) {
        return children[0];
      }
      if (shapeFlag & 32 && isFunction$1(children.default)) {
        return children.default();
      }
    }
  }
  function setTransitionHooks(vnode, hooks) {
    if (vnode.shapeFlag & 6 && vnode.component) {
      vnode.transition = hooks;
      setTransitionHooks(vnode.component.subTree, hooks);
    } else if (vnode.shapeFlag & 128) {
      vnode.ssContent.transition = hooks.clone(vnode.ssContent);
      vnode.ssFallback.transition = hooks.clone(vnode.ssFallback);
    } else {
      vnode.transition = hooks;
    }
  }
  function getTransitionRawChildren(children, keepComment = false, parentKey) {
    let ret = [];
    let keyedFragmentCount = 0;
    for (let i = 0; i < children.length; i++) {
      let child = children[i];
      const key = parentKey == null ? child.key : String(parentKey) + String(child.key != null ? child.key : i);
      if (child.type === Fragment) {
        if (child.patchFlag & 128) keyedFragmentCount++;
        ret = ret.concat(
          getTransitionRawChildren(child.children, keepComment, key)
        );
      } else if (keepComment || child.type !== Comment) {
        ret.push(key != null ? cloneVNode(child, { key }) : child);
      }
    }
    if (keyedFragmentCount > 1) {
      for (let i = 0; i < ret.length; i++) {
        ret[i].patchFlag = -2;
      }
    }
    return ret;
  }
  // @__NO_SIDE_EFFECTS__
  function defineComponent(options, extraOptions) {
    return isFunction$1(options) ? (
      // #8236: extend call and options.name access are considered side-effects
      // by Rollup, so we have to wrap it in a pure-annotated IIFE.
      /* @__PURE__ */ (() => extend({ name: options.name }, extraOptions, { setup: options }))()
    ) : options;
  }
  function markAsyncBoundary(instance) {
    instance.ids = [instance.ids[0] + instance.ids[2]++ + "-", 0, 0];
  }
  function isTemplateRefKey(refs, key) {
    let desc;
    return !!((desc = Object.getOwnPropertyDescriptor(refs, key)) && !desc.configurable);
  }
  const pendingSetRefMap = /* @__PURE__ */ new WeakMap();
  function setRef(rawRef, oldRawRef, parentSuspense, vnode, isUnmount = false) {
    if (isArray$1(rawRef)) {
      rawRef.forEach(
        (r, i) => setRef(
          r,
          oldRawRef && (isArray$1(oldRawRef) ? oldRawRef[i] : oldRawRef),
          parentSuspense,
          vnode,
          isUnmount
        )
      );
      return;
    }
    if (isAsyncWrapper(vnode) && !isUnmount) {
      if (vnode.shapeFlag & 512 && vnode.type.__asyncResolved && vnode.component.subTree.component) {
        setRef(rawRef, oldRawRef, parentSuspense, vnode.component.subTree);
      }
      return;
    }
    const refValue = vnode.shapeFlag & 4 ? getComponentPublicInstance(vnode.component) : vnode.el;
    const value = isUnmount ? null : refValue;
    const { i: owner, r: ref3 } = rawRef;
    const oldRef = oldRawRef && oldRawRef.r;
    const refs = owner.refs === EMPTY_OBJ ? owner.refs = {} : owner.refs;
    const setupState = owner.setupState;
    const rawSetupState = /* @__PURE__ */ toRaw(setupState);
    const canSetSetupRef = setupState === EMPTY_OBJ ? NO : (key) => {
      if (isTemplateRefKey(refs, key)) {
        return false;
      }
      return hasOwn(rawSetupState, key);
    };
    const canSetRef = (ref22, key) => {
      if (key && isTemplateRefKey(refs, key)) {
        return false;
      }
      return true;
    };
    if (oldRef != null && oldRef !== ref3) {
      invalidatePendingSetRef(oldRawRef);
      if (isString(oldRef)) {
        refs[oldRef] = null;
        if (canSetSetupRef(oldRef)) {
          setupState[oldRef] = null;
        }
      } else if (/* @__PURE__ */ isRef(oldRef)) {
        const oldRawRefAtom = oldRawRef;
        if (canSetRef(oldRef, oldRawRefAtom.k)) {
          oldRef.value = null;
        }
        if (oldRawRefAtom.k) refs[oldRawRefAtom.k] = null;
      }
    }
    if (isFunction$1(ref3)) {
      callWithErrorHandling(ref3, owner, 12, [value, refs]);
    } else {
      const _isString = isString(ref3);
      const _isRef = /* @__PURE__ */ isRef(ref3);
      if (_isString || _isRef) {
        const doSet = () => {
          if (rawRef.f) {
            const existing = _isString ? canSetSetupRef(ref3) ? setupState[ref3] : refs[ref3] : canSetRef() || !rawRef.k ? ref3.value : refs[rawRef.k];
            if (isUnmount) {
              isArray$1(existing) && remove(existing, refValue);
            } else {
              if (!isArray$1(existing)) {
                if (_isString) {
                  refs[ref3] = [refValue];
                  if (canSetSetupRef(ref3)) {
                    setupState[ref3] = refs[ref3];
                  }
                } else {
                  const newVal = [refValue];
                  if (canSetRef(ref3, rawRef.k)) {
                    ref3.value = newVal;
                  }
                  if (rawRef.k) refs[rawRef.k] = newVal;
                }
              } else if (!existing.includes(refValue)) {
                existing.push(refValue);
              }
            }
          } else if (_isString) {
            refs[ref3] = value;
            if (canSetSetupRef(ref3)) {
              setupState[ref3] = value;
            }
          } else if (_isRef) {
            if (canSetRef(ref3, rawRef.k)) {
              ref3.value = value;
            }
            if (rawRef.k) refs[rawRef.k] = value;
          } else ;
        };
        if (value) {
          const job = () => {
            doSet();
            pendingSetRefMap.delete(rawRef);
          };
          job.id = -1;
          pendingSetRefMap.set(rawRef, job);
          queuePostRenderEffect(job, parentSuspense);
        } else {
          invalidatePendingSetRef(rawRef);
          doSet();
        }
      }
    }
  }
  function invalidatePendingSetRef(rawRef) {
    const pendingSetRef = pendingSetRefMap.get(rawRef);
    if (pendingSetRef) {
      pendingSetRef.flags |= 8;
      pendingSetRefMap.delete(rawRef);
    }
  }
  getGlobalThis().requestIdleCallback || ((cb) => setTimeout(cb, 1));
  getGlobalThis().cancelIdleCallback || ((id) => clearTimeout(id));
  const isAsyncWrapper = (i) => !!i.type.__asyncLoader;
  const isKeepAlive = (vnode) => vnode.type.__isKeepAlive;
  function onActivated(hook, target) {
    registerKeepAliveHook(hook, "a", target);
  }
  function onDeactivated(hook, target) {
    registerKeepAliveHook(hook, "da", target);
  }
  function registerKeepAliveHook(hook, type, target = currentInstance) {
    const wrappedHook = hook.__wdc || (hook.__wdc = () => {
      let current = target;
      while (current) {
        if (current.isDeactivated) {
          return;
        }
        current = current.parent;
      }
      return hook();
    });
    injectHook(type, wrappedHook, target);
    if (target) {
      let current = target.parent;
      while (current && current.parent) {
        if (isKeepAlive(current.parent.vnode)) {
          injectToKeepAliveRoot(wrappedHook, type, target, current);
        }
        current = current.parent;
      }
    }
  }
  function injectToKeepAliveRoot(hook, type, target, keepAliveRoot) {
    const injected = injectHook(
      type,
      hook,
      keepAliveRoot,
      true
      /* prepend */
    );
    onUnmounted(() => {
      remove(keepAliveRoot[type], injected);
    }, target);
  }
  function injectHook(type, hook, target = currentInstance, prepend = false) {
    if (target) {
      const hooks = target[type] || (target[type] = []);
      const wrappedHook = hook.__weh || (hook.__weh = (...args) => {
        pauseTracking();
        const reset = setCurrentInstance(target);
        const res = callWithAsyncErrorHandling(hook, target, type, args);
        reset();
        resetTracking();
        return res;
      });
      if (prepend) {
        hooks.unshift(wrappedHook);
      } else {
        hooks.push(wrappedHook);
      }
      return wrappedHook;
    }
  }
  const createHook = (lifecycle) => (hook, target = currentInstance) => {
    if (!isInSSRComponentSetup || lifecycle === "sp") {
      injectHook(lifecycle, (...args) => hook(...args), target);
    }
  };
  const onBeforeMount = createHook("bm");
  const onMounted = createHook("m");
  const onBeforeUpdate = createHook(
    "bu"
  );
  const onUpdated = createHook("u");
  const onBeforeUnmount = createHook(
    "bum"
  );
  const onUnmounted = createHook("um");
  const onServerPrefetch = createHook(
    "sp"
  );
  const onRenderTriggered = createHook("rtg");
  const onRenderTracked = createHook("rtc");
  function onErrorCaptured(hook, target = currentInstance) {
    injectHook("ec", hook, target);
  }
  const COMPONENTS = "components";
  const NULL_DYNAMIC_COMPONENT = /* @__PURE__ */ Symbol.for("v-ndc");
  function resolveDynamicComponent(component) {
    if (isString(component)) {
      return resolveAsset(COMPONENTS, component, false) || component;
    } else {
      return component || NULL_DYNAMIC_COMPONENT;
    }
  }
  function resolveAsset(type, name, warnMissing = true, maybeSelfReference = false) {
    const instance = currentRenderingInstance || currentInstance;
    if (instance) {
      const Component = instance.type;
      {
        const selfName = getComponentName(
          Component,
          false
        );
        if (selfName && (selfName === name || selfName === camelize(name) || selfName === capitalize(camelize(name)))) {
          return Component;
        }
      }
      const res = (
        // local registration
        // check instance[type] first which is resolved for options API
        resolve(instance[type] || Component[type], name) || // global registration
        resolve(instance.appContext[type], name)
      );
      if (!res && maybeSelfReference) {
        return Component;
      }
      return res;
    }
  }
  function resolve(registry, name) {
    return registry && (registry[name] || registry[camelize(name)] || registry[capitalize(camelize(name))]);
  }
  function renderList(source, renderItem, cache2, index) {
    let ret;
    const cached = cache2;
    const sourceIsArray = isArray$1(source);
    if (sourceIsArray || isString(source)) {
      const sourceIsReactiveArray = sourceIsArray && /* @__PURE__ */ isReactive(source);
      let needsWrap = false;
      let isReadonlySource = false;
      if (sourceIsReactiveArray) {
        needsWrap = !/* @__PURE__ */ isShallow(source);
        isReadonlySource = /* @__PURE__ */ isReadonly(source);
        source = shallowReadArray(source);
      }
      ret = new Array(source.length);
      for (let i = 0, l = source.length; i < l; i++) {
        ret[i] = renderItem(
          needsWrap ? isReadonlySource ? toReadonly(toReactive(source[i])) : toReactive(source[i]) : source[i],
          i,
          void 0,
          cached
        );
      }
    } else if (typeof source === "number") {
      {
        ret = new Array(source);
        for (let i = 0; i < source; i++) {
          ret[i] = renderItem(i + 1, i, void 0, cached);
        }
      }
    } else if (isObject$1(source)) {
      if (source[Symbol.iterator]) {
        ret = Array.from(
          source,
          (item, i) => renderItem(item, i, void 0, cached)
        );
      } else {
        const keys2 = Object.keys(source);
        ret = new Array(keys2.length);
        for (let i = 0, l = keys2.length; i < l; i++) {
          const key = keys2[i];
          ret[i] = renderItem(source[key], key, i, cached);
        }
      }
    } else {
      ret = [];
    }
    return ret;
  }
  function renderSlot(slots, name, props = {}, fallback, noSlotted) {
    if (currentRenderingInstance.ce || currentRenderingInstance.parent && isAsyncWrapper(currentRenderingInstance.parent) && currentRenderingInstance.parent.ce) {
      const hasProps = Object.keys(props).length > 0;
      return openBlock(), createBlock(
        Fragment,
        null,
        [createVNode("slot", props, fallback)],
        hasProps ? -2 : 64
      );
    }
    let slot = slots[name];
    if (slot && slot._c) {
      slot._d = false;
    }
    openBlock();
    const validSlotContent = slot && ensureValidVNode$1(slot(props));
    const slotKey = props.key || // slot content array of a dynamic conditional slot may have a branch
    // key attached in the `createSlots` helper, respect that
    validSlotContent && validSlotContent.key;
    const rendered = createBlock(
      Fragment,
      {
        key: (slotKey && !isSymbol$1(slotKey) ? slotKey : `_${name}`) + // #7256 force differentiate fallback content from actual content
        (!validSlotContent && fallback ? "_fb" : "")
      },
      validSlotContent || [],
      validSlotContent && slots._ === 1 ? 64 : -2
    );
    if (rendered.scopeId) {
      rendered.slotScopeIds = [rendered.scopeId + "-s"];
    }
    if (slot && slot._c) {
      slot._d = true;
    }
    return rendered;
  }
  function ensureValidVNode$1(vnodes) {
    return vnodes.some((child) => {
      if (!isVNode(child)) return true;
      if (child.type === Comment) return false;
      if (child.type === Fragment && !ensureValidVNode$1(child.children))
        return false;
      return true;
    }) ? vnodes : null;
  }
  const getPublicInstance = (i) => {
    if (!i) return null;
    if (isStatefulComponent(i)) return getComponentPublicInstance(i);
    return getPublicInstance(i.parent);
  };
  const publicPropertiesMap = (
    // Move PURE marker to new line to workaround compiler discarding it
    // due to type annotation
    /* @__PURE__ */ extend(/* @__PURE__ */ Object.create(null), {
      $: (i) => i,
      $el: (i) => i.vnode.el,
      $data: (i) => i.data,
      $props: (i) => i.props,
      $attrs: (i) => i.attrs,
      $slots: (i) => i.slots,
      $refs: (i) => i.refs,
      $parent: (i) => getPublicInstance(i.parent),
      $root: (i) => getPublicInstance(i.root),
      $host: (i) => i.ce,
      $emit: (i) => i.emit,
      $options: (i) => resolveMergedOptions(i),
      $forceUpdate: (i) => i.f || (i.f = () => {
        queueJob(i.update);
      }),
      $nextTick: (i) => i.n || (i.n = nextTick.bind(i.proxy)),
      $watch: (i) => instanceWatch.bind(i)
    })
  );
  const hasSetupBinding = (state, key) => state !== EMPTY_OBJ && !state.__isScriptSetup && hasOwn(state, key);
  const PublicInstanceProxyHandlers = {
    get({ _: instance }, key) {
      if (key === "__v_skip") {
        return true;
      }
      const { ctx: ctx2, setupState, data, props, accessCache, type, appContext } = instance;
      if (key[0] !== "$") {
        const n = accessCache[key];
        if (n !== void 0) {
          switch (n) {
            case 1:
              return setupState[key];
            case 2:
              return data[key];
            case 4:
              return ctx2[key];
            case 3:
              return props[key];
          }
        } else if (hasSetupBinding(setupState, key)) {
          accessCache[key] = 1;
          return setupState[key];
        } else if (data !== EMPTY_OBJ && hasOwn(data, key)) {
          accessCache[key] = 2;
          return data[key];
        } else if (hasOwn(props, key)) {
          accessCache[key] = 3;
          return props[key];
        } else if (ctx2 !== EMPTY_OBJ && hasOwn(ctx2, key)) {
          accessCache[key] = 4;
          return ctx2[key];
        } else if (shouldCacheAccess) {
          accessCache[key] = 0;
        }
      }
      const publicGetter = publicPropertiesMap[key];
      let cssModule, globalProperties;
      if (publicGetter) {
        if (key === "$attrs") {
          track(instance.attrs, "get", "");
        }
        return publicGetter(instance);
      } else if (
        // css module (injected by vue-loader)
        (cssModule = type.__cssModules) && (cssModule = cssModule[key])
      ) {
        return cssModule;
      } else if (ctx2 !== EMPTY_OBJ && hasOwn(ctx2, key)) {
        accessCache[key] = 4;
        return ctx2[key];
      } else if (
        // global properties
        globalProperties = appContext.config.globalProperties, hasOwn(globalProperties, key)
      ) {
        {
          return globalProperties[key];
        }
      } else ;
    },
    set({ _: instance }, key, value) {
      const { data, setupState, ctx: ctx2 } = instance;
      if (hasSetupBinding(setupState, key)) {
        setupState[key] = value;
        return true;
      } else if (data !== EMPTY_OBJ && hasOwn(data, key)) {
        data[key] = value;
        return true;
      } else if (hasOwn(instance.props, key)) {
        return false;
      }
      if (key[0] === "$" && key.slice(1) in instance) {
        return false;
      } else {
        {
          ctx2[key] = value;
        }
      }
      return true;
    },
    has({
      _: { data, setupState, accessCache, ctx: ctx2, appContext, props, type }
    }, key) {
      let cssModules;
      return !!(accessCache[key] || data !== EMPTY_OBJ && key[0] !== "$" && hasOwn(data, key) || hasSetupBinding(setupState, key) || hasOwn(props, key) || hasOwn(ctx2, key) || hasOwn(publicPropertiesMap, key) || hasOwn(appContext.config.globalProperties, key) || (cssModules = type.__cssModules) && cssModules[key]);
    },
    defineProperty(target, key, descriptor) {
      if (descriptor.get != null) {
        target._.accessCache[key] = 0;
      } else if (hasOwn(descriptor, "value")) {
        this.set(target, key, descriptor.value, null);
      }
      return Reflect.defineProperty(target, key, descriptor);
    }
  };
  function normalizePropsOrEmits(props) {
    return isArray$1(props) ? props.reduce(
      (normalized, p2) => (normalized[p2] = null, normalized),
      {}
    ) : props;
  }
  let shouldCacheAccess = true;
  function applyOptions(instance) {
    const options = resolveMergedOptions(instance);
    const publicThis = instance.proxy;
    const ctx2 = instance.ctx;
    shouldCacheAccess = false;
    if (options.beforeCreate) {
      callHook$1(options.beforeCreate, instance, "bc");
    }
    const {
      // state
      data: dataOptions,
      computed: computedOptions,
      methods,
      watch: watchOptions,
      provide: provideOptions,
      inject: injectOptions,
      // lifecycle
      created,
      beforeMount,
      mounted,
      beforeUpdate,
      updated,
      activated,
      deactivated,
      beforeDestroy,
      beforeUnmount,
      destroyed,
      unmounted,
      render: render2,
      renderTracked,
      renderTriggered,
      errorCaptured,
      serverPrefetch,
      // public API
      expose,
      inheritAttrs,
      // assets
      components,
      directives,
      filters
    } = options;
    const checkDuplicateProperties = null;
    if (injectOptions) {
      resolveInjections(injectOptions, ctx2, checkDuplicateProperties);
    }
    if (methods) {
      for (const key in methods) {
        const methodHandler = methods[key];
        if (isFunction$1(methodHandler)) {
          {
            ctx2[key] = methodHandler.bind(publicThis);
          }
        }
      }
    }
    if (dataOptions) {
      const data = dataOptions.call(publicThis, publicThis);
      if (!isObject$1(data)) ;
      else {
        instance.data = /* @__PURE__ */ reactive(data);
      }
    }
    shouldCacheAccess = true;
    if (computedOptions) {
      for (const key in computedOptions) {
        const opt = computedOptions[key];
        const get2 = isFunction$1(opt) ? opt.bind(publicThis, publicThis) : isFunction$1(opt.get) ? opt.get.bind(publicThis, publicThis) : NOOP;
        const set = !isFunction$1(opt) && isFunction$1(opt.set) ? opt.set.bind(publicThis) : NOOP;
        const c2 = computed({
          get: get2,
          set
        });
        Object.defineProperty(ctx2, key, {
          enumerable: true,
          configurable: true,
          get: () => c2.value,
          set: (v) => c2.value = v
        });
      }
    }
    if (watchOptions) {
      for (const key in watchOptions) {
        createWatcher(watchOptions[key], ctx2, publicThis, key);
      }
    }
    if (provideOptions) {
      const provides = isFunction$1(provideOptions) ? provideOptions.call(publicThis) : provideOptions;
      Reflect.ownKeys(provides).forEach((key) => {
        provide(key, provides[key]);
      });
    }
    if (created) {
      callHook$1(created, instance, "c");
    }
    function registerLifecycleHook(register, hook) {
      if (isArray$1(hook)) {
        hook.forEach((_hook) => register(_hook.bind(publicThis)));
      } else if (hook) {
        register(hook.bind(publicThis));
      }
    }
    registerLifecycleHook(onBeforeMount, beforeMount);
    registerLifecycleHook(onMounted, mounted);
    registerLifecycleHook(onBeforeUpdate, beforeUpdate);
    registerLifecycleHook(onUpdated, updated);
    registerLifecycleHook(onActivated, activated);
    registerLifecycleHook(onDeactivated, deactivated);
    registerLifecycleHook(onErrorCaptured, errorCaptured);
    registerLifecycleHook(onRenderTracked, renderTracked);
    registerLifecycleHook(onRenderTriggered, renderTriggered);
    registerLifecycleHook(onBeforeUnmount, beforeUnmount);
    registerLifecycleHook(onUnmounted, unmounted);
    registerLifecycleHook(onServerPrefetch, serverPrefetch);
    if (isArray$1(expose)) {
      if (expose.length) {
        const exposed = instance.exposed || (instance.exposed = {});
        expose.forEach((key) => {
          Object.defineProperty(exposed, key, {
            get: () => publicThis[key],
            set: (val) => publicThis[key] = val,
            enumerable: true
          });
        });
      } else if (!instance.exposed) {
        instance.exposed = {};
      }
    }
    if (render2 && instance.render === NOOP) {
      instance.render = render2;
    }
    if (inheritAttrs != null) {
      instance.inheritAttrs = inheritAttrs;
    }
    if (components) instance.components = components;
    if (directives) instance.directives = directives;
    if (serverPrefetch) {
      markAsyncBoundary(instance);
    }
  }
  function resolveInjections(injectOptions, ctx2, checkDuplicateProperties = NOOP) {
    if (isArray$1(injectOptions)) {
      injectOptions = normalizeInject(injectOptions);
    }
    for (const key in injectOptions) {
      const opt = injectOptions[key];
      let injected;
      if (isObject$1(opt)) {
        if ("default" in opt) {
          injected = inject(
            opt.from || key,
            opt.default,
            true
          );
        } else {
          injected = inject(opt.from || key);
        }
      } else {
        injected = inject(opt);
      }
      if (/* @__PURE__ */ isRef(injected)) {
        Object.defineProperty(ctx2, key, {
          enumerable: true,
          configurable: true,
          get: () => injected.value,
          set: (v) => injected.value = v
        });
      } else {
        ctx2[key] = injected;
      }
    }
  }
  function callHook$1(hook, instance, type) {
    callWithAsyncErrorHandling(
      isArray$1(hook) ? hook.map((h2) => h2.bind(instance.proxy)) : hook.bind(instance.proxy),
      instance,
      type
    );
  }
  function createWatcher(raw, ctx2, publicThis, key) {
    let getter = key.includes(".") ? createPathGetter(publicThis, key) : () => publicThis[key];
    if (isString(raw)) {
      const handler = ctx2[raw];
      if (isFunction$1(handler)) {
        {
          watch(getter, handler);
        }
      }
    } else if (isFunction$1(raw)) {
      {
        watch(getter, raw.bind(publicThis));
      }
    } else if (isObject$1(raw)) {
      if (isArray$1(raw)) {
        raw.forEach((r) => createWatcher(r, ctx2, publicThis, key));
      } else {
        const handler = isFunction$1(raw.handler) ? raw.handler.bind(publicThis) : ctx2[raw.handler];
        if (isFunction$1(handler)) {
          watch(getter, handler, raw);
        }
      }
    } else ;
  }
  function resolveMergedOptions(instance) {
    const base2 = instance.type;
    const { mixins, extends: extendsOptions } = base2;
    const {
      mixins: globalMixins,
      optionsCache: cache2,
      config: { optionMergeStrategies }
    } = instance.appContext;
    const cached = cache2.get(base2);
    let resolved;
    if (cached) {
      resolved = cached;
    } else if (!globalMixins.length && !mixins && !extendsOptions) {
      {
        resolved = base2;
      }
    } else {
      resolved = {};
      if (globalMixins.length) {
        globalMixins.forEach(
          (m) => mergeOptions(resolved, m, optionMergeStrategies, true)
        );
      }
      mergeOptions(resolved, base2, optionMergeStrategies);
    }
    if (isObject$1(base2)) {
      cache2.set(base2, resolved);
    }
    return resolved;
  }
  function mergeOptions(to, from, strats, asMixin = false) {
    const { mixins, extends: extendsOptions } = from;
    if (extendsOptions) {
      mergeOptions(to, extendsOptions, strats, true);
    }
    if (mixins) {
      mixins.forEach(
        (m) => mergeOptions(to, m, strats, true)
      );
    }
    for (const key in from) {
      if (asMixin && key === "expose") ;
      else {
        const strat = internalOptionMergeStrats[key] || strats && strats[key];
        to[key] = strat ? strat(to[key], from[key]) : from[key];
      }
    }
    return to;
  }
  const internalOptionMergeStrats = {
    data: mergeDataFn,
    props: mergeEmitsOrPropsOptions,
    emits: mergeEmitsOrPropsOptions,
    // objects
    methods: mergeObjectOptions,
    computed: mergeObjectOptions,
    // lifecycle
    beforeCreate: mergeAsArray,
    created: mergeAsArray,
    beforeMount: mergeAsArray,
    mounted: mergeAsArray,
    beforeUpdate: mergeAsArray,
    updated: mergeAsArray,
    beforeDestroy: mergeAsArray,
    beforeUnmount: mergeAsArray,
    destroyed: mergeAsArray,
    unmounted: mergeAsArray,
    activated: mergeAsArray,
    deactivated: mergeAsArray,
    errorCaptured: mergeAsArray,
    serverPrefetch: mergeAsArray,
    // assets
    components: mergeObjectOptions,
    directives: mergeObjectOptions,
    // watch
    watch: mergeWatchOptions,
    // provide / inject
    provide: mergeDataFn,
    inject: mergeInject
  };
  function mergeDataFn(to, from) {
    if (!from) {
      return to;
    }
    if (!to) {
      return from;
    }
    return function mergedDataFn() {
      return extend(
        isFunction$1(to) ? to.call(this, this) : to,
        isFunction$1(from) ? from.call(this, this) : from
      );
    };
  }
  function mergeInject(to, from) {
    return mergeObjectOptions(normalizeInject(to), normalizeInject(from));
  }
  function normalizeInject(raw) {
    if (isArray$1(raw)) {
      const res = {};
      for (let i = 0; i < raw.length; i++) {
        res[raw[i]] = raw[i];
      }
      return res;
    }
    return raw;
  }
  function mergeAsArray(to, from) {
    return to ? [...new Set([].concat(to, from))] : from;
  }
  function mergeObjectOptions(to, from) {
    return to ? extend(/* @__PURE__ */ Object.create(null), to, from) : from;
  }
  function mergeEmitsOrPropsOptions(to, from) {
    if (to) {
      if (isArray$1(to) && isArray$1(from)) {
        return [.../* @__PURE__ */ new Set([...to, ...from])];
      }
      return extend(
        /* @__PURE__ */ Object.create(null),
        normalizePropsOrEmits(to),
        normalizePropsOrEmits(from != null ? from : {})
      );
    } else {
      return from;
    }
  }
  function mergeWatchOptions(to, from) {
    if (!to) return from;
    if (!from) return to;
    const merged = extend(/* @__PURE__ */ Object.create(null), to);
    for (const key in from) {
      merged[key] = mergeAsArray(to[key], from[key]);
    }
    return merged;
  }
  function createAppContext() {
    return {
      app: null,
      config: {
        isNativeTag: NO,
        performance: false,
        globalProperties: {},
        optionMergeStrategies: {},
        errorHandler: void 0,
        warnHandler: void 0,
        compilerOptions: {}
      },
      mixins: [],
      components: {},
      directives: {},
      provides: /* @__PURE__ */ Object.create(null),
      optionsCache: /* @__PURE__ */ new WeakMap(),
      propsCache: /* @__PURE__ */ new WeakMap(),
      emitsCache: /* @__PURE__ */ new WeakMap()
    };
  }
  let uid$1 = 0;
  function createAppAPI(render2, hydrate) {
    return function createApp2(rootComponent, rootProps = null) {
      if (!isFunction$1(rootComponent)) {
        rootComponent = extend({}, rootComponent);
      }
      if (rootProps != null && !isObject$1(rootProps)) {
        rootProps = null;
      }
      const context = createAppContext();
      const installedPlugins = /* @__PURE__ */ new WeakSet();
      const pluginCleanupFns = [];
      let isMounted2 = false;
      const app = context.app = {
        _uid: uid$1++,
        _component: rootComponent,
        _props: rootProps,
        _container: null,
        _context: context,
        _instance: null,
        version,
        get config() {
          return context.config;
        },
        set config(v) {
        },
        use(plugin2, ...options) {
          if (installedPlugins.has(plugin2)) ;
          else if (plugin2 && isFunction$1(plugin2.install)) {
            installedPlugins.add(plugin2);
            plugin2.install(app, ...options);
          } else if (isFunction$1(plugin2)) {
            installedPlugins.add(plugin2);
            plugin2(app, ...options);
          } else ;
          return app;
        },
        mixin(mixin) {
          {
            if (!context.mixins.includes(mixin)) {
              context.mixins.push(mixin);
            }
          }
          return app;
        },
        component(name, component) {
          if (!component) {
            return context.components[name];
          }
          context.components[name] = component;
          return app;
        },
        directive(name, directive) {
          if (!directive) {
            return context.directives[name];
          }
          context.directives[name] = directive;
          return app;
        },
        mount(rootContainer, isHydrate, namespace2) {
          if (!isMounted2) {
            const vnode = app._ceVNode || createVNode(rootComponent, rootProps);
            vnode.appContext = context;
            if (namespace2 === true) {
              namespace2 = "svg";
            } else if (namespace2 === false) {
              namespace2 = void 0;
            }
            {
              render2(vnode, rootContainer, namespace2);
            }
            isMounted2 = true;
            app._container = rootContainer;
            rootContainer.__vue_app__ = app;
            return getComponentPublicInstance(vnode.component);
          }
        },
        onUnmount(cleanupFn) {
          pluginCleanupFns.push(cleanupFn);
        },
        unmount() {
          if (isMounted2) {
            callWithAsyncErrorHandling(
              pluginCleanupFns,
              app._instance,
              16
            );
            render2(null, app._container);
            delete app._container.__vue_app__;
          }
        },
        provide(key, value) {
          context.provides[key] = value;
          return app;
        },
        runWithContext(fn) {
          const lastApp = currentApp;
          currentApp = app;
          try {
            return fn();
          } finally {
            currentApp = lastApp;
          }
        }
      };
      return app;
    };
  }
  let currentApp = null;
  const getModelModifiers = (props, modelName) => {
    return modelName === "modelValue" || modelName === "model-value" ? props.modelModifiers : props[`${modelName}Modifiers`] || props[`${camelize(modelName)}Modifiers`] || props[`${hyphenate(modelName)}Modifiers`];
  };
  function emit(instance, event, ...rawArgs) {
    if (instance.isUnmounted) return;
    const props = instance.vnode.props || EMPTY_OBJ;
    let args = rawArgs;
    const isModelListener2 = event.startsWith("update:");
    const modifiers = isModelListener2 && getModelModifiers(props, event.slice(7));
    if (modifiers) {
      if (modifiers.trim) {
        args = rawArgs.map((a) => isString(a) ? a.trim() : a);
      }
      if (modifiers.number) {
        args = rawArgs.map(looseToNumber);
      }
    }
    let handlerName;
    let handler = props[handlerName = toHandlerKey(event)] || // also try camelCase event handler (#2249)
    props[handlerName = toHandlerKey(camelize(event))];
    if (!handler && isModelListener2) {
      handler = props[handlerName = toHandlerKey(hyphenate(event))];
    }
    if (handler) {
      callWithAsyncErrorHandling(
        handler,
        instance,
        6,
        args
      );
    }
    const onceHandler = props[handlerName + `Once`];
    if (onceHandler) {
      if (!instance.emitted) {
        instance.emitted = {};
      } else if (instance.emitted[handlerName]) {
        return;
      }
      instance.emitted[handlerName] = true;
      callWithAsyncErrorHandling(
        onceHandler,
        instance,
        6,
        args
      );
    }
  }
  const mixinEmitsCache = /* @__PURE__ */ new WeakMap();
  function normalizeEmitsOptions(comp, appContext, asMixin = false) {
    const cache2 = asMixin ? mixinEmitsCache : appContext.emitsCache;
    const cached = cache2.get(comp);
    if (cached !== void 0) {
      return cached;
    }
    const raw = comp.emits;
    let normalized = {};
    let hasExtends = false;
    if (!isFunction$1(comp)) {
      const extendEmits = (raw2) => {
        const normalizedFromExtend = normalizeEmitsOptions(raw2, appContext, true);
        if (normalizedFromExtend) {
          hasExtends = true;
          extend(normalized, normalizedFromExtend);
        }
      };
      if (!asMixin && appContext.mixins.length) {
        appContext.mixins.forEach(extendEmits);
      }
      if (comp.extends) {
        extendEmits(comp.extends);
      }
      if (comp.mixins) {
        comp.mixins.forEach(extendEmits);
      }
    }
    if (!raw && !hasExtends) {
      if (isObject$1(comp)) {
        cache2.set(comp, null);
      }
      return null;
    }
    if (isArray$1(raw)) {
      raw.forEach((key) => normalized[key] = null);
    } else {
      extend(normalized, raw);
    }
    if (isObject$1(comp)) {
      cache2.set(comp, normalized);
    }
    return normalized;
  }
  function isEmitListener(options, key) {
    if (!options || !isOn(key)) {
      return false;
    }
    key = key.slice(2).replace(/Once$/, "");
    return hasOwn(options, key[0].toLowerCase() + key.slice(1)) || hasOwn(options, hyphenate(key)) || hasOwn(options, key);
  }
  function markAttrsAccessed() {
  }
  function renderComponentRoot(instance) {
    const {
      type: Component,
      vnode,
      proxy,
      withProxy,
      propsOptions: [propsOptions],
      slots,
      attrs,
      emit: emit2,
      render: render2,
      renderCache,
      props,
      data,
      setupState,
      ctx: ctx2,
      inheritAttrs
    } = instance;
    const prev = setCurrentRenderingInstance(instance);
    let result;
    let fallthroughAttrs;
    try {
      if (vnode.shapeFlag & 4) {
        const proxyToUse = withProxy || proxy;
        const thisProxy = false ? new Proxy(proxyToUse, {
          get(target, key, receiver) {
            warn$1$1(
              `Property '${String(
                key
              )}' was accessed via 'this'. Avoid using 'this' in templates.`
            );
            return Reflect.get(target, key, receiver);
          }
        }) : proxyToUse;
        result = normalizeVNode(
          render2.call(
            thisProxy,
            proxyToUse,
            renderCache,
            false ? /* @__PURE__ */ shallowReadonly(props) : props,
            setupState,
            data,
            ctx2
          )
        );
        fallthroughAttrs = attrs;
      } else {
        const render22 = Component;
        if (false) ;
        result = normalizeVNode(
          render22.length > 1 ? render22(
            false ? /* @__PURE__ */ shallowReadonly(props) : props,
            false ? {
              get attrs() {
                markAttrsAccessed();
                return /* @__PURE__ */ shallowReadonly(attrs);
              },
              slots,
              emit: emit2
            } : { attrs, slots, emit: emit2 }
          ) : render22(
            false ? /* @__PURE__ */ shallowReadonly(props) : props,
            null
          )
        );
        fallthroughAttrs = Component.props ? attrs : getFunctionalFallthrough(attrs);
      }
    } catch (err) {
      blockStack.length = 0;
      handleError(err, instance, 1);
      result = createVNode(Comment);
    }
    let root2 = result;
    if (fallthroughAttrs && inheritAttrs !== false) {
      const keys2 = Object.keys(fallthroughAttrs);
      const { shapeFlag } = root2;
      if (keys2.length) {
        if (shapeFlag & (1 | 6)) {
          if (propsOptions && keys2.some(isModelListener)) {
            fallthroughAttrs = filterModelListeners(
              fallthroughAttrs,
              propsOptions
            );
          }
          root2 = cloneVNode(root2, fallthroughAttrs, false, true);
        }
      }
    }
    if (vnode.dirs) {
      root2 = cloneVNode(root2, null, false, true);
      root2.dirs = root2.dirs ? root2.dirs.concat(vnode.dirs) : vnode.dirs;
    }
    if (vnode.transition) {
      setTransitionHooks(root2, vnode.transition);
    }
    {
      result = root2;
    }
    setCurrentRenderingInstance(prev);
    return result;
  }
  const getFunctionalFallthrough = (attrs) => {
    let res;
    for (const key in attrs) {
      if (key === "class" || key === "style" || isOn(key)) {
        (res || (res = {}))[key] = attrs[key];
      }
    }
    return res;
  };
  const filterModelListeners = (attrs, props) => {
    const res = {};
    for (const key in attrs) {
      if (!isModelListener(key) || !(key.slice(9) in props)) {
        res[key] = attrs[key];
      }
    }
    return res;
  };
  function shouldUpdateComponent(prevVNode, nextVNode, optimized) {
    const { props: prevProps, children: prevChildren, component } = prevVNode;
    const { props: nextProps, children: nextChildren, patchFlag } = nextVNode;
    const emits = component.emitsOptions;
    if (nextVNode.dirs || nextVNode.transition) {
      return true;
    }
    if (optimized && patchFlag >= 0) {
      if (patchFlag & 1024) {
        return true;
      }
      if (patchFlag & 16) {
        if (!prevProps) {
          return !!nextProps;
        }
        return hasPropsChanged(prevProps, nextProps, emits);
      } else if (patchFlag & 8) {
        const dynamicProps = nextVNode.dynamicProps;
        for (let i = 0; i < dynamicProps.length; i++) {
          const key = dynamicProps[i];
          if (hasPropValueChanged(nextProps, prevProps, key) && !isEmitListener(emits, key)) {
            return true;
          }
        }
      }
    } else {
      if (prevChildren || nextChildren) {
        if (!nextChildren || !nextChildren.$stable) {
          return true;
        }
      }
      if (prevProps === nextProps) {
        return false;
      }
      if (!prevProps) {
        return !!nextProps;
      }
      if (!nextProps) {
        return true;
      }
      return hasPropsChanged(prevProps, nextProps, emits);
    }
    return false;
  }
  function hasPropsChanged(prevProps, nextProps, emitsOptions) {
    const nextKeys = Object.keys(nextProps);
    if (nextKeys.length !== Object.keys(prevProps).length) {
      return true;
    }
    for (let i = 0; i < nextKeys.length; i++) {
      const key = nextKeys[i];
      if (hasPropValueChanged(nextProps, prevProps, key) && !isEmitListener(emitsOptions, key)) {
        return true;
      }
    }
    return false;
  }
  function hasPropValueChanged(nextProps, prevProps, key) {
    const nextProp = nextProps[key];
    const prevProp = prevProps[key];
    if (key === "style" && isObject$1(nextProp) && isObject$1(prevProp)) {
      return !looseEqual(nextProp, prevProp);
    }
    return nextProp !== prevProp;
  }
  function updateHOCHostEl({ vnode, parent }, el) {
    while (parent) {
      const root2 = parent.subTree;
      if (root2.suspense && root2.suspense.activeBranch === vnode) {
        root2.el = vnode.el;
      }
      if (root2 === vnode) {
        (vnode = parent.vnode).el = el;
        parent = parent.parent;
      } else {
        break;
      }
    }
  }
  const internalObjectProto = {};
  const createInternalObject = () => Object.create(internalObjectProto);
  const isInternalObject = (obj) => Object.getPrototypeOf(obj) === internalObjectProto;
  function initProps(instance, rawProps, isStateful, isSSR = false) {
    const props = {};
    const attrs = createInternalObject();
    instance.propsDefaults = /* @__PURE__ */ Object.create(null);
    setFullProps(instance, rawProps, props, attrs);
    for (const key in instance.propsOptions[0]) {
      if (!(key in props)) {
        props[key] = void 0;
      }
    }
    if (isStateful) {
      instance.props = isSSR ? props : /* @__PURE__ */ shallowReactive(props);
    } else {
      if (!instance.type.props) {
        instance.props = attrs;
      } else {
        instance.props = props;
      }
    }
    instance.attrs = attrs;
  }
  function updateProps(instance, rawProps, rawPrevProps, optimized) {
    const {
      props,
      attrs,
      vnode: { patchFlag }
    } = instance;
    const rawCurrentProps = /* @__PURE__ */ toRaw(props);
    const [options] = instance.propsOptions;
    let hasAttrsChanged = false;
    if (
      // always force full diff in dev
      // - #1942 if hmr is enabled with sfc component
      // - vite#872 non-sfc component used by sfc component
      (optimized || patchFlag > 0) && !(patchFlag & 16)
    ) {
      if (patchFlag & 8) {
        const propsToUpdate = instance.vnode.dynamicProps;
        for (let i = 0; i < propsToUpdate.length; i++) {
          let key = propsToUpdate[i];
          if (isEmitListener(instance.emitsOptions, key)) {
            continue;
          }
          const value = rawProps[key];
          if (options) {
            if (hasOwn(attrs, key)) {
              if (value !== attrs[key]) {
                attrs[key] = value;
                hasAttrsChanged = true;
              }
            } else {
              const camelizedKey = camelize(key);
              props[camelizedKey] = resolvePropValue(
                options,
                rawCurrentProps,
                camelizedKey,
                value,
                instance,
                false
              );
            }
          } else {
            if (value !== attrs[key]) {
              attrs[key] = value;
              hasAttrsChanged = true;
            }
          }
        }
      }
    } else {
      if (setFullProps(instance, rawProps, props, attrs)) {
        hasAttrsChanged = true;
      }
      let kebabKey;
      for (const key in rawCurrentProps) {
        if (!rawProps || // for camelCase
        !hasOwn(rawProps, key) && // it's possible the original props was passed in as kebab-case
        // and converted to camelCase (#955)
        ((kebabKey = hyphenate(key)) === key || !hasOwn(rawProps, kebabKey))) {
          if (options) {
            if (rawPrevProps && // for camelCase
            (rawPrevProps[key] !== void 0 || // for kebab-case
            rawPrevProps[kebabKey] !== void 0)) {
              props[key] = resolvePropValue(
                options,
                rawCurrentProps,
                key,
                void 0,
                instance,
                true
              );
            }
          } else {
            delete props[key];
          }
        }
      }
      if (attrs !== rawCurrentProps) {
        for (const key in attrs) {
          if (!rawProps || !hasOwn(rawProps, key) && true) {
            delete attrs[key];
            hasAttrsChanged = true;
          }
        }
      }
    }
    if (hasAttrsChanged) {
      trigger$1(instance.attrs, "set", "");
    }
  }
  function setFullProps(instance, rawProps, props, attrs) {
    const [options, needCastKeys] = instance.propsOptions;
    let hasAttrsChanged = false;
    let rawCastValues;
    if (rawProps) {
      for (let key in rawProps) {
        if (isReservedProp(key)) {
          continue;
        }
        const value = rawProps[key];
        let camelKey;
        if (options && hasOwn(options, camelKey = camelize(key))) {
          if (!needCastKeys || !needCastKeys.includes(camelKey)) {
            props[camelKey] = value;
          } else {
            (rawCastValues || (rawCastValues = {}))[camelKey] = value;
          }
        } else if (!isEmitListener(instance.emitsOptions, key)) {
          if (!(key in attrs) || value !== attrs[key]) {
            attrs[key] = value;
            hasAttrsChanged = true;
          }
        }
      }
    }
    if (needCastKeys) {
      const rawCurrentProps = /* @__PURE__ */ toRaw(props);
      const castValues = rawCastValues || EMPTY_OBJ;
      for (let i = 0; i < needCastKeys.length; i++) {
        const key = needCastKeys[i];
        props[key] = resolvePropValue(
          options,
          rawCurrentProps,
          key,
          castValues[key],
          instance,
          !hasOwn(castValues, key)
        );
      }
    }
    return hasAttrsChanged;
  }
  function resolvePropValue(options, props, key, value, instance, isAbsent) {
    const opt = options[key];
    if (opt != null) {
      const hasDefault = hasOwn(opt, "default");
      if (hasDefault && value === void 0) {
        const defaultValue = opt.default;
        if (opt.type !== Function && !opt.skipFactory && isFunction$1(defaultValue)) {
          const { propsDefaults } = instance;
          if (key in propsDefaults) {
            value = propsDefaults[key];
          } else {
            const reset = setCurrentInstance(instance);
            value = propsDefaults[key] = defaultValue.call(
              null,
              props
            );
            reset();
          }
        } else {
          value = defaultValue;
        }
        if (instance.ce) {
          instance.ce._setProp(key, value);
        }
      }
      if (opt[
        0
        /* shouldCast */
      ]) {
        if (isAbsent && !hasDefault) {
          value = false;
        } else if (opt[
          1
          /* shouldCastTrue */
        ] && (value === "" || value === hyphenate(key))) {
          value = true;
        }
      }
    }
    return value;
  }
  const mixinPropsCache = /* @__PURE__ */ new WeakMap();
  function normalizePropsOptions(comp, appContext, asMixin = false) {
    const cache2 = asMixin ? mixinPropsCache : appContext.propsCache;
    const cached = cache2.get(comp);
    if (cached) {
      return cached;
    }
    const raw = comp.props;
    const normalized = {};
    const needCastKeys = [];
    let hasExtends = false;
    if (!isFunction$1(comp)) {
      const extendProps = (raw2) => {
        hasExtends = true;
        const [props, keys2] = normalizePropsOptions(raw2, appContext, true);
        extend(normalized, props);
        if (keys2) needCastKeys.push(...keys2);
      };
      if (!asMixin && appContext.mixins.length) {
        appContext.mixins.forEach(extendProps);
      }
      if (comp.extends) {
        extendProps(comp.extends);
      }
      if (comp.mixins) {
        comp.mixins.forEach(extendProps);
      }
    }
    if (!raw && !hasExtends) {
      if (isObject$1(comp)) {
        cache2.set(comp, EMPTY_ARR);
      }
      return EMPTY_ARR;
    }
    if (isArray$1(raw)) {
      for (let i = 0; i < raw.length; i++) {
        const normalizedKey = camelize(raw[i]);
        if (validatePropName(normalizedKey)) {
          normalized[normalizedKey] = EMPTY_OBJ;
        }
      }
    } else if (raw) {
      for (const key in raw) {
        const normalizedKey = camelize(key);
        if (validatePropName(normalizedKey)) {
          const opt = raw[key];
          const prop = normalized[normalizedKey] = isArray$1(opt) || isFunction$1(opt) ? { type: opt } : extend({}, opt);
          const propType = prop.type;
          let shouldCast = false;
          let shouldCastTrue = true;
          if (isArray$1(propType)) {
            for (let index = 0; index < propType.length; ++index) {
              const type = propType[index];
              const typeName = isFunction$1(type) && type.name;
              if (typeName === "Boolean") {
                shouldCast = true;
                break;
              } else if (typeName === "String") {
                shouldCastTrue = false;
              }
            }
          } else {
            shouldCast = isFunction$1(propType) && propType.name === "Boolean";
          }
          prop[
            0
            /* shouldCast */
          ] = shouldCast;
          prop[
            1
            /* shouldCastTrue */
          ] = shouldCastTrue;
          if (shouldCast || hasOwn(prop, "default")) {
            needCastKeys.push(normalizedKey);
          }
        }
      }
    }
    const res = [normalized, needCastKeys];
    if (isObject$1(comp)) {
      cache2.set(comp, res);
    }
    return res;
  }
  function validatePropName(key) {
    if (key[0] !== "$" && !isReservedProp(key)) {
      return true;
    }
    return false;
  }
  const isInternalKey = (key) => key === "_" || key === "_ctx" || key === "$stable";
  const normalizeSlotValue = (value) => isArray$1(value) ? value.map(normalizeVNode) : [normalizeVNode(value)];
  const normalizeSlot = (key, rawSlot, ctx2) => {
    if (rawSlot._n) {
      return rawSlot;
    }
    const normalized = withCtx((...args) => {
      if (false) ;
      return normalizeSlotValue(rawSlot(...args));
    }, ctx2);
    normalized._c = false;
    return normalized;
  };
  const normalizeObjectSlots = (rawSlots, slots, instance) => {
    const ctx2 = rawSlots._ctx;
    for (const key in rawSlots) {
      if (isInternalKey(key)) continue;
      const value = rawSlots[key];
      if (isFunction$1(value)) {
        slots[key] = normalizeSlot(key, value, ctx2);
      } else if (value != null) {
        const normalized = normalizeSlotValue(value);
        slots[key] = () => normalized;
      }
    }
  };
  const normalizeVNodeSlots = (instance, children) => {
    const normalized = normalizeSlotValue(children);
    instance.slots.default = () => normalized;
  };
  const assignSlots = (slots, children, optimized) => {
    for (const key in children) {
      if (optimized || !isInternalKey(key)) {
        slots[key] = children[key];
      }
    }
  };
  const initSlots = (instance, children, optimized) => {
    const slots = instance.slots = createInternalObject();
    if (instance.vnode.shapeFlag & 32) {
      const type = children._;
      if (type) {
        assignSlots(slots, children, optimized);
        if (optimized) {
          def(slots, "_", type, true);
        }
      } else {
        normalizeObjectSlots(children, slots);
      }
    } else if (children) {
      normalizeVNodeSlots(instance, children);
    }
  };
  const updateSlots = (instance, children, optimized) => {
    const { vnode, slots } = instance;
    let needDeletionCheck = true;
    let deletionComparisonTarget = EMPTY_OBJ;
    if (vnode.shapeFlag & 32) {
      const type = children._;
      if (type) {
        if (optimized && type === 1) {
          needDeletionCheck = false;
        } else {
          assignSlots(slots, children, optimized);
        }
      } else {
        needDeletionCheck = !children.$stable;
        normalizeObjectSlots(children, slots);
      }
      deletionComparisonTarget = children;
    } else if (children) {
      normalizeVNodeSlots(instance, children);
      deletionComparisonTarget = { default: 1 };
    }
    if (needDeletionCheck) {
      for (const key in slots) {
        if (!isInternalKey(key) && deletionComparisonTarget[key] == null) {
          delete slots[key];
        }
      }
    }
  };
  const queuePostRenderEffect = queueEffectWithSuspense;
  function createRenderer(options) {
    return baseCreateRenderer(options);
  }
  function baseCreateRenderer(options, createHydrationFns) {
    const target = getGlobalThis();
    target.__VUE__ = true;
    const {
      insert: hostInsert,
      remove: hostRemove,
      patchProp: hostPatchProp,
      createElement: hostCreateElement,
      createText: hostCreateText,
      createComment: hostCreateComment,
      setText: hostSetText,
      setElementText: hostSetElementText,
      parentNode: hostParentNode,
      nextSibling: hostNextSibling,
      setScopeId: hostSetScopeId = NOOP,
      insertStaticContent: hostInsertStaticContent
    } = options;
    const patch = (n1, n2, container, anchor = null, parentComponent = null, parentSuspense = null, namespace2 = void 0, slotScopeIds = null, optimized = !!n2.dynamicChildren) => {
      if (n1 === n2) {
        return;
      }
      if (n1 && !isSameVNodeType(n1, n2)) {
        anchor = getNextHostNode(n1);
        unmount2(n1, parentComponent, parentSuspense, true);
        n1 = null;
      }
      if (n2.patchFlag === -2) {
        optimized = false;
        n2.dynamicChildren = null;
      }
      const { type, ref: ref3, shapeFlag } = n2;
      switch (type) {
        case Text:
          processText(n1, n2, container, anchor);
          break;
        case Comment:
          processCommentNode(n1, n2, container, anchor);
          break;
        case Static:
          if (n1 == null) {
            mountStaticNode(n2, container, anchor, namespace2);
          }
          break;
        case Fragment:
          processFragment(
            n1,
            n2,
            container,
            anchor,
            parentComponent,
            parentSuspense,
            namespace2,
            slotScopeIds,
            optimized
          );
          break;
        default:
          if (shapeFlag & 1) {
            processElement(
              n1,
              n2,
              container,
              anchor,
              parentComponent,
              parentSuspense,
              namespace2,
              slotScopeIds,
              optimized
            );
          } else if (shapeFlag & 6) {
            processComponent(
              n1,
              n2,
              container,
              anchor,
              parentComponent,
              parentSuspense,
              namespace2,
              slotScopeIds,
              optimized
            );
          } else if (shapeFlag & 64) {
            type.process(
              n1,
              n2,
              container,
              anchor,
              parentComponent,
              parentSuspense,
              namespace2,
              slotScopeIds,
              optimized,
              internals
            );
          } else if (shapeFlag & 128) {
            type.process(
              n1,
              n2,
              container,
              anchor,
              parentComponent,
              parentSuspense,
              namespace2,
              slotScopeIds,
              optimized,
              internals
            );
          } else ;
      }
      if (ref3 != null && parentComponent) {
        setRef(ref3, n1 && n1.ref, parentSuspense, n2 || n1, !n2);
      } else if (ref3 == null && n1 && n1.ref != null) {
        setRef(n1.ref, null, parentSuspense, n1, true);
      }
    };
    const processText = (n1, n2, container, anchor) => {
      if (n1 == null) {
        hostInsert(
          n2.el = hostCreateText(n2.children),
          container,
          anchor
        );
      } else {
        const el = n2.el = n1.el;
        if (n2.children !== n1.children) {
          hostSetText(el, n2.children);
        }
      }
    };
    const processCommentNode = (n1, n2, container, anchor) => {
      if (n1 == null) {
        hostInsert(
          n2.el = hostCreateComment(n2.children || ""),
          container,
          anchor
        );
      } else {
        n2.el = n1.el;
      }
    };
    const mountStaticNode = (n2, container, anchor, namespace2) => {
      [n2.el, n2.anchor] = hostInsertStaticContent(
        n2.children,
        container,
        anchor,
        namespace2,
        n2.el,
        n2.anchor
      );
    };
    const moveStaticNode = ({ el, anchor }, container, nextSibling) => {
      let next;
      while (el && el !== anchor) {
        next = hostNextSibling(el);
        hostInsert(el, container, nextSibling);
        el = next;
      }
      hostInsert(anchor, container, nextSibling);
    };
    const removeStaticNode = ({ el, anchor }) => {
      let next;
      while (el && el !== anchor) {
        next = hostNextSibling(el);
        hostRemove(el);
        el = next;
      }
      hostRemove(anchor);
    };
    const processElement = (n1, n2, container, anchor, parentComponent, parentSuspense, namespace2, slotScopeIds, optimized) => {
      if (n2.type === "svg") {
        namespace2 = "svg";
      } else if (n2.type === "math") {
        namespace2 = "mathml";
      }
      if (n1 == null) {
        mountElement(
          n2,
          container,
          anchor,
          parentComponent,
          parentSuspense,
          namespace2,
          slotScopeIds,
          optimized
        );
      } else {
        const customElement = n1.el && n1.el._isVueCE ? n1.el : null;
        try {
          if (customElement) {
            customElement._beginPatch();
          }
          patchElement(
            n1,
            n2,
            parentComponent,
            parentSuspense,
            namespace2,
            slotScopeIds,
            optimized
          );
        } finally {
          if (customElement) {
            customElement._endPatch();
          }
        }
      }
    };
    const mountElement = (vnode, container, anchor, parentComponent, parentSuspense, namespace2, slotScopeIds, optimized) => {
      let el;
      let vnodeHook;
      const { props, shapeFlag, transition, dirs } = vnode;
      el = vnode.el = hostCreateElement(
        vnode.type,
        namespace2,
        props && props.is,
        props
      );
      if (shapeFlag & 8) {
        hostSetElementText(el, vnode.children);
      } else if (shapeFlag & 16) {
        mountChildren(
          vnode.children,
          el,
          null,
          parentComponent,
          parentSuspense,
          resolveChildrenNamespace(vnode, namespace2),
          slotScopeIds,
          optimized
        );
      }
      if (dirs) {
        invokeDirectiveHook(vnode, null, parentComponent, "created");
      }
      setScopeId(el, vnode, vnode.scopeId, slotScopeIds, parentComponent);
      if (props) {
        for (const key in props) {
          if (key !== "value" && !isReservedProp(key)) {
            hostPatchProp(el, key, null, props[key], namespace2, parentComponent);
          }
        }
        if ("value" in props) {
          hostPatchProp(el, "value", null, props.value, namespace2);
        }
        if (vnodeHook = props.onVnodeBeforeMount) {
          invokeVNodeHook(vnodeHook, parentComponent, vnode);
        }
      }
      if (dirs) {
        invokeDirectiveHook(vnode, null, parentComponent, "beforeMount");
      }
      const needCallTransitionHooks = needTransition(parentSuspense, transition);
      if (needCallTransitionHooks) {
        transition.beforeEnter(el);
      }
      hostInsert(el, container, anchor);
      if ((vnodeHook = props && props.onVnodeMounted) || needCallTransitionHooks || dirs) {
        queuePostRenderEffect(() => {
          vnodeHook && invokeVNodeHook(vnodeHook, parentComponent, vnode);
          needCallTransitionHooks && transition.enter(el);
          dirs && invokeDirectiveHook(vnode, null, parentComponent, "mounted");
        }, parentSuspense);
      }
    };
    const setScopeId = (el, vnode, scopeId, slotScopeIds, parentComponent) => {
      if (scopeId) {
        hostSetScopeId(el, scopeId);
      }
      if (slotScopeIds) {
        for (let i = 0; i < slotScopeIds.length; i++) {
          hostSetScopeId(el, slotScopeIds[i]);
        }
      }
      if (parentComponent) {
        let subTree = parentComponent.subTree;
        if (vnode === subTree || isSuspense(subTree.type) && (subTree.ssContent === vnode || subTree.ssFallback === vnode)) {
          const parentVNode = parentComponent.vnode;
          setScopeId(
            el,
            parentVNode,
            parentVNode.scopeId,
            parentVNode.slotScopeIds,
            parentComponent.parent
          );
        }
      }
    };
    const mountChildren = (children, container, anchor, parentComponent, parentSuspense, namespace2, slotScopeIds, optimized, start = 0) => {
      for (let i = start; i < children.length; i++) {
        const child = children[i] = optimized ? cloneIfMounted(children[i]) : normalizeVNode(children[i]);
        patch(
          null,
          child,
          container,
          anchor,
          parentComponent,
          parentSuspense,
          namespace2,
          slotScopeIds,
          optimized
        );
      }
    };
    const patchElement = (n1, n2, parentComponent, parentSuspense, namespace2, slotScopeIds, optimized) => {
      const el = n2.el = n1.el;
      let { patchFlag, dynamicChildren, dirs } = n2;
      patchFlag |= n1.patchFlag & 16;
      const oldProps = n1.props || EMPTY_OBJ;
      const newProps = n2.props || EMPTY_OBJ;
      let vnodeHook;
      parentComponent && toggleRecurse(parentComponent, false);
      if (vnodeHook = newProps.onVnodeBeforeUpdate) {
        invokeVNodeHook(vnodeHook, parentComponent, n2, n1);
      }
      if (dirs) {
        invokeDirectiveHook(n2, n1, parentComponent, "beforeUpdate");
      }
      parentComponent && toggleRecurse(parentComponent, true);
      if (oldProps.innerHTML && newProps.innerHTML == null || oldProps.textContent && newProps.textContent == null) {
        hostSetElementText(el, "");
      }
      if (dynamicChildren) {
        patchBlockChildren(
          n1.dynamicChildren,
          dynamicChildren,
          el,
          parentComponent,
          parentSuspense,
          resolveChildrenNamespace(n2, namespace2),
          slotScopeIds
        );
      } else if (!optimized) {
        patchChildren(
          n1,
          n2,
          el,
          null,
          parentComponent,
          parentSuspense,
          resolveChildrenNamespace(n2, namespace2),
          slotScopeIds,
          false
        );
      }
      if (patchFlag > 0) {
        if (patchFlag & 16) {
          patchProps(el, oldProps, newProps, parentComponent, namespace2);
        } else {
          if (patchFlag & 2) {
            if (oldProps.class !== newProps.class) {
              hostPatchProp(el, "class", null, newProps.class, namespace2);
            }
          }
          if (patchFlag & 4) {
            hostPatchProp(el, "style", oldProps.style, newProps.style, namespace2);
          }
          if (patchFlag & 8) {
            const propsToUpdate = n2.dynamicProps;
            for (let i = 0; i < propsToUpdate.length; i++) {
              const key = propsToUpdate[i];
              const prev = oldProps[key];
              const next = newProps[key];
              if (next !== prev || key === "value") {
                hostPatchProp(el, key, prev, next, namespace2, parentComponent);
              }
            }
          }
        }
        if (patchFlag & 1) {
          if (n1.children !== n2.children) {
            hostSetElementText(el, n2.children);
          }
        }
      } else if (!optimized && dynamicChildren == null) {
        patchProps(el, oldProps, newProps, parentComponent, namespace2);
      }
      if ((vnodeHook = newProps.onVnodeUpdated) || dirs) {
        queuePostRenderEffect(() => {
          vnodeHook && invokeVNodeHook(vnodeHook, parentComponent, n2, n1);
          dirs && invokeDirectiveHook(n2, n1, parentComponent, "updated");
        }, parentSuspense);
      }
    };
    const patchBlockChildren = (oldChildren, newChildren, fallbackContainer, parentComponent, parentSuspense, namespace2, slotScopeIds) => {
      for (let i = 0; i < newChildren.length; i++) {
        const oldVNode = oldChildren[i];
        const newVNode = newChildren[i];
        const container = (
          // oldVNode may be an errored async setup() component inside Suspense
          // which will not have a mounted element
          oldVNode.el && // - In the case of a Fragment, we need to provide the actual parent
          // of the Fragment itself so it can move its children.
          (oldVNode.type === Fragment || // - In the case of different nodes, there is going to be a replacement
          // which also requires the correct parent container
          !isSameVNodeType(oldVNode, newVNode) || // - In the case of a component, it could contain anything.
          oldVNode.shapeFlag & (6 | 64 | 128)) ? hostParentNode(oldVNode.el) : (
            // In other cases, the parent container is not actually used so we
            // just pass the block element here to avoid a DOM parentNode call.
            fallbackContainer
          )
        );
        patch(
          oldVNode,
          newVNode,
          container,
          null,
          parentComponent,
          parentSuspense,
          namespace2,
          slotScopeIds,
          true
        );
      }
    };
    const patchProps = (el, oldProps, newProps, parentComponent, namespace2) => {
      if (oldProps !== newProps) {
        if (oldProps !== EMPTY_OBJ) {
          for (const key in oldProps) {
            if (!isReservedProp(key) && !(key in newProps)) {
              hostPatchProp(
                el,
                key,
                oldProps[key],
                null,
                namespace2,
                parentComponent
              );
            }
          }
        }
        for (const key in newProps) {
          if (isReservedProp(key)) continue;
          const next = newProps[key];
          const prev = oldProps[key];
          if (next !== prev && key !== "value") {
            hostPatchProp(el, key, prev, next, namespace2, parentComponent);
          }
        }
        if ("value" in newProps) {
          hostPatchProp(el, "value", oldProps.value, newProps.value, namespace2);
        }
      }
    };
    const processFragment = (n1, n2, container, anchor, parentComponent, parentSuspense, namespace2, slotScopeIds, optimized) => {
      const fragmentStartAnchor = n2.el = n1 ? n1.el : hostCreateText("");
      const fragmentEndAnchor = n2.anchor = n1 ? n1.anchor : hostCreateText("");
      let { patchFlag, dynamicChildren, slotScopeIds: fragmentSlotScopeIds } = n2;
      if (fragmentSlotScopeIds) {
        slotScopeIds = slotScopeIds ? slotScopeIds.concat(fragmentSlotScopeIds) : fragmentSlotScopeIds;
      }
      if (n1 == null) {
        hostInsert(fragmentStartAnchor, container, anchor);
        hostInsert(fragmentEndAnchor, container, anchor);
        mountChildren(
          // #10007
          // such fragment like `<></>` will be compiled into
          // a fragment which doesn't have a children.
          // In this case fallback to an empty array
          n2.children || [],
          container,
          fragmentEndAnchor,
          parentComponent,
          parentSuspense,
          namespace2,
          slotScopeIds,
          optimized
        );
      } else {
        if (patchFlag > 0 && patchFlag & 64 && dynamicChildren && // #2715 the previous fragment could've been a BAILed one as a result
        // of renderSlot() with no valid children
        n1.dynamicChildren && n1.dynamicChildren.length === dynamicChildren.length) {
          patchBlockChildren(
            n1.dynamicChildren,
            dynamicChildren,
            container,
            parentComponent,
            parentSuspense,
            namespace2,
            slotScopeIds
          );
          if (
            // #2080 if the stable fragment has a key, it's a <template v-for> that may
            //  get moved around. Make sure all root level vnodes inherit el.
            // #2134 or if it's a component root, it may also get moved around
            // as the component is being moved.
            n2.key != null || parentComponent && n2 === parentComponent.subTree
          ) {
            traverseStaticChildren(
              n1,
              n2,
              true
              /* shallow */
            );
          }
        } else {
          patchChildren(
            n1,
            n2,
            container,
            fragmentEndAnchor,
            parentComponent,
            parentSuspense,
            namespace2,
            slotScopeIds,
            optimized
          );
        }
      }
    };
    const processComponent = (n1, n2, container, anchor, parentComponent, parentSuspense, namespace2, slotScopeIds, optimized) => {
      n2.slotScopeIds = slotScopeIds;
      if (n1 == null) {
        if (n2.shapeFlag & 512) {
          parentComponent.ctx.activate(
            n2,
            container,
            anchor,
            namespace2,
            optimized
          );
        } else {
          mountComponent(
            n2,
            container,
            anchor,
            parentComponent,
            parentSuspense,
            namespace2,
            optimized
          );
        }
      } else {
        updateComponent(n1, n2, optimized);
      }
    };
    const mountComponent = (initialVNode, container, anchor, parentComponent, parentSuspense, namespace2, optimized) => {
      const instance = initialVNode.component = createComponentInstance(
        initialVNode,
        parentComponent,
        parentSuspense
      );
      if (isKeepAlive(initialVNode)) {
        instance.ctx.renderer = internals;
      }
      {
        setupComponent(instance, false, optimized);
      }
      if (instance.asyncDep) {
        parentSuspense && parentSuspense.registerDep(instance, setupRenderEffect, optimized);
        if (!initialVNode.el) {
          const placeholder = instance.subTree = createVNode(Comment);
          processCommentNode(null, placeholder, container, anchor);
          initialVNode.placeholder = placeholder.el;
        }
      } else {
        setupRenderEffect(
          instance,
          initialVNode,
          container,
          anchor,
          parentSuspense,
          namespace2,
          optimized
        );
      }
    };
    const updateComponent = (n1, n2, optimized) => {
      const instance = n2.component = n1.component;
      if (shouldUpdateComponent(n1, n2, optimized)) {
        if (instance.asyncDep && !instance.asyncResolved) {
          updateComponentPreRender(instance, n2, optimized);
          return;
        } else {
          instance.next = n2;
          instance.update();
        }
      } else {
        n2.el = n1.el;
        instance.vnode = n2;
      }
    };
    const setupRenderEffect = (instance, initialVNode, container, anchor, parentSuspense, namespace2, optimized) => {
      const componentUpdateFn = () => {
        if (!instance.isMounted) {
          let vnodeHook;
          const { el, props } = initialVNode;
          const { bm, m, parent, root: root2, type } = instance;
          const isAsyncWrapperVNode = isAsyncWrapper(initialVNode);
          toggleRecurse(instance, false);
          if (bm) {
            invokeArrayFns(bm);
          }
          if (!isAsyncWrapperVNode && (vnodeHook = props && props.onVnodeBeforeMount)) {
            invokeVNodeHook(vnodeHook, parent, initialVNode);
          }
          toggleRecurse(instance, true);
          {
            if (root2.ce && root2.ce._hasShadowRoot()) {
              root2.ce._injectChildStyle(
                type,
                instance.parent ? instance.parent.type : void 0
              );
            }
            const subTree = instance.subTree = renderComponentRoot(instance);
            patch(
              null,
              subTree,
              container,
              anchor,
              instance,
              parentSuspense,
              namespace2
            );
            initialVNode.el = subTree.el;
          }
          if (m) {
            queuePostRenderEffect(m, parentSuspense);
          }
          if (!isAsyncWrapperVNode && (vnodeHook = props && props.onVnodeMounted)) {
            const scopedInitialVNode = initialVNode;
            queuePostRenderEffect(
              () => invokeVNodeHook(vnodeHook, parent, scopedInitialVNode),
              parentSuspense
            );
          }
          if (initialVNode.shapeFlag & 256 || parent && isAsyncWrapper(parent.vnode) && parent.vnode.shapeFlag & 256) {
            instance.a && queuePostRenderEffect(instance.a, parentSuspense);
          }
          instance.isMounted = true;
          initialVNode = container = anchor = null;
        } else {
          let { next, bu, u, parent, vnode } = instance;
          {
            const nonHydratedAsyncRoot = locateNonHydratedAsyncRoot(instance);
            if (nonHydratedAsyncRoot) {
              if (next) {
                next.el = vnode.el;
                updateComponentPreRender(instance, next, optimized);
              }
              nonHydratedAsyncRoot.asyncDep.then(() => {
                queuePostRenderEffect(() => {
                  if (!instance.isUnmounted) update();
                }, parentSuspense);
              });
              return;
            }
          }
          let originNext = next;
          let vnodeHook;
          toggleRecurse(instance, false);
          if (next) {
            next.el = vnode.el;
            updateComponentPreRender(instance, next, optimized);
          } else {
            next = vnode;
          }
          if (bu) {
            invokeArrayFns(bu);
          }
          if (vnodeHook = next.props && next.props.onVnodeBeforeUpdate) {
            invokeVNodeHook(vnodeHook, parent, next, vnode);
          }
          toggleRecurse(instance, true);
          const nextTree = renderComponentRoot(instance);
          const prevTree = instance.subTree;
          instance.subTree = nextTree;
          patch(
            prevTree,
            nextTree,
            // parent may have changed if it's in a teleport
            hostParentNode(prevTree.el),
            // anchor may have changed if it's in a fragment
            getNextHostNode(prevTree),
            instance,
            parentSuspense,
            namespace2
          );
          next.el = nextTree.el;
          if (originNext === null) {
            updateHOCHostEl(instance, nextTree.el);
          }
          if (u) {
            queuePostRenderEffect(u, parentSuspense);
          }
          if (vnodeHook = next.props && next.props.onVnodeUpdated) {
            queuePostRenderEffect(
              () => invokeVNodeHook(vnodeHook, parent, next, vnode),
              parentSuspense
            );
          }
        }
      };
      instance.scope.on();
      const effect2 = instance.effect = new ReactiveEffect(componentUpdateFn);
      instance.scope.off();
      const update = instance.update = effect2.run.bind(effect2);
      const job = instance.job = effect2.runIfDirty.bind(effect2);
      job.i = instance;
      job.id = instance.uid;
      effect2.scheduler = () => queueJob(job);
      toggleRecurse(instance, true);
      update();
    };
    const updateComponentPreRender = (instance, nextVNode, optimized) => {
      nextVNode.component = instance;
      const prevProps = instance.vnode.props;
      instance.vnode = nextVNode;
      instance.next = null;
      updateProps(instance, nextVNode.props, prevProps, optimized);
      updateSlots(instance, nextVNode.children, optimized);
      pauseTracking();
      flushPreFlushCbs(instance);
      resetTracking();
    };
    const patchChildren = (n1, n2, container, anchor, parentComponent, parentSuspense, namespace2, slotScopeIds, optimized = false) => {
      const c1 = n1 && n1.children;
      const prevShapeFlag = n1 ? n1.shapeFlag : 0;
      const c2 = n2.children;
      const { patchFlag, shapeFlag } = n2;
      if (patchFlag > 0) {
        if (patchFlag & 128) {
          patchKeyedChildren(
            c1,
            c2,
            container,
            anchor,
            parentComponent,
            parentSuspense,
            namespace2,
            slotScopeIds,
            optimized
          );
          return;
        } else if (patchFlag & 256) {
          patchUnkeyedChildren(
            c1,
            c2,
            container,
            anchor,
            parentComponent,
            parentSuspense,
            namespace2,
            slotScopeIds,
            optimized
          );
          return;
        }
      }
      if (shapeFlag & 8) {
        if (prevShapeFlag & 16) {
          unmountChildren(c1, parentComponent, parentSuspense);
        }
        if (c2 !== c1) {
          hostSetElementText(container, c2);
        }
      } else {
        if (prevShapeFlag & 16) {
          if (shapeFlag & 16) {
            patchKeyedChildren(
              c1,
              c2,
              container,
              anchor,
              parentComponent,
              parentSuspense,
              namespace2,
              slotScopeIds,
              optimized
            );
          } else {
            unmountChildren(c1, parentComponent, parentSuspense, true);
          }
        } else {
          if (prevShapeFlag & 8) {
            hostSetElementText(container, "");
          }
          if (shapeFlag & 16) {
            mountChildren(
              c2,
              container,
              anchor,
              parentComponent,
              parentSuspense,
              namespace2,
              slotScopeIds,
              optimized
            );
          }
        }
      }
    };
    const patchUnkeyedChildren = (c1, c2, container, anchor, parentComponent, parentSuspense, namespace2, slotScopeIds, optimized) => {
      c1 = c1 || EMPTY_ARR;
      c2 = c2 || EMPTY_ARR;
      const oldLength = c1.length;
      const newLength = c2.length;
      const commonLength = Math.min(oldLength, newLength);
      let i;
      for (i = 0; i < commonLength; i++) {
        const nextChild = c2[i] = optimized ? cloneIfMounted(c2[i]) : normalizeVNode(c2[i]);
        patch(
          c1[i],
          nextChild,
          container,
          null,
          parentComponent,
          parentSuspense,
          namespace2,
          slotScopeIds,
          optimized
        );
      }
      if (oldLength > newLength) {
        unmountChildren(
          c1,
          parentComponent,
          parentSuspense,
          true,
          false,
          commonLength
        );
      } else {
        mountChildren(
          c2,
          container,
          anchor,
          parentComponent,
          parentSuspense,
          namespace2,
          slotScopeIds,
          optimized,
          commonLength
        );
      }
    };
    const patchKeyedChildren = (c1, c2, container, parentAnchor, parentComponent, parentSuspense, namespace2, slotScopeIds, optimized) => {
      let i = 0;
      const l2 = c2.length;
      let e1 = c1.length - 1;
      let e2 = l2 - 1;
      while (i <= e1 && i <= e2) {
        const n1 = c1[i];
        const n2 = c2[i] = optimized ? cloneIfMounted(c2[i]) : normalizeVNode(c2[i]);
        if (isSameVNodeType(n1, n2)) {
          patch(
            n1,
            n2,
            container,
            null,
            parentComponent,
            parentSuspense,
            namespace2,
            slotScopeIds,
            optimized
          );
        } else {
          break;
        }
        i++;
      }
      while (i <= e1 && i <= e2) {
        const n1 = c1[e1];
        const n2 = c2[e2] = optimized ? cloneIfMounted(c2[e2]) : normalizeVNode(c2[e2]);
        if (isSameVNodeType(n1, n2)) {
          patch(
            n1,
            n2,
            container,
            null,
            parentComponent,
            parentSuspense,
            namespace2,
            slotScopeIds,
            optimized
          );
        } else {
          break;
        }
        e1--;
        e2--;
      }
      if (i > e1) {
        if (i <= e2) {
          const nextPos = e2 + 1;
          const anchor = nextPos < l2 ? c2[nextPos].el : parentAnchor;
          while (i <= e2) {
            patch(
              null,
              c2[i] = optimized ? cloneIfMounted(c2[i]) : normalizeVNode(c2[i]),
              container,
              anchor,
              parentComponent,
              parentSuspense,
              namespace2,
              slotScopeIds,
              optimized
            );
            i++;
          }
        }
      } else if (i > e2) {
        while (i <= e1) {
          unmount2(c1[i], parentComponent, parentSuspense, true);
          i++;
        }
      } else {
        const s1 = i;
        const s2 = i;
        const keyToNewIndexMap = /* @__PURE__ */ new Map();
        for (i = s2; i <= e2; i++) {
          const nextChild = c2[i] = optimized ? cloneIfMounted(c2[i]) : normalizeVNode(c2[i]);
          if (nextChild.key != null) {
            keyToNewIndexMap.set(nextChild.key, i);
          }
        }
        let j;
        let patched = 0;
        const toBePatched = e2 - s2 + 1;
        let moved = false;
        let maxNewIndexSoFar = 0;
        const newIndexToOldIndexMap = new Array(toBePatched);
        for (i = 0; i < toBePatched; i++) newIndexToOldIndexMap[i] = 0;
        for (i = s1; i <= e1; i++) {
          const prevChild = c1[i];
          if (patched >= toBePatched) {
            unmount2(prevChild, parentComponent, parentSuspense, true);
            continue;
          }
          let newIndex;
          if (prevChild.key != null) {
            newIndex = keyToNewIndexMap.get(prevChild.key);
          } else {
            for (j = s2; j <= e2; j++) {
              if (newIndexToOldIndexMap[j - s2] === 0 && isSameVNodeType(prevChild, c2[j])) {
                newIndex = j;
                break;
              }
            }
          }
          if (newIndex === void 0) {
            unmount2(prevChild, parentComponent, parentSuspense, true);
          } else {
            newIndexToOldIndexMap[newIndex - s2] = i + 1;
            if (newIndex >= maxNewIndexSoFar) {
              maxNewIndexSoFar = newIndex;
            } else {
              moved = true;
            }
            patch(
              prevChild,
              c2[newIndex],
              container,
              null,
              parentComponent,
              parentSuspense,
              namespace2,
              slotScopeIds,
              optimized
            );
            patched++;
          }
        }
        const increasingNewIndexSequence = moved ? getSequence(newIndexToOldIndexMap) : EMPTY_ARR;
        j = increasingNewIndexSequence.length - 1;
        for (i = toBePatched - 1; i >= 0; i--) {
          const nextIndex = s2 + i;
          const nextChild = c2[nextIndex];
          const anchorVNode = c2[nextIndex + 1];
          const anchor = nextIndex + 1 < l2 ? (
            // #13559, #14173 fallback to el placeholder for unresolved async component
            anchorVNode.el || resolveAsyncComponentPlaceholder(anchorVNode)
          ) : parentAnchor;
          if (newIndexToOldIndexMap[i] === 0) {
            patch(
              null,
              nextChild,
              container,
              anchor,
              parentComponent,
              parentSuspense,
              namespace2,
              slotScopeIds,
              optimized
            );
          } else if (moved) {
            if (j < 0 || i !== increasingNewIndexSequence[j]) {
              move2(nextChild, container, anchor, 2);
            } else {
              j--;
            }
          }
        }
      }
    };
    const move2 = (vnode, container, anchor, moveType, parentSuspense = null) => {
      const { el, type, transition, children, shapeFlag } = vnode;
      if (shapeFlag & 6) {
        move2(vnode.component.subTree, container, anchor, moveType);
        return;
      }
      if (shapeFlag & 128) {
        vnode.suspense.move(container, anchor, moveType);
        return;
      }
      if (shapeFlag & 64) {
        type.move(vnode, container, anchor, internals);
        return;
      }
      if (type === Fragment) {
        hostInsert(el, container, anchor);
        for (let i = 0; i < children.length; i++) {
          move2(children[i], container, anchor, moveType);
        }
        hostInsert(vnode.anchor, container, anchor);
        return;
      }
      if (type === Static) {
        moveStaticNode(vnode, container, anchor);
        return;
      }
      const needTransition2 = moveType !== 2 && shapeFlag & 1 && transition;
      if (needTransition2) {
        if (moveType === 0) {
          transition.beforeEnter(el);
          hostInsert(el, container, anchor);
          queuePostRenderEffect(() => transition.enter(el), parentSuspense);
        } else {
          const { leave, delayLeave, afterLeave } = transition;
          const remove22 = () => {
            if (vnode.ctx.isUnmounted) {
              hostRemove(el);
            } else {
              hostInsert(el, container, anchor);
            }
          };
          const performLeave = () => {
            if (el._isLeaving) {
              el[leaveCbKey](
                true
                /* cancelled */
              );
            }
            leave(el, () => {
              remove22();
              afterLeave && afterLeave();
            });
          };
          if (delayLeave) {
            delayLeave(el, remove22, performLeave);
          } else {
            performLeave();
          }
        }
      } else {
        hostInsert(el, container, anchor);
      }
    };
    const unmount2 = (vnode, parentComponent, parentSuspense, doRemove = false, optimized = false) => {
      const {
        type,
        props,
        ref: ref3,
        children,
        dynamicChildren,
        shapeFlag,
        patchFlag,
        dirs,
        cacheIndex
      } = vnode;
      if (patchFlag === -2) {
        optimized = false;
      }
      if (ref3 != null) {
        pauseTracking();
        setRef(ref3, null, parentSuspense, vnode, true);
        resetTracking();
      }
      if (cacheIndex != null) {
        parentComponent.renderCache[cacheIndex] = void 0;
      }
      if (shapeFlag & 256) {
        parentComponent.ctx.deactivate(vnode);
        return;
      }
      const shouldInvokeDirs = shapeFlag & 1 && dirs;
      const shouldInvokeVnodeHook = !isAsyncWrapper(vnode);
      let vnodeHook;
      if (shouldInvokeVnodeHook && (vnodeHook = props && props.onVnodeBeforeUnmount)) {
        invokeVNodeHook(vnodeHook, parentComponent, vnode);
      }
      if (shapeFlag & 6) {
        unmountComponent(vnode.component, parentSuspense, doRemove);
      } else {
        if (shapeFlag & 128) {
          vnode.suspense.unmount(parentSuspense, doRemove);
          return;
        }
        if (shouldInvokeDirs) {
          invokeDirectiveHook(vnode, null, parentComponent, "beforeUnmount");
        }
        if (shapeFlag & 64) {
          vnode.type.remove(
            vnode,
            parentComponent,
            parentSuspense,
            internals,
            doRemove
          );
        } else if (dynamicChildren && // #5154
        // when v-once is used inside a block, setBlockTracking(-1) marks the
        // parent block with hasOnce: true
        // so that it doesn't take the fast path during unmount - otherwise
        // components nested in v-once are never unmounted.
        !dynamicChildren.hasOnce && // #1153: fast path should not be taken for non-stable (v-for) fragments
        (type !== Fragment || patchFlag > 0 && patchFlag & 64)) {
          unmountChildren(
            dynamicChildren,
            parentComponent,
            parentSuspense,
            false,
            true
          );
        } else if (type === Fragment && patchFlag & (128 | 256) || !optimized && shapeFlag & 16) {
          unmountChildren(children, parentComponent, parentSuspense);
        }
        if (doRemove) {
          remove2(vnode);
        }
      }
      if (shouldInvokeVnodeHook && (vnodeHook = props && props.onVnodeUnmounted) || shouldInvokeDirs) {
        queuePostRenderEffect(() => {
          vnodeHook && invokeVNodeHook(vnodeHook, parentComponent, vnode);
          shouldInvokeDirs && invokeDirectiveHook(vnode, null, parentComponent, "unmounted");
        }, parentSuspense);
      }
    };
    const remove2 = (vnode) => {
      const { type, el, anchor, transition } = vnode;
      if (type === Fragment) {
        {
          removeFragment(el, anchor);
        }
        return;
      }
      if (type === Static) {
        removeStaticNode(vnode);
        return;
      }
      const performRemove = () => {
        hostRemove(el);
        if (transition && !transition.persisted && transition.afterLeave) {
          transition.afterLeave();
        }
      };
      if (vnode.shapeFlag & 1 && transition && !transition.persisted) {
        const { leave, delayLeave } = transition;
        const performLeave = () => leave(el, performRemove);
        if (delayLeave) {
          delayLeave(vnode.el, performRemove, performLeave);
        } else {
          performLeave();
        }
      } else {
        performRemove();
      }
    };
    const removeFragment = (cur, end) => {
      let next;
      while (cur !== end) {
        next = hostNextSibling(cur);
        hostRemove(cur);
        cur = next;
      }
      hostRemove(end);
    };
    const unmountComponent = (instance, parentSuspense, doRemove) => {
      const { bum, scope, job, subTree, um, m, a } = instance;
      invalidateMount(m);
      invalidateMount(a);
      if (bum) {
        invokeArrayFns(bum);
      }
      scope.stop();
      if (job) {
        job.flags |= 8;
        unmount2(subTree, instance, parentSuspense, doRemove);
      }
      if (um) {
        queuePostRenderEffect(um, parentSuspense);
      }
      queuePostRenderEffect(() => {
        instance.isUnmounted = true;
      }, parentSuspense);
    };
    const unmountChildren = (children, parentComponent, parentSuspense, doRemove = false, optimized = false, start = 0) => {
      for (let i = start; i < children.length; i++) {
        unmount2(children[i], parentComponent, parentSuspense, doRemove, optimized);
      }
    };
    const getNextHostNode = (vnode) => {
      if (vnode.shapeFlag & 6) {
        return getNextHostNode(vnode.component.subTree);
      }
      if (vnode.shapeFlag & 128) {
        return vnode.suspense.next();
      }
      const el = hostNextSibling(vnode.anchor || vnode.el);
      const teleportEnd = el && el[TeleportEndKey];
      return teleportEnd ? hostNextSibling(teleportEnd) : el;
    };
    let isFlushing = false;
    const render2 = (vnode, container, namespace2) => {
      let instance;
      if (vnode == null) {
        if (container._vnode) {
          unmount2(container._vnode, null, null, true);
          instance = container._vnode.component;
        }
      } else {
        patch(
          container._vnode || null,
          vnode,
          container,
          null,
          null,
          null,
          namespace2
        );
      }
      container._vnode = vnode;
      if (!isFlushing) {
        isFlushing = true;
        flushPreFlushCbs(instance);
        flushPostFlushCbs();
        isFlushing = false;
      }
    };
    const internals = {
      p: patch,
      um: unmount2,
      m: move2,
      r: remove2,
      mt: mountComponent,
      mc: mountChildren,
      pc: patchChildren,
      pbc: patchBlockChildren,
      n: getNextHostNode,
      o: options
    };
    let hydrate;
    return {
      render: render2,
      hydrate,
      createApp: createAppAPI(render2)
    };
  }
  function resolveChildrenNamespace({ type, props }, currentNamespace) {
    return currentNamespace === "svg" && type === "foreignObject" || currentNamespace === "mathml" && type === "annotation-xml" && props && props.encoding && props.encoding.includes("html") ? void 0 : currentNamespace;
  }
  function toggleRecurse({ effect: effect2, job }, allowed) {
    if (allowed) {
      effect2.flags |= 32;
      job.flags |= 4;
    } else {
      effect2.flags &= -33;
      job.flags &= -5;
    }
  }
  function needTransition(parentSuspense, transition) {
    return (!parentSuspense || parentSuspense && !parentSuspense.pendingBranch) && transition && !transition.persisted;
  }
  function traverseStaticChildren(n1, n2, shallow = false) {
    const ch1 = n1.children;
    const ch2 = n2.children;
    if (isArray$1(ch1) && isArray$1(ch2)) {
      for (let i = 0; i < ch1.length; i++) {
        const c1 = ch1[i];
        let c2 = ch2[i];
        if (c2.shapeFlag & 1 && !c2.dynamicChildren) {
          if (c2.patchFlag <= 0 || c2.patchFlag === 32) {
            c2 = ch2[i] = cloneIfMounted(ch2[i]);
            c2.el = c1.el;
          }
          if (!shallow && c2.patchFlag !== -2)
            traverseStaticChildren(c1, c2);
        }
        if (c2.type === Text) {
          if (c2.patchFlag === -1) {
            c2 = ch2[i] = cloneIfMounted(c2);
          }
          c2.el = c1.el;
        }
        if (c2.type === Comment && !c2.el) {
          c2.el = c1.el;
        }
      }
    }
  }
  function getSequence(arr) {
    const p2 = arr.slice();
    const result = [0];
    let i, j, u, v, c2;
    const len = arr.length;
    for (i = 0; i < len; i++) {
      const arrI = arr[i];
      if (arrI !== 0) {
        j = result[result.length - 1];
        if (arr[j] < arrI) {
          p2[i] = j;
          result.push(i);
          continue;
        }
        u = 0;
        v = result.length - 1;
        while (u < v) {
          c2 = u + v >> 1;
          if (arr[result[c2]] < arrI) {
            u = c2 + 1;
          } else {
            v = c2;
          }
        }
        if (arrI < arr[result[u]]) {
          if (u > 0) {
            p2[i] = result[u - 1];
          }
          result[u] = i;
        }
      }
    }
    u = result.length;
    v = result[u - 1];
    while (u-- > 0) {
      result[u] = v;
      v = p2[v];
    }
    return result;
  }
  function locateNonHydratedAsyncRoot(instance) {
    const subComponent = instance.subTree.component;
    if (subComponent) {
      if (subComponent.asyncDep && !subComponent.asyncResolved) {
        return subComponent;
      } else {
        return locateNonHydratedAsyncRoot(subComponent);
      }
    }
  }
  function invalidateMount(hooks) {
    if (hooks) {
      for (let i = 0; i < hooks.length; i++)
        hooks[i].flags |= 8;
    }
  }
  function resolveAsyncComponentPlaceholder(anchorVnode) {
    if (anchorVnode.placeholder) {
      return anchorVnode.placeholder;
    }
    const instance = anchorVnode.component;
    if (instance) {
      return resolveAsyncComponentPlaceholder(instance.subTree);
    }
    return null;
  }
  const isSuspense = (type) => type.__isSuspense;
  function queueEffectWithSuspense(fn, suspense) {
    if (suspense && suspense.pendingBranch) {
      if (isArray$1(fn)) {
        suspense.effects.push(...fn);
      } else {
        suspense.effects.push(fn);
      }
    } else {
      queuePostFlushCb(fn);
    }
  }
  const Fragment = /* @__PURE__ */ Symbol.for("v-fgt");
  const Text = /* @__PURE__ */ Symbol.for("v-txt");
  const Comment = /* @__PURE__ */ Symbol.for("v-cmt");
  const Static = /* @__PURE__ */ Symbol.for("v-stc");
  const blockStack = [];
  let currentBlock = null;
  function openBlock(disableTracking = false) {
    blockStack.push(currentBlock = disableTracking ? null : []);
  }
  function closeBlock() {
    blockStack.pop();
    currentBlock = blockStack[blockStack.length - 1] || null;
  }
  let isBlockTreeEnabled = 1;
  function setBlockTracking(value, inVOnce = false) {
    isBlockTreeEnabled += value;
    if (value < 0 && currentBlock && inVOnce) {
      currentBlock.hasOnce = true;
    }
  }
  function setupBlock(vnode) {
    vnode.dynamicChildren = isBlockTreeEnabled > 0 ? currentBlock || EMPTY_ARR : null;
    closeBlock();
    if (isBlockTreeEnabled > 0 && currentBlock) {
      currentBlock.push(vnode);
    }
    return vnode;
  }
  function createElementBlock(type, props, children, patchFlag, dynamicProps, shapeFlag) {
    return setupBlock(
      createBaseVNode(
        type,
        props,
        children,
        patchFlag,
        dynamicProps,
        shapeFlag,
        true
      )
    );
  }
  function createBlock(type, props, children, patchFlag, dynamicProps) {
    return setupBlock(
      createVNode(
        type,
        props,
        children,
        patchFlag,
        dynamicProps,
        true
      )
    );
  }
  function isVNode(value) {
    return value ? value.__v_isVNode === true : false;
  }
  function isSameVNodeType(n1, n2) {
    return n1.type === n2.type && n1.key === n2.key;
  }
  const normalizeKey = ({ key }) => key != null ? key : null;
  const normalizeRef = ({
    ref: ref3,
    ref_key,
    ref_for
  }) => {
    if (typeof ref3 === "number") {
      ref3 = "" + ref3;
    }
    return ref3 != null ? isString(ref3) || /* @__PURE__ */ isRef(ref3) || isFunction$1(ref3) ? { i: currentRenderingInstance, r: ref3, k: ref_key, f: !!ref_for } : ref3 : null;
  };
  function createBaseVNode(type, props = null, children = null, patchFlag = 0, dynamicProps = null, shapeFlag = type === Fragment ? 0 : 1, isBlockNode = false, needFullChildrenNormalization = false) {
    const vnode = {
      __v_isVNode: true,
      __v_skip: true,
      type,
      props,
      key: props && normalizeKey(props),
      ref: props && normalizeRef(props),
      scopeId: currentScopeId,
      slotScopeIds: null,
      children,
      component: null,
      suspense: null,
      ssContent: null,
      ssFallback: null,
      dirs: null,
      transition: null,
      el: null,
      anchor: null,
      target: null,
      targetStart: null,
      targetAnchor: null,
      staticCount: 0,
      shapeFlag,
      patchFlag,
      dynamicProps,
      dynamicChildren: null,
      appContext: null,
      ctx: currentRenderingInstance
    };
    if (needFullChildrenNormalization) {
      normalizeChildren(vnode, children);
      if (shapeFlag & 128) {
        type.normalize(vnode);
      }
    } else if (children) {
      vnode.shapeFlag |= isString(children) ? 8 : 16;
    }
    if (isBlockTreeEnabled > 0 && // avoid a block node from tracking itself
    !isBlockNode && // has current parent block
    currentBlock && // presence of a patch flag indicates this node needs patching on updates.
    // component nodes also should always be patched, because even if the
    // component doesn't need to update, it needs to persist the instance on to
    // the next vnode so that it can be properly unmounted later.
    (vnode.patchFlag > 0 || shapeFlag & 6) && // the EVENTS flag is only for hydration and if it is the only flag, the
    // vnode should not be considered dynamic due to handler caching.
    vnode.patchFlag !== 32) {
      currentBlock.push(vnode);
    }
    return vnode;
  }
  const createVNode = _createVNode;
  function _createVNode(type, props = null, children = null, patchFlag = 0, dynamicProps = null, isBlockNode = false) {
    if (!type || type === NULL_DYNAMIC_COMPONENT) {
      type = Comment;
    }
    if (isVNode(type)) {
      const cloned = cloneVNode(
        type,
        props,
        true
        /* mergeRef: true */
      );
      if (children) {
        normalizeChildren(cloned, children);
      }
      if (isBlockTreeEnabled > 0 && !isBlockNode && currentBlock) {
        if (cloned.shapeFlag & 6) {
          currentBlock[currentBlock.indexOf(type)] = cloned;
        } else {
          currentBlock.push(cloned);
        }
      }
      cloned.patchFlag = -2;
      return cloned;
    }
    if (isClassComponent(type)) {
      type = type.__vccOpts;
    }
    if (props) {
      props = guardReactiveProps(props);
      let { class: klass, style: style2 } = props;
      if (klass && !isString(klass)) {
        props.class = normalizeClass(klass);
      }
      if (isObject$1(style2)) {
        if (/* @__PURE__ */ isProxy(style2) && !isArray$1(style2)) {
          style2 = extend({}, style2);
        }
        props.style = normalizeStyle(style2);
      }
    }
    const shapeFlag = isString(type) ? 1 : isSuspense(type) ? 128 : isTeleport(type) ? 64 : isObject$1(type) ? 4 : isFunction$1(type) ? 2 : 0;
    return createBaseVNode(
      type,
      props,
      children,
      patchFlag,
      dynamicProps,
      shapeFlag,
      isBlockNode,
      true
    );
  }
  function guardReactiveProps(props) {
    if (!props) return null;
    return /* @__PURE__ */ isProxy(props) || isInternalObject(props) ? extend({}, props) : props;
  }
  function cloneVNode(vnode, extraProps, mergeRef = false, cloneTransition = false) {
    const { props, ref: ref3, patchFlag, children, transition } = vnode;
    const mergedProps = extraProps ? mergeProps(props || {}, extraProps) : props;
    const cloned = {
      __v_isVNode: true,
      __v_skip: true,
      type: vnode.type,
      props: mergedProps,
      key: mergedProps && normalizeKey(mergedProps),
      ref: extraProps && extraProps.ref ? (
        // #2078 in the case of <component :is="vnode" ref="extra"/>
        // if the vnode itself already has a ref, cloneVNode will need to merge
        // the refs so the single vnode can be set on multiple refs
        mergeRef && ref3 ? isArray$1(ref3) ? ref3.concat(normalizeRef(extraProps)) : [ref3, normalizeRef(extraProps)] : normalizeRef(extraProps)
      ) : ref3,
      scopeId: vnode.scopeId,
      slotScopeIds: vnode.slotScopeIds,
      children,
      target: vnode.target,
      targetStart: vnode.targetStart,
      targetAnchor: vnode.targetAnchor,
      staticCount: vnode.staticCount,
      shapeFlag: vnode.shapeFlag,
      // if the vnode is cloned with extra props, we can no longer assume its
      // existing patch flag to be reliable and need to add the FULL_PROPS flag.
      // note: preserve flag for fragments since they use the flag for children
      // fast paths only.
      patchFlag: extraProps && vnode.type !== Fragment ? patchFlag === -1 ? 16 : patchFlag | 16 : patchFlag,
      dynamicProps: vnode.dynamicProps,
      dynamicChildren: vnode.dynamicChildren,
      appContext: vnode.appContext,
      dirs: vnode.dirs,
      transition,
      // These should technically only be non-null on mounted VNodes. However,
      // they *should* be copied for kept-alive vnodes. So we just always copy
      // them since them being non-null during a mount doesn't affect the logic as
      // they will simply be overwritten.
      component: vnode.component,
      suspense: vnode.suspense,
      ssContent: vnode.ssContent && cloneVNode(vnode.ssContent),
      ssFallback: vnode.ssFallback && cloneVNode(vnode.ssFallback),
      placeholder: vnode.placeholder,
      el: vnode.el,
      anchor: vnode.anchor,
      ctx: vnode.ctx,
      ce: vnode.ce
    };
    if (transition && cloneTransition) {
      setTransitionHooks(
        cloned,
        transition.clone(cloned)
      );
    }
    return cloned;
  }
  function createTextVNode(text = " ", flag = 0) {
    return createVNode(Text, null, text, flag);
  }
  function createCommentVNode(text = "", asBlock = false) {
    return asBlock ? (openBlock(), createBlock(Comment, null, text)) : createVNode(Comment, null, text);
  }
  function normalizeVNode(child) {
    if (child == null || typeof child === "boolean") {
      return createVNode(Comment);
    } else if (isArray$1(child)) {
      return createVNode(
        Fragment,
        null,
        // #3666, avoid reference pollution when reusing vnode
        child.slice()
      );
    } else if (isVNode(child)) {
      return cloneIfMounted(child);
    } else {
      return createVNode(Text, null, String(child));
    }
  }
  function cloneIfMounted(child) {
    return child.el === null && child.patchFlag !== -1 || child.memo ? child : cloneVNode(child);
  }
  function normalizeChildren(vnode, children) {
    let type = 0;
    const { shapeFlag } = vnode;
    if (children == null) {
      children = null;
    } else if (isArray$1(children)) {
      type = 16;
    } else if (typeof children === "object") {
      if (shapeFlag & (1 | 64)) {
        const slot = children.default;
        if (slot) {
          slot._c && (slot._d = false);
          normalizeChildren(vnode, slot());
          slot._c && (slot._d = true);
        }
        return;
      } else {
        type = 32;
        const slotFlag = children._;
        if (!slotFlag && !isInternalObject(children)) {
          children._ctx = currentRenderingInstance;
        } else if (slotFlag === 3 && currentRenderingInstance) {
          if (currentRenderingInstance.slots._ === 1) {
            children._ = 1;
          } else {
            children._ = 2;
            vnode.patchFlag |= 1024;
          }
        }
      }
    } else if (isFunction$1(children)) {
      children = { default: children, _ctx: currentRenderingInstance };
      type = 32;
    } else {
      children = String(children);
      if (shapeFlag & 64) {
        type = 16;
        children = [createTextVNode(children)];
      } else {
        type = 8;
      }
    }
    vnode.children = children;
    vnode.shapeFlag |= type;
  }
  function mergeProps(...args) {
    const ret = {};
    for (let i = 0; i < args.length; i++) {
      const toMerge = args[i];
      for (const key in toMerge) {
        if (key === "class") {
          if (ret.class !== toMerge.class) {
            ret.class = normalizeClass([ret.class, toMerge.class]);
          }
        } else if (key === "style") {
          ret.style = normalizeStyle([ret.style, toMerge.style]);
        } else if (isOn(key)) {
          const existing = ret[key];
          const incoming = toMerge[key];
          if (incoming && existing !== incoming && !(isArray$1(existing) && existing.includes(incoming))) {
            ret[key] = existing ? [].concat(existing, incoming) : incoming;
          }
        } else if (key !== "") {
          ret[key] = toMerge[key];
        }
      }
    }
    return ret;
  }
  function invokeVNodeHook(hook, instance, vnode, prevVNode = null) {
    callWithAsyncErrorHandling(hook, instance, 7, [
      vnode,
      prevVNode
    ]);
  }
  const emptyAppContext = createAppContext();
  let uid = 0;
  function createComponentInstance(vnode, parent, suspense) {
    const type = vnode.type;
    const appContext = (parent ? parent.appContext : vnode.appContext) || emptyAppContext;
    const instance = {
      uid: uid++,
      vnode,
      type,
      parent,
      appContext,
      root: null,
      // to be immediately set
      next: null,
      subTree: null,
      // will be set synchronously right after creation
      effect: null,
      update: null,
      // will be set synchronously right after creation
      job: null,
      scope: new EffectScope(
        true
        /* detached */
      ),
      render: null,
      proxy: null,
      exposed: null,
      exposeProxy: null,
      withProxy: null,
      provides: parent ? parent.provides : Object.create(appContext.provides),
      ids: parent ? parent.ids : ["", 0, 0],
      accessCache: null,
      renderCache: [],
      // local resolved assets
      components: null,
      directives: null,
      // resolved props and emits options
      propsOptions: normalizePropsOptions(type, appContext),
      emitsOptions: normalizeEmitsOptions(type, appContext),
      // emit
      emit: null,
      // to be set immediately
      emitted: null,
      // props default value
      propsDefaults: EMPTY_OBJ,
      // inheritAttrs
      inheritAttrs: type.inheritAttrs,
      // state
      ctx: EMPTY_OBJ,
      data: EMPTY_OBJ,
      props: EMPTY_OBJ,
      attrs: EMPTY_OBJ,
      slots: EMPTY_OBJ,
      refs: EMPTY_OBJ,
      setupState: EMPTY_OBJ,
      setupContext: null,
      // suspense related
      suspense,
      suspenseId: suspense ? suspense.pendingId : 0,
      asyncDep: null,
      asyncResolved: false,
      // lifecycle hooks
      // not using enums here because it results in computed properties
      isMounted: false,
      isUnmounted: false,
      isDeactivated: false,
      bc: null,
      c: null,
      bm: null,
      m: null,
      bu: null,
      u: null,
      um: null,
      bum: null,
      da: null,
      a: null,
      rtg: null,
      rtc: null,
      ec: null,
      sp: null
    };
    {
      instance.ctx = { _: instance };
    }
    instance.root = parent ? parent.root : instance;
    instance.emit = emit.bind(null, instance);
    if (vnode.ce) {
      vnode.ce(instance);
    }
    return instance;
  }
  let currentInstance = null;
  const getCurrentInstance = () => currentInstance || currentRenderingInstance;
  let internalSetCurrentInstance;
  let setInSSRSetupState;
  {
    const g = getGlobalThis();
    const registerGlobalSetter = (key, setter) => {
      let setters;
      if (!(setters = g[key])) setters = g[key] = [];
      setters.push(setter);
      return (v) => {
        if (setters.length > 1) setters.forEach((set) => set(v));
        else setters[0](v);
      };
    };
    internalSetCurrentInstance = registerGlobalSetter(
      `__VUE_INSTANCE_SETTERS__`,
      (v) => currentInstance = v
    );
    setInSSRSetupState = registerGlobalSetter(
      `__VUE_SSR_SETTERS__`,
      (v) => isInSSRComponentSetup = v
    );
  }
  const setCurrentInstance = (instance) => {
    const prev = currentInstance;
    internalSetCurrentInstance(instance);
    instance.scope.on();
    return () => {
      instance.scope.off();
      internalSetCurrentInstance(prev);
    };
  };
  const unsetCurrentInstance = () => {
    currentInstance && currentInstance.scope.off();
    internalSetCurrentInstance(null);
  };
  function isStatefulComponent(instance) {
    return instance.vnode.shapeFlag & 4;
  }
  let isInSSRComponentSetup = false;
  function setupComponent(instance, isSSR = false, optimized = false) {
    isSSR && setInSSRSetupState(isSSR);
    const { props, children } = instance.vnode;
    const isStateful = isStatefulComponent(instance);
    initProps(instance, props, isStateful, isSSR);
    initSlots(instance, children, optimized || isSSR);
    const setupResult = isStateful ? setupStatefulComponent(instance, isSSR) : void 0;
    isSSR && setInSSRSetupState(false);
    return setupResult;
  }
  function setupStatefulComponent(instance, isSSR) {
    const Component = instance.type;
    instance.accessCache = /* @__PURE__ */ Object.create(null);
    instance.proxy = new Proxy(instance.ctx, PublicInstanceProxyHandlers);
    const { setup } = Component;
    if (setup) {
      pauseTracking();
      const setupContext = instance.setupContext = setup.length > 1 ? createSetupContext(instance) : null;
      const reset = setCurrentInstance(instance);
      const setupResult = callWithErrorHandling(
        setup,
        instance,
        0,
        [
          instance.props,
          setupContext
        ]
      );
      const isAsyncSetup = isPromise(setupResult);
      resetTracking();
      reset();
      if ((isAsyncSetup || instance.sp) && !isAsyncWrapper(instance)) {
        markAsyncBoundary(instance);
      }
      if (isAsyncSetup) {
        setupResult.then(unsetCurrentInstance, unsetCurrentInstance);
        if (isSSR) {
          return setupResult.then((resolvedResult) => {
            handleSetupResult(instance, resolvedResult);
          }).catch((e) => {
            handleError(e, instance, 0);
          });
        } else {
          instance.asyncDep = setupResult;
        }
      } else {
        handleSetupResult(instance, setupResult);
      }
    } else {
      finishComponentSetup(instance);
    }
  }
  function handleSetupResult(instance, setupResult, isSSR) {
    if (isFunction$1(setupResult)) {
      if (instance.type.__ssrInlineRender) {
        instance.ssrRender = setupResult;
      } else {
        instance.render = setupResult;
      }
    } else if (isObject$1(setupResult)) {
      instance.setupState = proxyRefs(setupResult);
    } else ;
    finishComponentSetup(instance);
  }
  function finishComponentSetup(instance, isSSR, skipOptions) {
    const Component = instance.type;
    if (!instance.render) {
      instance.render = Component.render || NOOP;
    }
    {
      const reset = setCurrentInstance(instance);
      pauseTracking();
      try {
        applyOptions(instance);
      } finally {
        resetTracking();
        reset();
      }
    }
  }
  const attrsProxyHandlers = {
    get(target, key) {
      track(target, "get", "");
      return target[key];
    }
  };
  function createSetupContext(instance) {
    const expose = (exposed) => {
      instance.exposed = exposed || {};
    };
    {
      return {
        attrs: new Proxy(instance.attrs, attrsProxyHandlers),
        slots: instance.slots,
        emit: instance.emit,
        expose
      };
    }
  }
  function getComponentPublicInstance(instance) {
    if (instance.exposed) {
      return instance.exposeProxy || (instance.exposeProxy = new Proxy(proxyRefs(markRaw(instance.exposed)), {
        get(target, key) {
          if (key in target) {
            return target[key];
          } else if (key in publicPropertiesMap) {
            return publicPropertiesMap[key](instance);
          }
        },
        has(target, key) {
          return key in target || key in publicPropertiesMap;
        }
      }));
    } else {
      return instance.proxy;
    }
  }
  const classifyRE = /(?:^|[-_])\w/g;
  const classify = (str) => str.replace(classifyRE, (c2) => c2.toUpperCase()).replace(/[-_]/g, "");
  function getComponentName(Component, includeInferred = true) {
    return isFunction$1(Component) ? Component.displayName || Component.name : Component.name || includeInferred && Component.__name;
  }
  function formatComponentName(instance, Component, isRoot = false) {
    let name = getComponentName(Component);
    if (!name && Component.__file) {
      const match2 = Component.__file.match(/([^/\\]+)\.\w+$/);
      if (match2) {
        name = match2[1];
      }
    }
    if (!name && instance) {
      const inferFromRegistry = (registry) => {
        for (const key in registry) {
          if (registry[key] === Component) {
            return key;
          }
        }
      };
      name = inferFromRegistry(instance.components) || instance.parent && inferFromRegistry(
        instance.parent.type.components
      ) || inferFromRegistry(instance.appContext.components);
    }
    return name ? classify(name) : isRoot ? `App` : `Anonymous`;
  }
  function isClassComponent(value) {
    return isFunction$1(value) && "__vccOpts" in value;
  }
  const computed = (getterOrOptions, debugOptions) => {
    const c2 = /* @__PURE__ */ computed$1(getterOrOptions, debugOptions, isInSSRComponentSetup);
    return c2;
  };
  function h(type, propsOrChildren, children) {
    try {
      setBlockTracking(-1);
      const l = arguments.length;
      if (l === 2) {
        if (isObject$1(propsOrChildren) && !isArray$1(propsOrChildren)) {
          if (isVNode(propsOrChildren)) {
            return createVNode(type, null, [propsOrChildren]);
          }
          return createVNode(type, propsOrChildren);
        } else {
          return createVNode(type, null, propsOrChildren);
        }
      } else {
        if (l > 3) {
          children = Array.prototype.slice.call(arguments, 2);
        } else if (l === 3 && isVNode(children)) {
          children = [children];
        }
        return createVNode(type, propsOrChildren, children);
      }
    } finally {
      setBlockTracking(1);
    }
  }
  const version = "3.5.30";
  /**
  * @vue/runtime-dom v3.5.30
  * (c) 2018-present Yuxi (Evan) You and Vue contributors
  * @license MIT
  **/
  let policy = void 0;
  const tt = typeof window !== "undefined" && window.trustedTypes;
  if (tt) {
    try {
      policy = /* @__PURE__ */ tt.createPolicy("vue", {
        createHTML: (val) => val
      });
    } catch (e) {
    }
  }
  const unsafeToTrustedHTML = policy ? (val) => policy.createHTML(val) : (val) => val;
  const svgNS = "http://www.w3.org/2000/svg";
  const mathmlNS = "http://www.w3.org/1998/Math/MathML";
  const doc = typeof document !== "undefined" ? document : null;
  const templateContainer = doc && /* @__PURE__ */ doc.createElement("template");
  const nodeOps = {
    insert: (child, parent, anchor) => {
      parent.insertBefore(child, anchor || null);
    },
    remove: (child) => {
      const parent = child.parentNode;
      if (parent) {
        parent.removeChild(child);
      }
    },
    createElement: (tag, namespace2, is, props) => {
      const el = namespace2 === "svg" ? doc.createElementNS(svgNS, tag) : namespace2 === "mathml" ? doc.createElementNS(mathmlNS, tag) : is ? doc.createElement(tag, { is }) : doc.createElement(tag);
      if (tag === "select" && props && props.multiple != null) {
        el.setAttribute("multiple", props.multiple);
      }
      return el;
    },
    createText: (text) => doc.createTextNode(text),
    createComment: (text) => doc.createComment(text),
    setText: (node, text) => {
      node.nodeValue = text;
    },
    setElementText: (el, text) => {
      el.textContent = text;
    },
    parentNode: (node) => node.parentNode,
    nextSibling: (node) => node.nextSibling,
    querySelector: (selector) => doc.querySelector(selector),
    setScopeId(el, id) {
      el.setAttribute(id, "");
    },
    // __UNSAFE__
    // Reason: innerHTML.
    // Static content here can only come from compiled templates.
    // As long as the user only uses trusted templates, this is safe.
    insertStaticContent(content, parent, anchor, namespace2, start, end) {
      const before = anchor ? anchor.previousSibling : parent.lastChild;
      if (start && (start === end || start.nextSibling)) {
        while (true) {
          parent.insertBefore(start.cloneNode(true), anchor);
          if (start === end || !(start = start.nextSibling)) break;
        }
      } else {
        templateContainer.innerHTML = unsafeToTrustedHTML(
          namespace2 === "svg" ? `<svg>${content}</svg>` : namespace2 === "mathml" ? `<math>${content}</math>` : content
        );
        const template = templateContainer.content;
        if (namespace2 === "svg" || namespace2 === "mathml") {
          const wrapper = template.firstChild;
          while (wrapper.firstChild) {
            template.appendChild(wrapper.firstChild);
          }
          template.removeChild(wrapper);
        }
        parent.insertBefore(template, anchor);
      }
      return [
        // first
        before ? before.nextSibling : parent.firstChild,
        // last
        anchor ? anchor.previousSibling : parent.lastChild
      ];
    }
  };
  const TRANSITION = "transition";
  const ANIMATION = "animation";
  const vtcKey = /* @__PURE__ */ Symbol("_vtc");
  const DOMTransitionPropsValidators = {
    name: String,
    type: String,
    css: {
      type: Boolean,
      default: true
    },
    duration: [String, Number, Object],
    enterFromClass: String,
    enterActiveClass: String,
    enterToClass: String,
    appearFromClass: String,
    appearActiveClass: String,
    appearToClass: String,
    leaveFromClass: String,
    leaveActiveClass: String,
    leaveToClass: String
  };
  const TransitionPropsValidators = /* @__PURE__ */ extend(
    {},
    BaseTransitionPropsValidators,
    DOMTransitionPropsValidators
  );
  const decorate$1 = (t) => {
    t.displayName = "Transition";
    t.props = TransitionPropsValidators;
    return t;
  };
  const Transition = /* @__PURE__ */ decorate$1(
    (props, { slots }) => h(BaseTransition, resolveTransitionProps(props), slots)
  );
  const callHook = (hook, args = []) => {
    if (isArray$1(hook)) {
      hook.forEach((h2) => h2(...args));
    } else if (hook) {
      hook(...args);
    }
  };
  const hasExplicitCallback = (hook) => {
    return hook ? isArray$1(hook) ? hook.some((h2) => h2.length > 1) : hook.length > 1 : false;
  };
  function resolveTransitionProps(rawProps) {
    const baseProps = {};
    for (const key in rawProps) {
      if (!(key in DOMTransitionPropsValidators)) {
        baseProps[key] = rawProps[key];
      }
    }
    if (rawProps.css === false) {
      return baseProps;
    }
    const {
      name = "v",
      type,
      duration: duration2,
      enterFromClass = `${name}-enter-from`,
      enterActiveClass = `${name}-enter-active`,
      enterToClass = `${name}-enter-to`,
      appearFromClass = enterFromClass,
      appearActiveClass = enterActiveClass,
      appearToClass = enterToClass,
      leaveFromClass = `${name}-leave-from`,
      leaveActiveClass = `${name}-leave-active`,
      leaveToClass = `${name}-leave-to`
    } = rawProps;
    const durations = normalizeDuration(duration2);
    const enterDuration = durations && durations[0];
    const leaveDuration = durations && durations[1];
    const {
      onBeforeEnter,
      onEnter,
      onEnterCancelled,
      onLeave,
      onLeaveCancelled,
      onBeforeAppear = onBeforeEnter,
      onAppear = onEnter,
      onAppearCancelled = onEnterCancelled
    } = baseProps;
    const finishEnter = (el, isAppear, done, isCancelled) => {
      el._enterCancelled = isCancelled;
      removeTransitionClass(el, isAppear ? appearToClass : enterToClass);
      removeTransitionClass(el, isAppear ? appearActiveClass : enterActiveClass);
      done && done();
    };
    const finishLeave = (el, done) => {
      el._isLeaving = false;
      removeTransitionClass(el, leaveFromClass);
      removeTransitionClass(el, leaveToClass);
      removeTransitionClass(el, leaveActiveClass);
      done && done();
    };
    const makeEnterHook = (isAppear) => {
      return (el, done) => {
        const hook = isAppear ? onAppear : onEnter;
        const resolve2 = () => finishEnter(el, isAppear, done);
        callHook(hook, [el, resolve2]);
        nextFrame(() => {
          removeTransitionClass(el, isAppear ? appearFromClass : enterFromClass);
          addTransitionClass(el, isAppear ? appearToClass : enterToClass);
          if (!hasExplicitCallback(hook)) {
            whenTransitionEnds(el, type, enterDuration, resolve2);
          }
        });
      };
    };
    return extend(baseProps, {
      onBeforeEnter(el) {
        callHook(onBeforeEnter, [el]);
        addTransitionClass(el, enterFromClass);
        addTransitionClass(el, enterActiveClass);
      },
      onBeforeAppear(el) {
        callHook(onBeforeAppear, [el]);
        addTransitionClass(el, appearFromClass);
        addTransitionClass(el, appearActiveClass);
      },
      onEnter: makeEnterHook(false),
      onAppear: makeEnterHook(true),
      onLeave(el, done) {
        el._isLeaving = true;
        const resolve2 = () => finishLeave(el, done);
        addTransitionClass(el, leaveFromClass);
        if (!el._enterCancelled) {
          forceReflow(el);
          addTransitionClass(el, leaveActiveClass);
        } else {
          addTransitionClass(el, leaveActiveClass);
          forceReflow(el);
        }
        nextFrame(() => {
          if (!el._isLeaving) {
            return;
          }
          removeTransitionClass(el, leaveFromClass);
          addTransitionClass(el, leaveToClass);
          if (!hasExplicitCallback(onLeave)) {
            whenTransitionEnds(el, type, leaveDuration, resolve2);
          }
        });
        callHook(onLeave, [el, resolve2]);
      },
      onEnterCancelled(el) {
        finishEnter(el, false, void 0, true);
        callHook(onEnterCancelled, [el]);
      },
      onAppearCancelled(el) {
        finishEnter(el, true, void 0, true);
        callHook(onAppearCancelled, [el]);
      },
      onLeaveCancelled(el) {
        finishLeave(el);
        callHook(onLeaveCancelled, [el]);
      }
    });
  }
  function normalizeDuration(duration2) {
    if (duration2 == null) {
      return null;
    } else if (isObject$1(duration2)) {
      return [NumberOf(duration2.enter), NumberOf(duration2.leave)];
    } else {
      const n = NumberOf(duration2);
      return [n, n];
    }
  }
  function NumberOf(val) {
    const res = toNumber(val);
    return res;
  }
  function addTransitionClass(el, cls) {
    cls.split(/\s+/).forEach((c2) => c2 && el.classList.add(c2));
    (el[vtcKey] || (el[vtcKey] = /* @__PURE__ */ new Set())).add(cls);
  }
  function removeTransitionClass(el, cls) {
    cls.split(/\s+/).forEach((c2) => c2 && el.classList.remove(c2));
    const _vtc = el[vtcKey];
    if (_vtc) {
      _vtc.delete(cls);
      if (!_vtc.size) {
        el[vtcKey] = void 0;
      }
    }
  }
  function nextFrame(cb) {
    requestAnimationFrame(() => {
      requestAnimationFrame(cb);
    });
  }
  let endId = 0;
  function whenTransitionEnds(el, expectedType, explicitTimeout, resolve2) {
    const id = el._endId = ++endId;
    const resolveIfNotStale = () => {
      if (id === el._endId) {
        resolve2();
      }
    };
    if (explicitTimeout != null) {
      return setTimeout(resolveIfNotStale, explicitTimeout);
    }
    const { type, timeout, propCount } = getTransitionInfo(el, expectedType);
    if (!type) {
      return resolve2();
    }
    const endEvent = type + "end";
    let ended = 0;
    const end = () => {
      el.removeEventListener(endEvent, onEnd);
      resolveIfNotStale();
    };
    const onEnd = (e) => {
      if (e.target === el && ++ended >= propCount) {
        end();
      }
    };
    setTimeout(() => {
      if (ended < propCount) {
        end();
      }
    }, timeout + 1);
    el.addEventListener(endEvent, onEnd);
  }
  function getTransitionInfo(el, expectedType) {
    const styles = window.getComputedStyle(el);
    const getStyleProperties = (key) => (styles[key] || "").split(", ");
    const transitionDelays = getStyleProperties(`${TRANSITION}Delay`);
    const transitionDurations = getStyleProperties(`${TRANSITION}Duration`);
    const transitionTimeout = getTimeout(transitionDelays, transitionDurations);
    const animationDelays = getStyleProperties(`${ANIMATION}Delay`);
    const animationDurations = getStyleProperties(`${ANIMATION}Duration`);
    const animationTimeout = getTimeout(animationDelays, animationDurations);
    let type = null;
    let timeout = 0;
    let propCount = 0;
    if (expectedType === TRANSITION) {
      if (transitionTimeout > 0) {
        type = TRANSITION;
        timeout = transitionTimeout;
        propCount = transitionDurations.length;
      }
    } else if (expectedType === ANIMATION) {
      if (animationTimeout > 0) {
        type = ANIMATION;
        timeout = animationTimeout;
        propCount = animationDurations.length;
      }
    } else {
      timeout = Math.max(transitionTimeout, animationTimeout);
      type = timeout > 0 ? transitionTimeout > animationTimeout ? TRANSITION : ANIMATION : null;
      propCount = type ? type === TRANSITION ? transitionDurations.length : animationDurations.length : 0;
    }
    const hasTransform = type === TRANSITION && /\b(?:transform|all)(?:,|$)/.test(
      getStyleProperties(`${TRANSITION}Property`).toString()
    );
    return {
      type,
      timeout,
      propCount,
      hasTransform
    };
  }
  function getTimeout(delays, durations) {
    while (delays.length < durations.length) {
      delays = delays.concat(delays);
    }
    return Math.max(...durations.map((d, i) => toMs(d) + toMs(delays[i])));
  }
  function toMs(s) {
    if (s === "auto") return 0;
    return Number(s.slice(0, -1).replace(",", ".")) * 1e3;
  }
  function forceReflow(el) {
    const targetDocument = el ? el.ownerDocument : document;
    return targetDocument.body.offsetHeight;
  }
  function patchClass(el, value, isSVG2) {
    const transitionClasses = el[vtcKey];
    if (transitionClasses) {
      value = (value ? [value, ...transitionClasses] : [...transitionClasses]).join(" ");
    }
    if (value == null) {
      el.removeAttribute("class");
    } else if (isSVG2) {
      el.setAttribute("class", value);
    } else {
      el.className = value;
    }
  }
  const vShowOriginalDisplay = /* @__PURE__ */ Symbol("_vod");
  const vShowHidden = /* @__PURE__ */ Symbol("_vsh");
  const vShow = {
    // used for prop mismatch check during hydration
    name: "show",
    beforeMount(el, { value }, { transition }) {
      el[vShowOriginalDisplay] = el.style.display === "none" ? "" : el.style.display;
      if (transition && value) {
        transition.beforeEnter(el);
      } else {
        setDisplay(el, value);
      }
    },
    mounted(el, { value }, { transition }) {
      if (transition && value) {
        transition.enter(el);
      }
    },
    updated(el, { value, oldValue }, { transition }) {
      if (!value === !oldValue) return;
      if (transition) {
        if (value) {
          transition.beforeEnter(el);
          setDisplay(el, true);
          transition.enter(el);
        } else {
          transition.leave(el, () => {
            setDisplay(el, false);
          });
        }
      } else {
        setDisplay(el, value);
      }
    },
    beforeUnmount(el, { value }) {
      setDisplay(el, value);
    }
  };
  function setDisplay(el, value) {
    el.style.display = value ? el[vShowOriginalDisplay] : "none";
    el[vShowHidden] = !value;
  }
  const CSS_VAR_TEXT = /* @__PURE__ */ Symbol("");
  const displayRE = /(?:^|;)\s*display\s*:/;
  function patchStyle(el, prev, next) {
    const style2 = el.style;
    const isCssString = isString(next);
    let hasControlledDisplay = false;
    if (next && !isCssString) {
      if (prev) {
        if (!isString(prev)) {
          for (const key in prev) {
            if (next[key] == null) {
              setStyle(style2, key, "");
            }
          }
        } else {
          for (const prevStyle of prev.split(";")) {
            const key = prevStyle.slice(0, prevStyle.indexOf(":")).trim();
            if (next[key] == null) {
              setStyle(style2, key, "");
            }
          }
        }
      }
      for (const key in next) {
        if (key === "display") {
          hasControlledDisplay = true;
        }
        setStyle(style2, key, next[key]);
      }
    } else {
      if (isCssString) {
        if (prev !== next) {
          const cssVarText = style2[CSS_VAR_TEXT];
          if (cssVarText) {
            next += ";" + cssVarText;
          }
          style2.cssText = next;
          hasControlledDisplay = displayRE.test(next);
        }
      } else if (prev) {
        el.removeAttribute("style");
      }
    }
    if (vShowOriginalDisplay in el) {
      el[vShowOriginalDisplay] = hasControlledDisplay ? style2.display : "";
      if (el[vShowHidden]) {
        style2.display = "none";
      }
    }
  }
  const importantRE = /\s*!important$/;
  function setStyle(style2, name, val) {
    if (isArray$1(val)) {
      val.forEach((v) => setStyle(style2, name, v));
    } else {
      if (val == null) val = "";
      if (name.startsWith("--")) {
        style2.setProperty(name, val);
      } else {
        const prefixed = autoPrefix(style2, name);
        if (importantRE.test(val)) {
          style2.setProperty(
            hyphenate(prefixed),
            val.replace(importantRE, ""),
            "important"
          );
        } else {
          style2[prefixed] = val;
        }
      }
    }
  }
  const prefixes = ["Webkit", "Moz", "ms"];
  const prefixCache = {};
  function autoPrefix(style2, rawName) {
    const cached = prefixCache[rawName];
    if (cached) {
      return cached;
    }
    let name = camelize(rawName);
    if (name !== "filter" && name in style2) {
      return prefixCache[rawName] = name;
    }
    name = capitalize(name);
    for (let i = 0; i < prefixes.length; i++) {
      const prefixed = prefixes[i] + name;
      if (prefixed in style2) {
        return prefixCache[rawName] = prefixed;
      }
    }
    return rawName;
  }
  const xlinkNS = "http://www.w3.org/1999/xlink";
  function patchAttr(el, key, value, isSVG2, instance, isBoolean = isSpecialBooleanAttr(key)) {
    if (isSVG2 && key.startsWith("xlink:")) {
      if (value == null) {
        el.removeAttributeNS(xlinkNS, key.slice(6, key.length));
      } else {
        el.setAttributeNS(xlinkNS, key, value);
      }
    } else {
      if (value == null || isBoolean && !includeBooleanAttr(value)) {
        el.removeAttribute(key);
      } else {
        el.setAttribute(
          key,
          isBoolean ? "" : isSymbol$1(value) ? String(value) : value
        );
      }
    }
  }
  function patchDOMProp(el, key, value, parentComponent, attrName) {
    if (key === "innerHTML" || key === "textContent") {
      if (value != null) {
        el[key] = key === "innerHTML" ? unsafeToTrustedHTML(value) : value;
      }
      return;
    }
    const tag = el.tagName;
    if (key === "value" && tag !== "PROGRESS" && // custom elements may use _value internally
    !tag.includes("-")) {
      const oldValue = tag === "OPTION" ? el.getAttribute("value") || "" : el.value;
      const newValue = value == null ? (
        // #11647: value should be set as empty string for null and undefined,
        // but <input type="checkbox"> should be set as 'on'.
        el.type === "checkbox" ? "on" : ""
      ) : String(value);
      if (oldValue !== newValue || !("_value" in el)) {
        el.value = newValue;
      }
      if (value == null) {
        el.removeAttribute(key);
      }
      el._value = value;
      return;
    }
    let needRemove = false;
    if (value === "" || value == null) {
      const type = typeof el[key];
      if (type === "boolean") {
        value = includeBooleanAttr(value);
      } else if (value == null && type === "string") {
        value = "";
        needRemove = true;
      } else if (type === "number") {
        value = 0;
        needRemove = true;
      }
    }
    try {
      el[key] = value;
    } catch (e) {
    }
    needRemove && el.removeAttribute(attrName || key);
  }
  function addEventListener(el, event, handler, options) {
    el.addEventListener(event, handler, options);
  }
  function removeEventListener(el, event, handler, options) {
    el.removeEventListener(event, handler, options);
  }
  const veiKey = /* @__PURE__ */ Symbol("_vei");
  function patchEvent(el, rawName, prevValue, nextValue, instance = null) {
    const invokers = el[veiKey] || (el[veiKey] = {});
    const existingInvoker = invokers[rawName];
    if (nextValue && existingInvoker) {
      existingInvoker.value = nextValue;
    } else {
      const [name, options] = parseName(rawName);
      if (nextValue) {
        const invoker = invokers[rawName] = createInvoker(
          nextValue,
          instance
        );
        addEventListener(el, name, invoker, options);
      } else if (existingInvoker) {
        removeEventListener(el, name, existingInvoker, options);
        invokers[rawName] = void 0;
      }
    }
  }
  const optionsModifierRE = /(?:Once|Passive|Capture)$/;
  function parseName(name) {
    let options;
    if (optionsModifierRE.test(name)) {
      options = {};
      let m;
      while (m = name.match(optionsModifierRE)) {
        name = name.slice(0, name.length - m[0].length);
        options[m[0].toLowerCase()] = true;
      }
    }
    const event = name[2] === ":" ? name.slice(3) : hyphenate(name.slice(2));
    return [event, options];
  }
  let cachedNow = 0;
  const p = /* @__PURE__ */ Promise.resolve();
  const getNow = () => cachedNow || (p.then(() => cachedNow = 0), cachedNow = Date.now());
  function createInvoker(initialValue, instance) {
    const invoker = (e) => {
      if (!e._vts) {
        e._vts = Date.now();
      } else if (e._vts <= invoker.attached) {
        return;
      }
      callWithAsyncErrorHandling(
        patchStopImmediatePropagation(e, invoker.value),
        instance,
        5,
        [e]
      );
    };
    invoker.value = initialValue;
    invoker.attached = getNow();
    return invoker;
  }
  function patchStopImmediatePropagation(e, value) {
    if (isArray$1(value)) {
      const originalStop = e.stopImmediatePropagation;
      e.stopImmediatePropagation = () => {
        originalStop.call(e);
        e._stopped = true;
      };
      return value.map(
        (fn) => (e2) => !e2._stopped && fn && fn(e2)
      );
    } else {
      return value;
    }
  }
  const isNativeOn = (key) => key.charCodeAt(0) === 111 && key.charCodeAt(1) === 110 && // lowercase letter
  key.charCodeAt(2) > 96 && key.charCodeAt(2) < 123;
  const patchProp = (el, key, prevValue, nextValue, namespace2, parentComponent) => {
    const isSVG2 = namespace2 === "svg";
    if (key === "class") {
      patchClass(el, nextValue, isSVG2);
    } else if (key === "style") {
      patchStyle(el, prevValue, nextValue);
    } else if (isOn(key)) {
      if (!isModelListener(key)) {
        patchEvent(el, key, prevValue, nextValue, parentComponent);
      }
    } else if (key[0] === "." ? (key = key.slice(1), true) : key[0] === "^" ? (key = key.slice(1), false) : shouldSetAsProp(el, key, nextValue, isSVG2)) {
      patchDOMProp(el, key, nextValue);
      if (!el.tagName.includes("-") && (key === "value" || key === "checked" || key === "selected")) {
        patchAttr(el, key, nextValue, isSVG2, parentComponent, key !== "value");
      }
    } else if (
      // #11081 force set props for possible async custom element
      el._isVueCE && // #12408 check if it's declared prop or it's async custom element
      (shouldSetAsPropForVueCE(el, key) || // @ts-expect-error _def is private
      el._def.__asyncLoader && (/[A-Z]/.test(key) || !isString(nextValue)))
    ) {
      patchDOMProp(el, camelize(key), nextValue, parentComponent, key);
    } else {
      if (key === "true-value") {
        el._trueValue = nextValue;
      } else if (key === "false-value") {
        el._falseValue = nextValue;
      }
      patchAttr(el, key, nextValue, isSVG2);
    }
  };
  function shouldSetAsProp(el, key, value, isSVG2) {
    if (isSVG2) {
      if (key === "innerHTML" || key === "textContent") {
        return true;
      }
      if (key in el && isNativeOn(key) && isFunction$1(value)) {
        return true;
      }
      return false;
    }
    if (key === "spellcheck" || key === "draggable" || key === "translate" || key === "autocorrect") {
      return false;
    }
    if (key === "sandbox" && el.tagName === "IFRAME") {
      return false;
    }
    if (key === "form") {
      return false;
    }
    if (key === "list" && el.tagName === "INPUT") {
      return false;
    }
    if (key === "type" && el.tagName === "TEXTAREA") {
      return false;
    }
    if (key === "width" || key === "height") {
      const tag = el.tagName;
      if (tag === "IMG" || tag === "VIDEO" || tag === "CANVAS" || tag === "SOURCE") {
        return false;
      }
    }
    if (isNativeOn(key) && isString(value)) {
      return false;
    }
    return key in el;
  }
  function shouldSetAsPropForVueCE(el, key) {
    const props = (
      // @ts-expect-error _def is private
      el._def.props
    );
    if (!props) {
      return false;
    }
    const camelKey = camelize(key);
    return Array.isArray(props) ? props.some((prop) => camelize(prop) === camelKey) : Object.keys(props).some((prop) => camelize(prop) === camelKey);
  }
  const positionMap = /* @__PURE__ */ new WeakMap();
  const newPositionMap = /* @__PURE__ */ new WeakMap();
  const moveCbKey = /* @__PURE__ */ Symbol("_moveCb");
  const enterCbKey = /* @__PURE__ */ Symbol("_enterCb");
  const decorate = (t) => {
    delete t.props.mode;
    return t;
  };
  const TransitionGroupImpl = /* @__PURE__ */ decorate({
    name: "TransitionGroup",
    props: /* @__PURE__ */ extend({}, TransitionPropsValidators, {
      tag: String,
      moveClass: String
    }),
    setup(props, { slots }) {
      const instance = getCurrentInstance();
      const state = useTransitionState();
      let prevChildren;
      let children;
      onUpdated(() => {
        if (!prevChildren.length) {
          return;
        }
        const moveClass = props.moveClass || `${props.name || "v"}-move`;
        if (!hasCSSTransform(
          prevChildren[0].el,
          instance.vnode.el,
          moveClass
        )) {
          prevChildren = [];
          return;
        }
        prevChildren.forEach(callPendingCbs);
        prevChildren.forEach(recordPosition);
        const movedChildren = prevChildren.filter(applyTranslation);
        forceReflow(instance.vnode.el);
        movedChildren.forEach((c2) => {
          const el = c2.el;
          const style2 = el.style;
          addTransitionClass(el, moveClass);
          style2.transform = style2.webkitTransform = style2.transitionDuration = "";
          const cb = el[moveCbKey] = (e) => {
            if (e && e.target !== el) {
              return;
            }
            if (!e || e.propertyName.endsWith("transform")) {
              el.removeEventListener("transitionend", cb);
              el[moveCbKey] = null;
              removeTransitionClass(el, moveClass);
            }
          };
          el.addEventListener("transitionend", cb);
        });
        prevChildren = [];
      });
      return () => {
        const rawProps = /* @__PURE__ */ toRaw(props);
        const cssTransitionProps = resolveTransitionProps(rawProps);
        let tag = rawProps.tag || Fragment;
        prevChildren = [];
        if (children) {
          for (let i = 0; i < children.length; i++) {
            const child = children[i];
            if (child.el && child.el instanceof Element) {
              prevChildren.push(child);
              setTransitionHooks(
                child,
                resolveTransitionHooks(
                  child,
                  cssTransitionProps,
                  state,
                  instance
                )
              );
              positionMap.set(child, getPosition(child.el));
            }
          }
        }
        children = slots.default ? getTransitionRawChildren(slots.default()) : [];
        for (let i = 0; i < children.length; i++) {
          const child = children[i];
          if (child.key != null) {
            setTransitionHooks(
              child,
              resolveTransitionHooks(child, cssTransitionProps, state, instance)
            );
          }
        }
        return createVNode(tag, null, children);
      };
    }
  });
  const TransitionGroup = TransitionGroupImpl;
  function callPendingCbs(c2) {
    const el = c2.el;
    if (el[moveCbKey]) {
      el[moveCbKey]();
    }
    if (el[enterCbKey]) {
      el[enterCbKey]();
    }
  }
  function recordPosition(c2) {
    newPositionMap.set(c2, getPosition(c2.el));
  }
  function applyTranslation(c2) {
    const oldPos = positionMap.get(c2);
    const newPos = newPositionMap.get(c2);
    const dx = oldPos.left - newPos.left;
    const dy = oldPos.top - newPos.top;
    if (dx || dy) {
      const el = c2.el;
      const s = el.style;
      const rect = el.getBoundingClientRect();
      let scaleX = 1;
      let scaleY = 1;
      if (el.offsetWidth) scaleX = rect.width / el.offsetWidth;
      if (el.offsetHeight) scaleY = rect.height / el.offsetHeight;
      if (!Number.isFinite(scaleX) || scaleX === 0) scaleX = 1;
      if (!Number.isFinite(scaleY) || scaleY === 0) scaleY = 1;
      if (Math.abs(scaleX - 1) < 0.01) scaleX = 1;
      if (Math.abs(scaleY - 1) < 0.01) scaleY = 1;
      s.transform = s.webkitTransform = `translate(${dx / scaleX}px,${dy / scaleY}px)`;
      s.transitionDuration = "0s";
      return c2;
    }
  }
  function getPosition(el) {
    const rect = el.getBoundingClientRect();
    return {
      left: rect.left,
      top: rect.top
    };
  }
  function hasCSSTransform(el, root2, moveClass) {
    const clone = el.cloneNode();
    const _vtc = el[vtcKey];
    if (_vtc) {
      _vtc.forEach((cls) => {
        cls.split(/\s+/).forEach((c2) => c2 && clone.classList.remove(c2));
      });
    }
    moveClass.split(/\s+/).forEach((c2) => c2 && clone.classList.add(c2));
    clone.style.display = "none";
    const container = root2.nodeType === 1 ? root2 : root2.parentNode;
    container.appendChild(clone);
    const { hasTransform } = getTransitionInfo(clone);
    container.removeChild(clone);
    return hasTransform;
  }
  const getModelAssigner = (vnode) => {
    const fn = vnode.props["onUpdate:modelValue"] || false;
    return isArray$1(fn) ? (value) => invokeArrayFns(fn, value) : fn;
  };
  function onCompositionStart(e) {
    e.target.composing = true;
  }
  function onCompositionEnd(e) {
    const target = e.target;
    if (target.composing) {
      target.composing = false;
      target.dispatchEvent(new Event("input"));
    }
  }
  const assignKey = /* @__PURE__ */ Symbol("_assign");
  function castValue(value, trim, number) {
    if (trim) value = value.trim();
    if (number) value = looseToNumber(value);
    return value;
  }
  const vModelText = {
    created(el, { modifiers: { lazy, trim, number } }, vnode) {
      el[assignKey] = getModelAssigner(vnode);
      const castToNumber = number || vnode.props && vnode.props.type === "number";
      addEventListener(el, lazy ? "change" : "input", (e) => {
        if (e.target.composing) return;
        el[assignKey](castValue(el.value, trim, castToNumber));
      });
      if (trim || castToNumber) {
        addEventListener(el, "change", () => {
          el.value = castValue(el.value, trim, castToNumber);
        });
      }
      if (!lazy) {
        addEventListener(el, "compositionstart", onCompositionStart);
        addEventListener(el, "compositionend", onCompositionEnd);
        addEventListener(el, "change", onCompositionEnd);
      }
    },
    // set value on mounted so it's after min/max for type="range"
    mounted(el, { value }) {
      el.value = value == null ? "" : value;
    },
    beforeUpdate(el, { value, oldValue, modifiers: { lazy, trim, number } }, vnode) {
      el[assignKey] = getModelAssigner(vnode);
      if (el.composing) return;
      const elValue = (number || el.type === "number") && !/^0\d/.test(el.value) ? looseToNumber(el.value) : el.value;
      const newValue = value == null ? "" : value;
      if (elValue === newValue) {
        return;
      }
      if (document.activeElement === el && el.type !== "range") {
        if (lazy && value === oldValue) {
          return;
        }
        if (trim && el.value.trim() === newValue) {
          return;
        }
      }
      el.value = newValue;
    }
  };
  const vModelCheckbox = {
    // #4096 array checkboxes need to be deep traversed
    deep: true,
    created(el, _, vnode) {
      el[assignKey] = getModelAssigner(vnode);
      addEventListener(el, "change", () => {
        const modelValue = el._modelValue;
        const elementValue = getValue$1(el);
        const checked = el.checked;
        const assign = el[assignKey];
        if (isArray$1(modelValue)) {
          const index = looseIndexOf(modelValue, elementValue);
          const found = index !== -1;
          if (checked && !found) {
            assign(modelValue.concat(elementValue));
          } else if (!checked && found) {
            const filtered = [...modelValue];
            filtered.splice(index, 1);
            assign(filtered);
          }
        } else if (isSet(modelValue)) {
          const cloned = new Set(modelValue);
          if (checked) {
            cloned.add(elementValue);
          } else {
            cloned.delete(elementValue);
          }
          assign(cloned);
        } else {
          assign(getCheckboxValue(el, checked));
        }
      });
    },
    // set initial checked on mount to wait for true-value/false-value
    mounted: setChecked,
    beforeUpdate(el, binding, vnode) {
      el[assignKey] = getModelAssigner(vnode);
      setChecked(el, binding, vnode);
    }
  };
  function setChecked(el, { value, oldValue }, vnode) {
    el._modelValue = value;
    let checked;
    if (isArray$1(value)) {
      checked = looseIndexOf(value, vnode.props.value) > -1;
    } else if (isSet(value)) {
      checked = value.has(vnode.props.value);
    } else {
      if (value === oldValue) return;
      checked = looseEqual(value, getCheckboxValue(el, true));
    }
    if (el.checked !== checked) {
      el.checked = checked;
    }
  }
  const vModelSelect = {
    // <select multiple> value need to be deep traversed
    deep: true,
    created(el, { value, modifiers: { number } }, vnode) {
      const isSetModel = isSet(value);
      addEventListener(el, "change", () => {
        const selectedVal = Array.prototype.filter.call(el.options, (o) => o.selected).map(
          (o) => number ? looseToNumber(getValue$1(o)) : getValue$1(o)
        );
        el[assignKey](
          el.multiple ? isSetModel ? new Set(selectedVal) : selectedVal : selectedVal[0]
        );
        el._assigning = true;
        nextTick(() => {
          el._assigning = false;
        });
      });
      el[assignKey] = getModelAssigner(vnode);
    },
    // set value in mounted & updated because <select> relies on its children
    // <option>s.
    mounted(el, { value }) {
      setSelected(el, value);
    },
    beforeUpdate(el, _binding, vnode) {
      el[assignKey] = getModelAssigner(vnode);
    },
    updated(el, { value }) {
      if (!el._assigning) {
        setSelected(el, value);
      }
    }
  };
  function setSelected(el, value) {
    const isMultiple = el.multiple;
    const isArrayValue = isArray$1(value);
    if (isMultiple && !isArrayValue && !isSet(value)) {
      return;
    }
    for (let i = 0, l = el.options.length; i < l; i++) {
      const option = el.options[i];
      const optionValue = getValue$1(option);
      if (isMultiple) {
        if (isArrayValue) {
          const optionType = typeof optionValue;
          if (optionType === "string" || optionType === "number") {
            option.selected = value.some((v) => String(v) === String(optionValue));
          } else {
            option.selected = looseIndexOf(value, optionValue) > -1;
          }
        } else {
          option.selected = value.has(optionValue);
        }
      } else if (looseEqual(getValue$1(option), value)) {
        if (el.selectedIndex !== i) el.selectedIndex = i;
        return;
      }
    }
    if (!isMultiple && el.selectedIndex !== -1) {
      el.selectedIndex = -1;
    }
  }
  function getValue$1(el) {
    return "_value" in el ? el._value : el.value;
  }
  function getCheckboxValue(el, checked) {
    const key = checked ? "_trueValue" : "_falseValue";
    return key in el ? el[key] : checked;
  }
  const systemModifiers = ["ctrl", "shift", "alt", "meta"];
  const modifierGuards = {
    stop: (e) => e.stopPropagation(),
    prevent: (e) => e.preventDefault(),
    self: (e) => e.target !== e.currentTarget,
    ctrl: (e) => !e.ctrlKey,
    shift: (e) => !e.shiftKey,
    alt: (e) => !e.altKey,
    meta: (e) => !e.metaKey,
    left: (e) => "button" in e && e.button !== 0,
    middle: (e) => "button" in e && e.button !== 1,
    right: (e) => "button" in e && e.button !== 2,
    exact: (e, modifiers) => systemModifiers.some((m) => e[`${m}Key`] && !modifiers.includes(m))
  };
  const withModifiers = (fn, modifiers) => {
    if (!fn) return fn;
    const cache2 = fn._withMods || (fn._withMods = {});
    const cacheKey = modifiers.join(".");
    return cache2[cacheKey] || (cache2[cacheKey] = (event, ...args) => {
      for (let i = 0; i < modifiers.length; i++) {
        const guard = modifierGuards[modifiers[i]];
        if (guard && guard(event, modifiers)) return;
      }
      return fn(event, ...args);
    });
  };
  const rendererOptions = /* @__PURE__ */ extend({ patchProp }, nodeOps);
  let renderer;
  function ensureRenderer() {
    return renderer || (renderer = createRenderer(rendererOptions));
  }
  const createApp = (...args) => {
    const app = ensureRenderer().createApp(...args);
    const { mount: mount2 } = app;
    app.mount = (containerOrSelector) => {
      const container = normalizeContainer(containerOrSelector);
      if (!container) return;
      const component = app._component;
      if (!isFunction$1(component) && !component.render && !component.template) {
        component.template = container.innerHTML;
      }
      if (container.nodeType === 1) {
        container.textContent = "";
      }
      const proxy = mount2(container, false, resolveRootNamespace(container));
      if (container instanceof Element) {
        container.removeAttribute("v-cloak");
        container.setAttribute("data-v-app", "");
      }
      return proxy;
    };
    return app;
  };
  function resolveRootNamespace(container) {
    if (container instanceof SVGElement) {
      return "svg";
    }
    if (typeof MathMLElement === "function" && container instanceof MathMLElement) {
      return "mathml";
    }
  }
  function normalizeContainer(container) {
    if (isString(container)) {
      const res = document.querySelector(container);
      return res;
    }
    return container;
  }
  const _export_sfc = (sfc, props) => {
    const target = sfc.__vccOpts || sfc;
    for (const [key, val] of props) {
      target[key] = val;
    }
    return target;
  };
  const _hoisted_1$4 = { class: "demo-component" };
  const _sfc_main$4 = {
    __name: "DemoComponent",
    props: {
      title: {
        type: String,
        default: "Demo Component"
      },
      message: {
        type: String,
        default: "This is a reusable component from the demo plugin."
      },
      buttonText: {
        type: String,
        default: "Click Me"
      }
    },
    emits: ["click"],
    setup(__props, { emit: __emit }) {
      const emit2 = __emit;
      const handleClick = () => {
        emit2("click", { timestamp: Date.now() });
      };
      return (_ctx, _cache) => {
        return openBlock(), createElementBlock("div", _hoisted_1$4, [
          createBaseVNode("h3", null, toDisplayString(__props.title), 1),
          createBaseVNode("p", null, toDisplayString(__props.message), 1),
          createBaseVNode("button", {
            class: "btn btn-primary",
            onClick: handleClick
          }, toDisplayString(__props.buttonText), 1)
        ]);
      };
    }
  };
  const DemoComponent = /* @__PURE__ */ _export_sfc(_sfc_main$4, [["__scopeId", "data-v-41d65d10"]]);
  const __vite_glob_0_0 = /* @__PURE__ */ Object.freeze(/* @__PURE__ */ Object.defineProperty({
    __proto__: null,
    default: DemoComponent
  }, Symbol.toStringTag, { value: "Module" }));
  function plugin$2(options) {
    let _bPrefix = ".";
    let _ePrefix = "__";
    let _mPrefix = "--";
    let c2;
    if (options) {
      let t = options.blockPrefix;
      if (t) {
        _bPrefix = t;
      }
      t = options.elementPrefix;
      if (t) {
        _ePrefix = t;
      }
      t = options.modifierPrefix;
      if (t) {
        _mPrefix = t;
      }
    }
    const _plugin = {
      install(instance) {
        c2 = instance.c;
        const ctx2 = instance.context;
        ctx2.bem = {};
        ctx2.bem.b = null;
        ctx2.bem.els = null;
      }
    };
    function b(arg) {
      let memorizedB;
      let memorizedE;
      return {
        before(ctx2) {
          memorizedB = ctx2.bem.b;
          memorizedE = ctx2.bem.els;
          ctx2.bem.els = null;
        },
        after(ctx2) {
          ctx2.bem.b = memorizedB;
          ctx2.bem.els = memorizedE;
        },
        $({ context, props }) {
          arg = typeof arg === "string" ? arg : arg({ context, props });
          context.bem.b = arg;
          return `${(props === null || props === void 0 ? void 0 : props.bPrefix) || _bPrefix}${context.bem.b}`;
        }
      };
    }
    function e(arg) {
      let memorizedE;
      return {
        before(ctx2) {
          memorizedE = ctx2.bem.els;
        },
        after(ctx2) {
          ctx2.bem.els = memorizedE;
        },
        $({ context, props }) {
          arg = typeof arg === "string" ? arg : arg({ context, props });
          context.bem.els = arg.split(",").map((v) => v.trim());
          return context.bem.els.map((el) => `${(props === null || props === void 0 ? void 0 : props.bPrefix) || _bPrefix}${context.bem.b}${_ePrefix}${el}`).join(", ");
        }
      };
    }
    function m(arg) {
      return {
        $({ context, props }) {
          arg = typeof arg === "string" ? arg : arg({ context, props });
          const modifiers = arg.split(",").map((v) => v.trim());
          function elementToSelector(el) {
            return modifiers.map((modifier) => `&${(props === null || props === void 0 ? void 0 : props.bPrefix) || _bPrefix}${context.bem.b}${el !== void 0 ? `${_ePrefix}${el}` : ""}${_mPrefix}${modifier}`).join(", ");
          }
          const els = context.bem.els;
          if (els !== null) {
            return elementToSelector(els[0]);
          } else {
            return elementToSelector();
          }
        }
      };
    }
    function notM(arg) {
      return {
        $({ context, props }) {
          arg = typeof arg === "string" ? arg : arg({ context, props });
          const els = context.bem.els;
          return `&:not(${(props === null || props === void 0 ? void 0 : props.bPrefix) || _bPrefix}${context.bem.b}${els !== null && els.length > 0 ? `${_ePrefix}${els[0]}` : ""}${_mPrefix}${arg})`;
        }
      };
    }
    const cB2 = (...args) => c2(b(args[0]), args[1], args[2]);
    const cE2 = (...args) => c2(e(args[0]), args[1], args[2]);
    const cM2 = (...args) => c2(m(args[0]), args[1], args[2]);
    const cNotM2 = (...args) => c2(notM(args[0]), args[1], args[2]);
    Object.assign(_plugin, {
      cB: cB2,
      cE: cE2,
      cM: cM2,
      cNotM: cNotM2
    });
    return _plugin;
  }
  function ampCount(selector) {
    let cnt = 0;
    for (let i = 0; i < selector.length; ++i) {
      if (selector[i] === "&")
        ++cnt;
    }
    return cnt;
  }
  const separatorRegex = /\s*,(?![^(]*\))\s*/g;
  const extraSpaceRegex = /\s+/g;
  function resolveSelectorWithAmp(amp, selector) {
    const nextAmp = [];
    selector.split(separatorRegex).forEach((partialSelector) => {
      let round = ampCount(partialSelector);
      if (!round) {
        amp.forEach((partialAmp) => {
          nextAmp.push(
            // eslint-disable-next-line @typescript-eslint/strict-boolean-expressions
            (partialAmp && partialAmp + " ") + partialSelector
          );
        });
        return;
      } else if (round === 1) {
        amp.forEach((partialAmp) => {
          nextAmp.push(partialSelector.replace("&", partialAmp));
        });
        return;
      }
      let partialNextAmp = [
        partialSelector
      ];
      while (round--) {
        const nextPartialNextAmp = [];
        partialNextAmp.forEach((selectorItr) => {
          amp.forEach((partialAmp) => {
            nextPartialNextAmp.push(selectorItr.replace("&", partialAmp));
          });
        });
        partialNextAmp = nextPartialNextAmp;
      }
      partialNextAmp.forEach((part) => nextAmp.push(part));
    });
    return nextAmp;
  }
  function resolveSelector(amp, selector) {
    const nextAmp = [];
    selector.split(separatorRegex).forEach((partialSelector) => {
      amp.forEach((partialAmp) => {
        nextAmp.push((partialAmp && partialAmp + " ") + partialSelector);
      });
    });
    return nextAmp;
  }
  function parseSelectorPath(selectorPaths) {
    let amp = [""];
    selectorPaths.forEach((selector) => {
      selector = selector && selector.trim();
      if (
        // eslint-disable-next-line @typescript-eslint/strict-boolean-expressions
        !selector
      ) {
        return;
      }
      if (selector.includes("&")) {
        amp = resolveSelectorWithAmp(amp, selector);
      } else {
        amp = resolveSelector(amp, selector);
      }
    });
    return amp.join(", ").replace(extraSpaceRegex, " ");
  }
  function removeElement(el) {
    if (!el)
      return;
    const parentElement = el.parentElement;
    if (parentElement)
      parentElement.removeChild(el);
  }
  function queryElement(id, parent) {
    return (parent !== null && parent !== void 0 ? parent : document.head).querySelector(`style[cssr-id="${id}"]`);
  }
  function createElement(id) {
    const el = document.createElement("style");
    el.setAttribute("cssr-id", id);
    return el;
  }
  function isMediaOrSupports(selector) {
    if (!selector)
      return false;
    return /^\s*@(s|m)/.test(selector);
  }
  const kebabRegex = /[A-Z]/g;
  function kebabCase(pattern) {
    return pattern.replace(kebabRegex, (match2) => "-" + match2.toLowerCase());
  }
  function unwrapProperty(prop, indent = "  ") {
    if (typeof prop === "object" && prop !== null) {
      return " {\n" + Object.entries(prop).map((v) => {
        return indent + `  ${kebabCase(v[0])}: ${v[1]};`;
      }).join("\n") + "\n" + indent + "}";
    }
    return `: ${prop};`;
  }
  function unwrapProperties(props, instance, params) {
    if (typeof props === "function") {
      return props({
        context: instance.context,
        props: params
      });
    }
    return props;
  }
  function createStyle(selector, props, instance, params) {
    if (!props)
      return "";
    const unwrappedProps = unwrapProperties(props, instance, params);
    if (!unwrappedProps)
      return "";
    if (typeof unwrappedProps === "string") {
      return `${selector} {
${unwrappedProps}
}`;
    }
    const propertyNames = Object.keys(unwrappedProps);
    if (propertyNames.length === 0) {
      if (instance.config.keepEmptyBlock)
        return selector + " {\n}";
      return "";
    }
    const statements = selector ? [
      selector + " {"
    ] : [];
    propertyNames.forEach((propertyName) => {
      const property2 = unwrappedProps[propertyName];
      if (propertyName === "raw") {
        statements.push("\n" + property2 + "\n");
        return;
      }
      propertyName = kebabCase(propertyName);
      if (property2 !== null && property2 !== void 0) {
        statements.push(`  ${propertyName}${unwrapProperty(property2)}`);
      }
    });
    if (selector) {
      statements.push("}");
    }
    return statements.join("\n");
  }
  function loopCNodeListWithCallback(children, options, callback) {
    if (!children)
      return;
    children.forEach((child) => {
      if (Array.isArray(child)) {
        loopCNodeListWithCallback(child, options, callback);
      } else if (typeof child === "function") {
        const grandChildren = child(options);
        if (Array.isArray(grandChildren)) {
          loopCNodeListWithCallback(grandChildren, options, callback);
        } else if (grandChildren) {
          callback(grandChildren);
        }
      } else if (child) {
        callback(child);
      }
    });
  }
  function traverseCNode(node, selectorPaths, styles, instance, params) {
    const $ = node.$;
    let blockSelector = "";
    if (!$ || typeof $ === "string") {
      if (isMediaOrSupports($)) {
        blockSelector = $;
      } else {
        selectorPaths.push($);
      }
    } else if (typeof $ === "function") {
      const selector2 = $({
        context: instance.context,
        props: params
      });
      if (isMediaOrSupports(selector2)) {
        blockSelector = selector2;
      } else {
        selectorPaths.push(selector2);
      }
    } else {
      if ($.before)
        $.before(instance.context);
      if (!$.$ || typeof $.$ === "string") {
        if (isMediaOrSupports($.$)) {
          blockSelector = $.$;
        } else {
          selectorPaths.push($.$);
        }
      } else if ($.$) {
        const selector2 = $.$({
          context: instance.context,
          props: params
        });
        if (isMediaOrSupports(selector2)) {
          blockSelector = selector2;
        } else {
          selectorPaths.push(selector2);
        }
      }
    }
    const selector = parseSelectorPath(selectorPaths);
    const style2 = createStyle(selector, node.props, instance, params);
    if (blockSelector) {
      styles.push(`${blockSelector} {`);
    } else if (style2.length) {
      styles.push(style2);
    }
    if (node.children) {
      loopCNodeListWithCallback(node.children, {
        context: instance.context,
        props: params
      }, (childNode) => {
        if (typeof childNode === "string") {
          const style3 = createStyle(selector, { raw: childNode }, instance, params);
          styles.push(style3);
        } else {
          traverseCNode(childNode, selectorPaths, styles, instance, params);
        }
      });
    }
    selectorPaths.pop();
    if (blockSelector) {
      styles.push("}");
    }
    if ($ && $.after)
      $.after(instance.context);
  }
  function render$1(node, instance, props) {
    const styles = [];
    traverseCNode(node, [], styles, instance, props);
    return styles.join("\n\n");
  }
  function murmur2(str) {
    var h2 = 0;
    var k, i = 0, len = str.length;
    for (; len >= 4; ++i, len -= 4) {
      k = str.charCodeAt(i) & 255 | (str.charCodeAt(++i) & 255) << 8 | (str.charCodeAt(++i) & 255) << 16 | (str.charCodeAt(++i) & 255) << 24;
      k = /* Math.imul(k, m): */
      (k & 65535) * 1540483477 + ((k >>> 16) * 59797 << 16);
      k ^= /* k >>> r: */
      k >>> 24;
      h2 = /* Math.imul(k, m): */
      (k & 65535) * 1540483477 + ((k >>> 16) * 59797 << 16) ^ /* Math.imul(h, m): */
      (h2 & 65535) * 1540483477 + ((h2 >>> 16) * 59797 << 16);
    }
    switch (len) {
      case 3:
        h2 ^= (str.charCodeAt(i + 2) & 255) << 16;
      case 2:
        h2 ^= (str.charCodeAt(i + 1) & 255) << 8;
      case 1:
        h2 ^= str.charCodeAt(i) & 255;
        h2 = /* Math.imul(h, m): */
        (h2 & 65535) * 1540483477 + ((h2 >>> 16) * 59797 << 16);
    }
    h2 ^= h2 >>> 13;
    h2 = /* Math.imul(h, m): */
    (h2 & 65535) * 1540483477 + ((h2 >>> 16) * 59797 << 16);
    return ((h2 ^ h2 >>> 15) >>> 0).toString(36);
  }
  if (typeof window !== "undefined") {
    window.__cssrContext = {};
  }
  function unmount(instance, node, id, parent) {
    const { els } = node;
    if (id === void 0) {
      els.forEach(removeElement);
      node.els = [];
    } else {
      const target = queryElement(id, parent);
      if (target && els.includes(target)) {
        removeElement(target);
        node.els = els.filter((el) => el !== target);
      }
    }
  }
  function addElementToList(els, target) {
    els.push(target);
  }
  function mount(instance, node, id, props, head, force, anchorMetaName, parent, ssrAdapter2) {
    let style2;
    if (id === void 0) {
      style2 = node.render(props);
      id = murmur2(style2);
    }
    if (ssrAdapter2) {
      ssrAdapter2.adapter(id, style2 !== null && style2 !== void 0 ? style2 : node.render(props));
      return;
    }
    if (parent === void 0) {
      parent = document.head;
    }
    const queriedTarget = queryElement(id, parent);
    if (queriedTarget !== null && !force) {
      return queriedTarget;
    }
    const target = queriedTarget !== null && queriedTarget !== void 0 ? queriedTarget : createElement(id);
    if (style2 === void 0)
      style2 = node.render(props);
    target.textContent = style2;
    if (queriedTarget !== null)
      return queriedTarget;
    if (anchorMetaName) {
      const anchorMetaEl = parent.querySelector(`meta[name="${anchorMetaName}"]`);
      if (anchorMetaEl) {
        parent.insertBefore(target, anchorMetaEl);
        addElementToList(node.els, target);
        return target;
      }
    }
    if (head) {
      parent.insertBefore(target, parent.querySelector("style, link"));
    } else {
      parent.appendChild(target);
    }
    addElementToList(node.els, target);
    return target;
  }
  function wrappedRender(props) {
    return render$1(this, this.instance, props);
  }
  function wrappedMount(options = {}) {
    const { id, ssr, props, head = false, force = false, anchorMetaName, parent } = options;
    const targetElement = mount(this.instance, this, id, props, head, force, anchorMetaName, parent, ssr);
    return targetElement;
  }
  function wrappedUnmount(options = {}) {
    const { id, parent } = options;
    unmount(this.instance, this, id, parent);
  }
  const createCNode = function(instance, $, props, children) {
    return {
      instance,
      $,
      props,
      children,
      els: [],
      render: wrappedRender,
      mount: wrappedMount,
      unmount: wrappedUnmount
    };
  };
  const c$2 = function(instance, $, props, children) {
    if (Array.isArray($)) {
      return createCNode(instance, { $: null }, null, $);
    } else if (Array.isArray(props)) {
      return createCNode(instance, $, null, props);
    } else if (Array.isArray(children)) {
      return createCNode(instance, $, props, children);
    } else {
      return createCNode(instance, $, props, null);
    }
  };
  function CssRender(config = {}) {
    const cssr2 = {
      c: (...args) => c$2(cssr2, ...args),
      use: (plugin2, ...args) => plugin2.install(cssr2, ...args),
      find: queryElement,
      context: {},
      config
    };
    return cssr2;
  }
  function exists(id, ssr) {
    if (id === void 0)
      return false;
    if (ssr) {
      const { context: { ids } } = ssr;
      return ids.has(id);
    }
    return queryElement(id) !== null;
  }
  const namespace = "n";
  const prefix$1 = `.${namespace}-`;
  const elementPrefix = "__";
  const modifierPrefix = "--";
  const cssr = CssRender();
  const plugin$1 = plugin$2({
    blockPrefix: prefix$1,
    elementPrefix,
    modifierPrefix
  });
  cssr.use(plugin$1);
  const {
    c: c$1,
    find
  } = cssr;
  const {
    cB,
    cE,
    cM,
    cNotM
  } = plugin$1;
  function insideModal(style2) {
    return c$1(({
      props: {
        bPrefix
      }
    }) => `${bPrefix || prefix$1}modal, ${bPrefix || prefix$1}drawer`, [style2]);
  }
  function insidePopover(style2) {
    return c$1(({
      props: {
        bPrefix
      }
    }) => `${bPrefix || prefix$1}popover`, [style2]);
  }
  function asModal(style2) {
    return c$1(({
      props: {
        bPrefix
      }
    }) => `&${bPrefix || prefix$1}modal`, style2);
  }
  const cCB = (...args) => {
    return c$1(">", [cB(...args)]);
  };
  function createKey(prefix2, suffix2) {
    return prefix2 + (suffix2 === "default" ? "" : suffix2.replace(/^[a-z]/, (startChar) => startChar.toUpperCase()));
  }
  let onceCbs = [];
  const paramsMap = /* @__PURE__ */ new WeakMap();
  function flushOnceCallbacks() {
    onceCbs.forEach((cb) => cb(...paramsMap.get(cb)));
    onceCbs = [];
  }
  function beforeNextFrameOnce(cb, ...params) {
    paramsMap.set(cb, params);
    if (onceCbs.includes(cb))
      return;
    onceCbs.push(cb) === 1 && requestAnimationFrame(flushOnceCallbacks);
  }
  function happensIn(e, dataSetPropName) {
    let { target } = e;
    while (target) {
      if (target.dataset) {
        if (target.dataset[dataSetPropName] !== void 0)
          return true;
      }
      target = target.parentElement;
    }
    return false;
  }
  function getPreciseEventTarget(event) {
    return event.composedPath()[0] || null;
  }
  function parseResponsiveProp(reponsiveProp) {
    if (typeof reponsiveProp === "number") {
      return {
        "": reponsiveProp.toString()
      };
    }
    const params = {};
    reponsiveProp.split(/ +/).forEach((pairLiteral) => {
      if (pairLiteral === "")
        return;
      const [prefix2, value] = pairLiteral.split(":");
      if (value === void 0) {
        params[""] = prefix2;
      } else {
        params[prefix2] = value;
      }
    });
    return params;
  }
  function parseResponsivePropValue(reponsiveProp, activeKeyOrSize) {
    var _a2;
    if (reponsiveProp === void 0 || reponsiveProp === null)
      return void 0;
    const classObj = parseResponsiveProp(reponsiveProp);
    if (activeKeyOrSize === void 0)
      return classObj[""];
    if (typeof activeKeyOrSize === "string") {
      return (_a2 = classObj[activeKeyOrSize]) !== null && _a2 !== void 0 ? _a2 : classObj[""];
    } else if (Array.isArray(activeKeyOrSize)) {
      for (let i = activeKeyOrSize.length - 1; i >= 0; --i) {
        const key = activeKeyOrSize[i];
        if (key in classObj)
          return classObj[key];
      }
      return classObj[""];
    } else {
      let activeValue = void 0;
      let activeKey = -1;
      Object.keys(classObj).forEach((key) => {
        const keyAsNum = Number(key);
        if (!Number.isNaN(keyAsNum) && activeKeyOrSize >= keyAsNum && keyAsNum >= activeKey) {
          activeKey = keyAsNum;
          activeValue = classObj[key];
        }
      });
      return activeValue;
    }
  }
  function depx(value) {
    if (typeof value === "string") {
      if (value.endsWith("px")) {
        return Number(value.slice(0, value.length - 2));
      }
      return Number(value);
    }
    return value;
  }
  function pxfy(value) {
    if (value === void 0 || value === null)
      return void 0;
    if (typeof value === "number")
      return `${value}px`;
    if (value.endsWith("px"))
      return value;
    return `${value}px`;
  }
  function getMargin(value, position) {
    const parts = value.trim().split(/\s+/g);
    const margin = {
      top: parts[0]
    };
    switch (parts.length) {
      case 1:
        margin.right = parts[0];
        margin.bottom = parts[0];
        margin.left = parts[0];
        break;
      case 2:
        margin.right = parts[1];
        margin.left = parts[1];
        margin.bottom = parts[0];
        break;
      case 3:
        margin.right = parts[1];
        margin.bottom = parts[2];
        margin.left = parts[1];
        break;
      case 4:
        margin.right = parts[1];
        margin.bottom = parts[2];
        margin.left = parts[3];
        break;
      default:
        throw new Error("[seemly/getMargin]:" + value + " is not a valid value.");
    }
    return margin;
  }
  function getGap(value, orient) {
    const [rowGap, colGap] = value.split(" ");
    return {
      row: rowGap,
      col: colGap || rowGap
    };
  }
  const colors = {
    aliceblue: "#F0F8FF",
    antiquewhite: "#FAEBD7",
    aqua: "#0FF",
    aquamarine: "#7FFFD4",
    azure: "#F0FFFF",
    beige: "#F5F5DC",
    bisque: "#FFE4C4",
    black: "#000",
    blanchedalmond: "#FFEBCD",
    blue: "#00F",
    blueviolet: "#8A2BE2",
    brown: "#A52A2A",
    burlywood: "#DEB887",
    cadetblue: "#5F9EA0",
    chartreuse: "#7FFF00",
    chocolate: "#D2691E",
    coral: "#FF7F50",
    cornflowerblue: "#6495ED",
    cornsilk: "#FFF8DC",
    crimson: "#DC143C",
    cyan: "#0FF",
    darkblue: "#00008B",
    darkcyan: "#008B8B",
    darkgoldenrod: "#B8860B",
    darkgray: "#A9A9A9",
    darkgrey: "#A9A9A9",
    darkgreen: "#006400",
    darkkhaki: "#BDB76B",
    darkmagenta: "#8B008B",
    darkolivegreen: "#556B2F",
    darkorange: "#FF8C00",
    darkorchid: "#9932CC",
    darkred: "#8B0000",
    darksalmon: "#E9967A",
    darkseagreen: "#8FBC8F",
    darkslateblue: "#483D8B",
    darkslategray: "#2F4F4F",
    darkslategrey: "#2F4F4F",
    darkturquoise: "#00CED1",
    darkviolet: "#9400D3",
    deeppink: "#FF1493",
    deepskyblue: "#00BFFF",
    dimgray: "#696969",
    dimgrey: "#696969",
    dodgerblue: "#1E90FF",
    firebrick: "#B22222",
    floralwhite: "#FFFAF0",
    forestgreen: "#228B22",
    fuchsia: "#F0F",
    gainsboro: "#DCDCDC",
    ghostwhite: "#F8F8FF",
    gold: "#FFD700",
    goldenrod: "#DAA520",
    gray: "#808080",
    grey: "#808080",
    green: "#008000",
    greenyellow: "#ADFF2F",
    honeydew: "#F0FFF0",
    hotpink: "#FF69B4",
    indianred: "#CD5C5C",
    indigo: "#4B0082",
    ivory: "#FFFFF0",
    khaki: "#F0E68C",
    lavender: "#E6E6FA",
    lavenderblush: "#FFF0F5",
    lawngreen: "#7CFC00",
    lemonchiffon: "#FFFACD",
    lightblue: "#ADD8E6",
    lightcoral: "#F08080",
    lightcyan: "#E0FFFF",
    lightgoldenrodyellow: "#FAFAD2",
    lightgray: "#D3D3D3",
    lightgrey: "#D3D3D3",
    lightgreen: "#90EE90",
    lightpink: "#FFB6C1",
    lightsalmon: "#FFA07A",
    lightseagreen: "#20B2AA",
    lightskyblue: "#87CEFA",
    lightslategray: "#778899",
    lightslategrey: "#778899",
    lightsteelblue: "#B0C4DE",
    lightyellow: "#FFFFE0",
    lime: "#0F0",
    limegreen: "#32CD32",
    linen: "#FAF0E6",
    magenta: "#F0F",
    maroon: "#800000",
    mediumaquamarine: "#66CDAA",
    mediumblue: "#0000CD",
    mediumorchid: "#BA55D3",
    mediumpurple: "#9370DB",
    mediumseagreen: "#3CB371",
    mediumslateblue: "#7B68EE",
    mediumspringgreen: "#00FA9A",
    mediumturquoise: "#48D1CC",
    mediumvioletred: "#C71585",
    midnightblue: "#191970",
    mintcream: "#F5FFFA",
    mistyrose: "#FFE4E1",
    moccasin: "#FFE4B5",
    navajowhite: "#FFDEAD",
    navy: "#000080",
    oldlace: "#FDF5E6",
    olive: "#808000",
    olivedrab: "#6B8E23",
    orange: "#FFA500",
    orangered: "#FF4500",
    orchid: "#DA70D6",
    palegoldenrod: "#EEE8AA",
    palegreen: "#98FB98",
    paleturquoise: "#AFEEEE",
    palevioletred: "#DB7093",
    papayawhip: "#FFEFD5",
    peachpuff: "#FFDAB9",
    peru: "#CD853F",
    pink: "#FFC0CB",
    plum: "#DDA0DD",
    powderblue: "#B0E0E6",
    purple: "#800080",
    rebeccapurple: "#663399",
    red: "#F00",
    rosybrown: "#BC8F8F",
    royalblue: "#4169E1",
    saddlebrown: "#8B4513",
    salmon: "#FA8072",
    sandybrown: "#F4A460",
    seagreen: "#2E8B57",
    seashell: "#FFF5EE",
    sienna: "#A0522D",
    silver: "#C0C0C0",
    skyblue: "#87CEEB",
    slateblue: "#6A5ACD",
    slategray: "#708090",
    slategrey: "#708090",
    snow: "#FFFAFA",
    springgreen: "#00FF7F",
    steelblue: "#4682B4",
    tan: "#D2B48C",
    teal: "#008080",
    thistle: "#D8BFD8",
    tomato: "#FF6347",
    turquoise: "#40E0D0",
    violet: "#EE82EE",
    wheat: "#F5DEB3",
    white: "#FFF",
    whitesmoke: "#F5F5F5",
    yellow: "#FF0",
    yellowgreen: "#9ACD32",
    transparent: "#0000"
  };
  function hsv2rgb(h2, s, v) {
    s /= 100;
    v /= 100;
    let f = (n, k = (n + h2 / 60) % 6) => v - v * s * Math.max(Math.min(k, 4 - k, 1), 0);
    return [f(5) * 255, f(3) * 255, f(1) * 255];
  }
  function hsl2rgb(h2, s, l) {
    s /= 100;
    l /= 100;
    let a = s * Math.min(l, 1 - l);
    let f = (n, k = (n + h2 / 30) % 12) => l - a * Math.max(Math.min(k - 3, 9 - k, 1), -1);
    return [f(0) * 255, f(8) * 255, f(4) * 255];
  }
  const prefix = "^\\s*";
  const suffix = "\\s*$";
  const percent = "\\s*((\\.\\d+)|(\\d+(\\.\\d*)?))%\\s*";
  const float = "\\s*((\\.\\d+)|(\\d+(\\.\\d*)?))\\s*";
  const hex = "([0-9A-Fa-f])";
  const dhex = "([0-9A-Fa-f]{2})";
  const hslRegex = new RegExp(`${prefix}hsl\\s*\\(${float},${percent},${percent}\\)${suffix}`);
  const hsvRegex = new RegExp(`${prefix}hsv\\s*\\(${float},${percent},${percent}\\)${suffix}`);
  const hslaRegex = new RegExp(`${prefix}hsla\\s*\\(${float},${percent},${percent},${float}\\)${suffix}`);
  const hsvaRegex = new RegExp(`${prefix}hsva\\s*\\(${float},${percent},${percent},${float}\\)${suffix}`);
  const rgbRegex = new RegExp(`${prefix}rgb\\s*\\(${float},${float},${float}\\)${suffix}`);
  const rgbaRegex = new RegExp(`${prefix}rgba\\s*\\(${float},${float},${float},${float}\\)${suffix}`);
  const sHexRegex = new RegExp(`${prefix}#${hex}${hex}${hex}${suffix}`);
  const hexRegex = new RegExp(`${prefix}#${dhex}${dhex}${dhex}${suffix}`);
  const sHexaRegex = new RegExp(`${prefix}#${hex}${hex}${hex}${hex}${suffix}`);
  const hexaRegex = new RegExp(`${prefix}#${dhex}${dhex}${dhex}${dhex}${suffix}`);
  function parseHex(value) {
    return parseInt(value, 16);
  }
  function hsla(color) {
    try {
      let i;
      if (i = hslaRegex.exec(color)) {
        return [
          roundDeg(i[1]),
          roundPercent(i[5]),
          roundPercent(i[9]),
          roundAlpha(i[13])
        ];
      } else if (i = hslRegex.exec(color)) {
        return [roundDeg(i[1]), roundPercent(i[5]), roundPercent(i[9]), 1];
      }
      throw new Error(`[seemly/hsla]: Invalid color value ${color}.`);
    } catch (e) {
      throw e;
    }
  }
  function hsva(color) {
    try {
      let i;
      if (i = hsvaRegex.exec(color)) {
        return [
          roundDeg(i[1]),
          roundPercent(i[5]),
          roundPercent(i[9]),
          roundAlpha(i[13])
        ];
      } else if (i = hsvRegex.exec(color)) {
        return [roundDeg(i[1]), roundPercent(i[5]), roundPercent(i[9]), 1];
      }
      throw new Error(`[seemly/hsva]: Invalid color value ${color}.`);
    } catch (e) {
      throw e;
    }
  }
  function rgba(color) {
    try {
      let i;
      if (i = hexRegex.exec(color)) {
        return [parseHex(i[1]), parseHex(i[2]), parseHex(i[3]), 1];
      } else if (i = rgbRegex.exec(color)) {
        return [roundChannel(i[1]), roundChannel(i[5]), roundChannel(i[9]), 1];
      } else if (i = rgbaRegex.exec(color)) {
        return [
          roundChannel(i[1]),
          roundChannel(i[5]),
          roundChannel(i[9]),
          roundAlpha(i[13])
        ];
      } else if (i = sHexRegex.exec(color)) {
        return [
          parseHex(i[1] + i[1]),
          parseHex(i[2] + i[2]),
          parseHex(i[3] + i[3]),
          1
        ];
      } else if (i = hexaRegex.exec(color)) {
        return [
          parseHex(i[1]),
          parseHex(i[2]),
          parseHex(i[3]),
          roundAlpha(parseHex(i[4]) / 255)
        ];
      } else if (i = sHexaRegex.exec(color)) {
        return [
          parseHex(i[1] + i[1]),
          parseHex(i[2] + i[2]),
          parseHex(i[3] + i[3]),
          roundAlpha(parseHex(i[4] + i[4]) / 255)
        ];
      } else if (color in colors) {
        return rgba(colors[color]);
      } else if (hslRegex.test(color) || hslaRegex.test(color)) {
        const [h2, s, l, a] = hsla(color);
        return [...hsl2rgb(h2, s, l), a];
      } else if (hsvRegex.test(color) || hsvaRegex.test(color)) {
        const [h2, s, v, a] = hsva(color);
        return [...hsv2rgb(h2, s, v), a];
      }
      throw new Error(`[seemly/rgba]: Invalid color value ${color}.`);
    } catch (e) {
      throw e;
    }
  }
  function normalizeAlpha(alphaValue) {
    return alphaValue > 1 ? 1 : alphaValue < 0 ? 0 : alphaValue;
  }
  function stringifyRgba(r, g, b, a) {
    return `rgba(${roundChannel(r)}, ${roundChannel(g)}, ${roundChannel(b)}, ${normalizeAlpha(a)})`;
  }
  function compositeChannel(v1, a1, v2, a2, a) {
    return roundChannel((v1 * a1 * (1 - a2) + v2 * a2) / a);
  }
  function composite(background, overlay2) {
    if (!Array.isArray(background))
      background = rgba(background);
    if (!Array.isArray(overlay2))
      overlay2 = rgba(overlay2);
    const a1 = background[3];
    const a2 = overlay2[3];
    const alpha = roundAlpha(a1 + a2 - a1 * a2);
    return stringifyRgba(compositeChannel(background[0], a1, overlay2[0], a2, alpha), compositeChannel(background[1], a1, overlay2[1], a2, alpha), compositeChannel(background[2], a1, overlay2[2], a2, alpha), alpha);
  }
  function changeColor(base2, options) {
    const [r, g, b, a = 1] = Array.isArray(base2) ? base2 : rgba(base2);
    if (typeof options.alpha === "number") {
      return stringifyRgba(r, g, b, options.alpha);
    }
    return stringifyRgba(r, g, b, a);
  }
  function scaleColor(base2, options) {
    const [r, g, b, a = 1] = Array.isArray(base2) ? base2 : rgba(base2);
    const { lightness = 1, alpha = 1 } = options;
    return toRgbaString([r * lightness, g * lightness, b * lightness, a * alpha]);
  }
  function roundAlpha(value) {
    const v = Math.round(Number(value) * 100) / 100;
    if (v > 1)
      return 1;
    if (v < 0)
      return 0;
    return v;
  }
  function roundDeg(value) {
    const v = Math.round(Number(value));
    if (v >= 360)
      return 0;
    if (v < 0)
      return 0;
    return v;
  }
  function roundChannel(value) {
    const v = Math.round(Number(value));
    if (v > 255)
      return 255;
    if (v < 0)
      return 0;
    return v;
  }
  function roundPercent(value) {
    const v = Math.round(Number(value));
    if (v > 100)
      return 100;
    if (v < 0)
      return 0;
    return v;
  }
  function toRgbaString(base2) {
    const [r, g, b] = base2;
    if (3 in base2) {
      return `rgba(${roundChannel(r)}, ${roundChannel(g)}, ${roundChannel(b)}, ${roundAlpha(base2[3])})`;
    }
    return `rgba(${roundChannel(r)}, ${roundChannel(g)}, ${roundChannel(b)}, 1)`;
  }
  function createId(length = 8) {
    return Math.random().toString(16).slice(2, 2 + length);
  }
  function repeat(count, v) {
    const ret = [];
    for (let i = 0; i < count; ++i) {
      ret.push(v);
    }
    return ret;
  }
  function getEventTarget(e) {
    const path = e.composedPath();
    return path[0];
  }
  const traps = {
    mousemoveoutside: /* @__PURE__ */ new WeakMap(),
    clickoutside: /* @__PURE__ */ new WeakMap()
  };
  function createTrapHandler(name, el, originalHandler) {
    if (name === "mousemoveoutside") {
      const moveHandler = (e) => {
        if (el.contains(getEventTarget(e)))
          return;
        originalHandler(e);
      };
      return {
        mousemove: moveHandler,
        touchstart: moveHandler
      };
    } else if (name === "clickoutside") {
      let mouseDownOutside = false;
      const downHandler = (e) => {
        mouseDownOutside = !el.contains(getEventTarget(e));
      };
      const upHanlder = (e) => {
        if (!mouseDownOutside)
          return;
        if (el.contains(getEventTarget(e)))
          return;
        originalHandler(e);
      };
      return {
        mousedown: downHandler,
        mouseup: upHanlder,
        touchstart: downHandler,
        touchend: upHanlder
      };
    }
    console.error(
      // eslint-disable-next-line @typescript-eslint/restrict-template-expressions
      `[evtd/create-trap-handler]: name \`${name}\` is invalid. This could be a bug of evtd.`
    );
    return {};
  }
  function ensureTrapHandlers(name, el, handler) {
    const handlers = traps[name];
    let elHandlers = handlers.get(el);
    if (elHandlers === void 0) {
      handlers.set(el, elHandlers = /* @__PURE__ */ new WeakMap());
    }
    let trapHandler = elHandlers.get(handler);
    if (trapHandler === void 0) {
      elHandlers.set(handler, trapHandler = createTrapHandler(name, el, handler));
    }
    return trapHandler;
  }
  function trapOn(name, el, handler, options) {
    if (name === "mousemoveoutside" || name === "clickoutside") {
      const trapHandlers = ensureTrapHandlers(name, el, handler);
      Object.keys(trapHandlers).forEach((key) => {
        on(key, document, trapHandlers[key], options);
      });
      return true;
    }
    return false;
  }
  function trapOff(name, el, handler, options) {
    if (name === "mousemoveoutside" || name === "clickoutside") {
      const trapHandlers = ensureTrapHandlers(name, el, handler);
      Object.keys(trapHandlers).forEach((key) => {
        off(key, document, trapHandlers[key], options);
      });
      return true;
    }
    return false;
  }
  function createDelegate() {
    if (typeof window === "undefined") {
      return {
        on: () => {
        },
        off: () => {
        }
      };
    }
    const propagationStopped = /* @__PURE__ */ new WeakMap();
    const immediatePropagationStopped = /* @__PURE__ */ new WeakMap();
    function trackPropagation() {
      propagationStopped.set(this, true);
    }
    function trackImmediate() {
      propagationStopped.set(this, true);
      immediatePropagationStopped.set(this, true);
    }
    function spy(event, propName, fn) {
      const source = event[propName];
      event[propName] = function() {
        fn.apply(event, arguments);
        return source.apply(event, arguments);
      };
      return event;
    }
    function unspy(event, propName) {
      event[propName] = Event.prototype[propName];
    }
    const currentTargets = /* @__PURE__ */ new WeakMap();
    const currentTargetDescriptor = Object.getOwnPropertyDescriptor(Event.prototype, "currentTarget");
    function getCurrentTarget() {
      var _a2;
      return (_a2 = currentTargets.get(this)) !== null && _a2 !== void 0 ? _a2 : null;
    }
    function defineCurrentTarget(event, getter) {
      if (currentTargetDescriptor === void 0)
        return;
      Object.defineProperty(event, "currentTarget", {
        configurable: true,
        enumerable: true,
        get: getter !== null && getter !== void 0 ? getter : currentTargetDescriptor.get
      });
    }
    const phaseToTypeToElToHandlers = {
      bubble: {},
      capture: {}
    };
    const typeToWindowEventHandlers = {};
    function createUnifiedHandler() {
      const delegeteHandler = function(e) {
        const { type, eventPhase, bubbles } = e;
        const target = getEventTarget(e);
        if (eventPhase === 2)
          return;
        const phase = eventPhase === 1 ? "capture" : "bubble";
        let cursor = target;
        const path = [];
        while (true) {
          if (cursor === null)
            cursor = window;
          path.push(cursor);
          if (cursor === window) {
            break;
          }
          cursor = cursor.parentNode || null;
        }
        const captureElToHandlers = phaseToTypeToElToHandlers.capture[type];
        const bubbleElToHandlers = phaseToTypeToElToHandlers.bubble[type];
        spy(e, "stopPropagation", trackPropagation);
        spy(e, "stopImmediatePropagation", trackImmediate);
        defineCurrentTarget(e, getCurrentTarget);
        if (phase === "capture") {
          if (captureElToHandlers === void 0)
            return;
          for (let i = path.length - 1; i >= 0; --i) {
            if (propagationStopped.has(e))
              break;
            const target2 = path[i];
            const handlers = captureElToHandlers.get(target2);
            if (handlers !== void 0) {
              currentTargets.set(e, target2);
              for (const handler of handlers) {
                if (immediatePropagationStopped.has(e))
                  break;
                handler(e);
              }
            }
            if (i === 0 && !bubbles && bubbleElToHandlers !== void 0) {
              const bubbleHandlers = bubbleElToHandlers.get(target2);
              if (bubbleHandlers !== void 0) {
                for (const handler of bubbleHandlers) {
                  if (immediatePropagationStopped.has(e))
                    break;
                  handler(e);
                }
              }
            }
          }
        } else if (phase === "bubble") {
          if (bubbleElToHandlers === void 0)
            return;
          for (let i = 0; i < path.length; ++i) {
            if (propagationStopped.has(e))
              break;
            const target2 = path[i];
            const handlers = bubbleElToHandlers.get(target2);
            if (handlers !== void 0) {
              currentTargets.set(e, target2);
              for (const handler of handlers) {
                if (immediatePropagationStopped.has(e))
                  break;
                handler(e);
              }
            }
          }
        }
        unspy(e, "stopPropagation");
        unspy(e, "stopImmediatePropagation");
        defineCurrentTarget(e);
      };
      delegeteHandler.displayName = "evtdUnifiedHandler";
      return delegeteHandler;
    }
    function createUnifiedWindowEventHandler() {
      const delegateHandler = function(e) {
        const { type, eventPhase } = e;
        if (eventPhase !== 2)
          return;
        const handlers = typeToWindowEventHandlers[type];
        if (handlers === void 0)
          return;
        handlers.forEach((handler) => handler(e));
      };
      delegateHandler.displayName = "evtdUnifiedWindowEventHandler";
      return delegateHandler;
    }
    const unifiedHandler = createUnifiedHandler();
    const unfiendWindowEventHandler = createUnifiedWindowEventHandler();
    function ensureElToHandlers(phase, type) {
      const phaseHandlers = phaseToTypeToElToHandlers[phase];
      if (phaseHandlers[type] === void 0) {
        phaseHandlers[type] = /* @__PURE__ */ new Map();
        window.addEventListener(type, unifiedHandler, phase === "capture");
      }
      return phaseHandlers[type];
    }
    function ensureWindowEventHandlers(type) {
      const windowEventHandlers = typeToWindowEventHandlers[type];
      if (windowEventHandlers === void 0) {
        typeToWindowEventHandlers[type] = /* @__PURE__ */ new Set();
        window.addEventListener(type, unfiendWindowEventHandler);
      }
      return typeToWindowEventHandlers[type];
    }
    function ensureHandlers(elToHandlers, el) {
      let elHandlers = elToHandlers.get(el);
      if (elHandlers === void 0) {
        elToHandlers.set(el, elHandlers = /* @__PURE__ */ new Set());
      }
      return elHandlers;
    }
    function handlerExist(el, phase, type, handler) {
      const elToHandlers = phaseToTypeToElToHandlers[phase][type];
      if (elToHandlers !== void 0) {
        const handlers = elToHandlers.get(el);
        if (handlers !== void 0) {
          if (handlers.has(handler))
            return true;
        }
      }
      return false;
    }
    function windowEventHandlerExist(type, handler) {
      const handlers = typeToWindowEventHandlers[type];
      if (handlers !== void 0) {
        if (handlers.has(handler)) {
          return true;
        }
      }
      return false;
    }
    function on2(type, el, handler, options) {
      let mergedHandler;
      if (typeof options === "object" && options.once === true) {
        mergedHandler = (e) => {
          off2(type, el, mergedHandler, options);
          handler(e);
        };
      } else {
        mergedHandler = handler;
      }
      const trapped = trapOn(type, el, mergedHandler, options);
      if (trapped)
        return;
      const phase = options === true || typeof options === "object" && options.capture === true ? "capture" : "bubble";
      const elToHandlers = ensureElToHandlers(phase, type);
      const handlers = ensureHandlers(elToHandlers, el);
      if (!handlers.has(mergedHandler))
        handlers.add(mergedHandler);
      if (el === window) {
        const windowEventHandlers = ensureWindowEventHandlers(type);
        if (!windowEventHandlers.has(mergedHandler)) {
          windowEventHandlers.add(mergedHandler);
        }
      }
    }
    function off2(type, el, handler, options) {
      const trapped = trapOff(type, el, handler, options);
      if (trapped)
        return;
      const capture = options === true || typeof options === "object" && options.capture === true;
      const phase = capture ? "capture" : "bubble";
      const elToHandlers = ensureElToHandlers(phase, type);
      const handlers = ensureHandlers(elToHandlers, el);
      if (el === window) {
        const mirrorPhase = capture ? "bubble" : "capture";
        if (!handlerExist(el, mirrorPhase, type, handler) && windowEventHandlerExist(type, handler)) {
          const windowEventHandlers = typeToWindowEventHandlers[type];
          windowEventHandlers.delete(handler);
          if (windowEventHandlers.size === 0) {
            window.removeEventListener(type, unfiendWindowEventHandler);
            typeToWindowEventHandlers[type] = void 0;
          }
        }
      }
      if (handlers.has(handler))
        handlers.delete(handler);
      if (handlers.size === 0) {
        elToHandlers.delete(el);
      }
      if (elToHandlers.size === 0) {
        window.removeEventListener(type, unifiedHandler, phase === "capture");
        phaseToTypeToElToHandlers[phase][type] = void 0;
      }
    }
    return {
      on: on2,
      off: off2
    };
  }
  const { on, off } = createDelegate();
  function useFalseUntilTruthy(originalRef) {
    const currentRef = /* @__PURE__ */ ref(!!originalRef.value);
    if (currentRef.value)
      return /* @__PURE__ */ readonly(currentRef);
    const stop = watch(originalRef, (value) => {
      if (value) {
        currentRef.value = true;
        stop();
      }
    });
    return /* @__PURE__ */ readonly(currentRef);
  }
  function useMemo(getterOrOptions) {
    const computedValueRef = computed(getterOrOptions);
    const valueRef = /* @__PURE__ */ ref(computedValueRef.value);
    watch(computedValueRef, (value) => {
      valueRef.value = value;
    });
    if (typeof getterOrOptions === "function") {
      return valueRef;
    } else {
      return {
        __v_isRef: true,
        get value() {
          return valueRef.value;
        },
        set value(v) {
          getterOrOptions.set(v);
        }
      };
    }
  }
  function hasInstance() {
    return getCurrentInstance() !== null;
  }
  const isBrowser$2 = typeof window !== "undefined";
  let fontsReady;
  let isFontReady;
  const init = () => {
    var _a2, _b;
    fontsReady = isBrowser$2 ? (_b = (_a2 = document) === null || _a2 === void 0 ? void 0 : _a2.fonts) === null || _b === void 0 ? void 0 : _b.ready : void 0;
    isFontReady = false;
    if (fontsReady !== void 0) {
      void fontsReady.then(() => {
        isFontReady = true;
      });
    } else {
      isFontReady = true;
    }
  };
  init();
  function onFontsReady(cb) {
    if (isFontReady)
      return;
    let deactivated = false;
    onMounted(() => {
      if (!isFontReady) {
        fontsReady === null || fontsReady === void 0 ? void 0 : fontsReady.then(() => {
          if (deactivated)
            return;
          cb();
        });
      }
    });
    onBeforeUnmount(() => {
      deactivated = true;
    });
  }
  function useMergedState(controlledStateRef, uncontrolledStateRef) {
    watch(controlledStateRef, (value) => {
      if (value !== void 0) {
        uncontrolledStateRef.value = value;
      }
    });
    return computed(() => {
      if (controlledStateRef.value === void 0) {
        return uncontrolledStateRef.value;
      }
      return controlledStateRef.value;
    });
  }
  function isMounted() {
    const isMounted2 = /* @__PURE__ */ ref(false);
    onMounted(() => {
      isMounted2.value = true;
    });
    return /* @__PURE__ */ readonly(isMounted2);
  }
  function useCompitable(reactive2, keys2) {
    return computed(() => {
      for (const key of keys2) {
        if (reactive2[key] !== void 0)
          return reactive2[key];
      }
      return reactive2[keys2[keys2.length - 1]];
    });
  }
  const isIos = (typeof window === "undefined" ? false : /iPad|iPhone|iPod/.test(navigator.platform) || navigator.platform === "MacIntel" && navigator.maxTouchPoints > 1) && // eslint-disable-next-line @typescript-eslint/strict-boolean-expressions
  !window.MSStream;
  function useIsIos() {
    return isIos;
  }
  const defaultBreakpointOptions = {
    // mobile
    // 0 ~ 640 doesn't mean it should display well in all the range,
    // but means you should treat it like a mobile phone.)
    xs: 0,
    s: 640,
    m: 1024,
    l: 1280,
    xl: 1536,
    "2xl": 1920
    // normal desktop display
  };
  function createMediaQuery(screenWidth) {
    return `(min-width: ${screenWidth}px)`;
  }
  const mqlMap = {};
  function useBreakpoints(screens = defaultBreakpointOptions) {
    if (!isBrowser$2)
      return computed(() => []);
    if (typeof window.matchMedia !== "function")
      return computed(() => []);
    const breakpointStatusRef = /* @__PURE__ */ ref({});
    const breakpoints = Object.keys(screens);
    const updateBreakpoints = (e, breakpointName) => {
      if (e.matches)
        breakpointStatusRef.value[breakpointName] = true;
      else
        breakpointStatusRef.value[breakpointName] = false;
    };
    breakpoints.forEach((key) => {
      const breakpointValue = screens[key];
      let mql;
      let cbs;
      if (mqlMap[breakpointValue] === void 0) {
        mql = window.matchMedia(createMediaQuery(breakpointValue));
        if (mql.addEventListener) {
          mql.addEventListener("change", (e) => {
            cbs.forEach((cb) => {
              cb(e, key);
            });
          });
        } else if (mql.addListener) {
          mql.addListener((e) => {
            cbs.forEach((cb) => {
              cb(e, key);
            });
          });
        }
        cbs = /* @__PURE__ */ new Set();
        mqlMap[breakpointValue] = {
          mql,
          cbs
        };
      } else {
        mql = mqlMap[breakpointValue].mql;
        cbs = mqlMap[breakpointValue].cbs;
      }
      cbs.add(updateBreakpoints);
      if (mql.matches) {
        cbs.forEach((cb) => {
          cb(mql, key);
        });
      }
    });
    onBeforeUnmount(() => {
      breakpoints.forEach((breakpoint) => {
        const { cbs } = mqlMap[screens[breakpoint]];
        if (cbs.has(updateBreakpoints)) {
          cbs.delete(updateBreakpoints);
        }
      });
    });
    return computed(() => {
      const { value } = breakpointStatusRef;
      return breakpoints.filter((key) => value[key]);
    });
  }
  function useKeyboard(options = {}, enabledRef) {
    const state = /* @__PURE__ */ reactive({
      ctrl: false,
      command: false,
      win: false,
      shift: false,
      tab: false
    });
    const { keydown, keyup } = options;
    const keydownHandler = (e) => {
      switch (e.key) {
        case "Control":
          state.ctrl = true;
          break;
        case "Meta":
          state.command = true;
          state.win = true;
          break;
        case "Shift":
          state.shift = true;
          break;
        case "Tab":
          state.tab = true;
          break;
      }
      if (keydown !== void 0) {
        Object.keys(keydown).forEach((key) => {
          if (key !== e.key)
            return;
          const handler = keydown[key];
          if (typeof handler === "function") {
            handler(e);
          } else {
            const { stop = false, prevent = false } = handler;
            if (stop)
              e.stopPropagation();
            if (prevent)
              e.preventDefault();
            handler.handler(e);
          }
        });
      }
    };
    const keyupHandler = (e) => {
      switch (e.key) {
        case "Control":
          state.ctrl = false;
          break;
        case "Meta":
          state.command = false;
          state.win = false;
          break;
        case "Shift":
          state.shift = false;
          break;
        case "Tab":
          state.tab = false;
          break;
      }
      if (keyup !== void 0) {
        Object.keys(keyup).forEach((key) => {
          if (key !== e.key)
            return;
          const handler = keyup[key];
          if (typeof handler === "function") {
            handler(e);
          } else {
            const { stop = false, prevent = false } = handler;
            if (stop)
              e.stopPropagation();
            if (prevent)
              e.preventDefault();
            handler.handler(e);
          }
        });
      }
    };
    const setup = () => {
      if (enabledRef === void 0 || enabledRef.value) {
        on("keydown", document, keydownHandler);
        on("keyup", document, keyupHandler);
      }
      if (enabledRef !== void 0) {
        watch(enabledRef, (value) => {
          if (value) {
            on("keydown", document, keydownHandler);
            on("keyup", document, keyupHandler);
          } else {
            off("keydown", document, keydownHandler);
            off("keyup", document, keyupHandler);
          }
        });
      }
    };
    if (hasInstance()) {
      onBeforeMount(setup);
      onBeforeUnmount(() => {
        if (enabledRef === void 0 || enabledRef.value) {
          off("keydown", document, keydownHandler);
          off("keyup", document, keyupHandler);
        }
      });
    } else {
      setup();
    }
    return /* @__PURE__ */ readonly(state);
  }
  function createInjectionKey(key) {
    return key;
  }
  const internalSelectionMenuBodyInjectionKey = createInjectionKey("n-internal-select-menu-body");
  const drawerBodyInjectionKey = createInjectionKey("n-drawer-body");
  const modalBodyInjectionKey = createInjectionKey("n-modal-body");
  const popoverBodyInjectionKey = createInjectionKey("n-popover-body");
  const teleportDisabled = "__disabled__";
  function useAdjustedTo(props) {
    const modal = inject(modalBodyInjectionKey, null);
    const drawer = inject(drawerBodyInjectionKey, null);
    const popover = inject(popoverBodyInjectionKey, null);
    const selectMenu = inject(internalSelectionMenuBodyInjectionKey, null);
    const fullscreenElementRef = /* @__PURE__ */ ref();
    if (typeof document !== "undefined") {
      fullscreenElementRef.value = document.fullscreenElement;
      const handleFullscreenChange = () => {
        fullscreenElementRef.value = document.fullscreenElement;
      };
      onMounted(() => {
        on("fullscreenchange", document, handleFullscreenChange);
      });
      onBeforeUnmount(() => {
        off("fullscreenchange", document, handleFullscreenChange);
      });
    }
    return useMemo(() => {
      var _a2;
      const {
        to
      } = props;
      if (to !== void 0) {
        if (to === false) return teleportDisabled;
        if (to === true) return fullscreenElementRef.value || "body";
        return to;
      }
      if (modal === null || modal === void 0 ? void 0 : modal.value) {
        return (_a2 = modal.value.$el) !== null && _a2 !== void 0 ? _a2 : modal.value;
      }
      if (drawer === null || drawer === void 0 ? void 0 : drawer.value) return drawer.value;
      if (popover === null || popover === void 0 ? void 0 : popover.value) return popover.value;
      if (selectMenu === null || selectMenu === void 0 ? void 0 : selectMenu.value) return selectMenu.value;
      return to !== null && to !== void 0 ? to : fullscreenElementRef.value || "body";
    });
  }
  useAdjustedTo.tdkey = teleportDisabled;
  useAdjustedTo.propTo = {
    type: [String, Object, Boolean],
    default: void 0
  };
  function useDeferredTrue(valueRef, delay, shouldDelayRef) {
    const delayedRef = /* @__PURE__ */ ref(valueRef.value);
    let timerId = null;
    watch(valueRef, (value) => {
      if (timerId !== null) window.clearTimeout(timerId);
      if (value === true) {
        if (shouldDelayRef && !shouldDelayRef.value) {
          delayedRef.value = true;
        } else {
          timerId = window.setTimeout(() => {
            delayedRef.value = true;
          }, delay);
        }
      } else {
        delayedRef.value = false;
      }
    });
    return delayedRef;
  }
  const isBrowser$1 = typeof document !== "undefined" && typeof window !== "undefined";
  function useReactivated(callback) {
    const isDeactivatedRef = {
      isDeactivated: false
    };
    let activateStateInitialized = false;
    onActivated(() => {
      isDeactivatedRef.isDeactivated = false;
      if (!activateStateInitialized) {
        activateStateInitialized = true;
        return;
      }
      callback();
    });
    onDeactivated(() => {
      isDeactivatedRef.isDeactivated = true;
      if (!activateStateInitialized) {
        activateStateInitialized = true;
      }
    });
    return isDeactivatedRef;
  }
  function getSlot$1(scope, slots, slotName = "default") {
    const slot = slots[slotName];
    if (slot === void 0) {
      throw new Error(`[vueuc/${scope}]: slot[${slotName}] is empty.`);
    }
    return slot();
  }
  function flatten$2(vNodes, filterCommentNode = true, result = []) {
    vNodes.forEach((vNode) => {
      if (vNode === null)
        return;
      if (typeof vNode !== "object") {
        if (typeof vNode === "string" || typeof vNode === "number") {
          result.push(createTextVNode(String(vNode)));
        }
        return;
      }
      if (Array.isArray(vNode)) {
        flatten$2(vNode, filterCommentNode, result);
        return;
      }
      if (vNode.type === Fragment) {
        if (vNode.children === null)
          return;
        if (Array.isArray(vNode.children)) {
          flatten$2(vNode.children, filterCommentNode, result);
        }
      } else if (vNode.type !== Comment) {
        result.push(vNode);
      }
    });
    return result;
  }
  function getFirstVNode(scope, slots, slotName = "default") {
    const slot = slots[slotName];
    if (slot === void 0) {
      throw new Error(`[vueuc/${scope}]: slot[${slotName}] is empty.`);
    }
    const content = flatten$2(slot());
    if (content.length === 1) {
      return content[0];
    } else {
      throw new Error(`[vueuc/${scope}]: slot[${slotName}] should have exactly one child.`);
    }
  }
  let viewMeasurer = null;
  function ensureViewBoundingRect() {
    if (viewMeasurer === null) {
      viewMeasurer = document.getElementById("v-binder-view-measurer");
      if (viewMeasurer === null) {
        viewMeasurer = document.createElement("div");
        viewMeasurer.id = "v-binder-view-measurer";
        const { style: style2 } = viewMeasurer;
        style2.position = "fixed";
        style2.left = "0";
        style2.right = "0";
        style2.top = "0";
        style2.bottom = "0";
        style2.pointerEvents = "none";
        style2.visibility = "hidden";
        document.body.appendChild(viewMeasurer);
      }
    }
    return viewMeasurer.getBoundingClientRect();
  }
  function getPointRect(x, y) {
    const viewRect = ensureViewBoundingRect();
    return {
      top: y,
      left: x,
      height: 0,
      width: 0,
      right: viewRect.width - x,
      bottom: viewRect.height - y
    };
  }
  function getRect(el) {
    const elRect = el.getBoundingClientRect();
    const viewRect = ensureViewBoundingRect();
    return {
      left: elRect.left - viewRect.left,
      top: elRect.top - viewRect.top,
      bottom: viewRect.height + viewRect.top - elRect.bottom,
      right: viewRect.width + viewRect.left - elRect.right,
      width: elRect.width,
      height: elRect.height
    };
  }
  function getParentNode(node) {
    if (node.nodeType === 9) {
      return null;
    }
    return node.parentNode;
  }
  function getScrollParent(node) {
    if (node === null)
      return null;
    const parentNode = getParentNode(node);
    if (parentNode === null) {
      return null;
    }
    if (parentNode.nodeType === 9) {
      return document;
    }
    if (parentNode.nodeType === 1) {
      const { overflow, overflowX, overflowY } = getComputedStyle(parentNode);
      if (/(auto|scroll|overlay)/.test(overflow + overflowY + overflowX)) {
        return parentNode;
      }
    }
    return getScrollParent(parentNode);
  }
  const Binder = /* @__PURE__ */ defineComponent({
    name: "Binder",
    props: {
      syncTargetWithParent: Boolean,
      syncTarget: {
        type: Boolean,
        default: true
      }
    },
    setup(props) {
      var _a2;
      provide("VBinder", (_a2 = getCurrentInstance()) === null || _a2 === void 0 ? void 0 : _a2.proxy);
      const VBinder = inject("VBinder", null);
      const targetRef = /* @__PURE__ */ ref(null);
      const setTargetRef = (el) => {
        targetRef.value = el;
        if (VBinder && props.syncTargetWithParent) {
          VBinder.setTargetRef(el);
        }
      };
      let scrollableNodes = [];
      const ensureScrollListener = () => {
        let cursor = targetRef.value;
        while (true) {
          cursor = getScrollParent(cursor);
          if (cursor === null)
            break;
          scrollableNodes.push(cursor);
        }
        for (const el of scrollableNodes) {
          on("scroll", el, onScroll, true);
        }
      };
      const removeScrollListeners = () => {
        for (const el of scrollableNodes) {
          off("scroll", el, onScroll, true);
        }
        scrollableNodes = [];
      };
      const followerScrollListeners = /* @__PURE__ */ new Set();
      const addScrollListener = (listener) => {
        if (followerScrollListeners.size === 0) {
          ensureScrollListener();
        }
        if (!followerScrollListeners.has(listener)) {
          followerScrollListeners.add(listener);
        }
      };
      const removeScrollListener = (listener) => {
        if (followerScrollListeners.has(listener)) {
          followerScrollListeners.delete(listener);
        }
        if (followerScrollListeners.size === 0) {
          removeScrollListeners();
        }
      };
      const onScroll = () => {
        beforeNextFrameOnce(onScrollRaf);
      };
      const onScrollRaf = () => {
        followerScrollListeners.forEach((listener) => listener());
      };
      const followerResizeListeners = /* @__PURE__ */ new Set();
      const addResizeListener = (listener) => {
        if (followerResizeListeners.size === 0) {
          on("resize", window, onResize);
        }
        if (!followerResizeListeners.has(listener)) {
          followerResizeListeners.add(listener);
        }
      };
      const removeResizeListener = (listener) => {
        if (followerResizeListeners.has(listener)) {
          followerResizeListeners.delete(listener);
        }
        if (followerResizeListeners.size === 0) {
          off("resize", window, onResize);
        }
      };
      const onResize = () => {
        followerResizeListeners.forEach((listener) => listener());
      };
      onBeforeUnmount(() => {
        off("resize", window, onResize);
        removeScrollListeners();
      });
      return {
        targetRef,
        setTargetRef,
        addScrollListener,
        removeScrollListener,
        addResizeListener,
        removeResizeListener
      };
    },
    render() {
      return getSlot$1("binder", this.$slots);
    }
  });
  const VTarget = /* @__PURE__ */ defineComponent({
    name: "Target",
    setup() {
      const { setTargetRef, syncTarget } = inject("VBinder");
      const setTargetDirective = {
        mounted: setTargetRef,
        updated: setTargetRef
      };
      return {
        syncTarget,
        setTargetDirective
      };
    },
    render() {
      const { syncTarget, setTargetDirective } = this;
      if (syncTarget) {
        return withDirectives(getFirstVNode("follower", this.$slots), [
          [setTargetDirective]
        ]);
      }
      return getFirstVNode("follower", this.$slots);
    }
  });
  const ctxKey$1 = "@@mmoContext";
  const mousemoveoutside = {
    mounted(el, { value }) {
      el[ctxKey$1] = {
        handler: void 0
      };
      if (typeof value === "function") {
        el[ctxKey$1].handler = value;
        on("mousemoveoutside", el, value);
      }
    },
    updated(el, { value }) {
      const ctx2 = el[ctxKey$1];
      if (typeof value === "function") {
        if (ctx2.handler) {
          if (ctx2.handler !== value) {
            off("mousemoveoutside", el, ctx2.handler);
            ctx2.handler = value;
            on("mousemoveoutside", el, value);
          }
        } else {
          el[ctxKey$1].handler = value;
          on("mousemoveoutside", el, value);
        }
      } else {
        if (ctx2.handler) {
          off("mousemoveoutside", el, ctx2.handler);
          ctx2.handler = void 0;
        }
      }
    },
    unmounted(el) {
      const { handler } = el[ctxKey$1];
      if (handler) {
        off("mousemoveoutside", el, handler);
      }
      el[ctxKey$1].handler = void 0;
    }
  };
  const ctxKey = "@@coContext";
  const clickoutside = {
    mounted(el, { value, modifiers }) {
      el[ctxKey] = {
        handler: void 0
      };
      if (typeof value === "function") {
        el[ctxKey].handler = value;
        on("clickoutside", el, value, {
          capture: modifiers.capture
        });
      }
    },
    updated(el, { value, modifiers }) {
      const ctx2 = el[ctxKey];
      if (typeof value === "function") {
        if (ctx2.handler) {
          if (ctx2.handler !== value) {
            off("clickoutside", el, ctx2.handler, {
              capture: modifiers.capture
            });
            ctx2.handler = value;
            on("clickoutside", el, value, {
              capture: modifiers.capture
            });
          }
        } else {
          el[ctxKey].handler = value;
          on("clickoutside", el, value, {
            capture: modifiers.capture
          });
        }
      } else {
        if (ctx2.handler) {
          off("clickoutside", el, ctx2.handler, {
            capture: modifiers.capture
          });
          ctx2.handler = void 0;
        }
      }
    },
    unmounted(el, { modifiers }) {
      const { handler } = el[ctxKey];
      if (handler) {
        off("clickoutside", el, handler, {
          capture: modifiers.capture
        });
      }
      el[ctxKey].handler = void 0;
    }
  };
  function warn$2(location, message) {
    console.error(`[vdirs/${location}]: ${message}`);
  }
  class ZIndexManager {
    constructor() {
      this.elementZIndex = /* @__PURE__ */ new Map();
      this.nextZIndex = 2e3;
    }
    get elementCount() {
      return this.elementZIndex.size;
    }
    ensureZIndex(el, zIndex) {
      const { elementZIndex } = this;
      if (zIndex !== void 0) {
        el.style.zIndex = `${zIndex}`;
        elementZIndex.delete(el);
        return;
      }
      const { nextZIndex } = this;
      if (elementZIndex.has(el)) {
        const currentZIndex = elementZIndex.get(el);
        if (currentZIndex + 1 === this.nextZIndex)
          return;
      }
      el.style.zIndex = `${nextZIndex}`;
      elementZIndex.set(el, nextZIndex);
      this.nextZIndex = nextZIndex + 1;
      this.squashState();
    }
    unregister(el, zIndex) {
      const { elementZIndex } = this;
      if (elementZIndex.has(el)) {
        elementZIndex.delete(el);
      } else if (zIndex === void 0) {
        warn$2("z-index-manager/unregister-element", "Element not found when unregistering.");
      }
      this.squashState();
    }
    squashState() {
      const { elementCount } = this;
      if (!elementCount) {
        this.nextZIndex = 2e3;
      }
      if (this.nextZIndex - elementCount > 2500)
        this.rearrange();
    }
    rearrange() {
      const elementZIndexPair = Array.from(this.elementZIndex.entries());
      elementZIndexPair.sort((pair1, pair2) => {
        return pair1[1] - pair2[1];
      });
      this.nextZIndex = 2e3;
      elementZIndexPair.forEach((pair) => {
        const el = pair[0];
        const zIndex = this.nextZIndex++;
        if (`${zIndex}` !== el.style.zIndex)
          el.style.zIndex = `${zIndex}`;
      });
    }
  }
  const zIndexManager = new ZIndexManager();
  const ctx = "@@ziContext";
  const zindexable = {
    mounted(el, bindings) {
      const { value = {} } = bindings;
      const { zIndex, enabled } = value;
      el[ctx] = {
        enabled: !!enabled,
        initialized: false
      };
      if (enabled) {
        zIndexManager.ensureZIndex(el, zIndex);
        el[ctx].initialized = true;
      }
    },
    updated(el, bindings) {
      const { value = {} } = bindings;
      const { zIndex, enabled } = value;
      const cachedEnabled = el[ctx].enabled;
      if (enabled && !cachedEnabled) {
        zIndexManager.ensureZIndex(el, zIndex);
        el[ctx].initialized = true;
      }
      el[ctx].enabled = !!enabled;
    },
    unmounted(el, bindings) {
      if (!el[ctx].initialized)
        return;
      const { value = {} } = bindings;
      const { zIndex } = value;
      zIndexManager.unregister(el, zIndex);
    }
  };
  const ssrContextKey = "@css-render/vue3-ssr";
  function createStyleString(id, style2) {
    return `<style cssr-id="${id}">
${style2}
</style>`;
  }
  function ssrAdapter(id, style2, ssrContext) {
    const { styles, ids } = ssrContext;
    if (ids.has(id))
      return;
    if (styles !== null) {
      ids.add(id);
      styles.push(createStyleString(id, style2));
    }
  }
  const isBrowser = typeof document !== "undefined";
  function useSsrAdapter() {
    if (isBrowser)
      return void 0;
    const context = inject(ssrContextKey, null);
    if (context === null)
      return void 0;
    return {
      adapter: (id, style2) => ssrAdapter(id, style2, context),
      context
    };
  }
  function warn$1(location, message) {
    console.error(`[vueuc/${location}]: ${message}`);
  }
  const { c } = CssRender();
  const cssrAnchorMetaName$1 = "vueuc-style";
  function resolveTo(selector) {
    if (typeof selector === "string") {
      return document.querySelector(selector);
    }
    return selector() || null;
  }
  const LazyTeleport = /* @__PURE__ */ defineComponent({
    name: "LazyTeleport",
    props: {
      to: {
        type: [String, Object],
        default: void 0
      },
      disabled: Boolean,
      show: {
        type: Boolean,
        required: true
      }
    },
    setup(props) {
      return {
        showTeleport: useFalseUntilTruthy(/* @__PURE__ */ toRef(props, "show")),
        mergedTo: computed(() => {
          const { to } = props;
          return to !== null && to !== void 0 ? to : "body";
        })
      };
    },
    render() {
      return this.showTeleport ? this.disabled ? getSlot$1("lazy-teleport", this.$slots) : h(Teleport, {
        disabled: this.disabled,
        to: this.mergedTo
      }, getSlot$1("lazy-teleport", this.$slots)) : null;
    }
  });
  const oppositionPositions = {
    top: "bottom",
    bottom: "top",
    left: "right",
    right: "left"
  };
  const oppositeAligns = {
    start: "end",
    center: "center",
    end: "start"
  };
  const propToCompare = {
    top: "height",
    bottom: "height",
    left: "width",
    right: "width"
  };
  const transformOrigins = {
    "bottom-start": "top left",
    bottom: "top center",
    "bottom-end": "top right",
    "top-start": "bottom left",
    top: "bottom center",
    "top-end": "bottom right",
    "right-start": "top left",
    right: "center left",
    "right-end": "bottom left",
    "left-start": "top right",
    left: "center right",
    "left-end": "bottom right"
  };
  const overlapTransformOrigin = {
    "bottom-start": "bottom left",
    bottom: "bottom center",
    "bottom-end": "bottom right",
    "top-start": "top left",
    top: "top center",
    "top-end": "top right",
    "right-start": "top right",
    right: "center right",
    "right-end": "bottom right",
    "left-start": "top left",
    left: "center left",
    "left-end": "bottom left"
  };
  const oppositeAlignCssPositionProps = {
    "bottom-start": "right",
    "bottom-end": "left",
    "top-start": "right",
    "top-end": "left",
    "right-start": "bottom",
    "right-end": "top",
    "left-start": "bottom",
    "left-end": "top"
  };
  const keepOffsetDirection = {
    top: true,
    // top++
    bottom: false,
    // top--
    left: true,
    // left++
    right: false
    // left--
  };
  const cssPositionToOppositeAlign = {
    top: "end",
    bottom: "start",
    left: "end",
    right: "start"
  };
  function getPlacementAndOffsetOfFollower(placement, targetRect, followerRect, shift, flip, overlap) {
    if (!flip || overlap) {
      return { placement, top: 0, left: 0 };
    }
    const [position, align] = placement.split("-");
    let properAlign = align !== null && align !== void 0 ? align : "center";
    let properOffset = {
      top: 0,
      left: 0
    };
    const deriveOffset = (oppositeAlignCssSizeProp, alignCssPositionProp, offsetVertically2) => {
      let left = 0;
      let top = 0;
      const diff = followerRect[oppositeAlignCssSizeProp] - targetRect[alignCssPositionProp] - targetRect[oppositeAlignCssSizeProp];
      if (diff > 0 && shift) {
        if (offsetVertically2) {
          top = keepOffsetDirection[alignCssPositionProp] ? diff : -diff;
        } else {
          left = keepOffsetDirection[alignCssPositionProp] ? diff : -diff;
        }
      }
      return {
        left,
        top
      };
    };
    const offsetVertically = position === "left" || position === "right";
    if (properAlign !== "center") {
      const oppositeAlignCssPositionProp = oppositeAlignCssPositionProps[placement];
      const currentAlignCssPositionProp = oppositionPositions[oppositeAlignCssPositionProp];
      const oppositeAlignCssSizeProp = propToCompare[oppositeAlignCssPositionProp];
      if (followerRect[oppositeAlignCssSizeProp] > targetRect[oppositeAlignCssSizeProp]) {
        if (
          // current space is not enough
          // ----------[ target ]---------|
          // -------[     follower        ]
          targetRect[oppositeAlignCssPositionProp] + targetRect[oppositeAlignCssSizeProp] < followerRect[oppositeAlignCssSizeProp]
        ) {
          const followerOverTargetSize = (followerRect[oppositeAlignCssSizeProp] - targetRect[oppositeAlignCssSizeProp]) / 2;
          if (targetRect[oppositeAlignCssPositionProp] < followerOverTargetSize || targetRect[currentAlignCssPositionProp] < followerOverTargetSize) {
            if (targetRect[oppositeAlignCssPositionProp] < targetRect[currentAlignCssPositionProp]) {
              properAlign = oppositeAligns[align];
              properOffset = deriveOffset(oppositeAlignCssSizeProp, currentAlignCssPositionProp, offsetVertically);
            } else {
              properOffset = deriveOffset(oppositeAlignCssSizeProp, oppositeAlignCssPositionProp, offsetVertically);
            }
          } else {
            properAlign = "center";
          }
        }
      } else if (followerRect[oppositeAlignCssSizeProp] < targetRect[oppositeAlignCssSizeProp]) {
        if (targetRect[currentAlignCssPositionProp] < 0 && // opposite align has larger space
        // ------------[   target   ]
        // ----------------[follower]
        targetRect[oppositeAlignCssPositionProp] > targetRect[currentAlignCssPositionProp]) {
          properAlign = oppositeAligns[align];
        }
      }
    } else {
      const possibleAlternativeAlignCssPositionProp1 = position === "bottom" || position === "top" ? "left" : "top";
      const possibleAlternativeAlignCssPositionProp2 = oppositionPositions[possibleAlternativeAlignCssPositionProp1];
      const alternativeAlignCssSizeProp = propToCompare[possibleAlternativeAlignCssPositionProp1];
      const followerOverTargetSize = (followerRect[alternativeAlignCssSizeProp] - targetRect[alternativeAlignCssSizeProp]) / 2;
      if (
        // center is not enough
        // ----------- [ target ]--|
        // -------[     follower     ]
        targetRect[possibleAlternativeAlignCssPositionProp1] < followerOverTargetSize || targetRect[possibleAlternativeAlignCssPositionProp2] < followerOverTargetSize
      ) {
        if (targetRect[possibleAlternativeAlignCssPositionProp1] > targetRect[possibleAlternativeAlignCssPositionProp2]) {
          properAlign = cssPositionToOppositeAlign[possibleAlternativeAlignCssPositionProp1];
          properOffset = deriveOffset(alternativeAlignCssSizeProp, possibleAlternativeAlignCssPositionProp1, offsetVertically);
        } else {
          properAlign = cssPositionToOppositeAlign[possibleAlternativeAlignCssPositionProp2];
          properOffset = deriveOffset(alternativeAlignCssSizeProp, possibleAlternativeAlignCssPositionProp2, offsetVertically);
        }
      }
    }
    let properPosition = position;
    if (
      // space is not enough
      targetRect[position] < followerRect[propToCompare[position]] && // opposite position's space is larger
      targetRect[position] < targetRect[oppositionPositions[position]]
    ) {
      properPosition = oppositionPositions[position];
    }
    return {
      placement: properAlign !== "center" ? `${properPosition}-${properAlign}` : properPosition,
      left: properOffset.left,
      top: properOffset.top
    };
  }
  function getProperTransformOrigin(placement, overlap) {
    if (overlap)
      return overlapTransformOrigin[placement];
    return transformOrigins[placement];
  }
  function getOffset(placement, offsetRect, targetRect, offsetTopToStandardPlacement, offsetLeftToStandardPlacement, overlap) {
    if (overlap) {
      switch (placement) {
        case "bottom-start":
          return {
            top: `${Math.round(targetRect.top - offsetRect.top + targetRect.height)}px`,
            left: `${Math.round(targetRect.left - offsetRect.left)}px`,
            transform: "translateY(-100%)"
          };
        case "bottom-end":
          return {
            top: `${Math.round(targetRect.top - offsetRect.top + targetRect.height)}px`,
            left: `${Math.round(targetRect.left - offsetRect.left + targetRect.width)}px`,
            transform: "translateX(-100%) translateY(-100%)"
          };
        case "top-start":
          return {
            top: `${Math.round(targetRect.top - offsetRect.top)}px`,
            left: `${Math.round(targetRect.left - offsetRect.left)}px`,
            transform: ""
          };
        case "top-end":
          return {
            top: `${Math.round(targetRect.top - offsetRect.top)}px`,
            left: `${Math.round(targetRect.left - offsetRect.left + targetRect.width)}px`,
            transform: "translateX(-100%)"
          };
        case "right-start":
          return {
            top: `${Math.round(targetRect.top - offsetRect.top)}px`,
            left: `${Math.round(targetRect.left - offsetRect.left + targetRect.width)}px`,
            transform: "translateX(-100%)"
          };
        case "right-end":
          return {
            top: `${Math.round(targetRect.top - offsetRect.top + targetRect.height)}px`,
            left: `${Math.round(targetRect.left - offsetRect.left + targetRect.width)}px`,
            transform: "translateX(-100%) translateY(-100%)"
          };
        case "left-start":
          return {
            top: `${Math.round(targetRect.top - offsetRect.top)}px`,
            left: `${Math.round(targetRect.left - offsetRect.left)}px`,
            transform: ""
          };
        case "left-end":
          return {
            top: `${Math.round(targetRect.top - offsetRect.top + targetRect.height)}px`,
            left: `${Math.round(targetRect.left - offsetRect.left)}px`,
            transform: "translateY(-100%)"
          };
        case "top":
          return {
            top: `${Math.round(targetRect.top - offsetRect.top)}px`,
            left: `${Math.round(targetRect.left - offsetRect.left + targetRect.width / 2)}px`,
            transform: "translateX(-50%)"
          };
        case "right":
          return {
            top: `${Math.round(targetRect.top - offsetRect.top + targetRect.height / 2)}px`,
            left: `${Math.round(targetRect.left - offsetRect.left + targetRect.width)}px`,
            transform: "translateX(-100%) translateY(-50%)"
          };
        case "left":
          return {
            top: `${Math.round(targetRect.top - offsetRect.top + targetRect.height / 2)}px`,
            left: `${Math.round(targetRect.left - offsetRect.left)}px`,
            transform: "translateY(-50%)"
          };
        case "bottom":
        default:
          return {
            top: `${Math.round(targetRect.top - offsetRect.top + targetRect.height)}px`,
            left: `${Math.round(targetRect.left - offsetRect.left + targetRect.width / 2)}px`,
            transform: "translateX(-50%) translateY(-100%)"
          };
      }
    }
    switch (placement) {
      case "bottom-start":
        return {
          top: `${Math.round(targetRect.top - offsetRect.top + targetRect.height + offsetTopToStandardPlacement)}px`,
          left: `${Math.round(targetRect.left - offsetRect.left + offsetLeftToStandardPlacement)}px`,
          transform: ""
        };
      case "bottom-end":
        return {
          top: `${Math.round(targetRect.top - offsetRect.top + targetRect.height + offsetTopToStandardPlacement)}px`,
          left: `${Math.round(targetRect.left - offsetRect.left + targetRect.width + offsetLeftToStandardPlacement)}px`,
          transform: "translateX(-100%)"
        };
      case "top-start":
        return {
          top: `${Math.round(targetRect.top - offsetRect.top + offsetTopToStandardPlacement)}px`,
          left: `${Math.round(targetRect.left - offsetRect.left + offsetLeftToStandardPlacement)}px`,
          transform: "translateY(-100%)"
        };
      case "top-end":
        return {
          top: `${Math.round(targetRect.top - offsetRect.top + offsetTopToStandardPlacement)}px`,
          left: `${Math.round(targetRect.left - offsetRect.left + targetRect.width + offsetLeftToStandardPlacement)}px`,
          transform: "translateX(-100%) translateY(-100%)"
        };
      case "right-start":
        return {
          top: `${Math.round(targetRect.top - offsetRect.top + offsetTopToStandardPlacement)}px`,
          left: `${Math.round(targetRect.left - offsetRect.left + targetRect.width + offsetLeftToStandardPlacement)}px`,
          transform: ""
        };
      case "right-end":
        return {
          top: `${Math.round(targetRect.top - offsetRect.top + targetRect.height + offsetTopToStandardPlacement)}px`,
          left: `${Math.round(targetRect.left - offsetRect.left + targetRect.width + offsetLeftToStandardPlacement)}px`,
          transform: "translateY(-100%)"
        };
      case "left-start":
        return {
          top: `${Math.round(targetRect.top - offsetRect.top + offsetTopToStandardPlacement)}px`,
          left: `${Math.round(targetRect.left - offsetRect.left + offsetLeftToStandardPlacement)}px`,
          transform: "translateX(-100%)"
        };
      case "left-end":
        return {
          top: `${Math.round(targetRect.top - offsetRect.top + targetRect.height + offsetTopToStandardPlacement)}px`,
          left: `${Math.round(targetRect.left - offsetRect.left + offsetLeftToStandardPlacement)}px`,
          transform: "translateX(-100%) translateY(-100%)"
        };
      case "top":
        return {
          top: `${Math.round(targetRect.top - offsetRect.top + offsetTopToStandardPlacement)}px`,
          left: `${Math.round(targetRect.left - offsetRect.left + targetRect.width / 2 + offsetLeftToStandardPlacement)}px`,
          transform: "translateY(-100%) translateX(-50%)"
        };
      case "right":
        return {
          top: `${Math.round(targetRect.top - offsetRect.top + targetRect.height / 2 + offsetTopToStandardPlacement)}px`,
          left: `${Math.round(targetRect.left - offsetRect.left + targetRect.width + offsetLeftToStandardPlacement)}px`,
          transform: "translateY(-50%)"
        };
      case "left":
        return {
          top: `${Math.round(targetRect.top - offsetRect.top + targetRect.height / 2 + offsetTopToStandardPlacement)}px`,
          left: `${Math.round(targetRect.left - offsetRect.left + offsetLeftToStandardPlacement)}px`,
          transform: "translateY(-50%) translateX(-100%)"
        };
      case "bottom":
      default:
        return {
          top: `${Math.round(targetRect.top - offsetRect.top + targetRect.height + offsetTopToStandardPlacement)}px`,
          left: `${Math.round(targetRect.left - offsetRect.left + targetRect.width / 2 + offsetLeftToStandardPlacement)}px`,
          transform: "translateX(-50%)"
        };
    }
  }
  const style$n = c([
    c(".v-binder-follower-container", {
      position: "absolute",
      left: "0",
      right: "0",
      top: "0",
      height: "0",
      pointerEvents: "none",
      zIndex: "auto"
    }),
    c(".v-binder-follower-content", {
      position: "absolute",
      zIndex: "auto"
    }, [
      c("> *", {
        pointerEvents: "all"
      })
    ])
  ]);
  const VFollower = /* @__PURE__ */ defineComponent({
    name: "Follower",
    inheritAttrs: false,
    props: {
      show: Boolean,
      enabled: {
        type: Boolean,
        default: void 0
      },
      placement: {
        type: String,
        default: "bottom"
      },
      syncTrigger: {
        type: Array,
        default: ["resize", "scroll"]
      },
      to: [String, Object],
      flip: {
        type: Boolean,
        default: true
      },
      internalShift: Boolean,
      x: Number,
      y: Number,
      width: String,
      minWidth: String,
      containerClass: String,
      teleportDisabled: Boolean,
      zindexable: {
        type: Boolean,
        default: true
      },
      zIndex: Number,
      overlap: Boolean
    },
    setup(props) {
      const VBinder = inject("VBinder");
      const mergedEnabledRef = useMemo(() => {
        return props.enabled !== void 0 ? props.enabled : props.show;
      });
      const followerRef = /* @__PURE__ */ ref(null);
      const offsetContainerRef = /* @__PURE__ */ ref(null);
      const ensureListeners = () => {
        const { syncTrigger } = props;
        if (syncTrigger.includes("scroll")) {
          VBinder.addScrollListener(syncPosition);
        }
        if (syncTrigger.includes("resize")) {
          VBinder.addResizeListener(syncPosition);
        }
      };
      const removeListeners = () => {
        VBinder.removeScrollListener(syncPosition);
        VBinder.removeResizeListener(syncPosition);
      };
      onMounted(() => {
        if (mergedEnabledRef.value) {
          syncPosition();
          ensureListeners();
        }
      });
      const ssrAdapter2 = useSsrAdapter();
      style$n.mount({
        id: "vueuc/binder",
        head: true,
        anchorMetaName: cssrAnchorMetaName$1,
        ssr: ssrAdapter2
      });
      onBeforeUnmount(() => {
        removeListeners();
      });
      onFontsReady(() => {
        if (mergedEnabledRef.value) {
          syncPosition();
        }
      });
      const syncPosition = () => {
        if (!mergedEnabledRef.value) {
          return;
        }
        const follower = followerRef.value;
        if (follower === null)
          return;
        const target = VBinder.targetRef;
        const { x, y, overlap } = props;
        const targetRect = x !== void 0 && y !== void 0 ? getPointRect(x, y) : getRect(target);
        follower.style.setProperty("--v-target-width", `${Math.round(targetRect.width)}px`);
        follower.style.setProperty("--v-target-height", `${Math.round(targetRect.height)}px`);
        const { width, minWidth, placement, internalShift, flip } = props;
        follower.setAttribute("v-placement", placement);
        if (overlap) {
          follower.setAttribute("v-overlap", "");
        } else {
          follower.removeAttribute("v-overlap");
        }
        const { style: style2 } = follower;
        if (width === "target") {
          style2.width = `${targetRect.width}px`;
        } else if (width !== void 0) {
          style2.width = width;
        } else {
          style2.width = "";
        }
        if (minWidth === "target") {
          style2.minWidth = `${targetRect.width}px`;
        } else if (minWidth !== void 0) {
          style2.minWidth = minWidth;
        } else {
          style2.minWidth = "";
        }
        const followerRect = getRect(follower);
        const offsetContainerRect = getRect(offsetContainerRef.value);
        const { left: offsetLeftToStandardPlacement, top: offsetTopToStandardPlacement, placement: properPlacement } = getPlacementAndOffsetOfFollower(placement, targetRect, followerRect, internalShift, flip, overlap);
        const properTransformOrigin = getProperTransformOrigin(properPlacement, overlap);
        const { left, top, transform } = getOffset(properPlacement, offsetContainerRect, targetRect, offsetTopToStandardPlacement, offsetLeftToStandardPlacement, overlap);
        follower.setAttribute("v-placement", properPlacement);
        follower.style.setProperty("--v-offset-left", `${Math.round(offsetLeftToStandardPlacement)}px`);
        follower.style.setProperty("--v-offset-top", `${Math.round(offsetTopToStandardPlacement)}px`);
        follower.style.transform = `translateX(${left}) translateY(${top}) ${transform}`;
        follower.style.setProperty("--v-transform-origin", properTransformOrigin);
        follower.style.transformOrigin = properTransformOrigin;
      };
      watch(mergedEnabledRef, (value) => {
        if (value) {
          ensureListeners();
          syncOnNextTick();
        } else {
          removeListeners();
        }
      });
      const syncOnNextTick = () => {
        nextTick().then(syncPosition).catch((e) => console.error(e));
      };
      [
        "placement",
        "x",
        "y",
        "internalShift",
        "flip",
        "width",
        "overlap",
        "minWidth"
      ].forEach((prop) => {
        watch(/* @__PURE__ */ toRef(props, prop), syncPosition);
      });
      ["teleportDisabled"].forEach((prop) => {
        watch(/* @__PURE__ */ toRef(props, prop), syncOnNextTick);
      });
      watch(/* @__PURE__ */ toRef(props, "syncTrigger"), (value) => {
        if (!value.includes("resize")) {
          VBinder.removeResizeListener(syncPosition);
        } else {
          VBinder.addResizeListener(syncPosition);
        }
        if (!value.includes("scroll")) {
          VBinder.removeScrollListener(syncPosition);
        } else {
          VBinder.addScrollListener(syncPosition);
        }
      });
      const isMountedRef = isMounted();
      const mergedToRef = useMemo(() => {
        const { to } = props;
        if (to !== void 0)
          return to;
        if (isMountedRef.value) {
          return void 0;
        }
        return void 0;
      });
      return {
        VBinder,
        mergedEnabled: mergedEnabledRef,
        offsetContainerRef,
        followerRef,
        mergedTo: mergedToRef,
        syncPosition
      };
    },
    render() {
      return h(LazyTeleport, {
        show: this.show,
        to: this.mergedTo,
        disabled: this.teleportDisabled
      }, {
        default: () => {
          var _a2, _b;
          const vNode = h("div", {
            class: ["v-binder-follower-container", this.containerClass],
            ref: "offsetContainerRef"
          }, [
            h("div", {
              class: "v-binder-follower-content",
              ref: "followerRef"
            }, (_b = (_a2 = this.$slots).default) === null || _b === void 0 ? void 0 : _b.call(_a2))
          ]);
          if (this.zindexable) {
            return withDirectives(vNode, [
              [
                zindexable,
                {
                  enabled: this.mergedEnabled,
                  zIndex: this.zIndex
                }
              ]
            ]);
          }
          return vNode;
        }
      });
    }
  });
  var resizeObservers = [];
  var hasActiveObservations = function() {
    return resizeObservers.some(function(ro) {
      return ro.activeTargets.length > 0;
    });
  };
  var hasSkippedObservations = function() {
    return resizeObservers.some(function(ro) {
      return ro.skippedTargets.length > 0;
    });
  };
  var msg = "ResizeObserver loop completed with undelivered notifications.";
  var deliverResizeLoopError = function() {
    var event;
    if (typeof ErrorEvent === "function") {
      event = new ErrorEvent("error", {
        message: msg
      });
    } else {
      event = document.createEvent("Event");
      event.initEvent("error", false, false);
      event.message = msg;
    }
    window.dispatchEvent(event);
  };
  var ResizeObserverBoxOptions;
  (function(ResizeObserverBoxOptions2) {
    ResizeObserverBoxOptions2["BORDER_BOX"] = "border-box";
    ResizeObserverBoxOptions2["CONTENT_BOX"] = "content-box";
    ResizeObserverBoxOptions2["DEVICE_PIXEL_CONTENT_BOX"] = "device-pixel-content-box";
  })(ResizeObserverBoxOptions || (ResizeObserverBoxOptions = {}));
  var freeze = function(obj) {
    return Object.freeze(obj);
  };
  var ResizeObserverSize = /* @__PURE__ */ function() {
    function ResizeObserverSize2(inlineSize, blockSize) {
      this.inlineSize = inlineSize;
      this.blockSize = blockSize;
      freeze(this);
    }
    return ResizeObserverSize2;
  }();
  var DOMRectReadOnly = function() {
    function DOMRectReadOnly2(x, y, width, height) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      this.top = this.y;
      this.left = this.x;
      this.bottom = this.top + this.height;
      this.right = this.left + this.width;
      return freeze(this);
    }
    DOMRectReadOnly2.prototype.toJSON = function() {
      var _a2 = this, x = _a2.x, y = _a2.y, top = _a2.top, right = _a2.right, bottom = _a2.bottom, left = _a2.left, width = _a2.width, height = _a2.height;
      return { x, y, top, right, bottom, left, width, height };
    };
    DOMRectReadOnly2.fromRect = function(rectangle) {
      return new DOMRectReadOnly2(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    };
    return DOMRectReadOnly2;
  }();
  var isSVG = function(target) {
    return target instanceof SVGElement && "getBBox" in target;
  };
  var isHidden = function(target) {
    if (isSVG(target)) {
      var _a2 = target.getBBox(), width = _a2.width, height = _a2.height;
      return !width && !height;
    }
    var _b = target, offsetWidth = _b.offsetWidth, offsetHeight = _b.offsetHeight;
    return !(offsetWidth || offsetHeight || target.getClientRects().length);
  };
  var isElement = function(obj) {
    var _a2;
    if (obj instanceof Element) {
      return true;
    }
    var scope = (_a2 = obj === null || obj === void 0 ? void 0 : obj.ownerDocument) === null || _a2 === void 0 ? void 0 : _a2.defaultView;
    return !!(scope && obj instanceof scope.Element);
  };
  var isReplacedElement = function(target) {
    switch (target.tagName) {
      case "INPUT":
        if (target.type !== "image") {
          break;
        }
      case "VIDEO":
      case "AUDIO":
      case "EMBED":
      case "OBJECT":
      case "CANVAS":
      case "IFRAME":
      case "IMG":
        return true;
    }
    return false;
  };
  var global$1 = typeof window !== "undefined" ? window : {};
  var cache = /* @__PURE__ */ new WeakMap();
  var scrollRegexp = /auto|scroll/;
  var verticalRegexp = /^tb|vertical/;
  var IE = /msie|trident/i.test(global$1.navigator && global$1.navigator.userAgent);
  var parseDimension = function(pixel) {
    return parseFloat(pixel || "0");
  };
  var size = function(inlineSize, blockSize, switchSizes) {
    if (inlineSize === void 0) {
      inlineSize = 0;
    }
    if (blockSize === void 0) {
      blockSize = 0;
    }
    if (switchSizes === void 0) {
      switchSizes = false;
    }
    return new ResizeObserverSize((switchSizes ? blockSize : inlineSize) || 0, (switchSizes ? inlineSize : blockSize) || 0);
  };
  var zeroBoxes = freeze({
    devicePixelContentBoxSize: size(),
    borderBoxSize: size(),
    contentBoxSize: size(),
    contentRect: new DOMRectReadOnly(0, 0, 0, 0)
  });
  var calculateBoxSizes = function(target, forceRecalculation) {
    if (forceRecalculation === void 0) {
      forceRecalculation = false;
    }
    if (cache.has(target) && !forceRecalculation) {
      return cache.get(target);
    }
    if (isHidden(target)) {
      cache.set(target, zeroBoxes);
      return zeroBoxes;
    }
    var cs = getComputedStyle(target);
    var svg = isSVG(target) && target.ownerSVGElement && target.getBBox();
    var removePadding = !IE && cs.boxSizing === "border-box";
    var switchSizes = verticalRegexp.test(cs.writingMode || "");
    var canScrollVertically = !svg && scrollRegexp.test(cs.overflowY || "");
    var canScrollHorizontally = !svg && scrollRegexp.test(cs.overflowX || "");
    var paddingTop = svg ? 0 : parseDimension(cs.paddingTop);
    var paddingRight = svg ? 0 : parseDimension(cs.paddingRight);
    var paddingBottom = svg ? 0 : parseDimension(cs.paddingBottom);
    var paddingLeft = svg ? 0 : parseDimension(cs.paddingLeft);
    var borderTop = svg ? 0 : parseDimension(cs.borderTopWidth);
    var borderRight = svg ? 0 : parseDimension(cs.borderRightWidth);
    var borderBottom = svg ? 0 : parseDimension(cs.borderBottomWidth);
    var borderLeft = svg ? 0 : parseDimension(cs.borderLeftWidth);
    var horizontalPadding = paddingLeft + paddingRight;
    var verticalPadding = paddingTop + paddingBottom;
    var horizontalBorderArea = borderLeft + borderRight;
    var verticalBorderArea = borderTop + borderBottom;
    var horizontalScrollbarThickness = !canScrollHorizontally ? 0 : target.offsetHeight - verticalBorderArea - target.clientHeight;
    var verticalScrollbarThickness = !canScrollVertically ? 0 : target.offsetWidth - horizontalBorderArea - target.clientWidth;
    var widthReduction = removePadding ? horizontalPadding + horizontalBorderArea : 0;
    var heightReduction = removePadding ? verticalPadding + verticalBorderArea : 0;
    var contentWidth = svg ? svg.width : parseDimension(cs.width) - widthReduction - verticalScrollbarThickness;
    var contentHeight = svg ? svg.height : parseDimension(cs.height) - heightReduction - horizontalScrollbarThickness;
    var borderBoxWidth = contentWidth + horizontalPadding + verticalScrollbarThickness + horizontalBorderArea;
    var borderBoxHeight = contentHeight + verticalPadding + horizontalScrollbarThickness + verticalBorderArea;
    var boxes = freeze({
      devicePixelContentBoxSize: size(Math.round(contentWidth * devicePixelRatio), Math.round(contentHeight * devicePixelRatio), switchSizes),
      borderBoxSize: size(borderBoxWidth, borderBoxHeight, switchSizes),
      contentBoxSize: size(contentWidth, contentHeight, switchSizes),
      contentRect: new DOMRectReadOnly(paddingLeft, paddingTop, contentWidth, contentHeight)
    });
    cache.set(target, boxes);
    return boxes;
  };
  var calculateBoxSize = function(target, observedBox, forceRecalculation) {
    var _a2 = calculateBoxSizes(target, forceRecalculation), borderBoxSize = _a2.borderBoxSize, contentBoxSize = _a2.contentBoxSize, devicePixelContentBoxSize = _a2.devicePixelContentBoxSize;
    switch (observedBox) {
      case ResizeObserverBoxOptions.DEVICE_PIXEL_CONTENT_BOX:
        return devicePixelContentBoxSize;
      case ResizeObserverBoxOptions.BORDER_BOX:
        return borderBoxSize;
      default:
        return contentBoxSize;
    }
  };
  var ResizeObserverEntry = /* @__PURE__ */ function() {
    function ResizeObserverEntry2(target) {
      var boxes = calculateBoxSizes(target);
      this.target = target;
      this.contentRect = boxes.contentRect;
      this.borderBoxSize = freeze([boxes.borderBoxSize]);
      this.contentBoxSize = freeze([boxes.contentBoxSize]);
      this.devicePixelContentBoxSize = freeze([boxes.devicePixelContentBoxSize]);
    }
    return ResizeObserverEntry2;
  }();
  var calculateDepthForNode = function(node) {
    if (isHidden(node)) {
      return Infinity;
    }
    var depth = 0;
    var parent = node.parentNode;
    while (parent) {
      depth += 1;
      parent = parent.parentNode;
    }
    return depth;
  };
  var broadcastActiveObservations = function() {
    var shallowestDepth = Infinity;
    var callbacks2 = [];
    resizeObservers.forEach(function processObserver(ro) {
      if (ro.activeTargets.length === 0) {
        return;
      }
      var entries = [];
      ro.activeTargets.forEach(function processTarget(ot) {
        var entry = new ResizeObserverEntry(ot.target);
        var targetDepth = calculateDepthForNode(ot.target);
        entries.push(entry);
        ot.lastReportedSize = calculateBoxSize(ot.target, ot.observedBox);
        if (targetDepth < shallowestDepth) {
          shallowestDepth = targetDepth;
        }
      });
      callbacks2.push(function resizeObserverCallback() {
        ro.callback.call(ro.observer, entries, ro.observer);
      });
      ro.activeTargets.splice(0, ro.activeTargets.length);
    });
    for (var _i = 0, callbacks_1 = callbacks2; _i < callbacks_1.length; _i++) {
      var callback = callbacks_1[_i];
      callback();
    }
    return shallowestDepth;
  };
  var gatherActiveObservationsAtDepth = function(depth) {
    resizeObservers.forEach(function processObserver(ro) {
      ro.activeTargets.splice(0, ro.activeTargets.length);
      ro.skippedTargets.splice(0, ro.skippedTargets.length);
      ro.observationTargets.forEach(function processTarget(ot) {
        if (ot.isActive()) {
          if (calculateDepthForNode(ot.target) > depth) {
            ro.activeTargets.push(ot);
          } else {
            ro.skippedTargets.push(ot);
          }
        }
      });
    });
  };
  var process = function() {
    var depth = 0;
    gatherActiveObservationsAtDepth(depth);
    while (hasActiveObservations()) {
      depth = broadcastActiveObservations();
      gatherActiveObservationsAtDepth(depth);
    }
    if (hasSkippedObservations()) {
      deliverResizeLoopError();
    }
    return depth > 0;
  };
  var trigger;
  var callbacks = [];
  var notify = function() {
    return callbacks.splice(0).forEach(function(cb) {
      return cb();
    });
  };
  var queueMicroTask = function(callback) {
    if (!trigger) {
      var toggle_1 = 0;
      var el_1 = document.createTextNode("");
      var config = { characterData: true };
      new MutationObserver(function() {
        return notify();
      }).observe(el_1, config);
      trigger = function() {
        el_1.textContent = "".concat(toggle_1 ? toggle_1-- : toggle_1++);
      };
    }
    callbacks.push(callback);
    trigger();
  };
  var queueResizeObserver = function(cb) {
    queueMicroTask(function ResizeObserver2() {
      requestAnimationFrame(cb);
    });
  };
  var watching = 0;
  var isWatching = function() {
    return !!watching;
  };
  var CATCH_PERIOD = 250;
  var observerConfig = { attributes: true, characterData: true, childList: true, subtree: true };
  var events = [
    "resize",
    "load",
    "transitionend",
    "animationend",
    "animationstart",
    "animationiteration",
    "keyup",
    "keydown",
    "mouseup",
    "mousedown",
    "mouseover",
    "mouseout",
    "blur",
    "focus"
  ];
  var time = function(timeout) {
    if (timeout === void 0) {
      timeout = 0;
    }
    return Date.now() + timeout;
  };
  var scheduled = false;
  var Scheduler = function() {
    function Scheduler2() {
      var _this = this;
      this.stopped = true;
      this.listener = function() {
        return _this.schedule();
      };
    }
    Scheduler2.prototype.run = function(timeout) {
      var _this = this;
      if (timeout === void 0) {
        timeout = CATCH_PERIOD;
      }
      if (scheduled) {
        return;
      }
      scheduled = true;
      var until = time(timeout);
      queueResizeObserver(function() {
        var elementsHaveResized = false;
        try {
          elementsHaveResized = process();
        } finally {
          scheduled = false;
          timeout = until - time();
          if (!isWatching()) {
            return;
          }
          if (elementsHaveResized) {
            _this.run(1e3);
          } else if (timeout > 0) {
            _this.run(timeout);
          } else {
            _this.start();
          }
        }
      });
    };
    Scheduler2.prototype.schedule = function() {
      this.stop();
      this.run();
    };
    Scheduler2.prototype.observe = function() {
      var _this = this;
      var cb = function() {
        return _this.observer && _this.observer.observe(document.body, observerConfig);
      };
      document.body ? cb() : global$1.addEventListener("DOMContentLoaded", cb);
    };
    Scheduler2.prototype.start = function() {
      var _this = this;
      if (this.stopped) {
        this.stopped = false;
        this.observer = new MutationObserver(this.listener);
        this.observe();
        events.forEach(function(name) {
          return global$1.addEventListener(name, _this.listener, true);
        });
      }
    };
    Scheduler2.prototype.stop = function() {
      var _this = this;
      if (!this.stopped) {
        this.observer && this.observer.disconnect();
        events.forEach(function(name) {
          return global$1.removeEventListener(name, _this.listener, true);
        });
        this.stopped = true;
      }
    };
    return Scheduler2;
  }();
  var scheduler = new Scheduler();
  var updateCount = function(n) {
    !watching && n > 0 && scheduler.start();
    watching += n;
    !watching && scheduler.stop();
  };
  var skipNotifyOnElement = function(target) {
    return !isSVG(target) && !isReplacedElement(target) && getComputedStyle(target).display === "inline";
  };
  var ResizeObservation = function() {
    function ResizeObservation2(target, observedBox) {
      this.target = target;
      this.observedBox = observedBox || ResizeObserverBoxOptions.CONTENT_BOX;
      this.lastReportedSize = {
        inlineSize: 0,
        blockSize: 0
      };
    }
    ResizeObservation2.prototype.isActive = function() {
      var size2 = calculateBoxSize(this.target, this.observedBox, true);
      if (skipNotifyOnElement(this.target)) {
        this.lastReportedSize = size2;
      }
      if (this.lastReportedSize.inlineSize !== size2.inlineSize || this.lastReportedSize.blockSize !== size2.blockSize) {
        return true;
      }
      return false;
    };
    return ResizeObservation2;
  }();
  var ResizeObserverDetail = /* @__PURE__ */ function() {
    function ResizeObserverDetail2(resizeObserver, callback) {
      this.activeTargets = [];
      this.skippedTargets = [];
      this.observationTargets = [];
      this.observer = resizeObserver;
      this.callback = callback;
    }
    return ResizeObserverDetail2;
  }();
  var observerMap = /* @__PURE__ */ new WeakMap();
  var getObservationIndex = function(observationTargets, target) {
    for (var i = 0; i < observationTargets.length; i += 1) {
      if (observationTargets[i].target === target) {
        return i;
      }
    }
    return -1;
  };
  var ResizeObserverController = function() {
    function ResizeObserverController2() {
    }
    ResizeObserverController2.connect = function(resizeObserver, callback) {
      var detail = new ResizeObserverDetail(resizeObserver, callback);
      observerMap.set(resizeObserver, detail);
    };
    ResizeObserverController2.observe = function(resizeObserver, target, options) {
      var detail = observerMap.get(resizeObserver);
      var firstObservation = detail.observationTargets.length === 0;
      if (getObservationIndex(detail.observationTargets, target) < 0) {
        firstObservation && resizeObservers.push(detail);
        detail.observationTargets.push(new ResizeObservation(target, options && options.box));
        updateCount(1);
        scheduler.schedule();
      }
    };
    ResizeObserverController2.unobserve = function(resizeObserver, target) {
      var detail = observerMap.get(resizeObserver);
      var index = getObservationIndex(detail.observationTargets, target);
      var lastObservation = detail.observationTargets.length === 1;
      if (index >= 0) {
        lastObservation && resizeObservers.splice(resizeObservers.indexOf(detail), 1);
        detail.observationTargets.splice(index, 1);
        updateCount(-1);
      }
    };
    ResizeObserverController2.disconnect = function(resizeObserver) {
      var _this = this;
      var detail = observerMap.get(resizeObserver);
      detail.observationTargets.slice().forEach(function(ot) {
        return _this.unobserve(resizeObserver, ot.target);
      });
      detail.activeTargets.splice(0, detail.activeTargets.length);
    };
    return ResizeObserverController2;
  }();
  var ResizeObserver = function() {
    function ResizeObserver2(callback) {
      if (arguments.length === 0) {
        throw new TypeError("Failed to construct 'ResizeObserver': 1 argument required, but only 0 present.");
      }
      if (typeof callback !== "function") {
        throw new TypeError("Failed to construct 'ResizeObserver': The callback provided as parameter 1 is not a function.");
      }
      ResizeObserverController.connect(this, callback);
    }
    ResizeObserver2.prototype.observe = function(target, options) {
      if (arguments.length === 0) {
        throw new TypeError("Failed to execute 'observe' on 'ResizeObserver': 1 argument required, but only 0 present.");
      }
      if (!isElement(target)) {
        throw new TypeError("Failed to execute 'observe' on 'ResizeObserver': parameter 1 is not of type 'Element");
      }
      ResizeObserverController.observe(this, target, options);
    };
    ResizeObserver2.prototype.unobserve = function(target) {
      if (arguments.length === 0) {
        throw new TypeError("Failed to execute 'unobserve' on 'ResizeObserver': 1 argument required, but only 0 present.");
      }
      if (!isElement(target)) {
        throw new TypeError("Failed to execute 'unobserve' on 'ResizeObserver': parameter 1 is not of type 'Element");
      }
      ResizeObserverController.unobserve(this, target);
    };
    ResizeObserver2.prototype.disconnect = function() {
      ResizeObserverController.disconnect(this);
    };
    ResizeObserver2.toString = function() {
      return "function ResizeObserver () { [polyfill code] }";
    };
    return ResizeObserver2;
  }();
  class ResizeObserverDelegate {
    constructor() {
      this.handleResize = this.handleResize.bind(this);
      this.observer = new (typeof window !== "undefined" && window.ResizeObserver || ResizeObserver)(this.handleResize);
      this.elHandlersMap = /* @__PURE__ */ new Map();
    }
    handleResize(entries) {
      for (const entry of entries) {
        const handler = this.elHandlersMap.get(entry.target);
        if (handler !== void 0) {
          handler(entry);
        }
      }
    }
    registerHandler(el, handler) {
      this.elHandlersMap.set(el, handler);
      this.observer.observe(el);
    }
    unregisterHandler(el) {
      if (!this.elHandlersMap.has(el)) {
        return;
      }
      this.elHandlersMap.delete(el);
      this.observer.unobserve(el);
    }
  }
  const resizeObserverManager = new ResizeObserverDelegate();
  const VResizeObserver = /* @__PURE__ */ defineComponent({
    name: "ResizeObserver",
    props: {
      onResize: Function
    },
    setup(props) {
      let registered = false;
      const proxy = getCurrentInstance().proxy;
      function handleResize(entry) {
        const { onResize } = props;
        if (onResize !== void 0)
          onResize(entry);
      }
      onMounted(() => {
        const el = proxy.$el;
        if (el === void 0) {
          warn$1("resize-observer", "$el does not exist.");
          return;
        }
        if (el.nextElementSibling !== el.nextSibling) {
          if (el.nodeType === 3 && el.nodeValue !== "") {
            warn$1("resize-observer", "$el can not be observed (it may be a text node).");
            return;
          }
        }
        if (el.nextElementSibling !== null) {
          resizeObserverManager.registerHandler(el.nextElementSibling, handleResize);
          registered = true;
        }
      });
      onBeforeUnmount(() => {
        if (registered) {
          resizeObserverManager.unregisterHandler(proxy.$el.nextElementSibling);
        }
      });
    },
    render() {
      return renderSlot(this.$slots, "default");
    }
  });
  const hiddenAttr = "v-hidden";
  const style$m = c("[v-hidden]", {
    display: "none!important"
  });
  const VOverflow = /* @__PURE__ */ defineComponent({
    name: "Overflow",
    props: {
      getCounter: Function,
      getTail: Function,
      updateCounter: Function,
      onUpdateCount: Function,
      onUpdateOverflow: Function
    },
    setup(props, { slots }) {
      const selfRef = /* @__PURE__ */ ref(null);
      const counterRef = /* @__PURE__ */ ref(null);
      function deriveCounter(options) {
        const { value: self2 } = selfRef;
        const { getCounter, getTail } = props;
        let counter;
        if (getCounter !== void 0)
          counter = getCounter();
        else {
          counter = counterRef.value;
        }
        if (!self2 || !counter)
          return;
        if (counter.hasAttribute(hiddenAttr)) {
          counter.removeAttribute(hiddenAttr);
        }
        const { children } = self2;
        if (options.showAllItemsBeforeCalculate) {
          for (const child of children) {
            if (child.hasAttribute(hiddenAttr)) {
              child.removeAttribute(hiddenAttr);
            }
          }
        }
        const containerWidth = self2.offsetWidth;
        const childWidths = [];
        const tail = slots.tail ? getTail === null || getTail === void 0 ? void 0 : getTail() : null;
        let childWidthSum = tail ? tail.offsetWidth : 0;
        let overflow = false;
        const len = self2.children.length - (slots.tail ? 1 : 0);
        for (let i = 0; i < len - 1; ++i) {
          if (i < 0)
            continue;
          const child = children[i];
          if (overflow) {
            if (!child.hasAttribute(hiddenAttr)) {
              child.setAttribute(hiddenAttr, "");
            }
            continue;
          } else if (child.hasAttribute(hiddenAttr)) {
            child.removeAttribute(hiddenAttr);
          }
          const childWidth = child.offsetWidth;
          childWidthSum += childWidth;
          childWidths[i] = childWidth;
          if (childWidthSum > containerWidth) {
            const { updateCounter } = props;
            for (let j = i; j >= 0; --j) {
              const restCount = len - 1 - j;
              if (updateCounter !== void 0) {
                updateCounter(restCount);
              } else {
                counter.textContent = `${restCount}`;
              }
              const counterWidth = counter.offsetWidth;
              childWidthSum -= childWidths[j];
              if (childWidthSum + counterWidth <= containerWidth || j === 0) {
                overflow = true;
                i = j - 1;
                if (tail) {
                  if (i === -1) {
                    tail.style.maxWidth = `${containerWidth - counterWidth}px`;
                    tail.style.boxSizing = "border-box";
                  } else {
                    tail.style.maxWidth = "";
                  }
                }
                const { onUpdateCount } = props;
                if (onUpdateCount)
                  onUpdateCount(restCount);
                break;
              }
            }
          }
        }
        const { onUpdateOverflow } = props;
        if (!overflow) {
          if (onUpdateOverflow !== void 0) {
            onUpdateOverflow(false);
          }
          counter.setAttribute(hiddenAttr, "");
        } else {
          if (onUpdateOverflow !== void 0) {
            onUpdateOverflow(true);
          }
        }
      }
      const ssrAdapter2 = useSsrAdapter();
      style$m.mount({
        id: "vueuc/overflow",
        head: true,
        anchorMetaName: cssrAnchorMetaName$1,
        ssr: ssrAdapter2
      });
      onMounted(() => deriveCounter({
        showAllItemsBeforeCalculate: false
      }));
      return {
        selfRef,
        counterRef,
        sync: deriveCounter
      };
    },
    render() {
      const { $slots } = this;
      nextTick(() => this.sync({
        showAllItemsBeforeCalculate: false
      }));
      return h("div", {
        class: "v-overflow",
        ref: "selfRef"
      }, [
        renderSlot($slots, "default"),
        // $slots.counter should only has 1 element
        $slots.counter ? $slots.counter() : h("span", {
          style: {
            display: "inline-block"
          },
          ref: "counterRef"
        }),
        // $slots.tail should only has 1 element
        $slots.tail ? $slots.tail() : null
      ]);
    }
  });
  function isHTMLElement(node) {
    return node instanceof HTMLElement;
  }
  function focusFirstDescendant(node) {
    for (let i = 0; i < node.childNodes.length; i++) {
      const child = node.childNodes[i];
      if (isHTMLElement(child)) {
        if (attemptFocus(child) || focusFirstDescendant(child)) {
          return true;
        }
      }
    }
    return false;
  }
  function focusLastDescendant(element) {
    for (let i = element.childNodes.length - 1; i >= 0; i--) {
      const child = element.childNodes[i];
      if (isHTMLElement(child)) {
        if (attemptFocus(child) || focusLastDescendant(child)) {
          return true;
        }
      }
    }
    return false;
  }
  function attemptFocus(element) {
    if (!isFocusable(element)) {
      return false;
    }
    try {
      element.focus({ preventScroll: true });
    } catch (e) {
    }
    return document.activeElement === element;
  }
  function isFocusable(element) {
    if (element.tabIndex > 0 || element.tabIndex === 0 && element.getAttribute("tabIndex") !== null) {
      return true;
    }
    if (element.getAttribute("disabled")) {
      return false;
    }
    switch (element.nodeName) {
      case "A":
        return !!element.href && element.rel !== "ignore";
      case "INPUT":
        return element.type !== "hidden" && element.type !== "file";
      case "SELECT":
      case "TEXTAREA":
        return true;
      default:
        return false;
    }
  }
  let stack = [];
  const FocusTrap = /* @__PURE__ */ defineComponent({
    name: "FocusTrap",
    props: {
      disabled: Boolean,
      active: Boolean,
      autoFocus: {
        type: Boolean,
        default: true
      },
      onEsc: Function,
      initialFocusTo: [String, Function],
      finalFocusTo: [String, Function],
      returnFocusOnDeactivated: {
        type: Boolean,
        default: true
      }
    },
    setup(props) {
      const id = createId();
      const focusableStartRef = /* @__PURE__ */ ref(null);
      const focusableEndRef = /* @__PURE__ */ ref(null);
      let activated = false;
      let ignoreInternalFocusChange = false;
      const lastFocusedElement = typeof document === "undefined" ? null : document.activeElement;
      function isCurrentActive() {
        const currentActiveId = stack[stack.length - 1];
        return currentActiveId === id;
      }
      function handleDocumentKeydown(e) {
        var _a2;
        if (e.code === "Escape") {
          if (isCurrentActive()) {
            (_a2 = props.onEsc) === null || _a2 === void 0 ? void 0 : _a2.call(props, e);
          }
        }
      }
      onMounted(() => {
        watch(() => props.active, (value) => {
          if (value) {
            activate();
            on("keydown", document, handleDocumentKeydown);
          } else {
            off("keydown", document, handleDocumentKeydown);
            if (activated) {
              deactivate();
            }
          }
        }, {
          immediate: true
        });
      });
      onBeforeUnmount(() => {
        off("keydown", document, handleDocumentKeydown);
        if (activated)
          deactivate();
      });
      function handleDocumentFocus(e) {
        if (ignoreInternalFocusChange)
          return;
        if (isCurrentActive()) {
          const mainEl = getMainEl();
          if (mainEl === null)
            return;
          if (mainEl.contains(getPreciseEventTarget(e)))
            return;
          resetFocusTo("first");
        }
      }
      function getMainEl() {
        const focusableStartEl = focusableStartRef.value;
        if (focusableStartEl === null)
          return null;
        let mainEl = focusableStartEl;
        while (true) {
          mainEl = mainEl.nextSibling;
          if (mainEl === null)
            break;
          if (mainEl instanceof Element && mainEl.tagName === "DIV") {
            break;
          }
        }
        return mainEl;
      }
      function activate() {
        var _a2;
        if (props.disabled)
          return;
        stack.push(id);
        if (props.autoFocus) {
          const { initialFocusTo } = props;
          if (initialFocusTo === void 0) {
            resetFocusTo("first");
          } else {
            (_a2 = resolveTo(initialFocusTo)) === null || _a2 === void 0 ? void 0 : _a2.focus({ preventScroll: true });
          }
        }
        activated = true;
        document.addEventListener("focus", handleDocumentFocus, true);
      }
      function deactivate() {
        var _a2;
        if (props.disabled)
          return;
        document.removeEventListener("focus", handleDocumentFocus, true);
        stack = stack.filter((idInStack) => idInStack !== id);
        if (isCurrentActive())
          return;
        const { finalFocusTo } = props;
        if (finalFocusTo !== void 0) {
          (_a2 = resolveTo(finalFocusTo)) === null || _a2 === void 0 ? void 0 : _a2.focus({ preventScroll: true });
        } else if (props.returnFocusOnDeactivated) {
          if (lastFocusedElement instanceof HTMLElement) {
            ignoreInternalFocusChange = true;
            lastFocusedElement.focus({ preventScroll: true });
            ignoreInternalFocusChange = false;
          }
        }
      }
      function resetFocusTo(target) {
        if (!isCurrentActive())
          return;
        if (props.active) {
          const focusableStartEl = focusableStartRef.value;
          const focusableEndEl = focusableEndRef.value;
          if (focusableStartEl !== null && focusableEndEl !== null) {
            const mainEl = getMainEl();
            if (mainEl == null || mainEl === focusableEndEl) {
              ignoreInternalFocusChange = true;
              focusableStartEl.focus({ preventScroll: true });
              ignoreInternalFocusChange = false;
              return;
            }
            ignoreInternalFocusChange = true;
            const focused = target === "first" ? focusFirstDescendant(mainEl) : focusLastDescendant(mainEl);
            ignoreInternalFocusChange = false;
            if (!focused) {
              ignoreInternalFocusChange = true;
              focusableStartEl.focus({ preventScroll: true });
              ignoreInternalFocusChange = false;
            }
          }
        }
      }
      function handleStartFocus(e) {
        if (ignoreInternalFocusChange)
          return;
        const mainEl = getMainEl();
        if (mainEl === null)
          return;
        if (e.relatedTarget !== null && mainEl.contains(e.relatedTarget)) {
          resetFocusTo("last");
        } else {
          resetFocusTo("first");
        }
      }
      function handleEndFocus(e) {
        if (ignoreInternalFocusChange)
          return;
        if (e.relatedTarget !== null && e.relatedTarget === focusableStartRef.value) {
          resetFocusTo("last");
        } else {
          resetFocusTo("first");
        }
      }
      return {
        focusableStartRef,
        focusableEndRef,
        focusableStyle: "position: absolute; height: 0; width: 0;",
        handleStartFocus,
        handleEndFocus
      };
    },
    render() {
      const { default: defaultSlot } = this.$slots;
      if (defaultSlot === void 0)
        return null;
      if (this.disabled)
        return defaultSlot();
      const { active, focusableStyle } = this;
      return h(Fragment, null, [
        h("div", {
          "aria-hidden": "true",
          tabindex: active ? "0" : "-1",
          ref: "focusableStartRef",
          style: focusableStyle,
          onFocus: this.handleStartFocus
        }),
        defaultSlot(),
        h("div", {
          "aria-hidden": "true",
          style: focusableStyle,
          ref: "focusableEndRef",
          tabindex: active ? "0" : "-1",
          onFocus: this.handleEndFocus
        })
      ]);
    }
  });
  function color2Class(color) {
    return color.replace(/#|\(|\)|,|\s|\./g, "_");
  }
  const pureNumberRegex = /^(\d|\.)+$/;
  const numberRegex = /(\d|\.)+/;
  function formatLength(length, {
    c: c2 = 1,
    offset = 0,
    attachPx = true
  } = {}) {
    if (typeof length === "number") {
      const result = (length + offset) * c2;
      if (result === 0) return "0";
      return `${result}px`;
    } else if (typeof length === "string") {
      if (pureNumberRegex.test(length)) {
        const result = (Number(length) + offset) * c2;
        if (attachPx) {
          if (result === 0) return "0";
          return `${result}px`;
        } else {
          return `${result}`;
        }
      } else {
        const result = numberRegex.exec(length);
        if (!result) return length;
        return length.replace(numberRegex, String((Number(result[0]) + offset) * c2));
      }
    }
    return length;
  }
  function rtlInset(inset) {
    const {
      left,
      right,
      top,
      bottom
    } = getMargin(inset);
    return `${top} ${left} ${bottom} ${right}`;
  }
  let _isJsdom;
  function isJsdom() {
    if (_isJsdom === void 0) {
      _isJsdom = navigator.userAgent.includes("Node.js") || navigator.userAgent.includes("jsdom");
    }
    return _isJsdom;
  }
  function warn(location, message) {
    console.error(`[naive/${location}]: ${message}`);
  }
  function throwError(location, message) {
    throw new Error(`[naive/${location}]: ${message}`);
  }
  function call(funcs, ...args) {
    if (Array.isArray(funcs)) {
      funcs.forEach((func) => call(func, ...args));
    } else {
      return funcs(...args);
    }
  }
  function createRefSetter(ref2) {
    return (inst) => {
      if (inst) {
        ref2.value = inst.$el;
      } else {
        ref2.value = null;
      }
    };
  }
  function flatten$1(vNodes, filterCommentNode = true, result = []) {
    vNodes.forEach((vNode) => {
      if (vNode === null) return;
      if (typeof vNode !== "object") {
        if (typeof vNode === "string" || typeof vNode === "number") {
          result.push(createTextVNode(String(vNode)));
        }
        return;
      }
      if (Array.isArray(vNode)) {
        flatten$1(vNode, filterCommentNode, result);
        return;
      }
      if (vNode.type === Fragment) {
        if (vNode.children === null) return;
        if (Array.isArray(vNode.children)) {
          flatten$1(vNode.children, filterCommentNode, result);
        }
      } else {
        if (vNode.type === Comment && filterCommentNode) return;
        result.push(vNode);
      }
    });
    return result;
  }
  function getFirstSlotVNode(slots, slotName = "default", props = void 0) {
    const slot = slots[slotName];
    if (!slot) {
      warn("getFirstSlotVNode", `slot[${slotName}] is empty`);
      return null;
    }
    const slotContent = flatten$1(slot(props));
    if (slotContent.length === 1) {
      return slotContent[0];
    } else {
      warn("getFirstSlotVNode", `slot[${slotName}] should have exactly one child`);
      return null;
    }
  }
  function getSlot(instance, slotName = "default", fallback = []) {
    const slots = instance.$slots;
    const slot = slots[slotName];
    if (slot === void 0) return fallback;
    return slot();
  }
  function getVNodeChildren(vNode, slotName = "default", fallback = []) {
    const {
      children
    } = vNode;
    if (children !== null && typeof children === "object" && !Array.isArray(children)) {
      const slot = children[slotName];
      if (typeof slot === "function") {
        return slot();
      }
    }
    return fallback;
  }
  function isNodeVShowFalse(vNode) {
    var _a2;
    const showDir = (_a2 = vNode.dirs) === null || _a2 === void 0 ? void 0 : _a2.find(({
      dir
    }) => dir === vShow);
    return !!(showDir && showDir.value === false);
  }
  function keep(object, keys2 = [], rest) {
    const keepedObject = {};
    keys2.forEach((key) => {
      keepedObject[key] = object[key];
    });
    return Object.assign(keepedObject, rest);
  }
  function keysOf(obj) {
    return Object.keys(obj);
  }
  function omit(object, keys2 = [], rest) {
    const omitedObject = {};
    const originalKeys = Object.getOwnPropertyNames(object);
    originalKeys.forEach((originalKey) => {
      if (!keys2.includes(originalKey)) {
        omitedObject[originalKey] = object[originalKey];
      }
    });
    return Object.assign(omitedObject, rest);
  }
  function render(r, ...args) {
    if (typeof r === "function") {
      return r(...args);
    } else if (typeof r === "string") {
      return createTextVNode(r);
    } else if (typeof r === "number") {
      return createTextVNode(String(r));
    } else {
      return null;
    }
  }
  function ensureValidVNode(vnodes) {
    return vnodes.some((child) => {
      if (!isVNode(child)) {
        return true;
      }
      if (child.type === Comment) {
        return false;
      }
      if (child.type === Fragment && !ensureValidVNode(child.children)) {
        return false;
      }
      return true;
    }) ? vnodes : null;
  }
  function resolveSlot(slot, fallback) {
    return slot && ensureValidVNode(slot()) || fallback();
  }
  function resolveWrappedSlot(slot, wrapper) {
    const children = slot && ensureValidVNode(slot());
    return wrapper(children || null);
  }
  function isSlotEmpty(slot) {
    return !(slot && ensureValidVNode(slot()));
  }
  const Wrapper = /* @__PURE__ */ defineComponent({
    render() {
      var _a2, _b;
      return (_b = (_a2 = this.$slots).default) === null || _b === void 0 ? void 0 : _b.call(_a2);
    }
  });
  const configProviderInjectionKey = createInjectionKey("n-config-provider");
  const defaultClsPrefix = "n";
  function useConfig(props = {}, options = {
    defaultBordered: true
  }) {
    const NConfigProvider2 = inject(configProviderInjectionKey, null);
    return {
      // NConfigProvider,
      inlineThemeDisabled: NConfigProvider2 === null || NConfigProvider2 === void 0 ? void 0 : NConfigProvider2.inlineThemeDisabled,
      mergedRtlRef: NConfigProvider2 === null || NConfigProvider2 === void 0 ? void 0 : NConfigProvider2.mergedRtlRef,
      mergedComponentPropsRef: NConfigProvider2 === null || NConfigProvider2 === void 0 ? void 0 : NConfigProvider2.mergedComponentPropsRef,
      mergedBreakpointsRef: NConfigProvider2 === null || NConfigProvider2 === void 0 ? void 0 : NConfigProvider2.mergedBreakpointsRef,
      mergedBorderedRef: computed(() => {
        var _a2, _b;
        const {
          bordered
        } = props;
        if (bordered !== void 0) return bordered;
        return (_b = (_a2 = NConfigProvider2 === null || NConfigProvider2 === void 0 ? void 0 : NConfigProvider2.mergedBorderedRef.value) !== null && _a2 !== void 0 ? _a2 : options.defaultBordered) !== null && _b !== void 0 ? _b : true;
      }),
      mergedClsPrefixRef: NConfigProvider2 ? NConfigProvider2.mergedClsPrefixRef : /* @__PURE__ */ shallowRef(defaultClsPrefix),
      namespaceRef: computed(() => NConfigProvider2 === null || NConfigProvider2 === void 0 ? void 0 : NConfigProvider2.mergedNamespaceRef.value)
    };
  }
  function useThemeClass(componentName, hashRef, cssVarsRef, props) {
    if (!cssVarsRef) throwError("useThemeClass", "cssVarsRef is not passed");
    const NConfigProvider2 = inject(configProviderInjectionKey, null);
    const mergedThemeHashRef = NConfigProvider2 === null || NConfigProvider2 === void 0 ? void 0 : NConfigProvider2.mergedThemeHashRef;
    const styleMountTarget = NConfigProvider2 === null || NConfigProvider2 === void 0 ? void 0 : NConfigProvider2.styleMountTarget;
    const themeClassRef = /* @__PURE__ */ ref("");
    const ssrAdapter2 = useSsrAdapter();
    let renderCallback;
    const hashClassPrefix = `__${componentName}`;
    const mountStyle = () => {
      let finalThemeHash = hashClassPrefix;
      const hashValue = hashRef ? hashRef.value : void 0;
      const themeHash = mergedThemeHashRef === null || mergedThemeHashRef === void 0 ? void 0 : mergedThemeHashRef.value;
      if (themeHash) finalThemeHash += `-${themeHash}`;
      if (hashValue) finalThemeHash += `-${hashValue}`;
      const {
        themeOverrides,
        builtinThemeOverrides
      } = props;
      if (themeOverrides) {
        finalThemeHash += `-${murmur2(JSON.stringify(themeOverrides))}`;
      }
      if (builtinThemeOverrides) {
        finalThemeHash += `-${murmur2(JSON.stringify(builtinThemeOverrides))}`;
      }
      themeClassRef.value = finalThemeHash;
      renderCallback = () => {
        const cssVars = cssVarsRef.value;
        let style2 = "";
        for (const key in cssVars) {
          style2 += `${key}: ${cssVars[key]};`;
        }
        c$1(`.${finalThemeHash}`, style2).mount({
          id: finalThemeHash,
          ssr: ssrAdapter2,
          parent: styleMountTarget
        });
        renderCallback = void 0;
      };
    };
    watchEffect(() => {
      mountStyle();
    });
    return {
      themeClass: themeClassRef,
      onRender: () => {
        renderCallback === null || renderCallback === void 0 ? void 0 : renderCallback();
      }
    };
  }
  const formItemInjectionKey = createInjectionKey("n-form-item");
  function useFormItem(props, {
    defaultSize = "medium",
    mergedSize,
    mergedDisabled
  } = {}) {
    const NFormItem = inject(formItemInjectionKey, null);
    provide(formItemInjectionKey, null);
    const mergedSizeRef = computed(mergedSize ? () => mergedSize(NFormItem) : () => {
      const {
        size: size2
      } = props;
      if (size2) return size2;
      if (NFormItem) {
        const {
          mergedSize: mergedSize2
        } = NFormItem;
        if (mergedSize2.value !== void 0) {
          return mergedSize2.value;
        }
      }
      return defaultSize;
    });
    const mergedDisabledRef = computed(mergedDisabled ? () => mergedDisabled(NFormItem) : () => {
      const {
        disabled
      } = props;
      if (disabled !== void 0) {
        return disabled;
      }
      if (NFormItem) {
        return NFormItem.disabled.value;
      }
      return false;
    });
    const mergedStatusRef = computed(() => {
      const {
        status
      } = props;
      if (status) return status;
      return NFormItem === null || NFormItem === void 0 ? void 0 : NFormItem.mergedValidationStatus.value;
    });
    onBeforeUnmount(() => {
      if (NFormItem) {
        NFormItem.restoreValidation();
      }
    });
    return {
      mergedSizeRef,
      mergedDisabledRef,
      mergedStatusRef,
      nTriggerFormBlur() {
        if (NFormItem) {
          NFormItem.handleContentBlur();
        }
      },
      nTriggerFormChange() {
        if (NFormItem) {
          NFormItem.handleContentChange();
        }
      },
      nTriggerFormFocus() {
        if (NFormItem) {
          NFormItem.handleContentFocus();
        }
      },
      nTriggerFormInput() {
        if (NFormItem) {
          NFormItem.handleContentInput();
        }
      }
    };
  }
  const enUS$1 = {
    name: "en-US",
    global: {
      undo: "Undo",
      redo: "Redo",
      confirm: "Confirm",
      clear: "Clear"
    },
    Popconfirm: {
      positiveText: "Confirm",
      negativeText: "Cancel"
    },
    Cascader: {
      placeholder: "Please Select",
      loading: "Loading",
      loadingRequiredMessage: (label) => `Please load all ${label}'s descendants before checking it.`
    },
    Time: {
      dateFormat: "yyyy-MM-dd",
      dateTimeFormat: "yyyy-MM-dd HH:mm:ss"
    },
    DatePicker: {
      yearFormat: "yyyy",
      monthFormat: "MMM",
      dayFormat: "eeeeee",
      yearTypeFormat: "yyyy",
      monthTypeFormat: "yyyy-MM",
      dateFormat: "yyyy-MM-dd",
      dateTimeFormat: "yyyy-MM-dd HH:mm:ss",
      quarterFormat: "yyyy-qqq",
      weekFormat: "YYYY-w",
      clear: "Clear",
      now: "Now",
      confirm: "Confirm",
      selectTime: "Select Time",
      selectDate: "Select Date",
      datePlaceholder: "Select Date",
      datetimePlaceholder: "Select Date and Time",
      monthPlaceholder: "Select Month",
      yearPlaceholder: "Select Year",
      quarterPlaceholder: "Select Quarter",
      weekPlaceholder: "Select Week",
      startDatePlaceholder: "Start Date",
      endDatePlaceholder: "End Date",
      startDatetimePlaceholder: "Start Date and Time",
      endDatetimePlaceholder: "End Date and Time",
      startMonthPlaceholder: "Start Month",
      endMonthPlaceholder: "End Month",
      monthBeforeYear: true,
      firstDayOfWeek: 6,
      today: "Today"
    },
    DataTable: {
      checkTableAll: "Select all in the table",
      uncheckTableAll: "Unselect all in the table",
      confirm: "Confirm",
      clear: "Clear"
    },
    LegacyTransfer: {
      sourceTitle: "Source",
      targetTitle: "Target"
    },
    Transfer: {
      selectAll: "Select all",
      unselectAll: "Unselect all",
      clearAll: "Clear",
      total: (num) => `Total ${num} items`,
      selected: (num) => `${num} items selected`
    },
    Empty: {
      description: "No Data"
    },
    Select: {
      placeholder: "Please Select"
    },
    TimePicker: {
      placeholder: "Select Time",
      positiveText: "OK",
      negativeText: "Cancel",
      now: "Now",
      clear: "Clear"
    },
    Pagination: {
      goto: "Goto",
      selectionSuffix: "page"
    },
    DynamicTags: {
      add: "Add"
    },
    Log: {
      loading: "Loading"
    },
    Input: {
      placeholder: "Please Input"
    },
    InputNumber: {
      placeholder: "Please Input"
    },
    DynamicInput: {
      create: "Create"
    },
    ThemeEditor: {
      title: "Theme Editor",
      clearAllVars: "Clear All Variables",
      clearSearch: "Clear Search",
      filterCompName: "Filter Component Name",
      filterVarName: "Filter Variable Name",
      import: "Import",
      export: "Export",
      restore: "Reset to Default"
    },
    Image: {
      tipPrevious: "Previous picture (←)",
      tipNext: "Next picture (→)",
      tipCounterclockwise: "Counterclockwise",
      tipClockwise: "Clockwise",
      tipZoomOut: "Zoom out",
      tipZoomIn: "Zoom in",
      tipDownload: "Download",
      tipClose: "Close (Esc)",
      // TODO: translation
      tipOriginalSize: "Zoom to original size"
    },
    Heatmap: {
      less: "less",
      more: "more",
      monthFormat: "MMM",
      weekdayFormat: "eee"
    }
  };
  function buildFormatLongFn(args) {
    return (options = {}) => {
      const width = options.width ? String(options.width) : args.defaultWidth;
      const format = args.formats[width] || args.formats[args.defaultWidth];
      return format;
    };
  }
  function buildLocalizeFn(args) {
    return (value, options) => {
      const context = (options == null ? void 0 : options.context) ? String(options.context) : "standalone";
      let valuesArray;
      if (context === "formatting" && args.formattingValues) {
        const defaultWidth = args.defaultFormattingWidth || args.defaultWidth;
        const width = (options == null ? void 0 : options.width) ? String(options.width) : defaultWidth;
        valuesArray = args.formattingValues[width] || args.formattingValues[defaultWidth];
      } else {
        const defaultWidth = args.defaultWidth;
        const width = (options == null ? void 0 : options.width) ? String(options.width) : args.defaultWidth;
        valuesArray = args.values[width] || args.values[defaultWidth];
      }
      const index = args.argumentCallback ? args.argumentCallback(value) : value;
      return valuesArray[index];
    };
  }
  function buildMatchFn(args) {
    return (string, options = {}) => {
      const width = options.width;
      const matchPattern = width && args.matchPatterns[width] || args.matchPatterns[args.defaultMatchWidth];
      const matchResult = string.match(matchPattern);
      if (!matchResult) {
        return null;
      }
      const matchedString = matchResult[0];
      const parsePatterns = width && args.parsePatterns[width] || args.parsePatterns[args.defaultParseWidth];
      const key = Array.isArray(parsePatterns) ? findIndex(parsePatterns, (pattern) => pattern.test(matchedString)) : (
        // [TODO] -- I challenge you to fix the type
        findKey(parsePatterns, (pattern) => pattern.test(matchedString))
      );
      let value;
      value = args.valueCallback ? args.valueCallback(key) : key;
      value = options.valueCallback ? (
        // [TODO] -- I challenge you to fix the type
        options.valueCallback(value)
      ) : value;
      const rest = string.slice(matchedString.length);
      return { value, rest };
    };
  }
  function findKey(object, predicate) {
    for (const key in object) {
      if (Object.prototype.hasOwnProperty.call(object, key) && predicate(object[key])) {
        return key;
      }
    }
    return void 0;
  }
  function findIndex(array, predicate) {
    for (let key = 0; key < array.length; key++) {
      if (predicate(array[key])) {
        return key;
      }
    }
    return void 0;
  }
  function buildMatchPatternFn(args) {
    return (string, options = {}) => {
      const matchResult = string.match(args.matchPattern);
      if (!matchResult) return null;
      const matchedString = matchResult[0];
      const parseResult = string.match(args.parsePattern);
      if (!parseResult) return null;
      let value = args.valueCallback ? args.valueCallback(parseResult[0]) : parseResult[0];
      value = options.valueCallback ? options.valueCallback(value) : value;
      const rest = string.slice(matchedString.length);
      return { value, rest };
    };
  }
  const formatDistanceLocale = {
    lessThanXSeconds: {
      one: "less than a second",
      other: "less than {{count}} seconds"
    },
    xSeconds: {
      one: "1 second",
      other: "{{count}} seconds"
    },
    halfAMinute: "half a minute",
    lessThanXMinutes: {
      one: "less than a minute",
      other: "less than {{count}} minutes"
    },
    xMinutes: {
      one: "1 minute",
      other: "{{count}} minutes"
    },
    aboutXHours: {
      one: "about 1 hour",
      other: "about {{count}} hours"
    },
    xHours: {
      one: "1 hour",
      other: "{{count}} hours"
    },
    xDays: {
      one: "1 day",
      other: "{{count}} days"
    },
    aboutXWeeks: {
      one: "about 1 week",
      other: "about {{count}} weeks"
    },
    xWeeks: {
      one: "1 week",
      other: "{{count}} weeks"
    },
    aboutXMonths: {
      one: "about 1 month",
      other: "about {{count}} months"
    },
    xMonths: {
      one: "1 month",
      other: "{{count}} months"
    },
    aboutXYears: {
      one: "about 1 year",
      other: "about {{count}} years"
    },
    xYears: {
      one: "1 year",
      other: "{{count}} years"
    },
    overXYears: {
      one: "over 1 year",
      other: "over {{count}} years"
    },
    almostXYears: {
      one: "almost 1 year",
      other: "almost {{count}} years"
    }
  };
  const formatDistance = (token, count, options) => {
    let result;
    const tokenValue = formatDistanceLocale[token];
    if (typeof tokenValue === "string") {
      result = tokenValue;
    } else if (count === 1) {
      result = tokenValue.one;
    } else {
      result = tokenValue.other.replace("{{count}}", count.toString());
    }
    if (options == null ? void 0 : options.addSuffix) {
      if (options.comparison && options.comparison > 0) {
        return "in " + result;
      } else {
        return result + " ago";
      }
    }
    return result;
  };
  const formatRelativeLocale = {
    lastWeek: "'last' eeee 'at' p",
    yesterday: "'yesterday at' p",
    today: "'today at' p",
    tomorrow: "'tomorrow at' p",
    nextWeek: "eeee 'at' p",
    other: "P"
  };
  const formatRelative = (token, _date, _baseDate, _options) => formatRelativeLocale[token];
  const eraValues = {
    narrow: ["B", "A"],
    abbreviated: ["BC", "AD"],
    wide: ["Before Christ", "Anno Domini"]
  };
  const quarterValues = {
    narrow: ["1", "2", "3", "4"],
    abbreviated: ["Q1", "Q2", "Q3", "Q4"],
    wide: ["1st quarter", "2nd quarter", "3rd quarter", "4th quarter"]
  };
  const monthValues = {
    narrow: ["J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D"],
    abbreviated: [
      "Jan",
      "Feb",
      "Mar",
      "Apr",
      "May",
      "Jun",
      "Jul",
      "Aug",
      "Sep",
      "Oct",
      "Nov",
      "Dec"
    ],
    wide: [
      "January",
      "February",
      "March",
      "April",
      "May",
      "June",
      "July",
      "August",
      "September",
      "October",
      "November",
      "December"
    ]
  };
  const dayValues = {
    narrow: ["S", "M", "T", "W", "T", "F", "S"],
    short: ["Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"],
    abbreviated: ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"],
    wide: [
      "Sunday",
      "Monday",
      "Tuesday",
      "Wednesday",
      "Thursday",
      "Friday",
      "Saturday"
    ]
  };
  const dayPeriodValues = {
    narrow: {
      am: "a",
      pm: "p",
      midnight: "mi",
      noon: "n",
      morning: "morning",
      afternoon: "afternoon",
      evening: "evening",
      night: "night"
    },
    abbreviated: {
      am: "AM",
      pm: "PM",
      midnight: "midnight",
      noon: "noon",
      morning: "morning",
      afternoon: "afternoon",
      evening: "evening",
      night: "night"
    },
    wide: {
      am: "a.m.",
      pm: "p.m.",
      midnight: "midnight",
      noon: "noon",
      morning: "morning",
      afternoon: "afternoon",
      evening: "evening",
      night: "night"
    }
  };
  const formattingDayPeriodValues = {
    narrow: {
      am: "a",
      pm: "p",
      midnight: "mi",
      noon: "n",
      morning: "in the morning",
      afternoon: "in the afternoon",
      evening: "in the evening",
      night: "at night"
    },
    abbreviated: {
      am: "AM",
      pm: "PM",
      midnight: "midnight",
      noon: "noon",
      morning: "in the morning",
      afternoon: "in the afternoon",
      evening: "in the evening",
      night: "at night"
    },
    wide: {
      am: "a.m.",
      pm: "p.m.",
      midnight: "midnight",
      noon: "noon",
      morning: "in the morning",
      afternoon: "in the afternoon",
      evening: "in the evening",
      night: "at night"
    }
  };
  const ordinalNumber = (dirtyNumber, _options) => {
    const number = Number(dirtyNumber);
    const rem100 = number % 100;
    if (rem100 > 20 || rem100 < 10) {
      switch (rem100 % 10) {
        case 1:
          return number + "st";
        case 2:
          return number + "nd";
        case 3:
          return number + "rd";
      }
    }
    return number + "th";
  };
  const localize = {
    ordinalNumber,
    era: buildLocalizeFn({
      values: eraValues,
      defaultWidth: "wide"
    }),
    quarter: buildLocalizeFn({
      values: quarterValues,
      defaultWidth: "wide",
      argumentCallback: (quarter) => quarter - 1
    }),
    month: buildLocalizeFn({
      values: monthValues,
      defaultWidth: "wide"
    }),
    day: buildLocalizeFn({
      values: dayValues,
      defaultWidth: "wide"
    }),
    dayPeriod: buildLocalizeFn({
      values: dayPeriodValues,
      defaultWidth: "wide",
      formattingValues: formattingDayPeriodValues,
      defaultFormattingWidth: "wide"
    })
  };
  const matchOrdinalNumberPattern = /^(\d+)(th|st|nd|rd)?/i;
  const parseOrdinalNumberPattern = /\d+/i;
  const matchEraPatterns = {
    narrow: /^(b|a)/i,
    abbreviated: /^(b\.?\s?c\.?|b\.?\s?c\.?\s?e\.?|a\.?\s?d\.?|c\.?\s?e\.?)/i,
    wide: /^(before christ|before common era|anno domini|common era)/i
  };
  const parseEraPatterns = {
    any: [/^b/i, /^(a|c)/i]
  };
  const matchQuarterPatterns = {
    narrow: /^[1234]/i,
    abbreviated: /^q[1234]/i,
    wide: /^[1234](th|st|nd|rd)? quarter/i
  };
  const parseQuarterPatterns = {
    any: [/1/i, /2/i, /3/i, /4/i]
  };
  const matchMonthPatterns = {
    narrow: /^[jfmasond]/i,
    abbreviated: /^(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)/i,
    wide: /^(january|february|march|april|may|june|july|august|september|october|november|december)/i
  };
  const parseMonthPatterns = {
    narrow: [
      /^j/i,
      /^f/i,
      /^m/i,
      /^a/i,
      /^m/i,
      /^j/i,
      /^j/i,
      /^a/i,
      /^s/i,
      /^o/i,
      /^n/i,
      /^d/i
    ],
    any: [
      /^ja/i,
      /^f/i,
      /^mar/i,
      /^ap/i,
      /^may/i,
      /^jun/i,
      /^jul/i,
      /^au/i,
      /^s/i,
      /^o/i,
      /^n/i,
      /^d/i
    ]
  };
  const matchDayPatterns = {
    narrow: /^[smtwf]/i,
    short: /^(su|mo|tu|we|th|fr|sa)/i,
    abbreviated: /^(sun|mon|tue|wed|thu|fri|sat)/i,
    wide: /^(sunday|monday|tuesday|wednesday|thursday|friday|saturday)/i
  };
  const parseDayPatterns = {
    narrow: [/^s/i, /^m/i, /^t/i, /^w/i, /^t/i, /^f/i, /^s/i],
    any: [/^su/i, /^m/i, /^tu/i, /^w/i, /^th/i, /^f/i, /^sa/i]
  };
  const matchDayPeriodPatterns = {
    narrow: /^(a|p|mi|n|(in the|at) (morning|afternoon|evening|night))/i,
    any: /^([ap]\.?\s?m\.?|midnight|noon|(in the|at) (morning|afternoon|evening|night))/i
  };
  const parseDayPeriodPatterns = {
    any: {
      am: /^a/i,
      pm: /^p/i,
      midnight: /^mi/i,
      noon: /^no/i,
      morning: /morning/i,
      afternoon: /afternoon/i,
      evening: /evening/i,
      night: /night/i
    }
  };
  const match = {
    ordinalNumber: buildMatchPatternFn({
      matchPattern: matchOrdinalNumberPattern,
      parsePattern: parseOrdinalNumberPattern,
      valueCallback: (value) => parseInt(value, 10)
    }),
    era: buildMatchFn({
      matchPatterns: matchEraPatterns,
      defaultMatchWidth: "wide",
      parsePatterns: parseEraPatterns,
      defaultParseWidth: "any"
    }),
    quarter: buildMatchFn({
      matchPatterns: matchQuarterPatterns,
      defaultMatchWidth: "wide",
      parsePatterns: parseQuarterPatterns,
      defaultParseWidth: "any",
      valueCallback: (index) => index + 1
    }),
    month: buildMatchFn({
      matchPatterns: matchMonthPatterns,
      defaultMatchWidth: "wide",
      parsePatterns: parseMonthPatterns,
      defaultParseWidth: "any"
    }),
    day: buildMatchFn({
      matchPatterns: matchDayPatterns,
      defaultMatchWidth: "wide",
      parsePatterns: parseDayPatterns,
      defaultParseWidth: "any"
    }),
    dayPeriod: buildMatchFn({
      matchPatterns: matchDayPeriodPatterns,
      defaultMatchWidth: "any",
      parsePatterns: parseDayPeriodPatterns,
      defaultParseWidth: "any"
    })
  };
  const dateFormats = {
    full: "EEEE, MMMM do, y",
    long: "MMMM do, y",
    medium: "MMM d, y",
    short: "MM/dd/yyyy"
  };
  const timeFormats = {
    full: "h:mm:ss a zzzz",
    long: "h:mm:ss a z",
    medium: "h:mm:ss a",
    short: "h:mm a"
  };
  const dateTimeFormats = {
    full: "{{date}} 'at' {{time}}",
    long: "{{date}} 'at' {{time}}",
    medium: "{{date}}, {{time}}",
    short: "{{date}}, {{time}}"
  };
  const formatLong = {
    date: buildFormatLongFn({
      formats: dateFormats,
      defaultWidth: "full"
    }),
    time: buildFormatLongFn({
      formats: timeFormats,
      defaultWidth: "full"
    }),
    dateTime: buildFormatLongFn({
      formats: dateTimeFormats,
      defaultWidth: "full"
    })
  };
  const enUS = {
    code: "en-US",
    formatDistance,
    formatLong,
    formatRelative,
    localize,
    match,
    options: {
      weekStartsOn: 0,
      firstWeekContainsDate: 1
    }
  };
  const dateEnUs = {
    name: "en-US",
    locale: enUS
  };
  var freeGlobal = typeof global == "object" && global && global.Object === Object && global;
  var freeSelf = typeof self == "object" && self && self.Object === Object && self;
  var root = freeGlobal || freeSelf || Function("return this")();
  var Symbol$1 = root.Symbol;
  var objectProto$e = Object.prototype;
  var hasOwnProperty$b = objectProto$e.hasOwnProperty;
  var nativeObjectToString$1 = objectProto$e.toString;
  var symToStringTag$1 = Symbol$1 ? Symbol$1.toStringTag : void 0;
  function getRawTag(value) {
    var isOwn = hasOwnProperty$b.call(value, symToStringTag$1), tag = value[symToStringTag$1];
    try {
      value[symToStringTag$1] = void 0;
      var unmasked = true;
    } catch (e) {
    }
    var result = nativeObjectToString$1.call(value);
    if (unmasked) {
      if (isOwn) {
        value[symToStringTag$1] = tag;
      } else {
        delete value[symToStringTag$1];
      }
    }
    return result;
  }
  var objectProto$d = Object.prototype;
  var nativeObjectToString = objectProto$d.toString;
  function objectToString(value) {
    return nativeObjectToString.call(value);
  }
  var nullTag = "[object Null]", undefinedTag = "[object Undefined]";
  var symToStringTag = Symbol$1 ? Symbol$1.toStringTag : void 0;
  function baseGetTag(value) {
    if (value == null) {
      return value === void 0 ? undefinedTag : nullTag;
    }
    return symToStringTag && symToStringTag in Object(value) ? getRawTag(value) : objectToString(value);
  }
  function isObjectLike(value) {
    return value != null && typeof value == "object";
  }
  var symbolTag$1 = "[object Symbol]";
  function isSymbol(value) {
    return typeof value == "symbol" || isObjectLike(value) && baseGetTag(value) == symbolTag$1;
  }
  function arrayMap(array, iteratee) {
    var index = -1, length = array == null ? 0 : array.length, result = Array(length);
    while (++index < length) {
      result[index] = iteratee(array[index], index, array);
    }
    return result;
  }
  var isArray = Array.isArray;
  var symbolProto$1 = Symbol$1 ? Symbol$1.prototype : void 0, symbolToString = symbolProto$1 ? symbolProto$1.toString : void 0;
  function baseToString(value) {
    if (typeof value == "string") {
      return value;
    }
    if (isArray(value)) {
      return arrayMap(value, baseToString) + "";
    }
    if (isSymbol(value)) {
      return symbolToString ? symbolToString.call(value) : "";
    }
    var result = value + "";
    return result == "0" && 1 / value == -Infinity ? "-0" : result;
  }
  function isObject(value) {
    var type = typeof value;
    return value != null && (type == "object" || type == "function");
  }
  function identity(value) {
    return value;
  }
  var asyncTag = "[object AsyncFunction]", funcTag$1 = "[object Function]", genTag = "[object GeneratorFunction]", proxyTag = "[object Proxy]";
  function isFunction(value) {
    if (!isObject(value)) {
      return false;
    }
    var tag = baseGetTag(value);
    return tag == funcTag$1 || tag == genTag || tag == asyncTag || tag == proxyTag;
  }
  var coreJsData = root["__core-js_shared__"];
  var maskSrcKey = function() {
    var uid2 = /[^.]+$/.exec(coreJsData && coreJsData.keys && coreJsData.keys.IE_PROTO || "");
    return uid2 ? "Symbol(src)_1." + uid2 : "";
  }();
  function isMasked(func) {
    return !!maskSrcKey && maskSrcKey in func;
  }
  var funcProto$2 = Function.prototype;
  var funcToString$2 = funcProto$2.toString;
  function toSource(func) {
    if (func != null) {
      try {
        return funcToString$2.call(func);
      } catch (e) {
      }
      try {
        return func + "";
      } catch (e) {
      }
    }
    return "";
  }
  var reRegExpChar = /[\\^$.*+?()[\]{}|]/g;
  var reIsHostCtor = /^\[object .+?Constructor\]$/;
  var funcProto$1 = Function.prototype, objectProto$c = Object.prototype;
  var funcToString$1 = funcProto$1.toString;
  var hasOwnProperty$a = objectProto$c.hasOwnProperty;
  var reIsNative = RegExp(
    "^" + funcToString$1.call(hasOwnProperty$a).replace(reRegExpChar, "\\$&").replace(/hasOwnProperty|(function).*?(?=\\\()| for .+?(?=\\\])/g, "$1.*?") + "$"
  );
  function baseIsNative(value) {
    if (!isObject(value) || isMasked(value)) {
      return false;
    }
    var pattern = isFunction(value) ? reIsNative : reIsHostCtor;
    return pattern.test(toSource(value));
  }
  function getValue(object, key) {
    return object == null ? void 0 : object[key];
  }
  function getNative(object, key) {
    var value = getValue(object, key);
    return baseIsNative(value) ? value : void 0;
  }
  var WeakMap$1 = getNative(root, "WeakMap");
  var objectCreate = Object.create;
  var baseCreate = /* @__PURE__ */ function() {
    function object() {
    }
    return function(proto) {
      if (!isObject(proto)) {
        return {};
      }
      if (objectCreate) {
        return objectCreate(proto);
      }
      object.prototype = proto;
      var result = new object();
      object.prototype = void 0;
      return result;
    };
  }();
  function apply(func, thisArg, args) {
    switch (args.length) {
      case 0:
        return func.call(thisArg);
      case 1:
        return func.call(thisArg, args[0]);
      case 2:
        return func.call(thisArg, args[0], args[1]);
      case 3:
        return func.call(thisArg, args[0], args[1], args[2]);
    }
    return func.apply(thisArg, args);
  }
  function copyArray(source, array) {
    var index = -1, length = source.length;
    array || (array = Array(length));
    while (++index < length) {
      array[index] = source[index];
    }
    return array;
  }
  var HOT_COUNT = 800, HOT_SPAN = 16;
  var nativeNow = Date.now;
  function shortOut(func) {
    var count = 0, lastCalled = 0;
    return function() {
      var stamp = nativeNow(), remaining = HOT_SPAN - (stamp - lastCalled);
      lastCalled = stamp;
      if (remaining > 0) {
        if (++count >= HOT_COUNT) {
          return arguments[0];
        }
      } else {
        count = 0;
      }
      return func.apply(void 0, arguments);
    };
  }
  function constant(value) {
    return function() {
      return value;
    };
  }
  var defineProperty = function() {
    try {
      var func = getNative(Object, "defineProperty");
      func({}, "", {});
      return func;
    } catch (e) {
    }
  }();
  var baseSetToString = !defineProperty ? identity : function(func, string) {
    return defineProperty(func, "toString", {
      "configurable": true,
      "enumerable": false,
      "value": constant(string),
      "writable": true
    });
  };
  var setToString = shortOut(baseSetToString);
  var MAX_SAFE_INTEGER$1 = 9007199254740991;
  var reIsUint = /^(?:0|[1-9]\d*)$/;
  function isIndex(value, length) {
    var type = typeof value;
    length = length == null ? MAX_SAFE_INTEGER$1 : length;
    return !!length && (type == "number" || type != "symbol" && reIsUint.test(value)) && (value > -1 && value % 1 == 0 && value < length);
  }
  function baseAssignValue(object, key, value) {
    if (key == "__proto__" && defineProperty) {
      defineProperty(object, key, {
        "configurable": true,
        "enumerable": true,
        "value": value,
        "writable": true
      });
    } else {
      object[key] = value;
    }
  }
  function eq(value, other) {
    return value === other || value !== value && other !== other;
  }
  var objectProto$b = Object.prototype;
  var hasOwnProperty$9 = objectProto$b.hasOwnProperty;
  function assignValue(object, key, value) {
    var objValue = object[key];
    if (!(hasOwnProperty$9.call(object, key) && eq(objValue, value)) || value === void 0 && !(key in object)) {
      baseAssignValue(object, key, value);
    }
  }
  function copyObject(source, props, object, customizer) {
    var isNew = !object;
    object || (object = {});
    var index = -1, length = props.length;
    while (++index < length) {
      var key = props[index];
      var newValue = void 0;
      if (newValue === void 0) {
        newValue = source[key];
      }
      if (isNew) {
        baseAssignValue(object, key, newValue);
      } else {
        assignValue(object, key, newValue);
      }
    }
    return object;
  }
  var nativeMax = Math.max;
  function overRest(func, start, transform) {
    start = nativeMax(start === void 0 ? func.length - 1 : start, 0);
    return function() {
      var args = arguments, index = -1, length = nativeMax(args.length - start, 0), array = Array(length);
      while (++index < length) {
        array[index] = args[start + index];
      }
      index = -1;
      var otherArgs = Array(start + 1);
      while (++index < start) {
        otherArgs[index] = args[index];
      }
      otherArgs[start] = transform(array);
      return apply(func, this, otherArgs);
    };
  }
  function baseRest(func, start) {
    return setToString(overRest(func, start, identity), func + "");
  }
  var MAX_SAFE_INTEGER = 9007199254740991;
  function isLength(value) {
    return typeof value == "number" && value > -1 && value % 1 == 0 && value <= MAX_SAFE_INTEGER;
  }
  function isArrayLike(value) {
    return value != null && isLength(value.length) && !isFunction(value);
  }
  function isIterateeCall(value, index, object) {
    if (!isObject(object)) {
      return false;
    }
    var type = typeof index;
    if (type == "number" ? isArrayLike(object) && isIndex(index, object.length) : type == "string" && index in object) {
      return eq(object[index], value);
    }
    return false;
  }
  function createAssigner(assigner) {
    return baseRest(function(object, sources) {
      var index = -1, length = sources.length, customizer = length > 1 ? sources[length - 1] : void 0, guard = length > 2 ? sources[2] : void 0;
      customizer = assigner.length > 3 && typeof customizer == "function" ? (length--, customizer) : void 0;
      if (guard && isIterateeCall(sources[0], sources[1], guard)) {
        customizer = length < 3 ? void 0 : customizer;
        length = 1;
      }
      object = Object(object);
      while (++index < length) {
        var source = sources[index];
        if (source) {
          assigner(object, source, index, customizer);
        }
      }
      return object;
    });
  }
  var objectProto$a = Object.prototype;
  function isPrototype(value) {
    var Ctor = value && value.constructor, proto = typeof Ctor == "function" && Ctor.prototype || objectProto$a;
    return value === proto;
  }
  function baseTimes(n, iteratee) {
    var index = -1, result = Array(n);
    while (++index < n) {
      result[index] = iteratee(index);
    }
    return result;
  }
  var argsTag$2 = "[object Arguments]";
  function baseIsArguments(value) {
    return isObjectLike(value) && baseGetTag(value) == argsTag$2;
  }
  var objectProto$9 = Object.prototype;
  var hasOwnProperty$8 = objectProto$9.hasOwnProperty;
  var propertyIsEnumerable$1 = objectProto$9.propertyIsEnumerable;
  var isArguments = baseIsArguments(/* @__PURE__ */ function() {
    return arguments;
  }()) ? baseIsArguments : function(value) {
    return isObjectLike(value) && hasOwnProperty$8.call(value, "callee") && !propertyIsEnumerable$1.call(value, "callee");
  };
  function stubFalse() {
    return false;
  }
  var freeExports$2 = typeof exports == "object" && exports && !exports.nodeType && exports;
  var freeModule$2 = freeExports$2 && typeof module == "object" && module && !module.nodeType && module;
  var moduleExports$2 = freeModule$2 && freeModule$2.exports === freeExports$2;
  var Buffer$1 = moduleExports$2 ? root.Buffer : void 0;
  var nativeIsBuffer = Buffer$1 ? Buffer$1.isBuffer : void 0;
  var isBuffer = nativeIsBuffer || stubFalse;
  var argsTag$1 = "[object Arguments]", arrayTag$1 = "[object Array]", boolTag$1 = "[object Boolean]", dateTag$1 = "[object Date]", errorTag$1 = "[object Error]", funcTag = "[object Function]", mapTag$2 = "[object Map]", numberTag$1 = "[object Number]", objectTag$3 = "[object Object]", regexpTag$1 = "[object RegExp]", setTag$2 = "[object Set]", stringTag$1 = "[object String]", weakMapTag$1 = "[object WeakMap]";
  var arrayBufferTag$1 = "[object ArrayBuffer]", dataViewTag$2 = "[object DataView]", float32Tag = "[object Float32Array]", float64Tag = "[object Float64Array]", int8Tag = "[object Int8Array]", int16Tag = "[object Int16Array]", int32Tag = "[object Int32Array]", uint8Tag = "[object Uint8Array]", uint8ClampedTag = "[object Uint8ClampedArray]", uint16Tag = "[object Uint16Array]", uint32Tag = "[object Uint32Array]";
  var typedArrayTags = {};
  typedArrayTags[float32Tag] = typedArrayTags[float64Tag] = typedArrayTags[int8Tag] = typedArrayTags[int16Tag] = typedArrayTags[int32Tag] = typedArrayTags[uint8Tag] = typedArrayTags[uint8ClampedTag] = typedArrayTags[uint16Tag] = typedArrayTags[uint32Tag] = true;
  typedArrayTags[argsTag$1] = typedArrayTags[arrayTag$1] = typedArrayTags[arrayBufferTag$1] = typedArrayTags[boolTag$1] = typedArrayTags[dataViewTag$2] = typedArrayTags[dateTag$1] = typedArrayTags[errorTag$1] = typedArrayTags[funcTag] = typedArrayTags[mapTag$2] = typedArrayTags[numberTag$1] = typedArrayTags[objectTag$3] = typedArrayTags[regexpTag$1] = typedArrayTags[setTag$2] = typedArrayTags[stringTag$1] = typedArrayTags[weakMapTag$1] = false;
  function baseIsTypedArray(value) {
    return isObjectLike(value) && isLength(value.length) && !!typedArrayTags[baseGetTag(value)];
  }
  function baseUnary(func) {
    return function(value) {
      return func(value);
    };
  }
  var freeExports$1 = typeof exports == "object" && exports && !exports.nodeType && exports;
  var freeModule$1 = freeExports$1 && typeof module == "object" && module && !module.nodeType && module;
  var moduleExports$1 = freeModule$1 && freeModule$1.exports === freeExports$1;
  var freeProcess = moduleExports$1 && freeGlobal.process;
  var nodeUtil = function() {
    try {
      var types = freeModule$1 && freeModule$1.require && freeModule$1.require("util").types;
      if (types) {
        return types;
      }
      return freeProcess && freeProcess.binding && freeProcess.binding("util");
    } catch (e) {
    }
  }();
  var nodeIsTypedArray = nodeUtil && nodeUtil.isTypedArray;
  var isTypedArray = nodeIsTypedArray ? baseUnary(nodeIsTypedArray) : baseIsTypedArray;
  var objectProto$8 = Object.prototype;
  var hasOwnProperty$7 = objectProto$8.hasOwnProperty;
  function arrayLikeKeys(value, inherited) {
    var isArr = isArray(value), isArg = !isArr && isArguments(value), isBuff = !isArr && !isArg && isBuffer(value), isType = !isArr && !isArg && !isBuff && isTypedArray(value), skipIndexes = isArr || isArg || isBuff || isType, result = skipIndexes ? baseTimes(value.length, String) : [], length = result.length;
    for (var key in value) {
      if ((inherited || hasOwnProperty$7.call(value, key)) && !(skipIndexes && // Safari 9 has enumerable `arguments.length` in strict mode.
      (key == "length" || // Node.js 0.10 has enumerable non-index properties on buffers.
      isBuff && (key == "offset" || key == "parent") || // PhantomJS 2 has enumerable non-index properties on typed arrays.
      isType && (key == "buffer" || key == "byteLength" || key == "byteOffset") || // Skip index properties.
      isIndex(key, length)))) {
        result.push(key);
      }
    }
    return result;
  }
  function overArg(func, transform) {
    return function(arg) {
      return func(transform(arg));
    };
  }
  var nativeKeys = overArg(Object.keys, Object);
  var objectProto$7 = Object.prototype;
  var hasOwnProperty$6 = objectProto$7.hasOwnProperty;
  function baseKeys(object) {
    if (!isPrototype(object)) {
      return nativeKeys(object);
    }
    var result = [];
    for (var key in Object(object)) {
      if (hasOwnProperty$6.call(object, key) && key != "constructor") {
        result.push(key);
      }
    }
    return result;
  }
  function keys(object) {
    return isArrayLike(object) ? arrayLikeKeys(object) : baseKeys(object);
  }
  function nativeKeysIn(object) {
    var result = [];
    if (object != null) {
      for (var key in Object(object)) {
        result.push(key);
      }
    }
    return result;
  }
  var objectProto$6 = Object.prototype;
  var hasOwnProperty$5 = objectProto$6.hasOwnProperty;
  function baseKeysIn(object) {
    if (!isObject(object)) {
      return nativeKeysIn(object);
    }
    var isProto = isPrototype(object), result = [];
    for (var key in object) {
      if (!(key == "constructor" && (isProto || !hasOwnProperty$5.call(object, key)))) {
        result.push(key);
      }
    }
    return result;
  }
  function keysIn(object) {
    return isArrayLike(object) ? arrayLikeKeys(object, true) : baseKeysIn(object);
  }
  var reIsDeepProp = /\.|\[(?:[^[\]]*|(["'])(?:(?!\1)[^\\]|\\.)*?\1)\]/, reIsPlainProp = /^\w*$/;
  function isKey(value, object) {
    if (isArray(value)) {
      return false;
    }
    var type = typeof value;
    if (type == "number" || type == "symbol" || type == "boolean" || value == null || isSymbol(value)) {
      return true;
    }
    return reIsPlainProp.test(value) || !reIsDeepProp.test(value) || object != null && value in Object(object);
  }
  var nativeCreate = getNative(Object, "create");
  function hashClear() {
    this.__data__ = nativeCreate ? nativeCreate(null) : {};
    this.size = 0;
  }
  function hashDelete(key) {
    var result = this.has(key) && delete this.__data__[key];
    this.size -= result ? 1 : 0;
    return result;
  }
  var HASH_UNDEFINED$2 = "__lodash_hash_undefined__";
  var objectProto$5 = Object.prototype;
  var hasOwnProperty$4 = objectProto$5.hasOwnProperty;
  function hashGet(key) {
    var data = this.__data__;
    if (nativeCreate) {
      var result = data[key];
      return result === HASH_UNDEFINED$2 ? void 0 : result;
    }
    return hasOwnProperty$4.call(data, key) ? data[key] : void 0;
  }
  var objectProto$4 = Object.prototype;
  var hasOwnProperty$3 = objectProto$4.hasOwnProperty;
  function hashHas(key) {
    var data = this.__data__;
    return nativeCreate ? data[key] !== void 0 : hasOwnProperty$3.call(data, key);
  }
  var HASH_UNDEFINED$1 = "__lodash_hash_undefined__";
  function hashSet(key, value) {
    var data = this.__data__;
    this.size += this.has(key) ? 0 : 1;
    data[key] = nativeCreate && value === void 0 ? HASH_UNDEFINED$1 : value;
    return this;
  }
  function Hash(entries) {
    var index = -1, length = entries == null ? 0 : entries.length;
    this.clear();
    while (++index < length) {
      var entry = entries[index];
      this.set(entry[0], entry[1]);
    }
  }
  Hash.prototype.clear = hashClear;
  Hash.prototype["delete"] = hashDelete;
  Hash.prototype.get = hashGet;
  Hash.prototype.has = hashHas;
  Hash.prototype.set = hashSet;
  function listCacheClear() {
    this.__data__ = [];
    this.size = 0;
  }
  function assocIndexOf(array, key) {
    var length = array.length;
    while (length--) {
      if (eq(array[length][0], key)) {
        return length;
      }
    }
    return -1;
  }
  var arrayProto = Array.prototype;
  var splice = arrayProto.splice;
  function listCacheDelete(key) {
    var data = this.__data__, index = assocIndexOf(data, key);
    if (index < 0) {
      return false;
    }
    var lastIndex = data.length - 1;
    if (index == lastIndex) {
      data.pop();
    } else {
      splice.call(data, index, 1);
    }
    --this.size;
    return true;
  }
  function listCacheGet(key) {
    var data = this.__data__, index = assocIndexOf(data, key);
    return index < 0 ? void 0 : data[index][1];
  }
  function listCacheHas(key) {
    return assocIndexOf(this.__data__, key) > -1;
  }
  function listCacheSet(key, value) {
    var data = this.__data__, index = assocIndexOf(data, key);
    if (index < 0) {
      ++this.size;
      data.push([key, value]);
    } else {
      data[index][1] = value;
    }
    return this;
  }
  function ListCache(entries) {
    var index = -1, length = entries == null ? 0 : entries.length;
    this.clear();
    while (++index < length) {
      var entry = entries[index];
      this.set(entry[0], entry[1]);
    }
  }
  ListCache.prototype.clear = listCacheClear;
  ListCache.prototype["delete"] = listCacheDelete;
  ListCache.prototype.get = listCacheGet;
  ListCache.prototype.has = listCacheHas;
  ListCache.prototype.set = listCacheSet;
  var Map$1 = getNative(root, "Map");
  function mapCacheClear() {
    this.size = 0;
    this.__data__ = {
      "hash": new Hash(),
      "map": new (Map$1 || ListCache)(),
      "string": new Hash()
    };
  }
  function isKeyable(value) {
    var type = typeof value;
    return type == "string" || type == "number" || type == "symbol" || type == "boolean" ? value !== "__proto__" : value === null;
  }
  function getMapData(map2, key) {
    var data = map2.__data__;
    return isKeyable(key) ? data[typeof key == "string" ? "string" : "hash"] : data.map;
  }
  function mapCacheDelete(key) {
    var result = getMapData(this, key)["delete"](key);
    this.size -= result ? 1 : 0;
    return result;
  }
  function mapCacheGet(key) {
    return getMapData(this, key).get(key);
  }
  function mapCacheHas(key) {
    return getMapData(this, key).has(key);
  }
  function mapCacheSet(key, value) {
    var data = getMapData(this, key), size2 = data.size;
    data.set(key, value);
    this.size += data.size == size2 ? 0 : 1;
    return this;
  }
  function MapCache(entries) {
    var index = -1, length = entries == null ? 0 : entries.length;
    this.clear();
    while (++index < length) {
      var entry = entries[index];
      this.set(entry[0], entry[1]);
    }
  }
  MapCache.prototype.clear = mapCacheClear;
  MapCache.prototype["delete"] = mapCacheDelete;
  MapCache.prototype.get = mapCacheGet;
  MapCache.prototype.has = mapCacheHas;
  MapCache.prototype.set = mapCacheSet;
  var FUNC_ERROR_TEXT = "Expected a function";
  function memoize(func, resolver) {
    if (typeof func != "function" || resolver != null && typeof resolver != "function") {
      throw new TypeError(FUNC_ERROR_TEXT);
    }
    var memoized = function() {
      var args = arguments, key = resolver ? resolver.apply(this, args) : args[0], cache2 = memoized.cache;
      if (cache2.has(key)) {
        return cache2.get(key);
      }
      var result = func.apply(this, args);
      memoized.cache = cache2.set(key, result) || cache2;
      return result;
    };
    memoized.cache = new (memoize.Cache || MapCache)();
    return memoized;
  }
  memoize.Cache = MapCache;
  var MAX_MEMOIZE_SIZE = 500;
  function memoizeCapped(func) {
    var result = memoize(func, function(key) {
      if (cache2.size === MAX_MEMOIZE_SIZE) {
        cache2.clear();
      }
      return key;
    });
    var cache2 = result.cache;
    return result;
  }
  var rePropName = /[^.[\]]+|\[(?:(-?\d+(?:\.\d+)?)|(["'])((?:(?!\2)[^\\]|\\.)*?)\2)\]|(?=(?:\.|\[\])(?:\.|\[\]|$))/g;
  var reEscapeChar = /\\(\\)?/g;
  var stringToPath = memoizeCapped(function(string) {
    var result = [];
    if (string.charCodeAt(0) === 46) {
      result.push("");
    }
    string.replace(rePropName, function(match2, number, quote, subString) {
      result.push(quote ? subString.replace(reEscapeChar, "$1") : number || match2);
    });
    return result;
  });
  function toString(value) {
    return value == null ? "" : baseToString(value);
  }
  function castPath(value, object) {
    if (isArray(value)) {
      return value;
    }
    return isKey(value, object) ? [value] : stringToPath(toString(value));
  }
  function toKey(value) {
    if (typeof value == "string" || isSymbol(value)) {
      return value;
    }
    var result = value + "";
    return result == "0" && 1 / value == -Infinity ? "-0" : result;
  }
  function baseGet(object, path) {
    path = castPath(path, object);
    var index = 0, length = path.length;
    while (object != null && index < length) {
      object = object[toKey(path[index++])];
    }
    return index && index == length ? object : void 0;
  }
  function get(object, path, defaultValue) {
    var result = object == null ? void 0 : baseGet(object, path);
    return result === void 0 ? defaultValue : result;
  }
  function arrayPush(array, values) {
    var index = -1, length = values.length, offset = array.length;
    while (++index < length) {
      array[offset + index] = values[index];
    }
    return array;
  }
  var getPrototype = overArg(Object.getPrototypeOf, Object);
  var objectTag$2 = "[object Object]";
  var funcProto = Function.prototype, objectProto$3 = Object.prototype;
  var funcToString = funcProto.toString;
  var hasOwnProperty$2 = objectProto$3.hasOwnProperty;
  var objectCtorString = funcToString.call(Object);
  function isPlainObject(value) {
    if (!isObjectLike(value) || baseGetTag(value) != objectTag$2) {
      return false;
    }
    var proto = getPrototype(value);
    if (proto === null) {
      return true;
    }
    var Ctor = hasOwnProperty$2.call(proto, "constructor") && proto.constructor;
    return typeof Ctor == "function" && Ctor instanceof Ctor && funcToString.call(Ctor) == objectCtorString;
  }
  function baseSlice(array, start, end) {
    var index = -1, length = array.length;
    if (start < 0) {
      start = -start > length ? 0 : length + start;
    }
    end = end > length ? length : end;
    if (end < 0) {
      end += length;
    }
    length = start > end ? 0 : end - start >>> 0;
    start >>>= 0;
    var result = Array(length);
    while (++index < length) {
      result[index] = array[index + start];
    }
    return result;
  }
  function castSlice(array, start, end) {
    var length = array.length;
    end = end === void 0 ? length : end;
    return !start && end >= length ? array : baseSlice(array, start, end);
  }
  var rsAstralRange$1 = "\\ud800-\\udfff", rsComboMarksRange$1 = "\\u0300-\\u036f", reComboHalfMarksRange$1 = "\\ufe20-\\ufe2f", rsComboSymbolsRange$1 = "\\u20d0-\\u20ff", rsComboRange$1 = rsComboMarksRange$1 + reComboHalfMarksRange$1 + rsComboSymbolsRange$1, rsVarRange$1 = "\\ufe0e\\ufe0f";
  var rsZWJ$1 = "\\u200d";
  var reHasUnicode = RegExp("[" + rsZWJ$1 + rsAstralRange$1 + rsComboRange$1 + rsVarRange$1 + "]");
  function hasUnicode(string) {
    return reHasUnicode.test(string);
  }
  function asciiToArray(string) {
    return string.split("");
  }
  var rsAstralRange = "\\ud800-\\udfff", rsComboMarksRange = "\\u0300-\\u036f", reComboHalfMarksRange = "\\ufe20-\\ufe2f", rsComboSymbolsRange = "\\u20d0-\\u20ff", rsComboRange = rsComboMarksRange + reComboHalfMarksRange + rsComboSymbolsRange, rsVarRange = "\\ufe0e\\ufe0f";
  var rsAstral = "[" + rsAstralRange + "]", rsCombo = "[" + rsComboRange + "]", rsFitz = "\\ud83c[\\udffb-\\udfff]", rsModifier = "(?:" + rsCombo + "|" + rsFitz + ")", rsNonAstral = "[^" + rsAstralRange + "]", rsRegional = "(?:\\ud83c[\\udde6-\\uddff]){2}", rsSurrPair = "[\\ud800-\\udbff][\\udc00-\\udfff]", rsZWJ = "\\u200d";
  var reOptMod = rsModifier + "?", rsOptVar = "[" + rsVarRange + "]?", rsOptJoin = "(?:" + rsZWJ + "(?:" + [rsNonAstral, rsRegional, rsSurrPair].join("|") + ")" + rsOptVar + reOptMod + ")*", rsSeq = rsOptVar + reOptMod + rsOptJoin, rsSymbol = "(?:" + [rsNonAstral + rsCombo + "?", rsCombo, rsRegional, rsSurrPair, rsAstral].join("|") + ")";
  var reUnicode = RegExp(rsFitz + "(?=" + rsFitz + ")|" + rsSymbol + rsSeq, "g");
  function unicodeToArray(string) {
    return string.match(reUnicode) || [];
  }
  function stringToArray(string) {
    return hasUnicode(string) ? unicodeToArray(string) : asciiToArray(string);
  }
  function createCaseFirst(methodName) {
    return function(string) {
      string = toString(string);
      var strSymbols = hasUnicode(string) ? stringToArray(string) : void 0;
      var chr = strSymbols ? strSymbols[0] : string.charAt(0);
      var trailing = strSymbols ? castSlice(strSymbols, 1).join("") : string.slice(1);
      return chr[methodName]() + trailing;
    };
  }
  var upperFirst = createCaseFirst("toUpperCase");
  function stackClear() {
    this.__data__ = new ListCache();
    this.size = 0;
  }
  function stackDelete(key) {
    var data = this.__data__, result = data["delete"](key);
    this.size = data.size;
    return result;
  }
  function stackGet(key) {
    return this.__data__.get(key);
  }
  function stackHas(key) {
    return this.__data__.has(key);
  }
  var LARGE_ARRAY_SIZE = 200;
  function stackSet(key, value) {
    var data = this.__data__;
    if (data instanceof ListCache) {
      var pairs = data.__data__;
      if (!Map$1 || pairs.length < LARGE_ARRAY_SIZE - 1) {
        pairs.push([key, value]);
        this.size = ++data.size;
        return this;
      }
      data = this.__data__ = new MapCache(pairs);
    }
    data.set(key, value);
    this.size = data.size;
    return this;
  }
  function Stack(entries) {
    var data = this.__data__ = new ListCache(entries);
    this.size = data.size;
  }
  Stack.prototype.clear = stackClear;
  Stack.prototype["delete"] = stackDelete;
  Stack.prototype.get = stackGet;
  Stack.prototype.has = stackHas;
  Stack.prototype.set = stackSet;
  var freeExports = typeof exports == "object" && exports && !exports.nodeType && exports;
  var freeModule = freeExports && typeof module == "object" && module && !module.nodeType && module;
  var moduleExports = freeModule && freeModule.exports === freeExports;
  var Buffer = moduleExports ? root.Buffer : void 0;
  Buffer ? Buffer.allocUnsafe : void 0;
  function cloneBuffer(buffer, isDeep) {
    {
      return buffer.slice();
    }
  }
  function arrayFilter(array, predicate) {
    var index = -1, length = array == null ? 0 : array.length, resIndex = 0, result = [];
    while (++index < length) {
      var value = array[index];
      if (predicate(value, index, array)) {
        result[resIndex++] = value;
      }
    }
    return result;
  }
  function stubArray() {
    return [];
  }
  var objectProto$2 = Object.prototype;
  var propertyIsEnumerable = objectProto$2.propertyIsEnumerable;
  var nativeGetSymbols = Object.getOwnPropertySymbols;
  var getSymbols = !nativeGetSymbols ? stubArray : function(object) {
    if (object == null) {
      return [];
    }
    object = Object(object);
    return arrayFilter(nativeGetSymbols(object), function(symbol) {
      return propertyIsEnumerable.call(object, symbol);
    });
  };
  function baseGetAllKeys(object, keysFunc, symbolsFunc) {
    var result = keysFunc(object);
    return isArray(object) ? result : arrayPush(result, symbolsFunc(object));
  }
  function getAllKeys(object) {
    return baseGetAllKeys(object, keys, getSymbols);
  }
  var DataView$2 = getNative(root, "DataView");
  var Promise$1 = getNative(root, "Promise");
  var Set$1 = getNative(root, "Set");
  var mapTag$1 = "[object Map]", objectTag$1 = "[object Object]", promiseTag = "[object Promise]", setTag$1 = "[object Set]", weakMapTag = "[object WeakMap]";
  var dataViewTag$1 = "[object DataView]";
  var dataViewCtorString = toSource(DataView$2), mapCtorString = toSource(Map$1), promiseCtorString = toSource(Promise$1), setCtorString = toSource(Set$1), weakMapCtorString = toSource(WeakMap$1);
  var getTag = baseGetTag;
  if (DataView$2 && getTag(new DataView$2(new ArrayBuffer(1))) != dataViewTag$1 || Map$1 && getTag(new Map$1()) != mapTag$1 || Promise$1 && getTag(Promise$1.resolve()) != promiseTag || Set$1 && getTag(new Set$1()) != setTag$1 || WeakMap$1 && getTag(new WeakMap$1()) != weakMapTag) {
    getTag = function(value) {
      var result = baseGetTag(value), Ctor = result == objectTag$1 ? value.constructor : void 0, ctorString = Ctor ? toSource(Ctor) : "";
      if (ctorString) {
        switch (ctorString) {
          case dataViewCtorString:
            return dataViewTag$1;
          case mapCtorString:
            return mapTag$1;
          case promiseCtorString:
            return promiseTag;
          case setCtorString:
            return setTag$1;
          case weakMapCtorString:
            return weakMapTag;
        }
      }
      return result;
    };
  }
  var Uint8Array = root.Uint8Array;
  function cloneArrayBuffer(arrayBuffer) {
    var result = new arrayBuffer.constructor(arrayBuffer.byteLength);
    new Uint8Array(result).set(new Uint8Array(arrayBuffer));
    return result;
  }
  function cloneTypedArray(typedArray, isDeep) {
    var buffer = cloneArrayBuffer(typedArray.buffer);
    return new typedArray.constructor(buffer, typedArray.byteOffset, typedArray.length);
  }
  function initCloneObject(object) {
    return typeof object.constructor == "function" && !isPrototype(object) ? baseCreate(getPrototype(object)) : {};
  }
  var HASH_UNDEFINED = "__lodash_hash_undefined__";
  function setCacheAdd(value) {
    this.__data__.set(value, HASH_UNDEFINED);
    return this;
  }
  function setCacheHas(value) {
    return this.__data__.has(value);
  }
  function SetCache(values) {
    var index = -1, length = values == null ? 0 : values.length;
    this.__data__ = new MapCache();
    while (++index < length) {
      this.add(values[index]);
    }
  }
  SetCache.prototype.add = SetCache.prototype.push = setCacheAdd;
  SetCache.prototype.has = setCacheHas;
  function arraySome(array, predicate) {
    var index = -1, length = array == null ? 0 : array.length;
    while (++index < length) {
      if (predicate(array[index], index, array)) {
        return true;
      }
    }
    return false;
  }
  function cacheHas(cache2, key) {
    return cache2.has(key);
  }
  var COMPARE_PARTIAL_FLAG$5 = 1, COMPARE_UNORDERED_FLAG$3 = 2;
  function equalArrays(array, other, bitmask, customizer, equalFunc, stack2) {
    var isPartial = bitmask & COMPARE_PARTIAL_FLAG$5, arrLength = array.length, othLength = other.length;
    if (arrLength != othLength && !(isPartial && othLength > arrLength)) {
      return false;
    }
    var arrStacked = stack2.get(array);
    var othStacked = stack2.get(other);
    if (arrStacked && othStacked) {
      return arrStacked == other && othStacked == array;
    }
    var index = -1, result = true, seen = bitmask & COMPARE_UNORDERED_FLAG$3 ? new SetCache() : void 0;
    stack2.set(array, other);
    stack2.set(other, array);
    while (++index < arrLength) {
      var arrValue = array[index], othValue = other[index];
      if (customizer) {
        var compared = isPartial ? customizer(othValue, arrValue, index, other, array, stack2) : customizer(arrValue, othValue, index, array, other, stack2);
      }
      if (compared !== void 0) {
        if (compared) {
          continue;
        }
        result = false;
        break;
      }
      if (seen) {
        if (!arraySome(other, function(othValue2, othIndex) {
          if (!cacheHas(seen, othIndex) && (arrValue === othValue2 || equalFunc(arrValue, othValue2, bitmask, customizer, stack2))) {
            return seen.push(othIndex);
          }
        })) {
          result = false;
          break;
        }
      } else if (!(arrValue === othValue || equalFunc(arrValue, othValue, bitmask, customizer, stack2))) {
        result = false;
        break;
      }
    }
    stack2["delete"](array);
    stack2["delete"](other);
    return result;
  }
  function mapToArray(map2) {
    var index = -1, result = Array(map2.size);
    map2.forEach(function(value, key) {
      result[++index] = [key, value];
    });
    return result;
  }
  function setToArray(set) {
    var index = -1, result = Array(set.size);
    set.forEach(function(value) {
      result[++index] = value;
    });
    return result;
  }
  var COMPARE_PARTIAL_FLAG$4 = 1, COMPARE_UNORDERED_FLAG$2 = 2;
  var boolTag = "[object Boolean]", dateTag = "[object Date]", errorTag = "[object Error]", mapTag = "[object Map]", numberTag = "[object Number]", regexpTag = "[object RegExp]", setTag = "[object Set]", stringTag = "[object String]", symbolTag = "[object Symbol]";
  var arrayBufferTag = "[object ArrayBuffer]", dataViewTag = "[object DataView]";
  var symbolProto = Symbol$1 ? Symbol$1.prototype : void 0, symbolValueOf = symbolProto ? symbolProto.valueOf : void 0;
  function equalByTag(object, other, tag, bitmask, customizer, equalFunc, stack2) {
    switch (tag) {
      case dataViewTag:
        if (object.byteLength != other.byteLength || object.byteOffset != other.byteOffset) {
          return false;
        }
        object = object.buffer;
        other = other.buffer;
      case arrayBufferTag:
        if (object.byteLength != other.byteLength || !equalFunc(new Uint8Array(object), new Uint8Array(other))) {
          return false;
        }
        return true;
      case boolTag:
      case dateTag:
      case numberTag:
        return eq(+object, +other);
      case errorTag:
        return object.name == other.name && object.message == other.message;
      case regexpTag:
      case stringTag:
        return object == other + "";
      case mapTag:
        var convert = mapToArray;
      case setTag:
        var isPartial = bitmask & COMPARE_PARTIAL_FLAG$4;
        convert || (convert = setToArray);
        if (object.size != other.size && !isPartial) {
          return false;
        }
        var stacked = stack2.get(object);
        if (stacked) {
          return stacked == other;
        }
        bitmask |= COMPARE_UNORDERED_FLAG$2;
        stack2.set(object, other);
        var result = equalArrays(convert(object), convert(other), bitmask, customizer, equalFunc, stack2);
        stack2["delete"](object);
        return result;
      case symbolTag:
        if (symbolValueOf) {
          return symbolValueOf.call(object) == symbolValueOf.call(other);
        }
    }
    return false;
  }
  var COMPARE_PARTIAL_FLAG$3 = 1;
  var objectProto$1 = Object.prototype;
  var hasOwnProperty$1 = objectProto$1.hasOwnProperty;
  function equalObjects(object, other, bitmask, customizer, equalFunc, stack2) {
    var isPartial = bitmask & COMPARE_PARTIAL_FLAG$3, objProps = getAllKeys(object), objLength = objProps.length, othProps = getAllKeys(other), othLength = othProps.length;
    if (objLength != othLength && !isPartial) {
      return false;
    }
    var index = objLength;
    while (index--) {
      var key = objProps[index];
      if (!(isPartial ? key in other : hasOwnProperty$1.call(other, key))) {
        return false;
      }
    }
    var objStacked = stack2.get(object);
    var othStacked = stack2.get(other);
    if (objStacked && othStacked) {
      return objStacked == other && othStacked == object;
    }
    var result = true;
    stack2.set(object, other);
    stack2.set(other, object);
    var skipCtor = isPartial;
    while (++index < objLength) {
      key = objProps[index];
      var objValue = object[key], othValue = other[key];
      if (customizer) {
        var compared = isPartial ? customizer(othValue, objValue, key, other, object, stack2) : customizer(objValue, othValue, key, object, other, stack2);
      }
      if (!(compared === void 0 ? objValue === othValue || equalFunc(objValue, othValue, bitmask, customizer, stack2) : compared)) {
        result = false;
        break;
      }
      skipCtor || (skipCtor = key == "constructor");
    }
    if (result && !skipCtor) {
      var objCtor = object.constructor, othCtor = other.constructor;
      if (objCtor != othCtor && ("constructor" in object && "constructor" in other) && !(typeof objCtor == "function" && objCtor instanceof objCtor && typeof othCtor == "function" && othCtor instanceof othCtor)) {
        result = false;
      }
    }
    stack2["delete"](object);
    stack2["delete"](other);
    return result;
  }
  var COMPARE_PARTIAL_FLAG$2 = 1;
  var argsTag = "[object Arguments]", arrayTag = "[object Array]", objectTag = "[object Object]";
  var objectProto = Object.prototype;
  var hasOwnProperty = objectProto.hasOwnProperty;
  function baseIsEqualDeep(object, other, bitmask, customizer, equalFunc, stack2) {
    var objIsArr = isArray(object), othIsArr = isArray(other), objTag = objIsArr ? arrayTag : getTag(object), othTag = othIsArr ? arrayTag : getTag(other);
    objTag = objTag == argsTag ? objectTag : objTag;
    othTag = othTag == argsTag ? objectTag : othTag;
    var objIsObj = objTag == objectTag, othIsObj = othTag == objectTag, isSameTag = objTag == othTag;
    if (isSameTag && isBuffer(object)) {
      if (!isBuffer(other)) {
        return false;
      }
      objIsArr = true;
      objIsObj = false;
    }
    if (isSameTag && !objIsObj) {
      stack2 || (stack2 = new Stack());
      return objIsArr || isTypedArray(object) ? equalArrays(object, other, bitmask, customizer, equalFunc, stack2) : equalByTag(object, other, objTag, bitmask, customizer, equalFunc, stack2);
    }
    if (!(bitmask & COMPARE_PARTIAL_FLAG$2)) {
      var objIsWrapped = objIsObj && hasOwnProperty.call(object, "__wrapped__"), othIsWrapped = othIsObj && hasOwnProperty.call(other, "__wrapped__");
      if (objIsWrapped || othIsWrapped) {
        var objUnwrapped = objIsWrapped ? object.value() : object, othUnwrapped = othIsWrapped ? other.value() : other;
        stack2 || (stack2 = new Stack());
        return equalFunc(objUnwrapped, othUnwrapped, bitmask, customizer, stack2);
      }
    }
    if (!isSameTag) {
      return false;
    }
    stack2 || (stack2 = new Stack());
    return equalObjects(object, other, bitmask, customizer, equalFunc, stack2);
  }
  function baseIsEqual(value, other, bitmask, customizer, stack2) {
    if (value === other) {
      return true;
    }
    if (value == null || other == null || !isObjectLike(value) && !isObjectLike(other)) {
      return value !== value && other !== other;
    }
    return baseIsEqualDeep(value, other, bitmask, customizer, baseIsEqual, stack2);
  }
  var COMPARE_PARTIAL_FLAG$1 = 1, COMPARE_UNORDERED_FLAG$1 = 2;
  function baseIsMatch(object, source, matchData, customizer) {
    var index = matchData.length, length = index;
    if (object == null) {
      return !length;
    }
    object = Object(object);
    while (index--) {
      var data = matchData[index];
      if (data[2] ? data[1] !== object[data[0]] : !(data[0] in object)) {
        return false;
      }
    }
    while (++index < length) {
      data = matchData[index];
      var key = data[0], objValue = object[key], srcValue = data[1];
      if (data[2]) {
        if (objValue === void 0 && !(key in object)) {
          return false;
        }
      } else {
        var stack2 = new Stack();
        var result;
        if (!(result === void 0 ? baseIsEqual(srcValue, objValue, COMPARE_PARTIAL_FLAG$1 | COMPARE_UNORDERED_FLAG$1, customizer, stack2) : result)) {
          return false;
        }
      }
    }
    return true;
  }
  function isStrictComparable(value) {
    return value === value && !isObject(value);
  }
  function getMatchData(object) {
    var result = keys(object), length = result.length;
    while (length--) {
      var key = result[length], value = object[key];
      result[length] = [key, value, isStrictComparable(value)];
    }
    return result;
  }
  function matchesStrictComparable(key, srcValue) {
    return function(object) {
      if (object == null) {
        return false;
      }
      return object[key] === srcValue && (srcValue !== void 0 || key in Object(object));
    };
  }
  function baseMatches(source) {
    var matchData = getMatchData(source);
    if (matchData.length == 1 && matchData[0][2]) {
      return matchesStrictComparable(matchData[0][0], matchData[0][1]);
    }
    return function(object) {
      return object === source || baseIsMatch(object, source, matchData);
    };
  }
  function baseHasIn(object, key) {
    return object != null && key in Object(object);
  }
  function hasPath(object, path, hasFunc) {
    path = castPath(path, object);
    var index = -1, length = path.length, result = false;
    while (++index < length) {
      var key = toKey(path[index]);
      if (!(result = object != null && hasFunc(object, key))) {
        break;
      }
      object = object[key];
    }
    if (result || ++index != length) {
      return result;
    }
    length = object == null ? 0 : object.length;
    return !!length && isLength(length) && isIndex(key, length) && (isArray(object) || isArguments(object));
  }
  function hasIn(object, path) {
    return object != null && hasPath(object, path, baseHasIn);
  }
  var COMPARE_PARTIAL_FLAG = 1, COMPARE_UNORDERED_FLAG = 2;
  function baseMatchesProperty(path, srcValue) {
    if (isKey(path) && isStrictComparable(srcValue)) {
      return matchesStrictComparable(toKey(path), srcValue);
    }
    return function(object) {
      var objValue = get(object, path);
      return objValue === void 0 && objValue === srcValue ? hasIn(object, path) : baseIsEqual(srcValue, objValue, COMPARE_PARTIAL_FLAG | COMPARE_UNORDERED_FLAG);
    };
  }
  function baseProperty(key) {
    return function(object) {
      return object == null ? void 0 : object[key];
    };
  }
  function basePropertyDeep(path) {
    return function(object) {
      return baseGet(object, path);
    };
  }
  function property(path) {
    return isKey(path) ? baseProperty(toKey(path)) : basePropertyDeep(path);
  }
  function baseIteratee(value) {
    if (typeof value == "function") {
      return value;
    }
    if (value == null) {
      return identity;
    }
    if (typeof value == "object") {
      return isArray(value) ? baseMatchesProperty(value[0], value[1]) : baseMatches(value);
    }
    return property(value);
  }
  function createBaseFor(fromRight) {
    return function(object, iteratee, keysFunc) {
      var index = -1, iterable = Object(object), props = keysFunc(object), length = props.length;
      while (length--) {
        var key = props[++index];
        if (iteratee(iterable[key], key, iterable) === false) {
          break;
        }
      }
      return object;
    };
  }
  var baseFor = createBaseFor();
  function baseForOwn(object, iteratee) {
    return object && baseFor(object, iteratee, keys);
  }
  function createBaseEach(eachFunc, fromRight) {
    return function(collection, iteratee) {
      if (collection == null) {
        return collection;
      }
      if (!isArrayLike(collection)) {
        return eachFunc(collection, iteratee);
      }
      var length = collection.length, index = -1, iterable = Object(collection);
      while (++index < length) {
        if (iteratee(iterable[index], index, iterable) === false) {
          break;
        }
      }
      return collection;
    };
  }
  var baseEach = createBaseEach(baseForOwn);
  function assignMergeValue(object, key, value) {
    if (value !== void 0 && !eq(object[key], value) || value === void 0 && !(key in object)) {
      baseAssignValue(object, key, value);
    }
  }
  function isArrayLikeObject(value) {
    return isObjectLike(value) && isArrayLike(value);
  }
  function safeGet(object, key) {
    if (key === "constructor" && typeof object[key] === "function") {
      return;
    }
    if (key == "__proto__") {
      return;
    }
    return object[key];
  }
  function toPlainObject(value) {
    return copyObject(value, keysIn(value));
  }
  function baseMergeDeep(object, source, key, srcIndex, mergeFunc, customizer, stack2) {
    var objValue = safeGet(object, key), srcValue = safeGet(source, key), stacked = stack2.get(srcValue);
    if (stacked) {
      assignMergeValue(object, key, stacked);
      return;
    }
    var newValue = customizer ? customizer(objValue, srcValue, key + "", object, source, stack2) : void 0;
    var isCommon = newValue === void 0;
    if (isCommon) {
      var isArr = isArray(srcValue), isBuff = !isArr && isBuffer(srcValue), isTyped = !isArr && !isBuff && isTypedArray(srcValue);
      newValue = srcValue;
      if (isArr || isBuff || isTyped) {
        if (isArray(objValue)) {
          newValue = objValue;
        } else if (isArrayLikeObject(objValue)) {
          newValue = copyArray(objValue);
        } else if (isBuff) {
          isCommon = false;
          newValue = cloneBuffer(srcValue);
        } else if (isTyped) {
          isCommon = false;
          newValue = cloneTypedArray(srcValue);
        } else {
          newValue = [];
        }
      } else if (isPlainObject(srcValue) || isArguments(srcValue)) {
        newValue = objValue;
        if (isArguments(objValue)) {
          newValue = toPlainObject(objValue);
        } else if (!isObject(objValue) || isFunction(objValue)) {
          newValue = initCloneObject(srcValue);
        }
      } else {
        isCommon = false;
      }
    }
    if (isCommon) {
      stack2.set(srcValue, newValue);
      mergeFunc(newValue, srcValue, srcIndex, customizer, stack2);
      stack2["delete"](srcValue);
    }
    assignMergeValue(object, key, newValue);
  }
  function baseMerge(object, source, srcIndex, customizer, stack2) {
    if (object === source) {
      return;
    }
    baseFor(source, function(srcValue, key) {
      stack2 || (stack2 = new Stack());
      if (isObject(srcValue)) {
        baseMergeDeep(object, source, key, srcIndex, baseMerge, customizer, stack2);
      } else {
        var newValue = customizer ? customizer(safeGet(object, key), srcValue, key + "", object, source, stack2) : void 0;
        if (newValue === void 0) {
          newValue = srcValue;
        }
        assignMergeValue(object, key, newValue);
      }
    }, keysIn);
  }
  function baseMap(collection, iteratee) {
    var index = -1, result = isArrayLike(collection) ? Array(collection.length) : [];
    baseEach(collection, function(value, key, collection2) {
      result[++index] = iteratee(value, key, collection2);
    });
    return result;
  }
  function map(collection, iteratee) {
    var func = isArray(collection) ? arrayMap : baseMap;
    return func(collection, baseIteratee(iteratee));
  }
  var merge$1 = createAssigner(function(object, source, srcIndex) {
    baseMerge(object, source, srcIndex);
  });
  function useLocale(ns) {
    const {
      mergedLocaleRef,
      mergedDateLocaleRef
    } = inject(configProviderInjectionKey, null) || {};
    const localeRef = computed(() => {
      var _a2, _b;
      return (_b = (_a2 = mergedLocaleRef === null || mergedLocaleRef === void 0 ? void 0 : mergedLocaleRef.value) === null || _a2 === void 0 ? void 0 : _a2[ns]) !== null && _b !== void 0 ? _b : enUS$1[ns];
    });
    const dateLocaleRef = computed(() => {
      var _a2;
      return (_a2 = mergedDateLocaleRef === null || mergedDateLocaleRef === void 0 ? void 0 : mergedDateLocaleRef.value) !== null && _a2 !== void 0 ? _a2 : dateEnUs;
    });
    return {
      dateLocaleRef,
      localeRef
    };
  }
  const cssrAnchorMetaName = "naive-ui-style";
  function useRtl(mountId, rtlStateRef, clsPrefixRef) {
    if (!rtlStateRef) return void 0;
    const ssrAdapter2 = useSsrAdapter();
    const componentRtlStateRef = computed(() => {
      const {
        value: rtlState
      } = rtlStateRef;
      if (!rtlState) {
        return void 0;
      }
      const componentRtlState = rtlState[mountId];
      if (!componentRtlState) {
        return void 0;
      }
      return componentRtlState;
    });
    const NConfigProvider2 = inject(configProviderInjectionKey, null);
    const mountStyle = () => {
      watchEffect(() => {
        const {
          value: clsPrefix
        } = clsPrefixRef;
        const id = `${clsPrefix}${mountId}Rtl`;
        if (exists(id, ssrAdapter2)) return;
        const {
          value: componentRtlState
        } = componentRtlStateRef;
        if (!componentRtlState) return;
        componentRtlState.style.mount({
          id,
          head: true,
          anchorMetaName: cssrAnchorMetaName,
          props: {
            bPrefix: clsPrefix ? `.${clsPrefix}-` : void 0
          },
          ssr: ssrAdapter2,
          parent: NConfigProvider2 === null || NConfigProvider2 === void 0 ? void 0 : NConfigProvider2.styleMountTarget
        });
      });
    };
    if (ssrAdapter2) {
      mountStyle();
    } else {
      onBeforeMount(mountStyle);
    }
    return componentRtlStateRef;
  }
  const commonVariables$7 = {
    fontFamily: 'v-sans, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol"',
    fontFamilyMono: "v-mono, SFMono-Regular, Menlo, Consolas, Courier, monospace",
    fontWeight: "400",
    fontWeightStrong: "500",
    cubicBezierEaseInOut: "cubic-bezier(.4, 0, .2, 1)",
    cubicBezierEaseOut: "cubic-bezier(0, 0, .2, 1)",
    cubicBezierEaseIn: "cubic-bezier(.4, 0, 1, 1)",
    borderRadius: "3px",
    borderRadiusSmall: "2px",
    fontSize: "14px",
    fontSizeMini: "12px",
    fontSizeTiny: "12px",
    fontSizeSmall: "14px",
    fontSizeMedium: "14px",
    fontSizeLarge: "15px",
    fontSizeHuge: "16px",
    lineHeight: "1.6",
    heightMini: "16px",
    // private now, it's too small
    heightTiny: "22px",
    heightSmall: "28px",
    heightMedium: "34px",
    heightLarge: "40px",
    heightHuge: "46px"
  };
  const {
    fontSize,
    fontFamily,
    lineHeight
  } = commonVariables$7;
  const globalStyle = c$1("body", `
 margin: 0;
 font-size: ${fontSize};
 font-family: ${fontFamily};
 line-height: ${lineHeight};
 -webkit-text-size-adjust: 100%;
 -webkit-tap-highlight-color: transparent;
`, [c$1("input", `
 font-family: inherit;
 font-size: inherit;
 `)]);
  function useStyle(mountId, style2, clsPrefixRef) {
    if (!style2) {
      return;
    }
    const ssrAdapter2 = useSsrAdapter();
    const NConfigProvider2 = inject(configProviderInjectionKey, null);
    const mountStyle = () => {
      const clsPrefix = clsPrefixRef.value;
      style2.mount({
        id: clsPrefix === void 0 ? mountId : clsPrefix + mountId,
        head: true,
        anchorMetaName: cssrAnchorMetaName,
        props: {
          bPrefix: clsPrefix ? `.${clsPrefix}-` : void 0
        },
        ssr: ssrAdapter2,
        parent: NConfigProvider2 === null || NConfigProvider2 === void 0 ? void 0 : NConfigProvider2.styleMountTarget
      });
      if (!(NConfigProvider2 === null || NConfigProvider2 === void 0 ? void 0 : NConfigProvider2.preflightStyleDisabled)) {
        globalStyle.mount({
          id: "n-global",
          head: true,
          anchorMetaName: cssrAnchorMetaName,
          ssr: ssrAdapter2,
          parent: NConfigProvider2 === null || NConfigProvider2 === void 0 ? void 0 : NConfigProvider2.styleMountTarget
        });
      }
    };
    if (ssrAdapter2) {
      mountStyle();
    } else {
      onBeforeMount(mountStyle);
    }
  }
  function createTheme(theme) {
    return theme;
  }
  function useTheme(resolveId, mountId, style2, defaultTheme, props, clsPrefixRef) {
    const ssrAdapter2 = useSsrAdapter();
    const NConfigProvider2 = inject(configProviderInjectionKey, null);
    if (style2) {
      const mountStyle = () => {
        const clsPrefix = clsPrefixRef === null || clsPrefixRef === void 0 ? void 0 : clsPrefixRef.value;
        style2.mount({
          id: clsPrefix === void 0 ? mountId : clsPrefix + mountId,
          head: true,
          props: {
            bPrefix: clsPrefix ? `.${clsPrefix}-` : void 0
          },
          anchorMetaName: cssrAnchorMetaName,
          ssr: ssrAdapter2,
          parent: NConfigProvider2 === null || NConfigProvider2 === void 0 ? void 0 : NConfigProvider2.styleMountTarget
        });
        if (!(NConfigProvider2 === null || NConfigProvider2 === void 0 ? void 0 : NConfigProvider2.preflightStyleDisabled)) {
          globalStyle.mount({
            id: "n-global",
            head: true,
            anchorMetaName: cssrAnchorMetaName,
            ssr: ssrAdapter2,
            parent: NConfigProvider2 === null || NConfigProvider2 === void 0 ? void 0 : NConfigProvider2.styleMountTarget
          });
        }
      };
      if (ssrAdapter2) {
        mountStyle();
      } else {
        onBeforeMount(mountStyle);
      }
    }
    const mergedThemeRef = computed(() => {
      var _a2;
      const {
        theme: {
          common: selfCommon,
          self: self2,
          peers = {}
        } = {},
        themeOverrides: selfOverrides = {},
        builtinThemeOverrides: builtinOverrides = {}
      } = props;
      const {
        common: selfCommonOverrides,
        peers: peersOverrides
      } = selfOverrides;
      const {
        common: globalCommon = void 0,
        [resolveId]: {
          common: globalSelfCommon = void 0,
          self: globalSelf = void 0,
          peers: globalPeers = {}
        } = {}
      } = (NConfigProvider2 === null || NConfigProvider2 === void 0 ? void 0 : NConfigProvider2.mergedThemeRef.value) || {};
      const {
        common: globalCommonOverrides = void 0,
        [resolveId]: globalSelfOverrides = {}
      } = (NConfigProvider2 === null || NConfigProvider2 === void 0 ? void 0 : NConfigProvider2.mergedThemeOverridesRef.value) || {};
      const {
        common: globalSelfCommonOverrides,
        peers: globalPeersOverrides = {}
      } = globalSelfOverrides;
      const mergedCommon = merge$1({}, selfCommon || globalSelfCommon || globalCommon || defaultTheme.common, globalCommonOverrides, globalSelfCommonOverrides, selfCommonOverrides);
      const mergedSelf = merge$1(
        // {}, executed every time, no need for empty obj
        (_a2 = self2 || globalSelf || defaultTheme.self) === null || _a2 === void 0 ? void 0 : _a2(mergedCommon),
        builtinOverrides,
        globalSelfOverrides,
        selfOverrides
      );
      return {
        common: mergedCommon,
        self: mergedSelf,
        peers: merge$1({}, defaultTheme.peers, globalPeers, peers),
        peerOverrides: merge$1({}, builtinOverrides.peers, globalPeersOverrides, peersOverrides)
      };
    });
    return mergedThemeRef;
  }
  useTheme.props = {
    theme: Object,
    themeOverrides: Object,
    builtinThemeOverrides: Object
  };
  const style$l = cB("base-icon", `
 height: 1em;
 width: 1em;
 line-height: 1em;
 text-align: center;
 display: inline-block;
 position: relative;
 fill: currentColor;
`, [c$1("svg", `
 height: 1em;
 width: 1em;
 `)]);
  const NBaseIcon = /* @__PURE__ */ defineComponent({
    name: "BaseIcon",
    props: {
      role: String,
      ariaLabel: String,
      ariaDisabled: {
        type: Boolean,
        default: void 0
      },
      ariaHidden: {
        type: Boolean,
        default: void 0
      },
      clsPrefix: {
        type: String,
        required: true
      },
      onClick: Function,
      onMousedown: Function,
      onMouseup: Function
    },
    setup(props) {
      useStyle("-base-icon", style$l, /* @__PURE__ */ toRef(props, "clsPrefix"));
    },
    render() {
      return h("i", {
        class: `${this.clsPrefix}-base-icon`,
        onClick: this.onClick,
        onMousedown: this.onMousedown,
        onMouseup: this.onMouseup,
        role: this.role,
        "aria-label": this.ariaLabel,
        "aria-hidden": this.ariaHidden,
        "aria-disabled": this.ariaDisabled
      }, this.$slots);
    }
  });
  const NIconSwitchTransition = /* @__PURE__ */ defineComponent({
    name: "BaseIconSwitchTransition",
    setup(_, {
      slots
    }) {
      const isMountedRef = isMounted();
      return () => h(Transition, {
        name: "icon-switch-transition",
        appear: isMountedRef.value
      }, slots);
    }
  });
  function replaceable(name, icon) {
    const IconComponent = /* @__PURE__ */ defineComponent({
      render() {
        return icon();
      }
    });
    return /* @__PURE__ */ defineComponent({
      name: upperFirst(name),
      setup() {
        var _a2;
        const mergedIconsRef = (_a2 = inject(configProviderInjectionKey, null)) === null || _a2 === void 0 ? void 0 : _a2.mergedIconsRef;
        return () => {
          var _a3;
          const iconOverride = (_a3 = mergedIconsRef === null || mergedIconsRef === void 0 ? void 0 : mergedIconsRef.value) === null || _a3 === void 0 ? void 0 : _a3[name];
          return iconOverride ? iconOverride() : h(IconComponent, null);
        };
      }
    });
  }
  const ChevronDownFilledIcon = /* @__PURE__ */ defineComponent({
    name: "ChevronDownFilled",
    render() {
      return h("svg", {
        viewBox: "0 0 16 16",
        fill: "none",
        xmlns: "http://www.w3.org/2000/svg"
      }, h("path", {
        d: "M3.20041 5.73966C3.48226 5.43613 3.95681 5.41856 4.26034 5.70041L8 9.22652L11.7397 5.70041C12.0432 5.41856 12.5177 5.43613 12.7996 5.73966C13.0815 6.0432 13.0639 6.51775 12.7603 6.7996L8.51034 10.7996C8.22258 11.0668 7.77743 11.0668 7.48967 10.7996L3.23966 6.7996C2.93613 6.51775 2.91856 6.0432 3.20041 5.73966Z",
        fill: "currentColor"
      }));
    }
  });
  const ChevronRightIcon = /* @__PURE__ */ defineComponent({
    name: "ChevronRight",
    render() {
      return h("svg", {
        viewBox: "0 0 16 16",
        fill: "none",
        xmlns: "http://www.w3.org/2000/svg"
      }, h("path", {
        d: "M5.64645 3.14645C5.45118 3.34171 5.45118 3.65829 5.64645 3.85355L9.79289 8L5.64645 12.1464C5.45118 12.3417 5.45118 12.6583 5.64645 12.8536C5.84171 13.0488 6.15829 13.0488 6.35355 12.8536L10.8536 8.35355C11.0488 8.15829 11.0488 7.84171 10.8536 7.64645L6.35355 3.14645C6.15829 2.95118 5.84171 2.95118 5.64645 3.14645Z",
        fill: "currentColor"
      }));
    }
  });
  const ErrorIcon$1 = replaceable("close", () => h("svg", {
    viewBox: "0 0 12 12",
    version: "1.1",
    xmlns: "http://www.w3.org/2000/svg",
    "aria-hidden": true
  }, h("g", {
    stroke: "none",
    "stroke-width": "1",
    fill: "none",
    "fill-rule": "evenodd"
  }, h("g", {
    fill: "currentColor",
    "fill-rule": "nonzero"
  }, h("path", {
    d: "M2.08859116,2.2156945 L2.14644661,2.14644661 C2.32001296,1.97288026 2.58943736,1.95359511 2.7843055,2.08859116 L2.85355339,2.14644661 L6,5.293 L9.14644661,2.14644661 C9.34170876,1.95118446 9.65829124,1.95118446 9.85355339,2.14644661 C10.0488155,2.34170876 10.0488155,2.65829124 9.85355339,2.85355339 L6.707,6 L9.85355339,9.14644661 C10.0271197,9.32001296 10.0464049,9.58943736 9.91140884,9.7843055 L9.85355339,9.85355339 C9.67998704,10.0271197 9.41056264,10.0464049 9.2156945,9.91140884 L9.14644661,9.85355339 L6,6.707 L2.85355339,9.85355339 C2.65829124,10.0488155 2.34170876,10.0488155 2.14644661,9.85355339 C1.95118446,9.65829124 1.95118446,9.34170876 2.14644661,9.14644661 L5.293,6 L2.14644661,2.85355339 C1.97288026,2.67998704 1.95359511,2.41056264 2.08859116,2.2156945 L2.14644661,2.14644661 L2.08859116,2.2156945 Z"
  })))));
  const EmptyIcon = /* @__PURE__ */ defineComponent({
    name: "Empty",
    render() {
      return h("svg", {
        viewBox: "0 0 28 28",
        fill: "none",
        xmlns: "http://www.w3.org/2000/svg"
      }, h("path", {
        d: "M26 7.5C26 11.0899 23.0899 14 19.5 14C15.9101 14 13 11.0899 13 7.5C13 3.91015 15.9101 1 19.5 1C23.0899 1 26 3.91015 26 7.5ZM16.8536 4.14645C16.6583 3.95118 16.3417 3.95118 16.1464 4.14645C15.9512 4.34171 15.9512 4.65829 16.1464 4.85355L18.7929 7.5L16.1464 10.1464C15.9512 10.3417 15.9512 10.6583 16.1464 10.8536C16.3417 11.0488 16.6583 11.0488 16.8536 10.8536L19.5 8.20711L22.1464 10.8536C22.3417 11.0488 22.6583 11.0488 22.8536 10.8536C23.0488 10.6583 23.0488 10.3417 22.8536 10.1464L20.2071 7.5L22.8536 4.85355C23.0488 4.65829 23.0488 4.34171 22.8536 4.14645C22.6583 3.95118 22.3417 3.95118 22.1464 4.14645L19.5 6.79289L16.8536 4.14645Z",
        fill: "currentColor"
      }), h("path", {
        d: "M25 22.75V12.5991C24.5572 13.0765 24.053 13.4961 23.5 13.8454V16H17.5L17.3982 16.0068C17.0322 16.0565 16.75 16.3703 16.75 16.75C16.75 18.2688 15.5188 19.5 14 19.5C12.4812 19.5 11.25 18.2688 11.25 16.75L11.2432 16.6482C11.1935 16.2822 10.8797 16 10.5 16H4.5V7.25C4.5 6.2835 5.2835 5.5 6.25 5.5H12.2696C12.4146 4.97463 12.6153 4.47237 12.865 4H6.25C4.45507 4 3 5.45507 3 7.25V22.75C3 24.5449 4.45507 26 6.25 26H21.75C23.5449 26 25 24.5449 25 22.75ZM4.5 22.75V17.5H9.81597L9.85751 17.7041C10.2905 19.5919 11.9808 21 14 21L14.215 20.9947C16.2095 20.8953 17.842 19.4209 18.184 17.5H23.5V22.75C23.5 23.7165 22.7165 24.5 21.75 24.5H6.25C5.2835 24.5 4.5 23.7165 4.5 22.75Z",
        fill: "currentColor"
      }));
    }
  });
  const ErrorIcon = replaceable("error", () => h("svg", {
    viewBox: "0 0 48 48",
    version: "1.1",
    xmlns: "http://www.w3.org/2000/svg"
  }, h("g", {
    stroke: "none",
    "stroke-width": "1",
    "fill-rule": "evenodd"
  }, h("g", {
    "fill-rule": "nonzero"
  }, h("path", {
    d: "M24,4 C35.045695,4 44,12.954305 44,24 C44,35.045695 35.045695,44 24,44 C12.954305,44 4,35.045695 4,24 C4,12.954305 12.954305,4 24,4 Z M17.8838835,16.1161165 L17.7823881,16.0249942 C17.3266086,15.6583353 16.6733914,15.6583353 16.2176119,16.0249942 L16.1161165,16.1161165 L16.0249942,16.2176119 C15.6583353,16.6733914 15.6583353,17.3266086 16.0249942,17.7823881 L16.1161165,17.8838835 L22.233,24 L16.1161165,30.1161165 L16.0249942,30.2176119 C15.6583353,30.6733914 15.6583353,31.3266086 16.0249942,31.7823881 L16.1161165,31.8838835 L16.2176119,31.9750058 C16.6733914,32.3416647 17.3266086,32.3416647 17.7823881,31.9750058 L17.8838835,31.8838835 L24,25.767 L30.1161165,31.8838835 L30.2176119,31.9750058 C30.6733914,32.3416647 31.3266086,32.3416647 31.7823881,31.9750058 L31.8838835,31.8838835 L31.9750058,31.7823881 C32.3416647,31.3266086 32.3416647,30.6733914 31.9750058,30.2176119 L31.8838835,30.1161165 L25.767,24 L31.8838835,17.8838835 L31.9750058,17.7823881 C32.3416647,17.3266086 32.3416647,16.6733914 31.9750058,16.2176119 L31.8838835,16.1161165 L31.7823881,16.0249942 C31.3266086,15.6583353 30.6733914,15.6583353 30.2176119,16.0249942 L30.1161165,16.1161165 L24,22.233 L17.8838835,16.1161165 L17.7823881,16.0249942 L17.8838835,16.1161165 Z"
  })))));
  const InfoIcon = replaceable("info", () => h("svg", {
    viewBox: "0 0 28 28",
    version: "1.1",
    xmlns: "http://www.w3.org/2000/svg"
  }, h("g", {
    stroke: "none",
    "stroke-width": "1",
    "fill-rule": "evenodd"
  }, h("g", {
    "fill-rule": "nonzero"
  }, h("path", {
    d: "M14,2 C20.6274,2 26,7.37258 26,14 C26,20.6274 20.6274,26 14,26 C7.37258,26 2,20.6274 2,14 C2,7.37258 7.37258,2 14,2 Z M14,11 C13.4477,11 13,11.4477 13,12 L13,12 L13,20 C13,20.5523 13.4477,21 14,21 C14.5523,21 15,20.5523 15,20 L15,20 L15,12 C15,11.4477 14.5523,11 14,11 Z M14,6.75 C13.3096,6.75 12.75,7.30964 12.75,8 C12.75,8.69036 13.3096,9.25 14,9.25 C14.6904,9.25 15.25,8.69036 15.25,8 C15.25,7.30964 14.6904,6.75 14,6.75 Z"
  })))));
  const SuccessIcon = replaceable("success", () => h("svg", {
    viewBox: "0 0 48 48",
    version: "1.1",
    xmlns: "http://www.w3.org/2000/svg"
  }, h("g", {
    stroke: "none",
    "stroke-width": "1",
    "fill-rule": "evenodd"
  }, h("g", {
    "fill-rule": "nonzero"
  }, h("path", {
    d: "M24,4 C35.045695,4 44,12.954305 44,24 C44,35.045695 35.045695,44 24,44 C12.954305,44 4,35.045695 4,24 C4,12.954305 12.954305,4 24,4 Z M32.6338835,17.6161165 C32.1782718,17.1605048 31.4584514,17.1301307 30.9676119,17.5249942 L30.8661165,17.6161165 L20.75,27.732233 L17.1338835,24.1161165 C16.6457281,23.6279612 15.8542719,23.6279612 15.3661165,24.1161165 C14.9105048,24.5717282 14.8801307,25.2915486 15.2749942,25.7823881 L15.3661165,25.8838835 L19.8661165,30.3838835 C20.3217282,30.8394952 21.0415486,30.8698693 21.5323881,30.4750058 L21.6338835,30.3838835 L32.6338835,19.3838835 C33.1220388,18.8957281 33.1220388,18.1042719 32.6338835,17.6161165 Z"
  })))));
  const WarningIcon = replaceable("warning", () => h("svg", {
    viewBox: "0 0 24 24",
    version: "1.1",
    xmlns: "http://www.w3.org/2000/svg"
  }, h("g", {
    stroke: "none",
    "stroke-width": "1",
    "fill-rule": "evenodd"
  }, h("g", {
    "fill-rule": "nonzero"
  }, h("path", {
    d: "M12,2 C17.523,2 22,6.478 22,12 C22,17.522 17.523,22 12,22 C6.477,22 2,17.522 2,12 C2,6.478 6.477,2 12,2 Z M12.0018002,15.0037242 C11.450254,15.0037242 11.0031376,15.4508407 11.0031376,16.0023869 C11.0031376,16.553933 11.450254,17.0010495 12.0018002,17.0010495 C12.5533463,17.0010495 13.0004628,16.553933 13.0004628,16.0023869 C13.0004628,15.4508407 12.5533463,15.0037242 12.0018002,15.0037242 Z M11.99964,7 C11.4868042,7.00018474 11.0642719,7.38637706 11.0066858,7.8837365 L11,8.00036004 L11.0018003,13.0012393 L11.00857,13.117858 C11.0665141,13.6151758 11.4893244,14.0010638 12.0021602,14.0008793 C12.514996,14.0006946 12.9375283,13.6145023 12.9951144,13.1171428 L13.0018002,13.0005193 L13,7.99964009 L12.9932303,7.8830214 C12.9352861,7.38570354 12.5124758,6.99981552 11.99964,7 Z"
  })))));
  const {
    cubicBezierEaseInOut: cubicBezierEaseInOut$3
  } = commonVariables$7;
  function iconSwitchTransition({
    originalTransform = "",
    left = 0,
    top = 0,
    transition = `all .3s ${cubicBezierEaseInOut$3} !important`
  } = {}) {
    return [c$1("&.icon-switch-transition-enter-from, &.icon-switch-transition-leave-to", {
      transform: `${originalTransform} scale(0.75)`,
      left,
      top,
      opacity: 0
    }), c$1("&.icon-switch-transition-enter-to, &.icon-switch-transition-leave-from", {
      transform: `scale(1) ${originalTransform}`,
      left,
      top,
      opacity: 1
    }), c$1("&.icon-switch-transition-enter-active, &.icon-switch-transition-leave-active", {
      transformOrigin: "center",
      position: "absolute",
      left,
      top,
      transition
    })];
  }
  const style$k = cB("base-close", `
 display: flex;
 align-items: center;
 justify-content: center;
 cursor: pointer;
 background-color: transparent;
 color: var(--n-close-icon-color);
 border-radius: var(--n-close-border-radius);
 height: var(--n-close-size);
 width: var(--n-close-size);
 font-size: var(--n-close-icon-size);
 outline: none;
 border: none;
 position: relative;
 padding: 0;
`, [cM("absolute", `
 height: var(--n-close-icon-size);
 width: var(--n-close-icon-size);
 `), c$1("&::before", `
 content: "";
 position: absolute;
 width: var(--n-close-size);
 height: var(--n-close-size);
 left: 50%;
 top: 50%;
 transform: translateY(-50%) translateX(-50%);
 transition: inherit;
 border-radius: inherit;
 `), cNotM("disabled", [c$1("&:hover", `
 color: var(--n-close-icon-color-hover);
 `), c$1("&:hover::before", `
 background-color: var(--n-close-color-hover);
 `), c$1("&:focus::before", `
 background-color: var(--n-close-color-hover);
 `), c$1("&:active", `
 color: var(--n-close-icon-color-pressed);
 `), c$1("&:active::before", `
 background-color: var(--n-close-color-pressed);
 `)]), cM("disabled", `
 cursor: not-allowed;
 color: var(--n-close-icon-color-disabled);
 background-color: transparent;
 `), cM("round", [c$1("&::before", `
 border-radius: 50%;
 `)])]);
  const NBaseClose = /* @__PURE__ */ defineComponent({
    name: "BaseClose",
    props: {
      isButtonTag: {
        type: Boolean,
        default: true
      },
      clsPrefix: {
        type: String,
        required: true
      },
      disabled: {
        type: Boolean,
        default: void 0
      },
      focusable: {
        type: Boolean,
        default: true
      },
      round: Boolean,
      onClick: Function,
      absolute: Boolean
    },
    setup(props) {
      useStyle("-base-close", style$k, /* @__PURE__ */ toRef(props, "clsPrefix"));
      return () => {
        const {
          clsPrefix,
          disabled,
          absolute,
          round,
          isButtonTag
        } = props;
        const Tag = isButtonTag ? "button" : "div";
        return h(Tag, {
          type: isButtonTag ? "button" : void 0,
          tabindex: disabled || !props.focusable ? -1 : 0,
          "aria-disabled": disabled,
          "aria-label": "close",
          role: isButtonTag ? void 0 : "button",
          disabled,
          class: [`${clsPrefix}-base-close`, absolute && `${clsPrefix}-base-close--absolute`, disabled && `${clsPrefix}-base-close--disabled`, round && `${clsPrefix}-base-close--round`],
          onMousedown: (e) => {
            if (!props.focusable) {
              e.preventDefault();
            }
          },
          onClick: props.onClick
        }, h(NBaseIcon, {
          clsPrefix
        }, {
          default: () => h(ErrorIcon$1, null)
        }));
      };
    }
  });
  const NFadeInExpandTransition = /* @__PURE__ */ defineComponent({
    name: "FadeInExpandTransition",
    props: {
      appear: Boolean,
      group: Boolean,
      mode: String,
      onLeave: Function,
      onAfterLeave: Function,
      onAfterEnter: Function,
      width: Boolean,
      // reverse mode is only used in tree
      // it make it from expanded to collapsed after mounted
      reverse: Boolean
    },
    setup(props, {
      slots
    }) {
      function handleBeforeLeave(el) {
        if (props.width) {
          el.style.maxWidth = `${el.offsetWidth}px`;
        } else {
          el.style.maxHeight = `${el.offsetHeight}px`;
        }
        void el.offsetWidth;
      }
      function handleLeave(el) {
        if (props.width) {
          el.style.maxWidth = "0";
        } else {
          el.style.maxHeight = "0";
        }
        void el.offsetWidth;
        const {
          onLeave
        } = props;
        if (onLeave) onLeave();
      }
      function handleAfterLeave(el) {
        if (props.width) {
          el.style.maxWidth = "";
        } else {
          el.style.maxHeight = "";
        }
        const {
          onAfterLeave
        } = props;
        if (onAfterLeave) onAfterLeave();
      }
      function handleEnter(el) {
        el.style.transition = "none";
        if (props.width) {
          const memorizedWidth = el.offsetWidth;
          el.style.maxWidth = "0";
          void el.offsetWidth;
          el.style.transition = "";
          el.style.maxWidth = `${memorizedWidth}px`;
        } else {
          if (props.reverse) {
            el.style.maxHeight = `${el.offsetHeight}px`;
            void el.offsetHeight;
            el.style.transition = "";
            el.style.maxHeight = "0";
          } else {
            const memorizedHeight = el.offsetHeight;
            el.style.maxHeight = "0";
            void el.offsetWidth;
            el.style.transition = "";
            el.style.maxHeight = `${memorizedHeight}px`;
          }
        }
        void el.offsetWidth;
      }
      function handleAfterEnter(el) {
        var _a2;
        if (props.width) {
          el.style.maxWidth = "";
        } else {
          if (!props.reverse) {
            el.style.maxHeight = "";
          }
        }
        (_a2 = props.onAfterEnter) === null || _a2 === void 0 ? void 0 : _a2.call(props);
      }
      return () => {
        const {
          group,
          width,
          appear,
          mode
        } = props;
        const type = group ? TransitionGroup : Transition;
        const resolvedProps = {
          name: width ? "fade-in-width-expand-transition" : "fade-in-height-expand-transition",
          appear,
          onEnter: handleEnter,
          onAfterEnter: handleAfterEnter,
          onBeforeLeave: handleBeforeLeave,
          onLeave: handleLeave,
          onAfterLeave: handleAfterLeave
        };
        if (!group) {
          resolvedProps.mode = mode;
        }
        return h(type, resolvedProps, slots);
      };
    }
  });
  const style$j = c$1([c$1("@keyframes rotator", `
 0% {
 -webkit-transform: rotate(0deg);
 transform: rotate(0deg);
 }
 100% {
 -webkit-transform: rotate(360deg);
 transform: rotate(360deg);
 }`), cB("base-loading", `
 position: relative;
 line-height: 0;
 width: 1em;
 height: 1em;
 `, [cE("transition-wrapper", `
 position: absolute;
 width: 100%;
 height: 100%;
 `, [iconSwitchTransition()]), cE("placeholder", `
 position: absolute;
 left: 50%;
 top: 50%;
 transform: translateX(-50%) translateY(-50%);
 `, [iconSwitchTransition({
    left: "50%",
    top: "50%",
    originalTransform: "translateX(-50%) translateY(-50%)"
  })]), cE("container", `
 animation: rotator 3s linear infinite both;
 `, [cE("icon", `
 height: 1em;
 width: 1em;
 `)])])]);
  const duration = "1.6s";
  const exposedLoadingProps = {
    strokeWidth: {
      type: Number,
      default: 28
    },
    stroke: {
      type: String,
      default: void 0
    },
    scale: {
      type: Number,
      default: 1
    },
    radius: {
      type: Number,
      default: 100
    }
  };
  const NBaseLoading = /* @__PURE__ */ defineComponent({
    name: "BaseLoading",
    props: Object.assign({
      clsPrefix: {
        type: String,
        required: true
      },
      show: {
        type: Boolean,
        default: true
      }
    }, exposedLoadingProps),
    setup(props) {
      useStyle("-base-loading", style$j, /* @__PURE__ */ toRef(props, "clsPrefix"));
    },
    render() {
      const {
        clsPrefix,
        radius,
        strokeWidth,
        stroke,
        scale
      } = this;
      const scaledRadius = radius / scale;
      return h("div", {
        class: `${clsPrefix}-base-loading`,
        role: "img",
        "aria-label": "loading"
      }, h(NIconSwitchTransition, null, {
        default: () => this.show ? h("div", {
          key: "icon",
          class: `${clsPrefix}-base-loading__transition-wrapper`
        }, h("div", {
          class: `${clsPrefix}-base-loading__container`
        }, h("svg", {
          class: `${clsPrefix}-base-loading__icon`,
          viewBox: `0 0 ${2 * scaledRadius} ${2 * scaledRadius}`,
          xmlns: "http://www.w3.org/2000/svg",
          style: {
            color: stroke
          }
        }, h("g", null, h("animateTransform", {
          attributeName: "transform",
          type: "rotate",
          values: `0 ${scaledRadius} ${scaledRadius};270 ${scaledRadius} ${scaledRadius}`,
          begin: "0s",
          dur: duration,
          fill: "freeze",
          repeatCount: "indefinite"
        }), h("circle", {
          class: `${clsPrefix}-base-loading__icon`,
          fill: "none",
          stroke: "currentColor",
          "stroke-width": strokeWidth,
          "stroke-linecap": "round",
          cx: scaledRadius,
          cy: scaledRadius,
          r: radius - strokeWidth / 2,
          "stroke-dasharray": 5.67 * radius,
          "stroke-dashoffset": 18.48 * radius
        }, h("animateTransform", {
          attributeName: "transform",
          type: "rotate",
          values: `0 ${scaledRadius} ${scaledRadius};135 ${scaledRadius} ${scaledRadius};450 ${scaledRadius} ${scaledRadius}`,
          begin: "0s",
          dur: duration,
          fill: "freeze",
          repeatCount: "indefinite"
        }), h("animate", {
          attributeName: "stroke-dashoffset",
          values: `${5.67 * radius};${1.42 * radius};${5.67 * radius}`,
          begin: "0s",
          dur: duration,
          fill: "freeze",
          repeatCount: "indefinite"
        })))))) : h("div", {
          key: "placeholder",
          class: `${clsPrefix}-base-loading__placeholder`
        }, this.$slots)
      }));
    }
  });
  const {
    cubicBezierEaseInOut: cubicBezierEaseInOut$2
  } = commonVariables$7;
  function fadeInTransition({
    name = "fade-in",
    enterDuration = "0.2s",
    leaveDuration = "0.2s",
    enterCubicBezier = cubicBezierEaseInOut$2,
    leaveCubicBezier = cubicBezierEaseInOut$2
  } = {}) {
    return [c$1(`&.${name}-transition-enter-active`, {
      transition: `all ${enterDuration} ${enterCubicBezier}!important`
    }), c$1(`&.${name}-transition-leave-active`, {
      transition: `all ${leaveDuration} ${leaveCubicBezier}!important`
    }), c$1(`&.${name}-transition-enter-from, &.${name}-transition-leave-to`, {
      opacity: 0
    }), c$1(`&.${name}-transition-leave-from, &.${name}-transition-enter-to`, {
      opacity: 1
    })];
  }
  const base = {
    neutralBase: "#FFF",
    neutralInvertBase: "#000",
    neutralTextBase: "#000",
    neutralPopover: "#fff",
    neutralCard: "#fff",
    neutralModal: "#fff",
    neutralBody: "#fff",
    alpha1: "0.82",
    alpha2: "0.72",
    alpha3: "0.38",
    alpha4: "0.24",
    // disabled text, placeholder, icon
    alpha5: "0.18",
    // disabled placeholder
    alphaClose: "0.6",
    alphaDisabled: "0.5",
    alphaAvatar: "0.2",
    alphaProgressRail: ".08",
    alphaInput: "0",
    alphaScrollbar: "0.25",
    alphaScrollbarHover: "0.4",
    // primary
    primaryHover: "#36ad6a",
    primaryDefault: "#18a058",
    primaryActive: "#0c7a43",
    primarySuppl: "#36ad6a",
    // info
    infoHover: "#4098fc",
    infoDefault: "#2080f0",
    infoActive: "#1060c9",
    infoSuppl: "#4098fc",
    // error
    errorHover: "#de576d",
    errorDefault: "#d03050",
    errorActive: "#ab1f3f",
    errorSuppl: "#de576d",
    // warning
    warningHover: "#fcb040",
    warningDefault: "#f0a020",
    warningActive: "#c97c10",
    warningSuppl: "#fcb040",
    // success
    successHover: "#36ad6a",
    successDefault: "#18a058",
    successActive: "#0c7a43",
    successSuppl: "#36ad6a"
  };
  const baseBackgroundRgb = rgba(base.neutralBase);
  const baseInvertBackgroundRgb = rgba(base.neutralInvertBase);
  const overlayPrefix = `rgba(${baseInvertBackgroundRgb.slice(0, 3).join(", ")}, `;
  function overlay(alpha) {
    return `${overlayPrefix + String(alpha)})`;
  }
  function neutral(alpha) {
    const overlayRgba = Array.from(baseInvertBackgroundRgb);
    overlayRgba[3] = Number(alpha);
    return composite(baseBackgroundRgb, overlayRgba);
  }
  const derived = Object.assign(Object.assign({
    name: "common"
  }, commonVariables$7), {
    baseColor: base.neutralBase,
    // primary color
    primaryColor: base.primaryDefault,
    primaryColorHover: base.primaryHover,
    primaryColorPressed: base.primaryActive,
    primaryColorSuppl: base.primarySuppl,
    // info color
    infoColor: base.infoDefault,
    infoColorHover: base.infoHover,
    infoColorPressed: base.infoActive,
    infoColorSuppl: base.infoSuppl,
    // success color
    successColor: base.successDefault,
    successColorHover: base.successHover,
    successColorPressed: base.successActive,
    successColorSuppl: base.successSuppl,
    // warning color
    warningColor: base.warningDefault,
    warningColorHover: base.warningHover,
    warningColorPressed: base.warningActive,
    warningColorSuppl: base.warningSuppl,
    // error color
    errorColor: base.errorDefault,
    errorColorHover: base.errorHover,
    errorColorPressed: base.errorActive,
    errorColorSuppl: base.errorSuppl,
    // text color
    textColorBase: base.neutralTextBase,
    textColor1: "rgb(31, 34, 37)",
    textColor2: "rgb(51, 54, 57)",
    textColor3: "rgb(118, 124, 130)",
    // textColor4: neutral(base.alpha4), // disabled, placeholder, icon
    // textColor5: neutral(base.alpha5),
    textColorDisabled: neutral(base.alpha4),
    placeholderColor: neutral(base.alpha4),
    placeholderColorDisabled: neutral(base.alpha5),
    iconColor: neutral(base.alpha4),
    iconColorHover: scaleColor(neutral(base.alpha4), {
      lightness: 0.75
    }),
    iconColorPressed: scaleColor(neutral(base.alpha4), {
      lightness: 0.9
    }),
    iconColorDisabled: neutral(base.alpha5),
    opacity1: base.alpha1,
    opacity2: base.alpha2,
    opacity3: base.alpha3,
    opacity4: base.alpha4,
    opacity5: base.alpha5,
    dividerColor: "rgb(239, 239, 245)",
    borderColor: "rgb(224, 224, 230)",
    // close
    closeIconColor: neutral(Number(base.alphaClose)),
    closeIconColorHover: neutral(Number(base.alphaClose)),
    closeIconColorPressed: neutral(Number(base.alphaClose)),
    closeColorHover: "rgba(0, 0, 0, .09)",
    closeColorPressed: "rgba(0, 0, 0, .13)",
    // clear
    clearColor: neutral(base.alpha4),
    clearColorHover: scaleColor(neutral(base.alpha4), {
      lightness: 0.75
    }),
    clearColorPressed: scaleColor(neutral(base.alpha4), {
      lightness: 0.9
    }),
    scrollbarColor: overlay(base.alphaScrollbar),
    scrollbarColorHover: overlay(base.alphaScrollbarHover),
    scrollbarWidth: "5px",
    scrollbarHeight: "5px",
    scrollbarBorderRadius: "5px",
    progressRailColor: neutral(base.alphaProgressRail),
    railColor: "rgb(219, 219, 223)",
    popoverColor: base.neutralPopover,
    tableColor: base.neutralCard,
    cardColor: base.neutralCard,
    modalColor: base.neutralModal,
    bodyColor: base.neutralBody,
    tagColor: "#eee",
    avatarColor: neutral(base.alphaAvatar),
    invertedColor: "rgb(0, 20, 40)",
    inputColor: neutral(base.alphaInput),
    codeColor: "rgb(244, 244, 248)",
    tabColor: "rgb(247, 247, 250)",
    actionColor: "rgb(250, 250, 252)",
    tableHeaderColor: "rgb(250, 250, 252)",
    hoverColor: "rgb(243, 243, 245)",
    // use color with alpha since it can be nested with header filter & sorter effect
    tableColorHover: "rgba(0, 0, 100, 0.03)",
    tableColorStriped: "rgba(0, 0, 100, 0.02)",
    pressedColor: "rgb(237, 237, 239)",
    opacityDisabled: base.alphaDisabled,
    inputColorDisabled: "rgb(250, 250, 252)",
    // secondary button color
    // can also be used in tertiary button & quaternary button
    buttonColor2: "rgba(46, 51, 56, .05)",
    buttonColor2Hover: "rgba(46, 51, 56, .09)",
    buttonColor2Pressed: "rgba(46, 51, 56, .13)",
    boxShadow1: "0 1px 2px -2px rgba(0, 0, 0, .08), 0 3px 6px 0 rgba(0, 0, 0, .06), 0 5px 12px 4px rgba(0, 0, 0, .04)",
    boxShadow2: "0 3px 6px -4px rgba(0, 0, 0, .12), 0 6px 16px 0 rgba(0, 0, 0, .08), 0 9px 28px 8px rgba(0, 0, 0, .05)",
    boxShadow3: "0 6px 16px -9px rgba(0, 0, 0, .08), 0 9px 28px 0 rgba(0, 0, 0, .05), 0 12px 48px 16px rgba(0, 0, 0, .03)"
  });
  const commonVars$5 = {
    railInsetHorizontalBottom: "auto 2px 4px 2px",
    railInsetHorizontalTop: "4px 2px auto 2px",
    railInsetVerticalRight: "2px 4px 2px auto",
    railInsetVerticalLeft: "2px auto 2px 4px",
    railColor: "transparent"
  };
  function self$h(vars) {
    const {
      scrollbarColor,
      scrollbarColorHover,
      scrollbarHeight,
      scrollbarWidth,
      scrollbarBorderRadius
    } = vars;
    return Object.assign(Object.assign({}, commonVars$5), {
      height: scrollbarHeight,
      width: scrollbarWidth,
      borderRadius: scrollbarBorderRadius,
      color: scrollbarColor,
      colorHover: scrollbarColorHover
    });
  }
  const scrollbarLight = {
    name: "Scrollbar",
    common: derived,
    self: self$h
  };
  const style$i = cB("scrollbar", `
 overflow: hidden;
 position: relative;
 z-index: auto;
 height: 100%;
 width: 100%;
`, [c$1(">", [cB("scrollbar-container", `
 width: 100%;
 overflow: scroll;
 height: 100%;
 min-height: inherit;
 max-height: inherit;
 scrollbar-width: none;
 `, [c$1("&::-webkit-scrollbar, &::-webkit-scrollbar-track-piece, &::-webkit-scrollbar-thumb", `
 width: 0;
 height: 0;
 display: none;
 `), c$1(">", [
    // We can't set overflow hidden since it affects positioning.
    cB("scrollbar-content", `
 box-sizing: border-box;
 min-width: 100%;
 `)
  ])])]), c$1(">, +", [cB("scrollbar-rail", `
 position: absolute;
 pointer-events: none;
 user-select: none;
 background: var(--n-scrollbar-rail-color);
 -webkit-user-select: none;
 `, [cM("horizontal", `
 height: var(--n-scrollbar-height);
 `, [c$1(">", [cE("scrollbar", `
 height: var(--n-scrollbar-height);
 border-radius: var(--n-scrollbar-border-radius);
 right: 0;
 `)])]), cM("horizontal--top", `
 top: var(--n-scrollbar-rail-top-horizontal-top); 
 right: var(--n-scrollbar-rail-right-horizontal-top); 
 bottom: var(--n-scrollbar-rail-bottom-horizontal-top); 
 left: var(--n-scrollbar-rail-left-horizontal-top); 
 `), cM("horizontal--bottom", `
 top: var(--n-scrollbar-rail-top-horizontal-bottom); 
 right: var(--n-scrollbar-rail-right-horizontal-bottom); 
 bottom: var(--n-scrollbar-rail-bottom-horizontal-bottom); 
 left: var(--n-scrollbar-rail-left-horizontal-bottom); 
 `), cM("vertical", `
 width: var(--n-scrollbar-width);
 `, [c$1(">", [cE("scrollbar", `
 width: var(--n-scrollbar-width);
 border-radius: var(--n-scrollbar-border-radius);
 bottom: 0;
 `)])]), cM("vertical--left", `
 top: var(--n-scrollbar-rail-top-vertical-left); 
 right: var(--n-scrollbar-rail-right-vertical-left); 
 bottom: var(--n-scrollbar-rail-bottom-vertical-left); 
 left: var(--n-scrollbar-rail-left-vertical-left); 
 `), cM("vertical--right", `
 top: var(--n-scrollbar-rail-top-vertical-right); 
 right: var(--n-scrollbar-rail-right-vertical-right); 
 bottom: var(--n-scrollbar-rail-bottom-vertical-right); 
 left: var(--n-scrollbar-rail-left-vertical-right); 
 `), cM("disabled", [c$1(">", [cE("scrollbar", "pointer-events: none;")])]), c$1(">", [cE("scrollbar", `
 z-index: 1;
 position: absolute;
 cursor: pointer;
 pointer-events: all;
 background-color: var(--n-scrollbar-color);
 transition: background-color .2s var(--n-scrollbar-bezier);
 `, [fadeInTransition(), c$1("&:hover", "background-color: var(--n-scrollbar-color-hover);")])])])])]);
  const scrollbarProps = Object.assign(Object.assign({}, useTheme.props), {
    duration: {
      type: Number,
      default: 0
    },
    scrollable: {
      type: Boolean,
      default: true
    },
    xScrollable: Boolean,
    trigger: {
      type: String,
      default: "hover"
    },
    useUnifiedContainer: Boolean,
    triggerDisplayManually: Boolean,
    // If container is set, resize observer won't not attached
    container: Function,
    content: Function,
    containerClass: String,
    containerStyle: [String, Object],
    contentClass: [String, Array],
    contentStyle: [String, Object],
    horizontalRailStyle: [String, Object],
    verticalRailStyle: [String, Object],
    onScroll: Function,
    onWheel: Function,
    onResize: Function,
    internalOnUpdateScrollLeft: Function,
    internalHoistYRail: Boolean,
    internalExposeWidthCssVar: Boolean,
    yPlacement: {
      type: String,
      default: "right"
    },
    xPlacement: {
      type: String,
      default: "bottom"
    }
  });
  const Scrollbar = /* @__PURE__ */ defineComponent({
    name: "Scrollbar",
    props: scrollbarProps,
    inheritAttrs: false,
    setup(props) {
      const {
        mergedClsPrefixRef,
        inlineThemeDisabled,
        mergedRtlRef
      } = useConfig(props);
      const rtlEnabledRef = useRtl("Scrollbar", mergedRtlRef, mergedClsPrefixRef);
      const wrapperRef = /* @__PURE__ */ ref(null);
      const containerRef = /* @__PURE__ */ ref(null);
      const contentRef = /* @__PURE__ */ ref(null);
      const yRailRef = /* @__PURE__ */ ref(null);
      const xRailRef = /* @__PURE__ */ ref(null);
      const contentHeightRef = /* @__PURE__ */ ref(null);
      const contentWidthRef = /* @__PURE__ */ ref(null);
      const containerHeightRef = /* @__PURE__ */ ref(null);
      const containerWidthRef = /* @__PURE__ */ ref(null);
      const yRailSizeRef = /* @__PURE__ */ ref(null);
      const xRailSizeRef = /* @__PURE__ */ ref(null);
      const containerScrollTopRef = /* @__PURE__ */ ref(0);
      const containerScrollLeftRef = /* @__PURE__ */ ref(0);
      const isShowXBarRef = /* @__PURE__ */ ref(false);
      const isShowYBarRef = /* @__PURE__ */ ref(false);
      let yBarPressed = false;
      let xBarPressed = false;
      let xBarVanishTimerId;
      let yBarVanishTimerId;
      let memoYTop = 0;
      let memoXLeft = 0;
      let memoMouseX = 0;
      let memoMouseY = 0;
      const isIos2 = useIsIos();
      const themeRef = useTheme("Scrollbar", "-scrollbar", style$i, scrollbarLight, props, mergedClsPrefixRef);
      const yBarSizeRef = computed(() => {
        const {
          value: containerHeight
        } = containerHeightRef;
        const {
          value: contentHeight
        } = contentHeightRef;
        const {
          value: yRailSize
        } = yRailSizeRef;
        if (containerHeight === null || contentHeight === null || yRailSize === null) {
          return 0;
        } else {
          return Math.min(containerHeight, yRailSize * containerHeight / contentHeight + depx(themeRef.value.self.width) * 1.5);
        }
      });
      const yBarSizePxRef = computed(() => {
        return `${yBarSizeRef.value}px`;
      });
      const xBarSizeRef = computed(() => {
        const {
          value: containerWidth
        } = containerWidthRef;
        const {
          value: contentWidth
        } = contentWidthRef;
        const {
          value: xRailSize
        } = xRailSizeRef;
        if (containerWidth === null || contentWidth === null || xRailSize === null) {
          return 0;
        } else {
          return xRailSize * containerWidth / contentWidth + depx(themeRef.value.self.height) * 1.5;
        }
      });
      const xBarSizePxRef = computed(() => {
        return `${xBarSizeRef.value}px`;
      });
      const yBarTopRef = computed(() => {
        const {
          value: containerHeight
        } = containerHeightRef;
        const {
          value: containerScrollTop
        } = containerScrollTopRef;
        const {
          value: contentHeight
        } = contentHeightRef;
        const {
          value: yRailSize
        } = yRailSizeRef;
        if (containerHeight === null || contentHeight === null || yRailSize === null) {
          return 0;
        } else {
          const heightDiff = contentHeight - containerHeight;
          if (!heightDiff) return 0;
          return containerScrollTop / heightDiff * (yRailSize - yBarSizeRef.value);
        }
      });
      const yBarTopPxRef = computed(() => {
        return `${yBarTopRef.value}px`;
      });
      const xBarLeftRef = computed(() => {
        const {
          value: containerWidth
        } = containerWidthRef;
        const {
          value: containerScrollLeft
        } = containerScrollLeftRef;
        const {
          value: contentWidth
        } = contentWidthRef;
        const {
          value: xRailSize
        } = xRailSizeRef;
        if (containerWidth === null || contentWidth === null || xRailSize === null) {
          return 0;
        } else {
          const widthDiff = contentWidth - containerWidth;
          if (!widthDiff) return 0;
          return containerScrollLeft / widthDiff * (xRailSize - xBarSizeRef.value);
        }
      });
      const xBarLeftPxRef = computed(() => {
        return `${xBarLeftRef.value}px`;
      });
      const needYBarRef = computed(() => {
        const {
          value: containerHeight
        } = containerHeightRef;
        const {
          value: contentHeight
        } = contentHeightRef;
        return containerHeight !== null && contentHeight !== null && contentHeight > containerHeight;
      });
      const needXBarRef = computed(() => {
        const {
          value: containerWidth
        } = containerWidthRef;
        const {
          value: contentWidth
        } = contentWidthRef;
        return containerWidth !== null && contentWidth !== null && contentWidth > containerWidth;
      });
      const mergedShowXBarRef = computed(() => {
        const {
          trigger: trigger2
        } = props;
        return trigger2 === "none" || isShowXBarRef.value;
      });
      const mergedShowYBarRef = computed(() => {
        const {
          trigger: trigger2
        } = props;
        return trigger2 === "none" || isShowYBarRef.value;
      });
      const mergedContainerRef = computed(() => {
        const {
          container
        } = props;
        if (container) return container();
        return containerRef.value;
      });
      const mergedContentRef = computed(() => {
        const {
          content
        } = props;
        if (content) return content();
        return contentRef.value;
      });
      const scrollTo = (options, y) => {
        if (!props.scrollable) return;
        if (typeof options === "number") {
          scrollToPosition(options, y !== null && y !== void 0 ? y : 0, 0, false, "auto");
          return;
        }
        const {
          left,
          top,
          index,
          elSize,
          position,
          behavior,
          el,
          debounce = true
        } = options;
        if (left !== void 0 || top !== void 0) {
          scrollToPosition(left !== null && left !== void 0 ? left : 0, top !== null && top !== void 0 ? top : 0, 0, false, behavior);
        }
        if (el !== void 0) {
          scrollToPosition(0, el.offsetTop, el.offsetHeight, debounce, behavior);
        } else if (index !== void 0 && elSize !== void 0) {
          scrollToPosition(0, index * elSize, elSize, debounce, behavior);
        } else if (position === "bottom") {
          scrollToPosition(0, Number.MAX_SAFE_INTEGER, 0, false, behavior);
        } else if (position === "top") {
          scrollToPosition(0, 0, 0, false, behavior);
        }
      };
      const activateState = useReactivated(() => {
        if (!props.container) {
          scrollTo({
            top: containerScrollTopRef.value,
            left: containerScrollLeftRef.value
          });
        }
      });
      const handleContentResize = () => {
        if (activateState.isDeactivated) return;
        sync();
      };
      const handleContainerResize = (e) => {
        if (activateState.isDeactivated) return;
        const {
          onResize
        } = props;
        if (onResize) onResize(e);
        sync();
      };
      const scrollBy = (options, y) => {
        if (!props.scrollable) return;
        const {
          value: container
        } = mergedContainerRef;
        if (!container) return;
        if (typeof options === "object") {
          container.scrollBy(options);
        } else {
          container.scrollBy(options, y || 0);
        }
      };
      function scrollToPosition(left, top, elSize, debounce, behavior) {
        const {
          value: container
        } = mergedContainerRef;
        if (!container) return;
        if (debounce) {
          const {
            scrollTop,
            offsetHeight
          } = container;
          if (top > scrollTop) {
            if (top + elSize <= scrollTop + offsetHeight) ;
            else {
              container.scrollTo({
                left,
                top: top + elSize - offsetHeight,
                behavior
              });
            }
            return;
          }
        }
        container.scrollTo({
          left,
          top,
          behavior
        });
      }
      function handleMouseEnterWrapper() {
        showXBar();
        showYBar();
        sync();
      }
      function handleMouseLeaveWrapper() {
        hideBar();
      }
      function hideBar() {
        hideYBar();
        hideXBar();
      }
      function hideYBar() {
        if (yBarVanishTimerId !== void 0) {
          window.clearTimeout(yBarVanishTimerId);
        }
        yBarVanishTimerId = window.setTimeout(() => {
          isShowYBarRef.value = false;
        }, props.duration);
      }
      function hideXBar() {
        if (xBarVanishTimerId !== void 0) {
          window.clearTimeout(xBarVanishTimerId);
        }
        xBarVanishTimerId = window.setTimeout(() => {
          isShowXBarRef.value = false;
        }, props.duration);
      }
      function showXBar() {
        if (xBarVanishTimerId !== void 0) {
          window.clearTimeout(xBarVanishTimerId);
        }
        isShowXBarRef.value = true;
      }
      function showYBar() {
        if (yBarVanishTimerId !== void 0) {
          window.clearTimeout(yBarVanishTimerId);
        }
        isShowYBarRef.value = true;
      }
      function handleScroll(e) {
        const {
          onScroll
        } = props;
        if (onScroll) onScroll(e);
        syncScrollState();
      }
      function syncScrollState() {
        const {
          value: container
        } = mergedContainerRef;
        if (container) {
          containerScrollTopRef.value = container.scrollTop;
          containerScrollLeftRef.value = container.scrollLeft * ((rtlEnabledRef === null || rtlEnabledRef === void 0 ? void 0 : rtlEnabledRef.value) ? -1 : 1);
        }
      }
      function syncPositionState() {
        const {
          value: content
        } = mergedContentRef;
        if (content) {
          contentHeightRef.value = content.offsetHeight;
          contentWidthRef.value = content.offsetWidth;
        }
        const {
          value: container
        } = mergedContainerRef;
        if (container) {
          containerHeightRef.value = container.offsetHeight;
          containerWidthRef.value = container.offsetWidth;
        }
        const {
          value: xRailEl
        } = xRailRef;
        const {
          value: yRailEl
        } = yRailRef;
        if (xRailEl) {
          xRailSizeRef.value = xRailEl.offsetWidth;
        }
        if (yRailEl) {
          yRailSizeRef.value = yRailEl.offsetHeight;
        }
      }
      function syncUnifiedContainer() {
        const {
          value: container
        } = mergedContainerRef;
        if (container) {
          containerScrollTopRef.value = container.scrollTop;
          containerScrollLeftRef.value = container.scrollLeft * ((rtlEnabledRef === null || rtlEnabledRef === void 0 ? void 0 : rtlEnabledRef.value) ? -1 : 1);
          containerHeightRef.value = container.offsetHeight;
          containerWidthRef.value = container.offsetWidth;
          contentHeightRef.value = container.scrollHeight;
          contentWidthRef.value = container.scrollWidth;
        }
        const {
          value: xRailEl
        } = xRailRef;
        const {
          value: yRailEl
        } = yRailRef;
        if (xRailEl) {
          xRailSizeRef.value = xRailEl.offsetWidth;
        }
        if (yRailEl) {
          yRailSizeRef.value = yRailEl.offsetHeight;
        }
      }
      function sync() {
        if (!props.scrollable) return;
        if (props.useUnifiedContainer) {
          syncUnifiedContainer();
        } else {
          syncPositionState();
          syncScrollState();
        }
      }
      function isMouseUpAway(e) {
        var _a2;
        return !((_a2 = wrapperRef.value) === null || _a2 === void 0 ? void 0 : _a2.contains(getPreciseEventTarget(e)));
      }
      function handleXScrollMouseDown(e) {
        e.preventDefault();
        e.stopPropagation();
        xBarPressed = true;
        on("mousemove", window, handleXScrollMouseMove, true);
        on("mouseup", window, handleXScrollMouseUp, true);
        memoXLeft = containerScrollLeftRef.value;
        memoMouseX = (rtlEnabledRef === null || rtlEnabledRef === void 0 ? void 0 : rtlEnabledRef.value) ? window.innerWidth - e.clientX : e.clientX;
      }
      function handleXScrollMouseMove(e) {
        if (!xBarPressed) return;
        if (xBarVanishTimerId !== void 0) {
          window.clearTimeout(xBarVanishTimerId);
        }
        if (yBarVanishTimerId !== void 0) {
          window.clearTimeout(yBarVanishTimerId);
        }
        const {
          value: containerWidth
        } = containerWidthRef;
        const {
          value: contentWidth
        } = contentWidthRef;
        const {
          value: xBarSize
        } = xBarSizeRef;
        if (containerWidth === null || contentWidth === null) return;
        const dX = (rtlEnabledRef === null || rtlEnabledRef === void 0 ? void 0 : rtlEnabledRef.value) ? window.innerWidth - e.clientX - memoMouseX : e.clientX - memoMouseX;
        const dScrollLeft = dX * (contentWidth - containerWidth) / (containerWidth - xBarSize);
        const toScrollLeftUpperBound = contentWidth - containerWidth;
        let toScrollLeft = memoXLeft + dScrollLeft;
        toScrollLeft = Math.min(toScrollLeftUpperBound, toScrollLeft);
        toScrollLeft = Math.max(toScrollLeft, 0);
        const {
          value: container
        } = mergedContainerRef;
        if (container) {
          container.scrollLeft = toScrollLeft * ((rtlEnabledRef === null || rtlEnabledRef === void 0 ? void 0 : rtlEnabledRef.value) ? -1 : 1);
          const {
            internalOnUpdateScrollLeft
          } = props;
          if (internalOnUpdateScrollLeft) internalOnUpdateScrollLeft(toScrollLeft);
        }
      }
      function handleXScrollMouseUp(e) {
        e.preventDefault();
        e.stopPropagation();
        off("mousemove", window, handleXScrollMouseMove, true);
        off("mouseup", window, handleXScrollMouseUp, true);
        xBarPressed = false;
        sync();
        if (isMouseUpAway(e)) {
          hideBar();
        }
      }
      function handleYScrollMouseDown(e) {
        e.preventDefault();
        e.stopPropagation();
        yBarPressed = true;
        on("mousemove", window, handleYScrollMouseMove, true);
        on("mouseup", window, handleYScrollMouseUp, true);
        memoYTop = containerScrollTopRef.value;
        memoMouseY = e.clientY;
      }
      function handleYScrollMouseMove(e) {
        if (!yBarPressed) return;
        if (xBarVanishTimerId !== void 0) {
          window.clearTimeout(xBarVanishTimerId);
        }
        if (yBarVanishTimerId !== void 0) {
          window.clearTimeout(yBarVanishTimerId);
        }
        const {
          value: containerHeight
        } = containerHeightRef;
        const {
          value: contentHeight
        } = contentHeightRef;
        const {
          value: yBarSize
        } = yBarSizeRef;
        if (containerHeight === null || contentHeight === null) return;
        const dY = e.clientY - memoMouseY;
        const dScrollTop = dY * (contentHeight - containerHeight) / (containerHeight - yBarSize);
        const toScrollTopUpperBound = contentHeight - containerHeight;
        let toScrollTop = memoYTop + dScrollTop;
        toScrollTop = Math.min(toScrollTopUpperBound, toScrollTop);
        toScrollTop = Math.max(toScrollTop, 0);
        const {
          value: container
        } = mergedContainerRef;
        if (container) {
          container.scrollTop = toScrollTop;
        }
      }
      function handleYScrollMouseUp(e) {
        e.preventDefault();
        e.stopPropagation();
        off("mousemove", window, handleYScrollMouseMove, true);
        off("mouseup", window, handleYScrollMouseUp, true);
        yBarPressed = false;
        sync();
        if (isMouseUpAway(e)) {
          hideBar();
        }
      }
      watchEffect(() => {
        const {
          value: needXBar
        } = needXBarRef;
        const {
          value: needYBar
        } = needYBarRef;
        const {
          value: mergedClsPrefix
        } = mergedClsPrefixRef;
        const {
          value: xRailEl
        } = xRailRef;
        const {
          value: yRailEl
        } = yRailRef;
        if (xRailEl) {
          if (!needXBar) {
            xRailEl.classList.add(`${mergedClsPrefix}-scrollbar-rail--disabled`);
          } else {
            xRailEl.classList.remove(`${mergedClsPrefix}-scrollbar-rail--disabled`);
          }
        }
        if (yRailEl) {
          if (!needYBar) {
            yRailEl.classList.add(`${mergedClsPrefix}-scrollbar-rail--disabled`);
          } else {
            yRailEl.classList.remove(`${mergedClsPrefix}-scrollbar-rail--disabled`);
          }
        }
      });
      onMounted(() => {
        if (props.container) return;
        sync();
      });
      onBeforeUnmount(() => {
        if (xBarVanishTimerId !== void 0) {
          window.clearTimeout(xBarVanishTimerId);
        }
        if (yBarVanishTimerId !== void 0) {
          window.clearTimeout(yBarVanishTimerId);
        }
        off("mousemove", window, handleYScrollMouseMove, true);
        off("mouseup", window, handleYScrollMouseUp, true);
      });
      const cssVarsRef = computed(() => {
        const {
          common: {
            cubicBezierEaseInOut: cubicBezierEaseInOut2
          },
          self: {
            color,
            colorHover,
            height,
            width,
            borderRadius,
            railInsetHorizontalTop,
            railInsetHorizontalBottom,
            railInsetVerticalRight,
            railInsetVerticalLeft,
            railColor
          }
        } = themeRef.value;
        const {
          top: railTopHorizontalTop,
          right: railRightHorizontalTop,
          bottom: railBottomHorizontalTop,
          left: railLeftHorizontalTop
        } = getMargin(railInsetHorizontalTop);
        const {
          top: railTopHorizontalBottom,
          right: railRightHorizontalBottom,
          bottom: railBottomHorizontalBottom,
          left: railLeftHorizontalBottom
        } = getMargin(railInsetHorizontalBottom);
        const {
          top: railTopVerticalRight,
          right: railRightVerticalRight,
          bottom: railBottomVerticalRight,
          left: railLeftVerticalRight
        } = getMargin((rtlEnabledRef === null || rtlEnabledRef === void 0 ? void 0 : rtlEnabledRef.value) ? rtlInset(railInsetVerticalRight) : railInsetVerticalRight);
        const {
          top: railTopVerticalLeft,
          right: railRightVerticalLeft,
          bottom: railBottomVerticalLeft,
          left: railLeftVerticalLeft
        } = getMargin((rtlEnabledRef === null || rtlEnabledRef === void 0 ? void 0 : rtlEnabledRef.value) ? rtlInset(railInsetVerticalLeft) : railInsetVerticalLeft);
        return {
          "--n-scrollbar-bezier": cubicBezierEaseInOut2,
          "--n-scrollbar-color": color,
          "--n-scrollbar-color-hover": colorHover,
          "--n-scrollbar-border-radius": borderRadius,
          "--n-scrollbar-width": width,
          "--n-scrollbar-height": height,
          "--n-scrollbar-rail-top-horizontal-top": railTopHorizontalTop,
          "--n-scrollbar-rail-right-horizontal-top": railRightHorizontalTop,
          "--n-scrollbar-rail-bottom-horizontal-top": railBottomHorizontalTop,
          "--n-scrollbar-rail-left-horizontal-top": railLeftHorizontalTop,
          "--n-scrollbar-rail-top-horizontal-bottom": railTopHorizontalBottom,
          "--n-scrollbar-rail-right-horizontal-bottom": railRightHorizontalBottom,
          "--n-scrollbar-rail-bottom-horizontal-bottom": railBottomHorizontalBottom,
          "--n-scrollbar-rail-left-horizontal-bottom": railLeftHorizontalBottom,
          "--n-scrollbar-rail-top-vertical-right": railTopVerticalRight,
          "--n-scrollbar-rail-right-vertical-right": railRightVerticalRight,
          "--n-scrollbar-rail-bottom-vertical-right": railBottomVerticalRight,
          "--n-scrollbar-rail-left-vertical-right": railLeftVerticalRight,
          "--n-scrollbar-rail-top-vertical-left": railTopVerticalLeft,
          "--n-scrollbar-rail-right-vertical-left": railRightVerticalLeft,
          "--n-scrollbar-rail-bottom-vertical-left": railBottomVerticalLeft,
          "--n-scrollbar-rail-left-vertical-left": railLeftVerticalLeft,
          "--n-scrollbar-rail-color": railColor
        };
      });
      const themeClassHandle = inlineThemeDisabled ? useThemeClass("scrollbar", void 0, cssVarsRef, props) : void 0;
      const exposedMethods = {
        scrollTo,
        scrollBy,
        sync,
        syncUnifiedContainer,
        handleMouseEnterWrapper,
        handleMouseLeaveWrapper
      };
      return Object.assign(Object.assign({}, exposedMethods), {
        mergedClsPrefix: mergedClsPrefixRef,
        rtlEnabled: rtlEnabledRef,
        containerScrollTop: containerScrollTopRef,
        wrapperRef,
        containerRef,
        contentRef,
        yRailRef,
        xRailRef,
        needYBar: needYBarRef,
        needXBar: needXBarRef,
        yBarSizePx: yBarSizePxRef,
        xBarSizePx: xBarSizePxRef,
        yBarTopPx: yBarTopPxRef,
        xBarLeftPx: xBarLeftPxRef,
        isShowXBar: mergedShowXBarRef,
        isShowYBar: mergedShowYBarRef,
        isIos: isIos2,
        handleScroll,
        handleContentResize,
        handleContainerResize,
        handleYScrollMouseDown,
        handleXScrollMouseDown,
        containerWidth: containerWidthRef,
        cssVars: inlineThemeDisabled ? void 0 : cssVarsRef,
        themeClass: themeClassHandle === null || themeClassHandle === void 0 ? void 0 : themeClassHandle.themeClass,
        onRender: themeClassHandle === null || themeClassHandle === void 0 ? void 0 : themeClassHandle.onRender
      });
    },
    render() {
      var _a2;
      const {
        $slots,
        mergedClsPrefix,
        triggerDisplayManually,
        rtlEnabled,
        internalHoistYRail,
        yPlacement,
        xPlacement,
        xScrollable
      } = this;
      if (!this.scrollable) return (_a2 = $slots.default) === null || _a2 === void 0 ? void 0 : _a2.call($slots);
      const triggerIsNone = this.trigger === "none";
      const createYRail = (className, style2) => {
        return h("div", {
          ref: "yRailRef",
          class: [`${mergedClsPrefix}-scrollbar-rail`, `${mergedClsPrefix}-scrollbar-rail--vertical`, `${mergedClsPrefix}-scrollbar-rail--vertical--${yPlacement}`, className],
          "data-scrollbar-rail": true,
          style: [style2 || "", this.verticalRailStyle],
          "aria-hidden": true
        }, h(triggerIsNone ? Wrapper : Transition, triggerIsNone ? null : {
          name: "fade-in-transition"
        }, {
          default: () => this.needYBar && this.isShowYBar && !this.isIos ? h("div", {
            class: `${mergedClsPrefix}-scrollbar-rail__scrollbar`,
            style: {
              height: this.yBarSizePx,
              top: this.yBarTopPx
            },
            onMousedown: this.handleYScrollMouseDown
          }) : null
        }));
      };
      const createChildren = () => {
        var _a3, _b;
        (_a3 = this.onRender) === null || _a3 === void 0 ? void 0 : _a3.call(this);
        return h("div", mergeProps(this.$attrs, {
          role: "none",
          ref: "wrapperRef",
          class: [`${mergedClsPrefix}-scrollbar`, this.themeClass, rtlEnabled && `${mergedClsPrefix}-scrollbar--rtl`],
          style: this.cssVars,
          onMouseenter: triggerDisplayManually ? void 0 : this.handleMouseEnterWrapper,
          onMouseleave: triggerDisplayManually ? void 0 : this.handleMouseLeaveWrapper
        }), [this.container ? (_b = $slots.default) === null || _b === void 0 ? void 0 : _b.call($slots) : h("div", {
          role: "none",
          ref: "containerRef",
          class: [`${mergedClsPrefix}-scrollbar-container`, this.containerClass],
          style: [this.containerStyle, this.internalExposeWidthCssVar ? {
            "--n-scrollbar-current-width": pxfy(this.containerWidth)
          } : void 0],
          onScroll: this.handleScroll,
          onWheel: this.onWheel
        }, h(VResizeObserver, {
          onResize: this.handleContentResize
        }, {
          default: () => h("div", {
            ref: "contentRef",
            role: "none",
            style: [{
              width: this.xScrollable ? "fit-content" : null
            }, this.contentStyle],
            class: [`${mergedClsPrefix}-scrollbar-content`, this.contentClass]
          }, $slots)
        })), internalHoistYRail ? null : createYRail(void 0, void 0), xScrollable && h("div", {
          ref: "xRailRef",
          class: [`${mergedClsPrefix}-scrollbar-rail`, `${mergedClsPrefix}-scrollbar-rail--horizontal`, `${mergedClsPrefix}-scrollbar-rail--horizontal--${xPlacement}`],
          style: this.horizontalRailStyle,
          "data-scrollbar-rail": true,
          "aria-hidden": true
        }, h(triggerIsNone ? Wrapper : Transition, triggerIsNone ? null : {
          name: "fade-in-transition"
        }, {
          default: () => this.needXBar && this.isShowXBar && !this.isIos ? h("div", {
            class: `${mergedClsPrefix}-scrollbar-rail__scrollbar`,
            style: {
              width: this.xBarSizePx,
              right: rtlEnabled ? this.xBarLeftPx : void 0,
              left: rtlEnabled ? void 0 : this.xBarLeftPx
            },
            onMousedown: this.handleXScrollMouseDown
          }) : null
        }))]);
      };
      const scrollbarNode = this.container ? createChildren() : h(VResizeObserver, {
        onResize: this.handleContainerResize
      }, {
        default: createChildren
      });
      if (internalHoistYRail) {
        return h(Fragment, null, scrollbarNode, createYRail(this.themeClass, this.cssVars));
      } else {
        return scrollbarNode;
      }
    }
  });
  const XScrollbar = Scrollbar;
  function toArray(arg) {
    if (Array.isArray(arg))
      return arg;
    return [arg];
  }
  const TRAVERSE_COMMAND = {
    STOP: "STOP"
  };
  function traverseWithCb(treeNode, callback) {
    const command = callback(treeNode);
    if (treeNode.children !== void 0 && command !== TRAVERSE_COMMAND.STOP) {
      treeNode.children.forEach((childNode) => traverseWithCb(childNode, callback));
    }
  }
  function getNonLeafKeys(treeNodes, options = {}) {
    const { preserveGroup = false } = options;
    const keys2 = [];
    const cb = preserveGroup ? (node) => {
      if (!node.isLeaf) {
        keys2.push(node.key);
        traverse2(node.children);
      }
    } : (node) => {
      if (!node.isLeaf) {
        if (!node.isGroup)
          keys2.push(node.key);
        traverse2(node.children);
      }
    };
    function traverse2(nodes) {
      nodes.forEach(cb);
    }
    traverse2(treeNodes);
    return keys2;
  }
  function isLeaf(rawNode, getChildren) {
    const { isLeaf: isLeaf2 } = rawNode;
    if (isLeaf2 !== void 0)
      return isLeaf2;
    else if (!getChildren(rawNode))
      return true;
    return false;
  }
  function defaultGetChildren(node) {
    return node.children;
  }
  function defaultGetKey(node) {
    return node.key;
  }
  function isIgnored() {
    return false;
  }
  function isShallowLoaded(rawNode, getChildren) {
    const { isLeaf: isLeaf2 } = rawNode;
    if (isLeaf2 === false && !Array.isArray(getChildren(rawNode)))
      return false;
    return true;
  }
  function isDisabled(rawNode) {
    return rawNode.disabled === true;
  }
  function isExpilicitlyNotLoaded(rawNode, getChildren) {
    return rawNode.isLeaf === false && !Array.isArray(getChildren(rawNode));
  }
  function unwrapCheckedKeys(result) {
    var _a2;
    if (result === void 0 || result === null)
      return [];
    if (Array.isArray(result))
      return result;
    return (_a2 = result.checkedKeys) !== null && _a2 !== void 0 ? _a2 : [];
  }
  function unwrapIndeterminateKeys(result) {
    var _a2;
    if (result === void 0 || result === null || Array.isArray(result)) {
      return [];
    }
    return (_a2 = result.indeterminateKeys) !== null && _a2 !== void 0 ? _a2 : [];
  }
  function merge(originalKeys, keysToAdd) {
    const set = new Set(originalKeys);
    keysToAdd.forEach((key) => {
      if (!set.has(key)) {
        set.add(key);
      }
    });
    return Array.from(set);
  }
  function minus(originalKeys, keysToRemove) {
    const set = new Set(originalKeys);
    keysToRemove.forEach((key) => {
      if (set.has(key)) {
        set.delete(key);
      }
    });
    return Array.from(set);
  }
  function isGroup(rawNode) {
    return (rawNode === null || rawNode === void 0 ? void 0 : rawNode.type) === "group";
  }
  class SubtreeNotLoadedError extends Error {
    constructor() {
      super();
      this.message = "SubtreeNotLoadedError: checking a subtree whose required nodes are not fully loaded.";
    }
  }
  function getExtendedCheckedKeySetAfterCheck(checkKeys, currentCheckedKeys, treeMate, allowNotLoaded) {
    return getExtendedCheckedKeySet(currentCheckedKeys.concat(checkKeys), treeMate, allowNotLoaded, false);
  }
  function getAvailableAscendantNodeSet(uncheckedKeys, treeMate) {
    const visitedKeys = /* @__PURE__ */ new Set();
    uncheckedKeys.forEach((uncheckedKey) => {
      const uncheckedTreeNode = treeMate.treeNodeMap.get(uncheckedKey);
      if (uncheckedTreeNode !== void 0) {
        let nodeCursor = uncheckedTreeNode.parent;
        while (nodeCursor !== null) {
          if (nodeCursor.disabled)
            break;
          if (visitedKeys.has(nodeCursor.key))
            break;
          else {
            visitedKeys.add(nodeCursor.key);
          }
          nodeCursor = nodeCursor.parent;
        }
      }
    });
    return visitedKeys;
  }
  function getExtendedCheckedKeySetAfterUncheck(uncheckedKeys, currentCheckedKeys, treeMate, allowNotLoaded) {
    const extendedCheckedKeySet = getExtendedCheckedKeySet(currentCheckedKeys, treeMate, allowNotLoaded, false);
    const extendedKeySetToUncheck = getExtendedCheckedKeySet(uncheckedKeys, treeMate, allowNotLoaded, true);
    const ascendantKeySet = getAvailableAscendantNodeSet(uncheckedKeys, treeMate);
    const keysToRemove = [];
    extendedCheckedKeySet.forEach((key) => {
      if (extendedKeySetToUncheck.has(key) || ascendantKeySet.has(key)) {
        keysToRemove.push(key);
      }
    });
    keysToRemove.forEach((key) => extendedCheckedKeySet.delete(key));
    return extendedCheckedKeySet;
  }
  function getCheckedKeys(options, treeMate) {
    const { checkedKeys, keysToCheck, keysToUncheck, indeterminateKeys, cascade, leafOnly, checkStrategy, allowNotLoaded } = options;
    if (!cascade) {
      if (keysToCheck !== void 0) {
        return {
          checkedKeys: merge(checkedKeys, keysToCheck),
          indeterminateKeys: Array.from(indeterminateKeys)
        };
      } else if (keysToUncheck !== void 0) {
        return {
          checkedKeys: minus(checkedKeys, keysToUncheck),
          indeterminateKeys: Array.from(indeterminateKeys)
        };
      } else {
        return {
          checkedKeys: Array.from(checkedKeys),
          indeterminateKeys: Array.from(indeterminateKeys)
        };
      }
    }
    const { levelTreeNodeMap } = treeMate;
    let extendedCheckedKeySet;
    if (keysToUncheck !== void 0) {
      extendedCheckedKeySet = getExtendedCheckedKeySetAfterUncheck(keysToUncheck, checkedKeys, treeMate, allowNotLoaded);
    } else if (keysToCheck !== void 0) {
      extendedCheckedKeySet = getExtendedCheckedKeySetAfterCheck(keysToCheck, checkedKeys, treeMate, allowNotLoaded);
    } else {
      extendedCheckedKeySet = getExtendedCheckedKeySet(checkedKeys, treeMate, allowNotLoaded, false);
    }
    const checkStrategyIsParent = checkStrategy === "parent";
    const checkStrategyIsChild = checkStrategy === "child" || leafOnly;
    const syntheticCheckedKeySet = extendedCheckedKeySet;
    const syntheticIndeterminateKeySet = /* @__PURE__ */ new Set();
    const maxLevel = Math.max.apply(null, Array.from(levelTreeNodeMap.keys()));
    for (let level = maxLevel; level >= 0; level -= 1) {
      const levelIsZero = level === 0;
      const levelTreeNodes = levelTreeNodeMap.get(level);
      for (const levelTreeNode of levelTreeNodes) {
        if (levelTreeNode.isLeaf)
          continue;
        const { key: levelTreeNodeKey, shallowLoaded } = levelTreeNode;
        if (checkStrategyIsChild && shallowLoaded) {
          levelTreeNode.children.forEach((v) => {
            if (!v.disabled && !v.isLeaf && v.shallowLoaded && syntheticCheckedKeySet.has(v.key)) {
              syntheticCheckedKeySet.delete(v.key);
            }
          });
        }
        if (levelTreeNode.disabled || !shallowLoaded) {
          continue;
        }
        let fullyChecked = true;
        let partialChecked = false;
        let allDisabled = true;
        for (const childNode of levelTreeNode.children) {
          const childKey = childNode.key;
          if (childNode.disabled)
            continue;
          if (allDisabled)
            allDisabled = false;
          if (syntheticCheckedKeySet.has(childKey)) {
            partialChecked = true;
          } else if (syntheticIndeterminateKeySet.has(childKey)) {
            partialChecked = true;
            fullyChecked = false;
            break;
          } else {
            fullyChecked = false;
            if (partialChecked) {
              break;
            }
          }
        }
        if (fullyChecked && !allDisabled) {
          if (checkStrategyIsParent) {
            levelTreeNode.children.forEach((v) => {
              if (!v.disabled && syntheticCheckedKeySet.has(v.key)) {
                syntheticCheckedKeySet.delete(v.key);
              }
            });
          }
          syntheticCheckedKeySet.add(levelTreeNodeKey);
        } else if (partialChecked) {
          syntheticIndeterminateKeySet.add(levelTreeNodeKey);
        }
        if (levelIsZero && checkStrategyIsChild && syntheticCheckedKeySet.has(levelTreeNodeKey)) {
          syntheticCheckedKeySet.delete(levelTreeNodeKey);
        }
      }
    }
    return {
      checkedKeys: Array.from(syntheticCheckedKeySet),
      indeterminateKeys: Array.from(syntheticIndeterminateKeySet)
    };
  }
  function getExtendedCheckedKeySet(checkedKeys, treeMate, allowNotLoaded, isUnchecking) {
    const { treeNodeMap, getChildren } = treeMate;
    const visitedKeySet = /* @__PURE__ */ new Set();
    const extendedKeySet = new Set(checkedKeys);
    checkedKeys.forEach((checkedKey) => {
      const checkedTreeNode = treeNodeMap.get(checkedKey);
      if (checkedTreeNode !== void 0) {
        traverseWithCb(checkedTreeNode, (treeNode) => {
          if (treeNode.disabled) {
            return TRAVERSE_COMMAND.STOP;
          }
          const { key } = treeNode;
          if (visitedKeySet.has(key))
            return;
          visitedKeySet.add(key);
          extendedKeySet.add(key);
          if (isExpilicitlyNotLoaded(treeNode.rawNode, getChildren)) {
            if (isUnchecking) {
              return TRAVERSE_COMMAND.STOP;
            } else if (!allowNotLoaded) {
              throw new SubtreeNotLoadedError();
            }
          }
        });
      }
    });
    return extendedKeySet;
  }
  function getPath(key, { includeGroup = false, includeSelf = true }, treeMate) {
    var _a2;
    const treeNodeMap = treeMate.treeNodeMap;
    let treeNode = key === null || key === void 0 ? null : (_a2 = treeNodeMap.get(key)) !== null && _a2 !== void 0 ? _a2 : null;
    const mergedPath = {
      keyPath: [],
      treeNodePath: [],
      treeNode
    };
    if (treeNode === null || treeNode === void 0 ? void 0 : treeNode.ignored) {
      mergedPath.treeNode = null;
      return mergedPath;
    }
    while (treeNode) {
      if (!treeNode.ignored && (includeGroup || !treeNode.isGroup)) {
        mergedPath.treeNodePath.push(treeNode);
      }
      treeNode = treeNode.parent;
    }
    mergedPath.treeNodePath.reverse();
    if (!includeSelf)
      mergedPath.treeNodePath.pop();
    mergedPath.keyPath = mergedPath.treeNodePath.map((treeNode2) => treeNode2.key);
    return mergedPath;
  }
  function getFirstAvailableNode(nodes) {
    if (nodes.length === 0)
      return null;
    const node = nodes[0];
    if (node.isGroup || node.ignored || node.disabled) {
      return node.getNext();
    }
    return node;
  }
  function rawGetNext(node, loop) {
    const sibs = node.siblings;
    const l = sibs.length;
    const { index } = node;
    if (loop) {
      return sibs[(index + 1) % l];
    } else {
      if (index === sibs.length - 1)
        return null;
      return sibs[index + 1];
    }
  }
  function move(fromNode, dir, { loop = false, includeDisabled = false } = {}) {
    const iterate = dir === "prev" ? rawGetPrev : rawGetNext;
    const getChildOptions = {
      reverse: dir === "prev"
    };
    let meet = false;
    let endNode = null;
    function traverse2(node) {
      if (node === null)
        return;
      if (node === fromNode) {
        if (!meet) {
          meet = true;
        } else if (!fromNode.disabled && !fromNode.isGroup) {
          endNode = fromNode;
          return;
        }
      } else {
        if ((!node.disabled || includeDisabled) && !node.ignored && !node.isGroup) {
          endNode = node;
          return;
        }
      }
      if (node.isGroup) {
        const child = getChild(node, getChildOptions);
        if (child !== null) {
          endNode = child;
        } else {
          traverse2(iterate(node, loop));
        }
      } else {
        const nextNode = iterate(node, false);
        if (nextNode !== null) {
          traverse2(nextNode);
        } else {
          const parent = rawGetParent(node);
          if (parent === null || parent === void 0 ? void 0 : parent.isGroup) {
            traverse2(iterate(parent, loop));
          } else if (loop) {
            traverse2(iterate(node, true));
          }
        }
      }
    }
    traverse2(fromNode);
    return endNode;
  }
  function rawGetPrev(node, loop) {
    const sibs = node.siblings;
    const l = sibs.length;
    const { index } = node;
    if (loop) {
      return sibs[(index - 1 + l) % l];
    } else {
      if (index === 0)
        return null;
      return sibs[index - 1];
    }
  }
  function rawGetParent(node) {
    return node.parent;
  }
  function getChild(node, options = {}) {
    const { reverse = false } = options;
    const { children } = node;
    if (children) {
      const { length } = children;
      const start = reverse ? length - 1 : 0;
      const end = reverse ? -1 : length;
      const delta = reverse ? -1 : 1;
      for (let i = start; i !== end; i += delta) {
        const child = children[i];
        if (!child.disabled && !child.ignored) {
          if (child.isGroup) {
            const childInGroup = getChild(child, options);
            if (childInGroup !== null)
              return childInGroup;
          } else {
            return child;
          }
        }
      }
    }
    return null;
  }
  const moveMethods = {
    getChild() {
      if (this.ignored)
        return null;
      return getChild(this);
    },
    getParent() {
      const { parent } = this;
      if (parent === null || parent === void 0 ? void 0 : parent.isGroup) {
        return parent.getParent();
      }
      return parent;
    },
    getNext(options = {}) {
      return move(this, "next", options);
    },
    getPrev(options = {}) {
      return move(this, "prev", options);
    }
  };
  function flatten(treeNodes, expandedKeys) {
    const expandedKeySet = expandedKeys ? new Set(expandedKeys) : void 0;
    const flattenedNodes = [];
    function traverse2(treeNodes2) {
      treeNodes2.forEach((treeNode) => {
        flattenedNodes.push(treeNode);
        if (treeNode.isLeaf || !treeNode.children || treeNode.ignored)
          return;
        if (treeNode.isGroup) {
          traverse2(treeNode.children);
        } else if (
          // normal non-leaf node
          expandedKeySet === void 0 || expandedKeySet.has(treeNode.key)
        ) {
          traverse2(treeNode.children);
        }
      });
    }
    traverse2(treeNodes);
    return flattenedNodes;
  }
  function contains(parent, child) {
    const parentKey = parent.key;
    while (child) {
      if (child.key === parentKey)
        return true;
      child = child.parent;
    }
    return false;
  }
  function createTreeNodes(rawNodes, treeNodeMap, levelTreeNodeMap, nodeProto, getChildren, parent = null, level = 0) {
    const treeNodes = [];
    rawNodes.forEach((rawNode, index) => {
      var _a2;
      const treeNode = Object.create(nodeProto);
      treeNode.rawNode = rawNode;
      treeNode.siblings = treeNodes;
      treeNode.level = level;
      treeNode.index = index;
      treeNode.isFirstChild = index === 0;
      treeNode.isLastChild = index + 1 === rawNodes.length;
      treeNode.parent = parent;
      if (!treeNode.ignored) {
        const rawChildren = getChildren(rawNode);
        if (Array.isArray(rawChildren)) {
          treeNode.children = createTreeNodes(rawChildren, treeNodeMap, levelTreeNodeMap, nodeProto, getChildren, treeNode, level + 1);
        }
      }
      treeNodes.push(treeNode);
      treeNodeMap.set(treeNode.key, treeNode);
      if (!levelTreeNodeMap.has(level))
        levelTreeNodeMap.set(level, []);
      (_a2 = levelTreeNodeMap.get(level)) === null || _a2 === void 0 ? void 0 : _a2.push(treeNode);
    });
    return treeNodes;
  }
  function createTreeMate(rawNodes, options = {}) {
    var _a2;
    const treeNodeMap = /* @__PURE__ */ new Map();
    const levelTreeNodeMap = /* @__PURE__ */ new Map();
    const { getDisabled = isDisabled, getIgnored = isIgnored, getIsGroup = isGroup, getKey = defaultGetKey } = options;
    const _getChildren = (_a2 = options.getChildren) !== null && _a2 !== void 0 ? _a2 : defaultGetChildren;
    const getChildren = options.ignoreEmptyChildren ? (node) => {
      const children = _getChildren(node);
      if (Array.isArray(children)) {
        if (!children.length)
          return null;
        return children;
      }
      return children;
    } : _getChildren;
    const nodeProto = Object.assign({
      get key() {
        return getKey(this.rawNode);
      },
      get disabled() {
        return getDisabled(this.rawNode);
      },
      get isGroup() {
        return getIsGroup(this.rawNode);
      },
      get isLeaf() {
        return isLeaf(this.rawNode, getChildren);
      },
      get shallowLoaded() {
        return isShallowLoaded(this.rawNode, getChildren);
      },
      get ignored() {
        return getIgnored(this.rawNode);
      },
      contains(node) {
        return contains(this, node);
      }
    }, moveMethods);
    const treeNodes = createTreeNodes(rawNodes, treeNodeMap, levelTreeNodeMap, nodeProto, getChildren);
    function getNode(key) {
      if (key === null || key === void 0)
        return null;
      const tmNode = treeNodeMap.get(key);
      if (tmNode && !tmNode.isGroup && !tmNode.ignored) {
        return tmNode;
      }
      return null;
    }
    function _getNode(key) {
      if (key === null || key === void 0)
        return null;
      const tmNode = treeNodeMap.get(key);
      if (tmNode && !tmNode.ignored) {
        return tmNode;
      }
      return null;
    }
    function getPrev(key, options2) {
      const node = _getNode(key);
      if (!node)
        return null;
      return node.getPrev(options2);
    }
    function getNext(key, options2) {
      const node = _getNode(key);
      if (!node)
        return null;
      return node.getNext(options2);
    }
    function getParent(key) {
      const node = _getNode(key);
      if (!node)
        return null;
      return node.getParent();
    }
    function getChild2(key) {
      const node = _getNode(key);
      if (!node)
        return null;
      return node.getChild();
    }
    const treemate = {
      treeNodes,
      treeNodeMap,
      levelTreeNodeMap,
      maxLevel: Math.max(...levelTreeNodeMap.keys()),
      getChildren,
      getFlattenedNodes(expandedKeys) {
        return flatten(treeNodes, expandedKeys);
      },
      getNode,
      getPrev,
      getNext,
      getParent,
      getChild: getChild2,
      getFirstAvailableNode() {
        return getFirstAvailableNode(treeNodes);
      },
      getPath(key, options2 = {}) {
        return getPath(key, options2, treemate);
      },
      getCheckedKeys(checkedKeys, options2 = {}) {
        const { cascade = true, leafOnly = false, checkStrategy = "all", allowNotLoaded = false } = options2;
        return getCheckedKeys({
          checkedKeys: unwrapCheckedKeys(checkedKeys),
          indeterminateKeys: unwrapIndeterminateKeys(checkedKeys),
          cascade,
          leafOnly,
          checkStrategy,
          allowNotLoaded
        }, treemate);
      },
      check(keysToCheck, checkedKeys, options2 = {}) {
        const { cascade = true, leafOnly = false, checkStrategy = "all", allowNotLoaded = false } = options2;
        return getCheckedKeys({
          checkedKeys: unwrapCheckedKeys(checkedKeys),
          indeterminateKeys: unwrapIndeterminateKeys(checkedKeys),
          keysToCheck: keysToCheck === void 0 || keysToCheck === null ? [] : toArray(keysToCheck),
          cascade,
          leafOnly,
          checkStrategy,
          allowNotLoaded
        }, treemate);
      },
      uncheck(keysToUncheck, checkedKeys, options2 = {}) {
        const { cascade = true, leafOnly = false, checkStrategy = "all", allowNotLoaded = false } = options2;
        return getCheckedKeys({
          checkedKeys: unwrapCheckedKeys(checkedKeys),
          indeterminateKeys: unwrapIndeterminateKeys(checkedKeys),
          keysToUncheck: keysToUncheck === null || keysToUncheck === void 0 ? [] : toArray(keysToUncheck),
          cascade,
          leafOnly,
          checkStrategy,
          allowNotLoaded
        }, treemate);
      },
      getNonLeafKeys(options2 = {}) {
        return getNonLeafKeys(treeNodes, options2);
      }
    };
    return treemate;
  }
  const commonVars$4 = {
    iconSizeTiny: "28px",
    iconSizeSmall: "34px",
    iconSizeMedium: "40px",
    iconSizeLarge: "46px",
    iconSizeHuge: "52px"
  };
  function self$g(vars) {
    const {
      textColorDisabled,
      iconColor,
      textColor2,
      fontSizeTiny,
      fontSizeSmall,
      fontSizeMedium,
      fontSizeLarge,
      fontSizeHuge
    } = vars;
    return Object.assign(Object.assign({}, commonVars$4), {
      fontSizeTiny,
      fontSizeSmall,
      fontSizeMedium,
      fontSizeLarge,
      fontSizeHuge,
      textColor: textColorDisabled,
      iconColor,
      extraTextColor: textColor2
    });
  }
  const emptyLight = {
    common: derived,
    self: self$g
  };
  const style$h = cB("empty", `
 display: flex;
 flex-direction: column;
 align-items: center;
 font-size: var(--n-font-size);
`, [cE("icon", `
 width: var(--n-icon-size);
 height: var(--n-icon-size);
 font-size: var(--n-icon-size);
 line-height: var(--n-icon-size);
 color: var(--n-icon-color);
 transition:
 color .3s var(--n-bezier);
 `, [c$1("+", [cE("description", `
 margin-top: 8px;
 `)])]), cE("description", `
 transition: color .3s var(--n-bezier);
 color: var(--n-text-color);
 `), cE("extra", `
 text-align: center;
 transition: color .3s var(--n-bezier);
 margin-top: 12px;
 color: var(--n-extra-text-color);
 `)]);
  const emptyProps = Object.assign(Object.assign({}, useTheme.props), {
    description: String,
    showDescription: {
      type: Boolean,
      default: true
    },
    showIcon: {
      type: Boolean,
      default: true
    },
    size: {
      type: String,
      default: "medium"
    },
    renderIcon: Function
  });
  const NEmpty = /* @__PURE__ */ defineComponent({
    name: "Empty",
    props: emptyProps,
    slots: Object,
    setup(props) {
      const {
        mergedClsPrefixRef,
        inlineThemeDisabled,
        mergedComponentPropsRef
      } = useConfig(props);
      const themeRef = useTheme("Empty", "-empty", style$h, emptyLight, props, mergedClsPrefixRef);
      const {
        localeRef
      } = useLocale("Empty");
      const mergedDescriptionRef = computed(() => {
        var _a2, _b, _c;
        return (_a2 = props.description) !== null && _a2 !== void 0 ? _a2 : (_c = (_b = mergedComponentPropsRef === null || mergedComponentPropsRef === void 0 ? void 0 : mergedComponentPropsRef.value) === null || _b === void 0 ? void 0 : _b.Empty) === null || _c === void 0 ? void 0 : _c.description;
      });
      const mergedRenderIconRef = computed(() => {
        var _a2, _b;
        return ((_b = (_a2 = mergedComponentPropsRef === null || mergedComponentPropsRef === void 0 ? void 0 : mergedComponentPropsRef.value) === null || _a2 === void 0 ? void 0 : _a2.Empty) === null || _b === void 0 ? void 0 : _b.renderIcon) || (() => h(EmptyIcon, null));
      });
      const cssVarsRef = computed(() => {
        const {
          size: size2
        } = props;
        const {
          common: {
            cubicBezierEaseInOut: cubicBezierEaseInOut2
          },
          self: {
            [createKey("iconSize", size2)]: iconSize,
            [createKey("fontSize", size2)]: fontSize2,
            textColor,
            iconColor,
            extraTextColor
          }
        } = themeRef.value;
        return {
          "--n-icon-size": iconSize,
          "--n-font-size": fontSize2,
          "--n-bezier": cubicBezierEaseInOut2,
          "--n-text-color": textColor,
          "--n-icon-color": iconColor,
          "--n-extra-text-color": extraTextColor
        };
      });
      const themeClassHandle = inlineThemeDisabled ? useThemeClass("empty", computed(() => {
        let hash = "";
        const {
          size: size2
        } = props;
        hash += size2[0];
        return hash;
      }), cssVarsRef, props) : void 0;
      return {
        mergedClsPrefix: mergedClsPrefixRef,
        mergedRenderIcon: mergedRenderIconRef,
        localizedDescription: computed(() => {
          return mergedDescriptionRef.value || localeRef.value.description;
        }),
        cssVars: inlineThemeDisabled ? void 0 : cssVarsRef,
        themeClass: themeClassHandle === null || themeClassHandle === void 0 ? void 0 : themeClassHandle.themeClass,
        onRender: themeClassHandle === null || themeClassHandle === void 0 ? void 0 : themeClassHandle.onRender
      };
    },
    render() {
      const {
        $slots,
        mergedClsPrefix,
        onRender
      } = this;
      onRender === null || onRender === void 0 ? void 0 : onRender();
      return h("div", {
        class: [`${mergedClsPrefix}-empty`, this.themeClass],
        style: this.cssVars
      }, this.showIcon ? h("div", {
        class: `${mergedClsPrefix}-empty__icon`
      }, $slots.icon ? $slots.icon() : h(NBaseIcon, {
        clsPrefix: mergedClsPrefix
      }, {
        default: this.mergedRenderIcon
      })) : null, this.showDescription ? h("div", {
        class: `${mergedClsPrefix}-empty__description`
      }, $slots.default ? $slots.default() : this.localizedDescription) : null, $slots.extra ? h("div", {
        class: `${mergedClsPrefix}-empty__extra`
      }, $slots.extra()) : null);
    }
  });
  const {
    cubicBezierEaseIn: cubicBezierEaseIn$1,
    cubicBezierEaseOut: cubicBezierEaseOut$1
  } = commonVariables$7;
  function fadeInScaleUpTransition({
    transformOrigin = "inherit",
    duration: duration2 = ".2s",
    enterScale = ".9",
    originalTransform = "",
    originalTransition = ""
  } = {}) {
    return [c$1("&.fade-in-scale-up-transition-leave-active", {
      transformOrigin,
      transition: `opacity ${duration2} ${cubicBezierEaseIn$1}, transform ${duration2} ${cubicBezierEaseIn$1} ${originalTransition && `,${originalTransition}`}`
    }), c$1("&.fade-in-scale-up-transition-enter-active", {
      transformOrigin,
      transition: `opacity ${duration2} ${cubicBezierEaseOut$1}, transform ${duration2} ${cubicBezierEaseOut$1} ${originalTransition && `,${originalTransition}`}`
    }), c$1("&.fade-in-scale-up-transition-enter-from, &.fade-in-scale-up-transition-leave-to", {
      opacity: 0,
      transform: `${originalTransform} scale(${enterScale})`
    }), c$1("&.fade-in-scale-up-transition-leave-from, &.fade-in-scale-up-transition-enter-to", {
      opacity: 1,
      transform: `${originalTransform} scale(1)`
    })];
  }
  const commonVariables$6 = {
    space: "6px",
    spaceArrow: "10px",
    arrowOffset: "10px",
    arrowOffsetVertical: "10px",
    arrowHeight: "6px",
    padding: "8px 14px"
  };
  function self$f(vars) {
    const {
      boxShadow2,
      popoverColor,
      textColor2,
      borderRadius,
      fontSize: fontSize2,
      dividerColor
    } = vars;
    return Object.assign(Object.assign({}, commonVariables$6), {
      fontSize: fontSize2,
      borderRadius,
      color: popoverColor,
      dividerColor,
      textColor: textColor2,
      boxShadow: boxShadow2
    });
  }
  const popoverLight = createTheme({
    name: "Popover",
    common: derived,
    peers: {
      Scrollbar: scrollbarLight
    },
    self: self$f
  });
  const oppositePlacement = {
    top: "bottom",
    bottom: "top",
    left: "right",
    right: "left"
  };
  const arrowSize = "var(--n-arrow-height) * 1.414";
  const style$g = c$1([cB("popover", `
 transition:
 box-shadow .3s var(--n-bezier),
 background-color .3s var(--n-bezier),
 color .3s var(--n-bezier);
 position: relative;
 font-size: var(--n-font-size);
 color: var(--n-text-color);
 box-shadow: var(--n-box-shadow);
 word-break: break-word;
 `, [c$1(">", [cB("scrollbar", `
 height: inherit;
 max-height: inherit;
 `)]), cNotM("raw", `
 background-color: var(--n-color);
 border-radius: var(--n-border-radius);
 `, [cNotM("scrollable", [cNotM("show-header-or-footer", "padding: var(--n-padding);")])]), cE("header", `
 padding: var(--n-padding);
 border-bottom: 1px solid var(--n-divider-color);
 transition: border-color .3s var(--n-bezier);
 `), cE("footer", `
 padding: var(--n-padding);
 border-top: 1px solid var(--n-divider-color);
 transition: border-color .3s var(--n-bezier);
 `), cM("scrollable, show-header-or-footer", [cE("content", `
 padding: var(--n-padding);
 `)])]), cB("popover-shared", `
 transform-origin: inherit;
 `, [
    cB("popover-arrow-wrapper", `
 position: absolute;
 overflow: hidden;
 pointer-events: none;
 `, [cB("popover-arrow", `
 transition: background-color .3s var(--n-bezier);
 position: absolute;
 display: block;
 width: calc(${arrowSize});
 height: calc(${arrowSize});
 box-shadow: 0 0 8px 0 rgba(0, 0, 0, .12);
 transform: rotate(45deg);
 background-color: var(--n-color);
 pointer-events: all;
 `)]),
    // body transition
    c$1("&.popover-transition-enter-from, &.popover-transition-leave-to", `
 opacity: 0;
 transform: scale(.85);
 `),
    c$1("&.popover-transition-enter-to, &.popover-transition-leave-from", `
 transform: scale(1);
 opacity: 1;
 `),
    c$1("&.popover-transition-enter-active", `
 transition:
 box-shadow .3s var(--n-bezier),
 background-color .3s var(--n-bezier),
 color .3s var(--n-bezier),
 opacity .15s var(--n-bezier-ease-out),
 transform .15s var(--n-bezier-ease-out);
 `),
    c$1("&.popover-transition-leave-active", `
 transition:
 box-shadow .3s var(--n-bezier),
 background-color .3s var(--n-bezier),
 color .3s var(--n-bezier),
 opacity .15s var(--n-bezier-ease-in),
 transform .15s var(--n-bezier-ease-in);
 `)
  ]), placementStyle("top-start", `
 top: calc(${arrowSize} / -2);
 left: calc(${getArrowOffset("top-start")} - var(--v-offset-left));
 `), placementStyle("top", `
 top: calc(${arrowSize} / -2);
 transform: translateX(calc(${arrowSize} / -2)) rotate(45deg);
 left: 50%;
 `), placementStyle("top-end", `
 top: calc(${arrowSize} / -2);
 right: calc(${getArrowOffset("top-end")} + var(--v-offset-left));
 `), placementStyle("bottom-start", `
 bottom: calc(${arrowSize} / -2);
 left: calc(${getArrowOffset("bottom-start")} - var(--v-offset-left));
 `), placementStyle("bottom", `
 bottom: calc(${arrowSize} / -2);
 transform: translateX(calc(${arrowSize} / -2)) rotate(45deg);
 left: 50%;
 `), placementStyle("bottom-end", `
 bottom: calc(${arrowSize} / -2);
 right: calc(${getArrowOffset("bottom-end")} + var(--v-offset-left));
 `), placementStyle("left-start", `
 left: calc(${arrowSize} / -2);
 top: calc(${getArrowOffset("left-start")} - var(--v-offset-top));
 `), placementStyle("left", `
 left: calc(${arrowSize} / -2);
 transform: translateY(calc(${arrowSize} / -2)) rotate(45deg);
 top: 50%;
 `), placementStyle("left-end", `
 left: calc(${arrowSize} / -2);
 bottom: calc(${getArrowOffset("left-end")} + var(--v-offset-top));
 `), placementStyle("right-start", `
 right: calc(${arrowSize} / -2);
 top: calc(${getArrowOffset("right-start")} - var(--v-offset-top));
 `), placementStyle("right", `
 right: calc(${arrowSize} / -2);
 transform: translateY(calc(${arrowSize} / -2)) rotate(45deg);
 top: 50%;
 `), placementStyle("right-end", `
 right: calc(${arrowSize} / -2);
 bottom: calc(${getArrowOffset("right-end")} + var(--v-offset-top));
 `), ...map({
    top: ["right-start", "left-start"],
    right: ["top-end", "bottom-end"],
    bottom: ["right-end", "left-end"],
    left: ["top-start", "bottom-start"]
  }, (placements, direction) => {
    const isVertical = ["right", "left"].includes(direction);
    const sizeType = isVertical ? "width" : "height";
    return placements.map((placement) => {
      const isReverse = placement.split("-")[1] === "end";
      const targetSize = `var(--v-target-${sizeType}, 0px)`;
      const centerOffset = `calc((${targetSize} - ${arrowSize}) / 2)`;
      const offset = getArrowOffset(placement);
      return c$1(`[v-placement="${placement}"] >`, [cB("popover-shared", [cM("center-arrow", [cB("popover-arrow", `${direction}: calc(max(${centerOffset}, ${offset}) ${isReverse ? "+" : "-"} var(--v-offset-${isVertical ? "left" : "top"}));`)])])]);
    });
  })]);
  function getArrowOffset(placement) {
    return ["top", "bottom"].includes(placement.split("-")[0]) ? "var(--n-arrow-offset)" : "var(--n-arrow-offset-vertical)";
  }
  function placementStyle(placement, arrowStyleLiteral) {
    const position = placement.split("-")[0];
    const sizeStyle = ["top", "bottom"].includes(position) ? "height: var(--n-space-arrow);" : "width: var(--n-space-arrow);";
    return c$1(`[v-placement="${placement}"] >`, [cB("popover-shared", `
 margin-${oppositePlacement[position]}: var(--n-space);
 `, [cM("show-arrow", `
 margin-${oppositePlacement[position]}: var(--n-space-arrow);
 `), cM("overlap", `
 margin: 0;
 `), cCB("popover-arrow-wrapper", `
 right: 0;
 left: 0;
 top: 0;
 bottom: 0;
 ${position}: 100%;
 ${oppositePlacement[position]}: auto;
 ${sizeStyle}
 `, [cB("popover-arrow", arrowStyleLiteral)])])]);
  }
  const popoverBodyProps = Object.assign(Object.assign({}, useTheme.props), {
    to: useAdjustedTo.propTo,
    show: Boolean,
    trigger: String,
    showArrow: Boolean,
    delay: Number,
    duration: Number,
    raw: Boolean,
    arrowPointToCenter: Boolean,
    arrowClass: String,
    arrowStyle: [String, Object],
    arrowWrapperClass: String,
    arrowWrapperStyle: [String, Object],
    displayDirective: String,
    x: Number,
    y: Number,
    flip: Boolean,
    overlap: Boolean,
    placement: String,
    width: [Number, String],
    keepAliveOnHover: Boolean,
    scrollable: Boolean,
    contentClass: String,
    contentStyle: [Object, String],
    headerClass: String,
    headerStyle: [Object, String],
    footerClass: String,
    footerStyle: [Object, String],
    // private
    internalDeactivateImmediately: Boolean,
    animated: Boolean,
    onClickoutside: Function,
    internalTrapFocus: Boolean,
    internalOnAfterLeave: Function,
    // deprecated
    minWidth: Number,
    maxWidth: Number
  });
  function renderArrow({
    arrowClass,
    arrowStyle,
    arrowWrapperClass,
    arrowWrapperStyle,
    clsPrefix
  }) {
    return h("div", {
      key: "__popover-arrow__",
      style: arrowWrapperStyle,
      class: [`${clsPrefix}-popover-arrow-wrapper`, arrowWrapperClass]
    }, h("div", {
      class: [`${clsPrefix}-popover-arrow`, arrowClass],
      style: arrowStyle
    }));
  }
  const NPopoverBody = /* @__PURE__ */ defineComponent({
    name: "PopoverBody",
    inheritAttrs: false,
    props: popoverBodyProps,
    setup(props, {
      slots,
      attrs
    }) {
      const {
        namespaceRef,
        mergedClsPrefixRef,
        inlineThemeDisabled,
        mergedRtlRef
      } = useConfig(props);
      const themeRef = useTheme("Popover", "-popover", style$g, popoverLight, props, mergedClsPrefixRef);
      const rtlEnabledRef = useRtl("Popover", mergedRtlRef, mergedClsPrefixRef);
      const followerRef = /* @__PURE__ */ ref(null);
      const NPopover2 = inject("NPopover");
      const bodyRef = /* @__PURE__ */ ref(null);
      const followerEnabledRef = /* @__PURE__ */ ref(props.show);
      const displayedRef = /* @__PURE__ */ ref(false);
      watchEffect(() => {
        const {
          show
        } = props;
        if (show && !isJsdom() && !props.internalDeactivateImmediately) {
          displayedRef.value = true;
        }
      });
      const directivesRef = computed(() => {
        const {
          trigger: trigger2,
          onClickoutside
        } = props;
        const directives = [];
        const {
          positionManuallyRef: {
            value: positionManually
          }
        } = NPopover2;
        if (!positionManually) {
          if (trigger2 === "click" && !onClickoutside) {
            directives.push([clickoutside, handleClickOutside, void 0, {
              capture: true
            }]);
          }
          if (trigger2 === "hover") {
            directives.push([mousemoveoutside, handleMouseMoveOutside]);
          }
        }
        if (onClickoutside) {
          directives.push([clickoutside, handleClickOutside, void 0, {
            capture: true
          }]);
        }
        if (props.displayDirective === "show" || props.animated && displayedRef.value) {
          directives.push([vShow, props.show]);
        }
        return directives;
      });
      const cssVarsRef = computed(() => {
        const {
          common: {
            cubicBezierEaseInOut: cubicBezierEaseInOut2,
            cubicBezierEaseIn: cubicBezierEaseIn2,
            cubicBezierEaseOut: cubicBezierEaseOut2
          },
          self: {
            space,
            spaceArrow,
            padding,
            fontSize: fontSize2,
            textColor,
            dividerColor,
            color,
            boxShadow,
            borderRadius,
            arrowHeight,
            arrowOffset,
            arrowOffsetVertical
          }
        } = themeRef.value;
        return {
          "--n-box-shadow": boxShadow,
          "--n-bezier": cubicBezierEaseInOut2,
          "--n-bezier-ease-in": cubicBezierEaseIn2,
          "--n-bezier-ease-out": cubicBezierEaseOut2,
          "--n-font-size": fontSize2,
          "--n-text-color": textColor,
          "--n-color": color,
          "--n-divider-color": dividerColor,
          "--n-border-radius": borderRadius,
          "--n-arrow-height": arrowHeight,
          "--n-arrow-offset": arrowOffset,
          "--n-arrow-offset-vertical": arrowOffsetVertical,
          "--n-padding": padding,
          "--n-space": space,
          "--n-space-arrow": spaceArrow
        };
      });
      const styleRef = computed(() => {
        const width = props.width === "trigger" ? void 0 : formatLength(props.width);
        const style2 = [];
        if (width) {
          style2.push({
            width
          });
        }
        const {
          maxWidth,
          minWidth
        } = props;
        if (maxWidth) {
          style2.push({
            maxWidth: formatLength(maxWidth)
          });
        }
        if (minWidth) {
          style2.push({
            maxWidth: formatLength(minWidth)
          });
        }
        if (!inlineThemeDisabled) {
          style2.push(cssVarsRef.value);
        }
        return style2;
      });
      const themeClassHandle = inlineThemeDisabled ? useThemeClass("popover", void 0, cssVarsRef, props) : void 0;
      NPopover2.setBodyInstance({
        syncPosition
      });
      onBeforeUnmount(() => {
        NPopover2.setBodyInstance(null);
      });
      watch(/* @__PURE__ */ toRef(props, "show"), (value) => {
        if (props.animated) return;
        if (value) {
          followerEnabledRef.value = true;
        } else {
          followerEnabledRef.value = false;
        }
      });
      function syncPosition() {
        var _a2;
        (_a2 = followerRef.value) === null || _a2 === void 0 ? void 0 : _a2.syncPosition();
      }
      function handleMouseEnter(e) {
        if (props.trigger === "hover" && props.keepAliveOnHover && props.show) {
          NPopover2.handleMouseEnter(e);
        }
      }
      function handleMouseLeave(e) {
        if (props.trigger === "hover" && props.keepAliveOnHover) {
          NPopover2.handleMouseLeave(e);
        }
      }
      function handleMouseMoveOutside(e) {
        if (props.trigger === "hover" && !getTriggerElement().contains(getPreciseEventTarget(e))) {
          NPopover2.handleMouseMoveOutside(e);
        }
      }
      function handleClickOutside(e) {
        if (props.trigger === "click" && !getTriggerElement().contains(getPreciseEventTarget(e)) || props.onClickoutside) {
          NPopover2.handleClickOutside(e);
        }
      }
      function getTriggerElement() {
        return NPopover2.getTriggerElement();
      }
      provide(popoverBodyInjectionKey, bodyRef);
      provide(drawerBodyInjectionKey, null);
      provide(modalBodyInjectionKey, null);
      function renderContentNode() {
        themeClassHandle === null || themeClassHandle === void 0 ? void 0 : themeClassHandle.onRender();
        const shouldRenderDom = props.displayDirective === "show" || props.show || props.animated && displayedRef.value;
        if (!shouldRenderDom) {
          return null;
        }
        let contentNode;
        const renderBody = NPopover2.internalRenderBodyRef.value;
        const {
          value: mergedClsPrefix
        } = mergedClsPrefixRef;
        if (!renderBody) {
          const {
            value: extraClass
          } = NPopover2.extraClassRef;
          const {
            internalTrapFocus
          } = props;
          const hasHeaderOrFooter = !isSlotEmpty(slots.header) || !isSlotEmpty(slots.footer);
          const renderContentInnerNode = () => {
            var _a2, _b;
            const body = hasHeaderOrFooter ? h(Fragment, null, resolveWrappedSlot(slots.header, (children) => {
              return children ? h("div", {
                class: [`${mergedClsPrefix}-popover__header`, props.headerClass],
                style: props.headerStyle
              }, children) : null;
            }), resolveWrappedSlot(slots.default, (children) => {
              return children ? h("div", {
                class: [`${mergedClsPrefix}-popover__content`, props.contentClass],
                style: props.contentStyle
              }, slots) : null;
            }), resolveWrappedSlot(slots.footer, (children) => {
              return children ? h("div", {
                class: [`${mergedClsPrefix}-popover__footer`, props.footerClass],
                style: props.footerStyle
              }, children) : null;
            })) : props.scrollable ? (_a2 = slots.default) === null || _a2 === void 0 ? void 0 : _a2.call(slots) : h("div", {
              class: [`${mergedClsPrefix}-popover__content`, props.contentClass],
              style: props.contentStyle
            }, slots);
            const maybeScrollableBody = props.scrollable ? h(XScrollbar, {
              themeOverrides: themeRef.value.peerOverrides.Scrollbar,
              theme: themeRef.value.peers.Scrollbar,
              contentClass: hasHeaderOrFooter ? void 0 : `${mergedClsPrefix}-popover__content ${(_b = props.contentClass) !== null && _b !== void 0 ? _b : ""}`,
              contentStyle: hasHeaderOrFooter ? void 0 : props.contentStyle
            }, {
              default: () => body
            }) : body;
            const arrow = props.showArrow ? renderArrow({
              arrowClass: props.arrowClass,
              arrowStyle: props.arrowStyle,
              arrowWrapperClass: props.arrowWrapperClass,
              arrowWrapperStyle: props.arrowWrapperStyle,
              clsPrefix: mergedClsPrefix
            }) : null;
            return [maybeScrollableBody, arrow];
          };
          contentNode = h("div", mergeProps({
            class: [`${mergedClsPrefix}-popover`, `${mergedClsPrefix}-popover-shared`, (rtlEnabledRef === null || rtlEnabledRef === void 0 ? void 0 : rtlEnabledRef.value) && `${mergedClsPrefix}-popover--rtl`, themeClassHandle === null || themeClassHandle === void 0 ? void 0 : themeClassHandle.themeClass.value, extraClass.map((v) => `${mergedClsPrefix}-${v}`), {
              [`${mergedClsPrefix}-popover--scrollable`]: props.scrollable,
              [`${mergedClsPrefix}-popover--show-header-or-footer`]: hasHeaderOrFooter,
              [`${mergedClsPrefix}-popover--raw`]: props.raw,
              [`${mergedClsPrefix}-popover-shared--overlap`]: props.overlap,
              [`${mergedClsPrefix}-popover-shared--show-arrow`]: props.showArrow,
              [`${mergedClsPrefix}-popover-shared--center-arrow`]: props.arrowPointToCenter
            }],
            ref: bodyRef,
            style: styleRef.value,
            onKeydown: NPopover2.handleKeydown,
            onMouseenter: handleMouseEnter,
            onMouseleave: handleMouseLeave
          }, attrs), internalTrapFocus ? h(FocusTrap, {
            active: props.show,
            autoFocus: true
          }, {
            default: renderContentInnerNode
          }) : renderContentInnerNode());
        } else {
          contentNode = renderBody(
            // The popover class and overlap class must exists, they will be used
            // to place the body & transition animation.
            // Shadow class exists for reuse box-shadow.
            [`${mergedClsPrefix}-popover-shared`, (rtlEnabledRef === null || rtlEnabledRef === void 0 ? void 0 : rtlEnabledRef.value) && `${mergedClsPrefix}-popover--rtl`, themeClassHandle === null || themeClassHandle === void 0 ? void 0 : themeClassHandle.themeClass.value, props.overlap && `${mergedClsPrefix}-popover-shared--overlap`, props.showArrow && `${mergedClsPrefix}-popover-shared--show-arrow`, props.arrowPointToCenter && `${mergedClsPrefix}-popover-shared--center-arrow`],
            bodyRef,
            styleRef.value,
            handleMouseEnter,
            handleMouseLeave
          );
        }
        return withDirectives(contentNode, directivesRef.value);
      }
      return {
        displayed: displayedRef,
        namespace: namespaceRef,
        isMounted: NPopover2.isMountedRef,
        zIndex: NPopover2.zIndexRef,
        followerRef,
        adjustedTo: useAdjustedTo(props),
        followerEnabled: followerEnabledRef,
        renderContentNode
      };
    },
    render() {
      return h(VFollower, {
        ref: "followerRef",
        zIndex: this.zIndex,
        show: this.show,
        enabled: this.followerEnabled,
        to: this.adjustedTo,
        x: this.x,
        y: this.y,
        flip: this.flip,
        placement: this.placement,
        containerClass: this.namespace,
        overlap: this.overlap,
        width: this.width === "trigger" ? "target" : void 0,
        teleportDisabled: this.adjustedTo === useAdjustedTo.tdkey
      }, {
        default: () => {
          return this.animated ? h(Transition, {
            name: "popover-transition",
            appear: this.isMounted,
            // Don't use watch to enable follower, since the transition may
            // make position sync timing very subtle and buggy.
            onEnter: () => {
              this.followerEnabled = true;
            },
            onAfterLeave: () => {
              var _a2;
              (_a2 = this.internalOnAfterLeave) === null || _a2 === void 0 ? void 0 : _a2.call(this);
              this.followerEnabled = false;
              this.displayed = false;
            }
          }, {
            default: this.renderContentNode
          }) : this.renderContentNode();
        }
      });
    }
  });
  const bodyPropKeys = Object.keys(popoverBodyProps);
  const triggerEventMap = {
    focus: ["onFocus", "onBlur"],
    click: ["onClick"],
    hover: ["onMouseenter", "onMouseleave"],
    manual: [],
    nested: ["onFocus", "onBlur", "onMouseenter", "onMouseleave", "onClick"]
  };
  function appendEvents(vNode, trigger2, events2) {
    triggerEventMap[trigger2].forEach((eventName) => {
      if (!vNode.props) {
        vNode.props = {};
      } else {
        vNode.props = Object.assign({}, vNode.props);
      }
      const originalHandler = vNode.props[eventName];
      const handler = events2[eventName];
      if (!originalHandler) {
        vNode.props[eventName] = handler;
      } else {
        vNode.props[eventName] = (...args) => {
          originalHandler(...args);
          handler(...args);
        };
      }
    });
  }
  const popoverBaseProps = {
    show: {
      type: Boolean,
      default: void 0
    },
    defaultShow: Boolean,
    showArrow: {
      type: Boolean,
      default: true
    },
    trigger: {
      type: String,
      default: "hover"
    },
    delay: {
      type: Number,
      default: 100
    },
    duration: {
      type: Number,
      default: 100
    },
    raw: Boolean,
    placement: {
      type: String,
      default: "top"
    },
    x: Number,
    y: Number,
    arrowPointToCenter: Boolean,
    disabled: Boolean,
    getDisabled: Function,
    displayDirective: {
      type: String,
      default: "if"
    },
    arrowClass: String,
    arrowStyle: [String, Object],
    arrowWrapperClass: String,
    arrowWrapperStyle: [String, Object],
    flip: {
      type: Boolean,
      default: true
    },
    animated: {
      type: Boolean,
      default: true
    },
    width: {
      type: [Number, String],
      default: void 0
    },
    overlap: Boolean,
    keepAliveOnHover: {
      type: Boolean,
      default: true
    },
    zIndex: Number,
    to: useAdjustedTo.propTo,
    scrollable: Boolean,
    contentClass: String,
    contentStyle: [Object, String],
    headerClass: String,
    headerStyle: [Object, String],
    footerClass: String,
    footerStyle: [Object, String],
    // events
    onClickoutside: Function,
    "onUpdate:show": [Function, Array],
    onUpdateShow: [Function, Array],
    // internal
    internalDeactivateImmediately: Boolean,
    internalSyncTargetWithParent: Boolean,
    internalInheritedEventHandlers: {
      type: Array,
      default: () => []
    },
    internalTrapFocus: Boolean,
    internalExtraClass: {
      type: Array,
      default: () => []
    },
    // deprecated
    onShow: [Function, Array],
    onHide: [Function, Array],
    arrow: {
      type: Boolean,
      default: void 0
    },
    minWidth: Number,
    maxWidth: Number
  };
  const popoverProps = Object.assign(Object.assign(Object.assign({}, useTheme.props), popoverBaseProps), {
    internalOnAfterLeave: Function,
    internalRenderBody: Function
  });
  const NPopover = /* @__PURE__ */ defineComponent({
    name: "Popover",
    inheritAttrs: false,
    props: popoverProps,
    slots: Object,
    __popover__: true,
    setup(props) {
      const isMountedRef = isMounted();
      const binderInstRef = /* @__PURE__ */ ref(null);
      const controlledShowRef = computed(() => props.show);
      const uncontrolledShowRef = /* @__PURE__ */ ref(props.defaultShow);
      const mergedShowWithoutDisabledRef = useMergedState(controlledShowRef, uncontrolledShowRef);
      const mergedShowConsideringDisabledPropRef = useMemo(() => {
        if (props.disabled) return false;
        return mergedShowWithoutDisabledRef.value;
      });
      const getMergedDisabled = () => {
        if (props.disabled) return true;
        const {
          getDisabled
        } = props;
        if (getDisabled === null || getDisabled === void 0 ? void 0 : getDisabled()) return true;
        return false;
      };
      const getMergedShow = () => {
        if (getMergedDisabled()) return false;
        return mergedShowWithoutDisabledRef.value;
      };
      const compatibleShowArrowRef = useCompitable(props, ["arrow", "showArrow"]);
      const mergedShowArrowRef = computed(() => {
        if (props.overlap) return false;
        return compatibleShowArrowRef.value;
      });
      let bodyInstance = null;
      const showTimerIdRef = /* @__PURE__ */ ref(null);
      const hideTimerIdRef = /* @__PURE__ */ ref(null);
      const positionManuallyRef = useMemo(() => {
        return props.x !== void 0 && props.y !== void 0;
      });
      function doUpdateShow(value) {
        const {
          "onUpdate:show": _onUpdateShow,
          onUpdateShow,
          onShow,
          onHide
        } = props;
        uncontrolledShowRef.value = value;
        if (_onUpdateShow) {
          call(_onUpdateShow, value);
        }
        if (onUpdateShow) {
          call(onUpdateShow, value);
        }
        if (value && onShow) {
          call(onShow, true);
        }
        if (value && onHide) {
          call(onHide, false);
        }
      }
      function syncPosition() {
        if (bodyInstance) {
          bodyInstance.syncPosition();
        }
      }
      function clearShowTimer() {
        const {
          value: showTimerId
        } = showTimerIdRef;
        if (showTimerId) {
          window.clearTimeout(showTimerId);
          showTimerIdRef.value = null;
        }
      }
      function clearHideTimer() {
        const {
          value: hideTimerId
        } = hideTimerIdRef;
        if (hideTimerId) {
          window.clearTimeout(hideTimerId);
          hideTimerIdRef.value = null;
        }
      }
      function handleFocus() {
        const mergedDisabled = getMergedDisabled();
        if (props.trigger === "focus" && !mergedDisabled) {
          if (getMergedShow()) return;
          doUpdateShow(true);
        }
      }
      function handleBlur() {
        const mergedDisabled = getMergedDisabled();
        if (props.trigger === "focus" && !mergedDisabled) {
          if (!getMergedShow()) return;
          doUpdateShow(false);
        }
      }
      function handleMouseEnter() {
        const mergedDisabled = getMergedDisabled();
        if (props.trigger === "hover" && !mergedDisabled) {
          clearHideTimer();
          if (showTimerIdRef.value !== null) return;
          if (getMergedShow()) return;
          const delayCallback = () => {
            doUpdateShow(true);
            showTimerIdRef.value = null;
          };
          const {
            delay
          } = props;
          if (delay === 0) {
            delayCallback();
          } else {
            showTimerIdRef.value = window.setTimeout(delayCallback, delay);
          }
        }
      }
      function handleMouseLeave() {
        const mergedDisabled = getMergedDisabled();
        if (props.trigger === "hover" && !mergedDisabled) {
          clearShowTimer();
          if (hideTimerIdRef.value !== null) return;
          if (!getMergedShow()) return;
          const delayedCallback = () => {
            doUpdateShow(false);
            hideTimerIdRef.value = null;
          };
          const {
            duration: duration2
          } = props;
          if (duration2 === 0) {
            delayedCallback();
          } else {
            hideTimerIdRef.value = window.setTimeout(delayedCallback, duration2);
          }
        }
      }
      function handleMouseMoveOutside() {
        handleMouseLeave();
      }
      function handleClickOutside(e) {
        var _a2;
        if (!getMergedShow()) return;
        if (props.trigger === "click") {
          clearShowTimer();
          clearHideTimer();
          doUpdateShow(false);
        }
        (_a2 = props.onClickoutside) === null || _a2 === void 0 ? void 0 : _a2.call(props, e);
      }
      function handleClick() {
        if (props.trigger === "click" && !getMergedDisabled()) {
          clearShowTimer();
          clearHideTimer();
          const nextShow = !getMergedShow();
          doUpdateShow(nextShow);
        }
      }
      function handleKeydown(e) {
        if (!props.internalTrapFocus) return;
        if (e.key === "Escape") {
          clearShowTimer();
          clearHideTimer();
          doUpdateShow(false);
        }
      }
      function setShow(value) {
        uncontrolledShowRef.value = value;
      }
      function getTriggerElement() {
        var _a2;
        return (_a2 = binderInstRef.value) === null || _a2 === void 0 ? void 0 : _a2.targetRef;
      }
      function setBodyInstance(value) {
        bodyInstance = value;
      }
      provide("NPopover", {
        getTriggerElement,
        handleKeydown,
        handleMouseEnter,
        handleMouseLeave,
        handleClickOutside,
        handleMouseMoveOutside,
        setBodyInstance,
        positionManuallyRef,
        isMountedRef,
        zIndexRef: /* @__PURE__ */ toRef(props, "zIndex"),
        extraClassRef: /* @__PURE__ */ toRef(props, "internalExtraClass"),
        internalRenderBodyRef: /* @__PURE__ */ toRef(props, "internalRenderBody")
      });
      watchEffect(() => {
        if (mergedShowWithoutDisabledRef.value && getMergedDisabled()) {
          doUpdateShow(false);
        }
      });
      const returned = {
        binderInstRef,
        positionManually: positionManuallyRef,
        mergedShowConsideringDisabledProp: mergedShowConsideringDisabledPropRef,
        // if to show popover body
        uncontrolledShow: uncontrolledShowRef,
        mergedShowArrow: mergedShowArrowRef,
        getMergedShow,
        setShow,
        handleClick,
        handleMouseEnter,
        handleMouseLeave,
        handleFocus,
        handleBlur,
        syncPosition
      };
      return returned;
    },
    render() {
      var _a2;
      const {
        positionManually,
        $slots: slots
      } = this;
      let triggerVNode;
      let popoverInside = false;
      if (!positionManually) {
        triggerVNode = getFirstSlotVNode(slots, "trigger");
        if (triggerVNode) {
          triggerVNode = cloneVNode(triggerVNode);
          triggerVNode = triggerVNode.type === Text ? h("span", [triggerVNode]) : triggerVNode;
          const handlers = {
            onClick: this.handleClick,
            onMouseenter: this.handleMouseEnter,
            onMouseleave: this.handleMouseLeave,
            onFocus: this.handleFocus,
            onBlur: this.handleBlur
          };
          if ((_a2 = triggerVNode.type) === null || _a2 === void 0 ? void 0 : _a2.__popover__) {
            popoverInside = true;
            if (!triggerVNode.props) {
              triggerVNode.props = {
                internalSyncTargetWithParent: true,
                internalInheritedEventHandlers: []
              };
            }
            triggerVNode.props.internalSyncTargetWithParent = true;
            if (!triggerVNode.props.internalInheritedEventHandlers) {
              triggerVNode.props.internalInheritedEventHandlers = [handlers];
            } else {
              triggerVNode.props.internalInheritedEventHandlers = [handlers, ...triggerVNode.props.internalInheritedEventHandlers];
            }
          } else {
            const {
              internalInheritedEventHandlers
            } = this;
            const ascendantAndCurrentHandlers = [handlers, ...internalInheritedEventHandlers];
            const mergedHandlers = {
              onBlur: (e) => {
                ascendantAndCurrentHandlers.forEach((_handlers) => {
                  _handlers.onBlur(e);
                });
              },
              onFocus: (e) => {
                ascendantAndCurrentHandlers.forEach((_handlers) => {
                  _handlers.onFocus(e);
                });
              },
              onClick: (e) => {
                ascendantAndCurrentHandlers.forEach((_handlers) => {
                  _handlers.onClick(e);
                });
              },
              onMouseenter: (e) => {
                ascendantAndCurrentHandlers.forEach((_handlers) => {
                  _handlers.onMouseenter(e);
                });
              },
              onMouseleave: (e) => {
                ascendantAndCurrentHandlers.forEach((_handlers) => {
                  _handlers.onMouseleave(e);
                });
              }
            };
            appendEvents(triggerVNode, internalInheritedEventHandlers ? "nested" : positionManually ? "manual" : this.trigger, mergedHandlers);
          }
        }
      }
      return h(Binder, {
        ref: "binderInstRef",
        syncTarget: !popoverInside,
        syncTargetWithParent: this.internalSyncTargetWithParent
      }, {
        default: () => {
          void this.mergedShowConsideringDisabledProp;
          const mergedShow = this.getMergedShow();
          return [this.internalTrapFocus && mergedShow ? withDirectives(h("div", {
            style: {
              position: "fixed",
              top: 0,
              right: 0,
              bottom: 0,
              left: 0
            }
          }), [[zindexable, {
            enabled: mergedShow,
            zIndex: this.zIndex
          }]]) : null, positionManually ? null : h(VTarget, null, {
            default: () => triggerVNode
          }), h(NPopoverBody, keep(this.$props, bodyPropKeys, Object.assign(Object.assign({}, this.$attrs), {
            showArrow: this.mergedShowArrow,
            show: mergedShow
          })), {
            default: () => {
              var _a22, _b;
              return (_b = (_a22 = this.$slots).default) === null || _b === void 0 ? void 0 : _b.call(_a22);
            },
            header: () => {
              var _a22, _b;
              return (_b = (_a22 = this.$slots).header) === null || _b === void 0 ? void 0 : _b.call(_a22);
            },
            footer: () => {
              var _a22, _b;
              return (_b = (_a22 = this.$slots).footer) === null || _b === void 0 ? void 0 : _b.call(_a22);
            }
          })];
        }
      });
    }
  });
  const commonVariables$5 = {
    closeIconSizeTiny: "12px",
    closeIconSizeSmall: "12px",
    closeIconSizeMedium: "14px",
    closeIconSizeLarge: "14px",
    closeSizeTiny: "16px",
    closeSizeSmall: "16px",
    closeSizeMedium: "18px",
    closeSizeLarge: "18px",
    padding: "0 7px",
    closeMargin: "0 0 0 4px"
  };
  function self$e(vars) {
    const {
      textColor2,
      primaryColorHover,
      primaryColorPressed,
      primaryColor,
      infoColor,
      successColor,
      warningColor,
      errorColor,
      baseColor,
      borderColor,
      opacityDisabled,
      tagColor,
      closeIconColor,
      closeIconColorHover,
      closeIconColorPressed,
      borderRadiusSmall: borderRadius,
      fontSizeMini,
      fontSizeTiny,
      fontSizeSmall,
      fontSizeMedium,
      heightMini,
      heightTiny,
      heightSmall,
      heightMedium,
      closeColorHover,
      closeColorPressed,
      buttonColor2Hover,
      buttonColor2Pressed,
      fontWeightStrong
    } = vars;
    return Object.assign(Object.assign({}, commonVariables$5), {
      closeBorderRadius: borderRadius,
      heightTiny: heightMini,
      heightSmall: heightTiny,
      heightMedium: heightSmall,
      heightLarge: heightMedium,
      borderRadius,
      opacityDisabled,
      fontSizeTiny: fontSizeMini,
      fontSizeSmall: fontSizeTiny,
      fontSizeMedium: fontSizeSmall,
      fontSizeLarge: fontSizeMedium,
      fontWeightStrong,
      // checked
      textColorCheckable: textColor2,
      textColorHoverCheckable: textColor2,
      textColorPressedCheckable: textColor2,
      textColorChecked: baseColor,
      colorCheckable: "#0000",
      colorHoverCheckable: buttonColor2Hover,
      colorPressedCheckable: buttonColor2Pressed,
      colorChecked: primaryColor,
      colorCheckedHover: primaryColorHover,
      colorCheckedPressed: primaryColorPressed,
      // default
      border: `1px solid ${borderColor}`,
      textColor: textColor2,
      color: tagColor,
      colorBordered: "rgb(250, 250, 252)",
      closeIconColor,
      closeIconColorHover,
      closeIconColorPressed,
      closeColorHover,
      closeColorPressed,
      borderPrimary: `1px solid ${changeColor(primaryColor, {
        alpha: 0.3
      })}`,
      textColorPrimary: primaryColor,
      colorPrimary: changeColor(primaryColor, {
        alpha: 0.12
      }),
      colorBorderedPrimary: changeColor(primaryColor, {
        alpha: 0.1
      }),
      closeIconColorPrimary: primaryColor,
      closeIconColorHoverPrimary: primaryColor,
      closeIconColorPressedPrimary: primaryColor,
      closeColorHoverPrimary: changeColor(primaryColor, {
        alpha: 0.12
      }),
      closeColorPressedPrimary: changeColor(primaryColor, {
        alpha: 0.18
      }),
      borderInfo: `1px solid ${changeColor(infoColor, {
        alpha: 0.3
      })}`,
      textColorInfo: infoColor,
      colorInfo: changeColor(infoColor, {
        alpha: 0.12
      }),
      colorBorderedInfo: changeColor(infoColor, {
        alpha: 0.1
      }),
      closeIconColorInfo: infoColor,
      closeIconColorHoverInfo: infoColor,
      closeIconColorPressedInfo: infoColor,
      closeColorHoverInfo: changeColor(infoColor, {
        alpha: 0.12
      }),
      closeColorPressedInfo: changeColor(infoColor, {
        alpha: 0.18
      }),
      borderSuccess: `1px solid ${changeColor(successColor, {
        alpha: 0.3
      })}`,
      textColorSuccess: successColor,
      colorSuccess: changeColor(successColor, {
        alpha: 0.12
      }),
      colorBorderedSuccess: changeColor(successColor, {
        alpha: 0.1
      }),
      closeIconColorSuccess: successColor,
      closeIconColorHoverSuccess: successColor,
      closeIconColorPressedSuccess: successColor,
      closeColorHoverSuccess: changeColor(successColor, {
        alpha: 0.12
      }),
      closeColorPressedSuccess: changeColor(successColor, {
        alpha: 0.18
      }),
      borderWarning: `1px solid ${changeColor(warningColor, {
        alpha: 0.35
      })}`,
      textColorWarning: warningColor,
      colorWarning: changeColor(warningColor, {
        alpha: 0.15
      }),
      colorBorderedWarning: changeColor(warningColor, {
        alpha: 0.12
      }),
      closeIconColorWarning: warningColor,
      closeIconColorHoverWarning: warningColor,
      closeIconColorPressedWarning: warningColor,
      closeColorHoverWarning: changeColor(warningColor, {
        alpha: 0.12
      }),
      closeColorPressedWarning: changeColor(warningColor, {
        alpha: 0.18
      }),
      borderError: `1px solid ${changeColor(errorColor, {
        alpha: 0.23
      })}`,
      textColorError: errorColor,
      colorError: changeColor(errorColor, {
        alpha: 0.1
      }),
      colorBorderedError: changeColor(errorColor, {
        alpha: 0.08
      }),
      closeIconColorError: errorColor,
      closeIconColorHoverError: errorColor,
      closeIconColorPressedError: errorColor,
      closeColorHoverError: changeColor(errorColor, {
        alpha: 0.12
      }),
      closeColorPressedError: changeColor(errorColor, {
        alpha: 0.18
      })
    });
  }
  const tagLight = {
    common: derived,
    self: self$e
  };
  const commonProps = {
    color: Object,
    type: {
      type: String,
      default: "default"
    },
    round: Boolean,
    size: String,
    closable: Boolean,
    disabled: {
      type: Boolean,
      default: void 0
    }
  };
  const style$f = cB("tag", `
 --n-close-margin: var(--n-close-margin-top) var(--n-close-margin-right) var(--n-close-margin-bottom) var(--n-close-margin-left);
 white-space: nowrap;
 position: relative;
 box-sizing: border-box;
 cursor: default;
 display: inline-flex;
 align-items: center;
 flex-wrap: nowrap;
 padding: var(--n-padding);
 border-radius: var(--n-border-radius);
 color: var(--n-text-color);
 background-color: var(--n-color);
 transition: 
 border-color .3s var(--n-bezier),
 background-color .3s var(--n-bezier),
 color .3s var(--n-bezier),
 box-shadow .3s var(--n-bezier),
 opacity .3s var(--n-bezier);
 line-height: 1;
 height: var(--n-height);
 font-size: var(--n-font-size);
`, [cM("strong", `
 font-weight: var(--n-font-weight-strong);
 `), cE("border", `
 pointer-events: none;
 position: absolute;
 left: 0;
 right: 0;
 top: 0;
 bottom: 0;
 border-radius: inherit;
 border: var(--n-border);
 transition: border-color .3s var(--n-bezier);
 `), cE("icon", `
 display: flex;
 margin: 0 4px 0 0;
 color: var(--n-text-color);
 transition: color .3s var(--n-bezier);
 font-size: var(--n-avatar-size-override);
 `), cE("avatar", `
 display: flex;
 margin: 0 6px 0 0;
 `), cE("close", `
 margin: var(--n-close-margin);
 transition:
 background-color .3s var(--n-bezier),
 color .3s var(--n-bezier);
 `), cM("round", `
 padding: 0 calc(var(--n-height) / 3);
 border-radius: calc(var(--n-height) / 2);
 `, [cE("icon", `
 margin: 0 4px 0 calc((var(--n-height) - 8px) / -2);
 `), cE("avatar", `
 margin: 0 6px 0 calc((var(--n-height) - 8px) / -2);
 `), cM("closable", `
 padding: 0 calc(var(--n-height) / 4) 0 calc(var(--n-height) / 3);
 `)]), cM("icon, avatar", [cM("round", `
 padding: 0 calc(var(--n-height) / 3) 0 calc(var(--n-height) / 2);
 `)]), cM("disabled", `
 cursor: not-allowed !important;
 opacity: var(--n-opacity-disabled);
 `), cM("checkable", `
 cursor: pointer;
 box-shadow: none;
 color: var(--n-text-color-checkable);
 background-color: var(--n-color-checkable);
 `, [cNotM("disabled", [c$1("&:hover", "background-color: var(--n-color-hover-checkable);", [cNotM("checked", "color: var(--n-text-color-hover-checkable);")]), c$1("&:active", "background-color: var(--n-color-pressed-checkable);", [cNotM("checked", "color: var(--n-text-color-pressed-checkable);")])]), cM("checked", `
 color: var(--n-text-color-checked);
 background-color: var(--n-color-checked);
 `, [cNotM("disabled", [c$1("&:hover", "background-color: var(--n-color-checked-hover);"), c$1("&:active", "background-color: var(--n-color-checked-pressed);")])])])]);
  const tagProps = Object.assign(Object.assign(Object.assign({}, useTheme.props), commonProps), {
    bordered: {
      type: Boolean,
      default: void 0
    },
    checked: Boolean,
    checkable: Boolean,
    strong: Boolean,
    triggerClickOnClose: Boolean,
    onClose: [Array, Function],
    onMouseenter: Function,
    onMouseleave: Function,
    "onUpdate:checked": Function,
    onUpdateChecked: Function,
    // private
    internalCloseFocusable: {
      type: Boolean,
      default: true
    },
    internalCloseIsButtonTag: {
      type: Boolean,
      default: true
    },
    // deprecated
    onCheckedChange: Function
  });
  const tagInjectionKey = createInjectionKey("n-tag");
  const NTag = /* @__PURE__ */ defineComponent({
    name: "Tag",
    props: tagProps,
    slots: Object,
    setup(props) {
      const contentRef = /* @__PURE__ */ ref(null);
      const {
        mergedBorderedRef,
        mergedClsPrefixRef,
        inlineThemeDisabled,
        mergedRtlRef,
        mergedComponentPropsRef
      } = useConfig(props);
      const mergedSizeRef = computed(() => {
        var _a2, _b;
        return props.size || ((_b = (_a2 = mergedComponentPropsRef === null || mergedComponentPropsRef === void 0 ? void 0 : mergedComponentPropsRef.value) === null || _a2 === void 0 ? void 0 : _a2.Tag) === null || _b === void 0 ? void 0 : _b.size) || "medium";
      });
      const themeRef = useTheme("Tag", "-tag", style$f, tagLight, props, mergedClsPrefixRef);
      provide(tagInjectionKey, {
        roundRef: /* @__PURE__ */ toRef(props, "round")
      });
      function handleClick() {
        if (!props.disabled) {
          if (props.checkable) {
            const {
              checked,
              onCheckedChange,
              onUpdateChecked,
              "onUpdate:checked": _onUpdateChecked
            } = props;
            if (onUpdateChecked) onUpdateChecked(!checked);
            if (_onUpdateChecked) _onUpdateChecked(!checked);
            if (onCheckedChange) onCheckedChange(!checked);
          }
        }
      }
      function handleCloseClick(e) {
        if (!props.triggerClickOnClose) {
          e.stopPropagation();
        }
        if (!props.disabled) {
          const {
            onClose
          } = props;
          if (onClose) call(onClose, e);
        }
      }
      const tagPublicMethods = {
        setTextContent(textContent) {
          const {
            value
          } = contentRef;
          if (value) value.textContent = textContent;
        }
      };
      const rtlEnabledRef = useRtl("Tag", mergedRtlRef, mergedClsPrefixRef);
      const cssVarsRef = computed(() => {
        const {
          type,
          color: {
            color,
            textColor
          } = {}
        } = props;
        const size2 = mergedSizeRef.value;
        const {
          common: {
            cubicBezierEaseInOut: cubicBezierEaseInOut2
          },
          self: {
            padding,
            closeMargin,
            borderRadius,
            opacityDisabled,
            textColorCheckable,
            textColorHoverCheckable,
            textColorPressedCheckable,
            textColorChecked,
            colorCheckable,
            colorHoverCheckable,
            colorPressedCheckable,
            colorChecked,
            colorCheckedHover,
            colorCheckedPressed,
            closeBorderRadius,
            fontWeightStrong,
            [createKey("colorBordered", type)]: colorBordered,
            [createKey("closeSize", size2)]: closeSize,
            [createKey("closeIconSize", size2)]: closeIconSize,
            [createKey("fontSize", size2)]: fontSize2,
            [createKey("height", size2)]: height,
            [createKey("color", type)]: typedColor,
            [createKey("textColor", type)]: typeTextColor,
            [createKey("border", type)]: border,
            [createKey("closeIconColor", type)]: closeIconColor,
            [createKey("closeIconColorHover", type)]: closeIconColorHover,
            [createKey("closeIconColorPressed", type)]: closeIconColorPressed,
            [createKey("closeColorHover", type)]: closeColorHover,
            [createKey("closeColorPressed", type)]: closeColorPressed
          }
        } = themeRef.value;
        const closeMarginDiscrete = getMargin(closeMargin);
        return {
          "--n-font-weight-strong": fontWeightStrong,
          "--n-avatar-size-override": `calc(${height} - 8px)`,
          "--n-bezier": cubicBezierEaseInOut2,
          "--n-border-radius": borderRadius,
          "--n-border": border,
          "--n-close-icon-size": closeIconSize,
          "--n-close-color-pressed": closeColorPressed,
          "--n-close-color-hover": closeColorHover,
          "--n-close-border-radius": closeBorderRadius,
          "--n-close-icon-color": closeIconColor,
          "--n-close-icon-color-hover": closeIconColorHover,
          "--n-close-icon-color-pressed": closeIconColorPressed,
          "--n-close-icon-color-disabled": closeIconColor,
          "--n-close-margin-top": closeMarginDiscrete.top,
          "--n-close-margin-right": closeMarginDiscrete.right,
          "--n-close-margin-bottom": closeMarginDiscrete.bottom,
          "--n-close-margin-left": closeMarginDiscrete.left,
          "--n-close-size": closeSize,
          "--n-color": color || (mergedBorderedRef.value ? colorBordered : typedColor),
          "--n-color-checkable": colorCheckable,
          "--n-color-checked": colorChecked,
          "--n-color-checked-hover": colorCheckedHover,
          "--n-color-checked-pressed": colorCheckedPressed,
          "--n-color-hover-checkable": colorHoverCheckable,
          "--n-color-pressed-checkable": colorPressedCheckable,
          "--n-font-size": fontSize2,
          "--n-height": height,
          "--n-opacity-disabled": opacityDisabled,
          "--n-padding": padding,
          "--n-text-color": textColor || typeTextColor,
          "--n-text-color-checkable": textColorCheckable,
          "--n-text-color-checked": textColorChecked,
          "--n-text-color-hover-checkable": textColorHoverCheckable,
          "--n-text-color-pressed-checkable": textColorPressedCheckable
        };
      });
      const themeClassHandle = inlineThemeDisabled ? useThemeClass("tag", computed(() => {
        let hash = "";
        const {
          type,
          color: {
            color,
            textColor
          } = {}
        } = props;
        hash += type[0];
        hash += mergedSizeRef.value[0];
        if (color) {
          hash += `a${color2Class(color)}`;
        }
        if (textColor) {
          hash += `b${color2Class(textColor)}`;
        }
        if (mergedBorderedRef.value) {
          hash += "c";
        }
        return hash;
      }), cssVarsRef, props) : void 0;
      return Object.assign(Object.assign({}, tagPublicMethods), {
        rtlEnabled: rtlEnabledRef,
        mergedClsPrefix: mergedClsPrefixRef,
        contentRef,
        mergedBordered: mergedBorderedRef,
        handleClick,
        handleCloseClick,
        cssVars: inlineThemeDisabled ? void 0 : cssVarsRef,
        themeClass: themeClassHandle === null || themeClassHandle === void 0 ? void 0 : themeClassHandle.themeClass,
        onRender: themeClassHandle === null || themeClassHandle === void 0 ? void 0 : themeClassHandle.onRender
      });
    },
    render() {
      var _a2, _b;
      const {
        mergedClsPrefix,
        rtlEnabled,
        closable,
        color: {
          borderColor
        } = {},
        round,
        onRender,
        $slots
      } = this;
      onRender === null || onRender === void 0 ? void 0 : onRender();
      const avatarNode = resolveWrappedSlot($slots.avatar, (children) => children && h("div", {
        class: `${mergedClsPrefix}-tag__avatar`
      }, children));
      const iconNode = resolveWrappedSlot($slots.icon, (children) => children && h("div", {
        class: `${mergedClsPrefix}-tag__icon`
      }, children));
      return h("div", {
        class: [`${mergedClsPrefix}-tag`, this.themeClass, {
          [`${mergedClsPrefix}-tag--rtl`]: rtlEnabled,
          [`${mergedClsPrefix}-tag--strong`]: this.strong,
          [`${mergedClsPrefix}-tag--disabled`]: this.disabled,
          [`${mergedClsPrefix}-tag--checkable`]: this.checkable,
          [`${mergedClsPrefix}-tag--checked`]: this.checkable && this.checked,
          [`${mergedClsPrefix}-tag--round`]: round,
          [`${mergedClsPrefix}-tag--avatar`]: avatarNode,
          [`${mergedClsPrefix}-tag--icon`]: iconNode,
          [`${mergedClsPrefix}-tag--closable`]: closable
        }],
        style: this.cssVars,
        onClick: this.handleClick,
        onMouseenter: this.onMouseenter,
        onMouseleave: this.onMouseleave
      }, iconNode || avatarNode, h("span", {
        class: `${mergedClsPrefix}-tag__content`,
        ref: "contentRef"
      }, (_b = (_a2 = this.$slots).default) === null || _b === void 0 ? void 0 : _b.call(_a2)), !this.checkable && closable ? h(NBaseClose, {
        clsPrefix: mergedClsPrefix,
        class: `${mergedClsPrefix}-tag__close`,
        disabled: this.disabled,
        onClick: this.handleCloseClick,
        focusable: this.internalCloseFocusable,
        round,
        isButtonTag: this.internalCloseIsButtonTag,
        absolute: true
      }) : null, !this.checkable && this.mergedBordered ? h("div", {
        class: `${mergedClsPrefix}-tag__border`,
        style: {
          borderColor
        }
      }) : null);
    }
  });
  const {
    cubicBezierEaseInOut: cubicBezierEaseInOut$1
  } = commonVariables$7;
  function fadeInWidthExpandTransition({
    duration: duration2 = ".2s",
    delay = ".1s"
  } = {}) {
    return [c$1("&.fade-in-width-expand-transition-leave-from, &.fade-in-width-expand-transition-enter-to", {
      opacity: 1
    }), c$1("&.fade-in-width-expand-transition-leave-to, &.fade-in-width-expand-transition-enter-from", `
 opacity: 0!important;
 margin-left: 0!important;
 margin-right: 0!important;
 `), c$1("&.fade-in-width-expand-transition-leave-active", `
 overflow: hidden;
 transition:
 opacity ${duration2} ${cubicBezierEaseInOut$1},
 max-width ${duration2} ${cubicBezierEaseInOut$1} ${delay},
 margin-left ${duration2} ${cubicBezierEaseInOut$1} ${delay},
 margin-right ${duration2} ${cubicBezierEaseInOut$1} ${delay};
 `), c$1("&.fade-in-width-expand-transition-enter-active", `
 overflow: hidden;
 transition:
 opacity ${duration2} ${cubicBezierEaseInOut$1} ${delay},
 max-width ${duration2} ${cubicBezierEaseInOut$1},
 margin-left ${duration2} ${cubicBezierEaseInOut$1},
 margin-right ${duration2} ${cubicBezierEaseInOut$1};
 `)];
  }
  const style$e = cB("base-wave", `
 position: absolute;
 left: 0;
 right: 0;
 top: 0;
 bottom: 0;
 border-radius: inherit;
`);
  const NBaseWave = /* @__PURE__ */ defineComponent({
    name: "BaseWave",
    props: {
      clsPrefix: {
        type: String,
        required: true
      }
    },
    setup(props) {
      useStyle("-base-wave", style$e, /* @__PURE__ */ toRef(props, "clsPrefix"));
      const selfRef = /* @__PURE__ */ ref(null);
      const activeRef = /* @__PURE__ */ ref(false);
      let animationTimerId = null;
      onBeforeUnmount(() => {
        if (animationTimerId !== null) {
          window.clearTimeout(animationTimerId);
        }
      });
      return {
        active: activeRef,
        selfRef,
        play() {
          if (animationTimerId !== null) {
            window.clearTimeout(animationTimerId);
            activeRef.value = false;
            animationTimerId = null;
          }
          void nextTick(() => {
            var _a2;
            void ((_a2 = selfRef.value) === null || _a2 === void 0 ? void 0 : _a2.offsetHeight);
            activeRef.value = true;
            animationTimerId = window.setTimeout(() => {
              activeRef.value = false;
              animationTimerId = null;
            }, 1e3);
          });
        }
      };
    },
    render() {
      const {
        clsPrefix
      } = this;
      return h("div", {
        ref: "selfRef",
        "aria-hidden": true,
        class: [`${clsPrefix}-base-wave`, this.active && `${clsPrefix}-base-wave--active`]
      });
    }
  });
  const commonVars$3 = {
    iconMargin: "11px 8px 0 12px",
    iconMarginRtl: "11px 12px 0 8px",
    iconSize: "24px",
    closeIconSize: "16px",
    closeSize: "20px",
    closeMargin: "13px 14px 0 0",
    closeMarginRtl: "13px 0 0 14px",
    padding: "13px"
  };
  function self$d(vars) {
    const {
      lineHeight: lineHeight2,
      borderRadius,
      fontWeightStrong,
      baseColor,
      dividerColor,
      actionColor,
      textColor1,
      textColor2,
      closeColorHover,
      closeColorPressed,
      closeIconColor,
      closeIconColorHover,
      closeIconColorPressed,
      infoColor,
      successColor,
      warningColor,
      errorColor,
      fontSize: fontSize2
    } = vars;
    return Object.assign(Object.assign({}, commonVars$3), {
      fontSize: fontSize2,
      lineHeight: lineHeight2,
      titleFontWeight: fontWeightStrong,
      borderRadius,
      border: `1px solid ${dividerColor}`,
      color: actionColor,
      titleTextColor: textColor1,
      iconColor: textColor2,
      contentTextColor: textColor2,
      closeBorderRadius: borderRadius,
      closeColorHover,
      closeColorPressed,
      closeIconColor,
      closeIconColorHover,
      closeIconColorPressed,
      borderInfo: `1px solid ${composite(baseColor, changeColor(infoColor, {
        alpha: 0.25
      }))}`,
      colorInfo: composite(baseColor, changeColor(infoColor, {
        alpha: 0.08
      })),
      titleTextColorInfo: textColor1,
      iconColorInfo: infoColor,
      contentTextColorInfo: textColor2,
      closeColorHoverInfo: closeColorHover,
      closeColorPressedInfo: closeColorPressed,
      closeIconColorInfo: closeIconColor,
      closeIconColorHoverInfo: closeIconColorHover,
      closeIconColorPressedInfo: closeIconColorPressed,
      borderSuccess: `1px solid ${composite(baseColor, changeColor(successColor, {
        alpha: 0.25
      }))}`,
      colorSuccess: composite(baseColor, changeColor(successColor, {
        alpha: 0.08
      })),
      titleTextColorSuccess: textColor1,
      iconColorSuccess: successColor,
      contentTextColorSuccess: textColor2,
      closeColorHoverSuccess: closeColorHover,
      closeColorPressedSuccess: closeColorPressed,
      closeIconColorSuccess: closeIconColor,
      closeIconColorHoverSuccess: closeIconColorHover,
      closeIconColorPressedSuccess: closeIconColorPressed,
      borderWarning: `1px solid ${composite(baseColor, changeColor(warningColor, {
        alpha: 0.33
      }))}`,
      colorWarning: composite(baseColor, changeColor(warningColor, {
        alpha: 0.08
      })),
      titleTextColorWarning: textColor1,
      iconColorWarning: warningColor,
      contentTextColorWarning: textColor2,
      closeColorHoverWarning: closeColorHover,
      closeColorPressedWarning: closeColorPressed,
      closeIconColorWarning: closeIconColor,
      closeIconColorHoverWarning: closeIconColorHover,
      closeIconColorPressedWarning: closeIconColorPressed,
      borderError: `1px solid ${composite(baseColor, changeColor(errorColor, {
        alpha: 0.25
      }))}`,
      colorError: composite(baseColor, changeColor(errorColor, {
        alpha: 0.08
      })),
      titleTextColorError: textColor1,
      iconColorError: errorColor,
      contentTextColorError: textColor2,
      closeColorHoverError: closeColorHover,
      closeColorPressedError: closeColorPressed,
      closeIconColorError: closeIconColor,
      closeIconColorHoverError: closeIconColorHover,
      closeIconColorPressedError: closeIconColorPressed
    });
  }
  const alertLight = {
    common: derived,
    self: self$d
  };
  const {
    cubicBezierEaseInOut,
    cubicBezierEaseOut,
    cubicBezierEaseIn
  } = commonVariables$7;
  function fadeInHeightExpandTransition({
    overflow = "hidden",
    duration: duration2 = ".3s",
    originalTransition = "",
    leavingDelay = "0s",
    foldPadding = false,
    enterToProps = void 0,
    leaveToProps = void 0,
    reverse = false
  } = {}) {
    const enterClass = reverse ? "leave" : "enter";
    const leaveClass = reverse ? "enter" : "leave";
    return [c$1(`&.fade-in-height-expand-transition-${leaveClass}-from,
 &.fade-in-height-expand-transition-${enterClass}-to`, Object.assign(Object.assign({}, enterToProps), {
      opacity: 1
    })), c$1(`&.fade-in-height-expand-transition-${leaveClass}-to,
 &.fade-in-height-expand-transition-${enterClass}-from`, Object.assign(Object.assign({}, leaveToProps), {
      opacity: 0,
      marginTop: "0 !important",
      marginBottom: "0 !important",
      paddingTop: foldPadding ? "0 !important" : void 0,
      paddingBottom: foldPadding ? "0 !important" : void 0
    })), c$1(`&.fade-in-height-expand-transition-${leaveClass}-active`, `
 overflow: ${overflow};
 transition:
 max-height ${duration2} ${cubicBezierEaseInOut} ${leavingDelay},
 opacity ${duration2} ${cubicBezierEaseOut} ${leavingDelay},
 margin-top ${duration2} ${cubicBezierEaseInOut} ${leavingDelay},
 margin-bottom ${duration2} ${cubicBezierEaseInOut} ${leavingDelay},
 padding-top ${duration2} ${cubicBezierEaseInOut} ${leavingDelay},
 padding-bottom ${duration2} ${cubicBezierEaseInOut} ${leavingDelay}
 ${originalTransition ? `,${originalTransition}` : ""}
 `), c$1(`&.fade-in-height-expand-transition-${enterClass}-active`, `
 overflow: ${overflow};
 transition:
 max-height ${duration2} ${cubicBezierEaseInOut},
 opacity ${duration2} ${cubicBezierEaseIn},
 margin-top ${duration2} ${cubicBezierEaseInOut},
 margin-bottom ${duration2} ${cubicBezierEaseInOut},
 padding-top ${duration2} ${cubicBezierEaseInOut},
 padding-bottom ${duration2} ${cubicBezierEaseInOut}
 ${originalTransition ? `,${originalTransition}` : ""}
 `)];
  }
  const style$d = cB("alert", `
 line-height: var(--n-line-height);
 border-radius: var(--n-border-radius);
 position: relative;
 transition: background-color .3s var(--n-bezier);
 background-color: var(--n-color);
 text-align: start;
 word-break: break-word;
`, [
    cE("border", `
 border-radius: inherit;
 position: absolute;
 left: 0;
 right: 0;
 top: 0;
 bottom: 0;
 transition: border-color .3s var(--n-bezier);
 border: var(--n-border);
 pointer-events: none;
 `),
    cM("closable", [cB("alert-body", [cE("title", `
 padding-right: 24px;
 `)])]),
    cE("icon", {
      color: "var(--n-icon-color)"
    }),
    cB("alert-body", {
      padding: "var(--n-padding)"
    }, [cE("title", {
      color: "var(--n-title-text-color)"
    }), cE("content", {
      color: "var(--n-content-text-color)"
    })]),
    fadeInHeightExpandTransition({
      originalTransition: "transform .3s var(--n-bezier)",
      enterToProps: {
        transform: "scale(1)"
      },
      leaveToProps: {
        transform: "scale(0.9)"
      }
    }),
    cE("icon", `
 position: absolute;
 left: 0;
 top: 0;
 align-items: center;
 justify-content: center;
 display: flex;
 width: var(--n-icon-size);
 height: var(--n-icon-size);
 font-size: var(--n-icon-size);
 margin: var(--n-icon-margin);
 `),
    cE("close", `
 transition:
 color .3s var(--n-bezier),
 background-color .3s var(--n-bezier);
 position: absolute;
 right: 0;
 top: 0;
 margin: var(--n-close-margin);
 `),
    cM("show-icon", [cB("alert-body", {
      paddingLeft: "calc(var(--n-icon-margin-left) + var(--n-icon-size) + var(--n-icon-margin-right))"
    })]),
    // fix: https://github.com/tusen-ai/naive-ui/issues/4588
    cM("right-adjust", [cB("alert-body", {
      paddingRight: "calc(var(--n-close-size) + var(--n-padding) + 2px)"
    })]),
    cB("alert-body", `
 border-radius: var(--n-border-radius);
 transition: border-color .3s var(--n-bezier);
 `, [cE("title", `
 transition: color .3s var(--n-bezier);
 font-size: 16px;
 line-height: 19px;
 font-weight: var(--n-title-font-weight);
 `, [c$1("& +", [cE("content", {
      marginTop: "9px"
    })])]), cE("content", {
      transition: "color .3s var(--n-bezier)",
      fontSize: "var(--n-font-size)"
    })]),
    cE("icon", {
      transition: "color .3s var(--n-bezier)"
    })
  ]);
  const alertProps = Object.assign(Object.assign({}, useTheme.props), {
    title: String,
    showIcon: {
      type: Boolean,
      default: true
    },
    type: {
      type: String,
      default: "default"
    },
    bordered: {
      type: Boolean,
      default: true
    },
    closable: Boolean,
    onClose: Function,
    onAfterLeave: Function,
    /** @deprecated */
    onAfterHide: Function
  });
  const NAlert = /* @__PURE__ */ defineComponent({
    name: "Alert",
    inheritAttrs: false,
    props: alertProps,
    slots: Object,
    setup(props) {
      const {
        mergedClsPrefixRef,
        mergedBorderedRef,
        inlineThemeDisabled,
        mergedRtlRef
      } = useConfig(props);
      const themeRef = useTheme("Alert", "-alert", style$d, alertLight, props, mergedClsPrefixRef);
      const rtlEnabledRef = useRtl("Alert", mergedRtlRef, mergedClsPrefixRef);
      const cssVarsRef = computed(() => {
        const {
          common: {
            cubicBezierEaseInOut: cubicBezierEaseInOut2
          },
          self: self2
        } = themeRef.value;
        const {
          fontSize: fontSize2,
          borderRadius,
          titleFontWeight,
          lineHeight: lineHeight2,
          iconSize,
          iconMargin,
          iconMarginRtl,
          closeIconSize,
          closeBorderRadius,
          closeSize,
          closeMargin,
          closeMarginRtl,
          padding
        } = self2;
        const {
          type
        } = props;
        const {
          left,
          right
        } = getMargin(iconMargin);
        return {
          "--n-bezier": cubicBezierEaseInOut2,
          "--n-color": self2[createKey("color", type)],
          "--n-close-icon-size": closeIconSize,
          "--n-close-border-radius": closeBorderRadius,
          "--n-close-color-hover": self2[createKey("closeColorHover", type)],
          "--n-close-color-pressed": self2[createKey("closeColorPressed", type)],
          "--n-close-icon-color": self2[createKey("closeIconColor", type)],
          "--n-close-icon-color-hover": self2[createKey("closeIconColorHover", type)],
          "--n-close-icon-color-pressed": self2[createKey("closeIconColorPressed", type)],
          "--n-icon-color": self2[createKey("iconColor", type)],
          "--n-border": self2[createKey("border", type)],
          "--n-title-text-color": self2[createKey("titleTextColor", type)],
          "--n-content-text-color": self2[createKey("contentTextColor", type)],
          "--n-line-height": lineHeight2,
          "--n-border-radius": borderRadius,
          "--n-font-size": fontSize2,
          "--n-title-font-weight": titleFontWeight,
          "--n-icon-size": iconSize,
          "--n-icon-margin": iconMargin,
          "--n-icon-margin-rtl": iconMarginRtl,
          "--n-close-size": closeSize,
          "--n-close-margin": closeMargin,
          "--n-close-margin-rtl": closeMarginRtl,
          "--n-padding": padding,
          "--n-icon-margin-left": left,
          "--n-icon-margin-right": right
        };
      });
      const themeClassHandle = inlineThemeDisabled ? useThemeClass("alert", computed(() => {
        return props.type[0];
      }), cssVarsRef, props) : void 0;
      const visibleRef = /* @__PURE__ */ ref(true);
      const doAfterLeave = () => {
        const {
          onAfterLeave,
          onAfterHide
          // deprecated
        } = props;
        if (onAfterLeave) onAfterLeave();
        if (onAfterHide) onAfterHide();
      };
      const handleCloseClick = () => {
        var _a2;
        void Promise.resolve((_a2 = props.onClose) === null || _a2 === void 0 ? void 0 : _a2.call(props)).then((result) => {
          if (result === false) return;
          visibleRef.value = false;
        });
      };
      const handleAfterLeave = () => {
        doAfterLeave();
      };
      return {
        rtlEnabled: rtlEnabledRef,
        mergedClsPrefix: mergedClsPrefixRef,
        mergedBordered: mergedBorderedRef,
        visible: visibleRef,
        handleCloseClick,
        handleAfterLeave,
        mergedTheme: themeRef,
        cssVars: inlineThemeDisabled ? void 0 : cssVarsRef,
        themeClass: themeClassHandle === null || themeClassHandle === void 0 ? void 0 : themeClassHandle.themeClass,
        onRender: themeClassHandle === null || themeClassHandle === void 0 ? void 0 : themeClassHandle.onRender
      };
    },
    render() {
      var _a2;
      (_a2 = this.onRender) === null || _a2 === void 0 ? void 0 : _a2.call(this);
      return h(NFadeInExpandTransition, {
        onAfterLeave: this.handleAfterLeave
      }, {
        default: () => {
          const {
            mergedClsPrefix,
            $slots
          } = this;
          const attrs = {
            class: [
              `${mergedClsPrefix}-alert`,
              this.themeClass,
              this.closable && `${mergedClsPrefix}-alert--closable`,
              this.showIcon && `${mergedClsPrefix}-alert--show-icon`,
              // fix: https://github.com/tusen-ai/naive-ui/issues/4588
              !this.title && this.closable && `${mergedClsPrefix}-alert--right-adjust`,
              this.rtlEnabled && `${mergedClsPrefix}-alert--rtl`
            ],
            style: this.cssVars,
            role: "alert"
          };
          return this.visible ? h("div", Object.assign({}, mergeProps(this.$attrs, attrs)), this.closable && h(NBaseClose, {
            clsPrefix: mergedClsPrefix,
            class: `${mergedClsPrefix}-alert__close`,
            onClick: this.handleCloseClick
          }), this.bordered && h("div", {
            class: `${mergedClsPrefix}-alert__border`
          }), this.showIcon && h("div", {
            class: `${mergedClsPrefix}-alert__icon`,
            "aria-hidden": "true"
          }, resolveSlot($slots.icon, () => [h(NBaseIcon, {
            clsPrefix: mergedClsPrefix
          }, {
            default: () => {
              switch (this.type) {
                case "success":
                  return h(SuccessIcon, null);
                case "info":
                  return h(InfoIcon, null);
                case "warning":
                  return h(WarningIcon, null);
                case "error":
                  return h(ErrorIcon, null);
                default:
                  return null;
              }
            }
          })])), h("div", {
            class: [`${mergedClsPrefix}-alert-body`, this.mergedBordered && `${mergedClsPrefix}-alert-body--bordered`]
          }, resolveWrappedSlot($slots.header, (children) => {
            const mergedChildren = children || this.title;
            return mergedChildren ? h("div", {
              class: `${mergedClsPrefix}-alert-body__title`
            }, mergedChildren) : null;
          }), $slots.default && h("div", {
            class: `${mergedClsPrefix}-alert-body__content`
          }, $slots))) : null;
        }
      });
    }
  });
  const isChrome = isBrowser$1 && "chrome" in window;
  isBrowser$1 && navigator.userAgent.includes("Firefox");
  const isSafari = isBrowser$1 && navigator.userAgent.includes("Safari") && !isChrome;
  function createHoverColor(rgb) {
    return composite(rgb, [255, 255, 255, 0.16]);
  }
  function createPressedColor(rgb) {
    return composite(rgb, [0, 0, 0, 0.12]);
  }
  const buttonGroupInjectionKey = createInjectionKey("n-button-group");
  const commonVariables$4 = {
    paddingTiny: "0 6px",
    paddingSmall: "0 10px",
    paddingMedium: "0 14px",
    paddingLarge: "0 18px",
    paddingRoundTiny: "0 10px",
    paddingRoundSmall: "0 14px",
    paddingRoundMedium: "0 18px",
    paddingRoundLarge: "0 22px",
    iconMarginTiny: "6px",
    iconMarginSmall: "6px",
    iconMarginMedium: "6px",
    iconMarginLarge: "6px",
    iconSizeTiny: "14px",
    iconSizeSmall: "18px",
    iconSizeMedium: "18px",
    iconSizeLarge: "20px",
    rippleDuration: ".6s"
  };
  function self$c(vars) {
    const {
      heightTiny,
      heightSmall,
      heightMedium,
      heightLarge,
      borderRadius,
      fontSizeTiny,
      fontSizeSmall,
      fontSizeMedium,
      fontSizeLarge,
      opacityDisabled,
      textColor2,
      textColor3,
      primaryColorHover,
      primaryColorPressed,
      borderColor,
      primaryColor,
      baseColor,
      infoColor,
      infoColorHover,
      infoColorPressed,
      successColor,
      successColorHover,
      successColorPressed,
      warningColor,
      warningColorHover,
      warningColorPressed,
      errorColor,
      errorColorHover,
      errorColorPressed,
      fontWeight,
      buttonColor2,
      buttonColor2Hover,
      buttonColor2Pressed,
      fontWeightStrong
    } = vars;
    return Object.assign(Object.assign({}, commonVariables$4), {
      heightTiny,
      heightSmall,
      heightMedium,
      heightLarge,
      borderRadiusTiny: borderRadius,
      borderRadiusSmall: borderRadius,
      borderRadiusMedium: borderRadius,
      borderRadiusLarge: borderRadius,
      fontSizeTiny,
      fontSizeSmall,
      fontSizeMedium,
      fontSizeLarge,
      opacityDisabled,
      // secondary
      colorOpacitySecondary: "0.16",
      colorOpacitySecondaryHover: "0.22",
      colorOpacitySecondaryPressed: "0.28",
      colorSecondary: buttonColor2,
      colorSecondaryHover: buttonColor2Hover,
      colorSecondaryPressed: buttonColor2Pressed,
      // tertiary
      colorTertiary: buttonColor2,
      colorTertiaryHover: buttonColor2Hover,
      colorTertiaryPressed: buttonColor2Pressed,
      // quaternary
      colorQuaternary: "#0000",
      colorQuaternaryHover: buttonColor2Hover,
      colorQuaternaryPressed: buttonColor2Pressed,
      // default type
      color: "#0000",
      colorHover: "#0000",
      colorPressed: "#0000",
      colorFocus: "#0000",
      colorDisabled: "#0000",
      textColor: textColor2,
      textColorTertiary: textColor3,
      textColorHover: primaryColorHover,
      textColorPressed: primaryColorPressed,
      textColorFocus: primaryColorHover,
      textColorDisabled: textColor2,
      textColorText: textColor2,
      textColorTextHover: primaryColorHover,
      textColorTextPressed: primaryColorPressed,
      textColorTextFocus: primaryColorHover,
      textColorTextDisabled: textColor2,
      textColorGhost: textColor2,
      textColorGhostHover: primaryColorHover,
      textColorGhostPressed: primaryColorPressed,
      textColorGhostFocus: primaryColorHover,
      textColorGhostDisabled: textColor2,
      border: `1px solid ${borderColor}`,
      borderHover: `1px solid ${primaryColorHover}`,
      borderPressed: `1px solid ${primaryColorPressed}`,
      borderFocus: `1px solid ${primaryColorHover}`,
      borderDisabled: `1px solid ${borderColor}`,
      rippleColor: primaryColor,
      // primary
      colorPrimary: primaryColor,
      colorHoverPrimary: primaryColorHover,
      colorPressedPrimary: primaryColorPressed,
      colorFocusPrimary: primaryColorHover,
      colorDisabledPrimary: primaryColor,
      textColorPrimary: baseColor,
      textColorHoverPrimary: baseColor,
      textColorPressedPrimary: baseColor,
      textColorFocusPrimary: baseColor,
      textColorDisabledPrimary: baseColor,
      textColorTextPrimary: primaryColor,
      textColorTextHoverPrimary: primaryColorHover,
      textColorTextPressedPrimary: primaryColorPressed,
      textColorTextFocusPrimary: primaryColorHover,
      textColorTextDisabledPrimary: textColor2,
      textColorGhostPrimary: primaryColor,
      textColorGhostHoverPrimary: primaryColorHover,
      textColorGhostPressedPrimary: primaryColorPressed,
      textColorGhostFocusPrimary: primaryColorHover,
      textColorGhostDisabledPrimary: primaryColor,
      borderPrimary: `1px solid ${primaryColor}`,
      borderHoverPrimary: `1px solid ${primaryColorHover}`,
      borderPressedPrimary: `1px solid ${primaryColorPressed}`,
      borderFocusPrimary: `1px solid ${primaryColorHover}`,
      borderDisabledPrimary: `1px solid ${primaryColor}`,
      rippleColorPrimary: primaryColor,
      // info
      colorInfo: infoColor,
      colorHoverInfo: infoColorHover,
      colorPressedInfo: infoColorPressed,
      colorFocusInfo: infoColorHover,
      colorDisabledInfo: infoColor,
      textColorInfo: baseColor,
      textColorHoverInfo: baseColor,
      textColorPressedInfo: baseColor,
      textColorFocusInfo: baseColor,
      textColorDisabledInfo: baseColor,
      textColorTextInfo: infoColor,
      textColorTextHoverInfo: infoColorHover,
      textColorTextPressedInfo: infoColorPressed,
      textColorTextFocusInfo: infoColorHover,
      textColorTextDisabledInfo: textColor2,
      textColorGhostInfo: infoColor,
      textColorGhostHoverInfo: infoColorHover,
      textColorGhostPressedInfo: infoColorPressed,
      textColorGhostFocusInfo: infoColorHover,
      textColorGhostDisabledInfo: infoColor,
      borderInfo: `1px solid ${infoColor}`,
      borderHoverInfo: `1px solid ${infoColorHover}`,
      borderPressedInfo: `1px solid ${infoColorPressed}`,
      borderFocusInfo: `1px solid ${infoColorHover}`,
      borderDisabledInfo: `1px solid ${infoColor}`,
      rippleColorInfo: infoColor,
      // success
      colorSuccess: successColor,
      colorHoverSuccess: successColorHover,
      colorPressedSuccess: successColorPressed,
      colorFocusSuccess: successColorHover,
      colorDisabledSuccess: successColor,
      textColorSuccess: baseColor,
      textColorHoverSuccess: baseColor,
      textColorPressedSuccess: baseColor,
      textColorFocusSuccess: baseColor,
      textColorDisabledSuccess: baseColor,
      textColorTextSuccess: successColor,
      textColorTextHoverSuccess: successColorHover,
      textColorTextPressedSuccess: successColorPressed,
      textColorTextFocusSuccess: successColorHover,
      textColorTextDisabledSuccess: textColor2,
      textColorGhostSuccess: successColor,
      textColorGhostHoverSuccess: successColorHover,
      textColorGhostPressedSuccess: successColorPressed,
      textColorGhostFocusSuccess: successColorHover,
      textColorGhostDisabledSuccess: successColor,
      borderSuccess: `1px solid ${successColor}`,
      borderHoverSuccess: `1px solid ${successColorHover}`,
      borderPressedSuccess: `1px solid ${successColorPressed}`,
      borderFocusSuccess: `1px solid ${successColorHover}`,
      borderDisabledSuccess: `1px solid ${successColor}`,
      rippleColorSuccess: successColor,
      // warning
      colorWarning: warningColor,
      colorHoverWarning: warningColorHover,
      colorPressedWarning: warningColorPressed,
      colorFocusWarning: warningColorHover,
      colorDisabledWarning: warningColor,
      textColorWarning: baseColor,
      textColorHoverWarning: baseColor,
      textColorPressedWarning: baseColor,
      textColorFocusWarning: baseColor,
      textColorDisabledWarning: baseColor,
      textColorTextWarning: warningColor,
      textColorTextHoverWarning: warningColorHover,
      textColorTextPressedWarning: warningColorPressed,
      textColorTextFocusWarning: warningColorHover,
      textColorTextDisabledWarning: textColor2,
      textColorGhostWarning: warningColor,
      textColorGhostHoverWarning: warningColorHover,
      textColorGhostPressedWarning: warningColorPressed,
      textColorGhostFocusWarning: warningColorHover,
      textColorGhostDisabledWarning: warningColor,
      borderWarning: `1px solid ${warningColor}`,
      borderHoverWarning: `1px solid ${warningColorHover}`,
      borderPressedWarning: `1px solid ${warningColorPressed}`,
      borderFocusWarning: `1px solid ${warningColorHover}`,
      borderDisabledWarning: `1px solid ${warningColor}`,
      rippleColorWarning: warningColor,
      // error
      colorError: errorColor,
      colorHoverError: errorColorHover,
      colorPressedError: errorColorPressed,
      colorFocusError: errorColorHover,
      colorDisabledError: errorColor,
      textColorError: baseColor,
      textColorHoverError: baseColor,
      textColorPressedError: baseColor,
      textColorFocusError: baseColor,
      textColorDisabledError: baseColor,
      textColorTextError: errorColor,
      textColorTextHoverError: errorColorHover,
      textColorTextPressedError: errorColorPressed,
      textColorTextFocusError: errorColorHover,
      textColorTextDisabledError: textColor2,
      textColorGhostError: errorColor,
      textColorGhostHoverError: errorColorHover,
      textColorGhostPressedError: errorColorPressed,
      textColorGhostFocusError: errorColorHover,
      textColorGhostDisabledError: errorColor,
      borderError: `1px solid ${errorColor}`,
      borderHoverError: `1px solid ${errorColorHover}`,
      borderPressedError: `1px solid ${errorColorPressed}`,
      borderFocusError: `1px solid ${errorColorHover}`,
      borderDisabledError: `1px solid ${errorColor}`,
      rippleColorError: errorColor,
      waveOpacity: "0.6",
      fontWeight,
      fontWeightStrong
    });
  }
  const buttonLight = {
    common: derived,
    self: self$c
  };
  const style$c = c$1([cB("button", `
 margin: 0;
 font-weight: var(--n-font-weight);
 line-height: 1;
 font-family: inherit;
 padding: var(--n-padding);
 height: var(--n-height);
 font-size: var(--n-font-size);
 border-radius: var(--n-border-radius);
 color: var(--n-text-color);
 background-color: var(--n-color);
 width: var(--n-width);
 white-space: nowrap;
 outline: none;
 position: relative;
 z-index: auto;
 border: none;
 display: inline-flex;
 flex-wrap: nowrap;
 flex-shrink: 0;
 align-items: center;
 justify-content: center;
 user-select: none;
 -webkit-user-select: none;
 text-align: center;
 cursor: pointer;
 text-decoration: none;
 transition:
 color .3s var(--n-bezier),
 background-color .3s var(--n-bezier),
 opacity .3s var(--n-bezier),
 border-color .3s var(--n-bezier);
 `, [cM("color", [cE("border", {
    borderColor: "var(--n-border-color)"
  }), cM("disabled", [cE("border", {
    borderColor: "var(--n-border-color-disabled)"
  })]), cNotM("disabled", [c$1("&:focus", [cE("state-border", {
    borderColor: "var(--n-border-color-focus)"
  })]), c$1("&:hover", [cE("state-border", {
    borderColor: "var(--n-border-color-hover)"
  })]), c$1("&:active", [cE("state-border", {
    borderColor: "var(--n-border-color-pressed)"
  })]), cM("pressed", [cE("state-border", {
    borderColor: "var(--n-border-color-pressed)"
  })])])]), cM("disabled", {
    backgroundColor: "var(--n-color-disabled)",
    color: "var(--n-text-color-disabled)"
  }, [cE("border", {
    border: "var(--n-border-disabled)"
  })]), cNotM("disabled", [c$1("&:focus", {
    backgroundColor: "var(--n-color-focus)",
    color: "var(--n-text-color-focus)"
  }, [cE("state-border", {
    border: "var(--n-border-focus)"
  })]), c$1("&:hover", {
    backgroundColor: "var(--n-color-hover)",
    color: "var(--n-text-color-hover)"
  }, [cE("state-border", {
    border: "var(--n-border-hover)"
  })]), c$1("&:active", {
    backgroundColor: "var(--n-color-pressed)",
    color: "var(--n-text-color-pressed)"
  }, [cE("state-border", {
    border: "var(--n-border-pressed)"
  })]), cM("pressed", {
    backgroundColor: "var(--n-color-pressed)",
    color: "var(--n-text-color-pressed)"
  }, [cE("state-border", {
    border: "var(--n-border-pressed)"
  })])]), cM("loading", "cursor: wait;"), cB("base-wave", `
 pointer-events: none;
 top: 0;
 right: 0;
 bottom: 0;
 left: 0;
 animation-iteration-count: 1;
 animation-duration: var(--n-ripple-duration);
 animation-timing-function: var(--n-bezier-ease-out), var(--n-bezier-ease-out);
 `, [cM("active", {
    zIndex: 1,
    animationName: "button-wave-spread, button-wave-opacity"
  })]), isBrowser$1 && "MozBoxSizing" in document.createElement("div").style ? c$1("&::moz-focus-inner", {
    border: 0
  }) : null, cE("border, state-border", `
 position: absolute;
 left: 0;
 top: 0;
 right: 0;
 bottom: 0;
 border-radius: inherit;
 transition: border-color .3s var(--n-bezier);
 pointer-events: none;
 `), cE("border", `
 border: var(--n-border);
 `), cE("state-border", `
 border: var(--n-border);
 border-color: #0000;
 z-index: 1;
 `), cE("icon", `
 margin: var(--n-icon-margin);
 margin-left: 0;
 height: var(--n-icon-size);
 width: var(--n-icon-size);
 max-width: var(--n-icon-size);
 font-size: var(--n-icon-size);
 position: relative;
 flex-shrink: 0;
 `, [cB("icon-slot", `
 height: var(--n-icon-size);
 width: var(--n-icon-size);
 position: absolute;
 left: 0;
 top: 50%;
 transform: translateY(-50%);
 display: flex;
 align-items: center;
 justify-content: center;
 `, [iconSwitchTransition({
    top: "50%",
    originalTransform: "translateY(-50%)"
  })]), fadeInWidthExpandTransition()]), cE("content", `
 display: flex;
 align-items: center;
 flex-wrap: nowrap;
 min-width: 0;
 `, [c$1("~", [cE("icon", {
    margin: "var(--n-icon-margin)",
    marginRight: 0
  })])]), cM("block", `
 display: flex;
 width: 100%;
 `), cM("dashed", [cE("border, state-border", {
    borderStyle: "dashed !important"
  })]), cM("disabled", {
    cursor: "not-allowed",
    opacity: "var(--n-opacity-disabled)"
  })]), c$1("@keyframes button-wave-spread", {
    from: {
      boxShadow: "0 0 0.5px 0 var(--n-ripple-color)"
    },
    to: {
      // don't use exact 5px since chrome will display the animation with glitches
      boxShadow: "0 0 0.5px 4.5px var(--n-ripple-color)"
    }
  }), c$1("@keyframes button-wave-opacity", {
    from: {
      opacity: "var(--n-wave-opacity)"
    },
    to: {
      opacity: 0
    }
  })]);
  const buttonProps = Object.assign(Object.assign({}, useTheme.props), {
    color: String,
    textColor: String,
    text: Boolean,
    block: Boolean,
    loading: Boolean,
    disabled: Boolean,
    circle: Boolean,
    size: String,
    ghost: Boolean,
    round: Boolean,
    secondary: Boolean,
    tertiary: Boolean,
    quaternary: Boolean,
    strong: Boolean,
    focusable: {
      type: Boolean,
      default: true
    },
    keyboard: {
      type: Boolean,
      default: true
    },
    tag: {
      type: String,
      default: "button"
    },
    type: {
      type: String,
      default: "default"
    },
    dashed: Boolean,
    renderIcon: Function,
    iconPlacement: {
      type: String,
      default: "left"
    },
    attrType: {
      type: String,
      default: "button"
    },
    bordered: {
      type: Boolean,
      default: true
    },
    onClick: [Function, Array],
    nativeFocusBehavior: {
      type: Boolean,
      default: !isSafari
    },
    spinProps: Object
  });
  const Button = /* @__PURE__ */ defineComponent({
    name: "Button",
    props: buttonProps,
    slots: Object,
    setup(props) {
      const selfElRef = /* @__PURE__ */ ref(null);
      const waveElRef = /* @__PURE__ */ ref(null);
      const enterPressedRef = /* @__PURE__ */ ref(false);
      const showBorderRef = useMemo(() => {
        return !props.quaternary && !props.tertiary && !props.secondary && !props.text && (!props.color || props.ghost || props.dashed) && props.bordered;
      });
      const NButtonGroup = inject(buttonGroupInjectionKey, {});
      const {
        inlineThemeDisabled,
        mergedClsPrefixRef,
        mergedRtlRef,
        mergedComponentPropsRef
      } = useConfig(props);
      const {
        mergedSizeRef
      } = useFormItem({}, {
        defaultSize: "medium",
        mergedSize: (NFormItem) => {
          var _a2, _b;
          const {
            size: size2
          } = props;
          if (size2) return size2;
          const {
            size: buttonGroupSize
          } = NButtonGroup;
          if (buttonGroupSize) return buttonGroupSize;
          const {
            mergedSize: formItemSize
          } = NFormItem || {};
          if (formItemSize) return formItemSize.value;
          const configSize = (_b = (_a2 = mergedComponentPropsRef === null || mergedComponentPropsRef === void 0 ? void 0 : mergedComponentPropsRef.value) === null || _a2 === void 0 ? void 0 : _a2.Button) === null || _b === void 0 ? void 0 : _b.size;
          if (configSize) return configSize;
          return "medium";
        }
      });
      const mergedFocusableRef = computed(() => {
        return props.focusable && !props.disabled;
      });
      const handleMousedown = (e) => {
        var _a2;
        if (!mergedFocusableRef.value) {
          e.preventDefault();
        }
        if (props.nativeFocusBehavior) {
          return;
        }
        e.preventDefault();
        if (props.disabled) {
          return;
        }
        if (mergedFocusableRef.value) {
          (_a2 = selfElRef.value) === null || _a2 === void 0 ? void 0 : _a2.focus({
            preventScroll: true
          });
        }
      };
      const handleClick = (e) => {
        var _a2;
        if (!props.disabled && !props.loading) {
          const {
            onClick
          } = props;
          if (onClick) call(onClick, e);
          if (!props.text) {
            (_a2 = waveElRef.value) === null || _a2 === void 0 ? void 0 : _a2.play();
          }
        }
      };
      const handleKeyup = (e) => {
        switch (e.key) {
          case "Enter":
            if (!props.keyboard) {
              return;
            }
            enterPressedRef.value = false;
        }
      };
      const handleKeydown = (e) => {
        switch (e.key) {
          case "Enter":
            if (!props.keyboard || props.loading) {
              e.preventDefault();
              return;
            }
            enterPressedRef.value = true;
        }
      };
      const handleBlur = () => {
        enterPressedRef.value = false;
      };
      const themeRef = useTheme("Button", "-button", style$c, buttonLight, props, mergedClsPrefixRef);
      const rtlEnabledRef = useRtl("Button", mergedRtlRef, mergedClsPrefixRef);
      const cssVarsRef = computed(() => {
        const theme = themeRef.value;
        const {
          common: {
            cubicBezierEaseInOut: cubicBezierEaseInOut2,
            cubicBezierEaseOut: cubicBezierEaseOut2
          },
          self: self2
        } = theme;
        const {
          rippleDuration,
          opacityDisabled,
          fontWeight,
          fontWeightStrong
        } = self2;
        const size2 = mergedSizeRef.value;
        const {
          dashed,
          type,
          ghost,
          text,
          color,
          round,
          circle,
          textColor,
          secondary,
          tertiary,
          quaternary,
          strong
        } = props;
        const fontProps = {
          "--n-font-weight": strong ? fontWeightStrong : fontWeight
        };
        let colorProps = {
          "--n-color": "initial",
          "--n-color-hover": "initial",
          "--n-color-pressed": "initial",
          "--n-color-focus": "initial",
          "--n-color-disabled": "initial",
          "--n-ripple-color": "initial",
          "--n-text-color": "initial",
          "--n-text-color-hover": "initial",
          "--n-text-color-pressed": "initial",
          "--n-text-color-focus": "initial",
          "--n-text-color-disabled": "initial"
        };
        const typeIsTertiary = type === "tertiary";
        const typeIsDefault = type === "default";
        const mergedType = typeIsTertiary ? "default" : type;
        if (text) {
          const propTextColor = textColor || color;
          const mergedTextColor = propTextColor || self2[createKey("textColorText", mergedType)];
          colorProps = {
            "--n-color": "#0000",
            "--n-color-hover": "#0000",
            "--n-color-pressed": "#0000",
            "--n-color-focus": "#0000",
            "--n-color-disabled": "#0000",
            "--n-ripple-color": "#0000",
            "--n-text-color": mergedTextColor,
            "--n-text-color-hover": propTextColor ? createHoverColor(propTextColor) : self2[createKey("textColorTextHover", mergedType)],
            "--n-text-color-pressed": propTextColor ? createPressedColor(propTextColor) : self2[createKey("textColorTextPressed", mergedType)],
            "--n-text-color-focus": propTextColor ? createHoverColor(propTextColor) : self2[createKey("textColorTextHover", mergedType)],
            "--n-text-color-disabled": propTextColor || self2[createKey("textColorTextDisabled", mergedType)]
          };
        } else if (ghost || dashed) {
          const mergedTextColor = textColor || color;
          colorProps = {
            "--n-color": "#0000",
            "--n-color-hover": "#0000",
            "--n-color-pressed": "#0000",
            "--n-color-focus": "#0000",
            "--n-color-disabled": "#0000",
            "--n-ripple-color": color || self2[createKey("rippleColor", mergedType)],
            "--n-text-color": mergedTextColor || self2[createKey("textColorGhost", mergedType)],
            "--n-text-color-hover": mergedTextColor ? createHoverColor(mergedTextColor) : self2[createKey("textColorGhostHover", mergedType)],
            "--n-text-color-pressed": mergedTextColor ? createPressedColor(mergedTextColor) : self2[createKey("textColorGhostPressed", mergedType)],
            "--n-text-color-focus": mergedTextColor ? createHoverColor(mergedTextColor) : self2[createKey("textColorGhostHover", mergedType)],
            "--n-text-color-disabled": mergedTextColor || self2[createKey("textColorGhostDisabled", mergedType)]
          };
        } else if (secondary) {
          const typeTextColor = typeIsDefault ? self2.textColor : typeIsTertiary ? self2.textColorTertiary : self2[createKey("color", mergedType)];
          const mergedTextColor = color || typeTextColor;
          const isColoredType = type !== "default" && type !== "tertiary";
          colorProps = {
            "--n-color": isColoredType ? changeColor(mergedTextColor, {
              alpha: Number(self2.colorOpacitySecondary)
            }) : self2.colorSecondary,
            "--n-color-hover": isColoredType ? changeColor(mergedTextColor, {
              alpha: Number(self2.colorOpacitySecondaryHover)
            }) : self2.colorSecondaryHover,
            "--n-color-pressed": isColoredType ? changeColor(mergedTextColor, {
              alpha: Number(self2.colorOpacitySecondaryPressed)
            }) : self2.colorSecondaryPressed,
            "--n-color-focus": isColoredType ? changeColor(mergedTextColor, {
              alpha: Number(self2.colorOpacitySecondaryHover)
            }) : self2.colorSecondaryHover,
            "--n-color-disabled": self2.colorSecondary,
            "--n-ripple-color": "#0000",
            "--n-text-color": mergedTextColor,
            "--n-text-color-hover": mergedTextColor,
            "--n-text-color-pressed": mergedTextColor,
            "--n-text-color-focus": mergedTextColor,
            "--n-text-color-disabled": mergedTextColor
          };
        } else if (tertiary || quaternary) {
          const typeColor = typeIsDefault ? self2.textColor : typeIsTertiary ? self2.textColorTertiary : self2[createKey("color", mergedType)];
          const mergedColor = color || typeColor;
          if (tertiary) {
            colorProps["--n-color"] = self2.colorTertiary;
            colorProps["--n-color-hover"] = self2.colorTertiaryHover;
            colorProps["--n-color-pressed"] = self2.colorTertiaryPressed;
            colorProps["--n-color-focus"] = self2.colorSecondaryHover;
            colorProps["--n-color-disabled"] = self2.colorTertiary;
          } else {
            colorProps["--n-color"] = self2.colorQuaternary;
            colorProps["--n-color-hover"] = self2.colorQuaternaryHover;
            colorProps["--n-color-pressed"] = self2.colorQuaternaryPressed;
            colorProps["--n-color-focus"] = self2.colorQuaternaryHover;
            colorProps["--n-color-disabled"] = self2.colorQuaternary;
          }
          colorProps["--n-ripple-color"] = "#0000";
          colorProps["--n-text-color"] = mergedColor;
          colorProps["--n-text-color-hover"] = mergedColor;
          colorProps["--n-text-color-pressed"] = mergedColor;
          colorProps["--n-text-color-focus"] = mergedColor;
          colorProps["--n-text-color-disabled"] = mergedColor;
        } else {
          colorProps = {
            "--n-color": color || self2[createKey("color", mergedType)],
            "--n-color-hover": color ? createHoverColor(color) : self2[createKey("colorHover", mergedType)],
            "--n-color-pressed": color ? createPressedColor(color) : self2[createKey("colorPressed", mergedType)],
            "--n-color-focus": color ? createHoverColor(color) : self2[createKey("colorFocus", mergedType)],
            "--n-color-disabled": color || self2[createKey("colorDisabled", mergedType)],
            "--n-ripple-color": color || self2[createKey("rippleColor", mergedType)],
            "--n-text-color": textColor || (color ? self2.textColorPrimary : typeIsTertiary ? self2.textColorTertiary : self2[createKey("textColor", mergedType)]),
            "--n-text-color-hover": textColor || (color ? self2.textColorHoverPrimary : self2[createKey("textColorHover", mergedType)]),
            "--n-text-color-pressed": textColor || (color ? self2.textColorPressedPrimary : self2[createKey("textColorPressed", mergedType)]),
            "--n-text-color-focus": textColor || (color ? self2.textColorFocusPrimary : self2[createKey("textColorFocus", mergedType)]),
            "--n-text-color-disabled": textColor || (color ? self2.textColorDisabledPrimary : self2[createKey("textColorDisabled", mergedType)])
          };
        }
        let borderProps = {
          "--n-border": "initial",
          "--n-border-hover": "initial",
          "--n-border-pressed": "initial",
          "--n-border-focus": "initial",
          "--n-border-disabled": "initial"
        };
        if (text) {
          borderProps = {
            "--n-border": "none",
            "--n-border-hover": "none",
            "--n-border-pressed": "none",
            "--n-border-focus": "none",
            "--n-border-disabled": "none"
          };
        } else {
          borderProps = {
            "--n-border": self2[createKey("border", mergedType)],
            "--n-border-hover": self2[createKey("borderHover", mergedType)],
            "--n-border-pressed": self2[createKey("borderPressed", mergedType)],
            "--n-border-focus": self2[createKey("borderFocus", mergedType)],
            "--n-border-disabled": self2[createKey("borderDisabled", mergedType)]
          };
        }
        const {
          [createKey("height", size2)]: height,
          [createKey("fontSize", size2)]: fontSize2,
          [createKey("padding", size2)]: padding,
          [createKey("paddingRound", size2)]: paddingRound,
          [createKey("iconSize", size2)]: iconSize,
          [createKey("borderRadius", size2)]: borderRadius,
          [createKey("iconMargin", size2)]: iconMargin,
          waveOpacity
        } = self2;
        const sizeProps = {
          "--n-width": circle && !text ? height : "initial",
          "--n-height": text ? "initial" : height,
          "--n-font-size": fontSize2,
          "--n-padding": circle ? "initial" : text ? "initial" : round ? paddingRound : padding,
          "--n-icon-size": iconSize,
          "--n-icon-margin": iconMargin,
          "--n-border-radius": text ? "initial" : circle || round ? height : borderRadius
        };
        return Object.assign(Object.assign(Object.assign(Object.assign({
          "--n-bezier": cubicBezierEaseInOut2,
          "--n-bezier-ease-out": cubicBezierEaseOut2,
          "--n-ripple-duration": rippleDuration,
          "--n-opacity-disabled": opacityDisabled,
          "--n-wave-opacity": waveOpacity
        }, fontProps), colorProps), borderProps), sizeProps);
      });
      const themeClassHandle = inlineThemeDisabled ? useThemeClass("button", computed(() => {
        let hash = "";
        const {
          dashed,
          type,
          ghost,
          text,
          color,
          round,
          circle,
          textColor,
          secondary,
          tertiary,
          quaternary,
          strong
        } = props;
        if (dashed) hash += "a";
        if (ghost) hash += "b";
        if (text) hash += "c";
        if (round) hash += "d";
        if (circle) hash += "e";
        if (secondary) hash += "f";
        if (tertiary) hash += "g";
        if (quaternary) hash += "h";
        if (strong) hash += "i";
        if (color) hash += `j${color2Class(color)}`;
        if (textColor) hash += `k${color2Class(textColor)}`;
        const {
          value: size2
        } = mergedSizeRef;
        hash += `l${size2[0]}`;
        hash += `m${type[0]}`;
        return hash;
      }), cssVarsRef, props) : void 0;
      return {
        selfElRef,
        waveElRef,
        mergedClsPrefix: mergedClsPrefixRef,
        mergedFocusable: mergedFocusableRef,
        mergedSize: mergedSizeRef,
        showBorder: showBorderRef,
        enterPressed: enterPressedRef,
        rtlEnabled: rtlEnabledRef,
        handleMousedown,
        handleKeydown,
        handleBlur,
        handleKeyup,
        handleClick,
        customColorCssVars: computed(() => {
          const {
            color
          } = props;
          if (!color) return null;
          const hoverColor = createHoverColor(color);
          return {
            "--n-border-color": color,
            "--n-border-color-hover": hoverColor,
            "--n-border-color-pressed": createPressedColor(color),
            "--n-border-color-focus": hoverColor,
            "--n-border-color-disabled": color
          };
        }),
        cssVars: inlineThemeDisabled ? void 0 : cssVarsRef,
        themeClass: themeClassHandle === null || themeClassHandle === void 0 ? void 0 : themeClassHandle.themeClass,
        onRender: themeClassHandle === null || themeClassHandle === void 0 ? void 0 : themeClassHandle.onRender
      };
    },
    render() {
      const {
        mergedClsPrefix,
        tag: Component,
        onRender
      } = this;
      onRender === null || onRender === void 0 ? void 0 : onRender();
      const children = resolveWrappedSlot(this.$slots.default, (children2) => children2 && h("span", {
        class: `${mergedClsPrefix}-button__content`
      }, children2));
      return h(Component, {
        ref: "selfElRef",
        class: [
          this.themeClass,
          `${mergedClsPrefix}-button`,
          `${mergedClsPrefix}-button--${this.type}-type`,
          `${mergedClsPrefix}-button--${this.mergedSize}-type`,
          this.rtlEnabled && `${mergedClsPrefix}-button--rtl`,
          this.disabled && `${mergedClsPrefix}-button--disabled`,
          this.block && `${mergedClsPrefix}-button--block`,
          this.enterPressed && `${mergedClsPrefix}-button--pressed`,
          !this.text && this.dashed && `${mergedClsPrefix}-button--dashed`,
          this.color && `${mergedClsPrefix}-button--color`,
          this.secondary && `${mergedClsPrefix}-button--secondary`,
          this.loading && `${mergedClsPrefix}-button--loading`,
          this.ghost && `${mergedClsPrefix}-button--ghost`
          // required for button group border collapse
        ],
        tabindex: this.mergedFocusable ? 0 : -1,
        type: this.attrType,
        style: this.cssVars,
        disabled: this.disabled,
        onClick: this.handleClick,
        onBlur: this.handleBlur,
        onMousedown: this.handleMousedown,
        onKeyup: this.handleKeyup,
        onKeydown: this.handleKeydown
      }, this.iconPlacement === "right" && children, h(NFadeInExpandTransition, {
        width: true
      }, {
        default: () => resolveWrappedSlot(this.$slots.icon, (children2) => (this.loading || this.renderIcon || children2) && h("span", {
          class: `${mergedClsPrefix}-button__icon`,
          style: {
            margin: isSlotEmpty(this.$slots.default) ? "0" : ""
          }
        }, h(NIconSwitchTransition, null, {
          default: () => this.loading ? h(NBaseLoading, Object.assign({
            clsPrefix: mergedClsPrefix,
            key: "loading",
            class: `${mergedClsPrefix}-icon-slot`,
            strokeWidth: 20
          }, this.spinProps)) : h("div", {
            key: "icon",
            class: `${mergedClsPrefix}-icon-slot`,
            role: "none"
          }, this.renderIcon ? this.renderIcon() : children2)
        })))
      }), this.iconPlacement === "left" && children, !this.text ? h(NBaseWave, {
        ref: "waveElRef",
        clsPrefix: mergedClsPrefix
      }) : null, this.showBorder ? h("div", {
        "aria-hidden": true,
        class: `${mergedClsPrefix}-button__border`,
        style: this.customColorCssVars
      }) : null, this.showBorder ? h("div", {
        "aria-hidden": true,
        class: `${mergedClsPrefix}-button__state-border`,
        style: this.customColorCssVars
      }) : null);
    }
  });
  const commonVariables$3 = {
    paddingSmall: "12px 16px 12px",
    paddingMedium: "19px 24px 20px",
    paddingLarge: "23px 32px 24px",
    paddingHuge: "27px 40px 28px",
    titleFontSizeSmall: "16px",
    titleFontSizeMedium: "18px",
    titleFontSizeLarge: "18px",
    titleFontSizeHuge: "18px",
    closeIconSize: "18px",
    closeSize: "22px"
  };
  function self$b(vars) {
    const {
      primaryColor,
      borderRadius,
      lineHeight: lineHeight2,
      fontSize: fontSize2,
      cardColor,
      textColor2,
      textColor1,
      dividerColor,
      fontWeightStrong,
      closeIconColor,
      closeIconColorHover,
      closeIconColorPressed,
      closeColorHover,
      closeColorPressed,
      modalColor,
      boxShadow1,
      popoverColor,
      actionColor
    } = vars;
    return Object.assign(Object.assign({}, commonVariables$3), {
      lineHeight: lineHeight2,
      color: cardColor,
      colorModal: modalColor,
      colorPopover: popoverColor,
      colorTarget: primaryColor,
      colorEmbedded: actionColor,
      colorEmbeddedModal: actionColor,
      colorEmbeddedPopover: actionColor,
      textColor: textColor2,
      titleTextColor: textColor1,
      borderColor: dividerColor,
      actionColor,
      titleFontWeight: fontWeightStrong,
      closeColorHover,
      closeColorPressed,
      closeBorderRadius: borderRadius,
      closeIconColor,
      closeIconColorHover,
      closeIconColorPressed,
      fontSizeSmall: fontSize2,
      fontSizeMedium: fontSize2,
      fontSizeLarge: fontSize2,
      fontSizeHuge: fontSize2,
      boxShadow: boxShadow1,
      borderRadius
    });
  }
  const cardLight = {
    common: derived,
    self: self$b
  };
  const contentBaseStyle = cB("card-content", `
 flex: 1;
 min-width: 0;
 box-sizing: border-box;
 padding: 0 var(--n-padding-left) var(--n-padding-bottom) var(--n-padding-left);
 font-size: var(--n-font-size);
`);
  const style$b = c$1([cB("card", `
 font-size: var(--n-font-size);
 line-height: var(--n-line-height);
 display: flex;
 flex-direction: column;
 width: 100%;
 box-sizing: border-box;
 position: relative;
 border-radius: var(--n-border-radius);
 background-color: var(--n-color);
 color: var(--n-text-color);
 word-break: break-word;
 transition: 
 color .3s var(--n-bezier),
 background-color .3s var(--n-bezier),
 box-shadow .3s var(--n-bezier),
 border-color .3s var(--n-bezier);
 `, [asModal({
    background: "var(--n-color-modal)"
  }), cM("hoverable", [c$1("&:hover", "box-shadow: var(--n-box-shadow);")]), cM("content-segmented", [c$1(">", [cB("card-content", `
 padding-top: var(--n-padding-bottom);
 `), cE("content-scrollbar", [c$1(">", [cB("scrollbar-container", [c$1(">", [cB("card-content", `
 padding-top: var(--n-padding-bottom);
 `)])])])])])]), cM("content-soft-segmented", [c$1(">", [cB("card-content", `
 margin: 0 var(--n-padding-left);
 padding: var(--n-padding-bottom) 0;
 `), cE("content-scrollbar", [c$1(">", [cB("scrollbar-container", [c$1(">", [cB("card-content", `
 margin: 0 var(--n-padding-left);
 padding: var(--n-padding-bottom) 0;
 `)])])])])])]), cM("footer-segmented", [c$1(">", [cE("footer", `
 padding-top: var(--n-padding-bottom);
 `)])]), cM("footer-soft-segmented", [c$1(">", [cE("footer", `
 padding: var(--n-padding-bottom) 0;
 margin: 0 var(--n-padding-left);
 `)])]), c$1(">", [cB("card-header", `
 box-sizing: border-box;
 display: flex;
 align-items: center;
 font-size: var(--n-title-font-size);
 padding:
 var(--n-padding-top)
 var(--n-padding-left)
 var(--n-padding-bottom)
 var(--n-padding-left);
 `, [cE("main", `
 font-weight: var(--n-title-font-weight);
 transition: color .3s var(--n-bezier);
 flex: 1;
 min-width: 0;
 color: var(--n-title-text-color);
 `), cE("extra", `
 display: flex;
 align-items: center;
 font-size: var(--n-font-size);
 font-weight: 400;
 transition: color .3s var(--n-bezier);
 color: var(--n-text-color);
 `), cE("close", `
 margin: 0 0 0 8px;
 transition:
 background-color .3s var(--n-bezier),
 color .3s var(--n-bezier);
 `)]), cE("action", `
 box-sizing: border-box;
 transition:
 background-color .3s var(--n-bezier),
 border-color .3s var(--n-bezier);
 background-clip: padding-box;
 background-color: var(--n-action-color);
 `), contentBaseStyle, cB("card-content", [c$1("&:first-child", `
 padding-top: var(--n-padding-bottom);
 `)]), cE("content-scrollbar", `
 display: flex;
 flex-direction: column;
 `, [c$1(">", [cB("scrollbar-container", [c$1(">", [contentBaseStyle])])]), c$1("&:first-child >", [cB("scrollbar-container", [c$1(">", [cB("card-content", `
 padding-top: var(--n-padding-bottom);
 `)])])])]), cE("footer", `
 box-sizing: border-box;
 padding: 0 var(--n-padding-left) var(--n-padding-bottom) var(--n-padding-left);
 font-size: var(--n-font-size);
 `, [c$1("&:first-child", `
 padding-top: var(--n-padding-bottom);
 `)]), cE("action", `
 background-color: var(--n-action-color);
 padding: var(--n-padding-bottom) var(--n-padding-left);
 border-bottom-left-radius: var(--n-border-radius);
 border-bottom-right-radius: var(--n-border-radius);
 `)]), cB("card-cover", `
 overflow: hidden;
 width: 100%;
 border-radius: var(--n-border-radius) var(--n-border-radius) 0 0;
 `, [c$1("img", `
 display: block;
 width: 100%;
 `)]), cM("bordered", `
 border: 1px solid var(--n-border-color);
 `, [c$1("&:target", "border-color: var(--n-color-target);")]), cM("action-segmented", [c$1(">", [cE("action", [c$1("&:not(:first-child)", `
 border-top: 1px solid var(--n-border-color);
 `)])])]), cM("content-segmented, content-soft-segmented", [c$1(">", [cB("card-content", `
 transition: border-color 0.3s var(--n-bezier);
 `, [c$1("&:not(:first-child)", `
 border-top: 1px solid var(--n-border-color);
 `)]), cE("content-scrollbar", `
 transition: border-color 0.3s var(--n-bezier);
 `, [c$1("&:not(:first-child)", `
 border-top: 1px solid var(--n-border-color);
 `)])])]), cM("footer-segmented, footer-soft-segmented", [c$1(">", [cE("footer", `
 transition: border-color 0.3s var(--n-bezier);
 `, [c$1("&:not(:first-child)", `
 border-top: 1px solid var(--n-border-color);
 `)])])]), cM("embedded", `
 background-color: var(--n-color-embedded);
 `)]), insideModal(cB("card", `
 background: var(--n-color-modal);
 `, [cM("embedded", `
 background-color: var(--n-color-embedded-modal);
 `)])), insidePopover(cB("card", `
 background: var(--n-color-popover);
 `, [cM("embedded", `
 background-color: var(--n-color-embedded-popover);
 `)]))]);
  const cardBaseProps = {
    title: [String, Function],
    contentClass: String,
    contentStyle: [Object, String],
    contentScrollable: Boolean,
    headerClass: String,
    headerStyle: [Object, String],
    headerExtraClass: String,
    headerExtraStyle: [Object, String],
    footerClass: String,
    footerStyle: [Object, String],
    embedded: Boolean,
    segmented: {
      type: [Boolean, Object],
      default: false
    },
    size: String,
    bordered: {
      type: Boolean,
      default: true
    },
    closable: Boolean,
    hoverable: Boolean,
    role: String,
    onClose: [Function, Array],
    tag: {
      type: String,
      default: "div"
    },
    cover: Function,
    content: [String, Function],
    footer: Function,
    action: Function,
    headerExtra: Function,
    closeFocusable: Boolean
  };
  const cardProps = Object.assign(Object.assign({}, useTheme.props), cardBaseProps);
  const NCard = /* @__PURE__ */ defineComponent({
    name: "Card",
    props: cardProps,
    slots: Object,
    setup(props) {
      const handleCloseClick = () => {
        const {
          onClose
        } = props;
        if (onClose) {
          call(onClose);
        }
      };
      const {
        inlineThemeDisabled,
        mergedClsPrefixRef,
        mergedRtlRef,
        mergedComponentPropsRef
      } = useConfig(props);
      const themeRef = useTheme("Card", "-card", style$b, cardLight, props, mergedClsPrefixRef);
      const rtlEnabledRef = useRtl("Card", mergedRtlRef, mergedClsPrefixRef);
      const mergedSizeRef = computed(() => {
        var _a2, _b;
        return props.size || ((_b = (_a2 = mergedComponentPropsRef === null || mergedComponentPropsRef === void 0 ? void 0 : mergedComponentPropsRef.value) === null || _a2 === void 0 ? void 0 : _a2.Card) === null || _b === void 0 ? void 0 : _b.size) || "medium";
      });
      const cssVarsRef = computed(() => {
        const mergedSize = mergedSizeRef.value;
        const {
          self: {
            color,
            colorModal,
            colorTarget,
            textColor,
            titleTextColor,
            titleFontWeight,
            borderColor,
            actionColor,
            borderRadius,
            lineHeight: lineHeight2,
            closeIconColor,
            closeIconColorHover,
            closeIconColorPressed,
            closeColorHover,
            closeColorPressed,
            closeBorderRadius,
            closeIconSize,
            closeSize,
            boxShadow,
            colorPopover,
            colorEmbedded,
            colorEmbeddedModal,
            colorEmbeddedPopover,
            [createKey("padding", mergedSize)]: padding,
            [createKey("fontSize", mergedSize)]: fontSize2,
            [createKey("titleFontSize", mergedSize)]: titleFontSize
          },
          common: {
            cubicBezierEaseInOut: cubicBezierEaseInOut2
          }
        } = themeRef.value;
        const {
          top: paddingTop,
          left: paddingLeft,
          bottom: paddingBottom
        } = getMargin(padding);
        return {
          "--n-bezier": cubicBezierEaseInOut2,
          "--n-border-radius": borderRadius,
          "--n-color": color,
          "--n-color-modal": colorModal,
          "--n-color-popover": colorPopover,
          "--n-color-embedded": colorEmbedded,
          "--n-color-embedded-modal": colorEmbeddedModal,
          "--n-color-embedded-popover": colorEmbeddedPopover,
          "--n-color-target": colorTarget,
          "--n-text-color": textColor,
          "--n-line-height": lineHeight2,
          "--n-action-color": actionColor,
          "--n-title-text-color": titleTextColor,
          "--n-title-font-weight": titleFontWeight,
          "--n-close-icon-color": closeIconColor,
          "--n-close-icon-color-hover": closeIconColorHover,
          "--n-close-icon-color-pressed": closeIconColorPressed,
          "--n-close-color-hover": closeColorHover,
          "--n-close-color-pressed": closeColorPressed,
          "--n-border-color": borderColor,
          "--n-box-shadow": boxShadow,
          // size
          "--n-padding-top": paddingTop,
          "--n-padding-bottom": paddingBottom,
          "--n-padding-left": paddingLeft,
          "--n-font-size": fontSize2,
          "--n-title-font-size": titleFontSize,
          "--n-close-size": closeSize,
          "--n-close-icon-size": closeIconSize,
          "--n-close-border-radius": closeBorderRadius
        };
      });
      const themeClassHandle = inlineThemeDisabled ? useThemeClass("card", computed(() => {
        return mergedSizeRef.value[0];
      }), cssVarsRef, props) : void 0;
      return {
        rtlEnabled: rtlEnabledRef,
        mergedClsPrefix: mergedClsPrefixRef,
        mergedTheme: themeRef,
        handleCloseClick,
        cssVars: inlineThemeDisabled ? void 0 : cssVarsRef,
        themeClass: themeClassHandle === null || themeClassHandle === void 0 ? void 0 : themeClassHandle.themeClass,
        onRender: themeClassHandle === null || themeClassHandle === void 0 ? void 0 : themeClassHandle.onRender
      };
    },
    render() {
      const {
        segmented,
        bordered,
        hoverable,
        mergedClsPrefix,
        rtlEnabled,
        onRender,
        embedded,
        tag: Component,
        $slots
      } = this;
      onRender === null || onRender === void 0 ? void 0 : onRender();
      return h(Component, {
        class: [`${mergedClsPrefix}-card`, this.themeClass, embedded && `${mergedClsPrefix}-card--embedded`, {
          [`${mergedClsPrefix}-card--rtl`]: rtlEnabled,
          [`${mergedClsPrefix}-card--content-scrollable`]: this.contentScrollable,
          [`${mergedClsPrefix}-card--content${typeof segmented !== "boolean" && segmented.content === "soft" ? "-soft" : ""}-segmented`]: segmented === true || segmented !== false && segmented.content,
          [`${mergedClsPrefix}-card--footer${typeof segmented !== "boolean" && segmented.footer === "soft" ? "-soft" : ""}-segmented`]: segmented === true || segmented !== false && segmented.footer,
          [`${mergedClsPrefix}-card--action-segmented`]: segmented === true || segmented !== false && segmented.action,
          [`${mergedClsPrefix}-card--bordered`]: bordered,
          [`${mergedClsPrefix}-card--hoverable`]: hoverable
        }],
        style: this.cssVars,
        role: this.role
      }, resolveWrappedSlot($slots.cover, (children) => {
        const mergedChildren = this.cover ? ensureValidVNode([this.cover()]) : children;
        return mergedChildren && h("div", {
          class: `${mergedClsPrefix}-card-cover`,
          role: "none"
        }, mergedChildren);
      }), resolveWrappedSlot($slots.header, (children) => {
        const {
          title
        } = this;
        const mergedChildren = title ? ensureValidVNode(typeof title === "function" ? [title()] : [title]) : children;
        return mergedChildren || this.closable ? h("div", {
          class: [`${mergedClsPrefix}-card-header`, this.headerClass],
          style: this.headerStyle,
          role: "heading"
        }, h("div", {
          class: `${mergedClsPrefix}-card-header__main`,
          role: "heading"
        }, mergedChildren), resolveWrappedSlot($slots["header-extra"], (children2) => {
          const mergedChildren2 = this.headerExtra ? ensureValidVNode([this.headerExtra()]) : children2;
          return mergedChildren2 && h("div", {
            class: [`${mergedClsPrefix}-card-header__extra`, this.headerExtraClass],
            style: this.headerExtraStyle
          }, mergedChildren2);
        }), this.closable && h(NBaseClose, {
          clsPrefix: mergedClsPrefix,
          class: `${mergedClsPrefix}-card-header__close`,
          onClick: this.handleCloseClick,
          focusable: this.closeFocusable,
          absolute: true
        })) : null;
      }), resolveWrappedSlot($slots.default, (children) => {
        const {
          content
        } = this;
        const mergedChildren = content ? ensureValidVNode(typeof content === "function" ? [content()] : [content]) : children;
        return mergedChildren ? this.contentScrollable ? h(Scrollbar, {
          class: `${mergedClsPrefix}-card__content-scrollbar`,
          contentClass: [`${mergedClsPrefix}-card-content`, this.contentClass],
          contentStyle: this.contentStyle
        }, mergedChildren) : h("div", {
          class: [`${mergedClsPrefix}-card-content`, this.contentClass],
          style: this.contentStyle,
          role: "none"
        }, mergedChildren) : null;
      }), resolveWrappedSlot($slots.footer, (children) => {
        const mergedChildren = this.footer ? ensureValidVNode([this.footer()]) : children;
        return mergedChildren && h("div", {
          class: [`${mergedClsPrefix}-card__footer`, this.footerClass],
          style: this.footerStyle,
          role: "none"
        }, mergedChildren);
      }), resolveWrappedSlot($slots.action, (children) => {
        const mergedChildren = this.action ? ensureValidVNode([this.action()]) : children;
        return mergedChildren && h("div", {
          class: `${mergedClsPrefix}-card__action`,
          role: "none"
        }, mergedChildren);
      }));
    }
  });
  const configProviderProps = {
    abstract: Boolean,
    bordered: {
      type: Boolean,
      default: void 0
    },
    clsPrefix: String,
    locale: Object,
    dateLocale: Object,
    namespace: String,
    rtl: Array,
    tag: {
      type: String,
      default: "div"
    },
    hljs: Object,
    katex: Object,
    theme: Object,
    themeOverrides: Object,
    componentOptions: Object,
    icons: Object,
    breakpoints: Object,
    preflightStyleDisabled: Boolean,
    styleMountTarget: Object,
    inlineThemeDisabled: {
      type: Boolean,
      default: void 0
    },
    // deprecated
    as: {
      type: String,
      validator: () => {
        warn("config-provider", "`as` is deprecated, please use `tag` instead.");
        return true;
      },
      default: void 0
    }
  };
  const NConfigProvider = /* @__PURE__ */ defineComponent({
    name: "ConfigProvider",
    alias: ["App"],
    props: configProviderProps,
    setup(props) {
      const NConfigProvider2 = inject(configProviderInjectionKey, null);
      const mergedThemeRef = computed(() => {
        const {
          theme
        } = props;
        if (theme === null) return void 0;
        const inheritedTheme = NConfigProvider2 === null || NConfigProvider2 === void 0 ? void 0 : NConfigProvider2.mergedThemeRef.value;
        return theme === void 0 ? inheritedTheme : inheritedTheme === void 0 ? theme : Object.assign({}, inheritedTheme, theme);
      });
      const mergedThemeOverridesRef = computed(() => {
        const {
          themeOverrides
        } = props;
        if (themeOverrides === null) return void 0;
        if (themeOverrides === void 0) {
          return NConfigProvider2 === null || NConfigProvider2 === void 0 ? void 0 : NConfigProvider2.mergedThemeOverridesRef.value;
        } else {
          const inheritedThemeOverrides = NConfigProvider2 === null || NConfigProvider2 === void 0 ? void 0 : NConfigProvider2.mergedThemeOverridesRef.value;
          if (inheritedThemeOverrides === void 0) {
            return themeOverrides;
          } else {
            return merge$1({}, inheritedThemeOverrides, themeOverrides);
          }
        }
      });
      const mergedNamespaceRef = useMemo(() => {
        const {
          namespace: namespace2
        } = props;
        return namespace2 === void 0 ? NConfigProvider2 === null || NConfigProvider2 === void 0 ? void 0 : NConfigProvider2.mergedNamespaceRef.value : namespace2;
      });
      const mergedBorderedRef = useMemo(() => {
        const {
          bordered
        } = props;
        return bordered === void 0 ? NConfigProvider2 === null || NConfigProvider2 === void 0 ? void 0 : NConfigProvider2.mergedBorderedRef.value : bordered;
      });
      const mergedIconsRef = computed(() => {
        const {
          icons
        } = props;
        return icons === void 0 ? NConfigProvider2 === null || NConfigProvider2 === void 0 ? void 0 : NConfigProvider2.mergedIconsRef.value : icons;
      });
      const mergedComponentPropsRef = computed(() => {
        const {
          componentOptions
        } = props;
        if (componentOptions !== void 0) return componentOptions;
        return NConfigProvider2 === null || NConfigProvider2 === void 0 ? void 0 : NConfigProvider2.mergedComponentPropsRef.value;
      });
      const mergedClsPrefixRef = computed(() => {
        const {
          clsPrefix
        } = props;
        if (clsPrefix !== void 0) return clsPrefix;
        if (NConfigProvider2) return NConfigProvider2.mergedClsPrefixRef.value;
        return defaultClsPrefix;
      });
      const mergedRtlRef = computed(() => {
        var _a2;
        const {
          rtl
        } = props;
        if (rtl === void 0) {
          return NConfigProvider2 === null || NConfigProvider2 === void 0 ? void 0 : NConfigProvider2.mergedRtlRef.value;
        }
        const rtlEnabledState = {};
        for (const rtlInfo of rtl) {
          rtlEnabledState[rtlInfo.name] = markRaw(rtlInfo);
          (_a2 = rtlInfo.peers) === null || _a2 === void 0 ? void 0 : _a2.forEach((peerRtlInfo) => {
            if (!(peerRtlInfo.name in rtlEnabledState)) {
              rtlEnabledState[peerRtlInfo.name] = markRaw(peerRtlInfo);
            }
          });
        }
        return rtlEnabledState;
      });
      const mergedBreakpointsRef = computed(() => {
        return props.breakpoints || (NConfigProvider2 === null || NConfigProvider2 === void 0 ? void 0 : NConfigProvider2.mergedBreakpointsRef.value);
      });
      const inlineThemeDisabled = props.inlineThemeDisabled || (NConfigProvider2 === null || NConfigProvider2 === void 0 ? void 0 : NConfigProvider2.inlineThemeDisabled);
      const preflightStyleDisabled = props.preflightStyleDisabled || (NConfigProvider2 === null || NConfigProvider2 === void 0 ? void 0 : NConfigProvider2.preflightStyleDisabled);
      const styleMountTarget = props.styleMountTarget || (NConfigProvider2 === null || NConfigProvider2 === void 0 ? void 0 : NConfigProvider2.styleMountTarget);
      const mergedThemeHashRef = computed(() => {
        const {
          value: theme
        } = mergedThemeRef;
        const {
          value: mergedThemeOverrides
        } = mergedThemeOverridesRef;
        const hasThemeOverrides = mergedThemeOverrides && Object.keys(mergedThemeOverrides).length !== 0;
        const themeName = theme === null || theme === void 0 ? void 0 : theme.name;
        if (themeName) {
          if (hasThemeOverrides) {
            return `${themeName}-${murmur2(JSON.stringify(mergedThemeOverridesRef.value))}`;
          }
          return themeName;
        } else {
          if (hasThemeOverrides) {
            return murmur2(JSON.stringify(mergedThemeOverridesRef.value));
          }
          return "";
        }
      });
      provide(configProviderInjectionKey, {
        mergedThemeHashRef,
        mergedBreakpointsRef,
        mergedRtlRef,
        mergedIconsRef,
        mergedComponentPropsRef,
        mergedBorderedRef,
        mergedNamespaceRef,
        mergedClsPrefixRef,
        mergedLocaleRef: computed(() => {
          const {
            locale
          } = props;
          if (locale === null) return void 0;
          return locale === void 0 ? NConfigProvider2 === null || NConfigProvider2 === void 0 ? void 0 : NConfigProvider2.mergedLocaleRef.value : locale;
        }),
        mergedDateLocaleRef: computed(() => {
          const {
            dateLocale
          } = props;
          if (dateLocale === null) return void 0;
          return dateLocale === void 0 ? NConfigProvider2 === null || NConfigProvider2 === void 0 ? void 0 : NConfigProvider2.mergedDateLocaleRef.value : dateLocale;
        }),
        mergedHljsRef: computed(() => {
          const {
            hljs
          } = props;
          return hljs === void 0 ? NConfigProvider2 === null || NConfigProvider2 === void 0 ? void 0 : NConfigProvider2.mergedHljsRef.value : hljs;
        }),
        mergedKatexRef: computed(() => {
          const {
            katex
          } = props;
          return katex === void 0 ? NConfigProvider2 === null || NConfigProvider2 === void 0 ? void 0 : NConfigProvider2.mergedKatexRef.value : katex;
        }),
        mergedThemeRef,
        mergedThemeOverridesRef,
        inlineThemeDisabled: inlineThemeDisabled || false,
        preflightStyleDisabled: preflightStyleDisabled || false,
        styleMountTarget
      });
      return {
        mergedClsPrefix: mergedClsPrefixRef,
        mergedBordered: mergedBorderedRef,
        mergedNamespace: mergedNamespaceRef,
        mergedTheme: mergedThemeRef,
        mergedThemeOverrides: mergedThemeOverridesRef
      };
    },
    render() {
      var _a2, _b, _c, _d;
      return !this.abstract ? h(this.as || this.tag, {
        class: `${this.mergedClsPrefix || defaultClsPrefix}-config-provider`
      }, (_b = (_a2 = this.$slots).default) === null || _b === void 0 ? void 0 : _b.call(_a2)) : (_d = (_c = this.$slots).default) === null || _d === void 0 ? void 0 : _d.call(_c);
    }
  });
  const commonVariables$2 = {
    padding: "4px 0",
    optionIconSizeSmall: "14px",
    optionIconSizeMedium: "16px",
    optionIconSizeLarge: "16px",
    optionIconSizeHuge: "18px",
    optionSuffixWidthSmall: "14px",
    optionSuffixWidthMedium: "14px",
    optionSuffixWidthLarge: "16px",
    optionSuffixWidthHuge: "16px",
    optionIconSuffixWidthSmall: "32px",
    optionIconSuffixWidthMedium: "32px",
    optionIconSuffixWidthLarge: "36px",
    optionIconSuffixWidthHuge: "36px",
    optionPrefixWidthSmall: "14px",
    optionPrefixWidthMedium: "14px",
    optionPrefixWidthLarge: "16px",
    optionPrefixWidthHuge: "16px",
    optionIconPrefixWidthSmall: "36px",
    optionIconPrefixWidthMedium: "36px",
    optionIconPrefixWidthLarge: "40px",
    optionIconPrefixWidthHuge: "40px"
  };
  function self$a(vars) {
    const {
      primaryColor,
      textColor2,
      dividerColor,
      hoverColor,
      popoverColor,
      invertedColor,
      borderRadius,
      fontSizeSmall,
      fontSizeMedium,
      fontSizeLarge,
      fontSizeHuge,
      heightSmall,
      heightMedium,
      heightLarge,
      heightHuge,
      textColor3,
      opacityDisabled
    } = vars;
    return Object.assign(Object.assign({}, commonVariables$2), {
      optionHeightSmall: heightSmall,
      optionHeightMedium: heightMedium,
      optionHeightLarge: heightLarge,
      optionHeightHuge: heightHuge,
      borderRadius,
      fontSizeSmall,
      fontSizeMedium,
      fontSizeLarge,
      fontSizeHuge,
      // non-inverted
      optionTextColor: textColor2,
      optionTextColorHover: textColor2,
      optionTextColorActive: primaryColor,
      optionTextColorChildActive: primaryColor,
      color: popoverColor,
      dividerColor,
      suffixColor: textColor2,
      prefixColor: textColor2,
      optionColorHover: hoverColor,
      optionColorActive: changeColor(primaryColor, {
        alpha: 0.1
      }),
      groupHeaderTextColor: textColor3,
      // inverted
      optionTextColorInverted: "#BBB",
      optionTextColorHoverInverted: "#FFF",
      optionTextColorActiveInverted: "#FFF",
      optionTextColorChildActiveInverted: "#FFF",
      colorInverted: invertedColor,
      dividerColorInverted: "#BBB",
      suffixColorInverted: "#BBB",
      prefixColorInverted: "#BBB",
      optionColorHoverInverted: primaryColor,
      optionColorActiveInverted: primaryColor,
      groupHeaderTextColorInverted: "#AAA",
      optionOpacityDisabled: opacityDisabled
    });
  }
  const dropdownLight = createTheme({
    name: "Dropdown",
    common: derived,
    peers: {
      Popover: popoverLight
    },
    self: self$a
  });
  const commonVars$2 = {
    padding: "8px 14px"
  };
  function self$9(vars) {
    const {
      borderRadius,
      boxShadow2,
      baseColor
    } = vars;
    return Object.assign(Object.assign({}, commonVars$2), {
      borderRadius,
      boxShadow: boxShadow2,
      color: composite(baseColor, "rgba(0, 0, 0, .85)"),
      textColor: baseColor
    });
  }
  const tooltipLight = createTheme({
    name: "Tooltip",
    common: derived,
    peers: {
      Popover: popoverLight
    },
    self: self$9
  });
  const tooltipProps = Object.assign(Object.assign({}, popoverBaseProps), useTheme.props);
  const NTooltip = /* @__PURE__ */ defineComponent({
    name: "Tooltip",
    props: tooltipProps,
    slots: Object,
    __popover__: true,
    setup(props) {
      const {
        mergedClsPrefixRef
      } = useConfig(props);
      const themeRef = useTheme("Tooltip", "-tooltip", void 0, tooltipLight, props, mergedClsPrefixRef);
      const popoverRef = /* @__PURE__ */ ref(null);
      const tooltipExposedMethod = {
        syncPosition() {
          popoverRef.value.syncPosition();
        },
        setShow(show) {
          popoverRef.value.setShow(show);
        }
      };
      return Object.assign(Object.assign({}, tooltipExposedMethod), {
        popoverRef,
        mergedTheme: themeRef,
        popoverThemeOverrides: computed(() => {
          return themeRef.value.self;
        })
      });
    },
    render() {
      const {
        mergedTheme,
        internalExtraClass
      } = this;
      return h(NPopover, Object.assign(Object.assign({}, this.$props), {
        theme: mergedTheme.peers.Popover,
        themeOverrides: mergedTheme.peerOverrides.Popover,
        builtinThemeOverrides: this.popoverThemeOverrides,
        internalExtraClass: internalExtraClass.concat("tooltip"),
        ref: "popoverRef"
      }), this.$slots);
    }
  });
  const dropdownMenuInjectionKey = createInjectionKey("n-dropdown-menu");
  const dropdownInjectionKey = createInjectionKey("n-dropdown");
  const dropdownOptionInjectionKey = createInjectionKey("n-dropdown-option");
  const NDropdownDivider = /* @__PURE__ */ defineComponent({
    name: "DropdownDivider",
    props: {
      clsPrefix: {
        type: String,
        required: true
      }
    },
    render() {
      return h("div", {
        class: `${this.clsPrefix}-dropdown-divider`
      });
    }
  });
  const NDropdownGroupHeader = /* @__PURE__ */ defineComponent({
    name: "DropdownGroupHeader",
    props: {
      clsPrefix: {
        type: String,
        required: true
      },
      tmNode: {
        type: Object,
        required: true
      }
    },
    setup() {
      const {
        showIconRef,
        hasSubmenuRef
      } = inject(dropdownMenuInjectionKey);
      const {
        renderLabelRef,
        labelFieldRef,
        nodePropsRef,
        renderOptionRef
      } = inject(dropdownInjectionKey);
      return {
        labelField: labelFieldRef,
        showIcon: showIconRef,
        hasSubmenu: hasSubmenuRef,
        renderLabel: renderLabelRef,
        nodeProps: nodePropsRef,
        renderOption: renderOptionRef
      };
    },
    render() {
      var _a2;
      const {
        clsPrefix,
        hasSubmenu,
        showIcon,
        nodeProps,
        renderLabel,
        renderOption
      } = this;
      const {
        rawNode
      } = this.tmNode;
      const node = h("div", Object.assign({
        class: `${clsPrefix}-dropdown-option`
      }, nodeProps === null || nodeProps === void 0 ? void 0 : nodeProps(rawNode)), h("div", {
        class: `${clsPrefix}-dropdown-option-body ${clsPrefix}-dropdown-option-body--group`
      }, h("div", {
        "data-dropdown-option": true,
        class: [`${clsPrefix}-dropdown-option-body__prefix`, showIcon && `${clsPrefix}-dropdown-option-body__prefix--show-icon`]
      }, render(rawNode.icon)), h("div", {
        class: `${clsPrefix}-dropdown-option-body__label`,
        "data-dropdown-option": true
      }, renderLabel ? renderLabel(rawNode) : render((_a2 = rawNode.title) !== null && _a2 !== void 0 ? _a2 : rawNode[this.labelField])), h("div", {
        class: [`${clsPrefix}-dropdown-option-body__suffix`, hasSubmenu && `${clsPrefix}-dropdown-option-body__suffix--has-submenu`],
        "data-dropdown-option": true
      })));
      if (renderOption) {
        return renderOption({
          node,
          option: rawNode
        });
      }
      return node;
    }
  });
  function self$8(vars) {
    const {
      textColorBase,
      opacity1,
      opacity2,
      opacity3,
      opacity4,
      opacity5
    } = vars;
    return {
      color: textColorBase,
      opacity1Depth: opacity1,
      opacity2Depth: opacity2,
      opacity3Depth: opacity3,
      opacity4Depth: opacity4,
      opacity5Depth: opacity5
    };
  }
  const iconLight = {
    common: derived,
    self: self$8
  };
  const style$a = cB("icon", `
 height: 1em;
 width: 1em;
 line-height: 1em;
 text-align: center;
 display: inline-block;
 position: relative;
 fill: currentColor;
`, [cM("color-transition", {
    transition: "color .3s var(--n-bezier)"
  }), cM("depth", {
    color: "var(--n-color)"
  }, [c$1("svg", {
    opacity: "var(--n-opacity)",
    transition: "opacity .3s var(--n-bezier)"
  })]), c$1("svg", {
    height: "1em",
    width: "1em"
  })]);
  const iconProps = Object.assign(Object.assign({}, useTheme.props), {
    depth: [String, Number],
    size: [Number, String],
    color: String,
    component: [Object, Function]
  });
  const NIcon = /* @__PURE__ */ defineComponent({
    _n_icon__: true,
    name: "Icon",
    inheritAttrs: false,
    props: iconProps,
    setup(props) {
      const {
        mergedClsPrefixRef,
        inlineThemeDisabled
      } = useConfig(props);
      const themeRef = useTheme("Icon", "-icon", style$a, iconLight, props, mergedClsPrefixRef);
      const cssVarsRef = computed(() => {
        const {
          depth
        } = props;
        const {
          common: {
            cubicBezierEaseInOut: cubicBezierEaseInOut2
          },
          self: self2
        } = themeRef.value;
        if (depth !== void 0) {
          const {
            color,
            [`opacity${depth}Depth`]: opacity
          } = self2;
          return {
            "--n-bezier": cubicBezierEaseInOut2,
            "--n-color": color,
            "--n-opacity": opacity
          };
        }
        return {
          "--n-bezier": cubicBezierEaseInOut2,
          "--n-color": "",
          "--n-opacity": ""
        };
      });
      const themeClassHandle = inlineThemeDisabled ? useThemeClass("icon", computed(() => `${props.depth || "d"}`), cssVarsRef, props) : void 0;
      return {
        mergedClsPrefix: mergedClsPrefixRef,
        mergedStyle: computed(() => {
          const {
            size: size2,
            color
          } = props;
          return {
            fontSize: formatLength(size2),
            color
          };
        }),
        cssVars: inlineThemeDisabled ? void 0 : cssVarsRef,
        themeClass: themeClassHandle === null || themeClassHandle === void 0 ? void 0 : themeClassHandle.themeClass,
        onRender: themeClassHandle === null || themeClassHandle === void 0 ? void 0 : themeClassHandle.onRender
      };
    },
    render() {
      var _a2;
      const {
        $parent,
        depth,
        mergedClsPrefix,
        component,
        onRender,
        themeClass
      } = this;
      if ((_a2 = $parent === null || $parent === void 0 ? void 0 : $parent.$options) === null || _a2 === void 0 ? void 0 : _a2._n_icon__) {
        warn("icon", "don't wrap `n-icon` inside `n-icon`");
      }
      onRender === null || onRender === void 0 ? void 0 : onRender();
      return h("i", mergeProps(this.$attrs, {
        role: "img",
        class: [`${mergedClsPrefix}-icon`, themeClass, {
          [`${mergedClsPrefix}-icon--depth`]: depth,
          [`${mergedClsPrefix}-icon--color-transition`]: depth !== void 0
        }],
        style: [this.cssVars, this.mergedStyle]
      }), component ? h(component) : this.$slots);
    }
  });
  function isSubmenuNode(rawNode, childrenField) {
    return rawNode.type === "submenu" || rawNode.type === void 0 && rawNode[childrenField] !== void 0;
  }
  function isGroupNode(rawNode) {
    return rawNode.type === "group";
  }
  function isDividerNode$1(rawNode) {
    return rawNode.type === "divider";
  }
  function isRenderNode(rawNode) {
    return rawNode.type === "render";
  }
  const NDropdownOption = /* @__PURE__ */ defineComponent({
    name: "DropdownOption",
    props: {
      clsPrefix: {
        type: String,
        required: true
      },
      tmNode: {
        type: Object,
        required: true
      },
      parentKey: {
        type: [String, Number],
        default: null
      },
      placement: {
        type: String,
        default: "right-start"
      },
      props: Object,
      scrollable: Boolean
    },
    setup(props) {
      const NDropdown2 = inject(dropdownInjectionKey);
      const {
        hoverKeyRef,
        keyboardKeyRef,
        lastToggledSubmenuKeyRef,
        pendingKeyPathRef,
        activeKeyPathRef,
        animatedRef,
        mergedShowRef,
        renderLabelRef,
        renderIconRef,
        labelFieldRef,
        childrenFieldRef,
        renderOptionRef,
        nodePropsRef,
        menuPropsRef
      } = NDropdown2;
      const NDropdownOption2 = inject(dropdownOptionInjectionKey, null);
      const NDropdownMenu2 = inject(dropdownMenuInjectionKey);
      const NPopoverBody2 = inject(popoverBodyInjectionKey);
      const rawNodeRef = computed(() => props.tmNode.rawNode);
      const hasSubmenuRef = computed(() => {
        const {
          value: childrenField
        } = childrenFieldRef;
        return isSubmenuNode(props.tmNode.rawNode, childrenField);
      });
      const mergedDisabledRef = computed(() => {
        const {
          disabled
        } = props.tmNode;
        return disabled;
      });
      const showSubmenuRef = computed(() => {
        if (!hasSubmenuRef.value) return false;
        const {
          key,
          disabled
        } = props.tmNode;
        if (disabled) return false;
        const {
          value: hoverKey
        } = hoverKeyRef;
        const {
          value: keyboardKey
        } = keyboardKeyRef;
        const {
          value: lastToggledSubmenuKey
        } = lastToggledSubmenuKeyRef;
        const {
          value: pendingKeyPath
        } = pendingKeyPathRef;
        if (hoverKey !== null) return pendingKeyPath.includes(key);
        if (keyboardKey !== null) {
          return pendingKeyPath.includes(key) && pendingKeyPath[pendingKeyPath.length - 1] !== key;
        }
        if (lastToggledSubmenuKey !== null) return pendingKeyPath.includes(key);
        return false;
      });
      const shouldDelayRef = computed(() => {
        return keyboardKeyRef.value === null && !animatedRef.value;
      });
      const deferredShowSubmenuRef = useDeferredTrue(showSubmenuRef, 300, shouldDelayRef);
      const parentEnteringSubmenuRef = computed(() => {
        return !!(NDropdownOption2 === null || NDropdownOption2 === void 0 ? void 0 : NDropdownOption2.enteringSubmenuRef.value);
      });
      const enteringSubmenuRef = /* @__PURE__ */ ref(false);
      provide(dropdownOptionInjectionKey, {
        enteringSubmenuRef
      });
      function handleSubmenuBeforeEnter() {
        enteringSubmenuRef.value = true;
      }
      function handleSubmenuAfterEnter() {
        enteringSubmenuRef.value = false;
      }
      function handleMouseEnter() {
        const {
          parentKey,
          tmNode
        } = props;
        if (tmNode.disabled) return;
        if (!mergedShowRef.value) return;
        lastToggledSubmenuKeyRef.value = parentKey;
        keyboardKeyRef.value = null;
        hoverKeyRef.value = tmNode.key;
      }
      function handleMouseMove() {
        const {
          tmNode
        } = props;
        if (tmNode.disabled) return;
        if (!mergedShowRef.value) return;
        if (hoverKeyRef.value === tmNode.key) return;
        handleMouseEnter();
      }
      function handleMouseLeave(e) {
        if (props.tmNode.disabled) return;
        if (!mergedShowRef.value) return;
        const {
          relatedTarget
        } = e;
        if (relatedTarget && !happensIn({
          target: relatedTarget
        }, "dropdownOption") && !happensIn({
          target: relatedTarget
        }, "scrollbarRail")) {
          hoverKeyRef.value = null;
        }
      }
      function handleClick() {
        const {
          value: hasSubmenu
        } = hasSubmenuRef;
        const {
          tmNode
        } = props;
        if (!mergedShowRef.value) return;
        if (!hasSubmenu && !tmNode.disabled) {
          NDropdown2.doSelect(tmNode.key, tmNode.rawNode);
          NDropdown2.doUpdateShow(false);
        }
      }
      return {
        labelField: labelFieldRef,
        renderLabel: renderLabelRef,
        renderIcon: renderIconRef,
        siblingHasIcon: NDropdownMenu2.showIconRef,
        siblingHasSubmenu: NDropdownMenu2.hasSubmenuRef,
        menuProps: menuPropsRef,
        popoverBody: NPopoverBody2,
        animated: animatedRef,
        mergedShowSubmenu: computed(() => {
          return deferredShowSubmenuRef.value && !parentEnteringSubmenuRef.value;
        }),
        rawNode: rawNodeRef,
        hasSubmenu: hasSubmenuRef,
        pending: useMemo(() => {
          const {
            value: pendingKeyPath
          } = pendingKeyPathRef;
          const {
            key
          } = props.tmNode;
          return pendingKeyPath.includes(key);
        }),
        childActive: useMemo(() => {
          const {
            value: activeKeyPath
          } = activeKeyPathRef;
          const {
            key
          } = props.tmNode;
          const index = activeKeyPath.findIndex((k) => key === k);
          if (index === -1) return false;
          return index < activeKeyPath.length - 1;
        }),
        active: useMemo(() => {
          const {
            value: activeKeyPath
          } = activeKeyPathRef;
          const {
            key
          } = props.tmNode;
          const index = activeKeyPath.findIndex((k) => key === k);
          if (index === -1) return false;
          return index === activeKeyPath.length - 1;
        }),
        mergedDisabled: mergedDisabledRef,
        renderOption: renderOptionRef,
        nodeProps: nodePropsRef,
        handleClick,
        handleMouseMove,
        handleMouseEnter,
        handleMouseLeave,
        handleSubmenuBeforeEnter,
        handleSubmenuAfterEnter
      };
    },
    render() {
      var _a2, _b;
      const {
        animated,
        rawNode,
        mergedShowSubmenu,
        clsPrefix,
        siblingHasIcon,
        siblingHasSubmenu,
        renderLabel,
        renderIcon,
        renderOption,
        nodeProps,
        props,
        scrollable
      } = this;
      let submenuVNode = null;
      if (mergedShowSubmenu) {
        const submenuNodeProps = (_a2 = this.menuProps) === null || _a2 === void 0 ? void 0 : _a2.call(this, rawNode, rawNode.children);
        submenuVNode = h(NDropdownMenu, Object.assign({}, submenuNodeProps, {
          clsPrefix,
          scrollable: this.scrollable,
          tmNodes: this.tmNode.children,
          parentKey: this.tmNode.key
        }));
      }
      const builtinProps = {
        class: [`${clsPrefix}-dropdown-option-body`, this.pending && `${clsPrefix}-dropdown-option-body--pending`, this.active && `${clsPrefix}-dropdown-option-body--active`, this.childActive && `${clsPrefix}-dropdown-option-body--child-active`, this.mergedDisabled && `${clsPrefix}-dropdown-option-body--disabled`],
        onMousemove: this.handleMouseMove,
        onMouseenter: this.handleMouseEnter,
        onMouseleave: this.handleMouseLeave,
        onClick: this.handleClick
      };
      const optionNodeProps = nodeProps === null || nodeProps === void 0 ? void 0 : nodeProps(rawNode);
      const node = h("div", Object.assign({
        class: [`${clsPrefix}-dropdown-option`, optionNodeProps === null || optionNodeProps === void 0 ? void 0 : optionNodeProps.class],
        "data-dropdown-option": true
      }, optionNodeProps), h("div", mergeProps(builtinProps, props), [h("div", {
        class: [`${clsPrefix}-dropdown-option-body__prefix`, siblingHasIcon && `${clsPrefix}-dropdown-option-body__prefix--show-icon`]
      }, [renderIcon ? renderIcon(rawNode) : render(rawNode.icon)]), h("div", {
        "data-dropdown-option": true,
        class: `${clsPrefix}-dropdown-option-body__label`
      }, renderLabel ? renderLabel(rawNode) : render((_b = rawNode[this.labelField]) !== null && _b !== void 0 ? _b : rawNode.title)), h("div", {
        "data-dropdown-option": true,
        class: [`${clsPrefix}-dropdown-option-body__suffix`, siblingHasSubmenu && `${clsPrefix}-dropdown-option-body__suffix--has-submenu`]
      }, this.hasSubmenu ? h(NIcon, null, {
        default: () => h(ChevronRightIcon, null)
      }) : null)]), this.hasSubmenu ? h(Binder, null, {
        default: () => [h(VTarget, null, {
          default: () => h("div", {
            class: `${clsPrefix}-dropdown-offset-container`
          }, h(VFollower, {
            show: this.mergedShowSubmenu,
            placement: this.placement,
            to: scrollable ? this.popoverBody || void 0 : void 0,
            teleportDisabled: !scrollable
          }, {
            default: () => {
              return h("div", {
                class: `${clsPrefix}-dropdown-menu-wrapper`
              }, animated ? h(Transition, {
                onBeforeEnter: this.handleSubmenuBeforeEnter,
                onAfterEnter: this.handleSubmenuAfterEnter,
                name: "fade-in-scale-up-transition",
                appear: true
              }, {
                default: () => submenuVNode
              }) : submenuVNode);
            }
          }))
        })]
      }) : null);
      if (renderOption) {
        return renderOption({
          node,
          option: rawNode
        });
      }
      return node;
    }
  });
  const NDropdownGroup = /* @__PURE__ */ defineComponent({
    name: "NDropdownGroup",
    props: {
      clsPrefix: {
        type: String,
        required: true
      },
      tmNode: {
        type: Object,
        required: true
      },
      parentKey: {
        type: [String, Number],
        default: null
      }
    },
    render() {
      const {
        tmNode,
        parentKey,
        clsPrefix
      } = this;
      const {
        children
      } = tmNode;
      return h(Fragment, null, h(NDropdownGroupHeader, {
        clsPrefix,
        tmNode,
        key: tmNode.key
      }), children === null || children === void 0 ? void 0 : children.map((child) => {
        const {
          rawNode
        } = child;
        if (rawNode.show === false) return null;
        if (isDividerNode$1(rawNode)) {
          return h(NDropdownDivider, {
            clsPrefix,
            key: child.key
          });
        }
        if (child.isGroup) {
          warn("dropdown", "`group` node is not allowed to be put in `group` node.");
          return null;
        }
        return h(NDropdownOption, {
          clsPrefix,
          tmNode: child,
          parentKey,
          key: child.key
        });
      }));
    }
  });
  const NDropdownRenderOption = /* @__PURE__ */ defineComponent({
    name: "DropdownRenderOption",
    props: {
      tmNode: {
        type: Object,
        required: true
      }
    },
    render() {
      const {
        rawNode: {
          render: render2,
          props
        }
      } = this.tmNode;
      return h("div", props, [render2 === null || render2 === void 0 ? void 0 : render2()]);
    }
  });
  const NDropdownMenu = /* @__PURE__ */ defineComponent({
    name: "DropdownMenu",
    props: {
      scrollable: Boolean,
      showArrow: Boolean,
      arrowStyle: [String, Object],
      clsPrefix: {
        type: String,
        required: true
      },
      tmNodes: {
        type: Array,
        default: () => []
      },
      parentKey: {
        type: [String, Number],
        default: null
      }
    },
    setup(props) {
      const {
        renderIconRef,
        childrenFieldRef
      } = inject(dropdownInjectionKey);
      provide(dropdownMenuInjectionKey, {
        showIconRef: computed(() => {
          const renderIcon = renderIconRef.value;
          return props.tmNodes.some((tmNode) => {
            var _a2;
            if (tmNode.isGroup) {
              return (_a2 = tmNode.children) === null || _a2 === void 0 ? void 0 : _a2.some(({
                rawNode: rawChild
              }) => renderIcon ? renderIcon(rawChild) : rawChild.icon);
            }
            const {
              rawNode
            } = tmNode;
            return renderIcon ? renderIcon(rawNode) : rawNode.icon;
          });
        }),
        hasSubmenuRef: computed(() => {
          const {
            value: childrenField
          } = childrenFieldRef;
          return props.tmNodes.some((tmNode) => {
            var _a2;
            if (tmNode.isGroup) {
              return (_a2 = tmNode.children) === null || _a2 === void 0 ? void 0 : _a2.some(({
                rawNode: rawChild
              }) => isSubmenuNode(rawChild, childrenField));
            }
            const {
              rawNode
            } = tmNode;
            return isSubmenuNode(rawNode, childrenField);
          });
        })
      });
      const bodyRef = /* @__PURE__ */ ref(null);
      provide(modalBodyInjectionKey, null);
      provide(drawerBodyInjectionKey, null);
      provide(popoverBodyInjectionKey, bodyRef);
      return {
        bodyRef
      };
    },
    render() {
      const {
        parentKey,
        clsPrefix,
        scrollable
      } = this;
      const menuOptionsNode = this.tmNodes.map((tmNode) => {
        const {
          rawNode
        } = tmNode;
        if (rawNode.show === false) return null;
        if (isRenderNode(rawNode)) {
          return h(NDropdownRenderOption, {
            tmNode,
            key: tmNode.key
          });
        }
        if (isDividerNode$1(rawNode)) {
          return h(NDropdownDivider, {
            clsPrefix,
            key: tmNode.key
          });
        }
        if (isGroupNode(rawNode)) {
          return h(NDropdownGroup, {
            clsPrefix,
            tmNode,
            parentKey,
            key: tmNode.key
          });
        }
        return h(NDropdownOption, {
          clsPrefix,
          tmNode,
          parentKey,
          key: tmNode.key,
          props: rawNode.props,
          scrollable
        });
      });
      return h("div", {
        class: [`${clsPrefix}-dropdown-menu`, scrollable && `${clsPrefix}-dropdown-menu--scrollable`],
        ref: "bodyRef"
      }, scrollable ? h(XScrollbar, {
        contentClass: `${clsPrefix}-dropdown-menu__content`
      }, {
        default: () => menuOptionsNode
      }) : menuOptionsNode, this.showArrow ? renderArrow({
        clsPrefix,
        arrowStyle: this.arrowStyle,
        arrowClass: void 0,
        arrowWrapperClass: void 0,
        arrowWrapperStyle: void 0
      }) : null);
    }
  });
  const style$9 = cB("dropdown-menu", `
 transform-origin: var(--v-transform-origin);
 background-color: var(--n-color);
 border-radius: var(--n-border-radius);
 box-shadow: var(--n-box-shadow);
 position: relative;
 transition:
 background-color .3s var(--n-bezier),
 box-shadow .3s var(--n-bezier);
`, [fadeInScaleUpTransition(), cB("dropdown-option", `
 position: relative;
 `, [c$1("a", `
 text-decoration: none;
 color: inherit;
 outline: none;
 `, [c$1("&::before", `
 content: "";
 position: absolute;
 left: 0;
 right: 0;
 top: 0;
 bottom: 0;
 `)]), cB("dropdown-option-body", `
 display: flex;
 cursor: pointer;
 position: relative;
 height: var(--n-option-height);
 line-height: var(--n-option-height);
 font-size: var(--n-font-size);
 color: var(--n-option-text-color);
 transition: color .3s var(--n-bezier);
 `, [c$1("&::before", `
 content: "";
 position: absolute;
 top: 0;
 bottom: 0;
 left: 4px;
 right: 4px;
 transition: background-color .3s var(--n-bezier);
 border-radius: var(--n-border-radius);
 `), cNotM("disabled", [cM("pending", `
 color: var(--n-option-text-color-hover);
 `, [cE("prefix, suffix", `
 color: var(--n-option-text-color-hover);
 `), c$1("&::before", "background-color: var(--n-option-color-hover);")]), cM("active", `
 color: var(--n-option-text-color-active);
 `, [cE("prefix, suffix", `
 color: var(--n-option-text-color-active);
 `), c$1("&::before", "background-color: var(--n-option-color-active);")]), cM("child-active", `
 color: var(--n-option-text-color-child-active);
 `, [cE("prefix, suffix", `
 color: var(--n-option-text-color-child-active);
 `)])]), cM("disabled", `
 cursor: not-allowed;
 opacity: var(--n-option-opacity-disabled);
 `), cM("group", `
 font-size: calc(var(--n-font-size) - 1px);
 color: var(--n-group-header-text-color);
 `, [cE("prefix", `
 width: calc(var(--n-option-prefix-width) / 2);
 `, [cM("show-icon", `
 width: calc(var(--n-option-icon-prefix-width) / 2);
 `)])]), cE("prefix", `
 width: var(--n-option-prefix-width);
 display: flex;
 justify-content: center;
 align-items: center;
 color: var(--n-prefix-color);
 transition: color .3s var(--n-bezier);
 z-index: 1;
 `, [cM("show-icon", `
 width: var(--n-option-icon-prefix-width);
 `), cB("icon", `
 font-size: var(--n-option-icon-size);
 `)]), cE("label", `
 white-space: nowrap;
 flex: 1;
 z-index: 1;
 `), cE("suffix", `
 box-sizing: border-box;
 flex-grow: 0;
 flex-shrink: 0;
 display: flex;
 justify-content: flex-end;
 align-items: center;
 min-width: var(--n-option-suffix-width);
 padding: 0 8px;
 transition: color .3s var(--n-bezier);
 color: var(--n-suffix-color);
 z-index: 1;
 `, [cM("has-submenu", `
 width: var(--n-option-icon-suffix-width);
 `), cB("icon", `
 font-size: var(--n-option-icon-size);
 `)]), cB("dropdown-menu", "pointer-events: all;")]), cB("dropdown-offset-container", `
 pointer-events: none;
 position: absolute;
 left: 0;
 right: 0;
 top: -4px;
 bottom: -4px;
 `)]), cB("dropdown-divider", `
 transition: background-color .3s var(--n-bezier);
 background-color: var(--n-divider-color);
 height: 1px;
 margin: 4px 0;
 `), cB("dropdown-menu-wrapper", `
 transform-origin: var(--v-transform-origin);
 width: fit-content;
 `), c$1(">", [cB("scrollbar", `
 height: inherit;
 max-height: inherit;
 `)]), cNotM("scrollable", `
 padding: var(--n-padding);
 `), cM("scrollable", [cE("content", `
 padding: var(--n-padding);
 `)])]);
  const dropdownBaseProps = {
    animated: {
      type: Boolean,
      default: true
    },
    keyboard: {
      type: Boolean,
      default: true
    },
    size: String,
    inverted: Boolean,
    placement: {
      type: String,
      default: "bottom"
    },
    onSelect: [Function, Array],
    options: {
      type: Array,
      default: () => []
    },
    menuProps: Function,
    showArrow: Boolean,
    renderLabel: Function,
    renderIcon: Function,
    renderOption: Function,
    nodeProps: Function,
    labelField: {
      type: String,
      default: "label"
    },
    keyField: {
      type: String,
      default: "key"
    },
    childrenField: {
      type: String,
      default: "children"
    },
    // for menu, not documented
    value: [String, Number]
  };
  const popoverPropKeys = Object.keys(popoverBaseProps);
  const dropdownProps = Object.assign(Object.assign(Object.assign({}, popoverBaseProps), dropdownBaseProps), useTheme.props);
  const NDropdown = /* @__PURE__ */ defineComponent({
    name: "Dropdown",
    inheritAttrs: false,
    props: dropdownProps,
    setup(props) {
      const uncontrolledShowRef = /* @__PURE__ */ ref(false);
      const mergedShowRef = useMergedState(/* @__PURE__ */ toRef(props, "show"), uncontrolledShowRef);
      const treemateRef = computed(() => {
        const {
          keyField,
          childrenField
        } = props;
        return createTreeMate(props.options, {
          getKey(node) {
            return node[keyField];
          },
          getDisabled(node) {
            return node.disabled === true;
          },
          getIgnored(node) {
            return node.type === "divider" || node.type === "render";
          },
          getChildren(node) {
            return node[childrenField];
          }
        });
      });
      const tmNodesRef = computed(() => {
        return treemateRef.value.treeNodes;
      });
      const hoverKeyRef = /* @__PURE__ */ ref(null);
      const keyboardKeyRef = /* @__PURE__ */ ref(null);
      const lastToggledSubmenuKeyRef = /* @__PURE__ */ ref(null);
      const pendingKeyRef = computed(() => {
        var _a2, _b, _c;
        return (_c = (_b = (_a2 = hoverKeyRef.value) !== null && _a2 !== void 0 ? _a2 : keyboardKeyRef.value) !== null && _b !== void 0 ? _b : lastToggledSubmenuKeyRef.value) !== null && _c !== void 0 ? _c : null;
      });
      const pendingKeyPathRef = computed(() => treemateRef.value.getPath(pendingKeyRef.value).keyPath);
      const activeKeyPathRef = computed(() => treemateRef.value.getPath(props.value).keyPath);
      const keyboardEnabledRef = useMemo(() => {
        return props.keyboard && mergedShowRef.value;
      });
      useKeyboard({
        keydown: {
          ArrowUp: {
            prevent: true,
            handler: handleKeydownUp
          },
          ArrowRight: {
            prevent: true,
            handler: handleKeydownRight
          },
          ArrowDown: {
            prevent: true,
            handler: handleKeydownDown
          },
          ArrowLeft: {
            prevent: true,
            handler: handleKeydownLeft
          },
          Enter: {
            prevent: true,
            handler: handleKeydownEnter
          },
          Escape: handleKeydownEsc
        }
      }, keyboardEnabledRef);
      const {
        mergedClsPrefixRef,
        inlineThemeDisabled,
        mergedComponentPropsRef
      } = useConfig(props);
      const mergedSizeRef = computed(() => {
        var _a2, _b;
        return props.size || ((_b = (_a2 = mergedComponentPropsRef === null || mergedComponentPropsRef === void 0 ? void 0 : mergedComponentPropsRef.value) === null || _a2 === void 0 ? void 0 : _a2.Dropdown) === null || _b === void 0 ? void 0 : _b.size) || "medium";
      });
      const themeRef = useTheme("Dropdown", "-dropdown", style$9, dropdownLight, props, mergedClsPrefixRef);
      provide(dropdownInjectionKey, {
        labelFieldRef: /* @__PURE__ */ toRef(props, "labelField"),
        childrenFieldRef: /* @__PURE__ */ toRef(props, "childrenField"),
        renderLabelRef: /* @__PURE__ */ toRef(props, "renderLabel"),
        renderIconRef: /* @__PURE__ */ toRef(props, "renderIcon"),
        hoverKeyRef,
        keyboardKeyRef,
        lastToggledSubmenuKeyRef,
        pendingKeyPathRef,
        activeKeyPathRef,
        animatedRef: /* @__PURE__ */ toRef(props, "animated"),
        mergedShowRef,
        nodePropsRef: /* @__PURE__ */ toRef(props, "nodeProps"),
        renderOptionRef: /* @__PURE__ */ toRef(props, "renderOption"),
        menuPropsRef: /* @__PURE__ */ toRef(props, "menuProps"),
        doSelect,
        doUpdateShow
      });
      watch(mergedShowRef, (value) => {
        if (!props.animated && !value) {
          clearPendingState();
        }
      });
      function doSelect(key, node) {
        const {
          onSelect
        } = props;
        if (onSelect) call(onSelect, key, node);
      }
      function doUpdateShow(value) {
        const {
          "onUpdate:show": _onUpdateShow,
          onUpdateShow
        } = props;
        if (_onUpdateShow) call(_onUpdateShow, value);
        if (onUpdateShow) call(onUpdateShow, value);
        uncontrolledShowRef.value = value;
      }
      function clearPendingState() {
        hoverKeyRef.value = null;
        keyboardKeyRef.value = null;
        lastToggledSubmenuKeyRef.value = null;
      }
      function handleKeydownEsc() {
        doUpdateShow(false);
      }
      function handleKeydownLeft() {
        handleKeydown("left");
      }
      function handleKeydownRight() {
        handleKeydown("right");
      }
      function handleKeydownUp() {
        handleKeydown("up");
      }
      function handleKeydownDown() {
        handleKeydown("down");
      }
      function handleKeydownEnter() {
        const pendingNode = getPendingNode();
        if ((pendingNode === null || pendingNode === void 0 ? void 0 : pendingNode.isLeaf) && mergedShowRef.value) {
          doSelect(pendingNode.key, pendingNode.rawNode);
          doUpdateShow(false);
        }
      }
      function getPendingNode() {
        var _a2;
        const {
          value: treeMate
        } = treemateRef;
        const {
          value: pendingKey
        } = pendingKeyRef;
        if (!treeMate || pendingKey === null) return null;
        return (_a2 = treeMate.getNode(pendingKey)) !== null && _a2 !== void 0 ? _a2 : null;
      }
      function handleKeydown(direction) {
        const {
          value: pendingKey
        } = pendingKeyRef;
        const {
          value: {
            getFirstAvailableNode: getFirstAvailableNode2
          }
        } = treemateRef;
        let nextKeyboardKey = null;
        if (pendingKey === null) {
          const firstNode = getFirstAvailableNode2();
          if (firstNode !== null) {
            nextKeyboardKey = firstNode.key;
          }
        } else {
          const currentNode = getPendingNode();
          if (currentNode) {
            let nextNode;
            switch (direction) {
              case "down":
                nextNode = currentNode.getNext();
                break;
              case "up":
                nextNode = currentNode.getPrev();
                break;
              case "right":
                nextNode = currentNode.getChild();
                break;
              case "left":
                nextNode = currentNode.getParent();
                break;
            }
            if (nextNode) nextKeyboardKey = nextNode.key;
          }
        }
        if (nextKeyboardKey !== null) {
          hoverKeyRef.value = null;
          keyboardKeyRef.value = nextKeyboardKey;
        }
      }
      const cssVarsRef = computed(() => {
        const {
          inverted
        } = props;
        const size2 = mergedSizeRef.value;
        const {
          common: {
            cubicBezierEaseInOut: cubicBezierEaseInOut2
          },
          self: self2
        } = themeRef.value;
        const {
          padding,
          dividerColor,
          borderRadius,
          optionOpacityDisabled,
          [createKey("optionIconSuffixWidth", size2)]: optionIconSuffixWidth,
          [createKey("optionSuffixWidth", size2)]: optionSuffixWidth,
          [createKey("optionIconPrefixWidth", size2)]: optionIconPrefixWidth,
          [createKey("optionPrefixWidth", size2)]: optionPrefixWidth,
          [createKey("fontSize", size2)]: fontSize2,
          [createKey("optionHeight", size2)]: optionHeight,
          [createKey("optionIconSize", size2)]: optionIconSize
        } = self2;
        const vars = {
          "--n-bezier": cubicBezierEaseInOut2,
          "--n-font-size": fontSize2,
          "--n-padding": padding,
          "--n-border-radius": borderRadius,
          "--n-option-height": optionHeight,
          "--n-option-prefix-width": optionPrefixWidth,
          "--n-option-icon-prefix-width": optionIconPrefixWidth,
          "--n-option-suffix-width": optionSuffixWidth,
          "--n-option-icon-suffix-width": optionIconSuffixWidth,
          "--n-option-icon-size": optionIconSize,
          "--n-divider-color": dividerColor,
          "--n-option-opacity-disabled": optionOpacityDisabled
        };
        if (inverted) {
          vars["--n-color"] = self2.colorInverted;
          vars["--n-option-color-hover"] = self2.optionColorHoverInverted;
          vars["--n-option-color-active"] = self2.optionColorActiveInverted;
          vars["--n-option-text-color"] = self2.optionTextColorInverted;
          vars["--n-option-text-color-hover"] = self2.optionTextColorHoverInverted;
          vars["--n-option-text-color-active"] = self2.optionTextColorActiveInverted;
          vars["--n-option-text-color-child-active"] = self2.optionTextColorChildActiveInverted;
          vars["--n-prefix-color"] = self2.prefixColorInverted;
          vars["--n-suffix-color"] = self2.suffixColorInverted;
          vars["--n-group-header-text-color"] = self2.groupHeaderTextColorInverted;
        } else {
          vars["--n-color"] = self2.color;
          vars["--n-option-color-hover"] = self2.optionColorHover;
          vars["--n-option-color-active"] = self2.optionColorActive;
          vars["--n-option-text-color"] = self2.optionTextColor;
          vars["--n-option-text-color-hover"] = self2.optionTextColorHover;
          vars["--n-option-text-color-active"] = self2.optionTextColorActive;
          vars["--n-option-text-color-child-active"] = self2.optionTextColorChildActive;
          vars["--n-prefix-color"] = self2.prefixColor;
          vars["--n-suffix-color"] = self2.suffixColor;
          vars["--n-group-header-text-color"] = self2.groupHeaderTextColor;
        }
        return vars;
      });
      const themeClassHandle = inlineThemeDisabled ? useThemeClass("dropdown", computed(() => `${mergedSizeRef.value[0]}${props.inverted ? "i" : ""}`), cssVarsRef, props) : void 0;
      return {
        mergedClsPrefix: mergedClsPrefixRef,
        mergedTheme: themeRef,
        mergedSize: mergedSizeRef,
        // data
        tmNodes: tmNodesRef,
        // show
        mergedShow: mergedShowRef,
        // methods
        handleAfterLeave: () => {
          if (!props.animated) return;
          clearPendingState();
        },
        doUpdateShow,
        cssVars: inlineThemeDisabled ? void 0 : cssVarsRef,
        themeClass: themeClassHandle === null || themeClassHandle === void 0 ? void 0 : themeClassHandle.themeClass,
        onRender: themeClassHandle === null || themeClassHandle === void 0 ? void 0 : themeClassHandle.onRender
      };
    },
    render() {
      const renderPopoverBody = (className, ref2, style2, onMouseenter, onMouseleave) => {
        var _a2;
        const {
          mergedClsPrefix,
          menuProps: menuProps2
        } = this;
        (_a2 = this.onRender) === null || _a2 === void 0 ? void 0 : _a2.call(this);
        const menuNodeProps = (menuProps2 === null || menuProps2 === void 0 ? void 0 : menuProps2(void 0, this.tmNodes.map((v) => v.rawNode))) || {};
        const dropdownProps2 = {
          ref: createRefSetter(ref2),
          class: [className, `${mergedClsPrefix}-dropdown`, `${mergedClsPrefix}-dropdown--${this.mergedSize}-size`, this.themeClass],
          clsPrefix: mergedClsPrefix,
          tmNodes: this.tmNodes,
          style: [...style2, this.cssVars],
          showArrow: this.showArrow,
          arrowStyle: this.arrowStyle,
          scrollable: this.scrollable,
          onMouseenter,
          onMouseleave
        };
        return h(NDropdownMenu, mergeProps(this.$attrs, dropdownProps2, menuNodeProps));
      };
      const {
        mergedTheme
      } = this;
      const popoverProps2 = {
        show: this.mergedShow,
        theme: mergedTheme.peers.Popover,
        themeOverrides: mergedTheme.peerOverrides.Popover,
        internalOnAfterLeave: this.handleAfterLeave,
        internalRenderBody: renderPopoverBody,
        onUpdateShow: this.doUpdateShow,
        "onUpdate:show": void 0
      };
      return h(NPopover, Object.assign({}, keep(this.$props, popoverPropKeys), popoverProps2), {
        trigger: () => {
          var _a2, _b;
          return (_b = (_a2 = this.$slots).default) === null || _b === void 0 ? void 0 : _b.call(_a2);
        }
      });
    }
  });
  const commonVariables$1 = {
    thPaddingBorderedSmall: "8px 12px",
    thPaddingBorderedMedium: "12px 16px",
    thPaddingBorderedLarge: "16px 24px",
    thPaddingSmall: "0",
    thPaddingMedium: "0",
    thPaddingLarge: "0",
    tdPaddingBorderedSmall: "8px 12px",
    tdPaddingBorderedMedium: "12px 16px",
    tdPaddingBorderedLarge: "16px 24px",
    tdPaddingSmall: "0 0 8px 0",
    tdPaddingMedium: "0 0 12px 0",
    tdPaddingLarge: "0 0 16px 0"
  };
  function self$7(vars) {
    const {
      tableHeaderColor,
      textColor2,
      textColor1,
      cardColor,
      modalColor,
      popoverColor,
      dividerColor,
      borderRadius,
      fontWeightStrong,
      lineHeight: lineHeight2,
      fontSizeSmall,
      fontSizeMedium,
      fontSizeLarge
    } = vars;
    return Object.assign(Object.assign({}, commonVariables$1), {
      lineHeight: lineHeight2,
      fontSizeSmall,
      fontSizeMedium,
      fontSizeLarge,
      titleTextColor: textColor1,
      thColor: composite(cardColor, tableHeaderColor),
      thColorModal: composite(modalColor, tableHeaderColor),
      thColorPopover: composite(popoverColor, tableHeaderColor),
      thTextColor: textColor1,
      thFontWeight: fontWeightStrong,
      tdTextColor: textColor2,
      tdColor: cardColor,
      tdColorModal: modalColor,
      tdColorPopover: popoverColor,
      borderColor: composite(cardColor, dividerColor),
      borderColorModal: composite(modalColor, dividerColor),
      borderColorPopover: composite(popoverColor, dividerColor),
      borderRadius
    });
  }
  const descriptionsLight = {
    common: derived,
    self: self$7
  };
  const style$8 = c$1([cB("descriptions", {
    fontSize: "var(--n-font-size)"
  }, [cB("descriptions-separator", `
 display: inline-block;
 margin: 0 8px 0 2px;
 `), cB("descriptions-table-wrapper", [cB("descriptions-table", [cB("descriptions-table-row", [cB("descriptions-table-header", {
    padding: "var(--n-th-padding)"
  }), cB("descriptions-table-content", {
    padding: "var(--n-td-padding)"
  })])])]), cNotM("bordered", [cB("descriptions-table-wrapper", [cB("descriptions-table", [cB("descriptions-table-row", [c$1("&:last-child", [cB("descriptions-table-content", {
    paddingBottom: 0
  })])])])])]), cM("left-label-placement", [cB("descriptions-table-content", [c$1("> *", {
    verticalAlign: "top"
  })])]), cM("left-label-align", [c$1("th", {
    textAlign: "left"
  })]), cM("center-label-align", [c$1("th", {
    textAlign: "center"
  })]), cM("right-label-align", [c$1("th", {
    textAlign: "right"
  })]), cM("bordered", [cB("descriptions-table-wrapper", `
 border-radius: var(--n-border-radius);
 overflow: hidden;
 background: var(--n-merged-td-color);
 border: 1px solid var(--n-merged-border-color);
 `, [cB("descriptions-table", [cB("descriptions-table-row", [c$1("&:not(:last-child)", [cB("descriptions-table-content", {
    borderBottom: "1px solid var(--n-merged-border-color)"
  }), cB("descriptions-table-header", {
    borderBottom: "1px solid var(--n-merged-border-color)"
  })]), cB("descriptions-table-header", `
 font-weight: 400;
 background-clip: padding-box;
 background-color: var(--n-merged-th-color);
 `, [c$1("&:not(:last-child)", {
    borderRight: "1px solid var(--n-merged-border-color)"
  })]), cB("descriptions-table-content", [c$1("&:not(:last-child)", {
    borderRight: "1px solid var(--n-merged-border-color)"
  })])])])])]), cB("descriptions-header", `
 font-weight: var(--n-th-font-weight);
 font-size: 18px;
 transition: color .3s var(--n-bezier);
 line-height: var(--n-line-height);
 margin-bottom: 16px;
 color: var(--n-title-text-color);
 `), cB("descriptions-table-wrapper", `
 transition:
 background-color .3s var(--n-bezier),
 border-color .3s var(--n-bezier);
 `, [cB("descriptions-table", `
 width: 100%;
 border-collapse: separate;
 border-spacing: 0;
 box-sizing: border-box;
 `, [cB("descriptions-table-row", `
 box-sizing: border-box;
 transition: border-color .3s var(--n-bezier);
 `, [cB("descriptions-table-header", `
 font-weight: var(--n-th-font-weight);
 line-height: var(--n-line-height);
 display: table-cell;
 box-sizing: border-box;
 color: var(--n-th-text-color);
 transition:
 color .3s var(--n-bezier),
 background-color .3s var(--n-bezier),
 border-color .3s var(--n-bezier);
 `), cB("descriptions-table-content", `
 vertical-align: top;
 line-height: var(--n-line-height);
 display: table-cell;
 box-sizing: border-box;
 color: var(--n-td-text-color);
 transition:
 color .3s var(--n-bezier),
 background-color .3s var(--n-bezier),
 border-color .3s var(--n-bezier);
 `, [cE("content", `
 transition: color .3s var(--n-bezier);
 display: inline-block;
 color: var(--n-td-text-color);
 `)]), cE("label", `
 font-weight: var(--n-th-font-weight);
 transition: color .3s var(--n-bezier);
 display: inline-block;
 margin-right: 14px;
 color: var(--n-th-text-color);
 `)])])])]), cB("descriptions-table-wrapper", `
 --n-merged-th-color: var(--n-th-color);
 --n-merged-td-color: var(--n-td-color);
 --n-merged-border-color: var(--n-border-color);
 `), insideModal(cB("descriptions-table-wrapper", `
 --n-merged-th-color: var(--n-th-color-modal);
 --n-merged-td-color: var(--n-td-color-modal);
 --n-merged-border-color: var(--n-border-color-modal);
 `)), insidePopover(cB("descriptions-table-wrapper", `
 --n-merged-th-color: var(--n-th-color-popover);
 --n-merged-td-color: var(--n-td-color-popover);
 --n-merged-border-color: var(--n-border-color-popover);
 `))]);
  const DESCRIPTION_ITEM_FLAG = "DESCRIPTION_ITEM_FLAG";
  function isDescriptionsItem(vNode) {
    if (typeof vNode === "object" && vNode && !Array.isArray(vNode)) {
      return vNode.type && vNode.type[DESCRIPTION_ITEM_FLAG];
    }
    return false;
  }
  const descriptionsProps = Object.assign(Object.assign({}, useTheme.props), {
    title: String,
    column: {
      type: Number,
      default: 3
    },
    columns: Number,
    labelPlacement: {
      type: String,
      default: "top"
    },
    labelAlign: {
      type: String,
      default: "left"
    },
    separator: {
      type: String,
      default: ":"
    },
    size: String,
    bordered: Boolean,
    labelClass: String,
    labelStyle: [Object, String],
    contentClass: String,
    contentStyle: [Object, String]
  });
  const NDescriptions = /* @__PURE__ */ defineComponent({
    name: "Descriptions",
    props: descriptionsProps,
    slots: Object,
    setup(props) {
      const {
        mergedClsPrefixRef,
        inlineThemeDisabled,
        mergedComponentPropsRef
      } = useConfig(props);
      const mergedSizeRef = computed(() => {
        var _a2, _b;
        return props.size || ((_b = (_a2 = mergedComponentPropsRef === null || mergedComponentPropsRef === void 0 ? void 0 : mergedComponentPropsRef.value) === null || _a2 === void 0 ? void 0 : _a2.Descriptions) === null || _b === void 0 ? void 0 : _b.size) || "medium";
      });
      const themeRef = useTheme("Descriptions", "-descriptions", style$8, descriptionsLight, props, mergedClsPrefixRef);
      const cssVarsRef = computed(() => {
        const {
          bordered
        } = props;
        const mergedSize = mergedSizeRef.value;
        const {
          common: {
            cubicBezierEaseInOut: cubicBezierEaseInOut2
          },
          self: {
            titleTextColor,
            thColor,
            thColorModal,
            thColorPopover,
            thTextColor,
            thFontWeight,
            tdTextColor,
            tdColor,
            tdColorModal,
            tdColorPopover,
            borderColor,
            borderColorModal,
            borderColorPopover,
            borderRadius,
            lineHeight: lineHeight2,
            [createKey("fontSize", mergedSize)]: fontSize2,
            [createKey(bordered ? "thPaddingBordered" : "thPadding", mergedSize)]: thPadding,
            [createKey(bordered ? "tdPaddingBordered" : "tdPadding", mergedSize)]: tdPadding
          }
        } = themeRef.value;
        return {
          "--n-title-text-color": titleTextColor,
          "--n-th-padding": thPadding,
          "--n-td-padding": tdPadding,
          "--n-font-size": fontSize2,
          "--n-bezier": cubicBezierEaseInOut2,
          "--n-th-font-weight": thFontWeight,
          "--n-line-height": lineHeight2,
          "--n-th-text-color": thTextColor,
          "--n-td-text-color": tdTextColor,
          "--n-th-color": thColor,
          "--n-th-color-modal": thColorModal,
          "--n-th-color-popover": thColorPopover,
          "--n-td-color": tdColor,
          "--n-td-color-modal": tdColorModal,
          "--n-td-color-popover": tdColorPopover,
          "--n-border-radius": borderRadius,
          "--n-border-color": borderColor,
          "--n-border-color-modal": borderColorModal,
          "--n-border-color-popover": borderColorPopover
        };
      });
      const themeClassHandle = inlineThemeDisabled ? useThemeClass("descriptions", computed(() => {
        let hash = "";
        const {
          bordered
        } = props;
        if (bordered) hash += "a";
        hash += mergedSizeRef.value[0];
        return hash;
      }), cssVarsRef, props) : void 0;
      return {
        mergedClsPrefix: mergedClsPrefixRef,
        cssVars: inlineThemeDisabled ? void 0 : cssVarsRef,
        themeClass: themeClassHandle === null || themeClassHandle === void 0 ? void 0 : themeClassHandle.themeClass,
        onRender: themeClassHandle === null || themeClassHandle === void 0 ? void 0 : themeClassHandle.onRender,
        compitableColumn: useCompitable(props, ["columns", "column"]),
        inlineThemeDisabled,
        mergedSize: mergedSizeRef
      };
    },
    render() {
      const defaultSlots = this.$slots.default;
      const children = defaultSlots ? flatten$1(defaultSlots()) : [];
      children.length;
      const {
        contentClass,
        labelClass,
        compitableColumn,
        labelPlacement,
        labelAlign,
        mergedSize,
        bordered,
        title,
        cssVars,
        mergedClsPrefix,
        separator,
        onRender
      } = this;
      onRender === null || onRender === void 0 ? void 0 : onRender();
      const filteredChildren = children.filter((child) => isDescriptionsItem(child));
      const defaultState = {
        span: 0,
        row: [],
        secondRow: [],
        rows: []
      };
      const itemState = filteredChildren.reduce((state, vNode, index) => {
        const props = vNode.props || {};
        const isLastIteration = filteredChildren.length - 1 === index;
        const itemLabel = ["label" in props ? props.label : getVNodeChildren(vNode, "label")];
        const itemChildren = [getVNodeChildren(vNode)];
        const itemSpan = props.span || 1;
        const memorizedSpan = state.span;
        state.span += itemSpan;
        const labelStyle = props.labelStyle || props["label-style"] || this.labelStyle;
        const contentStyle = props.contentStyle || props["content-style"] || this.contentStyle;
        if (labelPlacement === "left") {
          if (bordered) {
            state.row.push(h("th", {
              class: [`${mergedClsPrefix}-descriptions-table-header`, labelClass],
              colspan: 1,
              style: labelStyle
            }, itemLabel), h("td", {
              class: [`${mergedClsPrefix}-descriptions-table-content`, contentClass],
              colspan: isLastIteration ? (compitableColumn - memorizedSpan) * 2 + 1 : itemSpan * 2 - 1,
              style: contentStyle
            }, itemChildren));
          } else {
            state.row.push(h("td", {
              class: `${mergedClsPrefix}-descriptions-table-content`,
              colspan: isLastIteration ? (compitableColumn - memorizedSpan) * 2 : itemSpan * 2
            }, h("span", {
              class: [`${mergedClsPrefix}-descriptions-table-content__label`, labelClass],
              style: labelStyle
            }, [...itemLabel, separator && h("span", {
              class: `${mergedClsPrefix}-descriptions-separator`
            }, separator)]), h("span", {
              class: [`${mergedClsPrefix}-descriptions-table-content__content`, contentClass],
              style: contentStyle
            }, itemChildren)));
          }
        } else {
          const colspan = isLastIteration ? (compitableColumn - memorizedSpan) * 2 : itemSpan * 2;
          state.row.push(h("th", {
            class: [`${mergedClsPrefix}-descriptions-table-header`, labelClass],
            colspan,
            style: labelStyle
          }, itemLabel));
          state.secondRow.push(h("td", {
            class: [`${mergedClsPrefix}-descriptions-table-content`, contentClass],
            colspan,
            style: contentStyle
          }, itemChildren));
        }
        if (state.span >= compitableColumn || isLastIteration) {
          state.span = 0;
          if (state.row.length) {
            state.rows.push(state.row);
            state.row = [];
          }
          if (labelPlacement !== "left") {
            if (state.secondRow.length) {
              state.rows.push(state.secondRow);
              state.secondRow = [];
            }
          }
        }
        return state;
      }, defaultState);
      const rows = itemState.rows.map((row) => h("tr", {
        class: `${mergedClsPrefix}-descriptions-table-row`
      }, row));
      return h("div", {
        style: cssVars,
        class: [`${mergedClsPrefix}-descriptions`, this.themeClass, `${mergedClsPrefix}-descriptions--${labelPlacement}-label-placement`, `${mergedClsPrefix}-descriptions--${labelAlign}-label-align`, `${mergedClsPrefix}-descriptions--${mergedSize}-size`, bordered && `${mergedClsPrefix}-descriptions--bordered`]
      }, title || this.$slots.header ? h("div", {
        class: `${mergedClsPrefix}-descriptions-header`
      }, title || getSlot(this, "header")) : null, h("div", {
        class: `${mergedClsPrefix}-descriptions-table-wrapper`
      }, h("table", {
        class: `${mergedClsPrefix}-descriptions-table`
      }, h("tbody", null, labelPlacement === "top" && h("tr", {
        class: `${mergedClsPrefix}-descriptions-table-row`,
        style: {
          visibility: "collapse"
        }
      }, repeat(compitableColumn * 2, h("td", null))), rows))));
    }
  });
  const descriptionsItemProps = {
    label: String,
    span: {
      type: Number,
      default: 1
    },
    labelClass: String,
    labelStyle: [Object, String],
    contentClass: String,
    contentStyle: [Object, String]
  };
  const NDescriptionsItem = /* @__PURE__ */ defineComponent({
    name: "DescriptionsItem",
    [DESCRIPTION_ITEM_FLAG]: true,
    props: descriptionsItemProps,
    slots: Object,
    render() {
      return null;
    }
  });
  const messageApiInjectionKey = createInjectionKey("n-message-api");
  const messageProviderInjectionKey = createInjectionKey("n-message-provider");
  const commonVariables = {
    margin: "0 0 8px 0",
    padding: "10px 20px",
    maxWidth: "720px",
    minWidth: "420px",
    iconMargin: "0 10px 0 0",
    closeMargin: "0 0 0 10px",
    closeSize: "20px",
    closeIconSize: "16px",
    iconSize: "20px",
    fontSize: "14px"
  };
  function self$6(vars) {
    const {
      textColor2,
      closeIconColor,
      closeIconColorHover,
      closeIconColorPressed,
      infoColor,
      successColor,
      errorColor,
      warningColor,
      popoverColor,
      boxShadow2,
      primaryColor,
      lineHeight: lineHeight2,
      borderRadius,
      closeColorHover,
      closeColorPressed
    } = vars;
    return Object.assign(Object.assign({}, commonVariables), {
      closeBorderRadius: borderRadius,
      textColor: textColor2,
      textColorInfo: textColor2,
      textColorSuccess: textColor2,
      textColorError: textColor2,
      textColorWarning: textColor2,
      textColorLoading: textColor2,
      color: popoverColor,
      colorInfo: popoverColor,
      colorSuccess: popoverColor,
      colorError: popoverColor,
      colorWarning: popoverColor,
      colorLoading: popoverColor,
      boxShadow: boxShadow2,
      boxShadowInfo: boxShadow2,
      boxShadowSuccess: boxShadow2,
      boxShadowError: boxShadow2,
      boxShadowWarning: boxShadow2,
      boxShadowLoading: boxShadow2,
      iconColor: textColor2,
      iconColorInfo: infoColor,
      iconColorSuccess: successColor,
      iconColorWarning: warningColor,
      iconColorError: errorColor,
      iconColorLoading: primaryColor,
      closeColorHover,
      closeColorPressed,
      closeIconColor,
      closeIconColorHover,
      closeIconColorPressed,
      closeColorHoverInfo: closeColorHover,
      closeColorPressedInfo: closeColorPressed,
      closeIconColorInfo: closeIconColor,
      closeIconColorHoverInfo: closeIconColorHover,
      closeIconColorPressedInfo: closeIconColorPressed,
      closeColorHoverSuccess: closeColorHover,
      closeColorPressedSuccess: closeColorPressed,
      closeIconColorSuccess: closeIconColor,
      closeIconColorHoverSuccess: closeIconColorHover,
      closeIconColorPressedSuccess: closeIconColorPressed,
      closeColorHoverError: closeColorHover,
      closeColorPressedError: closeColorPressed,
      closeIconColorError: closeIconColor,
      closeIconColorHoverError: closeIconColorHover,
      closeIconColorPressedError: closeIconColorPressed,
      closeColorHoverWarning: closeColorHover,
      closeColorPressedWarning: closeColorPressed,
      closeIconColorWarning: closeIconColor,
      closeIconColorHoverWarning: closeIconColorHover,
      closeIconColorPressedWarning: closeIconColorPressed,
      closeColorHoverLoading: closeColorHover,
      closeColorPressedLoading: closeColorPressed,
      closeIconColorLoading: closeIconColor,
      closeIconColorHoverLoading: closeIconColorHover,
      closeIconColorPressedLoading: closeIconColorPressed,
      loadingColor: primaryColor,
      lineHeight: lineHeight2,
      borderRadius,
      border: "0"
    });
  }
  const messageLight = {
    common: derived,
    self: self$6
  };
  const messageProps = {
    icon: Function,
    type: {
      type: String,
      default: "info"
    },
    content: [String, Number, Function],
    showIcon: {
      type: Boolean,
      default: true
    },
    closable: Boolean,
    keepAliveOnHover: Boolean,
    spinProps: Object,
    onClose: Function,
    onMouseenter: Function,
    onMouseleave: Function
  };
  const style$7 = c$1([cB("message-wrapper", `
 margin: var(--n-margin);
 z-index: 0;
 transform-origin: top center;
 display: flex;
 `, [fadeInHeightExpandTransition({
    overflow: "visible",
    originalTransition: "transform .3s var(--n-bezier)",
    enterToProps: {
      transform: "scale(1)"
    },
    leaveToProps: {
      transform: "scale(0.85)"
    }
  })]), cB("message", `
 box-sizing: border-box;
 display: flex;
 align-items: center;
 transition:
 color .3s var(--n-bezier),
 box-shadow .3s var(--n-bezier),
 background-color .3s var(--n-bezier),
 opacity .3s var(--n-bezier),
 transform .3s var(--n-bezier),
 margin-bottom .3s var(--n-bezier);
 padding: var(--n-padding);
 border-radius: var(--n-border-radius);
 border: var(--n-border);
 flex-wrap: nowrap;
 overflow: hidden;
 max-width: var(--n-max-width);
 color: var(--n-text-color);
 background-color: var(--n-color);
 box-shadow: var(--n-box-shadow);
 `, [cE("content", `
 display: inline-block;
 line-height: var(--n-line-height);
 font-size: var(--n-font-size);
 `), cE("icon", `
 position: relative;
 margin: var(--n-icon-margin);
 height: var(--n-icon-size);
 width: var(--n-icon-size);
 font-size: var(--n-icon-size);
 flex-shrink: 0;
 `, [["default", "info", "success", "warning", "error", "loading"].map((type) => cM(`${type}-type`, [c$1("> *", `
 color: var(--n-icon-color-${type});
 transition: color .3s var(--n-bezier);
 `)])), c$1("> *", `
 position: absolute;
 left: 0;
 top: 0;
 right: 0;
 bottom: 0;
 `, [iconSwitchTransition()])]), cE("close", `
 margin: var(--n-close-margin);
 transition:
 background-color .3s var(--n-bezier),
 color .3s var(--n-bezier);
 flex-shrink: 0;
 `, [c$1("&:hover", `
 color: var(--n-close-icon-color-hover);
 `), c$1("&:active", `
 color: var(--n-close-icon-color-pressed);
 `)])]), cB("message-container", `
 z-index: 6000;
 position: fixed;
 height: 0;
 overflow: visible;
 display: flex;
 flex-direction: column;
 align-items: center;
 `, [cM("top", `
 top: 12px;
 left: 0;
 right: 0;
 `), cM("top-left", `
 top: 12px;
 left: 12px;
 right: 0;
 align-items: flex-start;
 `), cM("top-right", `
 top: 12px;
 left: 0;
 right: 12px;
 align-items: flex-end;
 `), cM("bottom", `
 bottom: 4px;
 left: 0;
 right: 0;
 justify-content: flex-end;
 `), cM("bottom-left", `
 bottom: 4px;
 left: 12px;
 right: 0;
 justify-content: flex-end;
 align-items: flex-start;
 `), cM("bottom-right", `
 bottom: 4px;
 left: 0;
 right: 12px;
 justify-content: flex-end;
 align-items: flex-end;
 `)])]);
  const iconRenderMap = {
    info: () => h(InfoIcon, null),
    success: () => h(SuccessIcon, null),
    warning: () => h(WarningIcon, null),
    error: () => h(ErrorIcon, null),
    default: () => null
  };
  const NMessage = /* @__PURE__ */ defineComponent({
    name: "Message",
    props: Object.assign(Object.assign({}, messageProps), {
      render: Function
    }),
    setup(props) {
      const {
        inlineThemeDisabled,
        mergedRtlRef
      } = useConfig(props);
      const {
        props: messageProviderProps2,
        mergedClsPrefixRef
      } = inject(messageProviderInjectionKey);
      const rtlEnabledRef = useRtl("Message", mergedRtlRef, mergedClsPrefixRef);
      const themeRef = useTheme("Message", "-message", style$7, messageLight, messageProviderProps2, mergedClsPrefixRef);
      const cssVarsRef = computed(() => {
        const {
          type
        } = props;
        const {
          common: {
            cubicBezierEaseInOut: cubicBezierEaseInOut2
          },
          self: {
            padding,
            margin,
            maxWidth,
            iconMargin,
            closeMargin,
            closeSize,
            iconSize,
            fontSize: fontSize2,
            lineHeight: lineHeight2,
            borderRadius,
            border,
            iconColorInfo,
            iconColorSuccess,
            iconColorWarning,
            iconColorError,
            iconColorLoading,
            closeIconSize,
            closeBorderRadius,
            [createKey("textColor", type)]: textColor,
            [createKey("boxShadow", type)]: boxShadow,
            [createKey("color", type)]: color,
            [createKey("closeColorHover", type)]: closeColorHover,
            [createKey("closeColorPressed", type)]: closeColorPressed,
            [createKey("closeIconColor", type)]: closeIconColor,
            [createKey("closeIconColorPressed", type)]: closeIconColorPressed,
            [createKey("closeIconColorHover", type)]: closeIconColorHover
          }
        } = themeRef.value;
        return {
          "--n-bezier": cubicBezierEaseInOut2,
          "--n-margin": margin,
          "--n-padding": padding,
          "--n-max-width": maxWidth,
          "--n-font-size": fontSize2,
          "--n-icon-margin": iconMargin,
          "--n-icon-size": iconSize,
          "--n-close-icon-size": closeIconSize,
          "--n-close-border-radius": closeBorderRadius,
          "--n-close-size": closeSize,
          "--n-close-margin": closeMargin,
          "--n-text-color": textColor,
          "--n-color": color,
          "--n-box-shadow": boxShadow,
          "--n-icon-color-info": iconColorInfo,
          "--n-icon-color-success": iconColorSuccess,
          "--n-icon-color-warning": iconColorWarning,
          "--n-icon-color-error": iconColorError,
          "--n-icon-color-loading": iconColorLoading,
          "--n-close-color-hover": closeColorHover,
          "--n-close-color-pressed": closeColorPressed,
          "--n-close-icon-color": closeIconColor,
          "--n-close-icon-color-pressed": closeIconColorPressed,
          "--n-close-icon-color-hover": closeIconColorHover,
          "--n-line-height": lineHeight2,
          "--n-border-radius": borderRadius,
          "--n-border": border
        };
      });
      const themeClassHandle = inlineThemeDisabled ? useThemeClass("message", computed(() => props.type[0]), cssVarsRef, {}) : void 0;
      return {
        mergedClsPrefix: mergedClsPrefixRef,
        rtlEnabled: rtlEnabledRef,
        messageProviderProps: messageProviderProps2,
        handleClose() {
          var _a2;
          (_a2 = props.onClose) === null || _a2 === void 0 ? void 0 : _a2.call(props);
        },
        cssVars: inlineThemeDisabled ? void 0 : cssVarsRef,
        themeClass: themeClassHandle === null || themeClassHandle === void 0 ? void 0 : themeClassHandle.themeClass,
        onRender: themeClassHandle === null || themeClassHandle === void 0 ? void 0 : themeClassHandle.onRender,
        placement: messageProviderProps2.placement
      };
    },
    render() {
      const {
        render: renderMessage,
        type,
        closable,
        content,
        mergedClsPrefix,
        cssVars,
        themeClass,
        onRender,
        icon,
        handleClose,
        showIcon
      } = this;
      onRender === null || onRender === void 0 ? void 0 : onRender();
      let iconNode;
      return h("div", {
        class: [`${mergedClsPrefix}-message-wrapper`, themeClass],
        onMouseenter: this.onMouseenter,
        onMouseleave: this.onMouseleave,
        style: [{
          alignItems: this.placement.startsWith("top") ? "flex-start" : "flex-end"
        }, cssVars]
      }, renderMessage ? renderMessage(this.$props) : h("div", {
        class: [`${mergedClsPrefix}-message ${mergedClsPrefix}-message--${type}-type`, this.rtlEnabled && `${mergedClsPrefix}-message--rtl`]
      }, (iconNode = createIconVNode(icon, type, mergedClsPrefix, this.spinProps)) && showIcon ? h("div", {
        class: `${mergedClsPrefix}-message__icon ${mergedClsPrefix}-message__icon--${type}-type`
      }, h(NIconSwitchTransition, null, {
        default: () => iconNode
      })) : null, h("div", {
        class: `${mergedClsPrefix}-message__content`
      }, render(content)), closable ? h(NBaseClose, {
        clsPrefix: mergedClsPrefix,
        class: `${mergedClsPrefix}-message__close`,
        onClick: handleClose,
        absolute: true
      }) : null));
    }
  });
  function createIconVNode(icon, type, clsPrefix, spinProps) {
    if (typeof icon === "function") {
      return icon();
    } else {
      const innerIcon = type === "loading" ? h(NBaseLoading, Object.assign({
        clsPrefix,
        strokeWidth: 24,
        scale: 0.85
      }, spinProps)) : iconRenderMap[type]();
      if (!innerIcon) return null;
      return h(NBaseIcon, {
        clsPrefix,
        key: type
      }, {
        default: () => innerIcon
      });
    }
  }
  const MessageEnvironment = /* @__PURE__ */ defineComponent({
    name: "MessageEnvironment",
    props: Object.assign(Object.assign({}, messageProps), {
      duration: {
        type: Number,
        default: 3e3
      },
      onAfterLeave: Function,
      onLeave: Function,
      internalKey: {
        type: String,
        required: true
      },
      // private
      onInternalAfterLeave: Function,
      // deprecated
      onHide: Function,
      onAfterHide: Function
    }),
    setup(props) {
      let timerId = null;
      const showRef = /* @__PURE__ */ ref(true);
      onMounted(() => {
        setHideTimeout();
      });
      function setHideTimeout() {
        const {
          duration: duration2
        } = props;
        if (duration2) {
          timerId = window.setTimeout(hide, duration2);
        }
      }
      function handleMouseenter(e) {
        if (e.currentTarget !== e.target) return;
        if (timerId !== null) {
          window.clearTimeout(timerId);
          timerId = null;
        }
      }
      function handleMouseleave(e) {
        if (e.currentTarget !== e.target) return;
        setHideTimeout();
      }
      function hide() {
        const {
          onHide
        } = props;
        showRef.value = false;
        if (timerId) {
          window.clearTimeout(timerId);
          timerId = null;
        }
        if (onHide) onHide();
      }
      function handleClose() {
        const {
          onClose
        } = props;
        if (onClose) onClose();
        hide();
      }
      function handleAfterLeave() {
        const {
          onAfterLeave,
          onInternalAfterLeave,
          onAfterHide,
          internalKey
        } = props;
        if (onAfterLeave) onAfterLeave();
        if (onInternalAfterLeave) onInternalAfterLeave(internalKey);
        if (onAfterHide) onAfterHide();
      }
      function deactivate() {
        hide();
      }
      return {
        show: showRef,
        hide,
        handleClose,
        handleAfterLeave,
        handleMouseleave,
        handleMouseenter,
        deactivate
      };
    },
    render() {
      return h(NFadeInExpandTransition, {
        appear: true,
        onAfterLeave: this.handleAfterLeave,
        onLeave: this.onLeave
      }, {
        default: () => [this.show ? h(NMessage, {
          content: this.content,
          type: this.type,
          icon: this.icon,
          showIcon: this.showIcon,
          closable: this.closable,
          spinProps: this.spinProps,
          onClose: this.handleClose,
          onMouseenter: this.keepAliveOnHover ? this.handleMouseenter : void 0,
          onMouseleave: this.keepAliveOnHover ? this.handleMouseleave : void 0
        }) : null]
      });
    }
  });
  const messageProviderProps = Object.assign(Object.assign({}, useTheme.props), {
    to: [String, Object],
    duration: {
      type: Number,
      default: 3e3
    },
    keepAliveOnHover: Boolean,
    max: Number,
    placement: {
      type: String,
      default: "top"
    },
    closable: Boolean,
    containerClass: String,
    containerStyle: [String, Object]
  });
  const NMessageProvider = /* @__PURE__ */ defineComponent({
    name: "MessageProvider",
    props: messageProviderProps,
    setup(props) {
      const {
        mergedClsPrefixRef
      } = useConfig(props);
      const messageListRef = /* @__PURE__ */ ref([]);
      const messageRefs = /* @__PURE__ */ ref({});
      const api = {
        create(content, options) {
          return create(content, Object.assign({
            type: "default"
          }, options));
        },
        info(content, options) {
          return create(content, Object.assign(Object.assign({}, options), {
            type: "info"
          }));
        },
        success(content, options) {
          return create(content, Object.assign(Object.assign({}, options), {
            type: "success"
          }));
        },
        warning(content, options) {
          return create(content, Object.assign(Object.assign({}, options), {
            type: "warning"
          }));
        },
        error(content, options) {
          return create(content, Object.assign(Object.assign({}, options), {
            type: "error"
          }));
        },
        loading(content, options) {
          return create(content, Object.assign(Object.assign({}, options), {
            type: "loading"
          }));
        },
        destroyAll
      };
      provide(messageProviderInjectionKey, {
        props,
        mergedClsPrefixRef
      });
      provide(messageApiInjectionKey, api);
      function create(content, options) {
        const key = createId();
        const messageReactive = /* @__PURE__ */ reactive(Object.assign(Object.assign({}, options), {
          content,
          key,
          destroy: () => {
            var _a2;
            (_a2 = messageRefs.value[key]) === null || _a2 === void 0 ? void 0 : _a2.hide();
          }
        }));
        const {
          max
        } = props;
        if (max && messageListRef.value.length >= max) {
          messageListRef.value.shift();
        }
        messageListRef.value.push(messageReactive);
        return messageReactive;
      }
      function handleAfterLeave(key) {
        messageListRef.value.splice(messageListRef.value.findIndex((message) => message.key === key), 1);
        delete messageRefs.value[key];
      }
      function destroyAll() {
        Object.values(messageRefs.value).forEach((messageInstRef) => {
          messageInstRef.hide();
        });
      }
      return Object.assign({
        mergedClsPrefix: mergedClsPrefixRef,
        messageRefs,
        messageList: messageListRef,
        handleAfterLeave
      }, api);
    },
    render() {
      var _a2, _b, _c;
      return h(Fragment, null, (_b = (_a2 = this.$slots).default) === null || _b === void 0 ? void 0 : _b.call(_a2), this.messageList.length ? h(Teleport, {
        to: (_c = this.to) !== null && _c !== void 0 ? _c : "body"
      }, h("div", {
        class: [`${this.mergedClsPrefix}-message-container`, `${this.mergedClsPrefix}-message-container--${this.placement}`, this.containerClass],
        key: "message-container",
        style: this.containerStyle
      }, this.messageList.map((message) => {
        return h(MessageEnvironment, Object.assign({
          ref: (inst) => {
            if (inst) {
              this.messageRefs[message.key] = inst;
            }
          },
          internalKey: message.key,
          onInternalAfterLeave: this.handleAfterLeave
        }, omit(message, ["destroy"], void 0), {
          duration: message.duration === void 0 ? this.duration : message.duration,
          keepAliveOnHover: message.keepAliveOnHover === void 0 ? this.keepAliveOnHover : message.keepAliveOnHover,
          closable: message.closable === void 0 ? this.closable : message.closable
        }));
      }))) : null);
    }
  });
  function useMessage() {
    const api = inject(messageApiInjectionKey, null);
    if (api === null) {
      throwError("use-message", "No outer <n-message-provider /> founded. See prerequisite in https://www.naiveui.com/en-US/os-theme/components/message for more details. If you want to use `useMessage` outside setup, please check https://www.naiveui.com/zh-CN/os-theme/components/message#Q-&-A.");
    }
    return api;
  }
  const commonVars$1 = {
    gapSmall: "4px 8px",
    gapMedium: "8px 12px",
    gapLarge: "12px 16px"
  };
  function self$5() {
    return commonVars$1;
  }
  const spaceLight = {
    self: self$5
  };
  let supportFlexGap;
  function ensureSupportFlexGap() {
    if (!isBrowser$1) return true;
    if (supportFlexGap === void 0) {
      const flex = document.createElement("div");
      flex.style.display = "flex";
      flex.style.flexDirection = "column";
      flex.style.rowGap = "1px";
      flex.appendChild(document.createElement("div"));
      flex.appendChild(document.createElement("div"));
      document.body.appendChild(flex);
      const isSupported = flex.scrollHeight === 1;
      document.body.removeChild(flex);
      return supportFlexGap = isSupported;
    }
    return supportFlexGap;
  }
  const spaceProps = Object.assign(Object.assign({}, useTheme.props), {
    align: String,
    justify: {
      type: String,
      default: "start"
    },
    inline: Boolean,
    vertical: Boolean,
    reverse: Boolean,
    size: [String, Number, Array],
    wrapItem: {
      type: Boolean,
      default: true
    },
    itemClass: String,
    itemStyle: [String, Object],
    wrap: {
      type: Boolean,
      default: true
    },
    // internal
    internalUseGap: {
      type: Boolean,
      default: void 0
    }
  });
  const NSpace = /* @__PURE__ */ defineComponent({
    name: "Space",
    props: spaceProps,
    setup(props) {
      const {
        mergedClsPrefixRef,
        mergedRtlRef,
        mergedComponentPropsRef
      } = useConfig(props);
      const mergedSizeRef = computed(() => {
        var _a2, _b;
        return props.size || ((_b = (_a2 = mergedComponentPropsRef === null || mergedComponentPropsRef === void 0 ? void 0 : mergedComponentPropsRef.value) === null || _a2 === void 0 ? void 0 : _a2.Space) === null || _b === void 0 ? void 0 : _b.size) || "medium";
      });
      const themeRef = useTheme("Space", "-space", void 0, spaceLight, props, mergedClsPrefixRef);
      const rtlEnabledRef = useRtl("Space", mergedRtlRef, mergedClsPrefixRef);
      return {
        useGap: ensureSupportFlexGap(),
        rtlEnabled: rtlEnabledRef,
        mergedClsPrefix: mergedClsPrefixRef,
        margin: computed(() => {
          const size2 = mergedSizeRef.value;
          if (Array.isArray(size2)) {
            return {
              horizontal: size2[0],
              vertical: size2[1]
            };
          }
          if (typeof size2 === "number") {
            return {
              horizontal: size2,
              vertical: size2
            };
          }
          const {
            self: {
              [createKey("gap", size2)]: gap
            }
          } = themeRef.value;
          const {
            row,
            col
          } = getGap(gap);
          return {
            horizontal: depx(col),
            vertical: depx(row)
          };
        })
      };
    },
    render() {
      const {
        vertical,
        reverse,
        align,
        inline,
        justify,
        itemClass,
        itemStyle,
        margin,
        wrap,
        mergedClsPrefix,
        rtlEnabled,
        useGap,
        wrapItem,
        internalUseGap
      } = this;
      const children = flatten$1(getSlot(this), false);
      if (!children.length) return null;
      const horizontalMargin = `${margin.horizontal}px`;
      const semiHorizontalMargin = `${margin.horizontal / 2}px`;
      const verticalMargin = `${margin.vertical}px`;
      const semiVerticalMargin = `${margin.vertical / 2}px`;
      const lastIndex = children.length - 1;
      const isJustifySpace = justify.startsWith("space-");
      return h("div", {
        role: "none",
        class: [`${mergedClsPrefix}-space`, rtlEnabled && `${mergedClsPrefix}-space--rtl`],
        style: {
          display: inline ? "inline-flex" : "flex",
          flexDirection: (() => {
            if (vertical && !reverse) return "column";
            if (vertical && reverse) return "column-reverse";
            if (!vertical && reverse) return "row-reverse";
            else return "row";
          })(),
          justifyContent: ["start", "end"].includes(justify) ? `flex-${justify}` : justify,
          flexWrap: !wrap || vertical ? "nowrap" : "wrap",
          marginTop: useGap || vertical ? "" : `-${semiVerticalMargin}`,
          marginBottom: useGap || vertical ? "" : `-${semiVerticalMargin}`,
          alignItems: align,
          gap: useGap ? `${margin.vertical}px ${margin.horizontal}px` : ""
        }
      }, !wrapItem && (useGap || internalUseGap) ? children : children.map((child, index) => child.type === Comment ? child : h("div", {
        role: "none",
        class: itemClass,
        style: [itemStyle, {
          maxWidth: "100%"
        }, useGap ? "" : vertical ? {
          marginBottom: index !== lastIndex ? verticalMargin : ""
        } : rtlEnabled ? {
          marginLeft: isJustifySpace ? justify === "space-between" && index === lastIndex ? "" : semiHorizontalMargin : index !== lastIndex ? horizontalMargin : "",
          marginRight: isJustifySpace ? justify === "space-between" && index === 0 ? "" : semiHorizontalMargin : "",
          paddingTop: semiVerticalMargin,
          paddingBottom: semiVerticalMargin
        } : {
          marginRight: isJustifySpace ? justify === "space-between" && index === lastIndex ? "" : semiHorizontalMargin : index !== lastIndex ? horizontalMargin : "",
          marginLeft: isJustifySpace ? justify === "space-between" && index === 0 ? "" : semiHorizontalMargin : "",
          paddingTop: semiVerticalMargin,
          paddingBottom: semiVerticalMargin
        }]
      }, child)));
    }
  });
  function self$4(vars) {
    const {
      baseColor,
      textColor2,
      bodyColor,
      cardColor,
      dividerColor,
      actionColor,
      scrollbarColor,
      scrollbarColorHover,
      invertedColor
    } = vars;
    return {
      textColor: textColor2,
      textColorInverted: "#FFF",
      color: bodyColor,
      colorEmbedded: actionColor,
      headerColor: cardColor,
      headerColorInverted: invertedColor,
      footerColor: actionColor,
      footerColorInverted: invertedColor,
      headerBorderColor: dividerColor,
      headerBorderColorInverted: invertedColor,
      footerBorderColor: dividerColor,
      footerBorderColorInverted: invertedColor,
      siderBorderColor: dividerColor,
      siderBorderColorInverted: invertedColor,
      siderColor: cardColor,
      siderColorInverted: invertedColor,
      siderToggleButtonBorder: `1px solid ${dividerColor}`,
      siderToggleButtonColor: baseColor,
      siderToggleButtonIconColor: textColor2,
      siderToggleButtonIconColorInverted: textColor2,
      siderToggleBarColor: composite(bodyColor, scrollbarColor),
      siderToggleBarColorHover: composite(bodyColor, scrollbarColorHover),
      // hack for inverted background
      __invertScrollbar: "true"
    };
  }
  const layoutLight = createTheme({
    name: "Layout",
    common: derived,
    peers: {
      Scrollbar: scrollbarLight
    },
    self: self$4
  });
  function createPartialInvertedVars(color, activeItemColor, activeTextColor, groupTextColor) {
    return {
      itemColorHoverInverted: "#0000",
      itemColorActiveInverted: activeItemColor,
      itemColorActiveHoverInverted: activeItemColor,
      itemColorActiveCollapsedInverted: activeItemColor,
      itemTextColorInverted: color,
      itemTextColorHoverInverted: activeTextColor,
      itemTextColorChildActiveInverted: activeTextColor,
      itemTextColorChildActiveHoverInverted: activeTextColor,
      itemTextColorActiveInverted: activeTextColor,
      itemTextColorActiveHoverInverted: activeTextColor,
      itemTextColorHorizontalInverted: color,
      itemTextColorHoverHorizontalInverted: activeTextColor,
      itemTextColorChildActiveHorizontalInverted: activeTextColor,
      itemTextColorChildActiveHoverHorizontalInverted: activeTextColor,
      itemTextColorActiveHorizontalInverted: activeTextColor,
      itemTextColorActiveHoverHorizontalInverted: activeTextColor,
      itemIconColorInverted: color,
      itemIconColorHoverInverted: activeTextColor,
      itemIconColorActiveInverted: activeTextColor,
      itemIconColorActiveHoverInverted: activeTextColor,
      itemIconColorChildActiveInverted: activeTextColor,
      itemIconColorChildActiveHoverInverted: activeTextColor,
      itemIconColorCollapsedInverted: color,
      itemIconColorHorizontalInverted: color,
      itemIconColorHoverHorizontalInverted: activeTextColor,
      itemIconColorActiveHorizontalInverted: activeTextColor,
      itemIconColorActiveHoverHorizontalInverted: activeTextColor,
      itemIconColorChildActiveHorizontalInverted: activeTextColor,
      itemIconColorChildActiveHoverHorizontalInverted: activeTextColor,
      arrowColorInverted: color,
      arrowColorHoverInverted: activeTextColor,
      arrowColorActiveInverted: activeTextColor,
      arrowColorActiveHoverInverted: activeTextColor,
      arrowColorChildActiveInverted: activeTextColor,
      arrowColorChildActiveHoverInverted: activeTextColor,
      groupTextColorInverted: groupTextColor
    };
  }
  function self$3(vars) {
    const {
      borderRadius,
      textColor3,
      primaryColor,
      textColor2,
      textColor1,
      fontSize: fontSize2,
      dividerColor,
      hoverColor,
      primaryColorHover
    } = vars;
    return Object.assign({
      borderRadius,
      color: "#0000",
      groupTextColor: textColor3,
      itemColorHover: hoverColor,
      itemColorActive: changeColor(primaryColor, {
        alpha: 0.1
      }),
      itemColorActiveHover: changeColor(primaryColor, {
        alpha: 0.1
      }),
      itemColorActiveCollapsed: changeColor(primaryColor, {
        alpha: 0.1
      }),
      itemTextColor: textColor2,
      itemTextColorHover: textColor2,
      itemTextColorActive: primaryColor,
      itemTextColorActiveHover: primaryColor,
      itemTextColorChildActive: primaryColor,
      itemTextColorChildActiveHover: primaryColor,
      itemTextColorHorizontal: textColor2,
      itemTextColorHoverHorizontal: primaryColorHover,
      itemTextColorActiveHorizontal: primaryColor,
      itemTextColorActiveHoverHorizontal: primaryColor,
      itemTextColorChildActiveHorizontal: primaryColor,
      itemTextColorChildActiveHoverHorizontal: primaryColor,
      itemIconColor: textColor1,
      itemIconColorHover: textColor1,
      itemIconColorActive: primaryColor,
      itemIconColorActiveHover: primaryColor,
      itemIconColorChildActive: primaryColor,
      itemIconColorChildActiveHover: primaryColor,
      itemIconColorCollapsed: textColor1,
      itemIconColorHorizontal: textColor1,
      itemIconColorHoverHorizontal: primaryColorHover,
      itemIconColorActiveHorizontal: primaryColor,
      itemIconColorActiveHoverHorizontal: primaryColor,
      itemIconColorChildActiveHorizontal: primaryColor,
      itemIconColorChildActiveHoverHorizontal: primaryColor,
      itemHeight: "42px",
      arrowColor: textColor2,
      arrowColorHover: textColor2,
      arrowColorActive: primaryColor,
      arrowColorActiveHover: primaryColor,
      arrowColorChildActive: primaryColor,
      arrowColorChildActiveHover: primaryColor,
      colorInverted: "#0000",
      borderColorHorizontal: "#0000",
      fontSize: fontSize2,
      dividerColor
    }, createPartialInvertedVars("#BBB", primaryColor, "#FFF", "#AAA"));
  }
  const menuLight = createTheme({
    name: "Menu",
    common: derived,
    peers: {
      Tooltip: tooltipLight,
      Dropdown: dropdownLight
    },
    self: self$3
  });
  function self$2(vars) {
    const {
      textColor2,
      textColor3,
      fontSize: fontSize2,
      fontWeight
    } = vars;
    return {
      labelFontSize: fontSize2,
      labelFontWeight: fontWeight,
      valueFontWeight: fontWeight,
      valueFontSize: "24px",
      labelTextColor: textColor3,
      valuePrefixTextColor: textColor2,
      valueSuffixTextColor: textColor2,
      valueTextColor: textColor2
    };
  }
  const statisticLight = {
    common: derived,
    self: self$2
  };
  const commonVars = {
    headerFontSize1: "30px",
    headerFontSize2: "22px",
    headerFontSize3: "18px",
    headerFontSize4: "16px",
    headerFontSize5: "16px",
    headerFontSize6: "16px",
    headerMargin1: "28px 0 20px 0",
    headerMargin2: "28px 0 20px 0",
    headerMargin3: "28px 0 20px 0",
    headerMargin4: "28px 0 18px 0",
    headerMargin5: "28px 0 18px 0",
    headerMargin6: "28px 0 18px 0",
    headerPrefixWidth1: "16px",
    headerPrefixWidth2: "16px",
    headerPrefixWidth3: "12px",
    headerPrefixWidth4: "12px",
    headerPrefixWidth5: "12px",
    headerPrefixWidth6: "12px",
    headerBarWidth1: "4px",
    headerBarWidth2: "4px",
    headerBarWidth3: "3px",
    headerBarWidth4: "3px",
    headerBarWidth5: "3px",
    headerBarWidth6: "3px",
    pMargin: "16px 0 16px 0",
    liMargin: ".25em 0 0 0",
    olPadding: "0 0 0 2em",
    ulPadding: "0 0 0 2em"
  };
  function self$1(vars) {
    const {
      primaryColor,
      textColor2,
      borderColor,
      lineHeight: lineHeight2,
      fontSize: fontSize2,
      borderRadiusSmall,
      dividerColor,
      fontWeightStrong,
      textColor1,
      textColor3,
      infoColor,
      warningColor,
      errorColor,
      successColor,
      codeColor
    } = vars;
    return Object.assign(Object.assign({}, commonVars), {
      aTextColor: primaryColor,
      blockquoteTextColor: textColor2,
      blockquotePrefixColor: borderColor,
      blockquoteLineHeight: lineHeight2,
      blockquoteFontSize: fontSize2,
      codeBorderRadius: borderRadiusSmall,
      liTextColor: textColor2,
      liLineHeight: lineHeight2,
      liFontSize: fontSize2,
      hrColor: dividerColor,
      headerFontWeight: fontWeightStrong,
      headerTextColor: textColor1,
      pTextColor: textColor2,
      pTextColor1Depth: textColor1,
      pTextColor2Depth: textColor2,
      pTextColor3Depth: textColor3,
      pLineHeight: lineHeight2,
      pFontSize: fontSize2,
      headerBarColor: primaryColor,
      headerBarColorPrimary: primaryColor,
      headerBarColorInfo: infoColor,
      headerBarColorError: errorColor,
      headerBarColorWarning: warningColor,
      headerBarColorSuccess: successColor,
      textColor: textColor2,
      textColor1Depth: textColor1,
      textColor2Depth: textColor2,
      textColor3Depth: textColor3,
      textColorPrimary: primaryColor,
      textColorInfo: infoColor,
      textColorSuccess: successColor,
      textColorWarning: warningColor,
      textColorError: errorColor,
      codeTextColor: textColor2,
      codeColor,
      codeBorder: "1px solid #0000"
    });
  }
  const typographyLight = {
    common: derived,
    self: self$1
  };
  const defaultSpan$1 = 1;
  const gridInjectionKey = createInjectionKey("n-grid");
  const defaultSpan = 1;
  const gridItemProps = {
    span: {
      type: [Number, String],
      default: defaultSpan
    },
    offset: {
      type: [Number, String],
      default: 0
    },
    suffix: Boolean,
    // private props
    privateOffset: Number,
    privateSpan: Number,
    privateColStart: Number,
    privateShow: {
      type: Boolean,
      default: true
    }
  };
  const NGi = /* @__PURE__ */ defineComponent({
    __GRID_ITEM__: true,
    name: "GridItem",
    alias: ["Gi"],
    props: gridItemProps,
    setup() {
      const {
        isSsrRef,
        xGapRef,
        itemStyleRef,
        overflowRef,
        layoutShiftDisabledRef
      } = inject(gridInjectionKey);
      const self2 = getCurrentInstance();
      return {
        overflow: overflowRef,
        itemStyle: itemStyleRef,
        layoutShiftDisabled: layoutShiftDisabledRef,
        mergedXGap: computed(() => {
          return pxfy(xGapRef.value || 0);
        }),
        deriveStyle: () => {
          void isSsrRef.value;
          const {
            privateSpan = defaultSpan,
            privateShow = true,
            privateColStart = void 0,
            privateOffset = 0
          } = self2.vnode.props;
          const {
            value: xGap
          } = xGapRef;
          const mergedXGap = pxfy(xGap || 0);
          return {
            display: !privateShow ? "none" : "",
            gridColumn: `${privateColStart !== null && privateColStart !== void 0 ? privateColStart : `span ${privateSpan}`} / span ${privateSpan}`,
            marginLeft: privateOffset ? `calc((100% - (${privateSpan} - 1) * ${mergedXGap}) / ${privateSpan} * ${privateOffset} + ${mergedXGap} * ${privateOffset})` : ""
          };
        }
      };
    },
    render() {
      var _a2, _b;
      if (this.layoutShiftDisabled) {
        const {
          span,
          offset,
          mergedXGap
        } = this;
        return h("div", {
          style: {
            gridColumn: `span ${span} / span ${span}`,
            marginLeft: offset ? `calc((100% - (${span} - 1) * ${mergedXGap}) / ${span} * ${offset} + ${mergedXGap} * ${offset})` : ""
          }
        }, this.$slots);
      }
      return h("div", {
        style: [this.itemStyle, this.deriveStyle()]
      }, (_b = (_a2 = this.$slots).default) === null || _b === void 0 ? void 0 : _b.call(_a2, {
        overflow: this.overflow
      }));
    }
  });
  const defaultBreakpoints = {
    xs: 0,
    // mobile
    s: 640,
    // tablet
    m: 1024,
    // laptop s
    l: 1280,
    // laptop
    xl: 1536,
    // laptop l
    xxl: 1920
    // normal desktop display
  };
  const defaultCols = 24;
  const SSR_ATTR_NAME = "__ssr__";
  const gridProps = {
    layoutShiftDisabled: Boolean,
    responsive: {
      type: [String, Boolean],
      default: "self"
    },
    cols: {
      type: [Number, String],
      default: defaultCols
    },
    itemResponsive: Boolean,
    collapsed: Boolean,
    // may create grid rows < collapsedRows since a item may take all the row
    collapsedRows: {
      type: Number,
      default: 1
    },
    itemStyle: [Object, String],
    xGap: {
      type: [Number, String],
      default: 0
    },
    yGap: {
      type: [Number, String],
      default: 0
    }
  };
  const NGrid = /* @__PURE__ */ defineComponent({
    name: "Grid",
    inheritAttrs: false,
    props: gridProps,
    setup(props) {
      const {
        mergedClsPrefixRef,
        mergedBreakpointsRef
      } = useConfig(props);
      const numRegex = /^\d+$/;
      const widthRef = /* @__PURE__ */ ref(void 0);
      const breakpointsRef = useBreakpoints((mergedBreakpointsRef === null || mergedBreakpointsRef === void 0 ? void 0 : mergedBreakpointsRef.value) || defaultBreakpoints);
      const isResponsiveRef = useMemo(() => {
        if (props.itemResponsive) return true;
        if (!numRegex.test(props.cols.toString())) return true;
        if (!numRegex.test(props.xGap.toString())) return true;
        if (!numRegex.test(props.yGap.toString())) return true;
        return false;
      });
      const responsiveQueryRef = computed(() => {
        if (!isResponsiveRef.value) return void 0;
        return props.responsive === "self" ? widthRef.value : breakpointsRef.value;
      });
      const responsiveColsRef = useMemo(() => {
        var _a2;
        return (_a2 = Number(parseResponsivePropValue(props.cols.toString(), responsiveQueryRef.value))) !== null && _a2 !== void 0 ? _a2 : defaultCols;
      });
      const responsiveXGapRef = useMemo(() => parseResponsivePropValue(props.xGap.toString(), responsiveQueryRef.value));
      const responsiveYGapRef = useMemo(() => parseResponsivePropValue(props.yGap.toString(), responsiveQueryRef.value));
      const handleResize = (entry) => {
        widthRef.value = entry.contentRect.width;
      };
      const handleResizeRaf = (entry) => {
        beforeNextFrameOnce(handleResize, entry);
      };
      const overflowRef = /* @__PURE__ */ ref(false);
      const handleResizeRef = computed(() => {
        if (props.responsive === "self") {
          return handleResizeRaf;
        }
        return void 0;
      });
      const isSsrRef = /* @__PURE__ */ ref(false);
      const contentElRef = /* @__PURE__ */ ref();
      onMounted(() => {
        const {
          value: contentEl
        } = contentElRef;
        if (contentEl) {
          if (contentEl.hasAttribute(SSR_ATTR_NAME)) {
            contentEl.removeAttribute(SSR_ATTR_NAME);
            isSsrRef.value = true;
          }
        }
      });
      provide(gridInjectionKey, {
        layoutShiftDisabledRef: /* @__PURE__ */ toRef(props, "layoutShiftDisabled"),
        isSsrRef,
        itemStyleRef: /* @__PURE__ */ toRef(props, "itemStyle"),
        xGapRef: responsiveXGapRef,
        overflowRef
      });
      return {
        isSsr: !isBrowser$1,
        contentEl: contentElRef,
        mergedClsPrefix: mergedClsPrefixRef,
        style: computed(() => {
          if (props.layoutShiftDisabled) {
            return {
              width: "100%",
              display: "grid",
              gridTemplateColumns: `repeat(${props.cols}, minmax(0, 1fr))`,
              columnGap: pxfy(props.xGap),
              rowGap: pxfy(props.yGap)
            };
          }
          return {
            width: "100%",
            display: "grid",
            gridTemplateColumns: `repeat(${responsiveColsRef.value}, minmax(0, 1fr))`,
            columnGap: pxfy(responsiveXGapRef.value),
            rowGap: pxfy(responsiveYGapRef.value)
          };
        }),
        isResponsive: isResponsiveRef,
        responsiveQuery: responsiveQueryRef,
        responsiveCols: responsiveColsRef,
        handleResize: handleResizeRef,
        overflow: overflowRef
      };
    },
    render() {
      if (this.layoutShiftDisabled) {
        return h("div", mergeProps({
          ref: "contentEl",
          class: `${this.mergedClsPrefix}-grid`,
          style: this.style
        }, this.$attrs), this.$slots);
      }
      const renderContent = () => {
        var _a2, _b, _c, _d, _e, _f, _g;
        this.overflow = false;
        const rawChildren = flatten$1(getSlot(this));
        const childrenAndRawSpan = [];
        const {
          collapsed,
          collapsedRows,
          responsiveCols,
          responsiveQuery
        } = this;
        rawChildren.forEach((child) => {
          var _a3, _b2, _c2, _d2, _e2;
          if (((_a3 = child === null || child === void 0 ? void 0 : child.type) === null || _a3 === void 0 ? void 0 : _a3.__GRID_ITEM__) !== true) return;
          if (isNodeVShowFalse(child)) {
            const clonedNode = cloneVNode(child);
            if (clonedNode.props) {
              clonedNode.props.privateShow = false;
            } else {
              clonedNode.props = {
                privateShow: false
              };
            }
            childrenAndRawSpan.push({
              child: clonedNode,
              rawChildSpan: 0
            });
            return;
          }
          child.dirs = ((_b2 = child.dirs) === null || _b2 === void 0 ? void 0 : _b2.filter(({
            dir
          }) => dir !== vShow)) || null;
          if (((_c2 = child.dirs) === null || _c2 === void 0 ? void 0 : _c2.length) === 0) {
            child.dirs = null;
          }
          const clonedChild = cloneVNode(child);
          const rawChildSpan = Number((_e2 = parseResponsivePropValue((_d2 = clonedChild.props) === null || _d2 === void 0 ? void 0 : _d2.span, responsiveQuery)) !== null && _e2 !== void 0 ? _e2 : defaultSpan$1);
          if (rawChildSpan === 0) return;
          childrenAndRawSpan.push({
            child: clonedChild,
            rawChildSpan
          });
        });
        let suffixSpan = 0;
        const maybeSuffixNode = (_a2 = childrenAndRawSpan[childrenAndRawSpan.length - 1]) === null || _a2 === void 0 ? void 0 : _a2.child;
        if (maybeSuffixNode === null || maybeSuffixNode === void 0 ? void 0 : maybeSuffixNode.props) {
          const suffixPropValue = (_b = maybeSuffixNode.props) === null || _b === void 0 ? void 0 : _b.suffix;
          if (suffixPropValue !== void 0 && suffixPropValue !== false) {
            suffixSpan = Number((_d = parseResponsivePropValue((_c = maybeSuffixNode.props) === null || _c === void 0 ? void 0 : _c.span, responsiveQuery)) !== null && _d !== void 0 ? _d : defaultSpan$1);
            maybeSuffixNode.props.privateSpan = suffixSpan;
            maybeSuffixNode.props.privateColStart = responsiveCols + 1 - suffixSpan;
            maybeSuffixNode.props.privateShow = (_e = maybeSuffixNode.props.privateShow) !== null && _e !== void 0 ? _e : true;
          }
        }
        let spanCounter = 0;
        let done = false;
        for (const {
          child,
          rawChildSpan
        } of childrenAndRawSpan) {
          if (done) {
            this.overflow = true;
          }
          if (!done) {
            const childOffset = Number((_g = parseResponsivePropValue((_f = child.props) === null || _f === void 0 ? void 0 : _f.offset, responsiveQuery)) !== null && _g !== void 0 ? _g : 0);
            const childSpan = Math.min(rawChildSpan + childOffset, responsiveCols);
            if (!child.props) {
              child.props = {
                privateSpan: childSpan,
                privateOffset: childOffset
              };
            } else {
              child.props.privateSpan = childSpan;
              child.props.privateOffset = childOffset;
            }
            if (collapsed) {
              const remainder = spanCounter % responsiveCols;
              if (childSpan + remainder > responsiveCols) {
                spanCounter += responsiveCols - remainder;
              }
              if (childSpan + spanCounter + suffixSpan > collapsedRows * responsiveCols) {
                done = true;
              } else {
                spanCounter += childSpan;
              }
            }
          }
          if (done) {
            if (child.props) {
              if (child.props.privateShow !== true) {
                child.props.privateShow = false;
              }
            } else {
              child.props = {
                privateShow: false
              };
            }
          }
        }
        return h("div", mergeProps({
          ref: "contentEl",
          class: `${this.mergedClsPrefix}-grid`,
          style: this.style,
          [SSR_ATTR_NAME]: this.isSsr || void 0
        }, this.$attrs), childrenAndRawSpan.map(({
          child
        }) => child));
      };
      return this.isResponsive && this.responsive === "self" ? h(VResizeObserver, {
        onResize: this.handleResize
      }, {
        default: renderContent
      }) : renderContent();
    }
  });
  const layoutSiderInjectionKey = createInjectionKey("n-layout-sider");
  const positionProp = {
    type: String,
    default: "static"
  };
  const style$6 = cB("layout", `
 color: var(--n-text-color);
 background-color: var(--n-color);
 box-sizing: border-box;
 position: relative;
 z-index: auto;
 flex: auto;
 overflow: hidden;
 transition:
 box-shadow .3s var(--n-bezier),
 background-color .3s var(--n-bezier),
 color .3s var(--n-bezier);
`, [cB("layout-scroll-container", `
 overflow-x: hidden;
 box-sizing: border-box;
 height: 100%;
 `), cM("absolute-positioned", `
 position: absolute;
 left: 0;
 right: 0;
 top: 0;
 bottom: 0;
 `)]);
  const layoutProps = {
    embedded: Boolean,
    position: positionProp,
    nativeScrollbar: {
      type: Boolean,
      default: true
    },
    scrollbarProps: Object,
    onScroll: Function,
    contentClass: String,
    contentStyle: {
      type: [String, Object],
      default: ""
    },
    hasSider: Boolean,
    siderPlacement: {
      type: String,
      default: "left"
    }
  };
  const layoutInjectionKey = createInjectionKey("n-layout");
  function createLayoutComponent(isContent) {
    return /* @__PURE__ */ defineComponent({
      name: isContent ? "LayoutContent" : "Layout",
      props: Object.assign(Object.assign({}, useTheme.props), layoutProps),
      setup(props) {
        const scrollableElRef = /* @__PURE__ */ ref(null);
        const scrollbarInstRef = /* @__PURE__ */ ref(null);
        const {
          mergedClsPrefixRef,
          inlineThemeDisabled
        } = useConfig(props);
        const themeRef = useTheme("Layout", "-layout", style$6, layoutLight, props, mergedClsPrefixRef);
        function scrollTo(options, y) {
          if (props.nativeScrollbar) {
            const {
              value: scrollableEl
            } = scrollableElRef;
            if (scrollableEl) {
              if (y === void 0) {
                scrollableEl.scrollTo(options);
              } else {
                scrollableEl.scrollTo(options, y);
              }
            }
          } else {
            const {
              value: scrollbarInst
            } = scrollbarInstRef;
            if (scrollbarInst) {
              scrollbarInst.scrollTo(options, y);
            }
          }
        }
        provide(layoutInjectionKey, props);
        let scrollX = 0;
        let scrollY = 0;
        const handleNativeElScroll = (e) => {
          var _a2;
          const target = e.target;
          scrollX = target.scrollLeft;
          scrollY = target.scrollTop;
          (_a2 = props.onScroll) === null || _a2 === void 0 ? void 0 : _a2.call(props, e);
        };
        useReactivated(() => {
          if (props.nativeScrollbar) {
            const el = scrollableElRef.value;
            if (el) {
              el.scrollTop = scrollY;
              el.scrollLeft = scrollX;
            }
          }
        });
        const hasSiderStyle = {
          display: "flex",
          flexWrap: "nowrap",
          width: "100%",
          flexDirection: "row"
        };
        const exposedMethods = {
          scrollTo
        };
        const cssVarsRef = computed(() => {
          const {
            common: {
              cubicBezierEaseInOut: cubicBezierEaseInOut2
            },
            self: self2
          } = themeRef.value;
          return {
            "--n-bezier": cubicBezierEaseInOut2,
            "--n-color": props.embedded ? self2.colorEmbedded : self2.color,
            "--n-text-color": self2.textColor
          };
        });
        const themeClassHandle = inlineThemeDisabled ? useThemeClass("layout", computed(() => {
          return props.embedded ? "e" : "";
        }), cssVarsRef, props) : void 0;
        return Object.assign({
          mergedClsPrefix: mergedClsPrefixRef,
          scrollableElRef,
          scrollbarInstRef,
          hasSiderStyle,
          mergedTheme: themeRef,
          handleNativeElScroll,
          cssVars: inlineThemeDisabled ? void 0 : cssVarsRef,
          themeClass: themeClassHandle === null || themeClassHandle === void 0 ? void 0 : themeClassHandle.themeClass,
          onRender: themeClassHandle === null || themeClassHandle === void 0 ? void 0 : themeClassHandle.onRender
        }, exposedMethods);
      },
      render() {
        var _a2;
        const {
          mergedClsPrefix,
          hasSider
        } = this;
        (_a2 = this.onRender) === null || _a2 === void 0 ? void 0 : _a2.call(this);
        const hasSiderStyle = hasSider ? this.hasSiderStyle : void 0;
        const layoutClass = [this.themeClass, isContent && `${mergedClsPrefix}-layout-content`, `${mergedClsPrefix}-layout`, `${mergedClsPrefix}-layout--${this.position}-positioned`];
        return h("div", {
          class: layoutClass,
          style: this.cssVars
        }, this.nativeScrollbar ? h("div", {
          ref: "scrollableElRef",
          class: [`${mergedClsPrefix}-layout-scroll-container`, this.contentClass],
          style: [this.contentStyle, hasSiderStyle],
          onScroll: this.handleNativeElScroll
        }, this.$slots) : h(Scrollbar, Object.assign({}, this.scrollbarProps, {
          onScroll: this.onScroll,
          ref: "scrollbarInstRef",
          theme: this.mergedTheme.peers.Scrollbar,
          themeOverrides: this.mergedTheme.peerOverrides.Scrollbar,
          contentClass: this.contentClass,
          contentStyle: [this.contentStyle, hasSiderStyle]
        }), this.$slots));
      }
    });
  }
  const NLayout = createLayoutComponent(false);
  const NLayoutContent = createLayoutComponent(true);
  const style$5 = cB("layout-footer", `
 transition:
 box-shadow .3s var(--n-bezier),
 color .3s var(--n-bezier),
 background-color .3s var(--n-bezier),
 border-color .3s var(--n-bezier);
 color: var(--n-text-color);
 background-color: var(--n-color);
 box-sizing: border-box;
`, [cM("absolute-positioned", `
 position: absolute;
 left: 0;
 right: 0;
 bottom: 0;
 `), cM("bordered", `
 border-top: solid 1px var(--n-border-color);
 `)]);
  const layoutFooterProps = Object.assign(Object.assign({}, useTheme.props), {
    inverted: Boolean,
    position: positionProp,
    bordered: Boolean
  });
  const NLayoutFooter = /* @__PURE__ */ defineComponent({
    name: "LayoutFooter",
    props: layoutFooterProps,
    setup(props) {
      const {
        mergedClsPrefixRef,
        inlineThemeDisabled
      } = useConfig(props);
      const themeRef = useTheme("Layout", "-layout-footer", style$5, layoutLight, props, mergedClsPrefixRef);
      const cssVarsRef = computed(() => {
        const {
          common: {
            cubicBezierEaseInOut: cubicBezierEaseInOut2
          },
          self: self2
        } = themeRef.value;
        const vars = {
          "--n-bezier": cubicBezierEaseInOut2
        };
        if (props.inverted) {
          vars["--n-color"] = self2.footerColorInverted;
          vars["--n-text-color"] = self2.textColorInverted;
          vars["--n-border-color"] = self2.footerBorderColorInverted;
        } else {
          vars["--n-color"] = self2.footerColor;
          vars["--n-text-color"] = self2.textColor;
          vars["--n-border-color"] = self2.footerBorderColor;
        }
        return vars;
      });
      const themeClassHandle = inlineThemeDisabled ? useThemeClass("layout-footer", computed(() => props.inverted ? "a" : "b"), cssVarsRef, props) : void 0;
      return {
        mergedClsPrefix: mergedClsPrefixRef,
        cssVars: inlineThemeDisabled ? void 0 : cssVarsRef,
        themeClass: themeClassHandle === null || themeClassHandle === void 0 ? void 0 : themeClassHandle.themeClass,
        onRender: themeClassHandle === null || themeClassHandle === void 0 ? void 0 : themeClassHandle.onRender
      };
    },
    render() {
      var _a2;
      const {
        mergedClsPrefix
      } = this;
      (_a2 = this.onRender) === null || _a2 === void 0 ? void 0 : _a2.call(this);
      return h("div", {
        class: [`${mergedClsPrefix}-layout-footer`, this.themeClass, this.position && `${mergedClsPrefix}-layout-footer--${this.position}-positioned`, this.bordered && `${mergedClsPrefix}-layout-footer--bordered`],
        style: this.cssVars
      }, this.$slots);
    }
  });
  const style$4 = cB("layout-header", `
 transition:
 color .3s var(--n-bezier),
 background-color .3s var(--n-bezier),
 box-shadow .3s var(--n-bezier),
 border-color .3s var(--n-bezier);
 box-sizing: border-box;
 width: 100%;
 background-color: var(--n-color);
 color: var(--n-text-color);
`, [cM("absolute-positioned", `
 position: absolute;
 left: 0;
 right: 0;
 top: 0;
 `), cM("bordered", `
 border-bottom: solid 1px var(--n-border-color);
 `)]);
  const headerProps$1 = {
    position: positionProp,
    inverted: Boolean,
    bordered: {
      type: Boolean,
      default: false
    }
  };
  const NLayoutHeader = /* @__PURE__ */ defineComponent({
    name: "LayoutHeader",
    props: Object.assign(Object.assign({}, useTheme.props), headerProps$1),
    setup(props) {
      const {
        mergedClsPrefixRef,
        inlineThemeDisabled
      } = useConfig(props);
      const themeRef = useTheme("Layout", "-layout-header", style$4, layoutLight, props, mergedClsPrefixRef);
      const cssVarsRef = computed(() => {
        const {
          common: {
            cubicBezierEaseInOut: cubicBezierEaseInOut2
          },
          self: self2
        } = themeRef.value;
        const vars = {
          "--n-bezier": cubicBezierEaseInOut2
        };
        if (props.inverted) {
          vars["--n-color"] = self2.headerColorInverted;
          vars["--n-text-color"] = self2.textColorInverted;
          vars["--n-border-color"] = self2.headerBorderColorInverted;
        } else {
          vars["--n-color"] = self2.headerColor;
          vars["--n-text-color"] = self2.textColor;
          vars["--n-border-color"] = self2.headerBorderColor;
        }
        return vars;
      });
      const themeClassHandle = inlineThemeDisabled ? useThemeClass("layout-header", computed(() => props.inverted ? "a" : "b"), cssVarsRef, props) : void 0;
      return {
        mergedClsPrefix: mergedClsPrefixRef,
        cssVars: inlineThemeDisabled ? void 0 : cssVarsRef,
        themeClass: themeClassHandle === null || themeClassHandle === void 0 ? void 0 : themeClassHandle.themeClass,
        onRender: themeClassHandle === null || themeClassHandle === void 0 ? void 0 : themeClassHandle.onRender
      };
    },
    render() {
      var _a2;
      const {
        mergedClsPrefix
      } = this;
      (_a2 = this.onRender) === null || _a2 === void 0 ? void 0 : _a2.call(this);
      return h("div", {
        class: [`${mergedClsPrefix}-layout-header`, this.themeClass, this.position && `${mergedClsPrefix}-layout-header--${this.position}-positioned`, this.bordered && `${mergedClsPrefix}-layout-header--bordered`],
        style: this.cssVars
      }, this.$slots);
    }
  });
  const menuInjectionKey = createInjectionKey("n-menu");
  const submenuInjectionKey = createInjectionKey("n-submenu");
  const menuItemGroupInjectionKey = createInjectionKey("n-menu-item-group");
  const hoverStyleChildren = [c$1("&::before", "background-color: var(--n-item-color-hover);"), cE("arrow", `
 color: var(--n-arrow-color-hover);
 `), cE("icon", `
 color: var(--n-item-icon-color-hover);
 `), cB("menu-item-content-header", `
 color: var(--n-item-text-color-hover);
 `, [c$1("a", `
 color: var(--n-item-text-color-hover);
 `), cE("extra", `
 color: var(--n-item-text-color-hover);
 `)])];
  const horizontalHoverStyleChildren = [cE("icon", `
 color: var(--n-item-icon-color-hover-horizontal);
 `), cB("menu-item-content-header", `
 color: var(--n-item-text-color-hover-horizontal);
 `, [c$1("a", `
 color: var(--n-item-text-color-hover-horizontal);
 `), cE("extra", `
 color: var(--n-item-text-color-hover-horizontal);
 `)])];
  const style$3 = c$1([cB("menu", `
 background-color: var(--n-color);
 color: var(--n-item-text-color);
 overflow: hidden;
 transition: background-color .3s var(--n-bezier);
 box-sizing: border-box;
 font-size: var(--n-font-size);
 padding-bottom: 6px;
 `, [cM("horizontal", `
 max-width: 100%;
 width: 100%;
 display: flex;
 overflow: hidden;
 padding-bottom: 0;
 `, [cB("submenu", "margin: 0;"), cB("menu-item", "margin: 0;"), cB("menu-item-content", `
 padding: 0 20px;
 border-bottom: 2px solid #0000;
 `, [c$1("&::before", "display: none;"), cM("selected", "border-bottom: 2px solid var(--n-border-color-horizontal)")]), cB("menu-item-content", [cM("selected", [cE("icon", "color: var(--n-item-icon-color-active-horizontal);"), cB("menu-item-content-header", `
 color: var(--n-item-text-color-active-horizontal);
 `, [c$1("a", "color: var(--n-item-text-color-active-horizontal);"), cE("extra", "color: var(--n-item-text-color-active-horizontal);")])]), cM("child-active", `
 border-bottom: 2px solid var(--n-border-color-horizontal);
 `, [cB("menu-item-content-header", `
 color: var(--n-item-text-color-child-active-horizontal);
 `, [c$1("a", `
 color: var(--n-item-text-color-child-active-horizontal);
 `), cE("extra", `
 color: var(--n-item-text-color-child-active-horizontal);
 `)]), cE("icon", `
 color: var(--n-item-icon-color-child-active-horizontal);
 `)]), cNotM("disabled", [cNotM("selected, child-active", [c$1("&:focus-within", horizontalHoverStyleChildren)]), cM("selected", [hoverStyle(null, [cE("icon", "color: var(--n-item-icon-color-active-hover-horizontal);"), cB("menu-item-content-header", `
 color: var(--n-item-text-color-active-hover-horizontal);
 `, [c$1("a", "color: var(--n-item-text-color-active-hover-horizontal);"), cE("extra", "color: var(--n-item-text-color-active-hover-horizontal);")])])]), cM("child-active", [hoverStyle(null, [cE("icon", "color: var(--n-item-icon-color-child-active-hover-horizontal);"), cB("menu-item-content-header", `
 color: var(--n-item-text-color-child-active-hover-horizontal);
 `, [c$1("a", "color: var(--n-item-text-color-child-active-hover-horizontal);"), cE("extra", "color: var(--n-item-text-color-child-active-hover-horizontal);")])])]), hoverStyle("border-bottom: 2px solid var(--n-border-color-horizontal);", horizontalHoverStyleChildren)]), cB("menu-item-content-header", [c$1("a", "color: var(--n-item-text-color-horizontal);")])])]), cNotM("responsive", [cB("menu-item-content-header", `
 overflow: hidden;
 text-overflow: ellipsis;
 `)]), cM("collapsed", [cB("menu-item-content", [cM("selected", [c$1("&::before", `
 background-color: var(--n-item-color-active-collapsed) !important;
 `)]), cB("menu-item-content-header", "opacity: 0;"), cE("arrow", "opacity: 0;"), cE("icon", "color: var(--n-item-icon-color-collapsed);")])]), cB("menu-item", `
 height: var(--n-item-height);
 margin-top: 6px;
 position: relative;
 `), cB("menu-item-content", `
 box-sizing: border-box;
 line-height: 1.75;
 height: 100%;
 display: grid;
 grid-template-areas: "icon content arrow";
 grid-template-columns: auto 1fr auto;
 align-items: center;
 cursor: pointer;
 position: relative;
 padding-right: 18px;
 transition:
 background-color .3s var(--n-bezier),
 padding-left .3s var(--n-bezier),
 border-color .3s var(--n-bezier);
 `, [c$1("> *", "z-index: 1;"), c$1("&::before", `
 z-index: auto;
 content: "";
 background-color: #0000;
 position: absolute;
 left: 8px;
 right: 8px;
 top: 0;
 bottom: 0;
 pointer-events: none;
 border-radius: var(--n-border-radius);
 transition: background-color .3s var(--n-bezier);
 `), cM("disabled", `
 opacity: .45;
 cursor: not-allowed;
 `), cM("collapsed", [cE("arrow", "transform: rotate(0);")]), cM("selected", [c$1("&::before", "background-color: var(--n-item-color-active);"), cE("arrow", "color: var(--n-arrow-color-active);"), cE("icon", "color: var(--n-item-icon-color-active);"), cB("menu-item-content-header", `
 color: var(--n-item-text-color-active);
 `, [c$1("a", "color: var(--n-item-text-color-active);"), cE("extra", "color: var(--n-item-text-color-active);")])]), cM("child-active", [cB("menu-item-content-header", `
 color: var(--n-item-text-color-child-active);
 `, [c$1("a", `
 color: var(--n-item-text-color-child-active);
 `), cE("extra", `
 color: var(--n-item-text-color-child-active);
 `)]), cE("arrow", `
 color: var(--n-arrow-color-child-active);
 `), cE("icon", `
 color: var(--n-item-icon-color-child-active);
 `)]), cNotM("disabled", [cNotM("selected, child-active", [c$1("&:focus-within", hoverStyleChildren)]), cM("selected", [hoverStyle(null, [cE("arrow", "color: var(--n-arrow-color-active-hover);"), cE("icon", "color: var(--n-item-icon-color-active-hover);"), cB("menu-item-content-header", `
 color: var(--n-item-text-color-active-hover);
 `, [c$1("a", "color: var(--n-item-text-color-active-hover);"), cE("extra", "color: var(--n-item-text-color-active-hover);")])])]), cM("child-active", [hoverStyle(null, [cE("arrow", "color: var(--n-arrow-color-child-active-hover);"), cE("icon", "color: var(--n-item-icon-color-child-active-hover);"), cB("menu-item-content-header", `
 color: var(--n-item-text-color-child-active-hover);
 `, [c$1("a", "color: var(--n-item-text-color-child-active-hover);"), cE("extra", "color: var(--n-item-text-color-child-active-hover);")])])]), cM("selected", [hoverStyle(null, [c$1("&::before", "background-color: var(--n-item-color-active-hover);")])]), hoverStyle(null, hoverStyleChildren)]), cE("icon", `
 grid-area: icon;
 color: var(--n-item-icon-color);
 transition:
 color .3s var(--n-bezier),
 font-size .3s var(--n-bezier),
 margin-right .3s var(--n-bezier);
 box-sizing: content-box;
 display: inline-flex;
 align-items: center;
 justify-content: center;
 `), cE("arrow", `
 grid-area: arrow;
 font-size: 16px;
 color: var(--n-arrow-color);
 transform: rotate(180deg);
 opacity: 1;
 transition:
 color .3s var(--n-bezier),
 transform 0.2s var(--n-bezier),
 opacity 0.2s var(--n-bezier);
 `), cB("menu-item-content-header", `
 grid-area: content;
 transition:
 color .3s var(--n-bezier),
 opacity .3s var(--n-bezier);
 opacity: 1;
 white-space: nowrap;
 color: var(--n-item-text-color);
 `, [c$1("a", `
 outline: none;
 text-decoration: none;
 transition: color .3s var(--n-bezier);
 color: var(--n-item-text-color);
 `, [c$1("&::before", `
 content: "";
 position: absolute;
 left: 0;
 right: 0;
 top: 0;
 bottom: 0;
 `)]), cE("extra", `
 font-size: .93em;
 color: var(--n-group-text-color);
 transition: color .3s var(--n-bezier);
 `)])]), cB("submenu", `
 cursor: pointer;
 position: relative;
 margin-top: 6px;
 `, [cB("menu-item-content", `
 height: var(--n-item-height);
 `), cB("submenu-children", `
 overflow: hidden;
 padding: 0;
 `, [fadeInHeightExpandTransition({
    duration: ".2s"
  })])]), cB("menu-item-group", [cB("menu-item-group-title", `
 margin-top: 6px;
 color: var(--n-group-text-color);
 cursor: default;
 font-size: .93em;
 height: 36px;
 display: flex;
 align-items: center;
 transition:
 padding-left .3s var(--n-bezier),
 color .3s var(--n-bezier);
 `)])]), cB("menu-tooltip", [c$1("a", `
 color: inherit;
 text-decoration: none;
 `)]), cB("menu-divider", `
 transition: background-color .3s var(--n-bezier);
 background-color: var(--n-divider-color);
 height: 1px;
 margin: 6px 18px;
 `)]);
  function hoverStyle(props, children) {
    return [cM("hover", props, children), c$1("&:hover", props, children)];
  }
  const NMenuOptionContent = /* @__PURE__ */ defineComponent({
    name: "MenuOptionContent",
    props: {
      collapsed: Boolean,
      disabled: Boolean,
      title: [String, Function],
      icon: Function,
      extra: [String, Function],
      showArrow: Boolean,
      childActive: Boolean,
      hover: Boolean,
      paddingLeft: Number,
      selected: Boolean,
      maxIconSize: {
        type: Number,
        required: true
      },
      activeIconSize: {
        type: Number,
        required: true
      },
      iconMarginRight: {
        type: Number,
        required: true
      },
      clsPrefix: {
        type: String,
        required: true
      },
      onClick: Function,
      tmNode: {
        type: Object,
        required: true
      },
      isEllipsisPlaceholder: Boolean
    },
    setup(props) {
      const {
        props: menuProps2
      } = inject(menuInjectionKey);
      return {
        menuProps: menuProps2,
        style: computed(() => {
          const {
            paddingLeft
          } = props;
          return {
            paddingLeft: paddingLeft && `${paddingLeft}px`
          };
        }),
        iconStyle: computed(() => {
          const {
            maxIconSize,
            activeIconSize,
            iconMarginRight
          } = props;
          return {
            width: `${maxIconSize}px`,
            height: `${maxIconSize}px`,
            fontSize: `${activeIconSize}px`,
            marginRight: `${iconMarginRight}px`
          };
        })
      };
    },
    render() {
      const {
        clsPrefix,
        tmNode,
        menuProps: {
          renderIcon,
          renderLabel,
          renderExtra,
          expandIcon
        }
      } = this;
      const icon = renderIcon ? renderIcon(tmNode.rawNode) : render(this.icon);
      return h("div", {
        onClick: (e) => {
          var _a2;
          (_a2 = this.onClick) === null || _a2 === void 0 ? void 0 : _a2.call(this, e);
        },
        role: "none",
        class: [`${clsPrefix}-menu-item-content`, {
          [`${clsPrefix}-menu-item-content--selected`]: this.selected,
          [`${clsPrefix}-menu-item-content--collapsed`]: this.collapsed,
          [`${clsPrefix}-menu-item-content--child-active`]: this.childActive,
          [`${clsPrefix}-menu-item-content--disabled`]: this.disabled,
          [`${clsPrefix}-menu-item-content--hover`]: this.hover
        }],
        style: this.style
      }, icon && h("div", {
        class: `${clsPrefix}-menu-item-content__icon`,
        style: this.iconStyle,
        role: "none"
      }, [icon]), h("div", {
        class: `${clsPrefix}-menu-item-content-header`,
        role: "none"
      }, this.isEllipsisPlaceholder ? this.title : renderLabel ? renderLabel(tmNode.rawNode) : render(this.title), this.extra || renderExtra ? h("span", {
        class: `${clsPrefix}-menu-item-content-header__extra`
      }, " ", renderExtra ? renderExtra(tmNode.rawNode) : render(this.extra)) : null), this.showArrow ? h(NBaseIcon, {
        ariaHidden: true,
        class: `${clsPrefix}-menu-item-content__arrow`,
        clsPrefix
      }, {
        default: () => expandIcon ? expandIcon(tmNode.rawNode) : h(ChevronDownFilledIcon, null)
      }) : null);
    }
  });
  const ICON_MARGIN_RIGHT = 8;
  function useMenuChild(props) {
    const NMenu2 = inject(menuInjectionKey);
    const {
      props: menuProps2,
      mergedCollapsedRef
    } = NMenu2;
    const NSubmenu2 = inject(submenuInjectionKey, null);
    const NMenuOptionGroup2 = inject(menuItemGroupInjectionKey, null);
    const horizontalRef = computed(() => {
      return menuProps2.mode === "horizontal";
    });
    const dropdownPlacementRef = computed(() => {
      if (horizontalRef.value) {
        return menuProps2.dropdownPlacement;
      }
      if ("tmNodes" in props) return "right-start";
      return "right";
    });
    const maxIconSizeRef = computed(() => {
      var _a2;
      return Math.max((_a2 = menuProps2.collapsedIconSize) !== null && _a2 !== void 0 ? _a2 : menuProps2.iconSize, menuProps2.iconSize);
    });
    const activeIconSizeRef = computed(() => {
      var _a2;
      if (!horizontalRef.value && props.root && mergedCollapsedRef.value) {
        return (_a2 = menuProps2.collapsedIconSize) !== null && _a2 !== void 0 ? _a2 : menuProps2.iconSize;
      } else {
        return menuProps2.iconSize;
      }
    });
    const paddingLeftRef = computed(() => {
      if (horizontalRef.value) return void 0;
      const {
        collapsedWidth,
        indent,
        rootIndent
      } = menuProps2;
      const {
        root: root2,
        isGroup: isGroup2
      } = props;
      const mergedRootIndent = rootIndent === void 0 ? indent : rootIndent;
      if (root2) {
        if (mergedCollapsedRef.value) {
          return collapsedWidth / 2 - maxIconSizeRef.value / 2;
        }
        return mergedRootIndent;
      }
      if (NMenuOptionGroup2 && typeof NMenuOptionGroup2.paddingLeftRef.value === "number") {
        return indent / 2 + NMenuOptionGroup2.paddingLeftRef.value;
      }
      if (NSubmenu2 && typeof NSubmenu2.paddingLeftRef.value === "number") {
        return (isGroup2 ? indent / 2 : indent) + NSubmenu2.paddingLeftRef.value;
      }
      return 0;
    });
    const iconMarginRightRef = computed(() => {
      const {
        collapsedWidth,
        indent,
        rootIndent
      } = menuProps2;
      const {
        value: maxIconSize
      } = maxIconSizeRef;
      const {
        root: root2
      } = props;
      if (horizontalRef.value) return ICON_MARGIN_RIGHT;
      if (!root2) return ICON_MARGIN_RIGHT;
      if (!mergedCollapsedRef.value) return ICON_MARGIN_RIGHT;
      const mergedRootIndent = rootIndent === void 0 ? indent : rootIndent;
      return mergedRootIndent + maxIconSize + ICON_MARGIN_RIGHT - (collapsedWidth + maxIconSize) / 2;
    });
    return {
      dropdownPlacement: dropdownPlacementRef,
      activeIconSize: activeIconSizeRef,
      maxIconSize: maxIconSizeRef,
      paddingLeft: paddingLeftRef,
      iconMarginRight: iconMarginRightRef,
      NMenu: NMenu2,
      NSubmenu: NSubmenu2,
      NMenuOptionGroup: NMenuOptionGroup2
    };
  }
  const useMenuChildProps = {
    internalKey: {
      type: [String, Number],
      required: true
    },
    root: Boolean,
    isGroup: Boolean,
    level: {
      type: Number,
      required: true
    },
    title: [String, Function],
    extra: [String, Function]
  };
  const NMenuDivider = /* @__PURE__ */ defineComponent({
    name: "MenuDivider",
    setup() {
      const NMenu2 = inject(menuInjectionKey);
      const {
        mergedClsPrefixRef,
        isHorizontalRef
      } = NMenu2;
      return () => isHorizontalRef.value ? null : h("div", {
        class: `${mergedClsPrefixRef.value}-menu-divider`
      });
    }
  });
  const menuItemProps = Object.assign(Object.assign({}, useMenuChildProps), {
    tmNode: {
      type: Object,
      required: true
    },
    disabled: Boolean,
    icon: Function,
    onClick: Function
  });
  const menuItemPropKeys = keysOf(menuItemProps);
  const NMenuOption = /* @__PURE__ */ defineComponent({
    name: "MenuOption",
    props: menuItemProps,
    setup(props) {
      const MenuChild = useMenuChild(props);
      const {
        NSubmenu: NSubmenu2,
        NMenu: NMenu2,
        NMenuOptionGroup: NMenuOptionGroup2
      } = MenuChild;
      const {
        props: menuProps2,
        mergedClsPrefixRef,
        mergedCollapsedRef
      } = NMenu2;
      const parentDisabledRef = NSubmenu2 ? NSubmenu2.mergedDisabledRef : NMenuOptionGroup2 ? NMenuOptionGroup2.mergedDisabledRef : {
        value: false
      };
      const mergedDisabledRef = computed(() => {
        return parentDisabledRef.value || props.disabled;
      });
      function doClick(e) {
        const {
          onClick
        } = props;
        if (onClick) onClick(e);
      }
      function handleClick(e) {
        if (!mergedDisabledRef.value) {
          NMenu2.doSelect(props.internalKey, props.tmNode.rawNode);
          doClick(e);
        }
      }
      return {
        mergedClsPrefix: mergedClsPrefixRef,
        dropdownPlacement: MenuChild.dropdownPlacement,
        paddingLeft: MenuChild.paddingLeft,
        iconMarginRight: MenuChild.iconMarginRight,
        maxIconSize: MenuChild.maxIconSize,
        activeIconSize: MenuChild.activeIconSize,
        mergedTheme: NMenu2.mergedThemeRef,
        menuProps: menuProps2,
        dropdownEnabled: useMemo(() => {
          return props.root && mergedCollapsedRef.value && menuProps2.mode !== "horizontal" && !mergedDisabledRef.value;
        }),
        selected: useMemo(() => {
          if (NMenu2.mergedValueRef.value === props.internalKey) return true;
          return false;
        }),
        mergedDisabled: mergedDisabledRef,
        handleClick
      };
    },
    render() {
      const {
        mergedClsPrefix,
        mergedTheme,
        tmNode,
        menuProps: {
          renderLabel,
          nodeProps
        }
      } = this;
      const attrs = nodeProps === null || nodeProps === void 0 ? void 0 : nodeProps(tmNode.rawNode);
      return h("div", Object.assign({}, attrs, {
        role: "menuitem",
        class: [`${mergedClsPrefix}-menu-item`, attrs === null || attrs === void 0 ? void 0 : attrs.class]
      }), h(NTooltip, {
        theme: mergedTheme.peers.Tooltip,
        themeOverrides: mergedTheme.peerOverrides.Tooltip,
        trigger: "hover",
        placement: this.dropdownPlacement,
        disabled: !this.dropdownEnabled || this.title === void 0,
        internalExtraClass: ["menu-tooltip"]
      }, {
        default: () => renderLabel ? renderLabel(tmNode.rawNode) : render(this.title),
        trigger: () => h(NMenuOptionContent, {
          tmNode,
          clsPrefix: mergedClsPrefix,
          paddingLeft: this.paddingLeft,
          iconMarginRight: this.iconMarginRight,
          maxIconSize: this.maxIconSize,
          activeIconSize: this.activeIconSize,
          selected: this.selected,
          title: this.title,
          extra: this.extra,
          disabled: this.mergedDisabled,
          icon: this.icon,
          onClick: this.handleClick
        })
      }));
    }
  });
  const menuItemGroupProps = Object.assign(Object.assign({}, useMenuChildProps), {
    tmNode: {
      type: Object,
      required: true
    },
    tmNodes: {
      type: Array,
      required: true
    }
  });
  const menuItemGroupPropKeys = keysOf(menuItemGroupProps);
  const NMenuOptionGroup = /* @__PURE__ */ defineComponent({
    name: "MenuOptionGroup",
    props: menuItemGroupProps,
    setup(props) {
      const MenuChild = useMenuChild(props);
      const {
        NSubmenu: NSubmenu2
      } = MenuChild;
      const mergedDisabledRef = computed(() => {
        if (NSubmenu2 === null || NSubmenu2 === void 0 ? void 0 : NSubmenu2.mergedDisabledRef.value) return true;
        return props.tmNode.disabled;
      });
      provide(menuItemGroupInjectionKey, {
        paddingLeftRef: MenuChild.paddingLeft,
        mergedDisabledRef
      });
      const {
        mergedClsPrefixRef,
        props: menuProps2
      } = inject(menuInjectionKey);
      return function() {
        const {
          value: mergedClsPrefix
        } = mergedClsPrefixRef;
        const paddingLeft = MenuChild.paddingLeft.value;
        const {
          nodeProps
        } = menuProps2;
        const attrs = nodeProps === null || nodeProps === void 0 ? void 0 : nodeProps(props.tmNode.rawNode);
        return h("div", {
          class: `${mergedClsPrefix}-menu-item-group`,
          role: "group"
        }, h("div", Object.assign({}, attrs, {
          class: [`${mergedClsPrefix}-menu-item-group-title`, attrs === null || attrs === void 0 ? void 0 : attrs.class],
          style: [(attrs === null || attrs === void 0 ? void 0 : attrs.style) || "", paddingLeft !== void 0 ? `padding-left: ${paddingLeft}px;` : ""]
        }), render(props.title), props.extra ? h(Fragment, null, " ", render(props.extra)) : null), h("div", null, props.tmNodes.map((tmNode) => itemRenderer(tmNode, menuProps2))));
      };
    }
  });
  function isIgnoredNode(rawNode) {
    return rawNode.type === "divider" || rawNode.type === "render";
  }
  function isDividerNode(rawNode) {
    return rawNode.type === "divider";
  }
  function itemRenderer(tmNode, menuProps2) {
    const {
      rawNode
    } = tmNode;
    const {
      show
    } = rawNode;
    if (show === false) {
      return null;
    }
    if (isIgnoredNode(rawNode)) {
      if (isDividerNode(rawNode)) {
        return h(NMenuDivider, Object.assign({
          key: tmNode.key
        }, rawNode.props));
      }
      return null;
    }
    const {
      labelField
    } = menuProps2;
    const {
      key,
      level,
      isGroup: isGroup2
    } = tmNode;
    const props = Object.assign(Object.assign({}, rawNode), {
      title: rawNode.title || rawNode[labelField],
      extra: rawNode.titleExtra || rawNode.extra,
      key,
      internalKey: key,
      // since key can't be used as a prop
      level,
      root: level === 0,
      isGroup: isGroup2
    });
    if (tmNode.children) {
      if (tmNode.isGroup) {
        return h(NMenuOptionGroup, keep(props, menuItemGroupPropKeys, {
          tmNode,
          tmNodes: tmNode.children,
          key
        }));
      }
      return h(NSubmenu, keep(props, submenuPropKeys, {
        key,
        rawNodes: rawNode[menuProps2.childrenField],
        tmNodes: tmNode.children,
        tmNode
      }));
    } else {
      return h(NMenuOption, keep(props, menuItemPropKeys, {
        key,
        tmNode
      }));
    }
  }
  const submenuProps = Object.assign(Object.assign({}, useMenuChildProps), {
    rawNodes: {
      type: Array,
      default: () => []
    },
    tmNodes: {
      type: Array,
      default: () => []
    },
    tmNode: {
      type: Object,
      required: true
    },
    disabled: Boolean,
    icon: Function,
    onClick: Function,
    domId: String,
    virtualChildActive: {
      type: Boolean,
      default: void 0
    },
    isEllipsisPlaceholder: Boolean
  });
  const submenuPropKeys = keysOf(submenuProps);
  const NSubmenu = /* @__PURE__ */ defineComponent({
    name: "Submenu",
    props: submenuProps,
    setup(props) {
      const MenuChild = useMenuChild(props);
      const {
        NMenu: NMenu2,
        NSubmenu: NSubmenu2
      } = MenuChild;
      const {
        props: menuProps2,
        mergedCollapsedRef,
        mergedThemeRef
      } = NMenu2;
      const mergedDisabledRef = computed(() => {
        const {
          disabled
        } = props;
        if (NSubmenu2 === null || NSubmenu2 === void 0 ? void 0 : NSubmenu2.mergedDisabledRef.value) return true;
        if (menuProps2.disabled) return true;
        return disabled;
      });
      const dropdownShowRef = /* @__PURE__ */ ref(false);
      provide(submenuInjectionKey, {
        paddingLeftRef: MenuChild.paddingLeft,
        mergedDisabledRef
      });
      provide(menuItemGroupInjectionKey, null);
      function doClick() {
        const {
          onClick
        } = props;
        if (onClick) onClick();
      }
      function handleClick() {
        if (!mergedDisabledRef.value) {
          if (!mergedCollapsedRef.value) {
            NMenu2.toggleExpand(props.internalKey);
          }
          doClick();
        }
      }
      function handlePopoverShowChange(value) {
        dropdownShowRef.value = value;
      }
      return {
        menuProps: menuProps2,
        mergedTheme: mergedThemeRef,
        doSelect: NMenu2.doSelect,
        inverted: NMenu2.invertedRef,
        isHorizontal: NMenu2.isHorizontalRef,
        mergedClsPrefix: NMenu2.mergedClsPrefixRef,
        maxIconSize: MenuChild.maxIconSize,
        activeIconSize: MenuChild.activeIconSize,
        iconMarginRight: MenuChild.iconMarginRight,
        dropdownPlacement: MenuChild.dropdownPlacement,
        dropdownShow: dropdownShowRef,
        paddingLeft: MenuChild.paddingLeft,
        mergedDisabled: mergedDisabledRef,
        mergedValue: NMenu2.mergedValueRef,
        childActive: useMemo(() => {
          var _a2;
          return (_a2 = props.virtualChildActive) !== null && _a2 !== void 0 ? _a2 : NMenu2.activePathRef.value.includes(props.internalKey);
        }),
        collapsed: computed(() => {
          if (menuProps2.mode === "horizontal") return false;
          if (mergedCollapsedRef.value) {
            return true;
          }
          return !NMenu2.mergedExpandedKeysRef.value.includes(props.internalKey);
        }),
        dropdownEnabled: computed(() => {
          return !mergedDisabledRef.value && (menuProps2.mode === "horizontal" || mergedCollapsedRef.value);
        }),
        handlePopoverShowChange,
        handleClick
      };
    },
    render() {
      var _a2;
      const {
        mergedClsPrefix,
        menuProps: {
          renderIcon,
          renderLabel
        }
      } = this;
      const createSubmenuItem = () => {
        const {
          isHorizontal,
          paddingLeft,
          collapsed,
          mergedDisabled,
          maxIconSize,
          activeIconSize,
          title,
          childActive,
          icon,
          handleClick,
          menuProps: {
            nodeProps
          },
          dropdownShow,
          iconMarginRight,
          tmNode,
          mergedClsPrefix: mergedClsPrefix2,
          isEllipsisPlaceholder,
          extra
        } = this;
        const attrs = nodeProps === null || nodeProps === void 0 ? void 0 : nodeProps(tmNode.rawNode);
        return h("div", Object.assign({}, attrs, {
          class: [`${mergedClsPrefix2}-menu-item`, attrs === null || attrs === void 0 ? void 0 : attrs.class],
          role: "menuitem"
        }), h(NMenuOptionContent, {
          tmNode,
          paddingLeft,
          collapsed,
          disabled: mergedDisabled,
          iconMarginRight,
          maxIconSize,
          activeIconSize,
          title,
          extra,
          showArrow: !isHorizontal,
          childActive,
          clsPrefix: mergedClsPrefix2,
          icon,
          hover: dropdownShow,
          onClick: handleClick,
          isEllipsisPlaceholder
        }));
      };
      const createSubmenuChildren = () => {
        return h(NFadeInExpandTransition, null, {
          default: () => {
            const {
              tmNodes,
              collapsed
            } = this;
            return !collapsed ? h("div", {
              class: `${mergedClsPrefix}-submenu-children`,
              role: "menu"
            }, tmNodes.map((item) => itemRenderer(item, this.menuProps))) : null;
          }
        });
      };
      return this.root ? h(NDropdown, Object.assign({
        size: "large",
        trigger: "hover"
      }, (_a2 = this.menuProps) === null || _a2 === void 0 ? void 0 : _a2.dropdownProps, {
        themeOverrides: this.mergedTheme.peerOverrides.Dropdown,
        theme: this.mergedTheme.peers.Dropdown,
        builtinThemeOverrides: {
          fontSizeLarge: "14px",
          optionIconSizeLarge: "18px"
        },
        value: this.mergedValue,
        disabled: !this.dropdownEnabled,
        placement: this.dropdownPlacement,
        keyField: this.menuProps.keyField,
        labelField: this.menuProps.labelField,
        childrenField: this.menuProps.childrenField,
        onUpdateShow: this.handlePopoverShowChange,
        options: this.rawNodes,
        onSelect: this.doSelect,
        inverted: this.inverted,
        renderIcon,
        renderLabel
      }), {
        default: () => h("div", {
          class: `${mergedClsPrefix}-submenu`,
          role: "menu",
          "aria-expanded": !this.collapsed,
          id: this.domId
        }, createSubmenuItem(), this.isHorizontal ? null : createSubmenuChildren())
      }) : h("div", {
        class: `${mergedClsPrefix}-submenu`,
        role: "menu",
        "aria-expanded": !this.collapsed,
        id: this.domId
      }, createSubmenuItem(), createSubmenuChildren());
    }
  });
  const menuProps = Object.assign(Object.assign({}, useTheme.props), {
    options: {
      type: Array,
      default: () => []
    },
    collapsed: {
      type: Boolean,
      default: void 0
    },
    collapsedWidth: {
      type: Number,
      default: 48
    },
    iconSize: {
      type: Number,
      default: 20
    },
    collapsedIconSize: {
      type: Number,
      default: 24
    },
    rootIndent: Number,
    indent: {
      type: Number,
      default: 32
    },
    labelField: {
      type: String,
      default: "label"
    },
    keyField: {
      type: String,
      default: "key"
    },
    childrenField: {
      type: String,
      default: "children"
    },
    disabledField: {
      type: String,
      default: "disabled"
    },
    defaultExpandAll: Boolean,
    defaultExpandedKeys: Array,
    expandedKeys: Array,
    value: [String, Number],
    defaultValue: {
      type: [String, Number],
      default: null
    },
    mode: {
      type: String,
      default: "vertical"
    },
    watchProps: {
      type: Array,
      default: void 0
    },
    disabled: Boolean,
    show: {
      type: Boolean,
      default: true
    },
    inverted: Boolean,
    "onUpdate:expandedKeys": [Function, Array],
    onUpdateExpandedKeys: [Function, Array],
    onUpdateValue: [Function, Array],
    "onUpdate:value": [Function, Array],
    expandIcon: Function,
    renderIcon: Function,
    renderLabel: Function,
    renderExtra: Function,
    dropdownProps: Object,
    accordion: Boolean,
    nodeProps: Function,
    dropdownPlacement: {
      type: String,
      default: "bottom"
    },
    responsive: Boolean,
    // deprecated
    items: Array,
    onOpenNamesChange: [Function, Array],
    onSelect: [Function, Array],
    onExpandedNamesChange: [Function, Array],
    expandedNames: Array,
    defaultExpandedNames: Array
  });
  const NMenu = /* @__PURE__ */ defineComponent({
    name: "Menu",
    inheritAttrs: false,
    props: menuProps,
    setup(props) {
      const {
        mergedClsPrefixRef,
        inlineThemeDisabled
      } = useConfig(props);
      const themeRef = useTheme("Menu", "-menu", style$3, menuLight, props, mergedClsPrefixRef);
      const layoutSider = inject(layoutSiderInjectionKey, null);
      const mergedCollapsedRef = computed(() => {
        var _a2;
        const {
          collapsed
        } = props;
        if (collapsed !== void 0) return collapsed;
        if (layoutSider) {
          const {
            collapseModeRef,
            collapsedRef
          } = layoutSider;
          if (collapseModeRef.value === "width") {
            return (_a2 = collapsedRef.value) !== null && _a2 !== void 0 ? _a2 : false;
          }
        }
        return false;
      });
      const treeMateRef = computed(() => {
        const {
          keyField,
          childrenField,
          disabledField
        } = props;
        return createTreeMate(props.items || props.options, {
          getIgnored(node) {
            return isIgnoredNode(node);
          },
          getChildren(node) {
            return node[childrenField];
          },
          getDisabled(node) {
            return node[disabledField];
          },
          getKey(node) {
            var _a2;
            return (_a2 = node[keyField]) !== null && _a2 !== void 0 ? _a2 : node.name;
          }
        });
      });
      const treeKeysLevelOneRef = computed(() => new Set(treeMateRef.value.treeNodes.map((e) => e.key)));
      const {
        watchProps
      } = props;
      const uncontrolledValueRef = /* @__PURE__ */ ref(null);
      if (watchProps === null || watchProps === void 0 ? void 0 : watchProps.includes("defaultValue")) {
        watchEffect(() => {
          uncontrolledValueRef.value = props.defaultValue;
        });
      } else {
        uncontrolledValueRef.value = props.defaultValue;
      }
      const controlledValueRef = /* @__PURE__ */ toRef(props, "value");
      const mergedValueRef = useMergedState(controlledValueRef, uncontrolledValueRef);
      const uncontrolledExpandedKeysRef = /* @__PURE__ */ ref([]);
      const initUncontrolledExpandedKeys = () => {
        uncontrolledExpandedKeysRef.value = props.defaultExpandAll ? treeMateRef.value.getNonLeafKeys() : props.defaultExpandedNames || props.defaultExpandedKeys || treeMateRef.value.getPath(mergedValueRef.value, {
          includeSelf: false
        }).keyPath;
      };
      if (watchProps === null || watchProps === void 0 ? void 0 : watchProps.includes("defaultExpandedKeys")) {
        watchEffect(initUncontrolledExpandedKeys);
      } else {
        initUncontrolledExpandedKeys();
      }
      const controlledExpandedKeysRef = useCompitable(props, ["expandedNames", "expandedKeys"]);
      const mergedExpandedKeysRef = useMergedState(controlledExpandedKeysRef, uncontrolledExpandedKeysRef);
      const tmNodesRef = computed(() => treeMateRef.value.treeNodes);
      const activePathRef = computed(() => {
        return treeMateRef.value.getPath(mergedValueRef.value).keyPath;
      });
      provide(menuInjectionKey, {
        props,
        mergedCollapsedRef,
        mergedThemeRef: themeRef,
        mergedValueRef,
        mergedExpandedKeysRef,
        activePathRef,
        mergedClsPrefixRef,
        isHorizontalRef: computed(() => props.mode === "horizontal"),
        invertedRef: /* @__PURE__ */ toRef(props, "inverted"),
        doSelect,
        toggleExpand
      });
      function doSelect(value, item) {
        const {
          "onUpdate:value": _onUpdateValue,
          onUpdateValue,
          onSelect
        } = props;
        if (onUpdateValue) {
          call(onUpdateValue, value, item);
        }
        if (_onUpdateValue) {
          call(_onUpdateValue, value, item);
        }
        if (onSelect) {
          call(onSelect, value, item);
        }
        uncontrolledValueRef.value = value;
      }
      function doUpdateExpandedKeys(value) {
        const {
          "onUpdate:expandedKeys": _onUpdateExpandedKeys,
          onUpdateExpandedKeys,
          onExpandedNamesChange,
          onOpenNamesChange
        } = props;
        if (_onUpdateExpandedKeys) {
          call(_onUpdateExpandedKeys, value);
        }
        if (onUpdateExpandedKeys) {
          call(onUpdateExpandedKeys, value);
        }
        if (onExpandedNamesChange) {
          call(onExpandedNamesChange, value);
        }
        if (onOpenNamesChange) {
          call(onOpenNamesChange, value);
        }
        uncontrolledExpandedKeysRef.value = value;
      }
      function toggleExpand(key) {
        const currentExpandedKeys = Array.from(mergedExpandedKeysRef.value);
        const index = currentExpandedKeys.findIndex((expanededKey) => expanededKey === key);
        if (~index) {
          currentExpandedKeys.splice(index, 1);
        } else {
          if (props.accordion) {
            if (treeKeysLevelOneRef.value.has(key)) {
              const closeKeyIndex = currentExpandedKeys.findIndex((e) => treeKeysLevelOneRef.value.has(e));
              if (closeKeyIndex > -1) {
                currentExpandedKeys.splice(closeKeyIndex, 1);
              }
            }
          }
          currentExpandedKeys.push(key);
        }
        doUpdateExpandedKeys(currentExpandedKeys);
      }
      const showOption = (key) => {
        const selectedKeyPath = treeMateRef.value.getPath(key !== null && key !== void 0 ? key : mergedValueRef.value, {
          includeSelf: false
        }).keyPath;
        if (!selectedKeyPath.length) return;
        const currentExpandedKeys = Array.from(mergedExpandedKeysRef.value);
        const nextExpandedKeys = /* @__PURE__ */ new Set([...currentExpandedKeys, ...selectedKeyPath]);
        if (props.accordion) {
          treeKeysLevelOneRef.value.forEach((firstLevelKey) => {
            if (nextExpandedKeys.has(firstLevelKey) && !selectedKeyPath.includes(firstLevelKey)) {
              nextExpandedKeys.delete(firstLevelKey);
            }
          });
        }
        doUpdateExpandedKeys(Array.from(nextExpandedKeys));
      };
      const cssVarsRef = computed(() => {
        const {
          inverted
        } = props;
        const {
          common: {
            cubicBezierEaseInOut: cubicBezierEaseInOut2
          },
          self: self2
        } = themeRef.value;
        const {
          borderRadius,
          borderColorHorizontal,
          fontSize: fontSize2,
          itemHeight,
          dividerColor
        } = self2;
        const vars = {
          "--n-divider-color": dividerColor,
          "--n-bezier": cubicBezierEaseInOut2,
          "--n-font-size": fontSize2,
          "--n-border-color-horizontal": borderColorHorizontal,
          "--n-border-radius": borderRadius,
          "--n-item-height": itemHeight
        };
        if (inverted) {
          vars["--n-group-text-color"] = self2.groupTextColorInverted;
          vars["--n-color"] = self2.colorInverted;
          vars["--n-item-text-color"] = self2.itemTextColorInverted;
          vars["--n-item-text-color-hover"] = self2.itemTextColorHoverInverted;
          vars["--n-item-text-color-active"] = self2.itemTextColorActiveInverted;
          vars["--n-item-text-color-child-active"] = self2.itemTextColorChildActiveInverted;
          vars["--n-item-text-color-child-active-hover"] = self2.itemTextColorChildActiveInverted;
          vars["--n-item-text-color-active-hover"] = self2.itemTextColorActiveHoverInverted;
          vars["--n-item-icon-color"] = self2.itemIconColorInverted;
          vars["--n-item-icon-color-hover"] = self2.itemIconColorHoverInverted;
          vars["--n-item-icon-color-active"] = self2.itemIconColorActiveInverted;
          vars["--n-item-icon-color-active-hover"] = self2.itemIconColorActiveHoverInverted;
          vars["--n-item-icon-color-child-active"] = self2.itemIconColorChildActiveInverted;
          vars["--n-item-icon-color-child-active-hover"] = self2.itemIconColorChildActiveHoverInverted;
          vars["--n-item-icon-color-collapsed"] = self2.itemIconColorCollapsedInverted;
          vars["--n-item-text-color-horizontal"] = self2.itemTextColorHorizontalInverted;
          vars["--n-item-text-color-hover-horizontal"] = self2.itemTextColorHoverHorizontalInverted;
          vars["--n-item-text-color-active-horizontal"] = self2.itemTextColorActiveHorizontalInverted;
          vars["--n-item-text-color-child-active-horizontal"] = self2.itemTextColorChildActiveHorizontalInverted;
          vars["--n-item-text-color-child-active-hover-horizontal"] = self2.itemTextColorChildActiveHoverHorizontalInverted;
          vars["--n-item-text-color-active-hover-horizontal"] = self2.itemTextColorActiveHoverHorizontalInverted;
          vars["--n-item-icon-color-horizontal"] = self2.itemIconColorHorizontalInverted;
          vars["--n-item-icon-color-hover-horizontal"] = self2.itemIconColorHoverHorizontalInverted;
          vars["--n-item-icon-color-active-horizontal"] = self2.itemIconColorActiveHorizontalInverted;
          vars["--n-item-icon-color-active-hover-horizontal"] = self2.itemIconColorActiveHoverHorizontalInverted;
          vars["--n-item-icon-color-child-active-horizontal"] = self2.itemIconColorChildActiveHorizontalInverted;
          vars["--n-item-icon-color-child-active-hover-horizontal"] = self2.itemIconColorChildActiveHoverHorizontalInverted;
          vars["--n-arrow-color"] = self2.arrowColorInverted;
          vars["--n-arrow-color-hover"] = self2.arrowColorHoverInverted;
          vars["--n-arrow-color-active"] = self2.arrowColorActiveInverted;
          vars["--n-arrow-color-active-hover"] = self2.arrowColorActiveHoverInverted;
          vars["--n-arrow-color-child-active"] = self2.arrowColorChildActiveInverted;
          vars["--n-arrow-color-child-active-hover"] = self2.arrowColorChildActiveHoverInverted;
          vars["--n-item-color-hover"] = self2.itemColorHoverInverted;
          vars["--n-item-color-active"] = self2.itemColorActiveInverted;
          vars["--n-item-color-active-hover"] = self2.itemColorActiveHoverInverted;
          vars["--n-item-color-active-collapsed"] = self2.itemColorActiveCollapsedInverted;
        } else {
          vars["--n-group-text-color"] = self2.groupTextColor;
          vars["--n-color"] = self2.color;
          vars["--n-item-text-color"] = self2.itemTextColor;
          vars["--n-item-text-color-hover"] = self2.itemTextColorHover;
          vars["--n-item-text-color-active"] = self2.itemTextColorActive;
          vars["--n-item-text-color-child-active"] = self2.itemTextColorChildActive;
          vars["--n-item-text-color-child-active-hover"] = self2.itemTextColorChildActiveHover;
          vars["--n-item-text-color-active-hover"] = self2.itemTextColorActiveHover;
          vars["--n-item-icon-color"] = self2.itemIconColor;
          vars["--n-item-icon-color-hover"] = self2.itemIconColorHover;
          vars["--n-item-icon-color-active"] = self2.itemIconColorActive;
          vars["--n-item-icon-color-active-hover"] = self2.itemIconColorActiveHover;
          vars["--n-item-icon-color-child-active"] = self2.itemIconColorChildActive;
          vars["--n-item-icon-color-child-active-hover"] = self2.itemIconColorChildActiveHover;
          vars["--n-item-icon-color-collapsed"] = self2.itemIconColorCollapsed;
          vars["--n-item-text-color-horizontal"] = self2.itemTextColorHorizontal;
          vars["--n-item-text-color-hover-horizontal"] = self2.itemTextColorHoverHorizontal;
          vars["--n-item-text-color-active-horizontal"] = self2.itemTextColorActiveHorizontal;
          vars["--n-item-text-color-child-active-horizontal"] = self2.itemTextColorChildActiveHorizontal;
          vars["--n-item-text-color-child-active-hover-horizontal"] = self2.itemTextColorChildActiveHoverHorizontal;
          vars["--n-item-text-color-active-hover-horizontal"] = self2.itemTextColorActiveHoverHorizontal;
          vars["--n-item-icon-color-horizontal"] = self2.itemIconColorHorizontal;
          vars["--n-item-icon-color-hover-horizontal"] = self2.itemIconColorHoverHorizontal;
          vars["--n-item-icon-color-active-horizontal"] = self2.itemIconColorActiveHorizontal;
          vars["--n-item-icon-color-active-hover-horizontal"] = self2.itemIconColorActiveHoverHorizontal;
          vars["--n-item-icon-color-child-active-horizontal"] = self2.itemIconColorChildActiveHorizontal;
          vars["--n-item-icon-color-child-active-hover-horizontal"] = self2.itemIconColorChildActiveHoverHorizontal;
          vars["--n-arrow-color"] = self2.arrowColor;
          vars["--n-arrow-color-hover"] = self2.arrowColorHover;
          vars["--n-arrow-color-active"] = self2.arrowColorActive;
          vars["--n-arrow-color-active-hover"] = self2.arrowColorActiveHover;
          vars["--n-arrow-color-child-active"] = self2.arrowColorChildActive;
          vars["--n-arrow-color-child-active-hover"] = self2.arrowColorChildActiveHover;
          vars["--n-item-color-hover"] = self2.itemColorHover;
          vars["--n-item-color-active"] = self2.itemColorActive;
          vars["--n-item-color-active-hover"] = self2.itemColorActiveHover;
          vars["--n-item-color-active-collapsed"] = self2.itemColorActiveCollapsed;
        }
        return vars;
      });
      const themeClassHandle = inlineThemeDisabled ? useThemeClass("menu", computed(() => props.inverted ? "a" : "b"), cssVarsRef, props) : void 0;
      const ellipsisNodeId = createId();
      const overflowRef = /* @__PURE__ */ ref(null);
      const counterRef = /* @__PURE__ */ ref(null);
      let isFirstResize = true;
      const onResize = () => {
        var _a2;
        if (isFirstResize) {
          isFirstResize = false;
        } else {
          (_a2 = overflowRef.value) === null || _a2 === void 0 ? void 0 : _a2.sync({
            showAllItemsBeforeCalculate: true
          });
        }
      };
      function getCounter() {
        return document.getElementById(ellipsisNodeId);
      }
      const ellipsisFromIndexRef = /* @__PURE__ */ ref(-1);
      function onUpdateCount(count) {
        ellipsisFromIndexRef.value = props.options.length - count;
      }
      function onUpdateOverflow(overflow) {
        if (!overflow) {
          ellipsisFromIndexRef.value = -1;
        }
      }
      const ellipsisOptionRef = computed(() => {
        const ellipsisFromIndex = ellipsisFromIndexRef.value;
        const option = {
          children: ellipsisFromIndex === -1 ? [] : props.options.slice(ellipsisFromIndex)
        };
        return option;
      });
      const ellipsisTreeMateRef = computed(() => {
        const {
          childrenField,
          disabledField,
          keyField
        } = props;
        return createTreeMate([ellipsisOptionRef.value], {
          getIgnored(node) {
            return isIgnoredNode(node);
          },
          getChildren(node) {
            return node[childrenField];
          },
          getDisabled(node) {
            return node[disabledField];
          },
          getKey(node) {
            var _a2;
            return (_a2 = node[keyField]) !== null && _a2 !== void 0 ? _a2 : node.name;
          }
        });
      });
      const emptyTmNodeRef = computed(() => {
        return createTreeMate([{}]).treeNodes[0];
      });
      function renderCounter() {
        var _a2;
        if (ellipsisFromIndexRef.value === -1) {
          return h(NSubmenu, {
            root: true,
            level: 0,
            key: "__ellpisisGroupPlaceholder__",
            internalKey: "__ellpisisGroupPlaceholder__",
            title: "···",
            tmNode: emptyTmNodeRef.value,
            domId: ellipsisNodeId,
            isEllipsisPlaceholder: true
          });
        }
        const tmNode = ellipsisTreeMateRef.value.treeNodes[0];
        const activePath = activePathRef.value;
        const childActive = !!((_a2 = tmNode.children) === null || _a2 === void 0 ? void 0 : _a2.some((tmNode2) => {
          return activePath.includes(tmNode2.key);
        }));
        return h(NSubmenu, {
          level: 0,
          root: true,
          key: "__ellpisisGroup__",
          internalKey: "__ellpisisGroup__",
          title: "···",
          virtualChildActive: childActive,
          tmNode,
          domId: ellipsisNodeId,
          rawNodes: tmNode.rawNode.children || [],
          tmNodes: tmNode.children || [],
          isEllipsisPlaceholder: true
        });
      }
      return {
        mergedClsPrefix: mergedClsPrefixRef,
        controlledExpandedKeys: controlledExpandedKeysRef,
        uncontrolledExpanededKeys: uncontrolledExpandedKeysRef,
        mergedExpandedKeys: mergedExpandedKeysRef,
        uncontrolledValue: uncontrolledValueRef,
        mergedValue: mergedValueRef,
        activePath: activePathRef,
        tmNodes: tmNodesRef,
        mergedTheme: themeRef,
        mergedCollapsed: mergedCollapsedRef,
        cssVars: inlineThemeDisabled ? void 0 : cssVarsRef,
        themeClass: themeClassHandle === null || themeClassHandle === void 0 ? void 0 : themeClassHandle.themeClass,
        overflowRef,
        counterRef,
        updateCounter: () => {
        },
        onResize,
        onUpdateOverflow,
        onUpdateCount,
        renderCounter,
        getCounter,
        onRender: themeClassHandle === null || themeClassHandle === void 0 ? void 0 : themeClassHandle.onRender,
        showOption,
        deriveResponsiveState: onResize
      };
    },
    render() {
      const {
        mergedClsPrefix,
        mode,
        themeClass,
        onRender
      } = this;
      onRender === null || onRender === void 0 ? void 0 : onRender();
      const renderMenuItemNodes = () => this.tmNodes.map((tmNode) => itemRenderer(tmNode, this.$props));
      const horizontal = mode === "horizontal";
      const finalResponsive = horizontal && this.responsive;
      const renderMainNode = () => h("div", mergeProps(this.$attrs, {
        role: mode === "horizontal" ? "menubar" : "menu",
        class: [`${mergedClsPrefix}-menu`, themeClass, `${mergedClsPrefix}-menu--${mode}`, finalResponsive && `${mergedClsPrefix}-menu--responsive`, this.mergedCollapsed && `${mergedClsPrefix}-menu--collapsed`],
        style: this.cssVars
      }), finalResponsive ? h(VOverflow, {
        ref: "overflowRef",
        onUpdateOverflow: this.onUpdateOverflow,
        getCounter: this.getCounter,
        onUpdateCount: this.onUpdateCount,
        updateCounter: this.updateCounter,
        style: {
          width: "100%",
          display: "flex",
          overflow: "hidden"
        }
      }, {
        default: renderMenuItemNodes,
        counter: this.renderCounter
      }) : renderMenuItemNodes());
      return finalResponsive ? h(VResizeObserver, {
        onResize: this.onResize
      }, {
        default: renderMainNode
      }) : renderMainNode();
    }
  });
  const style$2 = cB("statistic", [cE("label", `
 font-weight: var(--n-label-font-weight);
 transition: .3s color var(--n-bezier);
 font-size: var(--n-label-font-size);
 color: var(--n-label-text-color);
 `), cB("statistic-value", `
 margin-top: 4px;
 font-weight: var(--n-value-font-weight);
 `, [cE("prefix", `
 margin: 0 4px 0 0;
 font-size: var(--n-value-font-size);
 transition: .3s color var(--n-bezier);
 color: var(--n-value-prefix-text-color);
 `, [cB("icon", {
    verticalAlign: "-0.125em"
  })]), cE("content", `
 font-size: var(--n-value-font-size);
 transition: .3s color var(--n-bezier);
 color: var(--n-value-text-color);
 `), cE("suffix", `
 margin: 0 0 0 4px;
 font-size: var(--n-value-font-size);
 transition: .3s color var(--n-bezier);
 color: var(--n-value-suffix-text-color);
 `, [cB("icon", {
    verticalAlign: "-0.125em"
  })])])]);
  const statisticProps = Object.assign(Object.assign({}, useTheme.props), {
    tabularNums: Boolean,
    label: String,
    value: [String, Number]
  });
  const NStatistic = /* @__PURE__ */ defineComponent({
    name: "Statistic",
    props: statisticProps,
    slots: Object,
    setup(props) {
      const {
        mergedClsPrefixRef,
        inlineThemeDisabled,
        mergedRtlRef
      } = useConfig(props);
      const themeRef = useTheme("Statistic", "-statistic", style$2, statisticLight, props, mergedClsPrefixRef);
      const rtlEnabledRef = useRtl("Statistic", mergedRtlRef, mergedClsPrefixRef);
      const cssVarsRef = computed(() => {
        const {
          self: {
            labelFontWeight,
            valueFontSize,
            valueFontWeight,
            valuePrefixTextColor,
            labelTextColor,
            valueSuffixTextColor,
            valueTextColor,
            labelFontSize
          },
          common: {
            cubicBezierEaseInOut: cubicBezierEaseInOut2
          }
        } = themeRef.value;
        return {
          "--n-bezier": cubicBezierEaseInOut2,
          "--n-label-font-size": labelFontSize,
          "--n-label-font-weight": labelFontWeight,
          "--n-label-text-color": labelTextColor,
          "--n-value-font-weight": valueFontWeight,
          "--n-value-font-size": valueFontSize,
          "--n-value-prefix-text-color": valuePrefixTextColor,
          "--n-value-suffix-text-color": valueSuffixTextColor,
          "--n-value-text-color": valueTextColor
        };
      });
      const themeClassHandle = inlineThemeDisabled ? useThemeClass("statistic", void 0, cssVarsRef, props) : void 0;
      return {
        rtlEnabled: rtlEnabledRef,
        mergedClsPrefix: mergedClsPrefixRef,
        cssVars: inlineThemeDisabled ? void 0 : cssVarsRef,
        themeClass: themeClassHandle === null || themeClassHandle === void 0 ? void 0 : themeClassHandle.themeClass,
        onRender: themeClassHandle === null || themeClassHandle === void 0 ? void 0 : themeClassHandle.onRender
      };
    },
    render() {
      var _a2;
      const {
        mergedClsPrefix,
        $slots: {
          default: defaultSlot,
          label: labelSlot,
          prefix: prefixSlot,
          suffix: suffixSlot
        }
      } = this;
      (_a2 = this.onRender) === null || _a2 === void 0 ? void 0 : _a2.call(this);
      return h("div", {
        class: [`${mergedClsPrefix}-statistic`, this.themeClass, this.rtlEnabled && `${mergedClsPrefix}-statistic--rtl`],
        style: this.cssVars
      }, resolveWrappedSlot(labelSlot, (children) => h("div", {
        class: `${mergedClsPrefix}-statistic__label`
      }, this.label || children)), h("div", {
        class: `${mergedClsPrefix}-statistic-value`,
        style: {
          fontVariantNumeric: this.tabularNums ? "tabular-nums" : ""
        }
      }, resolveWrappedSlot(prefixSlot, (children) => children && h("span", {
        class: `${mergedClsPrefix}-statistic-value__prefix`
      }, children)), this.value !== void 0 ? h("span", {
        class: `${mergedClsPrefix}-statistic-value__content`
      }, this.value) : resolveWrappedSlot(defaultSlot, (children) => children && h("span", {
        class: `${mergedClsPrefix}-statistic-value__content`
      }, children)), resolveWrappedSlot(suffixSlot, (children) => children && h("span", {
        class: `${mergedClsPrefix}-statistic-value__suffix`
      }, children))));
    }
  });
  const style$1 = cB("h", `
 font-size: var(--n-font-size);
 font-weight: var(--n-font-weight);
 margin: var(--n-margin);
 transition: color .3s var(--n-bezier);
 color: var(--n-text-color);
`, [c$1("&:first-child", {
    marginTop: 0
  }), cM("prefix-bar", {
    position: "relative",
    paddingLeft: "var(--n-prefix-width)"
  }, [cM("align-text", {
    paddingLeft: 0
  }, [c$1("&::before", {
    left: "calc(-1 * var(--n-prefix-width))"
  })]), c$1("&::before", `
 content: "";
 width: var(--n-bar-width);
 border-radius: calc(var(--n-bar-width) / 2);
 transition: background-color .3s var(--n-bezier);
 left: 0;
 top: 0;
 bottom: 0;
 position: absolute;
 `), c$1("&::before", {
    backgroundColor: "var(--n-bar-color)"
  })])]);
  const headerProps = Object.assign(Object.assign({}, useTheme.props), {
    type: {
      type: String,
      default: "default"
    },
    prefix: String,
    alignText: Boolean
  });
  const createHeader = (level) => /* @__PURE__ */ defineComponent({
    name: `H${level}`,
    props: headerProps,
    setup(props) {
      const {
        mergedClsPrefixRef,
        inlineThemeDisabled
      } = useConfig(props);
      const themeRef = useTheme("Typography", "-h", style$1, typographyLight, props, mergedClsPrefixRef);
      const cssVarsRef = computed(() => {
        const {
          type
        } = props;
        const {
          common: {
            cubicBezierEaseInOut: cubicBezierEaseInOut2
          },
          self: {
            headerFontWeight,
            headerTextColor,
            [createKey("headerPrefixWidth", level)]: prefixWidth,
            [createKey("headerFontSize", level)]: fontSize2,
            [createKey("headerMargin", level)]: margin,
            [createKey("headerBarWidth", level)]: barWidth,
            [createKey("headerBarColor", type)]: barColor
          }
        } = themeRef.value;
        return {
          "--n-bezier": cubicBezierEaseInOut2,
          "--n-font-size": fontSize2,
          "--n-margin": margin,
          "--n-bar-color": barColor,
          "--n-bar-width": barWidth,
          "--n-font-weight": headerFontWeight,
          "--n-text-color": headerTextColor,
          "--n-prefix-width": prefixWidth
        };
      });
      const themeClassHandle = inlineThemeDisabled ? useThemeClass(`h${level}`, computed(() => props.type[0]), cssVarsRef, props) : void 0;
      return {
        mergedClsPrefix: mergedClsPrefixRef,
        cssVars: inlineThemeDisabled ? void 0 : cssVarsRef,
        themeClass: themeClassHandle === null || themeClassHandle === void 0 ? void 0 : themeClassHandle.themeClass,
        onRender: themeClassHandle === null || themeClassHandle === void 0 ? void 0 : themeClassHandle.onRender
      };
    },
    render() {
      var _a2;
      const {
        prefix: prefix2,
        alignText,
        mergedClsPrefix,
        cssVars,
        $slots
      } = this;
      (_a2 = this.onRender) === null || _a2 === void 0 ? void 0 : _a2.call(this);
      return h(`h${level}`, {
        class: [`${mergedClsPrefix}-h`, `${mergedClsPrefix}-h${level}`, this.themeClass, {
          [`${mergedClsPrefix}-h--prefix-bar`]: prefix2,
          [`${mergedClsPrefix}-h--align-text`]: alignText
        }],
        style: cssVars
      }, $slots);
    }
  });
  const NH2 = createHeader("2");
  const style = cB("text", `
 transition: color .3s var(--n-bezier);
 color: var(--n-text-color);
`, [cM("strong", `
 font-weight: var(--n-font-weight-strong);
 `), cM("italic", {
    fontStyle: "italic"
  }), cM("underline", {
    textDecoration: "underline"
  }), cM("code", `
 line-height: 1.4;
 display: inline-block;
 font-family: var(--n-font-famliy-mono);
 transition: 
 color .3s var(--n-bezier),
 border-color .3s var(--n-bezier),
 background-color .3s var(--n-bezier);
 box-sizing: border-box;
 padding: .05em .35em 0 .35em;
 border-radius: var(--n-code-border-radius);
 font-size: .9em;
 color: var(--n-code-text-color);
 background-color: var(--n-code-color);
 border: var(--n-code-border);
 `)]);
  const textProps = Object.assign(Object.assign({}, useTheme.props), {
    code: Boolean,
    type: {
      type: String,
      default: "default"
    },
    delete: Boolean,
    strong: Boolean,
    italic: Boolean,
    underline: Boolean,
    depth: [String, Number],
    tag: String,
    // deprecated
    as: {
      type: String,
      validator: () => {
        return true;
      },
      default: void 0
    }
  });
  const NText = /* @__PURE__ */ defineComponent({
    name: "Text",
    props: textProps,
    setup(props) {
      const {
        mergedClsPrefixRef,
        inlineThemeDisabled
      } = useConfig(props);
      const themeRef = useTheme("Typography", "-text", style, typographyLight, props, mergedClsPrefixRef);
      const cssVarsRef = computed(() => {
        const {
          depth,
          type
        } = props;
        const textColorKey = type === "default" ? depth === void 0 ? "textColor" : `textColor${depth}Depth` : createKey("textColor", type);
        const {
          common: {
            fontWeightStrong,
            fontFamilyMono,
            cubicBezierEaseInOut: cubicBezierEaseInOut2
          },
          self: {
            codeTextColor,
            codeBorderRadius,
            codeColor,
            codeBorder,
            [textColorKey]: textColor
          }
        } = themeRef.value;
        return {
          "--n-bezier": cubicBezierEaseInOut2,
          "--n-text-color": textColor,
          "--n-font-weight-strong": fontWeightStrong,
          "--n-font-famliy-mono": fontFamilyMono,
          "--n-code-border-radius": codeBorderRadius,
          "--n-code-text-color": codeTextColor,
          "--n-code-color": codeColor,
          "--n-code-border": codeBorder
        };
      });
      const themeClassHandle = inlineThemeDisabled ? useThemeClass("text", computed(() => `${props.type[0]}${props.depth || ""}`), cssVarsRef, props) : void 0;
      return {
        mergedClsPrefix: mergedClsPrefixRef,
        compitableTag: useCompitable(props, ["as", "tag"]),
        cssVars: inlineThemeDisabled ? void 0 : cssVarsRef,
        themeClass: themeClassHandle === null || themeClassHandle === void 0 ? void 0 : themeClassHandle.themeClass,
        onRender: themeClassHandle === null || themeClassHandle === void 0 ? void 0 : themeClassHandle.onRender
      };
    },
    render() {
      var _a2, _b, _c;
      const {
        mergedClsPrefix
      } = this;
      (_a2 = this.onRender) === null || _a2 === void 0 ? void 0 : _a2.call(this);
      const textClass = [`${mergedClsPrefix}-text`, this.themeClass, {
        [`${mergedClsPrefix}-text--code`]: this.code,
        [`${mergedClsPrefix}-text--delete`]: this.delete,
        [`${mergedClsPrefix}-text--strong`]: this.strong,
        [`${mergedClsPrefix}-text--italic`]: this.italic,
        [`${mergedClsPrefix}-text--underline`]: this.underline
      }];
      const children = (_c = (_b = this.$slots).default) === null || _c === void 0 ? void 0 : _c.call(_b);
      return this.code ? h("code", {
        class: textClass,
        style: this.cssVars
      }, this.delete ? h("del", null, children) : children) : this.delete ? h("del", {
        class: textClass,
        style: this.cssVars
      }, children) : h(this.compitableTag || "span", {
        class: textClass,
        style: this.cssVars
      }, children);
    }
  });
  const _hoisted_1$3 = { class: "dashboard" };
  const _sfc_main$3 = {
    __name: "Dashboard",
    setup(__props) {
      const message = useMessage();
      const stats = /* @__PURE__ */ ref({
        plugins: 5,
        apis: 23,
        users: 128,
        requests: "1,234"
      });
      const currentTime = /* @__PURE__ */ ref("");
      let timer = null;
      const updateTime = () => {
        currentTime.value = (/* @__PURE__ */ new Date()).toLocaleString("zh-CN");
      };
      const testApi = async () => {
        try {
          const response = await fetch("/plugin/demo-plugin/api/hello?name=Console");
          const data = await response.json();
          message.success(`API 响应: ${JSON.stringify(data)}`);
        } catch (error) {
          message.error(`API 调用失败: ${error.message}`);
        }
      };
      const refreshStats = () => {
        stats.value.requests = Math.floor(Math.random() * 1e4).toLocaleString();
        message.success("统计数据已刷新！");
      };
      onMounted(() => {
        updateTime();
        timer = setInterval(updateTime, 1e3);
      });
      onUnmounted(() => {
        if (timer) {
          clearInterval(timer);
        }
      });
      return (_ctx, _cache) => {
        return openBlock(), createElementBlock("div", _hoisted_1$3, [
          createVNode(unref(NSpace), {
            vertical: "",
            size: 16
          }, {
            default: withCtx(() => [
              createVNode(unref(NAlert), {
                type: "info",
                title: "欢迎使用演示插件"
              }, {
                default: withCtx(() => [..._cache[0] || (_cache[0] = [
                  createTextVNode(" 这是一个基于 Vue 3 + Naive UI 的插件前端控制台示例 ", -1)
                ])]),
                _: 1
              }),
              createVNode(unref(NGrid), {
                cols: 4,
                "x-gap": 16,
                "y-gap": 16,
                responsive: "screen"
              }, {
                default: withCtx(() => [
                  createVNode(unref(NGi), null, {
                    default: withCtx(() => [
                      createVNode(unref(NCard), null, {
                        default: withCtx(() => [
                          createVNode(unref(NStatistic), {
                            label: "已安装插件",
                            value: stats.value.plugins
                          }, null, 8, ["value"])
                        ]),
                        _: 1
                      })
                    ]),
                    _: 1
                  }),
                  createVNode(unref(NGi), null, {
                    default: withCtx(() => [
                      createVNode(unref(NCard), null, {
                        default: withCtx(() => [
                          createVNode(unref(NStatistic), {
                            label: "API 接口",
                            value: stats.value.apis
                          }, null, 8, ["value"])
                        ]),
                        _: 1
                      })
                    ]),
                    _: 1
                  }),
                  createVNode(unref(NGi), null, {
                    default: withCtx(() => [
                      createVNode(unref(NCard), null, {
                        default: withCtx(() => [
                          createVNode(unref(NStatistic), {
                            label: "活跃用户",
                            value: stats.value.users
                          }, null, 8, ["value"])
                        ]),
                        _: 1
                      })
                    ]),
                    _: 1
                  }),
                  createVNode(unref(NGi), null, {
                    default: withCtx(() => [
                      createVNode(unref(NCard), null, {
                        default: withCtx(() => [
                          createVNode(unref(NStatistic), {
                            label: "今日请求",
                            value: stats.value.requests
                          }, null, 8, ["value"])
                        ]),
                        _: 1
                      })
                    ]),
                    _: 1
                  })
                ]),
                _: 1
              }),
              createVNode(unref(NCard), { title: "快速操作" }, {
                default: withCtx(() => [
                  createVNode(unref(NSpace), null, {
                    default: withCtx(() => [
                      createVNode(unref(Button), {
                        type: "primary",
                        onClick: testApi
                      }, {
                        default: withCtx(() => [..._cache[1] || (_cache[1] = [
                          createTextVNode(" 测试 API ", -1)
                        ])]),
                        _: 1
                      }),
                      createVNode(unref(Button), { onClick: refreshStats }, {
                        default: withCtx(() => [..._cache[2] || (_cache[2] = [
                          createTextVNode(" 刷新统计 ", -1)
                        ])]),
                        _: 1
                      })
                    ]),
                    _: 1
                  })
                ]),
                _: 1
              }),
              createVNode(unref(NCard), { title: "系统信息" }, {
                default: withCtx(() => [
                  createVNode(unref(NDescriptions), {
                    "label-placement": "left",
                    column: 2
                  }, {
                    default: withCtx(() => [
                      createVNode(unref(NDescriptionsItem), { label: "插件版本" }, {
                        default: withCtx(() => [
                          createVNode(unref(NTag), { type: "success" }, {
                            default: withCtx(() => [..._cache[3] || (_cache[3] = [
                              createTextVNode("v1.0.0", -1)
                            ])]),
                            _: 1
                          })
                        ]),
                        _: 1
                      }),
                      createVNode(unref(NDescriptionsItem), { label: "Vue 版本" }, {
                        default: withCtx(() => [
                          createVNode(unref(NTag), { type: "info" }, {
                            default: withCtx(() => [
                              createTextVNode(toDisplayString(version))
                            ]),
                            _: 1
                          })
                        ]),
                        _: 1
                      }),
                      createVNode(unref(NDescriptionsItem), { label: "插件 ID" }, {
                        default: withCtx(() => [
                          createVNode(unref(NText), { code: "" }, {
                            default: withCtx(() => [..._cache[4] || (_cache[4] = [
                              createTextVNode("demo-plugin", -1)
                            ])]),
                            _: 1
                          })
                        ]),
                        _: 1
                      }),
                      createVNode(unref(NDescriptionsItem), { label: "UI 框架" }, {
                        default: withCtx(() => [
                          createVNode(unref(NTag), { type: "warning" }, {
                            default: withCtx(() => [..._cache[5] || (_cache[5] = [
                              createTextVNode("Naive UI", -1)
                            ])]),
                            _: 1
                          })
                        ]),
                        _: 1
                      }),
                      createVNode(unref(NDescriptionsItem), {
                        label: "当前时间",
                        span: 2
                      }, {
                        default: withCtx(() => [
                          createTextVNode(toDisplayString(currentTime.value), 1)
                        ]),
                        _: 1
                      })
                    ]),
                    _: 1
                  })
                ]),
                _: 1
              })
            ]),
            _: 1
          })
        ]);
      };
    }
  };
  const Dashboard = /* @__PURE__ */ _export_sfc(_sfc_main$3, [["__scopeId", "data-v-82fd5971"]]);
  const Dashboard$1 = /* @__PURE__ */ Object.freeze(/* @__PURE__ */ Object.defineProperty({
    __proto__: null,
    default: Dashboard
  }, Symbol.toStringTag, { value: "Module" }));
  const _hoisted_1$2 = { class: "settings" };
  const _hoisted_2$1 = { class: "card" };
  const _hoisted_3$1 = { class: "form-group" };
  const _hoisted_4$1 = { class: "form-group" };
  const _hoisted_5$1 = { class: "switch" };
  const _hoisted_6$1 = { class: "switch-label" };
  const _hoisted_7$1 = { class: "form-group" };
  const _hoisted_8$1 = { class: "form-group" };
  const _hoisted_9$1 = { class: "card" };
  const _hoisted_10$1 = { class: "form-group" };
  const _hoisted_11$1 = { class: "form-group" };
  const _sfc_main$2 = {
    __name: "Settings",
    setup(__props) {
      const defaultSettings = {
        name: "演示插件",
        debug: false,
        logLevel: "info",
        cacheTime: 300,
        apiTimeout: 5e3,
        maxRetries: 3
      };
      const settings = /* @__PURE__ */ ref({ ...defaultSettings });
      const saveSettings = () => {
        localStorage.setItem("demo-plugin-settings", JSON.stringify(settings.value));
        alert("设置已保存！");
      };
      const resetSettings = () => {
        settings.value = { ...defaultSettings };
        alert("已重置为默认设置");
      };
      const exportSettings = () => {
        const data = JSON.stringify(settings.value, null, 2);
        const blob = new Blob([data], { type: "application/json" });
        const url = URL.createObjectURL(blob);
        const a = document.createElement("a");
        a.href = url;
        a.download = "demo-plugin-settings.json";
        a.click();
        URL.revokeObjectURL(url);
      };
      const clearCache = () => {
        localStorage.removeItem("demo-plugin-cache");
        alert("缓存已清除！");
      };
      const resetAll = () => {
        if (confirm("确定要重置所有数据吗？此操作不可恢复。")) {
          localStorage.clear();
          settings.value = { ...defaultSettings };
          alert("所有数据已重置！");
        }
      };
      onMounted(() => {
        const saved = localStorage.getItem("demo-plugin-settings");
        if (saved) {
          try {
            Object.assign(settings.value, JSON.parse(saved));
          } catch (e) {
            console.error("加载设置失败:", e);
          }
        }
      });
      return (_ctx, _cache) => {
        return openBlock(), createElementBlock("div", _hoisted_1$2, [
          _cache[18] || (_cache[18] = createBaseVNode("div", { class: "card" }, [
            createBaseVNode("h2", null, "插件设置"),
            createBaseVNode("p", null, "配置演示插件的各项参数。")
          ], -1)),
          createBaseVNode("div", _hoisted_2$1, [
            _cache[13] || (_cache[13] = createBaseVNode("h2", null, "基本设置", -1)),
            createBaseVNode("form", {
              onSubmit: withModifiers(saveSettings, ["prevent"])
            }, [
              createBaseVNode("div", _hoisted_3$1, [
                _cache[6] || (_cache[6] = createBaseVNode("label", null, "插件名称", -1)),
                withDirectives(createBaseVNode("input", {
                  "onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => settings.value.name = $event),
                  type: "text",
                  class: "input",
                  placeholder: "请输入插件名称"
                }, null, 512), [
                  [vModelText, settings.value.name]
                ])
              ]),
              createBaseVNode("div", _hoisted_4$1, [
                _cache[8] || (_cache[8] = createBaseVNode("label", null, "调试模式", -1)),
                createBaseVNode("label", _hoisted_5$1, [
                  withDirectives(createBaseVNode("input", {
                    "onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => settings.value.debug = $event),
                    type: "checkbox"
                  }, null, 512), [
                    [vModelCheckbox, settings.value.debug]
                  ]),
                  _cache[7] || (_cache[7] = createBaseVNode("span", { class: "slider" }, null, -1))
                ]),
                createBaseVNode("span", _hoisted_6$1, toDisplayString(settings.value.debug ? "已开启" : "已关闭"), 1)
              ]),
              createBaseVNode("div", _hoisted_7$1, [
                _cache[10] || (_cache[10] = createBaseVNode("label", null, "日志级别", -1)),
                withDirectives(createBaseVNode("select", {
                  "onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => settings.value.logLevel = $event),
                  class: "input"
                }, [..._cache[9] || (_cache[9] = [
                  createBaseVNode("option", { value: "debug" }, "Debug", -1),
                  createBaseVNode("option", { value: "info" }, "Info", -1),
                  createBaseVNode("option", { value: "warn" }, "Warn", -1),
                  createBaseVNode("option", { value: "error" }, "Error", -1)
                ])], 512), [
                  [vModelSelect, settings.value.logLevel]
                ])
              ]),
              createBaseVNode("div", _hoisted_8$1, [
                _cache[11] || (_cache[11] = createBaseVNode("label", null, "缓存时间（秒）", -1)),
                withDirectives(createBaseVNode("input", {
                  "onUpdate:modelValue": _cache[3] || (_cache[3] = ($event) => settings.value.cacheTime = $event),
                  type: "number",
                  class: "input",
                  min: "0",
                  max: "3600"
                }, null, 512), [
                  [
                    vModelText,
                    settings.value.cacheTime,
                    void 0,
                    { number: true }
                  ]
                ])
              ]),
              createBaseVNode("div", { class: "form-actions" }, [
                _cache[12] || (_cache[12] = createBaseVNode("button", {
                  type: "submit",
                  class: "btn btn-primary"
                }, "保存设置", -1)),
                createBaseVNode("button", {
                  type: "button",
                  class: "btn btn-secondary",
                  onClick: resetSettings
                }, " 重置默认 ")
              ])
            ], 32)
          ]),
          createBaseVNode("div", _hoisted_9$1, [
            _cache[16] || (_cache[16] = createBaseVNode("h2", null, "高级设置", -1)),
            createBaseVNode("div", _hoisted_10$1, [
              _cache[14] || (_cache[14] = createBaseVNode("label", null, "API 超时时间（毫秒）", -1)),
              withDirectives(createBaseVNode("input", {
                "onUpdate:modelValue": _cache[4] || (_cache[4] = ($event) => settings.value.apiTimeout = $event),
                type: "number",
                class: "input",
                min: "1000",
                max: "60000"
              }, null, 512), [
                [
                  vModelText,
                  settings.value.apiTimeout,
                  void 0,
                  { number: true }
                ]
              ])
            ]),
            createBaseVNode("div", _hoisted_11$1, [
              _cache[15] || (_cache[15] = createBaseVNode("label", null, "最大重试次数", -1)),
              withDirectives(createBaseVNode("input", {
                "onUpdate:modelValue": _cache[5] || (_cache[5] = ($event) => settings.value.maxRetries = $event),
                type: "number",
                class: "input",
                min: "0",
                max: "10"
              }, null, 512), [
                [
                  vModelText,
                  settings.value.maxRetries,
                  void 0,
                  { number: true }
                ]
              ])
            ])
          ]),
          createBaseVNode("div", { class: "card" }, [
            _cache[17] || (_cache[17] = createBaseVNode("h2", null, "数据管理", -1)),
            createBaseVNode("div", { class: "data-actions" }, [
              createBaseVNode("button", {
                class: "btn btn-secondary",
                onClick: exportSettings
              }, "导出设置"),
              createBaseVNode("button", {
                class: "btn btn-secondary",
                onClick: clearCache
              }, "清除缓存"),
              createBaseVNode("button", {
                class: "btn btn-danger",
                onClick: resetAll
              }, "重置所有")
            ])
          ])
        ]);
      };
    }
  };
  const Settings = /* @__PURE__ */ _export_sfc(_sfc_main$2, [["__scopeId", "data-v-7acd1c9d"]]);
  const Settings$1 = /* @__PURE__ */ Object.freeze(/* @__PURE__ */ Object.defineProperty({
    __proto__: null,
    default: Settings
  }, Symbol.toStringTag, { value: "Module" }));
  const _hoisted_1$1 = { class: "data-view" };
  const _hoisted_2 = { class: "card" };
  const _hoisted_3 = { class: "toolbar" };
  const _hoisted_4 = { class: "table" };
  const _hoisted_5 = ["onClick"];
  const _hoisted_6 = ["onClick"];
  const _hoisted_7 = {
    key: 0,
    class: "empty-state"
  };
  const _hoisted_8 = { class: "card" };
  const _hoisted_9 = { class: "chart-container" };
  const _hoisted_10 = { class: "chart-placeholder" };
  const _hoisted_11 = { class: "bar-chart" };
  const _hoisted_12 = { class: "bar-label" };
  const _hoisted_13 = { class: "bar-value" };
  const _sfc_main$1 = {
    __name: "DataView",
    setup(__props) {
      const statusMap = {
        active: "活跃",
        inactive: "未激活",
        pending: "待处理"
      };
      const searchQuery = /* @__PURE__ */ ref("");
      const dataList = /* @__PURE__ */ ref([
        { id: 1, name: "示例数据 1", status: "active", createdAt: "2024-01-15 10:30" },
        { id: 2, name: "示例数据 2", status: "inactive", createdAt: "2024-01-16 14:20" },
        { id: 3, name: "示例数据 3", status: "pending", createdAt: "2024-01-17 09:15" },
        { id: 4, name: "测试数据 A", status: "active", createdAt: "2024-01-18 16:45" },
        { id: 5, name: "测试数据 B", status: "active", createdAt: "2024-01-19 11:00" }
      ]);
      const chartData = /* @__PURE__ */ ref([
        { label: "活跃", value: 60 },
        { label: "未激活", value: 25 },
        { label: "待处理", value: 15 }
      ]);
      const filteredData = computed(() => {
        if (!searchQuery.value) {
          return dataList.value;
        }
        const query = searchQuery.value.toLowerCase();
        return dataList.value.filter(
          (item) => item.name.toLowerCase().includes(query) || item.id.toString().includes(query)
        );
      });
      const addData = () => {
        const newId = Math.max(...dataList.value.map((d) => d.id)) + 1;
        dataList.value.push({
          id: newId,
          name: `新数据 ${newId}`,
          status: "pending",
          createdAt: (/* @__PURE__ */ new Date()).toLocaleString("zh-CN")
        });
        alert("数据已添加！");
      };
      const editData = (item) => {
        const newName = prompt("请输入新名称:", item.name);
        if (newName && newName !== item.name) {
          item.name = newName;
          alert("数据已更新！");
        }
      };
      const deleteData = (item) => {
        if (confirm(`确定要删除 "${item.name}" 吗？`)) {
          const index = dataList.value.findIndex((d) => d.id === item.id);
          if (index > -1) {
            dataList.value.splice(index, 1);
            alert("数据已删除！");
          }
        }
      };
      return (_ctx, _cache) => {
        return openBlock(), createElementBlock("div", _hoisted_1$1, [
          _cache[5] || (_cache[5] = createBaseVNode("div", { class: "card" }, [
            createBaseVNode("h2", null, "数据管理"),
            createBaseVNode("p", null, "查看和管理插件数据。")
          ], -1)),
          createBaseVNode("div", _hoisted_2, [
            _cache[3] || (_cache[3] = createBaseVNode("h2", null, "数据列表", -1)),
            createBaseVNode("div", _hoisted_3, [
              withDirectives(createBaseVNode("input", {
                "onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => searchQuery.value = $event),
                type: "text",
                class: "input search-input",
                placeholder: "搜索..."
              }, null, 512), [
                [vModelText, searchQuery.value]
              ]),
              createBaseVNode("button", {
                class: "btn btn-primary",
                onClick: addData
              }, "添加数据")
            ]),
            createBaseVNode("table", _hoisted_4, [
              _cache[1] || (_cache[1] = createBaseVNode("thead", null, [
                createBaseVNode("tr", null, [
                  createBaseVNode("th", null, "ID"),
                  createBaseVNode("th", null, "名称"),
                  createBaseVNode("th", null, "状态"),
                  createBaseVNode("th", null, "创建时间"),
                  createBaseVNode("th", null, "操作")
                ])
              ], -1)),
              createBaseVNode("tbody", null, [
                (openBlock(true), createElementBlock(Fragment, null, renderList(filteredData.value, (item) => {
                  return openBlock(), createElementBlock("tr", {
                    key: item.id
                  }, [
                    createBaseVNode("td", null, toDisplayString(item.id), 1),
                    createBaseVNode("td", null, toDisplayString(item.name), 1),
                    createBaseVNode("td", null, [
                      createBaseVNode("span", {
                        class: normalizeClass(["status", item.status])
                      }, toDisplayString(statusMap[item.status]), 3)
                    ]),
                    createBaseVNode("td", null, toDisplayString(item.createdAt), 1),
                    createBaseVNode("td", null, [
                      createBaseVNode("button", {
                        class: "btn-small",
                        onClick: ($event) => editData(item)
                      }, "编辑", 8, _hoisted_5),
                      createBaseVNode("button", {
                        class: "btn-small btn-small-danger",
                        onClick: ($event) => deleteData(item)
                      }, " 删除 ", 8, _hoisted_6)
                    ])
                  ]);
                }), 128))
              ])
            ]),
            filteredData.value.length === 0 ? (openBlock(), createElementBlock("div", _hoisted_7, [..._cache[2] || (_cache[2] = [
              createBaseVNode("p", null, "暂无数据", -1)
            ])])) : createCommentVNode("", true)
          ]),
          createBaseVNode("div", _hoisted_8, [
            _cache[4] || (_cache[4] = createBaseVNode("h2", null, "数据统计", -1)),
            createBaseVNode("div", _hoisted_9, [
              createBaseVNode("div", _hoisted_10, [
                createBaseVNode("div", _hoisted_11, [
                  (openBlock(true), createElementBlock(Fragment, null, renderList(chartData.value, (item, index) => {
                    return openBlock(), createElementBlock("div", {
                      key: index,
                      class: "bar",
                      style: normalizeStyle({ height: item.value + "%" })
                    }, [
                      createBaseVNode("span", _hoisted_12, toDisplayString(item.label), 1),
                      createBaseVNode("span", _hoisted_13, toDisplayString(item.value), 1)
                    ], 4);
                  }), 128))
                ])
              ])
            ])
          ])
        ]);
      };
    }
  };
  const DataView = /* @__PURE__ */ _export_sfc(_sfc_main$1, [["__scopeId", "data-v-3c75c63b"]]);
  const DataView$1 = /* @__PURE__ */ Object.freeze(/* @__PURE__ */ Object.defineProperty({
    __proto__: null,
    default: DataView
  }, Symbol.toStringTag, { value: "Module" }));
  const _hoisted_1 = { class: "demo-plugin-app" };
  const _sfc_main = {
    __name: "App",
    setup(__props) {
      const componentMap = {
        "/dashboard": Dashboard,
        "/settings": Settings,
        "/data": DataView
      };
      const currentRoute = /* @__PURE__ */ ref("/dashboard");
      const currentComponent = computed(() => componentMap[currentRoute.value]);
      const menuOptions = [
        {
          label: "控制台首页",
          key: "/dashboard"
        },
        {
          label: "插件设置",
          key: "/settings"
        },
        {
          label: "数据管理",
          key: "/data"
        }
      ];
      const navigate = (key) => {
        currentRoute.value = key;
        window.location.hash = key;
      };
      onMounted(() => {
        const hash = window.location.hash.slice(1);
        if (hash && componentMap[hash]) {
          currentRoute.value = hash;
        }
      });
      return (_ctx, _cache) => {
        return openBlock(), createBlock(unref(NConfigProvider), null, {
          default: withCtx(() => [
            createVNode(unref(NMessageProvider), null, {
              default: withCtx(() => [
                createBaseVNode("div", _hoisted_1, [
                  createVNode(unref(NLayout), null, {
                    default: withCtx(() => [
                      createVNode(unref(NLayoutHeader), {
                        bordered: "",
                        style: { "padding": "0 24px", "height": "64px", "display": "flex", "align-items": "center" }
                      }, {
                        default: withCtx(() => [
                          createVNode(unref(NSpace), {
                            justify: "space-between",
                            align: "center",
                            style: { "width": "100%" }
                          }, {
                            default: withCtx(() => [
                              createVNode(unref(NH2), { style: { "margin": "0", "color": "white" } }, {
                                default: withCtx(() => [..._cache[0] || (_cache[0] = [
                                  createTextVNode("演示插件控制台", -1)
                                ])]),
                                _: 1
                              }),
                              createVNode(unref(NMenu), {
                                mode: "horizontal",
                                options: menuOptions,
                                value: currentRoute.value,
                                "onUpdate:value": navigate,
                                inverted: true
                              }, null, 8, ["value"])
                            ]),
                            _: 1
                          })
                        ]),
                        _: 1
                      }),
                      createVNode(unref(NLayoutContent), null, {
                        default: withCtx(() => [
                          createVNode(unref(NCard), { style: { "margin": "16px" } }, {
                            default: withCtx(() => [
                              currentComponent.value ? (openBlock(), createBlock(resolveDynamicComponent(currentComponent.value), { key: 0 })) : (openBlock(), createBlock(unref(NEmpty), {
                                key: 1,
                                description: "页面未找到"
                              }))
                            ]),
                            _: 1
                          })
                        ]),
                        _: 1
                      }),
                      createVNode(unref(NLayoutFooter), {
                        bordered: "",
                        style: { "text-align": "center", "padding": "16px" }
                      }, {
                        default: withCtx(() => [
                          createVNode(unref(NText), { depth: "3" }, {
                            default: withCtx(() => [..._cache[1] || (_cache[1] = [
                              createTextVNode(" Demo Plugin v1.0.0 © 2024 - Built with Naive UI ", -1)
                            ])]),
                            _: 1
                          })
                        ]),
                        _: 1
                      })
                    ]),
                    _: 1
                  })
                ])
              ]),
              _: 1
            })
          ]),
          _: 1
        });
      };
    }
  };
  const plugin = {
    name: "demo-plugin",
    displayName: "演示插件",
    version: "1.0.0",
    /**
     * 插件安装函数
     * @param {import('vue').App} app - Vue 应用实例
     * @param {Object} options - 插件选项
     * @param {string} options.pluginId - 插件 ID
     * @param {string} options.apiBase - API 基础路径
     * @param {string} options.token - 认证 token
     */
    install(app, options = {}) {
      console.log("[DemoPlugin] Installing with options:", options);
      app.config.globalProperties.$pluginOptions = options;
      const components = /* @__PURE__ */ Object.assign({ "./components/DemoComponent.vue": __vite_glob_0_0 });
      Object.entries(components).forEach(([path, module2]) => {
        const name = path.split("/").pop().replace(".vue", "");
        app.component(`Demo${name}`, module2.default);
        console.log("[DemoPlugin] Registered component:", `Demo${name}`);
      });
      return {
        routes: [
          {
            path: "/dashboard",
            component: () => Promise.resolve().then(() => Dashboard$1),
            title: "控制台首页",
            icon: "dashboard"
          },
          {
            path: "/settings",
            component: () => Promise.resolve().then(() => Settings$1),
            title: "插件设置",
            icon: "settings"
          },
          {
            path: "/data",
            component: () => Promise.resolve().then(() => DataView$1),
            title: "数据管理",
            icon: "database"
          }
        ],
        menuItems: [
          {
            id: "demo-dashboard",
            title: "控制台首页",
            icon: "dashboard",
            route: "/dashboard",
            order: 100
          },
          {
            id: "demo-settings",
            title: "插件设置",
            icon: "settings",
            route: "/settings",
            order: 101
          },
          {
            id: "demo-data",
            title: "数据管理",
            icon: "database",
            route: "/data",
            order: 102
          }
        ]
      };
    }
  };
  function initStandalone(options) {
    console.log("[DemoPlugin] 初始化独立运行模式:", options);
    const app = createApp(_sfc_main);
    plugin.install(app, options);
    app.mount("#app");
  }
  if (window.self !== window.top) {
    console.log("[DemoPlugin] 检测到 iframe 环境，等待主应用初始化...");
    window.addEventListener("message", (event) => {
      var _a2;
      if (((_a2 = event.data) == null ? void 0 : _a2.type) === "INIT") {
        initStandalone(event.data);
      }
    });
  } else if (!((_a = window.Vue) == null ? void 0 : _a.__shared_mode__)) {
    console.log("[DemoPlugin] 检测到独立运行模式");
    initStandalone({
      pluginId: "demo-plugin",
      apiBase: "/plugin/demo-plugin/api",
      token: localStorage.getItem("token") || ""
    });
  } else {
    console.log("[DemoPlugin] 检测到共享模式，导出插件对象");
    window.DemoPlugin = plugin;
  }
  return plugin;
}();
