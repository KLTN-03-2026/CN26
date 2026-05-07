<template>
  <div class="dashboard-layout">
    <!-- Mobile Header with Hamburger -->
    <header class="mobile-header">
      <HamburgerButton :is-open="isSidebarOpen" @toggle="toggleSidebar" />
      <div class="mobile-header-title">
        <img src="/logo/logo.png" alt="Logo" class="mobile-logo" />
        <span>Hệ Thống Thi</span>
      </div>
    </header>

    <!-- Backdrop for mobile -->
    <Backdrop :show="isSidebarOpen" @close="closeSidebar" />

    <!-- Sidebar -->
    <aside class="sidebar" :class="{ 'is-open': isSidebarOpen }">
      <div class="sidebar-header">
        <img src="/logo/logo.png" alt="Logo" class="sidebar-logo" />
        <h2>Hệ Thống Thi</h2>
        <p>{{ roleLabel }}</p>
        
        <!-- Real-time Clock -->
        <div class="current-time">
          <div class="time-day">{{ currentDay }}</div>
          <div class="time-date">{{ currentDate }}</div>
          <div class="time-clock">{{ currentClock }}</div>
        </div>
      </div>
      <nav class="sidebar-menu">
        <a 
          v-for="item in menuItems" 
          :key="item.key"
          href="#" 
          @click.prevent="navigateTo(item.path)" 
          :class="['menu-item', { active: activeMenu === item.key }]"
        >
          {{ item.label }}
        </a>
      </nav>
      <div class="sidebar-footer">
        <button 
          v-for="btn in footerButtons" 
          :key="btn.label"
          :class="btn.class"
          @click="navigateTo(btn.path)"
          :style="btn.style"
        >
          {{ btn.label }}
        </button>
        <button class="btn-logout" @click="handleLogout" :style="footerButtons.length > 0 ? 'margin-top: 10px;' : ''">
          Đăng xuất
        </button>
      </div>
    </aside>

    <!-- Main Content -->
    <div class="main-content">
      <slot></slot>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth } from '../../composables/useAuth'
import HamburgerButton from '../common/HamburgerButton.vue'
import Backdrop from '../common/Backdrop.vue'

defineProps({
  roleLabel: {
    type: String,
    required: true
  },
  menuItems: {
    type: Array,
    required: true
  },
  activeMenu: {
    type: String,
    default: 'home'
  },
  footerButtons: {
    type: Array,
    default: () => []
  }
})

const router = useRouter()
const { handleLogout } = useAuth()

const isSidebarOpen = ref(false)
const currentDay = ref('')
const currentDate = ref('')
const currentClock = ref('')
let timer = null

const toggleSidebar = () => {
  isSidebarOpen.value = !isSidebarOpen.value
}

const closeSidebar = () => {
  isSidebarOpen.value = false
}

const navigateTo = (path) => {
  router.push(path)
  // Close sidebar on mobile after navigation
  if (window.innerWidth <= 1024) {
    closeSidebar()
  }
}

// Prevent body scroll when sidebar is open on mobile
watch(isSidebarOpen, (isOpen) => {
  if (window.innerWidth <= 1024) {
    if (isOpen) {
      document.body.style.overflow = 'hidden'
    } else {
      document.body.style.overflow = ''
    }
  }
})

// Close sidebar on window resize to desktop
const handleResize = () => {
  if (window.innerWidth > 1024 && isSidebarOpen.value) {
    closeSidebar()
    document.body.style.overflow = ''
  }
}

const updateTime = () => {
  const now = new Date()
  const days = ['Chủ Nhật', 'Thứ Hai', 'Thứ Ba', 'Thứ Tư', 'Thứ Năm', 'Thứ Sáu', 'Thứ Bảy']
  
  currentDay.value = days[now.getDay()]
  currentDate.value = now.toLocaleDateString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  })
  currentClock.value = now.toLocaleTimeString('vi-VN', {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 1000)
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
  window.removeEventListener('resize', handleResize)
  document.body.style.overflow = ''
})
</script>

<style scoped>
/* Mobile Header */
.mobile-header {
  display: none;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 60px;
  background: white;
  border-bottom: 1px solid #e5e7eb;
  padding: 0 20px;
  align-items: center;
  gap: 15px;
  z-index: 999;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.mobile-header-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 18px;
  font-weight: 700;
  color: #1f2937;
}

.mobile-logo {
  height: 35px;
  width: auto;
}

/* Sidebar Responsive */
.sidebar {
  transition: transform var(--transition-normal);
}

/* Clock styles */
.current-time {
  margin-top: 15px;
  padding: 12px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  text-align: center;
  border: 1px solid rgba(255, 255, 255, 0.15);
}

.time-day {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 5px;
  font-weight: 500;
}

.time-date {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.85);
  margin-bottom: 8px;
  font-weight: 600;
}

.time-clock {
  font-size: 20px;
  color: white;
  font-weight: 700;
  font-family: 'Courier New', monospace;
  letter-spacing: 1px;
}

/* Tablet: Sidebar visible but narrower */
@media (max-width: 1024px) and (min-width: 769px) {
  .sidebar {
    width: var(--sidebar-width-tablet);
  }
  
  .sidebar-header h2,
  .sidebar-header p,
  .current-time {
    display: none;
  }
  
  .sidebar-logo {
    height: 50px;
    margin-bottom: 0;
  }
  
  .menu-item {
    padding: 12px;
    text-align: center;
    font-size: 12px;
  }
  
  .main-content {
    margin-left: var(--sidebar-width-tablet);
  }
}

/* Mobile: Sidebar overlay */
@media (max-width: 1024px) {
  .mobile-header {
    display: flex;
  }
  
  .sidebar {
    position: fixed;
    top: 0;
    left: 0;
    height: 100vh;
    width: 280px;
    transform: translateX(-100%);
    z-index: var(--z-sidebar);
    box-shadow: 2px 0 8px rgba(0,0,0,0.1);
  }
  
  .sidebar.is-open {
    transform: translateX(0);
  }
  
  .main-content {
    margin-left: 0;
    margin-top: 60px;
    padding: 20px;
  }
}

/* Mobile Small */
@media (max-width: 480px) {
  .mobile-header {
    padding: 0 15px;
  }
  
  .mobile-header-title {
    font-size: 16px;
  }
  
  .mobile-logo {
    height: 30px;
  }
  
  .sidebar {
    width: 260px;
  }
  
  .main-content {
    padding: 15px;
  }
}
</style>
