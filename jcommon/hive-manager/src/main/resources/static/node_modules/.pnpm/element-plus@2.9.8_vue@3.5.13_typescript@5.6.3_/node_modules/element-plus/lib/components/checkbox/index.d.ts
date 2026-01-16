import Checkbox from './src/checkbox.vue';
import CheckboxButton from './src/checkbox-button.vue';
import CheckboxGroup from './src/checkbox-group.vue';
import type { SFCWithInstall } from 'element-plus/es/utils';
export declare const ElCheckbox: SFCWithInstall<typeof Checkbox> & {
    CheckboxButton: typeof CheckboxButton;
    CheckboxGroup: typeof CheckboxGroup;
};
export default ElCheckbox;
export declare const ElCheckboxButton: SFCWithInstall<typeof CheckboxButton>;
export declare const ElCheckboxGroup: SFCWithInstall<typeof CheckboxGroup>;
export * from './src/checkbox-group';
export * from './src/checkbox';
export * from './src/constants';
