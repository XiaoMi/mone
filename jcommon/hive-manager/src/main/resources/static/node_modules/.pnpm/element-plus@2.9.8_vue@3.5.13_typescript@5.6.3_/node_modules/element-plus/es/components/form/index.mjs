import Form from './src/form2.mjs';
import FormItem from './src/form-item2.mjs';
export { formEmits, formMetaProps, formProps } from './src/form.mjs';
export { formItemProps, formItemValidateStates } from './src/form-item.mjs';
export { formContextKey, formItemContextKey } from './src/constants.mjs';
import { withInstall, withNoopInstall } from '../../utils/vue/install.mjs';
export { useDisabled, useFormDisabled, useFormSize, useSize } from './src/hooks/use-form-common-props.mjs';
export { useFormItem, useFormItemInputId } from './src/hooks/use-form-item.mjs';

const ElForm = withInstall(Form, {
  FormItem
});
const ElFormItem = withNoopInstall(FormItem);

export { ElForm, ElFormItem, ElForm as default };
//# sourceMappingURL=index.mjs.map
