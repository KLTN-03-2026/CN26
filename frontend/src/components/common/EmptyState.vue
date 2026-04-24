<template>
  <div class="empty-state">
    <p>{{ message }}</p>
    <button v-if="actionText" class="btn-create" @click="$emit('action')">
      {{ actionText }}
    </button>
  </div>
</template>

<script setup>
defineProps({
  message: {
    type: String,
    required: true
  },
  actionText: {
    type: String,
    default: ''
  }
})

defineEmits(['action'])
</script>
