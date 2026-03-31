<template>
  <div class="dashboard-layout">
    <aside class="sidebar">
      <div class="sidebar-header">
        <img src="/logo/logo.png" alt="Logo" class="sidebar-logo" />
        <h2>Hệ Thống Thi</h2>
        <p>Quản trị viên</p>
      </div>
      <nav class="sidebar-menu">
        <a href="#" @click.prevent="$router.push('/admin')" class="menu-item active">
          Trang chủ
        </a>
        <a href="#" @click.prevent="$router.push('/admin/users')" class="menu-item">
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
          <h1>Trang chủ</h1>
        </div>
        <div class="header-right">
          <div class="user-menu">
            <span class="user-name">{{ authStore.user?.fullName }}</span>
          </div>
        </div>
      </header>

      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-info">
            <h3>{{ stats.totalUsers }}</h3>
            <p>Tổng người dùng</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-info">
            <h3>{{ stats.totalStudents }}</h3>
            <p>Học sinh</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-info">
            <h3>{{ stats.totalTeachers }}</h3>
            <p>Giáo viên</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-info">
            <h3>{{ stats.pendingRequests }}</h3>
            <p>Yêu cầu chờ duyệt</p>
          </div>
        </div>
      </div>

      <div class="quick-actions">
        <div class="action-card">
          <h3>Quản Lý Người Dùng</h3>
          <p>Xem và quản lý tất cả người dùng trong hệ thống</p>
          <button class="btn-action" @click="$router.push('/admin/users')">
            Quản lý người dùng →
          </button>
        </div>

        <div class="action-card">
          <h3>Duyệt Yêu Cầu Giáo Viên</h3>
          <p>Xem và duyệt yêu cầu nâng cấp tài khoản giáo viên</p>
          <button class="btn-action" @click="$router.push('/admin/teacher-requests')">
            Xem yêu cầu →
          </button>
        </div>
      </div>

      <div class="recent-requests">
        <div class="section-header">
          <h2>Yêu Cầu Gần Đây</h2>
          <button class="btn-view-all" @click="$router.push('/admin/teacher-requests')">
            Xem tất cả →
          </button>
        </div>
        
        <div v-if="loading" class="loading-state">
          <div class="spinner"></div>
          <p>Đang tải...</p>
        </div>
        
        <div v-else-if="requests.length === 0" class="empty-state">
          <p>Không có yêu cầu nào</p>
        </div>
        
        <div v-else class="requests-table">
          <table>
            <thead>
              <tr>
                <th>Người dùng</th>
                <th>Email</th>
                <th>Ngày gửi</th>
                <th>Trạng thái</th>
                <th>Hành động</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="request in requests.slice(0, 5)" :key="request.id">
                <td class="user-name">{{ request.userName }}</td>
                <td class="user-email">{{ request.userEmail }}</td>
                <td>{{ formatDate(request.createdAt) }}</td>
                <td>
                  <span :class="['status-badge', getStatusClass(request.status)]">
                    {{ getStatusText(request.status) }}
                  </span>
                </td>
                <td>
                  <button v-if="request.status === 'pending'" 
                          class="btn-view" 
                          @click="$router.push('/admin/teacher-requests')">
                    Xem chi tiết
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/authStore'
import userService from '../../services/userService'
import teacherRequestService from '../../services/teacherRequestService'

const router = useRouter()
const authStore = useAuthStore()

const requests = ref([])
const loading = ref(false)

const stats = ref({
  totalUsers: 0,
  totalStudents: 0,
  totalTeachers: 0,
  pendingRequests: 0
})

onMounted(() => {
  loadDashboardData()
})

const loadDashboardData = async () => {
  try {
    loading.value = true
    
    const [usersResponse, requestsResponse] = await Promise.all([
      userService.getAllUsers(),
      teacherRequestService.getAllRequests()
    ])

    if (usersResponse.success) {
      const users = usersResponse.data
      stats.value.totalUsers = users.length
      stats.value.totalStudents = users.filter(u => u.role === 'student').length
      stats.value.totalTeachers = users.filter(u => u.role === 'teacher').length
    }

    if (requestsResponse.success) {
      requests.value = requestsResponse.data
      stats.value.pendingRequests = requestsResponse.data.filter(r => r.status === 'pending').length
    }
  } catch (error) {
    console.error('Error loading dashboard:', error)
  } finally {
    loading.value = false
  }
}

const getStatusText = (status) => {
  const map = {
    pending: 'Chờ duyệt',
    approved: 'Đã duyệt',
    rejected: 'Từ chối'
  }
  return map[status] || status
}

const getStatusClass = (status) => {
  const map = {
    pending: 'pending',
    approved: 'approved',
    rejected: 'rejected'
  }
  return map[status] || 'pending'
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
  margin-bottom: 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left h1 {
  font-size: 28px;
  color: #1f2937;
  margin: 0;
  font-weight: 800;
}

.user-name {
  font-size: 15px;
  color: #374151;
  font-weight: 600;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  text-align: center;
}

.stat-info h3 {
  font-size: 48px;
  color: #1e40af;
  margin: 0 0 10px 0;
  font-weight: 800;
}

.stat-info p {
  font-size: 16px;
  color: #6b7280;
  margin: 0;
  font-weight: 500;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.action-card {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  text-align: center;
  transition: all 0.3s;
}

.action-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0,0,0,0.12);
}

.action-card h3 {
  font-size: 18px;
  color: #1f2937;
  margin: 0 0 10px 0;
  font-weight: 700;
}

.action-card p {
  font-size: 14px;
  color: #6b7280;
  margin: 0 0 20px 0;
  line-height: 1.6;
}

.btn-action {
  padding: 10px 20px;
  background: #1e40af;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-action:hover {
  background: #1e3a8a;
}

.recent-requests {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h2 {
  font-size: 20px;
  color: #1f2937;
  margin: 0;
  font-weight: 700;
}

.btn-view-all {
  padding: 8px 16px;
  background: #f3f4f6;
  color: #374151;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-view-all:hover {
  background: #e5e7eb;
}

.loading-state, .empty-state {
  text-align: center;
  padding: 40px 20px;
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
}

.requests-table table {
  width: 100%;
  border-collapse: collapse;
}

.requests-table th {
  text-align: left;
  padding: 12px;
  background: #f9fafb;
  color: #374151;
  font-weight: 600;
  font-size: 14px;
  border-bottom: 2px solid #e5e7eb;
}

.requests-table td {
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

.status-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.status-badge.pending {
  background: #fef3c7;
  color: #92400e;
}

.status-badge.approved {
  background: #d1fae5;
  color: #065f46;
}

.status-badge.rejected {
  background: #fee2e2;
  color: #991b1b;
}

.btn-view {
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

.btn-view:hover {
  background: #dbeafe;
}

@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .stats-grid, .quick-actions {
    grid-template-columns: 1fr;
  }
}
</style>
