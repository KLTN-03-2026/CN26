<template>
  <div class="auth-layout">
    <div class="auth-card">
      <div class="auth-header">
        <h1>{{ title }}</h1>
        <p v-if="subtitle">{{ subtitle }}</p>
      </div>
      <div class="auth-body">
        <slot />
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    required: true
  },
  subtitle: {
    type: String,
    default: ''
  }
})
</script>

<style scoped>
.auth-layout {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.auth-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  width: 100%;
  max-width: 450px;
  overflow: hidden;
}

.auth-header {
  padding: 40px 40px 30px;
  text-align: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.auth-header h1 {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 10px 0;
}

.auth-header p {
  font-size: 14px;
  opacity: 0.9;
  margin: 0;
  line-height: 1.5;
}

.auth-body {
  padding: 40px;
}

@media (max-width: 480px) {
  .auth-header {
    padding: 30px 20px 20px;
  }

  .auth-header h1 {
    font-size: 24px;
  }

  .auth-body {
    padding: 30px 20px;
  }
}
</style>
