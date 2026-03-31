<template>
  <div class="dashboard-layout">
    <aside class="sidebar">
      <div class="sidebar-header">
        <img src="/logo/logo.png" alt="Logo" class="sidebar-logo" />
        <h2>Hệ Thống Thi</h2>
        <p>Học sinh</p>
      </div>
      <nav class="sidebar-menu">
        <a href="#" @click.prevent="$router.push('/student')" class="menu-item">
          Trang chủ
        </a>
        <a href="#" @click.prevent="$router.push('/student/exams')" class="menu-item">
          Đề thi
        </a>
        <a href="#" @click.prevent="$router.push('/student/results')" class="menu-item">
          Kết quả
        </a>
        <a href="#" @click.prevent="$router.push('/student/profile')" class="menu-item active">
          Cài đặt
        </a>
      </nav>
      <div class="sidebar-footer">
        <button class="btn-help" @click="$router.push('/student/teacher-request')">Trở thành giáo viên</button>
      </div>
    </aside>
    <div class="main-content">
      <header class="top-header">
        <div class="header-left">
          <h1>Cài Đặt Tài Khoản</h1>
        </div>
        <div class="header-right">
          <div class="user-menu">
            <span class="user-name">{{ authStore.user?.fullName }}</span>
            <button class="btn-logout-small" @click="handleLogout">Đăng xuất</button>
          </div>
        </div>
      </header>

      <div v-if="success" class="alert alert-success">
        {{ success }}
      </div>
      <div v-if="error" class="alert alert-error">
        {{ error }}
      </div>

      <div class="profile-grid">
        <div class="profile-main">
          <div class="section-card">
            <div class="section-header">
              <h2>Thông Tin Cá Nhân</h2>
              <p>Cập nhật thông tin cá nhân của bạn</p>
            </div>
            <form @submit.prevent="updateProfile" class="profile-form">
              <div class="form-row">
                <div class="form-group">
                  <label>Họ và tên <span class="required">*</span></label>
                  <input type="text" v-model="form.fullName" required placeholder="Nhập họ và tên" />
                </div>
                <div class="form-group">
                  <label>Email <span class="required">*</span></label>
                  <input type="email" v-model="form.email" required placeholder="email@example.com" />
                </div>
              </div>

              <div class="form-row">
                <div class="form-group">
                  <label>Số điện thoại</label>
                  <input type="tel" v-model="form.phone" placeholder="0123456789" />
                </div>
                <div class="form-group">
                  <label>Ngày sinh</label>
                  <input type="date" v-model="form.dateOfBirth" />
                </div>
              </div>

              <div class="form-row">
                <div class="form-group">
                  <label>Giới tính</label>
                  <select v-model="form.gender">
                    <option value="">Chọn giới tính</option>
                    <option value="male">Nam</option>
                    <option value="female">Nữ</option>
                    <option value="other">Khác</option>
                  </select>
                </div>
              </div>

              <div class="form-group">
                <label>Địa chỉ</label>
                <textarea v-model="form.address" rows="3" placeholder="Nhập địa chỉ của bạn"></textarea>
              </div>

              <button type="submit" class="btn-submit" :disabled="loading">
                {{ loading ? 'Đang lưu...' : 'Lưu thay đổi' }}
              </button>
            </form>
          </div>

          <div class="section-card">
            <div class="section-header">
              <h2>Đổi Mật Khẩu</h2>
              <p>Cập nhật mật khẩu để bảo mật tài khoản</p>
            </div>
            <form @submit.prevent="changePassword" class="profile-form">
              <div class="form-group">
                <label>Mật khẩu hiện tại <span class="required">*</span></label>
                <input type="password" v-model="passwordForm.currentPassword" required placeholder="Nhập mật khẩu hiện tại" />
              </div>

              <div class="form-group">
                <label>Mật khẩu mới <span class="required">*</span></label>
                <input type="password" v-model="passwordForm.newPassword" required minlength="6" placeholder="Nhập mật khẩu mới (tối thiểu 6 ký tự)" />
              </div>

              <button type="submit" class="btn-submit" :disabled="loadingPassword">
                {{ loadingPassword ? 'Đang đổi...' : 'Đổi mật khẩu' }}
              </button>
            </form>
          </div>
        </div>

        <div class="profile-sidebar">
          <div class="user-info-card">
            <div class="user-avatar-large">{{ authStore.user?.fullName?.charAt(0) }}</div>
            <h3>{{ authStore.user?.fullName }}</h3>
            <p class="user-role">{{ getRoleName(authStore.user?.role) }}</p>
            <div class="user-email-display">{{ authStore.user?.email }}</div>
          </div>

          <div class="tips-card">
            <h4>Mẹo Bảo Mật</h4>
            <ul>
              <li>Sử dụng mật khẩu mạnh với ít nhất 8 ký tự</li>
              <li>Không chia sẻ mật khẩu với người khác</li>
              <li>Đổi mật khẩu định kỳ để bảo mật</li>
              <li>Cập nhật thông tin liên hệ chính xác</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/authStore'
import userService from '../../services/userService'

const router = useRouter()
const authStore = useAuthStore()

const form = ref({
  fullName: '',
  email: '',
  phone: '',
  dateOfBirth: '',
  gender: '',
  address: ''
})

const passwordForm = ref({
  currentPassword: '',
  newPassword: ''
})

const loading = ref(false)
const loadingPassword = ref(false)
const success = ref('')
const error = ref('')

onMounted(() => {
  loadProfile()
})

const loadProfile = async () => {
  try {
    const response = await userService.getProfile()
    if (response.success) {
      const data = response.data
      form.value = {
        fullName: data.fullName || '',
        email: data.email || '',
        phone: data.phone || '',
        dateOfBirth: data.dateOfBirth || '',
        gender: data.gender || '',
        address: data.address || ''
      }
    }
  } catch (err) {
    error.value = 'Không thể tải thông tin profile'
  }
}

const updateProfile = async () => {
  try {
    loading.value = true
    success.value = ''
    error.value = ''
    
    const response = await userService.updateProfile(form.value)
    if (response.success) {
      success.value = 'Cập nhật hồ sơ thành công!'
      setTimeout(() => success.value = '', 3000)
    }
  } catch (err) {
    error.value = err.message || 'Cập nhật thất bại'
    setTimeout(() => error.value = '', 3000)
  } finally {
    loading.value = false
  }
}

const changePassword = async () => {
  try {
    loadingPassword.value = true
    success.value = ''
    error.value = ''
    
    const response = await userService.changePassword(passwordForm.value)
    if (response.success) {
      success.value = 'Đổi mật khẩu thành công!'
      passwordForm.value = { currentPassword: '', newPassword: '' }
      setTimeout(() => success.value = '', 3000)
    }
  } catch (err) {
    error.value = err.message || 'Đổi mật khẩu thất bại'
    setTimeout(() => error.value = '', 3000)
  } finally {
    loadingPassword.value = false
  }
}

const getRoleName = (role) => {
  const roles = {
    student: 'Học sinh',
    teacher: 'Giáo viên',
    admin: 'Quản trị viên'
  }
  return roles[role] || role
}

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.dashboard-layout {
  display: flex;
  min-height: 100vh;
  background: #f5f7fa;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.sidebar {
  width: 280px;
  background: linear-gradient(180deg, #1e40af 0%, #1e3a8a 100%);
  display: flex;
  flex-direction: column;
  position: fixed;
  height: 100vh;
  left: 0;
  top: 0;
  box-shadow: 2px 0 10px rgba(0,0,0,0.1);
}

.sidebar-header {
  padding: 35px 25px;
  border-bottom: 1px solid rgba(255,255,255,0.1);
  text-align: center;
}

.sidebar-logo {
  height: 120px;
  width: auto;
  object-fit: contain;
  margin-bottom: 20px;
  background: white;
  padding: 18px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
}

.sidebar-header h2 {
  font-size: 22px;
  color: #ffffff;
  margin: 0 0 6px 0;
  font-weight: 700;
}

.sidebar-header p {
  font-size: 13px;
  color: rgba(255,255,255,0.7);
  margin: 0;
}

.sidebar-menu {
  flex: 1;
  padding: 25px 15px;
  overflow-y: auto;
}

.menu-item {
  display: block;
  width: 100%;
  padding: 14px 18px;
  color: #ffffff;
  text-decoration: none;
  transition: all 0.3s ease;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 500;
  margin-bottom: 6px;
}

.menu-item:hover {
  background: rgba(255,255,255,0.15);
}

.menu-item.active {
  background: #ffffff;
  color: #1e40af;
  font-weight: 600;
}

.sidebar-footer {
  padding: 20px 15px 25px;
  border-top: 1px solid rgba(255,255,255,0.1);
}

.btn-new-exam {
  width: 100%;
  background: white;
  color: #1e40af;
  border: none;
  padding: 14px;
  border-radius: 10px;
  font-weight: 700;
  font-size: 15px;
  cursor: pointer;
  margin-bottom: 12px;
  transition: all 0.3s ease;
}

.btn-new-exam:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.2);
}

.btn-help {
  width: 100%;
  background: white;
  color: #1e40af;
  border: none;
  padding: 14px;
  border-radius: 10px;
  font-weight: 700;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
}

.btn-help:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.2);
}

.main-content {
  margin-left: 280px;
  flex: 1;
  padding: 35px 40px;
}

.top-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 35px;
}

.header-left h1 {
  font-size: 32px;
  color: #1f2937;
  font-weight: 800;
  margin: 0;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-menu {
  display: flex;
  align-items: center;
  gap: 16px;
  background: white;
  padding: 10px 18px;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.user-name {
  font-size: 14px;
  color: #1f2937;
  font-weight: 600;
}

.btn-logout-small {
  padding: 8px 16px;
  background: #fee2e2;
  color: #dc2626;
  border: none;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
}

.btn-logout-small:hover {
  background: #fecaca;
}

.alert {
  padding: 16px 20px;
  border-radius: 12px;
  margin-bottom: 25px;
  font-size: 15px;
  font-weight: 500;
}

.alert-success {
  background: #d1fae5;
  color: #065f46;
  border: 1px solid #a7f3d0;
}

.alert-error {
  background: #fee2e2;
  color: #991b1b;
  border: 1px solid #fecaca;
}

.profile-grid {
  display: grid;
  grid-template-columns: 1fr 350px;
  gap: 30px;
}

.profile-main {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.section-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  padding: 35px;
}

.section-header {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid #f0f0f0;
}

.section-header h2 {
  font-size: 24px;
  color: #1f2937;
  margin: 0 0 8px 0;
  font-weight: 700;
}

.section-header p {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

.profile-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-size: 14px;
  color: #374151;
  font-weight: 600;
}

.required {
  color: #ef4444;
}

.form-group input,
.form-group select,
.form-group textarea {
  padding: 12px 16px;
  border: 2px solid #e5e7eb;
  border-radius: 10px;
  font-size: 15px;
  transition: all 0.3s ease;
  font-family: inherit;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #1e40af;
  box-shadow: 0 0 0 3px rgba(30,64,175,0.1);
}

.form-group textarea {
  resize: vertical;
}

.btn-submit {
  padding: 14px 24px;
  background: #1e40af;
  color: white;
  border: none;
  border-radius: 10px;
  font-weight: 700;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: 10px;
}

.btn-submit:hover {
  background: #1e3a8a;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(30,64,175,0.3);
}

.btn-submit:disabled {
  background: #9ca3af;
  cursor: not-allowed;
  transform: none;
}

.profile-sidebar {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.user-info-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  padding: 35px;
  text-align: center;
}

.user-avatar-large {
  width: 100px;
  height: 100px;
  background: linear-gradient(135deg, #1e40af 0%, #1e3a8a 100%);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
  font-weight: 700;
  margin: 0 auto 20px;
}

.user-info-card h3 {
  font-size: 22px;
  color: #1f2937;
  margin: 0 0 8px 0;
  font-weight: 700;
}

.user-role {
  font-size: 14px;
  color: #6b7280;
  margin: 0 0 15px 0;
  font-weight: 500;
}

.user-email-display {
  padding: 10px 16px;
  background: #f9fafb;
  border-radius: 8px;
  font-size: 13px;
  color: #6b7280;
  word-break: break-all;
}

.tips-card {
  background: linear-gradient(135deg, #1e3a8a 0%, #1e40af 100%);
  border-radius: 16px;
  padding: 30px;
  color: white;
}

.tips-card h4 {
  font-size: 18px;
  margin: 0 0 15px 0;
  font-weight: 700;
}

.tips-card ul {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.tips-card li {
  font-size: 14px;
  line-height: 1.6;
  padding-left: 20px;
  position: relative;
}

.tips-card li:before {
  content: '•';
  position: absolute;
  left: 0;
  font-size: 20px;
}

@media (max-width: 1200px) {
  .profile-grid {
    grid-template-columns: 1fr;
  }
  .form-row {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .sidebar {
    transform: translateX(-100%);
  }
  .main-content {
    margin-left: 0;
    padding: 20px;
  }
}
</style>
