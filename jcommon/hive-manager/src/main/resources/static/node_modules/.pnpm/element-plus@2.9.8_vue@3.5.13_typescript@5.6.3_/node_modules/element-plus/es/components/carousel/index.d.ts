import Carousel from './src/carousel.vue';
import CarouselItem from './src/carousel-item.vue';
import type { SFCWithInstall } from 'element-plus/es/utils';
export declare const ElCarousel: SFCWithInstall<typeof Carousel> & {
    CarouselItem: typeof CarouselItem;
};
export default ElCarousel;
export declare const ElCarouselItem: SFCWithInstall<typeof CarouselItem>;
export * from './src/carousel';
export * from './src/carousel-item';
export * from './src/constants';
export type { CarouselInstance, CarouselItemInstance } from './src/instance';
