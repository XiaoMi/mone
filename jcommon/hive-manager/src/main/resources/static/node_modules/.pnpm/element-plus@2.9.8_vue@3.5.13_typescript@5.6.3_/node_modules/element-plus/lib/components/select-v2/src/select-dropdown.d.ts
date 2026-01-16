import type { DynamicSizeListInstance, FixedSizeListInstance } from 'element-plus/es/components/virtual-list';
import type { Option } from './select.types';
import type { ComponentPublicInstance, ComputedRef, ExtractPropTypes, Ref } from 'vue';
declare const props: {
    loading: BooleanConstructor;
    data: {
        type: ArrayConstructor;
        required: true;
    };
    hoveringIndex: NumberConstructor;
    width: NumberConstructor;
};
interface SelectDropdownExposed {
    listRef: Ref<FixedSizeListInstance | DynamicSizeListInstance | undefined>;
    isSized: ComputedRef<boolean>;
    isItemDisabled: (modelValue: any[] | any, selected: boolean) => boolean;
    isItemHovering: (target: number) => boolean;
    isItemSelected: (modelValue: any[] | any, target: Option) => boolean;
    scrollToItem: (index: number) => void;
    resetScrollTop: () => void;
}
export type SelectDropdownInstance = ComponentPublicInstance<ExtractPropTypes<typeof props>, SelectDropdownExposed>;
declare const _default: import("vue").DefineComponent<{
    loading: BooleanConstructor;
    data: {
        type: ArrayConstructor;
        required: true;
    };
    hoveringIndex: NumberConstructor;
    width: NumberConstructor;
}, () => JSX.Element, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, Record<string, any>, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<ExtractPropTypes<{
    loading: BooleanConstructor;
    data: {
        type: ArrayConstructor;
        required: true;
    };
    hoveringIndex: NumberConstructor;
    width: NumberConstructor;
}>>, {
    loading: boolean;
}>;
export default _default;
