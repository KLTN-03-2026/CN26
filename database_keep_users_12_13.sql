-- Script xóa tất cả user NGOẠI TRỪ user có id = 12 và 13
-- Sử dụng: mysql -u root -p exam_system < database_keep_users_12_13.sql

USE exam_system;

-- Tắt foreign key checks để xóa dữ liệu
SET FOREIGN_KEY_CHECKS = 0;

-- Xóa tất cả user trừ id 12 và 13
DELETE FROM users WHERE id NOT IN (12, 13);

-- Bật lại foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- Hiển thị số lượng users còn lại
SELECT COUNT(*) as total_users FROM users;
SELECT id, email, full_name, role FROM users;

SELECT 'Đã xóa tất cả users, chỉ giữ lại user id 12 và 13!' as status;
