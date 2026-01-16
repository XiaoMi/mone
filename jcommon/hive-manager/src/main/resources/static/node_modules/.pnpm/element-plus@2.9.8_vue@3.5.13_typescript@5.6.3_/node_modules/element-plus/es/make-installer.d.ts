import type { App, Plugin } from '@vue/runtime-core';
import type { ConfigProviderContext } from 'element-plus/es/components/config-provider';
export declare const makeInstaller: (components?: Plugin[]) => {
    version: string;
    install: (app: App, options?: ConfigProviderContext) => void;
};
