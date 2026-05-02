<template>
  <transition name="fade">
    <button 
      v-if="isVisible" 
      class="scroll-to-top"
      @click="scrollToTop"
      title="Lên đầu trang"
    >
      ↑
    </button>
  </transition>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const isVisible = ref(false)

const checkScroll = () => {
  isVisible.value = window.scrollY > 300
}

const scrollToTop = () => {
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  })
}

onMounted(() => {
  window.addEventListener('scroll', checkScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', checkScroll)
})
</script>

<style scoped>
.scroll-to-top {
  position: fixed;
  bottom: 30px;
  right: 30px;
  width: 50px;
  height: 50px;
  background: #1e40af;
  color: white;
  border: none;
  border-radius: 50%;
  font-size: 24px;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(30, 64, 175, 0.4);
  transition: all 0.3s;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
}

.scroll-to-top:hover {
  background: #1e3a8a;
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(30, 64, 175, 0.5);
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s, transform 0.3s;
}

.fade-enter-from, .fade-leave-to {
  opacity: 0;
  transform: translateY(10px);
}
</style>
