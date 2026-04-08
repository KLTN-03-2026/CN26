# HỆ THỐNG THI TRẮC NGHIỆM TRỰC TUYẾN

Hệ thống thi trắc nghiệm trực tuyến hoàn chỉnh với 3 vai trò: Học sinh, Giáo viên, Admin.

## Công nghệ sử dụng

### Backend
- Java 17
- Spring Boot 3.x
- Spring Security + JWT
- MySQL 8.0
- OpenAI API (tạo câu hỏi tự động)
- Apache POI (nhập từ Word)

### Frontend
- Vue 3 (Composition API)
- Vue Router 4
- Pinia (State Management)
- Axios
- Vite

## Cài đặt và Chạy

### 1. Cài đặt Database

```bash
# Tạo database
mysql -u root -p

# Trong MySQL console
CREATE DATABASE exam_system;
USE exam_system;

# Import schema
source database_clean.sql
```

### 2. Cấu hình Backend

Sửa file `backend/src/main/resources/application.properties`:

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/exam_system
spring.datasource.username=root
spring.datasource.password=your_password

# JWT Secret
jwt.secret=your_secret_key_here

# OpenAI API (tùy chọn)
openai.api.key=your_openai_api_key

# Email (tùy chọn)
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
```

### 3. Chạy Backend

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

Backend sẽ chạy tại: http://localhost:8080

### 4. Chạy Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend sẽ chạy tại: http://localhost:5173

## Tài khoản mặc định

Sau khi import database, bạn có thể đăng nhập với:

### Admin
- Email:
- Password:

### Giáo viên
- Email: 
- Password: 

### Học sinh
- Email: 
- Password: 

## Tính năng chính

### Học sinh
- Đăng ký/Đăng nhập
- Xem danh sách đề thi
- Làm bài thi với timer
- Xem kết quả chi tiết
- Quản lý hồ sơ cá nhân

### Giáo viên
- Tạo câu hỏi thủ công
- Tạo câu hỏi bằng AI
- Nhập câu hỏi từ Word
- Tạo và quản lý đề thi
- Xem thống kê chi tiết
- Phân tích kết quả học sinh

### Admin
- Quản lý người dùng
- Duyệt yêu cầu nâng cấp giáo viên
- Xem thống kê hệ thống
- Kích hoạt/vô hiệu hóa tài khoản

## Cấu trúc thư mục

```
exam-system/
├── backend/                    # Spring Boot backend
│   ├── src/main/java/com/exam/
│   │   ├── controller/        # REST API controllers
│   │   ├── service/           # Business logic
│   │   ├── repository/        # JPA repositories
│   │   ├── entity/            # Database entities
│   │   ├── dto/               # Data transfer objects
│   │   ├── security/          # JWT & Security
│   │   └── config/            # Configuration
│   └── pom.xml
├── frontend/                   # Vue.js frontend
│   ├── src/
│   │   ├── views/             # Page components
│   │   ├── services/          # API services
│   │   ├── stores/            # Pinia stores
│   │   └── router/            # Vue Router
│   └── package.json
├── database_clean.sql          # Database schema
└── README.md
```



### Test Frontend
1. Chạy backend
2. Chạy frontend
3. Mở browser: http://localhost:5173
4. Đăng nhập và test các chức năng

## Lưu ý

1. Đảm bảo MySQL đang chạy
2. Backend phải chạy trước frontend
3. Cấu hình CORS đã được thiết lập cho localhost:5173
4. JWT token có thời hạn 24 giờ
5. OpenAI API key là tùy chọn (chỉ cần nếu dùng tính năng AI)

## Troubleshooting

### Lỗi kết nối database
- Kiểm tra MySQL đang chạy
- Kiểm tra username/password trong application.properties

### Lỗi CORS
- Đảm bảo backend đã cấu hình CORS cho localhost:5173
- Kiểm tra file CorsConfig.java

### Lỗi 401 Unauthorized
- Token hết hạn, đăng nhập lại
- Kiểm tra JWT secret trong application.properties

### Frontend không gọi được API
- Kiểm tra backend đang chạy
- Kiểm tra proxy trong vite.config.js

## Liên hệ

Nếu có vấn đề, vui lòng tạo issue hoặc liên hệ.
