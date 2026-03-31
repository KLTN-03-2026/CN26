# FRONTEND - HỆ THỐNG THI TRẮC NGHIỆM TRỰC TUYẾN

## Công nghệ sử dụng
- Vue 3 (Composition API)
- Vue Router 4
- Pinia (State Management)
- Axios (HTTP Client)
- Vite (Build Tool)

## Cài đặt

### 1. Cài đặt dependencies
```bash
cd frontend
npm install
```

### 2. Chạy development server
```bash
npm run dev
```

Frontend sẽ chạy tại: http://localhost:5173

### 3. Build cho production
```bash
npm run build
```

## Cấu trúc thư mục

```
frontend/
├── src/
│   ├── assets/          # CSS, images
│   ├── components/      # Vue components
│   ├── views/           # Page components
│   │   ├── auth/        # Login, Register
│   │   ├── student/     # Student pages
│   │   ├── teacher/     # Teacher pages
│   │   └── admin/       # Admin pages
│   ├── router/          # Vue Router config
│   ├── stores/          # Pinia stores
│   ├── services/        # API services
│   ├── App.vue
│   └── main.js
├── index.html
├── package.json
└── vite.config.js
```

## Các trang đã tạo

### Auth (2/2) ✅
- `/login` - Đăng nhập
- `/register` - Đăng ký

### Student (6/6) ✅
- `/student` - Dashboard học sinh
- `/student/profile` - Quản lý hồ sơ
- `/student/exams` - Danh sách đề thi
- `/student/exam/:id` - Làm bài thi (có timer, navigation)
- `/student/results` - Kết quả thi
- `/student/result/:id` - Chi tiết kết quả

### Teacher (4/4) ✅
- `/teacher` - Dashboard giáo viên
- `/teacher/questions` - Quản lý câu hỏi (CRUD, AI, Word import)
- `/teacher/exams` - Quản lý đề thi (CRUD, chọn câu hỏi)
- `/teacher/statistics/:id` - Thống kê đề thi (biểu đồ, phân tích)

### Admin (3/3) ✅
- `/admin` - Dashboard admin
- `/admin/users` - Quản lý người dùng
- `/admin/teacher-requests` - Duyệt yêu cầu giáo viên

## Tính năng đã triển khai

### Student
- Xem danh sách đề thi với filter
- Làm bài thi với timer đếm ngược
- Navigation giữa các câu hỏi
- Hiển thị số câu đã trả lời
- Xem kết quả chi tiết với đáp án đúng/sai
- Thống kê điểm số cá nhân

### Teacher
- Tạo câu hỏi thủ công
- Tạo câu hỏi bằng AI (OpenAI)
- Nhập câu hỏi từ file Word
- Tạo đề thi từ ngân hàng câu hỏi
- Xem thống kê chi tiết đề thi
- Phân tích từng câu hỏi

### Admin
- Quản lý tất cả người dùng
- Kích hoạt/vô hiệu hóa tài khoản
- Duyệt yêu cầu nâng cấp giáo viên
- Xem thống kê tổng quan hệ thống

## Lưu ý

1. **Backend phải chạy trước** tại http://localhost:8080
2. **Proxy đã được cấu hình** trong vite.config.js
3. **JWT Token** được lưu trong localStorage
4. **Auto redirect** khi token hết hạn
5. **Không sử dụng icon** - theo yêu cầu

## Pattern để tạo trang mới

Mỗi trang Vue component có cấu trúc:

```vue
<template>
  <!-- HTML template -->
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/authStore'
// Import services cần thiết

// Logic component
</script>
```

## Test Frontend

1. Chạy backend trước
2. Chạy frontend: `npm run dev`
3. Mở browser: http://localhost:5173
4. Test flow:
   - Đăng ký tài khoản
   - Đăng nhập
   - Xem dashboard
   - Test các chức năng

## Troubleshooting

### Lỗi CORS
- Đảm bảo backend đã cấu hình CORS cho http://localhost:5173

### Lỗi 401
- Token hết hạn, đăng nhập lại

### API không hoạt động
- Kiểm tra backend đang chạy
- Kiểm tra proxy trong vite.config.js
