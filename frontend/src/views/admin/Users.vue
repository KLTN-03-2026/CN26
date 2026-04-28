<template>
  <AdminLayout activeMenu="users">
    <header class="top-header">
      <div class="header-left">
        <h1>Quản Lý Người Dùng</h1>
      </div>
    </header>

    <LoadingSpinner v-if="loading" />

    <div v-else class="users-container">
      <!-- Cột Giáo viên -->
      <div class="user-column">
        <h2 class="section-title">Giáo Viên ({{ filteredTeachers.length }})</h2>
        
        <div class="filter-section">
          <input v-model="searchQueryTeacher" type="text" placeholder="Tìm kiếm giáo viên..." class="search-input">
          <select v-model="sortByTeacher" class="filter-select">
            <option value="name-asc">Tên A-Z</option>
            <option value="name-desc">Tên Z-A</option>
            <option value="date-desc">Mới nhất</option>
            <option value="date-asc">Cũ nhất</option>
          </select>
        </div>

        <EmptyState v-if="filteredTeachers.length === 0" message="Không có giáo viên" />
        <div v-else class="users-table">
          <table>
            <thead>
              <tr>
                <th>Họ tên</th>
                <th>Email</th>
                <th>Trạng thái</th>
                <th>Ngày tạo</th>
                <th>Hành động</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="user in filteredTeachers" :key="user.id">
                <td class="user-name">{{ user.fullName }}</td>
                <td class="user-email">{{ user.email }}</td>
                <td>
                  <span :class="['status-badge', user.status === 'active' ? 'active' : 'inactive']">
                    {{ user.status === 'active' ? 'Hoạt động' : 'Khóa' }}
                  </span>
                </td>
                <td>{{ formatDate(user.createdAt) }}</td>
                <td>
                  <button class="btn-toggle" @click="toggleUserStatus(user)">
                    {{ user.status === 'active' ? 'Khóa' : 'Mở khóa' }}
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Cột Học sinh -->
      <div class="user-column">
        <h2 class="section-title">Học Sinh ({{ filteredStudents.length }})</h2>
        
        <div class="filter-section">
          <input v-model="searchQueryStudent" type="text" placeholder="Tìm kiếm học sinh..." class="search-input">
          <select v-model="sortByStudent" class="filter-select">
            <option value="name-asc">Tên A-Z</option>
            <option value="name-desc">Tên Z-A</option>
            <option value="date-desc">Mới nhất</option>
            <option value="date-asc">Cũ nhất</option>
          </select>
        </div>

        <EmptyState v-if="filteredStudents.length === 0" message="Không có học sinh" />
        <div v-else class="users-table">
          <table>
            <thead>
              <tr>
                <th>Họ tên</th>
                <th>Email</th>
                <th>Trạng thái</th>
                <th>Ngày tạo</th>
                <th>Hành động</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="user in filteredStudents" :key="user.id">
                <td class="user-name">{{ user.fullName }}</td>
                <td class="user-email">{{ user.email }}</td>
                <td>
                  <span :class="['status-badge', user.status === 'active' ? 'active' : 'inactive']">
                    {{ user.status === 'active' ? 'Hoạt động' : 'Khóa' }}
                  </span>
                </td>
                <td>{{ formatDate(user.createdAt) }}</td>
                <td>
                  <div class="action-buttons">
                    <button class="btn-toggle" @click="toggleUserStatus(user)">
                      {{ user.status === 'active' ? 'Khóa' : 'Mở khóa' }}
                    </button>
                    <button class="btn-promote" @click="promoteToTeacher(user.id)">
                      Lên GV
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
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

const { formatDate } = useFormatters()

const users = ref([])
const loading = ref(false)
const searchQueryTeacher = ref('')
const searchQueryStudent = ref('')
const sortByTeacher = ref('name-asc')
const sortByStudent = ref('name-asc')

const getLastName = (fullName) => {
  // Lấy tên (chữ cuối cùng) từ họ tên đầy đủ
  const parts = fullName.trim().split(' ')
  return parts[parts.length - 1]
}

const sortUsers = (userList, sortBy) => {
  const sorted = [...userList]
  switch (sortBy) {
    case 'name-asc':
      return sorted.sort((a, b) => {
        const lastNameA = getLastName(a.fullName)
        const lastNameB = getLastName(b.fullName)
        const lastNameCompare = lastNameA.localeCompare(lastNameB, 'vi')
        // Nếu tên giống nhau, so sánh toàn bộ họ tên
        return lastNameCompare !== 0 ? lastNameCompare : a.fullName.localeCompare(b.fullName, 'vi')
      })
    case 'name-desc':
      return sorted.sort((a, b) => {
        const lastNameA = getLastName(a.fullName)
        const lastNameB = getLastName(b.fullName)
        const lastNameCompare = lastNameB.localeCompare(lastNameA, 'vi')
        return lastNameCompare !== 0 ? lastNameCompare : b.fullName.localeCompare(a.fullName, 'vi')
      })
    case 'date-desc':
      return sorted.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
    case 'date-asc':
      return sorted.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt))
    default:
      return sorted
  }
}

const filterUsers = (userList, searchQuery) => {
  return userList.filter(user => {
    const matchSearch = user.fullName.toLowerCase().includes(searchQuery.toLowerCase()) ||
                       user.email.toLowerCase().includes(searchQuery.toLowerCase())
    return matchSearch
  })
}

const filteredTeachers = computed(() => {
  const teachers = users.value.filter(u => u.role === 'teacher')
  const filtered = filterUsers(teachers, searchQueryTeacher.value)
  return sortUsers(filtered, sortByTeacher.value)
})

const filteredStudents = computed(() => {
  const students = users.value.filter(u => u.role === 'student')
  const filtered = filterUsers(students, searchQueryStudent.value)
  return sortUsers(filtered, sortByStudent.value)
})

const loadUsers = async () => {
  try {
    loading.value = true
    const response = await userService.getAllUsers()
    if (response.success) users.value = response.data
  } catch (error) {
    console.error('Error loading users:', error)
  } finally {
    loading.value = false
  }
}

onMounted(loadUsers)

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
  padding: 12px 15px; 
  margin-bottom: 0;
  border-radius: 0;
  box-shadow: none;
  border-bottom: 1px solid #e5e7eb;
  display: flex; 
  gap: 10px; 
}

.search-input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 13px;
  transition: border-color 0.3s;
}

.search-input:focus {
  outline: none;
  border-color: #3b82f6;
}

.filter-select {
  padding: 8px 12px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 13px;
  background: white;
  cursor: pointer;
  min-width: 130px;
}

.users-container { 
  display: grid; 
  grid-template-columns: 1fr 1fr; 
  gap: 15px; 
}

.user-column { 
  display: flex; 
  flex-direction: column; 
  min-width: 0;
}

.section-title { 
  font-size: 16px; 
  color: #1f2937; 
  font-weight: 700; 
  margin: 0; 
  padding: 12px 15px; 
  background: white; 
  border-radius: 12px 12px 0 0; 
  box-shadow: 0 2px 8px rgba(0,0,0,0.08); 
}

.users-table { 
  background: white; 
  padding: 0; 
  border-radius: 0 0 12px 12px; 
  box-shadow: 0 2px 8px rgba(0,0,0,0.08); 
  overflow: hidden;
}

.users-table table { 
  width: 100%; 
  border-collapse: collapse; 
  table-layout: auto;
}

.users-table th { 
  text-align: left; 
  padding: 10px 8px; 
  background: #f9fafb; 
  color: #374151; 
  font-weight: 600; 
  font-size: 12px; 
  border-bottom: 2px solid #e5e7eb; 
  white-space: nowrap;
}

.users-table td { 
  padding: 10px 8px; 
  border-bottom: 1px solid #f3f4f6; 
  color: #6b7280; 
  font-size: 12px; 
  vertical-align: top;
}

.user-name { 
  color: #1f2937; 
  font-weight: 600; 
  word-wrap: break-word;
  word-break: break-word;
}

.user-email { 
  color: #6b7280; 
  font-size: 11px; 
  word-wrap: break-word;
  word-break: break-all;
}

.status-badge { 
  display: inline-block;
  padding: 4px 10px; 
  border-radius: 20px; 
  font-size: 11px; 
  font-weight: 600; 
  white-space: nowrap;
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
  gap: 5px; 
  justify-content: flex-start;
  flex-wrap: wrap;
}

.btn-toggle, .btn-promote { 
  padding: 5px 10px; 
  border: none; 
  border-radius: 6px; 
  font-weight: 600; 
  font-size: 11px; 
  cursor: pointer; 
  transition: all 0.3s; 
  white-space: nowrap; 
}

.btn-toggle { 
  background: #fee2e2; 
  color: #991b1b; 
}

.btn-toggle:hover { 
  background: #fecaca; 
  transform: translateY(-1px);
}

.btn-promote { 
  background: #dbeafe; 
  color: #1e40af; 
}

.btn-promote:hover { 
  background: #bfdbfe; 
  transform: translateY(-1px);
}

@media (max-width: 1400px) {
  .users-container { 
    grid-template-columns: 1fr; 
  }
}

@media (max-width: 768px) {
  .filter-section {
    flex-direction: column;
  }
  
  .filter-select {
    width: 100%;
  }
  
  .users-table th,
  .users-table td {
    padding: 8px 6px;
    font-size: 11px;
  }
  
  .btn-toggle, .btn-promote {
    padding: 4px 8px;
    font-size: 10px;
  }
}
</style>
