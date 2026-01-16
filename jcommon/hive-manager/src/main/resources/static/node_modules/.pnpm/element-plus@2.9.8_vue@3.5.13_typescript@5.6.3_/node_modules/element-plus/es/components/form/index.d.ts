import Form from './src/form.vue';
import FormItem from './src/form-item.vue';
import type { SFCWithInstall } from 'element-plus/es/utils';
export declare const ElForm: SFCWithInstall<typeof Form> & {
    FormItem: typeof FormItem;
};
export default ElForm;
export declare const ElFormItem: SFCWithInstall<typeof FormItem>;
export * from './src/form';
export * from './src/form-item';
export * from './src/types';
export * from './src/constants';
export * from './src/hooks';
export type FormInstance = InstanceType<typeof Form> & unknown;
export type FormItemInstance = InstanceType<typeof FormItem> & unknown;
