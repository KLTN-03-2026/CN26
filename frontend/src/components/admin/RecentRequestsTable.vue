<template>
  <div class="recent-requests">
    <div class="section-header">
      <h2>Yêu Cầu Gần Đây</h2>
      <button class="btn-view-all" @click="$emit('view-all')">
        Xem tất cả →
      </button>
    </div>
    
    <LoadingSpinner v-if="loading" />
    
    <EmptyState 
      v-else-if="requests.length === 0"
      message="Không có yêu cầu nào"
    />
    
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
              <button 
                v-if="request.status === 'pending'" 
                class="btn-view" 
                @click="$emit('view-detail', request.id)">
                Xem chi tiết
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import LoadingSpinner from '../common/LoadingSpinner.vue'
import EmptyState from '../common/EmptyState.vue'
import { useFormatters } from '../../composables/useFormatters'

defineProps({
  requests: {
    type: Array,
    required: true
  },
  loading: {
    type: Boolean,
    default: false
  }
})

defineEmits(['view-all', 'view-detail'])

const { formatDate, getStatusText, getStatusClass } = useFormatters()
</script>

<style scoped>
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
  font-size: 15px;
  color: #374151;
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
</style>
