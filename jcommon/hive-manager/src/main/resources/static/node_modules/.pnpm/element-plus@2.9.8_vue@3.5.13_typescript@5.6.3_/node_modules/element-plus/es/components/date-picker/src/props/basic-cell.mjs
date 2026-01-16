import { buildProps, definePropType } from '../../../../utils/vue/props/runtime.mjs';

const basicCellProps = buildProps({
  cell: {
    type: definePropType(Object)
  }
});

export { basicCellProps };
//# sourceMappingURL=basic-cell.mjs.map
