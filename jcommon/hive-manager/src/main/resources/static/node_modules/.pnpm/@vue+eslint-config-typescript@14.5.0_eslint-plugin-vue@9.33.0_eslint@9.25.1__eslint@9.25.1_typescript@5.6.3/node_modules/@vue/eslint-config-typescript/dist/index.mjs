import process$1 from 'node:process';
import tseslint from 'typescript-eslint';
import fs from 'node:fs';
import fg from 'fast-glob';
import path from 'node:path';
import vueParser from 'vue-eslint-parser';
import pluginVue from 'eslint-plugin-vue';

const CONFIG_NAMES = [
  "all",
  "base",
  "disableTypeChecked",
  "eslintRecommended",
  "recommended",
  "recommendedTypeChecked",
  "recommendedTypeCheckedOnly",
  "strict",
  "strictTypeChecked",
  "strictTypeCheckedOnly",
  "stylistic",
  "stylisticTypeChecked",
  "stylisticTypeCheckedOnly"
];
function toArray(value) {
  return Array.isArray(value) ? value : [value];
}
class TsEslintConfigForVue {
  /**
   * The name of the config object as defined in `typescript-eslint`.
   */
  configName;
  /**
   * the name property is here to provide better error messages when ESLint throws an error
   */
  name;
  constructor(configName) {
    this.configName = configName;
    this.name = `vueTsConfigs.${configName}`;
  }
  extendsOptions;
  /**
   * Create a new instance of `TsEslintConfigForVue` with the `restOfConfig` merged into it.
   * Should be used when the config is used in the `extends` field of another config.
   */
  asExtendedWith(restOfConfig) {
    const extendedConfig = new TsEslintConfigForVue(this.configName);
    extendedConfig.extendsOptions = {
      name: [restOfConfig.name, this.name].filter(Boolean).join("__"),
      ...restOfConfig.files && { files: restOfConfig.files },
      ...restOfConfig.ignores && { ignores: restOfConfig.ignores }
    };
    return extendedConfig;
  }
  needsTypeChecking() {
    if (this.configName === "disableTypeChecked") {
      return false;
    }
    if (this.configName === "all") {
      return true;
    }
    return this.configName.includes("TypeChecked");
  }
  toConfigArray() {
    return toArray(tseslint.configs[this.configName]).flat().map((config) => ({
      ...config,
      ...config.files && config.files.includes("**/*.ts") ? { files: [...config.files, "**/*.vue"] } : {},
      ...this.extendsOptions
    }));
  }
}
const vueTsConfigs = Object.fromEntries(
  CONFIG_NAMES.map((name) => [
    name,
    new Proxy(new TsEslintConfigForVue(name), {
      // `ownKeys` is called by ESLint when validating the config object.
      // The only possible scenario where this is called is when the placeholder object
      // isn't replaced, which means it's passed to ESLint without being wrapped by
      // `defineConfigWithVueTs()`
      // We throw an error here to provide a better error message to the user.
      ownKeys() {
        throw new Error(
          "Please wrap the config object with `defineConfigWithVueTs()`"
        );
      }
    })
  ])
);

function groupVueFiles(rootDir) {
  const { vueFilesWithScriptTs, otherVueFiles } = fg.sync(["**/*.vue"], {
    cwd: rootDir,
    ignore: ["**/node_modules/**"]
  }).reduce(
    (acc, file) => {
      const absolutePath = path.resolve(rootDir, file);
      const contents = fs.readFileSync(absolutePath, "utf8");
      if (/<script[^>]*\blang\s*=\s*"ts"[^>]*>/i.test(contents)) {
        acc.vueFilesWithScriptTs.push(file);
      } else {
        acc.otherVueFiles.push(file);
      }
      return acc;
    },
    { vueFilesWithScriptTs: [], otherVueFiles: [] }
  );
  return {
    // Only `.vue` files with `<script lang="ts">` or `<script setup lang="ts">` can be type-checked.
    typeCheckable: vueFilesWithScriptTs,
    nonTypeCheckable: otherVueFiles
  };
}

const extraFileExtensions = [".vue"];
function escapePathForGlob(path) {
  return path.replace(/([*?{}[\]()])/g, "[$1]");
}
const additionalRulesRequiringParserServices = [
  "@typescript-eslint/consistent-type-imports",
  "@typescript-eslint/prefer-optional-chain"
];
function createBasicSetupConfigs(tsSyntaxInTemplates, scriptLangs) {
  const mayHaveJsxInSfc = scriptLangs.includes("jsx") || scriptLangs.includes("tsx");
  const parser = {
    // Fallback to espree for js/jsx scripts, as well as SFCs without scripts
    // for better performance.
    js: "espree",
    jsx: "espree",
    ts: tseslint.parser,
    tsx: tseslint.parser
    // Leave the template parser unspecified,
    // so that it could be determined by `<script lang="...">`
  };
  if (!tsSyntaxInTemplates) {
    parser["<template>"] = "espree";
  }
  return [
    // Must set eslint-plugin-vue's base config again no matter whether the user
    // has set it before. Otherwise it would be overridden by the tseslint's config.
    ...pluginVue.configs["flat/base"],
    {
      name: "@vue/typescript/setup",
      files: ["*.vue", "**/*.vue"],
      languageOptions: {
        parser: vueParser,
        parserOptions: {
          parser,
          // The internal espree version used by vue-eslint-parser is 9.x, which supports ES2024 at most.
          // While the parser may try to load the latest version of espree, it's not guaranteed to work.
          // For example, if npm accidentally hoists the older version to the top of the node_modules,
          // or if the user installs the older version of espree at the project root,
          // the older versions would be used.
          // But ESLint 9 allows setting the ecmaVersion to 2025, which may cause a crash.
          // So we set the ecmaVersion to 2024 here to avoid the potential issue.
          ecmaVersion: 2024,
          ecmaFeatures: {
            jsx: mayHaveJsxInSfc
          },
          extraFileExtensions
        }
      },
      rules: {
        "vue/block-lang": [
          "error",
          {
            script: {
              lang: scriptLangs,
              allowNoLang: scriptLangs.includes("js")
            }
          }
        ]
      }
    }
  ];
}
function createSkipTypeCheckingConfigs(nonTypeCheckableVueFiles) {
  const configs = [
    {
      name: "@vue/typescript/skip-type-checking-for-js-files",
      files: ["**/*.js", "**/*.jsx", "**/*.cjs", "**/*.mjs"],
      ...tseslint.configs.disableTypeChecked
    }
  ];
  if (nonTypeCheckableVueFiles.length > 0) {
    configs.push({
      name: "@vue/typescript/skip-type-checking-for-vue-files-without-ts",
      files: nonTypeCheckableVueFiles.map(escapePathForGlob),
      ...tseslint.configs.disableTypeChecked,
      rules: {
        ...tseslint.configs.disableTypeChecked.rules,
        ...Object.fromEntries(
          additionalRulesRequiringParserServices.map((rule) => [rule, "off"])
        )
      }
    });
  }
  return configs;
}
function createTypeCheckingConfigs(typeCheckableVueFiles) {
  const configs = [
    // Vue's own typing inevitably contains some `any`s,
    // so some of the `no-unsafe-*` rules can't be used.
    {
      name: "@vue/typescript/type-aware-rules-in-conflict-with-vue",
      files: ["**/*.ts", "**/*.tsx", "**/*.mts", "**/*.vue"],
      rules: {
        "@typescript-eslint/no-unsafe-argument": "off",
        "@typescript-eslint/no-unsafe-assignment": "off",
        "@typescript-eslint/no-unsafe-call": "off",
        "@typescript-eslint/no-unsafe-member-access": "off",
        "@typescript-eslint/no-unsafe-return": "off"
      }
    },
    {
      name: "@vue/typescript/default-project-service-for-ts-files",
      files: ["**/*.ts", "**/*.tsx", "**/*.mts"],
      languageOptions: {
        parser: tseslint.parser,
        parserOptions: {
          projectService: true,
          extraFileExtensions
        }
      }
    }
  ];
  if (typeCheckableVueFiles.length > 0) {
    configs.push({
      name: "@vue/typescript/default-project-service-for-vue-files",
      files: typeCheckableVueFiles.map(escapePathForGlob),
      languageOptions: {
        parser: vueParser,
        parserOptions: {
          projectService: true,
          parser: tseslint.parser,
          extraFileExtensions
        }
      }
    });
  }
  return configs;
}

function omit(obj, keys) {
  return Object.fromEntries(
    Object.entries(obj).filter(([key]) => !keys.includes(key))
  );
}
const pipe = (value, ...fns) => {
  return fns.reduce((acc, fn) => fn(acc), value);
};
function partition(array, predicate) {
  const truthy = [];
  const falsy = [];
  for (const element of array) {
    if (predicate(element)) {
      truthy.push(element);
    } else {
      falsy.push(element);
    }
  }
  return [truthy, falsy];
}

let projectOptions = {
  tsSyntaxInTemplates: true,
  scriptLangs: ["ts"],
  rootDir: process$1.cwd()
};
function configureVueProject(userOptions) {
  if (userOptions.tsSyntaxInTemplates !== void 0) {
    projectOptions.tsSyntaxInTemplates = userOptions.tsSyntaxInTemplates;
  }
  if (userOptions.scriptLangs) {
    projectOptions.scriptLangs = userOptions.scriptLangs;
  }
  if (userOptions.rootDir) {
    projectOptions.rootDir = userOptions.rootDir;
  }
}
function defineConfigWithVueTs(...configs) {
  return pipe(
    configs,
    flattenConfigs,
    insertAndReorderConfigs,
    resolveVueTsConfigs,
    tseslint.config
    // this might not be necessary, but it doesn't hurt to keep it
  );
}
function flattenConfigs(configs) {
  return configs.flat(Infinity).flatMap(
    (c) => {
      if (c instanceof TsEslintConfigForVue) {
        return c;
      }
      const { extends: extendsArray, ...restOfConfig } = c;
      if (extendsArray == null || extendsArray.length === 0) {
        return restOfConfig;
      }
      const flattenedExtends = extendsArray.flatMap(
        (configToExtend) => Array.isArray(configToExtend) ? flattenConfigs(configToExtend) : [configToExtend]
      );
      return [
        ...flattenedExtends.map((extension) => {
          if (extension instanceof TsEslintConfigForVue) {
            return extension.asExtendedWith(restOfConfig);
          } else {
            const name = [restOfConfig.name, extension.name].filter(Boolean).join("__");
            return {
              ...extension,
              ...restOfConfig.files && { files: restOfConfig.files },
              ...restOfConfig.ignores && { ignores: restOfConfig.ignores },
              ...name && { name }
            };
          }
        }),
        // If restOfConfig contains nothing but `ignores`/`name`, we shouldn't return it
        // Because that would make it a global `ignores` config, which is not what we want
        ...Object.keys(omit(restOfConfig, ["ignores", "name"])).length > 0 ? [restOfConfig] : []
      ];
    }
  );
}
function resolveVueTsConfigs(configs) {
  return configs.flatMap(
    (config) => config instanceof TsEslintConfigForVue ? config.toConfigArray() : config
  );
}
const userTypeAwareConfigs = [];
function insertAndReorderConfigs(configs) {
  const lastExtendedConfigIndex = configs.findLastIndex(
    (config) => config instanceof TsEslintConfigForVue
  );
  if (lastExtendedConfigIndex === -1) {
    return configs;
  }
  const vueFiles = groupVueFiles(projectOptions.rootDir);
  const configsWithoutTypeAwareRules = configs.map(extractTypeAwareRules);
  const hasTypeAwareConfigs = configs.some(
    (config) => config instanceof TsEslintConfigForVue && config.needsTypeChecking()
  );
  const needsTypeAwareLinting = hasTypeAwareConfigs || userTypeAwareConfigs.length > 0;
  return [
    ...configsWithoutTypeAwareRules.slice(0, lastExtendedConfigIndex + 1),
    ...createBasicSetupConfigs(projectOptions.tsSyntaxInTemplates, projectOptions.scriptLangs),
    // user-turned-off type-aware rules must come after the last extended config
    // in case some rules re-enabled by the extended config
    // user-turned-on type-aware rules must come before skipping type-checking
    // in case some rules targets those can't be type-checked files
    // So we extract all type-aware rules by users and put them in the middle
    ...userTypeAwareConfigs,
    ...needsTypeAwareLinting ? [
      ...createSkipTypeCheckingConfigs(vueFiles.nonTypeCheckable),
      ...createTypeCheckingConfigs(vueFiles.typeCheckable)
    ] : [],
    ...configsWithoutTypeAwareRules.slice(lastExtendedConfigIndex + 1)
  ];
}
function extractTypeAwareRules(config) {
  if (config instanceof TsEslintConfigForVue) {
    return config;
  }
  if (!config.rules) {
    return config;
  }
  const [typeAwareRuleEntries, otherRuleEntries] = partition(
    Object.entries(config.rules),
    ([name]) => doesRuleRequireTypeInformation(name)
  );
  if (typeAwareRuleEntries.length > 0) {
    userTypeAwareConfigs.push({
      rules: Object.fromEntries(typeAwareRuleEntries),
      ...config.files && { files: config.files }
    });
  }
  return {
    ...config,
    rules: Object.fromEntries(otherRuleEntries)
  };
}
const rulesRequiringTypeInformation = new Set(
  Object.entries(tseslint.plugin.rules).filter(([_name, def]) => def?.meta?.docs?.requiresTypeChecking).map(([name, _def]) => `@typescript-eslint/${name}`).concat(additionalRulesRequiringParserServices)
);
function doesRuleRequireTypeInformation(ruleName) {
  return rulesRequiringTypeInformation.has(ruleName);
}

function createConfig({
  extends: configNamesToExtend = ["recommended"],
  supportedScriptLangs = { ts: true, tsx: false, js: false, jsx: false },
  rootDir = process.cwd()
} = {}) {
  for (const name of configNamesToExtend) {
    if (!tseslint.configs[name]) {
      const nameInCamelCase = name.replace(
        /-([a-z])/g,
        (_, letter) => letter.toUpperCase()
      );
      if (tseslint.configs[nameInCamelCase]) {
        throw new Error(
          `The config name "${name}" is not supported in "extends". Please use "${nameInCamelCase}" instead.`
        );
      }
      throw new Error(`Unknown config name in "extends": ${name}.`);
    }
  }
  configureVueProject({
    scriptLangs: Object.keys(supportedScriptLangs).filter(
      (lang) => supportedScriptLangs[lang]
    ),
    rootDir
  });
  return defineConfigWithVueTs(
    ...configNamesToExtend.map((name) => vueTsConfigs[name])
  );
}

const defineConfig = defineConfigWithVueTs;

export { configureVueProject, createConfig, createConfig as default, defineConfig, defineConfigWithVueTs, vueTsConfigs };
