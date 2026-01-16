import type { IOptionV2Props, ISelectV2Props } from './defaults';
import type { InjectionKey, Ref } from 'vue';
import type { Option } from './select.types';
import type { TooltipInstance } from 'element-plus/es/components/tooltip';
export interface SelectV2Context {
    props: ISelectV2Props;
    expanded: Ref<boolean>;
    tooltipRef: Ref<TooltipInstance | undefined>;
    onSelect: (option: Option) => void;
    onHover: (idx?: number) => void;
    onKeyboardNavigate: (direction: 'forward' | 'backward') => void;
    onKeyboardSelect: () => void;
}
export declare const selectV2InjectionKey: InjectionKey<SelectV2Context>;
export type { ISelectV2Props, IOptionV2Props };
