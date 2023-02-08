import { useStore } from "vuex"
import { computed } from "vue"

export function useMapper(mapper, mapFn){
	const store = useStore()
	const storeStateFns = mapFn(mapper)
	const storeState = {}
	
	Object.keys(storeStateFns).forEach(k => {
		const f = storeStateFns[k].bind({$store: store})
		storeState[k] = computed(f)
	})

	return storeState
}
