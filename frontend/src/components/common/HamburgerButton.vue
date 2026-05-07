<template>
  <button 
    class="hamburger-button" 
    @click="$emit('toggle')"
    :class="{ 'is-active': isOpen }"
    aria-label="Toggle menu"
  >
    <span class="hamburger-line"></span>
    <span class="hamburger-line"></span>
    <span class="hamburger-line"></span>
  </button>
</template>

<script setup>
defineProps({
  isOpen: {
    type: Boolean,
    default: false
  }
})

defineEmits(['toggle'])
</script>

<style scoped>
.hamburger-button {
  display: none; /* Hidden on desktop */
  width: 44px;
  height: 44px;
  background: white;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  cursor: pointer;
  position: relative;
  transition: all var(--transition-normal);
  padding: 0;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 5px;
  z-index: 1002;
}

.hamburger-button:hover {
  background: #f9fafb;
  border-color: #1e40af;
}

.hamburger-button:active {
  transform: scale(0.95);
}

.hamburger-line {
  display: block;
  width: 24px;
  height: 3px;
  background: #1e40af;
  border-radius: 2px;
  transition: all var(--transition-normal);
}

/* Animated X when open */
.hamburger-button.is-active .hamburger-line:nth-child(1) {
  transform: translateY(8px) rotate(45deg);
}

.hamburger-button.is-active .hamburger-line:nth-child(2) {
  opacity: 0;
  transform: translateX(-10px);
}

.hamburger-button.is-active .hamburger-line:nth-child(3) {
  transform: translateY(-8px) rotate(-45deg);
}

/* Show on mobile and tablet */
@media (max-width: 1024px) {
  .hamburger-button {
    display: flex;
  }
}
</style>
