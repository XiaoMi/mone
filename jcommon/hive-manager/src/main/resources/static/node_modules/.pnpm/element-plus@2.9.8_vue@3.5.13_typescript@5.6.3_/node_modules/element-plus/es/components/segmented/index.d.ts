export declare const ElSegmented: import("element-plus/es/utils").SFCWithInstall<{
    new (...args: any[]): {
        $: import("vue").ComponentInternalInstance;
        $data: {};
        $props: Partial<{
            disabled: boolean;
            direction: import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => "horizontal" | "vertical") | (() => "horizontal" | "vertical") | ((new (...args: any[]) => "horizontal" | "vertical") | (() => "horizontal" | "vertical"))[], unknown, unknown>;
            block: boolean;
            props: import("./src/segmented").Props;
            modelValue: import("element-plus/es/utils").EpPropMergeType<(NumberConstructor | StringConstructor | BooleanConstructor)[], unknown, unknown>;
            options: import("./src/types.js").Option[];
            validateEvent: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
        }> & Omit<Readonly<import("vue").ExtractPropTypes<{
            ariaLabel: StringConstructor;
            direction: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => "horizontal" | "vertical") | (() => "horizontal" | "vertical") | ((new (...args: any[]) => "horizontal" | "vertical") | (() => "horizontal" | "vertical"))[], unknown, unknown, string, boolean>;
            options: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./src/types.js").Option[]) | (() => import("./src/types.js").Option[]) | ((new (...args: any[]) => import("./src/types.js").Option[]) | (() => import("./src/types.js").Option[]))[], unknown, unknown, () => never[], boolean>;
            modelValue: import("element-plus/es/utils").EpPropFinalized<(NumberConstructor | StringConstructor | BooleanConstructor)[], unknown, unknown, undefined, boolean>;
            props: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./src/segmented").Props) | (() => import("./src/segmented").Props) | ((new (...args: any[]) => import("./src/segmented").Props) | (() => import("./src/segmented").Props))[], unknown, unknown, () => Required<import("./src/segmented").Props>, boolean>;
            block: BooleanConstructor;
            size: {
                readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<StringConstructor, "" | "small" | "default" | "large", never>>;
                readonly required: false;
                readonly validator: ((val: unknown) => boolean) | undefined;
                __epPropKey: true;
            };
            disabled: BooleanConstructor;
            validateEvent: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, boolean, boolean>;
            id: StringConstructor;
            name: StringConstructor;
        }>> & {
            "onUpdate:modelValue"?: ((val: any) => any) | undefined;
            onChange?: ((val: any) => any) | undefined;
        } & import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, "disabled" | "direction" | "block" | "props" | "modelValue" | "options" | "validateEvent">;
        $attrs: {
            [x: string]: unknown;
        };
        $refs: {
            [x: string]: unknown;
        };
        $slots: import("vue").Slots;
        $root: import("vue").ComponentPublicInstance | null;
        $parent: import("vue").ComponentPublicInstance | null;
        $emit: ((event: "update:modelValue", val: any) => void) & ((event: "change", val: any) => void);
        $el: any;
        $options: import("vue").ComponentOptionsBase<Readonly<import("vue").ExtractPropTypes<{
            ariaLabel: StringConstructor;
            direction: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => "horizontal" | "vertical") | (() => "horizontal" | "vertical") | ((new (...args: any[]) => "horizontal" | "vertical") | (() => "horizontal" | "vertical"))[], unknown, unknown, string, boolean>;
            options: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./src/types.js").Option[]) | (() => import("./src/types.js").Option[]) | ((new (...args: any[]) => import("./src/types.js").Option[]) | (() => import("./src/types.js").Option[]))[], unknown, unknown, () => never[], boolean>;
            modelValue: import("element-plus/es/utils").EpPropFinalized<(NumberConstructor | StringConstructor | BooleanConstructor)[], unknown, unknown, undefined, boolean>;
            props: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./src/segmented").Props) | (() => import("./src/segmented").Props) | ((new (...args: any[]) => import("./src/segmented").Props) | (() => import("./src/segmented").Props))[], unknown, unknown, () => Required<import("./src/segmented").Props>, boolean>;
            block: BooleanConstructor;
            size: {
                readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<StringConstructor, "" | "small" | "default" | "large", never>>;
                readonly required: false;
                readonly validator: ((val: unknown) => boolean) | undefined;
                __epPropKey: true;
            };
            disabled: BooleanConstructor;
            validateEvent: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, boolean, boolean>;
            id: StringConstructor;
            name: StringConstructor;
        }>> & {
            "onUpdate:modelValue"?: ((val: any) => any) | undefined;
            onChange?: ((val: any) => any) | undefined;
        }, {}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, {
            "update:modelValue": (val: any) => void;
            change: (val: any) => void;
        }, string, {
            disabled: boolean;
            direction: import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => "horizontal" | "vertical") | (() => "horizontal" | "vertical") | ((new (...args: any[]) => "horizontal" | "vertical") | (() => "horizontal" | "vertical"))[], unknown, unknown>;
            block: boolean;
            props: import("./src/segmented").Props;
            modelValue: import("element-plus/es/utils").EpPropMergeType<(NumberConstructor | StringConstructor | BooleanConstructor)[], unknown, unknown>;
            options: import("./src/types.js").Option[];
            validateEvent: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
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
        $nextTick: typeof import("vue").nextTick;
        $watch(source: string | Function, cb: Function, options?: import("vue").WatchOptions): import("vue").WatchStopHandle;
    } & Readonly<import("vue").ExtractPropTypes<{
        ariaLabel: StringConstructor;
        direction: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => "horizontal" | "vertical") | (() => "horizontal" | "vertical") | ((new (...args: any[]) => "horizontal" | "vertical") | (() => "horizontal" | "vertical"))[], unknown, unknown, string, boolean>;
        options: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./src/types.js").Option[]) | (() => import("./src/types.js").Option[]) | ((new (...args: any[]) => import("./src/types.js").Option[]) | (() => import("./src/types.js").Option[]))[], unknown, unknown, () => never[], boolean>;
        modelValue: import("element-plus/es/utils").EpPropFinalized<(NumberConstructor | StringConstructor | BooleanConstructor)[], unknown, unknown, undefined, boolean>;
        props: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./src/segmented").Props) | (() => import("./src/segmented").Props) | ((new (...args: any[]) => import("./src/segmented").Props) | (() => import("./src/segmented").Props))[], unknown, unknown, () => Required<import("./src/segmented").Props>, boolean>;
        block: BooleanConstructor;
        size: {
            readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<StringConstructor, "" | "small" | "default" | "large", never>>;
            readonly required: false;
            readonly validator: ((val: unknown) => boolean) | undefined;
            __epPropKey: true;
        };
        disabled: BooleanConstructor;
        validateEvent: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, boolean, boolean>;
        id: StringConstructor;
        name: StringConstructor;
    }>> & {
        "onUpdate:modelValue"?: ((val: any) => any) | undefined;
        onChange?: ((val: any) => any) | undefined;
    } & import("vue").ShallowUnwrapRef<{}> & {} & import("vue").ComponentCustomProperties;
    __isFragment?: never;
    __isTeleport?: never;
    __isSuspense?: never;
} & import("vue").ComponentOptionsBase<Readonly<import("vue").ExtractPropTypes<{
    ariaLabel: StringConstructor;
    direction: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => "horizontal" | "vertical") | (() => "horizontal" | "vertical") | ((new (...args: any[]) => "horizontal" | "vertical") | (() => "horizontal" | "vertical"))[], unknown, unknown, string, boolean>;
    options: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./src/types.js").Option[]) | (() => import("./src/types.js").Option[]) | ((new (...args: any[]) => import("./src/types.js").Option[]) | (() => import("./src/types.js").Option[]))[], unknown, unknown, () => never[], boolean>;
    modelValue: import("element-plus/es/utils").EpPropFinalized<(NumberConstructor | StringConstructor | BooleanConstructor)[], unknown, unknown, undefined, boolean>;
    props: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./src/segmented").Props) | (() => import("./src/segmented").Props) | ((new (...args: any[]) => import("./src/segmented").Props) | (() => import("./src/segmented").Props))[], unknown, unknown, () => Required<import("./src/segmented").Props>, boolean>;
    block: BooleanConstructor;
    size: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<StringConstructor, "" | "small" | "default" | "large", never>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    disabled: BooleanConstructor;
    validateEvent: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, boolean, boolean>;
    id: StringConstructor;
    name: StringConstructor;
}>> & {
    "onUpdate:modelValue"?: ((val: any) => any) | undefined;
    onChange?: ((val: any) => any) | undefined;
}, {}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, {
    "update:modelValue": (val: any) => void;
    change: (val: any) => void;
}, string, {
    disabled: boolean;
    direction: import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => "horizontal" | "vertical") | (() => "horizontal" | "vertical") | ((new (...args: any[]) => "horizontal" | "vertical") | (() => "horizontal" | "vertical"))[], unknown, unknown>;
    block: boolean;
    props: import("./src/segmented").Props;
    modelValue: import("element-plus/es/utils").EpPropMergeType<(NumberConstructor | StringConstructor | BooleanConstructor)[], unknown, unknown>;
    options: import("./src/types.js").Option[];
    validateEvent: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
}> & import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps & (new () => {
    $slots: {
        default?(_: {
            item: import("./src/types.js").Option;
        }): any;
    };
})> & Record<string, any>;
export default ElSegmented;
export * from './src/segmented';
