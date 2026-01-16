import { Editor, EditorEventMap } from 'codemirror';

export type EditorEventNames = Exclude<keyof EditorEventMap, "change">;
interface EditorEventMapWithChange extends EditorEventMap {
    keyHandled: (instance: Editor, name: string, eventObj: Event) => void;
    focus: (instance: Editor, eventObj: FocusEvent) => void;
    blur: (instance: Editor, eventObj: FocusEvent) => void;
    scrollCursorIntoView: (instance: Editor, eventObj: Event) => void;
}
export interface ComponentEventMap {
    "update:value": (value: string) => any;
    change: (value: string, cm: Editor) => {
        value: string;
        cm: Editor;
    };
    input: (value: string) => any;
    ready: (cm: Editor) => Editor;
}
export declare const componentEventMap: ComponentEventMap;
export declare const cmEvts: EditorEventNames[];
export declare const getCmEvts: () => Pick<EditorEventMapWithChange, EditorEventNames>;
export declare const emitOptions: {
    swapDoc: (instance: Editor, oldDoc: import('codemirror').Doc) => void;
    refresh: (instance: Editor) => void;
    focus: (instance: Editor, eventObj: FocusEvent) => void;
    changes: (instance: Editor, changes: import('codemirror').EditorChange[]) => void;
    beforeChange: (instance: Editor, changeObj: import('codemirror').EditorChangeCancellable) => void;
    cursorActivity: (instance: Editor) => void;
    keyHandled: (instance: Editor, name: string, eventObj: Event) => void;
    inputRead: (instance: Editor, changeObj: import('codemirror').EditorChange) => void;
    electricInput: (instance: Editor, line: number) => void;
    beforeSelectionChange: (instance: Editor, obj: import('codemirror').EditorSelectionChange) => void;
    viewportChange: (instance: Editor, from: number, to: number) => void;
    gutterClick: (instance: Editor, line: number, gutter: string, clickEvent: Event) => void;
    gutterContextMenu: (instance: Editor, line: number, gutter: string, contextMenuEvent: MouseEvent) => void;
    blur: (instance: Editor, eventObj: FocusEvent) => void;
    scroll: (instance: Editor) => void;
    optionChange: (instance: Editor, option: keyof import('codemirror').EditorConfiguration) => void;
    scrollCursorIntoView: (instance: Editor, eventObj: Event) => void;
    update: (instance: Editor) => void;
    renderLine: (instance: Editor, lineHandle: import('codemirror').LineHandle, element: HTMLElement) => void;
    overwriteToggle: (instance: Editor, overwrite: boolean) => void;
    startCompletion: (instance: Editor) => void;
    endCompletion: (instance: Editor) => void;
    "update:value": (value: string) => any;
    change: (value: string, cm: Editor) => {
        value: string;
        cm: Editor;
    };
    input: (value: string) => any;
    ready: (cm: Editor) => Editor;
};
export declare const DEFAULT_OPTIONS: {
    mode: string;
    theme: string;
    lineNumbers: boolean;
    smartIndent: boolean;
    indentUnit: number;
    styleActiveLine: boolean;
};
export {};
