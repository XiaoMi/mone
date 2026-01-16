import Select from './src/select.vue';
import Option from './src/option.vue';
import OptionGroup from './src/option-group.vue';
import type { SFCWithInstall } from 'element-plus/es/utils';
export declare const ElSelect: SFCWithInstall<typeof Select> & {
    Option: typeof Option;
    OptionGroup: typeof OptionGroup;
};
export default ElSelect;
export declare const ElOption: SFCWithInstall<typeof Option>;
export declare const ElOptionGroup: SFCWithInstall<typeof OptionGroup>;
export * from './src/token';
export * from './src/select';
export type { SelectContext, OptionPublicInstance as SelectOptionProxy, OptionBasic, } from './src/type';
