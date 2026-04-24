<template>
  <span v-html="renderedText" class="math-text"></span>
</template>

<script setup>
import { computed } from 'vue'
import { useMathRenderer } from '../../composables/useMathRenderer'

const props = defineProps({
  text: {
    type: String,
    required: true
  },
  autoWrap: {
    type: Boolean,
    default: false
  }
})

const { renderMath, autoWrapMath } = useMathRenderer()

const renderedText = computed(() => {
  let text = props.text
  if (props.autoWrap) {
    text = autoWrapMath(text)
  }
  return renderMath(text)
})
</script>

<style scoped>
.math-text {
  display: inline;
}

.math-text :deep(.katex) {
  font-size: 1.1em;
}

.math-text :deep(.katex-display) {
  margin: 0.5em 0;
}
</style>
