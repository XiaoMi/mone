export { castArray as ensureArray } from 'lodash-unified';
import { isArray } from '@vue/shared';

const unique = (arr) => [...new Set(arr)];
const castArray = (arr) => {
  if (!arr && arr !== 0)
    return [];
  return isArray(arr) ? arr : [arr];
};

export { castArray, unique };
//# sourceMappingURL=arrays.mjs.map
