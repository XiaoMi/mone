import { FilterPattern, Plugin } from 'vite';
import { VueJSXPluginOptions } from '@vue/babel-plugin-jsx';

interface FilterOptions {
    include?: FilterPattern;
    exclude?: FilterPattern;
}
interface Options extends VueJSXPluginOptions, FilterOptions {
    babelPlugins?: any[];
    /** @default ['defineComponent'] */
    defineComponentName?: string[];
    tsPluginOptions?: any;
}

declare function vueJsxPlugin(options?: Options): Plugin;

export { type FilterOptions, type Options, vueJsxPlugin as default };
