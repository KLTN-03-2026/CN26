<template>
  <div class="pagination" v-if="totalPages > 1">
    <button 
      class="page-btn" 
      @click="$emit('change', currentPage - 1)"
      :disabled="currentPage === 1"
    >
      &laquo;
    </button>

    <button
      v-for="page in visiblePages"
      :key="page"
      class="page-btn"
      :class="{ active: page === currentPage, ellipsis: page === '...' }"
      @click="page !== '...' && $emit('change', page)"
      :disabled="page === '...'"
    >
      {{ page }}
    </button>

    <button 
      class="page-btn" 
      @click="$emit('change', currentPage + 1)"
      :disabled="currentPage === totalPages"
    >
      &raquo;
    </button>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  currentPage: {
    type: Number,
    required: true
  },
  totalPages: {
    type: Number,
    required: true
  },
  maxVisible: {
    type: Number,
    default: 5
  }
})

defineEmits(['change'])

const visiblePages = computed(() => {
  const pages = []
  const { currentPage, totalPages, maxVisible } = props

  if (totalPages <= maxVisible) {
    for (let i = 1; i <= totalPages; i++) {
      pages.push(i)
    }
  } else {
    const half = Math.floor(maxVisible / 2)
    let start = Math.max(1, currentPage - half)
    let end = Math.min(totalPages, start + maxVisible - 1)

    if (end - start < maxVisible - 1) {
      start = Math.max(1, end - maxVisible + 1)
    }

    if (start > 1) {
      pages.push(1)
      if (start > 2) pages.push('...')
    }

    for (let i = start; i <= end; i++) {
      pages.push(i)
    }

    if (end < totalPages) {
      if (end < totalPages - 1) pages.push('...')
      pages.push(totalPages)
    }
  }

  return pages
})
</script>

<style scoped>
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  margin-top: 30px;
  padding: 20px 0;
}

.page-btn {
  min-width: 40px;
  height: 40px;
  padding: 0 12px;
  background: white;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #374151;
  cursor: pointer;
  transition: all 0.2s;
}

.page-btn:hover:not(:disabled):not(.ellipsis) {
  background: #f3f4f6;
  border-color: #1e40af;
  color: #1e40af;
}

.page-btn.active {
  background: #1e40af;
  border-color: #1e40af;
  color: white;
}

.page-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.page-btn.ellipsis {
  border: none;
  background: transparent;
  cursor: default;
}

.page-btn.ellipsis:hover {
  background: transparent;
  border: none;
}
</style>
