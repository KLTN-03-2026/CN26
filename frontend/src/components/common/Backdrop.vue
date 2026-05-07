<template>
  <Transition name="backdrop-fade">
    <div 
      v-if="show"
      class="backdrop" 
      @click="$emit('close')"
      aria-label="Close menu"
    ></div>
  </Transition>
</template>

<script setup>
defineProps({
  show: {
    type: Boolean,
    default: false
  }
})

defineEmits(['close'])
</script>

<style scoped>
.backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  z-index: var(--z-backdrop);
  cursor: pointer;
}

/* Fade animation */
.backdrop-fade-enter-active,
.backdrop-fade-leave-active {
  transition: opacity var(--transition-normal);
}

.backdrop-fade-enter-from,
.backdrop-fade-leave-to {
  opacity: 0;
}
</style>
