import { AllowedComponentProps } from 'vue';
import { ComponentCustomProps } from 'vue';
import { ComponentOptions } from 'vue';
import { ComponentPublicInstance } from 'vue';
import { EmitsOptions } from 'vue';
import { Prop } from 'vue';
import { PropType } from 'vue';
import { Ref } from 'vue';
import { ShallowUnwrapRef } from 'vue';
import { VNode } from 'vue';
import { VNodeProps } from 'vue';

export declare interface ClassComponentHooks {
    data?(): object;
    beforeCreate?(): void;
    created?(): void;
    beforeMount?(): void;
    mounted?(): void;
    beforeUnmount?(): void;
    unmounted?(): void;
    beforeUpdate?(): void;
    updated?(): void;
    activated?(): void;
    deactivated?(): void;
    render?(): VNode | void;
    errorCaptured?(err: Error, vm: Vue, info: string): boolean | undefined;
    serverPrefetch?(): Promise<unknown>;
}

export declare function createDecorator(factory: (options: ComponentOptions, key: string, index: number) => void): VueDecorator;

export declare type DefaultFactory<T> = (props: Record<string, unknown>) => T | null | undefined;

export declare type DefaultKeys<P> = {
    [K in keyof P]: P[K] extends WithDefault<any> ? K : never;
}[keyof P];

export declare type ExtractDefaultProps<P> = {
    [K in DefaultKeys<P>]: P[K] extends WithDefault<infer T> ? T : never;
};

export declare type ExtractInstance<T> = T extends VueMixin<infer V> ? V : never;

export declare type ExtractProps<P> = {
    [K in keyof P]: P[K] extends WithDefault<infer T> ? T : P[K];
};

export declare type MixedVueBase<Mixins extends VueMixin[]> = Mixins extends (infer T)[] ? VueConstructor<UnionToIntersection<ExtractInstance<T>> & Vue> : never;

export declare function mixins<T extends VueMixin[]>(...Ctors: T): MixedVueBase<T>;

export declare function Options<V extends Vue>(options: ComponentOptions & ThisType<V>): <VC extends VueConstructor<VueBase>>(target: VC) => VC;

export declare function prop<T>(options: PropOptionsWithDefault<T>): WithDefault<T>;

export declare function prop<T>(options: PropOptionsWithRequired<T>): T;

export declare function prop<T>(options: Prop<T>): T | undefined;

export declare interface PropOptions<T = any, D = T> {
    type?: PropType<T> | true | null;
    required?: boolean;
    default?: D | DefaultFactory<D> | null | undefined | object;
    validator?(value: unknown): boolean;
}

export declare interface PropOptionsWithDefault<T, D = T> extends PropOptions<T, D> {
    default: PropOptions<T, D>['default'];
}

export declare interface PropOptionsWithRequired<T, D = T> extends PropOptions<T, D> {
    required: true;
}

export declare type PublicProps = VNodeProps & AllowedComponentProps & ComponentCustomProps;

export declare function setup<R>(setupFn: () => R): UnwrapSetupValue<UnwrapPromise<R>>;

export declare type UnionToIntersection<U> = (U extends any ? (k: U) => void : never) extends (k: infer I) => void ? I : never;

export declare type UnwrapPromise<T> = T extends Promise<infer R> ? R : T;

export declare type UnwrapSetupValue<T> = T extends Ref<infer R> ? R : ShallowUnwrapRef<T>;

export declare type Vue<Props = unknown, Emits extends EmitsOptions = {}, DefaultProps = {}> = ComponentPublicInstance<Props, {}, {}, {}, {}, Emits, PublicProps, DefaultProps, true> & ClassComponentHooks;

export declare const Vue: VueConstructor;

export declare type VueBase = Vue<unknown, never[]>;

export declare interface VueConstructor<V extends VueBase = Vue> extends VueMixin<V> {
    new (...args: any[]): V;
    registerHooks(keys: string[]): void;
    with<P extends {
        new (): unknown;
    }>(Props: P): VueConstructor<V & VueWithProps<InstanceType<P>>>;
}

export declare interface VueDecorator {
    (Ctor: VueConstructor<VueBase>): void;
    (target: VueBase, key: string): void;
    (target: VueBase, key: string, index: number): void;
}

export declare type VueMixin<V extends VueBase = VueBase> = VueStatic & {
    prototype: V;
};

export declare interface VueStatic {
    /* Excluded from this release type: __c */
    /* Excluded from this release type: __b */
    /* Excluded from this release type: __o */
    /* Excluded from this release type: __d */
    /* Excluded from this release type: __h */
    /* Excluded from this release type: __vccOpts */
    /* Excluded from this release type: render */
    /* Excluded from this release type: ssrRender */
    /* Excluded from this release type: __file */
    /* Excluded from this release type: __cssModules */
    /* Excluded from this release type: __scopeId */
    /* Excluded from this release type: __hmrId */
}

export declare type VueWithProps<P> = Vue<ExtractProps<P>, {}, ExtractDefaultProps<P>> & ExtractProps<P>;

export declare interface WithDefault<T> {
    [withDefaultSymbol]: T;
}

declare const withDefaultSymbol: unique symbol;

export { }
