<template>
  <div class="dashboard-layout">
    <aside class="sidebar">
      <div class="sidebar-header">
        <img src="/logo/logo.png" alt="Logo" class="sidebar-logo" />
        <h2>Hệ Thống Thi</h2>
        <p>Quản trị viên</p>
      </div>
      <nav class="sidebar-menu">
        <a href="#" @click.prevent="$router.push('/admin')" class="menu-item">
          Trang chủ
        </a>
        <a href="#" @click.prevent="$router.push('/admin/users')" class="menu-item active">
          Người dùng
        </a>
        <a href="#" @click.prevent="$router.push('/admin/teacher-requests')" class="menu-item">
          Yêu cầu GV
        </a>
      </nav>
      <div class="sidebar-footer">
        <button class="btn-logout" @click="handleLogout">Đăng xuất</button>
      </div>
    </aside>
    <div class="main-content">
      <header class="top-header">
        <div class="header-left">
          <h1>Quản Lý Người Dùng</h1>
        </div>
      </header>

      <div class="filter-section">
        <input v-model="searchQuery" type="text" placeholder="Tìm kiếm người dùng..." class="search-input">
        <select v-model="filterRole" class="filter-select">
          <option value="">Tất cả vai trò</option>
          <option value="student">Học sinh</option>
          <option value="teacher">Giáo viên</option>
          <option value="admin">Admin</option>
        </select>
        <select v-model="filterStatus" class="filter-select">
          <option value="">Tất cả trạng thái</option>
          <option value="active">Hoạt động</option>
          <option value="inactive">Không hoạt động</option>
        </select>
      </div>

      <div class="users-container">
        <div v-if="loading" class="loading-state">
          <div class="spinner"></div>
          <p>Đang tải...</p>
        </div>
        <div v-else-if="filteredUsers.length === 0" class="empty-state">
          <p>Không tìm thấy người dùng nào</p>
        </div>
        <div v-else class="users-table">
          <table>
            <thead>
              <tr>
                <th>Họ tên</th>
                <th>Email</th>
                <th>Vai trò</th>
                <th>Trạng thái</th>
                <th>Ngày tạo</th>
                <th>Hành động</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="user in filteredUsers" :key="user.id">
                <td class="user-name">{{ user.fullName }}</td>
                <td class="user-email">{{ user.email }}</td>
                <td>
                  <span :class="['role-badge', `role-${user.role}`]">
                    {{ getRoleText(user.role) }}
                  </span>
                </td>
                <td>
                  <span :class="['status-badge', user.status === 'active' ? 'active' : 'inactive']">
                    {{ user.status === 'active' ? 'Hoạt động' : 'Không hoạt động' }}
                  </span>
                </td>
                <td>{{ formatDate(user.createdAt) }}</td>
                <td>
                  <button class="btn-toggle" @click="toggleUserStatus(user)">
                    {{ user.status === 'active' ? 'Vô hiệu hóa' : 'Kích hoạt' }}
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/authStore'
import userService from '../../services/userService'

const router = useRouter()
const authStore = useAuthStore()

const users = ref([])
const loading = ref(false)
const searchQuery = ref('')
const filterRole = ref('')
const filterStatus = ref('')

const filteredUsers = computed(() => {
  return users.value.filter(user => {
    const matchSearch = user.fullName.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
                       user.email.toLowerCase().includes(searchQuery.value.toLowerCase())
    const matchRole = !filterRole.value || user.role === filterRole.value
    const matchStatus = !filterStatus.value || user.status === filterStatus.value
    return matchSearch && matchRole && matchStatus
  })
})

onMounted(() => {
  loadUsers()
})

const loadUsers = async () => {
  try {
    loading.value = true
    const response = await userService.getAllUsers()
    if (response.success) {
      users.value = response.data
    }
  } catch (error) {
    console.error('Error loading users:', error)
  } finally {
    loading.value = false
  }
}

const toggleUserStatus = async (user) => {
  const newStatus = user.status === 'active' ? 'inactive' : 'active'
  const action = newStatus === 'active' ? 'kích hoạt' : 'vô hiệu hóa'
  
  if (!confirm(`Bạn có chắc muốn ${action} tài khoản ${user.fullName}?`)) return
  
  try {
    const response = await userService.updateUser(user.id, { status: newStatus })
    if (response.success) {
      alert('Cập nhật thành công!')
      loadUsers()
    }
  } catch (error) {
    console.error('Error updating user:', error)
    alert('Có lỗi khi cập nhật người dùng')
  }
}

const getRoleText = (role) => {
  const map = {
    student: 'Học sinh',
    teacher: 'Giáo viên',
    admin: 'Admin'
  }
  return map[role] || role
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('vi-VN')
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
  color: white;
  display: flex;
  flex-direction: column;
  position: fixed;
  height: 100vh;
  overflow-y: auto;
}

.sidebar-header {
  padding: 35px 25px;
  border-bottom: 1px solid rgba(255,255,255,0.1);
  text-align: center;
}

.sidebar-logo {
  height: 80px;
  width: auto;
  object-fit: contain;
  margin-bottom: 15px;
  background: white;
  padding: 12px;
  border-radius: 10px;
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
  padding: 14px 25px;
  color: rgba(255,255,255,0.8);
  text-decoration: none;
  transition: all 0.3s;
  font-weight: 500;
  border-radius: 10px;
  margin-bottom: 6px;
}

.menu-item:hover {
  background: rgba(255,255,255,0.1);
  color: white;
}

.menu-item.active {
  background: #ffffff;
  color: #1e40af;
  font-weight: 600;
}

.sidebar-footer {
  padding: 20px 25px;
  border-top: 1px solid rgba(255,255,255,0.1);
}

.btn-logout {
  width: 100%;
  padding: 12px;
  background: rgba(255,255,255,0.1);
  color: white;
  border: 1px solid rgba(255,255,255,0.2);
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-logout:hover {
  background: rgba(255,255,255,0.2);
}

.main-content {
  margin-left: 280px;
  flex: 1;
  padding: 30px;
}

.top-header {
  background: white;
  padding: 20px 30px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  margin-bottom: 25px;
}

.header-left h1 {
  font-size: 28px;
  color: #1f2937;
  margin: 0;
  font-weight: 800;
}

.filter-section {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  margin-bottom: 25px;
  display: flex;
  gap: 15px;
}

.search-input {
  flex: 1;
  padding: 12px 16px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.3s;
}

.search-input:focus {
  outline: none;
  border-color: #1e40af;
  box-shadow: 0 0 0 3px rgba(30,64,175,0.1);
}

.filter-select {
  padding: 12px 16px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  min-width: 180px;
  transition: all 0.3s;
}

.filter-select:focus {
  outline: none;
  border-color: #1e40af;
}

.users-container {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.loading-state, .empty-state {
  text-align: center;
  padding: 60px 20px;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #e5e7eb;
  border-top-color: #1e40af;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 15px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.empty-state p {
  color: #6b7280;
  font-size: 16px;
}

.users-table table {
  width: 100%;
  border-collapse: collapse;
}

.users-table th {
  text-align: left;
  padding: 12px;
  background: #f9fafb;
  color: #374151;
  font-weight: 600;
  font-size: 14px;
  border-bottom: 2px solid #e5e7eb;
}

.users-table td {
  padding: 15px 12px;
  border-bottom: 1px solid #f3f4f6;
  color: #6b7280;
  font-size: 14px;
}

.user-name {
  color: #1f2937;
  font-weight: 600;
}

.user-email {
  color: #6b7280;
  font-size: 13px;
}

.role-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.role-badge.role-student {
  background: #dbeafe;
  color: #1e40af;
}

.role-badge.role-teacher {
  background: #d1fae5;
  color: #065f46;
}

.role-badge.role-admin {
  background: #fef3c7;
  color: #92400e;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.status-badge.active {
  background: #d1fae5;
  color: #065f46;
}

.status-badge.inactive {
  background: #fee2e2;
  color: #991b1b;
}

.btn-toggle {
  padding: 6px 16px;
  background: #eff6ff;
  color: #1e40af;
  border: 1px solid #bfdbfe;
  border-radius: 6px;
  font-weight: 600;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-toggle:hover {
  background: #dbeafe;
}

@media (max-width: 768px) {
  .filter-section {
    flex-direction: column;
  }
}
</style>
