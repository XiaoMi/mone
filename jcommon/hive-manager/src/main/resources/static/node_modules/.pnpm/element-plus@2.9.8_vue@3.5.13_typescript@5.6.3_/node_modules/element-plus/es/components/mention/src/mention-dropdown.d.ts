import type { MentionOption } from './types';
export declare const mentionDropdownProps: {
    options: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => MentionOption[]) | (() => MentionOption[]) | ((new (...args: any[]) => MentionOption[]) | (() => MentionOption[]))[], unknown, unknown, () => never[], boolean>;
    loading: BooleanConstructor;
    disabled: BooleanConstructor;
    contentId: StringConstructor;
    ariaLabel: StringConstructor;
};
export declare const mentionDropdownEmits: {
    select: (option: MentionOption) => boolean;
};
