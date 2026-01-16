import { type Placement } from 'element-plus/es/components/popper';
import type { Options } from '@popperjs/core';
import type { ButtonProps } from 'element-plus/es/components/button';
import type { ComponentInternalInstance, ComputedRef } from 'vue';
import type { Nullable } from 'element-plus/es/utils';
export interface IElDropdownInstance {
    instance?: ComponentInternalInstance;
    dropdownSize?: ComputedRef<string>;
    handleClick?: () => void;
    commandHandler?: (...arg: any[]) => void;
    show?: () => void;
    hide?: () => void;
    trigger?: ComputedRef<string>;
    hideOnClick?: ComputedRef<boolean>;
    triggerElm?: ComputedRef<Nullable<HTMLButtonElement>>;
}
export declare const dropdownProps: {
    readonly trigger: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => "click" | "contextmenu" | "focus" | "hover" | import("element-plus/es/components/tooltip").TooltipTriggerType[]) | (() => import("element-plus/es/utils").Arrayable<import("element-plus/es/components/tooltip").TooltipTriggerType>) | ((new (...args: any[]) => "click" | "contextmenu" | "focus" | "hover" | import("element-plus/es/components/tooltip").TooltipTriggerType[]) | (() => import("element-plus/es/utils").Arrayable<import("element-plus/es/components/tooltip").TooltipTriggerType>))[], unknown, unknown, "hover", boolean>;
    readonly triggerKeys: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => string[]) | (() => string[]) | ((new (...args: any[]) => string[]) | (() => string[]))[], unknown, unknown, () => string[], boolean>;
    readonly effect: {
        readonly default: "light";
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string) | (() => import("element-plus/es/components/popper").PopperEffect) | ((new (...args: any[]) => string) | (() => import("element-plus/es/components/popper").PopperEffect))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        readonly __epPropKey: true;
    };
    readonly type: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => "" | "text" | "default" | "success" | "warning" | "info" | "primary" | "danger") | (() => import("element-plus/es/utils").EpPropMergeType<StringConstructor, "" | "text" | "default" | "success" | "warning" | "info" | "primary" | "danger", unknown>) | ((new (...args: any[]) => "" | "text" | "default" | "success" | "warning" | "info" | "primary" | "danger") | (() => import("element-plus/es/utils").EpPropMergeType<StringConstructor, "" | "text" | "default" | "success" | "warning" | "info" | "primary" | "danger", unknown>))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly placement: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => "top" | "bottom" | "left" | "right" | "auto" | "auto-start" | "auto-end" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end") | (() => Placement) | ((new (...args: any[]) => "top" | "bottom" | "left" | "right" | "auto" | "auto-start" | "auto-end" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end") | (() => Placement))[], unknown, unknown, "bottom", boolean>;
    readonly popperOptions: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => Partial<Options>) | (() => Partial<Options>) | ((new (...args: any[]) => Partial<Options>) | (() => Partial<Options>))[], unknown, unknown, () => {}, boolean>;
    readonly id: StringConstructor;
    readonly size: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    readonly splitButton: BooleanConstructor;
    readonly hideOnClick: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly loop: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly showTimeout: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 150, boolean>;
    readonly hideTimeout: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 150, boolean>;
    readonly tabindex: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => string | number) | (() => string | number) | ((new (...args: any[]) => string | number) | (() => string | number))[], unknown, unknown, 0, boolean>;
    readonly maxHeight: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => string | number) | (() => string | number) | ((new (...args: any[]) => string | number) | (() => string | number))[], unknown, unknown, "", boolean>;
    readonly popperClass: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    readonly disabled: BooleanConstructor;
    readonly role: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "dialog" | "menu" | "grid" | "listbox" | "tooltip" | "tree" | "group" | "navigation", unknown, "menu", boolean>;
    readonly buttonProps: {
        readonly type: import("vue").PropType<Partial<ButtonProps>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly teleported: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly persistent: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
};
export declare const dropdownItemProps: {
    readonly command: import("element-plus/es/utils").EpPropFinalized<readonly [ObjectConstructor, StringConstructor, NumberConstructor], unknown, unknown, () => {}, boolean>;
    readonly disabled: BooleanConstructor;
    readonly divided: BooleanConstructor;
    readonly textValue: StringConstructor;
    readonly icon: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component) | ((new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
};
export declare const dropdownMenuProps: {
    onKeydown: {
        readonly type: import("vue").PropType<(e: KeyboardEvent) => void>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
};
export declare const FIRST_KEYS: string[];
export declare const LAST_KEYS: string[];
export declare const FIRST_LAST_KEYS: string[];
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
    data?: ((this: import("vue").CreateComponentPublicInstance<Readonly<import("vue").ExtractPropTypes<{}>>, {}, {}, {}, import("vue").MethodOptions, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, {}, Readonly<import("vue").ExtractPropTypes<{}>>, {}, false, {
        P: {};
        B: {};
        D: {};
        C: {};
        M: {};
        Defaults: {};
    }, Readonly<import("vue").ExtractPropTypes<{}>>, {}, {}, {}, import("vue").MethodOptions, {}>, vm: import("vue").CreateComponentPublicInstance<Readonly<import("vue").ExtractPropTypes<{}>>, {}, {}, {}, import("vue").MethodOptions, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, {}, Readonly<import("vue").ExtractPropTypes<{}>>, {}, false, {
        P: {};
        B: {};
        D: {};
        C: {};
        M: {};
        Defaults: {};
    }, Readonly<import("vue").ExtractPropTypes<{}>>, {}, {}, {}, import("vue").MethodOptions, {}>) => {}) | undefined;
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
    data?: ((this: import("vue").CreateComponentPublicInstance<Readonly<import("vue").ExtractPropTypes<{}>>, {}, {}, {}, import("vue").MethodOptions, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, {}, Readonly<import("vue").ExtractPropTypes<{}>>, {}, false, {
        P: {};
        B: {};
        D: {};
        C: {};
        M: {};
        Defaults: {};
    }, Readonly<import("vue").ExtractPropTypes<{}>>, {}, {}, {}, import("vue").MethodOptions, {}>, vm: import("vue").CreateComponentPublicInstance<Readonly<import("vue").ExtractPropTypes<{}>>, {}, {}, {}, import("vue").MethodOptions, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, {}, Readonly<import("vue").ExtractPropTypes<{}>>, {}, false, {
        P: {};
        B: {};
        D: {};
        C: {};
        M: {};
        Defaults: {};
    }, Readonly<import("vue").ExtractPropTypes<{}>>, {}, {}, {}, import("vue").MethodOptions, {}>) => {}) | undefined;
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
export { ElCollection, ElCollectionItem, COLLECTION_INJECTION_KEY as DROPDOWN_COLLECTION_INJECTION_KEY, COLLECTION_ITEM_INJECTION_KEY as DROPDOWN_COLLECTION_ITEM_INJECTION_KEY, };
