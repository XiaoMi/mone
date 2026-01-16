import * as sass from './index.js';

export const compile = sass.compile;
export const compileAsync = sass.compileAsync;
export const compileString = sass.compileString;
export const compileStringAsync = sass.compileStringAsync;
export const NodePackageImporter = sass.NodePackageImporter;
export const AsyncCompiler = sass.AsyncCompiler;
export const Compiler = sass.Compiler;
export const initAsyncCompiler = sass.initAsyncCompiler;
export const initCompiler = sass.initCompiler;
export const deprecations = sass.deprecations;
export const Version = sass.Version;
export const Logger = sass.Logger;
export const CalculationInterpolation = sass.CalculationInterpolation;
export const CalculationOperation = sass.CalculationOperation;
export const CalculationOperator = sass.CalculationOperator;
export const SassArgumentList = sass.SassArgumentList;
export const SassBoolean = sass.SassBoolean;
export const SassCalculation = sass.SassCalculation;
export const SassColor = sass.SassColor;
export const SassFunction = sass.SassFunction;
export const SassMixin = sass.SassMixin;
export const SassList = sass.SassList;
export const SassMap = sass.SassMap;
export const SassNumber = sass.SassNumber;
export const SassString = sass.SassString;
export const Value = sass.Value;
export const CustomFunction = sass.CustomFunction;
export const ListSeparator = sass.ListSeparator;
export const sassFalse = sass.sassFalse;
export const sassNull = sass.sassNull;
export const sassTrue = sass.sassTrue;
export const Exception = sass.Exception;
export const PromiseOr = sass.PromiseOr;
export const info = sass.info;
export const render = sass.render;
export const renderSync = sass.renderSync;
export const TRUE = sass.TRUE;
export const FALSE = sass.FALSE;
export const NULL = sass.NULL;
export const types = sass.types;

let printedDefaultExportDeprecation = false;
function defaultExportDeprecation() {
  if (printedDefaultExportDeprecation) return;
  printedDefaultExportDeprecation = true;
  console.error(
    "`import sass from 'sass'` is deprecated.\n" +
      "Please use `import * as sass from 'sass'` instead."
  );
}

export default {
  get compile() {
    defaultExportDeprecation();
    return sass.compile;
  },
  get compileAsync() {
    defaultExportDeprecation();
    return sass.compileAsync;
  },
  get compileString() {
    defaultExportDeprecation();
    return sass.compileString;
  },
  get compileStringAsync() {
    defaultExportDeprecation();
    return sass.compileStringAsync;
  },
  get NodePackageImporter() {
    defaultExportDeprecation();
    return sass.NodePackageImporter;
  },
  get initAsyncCompiler() {
    defaultExportDeprecation();
    return sass.initAsyncCompiler;
  },
  get initCompiler() {
    defaultExportDeprecation();
    return sass.initCompiler;
  },
  get AsyncCompiler() {
    defaultExportDeprecation();
    return sass.AsyncCompiler;
  },
  get Compiler() {
    defaultExportDeprecation();
    return sass.Compiler;
  },
  get deprecations() {
    defaultExportDeprecation();
    return sass.deprecations;
  },
  get Version() {
    defaultExportDeprecation();
    return sass.Version;
  },
  get Logger() {
    defaultExportDeprecation();
    return sass.Logger;
  },
  get CalculationOperation() {
    defaultExportDeprecation();
    return sass.CalculationOperation;
  },
  get CalculationOperator() {
    defaultExportDeprecation();
    return sass.CalculationOperator;
  },
  get CalculationInterpolation() {
    defaultExportDeprecation();
    return sass.CalculationInterpolation;
  },
  get SassArgumentList() {
    defaultExportDeprecation();
    return sass.SassArgumentList;
  },
  get SassBoolean() {
    defaultExportDeprecation();
    return sass.SassBoolean;
  },
  get SassCalculation() {
    defaultExportDeprecation();
    return sass.SassCalculation;
  },
  get SassColor() {
    defaultExportDeprecation();
    return sass.SassColor;
  },
  get SassFunction() {
    defaultExportDeprecation();
    return sass.SassFunction;
  },
  get SassMixin() {
    defaultExportDeprecation();
    return sass.SassMixin;
  },
  get SassList() {
    defaultExportDeprecation();
    return sass.SassList;
  },
  get SassMap() {
    defaultExportDeprecation();
    return sass.SassMap;
  },
  get SassNumber() {
    defaultExportDeprecation();
    return sass.SassNumber;
  },
  get SassString() {
    defaultExportDeprecation();
    return sass.SassString;
  },
  get Value() {
    defaultExportDeprecation();
    return sass.Value;
  },
  get CustomFunction() {
    defaultExportDeprecation();
    return sass.CustomFunction;
  },
  get ListSeparator() {
    defaultExportDeprecation();
    return sass.ListSeparator;
  },
  get sassFalse() {
    defaultExportDeprecation();
    return sass.sassFalse;
  },
  get sassNull() {
    defaultExportDeprecation();
    return sass.sassNull;
  },
  get sassTrue() {
    defaultExportDeprecation();
    return sass.sassTrue;
  },
  get Exception() {
    defaultExportDeprecation();
    return sass.Exception;
  },
  get PromiseOr() {
    defaultExportDeprecation();
    return sass.PromiseOr;
  },
  get info() {
    defaultExportDeprecation();
    return sass.info;
  },
  get render() {
    defaultExportDeprecation();
    return sass.render;
  },
  get renderSync() {
    defaultExportDeprecation();
    return sass.renderSync;
  },
  get TRUE() {
    defaultExportDeprecation();
    return sass.TRUE;
  },
  get FALSE() {
    defaultExportDeprecation();
    return sass.FALSE;
  },
  get NULL() {
    defaultExportDeprecation();
    return sass.NULL;
  },
  get types() {
    defaultExportDeprecation();
    return sass.types;
  },
};
