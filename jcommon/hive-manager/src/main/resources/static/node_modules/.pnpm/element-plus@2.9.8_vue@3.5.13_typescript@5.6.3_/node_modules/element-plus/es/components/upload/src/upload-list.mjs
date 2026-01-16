import { uploadListTypes } from './upload.mjs';
import { buildProps, definePropType } from '../../../utils/vue/props/runtime.mjs';
import { mutable } from '../../../utils/typescript.mjs';
import { NOOP } from '@vue/shared';

const uploadListProps = buildProps({
  files: {
    type: definePropType(Array),
    default: () => mutable([])
  },
  disabled: {
    type: Boolean,
    default: false
  },
  handlePreview: {
    type: definePropType(Function),
    default: NOOP
  },
  listType: {
    type: String,
    values: uploadListTypes,
    default: "text"
  },
  crossorigin: {
    type: definePropType(String)
  }
});
const uploadListEmits = {
  remove: (file) => !!file
};

export { uploadListEmits, uploadListProps };
//# sourceMappingURL=upload-list.mjs.map
