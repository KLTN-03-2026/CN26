package com.exam.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${app.base-url}")
    private String baseUrl;
    
    @Value("${app.frontend-url}")
    private String frontendUrl;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    public void sendVerificationEmail(String toEmail, String token) {
        String subject = "Xác thực tài khoản - Hệ thống thi trực tuyến";
        String verificationUrl = frontendUrl + "/verify-email?token=" + token;
        String message = "Xin chào,\n\n" +
                "Cảm ơn bạn đã đăng ký tài khoản. Vui lòng click vào link dưới đây để xác thực tài khoản:\n\n" +
                verificationUrl + "\n\n" +
                "Link này sẽ hết hạn sau 24 giờ.\n\n" +
                "Trân trọng,\n" +
                "Hệ thống thi trực tuyến";
        
        sendEmail(toEmail, subject, message);
    }
    
    public void sendPasswordResetEmail(String toEmail, String token) {
        String subject = "Đặt lại mật khẩu - Hệ thống thi trực tuyến";
        String resetUrl = baseUrl + "/reset-password?token=" + token;
        String message = "Xin chào,\n\n" +
                "Bạn đã yêu cầu đặt lại mật khẩu. Vui lòng click vào link dưới đây:\n\n" +
                resetUrl + "\n\n" +
                "Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.\n\n" +
                "Trân trọng,\n" +
                "Hệ thống thi trực tuyến";
        
        sendEmail(toEmail, subject, message);
    }
    
    public void sendTeacherRequestApprovalEmail(String toEmail, String userName) {
        String subject = "Yêu cầu trở thành giáo viên đã được chấp nhận";
        String message = "Xin chào " + userName + ",\n\n" +
                "Chúc mừng! Yêu cầu trở thành giáo viên của bạn đã được chấp nhận.\n" +
                "Bạn có thể đăng nhập và sử dụng các chức năng dành cho giáo viên.\n\n" +
                "Trân trọng,\n" +
                "Hệ thống thi trực tuyến";
        
        sendEmail(toEmail, subject, message);
    }
    
    public void sendTeacherRequestRejectionEmail(String toEmail, String userName, String reason) {
        String subject = "Yêu cầu trở thành giáo viên";
        String message = "Xin chào " + userName + ",\n\n" +
                "Rất tiếc, yêu cầu trở thành giáo viên của bạn chưa được chấp nhận.\n" +
                "Lý do: " + reason + "\n\n" +
                "Bạn có thể gửi yêu cầu mới sau khi đáp ứng đủ điều kiện.\n\n" +
                "Trân trọng,\n" +
                "Hệ thống thi trực tuyến";
        
        sendEmail(toEmail, subject, message);
    }
    
    private void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }
}
