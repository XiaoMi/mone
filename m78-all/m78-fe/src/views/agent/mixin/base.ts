import 'pixi-spine'
import { v4 as uuidv4 } from 'uuid'
import * as PIXI from 'pixi.js'
import { Spine } from 'pixi-spine'
import { all, characterWidth, characterHeight } from './characters'

let i = 0

export function createCharacter(name: string, colCount: number) {
  const character = new PIXI.Container()
  character.alpha = 0.9

  const style = new PIXI.TextStyle({
    fontFamily: 'Arial',
    fontSize: 18,
    fontWeight: 'bold',
    fill: ['#ffffff', '#00ff99'], // gradient
    stroke: '#4a1850',
    strokeThickness: 5,
    dropShadow: true,
    dropShadowColor: '#000000',
    dropShadowBlur: 4,
    dropShadowAngle: Math.PI / 6,
    dropShadowDistance: 6,
    wordWrap: true,
    wordWrapWidth: 440,
    lineJoin: 'round'
  })

  const id = Math.floor(Math.random() * all.length)
  console.log(id, all[id])
  const m = all[id]
  bindSpine(character, m, name, style)

  character.eventMode = 'static'
  character.cursor = 'pointer'

  // 自定义属性
  character.uuid = uuidv4()

  const row = Math.floor(i / colCount)
  const col = i % colCount
  character.x = col * characterWidth + characterWidth / 2
  character.y = row * characterHeight + characterHeight

  i++
  return character
}

function bindSpine(me: PIXI.Container, m: string, name: string, style: PIXI.TextStyle) {
  PIXI.Assets.load(`${import.meta.env.VITE_APP_STATIC_PATH}images/character/${m}.json`).then(
    (resource) => {
      const text = new PIXI.Text(name, style)
      text.anchor.set(0.5, 0.5)

      const animation = new Spine(resource.spineData)
      animation.scale.set(0.8)

      console.log(animation.state.data)
      me.addChild(animation)
      me.addChild(text)
      animation.position.set(0, 0)
      text.position.set(0, 0)

      me.playAnimation = (actionName: string, loop: boolean = false) => {
        if (!actionName) {
          const actions = animation.state.data.skeletonData.animations
          if (actions && actions.length > 0)
            actionName = actions[Math.floor(Math.random() * actions.length)].name
        }
        if (animation.state.hasAnimation(actionName)) {
          animation.state.setAnimation(0, actionName, loop)
          animation.state.timeScale = 0.6
          animation.autoUpdate = true
        }
      }
    }
  )
}
