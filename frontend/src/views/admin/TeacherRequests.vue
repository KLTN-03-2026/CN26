<template>
  <AdminLayout activeMenu="requests">
    <header class="top-header">
      <div class="header-left">
        <h1>Yêu Cầu Trở Thành Giáo Viên</h1>
      </div>
    </header>

    <div class="filter-section">
      <select v-model="filterStatus" class="filter-select">
        <option value="">Tất cả trạng thái</option>
        <option value="pending">Chờ duyệt</option>
        <option value="approved">Đã duyệt</option>
        <option value="rejected">Từ chối</option>
      </select>
    </div>

    <LoadingSpinner v-if="loading" />

    <EmptyState 
      v-else-if="filteredRequests.length === 0"
      message="Không có yêu cầu nào"
    />

    <div v-else class="requests-grid">
      <div v-for="request in filteredRequests" :key="request.id" class="request-card">
        <div class="request-header">
          <div class="user-info">
            <h3>{{ request.userName }}</h3>
            <p>{{ request.userEmail }}</p>
          </div>
          <span :class="['status-badge', `status-${request.status}`]">
            {{ getStatusText(request.status) }}
          </span>
        </div>
        <div class="request-body">
          <div class="info-section">
            <strong>Lý do:</strong>
            <p>{{ request.reason }}</p>
          </div>
          
          <div class="info-section">
            <strong>Chứng chỉ sư phạm:</strong>
            <div v-if="request.teachingCertificateUrl" class="image-preview">
              <img :src="getImageUrl(request.teachingCertificateUrl)" 
                   alt="Chứng chỉ sư phạm" 
                   @click="openImageModal(getImageUrl(request.teachingCertificateUrl))">
            </div>
            <p v-else class="no-data">Chưa có ảnh</p>
          </div>
          
          <div class="info-section">
            <strong>Bằng cấp:</strong>
            <div v-if="request.degreeUrl" class="image-preview">
              <img :src="getImageUrl(request.degreeUrl)" 
                   alt="Bằng cấp"
                   @click="openImageModal(getImageUrl(request.degreeUrl))">
            </div>
            <p v-else class="no-data">Chưa có ảnh</p>
          </div>
          
          <div class="info-section">
            <strong>Ngày gửi:</strong>
            <p>{{ formatDate(request.createdAt) }}</p>
          </div>
          <div v-if="request.reviewNote" class="review-note">
            <strong>Ghi chú duyệt:</strong>
            <p>{{ request.reviewNote }}</p>
          </div>
        </div>
        <div v-if="request.status === 'pending'" class="request-footer">
          <button class="btn-approve" @click="openReviewModal(request, 'approved')">
            Duyệt
          </button>
          <button class="btn-reject" @click="openReviewModal(request, 'rejected')">
            Từ chối
          </button>
        </div>
      </div>
    </div>

    <AppModal v-model="showReviewModal" :title="reviewAction === 'approved' ? 'Duyệt Yêu Cầu' : 'Từ Chối Yêu Cầu'">
      <form @submit.prevent="submitReview">
        <div class="form-group">
          <label>Ghi chú (tùy chọn)</label>
          <textarea v-model="reviewNote" rows="4" placeholder="Nhập ghi chú..."></textarea>
        </div>
        <div class="modal-actions">
          <button type="button" class="btn-cancel" @click="showReviewModal = false">Hủy</button>
          <button type="submit" class="btn-save" :disabled="reviewing">
            {{ reviewing ? 'Đang xử lý...' : 'Xác nhận' }}
          </button>
        </div>
      </form>
    </AppModal>

    <!-- Image Modal -->
    <AppModal v-model="showImageModal" title="Xem ảnh" size="large">
      <div class="image-modal-content">
        <img :src="selectedImage" alt="Full size image">
      </div>
    </AppModal>
  </AdminLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useFormatters } from '../../composables/useFormatters'
import teacherRequestService from '../../services/teacherRequestService'
import AdminLayout from '../../components/layouts/AdminLayout.vue'
import LoadingSpinner from '../../components/common/LoadingSpinner.vue'
import EmptyState from '../../components/common/EmptyState.vue'
import AppModal from '../../components/common/AppModal.vue'

const { formatDate, getStatusText } = useFormatters()

const requests = ref([])
const loading = ref(false)
const filterStatus = ref('')
const showReviewModal = ref(false)
const reviewingRequest = ref(null)
const reviewAction = ref('')
const reviewNote = ref('')
const reviewing = ref(false)
const showImageModal = ref(false)
const selectedImage = ref('')

// Helper function to build image URL
const getImageUrl = (path) => {
  if (!path) return ''
  // In development, Vite proxy /api to http://localhost:8080
  // So we need to use the full backend URL for static files
  const isDev = import.meta.env.DEV
  if (isDev) {
    return `http://localhost:8080${path}`
  }
  // In production, assume same origin
  return path
}

const filteredRequests = computed(() => {
  if (!filterStatus.value) return requests.value
  return requests.value.filter(r => r.status === filterStatus.value)
})

onMounted(() => {
  loadRequests()
})

const loadRequests = async () => {
  try {
    loading.value = true
    const response = await teacherRequestService.getAllRequests()
    if (response.success) {
      requests.value = response.data
    }
  } catch (error) {
    console.error('Error loading requests:', error)
  } finally {
    loading.value = false
  }
}

const openReviewModal = (request, action) => {
  reviewingRequest.value = request
  reviewAction.value = action
  reviewNote.value = ''
  showReviewModal.value = true
}

const submitReview = async () => {
  try {
    reviewing.value = true
    const response = await teacherRequestService.reviewRequest(reviewingRequest.value.id, {
      status: reviewAction.value,
      reviewNote: reviewNote.value
    })
    
    if (response.success) {
      alert('Xử lý yêu cầu thành công!')
      showReviewModal.value = false
      loadRequests()
    }
  } catch (error) {
    console.error('Error reviewing request:', error)
    alert('Có lỗi khi xử lý yêu cầu')
  } finally {
    reviewing.value = false
  }
}

const openImageModal = (imageUrl) => {
  selectedImage.value = imageUrl
  showImageModal.value = true
}
</script>

<style scoped>
.filter-section {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  margin-bottom: 25px;
}

.requests-grid {
  display: grid;
  gap: 20px;
}

.request-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  overflow: hidden;
}

.request-header {
  padding: 20px 25px;
  background: #f9fafb;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: start;
}

.user-info h3 {
  font-size: 18px;
  color: #1f2937;
  margin: 0 0 5px 0;
  font-weight: 700;
}

.user-info p {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

.status-badge {
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
}

.status-badge.status-pending {
  background: #fef3c7;
  color: #92400e;
}

.status-badge.status-approved {
  background: #d1fae5;
  color: #065f46;
}

.status-badge.status-rejected {
  background: #fee2e2;
  color: #991b1b;
}

.request-body {
  padding: 25px;
}

.info-section {
  margin-bottom: 20px;
}

.info-section strong {
  display: block;
  font-size: 14px;
  color: #374151;
  margin-bottom: 8px;
}

.info-section p {
  font-size: 14px;
  color: #6b7280;
  line-height: 1.6;
  margin: 0;
}

.review-note {
  padding: 15px;
  background: #eff6ff;
  border-left: 3px solid #3b82f6;
  border-radius: 6px;
  margin-top: 15px;
}

.review-note strong {
  display: block;
  color: #1e40af;
  margin-bottom: 8px;
  font-size: 14px;
}

.review-note p {
  margin: 0;
  color: #374151;
  font-size: 14px;
}

.request-footer {
  padding: 15px 25px;
  background: #f9fafb;
  border-top: 1px solid #e5e7eb;
  display: flex;
  gap: 12px;
}

.btn-approve,
.btn-reject {
  flex: 1;
  padding: 12px;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-approve {
  background: #10b981;
  color: white;
}

.btn-approve:hover {
  background: #059669;
}

.btn-reject {
  background: #ef4444;
  color: white;
}

.btn-reject:hover {
  background: #dc2626;
}

/* Shared button and modal styles moved to global style.css */

.image-preview {
  margin-top: 10px;
}

.image-preview img {
  max-width: 300px;
  max-height: 200px;
  border-radius: 8px;
  border: 2px solid #e5e7eb;
  cursor: pointer;
  transition: all 0.3s;
  object-fit: cover;
}

.image-preview img:hover {
  border-color: #3b82f6;
  transform: scale(1.02);
}

.no-data {
  color: #9ca3af;
  font-style: italic;
}

.image-modal-content {
  text-align: center;
  padding: 20px;
}

.image-modal-content img {
  max-width: 100%;
  max-height: 80vh;
  border-radius: 8px;
  object-fit: contain;
}
</style>
