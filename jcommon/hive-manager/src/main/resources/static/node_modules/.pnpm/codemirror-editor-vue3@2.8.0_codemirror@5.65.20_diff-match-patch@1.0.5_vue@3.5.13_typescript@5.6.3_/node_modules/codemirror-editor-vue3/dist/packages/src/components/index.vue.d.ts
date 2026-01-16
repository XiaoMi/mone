import { Ref, PropType } from 'vue';
import { Editor, EditorConfiguration } from 'codemirror';

declare const _default: import('vue').DefineComponent<globalThis.ExtractPropTypes<{
    value: {
        type: PropType<string>;
        default: string;
    };
    options: {
        type: PropType<EditorConfiguration>;
        default: () => {
            mode: string;
            theme: string;
            lineNumbers: boolean;
            smartIndent: boolean;
            indentUnit: number;
            styleActiveLine: boolean;
        };
    };
    globalOptions: {
        type: PropType<EditorConfiguration>;
        default: () => {
            mode: string;
            theme: string;
            lineNumbers: boolean;
            smartIndent: boolean;
            indentUnit: number;
            styleActiveLine: boolean;
        };
    };
    placeholder: {
        type: PropType<string>;
        default: string;
    };
    border: {
        type: PropType<boolean>;
        default: boolean;
    };
    width: {
        type: PropType<string | number | null>;
        default: null;
    };
    height: {
        type: PropType<string | number | null>;
        default: null;
    };
    originalStyle: {
        type: PropType<boolean>;
        default: boolean;
    };
    keepCursorInEnd: {
        type: PropType<boolean>;
        default: boolean;
    };
    merge: {
        type: PropType<boolean>;
        default: boolean;
    };
    name: {
        type: PropType<string>;
        default: string;
    };
    marker: {
        type: PropType<() => HTMLElement>;
        default: () => null;
    };
    unseenLines: {
        type: PropType<Array<any>>;
        default: () => never[];
    };
}>, {
    cminstance: Ref<{
        hasFocus: () => boolean;
        findPosH: (start: import('codemirror').Position, amount: number, unit: string, visually: boolean) => {
            line: number;
            ch: number;
            hitSide?: boolean | undefined;
        };
        findPosV: (start: import('codemirror').Position, amount: number, unit: string) => {
            line: number;
            ch: number;
            hitSide?: boolean | undefined;
        };
        findWordAt: (pos: import('codemirror').Position) => import('codemirror').Range;
        setOption: <K extends keyof EditorConfiguration>(option: K, value: EditorConfiguration[K]) => void;
        getOption: <K extends keyof EditorConfiguration>(option: K) => EditorConfiguration[K];
        addKeyMap: (map: string | import('codemirror').KeyMap, bottom?: boolean) => void;
        removeKeyMap: (map: string | import('codemirror').KeyMap) => void;
        addOverlay: (mode: any, options?: {
            opaque?: boolean | undefined;
            priority?: number | undefined;
        }) => void;
        removeOverlay: (mode: any) => void;
        getDoc: () => import('codemirror').Doc;
        swapDoc: (doc: import('codemirror').Doc) => import('codemirror').Doc;
        getValue: (seperator?: string) => string;
        setValue: (content: string) => void;
        getCursor: (start?: string) => import('codemirror').Position;
        setCursor: (pos: import('codemirror').Position | number, ch?: number, options?: {
            bias?: number | undefined;
            origin?: string | undefined;
            scroll?: boolean | undefined;
        }) => void;
        setGutterMarker: (line: any, gutterID: string, value: HTMLElement | null) => import('codemirror').LineHandle;
        clearGutter: (gutterID: string) => void;
        addLineClass: (line: any, where: string, _class_: string) => import('codemirror').LineHandle;
        removeLineClass: (line: any, where: string, class_?: string) => import('codemirror').LineHandle;
        lineAtHeight: (height: number, mode?: import('codemirror').CoordsMode) => number;
        heightAtLine: (line: any, mode?: import('codemirror').CoordsMode, includeWidgets?: boolean) => number;
        lineInfo: (line: any) => {
            line: any;
            handle: any;
            text: string;
            gutterMarkers: any;
            textClass: string;
            bgClass: string;
            wrapClass: string;
            widgets: any;
        };
        addWidget: (pos: import('codemirror').Position, node: HTMLElement, scrollIntoView: boolean) => void;
        addLineWidget: (line: any, node: HTMLElement, options?: import('codemirror').LineWidgetOptions) => import('codemirror').LineWidget;
        setSize: (width: any, height: any) => void;
        scrollTo: (x?: number | null, y?: number | null) => void;
        getScrollInfo: () => import('codemirror').ScrollInfo;
        scrollIntoView: (pos: import('codemirror').Position | null | {
            line: number;
            ch: number;
        } | {
            left: number;
            top: number;
            right: number;
            bottom: number;
        } | {
            from: import('codemirror').Position;
            to: import('codemirror').Position;
        }, margin?: number) => void;
        cursorCoords: (where?: boolean | import('codemirror').Position | null, mode?: import('codemirror').CoordsMode) => {
            left: number;
            top: number;
            bottom: number;
        };
        charCoords: (pos: import('codemirror').Position, mode?: import('codemirror').CoordsMode) => {
            left: number;
            right: number;
            top: number;
            bottom: number;
        };
        coordsChar: (object: {
            left: number;
            top: number;
        }, mode?: import('codemirror').CoordsMode) => import('codemirror').Position;
        defaultTextHeight: () => number;
        defaultCharWidth: () => number;
        getViewport: () => {
            from: number;
            to: number;
        };
        refresh: () => void;
        getModeAt: (pos: import('codemirror').Position) => import('codemirror').Mode<unknown>;
        getTokenAt: (pos: import('codemirror').Position, precise?: boolean) => import('codemirror').Token;
        getTokenTypeAt: (pos: import('codemirror').Position) => string;
        getLineTokens: (line: number, precise?: boolean) => import('codemirror').Token[];
        getStateAfter: (line?: number) => any;
        operation: <T>(fn: () => T) => T;
        startOperation: () => void;
        endOperation: () => void;
        indentLine: (line: number, dir?: string) => void;
        indentSelection: (how: string) => void;
        isReadOnly: () => boolean;
        toggleOverwrite: (value?: boolean) => void;
        execCommand: (name: string) => void;
        focus: () => void;
        phrase: (text: string) => unknown;
        getInputField: () => HTMLTextAreaElement;
        getWrapperElement: () => HTMLElement;
        getScrollerElement: () => HTMLElement;
        getGutterElement: () => HTMLElement;
        on: {
            <T extends keyof import('codemirror').EditorEventMap>(eventName: T, handler: import('codemirror').EditorEventMap[T]): void;
            <K extends import('codemirror').DOMEvent & keyof GlobalEventHandlersEventMap>(eventName: K, handler: (instance: Editor, event: GlobalEventHandlersEventMap[K]) => void): void;
        };
        off: {
            <T extends keyof import('codemirror').EditorEventMap>(eventName: T, handler: import('codemirror').EditorEventMap[T]): void;
            <K extends import('codemirror').DOMEvent & keyof GlobalEventHandlersEventMap>(eventName: K, handler: (instance: Editor, event: GlobalEventHandlersEventMap[K]) => void): void;
        };
        state: any;
        foldCode: (lineOrPos: number | import('codemirror').Position, rangeFindeOrFoldOptions?: import('codemirror/addon/fold/foldcode').FoldRangeFinder | import('codemirror').FoldOptions, force?: "fold" | "unfold") => void;
        isFolded: (pos: import('codemirror').Position) => boolean | undefined;
        foldOption: <K extends keyof import('codemirror').FoldOptions>(option: K) => import('codemirror').FoldOptions[K];
        showHint: (options?: import('codemirror').ShowHintOptions) => void;
        closeHint: () => void;
        performLint: () => void;
        openDialog: (template: string | Node, callback: (value: string, e: Event) => void, options?: import('codemirror/addon/dialog/dialog').OpenDialogOptions) => import('codemirror/addon/dialog/dialog').DialogCloseFunction;
        openNotification: (template: string | Node, options?: import('codemirror/addon/dialog/dialog').OpenNotificationOptions) => import('codemirror/addon/dialog/dialog').DialogCloseFunction;
        openConfirm: (template: string | Node, callbacks: ReadonlyArray<(editor: Editor) => void>, options?: import('codemirror/addon/dialog/dialog').DialogOptions) => import('codemirror/addon/dialog/dialog').DialogCloseFunction;
        modeOption: string | {
            highlightNonStandardPropertyKeywords?: boolean | undefined;
            version?: 2 | 3 | undefined;
            singleLineStringErrors?: boolean | undefined;
            hangingIndent?: number | undefined;
            singleOperators?: unknown | undefined;
            singleDelimiters?: unknown | undefined;
            doubleOperators?: unknown | undefined;
            doubleDelimiters?: unknown | undefined;
            tripleDelimiters?: unknown | undefined;
            identifiers?: unknown | undefined;
            extra_keywords?: string[] | undefined;
            extra_builtins?: string[] | undefined;
            useCPP?: boolean | undefined;
            base?: string | undefined;
            tags?: {
                [key: string]: unknown;
            } | undefined;
            json?: boolean | undefined;
            jsonld?: boolean | undefined;
            typescript?: boolean | undefined;
            trackScope?: boolean | undefined;
            statementIndent?: boolean | undefined;
            wordCharacters?: unknown | undefined;
            highlightFormatting?: boolean | undefined;
            maxBlockquoteDepth?: boolean | undefined;
            xml?: boolean | undefined;
            fencedCodeBlockHighlighting?: boolean | undefined;
            fencedCodeBlockDefaultMode?: string | undefined;
            tokenTypeOverrides?: unknown | undefined;
            allowAtxHeaderWithoutSpace?: boolean | undefined;
            gitHubSpice?: boolean | undefined;
            taskLists?: boolean | undefined;
            strikethrough?: boolean | undefined;
            emoji?: boolean | undefined;
            leftDelimiter?: string | undefined;
            rightDelimiter?: string | undefined;
            baseMode?: string | undefined;
            inMathMode?: boolean | undefined;
            noIndentKeywords?: unknown | undefined;
            atoms?: unknown | undefined;
            hooks?: unknown | undefined;
            multiLineStrings?: boolean | undefined;
            htmlMode?: boolean | undefined;
            matchClosing?: boolean | undefined;
            alignCDATA?: boolean | undefined;
            name: string;
        };
        getRange: (from: import('codemirror').Position, to: import('codemirror').Position, seperator?: string) => string;
        replaceRange: (replacement: string | string[], from: import('codemirror').Position, to?: import('codemirror').Position, origin?: string) => void;
        getLine: (n: number) => string;
        setLine: (n: number, text: string) => void;
        removeLine: (n: number) => void;
        lineCount: () => number;
        firstLine: () => number;
        lastLine: () => number;
        getLineHandle: (num: number) => import('codemirror').LineHandle;
        getLineNumber: (handle: import('codemirror').LineHandle) => number | null;
        eachLine: {
            (f: (line: import('codemirror').LineHandle) => void): void;
            (start: number, end: number, f: (line: import('codemirror').LineHandle) => void): void;
        };
        markClean: () => void;
        changeGeneration: (closeEvent?: boolean) => number;
        isClean: (generation?: number) => boolean;
        getSelection: () => string;
        getSelections: (lineSep?: string) => string[];
        replaceSelection: (replacement: string, collapse?: string) => void;
        replaceSelections: (replacements: string[], collapse?: string) => void;
        listSelections: () => import('codemirror').Range[];
        somethingSelected: () => boolean;
        setSelection: (anchor: import('codemirror').Position, head?: import('codemirror').Position, options?: {
            bias?: number | undefined;
            origin?: string | undefined;
            scroll?: boolean | undefined;
        }) => void;
        setSelections: (ranges: Array<{
            anchor: import('codemirror').Position;
            head: import('codemirror').Position;
        }>, primary?: number, options?: import('codemirror').SelectionOptions) => void;
        addSelection: (anchor: import('codemirror').Position, head?: import('codemirror').Position) => void;
        extendSelection: (from: import('codemirror').Position, to?: import('codemirror').Position, options?: import('codemirror').SelectionOptions) => void;
        extendSelections: (heads: import('codemirror').Position[], options?: import('codemirror').SelectionOptions) => void;
        extendSelectionsBy: (f: (range: import('codemirror').Range) => import('codemirror').Position) => void;
        setExtending: (value: boolean) => void;
        getExtending: () => boolean;
        linkedDoc: (options: {
            sharedHist?: boolean | undefined;
            from?: number | undefined;
            to?: number | undefined;
            mode?: string | import('codemirror').ModeSpec<import('codemirror').ModeSpecOptions> | undefined;
        }) => import('codemirror').Doc;
        unlinkDoc: (doc: import('codemirror').Doc) => void;
        iterLinkedDocs: (fn: (doc: import('codemirror').Doc, sharedHist: boolean) => void) => void;
        undo: () => void;
        redo: () => void;
        undoSelection: () => void;
        redoSelection: () => void;
        historySize: () => {
            undo: number;
            redo: number;
        };
        clearHistory: () => void;
        getHistory: () => any;
        setHistory: (history: any) => void;
        markText: (from: import('codemirror').Position, to: import('codemirror').Position, options?: import('codemirror').TextMarkerOptions) => import('codemirror').TextMarker<import('codemirror').MarkerRange>;
        setBookmark: (pos: import('codemirror').Position, options?: {
            widget?: HTMLElement | undefined;
            insertLeft?: boolean | undefined;
            shared?: boolean | undefined;
            handleMouseEvents?: boolean | undefined;
        }) => import('codemirror').TextMarker<import('codemirror').Position>;
        findMarks: (from: import('codemirror').Position, to: import('codemirror').Position) => import('codemirror').TextMarker[];
        findMarksAt: (pos: import('codemirror').Position) => import('codemirror').TextMarker[];
        getAllMarks: () => import('codemirror').TextMarker[];
        removeLineWidget: (widget: import('codemirror').LineWidget) => void;
        getMode: () => import('codemirror').Mode<unknown>;
        lineSeparator: () => string;
        posFromIndex: (index: number) => import('codemirror').Position;
        indexFromPos: (object: import('codemirror').Position) => number;
        getSearchCursor: (query: string | RegExp, start?: import('codemirror').Position, caseFold?: boolean) => import('codemirror').SearchCursor;
    } | null, Editor | {
        hasFocus: () => boolean;
        findPosH: (start: import('codemirror').Position, amount: number, unit: string, visually: boolean) => {
            line: number;
            ch: number;
            hitSide?: boolean | undefined;
        };
        findPosV: (start: import('codemirror').Position, amount: number, unit: string) => {
            line: number;
            ch: number;
            hitSide?: boolean | undefined;
        };
        findWordAt: (pos: import('codemirror').Position) => import('codemirror').Range;
        setOption: <K extends keyof EditorConfiguration>(option: K, value: EditorConfiguration[K]) => void;
        getOption: <K extends keyof EditorConfiguration>(option: K) => EditorConfiguration[K];
        addKeyMap: (map: string | import('codemirror').KeyMap, bottom?: boolean) => void;
        removeKeyMap: (map: string | import('codemirror').KeyMap) => void;
        addOverlay: (mode: any, options?: {
            opaque?: boolean | undefined;
            priority?: number | undefined;
        }) => void;
        removeOverlay: (mode: any) => void;
        getDoc: () => import('codemirror').Doc;
        swapDoc: (doc: import('codemirror').Doc) => import('codemirror').Doc;
        getValue: (seperator?: string) => string;
        setValue: (content: string) => void;
        getCursor: (start?: string) => import('codemirror').Position;
        setCursor: (pos: import('codemirror').Position | number, ch?: number, options?: {
            bias?: number | undefined;
            origin?: string | undefined;
            scroll?: boolean | undefined;
        }) => void;
        setGutterMarker: (line: any, gutterID: string, value: HTMLElement | null) => import('codemirror').LineHandle;
        clearGutter: (gutterID: string) => void;
        addLineClass: (line: any, where: string, _class_: string) => import('codemirror').LineHandle;
        removeLineClass: (line: any, where: string, class_?: string) => import('codemirror').LineHandle;
        lineAtHeight: (height: number, mode?: import('codemirror').CoordsMode) => number;
        heightAtLine: (line: any, mode?: import('codemirror').CoordsMode, includeWidgets?: boolean) => number;
        lineInfo: (line: any) => {
            line: any;
            handle: any;
            text: string;
            gutterMarkers: any;
            textClass: string;
            bgClass: string;
            wrapClass: string;
            widgets: any;
        };
        addWidget: (pos: import('codemirror').Position, node: HTMLElement, scrollIntoView: boolean) => void;
        addLineWidget: (line: any, node: HTMLElement, options?: import('codemirror').LineWidgetOptions) => import('codemirror').LineWidget;
        setSize: (width: any, height: any) => void;
        scrollTo: (x?: number | null, y?: number | null) => void;
        getScrollInfo: () => import('codemirror').ScrollInfo;
        scrollIntoView: (pos: import('codemirror').Position | null | {
            line: number;
            ch: number;
        } | {
            left: number;
            top: number;
            right: number;
            bottom: number;
        } | {
            from: import('codemirror').Position;
            to: import('codemirror').Position;
        }, margin?: number) => void;
        cursorCoords: (where?: boolean | import('codemirror').Position | null, mode?: import('codemirror').CoordsMode) => {
            left: number;
            top: number;
            bottom: number;
        };
        charCoords: (pos: import('codemirror').Position, mode?: import('codemirror').CoordsMode) => {
            left: number;
            right: number;
            top: number;
            bottom: number;
        };
        coordsChar: (object: {
            left: number;
            top: number;
        }, mode?: import('codemirror').CoordsMode) => import('codemirror').Position;
        defaultTextHeight: () => number;
        defaultCharWidth: () => number;
        getViewport: () => {
            from: number;
            to: number;
        };
        refresh: () => void;
        getModeAt: (pos: import('codemirror').Position) => import('codemirror').Mode<unknown>;
        getTokenAt: (pos: import('codemirror').Position, precise?: boolean) => import('codemirror').Token;
        getTokenTypeAt: (pos: import('codemirror').Position) => string;
        getLineTokens: (line: number, precise?: boolean) => import('codemirror').Token[];
        getStateAfter: (line?: number) => any;
        operation: <T>(fn: () => T) => T;
        startOperation: () => void;
        endOperation: () => void;
        indentLine: (line: number, dir?: string) => void;
        indentSelection: (how: string) => void;
        isReadOnly: () => boolean;
        toggleOverwrite: (value?: boolean) => void;
        execCommand: (name: string) => void;
        focus: () => void;
        phrase: (text: string) => unknown;
        getInputField: () => HTMLTextAreaElement;
        getWrapperElement: () => HTMLElement;
        getScrollerElement: () => HTMLElement;
        getGutterElement: () => HTMLElement;
        on: {
            <T extends keyof import('codemirror').EditorEventMap>(eventName: T, handler: import('codemirror').EditorEventMap[T]): void;
            <K extends import('codemirror').DOMEvent & keyof GlobalEventHandlersEventMap>(eventName: K, handler: (instance: Editor, event: GlobalEventHandlersEventMap[K]) => void): void;
        };
        off: {
            <T extends keyof import('codemirror').EditorEventMap>(eventName: T, handler: import('codemirror').EditorEventMap[T]): void;
            <K extends import('codemirror').DOMEvent & keyof GlobalEventHandlersEventMap>(eventName: K, handler: (instance: Editor, event: GlobalEventHandlersEventMap[K]) => void): void;
        };
        state: any;
        foldCode: (lineOrPos: number | import('codemirror').Position, rangeFindeOrFoldOptions?: import('codemirror/addon/fold/foldcode').FoldRangeFinder | import('codemirror').FoldOptions, force?: "fold" | "unfold") => void;
        isFolded: (pos: import('codemirror').Position) => boolean | undefined;
        foldOption: <K extends keyof import('codemirror').FoldOptions>(option: K) => import('codemirror').FoldOptions[K];
        showHint: (options?: import('codemirror').ShowHintOptions) => void;
        closeHint: () => void;
        performLint: () => void;
        openDialog: (template: string | Node, callback: (value: string, e: Event) => void, options?: import('codemirror/addon/dialog/dialog').OpenDialogOptions) => import('codemirror/addon/dialog/dialog').DialogCloseFunction;
        openNotification: (template: string | Node, options?: import('codemirror/addon/dialog/dialog').OpenNotificationOptions) => import('codemirror/addon/dialog/dialog').DialogCloseFunction;
        openConfirm: (template: string | Node, callbacks: ReadonlyArray<(editor: Editor) => void>, options?: import('codemirror/addon/dialog/dialog').DialogOptions) => import('codemirror/addon/dialog/dialog').DialogCloseFunction;
        modeOption: string | {
            highlightNonStandardPropertyKeywords?: boolean | undefined;
            version?: 2 | 3 | undefined;
            singleLineStringErrors?: boolean | undefined;
            hangingIndent?: number | undefined;
            singleOperators?: unknown | undefined;
            singleDelimiters?: unknown | undefined;
            doubleOperators?: unknown | undefined;
            doubleDelimiters?: unknown | undefined;
            tripleDelimiters?: unknown | undefined;
            identifiers?: unknown | undefined;
            extra_keywords?: string[] | undefined;
            extra_builtins?: string[] | undefined;
            useCPP?: boolean | undefined;
            base?: string | undefined;
            tags?: {
                [key: string]: unknown;
            } | undefined;
            json?: boolean | undefined;
            jsonld?: boolean | undefined;
            typescript?: boolean | undefined;
            trackScope?: boolean | undefined;
            statementIndent?: boolean | undefined;
            wordCharacters?: unknown | undefined;
            highlightFormatting?: boolean | undefined;
            maxBlockquoteDepth?: boolean | undefined;
            xml?: boolean | undefined;
            fencedCodeBlockHighlighting?: boolean | undefined;
            fencedCodeBlockDefaultMode?: string | undefined;
            tokenTypeOverrides?: unknown | undefined;
            allowAtxHeaderWithoutSpace?: boolean | undefined;
            gitHubSpice?: boolean | undefined;
            taskLists?: boolean | undefined;
            strikethrough?: boolean | undefined;
            emoji?: boolean | undefined;
            leftDelimiter?: string | undefined;
            rightDelimiter?: string | undefined;
            baseMode?: string | undefined;
            inMathMode?: boolean | undefined;
            noIndentKeywords?: unknown | undefined;
            atoms?: unknown | undefined;
            hooks?: unknown | undefined;
            multiLineStrings?: boolean | undefined;
            htmlMode?: boolean | undefined;
            matchClosing?: boolean | undefined;
            alignCDATA?: boolean | undefined;
            name: string;
        };
        getRange: (from: import('codemirror').Position, to: import('codemirror').Position, seperator?: string) => string;
        replaceRange: (replacement: string | string[], from: import('codemirror').Position, to?: import('codemirror').Position, origin?: string) => void;
        getLine: (n: number) => string;
        setLine: (n: number, text: string) => void;
        removeLine: (n: number) => void;
        lineCount: () => number;
        firstLine: () => number;
        lastLine: () => number;
        getLineHandle: (num: number) => import('codemirror').LineHandle;
        getLineNumber: (handle: import('codemirror').LineHandle) => number | null;
        eachLine: {
            (f: (line: import('codemirror').LineHandle) => void): void;
            (start: number, end: number, f: (line: import('codemirror').LineHandle) => void): void;
        };
        markClean: () => void;
        changeGeneration: (closeEvent?: boolean) => number;
        isClean: (generation?: number) => boolean;
        getSelection: () => string;
        getSelections: (lineSep?: string) => string[];
        replaceSelection: (replacement: string, collapse?: string) => void;
        replaceSelections: (replacements: string[], collapse?: string) => void;
        listSelections: () => import('codemirror').Range[];
        somethingSelected: () => boolean;
        setSelection: (anchor: import('codemirror').Position, head?: import('codemirror').Position, options?: {
            bias?: number | undefined;
            origin?: string | undefined;
            scroll?: boolean | undefined;
        }) => void;
        setSelections: (ranges: Array<{
            anchor: import('codemirror').Position;
            head: import('codemirror').Position;
        }>, primary?: number, options?: import('codemirror').SelectionOptions) => void;
        addSelection: (anchor: import('codemirror').Position, head?: import('codemirror').Position) => void;
        extendSelection: (from: import('codemirror').Position, to?: import('codemirror').Position, options?: import('codemirror').SelectionOptions) => void;
        extendSelections: (heads: import('codemirror').Position[], options?: import('codemirror').SelectionOptions) => void;
        extendSelectionsBy: (f: (range: import('codemirror').Range) => import('codemirror').Position) => void;
        setExtending: (value: boolean) => void;
        getExtending: () => boolean;
        linkedDoc: (options: {
            sharedHist?: boolean | undefined;
            from?: number | undefined;
            to?: number | undefined;
            mode?: string | import('codemirror').ModeSpec<import('codemirror').ModeSpecOptions> | undefined;
        }) => import('codemirror').Doc;
        unlinkDoc: (doc: import('codemirror').Doc) => void;
        iterLinkedDocs: (fn: (doc: import('codemirror').Doc, sharedHist: boolean) => void) => void;
        undo: () => void;
        redo: () => void;
        undoSelection: () => void;
        redoSelection: () => void;
        historySize: () => {
            undo: number;
            redo: number;
        };
        clearHistory: () => void;
        getHistory: () => any;
        setHistory: (history: any) => void;
        markText: (from: import('codemirror').Position, to: import('codemirror').Position, options?: import('codemirror').TextMarkerOptions) => import('codemirror').TextMarker<import('codemirror').MarkerRange>;
        setBookmark: (pos: import('codemirror').Position, options?: {
            widget?: HTMLElement | undefined;
            insertLeft?: boolean | undefined;
            shared?: boolean | undefined;
            handleMouseEvents?: boolean | undefined;
        }) => import('codemirror').TextMarker<import('codemirror').Position>;
        findMarks: (from: import('codemirror').Position, to: import('codemirror').Position) => import('codemirror').TextMarker[];
        findMarksAt: (pos: import('codemirror').Position) => import('codemirror').TextMarker[];
        getAllMarks: () => import('codemirror').TextMarker[];
        removeLineWidget: (widget: import('codemirror').LineWidget) => void;
        getMode: () => import('codemirror').Mode<unknown>;
        lineSeparator: () => string;
        posFromIndex: (index: number) => import('codemirror').Position;
        indexFromPos: (object: import('codemirror').Position) => number;
        getSearchCursor: (query: string | RegExp, start?: import('codemirror').Position, caseFold?: boolean) => import('codemirror').SearchCursor;
    } | null>;
    resize: (width?: string | number | null, height?: string | number | null) => void;
    refresh: () => void;
    destroy: () => void;
}, {}, {}, {}, import('vue').ComponentOptionsMixin, import('vue').ComponentOptionsMixin, {
    ready: (cm: Editor) => void;
    input: (value: string) => void;
    swapDoc: (instance: Editor, oldDoc: import('codemirror').Doc) => void;
    refresh: (instance: Editor) => void;
    focus: (instance: Editor, eventObj: FocusEvent) => void;
    change: (value: string, cm: Editor) => void;
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
    optionChange: (instance: Editor, option: keyof EditorConfiguration) => void;
    scrollCursorIntoView: (instance: Editor, eventObj: Event) => void;
    update: (instance: Editor) => void;
    renderLine: (instance: Editor, lineHandle: import('codemirror').LineHandle, element: HTMLElement) => void;
    overwriteToggle: (instance: Editor, overwrite: boolean) => void;
    startCompletion: (instance: Editor) => void;
    endCompletion: (instance: Editor) => void;
    "update:value": (value: string) => void;
}, string, import('vue').PublicProps, Readonly<globalThis.ExtractPropTypes<{
    value: {
        type: PropType<string>;
        default: string;
    };
    options: {
        type: PropType<EditorConfiguration>;
        default: () => {
            mode: string;
            theme: string;
            lineNumbers: boolean;
            smartIndent: boolean;
            indentUnit: number;
            styleActiveLine: boolean;
        };
    };
    globalOptions: {
        type: PropType<EditorConfiguration>;
        default: () => {
            mode: string;
            theme: string;
            lineNumbers: boolean;
            smartIndent: boolean;
            indentUnit: number;
            styleActiveLine: boolean;
        };
    };
    placeholder: {
        type: PropType<string>;
        default: string;
    };
    border: {
        type: PropType<boolean>;
        default: boolean;
    };
    width: {
        type: PropType<string | number | null>;
        default: null;
    };
    height: {
        type: PropType<string | number | null>;
        default: null;
    };
    originalStyle: {
        type: PropType<boolean>;
        default: boolean;
    };
    keepCursorInEnd: {
        type: PropType<boolean>;
        default: boolean;
    };
    merge: {
        type: PropType<boolean>;
        default: boolean;
    };
    name: {
        type: PropType<string>;
        default: string;
    };
    marker: {
        type: PropType<() => HTMLElement>;
        default: () => null;
    };
    unseenLines: {
        type: PropType<Array<any>>;
        default: () => never[];
    };
}>> & Readonly<{
    onReady?: ((cm: Editor) => any) | undefined;
    onInput?: ((value: string) => any) | undefined;
    onSwapDoc?: ((instance: Editor, oldDoc: import('codemirror').Doc) => any) | undefined;
    onRefresh?: ((instance: Editor) => any) | undefined;
    onFocus?: ((instance: Editor, eventObj: FocusEvent) => any) | undefined;
    onChange?: ((value: string, cm: Editor) => any) | undefined;
    onChanges?: ((instance: Editor, changes: import('codemirror').EditorChange[]) => any) | undefined;
    onBeforeChange?: ((instance: Editor, changeObj: import('codemirror').EditorChangeCancellable) => any) | undefined;
    onCursorActivity?: ((instance: Editor) => any) | undefined;
    onKeyHandled?: ((instance: Editor, name: string, eventObj: Event) => any) | undefined;
    onInputRead?: ((instance: Editor, changeObj: import('codemirror').EditorChange) => any) | undefined;
    onElectricInput?: ((instance: Editor, line: number) => any) | undefined;
    onBeforeSelectionChange?: ((instance: Editor, obj: import('codemirror').EditorSelectionChange) => any) | undefined;
    onViewportChange?: ((instance: Editor, from: number, to: number) => any) | undefined;
    onGutterClick?: ((instance: Editor, line: number, gutter: string, clickEvent: Event) => any) | undefined;
    onGutterContextMenu?: ((instance: Editor, line: number, gutter: string, contextMenuEvent: MouseEvent) => any) | undefined;
    onBlur?: ((instance: Editor, eventObj: FocusEvent) => any) | undefined;
    onScroll?: ((instance: Editor) => any) | undefined;
    onOptionChange?: ((instance: Editor, option: keyof EditorConfiguration) => any) | undefined;
    onScrollCursorIntoView?: ((instance: Editor, eventObj: Event) => any) | undefined;
    onUpdate?: ((instance: Editor) => any) | undefined;
    onRenderLine?: ((instance: Editor, lineHandle: import('codemirror').LineHandle, element: HTMLElement) => any) | undefined;
    onOverwriteToggle?: ((instance: Editor, overwrite: boolean) => any) | undefined;
    onStartCompletion?: ((instance: Editor) => any) | undefined;
    onEndCompletion?: ((instance: Editor) => any) | undefined;
    "onUpdate:value"?: ((value: string) => any) | undefined;
}>, {
    name: string;
    value: string;
    options: EditorConfiguration;
    placeholder: string;
    marker: () => HTMLElement;
    globalOptions: EditorConfiguration;
    border: boolean;
    width: string | number | null;
    height: string | number | null;
    originalStyle: boolean;
    keepCursorInEnd: boolean;
    merge: boolean;
    unseenLines: any[];
}, {}, {}, {}, string, import('vue').ComponentProvideOptions, true, {}, any>;
export default _default;
