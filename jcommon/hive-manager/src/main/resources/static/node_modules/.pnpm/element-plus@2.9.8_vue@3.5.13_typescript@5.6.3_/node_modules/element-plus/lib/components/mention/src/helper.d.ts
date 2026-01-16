import type { MentionCtx, MentionOption } from './types';
export declare const filterOption: (pattern: string, option: MentionOption) => boolean;
export declare const getMentionCtx: (inputEl: HTMLInputElement | HTMLTextAreaElement, prefix: string | string[], split: string) => MentionCtx | undefined;
/**
 * fork from textarea-caret-position
 * https://github.com/component/textarea-caret-position
 * The MIT License (MIT)
 * Copyright (c) 2015 Jonathan Ong me@jongleberry.com
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
export declare const getCursorPosition: (element: HTMLInputElement | HTMLTextAreaElement, options?: {
    debug: boolean;
    useSelectionEnd: boolean;
}) => {
    top: number;
    left: number;
    height: number;
};
