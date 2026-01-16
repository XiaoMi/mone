import { EditorConfiguration } from 'codemirror';

export declare interface CmProps {
    value: string;
    options?: EditorConfiguration;
    globalOptions?: EditorConfiguration;
    placeholder?: string;
    border?: boolean;
    width: string | number | null;
    height: string | number | null;
    keepCursorInEnd: boolean;
    merge?: boolean;
    name?: string;
    marker?: () => HTMLElement;
    unseenLines?: Array<any>;
}
