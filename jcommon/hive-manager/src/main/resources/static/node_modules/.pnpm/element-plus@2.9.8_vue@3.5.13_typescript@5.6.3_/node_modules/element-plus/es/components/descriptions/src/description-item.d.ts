import type { ExtractPropTypes, Slot, VNode } from 'vue';
export declare const descriptionItemProps: {
    label: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, string, boolean>;
    span: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, number, boolean>;
    rowspan: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, number, boolean>;
    width: import("element-plus/es/utils").EpPropFinalized<(NumberConstructor | StringConstructor)[], unknown, unknown, string, boolean>;
    minWidth: import("element-plus/es/utils").EpPropFinalized<(NumberConstructor | StringConstructor)[], unknown, unknown, string, boolean>;
    labelWidth: import("element-plus/es/utils").EpPropFinalized<(NumberConstructor | StringConstructor)[], unknown, unknown, string, boolean>;
    align: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, string, boolean>;
    labelAlign: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, string, boolean>;
    className: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, string, boolean>;
    labelClassName: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, string, boolean>;
};
declare const DescriptionItem: import("vue").DefineComponent<{
    label: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, string, boolean>;
    span: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, number, boolean>;
    rowspan: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, number, boolean>;
    width: import("element-plus/es/utils").EpPropFinalized<(NumberConstructor | StringConstructor)[], unknown, unknown, string, boolean>;
    minWidth: import("element-plus/es/utils").EpPropFinalized<(NumberConstructor | StringConstructor)[], unknown, unknown, string, boolean>;
    labelWidth: import("element-plus/es/utils").EpPropFinalized<(NumberConstructor | StringConstructor)[], unknown, unknown, string, boolean>;
    align: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, string, boolean>;
    labelAlign: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, string, boolean>;
    className: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, string, boolean>;
    labelClassName: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, string, boolean>;
}, unknown, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, Record<string, any>, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<ExtractPropTypes<{
    label: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, string, boolean>;
    span: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, number, boolean>;
    rowspan: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, number, boolean>;
    width: import("element-plus/es/utils").EpPropFinalized<(NumberConstructor | StringConstructor)[], unknown, unknown, string, boolean>;
    minWidth: import("element-plus/es/utils").EpPropFinalized<(NumberConstructor | StringConstructor)[], unknown, unknown, string, boolean>;
    labelWidth: import("element-plus/es/utils").EpPropFinalized<(NumberConstructor | StringConstructor)[], unknown, unknown, string, boolean>;
    align: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, string, boolean>;
    labelAlign: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, string, boolean>;
    className: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, string, boolean>;
    labelClassName: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, string, boolean>;
}>>, {
    label: string;
    span: number;
    width: import("element-plus/es/utils").EpPropMergeType<(NumberConstructor | StringConstructor)[], unknown, unknown>;
    minWidth: import("element-plus/es/utils").EpPropMergeType<(NumberConstructor | StringConstructor)[], unknown, unknown>;
    labelWidth: import("element-plus/es/utils").EpPropMergeType<(NumberConstructor | StringConstructor)[], unknown, unknown>;
    className: string;
    align: string;
    rowspan: number;
    labelAlign: string;
    labelClassName: string;
}>;
export default DescriptionItem;
export type DescriptionItemProps = ExtractPropTypes<typeof descriptionItemProps>;
export type DescriptionItemVNode = VNode & {
    children: {
        [name: string]: Slot;
    } | null;
    props: Partial<DescriptionItemProps> | null;
};
