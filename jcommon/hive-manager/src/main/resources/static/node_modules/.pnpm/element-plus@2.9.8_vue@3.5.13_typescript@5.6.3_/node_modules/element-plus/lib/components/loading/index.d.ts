import { Loading } from './src/service';
import { vLoading } from './src/directive';
import type { App } from 'vue';
export declare const ElLoading: {
    install(app: App): void;
    directive: import("vue").Directive<import("./src/directive").ElementLoading, import("./src/directive").LoadingBinding>;
    service: (options?: import("./src/types").LoadingOptions) => import("./src/loading").LoadingInstance;
};
export default ElLoading;
export { vLoading, vLoading as ElLoadingDirective, Loading as ElLoadingService };
export * from './src/types';
