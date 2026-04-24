<template>
  <AdminLayout activeMenu="users">
    <header class="top-header">
      <div class="header-left">
        <h1>Quản Lý Người Dùng</h1>
      </div>
    </header>

    <div class="filter-section">
      <input v-model="searchQuery" type="text" placeholder="Tìm kiếm theo tên hoặc email..." class="search-input">
      <select v-model="filterRole" class="filter-select">
        <option value="">Tất cả vai trò</option>
        <option value="student">Học sinh</option>
        <option value="teacher">Giáo viên</option>
        <option value="admin">Admin</option>
      </select>
    </div>

    <LoadingSpinner v-if="loading" />

    <EmptyState 
      v-else-if="filteredUsers.length === 0"
      message="Không tìm thấy người dùng"
    />

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
                {{ user.status === 'active' ? 'Hoạt động' : 'Khóa' }}
              </span>
            </td>
            <td>{{ formatDate(user.createdAt) }}</td>
            <td>
              <div class="action-buttons">
                <button v-if="user.role !== 'admin'" 
                        class="btn-toggle" 
                        @click="toggleUserStatus(user)">
                  {{ user.status === 'active' ? 'Khóa' : 'Mở khóa' }}
                </button>
                <button v-if="user.role === 'student'" 
                        class="btn-promote" 
                        @click="promoteToTeacher(user.id)">
                  Lên GV
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </AdminLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useFormatters } from '../../composables/useFormatters'
import userService from '../../services/userService'
import api from '../../services/api'
import AdminLayout from '../../components/layouts/AdminLayout.vue'
import LoadingSpinner from '../../components/common/LoadingSpinner.vue'
import EmptyState from '../../components/common/EmptyState.vue'

const { formatDate, getRoleText } = useFormatters()

const users = ref([])
const loading = ref(false)
const searchQuery = ref('')
const filterRole = ref('')

const filteredUsers = computed(() => {
  return users.value.filter(user => {
    const matchSearch = user.fullName.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
                       user.email.toLowerCase().includes(searchQuery.value.toLowerCase())
    const matchRole = !filterRole.value || user.role === filterRole.value
    return matchSearch && matchRole
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
  if (!confirm(`Bạn có chắc muốn ${user.status === 'active' ? 'khóa' : 'mở khóa'} tài khoản này?`)) return
  
  try {
    const response = user.status === 'active'
      ? await userService.lockUser(user.id)
      : await userService.unlockUser(user.id)
    if (response.success) {
      alert('Cập nhật thành công!')
      loadUsers()
    }
  } catch (error) {
    console.error('Error updating user:', error)
    alert('Có lỗi khi cập nhật')
  }
}

const promoteToTeacher = async (userId) => {
  if (!confirm('Bạn có chắc muốn nâng cấp người dùng này lên giáo viên?')) return
  
  try {
    const response = await api.post(`/users/${userId}/promote-teacher`)
    if (response.success) {
      alert('Nâng cấp thành công!')
      loadUsers()
    }
  } catch (error) {
    console.error('Error promoting user:', error)
    alert('Có lỗi khi nâng cấp')
  }
}
</script>

<style scoped>
.filter-section {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  margin-bottom: 25px;
  display: flex;
  gap: 15px;
}

.users-table {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
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

.action-buttons {
  display: flex;
  gap: 8px;
}

.btn-toggle,
.btn-promote {
  padding: 6px 14px;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-toggle {
  background: #fee2e2;
  color: #991b1b;
}

.btn-toggle:hover {
  background: #fecaca;
}

.btn-promote {
  background: #dbeafe;
  color: #1e40af;
}

.btn-promote:hover {
  background: #bfdbfe;
}
</style>
