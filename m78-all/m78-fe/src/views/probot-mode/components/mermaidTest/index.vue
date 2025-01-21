<!-- 使用mermaid实现一个渲染流程图的组件 https://mermaid.nodejs.cn/syntax/flowchart.html -->
 <template>
  <div>
    <div class="mermaid">
      {{ rrr }}
    </div>
    <div class="mermaid">
      {{ str5 }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, nextTick } from 'vue'

const rrr = ref(`
flowchart TD
    Start --> Stop
`)
const str = `
flowchart TD
    Start --> Stop
`
const str5 = `graph TB
    sq[Square shape] --> ci((Circle shape))

    subgraph A
        od>Odd shape]-- Two line<br/>edge comment --> ro
        di{Diamond with <br/> line break} -.-> ro(Rounded<br>square<br>shape)
        di==>ro2(Rounded square shape)
    end

    %% Notice that no text in shape are added here instead that is appended further down
    e --> od3>Really long text with linebreak<br>in an Odd shape]

    %% Comments after double percent signs
    e((Inner / circle<br>and some odd <br>special characters)) --> f(,.?!+-*ز)

    cyr[Cyrillic]-->cyr2((Circle shape Начало));

     classDef green fill:#9f6,stroke:#333,stroke-width:2px;
     classDef orange fill:#f96,stroke:#333,stroke-width:4px;
     class sq,e green
     class di orange`
const str7 = "`This **is** _Markdown_`"
const str8 = "`Line1\nLine2\nLine 3`"
const str6 = `
flowchart LR
    markdown["${str7}"]
    newLines["${str8}"]
    markdown --> newLines
`

const mermaidContainer = ref<HTMLElement | null>(null)
console.log(mermaid)
  onMounted(() => {
      // mermaid.initialize({startOnLoad:true, flowchart: {htmlLabels: true}})
      mermaid.initialize({
        // theme: "neutral",
        startOnLoad: true,
        flowchart:{
            useMaxWidth: true,
            htmlLabels: true
        } 
      })
  })
</script>

<style scoped>
/* Add any necessary styles here */
</style>