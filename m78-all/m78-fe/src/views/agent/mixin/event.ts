import * as PIXI from 'pixi.js'

// ArrowUp?: boolean
// ArrowDown?: boolean
// ArrowLeft?: boolean
// ArrowRight?: boolean
export const keys: Record<string, boolean> = {}

export const bindAppEvent = (app: PIXI.Application) => {
  app.ticker.add(() => {
    if (keys.ArrowUp && app.target) {
      app.target.y -= 1
    }
    if (keys.ArrowDown && app.target) {
      app.target.y += 1
    }
    if (keys.ArrowLeft && app.target) {
      app.target.x -= 1
    }
    if (keys.ArrowRight && app.target) {
      app.target.x += 1
    }
  })
}
