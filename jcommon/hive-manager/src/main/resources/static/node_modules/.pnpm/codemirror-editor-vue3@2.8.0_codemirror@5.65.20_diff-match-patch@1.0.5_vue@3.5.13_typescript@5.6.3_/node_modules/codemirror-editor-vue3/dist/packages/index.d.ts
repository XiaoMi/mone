import { App } from 'vue';
import { EditorConfiguration, Editor } from 'codemirror';
import { default as VueCodemirror } from '../src/components/index.vue';

interface CmComp {
    cminstance: Editor;
    resize: (width?: string | number | null, height?: string | number | null) => void;
    refresh: () => void;
    destroy: () => void;
}
export type CmComponentRef = CmComp | null;
declare interface InstallConfig {
    options: EditorConfiguration;
    componentName: string;
}
declare const CodeMirror: typeof import("codemirror");
/**
 * Use global components.
 * @example
 * import { createApp } from "vue";
 * const app = createApp(App);
 * app.use(InstallCodeMirror, { componentName: "customCodemirrorComponentName" });
 */
declare const GlobalCmComponent: (app: App, config?: InstallConfig) => App<any>;
declare const InstallCodeMirror: (app: App, config?: InstallConfig) => App<any>;
export * from '../src/components/presetMode/log/utils';
export { CodeMirror, GlobalCmComponent, InstallCodeMirror, VueCodemirror };
export default VueCodemirror;
