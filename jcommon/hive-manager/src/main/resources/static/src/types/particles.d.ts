interface Window {
  particlesJS: (elementId: string, config: any) => void;
}

declare module 'particles.js' {
  export default function particlesJS(elementId: string, config: any): void;
}
