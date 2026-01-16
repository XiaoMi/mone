import type { ExtractPropTypes } from 'vue';
import type TimelineItem from './timeline-item.vue';
export declare const timelineItemProps: {
    readonly timestamp: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    readonly hideTimestamp: BooleanConstructor;
    readonly center: BooleanConstructor;
    readonly placement: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "top" | "bottom", unknown, "bottom", boolean>;
    readonly type: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "success" | "warning" | "info" | "primary" | "danger", unknown, "", boolean>;
    readonly color: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    readonly size: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "normal" | "large", unknown, "normal", boolean>;
    readonly icon: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component) | ((new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly hollow: BooleanConstructor;
};
export type TimelineItemProps = ExtractPropTypes<typeof timelineItemProps>;
export type TimelineItemInstance = InstanceType<typeof TimelineItem> & unknown;
