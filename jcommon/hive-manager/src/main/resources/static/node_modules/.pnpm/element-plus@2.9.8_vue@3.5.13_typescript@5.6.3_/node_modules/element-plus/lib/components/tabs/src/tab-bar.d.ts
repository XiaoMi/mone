import type { ExtractPropTypes } from 'vue';
import type TabBar from './tab-bar.vue';
export declare const tabBarProps: {
    readonly tabs: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => {
        uid: number;
        slots: import("vue").Slots;
        props: {
            readonly label: string;
            readonly disabled: boolean;
            readonly closable: boolean;
            readonly lazy: boolean;
            readonly name?: import("element-plus/es/utils").EpPropMergeType<readonly [StringConstructor, NumberConstructor], unknown, unknown> | undefined;
        };
        paneName: string | number | undefined;
        active: boolean;
        index: string | undefined;
        isClosable: boolean;
    }[]) | (() => {
        uid: number;
        slots: import("vue").Slots;
        props: {
            readonly label: string;
            readonly disabled: boolean;
            readonly closable: boolean;
            readonly lazy: boolean;
            readonly name?: import("element-plus/es/utils").EpPropMergeType<readonly [StringConstructor, NumberConstructor], unknown, unknown> | undefined;
        };
        paneName: string | number | undefined;
        active: boolean;
        index: string | undefined;
        isClosable: boolean;
    }[]) | ((new (...args: any[]) => {
        uid: number;
        slots: import("vue").Slots;
        props: {
            readonly label: string;
            readonly disabled: boolean;
            readonly closable: boolean;
            readonly lazy: boolean;
            readonly name?: import("element-plus/es/utils").EpPropMergeType<readonly [StringConstructor, NumberConstructor], unknown, unknown> | undefined;
        };
        paneName: string | number | undefined;
        active: boolean;
        index: string | undefined;
        isClosable: boolean;
    }[]) | (() => {
        uid: number;
        slots: import("vue").Slots;
        props: {
            readonly label: string;
            readonly disabled: boolean;
            readonly closable: boolean;
            readonly lazy: boolean;
            readonly name?: import("element-plus/es/utils").EpPropMergeType<readonly [StringConstructor, NumberConstructor], unknown, unknown> | undefined;
        };
        paneName: string | number | undefined;
        active: boolean;
        index: string | undefined;
        isClosable: boolean;
    }[]))[], unknown, unknown, () => [], boolean>;
};
export type TabBarProps = ExtractPropTypes<typeof tabBarProps>;
export type TabBarInstance = InstanceType<typeof TabBar> & unknown;
