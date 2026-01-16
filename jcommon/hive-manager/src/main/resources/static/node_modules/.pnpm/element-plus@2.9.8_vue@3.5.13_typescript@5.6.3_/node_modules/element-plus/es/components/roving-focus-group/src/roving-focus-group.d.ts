import type { ExtractPropTypes, StyleValue } from 'vue';
export declare const rovingFocusGroupProps: {
    style: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string | import("vue").CSSProperties | StyleValue[]) | (() => StyleValue) | ((new (...args: any[]) => string | import("vue").CSSProperties | StyleValue[]) | (() => StyleValue))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    currentTabId: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string) | (() => string | null) | ((new (...args: any[]) => string) | (() => string | null))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    defaultCurrentTabId: StringConstructor;
    loop: BooleanConstructor;
    dir: import("element-plus/es/utils").EpPropFinalized<StringConstructor, string, unknown, string, boolean>;
    orientation: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => "horizontal" | "vertical") | (() => "horizontal" | "vertical" | undefined) | ((new (...args: any[]) => "horizontal" | "vertical") | (() => "horizontal" | "vertical" | undefined))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    onBlur: FunctionConstructor;
    onFocus: FunctionConstructor;
    onMousedown: FunctionConstructor;
};
export type ElRovingFocusGroupProps = ExtractPropTypes<typeof rovingFocusGroupProps>;
declare const ElCollection: {
    name: string;
    setup(): void;
    __isFragment?: never;
    __isTeleport?: never;
    __isSuspense?: never;
    template?: string | object;
    render?: Function;
    components?: Record<string, import("vue").Component>;
    directives?: Record<string, import("vue").Directive>;
    inheritAttrs?: boolean;
    emits?: (import("vue").EmitsOptions & ThisType<void>) | undefined;
    expose?: string[];
    serverPrefetch?(): Promise<any>;
    compilerOptions?: import("vue").RuntimeCompilerOptions;
    call?: (this: unknown, ...args: unknown[]) => never;
    __defaults?: {} | undefined;
    compatConfig?: Partial<Record<import("vue").DeprecationTypes, boolean | "suppress-warning">> & {
        MODE?: 2 | 3 | ((comp: import("vue").Component | null) => 2 | 3);
    };
    data?: ((this: import("vue").CreateComponentPublicInstance<Readonly<ExtractPropTypes<{}>>, {}, {}, {}, import("vue").MethodOptions, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, {}, Readonly<ExtractPropTypes<{}>>, {}, false, {
        P: {};
        B: {};
        D: {};
        C: {};
        M: {};
        Defaults: {};
    }, Readonly<ExtractPropTypes<{}>>, {}, {}, {}, import("vue").MethodOptions, {}>, vm: import("vue").CreateComponentPublicInstance<Readonly<ExtractPropTypes<{}>>, {}, {}, {}, import("vue").MethodOptions, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, {}, Readonly<ExtractPropTypes<{}>>, {}, false, {
        P: {};
        B: {};
        D: {};
        C: {};
        M: {};
        Defaults: {};
    }, Readonly<ExtractPropTypes<{}>>, {}, {}, {}, import("vue").MethodOptions, {}>) => {}) | undefined;
    computed?: {} | undefined;
    methods?: {} | undefined;
    watch?: {
        [x: string]: (string | import("vue").WatchCallback<any, any> | ({
            handler: import("vue").WatchCallback | string;
        } & import("vue").WatchOptions<boolean>)) | (string | import("vue").WatchCallback<any, any> | ({
            handler: import("vue").WatchCallback | string;
        } & import("vue").WatchOptions<boolean>))[];
    };
    provide?: import("vue").ComponentProvideOptions;
    inject?: string[] | {
        [x: string]: string | symbol | {
            from?: string | symbol;
            default?: unknown;
        };
        [x: symbol]: string | symbol | {
            from?: string | symbol;
            default?: unknown;
        };
    };
    filters?: Record<string, Function>;
    mixins?: import("vue").ComponentOptionsMixin[] | undefined;
    extends?: import("vue").ComponentOptionsMixin | undefined;
    beforeCreate?(): void;
    created?(): void;
    beforeMount?(): void;
    mounted?(): void;
    beforeUpdate?(): void;
    updated?(): void;
    activated?(): void;
    deactivated?(): void;
    beforeDestroy?(): void;
    beforeUnmount?(): void;
    destroyed?(): void;
    unmounted?(): void;
    renderTracked?: (e: import("vue").DebuggerEvent) => void;
    renderTriggered?: (e: import("vue").DebuggerEvent) => void;
    errorCaptured?: (err: unknown, instance: import("vue").ComponentPublicInstance | null, info: string) => boolean | void;
    delimiters?: [string, string];
    __differentiator?: undefined;
    __isBuiltIn?: boolean;
    __file?: string;
    __name?: string;
    beforeRouteEnter?: import("vue-router").NavigationGuardWithThis<undefined>;
    beforeRouteUpdate?: import("vue-router").NavigationGuard;
    beforeRouteLeave?: import("vue-router").NavigationGuard;
    key?: string | number | symbol;
    ref?: import("vue").VNodeRef;
    ref_for?: boolean;
    ref_key?: string;
    onVnodeBeforeMount?: ((vnode: import("vue").VNode) => void) | ((vnode: import("vue").VNode) => void)[];
    onVnodeMounted?: ((vnode: import("vue").VNode) => void) | ((vnode: import("vue").VNode) => void)[];
    onVnodeBeforeUpdate?: ((vnode: import("vue").VNode, oldVNode: import("vue").VNode) => void) | ((vnode: import("vue").VNode, oldVNode: import("vue").VNode) => void)[];
    onVnodeUpdated?: ((vnode: import("vue").VNode, oldVNode: import("vue").VNode) => void) | ((vnode: import("vue").VNode, oldVNode: import("vue").VNode) => void)[];
    onVnodeBeforeUnmount?: ((vnode: import("vue").VNode) => void) | ((vnode: import("vue").VNode) => void)[];
    onVnodeUnmounted?: ((vnode: import("vue").VNode) => void) | ((vnode: import("vue").VNode) => void)[];
    class?: unknown;
    style?: unknown;
}, ElCollectionItem: {
    name: string;
    setup(_: unknown, { attrs }: import("vue").SetupContext): void;
    __isFragment?: never;
    __isTeleport?: never;
    __isSuspense?: never;
    template?: string | object;
    render?: Function;
    components?: Record<string, import("vue").Component>;
    directives?: Record<string, import("vue").Directive>;
    inheritAttrs?: boolean;
    emits?: (import("vue").EmitsOptions & ThisType<void>) | undefined;
    expose?: string[];
    serverPrefetch?(): Promise<any>;
    compilerOptions?: import("vue").RuntimeCompilerOptions;
    call?: (this: unknown, ...args: unknown[]) => never;
    __defaults?: {} | undefined;
    compatConfig?: Partial<Record<import("vue").DeprecationTypes, boolean | "suppress-warning">> & {
        MODE?: 2 | 3 | ((comp: import("vue").Component | null) => 2 | 3);
    };
    data?: ((this: import("vue").CreateComponentPublicInstance<Readonly<ExtractPropTypes<{}>>, {}, {}, {}, import("vue").MethodOptions, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, {}, Readonly<ExtractPropTypes<{}>>, {}, false, {
        P: {};
        B: {};
        D: {};
        C: {};
        M: {};
        Defaults: {};
    }, Readonly<ExtractPropTypes<{}>>, {}, {}, {}, import("vue").MethodOptions, {}>, vm: import("vue").CreateComponentPublicInstance<Readonly<ExtractPropTypes<{}>>, {}, {}, {}, import("vue").MethodOptions, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, {}, Readonly<ExtractPropTypes<{}>>, {}, false, {
        P: {};
        B: {};
        D: {};
        C: {};
        M: {};
        Defaults: {};
    }, Readonly<ExtractPropTypes<{}>>, {}, {}, {}, import("vue").MethodOptions, {}>) => {}) | undefined;
    computed?: {} | undefined;
    methods?: {} | undefined;
    watch?: {
        [x: string]: (string | import("vue").WatchCallback<any, any> | ({
            handler: import("vue").WatchCallback | string;
        } & import("vue").WatchOptions<boolean>)) | (string | import("vue").WatchCallback<any, any> | ({
            handler: import("vue").WatchCallback | string;
        } & import("vue").WatchOptions<boolean>))[];
    };
    provide?: import("vue").ComponentProvideOptions;
    inject?: string[] | {
        [x: string]: string | symbol | {
            from?: string | symbol;
            default?: unknown;
        };
        [x: symbol]: string | symbol | {
            from?: string | symbol;
            default?: unknown;
        };
    };
    filters?: Record<string, Function>;
    mixins?: import("vue").ComponentOptionsMixin[] | undefined;
    extends?: import("vue").ComponentOptionsMixin | undefined;
    beforeCreate?(): void;
    created?(): void;
    beforeMount?(): void;
    mounted?(): void;
    beforeUpdate?(): void;
    updated?(): void;
    activated?(): void;
    deactivated?(): void;
    beforeDestroy?(): void;
    beforeUnmount?(): void;
    destroyed?(): void;
    unmounted?(): void;
    renderTracked?: (e: import("vue").DebuggerEvent) => void;
    renderTriggered?: (e: import("vue").DebuggerEvent) => void;
    errorCaptured?: (err: unknown, instance: import("vue").ComponentPublicInstance | null, info: string) => boolean | void;
    delimiters?: [string, string];
    __differentiator?: undefined;
    __isBuiltIn?: boolean;
    __file?: string;
    __name?: string;
    beforeRouteEnter?: import("vue-router").NavigationGuardWithThis<undefined>;
    beforeRouteUpdate?: import("vue-router").NavigationGuard;
    beforeRouteLeave?: import("vue-router").NavigationGuard;
    key?: string | number | symbol;
    ref?: import("vue").VNodeRef;
    ref_for?: boolean;
    ref_key?: string;
    onVnodeBeforeMount?: ((vnode: import("vue").VNode) => void) | ((vnode: import("vue").VNode) => void)[];
    onVnodeMounted?: ((vnode: import("vue").VNode) => void) | ((vnode: import("vue").VNode) => void)[];
    onVnodeBeforeUpdate?: ((vnode: import("vue").VNode, oldVNode: import("vue").VNode) => void) | ((vnode: import("vue").VNode, oldVNode: import("vue").VNode) => void)[];
    onVnodeUpdated?: ((vnode: import("vue").VNode, oldVNode: import("vue").VNode) => void) | ((vnode: import("vue").VNode, oldVNode: import("vue").VNode) => void)[];
    onVnodeBeforeUnmount?: ((vnode: import("vue").VNode) => void) | ((vnode: import("vue").VNode) => void)[];
    onVnodeUnmounted?: ((vnode: import("vue").VNode) => void) | ((vnode: import("vue").VNode) => void)[];
    class?: unknown;
    style?: unknown;
}, COLLECTION_INJECTION_KEY: import("vue").InjectionKey<import("element-plus/es/components/collection").ElCollectionInjectionContext>, COLLECTION_ITEM_INJECTION_KEY: import("vue").InjectionKey<import("element-plus/es/components/collection").ElCollectionItemInjectionContext>;
export { ElCollection, ElCollectionItem, COLLECTION_INJECTION_KEY as ROVING_FOCUS_COLLECTION_INJECTION_KEY, COLLECTION_ITEM_INJECTION_KEY as ROVING_FOCUS_ITEM_COLLECTION_INJECTION_KEY, };
