import type { ISelectV2Props } from './token';
import type { Option, SelectStates } from './select.types';
export declare function useAllowCreate(props: ISelectV2Props, states: SelectStates): {
    createNewOption: (query: string) => void;
    removeNewOption: (option: Option) => void;
    selectNewOption: (option: Option) => void;
    clearAllNewOption: () => void;
};
