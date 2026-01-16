import type { ExtractPropTypes } from 'vue';
import type { GetDisabledHours, GetDisabledMinutes, GetDisabledSeconds } from '../common/props';
export declare const disabledTimeListsProps: {
    readonly disabledHours: {
        readonly type: import("vue").PropType<GetDisabledHours>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly disabledMinutes: {
        readonly type: import("vue").PropType<GetDisabledMinutes>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly disabledSeconds: {
        readonly type: import("vue").PropType<GetDisabledSeconds>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
};
export type DisabledTimeListsProps = ExtractPropTypes<typeof disabledTimeListsProps>;
export declare const timePanelSharedProps: {
    readonly visible: BooleanConstructor;
    readonly actualVisible: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, undefined, boolean>;
    readonly format: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
};
export type TimePanelSharedProps = ExtractPropTypes<typeof timePanelSharedProps>;
