# BACKEND - HỆ THỐNG THI TRẮC NGHIỆM TRỰC TUYẾN

## Yêu cầu hệ thống
- Java 17 hoặc cao hơn
- Maven 3.6+
- MySQL 8.0
- IDE: IntelliJ IDEA hoặc Eclipse

## Cài đặt

### 1. Clone project và vào thư mục backend
```bash
cd backend
```

### 2. Cấu hình Database
Mở MySQL Workbench và chạy file `../database/database_clean.sql`

### 3. Cấu hình application.properties
Mở file `src/main/resources/application.properties` và cập nhật:

```properties
# Database
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD

# Email (Gmail)
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password

# JWT Secret (giữ nguyên hoặc đổi)
jwt.secret=YourSuperSecretKeyForJWTTokenGenerationMustBeLongEnough256Bits

# OpenAI API
openai.api.key=your-openai-api-key-here
```

### 4. Build project
```bash
mvn clean install
```

### 5. Chạy ứng dụng
```bash
mvn spring-boot:run
```

Backend sẽ chạy tại: http://localhost:8080


## Test API bằng Postman/Thunder Client

### 1. Đăng ký tài khoản
```
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "fullName": "Nguyen Van A",
  "email": "test@example.com",
  "password": "123456"
}
```

### 2. Đăng nhập
```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "test@example.com",
  "password": "123456"
}
```

Response sẽ trả về JWT token. Copy token này để dùng cho các request tiếp theo.

### 3. Lấy thông tin profile (cần token)
```
GET http://localhost:8080/api/users/profile
Authorization: Bearer YOUR_JWT_TOKEN
```

### 4. Tạo câu hỏi (Teacher/Admin)
```
POST http://localhost:8080/api/questions
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json

{
  "content": "2 + 2 = ?",
  "optionA": "3",
  "optionB": "4",
  "optionC": "5",
  "optionD": "6",
  "correctAnswer": "B",
  "level": "easy",
  "subject": "Toán học"
}
```


### 5. Sinh câu hỏi bằng AI (Teacher/Admin)
```
POST http://localhost:8080/api/questions/generate-ai
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json

{
  "topic": "Toán học lớp 10",
  "quantity": 5,
  "level": "medium",
  "subject": "Toán học"
}
```

### 6. Tạo đề thi (Teacher/Admin)
```
POST http://localhost:8080/api/exams
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json

{
  "name": "Kiểm tra Toán học",
  "description": "Đề thi giữa kỳ",
  "duration": 30,
  "subject": "Toán học",
  "passingScore": 50,
  "questionIds": [1, 2, 3, 4, 5]
}
```

### 7. Bắt đầu làm bài thi (Student)
```
POST http://localhost:8080/api/results/start/1
Authorization: Bearer YOUR_JWT_TOKEN
```

### 8. Nộp bài thi (Student)
```
POST http://localhost:8080/api/results/submit
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json

{
  "resultId": 1,
  "answers": [
    {"questionId": 1, "selectedAnswer": "B"},
    {"questionId": 2, "selectedAnswer": "A"},
    {"questionId": 3, "selectedAnswer": "C"}
  ]
}
```


### 9. Xem thống kê đề thi (Teacher/Admin)
```
GET http://localhost:8080/api/results/exam/1/statistics
Authorization: Bearer YOUR_JWT_TOKEN
```

### 10. Gửi yêu cầu trở thành giáo viên (Student)
```
POST http://localhost:8080/api/teacher-requests
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json

{
  "reason": "Tôi có kinh nghiệm giảng dạy 5 năm",
  "qualifications": "Thạc sĩ Toán học"
}
```

### 11. Duyệt yêu cầu giáo viên (Admin)
```
POST http://localhost:8080/api/teacher-requests/1/review
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json

{
  "status": "approved",
  "reviewNote": "Đạt yêu cầu"
}
```

## Danh sách API Endpoints

### Authentication
- POST `/api/auth/register` - Đăng ký
- POST `/api/auth/login` - Đăng nhập
- GET `/api/auth/verify?token=xxx` - Xác thực email
- POST `/api/auth/forgot-password?email=xxx` - Quên mật khẩu
- POST `/api/auth/reset-password?token=xxx&newPassword=xxx` - Đặt lại mật khẩu

### Users
- GET `/api/users/profile` - Xem profile
- PUT `/api/users/profile` - Cập nhật profile
- POST `/api/users/change-password` - Đổi mật khẩu
- GET `/api/users` - Danh sách người dùng (Admin)
- POST `/api/users/{id}/lock` - Khóa tài khoản (Admin)
- POST `/api/users/{id}/unlock` - Mở khóa (Admin)


### Questions
- GET `/api/questions` - Danh sách câu hỏi (Teacher/Admin)
- GET `/api/questions/my` - Câu hỏi của tôi (Teacher/Admin)
- GET `/api/questions/{id}` - Chi tiết câu hỏi
- POST `/api/questions` - Tạo câu hỏi (Teacher/Admin)
- PUT `/api/questions/{id}` - Sửa câu hỏi (Teacher/Admin)
- DELETE `/api/questions/{id}` - Xóa câu hỏi (Teacher/Admin)
- POST `/api/questions/generate-ai` - Sinh câu hỏi AI (Teacher/Admin)
- POST `/api/questions/import-word` - Import từ Word (Teacher/Admin)

### Exams
- GET `/api/exams` - Danh sách đề thi
- GET `/api/exams/active` - Đề thi đang mở
- GET `/api/exams/my` - Đề thi của tôi (Teacher/Admin)
- GET `/api/exams/{id}` - Chi tiết đề thi
- GET `/api/exams/{id}/questions` - Câu hỏi trong đề
- POST `/api/exams` - Tạo đề thi (Teacher/Admin)
- PUT `/api/exams/{id}` - Sửa đề thi (Teacher/Admin)
- DELETE `/api/exams/{id}` - Xóa đề thi (Teacher/Admin)

### Results
- POST `/api/results/start/{examId}` - Bắt đầu thi
- POST `/api/results/submit` - Nộp bài
- GET `/api/results/my` - Kết quả của tôi
- GET `/api/results/{id}` - Chi tiết kết quả
- GET `/api/results/{id}/answers` - Chi tiết bài làm
- GET `/api/results/exam/{examId}` - Kết quả đề thi (Teacher/Admin)
- GET `/api/results/exam/{examId}/statistics` - Thống kê (Teacher/Admin)

### Teacher Requests
- POST `/api/teacher-requests` - Gửi yêu cầu
- GET `/api/teacher-requests/my` - Yêu cầu của tôi
- GET `/api/teacher-requests` - Tất cả yêu cầu (Admin)
- GET `/api/teacher-requests/pending` - Yêu cầu chờ duyệt (Admin)
- POST `/api/teacher-requests/{id}/review` - Duyệt yêu cầu (Admin)

## Lưu ý quan trọng

1. **Email Service**: Cần cấu hình Gmail App Password để gửi email xác thực
2. **OpenAI API**: Cần API key để sử dụng tính năng sinh câu hỏi AI
3. **JWT Token**: Có hiệu lực 24 giờ (86400000ms)
4. **File Upload**: Giới hạn 10MB

## Troubleshooting

### Lỗi kết nối database
- Kiểm tra MySQL đã chạy chưa
- Kiểm tra username/password trong application.properties
- Kiểm tra database `exam_system` đã được tạo chưa

### Lỗi JWT
- Kiểm tra token có đúng format `Bearer YOUR_TOKEN`
- Token có thể hết hạn, đăng nhập lại để lấy token mới

### Lỗi 403 Forbidden
- Kiểm tra role của user có đủ quyền không
- Student không thể truy cập API của Teacher/Admin
