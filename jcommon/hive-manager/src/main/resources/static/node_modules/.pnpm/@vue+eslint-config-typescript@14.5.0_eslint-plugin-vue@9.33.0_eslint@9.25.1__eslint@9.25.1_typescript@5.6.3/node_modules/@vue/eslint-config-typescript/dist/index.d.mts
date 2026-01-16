import { TSESLint } from '@typescript-eslint/utils';
import { FlatConfig } from '@typescript-eslint/utils/ts-eslint';

declare const CONFIG_NAMES: readonly ["all", "base", "disableTypeChecked", "eslintRecommended", "recommended", "recommendedTypeChecked", "recommendedTypeCheckedOnly", "strict", "strictTypeChecked", "strictTypeCheckedOnly", "stylistic", "stylisticTypeChecked", "stylisticTypeCheckedOnly"];
type ExtendableConfigName = (typeof CONFIG_NAMES)[number];
/**
 * The options that a config in the `extends` should inherit.
 */
type ExtendsOptions = {
    name?: string;
    files?: (string | string[])[];
    ignores?: string[];
};
declare class TsEslintConfigForVue {
    /**
     * The name of the config object as defined in `typescript-eslint`.
     */
    configName: ExtendableConfigName;
    /**
     * the name property is here to provide better error messages when ESLint throws an error
     */
    name: string;
    constructor(configName: ExtendableConfigName);
    extendsOptions?: ExtendsOptions;
    /**
     * Create a new instance of `TsEslintConfigForVue` with the `restOfConfig` merged into it.
     * Should be used when the config is used in the `extends` field of another config.
     */
    asExtendedWith(restOfConfig: ExtendsOptions): TsEslintConfigForVue;
    needsTypeChecking(): boolean;
    toConfigArray(): FlatConfig.ConfigArray;
}
declare const vueTsConfigs: Record<ExtendableConfigName, TsEslintConfigForVue>;

type ScriptLang = 'ts' | 'tsx' | 'js' | 'jsx';

type ConfigItem = TSESLint.FlatConfig.Config;
type InfiniteDepthConfigWithExtendsAndVueSupport = TsEslintConfigForVue | ConfigItemWithExtendsAndVueSupport | InfiniteDepthConfigWithExtendsAndVueSupport[];
interface ConfigItemWithExtendsAndVueSupport extends ConfigItem {
    extends?: InfiniteDepthConfigWithExtendsAndVueSupport[];
}
type ProjectOptions = {
    /**
     * Whether to parse TypeScript syntax in Vue templates.
     * Defaults to `true`.
     * Setting it to `false` could improve performance.
     * But TypeScript syntax in Vue templates will then lead to syntax errors.
     * Also, type-aware rules won't be applied to expressions in templates in that case.
     */
    tsSyntaxInTemplates?: boolean;
    /**
     * Allowed script languages in `vue` files.
     * Defaults to `['ts']`
     */
    scriptLangs?: ScriptLang[];
    /**
     * The root directory of the project.
     * Defaults to `process.cwd()`.
     */
    rootDir?: string;
};
declare function configureVueProject(userOptions: ProjectOptions): void;
declare function defineConfigWithVueTs(...configs: InfiniteDepthConfigWithExtendsAndVueSupport[]): ConfigItem[];

type ConfigOptions = ProjectOptions & {
    extends?: ExtendableConfigName[];
    supportedScriptLangs?: Record<ScriptLang, boolean>;
};
/**
 * @deprecated Use `defineConfigWithVueTs` + `vueTsConfigs` instead.
 */
declare function createConfig({ extends: configNamesToExtend, supportedScriptLangs, rootDir, }?: ConfigOptions): FlatConfig.ConfigArray;

/**
 * @deprecated `defineConfig` is renamed to `defineConfigWithVueTs` in 14.3.0
 */
declare const defineConfig: typeof defineConfigWithVueTs;

export { configureVueProject, createConfig, createConfig as default, defineConfig, defineConfigWithVueTs, vueTsConfigs };
