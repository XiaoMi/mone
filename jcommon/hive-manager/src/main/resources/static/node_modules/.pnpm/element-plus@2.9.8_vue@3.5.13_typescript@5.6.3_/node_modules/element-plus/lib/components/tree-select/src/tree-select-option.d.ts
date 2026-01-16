import { nextTick } from 'vue';
declare const component: import("vue").DefineComponent<{}, any, {}, {}, {
    selectOptionClick(): void;
}, import("vue").ComponentOptionsMixin, ({
    new (...args: any[]): {
        $: import("vue").ComponentInternalInstance;
        $data: {};
        $props: Partial<{
            disabled: boolean;
            created: boolean;
        }> & Omit<Readonly<import("vue").ExtractPropTypes<{
            value: {
                readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(ObjectConstructor | NumberConstructor | StringConstructor | BooleanConstructor)[], unknown, unknown>>;
                readonly required: true;
                readonly validator: ((val: unknown) => boolean) | undefined;
                __epPropKey: true;
            };
            label: {
                readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(NumberConstructor | StringConstructor)[], unknown, unknown>>;
                readonly required: false;
                readonly validator: ((val: unknown) => boolean) | undefined;
                __epPropKey: true;
            };
            created: BooleanConstructor;
            disabled: BooleanConstructor;
        }>> & import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, "disabled" | "created">;
        $attrs: {
            [x: string]: unknown;
        };
        $refs: {
            [x: string]: unknown;
        };
        $slots: import("vue").Slots;
        $root: import("vue").ComponentPublicInstance | null;
        $parent: import("vue").ComponentPublicInstance | null;
        $emit: (event: string, ...args: any[]) => void;
        $el: any;
        $options: import("vue").ComponentOptionsBase<Readonly<import("vue").ExtractPropTypes<{
            value: {
                readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(ObjectConstructor | NumberConstructor | StringConstructor | BooleanConstructor)[], unknown, unknown>>;
                readonly required: true;
                readonly validator: ((val: unknown) => boolean) | undefined;
                __epPropKey: true;
            };
            label: {
                readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(NumberConstructor | StringConstructor)[], unknown, unknown>>;
                readonly required: false;
                readonly validator: ((val: unknown) => boolean) | undefined;
                __epPropKey: true;
            };
            created: BooleanConstructor;
            disabled: BooleanConstructor;
        }>>, {
            ns: {
                namespace: import("vue").ComputedRef<string>;
                b: (blockSuffix?: string) => string;
                e: (element?: string) => string;
                m: (modifier?: string) => string;
                be: (blockSuffix?: string, element?: string) => string;
                em: (element?: string, modifier?: string) => string;
                bm: (blockSuffix?: string, modifier?: string) => string;
                bem: (blockSuffix?: string, element?: string, modifier?: string) => string;
                is: {
                    (name: string, state: boolean | undefined): string;
                    (name: string): string;
                };
                cssVar: (object: Record<string, string>) => Record<string, string>;
                cssVarName: (name: string) => string;
                cssVarBlock: (object: Record<string, string>) => Record<string, string>;
                cssVarBlockName: (name: string) => string;
            };
            id: import("vue").Ref<string>;
            containerKls: import("vue").ComputedRef<string[]>;
            currentLabel: import("vue").ComputedRef<boolean | import("element-plus/es/utils").EpPropMergeType<(NumberConstructor | StringConstructor)[], unknown, unknown>>;
            itemSelected: import("vue").ComputedRef<boolean>;
            isDisabled: import("vue").ComputedRef<boolean>;
            select: import("element-plus/es/components/select").SelectContext;
            visible: import("vue").Ref<boolean>;
            hover: import("vue").Ref<boolean>;
            states: {
                index: number;
                groupDisabled: boolean;
                visible: boolean;
                hover: boolean;
            };
            hoverItem: () => void;
            updateOption: (query: string) => void;
            selectOptionClick: () => void;
        }, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, Record<string, any>, string, {
            disabled: boolean;
            created: boolean;
        }> & {
            beforeCreate?: (() => void) | (() => void)[];
            created?: (() => void) | (() => void)[];
            beforeMount?: (() => void) | (() => void)[];
            mounted?: (() => void) | (() => void)[];
            beforeUpdate?: (() => void) | (() => void)[];
            updated?: (() => void) | (() => void)[];
            activated?: (() => void) | (() => void)[];
            deactivated?: (() => void) | (() => void)[];
            beforeDestroy?: (() => void) | (() => void)[];
            beforeUnmount?: (() => void) | (() => void)[];
            destroyed?: (() => void) | (() => void)[];
            unmounted?: (() => void) | (() => void)[];
            renderTracked?: ((e: import("vue").DebuggerEvent) => void) | ((e: import("vue").DebuggerEvent) => void)[];
            renderTriggered?: ((e: import("vue").DebuggerEvent) => void) | ((e: import("vue").DebuggerEvent) => void)[];
            errorCaptured?: ((err: unknown, instance: import("vue").ComponentPublicInstance | null, info: string) => boolean | void) | ((err: unknown, instance: import("vue").ComponentPublicInstance | null, info: string) => boolean | void)[];
        };
        $forceUpdate: () => void;
        $nextTick: typeof nextTick;
        $watch(source: string | Function, cb: Function, options?: import("vue").WatchOptions): import("vue").WatchStopHandle;
    } & Readonly<import("vue").ExtractPropTypes<{
        value: {
            readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(ObjectConstructor | NumberConstructor | StringConstructor | BooleanConstructor)[], unknown, unknown>>;
            readonly required: true;
            readonly validator: ((val: unknown) => boolean) | undefined;
            __epPropKey: true;
        };
        label: {
            readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(NumberConstructor | StringConstructor)[], unknown, unknown>>;
            readonly required: false;
            readonly validator: ((val: unknown) => boolean) | undefined;
            __epPropKey: true;
        };
        created: BooleanConstructor;
        disabled: BooleanConstructor;
    }>> & import("vue").ShallowUnwrapRef<{
        ns: {
            namespace: import("vue").ComputedRef<string>;
            b: (blockSuffix?: string) => string;
            e: (element?: string) => string;
            m: (modifier?: string) => string;
            be: (blockSuffix?: string, element?: string) => string;
            em: (element?: string, modifier?: string) => string;
            bm: (blockSuffix?: string, modifier?: string) => string;
            bem: (blockSuffix?: string, element?: string, modifier?: string) => string;
            is: {
                (name: string, state: boolean | undefined): string;
                (name: string): string;
            };
            cssVar: (object: Record<string, string>) => Record<string, string>;
            cssVarName: (name: string) => string;
            cssVarBlock: (object: Record<string, string>) => Record<string, string>;
            cssVarBlockName: (name: string) => string;
        };
        id: import("vue").Ref<string>;
        containerKls: import("vue").ComputedRef<string[]>;
        currentLabel: import("vue").ComputedRef<boolean | import("element-plus/es/utils").EpPropMergeType<(NumberConstructor | StringConstructor)[], unknown, unknown>>;
        itemSelected: import("vue").ComputedRef<boolean>;
        isDisabled: import("vue").ComputedRef<boolean>;
        select: import("element-plus/es/components/select").SelectContext;
        visible: import("vue").Ref<boolean>;
        hover: import("vue").Ref<boolean>;
        states: {
            index: number;
            groupDisabled: boolean;
            visible: boolean;
            hover: boolean;
        };
        hoverItem: () => void;
        updateOption: (query: string) => void;
        selectOptionClick: () => void;
    }> & {} & import("vue").ComponentCustomProperties;
    __isFragment?: never;
    __isTeleport?: never;
    __isSuspense?: never;
} & import("vue").ComponentOptionsBase<Readonly<import("vue").ExtractPropTypes<{
    value: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(ObjectConstructor | NumberConstructor | StringConstructor | BooleanConstructor)[], unknown, unknown>>;
        readonly required: true;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    label: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(NumberConstructor | StringConstructor)[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    created: BooleanConstructor;
    disabled: BooleanConstructor;
}>>, {
    ns: {
        namespace: import("vue").ComputedRef<string>;
        b: (blockSuffix?: string) => string;
        e: (element?: string) => string;
        m: (modifier?: string) => string;
        be: (blockSuffix?: string, element?: string) => string;
        em: (element?: string, modifier?: string) => string;
        bm: (blockSuffix?: string, modifier?: string) => string;
        bem: (blockSuffix?: string, element?: string, modifier?: string) => string;
        is: {
            (name: string, state: boolean | undefined): string;
            (name: string): string;
        };
        cssVar: (object: Record<string, string>) => Record<string, string>;
        cssVarName: (name: string) => string;
        cssVarBlock: (object: Record<string, string>) => Record<string, string>;
        cssVarBlockName: (name: string) => string;
    };
    id: import("vue").Ref<string>;
    containerKls: import("vue").ComputedRef<string[]>;
    currentLabel: import("vue").ComputedRef<boolean | import("element-plus/es/utils").EpPropMergeType<(NumberConstructor | StringConstructor)[], unknown, unknown>>;
    itemSelected: import("vue").ComputedRef<boolean>;
    isDisabled: import("vue").ComputedRef<boolean>;
    select: import("element-plus/es/components/select").SelectContext;
    visible: import("vue").Ref<boolean>;
    hover: import("vue").Ref<boolean>;
    states: {
        index: number;
        groupDisabled: boolean;
        visible: boolean;
        hover: boolean;
    };
    hoverItem: () => void;
    updateOption: (query: string) => void;
    selectOptionClick: () => void;
}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, Record<string, any>, string, {
    disabled: boolean;
    created: boolean;
}> & import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps & ((app: import("vue").App, ...options: any[]) => any) & {
    install?: (app: import("vue").App, ...options: any[]) => any;
}) | ({
    new (...args: any[]): {
        $: import("vue").ComponentInternalInstance;
        $data: {};
        $props: Partial<{
            disabled: boolean;
            created: boolean;
        }> & Omit<Readonly<import("vue").ExtractPropTypes<{
            value: {
                readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(ObjectConstructor | NumberConstructor | StringConstructor | BooleanConstructor)[], unknown, unknown>>;
                readonly required: true;
                readonly validator: ((val: unknown) => boolean) | undefined;
                __epPropKey: true;
            };
            label: {
                readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(NumberConstructor | StringConstructor)[], unknown, unknown>>;
                readonly required: false;
                readonly validator: ((val: unknown) => boolean) | undefined;
                __epPropKey: true;
            };
            created: BooleanConstructor;
            disabled: BooleanConstructor;
        }>> & import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, "disabled" | "created">;
        $attrs: {
            [x: string]: unknown;
        };
        $refs: {
            [x: string]: unknown;
        };
        $slots: import("vue").Slots;
        $root: import("vue").ComponentPublicInstance | null;
        $parent: import("vue").ComponentPublicInstance | null;
        $emit: (event: string, ...args: any[]) => void;
        $el: any;
        $options: import("vue").ComponentOptionsBase<Readonly<import("vue").ExtractPropTypes<{
            value: {
                readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(ObjectConstructor | NumberConstructor | StringConstructor | BooleanConstructor)[], unknown, unknown>>;
                readonly required: true;
                readonly validator: ((val: unknown) => boolean) | undefined;
                __epPropKey: true;
            };
            label: {
                readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(NumberConstructor | StringConstructor)[], unknown, unknown>>;
                readonly required: false;
                readonly validator: ((val: unknown) => boolean) | undefined;
                __epPropKey: true;
            };
            created: BooleanConstructor;
            disabled: BooleanConstructor;
        }>>, {
            ns: {
                namespace: import("vue").ComputedRef<string>;
                b: (blockSuffix?: string) => string;
                e: (element?: string) => string;
                m: (modifier?: string) => string;
                be: (blockSuffix?: string, element?: string) => string;
                em: (element?: string, modifier?: string) => string;
                bm: (blockSuffix?: string, modifier?: string) => string;
                bem: (blockSuffix?: string, element?: string, modifier?: string) => string;
                is: {
                    (name: string, state: boolean | undefined): string;
                    (name: string): string;
                };
                cssVar: (object: Record<string, string>) => Record<string, string>;
                cssVarName: (name: string) => string;
                cssVarBlock: (object: Record<string, string>) => Record<string, string>;
                cssVarBlockName: (name: string) => string;
            };
            id: import("vue").Ref<string>;
            containerKls: import("vue").ComputedRef<string[]>;
            currentLabel: import("vue").ComputedRef<boolean | import("element-plus/es/utils").EpPropMergeType<(NumberConstructor | StringConstructor)[], unknown, unknown>>;
            itemSelected: import("vue").ComputedRef<boolean>;
            isDisabled: import("vue").ComputedRef<boolean>;
            select: import("element-plus/es/components/select").SelectContext;
            visible: import("vue").Ref<boolean>;
            hover: import("vue").Ref<boolean>;
            states: {
                index: number;
                groupDisabled: boolean;
                visible: boolean;
                hover: boolean;
            };
            hoverItem: () => void;
            updateOption: (query: string) => void;
            selectOptionClick: () => void;
        }, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, Record<string, any>, string, {
            disabled: boolean;
            created: boolean;
        }> & {
            beforeCreate?: (() => void) | (() => void)[];
            created?: (() => void) | (() => void)[];
            beforeMount?: (() => void) | (() => void)[];
            mounted?: (() => void) | (() => void)[];
            beforeUpdate?: (() => void) | (() => void)[];
            updated?: (() => void) | (() => void)[];
            activated?: (() => void) | (() => void)[];
            deactivated?: (() => void) | (() => void)[];
            beforeDestroy?: (() => void) | (() => void)[];
            beforeUnmount?: (() => void) | (() => void)[];
            destroyed?: (() => void) | (() => void)[];
            unmounted?: (() => void) | (() => void)[];
            renderTracked?: ((e: import("vue").DebuggerEvent) => void) | ((e: import("vue").DebuggerEvent) => void)[];
            renderTriggered?: ((e: import("vue").DebuggerEvent) => void) | ((e: import("vue").DebuggerEvent) => void)[];
            errorCaptured?: ((err: unknown, instance: import("vue").ComponentPublicInstance | null, info: string) => boolean | void) | ((err: unknown, instance: import("vue").ComponentPublicInstance | null, info: string) => boolean | void)[];
        };
        $forceUpdate: () => void;
        $nextTick: typeof nextTick;
        $watch(source: string | Function, cb: Function, options?: import("vue").WatchOptions): import("vue").WatchStopHandle;
    } & Readonly<import("vue").ExtractPropTypes<{
        value: {
            readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(ObjectConstructor | NumberConstructor | StringConstructor | BooleanConstructor)[], unknown, unknown>>;
            readonly required: true;
            readonly validator: ((val: unknown) => boolean) | undefined;
            __epPropKey: true;
        };
        label: {
            readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(NumberConstructor | StringConstructor)[], unknown, unknown>>;
            readonly required: false;
            readonly validator: ((val: unknown) => boolean) | undefined;
            __epPropKey: true;
        };
        created: BooleanConstructor;
        disabled: BooleanConstructor;
    }>> & import("vue").ShallowUnwrapRef<{
        ns: {
            namespace: import("vue").ComputedRef<string>;
            b: (blockSuffix?: string) => string;
            e: (element?: string) => string;
            m: (modifier?: string) => string;
            be: (blockSuffix?: string, element?: string) => string;
            em: (element?: string, modifier?: string) => string;
            bm: (blockSuffix?: string, modifier?: string) => string;
            bem: (blockSuffix?: string, element?: string, modifier?: string) => string;
            is: {
                (name: string, state: boolean | undefined): string;
                (name: string): string;
            };
            cssVar: (object: Record<string, string>) => Record<string, string>;
            cssVarName: (name: string) => string;
            cssVarBlock: (object: Record<string, string>) => Record<string, string>;
            cssVarBlockName: (name: string) => string;
        };
        id: import("vue").Ref<string>;
        containerKls: import("vue").ComputedRef<string[]>;
        currentLabel: import("vue").ComputedRef<boolean | import("element-plus/es/utils").EpPropMergeType<(NumberConstructor | StringConstructor)[], unknown, unknown>>;
        itemSelected: import("vue").ComputedRef<boolean>;
        isDisabled: import("vue").ComputedRef<boolean>;
        select: import("element-plus/es/components/select").SelectContext;
        visible: import("vue").Ref<boolean>;
        hover: import("vue").Ref<boolean>;
        states: {
            index: number;
            groupDisabled: boolean;
            visible: boolean;
            hover: boolean;
        };
        hoverItem: () => void;
        updateOption: (query: string) => void;
        selectOptionClick: () => void;
    }> & {} & import("vue").ComponentCustomProperties;
    __isFragment?: never;
    __isTeleport?: never;
    __isSuspense?: never;
} & import("vue").ComponentOptionsBase<Readonly<import("vue").ExtractPropTypes<{
    value: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(ObjectConstructor | NumberConstructor | StringConstructor | BooleanConstructor)[], unknown, unknown>>;
        readonly required: true;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    label: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(NumberConstructor | StringConstructor)[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    created: BooleanConstructor;
    disabled: BooleanConstructor;
}>>, {
    ns: {
        namespace: import("vue").ComputedRef<string>;
        b: (blockSuffix?: string) => string;
        e: (element?: string) => string;
        m: (modifier?: string) => string;
        be: (blockSuffix?: string, element?: string) => string;
        em: (element?: string, modifier?: string) => string;
        bm: (blockSuffix?: string, modifier?: string) => string;
        bem: (blockSuffix?: string, element?: string, modifier?: string) => string;
        is: {
            (name: string, state: boolean | undefined): string;
            (name: string): string;
        };
        cssVar: (object: Record<string, string>) => Record<string, string>;
        cssVarName: (name: string) => string;
        cssVarBlock: (object: Record<string, string>) => Record<string, string>;
        cssVarBlockName: (name: string) => string;
    };
    id: import("vue").Ref<string>;
    containerKls: import("vue").ComputedRef<string[]>;
    currentLabel: import("vue").ComputedRef<boolean | import("element-plus/es/utils").EpPropMergeType<(NumberConstructor | StringConstructor)[], unknown, unknown>>;
    itemSelected: import("vue").ComputedRef<boolean>;
    isDisabled: import("vue").ComputedRef<boolean>;
    select: import("element-plus/es/components/select").SelectContext;
    visible: import("vue").Ref<boolean>;
    hover: import("vue").Ref<boolean>;
    states: {
        index: number;
        groupDisabled: boolean;
        visible: boolean;
        hover: boolean;
    };
    hoverItem: () => void;
    updateOption: (query: string) => void;
    selectOptionClick: () => void;
}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, Record<string, any>, string, {
    disabled: boolean;
    created: boolean;
}> & import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps & {
    install: (app: import("vue").App, ...options: any[]) => any;
}), import("vue").EmitsOptions, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{}>>, {}>;
export default component;
