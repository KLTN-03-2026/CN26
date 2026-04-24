<template>
  <AdminLayout activeMenu="home">
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
      <StatsCard 
        v-for="s in statCards" 
        :key="s.label"
        :value="s.value"
        :label="s.label"
      />
    </div>

    <div class="quick-actions">
      <ActionCard
        title="Quản Lý Người Dùng"
        description="Xem và quản lý tất cả người dùng trong hệ thống"
        button-text="Quản lý người dùng"
        @click="$router.push('/admin/users')"
      />

      <ActionCard
        title="Duyệt Yêu Cầu Giáo Viên"
        description="Xem và duyệt yêu cầu nâng cấp tài khoản giáo viên"
        button-text="Xem yêu cầu"
        @click="$router.push('/admin/teacher-requests')"
      />
    </div>

    <RecentRequestsTable
      :requests="requests"
      :loading="loading"
      @view-all="$router.push('/admin/teacher-requests')"
      @view-detail="$router.push('/admin/teacher-requests')"
    />
  </AdminLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/authStore'
import userService from '../../services/userService'
import teacherRequestService from '../../services/teacherRequestService'
import AdminLayout from '../../components/layouts/AdminLayout.vue'
import StatsCard from '../../components/admin/StatsCard.vue'
import ActionCard from '../../components/admin/ActionCard.vue'
import RecentRequestsTable from '../../components/admin/RecentRequestsTable.vue'

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

const statCards = computed(() => [
  { value: stats.value.totalUsers, label: 'Tổng người dùng' },
  { value: stats.value.totalStudents, label: 'Học sinh' },
  { value: stats.value.totalTeachers, label: 'Giáo viên' },
  { value: stats.value.pendingRequests, label: 'Yêu cầu chờ duyệt' }
])

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
</script>

<style scoped>
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

.quick-actions {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 30px;
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
