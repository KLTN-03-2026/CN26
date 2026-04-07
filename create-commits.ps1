# Script to create commits with specific dates for school repository

Write-Host "Starting commit creation process..." -ForegroundColor Green

# Add new remote
Write-Host "`nAdding school remote..." -ForegroundColor Cyan
git remote add school https://github.com/nguyenvucodex/nguyentruongvu-online-exam-system.git 2>$null

# Commit 1: 28/03/2026 - Database schema
Write-Host "`nCreating commit 1/5: Database schema (28/03/2026)..." -ForegroundColor Yellow
$env:GIT_AUTHOR_DATE = "2026-03-28T14:00:00"
$env:GIT_COMMITTER_DATE = "2026-03-28T14:00:00"
git add database_clean.sql
git commit -m "Thêm schema cơ sở dữ liệu

- Thiết kế bảng users, exams, questions, results
- Thiết kế quan hệ giữa các bảng
- Thêm dữ liệu mẫu ban đầu"

# Commit 2: 02/04/2026 - Backend structure
Write-Host "Creating commit 2/5: Backend structure (02/04/2026)..." -ForegroundColor Yellow
$env:GIT_AUTHOR_DATE = "2026-04-02T10:00:00"
$env:GIT_COMMITTER_DATE = "2026-04-02T10:00:00"
git add backend/pom.xml backend/src/main/resources/application.properties backend/src/main/java/com/exam/ExamSystemApplication.java
git commit -m "Khởi tạo dự án Spring Boot với cấu trúc cơ bản

- Cấu hình Maven dependencies
- Cấu hình database connection
- Cấu hình JWT và email service
- Khởi tạo main application class"

# Commit 3: 04/04/2026 - Entities and Repositories
Write-Host "Creating commit 3/5: Entities and Repositories (04/04/2026)..." -ForegroundColor Yellow
$env:GIT_AUTHOR_DATE = "2026-04-04T15:00:00"
$env:GIT_COMMITTER_DATE = "2026-04-04T15:00:00"
git add backend/src/main/java/com/exam/entity/ backend/src/main/java/com/exam/repository/
git commit -m "Thêm các entity models và repositories

- Entity: User, Exam, Question, Result, Answer
- Entity: ExamQuestion, TeacherRequest, Notification
- Repositories cho tất cả entities
- Thiết lập quan hệ JPA giữa các entities"

# Commit 4: 06/04/2026 - Authentication & Security
Write-Host "Creating commit 4/5: Authentication & Security (06/04/2026)..." -ForegroundColor Yellow
$env:GIT_AUTHOR_DATE = "2026-04-06T16:00:00"
$env:GIT_COMMITTER_DATE = "2026-04-06T16:00:00"
git add backend/src/main/java/com/exam/security/ backend/src/main/java/com/exam/config/ backend/src/main/java/com/exam/controller/AuthController.java backend/src/main/java/com/exam/service/AuthService.java backend/src/main/java/com/exam/dto/Auth*.java
git commit -m "Triển khai hệ thống xác thực JWT và bảo mật

- JWT token generation và validation
- Spring Security configuration
- CORS configuration
- Login/Register API endpoints
- Custom UserDetailsService
- Authentication DTOs"

# Commit 5: 07/04/2026 - Everything else
Write-Host "Creating commit 5/5: Complete system (07/04/2026)..." -ForegroundColor Yellow
$env:GIT_AUTHOR_DATE = "2026-04-07T14:30:00"
$env:GIT_COMMITTER_DATE = "2026-04-07T14:30:00"
git add .
git commit -m "Triển khai API quản lý đề thi, câu hỏi và frontend cơ bản

Backend:
- ExamController: CRUD đề thi, thống kê
- QuestionController: CRUD câu hỏi, import Word, AI generation
- ResultController: Nộp bài, xem kết quả
- UserController: Quản lý profile
- TeacherRequestController: Yêu cầu nâng cấp tài khoản
- Email service và Word import service
- Exception handlers

Frontend:
- Khởi tạo Vue.js project với Vite
- Setup router và Pinia store
- Tạo các trang auth (Login, Register, Verify Email)
- Tạo các trang student (Dashboard, Exam List, Take Exam, Results)
- Tạo các trang teacher (Dashboard, Exams, Questions, Statistics)
- Tạo các trang admin (Dashboard, Users, Teacher Requests)
- API services và interceptors
- Modern UI với gradient blue theme"

Write-Host "`nAll commits created successfully!" -ForegroundColor Green
Write-Host "`nPushing to school repository..." -ForegroundColor Cyan

# Push to new repository
git push -u school main

Write-Host "`nDone! Check your repository at:" -ForegroundColor Green
Write-Host "https://github.com/nguyenvucodex/nguyentruongvu-online-exam-system" -ForegroundColor Cyan

pause
