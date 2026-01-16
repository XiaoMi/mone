import type { ExtractPropTypes } from 'vue';
import type TabPane from './tab-pane.vue';
export declare const tabPaneProps: {
    readonly label: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    readonly name: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<readonly [StringConstructor, NumberConstructor], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly closable: BooleanConstructor;
    readonly disabled: BooleanConstructor;
    readonly lazy: BooleanConstructor;
};
export type TabPaneProps = ExtractPropTypes<typeof tabPaneProps>;
export type TabPaneInstance = InstanceType<typeof TabPane> & unknown;
