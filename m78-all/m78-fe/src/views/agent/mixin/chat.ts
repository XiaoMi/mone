// import * as PIXI from 'pixi.js'

// export function showChat(me: PIXI.Container, text: string) {
//   // const dialogContainer = new PIXI.Container()
//   // dialogContainer.position.set(100, 100)

//   // const dialogBackground = new PIXI.Graphics()
//   // dialogBackground.beginFill(0xffffff)
//   // dialogBackground.drawRect(0, 0, 300, 100)
//   // dialogBackground.endFill()

//   // dialogContainer.addChild(dialogBackground)

//   const style = new PIXI.TextStyle({
//     fontFamily: 'Arial',
//     fontSize: 16,
//     fill: '#fff',
//     wordWrap: true,
//     wordWrapWidth: 100
//   })
//   const words = new PIXI.Text(text, style)
//   words.anchor.set(0.5, 0.5)
//   // words.x = me.width
//   // words.y = -me.height

//   me.removeChild(me.lastWords)
//   // dialogContainer.x = 0
//   // dialogContainer.y = -2 * me.height - dialogBackground.height
//   // dialogContainer.addChild(words)

//   me.addChild(words)
//   words.position.set(0, -me.height)
//   me.lastWords = words
// }

// export function showError(me: PIXI.Container, message: string) {
//   const style = new PIXI.TextStyle({
//     fontFamily: 'Arial',
//     fontSize: 36,
//     fontWeight: 'bold',
//     fill: 'red'
//   })
//   const text = new PIXI.Text(message, style)
//   text.anchor.set(0.5, 0.5)
//   text.x = me.width / 2
//   text.y = -2 * me.height

//   me.addChild(text)
//   setTimeout(() => {
//     me.removeChild(text)
//   }, 800)
// }
