import type { ExtractPropTypes } from 'vue';
import type Teleport from './teleport.vue';
export declare const teleportProps: {
    readonly to: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string | HTMLElement) | (() => string | HTMLElement) | ((new (...args: any[]) => string | HTMLElement) | (() => string | HTMLElement))[], unknown, unknown>>;
        readonly required: true;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly disabled: BooleanConstructor;
};
export type TeleportProps = ExtractPropTypes<typeof teleportProps>;
export type TeleportInstance = InstanceType<typeof Teleport> & unknown;
