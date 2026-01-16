import CascaderPanel from './src/index.mjs';
export { CASCADER_PANEL_INJECTION_KEY } from './src/types.mjs';
export { CommonProps, DefaultProps, useCascaderConfig } from './src/config.mjs';
import { withInstall } from '../../utils/vue/install.mjs';

const ElCascaderPanel = withInstall(CascaderPanel);

export { ElCascaderPanel, ElCascaderPanel as default };
//# sourceMappingURL=index.mjs.map
