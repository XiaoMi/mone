import { ajaxUpload } from './ajax.mjs';
import { buildProps, definePropType } from '../../../utils/vue/props/runtime.mjs';
import { mutable } from '../../../utils/typescript.mjs';
import { NOOP } from '@vue/shared';

const uploadListTypes = ["text", "picture", "picture-card"];
let fileId = 1;
const genFileId = () => Date.now() + fileId++;
const uploadBaseProps = buildProps({
  action: {
    type: String,
    default: "#"
  },
  headers: {
    type: definePropType(Object)
  },
  method: {
    type: String,
    default: "post"
  },
  data: {
    type: definePropType([Object, Function, Promise]),
    default: () => mutable({})
  },
  multiple: Boolean,
  name: {
    type: String,
    default: "file"
  },
  drag: Boolean,
  withCredentials: Boolean,
  showFileList: {
    type: Boolean,
    default: true
  },
  accept: {
    type: String,
    default: ""
  },
  fileList: {
    type: definePropType(Array),
    default: () => mutable([])
  },
  autoUpload: {
    type: Boolean,
    default: true
  },
  listType: {
    type: String,
    values: uploadListTypes,
    default: "text"
  },
  httpRequest: {
    type: definePropType(Function),
    default: ajaxUpload
  },
  disabled: Boolean,
  limit: Number
});
const uploadProps = buildProps({
  ...uploadBaseProps,
  beforeUpload: {
    type: definePropType(Function),
    default: NOOP
  },
  beforeRemove: {
    type: definePropType(Function)
  },
  onRemove: {
    type: definePropType(Function),
    default: NOOP
  },
  onChange: {
    type: definePropType(Function),
    default: NOOP
  },
  onPreview: {
    type: definePropType(Function),
    default: NOOP
  },
  onSuccess: {
    type: definePropType(Function),
    default: NOOP
  },
  onProgress: {
    type: definePropType(Function),
    default: NOOP
  },
  onError: {
    type: definePropType(Function),
    default: NOOP
  },
  onExceed: {
    type: definePropType(Function),
    default: NOOP
  },
  crossorigin: {
    type: definePropType(String)
  }
});

export { genFileId, uploadBaseProps, uploadListTypes, uploadProps };
//# sourceMappingURL=upload.mjs.map
