<template>
  <div class="auth-left">
    <div class="brand-section">
      <div class="brand-logo">
        <img src="/logo/logo.png" alt="Logo" class="logo-image" />
      </div>
      <h1>Hệ Thống Thi Trực Tuyến</h1>
      <p class="brand-tagline">Nền tảng thi trắc nghiệm hiện đại</p>
      <div class="features">
        <div class="feature-item">
          <div class="feature-title">Thời gian</div>
          <p>Giảm sát thời gian thực chính xác tuyệt đối.</p>
        </div>
        <div class="feature-item">
          <div class="feature-title">Bảo mật</div>
          <p>Công nghệ bảo mật hiện đại.</p>
        </div>
      </div>
      <div class="brand-footer">
        <span>PRECISION</span>
        <span>SECURITY</span>
        <span>INTEGRITY</span>
      </div>
    </div>
  </div>
</template>

<script setup>
// No props needed - static content
</script>

<style scoped>
.auth-left {
  background: linear-gradient(180deg, #1e40af 0%, #1e3a8a 100%);
  color: white;
  padding: 50px 40px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.brand-section {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.brand-logo {
  margin-bottom: 10px;
}

.logo-image {
  height: 180px;
  width: auto;
  object-fit: contain;
  background: white;
  padding: 20px;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.brand-section h1 {
  font-size: 28px;
  font-weight: 800;
  margin: 0;
  letter-spacing: -0.5px;
}

.brand-tagline {
  font-size: 15px;
  opacity: 0.9;
  margin: 0;
}

.features {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-top: 20px;
}

.feature-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.feature-title {
  font-size: 16px;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 10px;
}

.feature-item p {
  font-size: 14px;
  opacity: 0.85;
  line-height: 1.6;
  margin: 0;
}

.brand-footer {
  display: flex;
  gap: 15px;
  font-size: 11px;
  font-weight: 700;
  opacity: 0.7;
  text-transform: uppercase;
  letter-spacing: 1px;
  margin-top: 30px;
}
</style>
