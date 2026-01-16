import type { ExtractPropTypes, StyleValue, VNode, VNodeChild } from 'vue';
import type { Arrayable } from 'element-plus/es/utils';
export declare const spaceProps: {
    readonly direction: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "horizontal" | "vertical", unknown, "horizontal", boolean>;
    readonly class: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => string | Record<string, boolean> | (string | Record<string, boolean>)[]) | (() => Arrayable<string | Record<string, boolean>>) | ((new (...args: any[]) => string | Record<string, boolean> | (string | Record<string, boolean>)[]) | (() => Arrayable<string | Record<string, boolean>>))[], unknown, unknown, "", boolean>;
    readonly style: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => string | import("vue").CSSProperties | StyleValue[]) | (() => StyleValue) | ((new (...args: any[]) => string | import("vue").CSSProperties | StyleValue[]) | (() => StyleValue))[], unknown, unknown, "", boolean>;
    readonly alignment: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => string) | (() => string) | ((new (...args: any[]) => string) | (() => string))[], unknown, unknown, "center", boolean>;
    readonly prefixCls: {
        readonly type: import("vue").PropType<string>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly spacer: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => VNodeChild & {}) | (() => VNodeChild) | ((new (...args: any[]) => VNodeChild & {}) | (() => VNodeChild))[], unknown, string | number | VNode<import("vue").RendererNode, import("vue").RendererElement, {
        [key: string]: any;
    }>, null, boolean>;
    readonly wrap: BooleanConstructor;
    readonly fill: BooleanConstructor;
    readonly fillRatio: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 100, boolean>;
    readonly size: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<readonly [StringConstructor, ArrayConstructor, NumberConstructor], "" | "small" | "default" | "large", number | [number, number]>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
};
export type SpaceProps = ExtractPropTypes<typeof spaceProps>;
declare const Space: import("vue").DefineComponent<{
    readonly direction: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "horizontal" | "vertical", unknown, "horizontal", boolean>;
    readonly class: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => string | Record<string, boolean> | (string | Record<string, boolean>)[]) | (() => Arrayable<string | Record<string, boolean>>) | ((new (...args: any[]) => string | Record<string, boolean> | (string | Record<string, boolean>)[]) | (() => Arrayable<string | Record<string, boolean>>))[], unknown, unknown, "", boolean>;
    readonly style: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => string | import("vue").CSSProperties | StyleValue[]) | (() => StyleValue) | ((new (...args: any[]) => string | import("vue").CSSProperties | StyleValue[]) | (() => StyleValue))[], unknown, unknown, "", boolean>;
    readonly alignment: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => string) | (() => string) | ((new (...args: any[]) => string) | (() => string))[], unknown, unknown, "center", boolean>;
    readonly prefixCls: {
        readonly type: import("vue").PropType<string>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly spacer: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => VNodeChild & {}) | (() => VNodeChild) | ((new (...args: any[]) => VNodeChild & {}) | (() => VNodeChild))[], unknown, string | number | VNode<import("vue").RendererNode, import("vue").RendererElement, {
        [key: string]: any;
    }>, null, boolean>;
    readonly wrap: BooleanConstructor;
    readonly fill: BooleanConstructor;
    readonly fillRatio: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 100, boolean>;
    readonly size: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<readonly [StringConstructor, ArrayConstructor, NumberConstructor], "" | "small" | "default" | "large", number | [number, number]>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
}, () => string | VNode<import("vue").RendererNode, import("vue").RendererElement, {
    [key: string]: any;
}> | {
    [name: string]: unknown;
    $stable?: boolean;
} | null, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, Record<string, any>, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<ExtractPropTypes<{
    readonly direction: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "horizontal" | "vertical", unknown, "horizontal", boolean>;
    readonly class: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => string | Record<string, boolean> | (string | Record<string, boolean>)[]) | (() => Arrayable<string | Record<string, boolean>>) | ((new (...args: any[]) => string | Record<string, boolean> | (string | Record<string, boolean>)[]) | (() => Arrayable<string | Record<string, boolean>>))[], unknown, unknown, "", boolean>;
    readonly style: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => string | import("vue").CSSProperties | StyleValue[]) | (() => StyleValue) | ((new (...args: any[]) => string | import("vue").CSSProperties | StyleValue[]) | (() => StyleValue))[], unknown, unknown, "", boolean>;
    readonly alignment: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => string) | (() => string) | ((new (...args: any[]) => string) | (() => string))[], unknown, unknown, "center", boolean>;
    readonly prefixCls: {
        readonly type: import("vue").PropType<string>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly spacer: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => VNodeChild & {}) | (() => VNodeChild) | ((new (...args: any[]) => VNodeChild & {}) | (() => VNodeChild))[], unknown, string | number | VNode<import("vue").RendererNode, import("vue").RendererElement, {
        [key: string]: any;
    }>, null, boolean>;
    readonly wrap: BooleanConstructor;
    readonly fill: BooleanConstructor;
    readonly fillRatio: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 100, boolean>;
    readonly size: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<readonly [StringConstructor, ArrayConstructor, NumberConstructor], "" | "small" | "default" | "large", number | [number, number]>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
}>>, {
    readonly style: StyleValue;
    readonly spacer: import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => VNodeChild & {}) | (() => VNodeChild) | ((new (...args: any[]) => VNodeChild & {}) | (() => VNodeChild))[], unknown, string | number | VNode<import("vue").RendererNode, import("vue").RendererElement, {
        [key: string]: any;
    }>>;
    readonly fill: boolean;
    readonly direction: import("element-plus/es/utils").EpPropMergeType<StringConstructor, "horizontal" | "vertical", unknown>;
    readonly wrap: boolean;
    readonly class: import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string | Record<string, boolean> | (string | Record<string, boolean>)[]) | (() => Arrayable<string | Record<string, boolean>>) | ((new (...args: any[]) => string | Record<string, boolean> | (string | Record<string, boolean>)[]) | (() => Arrayable<string | Record<string, boolean>>))[], unknown, unknown>;
    readonly alignment: string;
    readonly fillRatio: number;
}>;
export type SpaceInstance = InstanceType<typeof Space> & unknown;
export default Space;
