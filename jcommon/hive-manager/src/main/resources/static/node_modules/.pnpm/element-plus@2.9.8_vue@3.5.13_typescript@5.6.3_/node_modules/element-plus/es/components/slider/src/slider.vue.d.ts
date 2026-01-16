declare const _default: import("vue").DefineComponent<{
    readonly ariaLabel: StringConstructor;
    readonly modelValue: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => number | number[]) | (() => import("element-plus/es/utils").Arrayable<number>) | ((new (...args: any[]) => number | number[]) | (() => import("element-plus/es/utils").Arrayable<number>))[], unknown, unknown, 0, boolean>;
    readonly id: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, undefined, boolean>;
    readonly min: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 0, boolean>;
    readonly max: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 100, boolean>;
    readonly step: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 1, boolean>;
    readonly showInput: BooleanConstructor;
    readonly showInputControls: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly size: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<StringConstructor, "" | "small" | "default" | "large", never>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly inputSize: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<StringConstructor, "" | "small" | "default" | "large", never>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly showStops: BooleanConstructor;
    readonly showTooltip: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly formatTooltip: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => (val: number) => number | string) | (() => (val: number) => number | string) | {
        (): (val: number) => number | string;
        new (): any;
        readonly prototype: any;
    } | ((new (...args: any[]) => (val: number) => number | string) | (() => (val: number) => number | string) | {
        (): (val: number) => number | string;
        new (): any;
        readonly prototype: any;
    })[], unknown, unknown, undefined, boolean>;
    readonly disabled: BooleanConstructor;
    readonly range: BooleanConstructor;
    readonly vertical: BooleanConstructor;
    readonly height: StringConstructor;
    readonly debounce: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 300, boolean>;
    readonly rangeStartLabel: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, undefined, boolean>;
    readonly rangeEndLabel: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, undefined, boolean>;
    readonly formatValueText: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => (val: number) => string) | (() => (val: number) => string) | {
        (): (val: number) => string;
        new (): any;
        readonly prototype: any;
    } | ((new (...args: any[]) => (val: number) => string) | (() => (val: number) => string) | {
        (): (val: number) => string;
        new (): any;
        readonly prototype: any;
    })[], unknown, unknown, undefined, boolean>;
    readonly tooltipClass: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, undefined, boolean>;
    readonly placement: import("element-plus/es/utils").EpPropFinalized<StringConstructor, import("element-plus").Placement, unknown, "top", boolean>;
    readonly marks: {
        readonly type: import("vue").PropType<{
            [x: number]: import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string | {
                style: import("vue").CSSProperties;
                label: any;
            }) | (() => string | {
                style: import("vue").CSSProperties;
                label: any;
            }) | ((new (...args: any[]) => string | {
                style: import("vue").CSSProperties;
                label: any;
            }) | (() => string | {
                style: import("vue").CSSProperties;
                label: any;
            }))[], unknown, unknown> | undefined;
        }>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly validateEvent: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly persistent: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
}, {
    onSliderClick: (event: MouseEvent | TouchEvent) => void;
}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, {
    "update:modelValue": (value: import("element-plus/es/utils").Arrayable<number>) => void;
    change: (value: import("element-plus/es/utils").Arrayable<number>) => void;
    input: (value: import("element-plus/es/utils").Arrayable<number>) => void;
}, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    readonly ariaLabel: StringConstructor;
    readonly modelValue: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => number | number[]) | (() => import("element-plus/es/utils").Arrayable<number>) | ((new (...args: any[]) => number | number[]) | (() => import("element-plus/es/utils").Arrayable<number>))[], unknown, unknown, 0, boolean>;
    readonly id: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, undefined, boolean>;
    readonly min: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 0, boolean>;
    readonly max: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 100, boolean>;
    readonly step: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 1, boolean>;
    readonly showInput: BooleanConstructor;
    readonly showInputControls: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly size: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<StringConstructor, "" | "small" | "default" | "large", never>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly inputSize: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<StringConstructor, "" | "small" | "default" | "large", never>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly showStops: BooleanConstructor;
    readonly showTooltip: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly formatTooltip: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => (val: number) => number | string) | (() => (val: number) => number | string) | {
        (): (val: number) => number | string;
        new (): any;
        readonly prototype: any;
    } | ((new (...args: any[]) => (val: number) => number | string) | (() => (val: number) => number | string) | {
        (): (val: number) => number | string;
        new (): any;
        readonly prototype: any;
    })[], unknown, unknown, undefined, boolean>;
    readonly disabled: BooleanConstructor;
    readonly range: BooleanConstructor;
    readonly vertical: BooleanConstructor;
    readonly height: StringConstructor;
    readonly debounce: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 300, boolean>;
    readonly rangeStartLabel: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, undefined, boolean>;
    readonly rangeEndLabel: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, undefined, boolean>;
    readonly formatValueText: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => (val: number) => string) | (() => (val: number) => string) | {
        (): (val: number) => string;
        new (): any;
        readonly prototype: any;
    } | ((new (...args: any[]) => (val: number) => string) | (() => (val: number) => string) | {
        (): (val: number) => string;
        new (): any;
        readonly prototype: any;
    })[], unknown, unknown, undefined, boolean>;
    readonly tooltipClass: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, undefined, boolean>;
    readonly placement: import("element-plus/es/utils").EpPropFinalized<StringConstructor, import("element-plus").Placement, unknown, "top", boolean>;
    readonly marks: {
        readonly type: import("vue").PropType<{
            [x: number]: import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string | {
                style: import("vue").CSSProperties;
                label: any;
            }) | (() => string | {
                style: import("vue").CSSProperties;
                label: any;
            }) | ((new (...args: any[]) => string | {
                style: import("vue").CSSProperties;
                label: any;
            }) | (() => string | {
                style: import("vue").CSSProperties;
                label: any;
            }))[], unknown, unknown> | undefined;
        }>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly validateEvent: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly persistent: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
}>> & {
    "onUpdate:modelValue"?: ((value: import("element-plus/es/utils").Arrayable<number>) => any) | undefined;
    onChange?: ((value: import("element-plus/es/utils").Arrayable<number>) => any) | undefined;
    onInput?: ((value: import("element-plus/es/utils").Arrayable<number>) => any) | undefined;
}, {
    readonly disabled: boolean;
    readonly vertical: boolean;
    readonly range: boolean;
    readonly id: string;
    readonly modelValue: import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => number | number[]) | (() => import("element-plus/es/utils").Arrayable<number>) | ((new (...args: any[]) => number | number[]) | (() => import("element-plus/es/utils").Arrayable<number>))[], unknown, unknown>;
    readonly placement: import("element-plus/es/utils").EpPropMergeType<StringConstructor, import("element-plus").Placement, unknown>;
    readonly min: number;
    readonly max: number;
    readonly validateEvent: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    readonly persistent: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    readonly debounce: number;
    readonly step: number;
    readonly showInputControls: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    readonly showTooltip: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    readonly formatTooltip: (val: number) => number | string;
    readonly rangeStartLabel: string;
    readonly rangeEndLabel: string;
    readonly formatValueText: (val: number) => string;
    readonly tooltipClass: string;
    readonly showInput: boolean;
    readonly showStops: boolean;
}>;
export default _default;
