<template>
  <StudentLayout activeMenu="home">
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

    <WelcomeBanner
      title="Chào mừng đến với Hệ Thống Thi Trực Tuyến!"
      description="Nền tảng thi trắc nghiệm hiện đại, giúp bạn học tập và kiểm tra kiến thức một cách hiệu quả"
    />

    <div class="intro-section">
      <IntroCard
        number="01"
        title="Thi Trực Tuyến"
        description="Làm bài thi mọi lúc mọi nơi với giao diện thân thiện và dễ sử dụng. Hệ thống tự động chấm điểm và thống kê kết quả chi tiết."
      />
      <IntroCard
        number="02"
        title="Ngân Hàng Đề Thi"
        description="Kho đề thi phong phú với nhiều môn học và độ khó khác nhau. Giáo viên liên tục cập nhật đề thi mới để bạn luyện tập."
      />
      <IntroCard
        number="03"
        title="Theo Dõi Tiến Độ"
        description="Xem lại kết quả các bài thi đã làm, phân tích điểm mạnh điểm yếu để cải thiện kết quả học tập của bạn."
      />
    </div>

    <div class="stats-grid">
      <StatCard
        :value="stats.availableExams"
        label="Đề thi khả dụng"
        badge-text="Tổng số"
      />
      <StatCard
        :value="stats.completedExams"
        label="Đã hoàn thành"
        badge-text="Hoàn thành"
        badge-type="success"
      />
      <StatCard
        :value="`${stats.averageScore}%`"
        label="Điểm trung bình"
        badge-text="Điểm số"
        badge-type="warning"
      />
    </div>

    <div class="content-grid">
      <div class="content-main">
        <div class="section-title">
          <h3>Hướng Dẫn Sử Dụng</h3>
        </div>
        <div class="guide-list">
          <GuideItem
            step="Bước 1"
            title="Chọn Đề Thi"
            description="Vào mục &quot;Đề thi&quot; để xem danh sách các đề thi khả dụng. Chọn đề thi phù hợp với môn học và độ khó bạn muốn luyện tập."
          />
          <GuideItem
            step="Bước 2"
            title="Làm Bài Thi"
            description="Đọc kỹ câu hỏi và chọn đáp án đúng. Hệ thống sẽ tự động lưu câu trả lời của bạn. Lưu ý thời gian làm bài."
          />
          <GuideItem
            step="Bước 3"
            title="Xem Kết Quả"
            description="Sau khi nộp bài, vào mục &quot;Kết quả&quot; để xem điểm số và đáp án chi tiết. Phân tích để cải thiện kết quả lần sau."
          />
        </div>

        <div class="section-title" style="margin-top: 40px;">
          <h3>Đề Thi Gần Đây</h3>
          <a href="#" @click.prevent="$router.push('/student/exams')" class="view-all">Xem tất cả</a>
        </div>

        <LoadingSpinner v-if="loading" />

        <EmptyState 
          v-else-if="exams.length === 0"
          message="Chưa có đề thi nào khả dụng"
        />

        <div v-else class="exam-list">
          <ExamListItem
            v-for="exam in exams.slice(0, 3)"
            :key="exam.id"
            :exam="exam"
            @start-exam="startExam"
          />
        </div>
      </div>

      <div class="content-sidebar">
        <UserProfileCard
          :user="authStore.user"
          :completed-exams="stats.completedExams"
          :average-score="stats.averageScore"
        />

        <TipCard
          title="Mẹo Học Tập"
          description="Học sinh ôn tập trước khi thi 48 giờ có điểm số cao hơn 15% so với trung bình. Hãy thử ngay!"
          button-text="Bắt đầu luyện tập"
          @action-click="$router.push('/student/exams')"
        />

        <ActivityList
          :results="results"
          :loading="loadingResults"
        />
      </div>
    </div>
  </StudentLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/authStore'
import examService from '../../services/examService'
import resultService from '../../services/resultService'
import StudentLayout from '../../components/layouts/StudentLayout.vue'
import LoadingSpinner from '../../components/common/LoadingSpinner.vue'
import EmptyState from '../../components/common/EmptyState.vue'
import WelcomeBanner from '../../components/student/WelcomeBanner.vue'
import IntroCard from '../../components/student/IntroCard.vue'
import StatCard from '../../components/student/StatCard.vue'
import GuideItem from '../../components/student/GuideItem.vue'
import ExamListItem from '../../components/student/ExamListItem.vue'
import UserProfileCard from '../../components/student/UserProfileCard.vue'
import TipCard from '../../components/student/TipCard.vue'
import ActivityList from '../../components/student/ActivityList.vue'

const router = useRouter()
const authStore = useAuthStore()

const exams = ref([])
const results = ref([])
const loading = ref(false)
const loadingResults = ref(false)
const stats = ref({
  availableExams: 0,
  completedExams: 0,
  averageScore: 0
})

onMounted(() => {
  loadExams()
  loadResults()
})

const loadExams = async () => {
  try {
    loading.value = true
    const response = await examService.getActiveExams()
    if (response.success) {
      exams.value = response.data
      stats.value.availableExams = exams.value.length
    }
  } catch (error) {
    console.error('Error loading exams:', error)
  } finally {
    loading.value = false
  }
}

const loadResults = async () => {
  try {
    loadingResults.value = true
    const response = await resultService.getMyResults()
    if (response.success) {
      results.value = response.data.filter(r => r.status === 'graded').slice(0, 5)
      stats.value.completedExams = response.data.filter(r => r.status === 'graded').length
      if (results.value.length > 0) {
        const total = results.value.reduce((sum, r) => sum + parseFloat(r.score), 0)
        stats.value.averageScore = Math.round(total / results.value.length)
      }
    }
  } catch (error) {
    console.error('Error loading results:', error)
  } finally {
    loadingResults.value = false
  }
}

const startExam = (examId) => {
  router.push(`/student/exam/${examId}`)
}
</script>

<style scoped>
.user-name {
  font-size: 15px;
  color: #374151;
  font-weight: 600;
}

.intro-section {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 25px;
  margin-bottom: 35px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 25px;
  margin-bottom: 35px;
}

.content-grid {
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: 30px;
}

.content-main {
  background: white;
  padding: 30px;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.section-title h3 {
  font-size: 20px;
  color: #1f2937;
  font-weight: 700;
  margin: 0;
}

.view-all {
  color: #1e40af;
  font-size: 14px;
  text-decoration: none;
  font-weight: 600;
}

.view-all:hover {
  color: #1e3a8a;
}

.guide-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 30px;
}

.exam-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.content-sidebar {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

@media (max-width: 1200px) {
  .content-grid {
    grid-template-columns: 1fr;
  }
  .intro-section {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>
