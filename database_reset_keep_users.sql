-- Script xóa tất cả dữ liệu NGOẠI TRỪ bảng users
-- Sử dụng: mysql -u root -p exam_system < database_reset_keep_users.sql

USE exam_system;

-- Tắt foreign key checks để xóa dữ liệu
SET FOREIGN_KEY_CHECKS = 0;

-- Xóa dữ liệu bảng answers
TRUNCATE TABLE answers;

-- Xóa dữ liệu bảng results
TRUNCATE TABLE results;

-- Xóa dữ liệu bảng exam_questions
TRUNCATE TABLE exam_questions;

-- Xóa dữ liệu bảng exams
TRUNCATE TABLE exams;

-- Xóa dữ liệu bảng questions
TRUNCATE TABLE questions;

-- Xóa dữ liệu bảng teacher_requests
TRUNCATE TABLE teacher_requests;

-- Xóa dữ liệu bảng notifications
TRUNCATE TABLE notifications;

-- Bật lại foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- Hiển thị số lượng users còn lại
SELECT COUNT(*) as total_users FROM users;

SELECT 'Đã xóa tất cả dữ liệu, chỉ giữ lại bảng users!' as status;
