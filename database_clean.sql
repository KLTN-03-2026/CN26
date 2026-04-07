--1. BẢNG USERS
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL COMMENT 'Hashed password',
    full_name VARCHAR(255) NOT NULL,
    role ENUM('student', 'teacher', 'admin') DEFAULT 'student',
    status ENUM('active', 'inactive', 'locked') DEFAULT 'inactive',
    email_verified BOOLEAN DEFAULT FALSE,
    verification_token VARCHAR(255),
    reset_token VARCHAR(255),
    avatar VARCHAR(500),
    phone VARCHAR(20),
    date_of_birth DATE,
    gender ENUM('male', 'female', 'other'),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL
) COMMENT 'Quản lý người dùng';

--2. BẢNG QUESTIONS
CREATE TABLE questions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    content TEXT NOT NULL COMMENT 'Nội dung câu hỏi',
    option_a VARCHAR(500) NOT NULL,
    option_b VARCHAR(500) NOT NULL,
    option_c VARCHAR(500) NOT NULL,
    option_d VARCHAR(500) NOT NULL,
    correct_answer ENUM('A', 'B', 'C', 'D') NOT NULL,
    level ENUM('easy', 'medium', 'hard') DEFAULT 'medium',
    subject VARCHAR(100) COMMENT 'Môn học: Toán, Lý, Hóa...',
    explanation TEXT COMMENT 'Giải thích đáp án',
    created_by INT NOT NULL COMMENT 'ID giáo viên tạo',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE
) COMMENT 'Ngân hàng câu hỏi';

--3. BẢNG EXAMS
CREATE TABLE exams (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL COMMENT 'Tên đề thi',
    description TEXT,
    duration INT NOT NULL COMMENT 'Thời gian làm bài (phút)',
    subject VARCHAR(100) COMMENT 'Môn học',
    total_questions INT DEFAULT 0,
    passing_score DECIMAL(5,2) DEFAULT 50.00 COMMENT 'Điểm đạt (%)',
    start_time TIMESTAMP NULL COMMENT 'Thời gian bắt đầu cho phép thi',
    end_time TIMESTAMP NULL COMMENT 'Thời gian kết thúc',
    is_active BOOLEAN DEFAULT TRUE,
    created_by INT NOT NULL COMMENT 'ID giáo viên tạo',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE
) COMMENT 'Đề thi';

-- 🟠 4. BẢNG EXAM_QUESTIONS
CREATE TABLE exam_questions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    exam_id INT NOT NULL,
    question_id INT NOT NULL,
    question_order INT NOT NULL COMMENT 'Thứ tự câu hỏi trong đề',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (exam_id) REFERENCES exams(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    UNIQUE KEY unique_exam_question (exam_id, question_id)
) COMMENT 'Câu hỏi trong đề thi';

-- 5. BẢNG RESULTS
CREATE TABLE results (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL COMMENT 'Học sinh thi',
    exam_id INT NOT NULL,
    score DECIMAL(5,2) NOT NULL COMMENT 'Điểm số (%)',
    total_correct INT DEFAULT 0 COMMENT 'Số câu đúng',
    total_questions INT DEFAULT 0 COMMENT 'Tổng số câu',
    time_spent INT COMMENT 'Thời gian làm bài (giây)',
    status ENUM('in_progress', 'submitted', 'graded') DEFAULT 'in_progress',
    start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    submit_time TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (exam_id) REFERENCES exams(id) ON DELETE CASCADE
) COMMENT 'Kết quả thi';

-- 6. BẢNG ANSWERS
CREATE TABLE answers (
    id INT PRIMARY KEY AUTO_INCREMENT,
    result_id INT NOT NULL,
    question_id INT NOT NULL,
    selected_answer ENUM('A', 'B', 'C', 'D') NOT NULL COMMENT 'Đáp án học sinh chọn',
    is_correct BOOLEAN DEFAULT FALSE,
    answered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (result_id) REFERENCES results(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
) COMMENT 'Chi tiết câu trả lời';

-- ⚫ 7. BẢNG TEACHER_REQUESTS
CREATE TABLE teacher_requests (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    reason TEXT NOT NULL COMMENT 'Lý do muốn trở thành giáo viên',
    qualifications TEXT COMMENT 'Bằng cấp, chứng chỉ',
    status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending',
    reviewed_by INT COMMENT 'ID admin duyệt',
    reviewed_at TIMESTAMP NULL,
    review_note TEXT COMMENT 'Ghi chú khi duyệt',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (reviewed_by) REFERENCES users(id) ON DELETE SET NULL
) COMMENT 'Yêu cầu trở thành giáo viên';

--8. BẢNG NOTIFICATIONS (Bonus)
CREATE TABLE notifications (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT,
    type VARCHAR(50) COMMENT 'exam_assigned, result_ready, request_approved...',
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) COMMENT 'Thông báo cho người dùng';
